package pl.emmajk.scoreboard;

/** Thrown when a team is already in an in-progress match. */
public final class TeamAlreadyPlayingException extends Exception {

    /** Creates the exception for the given team. */
    public TeamAlreadyPlayingException(String team) {
        super("Team is already playing: " + team);
    }
}
