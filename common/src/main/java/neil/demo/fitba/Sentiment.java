package neil.demo.fitba;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * Opinion about a game, good words, bad words, about each side.
 * </p>
 */
@SuppressWarnings("serial")
@Data
public class Sentiment implements Serializable {

        private long   homePositive;
        private long   homeNeutral;
        private long   homeNegative;
        
        private long   awayPositive;
        private long   awayNeutral;
        private long   awayNegative;
}
