package pl.emmajk.scoreboard

/**
 * Invalid team name pairs shared by the specs, each with a description and the exception it must cause.
 */
class InvalidTeamNames {

    /**
     * A combining accent without a letter, which no editor shows clearly.
     */
    static final String COMBINING_ACUTE = String.valueOf((char) 0x0301)

    /**
     * A combining circumflex, used to write "ô" as "o" plus an accent.
     */
    static final String COMBINING_CIRCUMFLEX = String.valueOf((char) 0x0302)

    /**
     * A space that Java's strip() does not remove.
     */
    static final String NO_BREAK_SPACE = String.valueOf((char) 0x00A0)

    /**
     * Every kind of invalid input, used by the spec of the validation itself.
     */
    static final List<List<Object>> PAIRS = [
            ["null home", null, "Brazil", NullPointerException],
            ["null away", "Spain", null, NullPointerException],
            ["null away after empty home", "", null, NullPointerException],
            ["null home before empty away", null, "", NullPointerException],
            ["empty home", "", "Brazil", IllegalArgumentException],
            ["blank home", "   ", "Brazil", IllegalArgumentException],
            ["empty away", "Spain", "", IllegalArgumentException],
            ["blank away", "Spain", "   ", IllegalArgumentException],
            ["accent only home", COMBINING_ACUTE, "Brazil", IllegalArgumentException],
            ["leading space home", " Spain", "Brazil", IllegalArgumentException],
            ["trailing space home", "Spain ", "Brazil", IllegalArgumentException],
            ["leading space away", "Spain", " Brazil", IllegalArgumentException],
            ["trailing space away", "Spain", "Brazil ", IllegalArgumentException],
            ["tab and newline padding", "\tSpain\n", "Brazil", IllegalArgumentException],
            ["same team", "Spain", "Spain", IllegalArgumentException],
            ["same team in another case", "Spain", "SPAIN", IllegalArgumentException],
            ["same team in another accent", "Côte d'Ivoire", "Cote d'Ivoire", IllegalArgumentException],
    ]

    /**
     * One row of each kind of invalid input, used by the specs of the operations to prove that they validate.
     */
    static final List<List<Object>> REPRESENTATIVE = PAIRS.findAll {
        it[0] in ["null home", "null away", "blank home", "trailing space away", "same team in another case"]
    }
}
