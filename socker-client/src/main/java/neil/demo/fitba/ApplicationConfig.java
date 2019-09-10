package neil.demo.fitba;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.YamlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationConfig {
	
	@Bean
	public ClientConfig clientConfig() throws Exception {
		ClientConfig clientConfig =
                new YamlClientConfigBuilder("hazelcast-client.yaml").build();

		String clusterIp = System.getProperty("MY_CLUSTER_IP");

		var addressesInYaml = clientConfig.getNetworkConfig().getAddresses();
		
		if (clusterIp == null || clusterIp.length() == 0) {
			clientConfig.setInstanceName(clientConfig.getInstanceName() + "-localhost");
			
			log.info("Assuming not running in Docker, addresses={}", addressesInYaml);
		} else {
			clientConfig.getNetworkConfig().setAddresses(Arrays.asList(clusterIp));
			var addressesOverridden = clientConfig.getNetworkConfig().getAddresses();
			clientConfig.setInstanceName(clientConfig.getInstanceName() + "-docker");

			log.info("Assuming running in Docker, addresses={}, was {}",
					addressesOverridden, addressesInYaml);
		}

		return clientConfig;
	}

	@Bean
	public HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
		return HazelcastClient.newHazelcastClient(clientConfig);
	}
}
