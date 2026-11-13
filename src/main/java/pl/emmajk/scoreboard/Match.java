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
     * Tells whether the match is between the two teams, given as lookup keys, in either order.
     */
    boolean isPair(String firstLookupKey, String secondLookupKey) {
        String home = lookupKey(homeTeam);
        String away = lookupKey(awayTeam);
        return (home.equals(firstLookupKey) && away.equals(secondLookupKey))
                || (home.equals(secondLookupKey) && away.equals(firstLookupKey));
    }

    /**
     * Returns a copy of the match with the scores of the first team (given as lookup key) and of the other team.
     */
    Match withScore(String firstLookupKey, int firstScore, int otherScore) {
        if (lookupKey(homeTeam).equals(firstLookupKey)) {
            return new Match(homeTeam, awayTeam, firstScore, otherScore, status);
        }
        return new Match(homeTeam, awayTeam, otherScore, firstScore, status);
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
