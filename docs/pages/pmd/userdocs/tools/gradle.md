---
title: Gradle
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_gradle.html
last_updated: September 2026 (7.28.0)
---

The [Gradle Build Tool](https://gradle.org/) provides a [PMD Plugin](https://docs.gradle.org/current/userguide/pmd_plugin.html)
that can be added to your build configuration. Technically it is based on the [Ant Task](pmd_userdocs_tools_ant.html).

## Example

In your `build.gradle` add the following:

```
plugins {
    id 'pmd'
}
```

### Custom ruleset

Configuration of a custom ruleset looks like this:

```
pmd {
    ruleSetFiles = files("custom-pmd-ruleset.xml")
    ruleSets = []
}
```

Note: The `ruleSets` array is explicitly set to empty to avoid using the default configuration.

### Fail the build

If you want to fail the build for pmd violations, you need to set `ignoreFailures`:

```
pmd {
    ignoreFailures = false
}
```

More configuration options are documented on [PMD Extension](https://docs.gradle.org/current/dsl/org.gradle.api.plugins.quality.PmdExtension.html).

### Upgrade PMD version

If you want to use a newer PMD version than the default one provided with Gradle, you can do so
with the property `toolVersion`:

```
pmd {
    toolVersion = "{{ site.pmd.version }}"
}
```

{% capture note-gradle-for-pmd-7 %}
For PMD 7, at least Gradle 8.6 is needed. See [Support for PMD 7.0](https://github.com/gradle/gradle/issues/24502).
{% endcapture %}
{% include note.html content=note-gradle-for-pmd-7 %}

### Aux Classpath

Gradle by default adds all project dependencies to the aux classpath. Since PMD 7.27.0, you'll get a warning
if the platform classes will be resolved by the running JVM. To avoid this, explicitly add the platform
classpath of the correct Java version to PMD's aux classpath. The example is using the [toolchains feature](https://docs.gradle.org/current/userguide/toolchains.html)
of Gradle:

```
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    pmdAux javaToolchains.launcherFor(java.toolchain)
            .map { it.metadata.installationPath }
            .map { it.files("lib/jrt-fs.jar")}
}
```

See [Providing the auxiliary classpath](pmd_languages_java.html#providing-the-auxiliary-classpath).

### Complete Example

See <https://github.com/pmd/pmd-examples/tree/main/gradle/simple-project>.

## References

Source code for Gradle's PMD Plugin is available here:

*   [gradle/gradle code-quality](https://github.com/gradle/gradle/tree/master/platforms/jvm/code-quality/src/main/java/org/gradle/api/plugins/quality)
    *   [Pmd.java](https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/java/org/gradle/api/plugins/quality/Pmd.java)
    *   [PmdExtension.java](https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/java/org/gradle/api/plugins/quality/PmdExtension.java)
    *   [PmdPlugin.java](https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/java/org/gradle/api/plugins/quality/PmdPlugin.java)
    *   [PmdInvoker.java](https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality-workers/src/main/java/org/gradle/api/plugins/quality/internal/PmdInvoker.java)
*   The default PMD version used by Gradle 9.7.1: [DEFAULT_PMD_VERSION](https://github.com/gradle/gradle/blob/v9.7.1/platforms/jvm/code-quality/src/main/java/org/gradle/api/plugins/quality/PmdPlugin.java#L63)
