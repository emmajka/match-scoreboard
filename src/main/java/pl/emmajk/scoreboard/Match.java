package pl.emmajk.scoreboard;

/** Internal match with its status, team names as first given. */
record Match(String homeTeam, String awayTeam, int homeScore, int awayScore, MatchStatus status) {
}
