package neil.demo.fitba;

import java.util.Properties;

import com.hazelcast.jet.function.Functions;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamStage;

/**
 * <p>
 * A 7-stage processing pipeline to analyse Twitter data, of which there could be a large
 * amount, and store a small amount -- then sentiment expressed in those tweets.
 * </p>
 * <p>
 * From top to bottom, the pipeline looks like this. 
 * </p>
 * <pre>
 *           +--------------+
 *           | (1)          |
 *           | Read Twitter |
 *           |              |
 *           +--------------+
 *                  |
 *                  |
 *                  |
 *           +--------------+
 *           | (2)          |
 *           | Filter On    |
 *           | Charset      |
 *           +--------------+
 *                  |
 *                  |
 *                  |
 *           +--------------+
 *           | (3)          |
 *           | Find Team    |
 *           | Name         |
 *           +--------------+
 *                  |
 *                  |
 *                  |
 *           +--------------+
 *           | (4)          |
 *           | Filter On    |
 *           | Team Name    |
 *           +--------------+
 *                  |
 *                  |
 *                  |
 *           +--------------+
 *           | (5)          |
 *           | Filter On    |
 *           | Hashtag      |
 *           +--------------+
 *                  |
 *                  |
 *                  |
 *           +--------------+
 *           | (6)          |
 *           | Determine    |
 *           | Sentiment    |
 *           +--------------+
 *                  |
 *                  |
 *                  |
 *           +--------------+
 *           | (7)          |
 *           | Save Results |
 *           | To Hazelcast |
 *           +--------------+
 * </pre>
 * <ol>
 * <li><p>Read Twitter</p>
 * <p>This is a "<i>source</i>" stage.
 * </p>
 * <p>What this means in streaming terminology is this stage is a source of data. It doesn't receive
 * data from an earlier step in the pipeline, but instead somehow generates output for the next
 * stage in the pipeline.
 * </p>
 * <p>In this demo we use Twitter's <a href="https://github.com/twitter/hbc">Hosebird</a>, a
 * Java library to pull tweets by hashtag. This doesn't give tweets at a very fast rate,
 * but it's fine for a demo. Twitter can give tweets at a faster rate if you use one of
 * their commercial APIs.
 * </p>
 * <p>For real use you would want this step to be parallelised, so that each pipeline
 * job instance received unique tweets, and this spreads the input load across all
 * the JVMs.
 * </p>
 * </li>
 * <li><p>Filter On Charset</p>
 * <p>This is a filtering intermediate stage. Data records come in from the previous stage,
 * and are immediately passed to the next stage if the filter returns "{@code true}".
 * </p>
 * <p>The input is a "{@code java.util.String}" (and therefore so is the output). It
 * contains the text of a tweet.
 * </p>
 * <p>Filter here is on the charset of the string. If everything is ASCII characters the
 * filter returns "{@code true}" and the data goes to the next stage. In the context of
 * the World Cup, this will filter out tweets containing Cyrillic characters, symbols
 * such as emojis and so on. This is not because such tweets are useless, but because
 * the business logic is simplistic and can't handle them. So the principle being
 * demonstrated is to eliminate items from the stream as early as possible.
 * </p>
 * </li>
 * <li><p>Find Team Name</p>
 * <p>This is a mapping intermediate stage.
 * </p>
 * <p>Data records come in from the previous stage as "{@code java.util.String}"
 * and are passed on the next stage as a tuple of two "{@code java.util.String}".
 * So input in one form is mapped to output in another form.
 * </p>
 * <p>In more detail this stage is doing enrichment. The incoming string is
 * still part of the output, as the second field of a tuple. However a new
 * string has been added, as the first field of the tuple. This new field
 * enriches the message with information ; here it is data derived from the
 * input, but it could equally be reference data. For example if the tweet
 * has "{@code KSA}" this could be enriched by team lookup to be
 * "{@code Saudi Arabia}".
 * </p>
 * <p>As the enriched value is the first field of the tuple, it can be
 * used as a routing key. So, all tweets about the one team can go to
 * the one processor.
 * </p>
 * </li>
 * <li><p>Filter On Team Name</p>
 * <p>This is a filtering intermediate stage.
 * </p>
 * <p>Tweets are suppressed if the enrichment hasn't found a team name
 * in the tweet. This is clearer to do as a separate stage, so that's
 * what has been done. However, it would be more efficient to build
 * this into the previous stage.
 * </li>
 * <li><p>Filter On Hashtag</p>
 * <p>This is a filtering intermediate stage.
 * </p>
 * <p>What this is doing is removing tweets that mention teams irrelevant
 * to the current match. Eg. "The winner of {@code #JAPSEN} will likely
 * face Belgium in the next round". So the derived team name is Belgium
 * but the tweet isn't expressing any sentiment about the Japan v
 * Senegal game.
 * </p>
 * </li>
 * <li><p>Determine Sentiment</p>
 * <p>This is a mapping intermediate stage.
 * </p>
 * <p>This is where the business logic occurs. The text is analysed
 * to determine if it is positive, negative or neutral about a team.
 * The logic is very simplistic, but the business logic is not what
 * this demo is about.
 * </li>
 * <li><p>Save Results To Hazelcast</p>
 * <p>This is a "<i>sink</i>" stage.
 * </p>
 * <p>
 * The job here is potentially infinite. The Twitter connection will keep giving
 * tweets, although the rate will drop between games, so we derive sentimennt as
 * we go and need to make this visible.
 * </p>
 * <p>The method chosen is to use an entry processor to merge results into
 * a map in Hazelcast IMDG. Assuming the earlier filtering and aggregation
 * steps reduce the volume of input,
 * </p>
 * <p>Once the data is in a map in Hazelcast IMDG, we can use the CLI to
 * browse the content.
 * </li>
 * </ol>
 * </br>
 * <p>
 * This pipeline is essentially sequential, as the name "<i>pipeline</i>" suggests
 * with two minor observations around this.
 * </p>
 * <ol>
 * <li><p><b>Step 2B</b></p>
 * <p>
 * Step 1 creates a {@link com.hazelcast.jet.pipeline.StreamStage StreamStage}, as a
 * Java object named "{@code start}", a step in the pipeline that creates a continuous (not batch)
 * stream of data.
 * </p>
 * <p>Step 2 takes the input from step 1 and passes it's output to step 3. Step 3 to step 4. Step 4
 * to step 5. Etc
 * </p>
 * <p>However, note "{@code Step 2b}". This *also* takes the output from step 1. So the output from
 * Step 1 passes both into Step 2 and Step 2B. There is a branch in the chain, the same output from
 * step 1 goes to both, it's not 50/50, round-robin or whatever. Both subsequent chains get all
 * input from Step 1.
 * </p>
 * <p>The reason this is done is so Step 2B can dump it's input to the console, to demonstrate
 * more easily that Jet is actually doing something. After all, there may be nothing found in
 * Twitter for the given hashtag.
 * </p>
 * <p>This Step 2B is optional, remove it if you prefer.
 * </p>
 * </li>
 * <li><p><b>Step 7</b></p>
 * <p>Step 7 uses the "{@code DistributedFunctions.entryKey()}" method.
 * </p>
 * <p>This is for group, the output from Step 6 are grouped by the key of
 * Step 6's output. In other words, Step 6 is emitting {@code Map.Entry}
 * values. These are distributed based on the key -- and the key here is
 * the name of a team "{@code DEN}" for Denmark for example.
 * </p>
 * <p>
 * This means one instance of a Jet job running in one JVM may send its
 * output to another instance of this job, potentially running in a
 * different JVM. All entries for key "{@code DEN}" for Denmark are
 * routed to a single instance of this job. 
 * </p>
 * <p>
 * A Jet job will typically run as many instances as CPUs can support
 * per JVM, and there are typically multiple JVMs in the cluster.
 * </p>
 * <p>So what this means in effect is a single instance of the
 * entry processor class looks after the collation of sentiment
 * for a team.
 * </p>
 * <p>As this routing means moving data from one JVM to another,
 * across a network, it is best to do this as far down the
 * pipeline as possible so that data volumes are reduced. Here
 * it is the last stage, can't do better than that.
 * </p>
 * </li>
 * </ol>
 * </br>
 * <p><b>Improvements possible</b>
 * </p>
 * <p>There are many, here are some ideas
 * </p>
 * <ol>
 * <li>
 * <p><b>Geographic Exclusion</b></p>
 * <p>
 * If enabled by the user, some tweets can have their geographic position.
 * A tweet originating in Brasil about "@{code BRA}" (Brasil) is probably
 * a home fan and they are inclined to be biased about their team.
 * </p>
 * <p>
 * We could simply exclude these. Or we could dilute the rating for positive
 * comments since we expect them to support their side, yet note negative
 * comments since they are less likely to be lying if they are saying their
 * side is poor.
 * </p>
 * </li>
 * <li>
 * <p><b>Western characters</b></p>
 * <p>
 * In {@link com.hazelcast.demo.worldcup.jet.MyFilterOnCharset MyFilterOnCharset} tweets that aren't in
 * ASCII are ignored. This is obviously wrong for tweets about countries where their natural language
 * is expressed in different characters. Cyrillic for Russia, and so on.
 * </p>
 * </li>
 * <li>
 * <p><b>Words list</b></p>
 * <p>
 * The way words are scanned for in {@link com.hazelcast.demo.worldcup.jet.MyDetermineSentiment MyDetermineSentiment}
 * has no context. If we see "{@code good}" in a string such as "{@code the team's performance is not good}" then
 * the word "<i>good</i>" gives the derivation of a positive sentiment, but the prefix of "<i>not</i>" should make
 * it a negative sentiment.
 * </p>
 * <p>Looking for "<i>not good</i>" would be a start here. It would miss "<i>not doing good</i>" but would still be
 * an improvement. Sentiment analysis is all about the big picture, getting it wrong for individual tweets doesn't
 * matter too much.
 * </p>
 * </li>
 * </ol>
 */
