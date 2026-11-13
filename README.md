# Match scoreboard

A small in-memory Java library for a live football World Cup scoreboard: start a match, update its score, finish it, and get a summary of matches in progress.

Design decisions are recorded in [docs/DECISIONS.md](docs/DECISIONS.md), and most assumptions below are backed by an entry there.

## Usage

```java
Scoreboard scoreboard = new Scoreboard();
scoreboard.startMatch("Mexico", "Canada");
scoreboard.updateScore("Mexico", "Canada", 0, 5);

List<MatchSummary> summary = scoreboard.getSummary();                    // one entry: Mexico 0, Canada 5
Optional<MatchSummary> match = scoreboard.findMatch("mexico", "canada"); // same match, names are case-insensitive

scoreboard.finishMatch("Mexico", "Canada");
```

`startMatch`, `updateScore` and `finishMatch` declare checked exceptions. The example scenario from the task is covered by a test in `ScoreboardSpec`.

## Build

The project needs JDK 25 and Maven. `mvn clean verify` compiles the library and runs the Spock tests from `src/test/groovy`.

## Additional feature: look up a match by team pair

`findMatch(home, away)` returns the current score of one in-progress match, or an empty `Optional` if that pair is not playing. The teams can be given in either order and the names are case-insensitive, as for the other operations. The result keeps the home and away order of the start. It never returns finished matches. Invalid input is rejected as for the other operations, so a blank name or the same team twice throws an unchecked exception instead of returning empty. Operations that change a match throw when it is not in progress, while this read reports absence as an empty result. Callers who want an error can use `orElseThrow`.

**Why this one.** Callers often need the live score of a single match, and without this they would fetch the whole summary and filter it. It reuses the pair lookup and the summary's public record, so it adds no new type. A team's match history would also have been possible, but it needs a second public record that includes status and the final score, which is more than this library needs. See decision #10 in [docs/DECISIONS.md](docs/DECISIONS.md).

## My assumptions

- **Match lifecycle.** A match is `IN_PROGRESS` or `FINISHED`. Only in-progress matches can be updated or finished. Finished matches stay in memory with status `FINISHED`.
- **Identity.** A match is addressed by its pair of normalised names in either order, with no generated id. A rematch may swap home and away. A team can be in at most one in-progress match, and a finished pair can be started again, for example a group match followed by a knockout rematch.
- **Team names.** Names must be non-null and non-blank, and a team cannot play itself. Matching is case-insensitive and ignores surrounding spaces. The summary shows the name as first given, without surrounding spaces.
- **Scores.** A match starts at 0-0. An update replaces both scores with the given values, each going to the team named with it whatever the team order, and may lower them, for example after a disallowed goal. Negative scores are rejected.
- **Ordering.** Only start order matters, not timestamps, and the finish time is not recorded. The summary lists total score descending, and on a tie the most recently started match first.
- **Summary.** It is an immutable snapshot of in-progress matches. It contains team names and scores only, and the library prints nothing, so the caller decides the format.
- **Errors.** `startMatch` throws a checked `TeamAlreadyPlayingException`. `updateScore` and `finishMatch` throw a checked `MatchNotFoundException` or `MatchAlreadyFinishedException`. Invalid input (null, blank name, same team, negative score) throws `NullPointerException` or `IllegalArgumentException`. Arguments are checked before the scoreboard is read, so invalid input fails the same way whatever it holds. `findMatch` throws no checked exceptions.
- **Threading.** The library is not thread-safe, and callers synchronise if they need to.
- **Out of scope.** Persistence, match time, scorers and events, a team's match history, a list of valid teams, and accented-character normalisation.

## My reasoning

- **Keep it plain.** A tournament has about 100 matches (104 in the 2026 format), so a simple in-memory list is enough. There is no id type, no timestamps and no clock.
- **Start order instead of time.** Matches are stored in start order, so the tie-break is the reverse of storage order. It does not depend on clock resolution.
- **One list.** Matches live in a single `ArrayList` in start order. A pair resolves by scanning backwards to its latest match, which is the in-progress one if there is one, so no match id is needed.
- **Finished matches are kept.** Without them, "finished" cannot be told apart from "never started", so the scoreboard could not report the right error. A pair therefore resolves to its in-progress match first, then to a finished one, because the same pair can meet twice.
- **Errors by cause.** Errors that happen in normal use, such as a late update after a match finished, are checked, so callers must handle them. Caller bugs such as a negative score are unchecked. Reads such as `findMatch` return an empty `Optional` instead of throwing, because "not playing" is a valid answer to a question and not a failed command.
- **Immutable output.** The summary is a separate record without the internal status, so callers cannot change the scoreboard through it, and later updates do not change a summary already returned.

## Trade-offs

- **Memory grows.** Finished matches are never removed. This is fine for a tournament, not for a long-running service.
- **Linear lookups.** Finding a match scans the list, which is fast enough for this size.
- **Plain `int` scores.** The total score is an `int` sum with no overflow guard, because real match scores never come near `Integer.MAX_VALUE`.
- **`findMatch` returns `Optional`.** The caller cannot tell a finished match from one that never started, but a read does not need a checked exception, and callers who want an error can use `orElseThrow`.
- **Checked exceptions** add handling code for callers, in exchange for compiler-enforced handling of expected errors.
- **Case-insensitive matching** is simple but does not treat accent variants ("Côte d'Ivoire" and "Cote d'Ivoire") or alternative names ("USA" and "United States") as the same team.
- **No thread-safety** keeps the code simple but leaves synchronisation to callers.
- **Two types for a match** (internal with status, public without) cost a little code and keep the internals hidden.
