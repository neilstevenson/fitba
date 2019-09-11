package neil.demo.fitba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	static {
		System.setProperty("java.awt.headless", "false");
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
