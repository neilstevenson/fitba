package neil.demo.fitba;

import com.hazelcast.jet.Traverser;
import com.hazelcast.jet.core.AbstractProcessor;
import com.hazelcast.jet.core.ProcessorMetaSupplier;
import com.hazelcast.jet.core.ProcessorSupplier;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamSource;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.hazelcast.jet.Traversers.traverseIterable;
import static com.hazelcast.jet.core.ProcessorMetaSupplier.preferLocalParallelismOne;

/**
 * <p>
 * A streaming source that connects and pull the tweets from Twitter using official Twitter client.
 * </p>
 * <p>
 * The hidden magic here is that Twitter collects the tweets matching the search tag into
 * a blocking queue. We output this queue's contents in the {@link #complete} method.
 * </p>
 */
public class StreamTwitterP extends AbstractProcessor {
	
	static final String CONSUMER_KEY = "consumerKey";
	static final String CONSUMER_SECRET = "consumerSecret";
	static final String TOKEN = "token";
	static final String TOKEN_SECRET = "tokenSecret";

    private final Properties properties;
    private final List<String> terms;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
    private final ArrayList<String> buffer = new ArrayList<>();

    private Traverser<String> traverser;
    private BasicClient client;

    private StreamTwitterP(Properties properties, String hashtag) {
        this.properties = properties;

		// For Twitter search, the keyword to use. Only one, that both sides might use equally.
		this.terms = new ArrayList<>();
		terms.add(hashtag);
    }

    /**
     * <p>
     * Initialize the job by connecting to Twitter.
     * We do the disconnect in {@link #close()}.
     * </p>
     */
    @Override
    protected void init(Context context) {
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(terms);

        String consumerKey = properties.getProperty(CONSUMER_KEY);
        String consumerSecret = properties.getProperty(CONSUMER_SECRET);
        String token = properties.getProperty(TOKEN);
        String tokenSecret = properties.getProperty(TOKEN_SECRET);

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);
        client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();
        client.connect();
    }

    /**
     * <p>
     * Invoked to drain the blocking queue, and to send tweets out of this processor
     * and into the next.
     * </p>
     * <p>
     * Tweets are JSON, much larger than 140 characters once all the metadata is
     * counted. We don't care about that, so just extract the text field from
     * the tweet.
     * </p>
     */
    @Override
    public boolean complete() {
        if (traverser == null) {
            if (queue.drainTo(buffer) == 0) {
                return false;
            } else {
                traverser = traverseIterable(buffer)
                        .map(JSONObject::new)
                        .filter(json -> json.has("text"))
                        .map(json -> json.getString("text"))
                        ;
            }
        }
        if (emitFromTraverser(traverser)) {
            buffer.clear();
            traverser = null;
        }
        return false;
    }

    /**
     * <p>
     * This job is not co-operative, meaning it does not give assurances that each run of the {@link #process()}
     * method will execute quickly. As it's depending on Twitter, we can't be sure.
     * </p>
     */
    @Override
    public boolean isCooperative() {
        return false;
    }

    /**
     * <p>
     * Disconnect from Twitter when the job is signalled to end.
     * </p>
     */
    @Override
    public void close() {
        if (client != null) {
            client.stop();
        }
    }

 
    /**
     * <p>
     * This method is used to build instances of the stream source on a JVM.
     * </p>
     * 
     * @param properties Connection properties for Twitter OAuth
     * @param tag String to search Twitter for
     * @return A stream source to use in a pipeline.
     * @throws Exception If the hashtag is invalid
     */
    public static ProcessorMetaSupplier streamTwitterP(Properties properties, String hashtag) throws Exception {
        return preferLocalParallelismOne(ProcessorSupplier.of(() -> new StreamTwitterP(properties, hashtag)));
    }

    /**
     * <p>
     * Create a {@link com.hazelcast.jet.pipeline.StreamSource StreamSource} for a
     * {@link com.hazelcast.jet.pipeline.Pipeline Pipeline} from this class. 
     * </p>
     * <p>A {@link com.hazelcast.jet.pipeline.StreamSource StreamSource} differs from a
     * {@link com.hazelcast.jet.pipeline.BatchSource BatchSource} in that it is infinite and
     * continuous, so the job using it needs to be told to stop processing input.
     * </p>
     * 
     * @param properties Connection properties for Twitter OAuth
     * @param tag String to search Twitter for
     * @return A stream source to use in a pipeline.
     * @throws Exception If the hashtag is invalid
     */
    public static StreamSource<String> streamTwitter(Properties properties, String tag) throws Exception {
		// Ensure hashtag begins with hash, throw Exception is invalid
		String hashtag = Util.makeHashtag(tag);
		
        return Sources.streamFromProcessor("twitterSource-'" + hashtag + "'", streamTwitterP(properties, hashtag));
    }

}
