# Running PMD on Maven Kotlin Projects

This guide explains how to run PMD with Kotlin rules against a Maven project,
including how to build a complete classpath so that type-aware rules
(e.g. `UnresolvedType`) produce accurate results.

## Prerequisites

- PMD 7.24+ with `pmd-kotlin` installed
- Maven (`mvn`) on the PATH, or use the project's `./mvnw` wrapper if present
- Java 17 or 21 (older Kotlin compiler plugins may not support Java 22+)

> **Tip:** Use sdkman to switch Java and Maven versions:
> ```bash
> source "$HOME/.sdkman/bin/sdkman-init.sh"
> sdk use java 21.0.7-tem
> sdk use maven 3.9.15   # install first with: sdk install maven
> ```

## Build the Project

PMD needs compiled class files on its classpath. Build the project including tests.
Use `./mvnw` if the project provides a wrapper, otherwise use system `mvn`:

```bash
mvn compile test-compile -DskipTests -q
```

This produces:
- `target/classes/` — main compiled classes
- `target/test-classes/` — test compiled classes

## Collect the Classpath

Use the Maven Dependency plugin to write the full transitive compile classpath to a file:

```bash
mvn dependency:build-classpath \
  -Dmdep.outputFile=/tmp/maven_classpath.txt \
  -Dmdep.includeScope=test
```

`-Dmdep.includeScope=test` ensures test-scoped dependencies (JUnit, Mockito, etc.)
are included — needed when scanning test sources.

For a multi-module project, run with `-pl` to target a specific module or use the
root POM and aggregate per-module outputs:

```bash
# single module
mvn -pl my-app dependency:build-classpath \
  -Dmdep.outputFile=/tmp/maven_classpath.txt \
  -Dmdep.includeScope=test

# all modules, one file per module — then merge
mvn dependency:build-classpath \
  -Dmdep.outputFile=/tmp/maven_classpath.txt \
  -Dmdep.includeScope=test \
  --also-make
cat target/maven_classpath.txt >> /tmp/maven_classpath.txt  # repeat per module or see tip below
```

> **Tip:** For multi-module builds, use `dependency:build-classpath` with
> `-Dmdep.outputFile` set to an absolute path — each module overwrites it.
> Use a shell loop instead:
>
> ```bash
> find . -name "pom.xml" -not -path "*/target/*" -mindepth 2 | while read pom; do
>   dir=$(dirname "$pom")
>   mvn -f "$pom" dependency:build-classpath \
>     -Dmdep.outputFile="$PWD/tmp_cp_$(basename $dir).txt" \
>     -Dmdep.includeScope=test -q
> done
> cat /tmp/tmp_cp_*.txt | tr ':' '\n' | sort -u > /tmp/maven_classpath.txt
> ```

The output file contains colon-separated JAR paths on a single line. Convert to
one-entry-per-line and append compiled classes:

```bash
tr ':' '\n' < /tmp/maven_classpath.txt \
  | sort -u > /tmp/maven_cp_lines.txt

echo "$PWD/target/classes"      >> /tmp/maven_cp_lines.txt
echo "$PWD/target/test-classes" >> /tmp/maven_cp_lines.txt
```

Build a colon-separated string for the PMD `--aux-classpath` flag:

```bash
CLASSPATH=$(paste -sd ':' /tmp/maven_cp_lines.txt)
```

## Run PMD

Run PMD against all Kotlin source files. Use `src/` to cover both main and test
sources; `target/` is excluded to avoid scanning generated sources:

```bash
RULESETS="category/kotlin/bestpractices.xml,category/kotlin/design.xml,category/kotlin/errorprone.xml,category/kotlin/multithreading.xml,category/kotlin/performance.xml"

pmd check \
  --dir src \
  --rulesets "$RULESETS" \
  --aux-classpath "$CLASSPATH" \
  --no-progress \
  --format text
```

> **Note:** The available Kotlin rulesets are: `bestpractices`, `design`, `errorprone`,
> `multithreading`, `performance`. There is no `codestyle` ruleset — omit it.

### Scanning only main sources

