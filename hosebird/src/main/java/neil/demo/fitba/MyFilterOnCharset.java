package neil.demo.fitba;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * A filter to indicate if text has only characters we can
 * analyse.
 * </p>
 */
public class MyFilterOnCharset {

	private static CharsetEncoder charsetEncoder
		= StandardCharsets.US_ASCII.newEncoder();
	
    public static boolean westernChars(String s) {
    	try {
        	return charsetEncoder.canEncode(s);
    	} catch (Exception e) {
    		return false;
    	}
    }

}
