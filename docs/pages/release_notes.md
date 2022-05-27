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

### Fixed Issues

* cli
    * [#1445](https://github.com/pmd/pmd/issues/1445): \[core] Allow CLI to take globs as parameters
* core
    * [#2352](https://github.com/pmd/pmd/issues/2352): \[core] Deprecate \<lang\>-\<ruleset\> hyphen notation for ruleset references
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

#### Deprecated ruleset references

Ruleset references with the following formats are now deprecated and will produce a warning
when used on the CLI or in a ruleset XML file:
- `<lang-name>-<ruleset-name>`, eg `java-basic`, which resolves to `rulesets/java/basic.xml`
- the internal release number, eg `600`, which resolves to `rulesets/releases/600.xml`

Use the explicit forms of these references to be compatible with PMD 7.

#### Deprecated API

- {% jdoc core::RuleSetReferenceId#toString() %} is now deprecated. The format of this
 method will remain the same until PMD 7. The deprecation is intended to steer users
 away from relying on this format, as it may be changed in PMD 7.
- {% jdoc core::PMDConfiguration#getInputPaths() %} and
{% jdoc core::PMDConfiguration#setInputPaths(java.lang.String) %} are now deprecated.
A new set of methods have been added, which use lists and do not rely on comma splitting.

### External Contributions

* [#3961](https://github.com/pmd/pmd/pull/3961): \[java] Fix #3954 - NPE in UseCollectionIsEmptyRule with record - [@flyhard](https://github.com/flyhard)
* [#3964](https://github.com/pmd/pmd/pull/3964): \[java] Fix #3874 - ImmutableField: fix mockito/spring false positives - [@lukelukes](https://github.com/lukelukes)
* [#3974](https://github.com/pmd/pmd/pull/3974): \[cs] Add option to ignore C# attributes (annotations) - [@maikelsteneker](https://github.com/maikelsteneker)

{% endtocmaker %}

