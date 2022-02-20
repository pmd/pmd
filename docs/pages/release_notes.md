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


#### New programmatic API

This release introduces a new programmatic API to replace the inflexible {% jdoc core::PMD %} class.
Programmatic execution of PMD should now be done with a {% jdoc core::PMDConfiguration %}
and a {% jdoc core::PmdAnalysis %}, for instance:
```java
PMDConfiguration config = new PMDConfiguration();
config.setDefaultLanguageVersion(LanguageRegistry.findLanguageVersionByTerseName("java 11"));
config.setInputPaths("src/main/java");
config.prependClasspath("target/classes");
config.setMinimumPriority(RulePriority.HIGH);
config.setRuleSets("rulesets/java/quickstart.xml");
config.setReportFormat("xml");

try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
    pmd.performAnalysis();
}
```

The `PMD` class still supports methods related to CLI execution: `runPmd` and `main`.
All other members are now deprecated for removal.

### Fixed Issues

*   core
    *   [#3427](https://github.com/pmd/pmd/issues/3427): \[core] Stop printing CLI usage text when exiting due to invalid parameters
*   java
    *   [#3698](https://github.com/pmd/pmd/issues/3697): \[java] Parsing error with try-with-resources and qualified resource
*   java-codestyle
    *   [#278](https://github.com/pmd/pmd/issues/278): \[java] ConfusingTernary should treat `!= null` as positive condition
*   java-performance
    *   [#3374](https://github.com/pmd/pmd/issues/3374): \[java] UseStringBufferForStringAppends: Wrong example in documentation
*   misc
    *   [#3759](https://github.com/pmd/pmd/issues/3759): \[lang-test] Upgrade dokka maven plugin to 1.4.32
*   plsql
    *   [#3746](https://github.com/pmd/pmd/issues/3746): \[plsql] Parsing exception "Less than or equal to/Greater than or equal to" operators in DML statements

### API Changes

#### Deprecated API

Some API deprecations were performed in core PMD classes, to improve compatibility with PMD 7.

* {% jdoc core::Report %}: the constructor and other construction methods like addViolation or createReport
* {% jdoc core::RuleContext %}: all constructors, getters and setters. A new set
of stable methods, matching those in PMD 7, was added to replace the `addViolation`
overloads of {% jdoc core::lang.rule.AbstractRule %}. In PMD 7, `RuleContext` will
be the API to report violations, and it can already be used as such in PMD 6.
* Several members of {% jdoc core::PMD %}, including:
  - `PMD#EOL`
  - `PMD#SUPPRESS_MARKER`
  - `PMD#configuration`
  - `PMD#processFiles`
  - `PMD#getApplicableFiles`

#### Source-incompatible changes

* {% jdoc core::PMDConfiguration#prependClasspath(java.lang.String) %} does not throw
a checked `IOException` anymore, but an unchecked `IllegalArgumentException`. This change
is not source-compatible, but should make the API easier to use.

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

- {% jdoc core::RuleSet %}: methods that serve to apply rules, including `apply`, `start`, `end`, `removeDysfunctionalRules`
- {% jdoc !!core::renderers.AbstractAccumulatingRenderer#renderFileReport(Report) %} is internal API
  and should not be overridden in own renderers.

#### Changed API

It is now forbidden to report a violation:
- With a `null` node
- With a `null` message
- With a `null` set of format arguments (prefer a zero-length array)

Note that the message is set from the XML rule declaration, so this is only relevant
if you instantiate rules manually.

{% jdoc core::RuleContext %} now requires setting the current rule before calling
{% jdoc core::Rule#apply(java.util.List, core::RuleContext) %}. This is
done automatically by `RuleSet#apply` and such. Creating and configuring a
`RuleContext` manually is strongly advised against, as the lifecycle of `RuleContext`
will change drastically in PMD 7.

### External Contributions

*   [#3767](https://github.com/pmd/pmd/pull/3767): \[core] Update GUI.java - [Vyom Yadav](https://github.com/Vyom-Yadav)

{% endtocmaker %}

