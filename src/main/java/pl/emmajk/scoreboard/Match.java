package pl.emmajk.scoreboard;

import java.util.Locale;

/**
 * Internal match with its status, team names as first given.
 */
record Match(String homeTeam, String awayTeam, int homeScore, int awayScore, MatchStatus status) {

    /**
     * Returns the lookup key of a team name: stripped and lower-cased.
     */
    static String lookupKey(String teamName) {
        return teamName.strip().toLowerCase(Locale.ROOT);
    }

    /**
     * Tells whether the match is in progress.
     */
    boolean isInProgress() {
        return status == MatchStatus.IN_PROGRESS;
    }

    /**
     * Tells whether the team, given as a lookup key, plays in this match.
     */
    boolean isPlayedBy(String teamLookupKey) {
        return lookupKey(homeTeam).equals(teamLookupKey) || lookupKey(awayTeam).equals(teamLookupKey);
    }

    /**
     * Tells whether the match is between the given home and away teams, given as lookup keys.
     */
    boolean isPair(String homeLookupKey, String awayLookupKey) {
        return lookupKey(homeTeam).equals(homeLookupKey) && lookupKey(awayTeam).equals(awayLookupKey);
    }

    /**
     * Returns a copy of the match with the given score.
     */
    Match withScore(int newHomeScore, int newAwayScore) {
        return new Match(homeTeam, awayTeam, newHomeScore, newAwayScore, status);
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
        return new Match(homeTeam, awayTeam, homeScore, awayScore, MatchStatus.FINISHED);
    }
}
