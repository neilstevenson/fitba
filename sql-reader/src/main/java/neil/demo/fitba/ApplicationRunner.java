package neil.demo.fitba;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationRunner implements CommandLineRunner {

	@Autowired
	private TeamRepository teamRepository;

	@Override
	public void run(String... args) throws Exception {
		for (String league : MyConstants.LEAGUES) {
			
			System.out.println(league);
			System.out.println("============");

			Collection<?> results = this.teamRepository.findTeamAndPoints(league);
					
			results
			.stream()
			.forEach(tuple -> {
				Object[] pair = (Object[]) tuple;
				System.out.printf("%s %d%n", pair[0], pair[1]);
			});
		
			System.out.println("======================================");
			System.out.printf("Count : %d%n",  results.size());
			System.out.println("======================================");
		}
	}
	
}
