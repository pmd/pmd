


## 25-July-2025 - 7.16.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.16.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [New: CPD support for CSS](#new-cpd-support-for-css)
    * [✨ New Rules](#new-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### New: CPD support for CSS
CPD now supports CSS (Cascading Style Sheets), a language for describing the rendering of structured
documents (such as HTML) on screen, on paper etc.  
It is shipped with the new module `pmd-css`.

#### ✨ New Rules

* Two new rules have been added to Java's Error Prone category: [`ReplaceJavaUtilCalendar`](https://docs.pmd-code.org/pmd-doc-7.16.0-SNAPSHOT/pmd_rules_java_errorprone.html#replacejavautilcalendar)
  and [`ReplaceJavaUtilDate`](https://docs.pmd-code.org/pmd-doc-7.16.0-SNAPSHOT/pmd_rules_java_errorprone.html#replacejavautildate). These rules help to migrate away from old Java APIs around
  `java.util.Calendar` and `java.util.Date`. It is recommended to use the modern `java.time` API instead, which
  is available since Java 8.

### 🐛 Fixed Issues
* java-codestyle
  * [#5892](https://github.com/pmd/pmd/issues/5892): \[java] ShortVariable false positive for java 22 unnamed variable `_`
* java-design
  * [#5858](https://github.com/pmd/pmd/issues/5858): \[java] FinalFieldCouldBeStatic false positive for array initializers
* java-errorprone
  * [#2862](https://github.com/pmd/pmd/issues/2862): \[java] New Rules: Avoid java.util.Date and Calendar classes

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5733](https://github.com/pmd/pmd/pull/5733): \[css] Add new CPD language - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#5859](https://github.com/pmd/pmd/pull/5859): Fix #5858: \[java] Fix false positive in FinalFieldCouldBeStatic for array initializers - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5876](https://github.com/pmd/pmd/pull/5876): chore: license header cleanup - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5883](https://github.com/pmd/pmd/pull/5883): Fix #2862: \[java] Add rules discouraging the use of java.util.Calendar and java.util.Date - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5914](https://github.com/pmd/pmd/pull/5914): Fix #5892: \[java] ShortVariable FP for java 22 Unnamed Variable - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->