public class TwitterPipeline {
	
	public static Pipeline buildPipeline(Properties properties, String hashtag) throws Exception {
		Pipeline pipeline = Pipeline.create();

		// (1) Pull from Twitter by hashtag
		StreamStage<String> start =
				pipeline
				.drawFrom(StreamTwitterP.streamTwitter(properties, hashtag))
				.withoutTimestamps();
		
		// (2a) Remove Tweets if the charset doesn't allow for easy interpretation
		start
        .filter(MyFilterOnCharset::westernChars)

        // (3) Try to determine the tweeter's team
        .map(MyDetermineTeam::determineTeam)
        
        // (4) Drop if can't determine tweeter's team abbreviation
        .filter(entry -> entry.getKey().length() == 3)

        // (5) Drop if not a team in the hashtag
        .filter(entry -> MyFilterOnTeam.keyIsInHashtag(entry.getKey(), hashtag))

        // (6) Determine sentiment, positive, negative or neutral
        .map(entry -> MyDetermineSentiment.determineSentiment(entry, hashtag))
		
        // (7) *MERGE* sentiment into map, this may cross JVM to find where the entry is stored
        .drainTo(
        	Sinks.mapWithEntryProcessor(
        			MyConstants.SENTIMENT, 
        	        Functions.entryKey(), 
        	        entry -> new MergeSentimentEntryProcessor(entry.getValue())
        		)
            )
        ;

		// (2b) [Optional] debug logging to screen of unfiltered tweets
		start
		.drainTo(Sinks.logger());
		
		return pipeline;
	}
}
