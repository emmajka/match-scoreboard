package pl.emmajk.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** In-memory scoreboard of live football matches. Not thread-safe. */
public final class Scoreboard {

    /** All matches in start order, finished ones included. */
    private final List<Match> matches = new ArrayList<>();

    /**
     * Starts a match at 0-0.
     *
     * @throws TeamAlreadyPlayingException if a team is in an in-progress match
     * @throws NullPointerException if a name is null
     * @throws IllegalArgumentException if a name is blank or both teams are the same
     */
    public void startMatch(String homeTeam, String awayTeam) throws TeamAlreadyPlayingException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Replaces the score of the in-progress match of the given pair.
     *
     * @throws MatchNotFoundException if the pair has no match
     * @throws MatchAlreadyFinishedException if the latest match of the pair is finished
     * @throws NullPointerException if a name is null
     * @throws IllegalArgumentException if a name is blank, the teams are the same or a score is negative
     */
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore)
            throws MatchNotFoundException, MatchAlreadyFinishedException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Finishes the in-progress match of the given pair.
     *
     * @throws MatchNotFoundException if the pair has no match
     * @throws MatchAlreadyFinishedException if the latest match of the pair is finished
     * @throws NullPointerException if a name is null
     * @throws IllegalArgumentException if a name is blank or both teams are the same
     */
    public void finishMatch(String homeTeam, String awayTeam)
            throws MatchNotFoundException, MatchAlreadyFinishedException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /** Returns in-progress matches, highest total score first, ties by most recently started. */
    public List<MatchSummary> getSummary() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Returns the in-progress match of the given pair, or empty if there is none.
     *
     * @throws NullPointerException if a name is null
     * @throws IllegalArgumentException if a name is blank or both teams are the same
     */
    public Optional<MatchSummary> findMatch(String homeTeam, String awayTeam) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
