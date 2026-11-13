## How I used AI tools

## Prompt log

### 1: Task analysis, .gitignore and Maven hello world setup
Prompt:

```text
This is the first prompt of this project and from this prompt on every prompt I will send it ought to be recorded in the `AI.md` file.
This repository contains a task description located in the `docs/coding-task.pdf` file. Analyze the file thoroughly, we will be working on it from now on. Do not create a file with summary or so, paste the results as inlined response.
Perform following operations:

1. In the `AI.md` file in the root directory add this prompt contents as first entry. Remember to not modify the prompts content, do not try to tidy it up, fix language errors, typos and so on.
2. modify existing `.gitignore` file, add there entries common for Intellij IDE and claude code which should be excluded from git history.
3. Do not create nor remove any elements from this repository if I have no explicitly said to do so.
4. Create a hello world java project, use java 25 with maven. Add a single dummy test using spock and groovy. Before stating a success verify implementation using `mvn clean verify` if the project compiles and tests are passing correctly.
```

Summary:

Analyzed `docs/coding-task.pdf`, extended `.gitignore` with IntelliJ, Maven and Claude Code entries, and created a Java 25 Maven hello world project (`pom.xml`, `HelloWorld`) with one Spock/Groovy test. `mvn clean verify` passes.

### 2: Discussion of implementation assumptions
Prompt:

```text
Let's discuss assumptions for our task implementation. Be wary that it ought to be a plain solution, let us try not to overengineer it, let us keep it simple whenever and wherever possible.

Assumptions I was thinking of are as follows:

* only games which are in-progress can be modified, as in game's score cannot be changed if game finished.
* finishing a game means removing it completely from internal storage, we do not care about finished games for now.
* printed game's summary format should be decided by the caller, we should return the data and caller should decide what to do with it.
* implementation should not enforce thread-safety, that responsibility should be of a caller/user of this library.
* we do not care about when game was finished, we only care about when it started or perhaps in what order games were started in.


Review my proposed assumptions, come up with Your own assumption proposals, verify it against the task description. Let us start the discussion.
```

Summary:

Reviewed the five proposed assumptions against the task, proposed additional ones and listed open questions. No code or decision changes; the discussion continues.

### 3: Merging assumptions, custom exceptions and case insensitivity
Prompt:

```text
I agree with assumptions #6, #7, #8, #9, #10, #12. Regarding assumption #11 - if there is a necessity to add a custom error let's do this, it would be even better if add custom errors in cases like "unable to modify a match which was finished". Also it would be better if method definition would define what sort of exceptions are expected instead of generic JDK runtime exceptions and hope caller will prepare for them using try-catch or so.  

Regarding assumption #10 - we can enforce case insensitivity as in we can store matches using lowercase and we can modify users input by making team names lowercase so user don't have to remember about i.


Please do merge them with my assumptions, give me a list to review.
```

Summary:

Merged the agreed assumptions with the original five into one numbered list and raised open points: finished vs missing match under #2, checked vs unchecked exceptions, and display casing. No code or decision changes.

### 4: Retaining finished matches with a status field
Prompt:

```text
regarding open points:
A: Agreed on the part that if we remove a finished match from internal storage then we can't say if match never existed or was simply finished. Perhaps let's do a different approach. Each match ought to have a separate field `status` which would contain an enum with two possible values `IN_PROGRESS` or `FINISHED`. We would not remove the game from storage but leave it there with explicit `FINISHED` status. World cup's do not have a lot of games, we should not worry about memory usage or internal storage growing in size too much, this part we can assume it ought to be simple in design. Another issue Ican see that theoreticaly (correct me if I am wrong) two teams can play one game during the group matches and after leaving the group. They would have different start date's though so they should be distinguishable, what do You think?
B: do the recommended exceptions split.
C: separating display and normalized search name is a good idea, please assume so.
D: regarding extra operation we will decide in another scope.

Please do modify the assumptions list, show me its final version. IF there are any open points show me them and let's continue discussion.
```

Summary:

Reworked the assumptions list for finished matches kept with a `FINISHED` status, a separate "already finished" exception, display vs normalised names, and the checked/unchecked exception split. Raised remaining open points; no code or decision changes.

### 5: Recording agreed assumptions in README.md and DECISIONS.md
Prompt:

```text
Regarding open points:

1. we will expose a separate record/class with only the required fields for the caller so status field will remain a hidden fields for the caller.
2. sounds good.
3. let;s ignore the dates for now.


Please do update the README.md's file with what we agreed on, modify `docs/DECISIONS.md` with whatever is appropriate to add there. Do not commit anything, let me review changes first.
```

Summary:

Wrote nine accepted design decisions to `docs/DECISIONS.md` and filled `README.md` (assumptions, reasoning, trade-offs) with the agreed design. Nothing was committed.

### 6: Discussion of the additional feature
Prompt:

```text
LEt's discuss now the additional feature.What I was thinking about is either:

1. retrieve given team's match history in world cups's
2. or retrieve current game's scope, game identified by the team's pair, possibility to retrieve only in-progress games.
```

Summary:

Compared the two candidate features (team match history vs. single match lookup by team pair) against the agreed decisions and listed open questions. No code or decision changes; the discussion continues.

### 7: Recording the additional feature (match lookup by team pair)
Prompt:

```text
Let's go with option 2 then. Modify README.md and DECISIONS.md and any other appropriate files, let me review it.
```

Summary:

Added decision #10 (look up an in-progress match by team pair, returning an `Optional`) to `docs/DECISIONS.md` and documented the feature, its reasoning and its trade-offs in `README.md`. Nothing was committed.

### 8: Optional vs exception consistency for the lookup
Prompt:

```text
regarding additional method signature, You say return an Optional and in design decision #7 we decided that one of `finishMAtch`'s  throwable errors would be `MatchNotFoundException` - cna You analyze if that approach is consistent? IS return an optional in one method and throwing an error in another is a proper approach? Is it considered best standard and practice to do so?
```

Summary:

Analyzed whether returning an `Optional` from `findMatch` while `finishMatch` throws `MatchNotFoundException` is consistent, and listed the alternatives. No code or decision changes; the question is still open.

### 9: Documenting the Optional vs exception rule
Prompt:

```text
okey, let's keep it like this, as You have recommended. Please do document it properly though. Modify files, let me review them. You can modify already modified files in this session which were not yet commited.
```

Summary:

Documented the rule "commands throw, queries return `Optional`" in decision #10 of `docs/DECISIONS.md` and in the README (feature section, reasoning and trade-offs). Nothing was committed.

### 10: Review of README.md and DECISIONS.md
Prompt:

```text
Review what we have, attempt to find any conflicting information in the readme.,d and decisions.md, any misinformation, false statements and so on.
```

Summary:

Reviewed `README.md` and `docs/DECISIONS.md` against each other, the task and `CLAUDE.md`, and listed five inaccuracies and several gaps with proposed fixes. No files other than `AI.md` were changed.

### 11: Fixing README.md and superseding decisions #3 and #4
Prompt:

```text
modify readme in-place but for decisions.md follow the principles as to not modify existing entires once commited, only the status ought to be mutable. Place new decisions fixing existing ones, use the supersede approach.

Show me modified files for review.
```

Summary:

Fixed the README inaccuracies in place. In `docs/DECISIONS.md` added decisions #11 and #12, which supersede #3 and #4, and changed only the status of #3 and #4. Nothing was committed.

## Artifacts