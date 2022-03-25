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

#### Better XML XPath support

The new rule class {% jdoc xml::lang.xml.rule.DomXPathRule %} is intended to replace
usage of the `XPathRule` for XML rules. This rule executes the XPath query in a different
way, which sticks to the XPath specification. This means the expression is interpreted
the same way in PMD as in all other XPath development tools that stick to the standard.
You can for instance test the expression in an online XPath editor.

Prefer using this class to define XPath rules: replace the value of the `class`
attribute with `net.sourceforge.pmd.lang.xml.rule.DomXPathRule` like so:
```xml
<rule name="MyXPathRule"
      language="xml"
      message="A message"
      class="net.sourceforge.pmd.lang.xml.rule.DomXPathRule">

      <properties>
        <property name="xpath">
            <value><![CDATA[
            /a/b/c[@attr = "5"]
            ]]></value>
        </property>
        <!-- Note: the property "version" is ignored, remove it. The query is XPath 2. -->
      </properties>
</rule>
```

The rule is more powerful than `XPathRule`, as it can now handle XML namespaces,
comments and processing instructions. Please refer to the Javadoc of {% jdoc xml::lang.xml.rule.DomXPathRule %}
for information about the differences with `XPathRule` and examples.

`XPathRule` is still perfectly supported for all other languages, including Apex and Java.

#### New XPath functions

The new XPath functions `pmd:startLine`, `pmd:endLine`, `pmd:startColumn`,
and `pmd:endColumn` are now available in XPath rules for all languages. They
replace the node attributes `@BeginLine`, `@EndLine` and such. These attributes
will be deprecated in a future release.

Please refer to [the documentation](https://pmd.github.io/latest/pmd_userdocs_extending_writing_xpath_rules.html#pmd-extension-functions) of these functions for more information, including usage samples.

Note that the function `pmd:endColumn` returns an exclusive index, while the
attribute `@EndColumn` is inclusive. This is for forward compatibility with PMD 7,
which uses exclusive end indices.

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

*   apex
    *   [#3817](https://github.com/pmd/pmd/pull/3817): \[apex] Add designer bindings to display main attributes
*   apex-performance
    *   [#3773](https://github.com/pmd/pmd/pull/3773): \[apex] EagerlyLoadedDescribeSObjectResult false positives with SObjectField.getDescribe()
*   core
    *   [#3299](https://github.com/pmd/pmd/issues/3299): \[core] Deprecate system properties of PMDCommandLineInterface
*   doc
    *   [#2504](https://github.com/pmd/pmd/issues/2504): \[doc] Improve "Edit me on github" button
    *   [#3812](https://github.com/pmd/pmd/issues/3812): \[doc] Documentation website table of contents broken on pages with many subheadings
*   java-design
    *   [#3851](https://github.com/pmd/pmd/issues/3851): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - false negative when a compilation unit contains two class declarations
*   xml
    *   [#2766](https://github.com/pmd/pmd/issues/2766): \[xml] XMLNS prefix is not pre-declared in xpath query
    *   [#3863](https://github.com/pmd/pmd/issues/3863): \[xml] Make XPath rules work exactly as in the XPath spec

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

* In modelica, the rule classes {% jdoc modelica::lang.modelica.rule.AmbiguousResolutionRule %}
  and {% jdoc modelica::lang.modelica.rule.ConnectUsingNonConnector %} have been deprecated,
  since they didn't comply to the usual rule class naming conventions yet.
  The replacements are in the subpackage `bestpractices`.

#### Experimental APIs

*   Together with the [new programmatic API](#new-programmatic-api) the interface
    {% jdoc core::lang.document.TextFile %} has been added as *experimental*. It intends
    to replace {% jdoc core::util.datasource.DataSource %} and {% jdoc core::cpd.SourceCode %} in the long term.
    
    This interface will change in PMD 7 to support read/write operations
    and other things. You don't need to use it in PMD 6, as {% jdoc core::lang.document.FileCollector %}
    decouples you from this. A file collector is available through {% jdoc !!core::PmdAnalysis#files() %}.


### External Contributions

*   [#3773](https://github.com/pmd/pmd/pull/3773): \[apex] EagerlyLoadedDescribeSObjectResult false positives with SObjectField.getDescribe() - [@filiprafalowicz](https://github.com/filiprafalowicz)
*   [#3811](https://github.com/pmd/pmd/pull/3811): \[doc] Improve "Edit me on github" button - [@btjiong](https://github.com/btjiong)
*   [#3836](https://github.com/pmd/pmd/pull/3836): \[doc] Make TOC scrollable when too many subheadings - [@JerritEic](https://github.com/JerritEic)

{% endtocmaker %}

