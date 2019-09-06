package neil.demo.fitba;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyConstants {

	// Used in GO also, change in both or neither
    static final int CLASS_ID_MYDATASERIALIZABLEFACTORY = 1000;
    static final int CLASS_ID_TEAM = 1;

    // Map names
    static final String FIBONACCI = "Fibonacci";
    static final String CHAMPIONSHIP_LEAGUE = "Championship";
    static final String PREMIER_LEAGUE = "Premier";

    static final List<String> LEAGUES =
    		new ArrayList<>(
    				List.of(CHAMPIONSHIP_LEAGUE, PREMIER_LEAGUE)
    			);
    static final List<String> OTHER_MAPS =
    		new ArrayList<>(
    				List.of(FIBONACCI)
    			);
    static final List<String> MAP_NAMES = 
    		Stream.concat(LEAGUES.stream(), OTHER_MAPS.stream())
    			.collect(Collectors.toList());

}

