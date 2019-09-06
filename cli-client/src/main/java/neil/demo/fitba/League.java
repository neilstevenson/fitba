package neil.demo.fitba;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;

import lombok.extern.slf4j.Slf4j;

@ShellComponent
@Slf4j
public class League {

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@SuppressWarnings("rawtypes")
	@ShellMethod(key = "LEADERS", value = "Who is winning")
	public String leaders(@ShellOption(value = "league") String league) {
		if (!MyConstants.LEAGUES.contains(league)) {
			return "Unknown league, only " + MyConstants.LEAGUES + " known";
		}
		
		// Hazelcast extension to java.util.Map
		IMap<String, Team> leagueMap = this.hazelcastInstance.getMap(league);
		
		if (leagueMap.isEmpty()) {
			return "Map '" + leagueMap.getName() + "' empty, probably expiry";
		}

		// Find highest number of points
		int maxPts = leagueMap.aggregate(Aggregators.integerMax("pts"));

		// May be a tie for the lead, not just a single leader
        Predicate predicate = new SqlPredicate("pts = " + maxPts);

        Set<String> keySet = leagueMap.keySet(predicate);

        if (keySet.size()==1) {
        	return "League leader on " + maxPts + " points, " + keySet;
        } else {
        	return "League leaders on " + maxPts + " points, " + keySet;
        }
	}

	@ShellMethod(key = "CHAMPIONSHIP_BONUS", value = "Add 1 point to a team")
	public String championshipBonus(@ShellOption(value = "team") String teamName) {
		IMap<String, Team> leagueMap = this.hazelcastInstance.getMap(MyConstants.CHAMPIONSHIP_LEAGUE);
		if (leagueMap.isEmpty()) {
			log.warn("Map '{}' empty, probably expiry", leagueMap.getName());
		}

		// If map empty (expiry), key access will pull in from MySql store
		
		Team teamBefore = leagueMap.get(teamName);
		if (teamBefore != null) {
			Team teamAfter = teamBefore.clone();
			teamAfter.setPts(teamBefore.getPts() + 1);
			if (leagueMap.replace(teamAfter.getName(), teamBefore, teamAfter)) {
				return teamName + " updated to " + teamAfter.getPts() + " points.";
			} else {
				return teamName + " not updated, concurrent access?";
			}
		} else {
			return teamName + " not found in '" + leagueMap.getName() + "' map";
		}
	}
	
	@ShellMethod(key = "GOALS", value = "Spread of goals")
	public void goals(@ShellOption(value = "league") String league) {
		if (!MyConstants.LEAGUES.contains(league)) {
			System.out.println("Unknown league, only " + MyConstants.LEAGUES + " known");
			return;
		}
		
		// Hazelcast extension to java.util.Map
		IMap<String, Team> leagueMap = this.hazelcastInstance.getMap(league);
		
		if (leagueMap.isEmpty()) {
			System.out.println("Map '" + leagueMap.getName() + "' empty, probably expiry");
			return;
		}
		
		IExecutorService executorService =
                hazelcastInstance.getExecutorService("default");

        System.out.println("======================================" +
        		"======================================");

        // Looks local but is remote
        int count = leagueMap.size();
        System.out.println("Number of teams....................: " + count);
        
        // Remote calc
        double totalGoalsFor = leagueMap.aggregate(Aggregators.integerSum("gf"));
        System.out.println("Total goals scored.................: " + totalGoalsFor);

        double average = totalGoalsFor / count;
        System.out.println("Average goals scored per team......: " + average);

        SumDifferenceSquaredFn sumDifferenceSquaredFn
                = new SumDifferenceSquaredFn(league, average);
        
        // Remote calc
        Map<Member, Future<Double>> results =
        		executorService.submitToAllMembers(sumDifferenceSquaredFn);

        double total = 0;
        for (Entry<Member, Future<Double>> entry: results.entrySet()) {
            try {
                double subTotal = entry.getValue().get();
                        System.out.println("Sub-Total of Difference Squared....: "
                        		+ subTotal + " from " + entry.getKey());
                
                total += subTotal;
            } catch (Exception e) {
                System.err.println(entry.getKey());
                e.printStackTrace();
            }
        }
        System.out.println("Total of Difference Squared........: " + total);
                
        double averageTotal = total / count;
        System.out.println("Average of Difference Squared......: " + averageTotal);
        
        double stdev = Math.sqrt(averageTotal);
        System.out.println("Standard Deviation.................: " + stdev);
                
        System.out.println("======================================" +
        		"======================================");
	}
}