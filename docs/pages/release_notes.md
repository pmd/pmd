---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date }} - {{ site.pmd.version }}

We're excited to bring you the next major version of PMD!

Since this is a big release, we provide here only a concise version of the release notes. We prepared a separate
page with the full [Detailed Release Notes for PMD 7.0.0]({{ baseurl }}pmd_release_notes_pmd7.html).

<div style="border: 1px solid; border-radius: .25rem; padding: .75rem 1.25rem;" role="alert">
<strong>ℹ️ Release Candidates</strong>
<p>PMD 7.0.0 is finally almost ready. In order to gather feedback, we are going to ship a couple of release candidates.
These are officially available on GitHub and Maven Central and can be used as usual (e.g. as a dependency).
We encourage you to try out the new features, but keep in mind that we may introduce API breaking changes between
the release candidates. It should be stable enough if you don't use custom rules.</p>

<p>We have still some tasks planned for the next release candidates.
You can see the progress in <a href="https://github.com/pmd/pmd/issues/3898">PMD 7 Tracking Issue #3898</a>.</p>

<p>If you find any problem or difficulty while updating from PMD 6, please provide feedback via our
<a href="https://github.com/pmd/pmd/issues/new/choose">issue tracker</a>. That way we can improve the experience
for all.</p>
</div>

{% tocmaker is_release_notes_processor %}

### Changes since 7.0.0-rc4

This section lists the most important changes from the last release candidate.
The remaining section describes the complete release notes for 7.0.0.

#### New and Noteworthy

##### Maven PMD Plugin compatibility with PMD 7

