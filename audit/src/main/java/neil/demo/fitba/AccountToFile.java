package neil.demo.fitba;

import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;

public class AccountToFile {

	protected static Pipeline buildPipeline(String dirName) {

		Pipeline pipeline = Pipeline.create();
		
		pipeline
		.drawFrom(
				Sources.mapJournal(MyConstants.ACCOUNT, JournalInitialPosition.START_FROM_OLDEST)
				)
				.withoutTimestamps()
		.map(entry -> entry.getKey().toString())  // Deliberate mistake, want getValue()
		.drainTo(Sinks.filesBuilder(dirName).build());
		
		return pipeline;
	}
}
