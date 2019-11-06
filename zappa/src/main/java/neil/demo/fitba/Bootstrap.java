package neil.demo.fitba;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.server.JetBootstrap;

public class Bootstrap {

	// jet.sh submit zappa-jar-with-dependencies.jar
	public static void main(String[] args) {
		
        JetInstance jetInstance = JetBootstrap.getInstance();
        
        Pipeline pipeline = AccountFromKafka.buildPipeline();
        
        JobConfig jobConfig = new JobConfig();
        jobConfig.setName(AccountFromKafka.class.getName());

    	jetInstance.newJob(pipeline, jobConfig);
	}
}
