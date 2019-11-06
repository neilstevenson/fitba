package neil.demo.fitba.web;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.core.IMap;

import lombok.extern.slf4j.Slf4j;
import neil.demo.fitba.MyConstants;

/**
 * <p>Display the "{@code Account}" map.
 * </p>
 */
@Controller
@RequestMapping("account")
@Slf4j
public class AccountController {

	private static final DecimalFormat TWO_DP = new DecimalFormat("0.00");
	private static final DateFormat ISO8601_SIMPLIFIED
		= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

	@Autowired
	private HazelcastInstance hazelcastInstance;
	@Autowired
	@Qualifier("currencySymbol")
	private String currencySymbol;

	/**
	 * <p>Basic account info, balances
	 * </p>
	 *
	 * @param httpSession
	 * @return
	 */
    @GetMapping("/index")
    public ModelAndView index(HttpSession httpSession) {
        log.info("index(), session={}", httpSession.getId());
        
        IMap<String, HazelcastJsonValue> accountMap = 
        		this.hazelcastInstance.getMap(MyConstants.ACCOUNT);

        ModelAndView modelAndView = new ModelAndView("account/index");

        List<String> columns = new ArrayList<>();
        columns.add("Account");
        columns.add("Name");
        columns.add("Balance");
        columns.add("Transaction Count");
        modelAndView.addObject("columns", columns);
        
        List<List<String>> data = new ArrayList<>();
        modelAndView.addObject("data", data);
        Set<String> keys = 
        		accountMap.keySet()
        		.stream()
        		.collect(Collectors.toCollection(TreeSet::new));
        
        keys.stream().forEach(key -> {
        	HazelcastJsonValue hazelcastJsonValue
        		= accountMap.get(key);
        	
        	// Field access coming in the next release
        	try {
            	JSONObject jsonObject
        			= new JSONObject(hazelcastJsonValue.toString());
        	
            	List<String> datum = new ArrayList<>();
        		datum.add(key);
			datum.add(jsonObject.getString("name"));
        		datum.add("" + this.currencySymbol + 
        			TWO_DP.format(Double.valueOf(jsonObject.getString("balance"))));
        		datum.add("" + jsonObject.getJSONArray("transactions").length());
        	
        		data.add(datum);
        	} catch (Exception e) {
        		log.error("index for key " + key, e);
        	}
        });
        
        return modelAndView;
    }

	/**
	 * <p>Detailed account info for one account, transactions
	 * </p>
	 *
	 * @param httpSession
	 * @return
	 */
    @GetMapping("/index2")
    public ModelAndView index2(HttpServletRequest httpServletRequest, HttpSession httpSession) {
    	String j_account = httpServletRequest.getParameter("j_account");
    	if (j_account==null) {
    		j_account="";
    	}
        log.info("index2({}), session={}", j_account, httpSession.getId());

        IMap<String, HazelcastJsonValue> accountMap = 
        		this.hazelcastInstance.getMap(MyConstants.ACCOUNT);

        ModelAndView modelAndView = new ModelAndView("account/index2");

        try {
            JSONObject jsonObject = new JSONObject(accountMap.get(j_account).toString());

        	modelAndView.addObject("account", 
            		j_account);
        	modelAndView.addObject("balance", 
        			TWO_DP.format(Double.valueOf(jsonObject.getString("balance"))));
        	
            List<String> columns = new ArrayList<>();
            columns.add("Amount");
            columns.add("Description");
            columns.add("Timestamp");
            modelAndView.addObject("columns", columns);

            List<List<String>> data = new ArrayList<>();
            modelAndView.addObject("data", data);
            
            JSONArray transactions = jsonObject.getJSONArray("transactions");
            for (int i=0 ; i<transactions.length() ; i++) {
            	List<String> datum = new ArrayList<>();
            	JSONObject transaction = (JSONObject) transactions.get(i);
            	
            	datum.add("" +
            			TWO_DP.format(Double.valueOf(transaction.getString("amount"))));

            	datum.add("" + transaction.getString("description"));
            	
            	String timestamp = transaction.getString("timestamp");
            	datum.add("" + ISO8601_SIMPLIFIED.parse(timestamp));
            	            	
            	data.add(datum);
        	}
        	
        } catch (Exception e) {
        	log.error("index2 for " + j_account, e);
        }
        
        return modelAndView;
    }

}
