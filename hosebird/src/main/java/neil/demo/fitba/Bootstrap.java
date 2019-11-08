package neil.demo.fitba;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.server.JetBootstrap;

public class Bootstrap {

	// jet.sh submit hosebird-jar-with-dependencies.jar
	public static void main(String[] args) throws Exception {
		
		if (args.length!=2) {
			System.err.println(Bootstrap.class.getName() + ":usage: " 
					+ Bootstrap.class.getSimpleName() + " properties_file hashtags");
			System.exit(1);
		}
		
		Properties properties = loadProperties(args[0]);
		
		String hashtag = args[1];
		if (hashtag.length()!=6) {
			throw new RuntimeException("Hashtag '" + hashtag + "' should be exactly 6 chars");
		}
		
        JetInstance jetInstance = JetBootstrap.getInstance();
        
        Pipeline pipeline = TwitterPipeline.buildPipeline(properties, hashtag);

        JobConfig jobConfig = new JobConfig();
        jobConfig.setName(TwitterPipeline.class.getSimpleName());
        
        jetInstance.newJob(pipeline, jobConfig);
	}
	
	static Properties loadProperties(String fileName) throws Exception {
		Properties properties = new Properties();

		try (FileInputStream fileInputStream = new FileInputStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				) {
			
			properties.load(inputStreamReader);
		}
		
		// If property is named "my.xxx" duplicate to "xxx"
		properties.keySet().stream()
		.filter(key -> key.toString().startsWith("my.") && key.toString().length() > 3)
		.forEach(originalKey -> {
			Object value = properties.get(originalKey);
			String newKey = originalKey.toString().substring(3);
			properties.put(newKey, value);
		});
		
		//properties.list(System.out);
		
		var requiredKeys =
				new String[] { StreamTwitterP.CONSUMER_KEY, 
						StreamTwitterP.CONSUMER_SECRET,
						StreamTwitterP.TOKEN,
						StreamTwitterP.TOKEN_SECRET };
		
		// Validate
		for (String key : requiredKeys) {
			String value = properties.getOrDefault(key, "").toString();
			if (value.length()==0) {
				throw new RuntimeException("No properties in " + fileName + " for " + key);
			}
		}
		
		return properties;
	}
	
}
