package pl.emmajk.scoreboard;

/** Thrown when no match exists for the given team pair. */
public final class MatchNotFoundException extends Exception {

    /** Creates the exception for the given team pair. */
    public MatchNotFoundException(String homeTeam, String awayTeam) {
        super("No match found for " + homeTeam + " vs " + awayTeam);
    }
}
