package neil.demo.fitba;

import java.util.Map.Entry;

import org.json.JSONObject;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.AbstractEntryProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Combine the sentiment provided as a constructor argument with the value
 * already stored in Hazelcast's {@link com.hazelcast.core.IMap IMap}.
 * </p>
 * <p>
 * This means the Jet job can be running continuously but we see results as they
 * are derived rather than have to end the job.
 * </p>
 * <p>
 * Hazelcast IMDG (<a href="https://hazelcast.org/>IMDG</a>) has more work to
 * do on a save than Hazelcast Jet (<a href="https://jet.hazelcast.org/>Jet</a>),
 * such as checking whether event listeners need to fire. Consequently this
 * mechanism needs Jet to have reduced the incoming data volume to a manageable
 * level before IMDG sees it.
 * </p>
 */
@Slf4j
@SuppressWarnings("serial")
public class MergeSentimentEntryProcessor extends AbstractEntryProcessor<String, HazelcastJsonValue> {
	
	private Sentiment sentiment;
	
	public MergeSentimentEntryProcessor(Sentiment arg0) {
		this.sentiment = arg0;
	}

	/**
	 * <p>
	 * Business logic : if we have sentiment figures, audment them. If
	 * we don't, save as new.
	 * </p>
	 */
	@Override
	public Object process(Entry<String, HazelcastJsonValue> entry) {
		// Get old
		HazelcastJsonValue oldValue = entry.getValue();
		HazelcastJsonValue newValue = null;
		
		// Merge or create
		if (oldValue != null) {
			newValue = mergeJson(oldValue, this.sentiment);
		} else {
			newValue = makeJson(this.sentiment);
		}
		
		// Save
		entry.setValue(newValue);
		
		// Caller doesn't care about return value
		return null;
	}

	/**
	 * <p>Turn the {@link Sentiment} object into JSON so interoperable.
	 * </p>
	 * 
	 * @param sentiment
	 * @return Input as JSON
	 */
	private HazelcastJsonValue makeJson(Sentiment sentiment) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{ ");
		
		sb.append("\"HomeNegative\" : \"" + sentiment.getHomeNegative() + "\"");
		sb.append(", ");
		sb.append("\"HomeNeutral\" : \"" + sentiment.getHomeNeutral() + "\"");
		sb.append(", ");
		sb.append("\"HomePositive\" : \"" + sentiment.getHomePositive() + "\"");
		sb.append(", ");
		
		sb.append("\"AwayNegative\" : \"" + sentiment.getAwayNegative() + "\"");
		sb.append(", ");
		sb.append("\"AwayNeutral\" : \"" + sentiment.getAwayNeutral() + "\"");
		sb.append(", ");
		sb.append("\"AwayPositive\" : \"" + sentiment.getAwayPositive() + "\"");
		
		sb.append(" }");
		
		return new HazelcastJsonValue(sb.toString());
	}
	
	
	/**
	 * <p>Incoming object is Java, what's stored is interoperable, so have
	 * to merge together.
	 * </p>
	 * 
	 * @param oldValue Existing JSON
	 * @param sentiment Structured Java object
	 * @return new JSON value
	 */
	private HazelcastJsonValue mergeJson(HazelcastJsonValue oldValue, Sentiment sentiment) {
		
		try {
			JSONObject jsonObject = new JSONObject(oldValue.toString());
			
			sentiment.setHomeNegative(jsonObject.getLong("HomeNegative") + sentiment.getHomeNegative());
			sentiment.setHomeNeutral(jsonObject.getLong("HomeNeutral") + sentiment.getHomeNeutral());
			sentiment.setHomePositive(jsonObject.getLong("HomePositive") + sentiment.getHomePositive());
			
			sentiment.setAwayNegative(jsonObject.getLong("AwayNegative") + sentiment.getHomeNegative());
			sentiment.setAwayNeutral(jsonObject.getLong("AwayNeutral") + sentiment.getAwayNeutral());
			sentiment.setAwayPositive(jsonObject.getLong("AwayPositive") + sentiment.getAwayPositive());

		} catch (Exception e) {
			log.error("mergeJson: {}", e.getMessage());
		}
		
		return makeJson(sentiment);
	}
}
