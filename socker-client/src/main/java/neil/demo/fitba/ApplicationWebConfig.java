package neil.demo.fitba;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.web.WebFilter;

/**
 * <p>
 * Additional configuration so that this JVM is both a Hazelcast client and
 * web-server, and stores the HTTP sessions of the web-server via the Hazelcast
 * client into the remote Hazelcast server(s).
 * </p>
 */
@Configuration
public class ApplicationWebConfig {

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Bean
	public WebFilter webFilter() {
		Properties properties = new Properties();

		properties.put("map-name", MyConstants.JSESSIONID);
		properties.put("instance-name", this.hazelcastInstance.getName());
		properties.put("sticky-session", "false");
		properties.put("use-client", "true");

		return new WebFilter(properties);
	}
}