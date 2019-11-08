package neil.demo.fitba;

/**
 * <p>
 * Helpful functions needed in more than one place. Well, potentially.
 * </p>
 */
public class Util {
	private static final String ALPHABET_UPPERCASE = "abcdefghijklmnopqrstuvwxyz".toUpperCase();
	
	/**
	 * <p>
	 * Ensure String is a hashtag, begins with exactly one hash.
	 * </p>
	 * 
	 * @param A String, could be empty
	 * @return Input possibly prepended with one char
	 * @throws Exception if String empty
	 */
	public static String makeHashtag(String arg0) throws Exception {
		String s = arg0;
		while (s.charAt(0) == '#') {
			s = s.substring(1);
		}
		if (s.length() != 6) {
			throw new RuntimeException("String '" + arg0 + "' should have 6 chars, ignored hash");
		}
		for (int i=0 ; i < s.length() ; i++) {
			if (ALPHABET_UPPERCASE.indexOf(s.toUpperCase().charAt(i)) == -1) {
				throw new RuntimeException("Only letters allowed, not '" + s.charAt(i) + "' in '" + arg0 + "'");
			}
		}
		return '#' + s;
	}
	
	
	/** <p>Team hashtags, as suggested by FIFA
	 *  </p>
	 */
    public static final String[][] TEAM_HASHTAGS = new String[][] {
            { "Argentina", "ARG" },
            { "Australia", "AUS" },
            { "Belgium", "BEL" },
            { "Brazil", "BRA" },
            { "Colombia", "COL" },
            { "Costa Rica", "CRC" },
            { "Croatia", "CRO" },
            { "Denmark", "DEN" },
            { "Egypt", "EGY" },
            { "England", "ENG" },
            { "Spain", "SPA" },
            { "France", "FRA" },
            { "Georgia", "GEO" },
            { "Germany", "GER" },
            { "Iran", "IRN" },
            { "Iceland", "ISL" },
            { "Italia", "ITA" },
            { "Japan", "JAP" },
            { "Jordan", "JOR" },
            { "South Korea", "KOR" },
            { "Saudi Arabia", "KSA" },
            { "Mexico", "MEX" },
            { "Montenegro", "MON" },
            { "Morocco", "MAR" },
            { "Nigeria", "NGA" },
            { "Panama", "PAN" },
            { "Peru", "PER" },
            { "Poland", "POL" },
            { "Portugal", "POR" },
            { "Russia", "RUS" },
            { "Senegal", "SEN" },
            { "Serbia", "SRB" },
            { "Switzerland", "SUI" },
            { "Sweden", "SWD" },
            { "Tunisia", "TUN" },
            { "Turkey", "TUR" },
            { "Uruguay", "URU" },
    };


}
