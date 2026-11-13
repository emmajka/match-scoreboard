# CLAUDE.md

## Project
- A live football world cup match scoreboard: a small, plain in-memory Java library.
- Full task description can be found in `docs/coding-task.pdf` file.
- Design decisions live in `docs/DECISIONS.md`.

## Design decisions (docs/DECISIONS.md)
- `docs/DECISIONS.md` contains numbered designed decisions, indexed from 1. 
- each decision has a `title`, `status` (`accepted` or `superseded by #N` where `N` is the decisions number it was superseded by),
  `context` (1-2 sentences max, optional), `decision` (1-2 sentences max, mandatory), `consequences` (1-2 sentences max, optional)
- keep each entry concise, up to 4-5 lines, no more.
- follow only decisions with status `accepted`, never follow a `superseded` one.
- never edit, renumber or delete a decision on Your own. Never add a new one without my approval. 
  You can edit only a `status` of a decision after its creation, for instance when a decision is being superseded by a different decision
- analyze whether a decision looks wrong, incomplete, in conflict with the code or with another decision,
  stop and inform me about it. Propose new entry or a superseding entry and wait for my approval.
- if new decision relates to already existing one please do mention it, as for instance `per decision #N`.
- when approving a new entry which supersedes an existing entry please do modify that given existing entry
  with status `superseded by #N`.
- with each decision modification/change please do verify against `README.md`'s assumptions,
  inform about any mismatch, conflict, propose a fix.

## Stack
- java 25 with maven.
- test with spock and groovy, in `src/test/groovy`. Use the standard setup (gmavenplus, surefire).
- do not add any other frameworks, libraries, dependencies or plugins without asking me first.
- do not add lombok to the project.

## Design
- keep it simple, keep the public API minimal.
- no abstractions without a concrete need.
- prefer immutability to mutability.

## In-code documentation
- keep in-code documentation short and concise, use simple vocabulary.
- with every method change, if an in-code documentation exists verify changes against it, analyze if it requires an update.
- in-code documentation should include public class, every method and constants, limit it to 1-2 lines max

## Workflow
- plan before code, wait for my approval.
- write tests first.
- one concern per change, sized to a single commit.
- verify changes and their correctness before saying they work or are done, run tests, compile code as mandatory steps.
- if something is ambiguous, ask, do not guess.
- no unrelated refactors or "improvements" unless I ask.

## Testing
- write tests using spock framework and groovy programming language.
- keep tests short and concise, limit single test to 50 lines of code at most.
- name tests using template `given ${argument description} when calling ${method name without arg list} then should throw ${exception name}`, as an example: `given a null id when calling generate should throw a NullPointerException`.
- for dataset tests use naming template `${method name} - dataset tests`. If single method can have multiple dataset tests differentiate them by adding suffix `- ${scenario description}`. As an example: `generate - dataset tests - success scenarios`
- use dataset tests if they can be kept short and concise. Use them only if there are at least 2 scenarios which can be tested
- test successful paths and failure/exception paths. Attempt to find edge cases, make scenarios realistic.

## Git
- never run commit or push code by yourself, never rewrite history.
- leave changes uncommitted for my review, I will commit and push manually.

## Docs
- `README.md` may only claim what the code and tests actually do.

## AI.md
- at the start of every turn, before other work, append an entry with my prompt exactly as I wrote it. Do not modify the prompt in any way.
- only modify the `Prompt log` section. Do not edit other sections present in the file.
- prompt log entry format, inside the `Prompt log` section:

```
### N: short title
Prompt:

<exact prompt>

Summary:

<1-3 sentences on what was changed>

```
- `N` is a running index
- wrap prompt using the text block so it stands out and is readable.
- do not add to prompt log progression, approvals or disapprovals of solutions during in-progress 
prompt as in for instance details on installing JDK or requirement to install additional tools to enable running tests.
- `Summary` - summarize what was changed, mention if something was modified by me manually. 
For instance You propose how should a class look like and I rename it or change/add/remove a field and others.