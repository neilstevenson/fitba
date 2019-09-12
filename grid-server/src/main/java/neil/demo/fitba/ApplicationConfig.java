package neil.demo.fitba;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.ClasspathYamlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;

@Configuration
public class ApplicationConfig {
	
	private final int THIRTY_MINUTES_IN_SECONDS = 30 * 60;

	@Bean
	public Config config(MyTeamMapStoreFactory myTeamMapStoreFactory) {
		Config config = new ClasspathYamlConfig("hazelcast.yaml");
		
		for (String league : MyConstants.LEAGUES) {
			MapConfig leagueMapConfig = new MapConfig(league);
			
		    MapStoreConfig myMapStoreConfig = new MapStoreConfig();
		    myMapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
		    myMapStoreConfig.setFactoryImplementation(myTeamMapStoreFactory);
		    leagueMapConfig.setMapStoreConfig(myMapStoreConfig);
		    
		    leagueMapConfig.setTimeToLiveSeconds(THIRTY_MINUTES_IN_SECONDS);
		    
		    config.getMapConfigs().put(leagueMapConfig.getName(), leagueMapConfig);
		}
	    
	    return config;
	}

	
	@Bean
	public JetInstance jetInstance(Config config) {
		JetConfig jetConfig = new JetConfig().setHazelcastConfig(config);
		jetConfig.getInstanceConfig().setCooperativeThreadCount(1);
		return Jet.newJetInstance(jetConfig);
	}

	@Bean
	public HazelcastInstance hazelcastInstance(JetInstance jetInstance) {
		return jetInstance.getHazelcastInstance();
	}

}
