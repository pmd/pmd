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
    // optional: here you can add more rulesets
    pmd.addRuleset(RuleSetLoader.fromPmdConfig(config).loadFromResource("custom-ruleset.xml");
    // optional: or you can add more files
    pmd.files().addFile(FileSystems.getDefault().getPath("src", "main", "more-java", "ExtraSource.java");

    // or just call PMD
    pmd.performAnalysis();
}
```

The `PMD` class still supports methods related to CLI execution: `runPmd` and `main`.
All other members are now deprecated for removal.
The CLI itself remains compatible, if you run PMD via command-line, no action is required on your part.

### Fixed Issues

### API Changes

#### Deprecated API

* Several members of {% jdoc core::PMD %} have been newly deprecated, including:
  - `PMD#EOL`: use `System#lineSeparator()`
  - `PMD#SUPPRESS_MARKER`: use {% jdoc core::PMDConfiguration#DEFAULT_SUPPRESS_MARKER %}
  - `PMD#processFiles`: use the [new programmatic API](#new-programmatic-api)
  - `PMD#getApplicableFiles`: is internal
* {% jdoc !!core::PMDConfiguration#prependClasspath(java.lang.String) %} is deprecated
  in favour of {% jdoc core::PMDConfiguration#prependAuxClasspath(java.lang.String) %}.

#### Experimental APIs

*   Together with the [new programmatic API](#new-programmatic-api) the interface
    {% jdoc lang.document.TextFile %} has been added as *experimental*. It intends
    to replace {% jdoc util.DataSource %} and {% jdoc cpd.SourceCode %} in the long term.
    
    This interface will change in PMD 7 to support read/write operations
    and other things. You don't need to use it in PMD 6, as {% jdoc lang.document.FileCollector %}
    decouples you from this. A file collector is available through {% jdoc !!PmdAnalysis#files() %}.


### External Contributions

{% endtocmaker %}

