---
title: Supported Java Versions
permalink: pmd_languages_java.html
---

## Overview of supported Java language versions

Usually the latest non-preview Java Version is the default version.

| Java Version | Alias | Supported by PMD since |
|--------------|-------|------------------------|
| 20-preview   |       | 6.55.0                 |
| 20 (default) |       | 6.55.0                 |
| 19-preview   |       | 6.48.0                 |
| 19           |       | 6.48.0                 |
| 18           |       | 6.44.0                 |
| 17           |       | 6.37.0                 |
| 16           |       | 6.32.0                 |
| 15           |       | 6.27.0                 |
| 14           |       | 6.22.0                 |
| 13           |       | 6.18.0                 |
| 12           |       | 6.13.0                 |
| 11           |       | 6.6.0                  |
| 10           | 1.10  | 6.4.0                  |
| 9            | 1.9   | 6.0.0                  |
| 8            | 1.8   | 5.1.0                  |
| 7            | 1.7   | 5.0.0                  |
| 6            | 1.6   | 3.9                    |
| 5            | 1.5   | 3.0                    |
| 1.4          |       | 1.2.2                  |
| 1.3          |       | 1.0.0                  |

## Using Java preview features

In order to analyze a project with PMD that uses preview language features, you'll need to enable
it via the environment variable `PMD_JAVA_OPTS` and select the new language version, e.g. `20-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd --use-version java-20-preview ...

Note: we only support preview language features for the latest two java versions.

Note: `--use-version` is only supported since PMD 6.52.0. Older versions of PMD use two CLI options that have to
be specified together: `-language java -version 20-preview`.


