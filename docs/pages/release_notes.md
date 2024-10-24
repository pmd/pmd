---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### CPD can now ignore literals and identifiers in C++ code

When searching for duplicated code in C++ differences in literals or identifiers can be
ignored now (like in Java). This can be enabled via the command line options `--ignore-literal`
and `--ignore-identifiers`.  
See [PR #5040](https://github.com/pmd/pmd/pull/5040) for details.

### 🌟 Rule Changes

#### Changed Rules
* {% rule java/bestpractices/SwitchStmtsShouldHaveDefault %} (Java Best Practices) doesn't report empty switch statements anymore.
  To detect these, use {% rule java/codestyle/EmptyControlStatement %}.
* {% rule java/bestpractices/UnitTestShouldUseAfterAnnotation %} (Java Best Practices) now also considers JUnit 5 and TestNG tests.
* {% rule java/bestpractices/UnitTestShouldUseBeforeAnnotation %} (Java Best Practices) now also considers JUnit 5 and TestNG tests.

#### Renamed Rules
* Several rules for unit testing have been renamed to better reflect their actual scope. Lots of them were called
  after JUnit / JUnit 4, even when they applied to JUnit 5 and / or TestNG.
  * {% rule java/bestpractices/UnitTestAssertionsShouldIncludeMessage %} (Java Best Practices) has been renamed from `JUnitAssertionsShouldIncludeMessage`.
  * {% rule java/bestpractices/UnitTestContainsTooManyAsserts %} (Java Best Practices) has been renamed from `JUnitTestContainsTooManyAsserts`.
  * {% rule java/bestpractices/UnitTestShouldIncludeAssert %} (Java Best Practices) has been renamed from `JUnitTestsShouldIncludeAssert`.
  * {% rule java/bestpractices/UnitTestShouldUseAfterAnnotation %} (Java Best Practices) has been renamed from `JUnit4TestShouldUseAfterAnnotation`.
  * {% rule java/bestpractices/UnitTestShouldUseBeforeAnnotation %} (Java Best Practices) has been renamed from `JUnit4TestShouldUseBeforeAnnotation`.
  * {% rule java/bestpractices/UnitTestShouldUseTestAnnotation %} (Java Best Practices) has been renamed from `JUnit4TestShouldUseTestAnnotation`.

The old rule names still work but are deprecated.

### 🐛 Fixed Issues
* java
  * [#4532](https://github.com/pmd/pmd/issues/4532): \[java] Rule misnomer for JUnit* rules
  * [#5261](https://github.com/pmd/pmd/issues/5261): \[java] Record patterns with empty deconstructor lists lead to NPE
* java-bestpractices
  * [#4813](https://github.com/pmd/pmd/issues/4813): \[java] SwitchStmtsShouldHaveDefault false positive with pattern matching
* java-codestyle
  * [#5253](https://github.com/pmd/pmd/issues/5253): \[java] BooleanGetMethodName: False-negatives with `Boolean` wrapper
* java-design
  * [#5030](https://github.com/pmd/pmd/issues/5030): \[java] SwitchDensity false positive with pattern matching
* java-errorprone
  * [#3362](https://github.com/pmd/pmd/issues/3362): \[java] ImplicitSwitchFallThrough should consider switch expressions
  * [#5067](https://github.com/pmd/pmd/issues/5067): \[java] CloseResource: False positive for FileSystems.getDefault()

### 🚨 API Changes
* java-bestpractices
  * The old rule name `JUnit4TestShouldUseAfterAnnotation` has been deprecated. Use the new name {% rule java/bestpractices/UnitTestShouldUseAfterAnnotation %} instead.
  * The old rule name `JUnit4TestShouldUseBeforeAnnotation` has been deprecated. Use the new name {% rule java/bestpractices/UnitTestShouldUseBeforeAnnotation %} instead.
  * The old rule name `JUnit4TestShouldUseTestAnnotation` has been deprecated. Use the new name {% rule java/bestpractices/UnitTestShouldUseTestAnnotation %} instead.
  * The old rule name `JUnitAssertionsShouldIncludeMessage` has been deprecated. Use the new name {% rule java/bestpractices/UnitTestAssertionsShouldIncludeMessage %} instead.
  * The old rule name `JUnitTestContainsTooManyAsserts` has been deprecated. Use the new name {% rule java/bestpractices/UnitTestContainsTooManyAsserts %} instead.
  * The old rule name `JUnitTestsShouldIncludeAssert` has been deprecated. Use the new name {% rule java/bestpractices/UnitTestShouldIncludeAssert %} instead.


### ✨ Merged pull requests
* [#4965](https://github.com/pmd/pmd/pull/4965): Fix #4532: \[java] Rename JUnit rules with overly restrictive names - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5040](https://github.com/pmd/pmd/pull/5040): \[cpp] Ignore literals and ignore identifiers capability to C++ CPD - [Jakub Dupak](https://github.com/jdupak) (@jdupak)
* [#5225](https://github.com/pmd/pmd/pull/5225): Fix #5067: \[java] CloseResource: False positive for FileSystems.getDefault() - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5241](https://github.com/pmd/pmd/pull/5241): Ignore javacc code in coverage report - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5245](https://github.com/pmd/pmd/pull/5245): \[java] Improve UnitTestShouldUse{After,Before}Annotation rules to support JUnit5 and TestNG - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5247](https://github.com/pmd/pmd/pull/5247): Fix #5030: \[java] SwitchDensity false positive with pattern matching - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5248](https://github.com/pmd/pmd/pull/5248): Fix #3362: \[java] ImplicitSwitchFallThrough should consider switch expressions - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5252](https://github.com/pmd/pmd/pull/5252): Fix #4813: \[java] SwitchStmtsShouldHaveDefault false positive with pattern matching - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5258](https://github.com/pmd/pmd/pull/5258): Ignore generated antlr classes in coverage reports - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5264](https://github.com/pmd/pmd/pull/5264): Fix #5261: \[java] Fix NPE with empty pattern list - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5269](https://github.com/pmd/pmd/pull/5269): Fix #5253: \[java] Support Boolean wrapper class for BooleanGetMethodName rule - [Aryant Tripathi](https://github.com/Aryant-Tripathi) (@Aryant-Tripathi)
* [#5275](https://github.com/pmd/pmd/pull/5275): Use plugin-classpath to simplify javacc-wrapper.xml - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5278](https://github.com/pmd/pmd/pull/5278): \[java] CouplingBetweenObjects: improve violation message - [Andreas Dangel](https://github.com/adangel) (@adangel)

{% endtocmaker %}

