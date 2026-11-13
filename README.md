# Match scoreboard

A small in-memory Java library for a live football World Cup scoreboard: start a match, update its score, finish it, and get a summary of matches in progress.

**Status:** design agreed, implementation pending. This README states the assumptions the implementation will follow and will be updated with the code. Each assumption is backed by an entry in [docs/DECISIONS.md](docs/DECISIONS.md).

## My assumptions

- **Match lifecycle.** A match is `IN_PROGRESS` or `FINISHED`. Only in-progress matches can be updated or finished. Finished matches stay in memory with status `FINISHED`.
- **Identity.** A match is addressed by its ordered (home, away) pair, with no generated id. A team can be in at most one in-progress match, and a finished pair can be started again, for example a group match followed by a knockout rematch.
- **Team names.** Names must be non-null and non-blank, and a team cannot play itself. Matching is case-insensitive and ignores surrounding spaces. The summary shows the name as first given.
- **Scores.** A match starts at 0-0. An update replaces both scores with the given values and may lower them, for example after a disallowed goal. Negative scores are rejected.
- **Ordering.** Only start order matters, not timestamps, and the finish time is not recorded. The summary lists total score descending, and on a tie the most recently started match first.
- **Summary.** It is an immutable snapshot of in-progress matches. It contains team names and scores only, and the library prints nothing, so the caller decides the format.
- **Errors.** `startMatch` throws a checked `TeamAlreadyPlayingException`. `updateScore` and `finishMatch` throw a checked `MatchNotFoundException` or `MatchAlreadyFinishedException`. Invalid input (null, blank name, same team, negative score) throws `NullPointerException` or `IllegalArgumentException`.
- **Threading.** The library is not thread-safe, and callers synchronise if they need to.
- **Out of scope.** Persistence, match time, scorers and events, a list of valid teams, and accented-character normalisation.

## My reasoning

- **Keep it plain.** A tournament has about 100 matches, so a simple in-memory list is enough. There is no id type, no timestamps and no clock.
- **Start order instead of time.** Matches are stored in start order, so the tie-break is the reverse of storage order. It does not depend on clock resolution.
- **Finished matches are kept.** Without them, "finished" cannot be told apart from "never started", so the scoreboard could not report the right error. This is why a pair resolves to its in-progress match first.
- **Errors by cause.** Errors that happen in normal use, such as a late update after a match finished, are checked, so callers must handle them. Caller bugs such as a negative score are unchecked.
- **Immutable output.** The summary is a separate record without the internal status, so callers cannot change the scoreboard through it, and later updates do not change a summary already returned.

## Trade-offs

- **Memory grows.** Finished matches are never removed. This is fine for a tournament, not for a long-running service.
- **Linear lookups.** Finding a match scans the list, which is fast enough for this size.
- **Checked exceptions** add handling code for callers, in exchange for compiler-enforced handling of expected errors.
- **Case-insensitive matching** is simple but does not treat accent variants ("Côte d'Ivoire" and "Cote d'Ivoire") or alternative names ("USA" and "United States") as the same team.
- **No thread-safety** keeps the code simple but leaves synchronisation to callers.
- **Two types for a match** (internal with status, public without) cost a little code and keep the internals hidden.
