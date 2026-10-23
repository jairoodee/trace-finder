# TraceFinder

**A Java command-line log analysis tool for identifying suspicious activity, flagging high-severity events, and reporting malformed log entries.**

TraceFinder reads a log file and a CSV rulebook, analyzes the log entries against the configured severity rules, and generates a structured report.

The project is built with Java and Maven. It includes unit tests for its core components and integration tests that exercise the command-line application through its packaged executable JAR.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Project Objectives](#2-project-objectives)
3. [Features and Scope](#3-features-and-scope)
4. [Technology Stack](#4-technology-stack)
5. [Prerequisites](#5-prerequisites)
6. [Clone the Repository](#6-clone-the-repository)
7. [Project Structure](#7-project-structure)
8. [Install Dependencies](#8-install-dependencies)
9. [Build the Project](#9-build-the-project)
10. [Running TraceFinder](#10-running-tracefinder)
11. [Input File Specifications](#11-input-file-specifications)
12. [Log Analysis Process](#12-log-analysis-process)
13. [Understanding the Report](#13-understanding-the-report)
14. [Error Handling](#14-error-handling)
15. [Testing](#15-testing)
16. [Development Workflow](#16-development-workflow)
17. [Troubleshooting](#17-troubleshooting)
18. [Current Limitations](#18-current-limitations)
19. [Potential Future Enhancements](#19-potential-future-enhancements)
20. [Contributing](#20-contributing)
21. [License](#21-license)

---

## 1. Project Overview

TraceFinder is a command-line application that processes log files using a configurable rulebook.

The application accepts three command-line arguments:

1. The path to the log file.
2. The path to the CSV rulebook containing log levels and severity scores.
3. The path where the generated report should be written.

It parses the log entries, evaluates their log levels against the rulebook, identifies entries that meet the flagging threshold, groups suspicious activity by source IP address, and records unknown patterns and malformed lines.

The application is designed to continue processing when individual log lines are invalid or contain unrecognized log levels, allowing useful results to be generated without requiring every input line to be valid.

### Example command

```bash
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

### Example output file

```text
report.txt
```

The generated report contains an activity summary and four additional sections covering flagged entries, suspicious IP addresses, unknown patterns, and malformed lines.

---

## 2. Project Objectives

TraceFinder aims to demonstrate the development of a modular Java command-line application with clear separation of responsibilities.

Its primary objectives are:

- Read and parse structured log files.
- Load severity rules from a configurable CSV file.
- Identify log entries with high-severity levels.
- Detect log entries containing unrecognized levels.
- Preserve the original line numbers and content of problematic entries.
- Identify source IP addresses associated with flagged events.
- Generate a structured, readable analysis report.
- Handle common file and input errors gracefully.
- Maintain test coverage for individual components and end-to-end CLI behavior.

The project also provides practical experience with Java object-oriented programming, file I/O, exception handling, Maven, JUnit, Git, and automated testing.

---

## 3. Features and Scope

### 3.1 Log file processing

TraceFinder reads a log file line by line.

Each valid entry is parsed into a `LogEntry` object. The application also retains the original line number and raw line content through the `ParsedLogLine` model.

This metadata allows the report to identify the original location of entries in the source file.

### 3.2 Log parsing and validation

The parser expects five pipe-delimited fields:

- Timestamp
- Log level
- Source IP address
- Target
- Action

Lines with an incorrect number of fields or an invalid timestamp are treated as malformed.

Malformed lines are recorded with their original line numbers and content rather than being included in the successfully parsed entries.

### 3.3 Configurable severity rules

Severity rules are loaded from a CSV file.

Each rule maps a log level to an integer severity score.

This separates severity configuration from the analysis logic and allows the rulebook to define which log levels are recognized and how they are scored.

The rulebook reader also validates the CSV structure, severity values, and duplicate log levels.

### 3.4 Severity analysis

TraceFinder compares each successfully parsed entry's log level with the configured rules.

An entry is flagged when its recognized severity score is **greater than or equal to 3**.

Entries with unrecognized levels are recorded as unknown patterns.

### 3.5 Suspicious IP identification

TraceFinder identifies suspicious IP addresses from the flagged entries.

Each suspicious IP address is listed once in the report.

Its activity count includes all successfully parsed entries associated with that IP address, not just the entries that were flagged.

### 3.6 Unknown pattern tracking

A log entry whose level is absent from the rulebook is treated as an unknown pattern.

The report preserves the original line number and raw content so that the entry can be investigated.

An unknown level does not automatically make the entire log file invalid.

### 3.7 Malformed line tracking

TraceFinder records lines that cannot be parsed successfully.

Examples include:

- Lines with fewer than five fields.
- Lines with more than five fields.
- Lines with invalid timestamps.
- Blank lines that do not satisfy the parser's requirements.

Malformed entries are skipped during analysis but included in the final report.

### 3.8 Structured report generation

The report contains five analysis sections, a main title, and a generation timestamp.

All five section headings are included even when a section has no entries.

### 3.9 Command-line error handling

The application validates the number of command-line arguments and handles failures when reading the log file or rulebook and writing the report.

Fatal errors produce a readable error message and a non-zero process exit status.

### 3.10 Automated testing

The project includes JUnit tests for the parser, rulebook reader, log reader, severity checker, analyzer, and report writer.

It also includes CLI integration tests that execute the packaged application and verify its behavior.

---

## 4. Technology Stack

| Technology                   | Purpose                                                     |
| ---------------------------- | ----------------------------------------------------------- |
| Java                         | Application implementation                                  |
| Java 17 language/API target  | Compatibility target configured in Maven                    |
| Apache Maven                 | Dependency management, compilation, testing, and packaging  |
| JUnit Jupiter 5.11.4         | Unit and integration testing                                |
| Maven Surefire 3.5.2         | Test execution during the Maven test phase                  |
| Maven Failsafe 3.5.2         | Integration testing during the Maven verification lifecycle |
| Maven Compiler Plugin 3.13.0 | Java compilation                                            |
| Maven JAR Plugin 3.4.2       | Executable JAR packaging                                    |
| Git                          | Version control                                             |

The project is configured to compile for Java 17 using Maven's `release` setting.

A newer installed JDK can be used, provided the configured build and runtime requirements are satisfied.

---

## 5. Prerequisites

Before working with TraceFinder, install the following tools.

### 5.1 Java Development Kit

Install a JDK compatible with the project's Java 17 compilation target.

Verify the installation:

```bash
java -version
```

Check that the Java compiler is available:

```bash
javac -version
```

The commands should report installed Java versions.

### 5.2 Apache Maven

Install Maven if it is not already available on your system.

Verify the installation:

```bash
mvn -version
```

Maven should report its version and the Java runtime it uses.

### 5.3 Git

Git is required to clone the repository and manage source-code changes.

Verify the installation:

```bash
git --version
```

### 5.4 Operating system

TraceFinder uses Java and Maven and can be developed on Windows, Linux, or macOS.

Some command examples in this README use Bash syntax. Windows PowerShell equivalents are provided where relevant.

---

## 6. Clone the Repository

The project's development branch is:

```text
feat/jairo/tracefinder
```

Replace `<repository-url>` with the actual Git remote URL for your repository.

### 6.1 Clone the repository

```bash
git clone <repository-url>
```

### 6.2 Enter the project directory

```bash
cd trace-finder
```

### 6.3 Check out the development branch

```bash
git checkout feat/jairo/tracefinder
```

If you cloned a repository that does not have this branch available locally, fetch the remote branches first:

```bash
git fetch origin
git checkout -b feat/jairo/tracefinder origin/feat/jairo/tracefinder
```

If the branch is already checked out, there is no need to create it again.

### 6.4 Inspect the repository status

```bash
git status
```

To inspect recent commits:

```bash
git log -5 --oneline
```

---

## 7. Project Structure

TraceFinder uses a package-based structure to separate its main responsibilities.

The project uses a Maven configuration that places production Java source files under `src/` and test source files under `src/test/java/`.

```text
trace-finder/
│
├── src/
│   ├── TraceFinder.java
│   │
│   ├── analyzer/
│   │   ├── LogAnalyzer.java
│   │   └── SeverityChecker.java
│   │
│   ├── model/
│   │   ├── LogEntry.java
│   │   ├── MalformedLine.java
│   │   ├── ParsedLogLine.java
│   │   ├── Rule.java
│   │   └── UnknownPattern.java
│   │
│   ├── parser/
│   │   └── LogParser.java
│   │
│   ├── reader/
│   │   ├── LogFileReader.java
│   │   └── RuleBookReader.java
│   │
│   ├── writer/
│   │   └── ReportWriter.java
│   │
│   └── test/
│       └── java/
│           ├── TraceFinderIT.java
│           ├── analyzer/
│           ├── parser/
│           ├── reader/
│           └── writer/
│
├── pom.xml
├── .gitignore
├── logs.txt
├── rules.csv
├── README.md
├── report.txt
└── tracefinder.jar
```

**Note:** This is a conceptual layout. Generated files such as `report.txt` and `tracefinder.jar` may be absent, ignored by Git, or present only in your local working directory. The exact test files are maintained under `src/test/java/`.

### 7.1 `TraceFinder.java`

The application entry point.

Responsibilities include:

- Validating command-line arguments.
- Loading the rulebook.
- Reading the log file.
- Passing valid entries to the analyzer.
- Generating the final report.
- Handling fatal application errors.

### 7.2 `model/`

Contains the data models used by the application.

| Class            | Responsibility                                                          |
| ---------------- | ----------------------------------------------------------------------- |
| `LogEntry`       | Represents a successfully parsed log entry                              |
| `ParsedLogLine`  | Associates a parsed entry with its original line number and raw content |
| `MalformedLine`  | Represents a line that could not be parsed                              |
| `Rule`           | Represents a log level and its severity score                           |
| `UnknownPattern` | Represents an entry whose log level is not recognized                   |

### 7.3 `parser/`

Contains the logic responsible for interpreting individual log lines.

`LogParser` validates the expected field count and timestamp format and returns a parsed entry when the line is valid.

### 7.4 `reader/`

Contains file-reading responsibilities.

`LogFileReader` reads log files and separates successfully parsed entries from malformed lines.

`RuleBookReader` loads and validates severity rules from the CSV file.

### 7.5 `analyzer/`

Contains the logic used to evaluate parsed entries.

`SeverityChecker` checks whether a log level is recognized and retrieves its configured severity score.

`LogAnalyzer` records flagged entries and unknown patterns.

### 7.6 `writer/`

Contains report-generation logic.

`ReportWriter` constructs the report from the parsed entries, malformed lines, analysis results, and configured rules.

### 7.7 `src/test/java/`

Contains automated tests for the application.

The unit tests cover individual components. `TraceFinderIT.java` contains integration tests for the packaged command-line application.

### 7.8 `pom.xml`

The Maven project configuration.

It defines the project's dependencies, source directories, Java compilation target, test execution, executable JAR configuration, and integration-test lifecycle.

### 7.9 `.gitignore`

Excludes generated artifacts and other files that should not be committed, including the generated executable JAR, Maven's `target/` directory, and generated report files.

---

## 8. Install Dependencies

TraceFinder uses Maven to resolve its dependencies.

From the project root, run:

```bash
mvn dependency:resolve
```

This asks Maven to resolve the dependencies declared in `pom.xml`.

To compile the project:

```bash
mvn compile
```

Maven downloads missing dependencies from its configured repositories and stores them in its local dependency cache.

You do not need to download JUnit or the Maven plugins manually when Maven can resolve them successfully.

### Verify dependency resolution

Run:

```bash
mvn dependency:tree
```

This displays the project's dependency tree.

The project uses JUnit Jupiter for automated testing, together with Maven's Surefire and Failsafe plugins for the appropriate test phases.

---

## 9. Build the Project

### 9.1 Compile the source code

```bash
mvn compile
```

This compiles the production source files.

### 9.2 Run the unit tests

```bash
mvn test
```

This runs the tests selected by Maven Surefire.

### 9.3 Build the executable JAR

```bash
mvn package
```

This compiles the project, runs the applicable test phase, and packages the application.

The configured final JAR name is:

```text
target/tracefinder.jar
```

### 9.4 Run the full verification lifecycle

The recommended verification command is:

```bash
mvn clean verify
```

This command:

1. Removes previous build output.
2. Compiles the project.
3. Runs the unit tests.
4. Packages the application.
5. Runs the configured integration-test lifecycle.
6. Verifies the build.

The project uses Maven Failsafe to run the CLI integration tests during the verification lifecycle.

**Use `mvn clean verify` before committing significant changes.**

### 9.5 Confirm the JAR exists

On Linux, macOS, or Git Bash:

```bash
ls -l target/tracefinder.jar
```

On Windows PowerShell:

```powershell
Get-Item target/tracefinder.jar
```

---

## 10. Running TraceFinder

The required command-line interface is:

```bash
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

This command assumes that `tracefinder.jar`, `logs.txt`, and `rules.csv` are in the current working directory.

### 10.1 Understand the command

```bash
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

| Argument          | Meaning                                   |
| ----------------- | ----------------------------------------- |
| `java`            | Starts the Java runtime                   |
| `-jar`            | Executes an application packaged as a JAR |
| `tracefinder.jar` | Executable application JAR                |
| `logs.txt`        | Input log file                            |
| `rules.csv`       | Input severity rulebook                   |
| `report.txt`      | Destination for the generated report      |

The application expects exactly three arguments after the JAR name.

### 10.2 Copy the packaged JAR to the project root

Maven builds the JAR inside `target/`, but the assignment requires running it from the project root using:

```bash
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

After building the project, copy the JAR to the root directory.

**Git Bash, Linux, or macOS:**

```bash
cp target/tracefinder.jar tracefinder.jar
```

**Windows PowerShell:**

```powershell
Copy-Item target/tracefinder.jar tracefinder.jar
```

Then execute:

```bash
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

This is the recommended workflow when the assignment requires the exact command above.

### 10.3 Complete build-and-run workflow

**Git Bash, Linux, or macOS:**

```bash
mvn clean verify
cp target/tracefinder.jar tracefinder.jar
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

**Windows PowerShell:**

```powershell
mvn clean verify
Copy-Item target/tracefinder.jar tracefinder.jar
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

Copying the JAR after a successful build ensures that the root-level executable reflects the latest packaged code.

### 10.4 Run the JAR directly from `target/`

If you do not need to use the assignment's exact command, you can execute the packaged JAR directly:

```bash
java -jar target/tracefinder.jar logs.txt rules.csv report.txt
```

Both commands run the same packaged application when the root-level JAR has been copied from the current build.

### 10.5 Use custom file paths

The application accepts file paths as arguments.

For example:

```bash
java -jar tracefinder.jar data/logs.txt config/rules.csv output/report.txt
```

The input paths must refer to readable files. The output destination must be writable and its parent directory must exist.

### 10.6 View the report

After a successful run, open the generated report:

```text
report.txt
```

On Windows PowerShell, you can display it in the terminal with:

```powershell
Get-Content report.txt
```

On Git Bash, Linux, or macOS:

```bash
cat report.txt
```

---

## 11. Input File Specifications

TraceFinder requires two input files:

1. A structured log file.
2. A CSV severity rulebook.

### 11.1 Log file format

Each valid log line contains five fields separated by the pipe character (`|`):

```text
timestamp | level | source_ip | target | action
```

The expected fields are:

| Position | Field     | Description                                     |
| -------- | --------- | ----------------------------------------------- |
| 1        | Timestamp | Date and time of the event                      |
| 2        | Level     | Log level evaluated against the rulebook        |
| 3        | Source IP | Source IP address associated with the event     |
| 4        | Target    | Target or destination associated with the event |
| 5        | Action    | Action or event description                     |

The timestamp must be parseable by the application's configured `LocalDateTime` parser.

The examples below use ISO-style local timestamps.

#### Example `logs.txt`

```text
2024-03-15T08:30:10 | INFO | 10.0.0.2 | server01 | LOGIN_SUCCESS
2024-03-15T08:32:10 | WARN | 10.0.0.4 | server02 | LOGIN_ATTEMPT
2024-03-15T08:34:10 | ALERT | 10.0.0.3 | server03 | ACCESS_DENIED
2024-03-15T08:35:10 | ERROR | 10.0.0.5 | server01 | LOGIN_FAILURE
2024-03-15T08:36:10 | INFO | 10.0.0.5 | server01 | SESSION_ACTIVITY
This is not a valid log entry
```

The final line is intentionally malformed.

**Important:** Each valid record must contain exactly five fields. The parser uses the pipe delimiter, not commas.

### 11.2 Rulebook CSV format

The rulebook maps log levels to integer severity scores.

Each row contains two comma-separated values:

```text
LEVEL,SCORE
```

For example:

```csv
INFO,1
WARN,2
ERROR,3
CRITICAL,4
```

This example defines four recognized log levels.

The score is an integer. The current analysis logic flags recognized levels with scores greater than or equal to `3`.

The example rulebook is illustrative; use the actual `rules.csv` supplied with your project or create one that matches your intended configuration.

#### Rulebook validation

The rulebook reader checks the CSV structure and severity values.

The current implementation rejects:

- Rows that do not contain exactly two fields.
- Severity scores that cannot be parsed as integers.
- Duplicate log levels.
- An empty rulebook file.

Invalid rulebook entries produce an error identifying the relevant row where applicable.

**Do not assume that a CSV header is supported.** The current reader processes the file's lines as rule rows. Unless the implementation is changed to handle headers explicitly, provide the rules directly without a header row.

### 11.3 Log level matching

Log level matching is case-sensitive.

For example, if the rulebook defines:

```csv
ERROR,3
```

then `ERROR` is recognized, while `error` is not recognized as the same level.

Make sure the levels used in your log file match the configured rulebook entries exactly.

### 11.4 Unknown levels

A structurally valid log entry can contain a level that is not present in the rulebook.

For example:

```text
2024-03-15T08:34:10 | ALERT | 10.0.0.3 | server03 | ACCESS_DENIED
```

If `ALERT` is not defined in `rules.csv`, the entry is recorded as an unknown pattern.

It is not treated as a malformed line solely because its level is unknown.

### 11.5 Malformed records

A line that fails parsing is recorded as malformed.

For example:

```text
This is not a valid log entry
```

The report preserves the line number and original content.

Malformed lines are excluded from the successfully parsed log entries used for the activity counts and suspicious-IP analysis.

---

## 12. Log Analysis Process

TraceFinder processes the inputs in a defined sequence.

### Step 1: Validate command-line arguments

The application checks that exactly three arguments have been supplied.

If the arguments are missing or the count is incorrect, the application displays usage instructions and exits with a non-zero status.

### Step 2: Load the rulebook

The CSV rulebook is read and validated.

If the rulebook cannot be read or contains invalid data, the application displays an error and terminates.

### Step 3: Read and parse the log file

Each log line is passed to the parser.

Successfully parsed lines are retained with their original line numbers and raw content.

Malformed lines are recorded separately.

### Step 4: Evaluate each valid entry

For each successfully parsed log entry, the analyzer checks the configured severity rules.

The analysis has three possible outcomes:

- A recognized level with a score below `3`: the entry is not flagged.
- A recognized level with a score of `3` or higher: the entry is flagged.
- An unrecognized level: the entry is recorded as an unknown pattern.

### Step 5: Identify suspicious IP addresses

The application extracts the source IP addresses from flagged entries.

Duplicate IP addresses are removed from the list of suspicious IPs.

For each suspicious IP, the application counts all successfully parsed log entries associated with that IP.

### Step 6: Generate the report

The report writer constructs the final report.

It includes the activity summary, flagged entries, suspicious IP addresses, unknown patterns, and malformed lines.

### Step 7: Write the report

The report is written to the requested output path.

If writing fails, the application displays an error and exits with a non-zero status.

---

## 13. Understanding the Report

The report begins with a title and generation timestamp, followed by five sections.

### 13.1 Report header

Example:

```text
=== Log Analysis Report ===
Generated: 2026-10-23 10:30:00
```

The timestamp is generated at report creation time.

The displayed date and time use the application's local date-time environment and the format:

```text
yyyy-MM-dd HH:mm:ss
```

The example timestamp is illustrative.

### 13.2 Activity Summary

Example:

```text
--- Activity Summary ---
INFO: 2
WARN: 1
ERROR: 1
CRITICAL: 0
```

This section counts successfully parsed log entries for each level defined in the rulebook.

A configured level appears even if no valid entries use that level, in which case its count is zero.

Entries with unknown levels are not included in the counts for configured levels.

Malformed lines are also excluded.

### 13.3 Flagged Entries

Example:

```text
--- Flagged Entries ---
2024-03-15T08:35:10 | ERROR | 10.0.0.5
```

This section lists entries whose recognized severity scores meet the flagging threshold.

The current threshold is:

```text
Severity score >= 3
```

The displayed fields are:

- Timestamp
- Log level
- Source IP address

### 13.4 Suspicious Activity by IP

Example:

```text
--- Suspicious Activity by IP ---
10.0.0.5: 2 entries
```

This section lists unique source IP addresses found in flagged entries.

The count represents all successfully parsed log entries associated with each listed IP, including entries that were not themselves flagged.

Consequently, an IP can have two entries in this section even when only one of its events appears under Flagged Entries.

An IP address is not listed as suspicious merely because it appears in the input file. It must be associated with at least one flagged entry.

### 13.5 Unknown Patterns

Example:

```text
--- Unknown Patterns ---
Line 3: 2024-03-15T08:34:10 | ALERT | 10.0.0.3 | server03 | ACCESS_DENIED
```

This section lists structurally valid entries whose log levels are not defined in the rulebook.

Each item includes the original line number and content.

This allows the operator to inspect unexpected log levels and decide whether the rulebook should be updated.

### 13.6 Malformed Lines

Example:

```text
--- Malformed Lines ---
Line 6: This is not a valid log entry
```

This section lists lines that could not be parsed.

Each item includes:

- The original, one-based line number.
- The raw line content.

The application skips malformed lines during normal log analysis but preserves them in the report for investigation.

### 13.7 Empty sections

All five section headings are included even when no entries qualify for a particular section.

For example, a log file containing no flagged entries still produces a Flagged Entries section.

This provides a consistent report structure for downstream review and automated checks.

---

## 14. Error Handling

TraceFinder distinguishes fatal input/output errors from individual malformed records.

### 14.1 Incorrect argument count

The application requires three arguments.

An incorrect number of arguments results in a usage message and a non-zero exit status.

Expected usage:

```text
java -jar tracefinder.jar <log-file> <rules-file> <report-file>
```

### 14.2 Missing or unreadable rulebook

If the rulebook cannot be read, the application reports the problem and exits unsuccessfully.

An invalid rulebook also causes the application to stop rather than analyze logs using incomplete or invalid rules.

### 14.3 Missing or unreadable log file

If the log file cannot be read, the application reports the problem and exits unsuccessfully.

### 14.4 Invalid rulebook contents

Examples include malformed CSV rows, invalid integer severity scores, and duplicate levels.

The rulebook reader provides contextual error messages for the relevant row where applicable.

### 14.5 Malformed log lines

Individual malformed lines do not cause the entire analysis to fail.

Instead, they are recorded and reported while the application continues processing other lines.

### 14.6 Unknown log levels

Unknown levels do not cause the entire analysis to fail.

They are recorded under Unknown Patterns.

### 14.7 Report-writing failures

If the output file cannot be written, the application reports the failure and exits with a non-zero status.

The parent directory of the output file must already exist.

### 14.8 Process exit status

The expected behavior is:

| Outcome                             | Exit status |
| ----------------------------------- | ----------: |
| Successful report generation        |         `0` |
| Incorrect argument count            |    Non-zero |
| Rulebook read or validation failure |    Non-zero |
| Log file read failure               |    Non-zero |
| Report-writing failure              |    Non-zero |

The current implementation uses `System.exit(1)` for fatal errors.

---

## 15. Testing

Automated tests are an important part of the TraceFinder development workflow.

The project contains unit tests for the core components and integration tests for the packaged command-line application.

### 15.1 Run the unit tests

```bash
mvn test
```

This runs the tests selected by Maven Surefire.

### 15.2 Run the complete verification suite

```bash
mvn clean verify
```

This is the recommended command because it runs the configured build and test lifecycle, including the integration-test verification phase.

### 15.3 Unit test coverage

The project includes tests for the following components.

| Test class            | Component         | Main focus                                  |
| --------------------- | ----------------- | ------------------------------------------- |
| `LogParserTest`       | `LogParser`       | Parsing valid and invalid log entries       |
| `SeverityCheckerTest` | `SeverityChecker` | Recognized levels and severity scores       |
| `LogFileReaderTest`   | `LogFileReader`   | Reading logs and separating malformed lines |
| `RuleBookReaderTest`  | `RuleBookReader`  | CSV validation and rule loading             |
| `LogAnalyzerTest`     | `LogAnalyzer`     | Flagged entries and unknown patterns        |
| `ReportWriterTest`    | `ReportWriter`    | Report contents and section generation      |

These tests help isolate failures to specific components.

### 15.4 CLI integration tests

`TraceFinderIT.java` exercises the application as a command-line program using the packaged JAR.

The existing integration-test scenarios include:

- Running with no arguments.
- Handling a missing log file.
- Handling an invalid rulebook.
- Handling a missing rulebook.
- Handling an unwritable report destination.
- Generating a report with valid arguments.
- Generating a report for an empty log file.

The integration tests check behavior such as process exit status, error messages, and report generation.

### 15.5 Why use `mvn clean verify`?

A successful `mvn test` run does not, by itself, establish that the packaged executable and integration tests work correctly.

The project is configured to run CLI integration tests through Maven Failsafe during the verification lifecycle.

Therefore, use:

```bash
mvn clean verify
```

before considering a change fully verified.

### 15.6 Recommended manual verification

After a successful build, copy the JAR to the project root and run:

```bash
cp target/tracefinder.jar tracefinder.jar
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

Inspect `report.txt` and confirm that:

- The report title appears at the beginning.
- A generation timestamp is present.
- All five section headings are present.
- Configured log levels have the expected counts.
- Flagged entries meet the severity threshold.
- Suspicious IP counts include all successfully parsed entries for each suspicious IP.
- Unknown patterns preserve their original line numbers and content.
- Malformed lines preserve their original line numbers and content.

On Windows PowerShell, use `Copy-Item` instead of `cp` if needed.

---

## 16. Development Workflow

TraceFinder is developed as a modular Java project.

Changes should be implemented and tested in manageable increments.

### 16.1 Start from the project root

```bash
cd trace-finder
```

### 16.2 Check the working tree

```bash
git status
```

### 16.3 Make a focused change

Prefer changes that address one responsibility at a time.

For example:

- Parser changes should be accompanied by parser tests.
- Analyzer changes should be accompanied by analyzer tests.
- Report changes should be accompanied by report-writer tests.
- CLI changes should be accompanied by integration tests where appropriate.

### 16.4 Run verification

```bash
mvn clean verify
```

Resolve failures before proceeding.

### 16.5 Review the diff

```bash
git diff
```

Review staged changes with:

```bash
git diff --cached
```

### 16.6 Stage and commit

Stage only the intended files:

```bash
git add <file-paths>
```

Use Conventional Commits for commit messages.

Examples:

```bash
git commit -m "test(parser): cover invalid timestamp handling"
```

```bash
git commit -m "fix(writer): correct report section formatting"
```

```bash
git commit -m "feat(analyzer): add severity filtering"
```

Choose the commit type and description that accurately represent the actual change.

### 16.7 Push the branch

```bash
git push
```

If the branch has not yet been configured to track its remote, use:

```bash
git push -u origin feat/jairo/tracefinder
```

### 16.8 Keep generated artifacts out of version control

The project ignores generated build output and report files.

In particular, do not commit the generated `tracefinder.jar` or Maven's `target/` directory.

The source code, Maven configuration, tests, documentation, and intended project input files should be tracked as appropriate.

---

## 17. Troubleshooting

### Problem: `Unable to access jarfile tracefinder.jar`

**Possible cause:** The JAR is not present in the current directory.

**Solution:** Build the application and copy the JAR from `target/`.

```bash
mvn clean verify
cp target/tracefinder.jar tracefinder.jar
```

Then rerun:

```bash
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

### Problem: `Unable to access jarfile target/tracefinder.jar`

**Possible cause:** The project has not been packaged successfully, or the JAR filename differs from the configured final name.

**Solution:**

```bash
mvn clean package
```

Check the Maven build output and confirm the configured final name in `pom.xml`.

### Problem: Maven cannot resolve dependencies

**Possible cause:** Network connectivity problems, repository availability, or an incorrect Maven configuration.

**Solution:**

```bash
mvn dependency:resolve
```

Check the resulting error message and verify that Maven can access its configured dependency repositories.

### Problem: Java version errors

**Possible cause:** Maven is running under an incompatible Java installation.

**Solution:**

```bash
java -version
javac -version
mvn -version
```

Check the Java version reported by Maven as well as the system Java installation.

### Problem: The report does not contain the expected heading

**Possible cause:** You may be running an older JAR.

**Solution:** Rebuild the project and copy the newly generated JAR to the root directory.

```bash
mvn clean verify
cp target/tracefinder.jar tracefinder.jar
```

Run the application again and inspect the generated report.

### Problem: A valid-looking log entry is reported as malformed

**Possible causes:**

- The line does not contain exactly five pipe-delimited fields.
- The timestamp cannot be parsed.
- The timestamp format differs from what the parser expects.

**Solution:** Check the source line and compare it with the documented input format.

### Problem: A log level appears under Unknown Patterns

**Possible cause:** The log level is absent from the rulebook or differs in capitalization.

**Solution:** Check the exact spelling and case of the log level in both files.

For example, `ERROR` and `error` are different values for the current case-sensitive matching behavior.

### Problem: Maven reports failing integration tests

**Possible causes:**

- The executable JAR was not packaged as expected.
- An expected error message or exit code changed.
- The application behavior no longer matches an integration test.
- A test fixture or temporary file is missing.

**Solution:**

```bash
mvn clean verify
```

Read the test failure output and identify the failing test before changing the implementation.

Do not disable a failing test simply to make the build pass. First determine whether the implementation or the test expectation is incorrect.

### Problem: The report cannot be written

**Possible causes:**

- The output directory does not exist.
- The output path points to a directory rather than a file.
- The process does not have permission to write to the destination.

**Solution:** Choose a valid output filename in an existing, writable directory.

For example:

```bash
java -jar tracefinder.jar logs.txt rules.csv output/report.txt
```

Ensure that `output/` exists before running the command.

---

## 18. Current Limitations

The following limitations describe the current implementation and should be considered when using or extending TraceFinder.

### 18.1 Fixed log structure

The parser expects five pipe-delimited fields.

Logs using different field counts or a different delimiter require changes to the parser.

### 18.2 Local date-time parsing

The timestamp is parsed into a `LocalDateTime`.

The current model does not represent a time-zone offset as part of the timestamp.

### 18.3 Integer severity scores

Severity scores are integers loaded from the rulebook.

The flagging threshold is currently fixed at `3` in the analysis logic rather than being supplied as a command-line argument.

### 18.4 Case-sensitive log levels

The log level must match the rulebook entry exactly.

### 18.5 Entire-file processing

The current implementation reads the log entries into memory before completing the analysis.

This approach is straightforward for ordinary input files but may require redesign for very large logs.

### 18.6 Basic report format

The report is plain text.

The current implementation does not produce HTML, JSON, or a machine-readable structured report format.

### 18.7 Basic suspicious-IP analysis

Suspicious IP identification is based on whether an IP appears in at least one flagged entry.

The current logic does not implement more advanced detection based on event frequency, time windows, IP reputation, geographic information, or external threat-intelligence feeds.

### 18.8 No interactive interface

TraceFinder is a command-line application.

It does not currently provide a graphical user interface or web dashboard.

### 18.9 No persistent database

The application uses files for its inputs and report output. It does not persist analysis results in a database.

---

## 19. Potential Future Enhancements

The following are ideas for future development. They are **not claims about functionality already implemented**.

### 19.1 Configurable severity threshold

Allow the user to supply the flagging threshold through a configuration file or command-line option.

### 19.2 Streaming log processing

Process very large log files without retaining every parsed entry in memory.

This would require careful consideration of how to maintain activity counts and suspicious-IP statistics.

### 19.3 Additional report formats

Add optional JSON or HTML report generation.

Structured formats would make it easier to integrate TraceFinder with other applications.

### 19.4 More flexible input parsing

Support configurable delimiters, optional fields, or additional log formats.

### 19.5 Enhanced suspicious-activity rules

Introduce configurable detection rules based on event frequency, repeated failures, or combinations of events.

### 19.6 Improved test coverage

Expand tests for edge cases, unusual inputs, and failure scenarios.

Potential areas include unusual whitespace, duplicate rule definitions, boundary severity scores, and report-format consistency.

### 19.7 Continuous integration

Add automated CI checks that compile the project and run the test suite on every proposed change.

### 19.8 Release automation

Automate packaging and distribution of the executable JAR so users can obtain a tested release without manually rebuilding the project.

### 19.9 Better command-line options

Consider adding help output, version information, and more flexible input and output options while preserving the existing assignment-compatible invocation.

---

## 20. Contributing

Contributions should preserve the project's modular design and existing behavior unless a change is intentional and documented.

A recommended contribution workflow is:

1. Check out the appropriate development branch.
2. Understand the responsibility of the component being modified.
3. Implement one focused change.
4. Add or update tests.
5. Run `mvn clean verify`.
6. Review the code diff.
7. Commit using Conventional Commits.
8. Push the branch for review.

### Coding practices

- Keep parsing, reading, analysis, and writing responsibilities separate.
- Use meaningful class, method, and variable names.
- Handle expected errors explicitly.
- Preserve source line numbers when recording problematic input.
- Add clear JavaDoc or code comments to new or modified methods, describing their purpose and important behavior.
- Add tests for behavior changes.
- Avoid committing generated reports and build artifacts.
- Update the README when the CLI, input formats, or supported functionality changes.

---

## Contributors

1. **Odee Jairo**
2. **Samuel Njoroge**

## 21. License

No license information has been specified in this documentation.

Before distributing TraceFinder publicly or accepting external contributions, add an appropriate `LICENSE` file and update this section to reflect the project's actual licensing terms.

---

## Quick Reference

### Build and test

```bash
mvn clean verify
```

### Copy the executable JAR to the project root

```bash
cp target/tracefinder.jar tracefinder.jar
```

### Run TraceFinder using the required command

```bash
java -jar tracefinder.jar logs.txt rules.csv report.txt
```

### Inspect the report

```bash
cat report.txt
```

### Check Git status

```bash
git status
```

### Review recent commits

```bash
git log -5 --oneline
```

---

**TraceFinder — Structured log analysis, severity-based flagging, and report generation in Java.**
