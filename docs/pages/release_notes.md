---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Support for HTML

This version of PMD ships a new language module to support analyzing of HTML.
Support for HTML is experimental and might change without notice.
The language implementation is not complete yet and the AST doesn't look
well for text nodes and comment nodes and might be changed in the future.
You can write your own rules, but we don't guarantee that the rules work with
the next (minor) version of PMD without adjustments.

Please give us feedback about how practical this new language is in
[discussions](https://github.com/pmd/pmd/discussions). Please report
missing features or bugs as new [issues](https://github.com/pmd/pmd/issues).

#### New rules

* The HTML rule {% rule html/bestpractices/AvoidInlineStyles %} finds elements which use a style attribute.
  In order to help maintaining a webpage it is considered good practice to separate content and styles. Instead
  of inline styles one should use CSS files and classes.

```xml
    <rule ref="category/html/bestpractices.xml/AvoidInlineStyles" />
```

* The HTML rule {% rule html/bestpractices/UnnecessaryTypeAttribute %} finds "link" and "script" elements which
  still have a "type" attribute. This is not necessary anymore since modern browsers automatically use CSS and
  JavaScript.

```xml
      <rule ref="category/html/bestpractices.xml/UnnecessaryTypeAttribute" />
```

* The HTML rule {% rule html/bestpractices/UseAltAttributeForImages %} finds "img" elements without an "alt"
  attribute. An alternate text should always be provided in order to help screen readers.

```xml
      <rule ref="category/html/bestpractices.xml/UseAltAttributeForImages" />
```

#### Modified rules

*   The Java rule {% rule java/bestpractices/UnusedPrivateField %} has a new property `ignoredFieldNames`.
    The default ignores serialization-specific fields (eg `serialVersionUID`).
    The property can be used to ignore more fields based on their name.
    Note that the rule used to ignore fields named `IDENT`, but doesn't anymore (add this value to the property to restore the old behaviour).

### Fixed Issues
* core
  * [#3792](https://github.com/pmd/pmd/issues/3792): \[core] Allow to filter violations in Report
  * [#3881](https://github.com/pmd/pmd/issues/3881): \[core] SARIF renderer depends on platform default encoding
  * [#3882](https://github.com/pmd/pmd/pull/3882): \[core] Fix AssertionError about exhaustive switch
  * [#3884](https://github.com/pmd/pmd/issues/3884): \[core] XML report via ant task contains XML header twice
  * [#3896](https://github.com/pmd/pmd/pull/3896): \[core] Fix ast-dump CLI when reading from stdin
* doc
  * [#2505](https://github.com/pmd/pmd/issues/2505): \[doc] Improve side bar to show release date
* java
  * [#3068](https://github.com/pmd/pmd/issues/3068): \[java] Some tests should not depend on real rules
  * [#3889](https://github.com/pmd/pmd/pull/3889): \[java] Catch LinkageError in UselessOverridingMethodRule
* java-bestpractices
  * [#3910](https://github.com/pmd/pmd/pull/3910): \[java] UnusedPrivateField - Allow the ignored fieldnames to be configurable
  * [#1185](https://github.com/pmd/pmd/issues/1185): \[java] ArrayIsStoredDirectly false positive with field access
  * [#1474](https://github.com/pmd/pmd/issues/1474): \[java] ArrayIsStoredDirectly false positive with method call
  * [#3879](https://github.com/pmd/pmd/issues/3879) \[java] ArrayIsStoredDirectly reports duplicated violation
  * [#3929](https://github.com/pmd/pmd/issues/3929): \[java] ArrayIsStoredDirectly should report the assignment rather than formal parameter
* java-design
  * [#3603](https://github.com/pmd/pmd/issues/3603): \[java] SimplifiedTernary: no violation for 'condition ? true : false' case
* java-performance
  * [#3867](https://github.com/pmd/pmd/issues/3867): \[java] UseArraysAsList with method call
* plsql
  * [#3687](https://github.com/pmd/pmd/issues/3687): \[plsql] Parsing exception EXECUTE IMMEDIATE l_sql BULK COLLECT INTO statement
  * [#3706](https://github.com/pmd/pmd/issues/3706): \[plsql] Parsing exception CURSOR statement with parenthesis groupings

### API Changes

#### Experimental APIs

* Report has two new methods which allow limited mutations of a given report:
  * {% jdoc !!core::Report#filterViolations(net.sourceforge.pmd.util.Predicate) %} creates a new report with
    some violations removed with a given predicate based filter.
  * {% jdoc !!core::Report#union(net.sourceforge.pmd.Report) %} can combine two reports into a single new Report.
* {% jdoc !!core::util.Predicate %} will be replaced in PMD7 with the standard Predicate interface from java8.
* The module `pmd-html` is entirely experimental right now. Anything in the package
  `net.sourceforge.pmd.lang.html` should be used cautiously.

### External Contributions
* [#3883](https://github.com/pmd/pmd/pull/3883): \[doc] Improve side bar by Adding Release Date - [@jasonqiu98](https://github.com/jasonqiu98)
* [#3910](https://github.com/pmd/pmd/pull/3910): \[java] UnusedPrivateField - Allow the ignored fieldnames to be configurable - [@laoseth](https://github.com/laoseth)
* [#3928](https://github.com/pmd/pmd/pull/3928): \[plsql] Fix plsql parsing error in parenthesis groups - [@LiGaOg](https://github.com/LiGaOg)
* [#3935](https://github.com/pmd/pmd/pull/3935): \[plsql] Fix parser exception in EXECUTE IMMEDIATE BULK COLLECT #3687 - [@Scrsloota](https://github.com/Scrsloota)
* [#3938](https://github.com/pmd/pmd/pull/3938): \[java] Modify SimplifiedTernary to meet the missing case #3603 - [@VoidxHoshi](https://github.com/VoidxHoshi)
* [#3943](https://github.com/pmd/pmd/pull/3943): chore: Set permissions for GitHub actions - [@naveensrinivasan](https://github.com/naveensrinivasan)

{% endtocmaker %}

