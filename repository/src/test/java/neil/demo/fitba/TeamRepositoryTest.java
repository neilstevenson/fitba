package neil.demo.fitba;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = {"neil.demo.fitba"})
@EntityScan(basePackages = {"neil.demo.fitba"})
@SpringBootTest(classes={Object.class})
public class TeamRepositoryTest {

	static {
		System.setProperty("spring.main.banner-mode", "off");
	}

	@Autowired
	private TeamRepository teamRepository;

	@Test
	public void find_all() {
        final AtomicInteger count = new AtomicInteger();
        
        this.teamRepository.findAll()
        .forEach(record -> {
            assertThat("Record " + count.get(), record, notNullValue());
            log.info(record.toString());
            count.incrementAndGet();
        });
        
        assertThat("Rows exist", count.get(), greaterThan(0));
	}  

	@Test
	public void find_all_championship_teams() {
		int count = 0;
		String previousTeam = "";
		
		Iterator<String> iterator = this.teamRepository.findLeagueTeams(MyConstants.CHAMPIONSHIP_LEAGUE).iterator();
		while (iterator.hasNext()) {
			String currentTeam = iterator.next();
			assertThat("Row " + count, currentTeam, notNullValue());
			assertThat("Asecnding", currentTeam, greaterThan(previousTeam));
            log.info(currentTeam);
			count++;
			previousTeam = currentTeam;
		}
		
		assertThat("Rows exist", count, greaterThan(0));
	}
	
	@Test
	public void find_premiership_leader() {
		Collection<?> results = this.teamRepository.findTeamAndPoints(MyConstants.PREMIER_LEAGUE);

		assertThat("Results not null", results, notNullValue());
		assertThat("Results not empty", results.size(), greaterThan(0));
		
		Object firstResult = results.iterator().next();

		assertThat("First results not null", firstResult, notNullValue());
		assertThat("First results class", firstResult, instanceOf(Object[].class));

		Object[] tuple = (Object[]) firstResult;

		assertThat("Pair", tuple.length, equalTo(2));
		assertThat("Pair, 1st", tuple[0], instanceOf(String.class));
		assertThat("Pair, 2nd", tuple[1], instanceOf(Integer.class));
		log.info("{},{}", tuple[0],tuple[1]);
	}
	
}