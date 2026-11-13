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

## Artifacts