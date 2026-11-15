package pl.emmajk.scoreboard;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * In-memory scoreboard of live football matches. Not thread-safe.
 * Team names are compared ignoring case and accents, and the caller keeps the home and away order consistent.
 */
public final class Scoreboard {

    /**
     * All matches, finished ones included.
     */
    private final MatchStore matches = new MatchStore();

    /**
     * Starts a match at 0-0.
     *
     * @throws TeamAlreadyPlayingException if a team is in an in-progress match, as home or away team
     * @throws NullPointerException        if a name is null
     * @throws IllegalArgumentException    if a name is blank or has surrounding whitespace, or both teams are the same
     */
    public void startMatch(String homeTeam, String awayTeam) throws TeamAlreadyPlayingException {
        MatchParticipants participants = MatchParticipants.of(homeTeam, awayTeam);

        if (matches.isTeamPlaying(participants.homeKey())) {
            throw new TeamAlreadyPlayingException(homeTeam);
        }
        if (matches.isTeamPlaying(participants.awayKey())) {
            throw new TeamAlreadyPlayingException(awayTeam);
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

        Match match = findInProgress(participants, homeTeam, awayTeam);
        matches.replaceLatest(participants, match.withScore(homeScore, awayScore));
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

        Match match = findInProgress(participants, homeTeam, awayTeam);
        matches.replaceLatest(participants, match.finished());
    }

    /**
     * Returns in-progress matches, highest total score first, ties by most recently started.
     */
    public List<MatchSummary> getSummary() {
        return matches.inProgressNewestFirst().stream()
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

        return matches.findLatest(participants)
                .filter(Match::isInProgress)
                .map(Match::toSummary);
    }

    /**
     * Returns the in-progress match of the participants, or throws if the latest one is finished or there is none.
     */
    private Match findInProgress(MatchParticipants participants, String homeTeam, String awayTeam)
            throws MatchNotFoundException, MatchAlreadyFinishedException {
        Match latest = matches.findLatest(participants)
                .orElseThrow(() -> new MatchNotFoundException(homeTeam, awayTeam));

        if (!latest.isInProgress()) {
            throw new MatchAlreadyFinishedException(homeTeam, awayTeam);
        }

        return latest;
    }
}
