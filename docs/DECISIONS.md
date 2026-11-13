# Design decisions

A reference to a superseded decision applies to the decision that superseded it: #3 is replaced by #11, which is replaced by #16, and #4 by #12.

## 1. Keep finished matches with a status
- Status: accepted
- Context: A finished match must be distinguishable from one that never existed.
- Decision: Each match has a `status` (`IN_PROGRESS` or `FINISHED`). Finishing changes the status and the match stays in an in-memory list.
- Consequences: Storage only grows, which is fine for a tournament of about 100 matches. The finish time is not recorded.

## 2. Order matches by start order, not by timestamps
- Status: accepted
- Decision: Matches are stored in start order and never reordered. Updating or finishing a match replaces it in place, and no timestamps are kept.
- Consequences: Ties are resolved by storage order, independent of clock resolution.

## 3. Address matches by the ordered team pair
- Status: superseded by #11
- Context: The task defines no match id.
- Decision: A match is addressed by its ordered (home, away) pair and resolves to the in-progress match of that pair. A team can be in at most one in-progress match, and a finished pair can be started again (per decision #1).
- Consequences: No id type is needed. A reversed pair is a different pair, and lookups scan the list.

## 4. Team name rules and normalisation
- Status: superseded by #12
- Decision: Names must be non-null and non-blank, and home and away must differ after normalisation. Lookups and checks use `strip()` and `toLowerCase(Locale.ROOT)`, while the match keeps the name as first given for display.
- Consequences: Callers need not care about casing. There is no list of valid teams and no accent normalisation.

## 5. Absolute scores that may decrease
- Status: accepted
- Decision: A match starts at 0-0 and `updateScore` replaces both scores with the given values. Scores may decrease but must not be negative.
- Consequences: Corrections such as a disallowed goal are possible, and callers send totals, not increments.

## 6. Summary is an immutable snapshot with its own record
- Status: accepted
- Decision: The summary returns an immutable list of an immutable record with display names and scores only (no status), for in-progress matches only. It is ordered by total score descending, then by most recently started first (per decision #2).
- Consequences: The library prints nothing, and the caller formats the output. There are two types: the internal match with status and the public record.

## 7. Checked exceptions for state errors
- Status: accepted
- Context: Callers must be forced to handle errors that occur in normal use, such as a late update after a match finished.
- Decision: `startMatch` declares `TeamAlreadyPlayingException`. `updateScore` and `finishMatch` declare `MatchAlreadyFinishedException` (only finished matches exist for the pair, per decision #3) and `MatchNotFoundException` (no match for the pair).
- Consequences: Finishing a finished match is an error, not a no-op, and callers must handle or declare these exceptions.

## 8. Unchecked exceptions for invalid input
- Status: accepted
- Decision: Null arguments throw `NullPointerException`. Blank names, identical teams (per decision #4) and negative scores throw `IllegalArgumentException`. Both are documented with `@throws`.
- Consequences: Caller bugs do not force try-catch blocks.

## 9. No thread-safety
- Status: accepted
- Decision: The library is not thread-safe, and callers synchronise if they need to.
- Consequences: The README and Javadoc state this. The returned snapshots are immutable and safe to share.

## 10. Extra operation: look up a match by team pair
- Status: accepted
- Context: The task requires one extra operation, and callers want one match's live score without scanning the summary.
- Decision: `findMatch(home, away)` returns an `Optional` of the public record from decision #6, empty unless the pair has an in-progress match (per decision #3). Queries report absence as an empty result, while commands throw (per decision #7).
- Consequences: It cannot tell a finished match from a missing one, and callers who want an error use `orElseThrow`. Input is validated as in decisions #4 and #8.

## 11. Address matches by the ordered, normalised team pair
- Status: superseded by #16
- Context: Supersedes decision #3, which did not say that pairs are compared after name normalisation.
- Decision: A match is addressed by its ordered pair of normalised names (per decision #12) and resolves to the in-progress match of that pair. A team can be in at most one in-progress match, and a finished pair can be started again (per decision #1).
- Consequences: No id type is needed. A reversed pair is a different pair, and lookups scan the list.

## 12. Team name rules, normalisation and display name
- Status: accepted
- Context: Supersedes decision #4, which left open whether the display name keeps surrounding spaces.
- Decision: Names must be non-null and non-blank, and home and away must differ after normalisation. Lookups and checks use `strip()` and `toLowerCase(Locale.ROOT)`, while the match keeps the stripped name in its original casing for display.
- Consequences: Callers need not care about casing or surrounding spaces. There is no list of valid teams and no accent normalisation.

## 13. Store matches in one list and resolve a pair by its latest match
- Status: accepted
- Context: The same pair can have several matches, but at most one of them is in progress (per decision #11).
- Decision: Matches are kept in a single `ArrayList` of immutable matches in start order, and finished matches are never removed (per decisions #1 and #2). A pair resolves by scanning backwards to its latest match, which is the in-progress one if any exists.
- Consequences: No match id is needed, and lookups are linear, which is fine for about 100 matches. The summary filters in-progress matches and sorts them on read.

## 14. Public API and package
- Status: accepted
- Context: The task leaves the names and the shape of the API open.
- Decision: The public API is `Scoreboard` (`startMatch`, `updateScore`, `finishMatch`, `getSummary`, `findMatch`), the record `MatchSummary` and the three checked exceptions, all in `pl.emmajk.scoreboard`. `Match` and `MatchStatus` are package-private (per decision #6).
- Consequences: Commands return nothing, and `getSummary` and `findMatch` share `MatchSummary` (per decision #10).

## 15. Validate arguments before reading state
- Status: accepted
- Decision: Every operation checks its arguments in a fixed order before looking at any match: null, blank, same team, then negative scores.
- Consequences: Invalid input fails the same way whatever the scoreboard holds (per decision #8), so a negative score for an unknown pair throws `IllegalArgumentException`, not `MatchNotFoundException`.

## 16. Address matches by the unordered team pair
- Status: accepted
- Context: Supersedes decision #11, because callers may not know which team is home.
- Decision: A match is addressed by its two normalised names in either order. Each score given to `updateScore` goes to the team named with it, and results keep the home and away order of the start.
- Consequences: A rematch may swap home and away. Decisions #7, #10 and #13 apply unchanged.
