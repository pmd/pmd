# Running PMD on Android Kotlin Projects

This guide explains how to run PMD with Kotlin rules against an Android project,
including how to build a complete classpath so that type-aware rules
(e.g. `UnresolvedType`) produce accurate results.

## Prerequisites

- PMD 7.24+ with `pmd-kotlin` installed
- Android SDK installed (see [Install Android SDK](#install-android-sdk))
- Gradle wrapper present in the Android project
- Java 17 or 21 (the Kotlin Gradle plugin does not support Java 25+)

## Install Android SDK

If you do not have the Android SDK, use `sdkmanager` from the command-line tools:

```bash
# Download command-line tools from https://developer.android.com/studio#command-tools
# Extract to ~/Android/Sdk/cmdline-tools/latest

export ANDROID_SDK_ROOT=~/Android/Sdk
sdkmanager "platforms;android-35"
```

Create `local.properties` in the project root:

```bash
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
```

## Build the Project

PMD needs compiled class files on its classpath. Build the debug variant including tests:

```bash
# Switch to Java 17 or 21 if needed.
# sdkman example (source init first if not in .bashrc):
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 21      # picks the latest Java 21 managed by sdkman

# or set JAVA_HOME directly:
export JAVA_HOME=/path/to/jdk-21

./gradlew :app:compileDebugKotlin :app:compileDebugJavaWithJavac \
          :app:compileDebugUnitTestKotlin :app:compileDebugAndroidTestKotlin
```

> **Note:** `compileDebugJavaWithJavac` is needed to compile generated Java sources
> such as `BuildConfig`. Without it, those imports will be flagged as `UnresolvedType`.

## Collect the Classpath

Add helper tasks to `app/build.gradle.kts` to print all resolved compile JARs — one for
the main source set and one each for unit tests and instrumented (Android) tests:

```kotlin
tasks.register("printAndroidClasspath") {
    dependsOn("compileDebugKotlin")
    doLast {
        val variant = android.applicationVariants.find { it.name == "debug" }!!
        variant.getCompileClasspath(null).files.forEach { println(it.absolutePath) }
    }
}

tasks.register("printTestClasspath") {
    dependsOn("compileDebugUnitTestKotlin")
    doLast {
        val variant = android.unitTestVariants.find { it.name == "debugUnitTest" }!!
        variant.getCompileClasspath(null).files.forEach { println(it.absolutePath) }
    }
}

tasks.register("printAndroidTestClasspath") {
    dependsOn("compileDebugAndroidTestKotlin")
    doLast {
        val variant = android.testVariants.find { it.name == "debugAndroidTest" }!!
        variant.getCompileClasspath(null).files.forEach { println(it.absolutePath) }
    }
}
```

Run all three tasks and merge into a single deduplicated classpath file. Set
`ANDROID_SDK_ROOT` explicitly so all subsequent commands work regardless of shell
environment:

```bash
export ANDROID_SDK_ROOT=~/Android/Sdk   # adjust if SDK is elsewhere

./gradlew :app:printAndroidClasspath \
          :app:printTestClasspath \
          :app:printAndroidTestClasspath -q 2>&1 \
  | grep -E "\.jar$|kotlin-classes|/classes$" \
  | sort -u > /tmp/android_classpath.txt
```

> **Tip:** The tasks use Gradle's transform pipeline, which automatically extracts
> `classes.jar` from AAR dependencies and includes compiled test class directories.
>
> **Important:** Some libraries (e.g. `okhttp-dnsoverhttps`) depend on transitive JARs
> (e.g. `okhttp-jvm`) that may not appear in the compile classpath directly. If PMD
> throws `NoClassDefFoundError` during analysis, find and add the missing transitive JAR:
>
> ```bash
> find ~/.gradle/caches -name "okhttp-jvm-*.jar" >> /tmp/android_classpath.txt
> ```

Then append the Android platform JAR and your compiled project classes:

```bash
echo "$ANDROID_SDK_ROOT/platforms/android-35/android.jar" >> /tmp/android_classpath.txt
echo "$PWD/app/build/tmp/kotlin-classes/debug"            >> /tmp/android_classpath.txt
echo "$PWD/app/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes" \
                                                           >> /tmp/android_classpath.txt
```

> **Warning:** If `ANDROID_SDK_ROOT` is not set, `android.jar` will be silently added
> with a broken path and PMD will crash with `NoClassDefFoundError: android/...`.
> PMD logs a `[WARN] Skipping invalid Kotlin aux classpath entry` when this happens.
> Always verify the path resolves: `ls "$ANDROID_SDK_ROOT/platforms/android-35/android.jar"`

Build a colon-separated string:

```bash
CLASSPATH=$(paste -sd ':' /tmp/android_classpath.txt)
```

## Run PMD

Run PMD against all Kotlin source files. Use `app/src` (not `app/src/main/java`) to
include test sources, and avoid `app/build` to skip large generated files that cause
parse timeouts:

```bash
RULESETS="category/kotlin/bestpractices.xml,category/kotlin/design.xml,category/kotlin/errorprone.xml,category/kotlin/multithreading.xml,category/kotlin/performance.xml"

pmd check \
  --dir app/src \
  --rulesets "$RULESETS" \
  --aux-classpath "$CLASSPATH" \
  --no-progress \
  --format text
```

> **Note:** The available Kotlin rulesets are: `bestpractices`, `design`, `errorprone`,
> `multithreading`, `performance`. There is no `codestyle` ruleset — omit it.

## Parse Timeouts

Some large or complex Kotlin files (typically > 50 KB) can cause ANTLR's LL prediction
to explode exponentially. PMD applies a per-file parse timeout (default: 30 seconds,
configurable via the system property `pmd.kotlin.parseTimeoutSeconds`) to prevent the
entire run from hanging. Files that exceed the timeout are skipped and logged as warnings:

```
[WARN] Kotlin parse timeout (30s) exceeded for file: LargeScreen.kt. Skipping.
```

They also appear in the PMD output as `ParseException` processing errors:

```
/path/to/LargeScreen.kt	-	ParseException: Parse timeout (30s) exceeded for LargeScreen.kt
```

### Excluding known timeout files from future runs

Once you have identified which files time out, build a file list that excludes them:

```bash
# Collect timed-out basenames from a previous run's stderr
grep "parse timeout" /tmp/pmd-err.txt \
  | grep -oP 'for file: \K\S+(?=\.)' \
  | sort > /tmp/timeout_files.txt

# Build a file list that omits those files
find app/src/main/java -name "*.kt" \
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

You can adjust the timeout via the `PMD_JAVA_OPTS` environment variable (the `-J-D`
syntax is not supported by the PMD CLI):

```bash
PMD_JAVA_OPTS="-Dpmd.kotlin.parseTimeoutSeconds=20" \
pmd check --dir app/src ...
```

The default of 30 seconds works well for most projects. Setting it lower (e.g. 10s)
causes more files to time out on legitimate complex code; setting it higher reduces
false timeouts but makes the run slower when large files are present.

> **Note:** Timed-out files are genuinely large and complex — they are worth reviewing
> manually or splitting into smaller files rather than simply suppressing them.

## Understanding `UnresolvedType` Findings

The `UnresolvedType` rule (in `errorprone.xml`) fires on `import` statements
whose type cannot be resolved by the kotlin-type-mapper. Common causes:

| Cause | Fix |
|-------|-----|
| Android SDK missing | Add `android.jar` to `--aux-classpath` |
| AAR dependencies not extracted | Use `printAndroidClasspath` task (extracts AARs via Gradle transforms) |
| Project-internal classes missing | Add `build/tmp/kotlin-classes/debug` to `--aux-classpath` |
| Generated sources (e.g. `BuildConfig`) | Run `compileDebugJavaWithJavac` and add `build/intermediates/javac/.../classes` |
| Test-only dependencies | Use `printTestClasspath` / `printAndroidTestClasspath` tasks (see above) |
| Truly missing 3rd-party dependencies | Add the missing JAR or suppress the rule |

Without a classpath, nearly every file will produce `UnresolvedType` findings.
With a complete classpath (as above), only a small number of hard-to-resolve types remain.

> **Note:** Even with a complete classpath, a few types may remain unresolved due to
> kotlin-type-mapper limitations:
> - **JDK platform types** (e.g. `java.security.SecureRandom`) — resolved via the
>   running JVM in most cases, but some module-system edge cases may be missed
> - **Kotlin extension properties** (e.g. `kotlinx.serialization.json.jsonObject`) —
>   extension members are not always resolvable from import statements alone
> - **Project submodule classes** not compiled as part of the debug variant
>
> These can be suppressed with `// NOPMD` or a ruleset override (see below).

## Example Results

Running against a medium-sized Android project (~400 Kotlin files, using `--dir app/src`):

| Classpath | UnresolvedType findings |
|-----------|------------------------|
| None | ~4,800 |
| Android SDK only (`android.jar`) | ~4,200 |
| SDK + AAR-extracted JARs | ~300 |
| SDK + AARs + compiled project classes | ~43 |
| + test JARs (unit test + Android test) | ~43 |

With a complete classpath the remaining ~43 findings are either genuine violations or
known kotlin-type-mapper limitations (JDK platform types, Kotlin extension properties,
classes from submodules not included in the compile variant). All other findings are
genuine code quality issues — no noise.

> **Tip:** Adding the test JARs primarily helps when scanning test source files.
> The main source set rarely references test-only types.

## Suppressing `UnresolvedType` for Known Missing Deps

Add a `@Suppress` annotation or use a PMD suppress comment:

```kotlin
import com.example.missing.Dep // NOPMD - dependency not available at analysis time
```

Or exclude the rule entirely for generated/vendor packages via a ruleset override:

```xml
<rule ref="category/kotlin/errorprone.xml/UnresolvedType">
  <properties>
    <property name="violationSuppressXPath"
              value="//ImportHeader[starts-with(@Image, 'com.example.generated')]"/>
  </properties>
</rule>
```
