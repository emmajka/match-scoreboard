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
}