In order to use PMD 7 with [maven-pmd-plugin](https://maven.apache.org/plugins/maven-pmd-plugin/) a new
compatibility module has been created. This allows to use PMD 7 by simply adding one additional dependency:

1. Follow the guide [Upgrading PMD at Runtime](https://maven.apache.org/plugins/maven-pmd-plugin/examples/upgrading-PMD-at-runtime.html)
2. Add additionally the following dependency:

```xml
<dependency>
  <groupId>net.sourceforge.pmd</groupId>
  <artifactId>pmd-compat6</artifactId>
  <version>${pmdVersion}</version>
</dependency>
```

It is important to add this dependency as the **first** in the list, so that maven-pmd-plugin sees the (old)
compatible versions of some classes.

This module is available beginning with version 7.0.0-rc4 and will be there at least for the first
final version PMD 7 (7.0.0). It's not decided yet, whether we will keep updating it, after PMD 7 is finally
released.

Note: This compatibility module only works for the built-in rules, that are still available in PMD 7. E.g. you need
to review your rulesets and look out for deprecated rules and such. See the use case
[I'm using only built-in rules]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html#im-using-only-built-in-rules)
in the [Migration Guide for PMD 7]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html).

As PMD 7 revamped the Java module, if you have custom rules, you need to migrate these rules.
See the use case [I'm using custom rules]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html#im-using-custom-rules)
in the Migration Guide.

##### Swift Support

* limited support for Swift 5.9 (Macro Expansions)

##### Groovy Support (CPD)

* We now support parsing all Groovy features from Groovy 3 and 4.
* We now support [suppression](pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs.
* See [PR #4726](https://github.com/pmd/pmd/pull/4726) for details.

##### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see
* [PMD Designer Changelog (7.0.0)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0).

##### Apex Support: Replaced Jorje with fully open source front-end

When PMD added Apex support with version 5.5.0, it utilized the Apex Jorje library to parse Apex source
and generate an AST. This library is however a binary-blob provided as part of the
[Salesforce Extensions for VS Code](https://github.com/forcedotcom/salesforcedx-vscode), and it is closed-source.

This causes problems, if binary blobs are not allowed by e.g. a company-wide policy. In that case, the Jorje
library prevented that PMD Apex could be used at all.

Also having access to the source code, enhancements and modifications are easier to do.

Under the hood, we use two open source libraries instead:

* [apex-parser](https://github.com/apex-dev-tools/apex-parser) originally by
  [Kevin Jones](https://github.com/nawforce) (@nawforce).
  This project provides the grammar for a ANTLR based parser.
* [Summit-AST](https://github.com/google/summit-ast) by [Google](https://github.com/google) (@google)
  This project translates the ANTLR parse tree into an AST, that is similar to the AST Jorje provided.
  Note: This is not an official Google product.

Although the parsers is completely switched, there are only little known changes to the AST.
These are documented in the [Migration Guide for PMD 7: Apex AST]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html#apex-ast).

See [#3766](https://github.com/pmd/pmd/issues/3766) for details.

Contributors: [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google),
  [Edward Klimoshenko](https://github.com/eklimo) (@eklimo)

##### Changed: HTML support

Support for HTML was introduced in PMD 6.55.0 as an experimental feature. With PMD 7.0.0 this
is now considered stable.

##### Changed: Kotlin support

Experimental Kotlin support has been promoted as stable API now.

#### Rule Changes

**New Rules**

* {% rule apex/performance/OperationWithHighCostInLoop %} finds Schema class methods called in a loop, which is a
  potential performance issue.
* {% rule java/codestyle/UseExplicitTypes %} reports usages of `var` keyword, which was introduced with Java 10.
* {% rule xml/bestpractices/MissingEncoding %} finds XML files without explicit encoding.

**Changed Rules**

* {% rule java/codestyle/EmptyControlStatement %}: The rule has a new property to allow empty blocks when
  they contain a comment (`allowCommentedBlocks`).
* {% rule apex/codestyle/MethodNamingConventions %}: The deprecated rule property `skipTestMethodUnderscores` has
  been removed. It was actually deprecated since PMD 6.15.0, but was not mentioned in the release notes
  back then. Use the property `testPattern` instead to configure valid names for test methods.
* {% rule java/documentation/CommentRequired %}: The deprecated property `headerCommentRequirement` has been removed.
  Use the property `classCommentRequirement` instead.
* {% rule java/errorprone/NonSerializableClass %}: The deprecated property `prefix` has been removed
  without replacement. In a serializable class all fields have to be serializable regardless of the name.

**Removed Rules**

The following previously deprecated rules have been finally removed:

* Apex
  * {% deleted_rule apex/performance/AvoidSoqlInLoops %} ➡️ use {% rule apex/performance/OperationWithLimitsInLoop %}
  * {% deleted_rule apex/performance/AvoidSoslInLoops %} ➡️ use {% rule apex/performance/OperationWithLimitsInLoop %}
  * {% deleted_rule apex/performance/AvoidDmlStatementsInLoops %} ➡️ use {% rule apex/performance/OperationWithLimitsInLoop %}
* Java
  * {% deleted_rule java/design/ExcessiveClassLength %} ➡️ use {% rule java/design/NcssCount %}
  * {% deleted_rule java/design/ExcessiveMethodLength %} ➡️ use {% rule java/design/NcssCount %}
  * {% deleted_rule java/errorprone/BeanMembersShouldSerialize %} ➡️ use {% rule java/errorprone/NonSerializableClass %}
  * {% deleted_rule java/errorprone/EmptyFinallyBlock %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
  * {% deleted_rule java/errorprone/EmptyIfStmt %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
  * {% deleted_rule java/errorprone/EmptyInitializer %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
  * {% deleted_rule java/errorprone/EmptyStatementBlock %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
  * {% deleted_rule java/errorprone/EmptyStatementNotInLoop %} ➡️ use {% rule java/codestyle/UnnecessarySemicolon %}
  * {% deleted_rule java/errorprone/EmptySwitchStatements %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
  * {% deleted_rule java/errorprone/EmptySynchronizedBlock %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
  * {% deleted_rule java/errorprone/EmptyTryBlock %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
  * {% deleted_rule java/errorprone/EmptyWhileStmt %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}

**Removed deprecated rulesets**

The following previously deprecated rulesets have been removed. These were the left-over rulesets from PMD 5.
The rules have been moved into categories with PMD 6.

* rulesets/apex/apexunit.xml
* rulesets/apex/braces.xml
* rulesets/apex/complexity.xml
* rulesets/apex/empty.xml
* rulesets/apex/metrics.xml
* rulesets/apex/performance.xml
* rulesets/apex/ruleset.xml
* rulesets/apex/securty.xml
* rulesets/apex/style.xml
* rulesets/java/android.xml
* rulesets/java/basic.xml
* rulesets/java/clone.xml
* rulesets/java/codesize.xml
* rulesets/java/comments.xml
* rulesets/java/controversial.xml
* rulesets/java/coupling.xml
* rulesets/java/design.xml
* rulesets/java/empty.xml
* rulesets/java/finalizers.xml
* rulesets/java/imports.xml
* rulesets/java/j2ee.xml
* rulesets/java/javabeans.xml
* rulesets/java/junit.xml
* rulesets/java/logging-jakarta-commons.xml
* rulesets/java/logging-java.xml
* rulesets/java/metrics.xml
* rulesets/java/migrating.xml
* rulesets/java/migrating_to_13.xml
* rulesets/java/migrating_to_14.xml
* rulesets/java/migrating_to_15.xml
* rulesets/java/migrating_to_junit4.xml
* rulesets/java/naming.xml
* rulesets/java/optimizations.xml
* rulesets/java/strictexception.xml
* rulesets/java/strings.xml
* rulesets/java/sunsecure.xml
* rulesets/java/typeresolution.xml
* rulesets/java/unnecessary.xml
* rulesets/java/unusedcode.xml
* rulesets/ecmascript/basic.xml
* rulesets/ecmascript/braces.xml
* rulesets/ecmascript/controversial.xml
* rulesets/ecmascript/unnecessary.xml
* rulesets/jsp/basic.xml
* rulesets/jsp/basic-jsf.xml
* rulesets/plsql/codesize.xml
* rulesets/plsql/dates.xml
* rulesets/plsql/strictsyntax.xml
* rulesets/plsql/TomKytesDespair.xml
* rulesets/vf/security.xml
* rulesets/vm/basic.xml
* rulesets/pom/basic.xml
* rulesets/xml/basic.xml
* rulesets/xsl/xpath.xml
* rulesets/releases/*

#### Fixed issues

* cli
  * [#4594](https://github.com/pmd/pmd/pull/4594):   \[cli] Change completion generation to runtime
  * [#4685](https://github.com/pmd/pmd/pull/4685):   \[cli] Clarify CPD documentation, fix positional parameter handling
  * [#4723](https://github.com/pmd/pmd/issues/4723): \[cli] Launch fails for "bash pmd"
* core
  * [#1027](https://github.com/pmd/pmd/issues/1027): \[core] Apply the new PropertyDescriptor&lt;Pattern&gt; type where applicable
  * [#3903](https://github.com/pmd/pmd/issues/3903): \[core] Consolidate `n.s.pmd.reporting` package
  * [#3905](https://github.com/pmd/pmd/issues/3905): \[core] Stabilize tree export API
  * [#3917](https://github.com/pmd/pmd/issues/3917): \[core] Consolidate `n.s.pmd.lang.rule` package
  * [#4065](https://github.com/pmd/pmd/issues/4065): \[core] Rename TokenMgrError to LexException, Tokenizer to CpdLexer
  * [#4309](https://github.com/pmd/pmd/issues/4309): \[core] Cleanups in XPath area
  * [#4312](https://github.com/pmd/pmd/issues/4312): \[core] Remove unnecessary property `color` and system property `pmd.color` in `TextColorRenderer`
  * [#4313](https://github.com/pmd/pmd/issues/4313): \[core] Remove support for &lt;lang&gt;-&lt;ruleset&gt; hyphen notation for ruleset references
  * [#4314](https://github.com/pmd/pmd/issues/4314): \[core] Remove ruleset compatibility filter (RuleSetFactoryCompatibility) and CLI option `--no-ruleset-compatibility`
  * [#4348](https://github.com/pmd/pmd/issues/4348): \[core] Consolidate @<!-- -->InternalApi classes
  * [#4349](https://github.com/pmd/pmd/issues/4349): \[core] Cleanup remaining experimental and deprecated API
  * [#4378](https://github.com/pmd/pmd/issues/4378): \[core] Ruleset loading processes commented rules
  * [#4674](https://github.com/pmd/pmd/issues/4674): \[core] WARNING: Illegal reflective access by org.codehaus.groovy.reflection.CachedClass
  * [#4694](https://github.com/pmd/pmd/pull/4694):   \[core] Fix line/col numbers in TokenMgrError
  * [#4717](https://github.com/pmd/pmd/issues/4717): \[core] XSLTRenderer doesn't close report file
  * [#4750](https://github.com/pmd/pmd/pull/4750):   \[core] Fix flaky SummaryHTMLRenderer
  * [#4782](https://github.com/pmd/pmd/pull/4782):   \[core] Avoid using getImage/@<!-- -->Image
* doc
  * [#995](https://github.com/pmd/pmd/issues/995):   \[doc] Document API evolution principles as ADR
  * [#2511](https://github.com/pmd/pmd/issues/2511): \[doc] Review guides for writing java/xpath rules for correctness with PMD 7
  * [#3175](https://github.com/pmd/pmd/issues/3175): \[doc] Document language module features
  * [#4308](https://github.com/pmd/pmd/issues/4308): \[doc] Document XPath API @<!-- ->NoAttribute and @<!-- -->DeprecatedAttribute
  * [#4319](https://github.com/pmd/pmd/issues/4319): \[doc] Document TypeRes API and Symbols API
  * [#4659](https://github.com/pmd/pmd/pull/4659):   \[doc] Improve ant documentation
  * [#4669](https://github.com/pmd/pmd/pull/4669):   \[doc] Add bld PMD Extension to Tools / Integrations
  * [#4676](https://github.com/pmd/pmd/issues/4676): \[doc] Clarify how CPD `--ignore-literals` and `--ignore-identifiers` work
  * [#4704](https://github.com/pmd/pmd/issues/4704): \[doc] Multivalued properties do not accept | as a separator
* miscellaneous
  * [#4699](https://github.com/pmd/pmd/pull/4699):   Make PMD buildable with java 21
  * [#4586](https://github.com/pmd/pmd/pull/4586):   Use explicit encoding in ruleset xml files
  * [#4642](https://github.com/pmd/pmd/issues/4642): Update regression tests with Java 21 language features
  * [#4736](https://github.com/pmd/pmd/issues/4736): \[ci] Improve build procedure
  * [#4741](https://github.com/pmd/pmd/pull/4741):   Add pmd-compat6 module for maven-pmd-plugin
  * [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6
  * [#4776](https://github.com/pmd/pmd/issues/4776): \[ci] Upgrade to ruby 3
  * [#4796](https://github.com/pmd/pmd/pull/4796):   Remove deprecated and release rulesets
* apex
  * [#3766](https://github.com/pmd/pmd/issues/3766): \[apex] Replace Jorje with fully open source front-end
* apex-documentation
  * [#4774](https://github.com/pmd/pmd/issues/4774): \[apex] ApexDoc false-positive for the first method of an annotated Apex class
* apex-performance
  * [#4675](https://github.com/pmd/pmd/issues/4675): \[apex] New Rule: OperationWithHighCostInLoop
* groovy
  * [#4726](https://github.com/pmd/pmd/pull/4726):   \[groovy] Support Groovy to 3 and 4 and CPD suppressions
* java
  * [#1307](https://github.com/pmd/pmd/issues/1307): \[java] AccessNode API changes
  * [#3751](https://github.com/pmd/pmd/issues/3751): \[java] Rename some node types
  * [#4628](https://github.com/pmd/pmd/pull/4628):   \[java] Support loading classes from java runtime images
  * [#4753](https://github.com/pmd/pmd/issues/4753): \[java] PMD crashes while using generics and wildcards
* java-bestpractices
  * [#4603](https://github.com/pmd/pmd/issues/4603): \[java] UnusedAssignment false positive in record compact constructor
  * [#4625](https://github.com/pmd/pmd/issues/4625): \[java] UnusedPrivateMethod false positive: Autoboxing into Number
* java-codestyle
  * [#2847](https://github.com/pmd/pmd/issues/2847): \[java] New Rule: Use Explicit Types
  * [#4239](https://github.com/pmd/pmd/issues/4239): \[java] UnnecessaryLocalBeforeReturn - false positive with catch clause
  * [#4578](https://github.com/pmd/pmd/issues/4578): \[java] CommentDefaultAccessModifier comment needs to be before annotation if present
  * [#4631](https://github.com/pmd/pmd/issues/4631): \[java] UnnecessaryFullyQualifiedName fails to recognize illegal self reference in enums
  * [#4645](https://github.com/pmd/pmd/issues/4645): \[java] CommentDefaultAccessModifier - False Positive with JUnit5's ParameterizedTest
  * [#4754](https://github.com/pmd/pmd/pull/4754):   \[java] EmptyControlStatementRule: Add allowCommentedBlocks property
  * [#4816](https://github.com/pmd/pmd/issues/4816): \[java] UnnecessaryImport false-positive on generic method call with on lambda
* java-design
  * [#174](https://github.com/pmd/pmd/issues/174):   \[java] SingularField false positive with switch in method that both assigns and reads field
* java-errorprone
  * [#718](https://github.com/pmd/pmd/issues/718):   \[java] BrokenNullCheck false positive with parameter/field confusion
  * [#1831](https://github.com/pmd/pmd/issues/1831): \[java] DetachedTestCase reports abstract methods
  * [#4719](https://github.com/pmd/pmd/pull/4719):   \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string
* javascript
  * [#4673](https://github.com/pmd/pmd/pull/4673):   \[javascript] CPD: Added support for decorator notation
* plsql
  * [#4820](https://github.com/pmd/pmd/issues/4820): \[plsql] WITH clause is ignored for SELECT INTO statements
* swift
  * [#4697](https://github.com/pmd/pmd/issues/4697): \[swift] Support Swift 5.9 features (mainly macros expansion expressions)
* xml-bestpractices
  * [#4592](https://github.com/pmd/pmd/pull/4592):   \[xml] Add MissingEncoding rule

#### API Changes

**New API**

The API around {%jdoc core::util.treeexport.TreeRenderer %} has been declared as stable. It was previously
experimental. It can be used via the CLI subcommand `ast-dump` or programmatically, as described
on [Creating XML dump of the AST]({{ baseurl }}pmd_userdocs_extending_ast_dump.html).

**General AST Changes to avoid `@Image`**

See [General AST Changes to avoid @Image]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html#general-ast-changes-to-avoid-image)
in the migration guide for details.

**XPath Rules**
* The property `version` was already deprecated and has finally been removed. Please don't define the version
  property anymore in your custom XPath rules. By default, the latest XPath version will be used, which
  is XPath 3.1.

**Moved classes/consolidated packages**

* pmd-core
  * Many types have been moved from the base package `net.sourceforge.pmd` into subpackage {% jdoc_package core::lang.rule %}
    * {%jdoc core::lang.rule.Rule %}
    * {%jdoc core::lang.rule.RulePriority %}
    * {%jdoc core::lang.rule.RuleSet %}
    * {%jdoc core::lang.rule.RuleSetFactory %}
    * {%jdoc core::lang.rule.RuleSetLoader %}
    * {%jdoc core::lang.rule.RuleSetLoadException %}
    * {%jdoc core::lang.rule.RuleSetWriter %}
  * Many types have been moved from the base package `net.sourceforge.pmd` into subpackage {% jdoc_package core::reporting %}
    * {%jdoc core::reporting.Report %}
    * {%jdoc core::reporting.RuleContext %}
    * {%jdoc core::reporting.RuleViolation %}
    * {%jdoc core::reporting.ViolationSuppressor %}
  * {%jdoc core::lang.rule.xpath.XPathRule %} has been moved into subpackage {% jdoc_package core::lang.rule.xpath %}.

**Internalized classes and interfaces and methods**

The following classes/methods have been marked as @<!-- -->InternalApi before and are now moved into a `internal`
package or made (package) private and are _not accessible_ anymore.

* pmd-core
  * `net.sourceforge.pmd.cache.AbstractAnalysisCache` (moved to internal, now package private)
  * `net.sourceforge.pmd.cache.AnalysisCache` (moved to internal)
  * `net.sourceforge.pmd.cache.AnalysisCacheListener` (moved to internal)
  * `net.sourceforge.pmd.cache.AnalysisResult` (moved to internal)
  * `net.sourceforge.pmd.cache.CachedRuleMapper` (moved to internal, now package private)
  * `net.sourceforge.pmd.cache.CachedRuleViolation` (moved to internal, now package private)
  * `net.sourceforge.pmd.cache.ChecksumAware` (moved to internal)
  * `net.sourceforge.pmd.cache.FileAnalysisCache` (moved to internal)
  * `net.sourceforge.pmd.cache.NoopAnalysisCache` (moved to internal)
  * `net.sourceforge.pmd.util.ResourceLoader` (moved to internal)
  * {%jdoc !!core::cpd.Tokens %}
    * Constructor is now package private.
  * {%jdoc !!core::lang.LanguageProcessor.AnalysisTask %}
    * Constructor is now package private.
    * Method `withFiles(java.util.List)` is now package private. Note: it was not previously marked with @<!-- -->InternalApi.
  * {%jdoc !!core::lang.rule.RuleTargetSelector %}
    * Method `isRuleChain()` has been removed.
  * {%jdoc !!core::renderers.AbstractAccumulatingRenderer %}
    * {%jdoc core::renderers.AbstractAccumulatingRenderer#renderFileReport(core::reporting.Report) %} - this method is now final
      and can't be overridden anymore.
  * {%jdoc !!core::reporting.Report %}
    * Constructor as well as the methods `addRuleViolation`, `addConfigError`, `addError` are now private.
  * {%jdoc !!core::reporting.RuleContext %}
    * Method `getRule()` is now package private.
    * Method `create(FileAnalysisListener listener, Rule rule)` has been removed.
  * `net.sourceforge.pmd.rules.RuleFactory`: moved into subpackage `lang.rule` and made package private.
    It has now been hidden completely from public API.
  * Many types have been moved from into subpackage `lang.rule.internal`.
    * `net.sourceforge.pmd.RuleSetReference`
    * `net.sourceforge.pmd.RuleSetReferenceId`
    * `net.sourceforge.pmd.RuleSets`
  * `net.sourceforge.pmd.lang.rule.ParametricRuleViolation` is now package private and moved to `net.sourceforge.pmd.reporting.ParametricRuleViolation`.
    The only public API is {%jdoc core::reporting.RuleViolation %}.
  * {%jdoc !!core::lang.rule.RuleSet %}
    * Method `applies(Rule,LanguageVersion)` is now package private.
    * Method `applies(TextFile)` has been removed.
    * Method `applies(FileId)` is now package private.
  * {%jdoc !!core::lang.rule.RuleSetLoader %}
    * Method `loadRuleSetsWithoutException(java.util.List)` is now package private.
  * {%jdoc !!core::lang.rule.RuleSetLoadException %}
    * All constructors are package private now.
  * {%jdoc !!core::lang.ast.LexException %} - the constructor `LexException(boolean, String, int, int, String, char)` is now package private.
    It is only used by JavaCC-generated token managers.
  * {%jdoc !!core::PMDConfiguration %}
    * Method `setAnalysisCache(AnalysisCache)` is now package private. Use {%jdoc core::PMDConfiguration#setAnalysisCacheLocation(java.lang.String) %} instead.
    * Method `getAnalysisCache()` is now package private.
  * {%jdoc !!core::lang.document.FileCollector %}
    * Method `newCollector(LanguageVersionDiscoverer, PmdReporter)` is now package private.
    * Method `newCollector(PmdReporter)` is now package private.
    * In order to create a FileCollector, use {%jdoc core::PmdAnalysis#files() %} instead.
  * {%jdoc !!core::lang.rule.xpath.Attribute %}
    * Method `replacementIfDeprecated()` is now package private.
  * `net.sourceforge.pmd.properties.PropertyTypeId` - moved in subpackage `internal`.
  * {%jdoc !!core::properties.PropertyDescriptor %} - method `getTypeId()` is now package private.
* pmd-ant
  * {%jdoc !!ant::ant.Formatter %}
    * Method `getRenderer()` has been removed.
    * Method `start(String)` is private now.
    * Method `end(Report)` has been removed.
    * Method `isNoOutputSupplied()` is now package private.
    * Method `newListener(Project)` is now package private.
  * {%jdoc !!ant::ant.PMDTask %}
    * Method `getRelativizeRoots()` has been removed.
  * `net.sourceforge.pmd.ant.ReportException` is now package private. Note: It was not marked with @<!-- -->InternalApi before.
* pmd-apex
  * {%jdoc !!apex::ast.ApexNode %}
    * Method `getNode()` has been removed. It was only deprecated before and not marked with @<!-- -->InternalApi.
      However, it gave access to the wrapped Jorje node and was thus internal API.
  * {%jdoc !!apex::ast.AbstractApexNode %}
    * Method `getNode()` is now package private.
  * {%jdoc !!apex::multifile.ApexMultifileAnalysis %}
    * Constructor is now package private.
  * `net.sourceforge.pmd.lang.apex.rule.design.AbstractNcssCountRule` (now package private)
  * `net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule` (moved to package `net.sourceforge.pmd.apex.rule.bestpractices`, now package private)
* pmd-java
  * `net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule` (moved to internal)
  * `net.sourceforge.pmd.lang.java.types.ast.LazyTypeResolver` (moved to internal)
  * {%jdoc !!java::types.JMethodSig %}
    * Method `internalApi()` has been removed.
  * {%jdoc !!java::types.TypeOps %}
    * Method `isSameTypeInInference(JTypeMirror,JTypeMirror)` is now package private.
* pmd-jsp
  * {%jdoc !!jsp::ast.JspParser %}
    * Method `getTokenBehavior()` has been removed.
* pmd-modelica
  * {%jdoc !!modelica::ast.InternalApiBridge %} renamed from `InternalModelicaNodeApi`.
  * {%jdoc !!modelica::resolver.InternalApiBridge %} renamed from `InternalModelicaResolverApi`.
  * `net.sourceforge.pmd.lang.modelica.resolver.ModelicaSymbolFacade` has been removed.
  * `net.sourceforge.pmd.lang.modelica.resolver.ResolutionContext` (moved to internal)
  * `net.sourceforge.pmd.lang.modelica.resolver.ResolutionState` (moved to internal). Note: it was not previously marked with @<!-- -->InternalApi.
  * `net.sourceforge.pmd.lang.modelica.resolver.Watchdog` (moved to internal). Note: it was not previously marked with @<!-- -->InternalApi.
* pmd-plsql
  * `net.sourceforge.pmd.lang.plsql.rule.design.AbstractNcssCountRule` is now package private.
* pmd-scala
  * {%jdoc !!scala::ScalaLanguageModule %}
    * Method `dialectOf(LanguageVersion)` has been removed.

**Removed classes and members (previously deprecated)**

The annotation `@DeprecatedUntil700` has been removed.

* pmd-core
  * {%jdoc !!core::cpd.CpdLanguageProperties %}. The field `DEFAULT_SKIP_BLOCKS_PATTERN` has been removed.
  * {%jdoc !!core::lang.ast.impl.antlr4.BaseAntlrNode %} - method `joinTokenText()` has been removed.
  * {%jdoc !!core::lang.ast.Node %} - many methods have been removed:
    * `getNthParent(int)` - Use {%jdoc core::lang.ast.Node#ancestors() %} instead, e.g. `node.ancestors().get(n-1)`
    * `getFirstParentOfType(Class)` - Use {%jdoc core::lang.ast.Node#ancestors(java.lang.Class) %} instead, e.g. `node.ancestors(parentType).first()`
    * `getParentsOfType(Class)` - Use {%jdoc core::lang.ast.Node#ancestors(java.lang.Class) %} instead, e.g. `node.ancestors(parentType).toList()`
    * `findChildrenOfType(Class)` - Use {%jdoc core::lang.ast.Node#children(java.lang.Class) %} instead, e.g. `node.children(childType).toList()`
    * `findDescendantsOfType(Class)` - Use {%jdoc core::lang.ast.Node#descendants(java.lang.Class) %} instead, e.g. `node.descendants(targetType).toList()`
    * `findDescendantsOfType(Class,boolean)` - Use {%jdoc core::lang.ast.Node#descendants(java.lang.Class) %} instead, e.g. `node.descendants(targetType).crossFindBoundaries(b).toList()`
    * `getFirstChildOfType(Class)` - Use {%jdoc core::lang.ast.Node#firstChild(java.lang.Class) %} instead
    * `getFirstDescendantOfType(Class)` - Use {%jdoc core::lang.ast.Node#descendants(java.lang.Class) %} instead, e.g. `node.descendants(targetType).first()`
    * `hasDescendantOfType(Class)` - Use {%jdoc core::lang.ast.Node#descendants(java.lang.Class) %} instead, e.g. `node.descendants(targetType).nonEmpty()`
    * `findChildNodesWithXPath(String)` - Use the {%jdoc core::lang.ast.NodeStream %} API instead.
  * {%jdoc !!core::lang.ast.impl.GenericNode %} - method `getNthParent(int)` has been removed. Use {%jdoc core::lang.ast.Node#ancestors() %} instead, e.g. `node.ancestors().get(n-1)`
  * {%jdoc !!core::lang.document.FileCollector %} - method `addZipFile(java.nio.file.Path)` has been removed. Use {%jdoc core::lang.document.FileCollector#addZipFileWithContent(java.nio.file.Path) %} instead
  * {%jdoc !!core::lang.document.TextDocument %} - method `readOnlyString(CharSequence,String,LanguageVersion)` has been removed.
    Use {%jdoc core::lang.document.TextDocument#readOnlyString(java.lang.CharSequence,core::lang.document.FileId,core::lang.LanguageVersion) %} instead.
  * {%jdoc !!core::lang.document.TextFile %} - method `dataSourceCompat(DataSource,PMDConfiguration)` has been removed.
    Use {%jdoc core::lang.document.TextFile %} directly, e.g. {%jdoc core::lang.document.TextFile.forPath(java.nio.file.Path,java.nio.charset.Charset,core::lang.LanguageVersion) %}
  * {%jdoc !!core::lang.rule.xpath.XPathVersion %}
    * `XPATH_1_0`
    * `XPATH_1_0_COMPATIBILITY`
    * `XPATH_2_0`
    * Only XPath version 3.1 is now supported.  This version of the XPath language is mostly identical to
      XPath 2.0. XPath rules by default use now {%jdoc core::lang.rule.xpath.XPathVersion#XPATH_3_1 %}.
  * `net.sourceforge.pmd.lang.rule.AbstractDelegateRule` removed. It has been merged with {%jdoc core::lang.rule.RuleReference %}.
  * {%jdoc !!core::lang.rule.AbstractRule %} - the following methods have been removed:
    * `deepCopyValuesTo(AbstractRule)` - use {%jdoc core::lang.rule.AbstractRule#deepCopy() %} instead.
    * `addRuleChainVisit(Class)` - override {%jdoc core::lang.rule.AbstractRule#buildTargetSelector() %} in order to register nodes for rule chain visits.
    * `addViolation(...)` - use {%jdoc core::RuleContext#addViolation(core::lang.ast.Node) %} instead, e.g. via `asCtx(data).addViolation(...)`.
      Note: These methods were only marked as deprected in javadoc.
    * `addViolationWithMessage(...)` - use {%jdoc core::RuleContext#addViolationWithMessage(core::lang.ast.Node,java.lang.String) %} instead, e.g. via
      `asCtx(data).addViolationWithMessage(...)`. Note: These methods were only marked as deprected in javadoc.
  * {%jdoc !!core::lang.rule.RuleReference %} - the following methods have been removed:
    * `setRuleSetReference(RuleSetReference)` - without replacement. Just construct new {%jdoc core::lang.rule.RuleReference %} instead.
    * `hasOverriddenProperty(PropertyDescriptor)` - use {%jdoc core::lang.rule.RuleReference#isPropertyOverridden(core::properties.PropertyDescriptor) %} instead.
  * {%jdoc !!core::lang.rule.XPathRule %}
    * The constant `XPATH_DESCRIPTOR` has been made private and is not accessible anymore.
  * {%jdoc !!core::lang.Language %} - method `getTerseName()` removed. Use {%jdoc core::lang.Language#getId() %} instead.
  * {%jdoc !!core::lang.LanguageModuleBase %} - method `getTerseName()` removed. Use {%jdoc core::lang.LanguageModuleBase#getId() %} instead.
  * {%jdoc !!core::lang.LanguageRegistry %} - the following methods have been removed:
    * `getLanguage(String)` - use {%jdoc core::lang.LanguageRegistry.getLanguageByFullName(java.lang.String) %}
      via {%jdoc core::lang.LanguageRegistry#PMD %} or {%jdoc core::lang.LanguageRegistry#CPD %} instead.
    * `findLanguageByTerseName(String)` - use {%jdoc core::lang.LanguageRegistry#getLanguageById(java.lang.String) %}
      via {%jdoc core::lang.LanguageRegistry#PMD %} or {%jdoc core::lang.LanguageRegistry#CPD %} instead.
    * `findByExtension(String)` - removed without replacement.
  * {%jdoc !!core::lang.LanguageVersionDiscoverer %} - method `getLanguagesForFile(java.io.File)` removed.
    Use {%jdoc core::lang.LanguageVersionDiscoverer#getLanguagesForFile(java.lang.String) %} instead.
  * {%jdoc !!core::properties.AbstractPropertySource %}
    * field `propertyDescriptors` has been made private and is not accessible anymore.
      Use {%jdoc core::properties.AbstractPropertySource#getPropertyDescriptors() %} instead.
    * field `propertyValuesByDescriptor` has been made private and is not accessible anymore.
      Use {%jdoc core::properties.AbstractPropertySource#getPropertiesByPropertyDescriptor() %}
      or {%jdoc core::properties.AbstractPropertySource#getOverriddenPropertiesByPropertyDescriptor() %} instead.
    * method `copyPropertyDescriptors()` has been removed. Use {%jdoc core::properties.AbstractPropertySource#getPropertyDescriptors() %} instead.
    * method `copyPropertyValues()` has been removed. Use {%jdoc core::properties.AbstractPropertySource#getPropertiesByPropertyDescriptor() %}
      or {%jdoc core::properties.AbstractPropertySource#getOverriddenPropertiesByPropertyDescriptor() %} instead.
  * {%jdoc !!core::reporting.Reportable %} - the following methods have been removed. Use {%jdoc core::reporting.Reportable#getReportLocation() %} instead
    * `getBeginLine()`
    * `getBeginColumn()`
    * `getEndLine()`
    * `getEndColumn()`
  * `net.sourceforge.pmd.util.datasource.DataSource` - use {%jdoc core::lang.document.TextFile %} instead.
  * `net.sourceforge.pmd.util.datasource.FileDataSource`
  * `net.sourceforge.pmd.util.datasource.ReaderDataSource`
  * `net.sourceforge.pmd.util.datasource.ZipDataSource`
  * {%jdoc !!core::util.CollectionUtil %}
    * method `invertedMapFrom(...)` has been removed.
    * method `mapFrom(...)` has been removed.
  * {%jdoc !!core::AbstractConfiguration %} - the following methods have been removed:
    * `setIgnoreFilePath(String)` - use {%jdoc core::AbstractConfiguration#setIgnoreFilePath(java.nio.file.Path) %} instead.
    * `setInputFilePath(String)` - use {%jdoc core::AbstractConfiguration#setInputFilePath(java.nio.file.Path) %} instead.
    * `setInputPaths(String)` - use {%jdoc core::AbstractConfiguration#setInputPathList(java.util.List) %} or
      {%jdoc core::AbstractConfiguration#addInputPath(java.nio.file.Path) %} instead.
    * `setInputUri(String)` - use {%jdoc core::AbstractConfiguration#setInputUri(java.net.URI) %} instead.
  * {%jdoc !!core::PMDConfiguration %} - the following methods have been removed
    * `prependClasspath(String)` - use {%jdoc core::PMDConfiguration#prependAuxClasspath(java.lang.String) %} instead.
    * `getRuleSets()` - use {%jdoc core::PMDConfiguration#getRuleSetPaths() %} instead.
    * `setRuleSets(String)` - use {%jdoc core::PMDConfiguration#setRuleSets(java.util.List) %} or
      {%jdoc core::PMDConfiguration#addRuleSet(java.lang.String) %} instead.
    * `setReportFile(String)` - use {%jdoc core::PMDConfiguration#setReportFile(java.nio.file.Path) %} instead.
    * `getReportFile()` - use {%jdoc core::PMDConfiguration#getReportFilePath() %} instead.
  * {%jdoc !!core::Report %} - method `merge(Report)` has been removed. Use {%jdoc core::Report#union(core::Report) %} instead.
  * {%jdoc !!core::RuleSetLoader %} - method `toFactory()` has been made package private and is not accessible anymore.
  * {%jdoc !!core::RuleViolation %} - the following methods have been removed:
    * `getPackageName()` - use {%jdoc core::RuleViolation#getAdditionalInfo() %} with {%jdoc core::RuleViolation#PACKAGE_NAME %} instead, e.g. `getAdditionalInfo().get(PACKAGE_NAME)`.
    * `getClassName()` - use {%jdoc core::RuleViolation#getAdditionalInfo() %} with {%jdoc core::RuleViolation#CLASS_NAME %} instead, e.g. `getAdditionalInfo().get(CLASS_NAME)`.
    * `getMethodName()` - use {%jdoc core::RuleViolation#getAdditionalInfo() %} with {%jdoc core::RuleViolation#METHOD_NAME %} instead, e.g. `getAdditionalInfo().get(METHOD_NAME)`.
    * `getVariableName()` - use {%jdoc core::RuleViolation#getAdditionalInfo() %} with {%jdoc core::RuleViolation#VARIABLE_NAME %} instead, e.g. `getAdditionalInfo().get(VARIABLE_NAME)`.
* pmd-apex
  * {%jdoc apex::lang.apex.ast.ApexNode %} and {% jdoc apex::lang.apex.ast.ASTApexFile %}
    * `#getApexVersion()`: In PMD 6, this method has been deprecated but was defined in the class `ApexRootNode`.
      The version returned is always "Version.CURRENT", as the apex compiler integration
      doesn't use additional information which Apex version actually is used. Therefore, this method can't be
      used to determine the Apex version of the project that is being analyzed.

      If the current version is needed, then `Node.getTextDocument().getLanguageVersion()` can be used. This
      is the version that has been selected via CLI `--use-version` parameter.
  * {%jdoc !!apex::lang.apex.ast.ApexNode %}
    * method `jjtAccept()` has been removed.
      Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
    * method `getNode()` has been removed. The underlying node is only available in AST nodes, but not in rule implementations.
  * {%jdoc !!apex::lang.apex.ast.AbstractApexNode %} - method `getNode()` is now package private.
    AST nodes still have access to the underlying Jorje node via the protected property `node`.
  * `net.sourceforge.pmd.lang.apex.ast.ApexParserVisitor`
    Use {%jdoc apex::lang.apex.ast.ApexVisitor %} or {%jdoc apex::lang.apex.ast.ApexVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter`
  * {%jdoc !!apex::lang.apex.ast.ASTAssignmentExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTAssignmentExpression#getOp() %} instead.
  * {%jdoc !!apex::lang.apex.ast.ASTBinaryExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTBinaryExpression#getOp() %} instead.
  * {%jdoc !!apex::lang.apex.ast.ASTBooleanExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTBooleanExpression#getOp() %} instead.
  * {%jdoc !!apex::lang.apex.ast.ASTPostfixExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTPostfixExpression#getOp() %} instead.
  * {%jdoc !!apex::lang.apex.ast.ASTPrefixExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTPrefixExpression#getOp() %} instead.
  * `net.sourceforge.pmd.lang.apex.rule.security.Helper` removed. This was actually internal API.
* pmd-java
  * {%jdoc !!java::lang.java.ast.AbstractPackageNameModuleDirective %} - method `getImage()` has been removed.
    Use {%jdoc java::lang.java.ast.AbstractPackageNameModuleDirective#getPackageName() %} instead.
  * {%jdoc !!java::lang.java.ast.AbstractTypeDeclaration %} - method `getImage()` has been removed.
    Use {%jdoc java::lang.java.ast.AbstractTypeDeclaration#getSimpleName() %} instead.
  * {%jdoc !!java::lang.java.ast.ASTAnnotation %} - method `getAnnotationName()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTClassType %}
    * constructor `ASTClassType(java.lang.String)` has been removed.
    * method `getImage()` has been removed.
    * method `isReferenceToClassSameCompilationUnit()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTFieldDeclaration %} - method `getVariableName()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTLiteral %} - the following methods have been removed:
    * `isStringLiteral()` - use `node instanceof ASTStringLiteral` instead.
    * `isCharLiteral()` - use `node instanceof ASTCharLiteral` instead.
    * `isNullLiteral()` - use `node instanceof ASTNullLiteral` instead.
    * `isBooleanLiteral()` - use `node instanceof ASTBooleanLiteral` instead.
    * `isNumericLiteral()` - use `node instanceof ASTNumericLiteral` instead.
    * `isIntLiteral()` - use {%jdoc java::lang.java.ast.ASTNumericLiteral#isIntLiteral() %} instead.
    * `isLongLiteral()` - use {%jdoc java::lang.java.ast.ASTNumericLiteral#isLongLiteral() %} instead.
    * `isFloatLiteral()` - use {%jdoc java::lang.java.ast.ASTNumericLiteral#isFloatLiteral() %} instead.
    * `isDoubleLiteral()` - use {%jdoc java::lang.java.ast.ASTNumericLiteral#isDoubleLiteral() %} instead.
  * {%jdoc !!java::lang.java.ast.ASTMethodDeclaration %} - methods `getImage()` and `getMethodName()` have been removed.
    Use {%jdoc java::lang.java.ast.ASTMethodDeclaration#getName() %} instead.
  * {%jdoc !!java::lang.java.ast.ASTMethodReference %} - method `getImage()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTModuleName %} - method `getImage()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTPrimitiveType %} - method `getImage()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTType %}
    * `getTypeImage()` has been removed.
    * `getArrayDepth()` has been removed. It's only available for arrays: {%jdoc java::lang.java.ast.ASTArrayType#getArrayDepth() %}.
    * `isPrimitiveType()` - use `node instanceof ASTPrimitiveType` instead.
    * `isArrayType()` - use `node instanceof ASTArrayType` instead.
    * `isClassOrInterfaceType()` - use `node instanceof ASTClassType` instead.
  * {%jdoc !!java::lang.java.ast.ASTTypeDeclaration %} - method `getImage()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTUnaryExpression %} - method `isPrefix()` has been removed.
    Use {%jdoc java::lang.java.ast.ASTUnaryExpression#getOperator() %}`.isPrefix()` instead.
  * {%jdoc !!java::lang.java.ast.ASTVariableId %} - methods `getImage()` and `getVariableName()` have been removed.
    Use {%jdoc java::lang.java.ast.ASTVariableId#getName() %} instead.
  * {%jdoc !!java::lang.java.ast.JavaComment %} - method `getImage()` has been removed.
    Use {%jdoc java::lang.java.ast.JavaComment#getText() %} instead.
  * {%jdoc !!java::lang.java.ast.JavaNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
  * `net.sourceforge.pmd.lang.java.ast.JavaParserVisitor`
    Use {%jdoc java::lang.java.ast.JavaVisitor %} or {%jdoc java::lang.java.ast.JavaVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter`
  * {%jdoc !!java::lang.java.ast.ModifierOwner %} 
    * `isFinal()` - This is still available in various subtypes, where it makes sense, e.g. {%jdoc java::lang.java.ast.ASTLocalVariableDeclaration#isFinal() %}.
    * `isAbstract()` - This is still available in subtypes, e.g. {%jdoc java::lang.java.ast.ASTTypeDeclaration#isAbstract() %}.
    * `isStrictfp()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(STRICTFP)`.
    * `isSynchronized()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(SYNCHRONIZED)`.
    * `isNative()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(NATIVE)`.
    * `isStatic()` - This is still available in subtypes, e.g. {%jdoc java::lang.java.ast.ASTMethodDeclaration#isStatic() %}.
    * `isVolatile()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(VOLATILE)`.
    * `isTransient()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(TRANSIENT)`.
    * `isPrivate()` - Use {%jdoc java::lang.java.ast.ModifierOwner#getVisibility() %} instead, e.g. `getVisibility() == Visibility.V_PRIVATE`.
    * `isPublic()` - Use {%jdoc java::lang.java.ast.ModifierOwner#getVisibility() %} instead, e.g. `getVisibility() == Visibility.V_PUBLIC`.
    * `isProtected()` - Use {%jdoc java::lang.java.ast.ModifierOwner#getVisibility() %} instead, e.g. `getVisibility() == Visibility.V_PROTECTED`.
    * `isPackagePrivate()` - Use {%jdoc java::lang.java.ast.ModifierOwner#getVisibility() %} instead, e.g. `getVisibility() == Visibility.V_PACKAGE`.
    * `isSyntacticallyAbstract()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasExplicitModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasExplicitModifiers(ABSTRACT)`.
    * `isSyntacticallyPublic()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasExplicitModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasExplicitModifiers(PUBLIC)`.
    * `isSyntacticallyStatic()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasExplicitModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasExplicitModifiers(STATIC)`.
    * `isSyntacticallyFinal()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasExplicitModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasExplicitModifiers(FINAL)`.
  * {%jdoc !!java::lang.java.ast.TypeNode %} - method `getType()` has been removed. Use {%jdoc java::lang.java.ast.TypeNode#getTypeMirror() %} instead.
* pmd-javascript
  * {%jdoc javascript::lang.ecmascript.ast.AbstractEcmascriptNode %} - method `getNode()` has been removed.
    AST nodes still have access to the underlying Rhino node via the protected property `node`.
  * {%jdoc javascript::lang.ecmascript.ast.ASTFunctionNode %} - method `getBody(int)` removed.
    Use {%jdoc javascript::lang.ecmascript.ast.ASTFunctionNode#getBody() %} instead.
  * {%jdoc javascript::lang.ecmascript.ast.ASTTryStatement %}
    * method `isCatch()` has been removed. Use {%jdoc javascript::lang.ecmascript.ast.ASTTryStatement#hasCatch() %} instead.
    * method `isFinally()` has been removed. USe {%jdoc javascript::lang.ecmascript.ast.ASTTryStatement#hasFinally() %} instead.
  * {%jdoc javascript::lang.ecmascript.ast.EcmascriptNode %}
    * method `jjtAccept()` has been removed. Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
    * method `getNode()` has been removed.  The underlying node is only available in AST nodes, but not in rule implementations.
  * `net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserVisitor`
    Use {%jdoc javascript::lang.ecmascript.ast.EcmascriptVisitor %} or {%jdoc javascript::lang.ecmascript.ast.EcmascriptVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserVisitorAdapter`
* pmd-jsp
  * `net.sourceforge.pmd.lang.jsp.ast.JspParserVisitor`
    Use {%jdoc jsp::lang.jsp.ast.JspVisitor %} or {%jdoc jsp::lang.jsp.ast.JspVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.jsp.ast.JspParserVisitorAdapter`
  * {%jdoc !!jsp::lang.jsp.ast.JspNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
* pmd-modelica
  * `net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitor`
    Use {%jdoc modelica::lang.modelica.ast.ModelicaVisitor %} or {%jdoc modelica::lang.modelica.ast.ModelicaVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitorAdapter`
  * {%jdoc !!modelica::lang.modelica.ast.ModelicaNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
  * `net.sourceforge.pmd.lang.modelica.rule.AmbiguousResolutionRule`
    Use {%jdoc modelica::lang.modelica.rule.bestpractices.AmbiguousResolutionRule %} instead.
  * `net.sourceforge.pmd.lang.modelica.rule.ConnectUsingNonConnector`
    Use {%jdoc modelica::lang.modelica.rule.bestpractices.ConnectUsingNonConnectorRule %}
* pmd-plsql
  * `net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitor`
    Use {%jdoc plsql::lang.plsql.ast.PlsqlVisitor %} or {% jdoc plsql::lang.plsql.ast.PlsqlVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter`
  * {%jdoc !!plsql::lang.plsql.ast.PLSQLNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
* pmd-scala
  * {%jdoc !!scala::lang.scala.ast.ScalaNode %}
    * Method `accept()` has been removed. Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
    * Method `getNode()` has been removed. The underlying node is only available in AST nodes, but not in rule implementations.
  * {%jdoc !!scala::lang.scala.ast.AbstractScalaNode %} - method `getNode()` has been removed. AST nodes still have access
    to the underlying Scala node via the protected property `node`.
* pmd-visualforce
  * {%jdoc !!visualforce::lang.vf.ast.VfNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
  * `net.sourceforge.pmd.lang.vf.ast.VfParserVisitor`
    Use {%jdoc visualforce::lang.vf.ast.VfVisitor %} or {%jdoc visualforce::lang.vf.ast.VfVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.vf.ast.VfParserVisitorAdapter`
  * {%jdoc !!visualforce::lang.vf.DataType %} - method `fromBasicType(BasicType)` has been removed.
    Use {%jdoc visualforce::lang.vf.DataType#fromTypeName(java.lang.String) %} instead.
* pmd-vm
  * {%jdoc !!vm::lang.vm.ast.VmNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
  * `net.sourceforge.pmd.lang.vm.ast.VmParserVisitor`
    Use {%jdoc vm::lang.vm.ast.VmVisitor %} or {%jdoc vm::lang.vm.ast.VmVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.vm.ast.VmParserVisitorAdapter`

**Removed classes, interfaces and methods (not previously deprecated)**

* pmd-apex
  * The method `isSynthetic()` in {%jdoc apex::lang.apex.ast.ASTMethod %} has been removed.
    With the switch from Jorje to Summit AST as underlying parser, no synthetic methods are generated by the
    parser anymore. This also means, that there is no XPath attribute `@Synthetic` anymore.
  * The constant `STATIC_INITIALIZER_METHOD_NAME` in {%jdoc apex::lang.apex.rule.codestyle.FieldDeclarationsShouldBeAtStartRule %}
    has been removed. It was used to filter out synthetic methods, but these are not generated anymore with the
    new parser.
  * The method `getContext()` in {%jdoc apex::lang.apex.ast.ASTReferenceExpression %} has been removed.
    It was not used and always returned `null`.
  * The method `getNamespace()` in all AST nodes (defined in {%jdoc apex::lang.apex.ast.ApexNode %}) has
    been removed, as it was never fully implemented. It always returned an empty string.
  * The method `getNameSpace()` in {%jdoc apex::lang.apex.ast.ApexQualifiedName %} has been removed.
  * The class `net.sourceforge.pmd.lang.apex.ast.ASTBridgeMethodCreator` has been removed. This was a node that has
    been generated by the old Jorje parser only.
* pmd-apex-jorje
  * With the switch from Jorje to Summit AST, this maven module is no longer needed and has been removed.
* pmd-core
  * `net.sourceforge.pmd.util.Predicate` has been removed. It was marked as Experimental before. Use
    `java.util.function.Predicate` instead.
* pmd-java
  * The interface `FinalizableNode` (introduced in 7.0.0-rc1) has been removed.
    Its method `isFinal()` has been moved down to the
    nodes where needed, e.g. {% jdoc !!java::lang.java.ast.ASTLocalVariableDeclaration#isFinal() %}.
  * The method `isPackagePrivate()` in {% jdoc java::lang.java.ast.ASTClassDeclaration %} (formerly ASTClassOrInterfaceDeclaration)
    has been removed.
    Use {% jdoc java::lang.java.ast.ModifierOwner#hasVisibility(java::lang.java.ast.ModifierOwner.Visibility) %} instead,
    which can correctly differentiate between local and package private classes.

**Renamed classes, interfaces**

* pmd-core
  * {%jdoc core::util.log.PmdReporter %} - has been renamed from `net.sourceforge.pmd.util.log.MessageReporter`

* pmd-java
  * The interface `AccessNode` has been renamed to {% jdoc java::lang.ast.ModifierOwner %}. This is only relevant
    for Java rules, which use that type directly e.g. through downcasting.
    Or when using the XPath function `pmd-java:nodeIs()`.
  * The node `ASTClassOrInterfaceType` has been renamed to {% jdoc java::lang.ast.ASTClassType %}. XPath rules
    need to be adjusted.
  * The node `ASTClassOrInterfaceDeclaration` has been renamed to {% jdoc java::lang.ast.ASTClassDeclaration %}.
    XPath rules need to be adjusted.
  * The interface `ASTAnyTypeDeclaration` has been renamed to {% jdoc java::lang.ast.ASTTypeDeclaration %}.
    This is only relevant for Java rules, which use that type directly, e.g. through downcasting.
    Or when using the XPath function `pmd-java:nodeIs()`.
  * The interface `ASTMethodOrConstructorDeclaration` has been renamed to
    {% jdoc java::lang.ast.ASTExecutableDeclaration %}. This is only relevant for Java rules, which sue that type
    directly, e.g. through downcasting. Or when using the XPath function `pmd-java:nodeIs()`.
  * The node `ASTVariableDeclaratorId` has been renamed to {% jdoc java::lang.ast.ASTVariableId %}. XPath rules
    need to be adjusted.
  * The node `ASTClassOrInterfaceBody` has been renamed to {% jdoc java::lang.ast.ASTClassBody %}. XPath rules
    need to be adjusted.
* pmd-scala
  * The interface `ScalaParserVisitor` has been renamed to {%jdoc scala::lang.scala.ast.ScalaVisitor %} in order
    to align the naming scheme for the different language modules.
  * The class `ScalaParserVisitorAdapter` has been renamed to {%jdoc scala::lang.scala.ast.ScalaVisitorBase %} in order
    to align the naming scheme for the different language modules.

**Renamed classes and methods**

* pmd-core
  * {%jdoc_old core::lang.ast.TokenMgrError %} has been renamed to {% jdoc core::lang.ast.LexException %}
  * {%jdoc_old core::cpd.Tokenizer %} has been renamed to {% jdoc core::cpd.CpdLexer %}. Along with this rename,
    all the implementations have been renamed as well (`Tokenizer` -> `CpdLexer`), e.g. "CppCpdLexer", "JavaCpdLexer".
    This affects all language modules.
  * {%jdoc_old core::cpd.AnyTokenizer %} has been renamed to {% jdoc core::cpd.AnyCpdLexer %}.

**Classes and methods, that are not experimental anymore**

These were annotated with `@Experimental`, but can now be considered stable.

* pmd-apex
  * {%jdoc !!apex::lang.apex.ast.ASTCommentContainer %}
  * {%jdoc !!apex::lang.apex.multifile.ApexMultifileAnalysis %}
* pmd-core
  * {%jdoc !!core::cpd.CPDReport#filterMatches(java.util.function.Predicate) %}
  * {%jdoc !!core::lang.ast.impl.antlr4.AntlrToken.getKind() %}
  * {%jdoc !!core::lang.ast.impl.javacc.AbstractJjtreeNode %}
  * {%jdoc !!core::lang.ast.impl.TokenDocument %}
  * {%jdoc !!core::lang.ast.AstInfo.getSuppressionComments() %}
  * {%jdoc !!core::lang.ast.AstInfo.withSuppressMap(java.util.Map) %}
  * {%jdoc !!core::lang.ast.GenericToken.getKind() %}
  * {%jdoc !!core::lang.document.FileCollector.addZipFileWithContent(java.nio.file.Path) %}
  * {%jdoc_package core::lang.document %}
  * {%jdoc !!core::lang.LanguageVersionHandler.getLanguageMetricsProvider() %}
  * {%jdoc !!core::lang.LanguageVersionHandler.getDesignerBindings() %}
  * {%jdoc !!core::lang.PlainTextLanguage %}
  * {%jdoc !!core::properties.PropertyConstraint.getXmlConstraint() %}
  * {%jdoc !!core::properties.PropertyConstraint.toOptionalConstraint() %}
  * {%jdoc !!core::properties.PropertyConstraint.fromPredicate(java.util.function.Predicate,java.lang.String) %}
  * {%jdoc !!core::properties.PropertyConstraint.fromPredicate(java.util.function.Predicate,java.lang.String,java.util.Map) %}
  * {%jdoc !!core::renderers.AbstractRenderer.setReportFile(java.lang.String) %}
  * {%jdoc !!core::renderers.Renderer.setReportFile(java.lang.String) %}
  * {%jdoc !!core::util.designerbindings.DesignerBindings %}
  * {%jdoc !!core::util.designerbindings.DesignerBindings.TreeIconId %}
  * {%jdoc !!core::util.designerbindings.RelatedNodesSelector %}
  * {%jdoc !!core::Report.filterViolations(java.util.function.Predicate) %}
  * {%jdoc !!core::Report.union(core::Report) %}
* pmd-groovy
  * {%jdoc !!groovy::lang.groovy.ast.impl.antlr4.GroovyToken.getKind() %}
* pmd-html
  * {%jdoc_package html::lang.html %}
* pmd-java
  * {%jdoc !!java::lang.java.ast.ASTExpression#getConversionContext() %}
  * {%jdoc !!java::lang.java.rule.AbstractJavaRulechainRule#AbstractJavaRulechainRule(java.lang.Class,java.lang.Class...) %}
  * {%jdoc !!java::lang.java.symbols.table.JSymbolTable %}
  * {%jdoc !!java::lang.java.symbols.JElementSymbol %}
  * {%jdoc_package java::lang.java.symbols %}
  * {%jdoc !!java::lang.java.types.ast.ExprContext %}
  * {%jdoc !!java::lang.java.types.JIntersectionType#getInducedClassType() %}
  * {%jdoc !!java::lang.java.types.JTypeMirror#streamMethods(java.util.function.Predicate) %}
  * {%jdoc !!java::lang.java.types.JTypeMirror#streamDeclaredMethods(java.util.function.Predicate) %}
  * {%jdoc !!java::lang.java.types.JTypeMirror#getConstructors() %}
* pmd-kotlin
  * {%jdoc !!kotlin::lang.kotlin.KotlinLanguageModule %}
* pmd-test-schema
  * {%jdoc !!test-schema::test.schema.TestSchemaParser %}

**Removed functionality**

* The CLI parameter `--no-ruleset-compatibility` has been removed. It was only used to allow loading
  some rulesets originally written for PMD 5 also in PMD 6 without fixing the rulesets.
* The class {% jdoc_old core::RuleSetFactoryCompatibility %} has been removed without replacement.
  The different ways to enable/disable this filter in {% jdoc core::PMDConfiguration %}
  (Property "RuleSetFactoryCompatibilityEnabled") and
  {% jdoc ant::ant.PMDTask %} (Property "noRuleSetCompatibility") have been removed as well.
* `textcolor` renderer ({%jdoc core::renderers.TextColorRenderer %}) now renders always in color.
  The property `color` has been removed. The possibility to override this with the system property `pmd.color`
  has been removed as well. If you don't want colors, use `text` renderer ({%jdoc core::renderers.TextRenderer %}).

#### External Contributions
* [#4093](https://github.com/pmd/pmd/pull/4093): \[apex] Summit-AST Apex module - Part 1 - [Edward Klimoshenko](https://github.com/eklimo) (@eklimo)
* [#4151](https://github.com/pmd/pmd/pull/4151): \[apex] Summit-AST Apex module - Part 2 - expression nodes - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4171](https://github.com/pmd/pmd/pull/4171): \[apex] Summit-AST Apex module - Part 3 - initializers - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4206](https://github.com/pmd/pmd/pull/4206): \[apex] Summit-AST Apex module - Part 4 - statements - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4219](https://github.com/pmd/pmd/pull/4219): \[apex] Summit-AST Apex module - Part 5 - annotations, triggers, misc. - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4242](https://github.com/pmd/pmd/pull/4242): \[apex] Merge 6.52 into experimental-apex-parser - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4251](https://github.com/pmd/pmd/pull/4251): \[apex] Summit-AST Apex module - Part 6 Passing testsuite - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4448](https://github.com/pmd/pmd/pull/4448): \[apex] Bump summit-ast to new release 2.1.0 (and remove workaround) - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4479](https://github.com/pmd/pmd/pull/4479): \[apex] Merge main (7.x) branch into experimental-apex-parser and fix tests - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4640](https://github.com/pmd/pmd/pull/4640): \[cli] Launch script fails if run via "bash pmd" - [Shai Bennathan](https://github.com/shai-bennathan) (@shai-bennathan)
* [#4673](https://github.com/pmd/pmd/pull/4673): \[javascript] CPD: Added support for decorator notation - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4677](https://github.com/pmd/pmd/pull/4677): \[apex] Add new rule: OperationWithHighCostInLoop - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#4698](https://github.com/pmd/pmd/pull/4698): \[swift] Add macro expansion support for swift 5.9 - [Richard B.](https://github.com/kenji21) (@kenji21)
* [#4706](https://github.com/pmd/pmd/pull/4706): \[java] DetachedTestCase should not report on abstract methods - [Debamoy Datta](https://github.com/Debamoy) (@Debamoy)
* [#4719](https://github.com/pmd/pmd/pull/4719): \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string - [ciufudean](https://github.com/ciufudean) (@ciufudean)
* [#4738](https://github.com/pmd/pmd/pull/4738): \[doc] Added reference to the PMD extension for bld - [Erik C. Thauvin](https://github.com/ethauvin) (@ethauvin)
* [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6 - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4750](https://github.com/pmd/pmd/pull/4750): \[core] Fix flaky SummaryHTMLRenderer - [219sansim](https://github.com/219sansim) (@219sansim)
* [#4754](https://github.com/pmd/pmd/pull/4754): \[java] EmptyControlStatementRule: Add allowCommentedBlocks property - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4759](https://github.com/pmd/pmd/pull/4759): \[java] fix: remove delimiter attribute from ruleset category/java/errorprone.xml - [Marcin Dąbrowski](https://github.com/marcindabrowski) (@marcindabrowski)
* [#4825](https://github.com/pmd/pmd/pull/4825): \[plsql] Fix ignored WITH clause for SELECT INTO statements - [Laurent Bovet](https://github.com/lbovet) (@lbovet)

### 🚀 Major Features and Enhancements

#### New official logo

The new official logo of PMD:

![New PMD Logo]({{ baseurl }}images/logo/pmd-logo-300px.png)

#### Revamped Java module

* Java grammar substantially refactored - more correct regarding the Java Language Specification (JLS)
* Built-in rules have been upgraded for the changed AST
* Rewritten type resolution framework and symbol table correctly implements the JLS
* AST exposes more semantic information (method calls, field accesses)

For more information, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#revamped-java).

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Revamped Command Line Interface

* unified and consistent Command Line Interface for both Linux/Unix and Windows across our different utilities
* single script `pmd` (`pmd.bat` for Windows) to launch the different utilities:
    * `pmd check` to run PMD rules and analyze a project
    * `pmd cpd` to run CPD (copy paste detector)
    * `pmd designer` to run the PMD Rule Designer
* progress bar support for `pmd check`
* shell completion

![Demo]({{ baseurl }}images/userdocs/pmd-demo.gif)

For more information, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Full Antlr support

* [Antlr](https://www.antlr.org/) based grammars can now be used to build full-fledged PMD rules.
* Previously, Antlr grammar could only be used for CPD
* New supported languages: Swift and Kotlin

For more information, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see
* [PMD Designer Changelog (7.0.0-rc1)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc1).
* [PMD Designer Changelog (7.0.0-rc4)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc4).
* [PMD Designer Changelog (7.0.0)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0).

#### New CPD report format cpdhtml-v2.xslt

Thanks to @mohan-chinnappan-n a new CPD report format has been added which features a data table.
It uses an XSLT stylesheet to convert CPD's XML format into HTML.

See [the example report]({{ baseurl }}report-examples/cpdhtml-v2.html).

### 🎉 Language Related Changes

Note that this is just a concise listing of the highlight.
For more information on the languages, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

#### New: Swift support

* use PMD to analyze Swift code with PMD rules
* limited support for Swift 5.9 (Macro Expansions)
* initially 4 built-in rules

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### New: Kotlin support

* use PMD to analyze Kotlin code with PMD rules
* Support for Kotlin 1.8 grammar
* initially 2 built-in rules

#### New: CPD support for TypeScript

Thanks to a contribution, CPD now supports the TypeScript language. It is shipped
with the rest of the JavaScript support in the module `pmd-javascript`.

Contributors: [Paul Guyot](https://github.com/pguyot) (@pguyot)

#### New: CPD support for Julia

Thanks to a contribution, CPD now supports the Julia language. It is shipped
in the new module `pmd-julia`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: CPD support for Coco

Thanks to a contribution, CPD now supports Coco, a modern programming language
designed specifically for building event-driven software. It is shipped in the new
module `pmd-coco`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: Java 21 Support

This release of PMD brings support for Java 21. There are the following new standard language features,
that are supported now:

* [JEP 440: Record Patterns](https://openjdk.org/jeps/440)
* [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)

PMD also supports the following preview language features:

* [JEP 430: String Templates (Preview)](https://openjdk.org/jeps/430)
* [JEP 443: Unnamed Patterns and Variables (Preview)](https://openjdk.org/jeps/443)
* [JEP 445: Unnamed Classes and Instance Main Methods (Preview)](https://openjdk.org/jeps/445)

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `21-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-21-preview ...

Note: Support for Java 19 preview language features have been removed. The version "19-preview" is no longer available.

#### Changed: HTML support

Support for HTML was introduced in PMD 6.55.0 as an experimental feature. With PMD 7.0.0 this
is now considered stable.

#### Changed: JavaScript support

* latest version supports ES6 and also some new constructs (see [Rhino](https://github.com/mozilla/rhino)])
* comments are retained

#### Changed: Language versions

* more predefined language versions for each supported language
* can be used to limit rule execution for specific versions only with `minimumLanguageVersion` and
  `maximumLanguageVersion` attributes.

#### Changed: CPP can now ignore identifiers in sequences (CPD)

* new command line option for CPD: `--ignore-sequences`.
* This option is used for CPP only: with the already existing option `--ignore-literal-sequences`, only
  literals were ignored. The new option additional ignores identifiers as well in sequences.
* See [PR #4470](https://github.com/pmd/pmd/pull/4470) for details.

#### Changed: Groovy Support (CPD)

* We now support parsing all Groovy features from Groovy 3 and 4.
* We now support [suppression](pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs.
* See [PR #4726](https://github.com/pmd/pmd/pull/4726) for details.

#### Changed: Apex Support: Replaced Jorje with fully open source front-end

When PMD added Apex support with version 5.5.0, it utilized the Apex Jorje library to parse Apex source
and generate an AST. This library is however a binary-blob provided as part of the
[Salesforce Extensions for VS Code](https://github.com/forcedotcom/salesforcedx-vscode), and it is closed-source.

This causes problems, if binary blobs are not allowed by e.g. a company-wide policy. In that case, the Jorje
library prevented that PMD Apex could be used at all.

Also having access to the source code, enhancements and modifications are easier to do.

Under the hood, we use two open source libraries instead:

* [apex-parser](https://github.com/nawforce/apex-parser) by [Kevin Jones](https://github.com/nawforce) (@nawforce)
  This project provides the grammar for a ANTLR based parser.
* [Summit-AST](https://github.com/google/summit-ast) by [Google](https://github.com/google) (@google)
  This project translates the ANTLR parse tree into an AST, that is similar to the AST Jorje provided.
  Note: This is not an official Google product.

Although the parsers is completely switched, there are only little known changes to the AST.
These are documented in the [Migration Guide for PMD 7: Apex AST]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html#apex-ast).

See [#3766](https://github.com/pmd/pmd/issues/3766) for details.

Contributors: [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google),
[Edward Klimoshenko](https://github.com/eklimo) (@eklimo)

#### Changed: Rule properties

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  {% jdoc core::properties.PropertyFactory %} to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

### 🌟 New and changed rules

#### New Rules

**Apex**
* {% rule apex/design/UnusedMethod %} finds unused methods in your code.
* {% rule apex/performance/OperationWithHighCostInLoop %} finds Schema class methods called in a loop, which is a
  potential performance issue.

**Java**
* {% rule java/codestyle/UnnecessaryBoxing %} reports boxing and unboxing conversions that may be made implicit.
* {% rule java/codestyle/UseExplicitTypes %} reports usages of `var` keyword, which was introduced with Java 10.

**Kotlin**
* {% rule kotlin/bestpractices/FunctionNameTooShort %}
* {% rule kotlin/errorprone/OverrideBothEqualsAndHashcode %}

**Swift**
* {% rule swift/bestpractices/ProhibitedInterfaceBuilder %}
* {% rule swift/bestpractices/UnavailableFunction %}
* {% rule swift/errorprone/ForceCast %}
* {% rule swift/errorprone/ForceTry %}

**XML**
* {% rule xml/bestpractices/MissingEncoding %} finds XML files without explicit encoding.

#### Changed Rules

**General changes**

* All statistical rules (like ExcessiveClassLength, ExcessiveParameterList) have been simplified and unified.
  The properties `topscore` and `sigma` have been removed. The property `minimum` is still there, however the type is not
  a decimal number anymore but has been changed to an integer. This affects rules in the languages Apex, Java, PLSQL
  and Velocity Template Language (vm):
    * Apex: {% rule apex/design/ExcessiveClassLength %}, {% rule apex/design/ExcessiveParameterList %},
      {% rule apex/design/ExcessivePublicCount %}, {% rule apex/design/NcssConstructorCount %},
      {% rule apex/design/NcssMethodCount %}, {% rule apex/design/NcssTypeCount %}
    * Java: {% rule java/design/ExcessiveImports %}, {% rule java/design/ExcessiveParameterList %},
      {% rule java/design/ExcessivePublicCount %}, {% rule java/design/SwitchDensity %}
    * PLSQL: {% rule plsql/design/ExcessiveMethodLength %}, {% rule plsql/design/ExcessiveObjectLength %},
      {% rule plsql/design/ExcessivePackageBodyLength %}, {% rule plsql/design/ExcessivePackageSpecificationLength %},
      {% rule plsql/design/ExcessiveParameterList %}, {% rule plsql/design/ExcessiveTypeLength %},
      {% rule plsql/design/NcssMethodCount %}, {% rule plsql/design/NcssObjectCount %},
      {% rule plsql/design/NPathComplexity %}
    * VM: {% rule vm/design/ExcessiveTemplateLength %}

* The general property `violationSuppressXPath` which is available for all rules to
  [suppress warnings]({{ baseurl }}pmd_userdocs_suppressing_warnings.html) now uses XPath version 3.1 by default.
  This version of the XPath language is mostly identical to XPath 2.0. In PMD 6, XPath 1.0 has been used.
  If you upgrade from PMD 6, you need to verify your `violationSuppressXPath` properties.

**Apex General changes**

* The properties `cc_categories`, `cc_remediation_points_multiplier`, `cc_block_highlighting` have been removed
  from all rules. These properties have been deprecated since PMD 6.13.0.
  See [issue #1648](https://github.com/pmd/pmd/issues/1648) for more details.

**Apex Codestyle**

* {% rule apex/codestyle/MethodNamingConventions %}: The deprecated rule property `skipTestMethodUnderscores` has
  been removed. It was actually deprecated since PMD 6.15.0, but was not mentioned in the release notes
  back then. Use the property `testPattern` instead to configure valid names for test methods.

**Java General changes**

* Violations reported on methods or classes previously reported the line range of the entire method
  or class. With PMD 7.0.0, the reported location is now just the identifier of the method or class.
  This affects various rules, e.g. {% rule java/design/CognitiveComplexity %}.

  The report location is controlled by the overrides of the method {% jdoc core::lang.ast.Node#getReportLocation() %}
  in different node types.

  See [issue #4439](https://github.com/pmd/pmd/issues/4439) and [issue #730](https://github.com/pmd/pmd/issues/730)
  for more details.

**Java Best Practices**

* {% rule java/bestpractices/ArrayIsStoredDirectly %}: Violations are now reported on the assignment and not
  anymore on the formal parameter. The reported line numbers will probably move.
* {% rule java/bestpractices/AvoidReassigningLoopVariables %}: This rule might not report anymore all
  reassignments of the control variable in for-loops when the property `forReassign` is set to `skip`.
  See [issue #4500](https://github.com/pmd/pmd/issues/4500) for more details.
* {% rule java/bestpractices/LooseCoupling %}: The rule has a new property to allow some types to be coupled
  to (`allowedTypes`).
* {% rule java/bestpractices/UnusedLocalVariable %}: This rule has some important false-negatives fixed
  and finds many more cases now. For details see issues [#2130](https://github.com/pmd/pmd/issues/2130),
  [#4516](https://github.com/pmd/pmd/issues/4516), and [#4517](https://github.com/pmd/pmd/issues/4517).

**Java Codestyle**

* {% rule java/codestyle/MethodNamingConventions %}: The property `checkNativeMethods` has been removed. The
  property was deprecated since PMD 6.3.0. Use the property `nativePattern` to control whether native methods
  should be considered or not.
* {% rule java/codestyle/ShortVariable %}: This rule now also reports short enum constant names.
* {% rule java/codestyle/UseDiamondOperator %}: The property `java7Compatibility` has been removed. The rule now
  handles Java 7 properly without a property.
* {% rule java/codestyle/UnnecessaryFullyQualifiedName %}: The rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved
  to be more precise.
* {% rule java/codestyle/UselessParentheses %}: The rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.
* {% rule java/codestyle/EmptyControlStatement %}: The rule has a new property to allow empty blocks when
  they contain a comment (`allowCommentedBlocks`).

**Java Design**

* {% rule java/design/CyclomaticComplexity %}: The property `reportLevel` has been removed. The property was
  deprecated since PMD 6.0.0. The report level can now be configured separated for classes and methods using
  `classReportLevel` and `methodReportLevel` instead.
* {% rule java/design/ImmutableField %}: The property `ignoredAnnotations` has been removed. The property was
  deprecated since PMD 6.52.0.
* {% rule java/design/LawOfDemeter %}: The rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.
* {% rule java/design/NPathComplexity %}: The property `minimum` has been removed. It was deprecated since PMD 6.0.0.
  Use the property `reportLevel` instead.
* {% rule java/design/SingularField %}: The properties `checkInnerClasses` and `disallowNotAssignment` have been removed.
  The rule is now more precise and will check these cases properly.
* {% rule java/design/UseUtilityClass %}: The property `ignoredAnnotations` has been removed.

**Java Documentation**

* {% rule java/documentation/CommentContent %}: The properties `caseSensitive` and `disallowedTerms` are removed. The
  new property `forbiddenRegex` can be used now to define the disallowed terms with a single regular
  expression.
* {% rule java/documentation/CommentRequired %}:
    * Overridden methods are now detected even without the `@Override`
      annotation. This is relevant for the property `methodWithOverrideCommentRequirement`.
      See also [pull request #3757](https://github.com/pmd/pmd/pull/3757).
    * Elements in annotation types are now detected as well. This might lead to an increased number of violations
      for missing public method comments.
    * The deprecated property `headerCommentRequirement` has been removed. Use the property `classCommentRequirement`
      instead.
* {% rule java/documentation/CommentSize %}: When determining the line-length of a comment, the leading comment
  prefix markers (e.g. `*` or `//`) are ignored and don't add up to the line-length.
  See also [pull request #4369](https://github.com/pmd/pmd/pull/4369).

**Java Error Prone**

* {% rule java/errorprone/AvoidDuplicateLiterals %}: The property `exceptionfile` has been removed. The property was
  deprecated since PMD 6.10.0. Use the property `exceptionList` instead.
* {% rule java/errorprone/DontImportSun %}: `sun.misc.Signal` is not special-cased anymore.
* {% rule java/errorprone/EmptyCatchBlock %}: `CloneNotSupportedException` and `InterruptedException` are not
  special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* {% rule java/errorprone/ImplicitSwitchFallThrough %}: Violations are now reported on the case statements
  rather than on the switch statements. This is more accurate but might result in more violations now.
* {% rule java/errorprone/NonSerializableClass %}: The deprecated property `prefix` has been removed
  without replacement. In a serializable class all fields have to be serializable regardless of the name.

#### Removed Rules

Many rules, that were previously deprecated have been finally removed.
See [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html) for the complete list.

### 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Also, there are some improvement and changes in different areas. For the detailed description
of the changes listed here, see [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

* Miscellaneous smaller changes and cleanups
* XPath 3.1 support for XPath-based rules
* Node stream API for AST traversal
* Metrics framework
* Testing framework
* Language Lifecycle and Language Properties
* Rule Properties
* New Programmatic API for CPD

### 💥 Compatibility and migration notes

A detailed documentation of required changes are available in the
[Migration Guide for PMD 7]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html).

See also [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

### 🐛 Fixed Issues

* miscellaneous
    * [#881](https://github.com/pmd/pmd/issues/881):   \[all] Breaking API changes for 7.0.0
    * [#896](https://github.com/pmd/pmd/issues/896):   \[all] Use slf4j
    * [#1431](https://github.com/pmd/pmd/pull/1431):   \[ui] Remove old GUI applications (designerold, bgastviewer)
    * [#1451](https://github.com/pmd/pmd/issues/1451): \[core] RulesetFactoryCompatibility stores the whole ruleset file in memory as a string
    * [#2496](https://github.com/pmd/pmd/issues/2496): Update PMD 7 Logo on landing page
    * [#2497](https://github.com/pmd/pmd/issues/2497): PMD 7 Logo page
    * [#2498](https://github.com/pmd/pmd/issues/2498): Update PMD 7 Logo in documentation
    * [#3797](https://github.com/pmd/pmd/issues/3797): \[all] Use JUnit5
    * [#4462](https://github.com/pmd/pmd/issues/4462): Provide Software Bill of Materials (SBOM)
    * [#4460](https://github.com/pmd/pmd/pull/4460):   Fix assembly-plugin warnings
    * [#4582](https://github.com/pmd/pmd/issues/4582): \[dist] Download link broken
    * [#4586](https://github.com/pmd/pmd/pull/4586):   Use explicit encoding in ruleset xml files
    * [#4642](https://github.com/pmd/pmd/issues/4642): Update regression tests with Java 21 language features
    * [#4691](https://github.com/pmd/pmd/issues/4691): \[CVEs] Critical and High CEVs reported on PMD and PMD dependencies
    * [#4699](https://github.com/pmd/pmd/pull/4699):   Make PMD buildable with java 21
    * [#4736](https://github.com/pmd/pmd/issues/4736): \[ci] Improve build procedure
    * [#4741](https://github.com/pmd/pmd/pull/4741):   Add pmd-compat6 module for maven-pmd-plugin
    * [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6
    * [#4776](https://github.com/pmd/pmd/issues/4776): \[ci] Upgrade to ruby 3
    * [#4796](https://github.com/pmd/pmd/pull/4796):   Remove deprecated and release rulesets
* ant
    * [#4080](https://github.com/pmd/pmd/issues/4080): \[ant] Split off Ant integration into a new submodule
* core
    * [#880](https://github.com/pmd/pmd/issues/880):   \[core] Make visitors generic
    * [#1027](https://github.com/pmd/pmd/issues/1027): \[core] Apply the new PropertyDescriptor&lt;Pattern&gt; type where applicable
    * [#1204](https://github.com/pmd/pmd/issues/1204): \[core] Allow numeric properties in XML to be within an unbounded range
    * [#1622](https://github.com/pmd/pmd/pull/1622):   \[core] NodeStream API
    * [#1687](https://github.com/pmd/pmd/issues/1687): \[core] Deprecate and Remove XPath 1.0 support
    * [#1785](https://github.com/pmd/pmd/issues/1785): \[core] Allow abstract node types to be valid rulechain visits
    * [#1825](https://github.com/pmd/pmd/pull/1825):   \[core] Support NoAttribute for XPath
    * [#2038](https://github.com/pmd/pmd/issues/2038): \[core] Remove DCD
    * [#2218](https://github.com/pmd/pmd/issues/2218): \[core] `isFindBoundary` should not be an attribute
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#2239](https://github.com/pmd/pmd/issues/2239): \[core] Merging Javacc build scripts
    * [#2500](https://github.com/pmd/pmd/issues/2500): \[core] Clarify API for ANTLR based languages
    * [#2518](https://github.com/pmd/pmd/issues/2518): \[core] Language properties
    * [#2602](https://github.com/pmd/pmd/issues/2602): \[core] Remove ParserOptions
    * [#2614](https://github.com/pmd/pmd/pull/2614):   \[core] Upgrade Saxon, add XPath 3.1, remove Jaxen
    * [#2696](https://github.com/pmd/pmd/pull/2696):   \[core] Remove DFA
    * [#2821](https://github.com/pmd/pmd/issues/2821): \[core] Rule processing error filenames are missing paths
    * [#2873](https://github.com/pmd/pmd/issues/2873): \[core] Utility classes in pmd 7
    * [#2885](https://github.com/pmd/pmd/issues/2885): \[core] Error recovery mode
    * [#3203](https://github.com/pmd/pmd/issues/3203): \[core] Replace RuleViolationFactory implementations with ViolationDecorator
    * [#3692](https://github.com/pmd/pmd/pull/3692):   \[core] Analysis listeners
    * [#3782](https://github.com/pmd/pmd/issues/3782): \[core] Language lifecycle
    * [#3815](https://github.com/pmd/pmd/issues/3815): \[core] Update Saxon HE to 10.7
    * [#3893](https://github.com/pmd/pmd/pull/3893):   \[core] Text documents
    * [#3902](https://github.com/pmd/pmd/issues/3902): \[core] Violation decorators
    * [#3903](https://github.com/pmd/pmd/issues/3903): \[core] Consolidate `n.s.pmd.reporting` package
    * [#3905](https://github.com/pmd/pmd/issues/3905): \[core] Stabilize tree export API
    * [#3917](https://github.com/pmd/pmd/issues/3917): \[core] Consolidate `n.s.pmd.lang.rule` package
    * [#3918](https://github.com/pmd/pmd/issues/3918): \[core] Make LanguageRegistry non static
    * [#3919](https://github.com/pmd/pmd/issues/3919): \[core] Merge CPD and PMD language
    * [#3922](https://github.com/pmd/pmd/pull/3922):   \[core] Better error reporting for the ruleset parser
    * [#4035](https://github.com/pmd/pmd/issues/4035): \[core] ConcurrentModificationException in DefaultRuleViolationFactory
    * [#4065](https://github.com/pmd/pmd/issues/4065): \[core] Rename TokenMgrError to LexException, Tokenizer to CpdLexer
    * [#4120](https://github.com/pmd/pmd/issues/4120): \[core] Explicitly name all language versions
    * [#4204](https://github.com/pmd/pmd/issues/4204): \[core] Provide a CpdAnalysis class as a programmatic entry point into CPD
    * [#4301](https://github.com/pmd/pmd/issues/4301): \[core] Remove deprecated property concrete classes
    * [#4302](https://github.com/pmd/pmd/issues/4302): \[core] Migrate Property Framework API to Java 8
    * [#4309](https://github.com/pmd/pmd/issues/4309): \[core] Cleanups in XPath area
    * [#4312](https://github.com/pmd/pmd/issues/4312): \[core] Remove unnecessary property `color` and system property `pmd.color` in `TextColorRenderer`
    * [#4313](https://github.com/pmd/pmd/issues/4313): \[core] Remove support for &lt;lang&gt;-&lt;ruleset&gt; hyphen notation for ruleset references
    * [#4314](https://github.com/pmd/pmd/issues/4314): \[core] Remove ruleset compatibility filter (RuleSetFactoryCompatibility) and CLI option `--no-ruleset-compatibility`
    * [#4323](https://github.com/pmd/pmd/issues/4323): \[core] Refactor CPD integration
    * [#4348](https://github.com/pmd/pmd/issues/4348): \[core] Consolidate @<!-- -->InternalApi classes
    * [#4349](https://github.com/pmd/pmd/issues/4349): \[core] Cleanup remaining experimental and deprecated API
    * [#4353](https://github.com/pmd/pmd/pull/4353):   \[core] Micro optimizations for Node API
    * [#4365](https://github.com/pmd/pmd/pull/4365):   \[core] Improve benchmarking
    * [#4397](https://github.com/pmd/pmd/pull/4397):   \[core] Refactor CPD
    * [#4378](https://github.com/pmd/pmd/issues/4378): \[core] Ruleset loading processes commented rules
    * [#4420](https://github.com/pmd/pmd/pull/4420):   \[core] Remove PMD.EOL
    * [#4425](https://github.com/pmd/pmd/pull/4425):   \[core] Replace TextFile::pathId
    * [#4454](https://github.com/pmd/pmd/issues/4454): \[core] "Unknown option: '-min'" but is referenced in documentation
    * [#4611](https://github.com/pmd/pmd/pull/4611):   \[core] Fix loading language properties from env vars
    * [#4621](https://github.com/pmd/pmd/issues/4621): \[core] Make `ClasspathClassLoader::getResource` child first
    * [#4674](https://github.com/pmd/pmd/issues/4674): \[core] WARNING: Illegal reflective access by org.codehaus.groovy.reflection.CachedClass
    * [#4694](https://github.com/pmd/pmd/pull/4694):   \[core] Fix line/col numbers in TokenMgrError
    * [#4717](https://github.com/pmd/pmd/issues/4717): \[core] XSLTRenderer doesn't close report file
    * [#4750](https://github.com/pmd/pmd/pull/4750):   \[core] Fix flaky SummaryHTMLRenderer
    * [#4782](https://github.com/pmd/pmd/pull/4782):   \[core] Avoid using getImage/@<!-- -->Image
* cli
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#3828](https://github.com/pmd/pmd/issues/3828): \[core] Progress reporting
    * [#4079](https://github.com/pmd/pmd/issues/4079): \[cli] Split off CLI implementation into a pmd-cli submodule
    * [#4423](https://github.com/pmd/pmd/pull/4423):   \[cli] Fix NPE when only `--file-list` is specified
    * [#4482](https://github.com/pmd/pmd/issues/4482): \[cli] pmd.bat can only be executed once
    * [#4484](https://github.com/pmd/pmd/issues/4484): \[cli] ast-dump with no properties produce an NPE
    * [#4594](https://github.com/pmd/pmd/pull/4594):   \[cli] Change completion generation to runtime
    * [#4685](https://github.com/pmd/pmd/pull/4685):   \[cli] Clarify CPD documentation, fix positional parameter handling
    * [#4723](https://github.com/pmd/pmd/issues/4723): \[cli] Launch fails for "bash pmd"
* doc
    * [#995](https://github.com/pmd/pmd/issues/995):   \[doc] Document API evolution principles as ADR
    * [#2501](https://github.com/pmd/pmd/issues/2501): \[doc] Verify ANTLR Documentation
    * [#2511](https://github.com/pmd/pmd/issues/2511): \[doc] Review guides for writing java/xpath rules for correctness with PMD 7
    * [#3175](https://github.com/pmd/pmd/issues/3175): \[doc] Document language module features
    * [#4294](https://github.com/pmd/pmd/issues/4294): \[doc] Migration Guide for upgrading PMD 6 ➡️ 7
    * [#4303](https://github.com/pmd/pmd/issues/4303): \[doc] Document new property framework
    * [#4308](https://github.com/pmd/pmd/issues/4308): \[doc] Document XPath API @<!-- ->NoAttribute and @<!-- -->DeprecatedAttribute
    * [#4319](https://github.com/pmd/pmd/issues/4319): \[doc] Document TypeRes API and Symbols API
    * [#4438](https://github.com/pmd/pmd/issues/4438): \[doc] Documentation links in VS Code are outdated
    * [#4521](https://github.com/pmd/pmd/issues/4521): \[doc] Website is not mobile friendly
    * [#4676](https://github.com/pmd/pmd/issues/4676): \[doc] Clarify how CPD `--ignore-literals` and `--ignore-identifiers` work
    * [#4659](https://github.com/pmd/pmd/pull/4659):   \[doc] Improve ant documentation
    * [#4669](https://github.com/pmd/pmd/pull/4669):   \[doc] Add bld PMD Extension to Tools / Integrations
    * [#4704](https://github.com/pmd/pmd/issues/4704): \[doc] Multivalued properties do not accept | as a separator
* testing
    * [#2435](https://github.com/pmd/pmd/issues/2435): \[test] Remove duplicated Dummy language module
    * [#4234](https://github.com/pmd/pmd/issues/4234): \[test] Tests that change the logging level do not work

Language specific fixes:

* apex
    * [#1937](https://github.com/pmd/pmd/issues/1937): \[apex] Apex should only have a single RootNode
    * [#1648](https://github.com/pmd/pmd/issues/1648): \[apex,vf] Remove CodeClimate dependency
    * [#1750](https://github.com/pmd/pmd/pull/1750):   \[apex] Remove apex statistical rules
    * [#2836](https://github.com/pmd/pmd/pull/2836):   \[apex] Remove Apex ProjectMirror
    * [#3766](https://github.com/pmd/pmd/issues/3766): \[apex] Replace Jorje with fully open source front-end
    * [#3973](https://github.com/pmd/pmd/issues/3973): \[apex] Update parser to support new 'as user' keywords (User Mode for Database Operations)
    * [#4427](https://github.com/pmd/pmd/issues/4427): \[apex] ApexBadCrypto test failing to detect inline code
    * [#4453](https://github.com/pmd/pmd/issues/4453): \[apex] \[7.0-rc1] Exception while initializing Apexlink (Index 34812 out of bounds for length 34812)
* apex-design
    * [#2667](https://github.com/pmd/pmd/issues/2667): \[apex] Integrate nawforce/ApexLink to build robust Unused rule
    * [#4509](https://github.com/pmd/pmd/issues/4509): \[apex] ExcessivePublicCount doesn't consider inner classes correctly
    * [#4596](https://github.com/pmd/pmd/issues/4596): \[apex] ExcessivePublicCount ignores properties
* apex-documentation
    * [#4774](https://github.com/pmd/pmd/issues/4774): \[apex] ApexDoc false-positive for the first method of an annotated Apex class
* apex-performance
    * [#4675](https://github.com/pmd/pmd/issues/4675): \[apex] New Rule: OperationWithHighCostInLoop
* apex-security
    * [#4646](https://github.com/pmd/pmd/issues/4646): \[apex] ApexSOQLInjection does not recognise SObjectType or SObjectField as safe variable types
* groovy
    * [#4726](https://github.com/pmd/pmd/pull/4726):   \[groovy] Support Groovy to 3 and 4 and CPD suppressions
* java
    * [#520](https://github.com/pmd/pmd/issues/520):   \[java] Allow `@SuppressWarnings` with constants instead of literals
    * [#864](https://github.com/pmd/pmd/issues/864):   \[java] Similar/duplicated implementations for determining FQCN
    * [#905](https://github.com/pmd/pmd/issues/905):   \[java] Add new node for anonymous class declaration
    * [#910](https://github.com/pmd/pmd/issues/910):   \[java] AST inconsistency between primitive and reference type arrays
    * [#997](https://github.com/pmd/pmd/issues/997):   \[java] Java8 parsing corner case with annotated array types
    * [#998](https://github.com/pmd/pmd/issues/998):   \[java] AST inconsistencies around FormalParameter
    * [#1019](https://github.com/pmd/pmd/issues/1019): \[java] Breaking Java Grammar changes for PMD 7.0.0
    * [#1124](https://github.com/pmd/pmd/issues/1124): \[java] ImmutableList implementation in the qname codebase
    * [#1128](https://github.com/pmd/pmd/issues/1128): \[java] Improve ASTLocalVariableDeclaration
    * [#1150](https://github.com/pmd/pmd/issues/1150): \[java] ClassOrInterfaceType AST improvements
    * [#1207](https://github.com/pmd/pmd/issues/1207): \[java] Resolve explicit types using FQCNs, without hitting the classloader
    * [#1307](https://github.com/pmd/pmd/issues/1307): \[java] AccessNode API changes
    * [#1367](https://github.com/pmd/pmd/issues/1367): \[java] Parsing error on annotated inner class
    * [#1661](https://github.com/pmd/pmd/issues/1661): \[java] About operator nodes
    * [#2366](https://github.com/pmd/pmd/pull/2366):   \[java] Remove qualified names
    * [#2819](https://github.com/pmd/pmd/issues/2819): \[java] GLB bugs in pmd 7
    * [#3642](https://github.com/pmd/pmd/issues/3642): \[java] Parse error on rare extra dimensions on method return type on annotation methods
    * [#3763](https://github.com/pmd/pmd/issues/3763): \[java] Ambiguous reference error in valid code
    * [#3749](https://github.com/pmd/pmd/issues/3749): \[java] Improve `isOverridden` in ASTMethodDeclaration
    * [#3750](https://github.com/pmd/pmd/issues/3750): \[java] Make symbol table support instanceof pattern bindings
    * [#3751](https://github.com/pmd/pmd/issues/3751): \[java] Rename some node types
    * [#3752](https://github.com/pmd/pmd/issues/3752): \[java] Expose annotations in symbol API
    * [#4237](https://github.com/pmd/pmd/pull/4237):   \[java] Cleanup handling of Java comments
    * [#4317](https://github.com/pmd/pmd/issues/4317): \[java] Some AST nodes should not be TypeNodes
    * [#4359](https://github.com/pmd/pmd/issues/4359): \[java] Type resolution fails with NPE when the scope is not a type declaration
    * [#4367](https://github.com/pmd/pmd/issues/4367): \[java] Move testrule TypeResTest into internal
    * [#4383](https://github.com/pmd/pmd/issues/4383): \[java] IllegalStateException: Object is not an array type!
    * [#4401](https://github.com/pmd/pmd/issues/4401): \[java] PMD 7 fails to build under Java 19
    * [#4405](https://github.com/pmd/pmd/issues/4405): \[java] Processing error with ArrayIndexOutOfBoundsException
    * [#4583](https://github.com/pmd/pmd/issues/4583): \[java] Support JDK 21 (LTS)
    * [#4628](https://github.com/pmd/pmd/pull/4628):   \[java] Support loading classes from java runtime images
    * [#4753](https://github.com/pmd/pmd/issues/4753): \[java] PMD crashes while using generics and wildcards
* java-bestpractices
    * [#342](https://github.com/pmd/pmd/issues/342):   \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
    * [#755](https://github.com/pmd/pmd/issues/755):   \[java] AccessorClassGeneration false positive for private constructors
    * [#770](https://github.com/pmd/pmd/issues/770):   \[java] UnusedPrivateMethod yields false positive for counter-variant arguments
    * [#807](https://github.com/pmd/pmd/issues/807):   \[java] AccessorMethodGeneration false positive with overloads
    * [#833](https://github.com/pmd/pmd/issues/833):   \[java] ForLoopCanBeForeach should consider iterating on this
    * [#1189](https://github.com/pmd/pmd/issues/1189): \[java] UnusedPrivateMethod false positive from inner class via external class
    * [#1205](https://github.com/pmd/pmd/issues/1205): \[java] Improve ConstantsInInterface message to mention alternatives
    * [#1212](https://github.com/pmd/pmd/issues/1212): \[java] Don't raise JUnitTestContainsTooManyAsserts on JUnit 5's assertAll
    * [#1422](https://github.com/pmd/pmd/issues/1422): \[java] JUnitTestsShouldIncludeAssert false positive with inherited @<!-- -->Rule field
    * [#1455](https://github.com/pmd/pmd/issues/1455): \[java] JUnitTestsShouldIncludeAssert: False positives for assert methods named "check" and "verify"
    * [#1563](https://github.com/pmd/pmd/issues/1563): \[java] ForLoopCanBeForeach false positive with method call using index variable
    * [#1565](https://github.com/pmd/pmd/issues/1565): \[java] JUnitAssertionsShouldIncludeMessage false positive with AssertJ
    * [#1747](https://github.com/pmd/pmd/issues/1747): \[java] PreserveStackTrace false-positive
    * [#1969](https://github.com/pmd/pmd/issues/1969): \[java] MissingOverride false-positive triggered by package-private method overwritten in another package by extending class
    * [#1998](https://github.com/pmd/pmd/issues/1998): \[java] AccessorClassGeneration false-negative: subclass calls private constructor
    * [#2130](https://github.com/pmd/pmd/issues/2130): \[java] UnusedLocalVariable: false-negative with array
    * [#2147](https://github.com/pmd/pmd/issues/2147): \[java] JUnitTestsShouldIncludeAssert - false positives with lambdas and static methods
    * [#2464](https://github.com/pmd/pmd/issues/2464): \[java] LooseCoupling must ignore class literals: ArrayList.class
    * [#2542](https://github.com/pmd/pmd/issues/2542): \[java] UseCollectionIsEmpty can not detect the case `foo.bar().size()`
    * [#2650](https://github.com/pmd/pmd/issues/2650): \[java] UseTryWithResources false positive when AutoCloseable helper used
    * [#2796](https://github.com/pmd/pmd/issues/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
    * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
    * [#2822](https://github.com/pmd/pmd/issues/2822): \[java] LooseCoupling rule: Extend to cover user defined implementations and interfaces
    * [#2843](https://github.com/pmd/pmd/pull/2843):   \[java] Fix UnusedAssignment FP with field accesses
    * [#2882](https://github.com/pmd/pmd/issues/2882): \[java] UseTryWithResources - false negative for explicit close
    * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
    * [#2890](https://github.com/pmd/pmd/issues/2890): \[java] UnusedPrivateMethod false positive with generics
    * [#2946](https://github.com/pmd/pmd/issues/2946): \[java] SwitchStmtsShouldHaveDefault false positive on enum inside enums
    * [#3672](https://github.com/pmd/pmd/pull/3672):   \[java] LooseCoupling - fix false positive with generics
    * [#3675](https://github.com/pmd/pmd/pull/3675):   \[java] MissingOverride - fix false positive with mixing type vars
    * [#3858](https://github.com/pmd/pmd/issues/3858): \[java] UseCollectionIsEmpty should infer local variable type from method invocation
    * [#4433](https://github.com/pmd/pmd/issues/4433): \[java] \[7.0-rc1] ReplaceHashtableWithMap on java.util.Properties
    * [#4492](https://github.com/pmd/pmd/issues/4492): \[java] GuardLogStatement gives false positive when argument is a Java method reference
    * [#4503](https://github.com/pmd/pmd/issues/4503): \[java] JUnitTestsShouldIncludeAssert: false negative with TestNG
    * [#4516](https://github.com/pmd/pmd/issues/4516): \[java] UnusedLocalVariable: false-negative with try-with-resources
    * [#4517](https://github.com/pmd/pmd/issues/4517): \[java] UnusedLocalVariable: false-negative with compound assignments
    * [#4518](https://github.com/pmd/pmd/issues/4518): \[java] UnusedLocalVariable: false-positive with multiple for-loop indices
    * [#4603](https://github.com/pmd/pmd/issues/4603): \[java] UnusedAssignment false positive in record compact constructor
    * [#4625](https://github.com/pmd/pmd/issues/4625): \[java] UnusedPrivateMethod false positive: Autoboxing into Number
    * [#4634](https://github.com/pmd/pmd/issues/4634): \[java] JUnit4TestShouldUseTestAnnotation false positive with TestNG
* java-codestyle
    * [#1208](https://github.com/pmd/pmd/issues/1208): \[java] PrematureDeclaration rule false-positive on variable declared to measure time
    * [#1429](https://github.com/pmd/pmd/issues/1429): \[java] PrematureDeclaration as result of method call (false positive)
    * [#1480](https://github.com/pmd/pmd/issues/1480): \[java] IdenticalCatchBranches false positive with return expressions
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2134](https://github.com/pmd/pmd/issues/2134): \[java] PreserveStackTrace not handling `Throwable.addSuppressed(...)`
    * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
    * [#2391](https://github.com/pmd/pmd/issues/2391): \[java] UseDiamondOperator FP when expected type and constructed type have a different parameterization
    * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support ParameterizedTest
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
    * [#2748](https://github.com/pmd/pmd/issues/2748): \[java] UnnecessaryCast false positive with unchecked cast
    * [#2847](https://github.com/pmd/pmd/issues/2847): \[java] New Rule: Use Explicit Types
    * [#2973](https://github.com/pmd/pmd/issues/2973): \[java] New rule: UnnecessaryBoxing
    * [#3195](https://github.com/pmd/pmd/pull/3195):   \[java] Improve rule UnnecessaryReturn to detect more cases
    * [#3218](https://github.com/pmd/pmd/pull/3218):   \[java] Generalize UnnecessaryCast to flag all unnecessary casts
    * [#3221](https://github.com/pmd/pmd/issues/3221): \[java] PrematureDeclaration false positive for unused variables
    * [#3238](https://github.com/pmd/pmd/issues/3238): \[java] Improve ExprContext, fix FNs of UnnecessaryCast
    * [#3500](https://github.com/pmd/pmd/pull/3500):   \[java] UnnecessaryBoxing - check for Integer.valueOf(String) calls
    * [#4239](https://github.com/pmd/pmd/issues/4239): \[java] UnnecessaryLocalBeforeReturn - false positive with catch clause
    * [#4268](https://github.com/pmd/pmd/issues/4268): \[java] CommentDefaultAccessModifier: false positive with TestNG annotations
    * [#4273](https://github.com/pmd/pmd/issues/4273): \[java] CommentDefaultAccessModifier ignoredAnnotations should include "org.junit.jupiter.api.extension.RegisterExtension" by default
    * [#4357](https://github.com/pmd/pmd/pull/4357):   \[java] Fix IllegalStateException in UseDiamondOperator rule
    * [#4432](https://github.com/pmd/pmd/issues/4432): \[java] \[7.0-rc1] UnnecessaryImport - Unused static import is being used
    * [#4455](https://github.com/pmd/pmd/issues/4455): \[java] FieldNamingConventions: false positive with lombok's @<!-- -->UtilityClass
    * [#4487](https://github.com/pmd/pmd/issues/4487): \[java] UnnecessaryConstructor: false-positive with @<!-- -->Inject and @<!-- -->Autowired
    * [#4511](https://github.com/pmd/pmd/issues/4511): \[java] LocalVariableCouldBeFinal shouldn't report unused variables
    * [#4512](https://github.com/pmd/pmd/issues/4512): \[java] MethodArgumentCouldBeFinal shouldn't report unused parameters
    * [#4557](https://github.com/pmd/pmd/issues/4557): \[java] UnnecessaryImport FP with static imports of overloaded methods
    * [#4578](https://github.com/pmd/pmd/issues/4578): \[java] CommentDefaultAccessModifier comment needs to be before annotation if present
    * [#4631](https://github.com/pmd/pmd/issues/4631): \[java] UnnecessaryFullyQualifiedName fails to recognize illegal self reference in enums
    * [#4645](https://github.com/pmd/pmd/issues/4645): \[java] CommentDefaultAccessModifier - False Positive with JUnit5's ParameterizedTest
    * [#4754](https://github.com/pmd/pmd/pull/4754):   \[java] EmptyControlStatementRule: Add allowCommentedBlocks property
    * [#4816](https://github.com/pmd/pmd/issues/4816): \[java] UnnecessaryImport false-positive on generic method call with on lambda
* java-design
    * [#174](https://github.com/pmd/pmd/issues/174):   \[java] SingularField false positive with switch in method that both assigns and reads field
    * [#1014](https://github.com/pmd/pmd/issues/1014): \[java] LawOfDemeter: False positive with lambda expression
    * [#1605](https://github.com/pmd/pmd/issues/1605): \[java] LawOfDemeter: False positive for standard UTF-8 charset name
    * [#2160](https://github.com/pmd/pmd/issues/2160): \[java] Issues with Law of Demeter
    * [#2175](https://github.com/pmd/pmd/issues/2175): \[java] LawOfDemeter: False positive for chained methods with generic method call
    * [#2179](https://github.com/pmd/pmd/issues/2179): \[java] LawOfDemeter: False positive with static property access - should treat class-level property as global object, not dot-accessed property
    * [#2180](https://github.com/pmd/pmd/issues/2180): \[java] LawOfDemeter: False positive with Thread and ThreadLocalRandom
    * [#2182](https://github.com/pmd/pmd/issues/2182): \[java] LawOfDemeter: False positive with package-private access
    * [#2188](https://github.com/pmd/pmd/issues/2188): \[java] LawOfDemeter: False positive with fields assigned to local vars
    * [#2536](https://github.com/pmd/pmd/issues/2536): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal can't detect inner class
    * [#3668](https://github.com/pmd/pmd/pull/3668):   \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - fix FP with inner private classes
    * [#3754](https://github.com/pmd/pmd/issues/3754): \[java] SingularField false positive with read in while condition
    * [#3786](https://github.com/pmd/pmd/issues/3786): \[java] SimplifyBooleanReturns should consider operator precedence
    * [#3840](https://github.com/pmd/pmd/issues/3840): \[java] LawOfDemeter disallows method call on locally created object
    * [#4238](https://github.com/pmd/pmd/pull/4238):   \[java] Make LawOfDemeter not use the rulechain
    * [#4254](https://github.com/pmd/pmd/issues/4254): \[java] ImmutableField - false positive with Lombok @<!-- -->Setter
    * [#4434](https://github.com/pmd/pmd/issues/4434): \[java] \[7.0-rc1] ExceptionAsFlowControl when simply propagating
    * [#4456](https://github.com/pmd/pmd/issues/4456): \[java] FinalFieldCouldBeStatic: false positive with lombok's @<!-- -->UtilityClass
    * [#4477](https://github.com/pmd/pmd/issues/4477): \[java] SignatureDeclareThrowsException: false-positive with TestNG annotations
    * [#4490](https://github.com/pmd/pmd/issues/4490): \[java] ImmutableField - false negative with Lombok @<!-- -->Getter
    * [#4549](https://github.com/pmd/pmd/pull/4549):   \[java] Make LawOfDemeter results deterministic
* java-documentation
    * [#4369](https://github.com/pmd/pmd/pull/4369):   \[java] Improve CommentSize
    * [#4416](https://github.com/pmd/pmd/pull/4416):   \[java] Fix reported line number in CommentContentRule
* java-errorprone
    * [#659](https://github.com/pmd/pmd/issues/659):   \[java] MissingBreakInSwitch - last default case does not contain a break
    * [#718](https://github.com/pmd/pmd/issues/718):   \[java] BrokenNullCheck false positive with parameter/field confusion
    * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
    * [#1669](https://github.com/pmd/pmd/issues/1669): \[java] NullAssignment - FP with ternay and null as constructor argument
    * [#1831](https://github.com/pmd/pmd/issues/1831): \[java] DetachedTestCase reports abstract methods
    * [#1899](https://github.com/pmd/pmd/issues/1899): \[java] Recognize @<!-- -->SuppressWanings("fallthrough") for MissingBreakInSwitch
    * [#2320](https://github.com/pmd/pmd/issues/2320): \[java] NullAssignment - FP with ternary and null as method argument
    * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case `new BigDecimal(Expression)`
    * [#2579](https://github.com/pmd/pmd/issues/2579): \[java] MissingBreakInSwitch detects the lack of break in the last case
    * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
    * [#2893](https://github.com/pmd/pmd/issues/2893): \[java] Remove special cases from rule EmptyCatchBlock
    * [#2894](https://github.com/pmd/pmd/issues/2894): \[java] Improve MissingBreakInSwitch
    * [#3071](https://github.com/pmd/pmd/issues/3071): \[java] BrokenNullCheck FP with PMD 6.30.0
    * [#3087](https://github.com/pmd/pmd/issues/3087): \[java] UnnecessaryBooleanAssertion overlaps with SimplifiableTestAssertion
    * [#3100](https://github.com/pmd/pmd/issues/3100): \[java] UseCorrectExceptionLogging FP in 6.31.0
    * [#3173](https://github.com/pmd/pmd/issues/3173): \[java] UseProperClassLoader false positive
    * [#3351](https://github.com/pmd/pmd/issues/3351): \[java] ConstructorCallsOverridableMethod ignores abstract methods
    * [#3400](https://github.com/pmd/pmd/issues/3400): \[java] AvoidUsingOctalValues FN with underscores
    * [#3843](https://github.com/pmd/pmd/issues/3843): \[java] UseEqualsToCompareStrings should consider return type
    * [#4063](https://github.com/pmd/pmd/issues/4063): \[java] AvoidBranchingStatementAsLastInLoop: False-negative about try/finally block
    * [#4356](https://github.com/pmd/pmd/pull/4356):   \[java] Fix NPE in CloseResourceRule
    * [#4449](https://github.com/pmd/pmd/issues/4449): \[java] AvoidAccessibilityAlteration: Possible false positive in AvoidAccessibilityAlteration rule when using Lambda expression
    * [#4457](https://github.com/pmd/pmd/issues/4457): \[java] OverrideBothEqualsAndHashcode: false negative with anonymous classes
    * [#4493](https://github.com/pmd/pmd/issues/4493): \[java] MissingStaticMethodInNonInstantiatableClass: false-positive about @<!-- -->Inject
    * [#4505](https://github.com/pmd/pmd/issues/4505): \[java] ImplicitSwitchFallThrough NPE in PMD 7.0.0-rc1
    * [#4510](https://github.com/pmd/pmd/issues/4510): \[java] ConstructorCallsOverridableMethod: false positive with lombok's @<!-- -->Value
    * [#4513](https://github.com/pmd/pmd/issues/4513): \[java] UselessOperationOnImmutable various false negatives with String
    * [#4514](https://github.com/pmd/pmd/issues/4514): \[java] AvoidLiteralsInIfCondition false positive and negative for String literals when ignoreExpressions=true
    * [#4546](https://github.com/pmd/pmd/issues/4546): \[java] OverrideBothEqualsAndHashCode ignores records
    * [#4719](https://github.com/pmd/pmd/pull/4719):   \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string
* java-multithreading
    * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
    * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
    * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
    * [#4483](https://github.com/pmd/pmd/issues/4483): \[java] NonThreadSafeSingleton false positive with double-checked locking
* java-performance
    * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
    * [#2587](https://github.com/pmd/pmd/issues/2587): \[java] AvoidArrayLoops could also check for list copy through iterated List.add()
    * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ
    * [#3486](https://github.com/pmd/pmd/pull/3486):   \[java] InsufficientStringBufferDeclaration: Fix NPE
    * [#3848](https://github.com/pmd/pmd/issues/3848): \[java] StringInstantiation: false negative when using method result
    * [#4070](https://github.com/pmd/pmd/issues/4070): \[java] A false positive about the rule RedundantFieldInitializer
    * [#4458](https://github.com/pmd/pmd/issues/4458): \[java] RedundantFieldInitializer: false positive with lombok's @<!-- -->Value
* javascript
    * [#4673](https://github.com/pmd/pmd/pull/4673):   \[javascript] CPD: Added support for decorator notation
* kotlin
    * [#419](https://github.com/pmd/pmd/issues/419):   \[kotlin] Add support for Kotlin
    * [#4389](https://github.com/pmd/pmd/pull/4389):   \[kotlin] Update grammar to version 1.8
* plsql
    * [#4820](https://github.com/pmd/pmd/issues/4820): \[plsql] WITH clause is ignored for SELECT INTO statements
* swift
    * [#1877](https://github.com/pmd/pmd/pull/1877):   \[swift] Feature/swift rules
    * [#1882](https://github.com/pmd/pmd/pull/1882):   \[swift] UnavailableFunction Swift rule
    * [#4697](https://github.com/pmd/pmd/issues/4697): \[swift] Support Swift 5.9 features (mainly macros expansion expressions)
* xml
    * [#1800](https://github.com/pmd/pmd/pull/1800):   \[xml] Unimplement org.w3c.dom.Node from the XmlNodeWrapper
* xml-bestpractices
    * [#4592](https://github.com/pmd/pmd/pull/4592):   \[xml] Add MissingEncoding rule

### ✨ External Contributions

* [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1881](https://github.com/pmd/pmd/pull/1881): \[doc] Add ANTLR documentation - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca) (@tomidelucca)
* [#2830](https://github.com/pmd/pmd/pull/2830): \[apex] Apexlink POC - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#3866](https://github.com/pmd/pmd/pull/3866): \[core] Add CLI Progress Bar - [@JerritEic](https://github.com/JerritEic) (@JerritEic)
* [#4093](https://github.com/pmd/pmd/pull/4093): \[apex] Summit-AST Apex module - Part 1 - [Edward Klimoshenko](https://github.com/eklimo) (@eklimo)
* [#4151](https://github.com/pmd/pmd/pull/4151): \[apex] Summit-AST Apex module - Part 2 - expression nodes - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4171](https://github.com/pmd/pmd/pull/4171): \[apex] Summit-AST Apex module - Part 3 - initializers - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4206](https://github.com/pmd/pmd/pull/4206): \[apex] Summit-AST Apex module - Part 4 - statements - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4219](https://github.com/pmd/pmd/pull/4219): \[apex] Summit-AST Apex module - Part 5 - annotations, triggers, misc. - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4242](https://github.com/pmd/pmd/pull/4242): \[apex] Merge 6.52 into experimental-apex-parser - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4251](https://github.com/pmd/pmd/pull/4251): \[apex] Summit-AST Apex module - Part 6 Passing testsuite - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4402](https://github.com/pmd/pmd/pull/4402): \[javascript] CPD: add support for Typescript using antlr4 grammar - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4403](https://github.com/pmd/pmd/pull/4403): \[julia] CPD: Add support for Julia code duplication  - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4412](https://github.com/pmd/pmd/pull/4412): \[doc] Added new error msg to ConstantsInInterface - [David Ljunggren](https://github.com/dague1) (@dague1)
* [#4426](https://github.com/pmd/pmd/pull/4426): \[cpd] New XML to HTML XLST report format for PMD CPD - [mohan-chinnappan-n](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)
* [#4428](https://github.com/pmd/pmd/pull/4428): \[apex] ApexBadCrypto bug fix for #4427 - inline detection of hard coded values - [Steven Stearns](https://github.com/sfdcsteve) (@sfdcsteve)
* [#4431](https://github.com/pmd/pmd/pull/4431): \[coco] CPD: Coco support for code duplication detection - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4444](https://github.com/pmd/pmd/pull/4444): \[java] CommentDefaultAccessModifier - ignore org.junit.jupiter.api.extension.RegisterExtension by default - [Nirvik Patel](https://github.com/nirvikpatel) (@nirvikpatel)
* [#4448](https://github.com/pmd/pmd/pull/4448): \[apex] Bump summit-ast to new release 2.1.0 (and remove workaround) - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4450](https://github.com/pmd/pmd/pull/4450): \[java] Fix #4449 AvoidAccessibilityAlteration: Correctly handle Lambda expressions in PrivilegedAction scenarios - [Seren](https://github.com/mohui1999) (@mohui1999)
* [#4452](https://github.com/pmd/pmd/pull/4452): \[doc] Update PMD_APEX_ROOT_DIRECTORY documentation reference - [nwcm](https://github.com/nwcm) (@nwcm)
* [#4470](https://github.com/pmd/pmd/pull/4470): \[cpp] CPD: Added strings as literal and ignore identifiers in sequences - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4474](https://github.com/pmd/pmd/pull/4474): \[java] ImmutableField: False positive with lombok (fixes #4254) - [Pim van der Loos](https://github.com/PimvanderLoos) (@PimvanderLoos)
* [#4479](https://github.com/pmd/pmd/pull/4479): \[apex] Merge main (7.x) branch into experimental-apex-parser and fix tests - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4488](https://github.com/pmd/pmd/pull/4488): \[java] Fix #4477: A false-positive about SignatureDeclareThrowsException - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4494](https://github.com/pmd/pmd/pull/4494): \[java] Fix #4487: A false-positive about UnnecessaryConstructor and @<!-- -->Inject and @<!-- -->Autowired - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4495](https://github.com/pmd/pmd/pull/4495): \[java] Fix #4493: false-positive about MissingStaticMethodInNonInstantiatableClass and @<!-- -->Inject - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4507](https://github.com/pmd/pmd/pull/4507): \[java] Fix #4503: A false negative about JUnitTestsShouldIncludeAssert and testng - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4520](https://github.com/pmd/pmd/pull/4520): \[doc] Fix typo: missing closing quotation mark after CPD-END - [João Dinis Ferreira](https://github.com/joaodinissf) (@joaodinissf)
* [#4528](https://github.com/pmd/pmd/pull/4528): \[apex] Update to apexlink - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#4533](https://github.com/pmd/pmd/pull/4533): \[java] Fix #4063: False-negative about try/catch block in Loop - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4536](https://github.com/pmd/pmd/pull/4536): \[java] Fix #4268: CommentDefaultAccessModifier - false positive with TestNG's @<!-- -->Test annotation - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4537](https://github.com/pmd/pmd/pull/4537): \[java] Fix #4455: A false positive about FieldNamingConventions and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4538](https://github.com/pmd/pmd/pull/4538): \[java] Fix #4456: A false positive about FinalFieldCouldBeStatic and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4540](https://github.com/pmd/pmd/pull/4540): \[java] Fix #4457: false negative about OverrideBothEqualsAndHashcode - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4541](https://github.com/pmd/pmd/pull/4541): \[java] Fix #4458: A false positive about RedundantFieldInitializer and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4542](https://github.com/pmd/pmd/pull/4542): \[java] Fix #4510: A false positive about ConstructorCallsOverridableMethod and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4553](https://github.com/pmd/pmd/pull/4553): \[java] Fix #4492: GuardLogStatement gives false positive when argument is a Java method reference - [Anastasiia Koba](https://github.com/anastasiia-koba) (@anastasiia-koba)
* [#4637](https://github.com/pmd/pmd/pull/4637): \[java] fix #4634 - JUnit4TestShouldUseTestAnnotation false positive with TestNG - [Krystian Dabrowski](https://github.com/krdabrowski) (@krdabrowski)
* [#4640](https://github.com/pmd/pmd/pull/4640): \[cli] Launch script fails if run via "bash pmd" - [Shai Bennathan](https://github.com/shai-bennathan) (@shai-bennathan)
* [#4649](https://github.com/pmd/pmd/pull/4649): \[apex] Add SObjectType and SObjectField to list of injectable SOQL variable types - [Richard Corfield](https://github.com/rcorfieldffdc) (@rcorfieldffdc)
* [#4651](https://github.com/pmd/pmd/pull/4651): \[doc] Add "Tencent Cloud Code Analysis" in Tools / Integrations - [yale](https://github.com/cyw3) (@cyw3)
* [#4664](https://github.com/pmd/pmd/pull/4664): \[cli] CPD: Fix NPE when only `--file-list` is specified - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4665](https://github.com/pmd/pmd/pull/4665): \[java] Doc: Fix references AutoClosable -> AutoCloseable - [Andrey Bozhko](https://github.com/AndreyBozhko) (@AndreyBozhko)
* [#4673](https://github.com/pmd/pmd/pull/4673): \[javascript] CPD: Added support for decorator notation - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4677](https://github.com/pmd/pmd/pull/4677): \[apex] Add new rule: OperationWithHighCostInLoop - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#4698](https://github.com/pmd/pmd/pull/4698): \[swift] Add macro expansion support for swift 5.9 - [Richard B.](https://github.com/kenji21) (@kenji21)
* [#4706](https://github.com/pmd/pmd/pull/4706): \[java] DetachedTestCase should not report on abstract methods - [Debamoy Datta](https://github.com/Debamoy) (@Debamoy)
* [#4719](https://github.com/pmd/pmd/pull/4719): \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string - [ciufudean](https://github.com/ciufudean) (@ciufudean)
* [#4738](https://github.com/pmd/pmd/pull/4738): \[doc] Added reference to the PMD extension for bld - [Erik C. Thauvin](https://github.com/ethauvin) (@ethauvin)
* [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6 - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4750](https://github.com/pmd/pmd/pull/4750): \[core] Fix flaky SummaryHTMLRenderer - [219sansim](https://github.com/219sansim) (@219sansim)
* [#4754](https://github.com/pmd/pmd/pull/4754): \[java] EmptyControlStatementRule: Add allowCommentedBlocks property - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4759](https://github.com/pmd/pmd/pull/4759): \[java] fix: remove delimiter attribute from ruleset category/java/errorprone.xml - [Marcin Dąbrowski](https://github.com/marcindabrowski) (@marcindabrowski)
* [#4825](https://github.com/pmd/pmd/pull/4825): \[plsql] Fix ignored WITH clause for SELECT INTO statements - [Laurent Bovet](https://github.com/lbovet) (@lbovet)

### 📈 Stats
* 5007 commits
* 658 closed tickets & PRs
* Days since last release: 122

{% endtocmaker %}
