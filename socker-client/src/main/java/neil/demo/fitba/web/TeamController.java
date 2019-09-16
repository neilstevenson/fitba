package neil.demo.fitba.web;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hazelcast.core.HazelcastInstance;

import lombok.extern.slf4j.Slf4j;
import neil.demo.fitba.MyConstants;
import neil.demo.fitba.Team;

/**
 * <p>Show the teams in a league on a web page
 * </p>
 */
@Controller
@RequestMapping("team")
@Slf4j
public class TeamController {

	@Autowired
	private HazelcastInstance hazelcastInstance;
	
    @GetMapping("/index")
    public ModelAndView index(HttpServletRequest httpServletRequest, HttpSession httpSession) {
    	String j_league = httpServletRequest.getParameter("j_league");
        if (j_league==null) {
        	j_league="";
        }
        log.info("index({}), session={}", j_league, httpSession.getId());
        
        if (!(j_league.equals(MyConstants.CHAMPIONSHIP_LEAGUE) || 
        		j_league.equals(MyConstants.PREMIER_LEAGUE))) {
        	
        	log.error("League '{}' not recognised", j_league);
        	
        	// Bounce to front page
        	return new ModelAndView("/index");
        }

        Map<String, Team> leagueMap;
        if (j_league.equals(MyConstants.CHAMPIONSHIP_LEAGUE)) {
        	leagueMap = this.hazelcastInstance.getMap(MyConstants.CHAMPIONSHIP_LEAGUE);
        } else {
        	leagueMap = this.hazelcastInstance.getMap(MyConstants.PREMIER_LEAGUE);
        }

        // Unordered
        Set<String> keys = 
                leagueMap.keySet()
                .stream()
                .collect(Collectors.toCollection(HashSet::new));
        
        // Ordered by position in league
        Map<Integer, Team> data = new TreeMap<>();
        keys.stream().forEach(key -> {
        	Team team = leagueMap.get(key);
        	data.put(team.getPos(), team);
        });
        
        ModelAndView modelAndView = new ModelAndView("team/index");
        modelAndView.addObject("data", data);
        modelAndView.addObject("j_league", j_league);
                
        return modelAndView;
    }

    /*TODO Client-side doesn't have access to @Repository so cannot easily
     * establish the keys to reload (team names). Change this to an
     * executor rather than hard-coded.
     */
    @GetMapping("/reload")
    public ModelAndView reload(HttpServletRequest httpServletRequest, HttpSession httpSession) {
    	String j_league = httpServletRequest.getParameter("j_league");
        if (j_league==null) {
        	j_league="";
        }
        log.info("reload({}), session={}", j_league, httpSession.getId());

        if (!(j_league.equals(MyConstants.CHAMPIONSHIP_LEAGUE) || 
        		j_league.equals(MyConstants.PREMIER_LEAGUE))) {
        	
        	log.error("League '{}' not recognised", j_league);
        	
        	// Bounce to front page
        	return new ModelAndView("/index");
        }

        Map<String, Team> leagueMap;
        String[] keys;
        if (j_league.equals(MyConstants.CHAMPIONSHIP_LEAGUE)) {
        	leagueMap = this.hazelcastInstance.getMap(MyConstants.CHAMPIONSHIP_LEAGUE);
        	keys = new String[] {
        			"Barnsley",
        			"Birmingham City",
        			"Blackburn Rovers",
        			"Brentford",
        			"Bristol City",
        			"Cardiff City",
        			"Charlton Athletic",
        			"Derby County",
        			"Fulham",
        			"Huddersfield Town",
        			"Hull City",
        			"Leeds United",
        			"Luton Town",
        			"Middlesbrough",
        			"Millwall",
        			"Nottingham Forest",
        			"Preston North End",
        			"Queens Park Rangers",
        			"Reading",
        			"Sheffield Wednesday",
        			"Stoke City",
        			"Swansea City",
        			"West Bromwich Albion",
        			"Wigan Athletic"
        			};
        } else {
        	leagueMap = this.hazelcastInstance.getMap(MyConstants.PREMIER_LEAGUE);
        	keys = new String[] {
        			"Arsenal",
        			"Aston Villa",
        			"Bournemouth",
        			"Brighton & Hove Albion",
        			"Burnley",
        			"Chelsea",
        			"Crystal Palace",
        			"Everton",
        			"Leicester City",
        			"Liverpool",
        			"Manchester City",
        			"Manchester United",
        			"Newcastle United",
        			"Norwich City",
        			"Sheffield United",
        			"Southampton",
        			"Tottenham Hotspur",
        			"Watford",
        			"West Ham United",
        			"Wolverhampton Wanderers"
        	};
        }
        for (int i = 0 ; i < keys.length ; i++) {
        	leagueMap.get(keys[i]);
        }
        log.info("reload({}), requested {}, loaded {}, for session={}", j_league, 
        		keys.length, leagueMap.size(), httpSession.getId());
        
        return this.index(httpServletRequest, httpSession);
    }

}
