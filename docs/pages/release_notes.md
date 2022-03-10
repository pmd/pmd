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

#### Java 18 Support

This release of PMD brings support for Java 18. There are no new standard language features.

PMD also supports [JEP 420: Pattern Matching for switch (Second Preview)](https://openjdk.java.net/jeps/420) as a
preview language feature. In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `18-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 18-preview ...

Note: Support for Java 16 preview language features have been removed. The version "16-preview" is no longer available.

#### New programmatic API

This release introduces a new programmatic API to replace the inflexible {% jdoc core::PMD %} class.
Programmatic execution of PMD should now be done with a {% jdoc core::PMDConfiguration %}
and a {% jdoc core::PmdAnalysis %}, for instance:

```java
PMDConfiguration config = new PMDConfiguration();
config.setDefaultLanguageVersion(LanguageRegistry.findLanguageByTerseName("java").getVersion("11"));
config.setInputPaths("src/main/java");
config.prependAuxClasspath("target/classes");
config.setMinimumPriority(RulePriority.HIGH);
config.addRuleSet("rulesets/java/quickstart.xml");
config.setReportFormat("xml");
config.setReportFile("target/pmd-report.xml");

try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
    // note: don't use `config` once a PmdAnalysis has been created.
    // optional: add more rulesets
    pmd.addRuleSet(pmd.newRuleSetLoader().loadFromResource("custom-ruleset.xml"));
    // optional: add more files
    pmd.files().addFile(Paths.get("src", "main", "more-java", "ExtraSource.java"));
    // optional: add more renderers
    pmd.addRenderer(renderer);

    // or just call PMD
    pmd.performAnalysis();
}
```

The `PMD` class still supports methods related to CLI execution: `runPmd` and `main`.
All other members are now deprecated for removal.
The CLI itself remains compatible, if you run PMD via command-line, no action is required on your part.

### Fixed Issues

*   apex-performance
    *   [#3773](https://github.com/pmd/pmd/pull/3773): \[apex] EagerlyLoadedDescribeSObjectResult false positives with SObjectField.getDescribe()
*   core
    *   [#3299](https://github.com/pmd/pmd/issues/3299): \[core] Deprecate system properties of PMDCommandLineInterface
*   java
    *   [#3809](https://github.com/pmd/pmd/issues/3809): \[java] Support JDK 18

### API Changes

#### Deprecated API

* Several members of {% jdoc core::PMD %} have been newly deprecated, including:
  - `PMD#EOL`: use `System#lineSeparator()`
  - `PMD#SUPPRESS_MARKER`: use {% jdoc core::PMDConfiguration#DEFAULT_SUPPRESS_MARKER %}
  - `PMD#processFiles`: use the [new programmatic API](#new-programmatic-api)
  - `PMD#getApplicableFiles`: is internal
* {% jdoc !!core::PMDConfiguration#prependClasspath(java.lang.String) %} is deprecated
  in favour of {% jdoc core::PMDConfiguration#prependAuxClasspath(java.lang.String) %}.
* {% jdoc !!core::PMDConfiguration#setRuleSets(java.lang.String) %} and
  {% jdoc core::PMDConfiguration#getRuleSets() %} are deprecated. Use instead
  {% jdoc core::PMDConfiguration#setRuleSets(java.util.List) %},
  {% jdoc core::PMDConfiguration#addRuleSet(java.lang.String) %},
  and {% jdoc core::PMDConfiguration#getRuleSetPaths() %}.
* Several members of {% jdoc test::cli.BaseCLITest %} have been deprecated with replacements.
* Several members of {% jdoc core::cli.PMDCommandLineInterface %} have been explicitly deprecated.
  The whole class however was deprecated long ago already with 6.30.0. It is internal API and should
  not be used.

#### Experimental APIs

*   Together with the [new programmatic API](#new-programmatic-api) the interface
    {% jdoc core::lang.document.TextFile %} has been added as *experimental*. It intends
    to replace {% jdoc core::util.datasource.DataSource %} and {% jdoc core::cpd.SourceCode %} in the long term.
    
    This interface will change in PMD 7 to support read/write operations
    and other things. You don't need to use it in PMD 6, as {% jdoc core::lang.document.FileCollector %}
    decouples you from this. A file collector is available through {% jdoc !!core::PmdAnalysis#files() %}.


### External Contributions

*   [#3773](https://github.com/pmd/pmd/pull/3773): \[apex] EagerlyLoadedDescribeSObjectResult false positives with SObjectField.getDescribe() - [@filiprafalowicz](https://github.com/filiprafalowicz)

{% endtocmaker %}

