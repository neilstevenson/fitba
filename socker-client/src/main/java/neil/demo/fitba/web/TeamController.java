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
        
        if (!(j_league.equals(MyConstants.BUNDESLIGA) || 
        		j_league.equals(MyConstants.ZWEITE_BUNDESLIGA))) {
        	
        	log.error("League '{}' not recognised", j_league);
        	
        	// Bounce to front page
        	return new ModelAndView("/index");
        }

        Map<String, Team> leagueMap;
        if (j_league.equals(MyConstants.BUNDESLIGA)) {
        	leagueMap = this.hazelcastInstance.getMap(MyConstants.BUNDESLIGA);
        } else {
        	leagueMap = this.hazelcastInstance.getMap(MyConstants.ZWEITE_BUNDESLIGA);
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

        if (!(j_league.equals(MyConstants.BUNDESLIGA) || 
        		j_league.equals(MyConstants.ZWEITE_BUNDESLIGA))) {
        	
        	log.error("League '{}' not recognised", j_league);
        	
        	// Bounce to front page
        	return new ModelAndView("/index");
        }

        Map<String, Team> leagueMap;
        String[] keys;
        if (j_league.equals(MyConstants.BUNDESLIGA)) {
        	leagueMap = this.hazelcastInstance.getMap(MyConstants.BUNDESLIGA);
        	keys = new String[] {
        			"1. FC Köln",
        			"1899 Hoffenheim",
        			"Bayer Leverkusen",
        			"Bayern Munich",
        			"Borussia Dortmund",
        			"Borussia Mönchengladbach",
        			"Eintracht Frankfurt",
        			"FC Augsburg",
        			"Fortuna Düsseldorf",
        			"Hertha BSC",
        			"Mainz 05",
        			"RB Leipzig",
        			"SC Freiburg",
        			"SC Paderborn",
        			"Schalke 04",
        			"Union Berlin",
        			"VfL Wolfsburg",
        			"Werder Bremen" 
        			};
        } else {
        	leagueMap = this.hazelcastInstance.getMap(MyConstants.ZWEITE_BUNDESLIGA);
        	keys = new String[] {
        			"1. FC Heidenheim",
        			"1. FC Nürnberg",
        			"Arminia Bielefeld",
        			"Darmstadt 98",
        			"Dynamo Dresden",
        			"Erzgebirge Aue",
        			"FC St. Pauli",
        			"Greuther Fürth",
        			"Hamburger SV",
        			"Hannover 96",
        			"Holstein Kiel",
        			"Jahn Regensburg",
        			"Karlsruher SC",
        			"SV Sandhausen",
        			"VfB Stuttgart",
        			"VfL Bochum",
        			"VfL Osnabrück",
        			"Wehen Wiesbaden" 
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
