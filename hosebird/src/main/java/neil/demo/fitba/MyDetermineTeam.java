package neil.demo.fitba;

import java.util.Arrays;
import java.util.HashMap;

import com.hazelcast.jet.datamodel.Tuple2;

/**
 * <p>
 * Determine if the tweet contains exactly on team name,
 * so is presumably about that one team.
 * </p>
 */
public class MyDetermineTeam {
	
	private static final HashMap<String, String> TEAMS;
	private static final String[] TEAMS_KEYS;
	
	static {
		TEAMS = new HashMap<>();
		Arrays.stream(Util.TEAM_HASHTAGS).forEach(line -> {
			TEAMS.put(line[0], line[1]);
		});
		
		TEAMS_KEYS = TEAMS.keySet().toArray(new String[TEAMS.size()]);
	}

	public static Tuple2<String, String> determineTeam(String text) {
		int count = 0;
		String team = "";
		
		for (int i = 0; i < TEAMS_KEYS.length; i++) {
			if (text.toLowerCase().contains(TEAMS_KEYS[i].toLowerCase())) {
				count++;
				if (count == 1) {
					team = TEAMS.get(TEAMS_KEYS[i]);
				} else {
					team = "";
				}
			}
		}
		
		return Tuple2.tuple2(team, text);
	}
}
