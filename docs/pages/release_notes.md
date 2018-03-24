---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.2.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.2.0.

This is a bug fixing release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
*   [Fixed Issues](#fixed-issues)
    *   [Ecmascript (JavaScript)](#ecmascript-javascript)
    *   [Disable Incremental Analysis](#disable-incremental-analysis)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
*   [API Changes](#api-changes)
*   [External Contributions](#external-contributions)

### New and noteworthy

#### Ecmascript (JavaScript)

The [Rhino Library](https://github.com/mozilla/rhino) has been upgraded from version 1.7.7 to version 1.7.7.2.

Detailed changes for changed in Rhino can be found:
* [For 1.7.7.2](https://github.com/mozilla/rhino/blob/master/RELEASE-NOTES.md#rhino-1772)
* [For 1.7.7.1](https://github.com/mozilla/rhino/blob/master/RELEASE-NOTES.md#rhino-1771)

Both are bugfixing releases.

### Disable Incremental Analysis

Some time ago, we added support for [Incremental Analysis](pmd_userdocs_getting_started.html). On PMD 6.0.0, we
started to add warns when not using it, as we strongly believe it's a great improvement to our user's experience as
analysis time is greatly reduced; and in the future we plan to have it enabled by default. However, we realize some
scenarios don't benefit from it (ie: CI jobs), and having the warning logged can be noisy and cause confusion.

To this end, we have added a new flag to allow you to explicitly disable incremental analysis. On CLI, this is
the new `-no-cache` flag. On Ant, there is a `noCache` attribute for the `<pmd>` task.

On both scenarios, disabling the cache takes precedence over setting a cache location.

#### New Rules

*   The new Java rule [`MissingOverride`](pmd_rules_java_bestpractices.html#missingoverride)
    (category `bestpractices`) detects overridden and implemented methods, which are not marked with the
    `@Override` annotation. Annotating overridden methods with `@Override` ensures at compile time that
    the method really overrides one, which helps refactoring and clarifies intent.

*   The new Java rule [`UnnecessaryAnnotationValueElement`](pmd_rules_java_codestyle.html#unnecessaryannotationvalueelement)
    (category `codestyle`) detects annotations with a single element (`value`) that explicitely names it.
    That is, doing `@SuppressWarnings(value = "unchecked")` would be flagged in favor of
    `@SuppressWarnings("unchecked")`.

*   The new Java rule [`ControlStatementBraces`](pmd_rules_java_codestyle.html#controlstatementbraces)
    (category `codestyle`) enforces the presence of braces on control statements where they are optional.
    Properties allow to customize which statements are required to have braces. This rule replaces the now
    deprecated rules `WhileLoopMustUseBraces`, `ForLoopMustUseBraces`, `IfStmtMustUseBraces`, and
    `IfElseStmtMustUseBraces`. More than covering the use cases of those rules, this rule also supports
    `do ... while` statements and `case` labels of `switch` statements (disabled by default).

#### Modified Rules

*   The Java rule `CommentContentRule` (`java-documentation`) previously had the property `wordsAreRegex`. But this 
    property never had been implemented and is removed now.

*   The Java rule `UnusedPrivateField` (`java-bestpractices`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the field should be ignored. By default `@java.lang.Deprecated`
    and `@javafx.fxml.FXML` are ignored.

*   The Java rule `UnusedPrivateMethod` (`java-bestpractices`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the method should be ignored. By default `@java.lang.Deprecated`
    is ignored.

#### Deprecated Rules

*   The Java rules `WhileLoopMustUseBraces`, `ForLoopMustUseBraces`, `IfStmtMustUseBraces`, and `IfElseStmtMustUseBraces`
    are deprecated. They will be replaced by the new rule `ControlStatementBraces`, in the category `codestyle`.

### Fixed Issues

*   all
    *   [#928](https://github.com/pmd/pmd/issues/928): \[core] PMD build failure on Windows
*   java-bestpracrtices
    *   [#907](https://github.com/pmd/pmd/issues/907): \[java] UnusedPrivateField false-positive with @FXML
    *   [#963](https://github.com/pmd/pmd/issues/965): \[java] ArrayIsStoredDirectly not triggered from variadic functions
*   java-codestyle
    *   [#974](https://github.com/pmd/pmd/issues/974): \[java] Merge *StmtMustUseBraces rules
    *   [#983](https://github.com/pmd/pmd/issues/983): \[java] Detect annotations with single value element
*   java-design
    *   [#832](https://github.com/pmd/pmd/issues/832): \[java] AvoidThrowingNullPointerException documentation suggestion
    *   [#837](https://github.com/pmd/pmd/issues/837): \[java] CFGs of declared but not called lambdas are treated as parts of an enclosing method's CFG
    *   [#839](https://github.com/pmd/pmd/issues/839): \[java] SignatureDeclareThrowsException's IgnoreJUnitCompletely property not honored for constructors
    *   [#968](https://github.com/pmd/pmd/issues/968): \[java] UseUtilityClassRule reports false positive with lombok NoArgsConstructor
*   documentation
    *   [#978](https://github.com/pmd/pmd/issues/978): \[core] Broken link in CONTRIBUTING.md
    *   [#992](https://github.com/pmd/pmd/issues/992): \[core] Include info about rule doc generation in "Writing Documentation" md page

### API Changes

*    A new CLI switch, `-no-cache`, disables incremental analysis and the related suggestion. This overrides the
    `-cache` option. The corresponding Ant task parameter is `noCache`.

*   The static method `PMDParameters.transformParametersIntoConfiguration(PMDParameters)` is now deprecated,
    for removal in 7.0.0. The new instance method `PMDParameters.toConfiguration()` replaces it.

*   The method `ASTConstructorDeclaration.getParameters()` has been deprecated in favor of the new method
    `getFormalParameters()`. This method is available for both `ASTConstructorDeclaration` and
    `ASTMethodDeclaration`.

### External Contributions

* [#941](https://github.com/pmd/pmd/pull/941): \[java] Use char notation to represent a character to improve performance - [reudismam](https://github.com/reudismam)
* [#943](https://github.com/pmd/pmd/pull/943): \[java] UnusedPrivateField false-positive with @FXML - [BBG](https://github.com/djydewang)
* [#951](https://github.com/pmd/pmd/pull/951): \[java] Add ignoredAnnotations property to unusedPrivateMethod rule - [BBG](https://github.com/djydewang)
* [#952](https://github.com/pmd/pmd/pull/952): \[java] SignatureDeclareThrowsException's IgnoreJUnitCompletely property not honored for constructors - [BBG](https://github.com/djydewang)
* [#965](https://github.com/pmd/pmd/pull/965): \[java] Make Varargs trigger ArrayIsStoredDirectly - [Stephen](https://github.com/pmd/pmd/issues/907)
* [#967](https://github.com/pmd/pmd/pull/967): \[doc] Issue 959: fixed broken link to XPath Rule Tutorial - [Andrey Mochalov](https://github.com/epidemia)
* [#969](https://github.com/pmd/pmd/pull/969): \[java] Issue 968 Add logic to handle lombok private constructors with utility classes - [Kirk Clemens](https://github.com/clem0110)
* [#970](https://github.com/pmd/pmd/pull/970): \[java] Fixed inefficient use of keySet iterator instead of entrySet iterator - [Andrey Mochalov](https://github.com/epidemia)
* [#984](https://github.com/pmd/pmd/pull/984): \[java] issue983 Add new UnnecessaryAnnotationValueElement rule - [Kirk Clemens](https://github.com/clem0110)
* [#989](https://github.com/pmd/pmd/pull/989): \[core] Update Contribute.md to close Issue #978 - [Bolarinwa Saheed Olayemi](https://github.com/refactormyself)
* [#990](https://github.com/pmd/pmd/pull/990): \[java] Updated Doc on AvoidThrowingNullPointerException to close Issue #832 - [Bolarinwa Saheed Olayemi](https://github.com/refactormyself)
* [#993](https://github.com/pmd/pmd/pull/993): \[core] Update writing_documentation.md to fix Issue #992 - [Bolarinwa Saheed Olayemi](https://github.com/refactormyself)

