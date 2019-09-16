package neil.demo.fitba.web;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import lombok.extern.slf4j.Slf4j;
import neil.demo.fitba.MyConstants;

/**
 * <p>Show the saved Fibonacci calculations on a web page
 * </p>
 */
@Controller
@RequestMapping("fibonacci")
@Slf4j
public class FibonacciController {

	@Autowired
	private HazelcastInstance hazelcastInstance;
	
    @GetMapping("/index")
    public ModelAndView index(HttpSession httpSession) {
        log.info("index(), session={}", httpSession.getId());
        
        IMap<Long, Long> fibonacciMap = 
        		this.hazelcastInstance.getMap(MyConstants.FIBONACCI);

        ModelAndView modelAndView = new ModelAndView("fibonacci/index");

        Set<Long> keys = 
                        fibonacciMap.keySet()
                        .stream()
                        .collect(Collectors.toCollection(TreeSet::new));

        Map<Long, Long> data = new TreeMap<>();
        keys.stream().forEach(key -> 
        	data.put(key, fibonacciMap.get(key)));
        
        modelAndView.addObject("data", data);
                
        return modelAndView;
    }
}
