# Running PMD on Gradle Kotlin Projects

This guide explains how to run PMD with Kotlin rules against a Gradle project,
including how to build a complete classpath so that type-aware rules
(e.g. `UnresolvedType`) produce accurate results.

## Prerequisites

- PMD 7.24+ with `pmd-kotlin` installed
- Gradle wrapper (`./gradlew`) present in the project
- Java 17 or 21 (the Kotlin Gradle plugin does not support Java 22+)

> **Tip:** Use sdkman to switch Java versions:
> ```bash
> source "$HOME/.sdkman/bin/sdkman-init.sh"
> sdk use java 21      # picks the latest Java 21 managed by sdkman
> ```

## Build the Project

PMD needs compiled class files on its classpath. Build main and test sources:

```bash
./gradlew compileKotlin compileTestKotlin
```

This produces:
- `build/classes/kotlin/main/` — main compiled classes
- `build/classes/kotlin/test/` — test compiled classes

## Collect the Classpath

Add a helper task to `build.gradle.kts` to print all resolved compile JARs:

```kotlin
tasks.register("printKotlinClasspath") {
    dependsOn("compileTestKotlin")
    doLast {
        val main = configurations.getByName("compileClasspath").resolvedConfiguration
            .resolvedArtifacts.map { it.file.absolutePath }
        val test = configurations.getByName("testCompileClasspath").resolvedConfiguration
            .resolvedArtifacts.map { it.file.absolutePath }
        (main + test).distinct().forEach { println(it) }
    }
}
```

Run the task and merge into a deduplicated classpath file:

```bash
./gradlew :printKotlinClasspath -q 2>/dev/null \
  | sort -u > /tmp/gradle_classpath.txt

echo "$PWD/build/classes/kotlin/main" >> /tmp/gradle_classpath.txt
echo "$PWD/build/classes/kotlin/test" >> /tmp/gradle_classpath.txt
```

Build a colon-separated string for the PMD `--aux-classpath` flag:

```bash
CLASSPATH=$(paste -sd ':' /tmp/gradle_classpath.txt)
```

## Run PMD

Run PMD against all Kotlin source files:

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

### Adjusting the timeout

Pass the property via `PMD_JAVA_OPTS` (the `-J-D` syntax is not supported by the PMD CLI):

```bash
PMD_JAVA_OPTS="-Dpmd.kotlin.parseTimeoutSeconds=60" \
pmd check --dir src ...
```

The default of 30 seconds works well for most projects. Generated files (e.g. from KSP
(Kotlin Symbol Processing) or KAPT (Kotlin Annotation Processing Tool)) often cause
timeouts — exclude `build/generated/` rather than raising the timeout.

### Excluding known timeout files

```bash
# Collect timed-out basenames from a previous run's stderr
grep "parse timeout" /tmp/pmd-err.txt \
  | grep -oP 'for file: \K\S+(?=\.)' \
  | sort > /tmp/timeout_files.txt

# Build a file list that omits those files
find src -name "*.kt" \
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
| Dependencies not on classpath | Use `printKotlinClasspath` task with both compile and test configurations |
| Compiled classes missing | Run `./gradlew compileKotlin compileTestKotlin` first; add both class output dirs |
| Multi-project: sibling project classes missing | Add each subproject's `build/classes/kotlin/main` to classpath |
| Generated sources (e.g. from KAPT or KSP) | Run `./gradlew kaptGenerateStubsKotlin` or `kspKotlin` first |
| Truly missing 3rd-party dependencies | Add the missing JAR or suppress the rule |

> **Note:** Even with a complete classpath, a few types may remain unresolved due to
> kotlin-type-mapper limitations:
> - **JDK platform types** in edge cases (most JDK types resolve fine via the running JVM)
> - **Kotlin extension properties** — extension members are not always resolvable from
>   import statements alone
> - **Package-level declarations** in external libraries
>
> These can be suppressed with a ruleset override (see below).

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

---

## Android Projects

Android Gradle projects require additional steps to handle AAR dependencies, Android SDK
classes, and generated sources such as `BuildConfig`.

### Prerequisites

- Android SDK installed (see [Install Android SDK](#install-android-sdk))
- Java 17 or 21 (the Kotlin Gradle plugin does not support Java 25+)

### Install Android SDK

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

### Build the Project

Build the debug variant including unit tests and instrumented tests:

```bash
./gradlew :app:compileDebugKotlin :app:compileDebugJavaWithJavac \
          :app:compileDebugUnitTestKotlin :app:compileDebugAndroidTestKotlin
```

> **Note:** `compileDebugJavaWithJavac` is needed to compile generated Java sources
> such as `BuildConfig`. Without it, those imports will be flagged as `UnresolvedType`.

### Collect the Classpath

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

### Run PMD

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

### Understanding `UnresolvedType` Findings

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

### Example Results

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
classes from submodules not included in the compile variant).

> **Tip:** Adding the test JARs primarily helps when scanning test source files.
> The main source set rarely references test-only types.
