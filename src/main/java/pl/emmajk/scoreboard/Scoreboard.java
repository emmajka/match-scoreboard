package pl.emmajk.scoreboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * In-memory scoreboard of live football matches. Not thread-safe.
 * Team names are compared ignoring case and accents, and the caller keeps the home and away order consistent.
 */
public final class Scoreboard {

    /**
     * All matches in start order, finished ones included.
     */
    private final List<Match> matches = new ArrayList<>();

    /**
     * Starts a match at 0-0.
     *
     * @throws TeamAlreadyPlayingException if a team is in an in-progress match, as home or away team
     * @throws NullPointerException        if a name is null
     * @throws IllegalArgumentException    if a name is blank or has surrounding whitespace, or both teams are the same
     */
    public void startMatch(String homeTeam, String awayTeam) throws TeamAlreadyPlayingException {
        MatchParticipants participants = MatchParticipants.of(homeTeam, awayTeam);

        for (Match match : matches) {
            if (match.isInProgress() && match.participants().isPlayedBy(participants.homeKey())) {
                throw new TeamAlreadyPlayingException(homeTeam);
            }
            if (match.isInProgress() && match.participants().isPlayedBy(participants.awayKey())) {
                throw new TeamAlreadyPlayingException(awayTeam);
            }
        }

        matches.add(new Match(homeTeam, awayTeam, participants, 0, 0, MatchStatus.IN_PROGRESS));
    }

    /**
     * Replaces the score of the in-progress match with the given home and away team.
     *
     * @throws MatchNotFoundException        if no match has these home and away teams
     * @throws MatchAlreadyFinishedException if the latest match with these home and away teams is finished
     * @throws NullPointerException          if a name is null
     * @throws IllegalArgumentException      if a name is invalid, the teams are the same or a score is negative
     */
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore)
            throws MatchNotFoundException, MatchAlreadyFinishedException {
        MatchParticipants participants = MatchParticipants.of(homeTeam, awayTeam);
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Scores must not be negative");
        }

        int index = indexOfInProgress(participants, homeTeam, awayTeam);
        matches.set(index, matches.get(index).withScore(homeScore, awayScore));
    }

    /**
     * Finishes the in-progress match with the given home and away team.
     *
     * @throws MatchNotFoundException        if no match has these home and away teams
     * @throws MatchAlreadyFinishedException if the latest match with these home and away teams is finished
     * @throws NullPointerException          if a name is null
     * @throws IllegalArgumentException      if a name is invalid or both teams are the same
     */
    public void finishMatch(String homeTeam, String awayTeam)
            throws MatchNotFoundException, MatchAlreadyFinishedException {
        MatchParticipants participants = MatchParticipants.of(homeTeam, awayTeam);

        int index = indexOfInProgress(participants, homeTeam, awayTeam);
        matches.set(index, matches.get(index).finished());
    }

    /**
     * Returns in-progress matches, highest total score first, ties by most recently started.
     */
    public List<MatchSummary> getSummary() {
        return matches.reversed().stream()
                .filter(Match::isInProgress)
                .sorted(Comparator.comparingInt(Match::totalScore).reversed())
                .map(Match::toSummary)
                .toList();
    }

    /**
     * Returns the in-progress match with the given home and away team, or empty if there is none.
     *
     * @throws NullPointerException     if a name is null
     * @throws IllegalArgumentException if a name is invalid or both teams are the same
     */
    public Optional<MatchSummary> findMatch(String homeTeam, String awayTeam) {
        MatchParticipants participants = MatchParticipants.of(homeTeam, awayTeam);

        return matches.reversed().stream()
                .filter(match -> match.participants().equals(participants))
                .findFirst()
                .filter(Match::isInProgress)
                .map(Match::toSummary);
    }

    /**
     * Returns the index of the in-progress match of the participants, or throws if there is none.
     */
    private int indexOfInProgress(MatchParticipants participants, String homeTeam, String awayTeam)
            throws MatchNotFoundException, MatchAlreadyFinishedException {
        int index = -1;
        for (int i = matches.size() - 1; i >= 0; i--) {
            if (matches.get(i).participants().equals(participants)) {
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
