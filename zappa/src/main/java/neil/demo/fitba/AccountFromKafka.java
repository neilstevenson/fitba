package neil.demo.fitba;

//TODO import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.function.Functions;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.map.EntryProcessor;

//TODO: Fix generics, use SimpleImmutableEntry instead of Tuple2
public class AccountFromKafka {

	static final String KAFKA_BOOTSTRAP_SERVERS = "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094";
	static final String KAFKA_TOPIC_NAME = "account";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static Pipeline buildPipeline() {
		Properties properties = new Properties();
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());

		Pipeline pipeline = Pipeline.create();

		StreamStage<Entry<Object, Object>> input = 
				pipeline
				.drawFrom(KafkaSources.kafka(properties, KAFKA_TOPIC_NAME))
				.withoutTimestamps();
		 
		input
		.filter(entry -> new JSONObject(entry.getValue().toString()).getString("kind").equals("baseline"))
		.map(entry -> {
				JSONObject jsonObject = new JSONObject(entry.getValue().toString());

				String balance = jsonObject.getString("balance");
				String name = jsonObject.getString("name");
				
				String valueStr = "{ \"account\" : \"" + entry.getKey() + "\","
						+ " \"name\" : \"" + name + "\","
						+ " \"balance\" : \"" + balance + "\","
						+ " \"transactions\" : [] }";
				
				HazelcastJsonValue hazelcastJsonValue
					= new HazelcastJsonValue(valueStr);

				return Tuple2.tuple2(entry.getKey(), hazelcastJsonValue);
			})
		.drainTo(Sinks.map("Account"));

		input
		.filter(entry -> 
			new JSONObject(entry.getValue().toString()).getString("kind").equals("transaction"))
		.drainTo((Sink<? super Entry<Object, Object>>) 
					Sinks.mapWithEntryProcessor("Account",
						Functions.entryKey(),
						entry -> ((EntryProcessor) 
							new AccountMergeEntryProcessor(entry.getValue().toString()))));

		// Optional!
		input.drainTo(Sinks.logger(entry -> entry.getValue().toString()));

		return pipeline;
	}
}
