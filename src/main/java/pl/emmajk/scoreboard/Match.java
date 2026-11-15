package pl.emmajk.scoreboard;

/**
 * Internal match with its status, team names as given and their normalised participants.
 */
record Match(String homeTeam, String awayTeam, MatchParticipants participants, int homeScore, int awayScore,
             MatchStatus status) {

    /**
     * Tells whether the match is in progress.
     */
    boolean isInProgress() {
        return status == MatchStatus.IN_PROGRESS;
    }

    /**
     * Returns a copy of the match with the given score.
     */
    Match withScore(int newHomeScore, int newAwayScore) {
        return new Match(homeTeam, awayTeam, participants, newHomeScore, newAwayScore, status);
    }

    /**
     * Returns the sum of both team scores.
     */
    int totalScore() {
        return homeScore + awayScore;
    }

    /**
     * Returns the match as seen by the caller.
     */
    MatchSummary toSummary() {
        return new MatchSummary(homeTeam, awayTeam, homeScore, awayScore);
    }

    /**
     * Returns a copy of the match marked as finished.
     */
    Match finished() {
        return new Match(homeTeam, awayTeam, participants, homeScore, awayScore, MatchStatus.FINISHED);
    }
}