```bash
pmd check --dir src/main/kotlin ...
```

### Scanning only test sources

```bash
pmd check --dir src/test/kotlin ...
```

## Parse Timeouts

Some large or complex Kotlin files (typically > 50 KB) can cause ANTLR's LL prediction
to explode exponentially. PMD applies a per-file parse timeout (default: 30 seconds,
configurable via `pmd.kotlin.parseTimeoutSeconds`) to prevent the entire run from
hanging. Files that exceed the timeout are skipped and logged as warnings:

```
[WARN] Kotlin parse timeout (30s) exceeded for file: LargeScreen.kt. Skipping.
```

They also appear in the PMD output as `ParseException` processing errors:

```
/path/to/LargeScreen.kt	-	ParseException: Parse timeout (30s) exceeded for LargeScreen.kt
```

### Adjusting the timeout

Pass the property via `PMD_JAVA_OPTS` (the `-J-D` syntax is not supported by the PMD CLI):

```bash
PMD_JAVA_OPTS="-Dpmd.kotlin.parseTimeoutSeconds=60" \
pmd check --dir src ...
```

The default of 30 seconds works well for most projects. Generated or auto-formatted files (e.g. from KSP (Kotlin Symbol Processing) or
KAPT (Kotlin Annotation Processing Tool)) often cause timeouts — exclude `target/` and
`build/generated/` rather than raising the timeout.

### Excluding known timeout files

```bash
# Collect timed-out basenames from a previous run's stderr
grep "parse timeout" /tmp/pmd-err.txt \
  | grep -oP 'for file: \K\S+(?=\.)' \
  | sort > /tmp/timeout_files.txt

# Build a file list that omits those files
find src/main/kotlin -name "*.kt" \
  | while read f; do
      base=$(basename "$f")
      grep -qxF "$base" /tmp/timeout_files.txt || echo "$f"
    done > /tmp/kt_files.txt

pmd check \
  --file-list /tmp/kt_files.txt \
  --rulesets "$RULESETS" \
  --aux-classpath "$CLASSPATH" \
  --no-progress \
  --format text
```

## Understanding `UnresolvedType` Findings

The `UnresolvedType` rule (in `errorprone.xml`) fires on `import` statements whose
type cannot be resolved by the kotlin-type-mapper. Common causes:

| Cause | Fix |
|-------|-----|
| Dependencies not on classpath | Run `dependency:build-classpath` with `-Dmdep.includeScope=test` |
| Compiled classes missing | Run `mvn compile test-compile` first; add `target/classes` and `target/test-classes` |
| Multi-module: sibling module classes missing | Add each module's `target/classes` to classpath |
| Generated sources (e.g. from KAPT (Kotlin Annotation Processing Tool) or KSP (Kotlin Symbol Processing)) | Run `mvn generate-sources` first |
| Truly missing 3rd-party dependencies | Add the missing JAR or suppress the rule |

> **Note:** Even with a complete classpath, a few types may remain unresolved due to
> kotlin-type-mapper limitations:
> - **JDK platform types** in edge cases (most JDK types resolve fine via the running JVM)
> - **Kotlin extension properties** — extension members are not always resolvable from
>   import statements alone
> - **Package-level declarations** in external libraries
>
> These can be suppressed with `// NOPMD` or a ruleset override (see below).

## Suppressing `UnresolvedType` for Known Missing Deps

`// NOPMD` comments do not suppress violations on import statements; use a ruleset
override with `violationSuppressRegex` to silence known-missing packages:

```xml
<rule ref="category/kotlin/errorprone.xml/UnresolvedType">
  <properties>
    <!-- suppress generated or runtime-only types by package prefix -->
    <property name="violationSuppressRegex" value=".*com\.example\.generated.*"/>
  </properties>
</rule>
```

Or suppress by XPath expression (matches on the import node text):

```xml
<rule ref="category/kotlin/errorprone.xml/UnresolvedType">
  <properties>
    <property name="violationSuppressXPath"
              value="//ImportHeader[starts-with(@Image, 'com.example.generated')]"/>
  </properties>
</rule>
```
