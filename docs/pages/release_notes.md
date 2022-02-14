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

*   misc
    *   [#3759](https://github.com/pmd/pmd/issues/3759): \[lang-test] Upgrade dokka maven plugin to 1.4.32

### API Changes

#### Deprecated API

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

### External Contributions

*   [#3767](https://github.com/pmd/pmd/pull/3767): \[core] Update GUI.java

{% endtocmaker %}

