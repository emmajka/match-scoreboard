package pl.emmajk.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory scoreboard of live football matches. Not thread-safe.
 */
public final class Scoreboard {

    /**
     * All matches in start order, finished ones included.
     */
    private final List<Match> matches = new ArrayList<>();

    /**
     * Starts a match at 0-0.
     *
     * @throws TeamAlreadyPlayingException if a team is in an in-progress match
     * @throws NullPointerException        if a name is null
     * @throws IllegalArgumentException    if a name is blank or both teams are the same
     */
    public void startMatch(String homeTeam, String awayTeam) throws TeamAlreadyPlayingException {
        validatePair(homeTeam, awayTeam);
        String homeLookupKey = Match.lookupKey(homeTeam);
        String awayLookupKey = Match.lookupKey(awayTeam);

        for (Match match : matches) {
            if (match.isInProgress() && match.isPlayedBy(homeLookupKey)) {
                throw new TeamAlreadyPlayingException(homeTeam);
            }
            if (match.isInProgress() && match.isPlayedBy(awayLookupKey)) {
                throw new TeamAlreadyPlayingException(awayTeam);
            }
        }

        matches.add(new Match(homeTeam.strip(), awayTeam.strip(), 0, 0, MatchStatus.IN_PROGRESS));
    }

    /**
     * Replaces the score of the in-progress match of the given pair.
     *
     * @throws MatchNotFoundException        if the pair has no match
     * @throws MatchAlreadyFinishedException if the latest match of the pair is finished
     * @throws NullPointerException          if a name is null
     * @throws IllegalArgumentException      if a name is blank, the teams are the same or a score is negative
     */
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore)
            throws MatchNotFoundException, MatchAlreadyFinishedException {
        validatePair(homeTeam, awayTeam);
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Scores must not be negative");
        }

        int index = indexOfInProgress(homeTeam, awayTeam);
        matches.set(index, matches.get(index).withScore(homeScore, awayScore));
    }

    /**
     * Finishes the in-progress match of the given pair.
     *
     * @throws MatchNotFoundException        if the pair has no match
     * @throws MatchAlreadyFinishedException if the latest match of the pair is finished
     * @throws NullPointerException          if a name is null
     * @throws IllegalArgumentException      if a name is blank or both teams are the same
     */
    public void finishMatch(String homeTeam, String awayTeam)
            throws MatchNotFoundException, MatchAlreadyFinishedException {
        validatePair(homeTeam, awayTeam);

        int index = indexOfInProgress(homeTeam, awayTeam);
        matches.set(index, matches.get(index).finished());
    }

    /**
     * Returns in-progress matches, highest total score first, ties by most recently started.
     */
    public List<MatchSummary> getSummary() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Returns the in-progress match of the given pair, or empty if there is none.
     *
     * @throws NullPointerException     if a name is null
     * @throws IllegalArgumentException if a name is blank or both teams are the same
     */
    public Optional<MatchSummary> findMatch(String homeTeam, String awayTeam) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Checks that a team pair has no null or blank name and is not the same team twice.
     */
    private static void validatePair(String homeTeam, String awayTeam) {
        Objects.requireNonNull(homeTeam, "home team must not be null");
        Objects.requireNonNull(awayTeam, "away team must not be null");
        String homeLookupKey = Match.lookupKey(homeTeam);
        String awayLookupKey = Match.lookupKey(awayTeam);
        if (homeLookupKey.isEmpty() || awayLookupKey.isEmpty()) {
            throw new IllegalArgumentException("Team names must not be blank");
        }
        if (homeLookupKey.equals(awayLookupKey)) {
            throw new IllegalArgumentException("A team cannot play itself");
        }
    }

    /**
     * Returns the index of the in-progress match of the pair, or throws if the pair has none.
     */
    private int indexOfInProgress(String homeTeam, String awayTeam)
            throws MatchNotFoundException, MatchAlreadyFinishedException {
        int index = -1;
        for (int i = matches.size() - 1; i >= 0; i--) {
            if (matches.get(i).isPair(Match.lookupKey(homeTeam), Match.lookupKey(awayTeam))) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            throw new MatchNotFoundException(homeTeam, awayTeam);
        }

        if (!matches.get(index).isInProgress()) {
            throw new MatchAlreadyFinishedException(homeTeam, awayTeam);
        }

        return index;
    }
}
