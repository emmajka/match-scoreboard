package pl.emmajk.scoreboard

/**
 * Invalid team name pairs shared by the specs, each with the exception it must cause.
 */
class InvalidTeamNames {

    static final List<List<Object>> PAIRS = [
            [null, "Brazil", NullPointerException],
            ["Spain", null, NullPointerException],
            ["", "Brazil", IllegalArgumentException],
            ["   ", "Brazil", IllegalArgumentException],
            ["Spain", "", IllegalArgumentException],
            ["Spain", "   ", IllegalArgumentException],
            ["́", "Brazil", IllegalArgumentException],
            [" Spain", "Brazil", IllegalArgumentException],
            ["Spain ", "Brazil", IllegalArgumentException],
            ["Spain", " Brazil", IllegalArgumentException],
            ["Spain", "Brazil ", IllegalArgumentException],
            ["Spain", "Spain", IllegalArgumentException],
            ["Spain", "SPAIN", IllegalArgumentException],
            ["Côte d'Ivoire", "Cote d'Ivoire", IllegalArgumentException],
    ]
}
