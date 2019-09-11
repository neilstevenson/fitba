package neil.demo.fitba;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationInitializer implements CommandLineRunner {

    @Autowired
    private HazelcastInstance hazelcastInstance;

	@Override
	public void run(String... args) throws Exception {
		for (String mapName : MyConstants.MAP_NAMES) {
			IMap<?, ?> map = this.hazelcastInstance.getMap(mapName);
			log.info("Map '{}'.size()=={}", map.getName(), map.size());
		}
	}
    
}
