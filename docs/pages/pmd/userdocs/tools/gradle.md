---
title: Gradle
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_gradle.html
last_updated: June 2024 (7.3.0)
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

If you want to use a newer PMD version than the default one provided with gradle, you can do so
with the property `toolVersion`:

```
pmd {
    toolVersion = "{{ site.pmd.version }}"
}
```

Note: For PMD 7, at least gradle 8.6 is needed. See [Support for PMD 7.0](https://github.com/gradle/gradle/issues/24502).

## References

Source code for Gradle's PMD Plugin is available here:

*   [gradle/gradle code-quality](https://github.com/gradle/gradle/tree/master/platforms/jvm/code-quality/src/main/groovy/org/gradle/api/plugins/quality)
    *   [Pmd.java](https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/groovy/org/gradle/api/plugins/quality/Pmd.java)
    *   [PmdExtension.java](https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/groovy/org/gradle/api/plugins/quality/PmdExtension.java)
    *   [PmdPlugin.java](https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/groovy/org/gradle/api/plugins/quality/PmdPlugin.java)
*   The default PMD version used by gradle: [DEFAULT_PMD_VERSION](https://github.com/gradle/gradle/blob/v8.8.0/platforms/jvm/code-quality/src/main/groovy/org/gradle/api/plugins/quality/PmdPlugin.java#L66)
