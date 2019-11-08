package neil.demo.fitba;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.server.JetBootstrap;

public class Bootstrap {

	// jet.sh submit audit-jar-with-dependencies.jar
	public static void main(String[] args) {
		
        JetInstance jetInstance = JetBootstrap.getInstance();

        String timestamp = "." + System.currentTimeMillis();
        
        String dirName = AccountToFile.class.getSimpleName() + timestamp;
        
        Pipeline pipeline = AccountToFile.buildPipeline(dirName);
        
        JobConfig jobConfig = new JobConfig();
        jobConfig.setName(AccountToFile.class.getSimpleName() + timestamp);

    	jetInstance.newJob(pipeline, jobConfig);
	}
}
