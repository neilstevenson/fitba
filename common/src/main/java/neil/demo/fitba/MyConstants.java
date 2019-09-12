package neil.demo.fitba;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyConstants {

	// Used in GO also, change in both or neither
	public static final int CLASS_ID_MYDATASERIALIZABLEFACTORY = 1000;
	public static final int CLASS_ID_TEAM = 1;

    // Map names
    public static final String ACCOUNT = "Account";
    public static final String FIBONACCI = "Fibonacci";
    public static final String JSESSIONID = "JSessionID";
    public static final String CHAMPIONSHIP_LEAGUE = "Championship";
    public static final String PREMIER_LEAGUE = "Premier";

    public static final List<String> LEAGUES =
    		new ArrayList<>(
    				List.of(CHAMPIONSHIP_LEAGUE, PREMIER_LEAGUE)
    			);
    public static final List<String> OTHER_MAPS =
    		new ArrayList<>(
    				List.of(ACCOUNT, FIBONACCI, JSESSIONID)
    			);
    public static final List<String> MAP_NAMES = 
    		Stream.concat(LEAGUES.stream(), OTHER_MAPS.stream())
    			.collect(Collectors.toList());

    // Attribute keys to store in a JSessionID web session
    public static final String ATTRIBUTE_MAIN_MENU_VISITS = "Main Menu";
    public static final String ATTRIBUTE_DEBUG_PAGE_VISITS = "Debug Page";
    public static final String ATTRIBUTE_CLIENT_ADDRESS = "Client Address";
}

