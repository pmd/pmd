---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### CLI improvements

The PMD CLI now allows repeating the `--dir` (`-d`) and `--rulesets` (`-R`) options,
 as well as providing several space-separated arguments to either of them. For instance:
```shell
pmd -d src/main/java src/test/java -R rset1.xml -R rset2.xml
```
This also allows globs to be used on the CLI if your shell supports shell expansion.
For instance, the above can be written
```shell
pmd -d src/*/java -R rset*.xml
```
Please use theses new forms instead of using comma-separated lists as argument to these options.

#### C# Improvements

When executing CPD on C# sources, the option `--ignore-annotations` is now supported as well.
It ignores C# attributes when detecting duplicated code. This option can also be enabled via
the CPD GUI. See [#3974](https://github.com/pmd/pmd/pull/3974) for details.

#### New Rules

This release ships with 2 new Java rules.

* {% rule java/codestyle/EmptyControlStatement %} reports many instances of empty things, e.g. control statements whose
  body is empty, as well as empty initializers.

  EmptyControlStatement also works for empty `for` and `do` loops, while there were previously
  no corresponding rules.

  This new rule replaces the rules EmptyFinallyBlock, EmptyIfStmt, EmptyInitializer, EmptyStatementBlock,
  EmptySwitchStatements, EmptySynchronizedBlock, EmptyTryBlock, and EmptyWhileStmt.

```xml
<rule ref="category/java/codestyle.xml/EmptyControlStatement"/>
```

The rule is part of the quickstart.xml ruleset.

* {%rule java/codestyle/UnnecessarySemicolon %} reports semicolons that are unnecessary  (so called "empty statements"
  and "empty declarations").

  This new rule replaces the rule EmptyStatementNotInLoop.

```xml
<rule ref="category/java/codestyle.xml/UnnecessarySemicolon"/>
```

The rule is part of the quickstart.xml ruleset.

#### Deprecated Rules

* The following Java rules are deprecated and removed from the quickstart ruleset, as the new rule
{% rule java/codestyle/EmptyControlStatement %} merges their functionality:
    * {% rule java/errorprone/EmptyFinallyBlock %}
    * {% rule java/errorprone/EmptyIfStmt %}
    * {% rule java/errorprone/EmptyInitializer %}
    * {% rule java/errorprone/EmptyStatementBlock %}
    * {% rule java/errorprone/EmptySwitchStatements %}
    * {% rule java/errorprone/EmptySynchronizedBlock %}
    * {% rule java/errorprone/EmptyTryBlock %}
    * {% rule java/errorprone/EmptyWhileStmt %}
* The Java rule {% rule java/errorprone/EmptyStatementNotInLoop %} is deprecated and removed from the quickstart
ruleset. Use the new rule {% rule java/codestyle/UnnecessarySemicolon %} instead.

### Fixed Issues

* cli
    * [#1445](https://github.com/pmd/pmd/issues/1445): \[core] Allow CLI to take globs as parameters
* core
    * [#3942](https://github.com/pmd/pmd/issues/3942): \[core] common-io path traversal vulnerability (CVE-2021-29425)
* cs (c#)
    * [#3974](https://github.com/pmd/pmd/pull/3974): \[cs] Add option to ignore C# attributes (annotations)
* go
    * [#2752](https://github.com/pmd/pmd/issues/2752): \[go] Error parsing unicode values
* html
    * [#3955](https://github.com/pmd/pmd/pull/3955): \[html] Improvements for handling text and comment nodes
* java
    * [#3423](https://github.com/pmd/pmd/issues/3423): \[java] Error processing identifiers with Unicode 
* java-bestpractices
    * [#3954](https://github.com/pmd/pmd/issues/3954): \[java] NPE in UseCollectionIsEmptyRule when .size() is called in a record
* java-design
    * [#3874](https://github.com/pmd/pmd/issues/3874): \[java] ImmutableField reports fields annotated with @Autowired (Spring) and @Mock (Mockito)
* java-performance
    * [#3379](https://github.com/pmd/pmd/issues/3379): \[java] UseArraysAsList must ignore primitive arrays
    * [#3965](https://github.com/pmd/pmd/issues/3965): \[java] UseArraysAsList false positive with non-trivial loops
* javascript
    * [#2605](https://github.com/pmd/pmd/issues/2605): \[js] Support unicode characters
    * [#3948](https://github.com/pmd/pmd/issues/3948): \[js] Invalid operator error for method property in object literal
* python
    * [#2604](https://github.com/pmd/pmd/issues/2604): \[python] Support unicode identifiers

### API Changes

#### Deprecated API

- {% jdoc core::PMDConfiguration#getInputPaths() %} and
{% jdoc core::PMDConfiguration#setInputPaths(java.lang.String) %} are now deprecated.
A new set of methods have been added, which use lists and do not rely on comma splitting.

### External Contributions

* [#3961](https://github.com/pmd/pmd/pull/3961): \[java] Fix #3954 - NPE in UseCollectionIsEmptyRule with record - [@flyhard](https://github.com/flyhard)
* [#3964](https://github.com/pmd/pmd/pull/3964): \[java] Fix #3874 - ImmutableField: fix mockito/spring false positives - [@lukelukes](https://github.com/lukelukes)
* [#3974](https://github.com/pmd/pmd/pull/3974): \[cs] Add option to ignore C# attributes (annotations) - [@maikelsteneker](https://github.com/maikelsteneker)

{% endtocmaker %}

