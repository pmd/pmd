# AGENTS.md

Guidance for AI coding agents working in this repository (`PMD`, a multi-language static
analysis tool). It only summarizes what the build, the tests and the existing documentation
already require. The human-oriented docs are in `CONTRIBUTING.md` and `docs/pages/pmd/devdocs/`
(rendered at <https://docs.pmd-code.org/latest/pmd_devdocs_contributing.html>).

## Project layout

- Multi-module Maven build: `pmd-core` (base package `net.sourceforge.pmd`), one module per
  language (`pmd-java`, `pmd-apex`, ...) with the base package
  `net.sourceforge.pmd.lang.<language id>`,
  plus `pmd-cli`, `pmd-dist`, `pmd-doc`, `pmd-test` and `pmd-lang-test`.
- The language id is not always the module name: `pmd-javascript` → `ecmascript`, and
  `pmd-xml` contains `xml`, `pom`, `wsdl` and `xsl`.
- Parsers are generated from the grammars in `src/main/javacc/` (`*.jj`, `*.jjt`) and
  `src/main/antlr4/` (`*.g4`) into `target/`. Change the grammar, never the generated sources.
- Some tests are written in Kotlin (`pmd-java/src/test/kotlin`, `pmd-modelica/src/test/kotlin`).

## Build

- Building requires `JDK 21+` (enforced by `maven-enforcer-plugin`). Always use `./mvnw`, with
  `--no-transfer-progress` to keep the output free of download progress logs.
- Use a `UTF-8` locale (`LANG=C.UTF-8 LC_ALL=C.UTF-8`). Under the `POSIX` locale, tests with
  non-ASCII file names fail with `InvalidPathException: Malformed input or input contains
  unmappable characters` (e.g. `HTMLRendererTest` in `pmd-core`).
- Main and test code are compiled with `--release 8`: no Java 9+ APIs or language features.
- `maven-compiler-plugin` uses `failOnWarning=true`, so a new compiler warning (e.g. a
  deprecated item without `@Deprecated`) fails the build.
- A module that depends on other `SNAPSHOT` modules needs them built in the same reactor
  (`-am`) or installed first; otherwise Maven fails with
  `Could not resolve dependencies ... -SNAPSHOT`.
- `-PfastSkip` skips Checkstyle, PMD/CPD self-checks, japicmp, Javadoc, Dokka and source jars.
  Use it while iterating, but run the checks once without it before pushing (see below).
- A full build prints `[ERROR] [replaceregexp] The following file is missing: ...ParserImpl.java`
  for some JavaCC-based modules (e.g. `pmd-cpp`, `pmd-python`). This is harmless; check for
  `BUILD SUCCESS` and the surefire summary instead.

## Common commands

```bash
# Compile everything without tests and checks (like the CI "compile" job)
./mvnw -e --no-transfer-progress clean verify -PfastSkip -DskipTests -DskipITs

# Install pmd-java's dependencies once, then build/test only pmd-java
./mvnw -e --no-transfer-progress -pl pmd-java -am -PfastSkip install -DskipTests -DskipITs
./mvnw -e --no-transfer-progress -pl pmd-java -PfastSkip verify
./mvnw -e --no-transfer-progress -pl pmd-java -PfastSkip test -Dtest=UnusedPrivateMethodTest

# One test together with the upstream modules: -am makes surefire fail in modules
# without a matching test, unless -Dsurefire.failIfNoSpecifiedTests=false is set
./mvnw -e --no-transfer-progress -pl pmd-java -am -PfastSkip test \
  -Dtest=UnusedPrivateMethodTest -Dsurefire.failIfNoSpecifiedTests=false

# Checkstyle, PMD/CPD self-checks and japicmp (all bound to the verify phase)
./mvnw -e --no-transfer-progress -pl pmd-java -am verify -DskipTests -DskipITs

# Full build, as asked for in the PR template
./mvnw --no-transfer-progress clean verify

# Spell check, like the CI "spelling" job (config: .ci/files/typos.toml)
.ci/tools/typos.sh --format brief .
```

The CI jobs are defined in `.github/workflows/build.yml`; look there to reproduce a failing
step. Every PR is additionally checked by the
[PMD Regression Tester](https://docs.pmd-code.org/latest/pmd_devdocs_pmdtester.html); a bot
comments with the new and removed violations on real projects. Unexpected changes there are
usually false positives/negatives introduced by the PR.

## Rules and rule tests

Docs: `docs/pages/pmd/userdocs/extending/` (writing rules, testing) and
`docs/pages/pmd/devdocs/major_contributions/rule_guidelines.md`.

- Rules are defined in `<module>/src/main/resources/category/<language id>/<category>.xml`.
  `AbstractRuleSetFactoryTest` (a `RuleSetFactoryTest` in every module that has rules) fails if
  - rules are not sorted alphabetically (case-insensitive) within the file,
  - a rule has no `since` attribute; a new rule uses the version from `pom.xml` without
    `-SNAPSHOT` (`7.29.0-SNAPSHOT` → `since="7.29.0"`),
  - `externalInfoUrl` is not
    `${pmd.website.baseurl}/pmd_rules_<language id>_<category>.html#<rule name in lowercase>`,
  - a Java-based rule class is not
    `net.sourceforge.pmd.lang.<language id>.rule.<category>.<RuleName>Rule`
    (XPath rules use `net.sourceforge.pmd.lang.rule.xpath.XPathRule`),
  - the XML does not validate against the ruleset schema.
- Rule tests are data-driven. For rule `Foo` in category `bestpractices` of `pmd-java`:
  - `src/test/java/net/sourceforge/pmd/lang/java/rule/bestpractices/FooTest.java` extends
    `PmdRuleTst` and normally has no body,
  - `src/test/resources/net/sourceforge/pmd/lang/java/rule/bestpractices/xml/Foo.xml` holds the
    `<test-code>` cases (`<description>`, `<expected-problems>`, optional
    `<expected-linenumbers>`, `<rule-property>`, `<code>`).
- A fix for a false positive/negative adds a `<test-code>` case with a minimal reproducer;
  the `<description>` usually mentions the issue (`#1234`).
- `<test-code focused="true">` runs only that case while iterating; remove it before committing.
- Rule documentation pages in `docs/pages/pmd/rules/` are generated (`-Pgenerate-rule-docs`) and
  ignored by git. Edit `<description>` and `<example>` in the category XML instead.
- Only `pmd-java` and `pmd-apex` have a `rulesets/<language id>/quickstart.xml`; whether a new
  rule belongs there is a maintainer decision.

## Public API

API rules are in [ADR 3](docs/pages/pmd/projectdocs/decisions/adr-3.md):

- Packages with an `internal` or `impl` segment are not public API. Members that must stay in a
  public package but are not API get `@InternalApi`; new unstable API can get `@Experimental`.
- Everything else is public. It can be deprecated at any time (`@Deprecated` plus the javadoc tag
  `@deprecated Since x.y.z. ...`), but only removed in a major release.
- japicmp compares each module with the last release and fails the `verify` build on
  incompatible changes outside `@InternalApi`/`@Experimental` (report in `target/japicmp/`).
- Concrete AST classes are final, with package-private constructors and setters.

## Code style

Checkstyle runs in `verify` (not with `-PfastSkip`), on main and test code, with the
[config from pmd/build-tools](https://github.com/pmd/build-tools/blob/main/src/main/resources/net/sourceforge/pmd/pmd-checkstyle-config.xml).
Frequent failures:

- every `.java`, `.kt` and `.less` file starts with exactly this header (`.properties` files use
  the same text with `#`):

  ```java
  /*
   * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
   */
  ```

- import order: static imports first, then `java`/`javax`, `org`, `net.sourceforge.pmd`, all
  other imports last, groups separated by an empty line; no star imports,
- no tabs, newline at end of file.

PMD also analyzes its own code with PMD (`pmd-main`/`pmd-test` executions in the root `pom.xml`).

## Release notes

Maintainers add the entries in `docs/pages/release_notes.md` (fixed issues, new and changed
rules, API changes) when they merge a PR. A PR doesn't need to change that file, and changing it
causes merge conflicts with other PRs.

## Issues and pull requests

- Bigger changes (more than about 5 files) need an issue first. Use the templates in
  `.github/ISSUE_TEMPLATE/`: false positive, false negative, new rule, feature request, bug report.
  For rule issues, give the PMD version, the rule and a minimal code sample.
- Issue and PR titles start with the language in brackets: `[java]`, `[apex]`, ... or `[core]`,
  `[cli]`, `[doc]` for changes that are not language specific (build and infrastructure changes
  use `chore: ...`). PRs that fix an issue are titled
  `[java] Fix #1234: <rule>: <summary>`; this title becomes the subject of the squash commit.
- Create the PR against `main` and fill in `.github/PULL_REQUEST_TEMPLATE.md`
  (`- Fix #1234` under "Related issues").
- Maintainers sometimes push to the PR branch (merging `main`, small fixes). Pull before you
  push again and don't force-push over their commits.
- Keep a PR focused on one issue; don't mix in unrelated reformatting or refactoring.
