package neil.demo.fitba;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.server.JetBootstrap;

public class Bootstrap {

	// jet.sh submit cat-scan-jar-with-dependencies.jar
	public static void main(String[] args) {
		
        JetInstance jetInstance = JetBootstrap.getInstance();
        
        Pipeline pipeline = RealTimeImageRecognition.buildPipeline();

        JobConfig jobConfig = new JobConfig();
        jobConfig.setName(RealTimeImageRecognition.class.getSimpleName());

        jetInstance.newJob(pipeline, jobConfig);
	}
}
