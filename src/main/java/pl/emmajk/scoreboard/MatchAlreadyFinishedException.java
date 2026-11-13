package pl.emmajk.scoreboard;

/** Thrown when the latest match of the given team pair is already finished. */
public final class MatchAlreadyFinishedException extends Exception {

    /** Creates the exception for the given team pair. */
    public MatchAlreadyFinishedException(String homeTeam, String awayTeam) {
        super("Match already finished: " + homeTeam + " vs " + awayTeam);
    }
}
