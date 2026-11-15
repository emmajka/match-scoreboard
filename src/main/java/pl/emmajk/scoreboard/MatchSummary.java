package pl.emmajk.scoreboard;

/** Immutable score of a match as seen by the caller, with team names as given. */
public record MatchSummary(String homeTeam, String awayTeam, int homeScore, int awayScore) {
}
