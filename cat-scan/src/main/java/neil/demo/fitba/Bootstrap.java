package neil.demo.fitba;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.server.JetBootstrap;

public class Bootstrap {

	// jet.sh submit cat-scan.jar.
	public static void main(String[] args) {
		
        JetInstance jetInstance = JetBootstrap.getInstance();
        
        Pipeline pipeline = RealTimeImageRecognition.buildPipeline();

    	jetInstance.newJob(pipeline);
	}
}
