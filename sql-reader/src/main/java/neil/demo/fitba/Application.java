package neil.demo.fitba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	static {
		System.setProperty("spring.jpa.properties.hibernate.show_sql", "false");
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
