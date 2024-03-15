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

🎉 After a long time, we're excited to bring you now the next major version of PMD! 🎉

Since this is a big release, we provide here only a concise version of the release notes. We prepared a separate
page with the full [Detailed Release Notes for PMD 7.0.0]({{ baseurl }}pmd_release_notes_pmd7.html).

🤝🙏 Many thanks to all users and contributors who were testing the release candidates and
provided feedback and/or PRs!

✨ PMD 7...

* ...has a new logo
* ...analyzes Java 21 and Java 22 projects with even better type resolution and symbol table support
* ...analyzes Kotlin and Swift
* ...analyzes Apex with a new parser
* ...finds duplicated code in Coco, Julia, TypeScript
* ...ships 11 new rules and tons of improvements for existing rules
* ...provides a new CLI interface with progress bar
* ...supports Antlr based languages
* ...and many more enhancements

💥 Note: Since PMD 7 is a major release, it is not a drop-in replacement for PMD 6.55.0.
A detailed documentation of required changes are available in the [Migration Guide for PMD 7]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html).

{% if is_release_notes_processor %}
<details>
<summary markdown="span">Expand to see Release Notes
</summary>
{% endif %}

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

Note: Once the default version of PMD is upgraded to PMD7 in maven-pmd-plugin
(see [MPMD-379](https://issues.apache.org/jira/projects/MPMD/issues/MPMD-379)), this
compatibility module is no longer needed. The module pmd-compat6 might not be maintained then
any further, hence it is already declared as deprecated.

No guarantee is given, that the (deprecated) module pmd-compat6 is being maintained over the
whole lifetime of PMD 7.

##### Java 22 Support

This release of PMD brings support for Java 22. There are the following new standard language features,
that are supported now:

* [JEP 456: Unnamed Variables & Patterns](https://openjdk.org/jeps/456)

PMD also supports the following preview language features:

* [JEP 447: Statements before super(...) (Preview)](https://openjdk.org/jeps/447)
* [JEP 459: String Templates (Second Preview)](https://openjdk.org/jeps/459)
* [JEP 463: Implicitly Declared Classes and Instance Main Methods (Second Preview)](https://openjdk.org/jeps/463)

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `22-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-22-preview ...

Note: Support for Java 20 preview language features have been removed. The version "20-preview" is no longer available.

##### Swift Support

* limited support for Swift 5.9 (Macro Expansions)

##### Groovy Support (CPD)

* We now support parsing all Groovy features from Groovy 3 and 4.
* We now support [suppression](pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs.
* See [PR #4726](https://github.com/pmd/pmd/pull/4726) for details.

##### Updated PMD Designer

This PMD release ships a new version of the pmd-designer. The designer artifact has been
renamed from "pmd-ui" to "pmd-designer". While the designer still works with Java 8, the
recommended Java Runtime is Java 11 (or later) with OpenJFX 17 (or later).

For the detailed changes, see [PMD Designer Changelog (7.0.0)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0).

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

Although the parser is completely switched, there are only little known changes to the AST.
These are documented in the [Migration Guide for PMD 7: Apex AST]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html#apex-ast).
With the new Apex parser, the new language constructs like User Mode Database Operations
can be parsed now. PMD should be able to parse Apex code up to version 59.0 (Winter '23).

See [#3766](https://github.com/pmd/pmd/issues/3766) for details.

Contributors: [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google),
  [Edward Klimoshenko](https://github.com/eklimo) (@eklimo)

##### Changed: Visualforce

There was an inconsistency between the naming of the maven module and the language id. The language id
used the abbreviation "vf", while the maven module used the longer name "visualforce". This has been
solved by renaming the language module to its full name "visualforce". The java packages have
been renamed as well.

If you import rules, you also need to adjust the paths, e.g.

* `category/vf/security.xml` ➡️ `category/visualforce/security.xml`

##### Changed: HTML support

Support for HTML was introduced in PMD 6.55.0 as an experimental feature. With PMD 7.0.0 this
is now considered stable.

##### Changed: Kotlin support

Support for Kotlin was introduced with PMD 7.0.0-rc1 as an experimental feature. With PMD 7.0.0 this
is now considered stable.

##### Changed: Velocity Template Language (VTL)

The module was named just "vm" which was not a good name. Its module name, language id and
package names have been renamed to "velocity".

If you import rules, you also need to adjust the paths, e.g.

* `category/vm/...` ➡️ `category/velocity/...`

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

**Renamed Rulesets**

* `category/vf/security.xml` ➡️ `category/visualforce/security.xml`
* `category/vm/bestpractices.xml` ➡️ `category/velocity/bestpractices.xml`
* `category/vm/design.xml` ➡️ `category/velocity/design.xml`
* `category/vm/errorprone.xml` ➡️ `category/velocity/errorprone.xml`

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

<details>
<summary markdown="span">List of deprecated rulesets
</summary>

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

</details>


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
  * [#4704](https://github.com/pmd/pmd/issues/4704): \[doc] Multivalued properties do not accept \| as a separator
* miscellaneous
  * [#4699](https://github.com/pmd/pmd/pull/4699):   Make PMD buildable with java 21
  * [#4586](https://github.com/pmd/pmd/pull/4586):   Use explicit encoding in ruleset xml files
  * [#4642](https://github.com/pmd/pmd/issues/4642): Update regression tests with Java 21 language features
  * [#4736](https://github.com/pmd/pmd/issues/4736): \[ci] Improve build procedure
  * [#4741](https://github.com/pmd/pmd/pull/4741):   Add pmd-compat6 module for maven-pmd-plugin
  * [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6
  * [#4776](https://github.com/pmd/pmd/issues/4776): \[ci] Upgrade to ruby 3
  * [#4796](https://github.com/pmd/pmd/pull/4796):   Remove deprecated and release rulesets
  * [#4823](https://github.com/pmd/pmd/pull/4823):   Update to use renamed pmd-designer
  * [#4827](https://github.com/pmd/pmd/pull/4827):   \[compat6] Support config errors and cpd for csharp
  * [#4830](https://github.com/pmd/pmd/issues/4830): Consolidate packages in each maven module
* apex
  * [#3766](https://github.com/pmd/pmd/issues/3766): \[apex] Replace Jorje with fully open source front-end
* apex-bestpractices
  * [#4556](https://github.com/pmd/pmd/issues/4556): \[apex] UnusedLocalVariable flags for variables which are using in SOQL/SOSL binds
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
  * [#4757](https://github.com/pmd/pmd/issues/4757): \[java] Intermittent NPEs while analyzing Java code
  * [#4794](https://github.com/pmd/pmd/issues/4794): \[java] Support JDK 22
* java-bestpractices
  * [#4603](https://github.com/pmd/pmd/issues/4603): \[java] UnusedAssignment false positive in record compact constructor
  * [#4625](https://github.com/pmd/pmd/issues/4625): \[java] UnusedPrivateMethod false positive: Autoboxing into Number
  * [#4817](https://github.com/pmd/pmd/issues/4817): \[java] UnusedPrivateMethod false-positive used in lambda
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
  * [#932](https://github.com/pmd/pmd/issues/932):   \[java] SingletonClassReturningNewInstance false positive with double assignment
  * [#1831](https://github.com/pmd/pmd/issues/1831): \[java] DetachedTestCase reports abstract methods
  * [#4719](https://github.com/pmd/pmd/pull/4719):   \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string
* javascript
  * [#2305](https://github.com/pmd/pmd/issues/2305): \[javascript] UnnecessaryBlock - false positives with destructuring assignments
  * [#4673](https://github.com/pmd/pmd/pull/4673):   \[javascript] CPD: Added support for decorator notation
* plsql
  * [#4820](https://github.com/pmd/pmd/issues/4820): \[plsql] WITH clause is ignored for SELECT INTO statements
* swift
  * [#4697](https://github.com/pmd/pmd/issues/4697): \[swift] Support Swift 5.9 features (mainly macros expansion expressions)
* xml-bestpractices
  * [#4592](https://github.com/pmd/pmd/pull/4592):   \[xml] Add MissingEncoding rule

#### API Changes

See [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#700).

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
* [#4562](https://github.com/pmd/pmd/pull/4562): \[apex] Fixes #4556 - Update Apex bind regex match for all possible combinations - [nwcm](https://github.com/nwcm) (@nwcm)
* [#4640](https://github.com/pmd/pmd/pull/4640): \[cli] Launch script fails if run via "bash pmd" - [Shai Bennathan](https://github.com/shai-bennathan) (@shai-bennathan)
* [#4673](https://github.com/pmd/pmd/pull/4673): \[javascript] CPD: Added support for decorator notation - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4677](https://github.com/pmd/pmd/pull/4677): \[apex] Add new rule: OperationWithHighCostInLoop - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#4698](https://github.com/pmd/pmd/pull/4698): \[swift] Add macro expansion support for swift 5.9 - [Richard B.](https://github.com/kenji21) (@kenji21)
* [#4706](https://github.com/pmd/pmd/pull/4706): \[java] DetachedTestCase should not report on abstract methods - [Debamoy Datta](https://github.com/Debamoy) (@Debamoy)
* [#4719](https://github.com/pmd/pmd/pull/4719): \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string - [ciufudean](https://github.com/ciufudean) (@ciufudean)
* [#4738](https://github.com/pmd/pmd/pull/4738): \[doc] Added reference to the PMD extension for bld - [Erik C. Thauvin](https://github.com/ethauvin) (@ethauvin)
* [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6 - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4750](https://github.com/pmd/pmd/pull/4750): \[core] Fix flaky SummaryHTMLRenderer - [219sansim](https://github.com/219sansim) (@219sansim)
* [#4752](https://github.com/pmd/pmd/pull/4752): \[core] Fix flaky LatticeRelationTest - [219sansim](https://github.com/219sansim) (@219sansim)
* [#4754](https://github.com/pmd/pmd/pull/4754): \[java] EmptyControlStatementRule: Add allowCommentedBlocks property - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4759](https://github.com/pmd/pmd/pull/4759): \[java] fix: remove delimiter attribute from ruleset category/java/errorprone.xml - [Marcin Dąbrowski](https://github.com/marcindabrowski) (@marcindabrowski)
* [#4825](https://github.com/pmd/pmd/pull/4825): \[plsql] Fix ignored WITH clause for SELECT INTO statements - [Laurent Bovet](https://github.com/lbovet) (@lbovet)
* [#4857](https://github.com/pmd/pmd/pull/4857): \[javascript] Fix UnnecessaryBlock issues with empty statements - [Oleksandr Shvets](https://github.com/oleksandr-shvets) (@oleksandr-shvets)

### 🚀 Major Features and Enhancements

#### New official logo

The new official logo of PMD:

![New PMD Logo]({{ baseurl }}images/logo/pmd-logo-300px.png)

For more information, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#new-official-logo).

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

* Unified and consistent Command Line Interface for both Linux/Unix and Windows across our different utilities
* Single script `pmd` (`pmd.bat` for Windows) to launch the different utilities:
    * `pmd check` to run PMD rules and analyze a project
    * `pmd cpd` to run CPD (copy paste detector)
    * `pmd designer` to run the PMD Rule Designer
* Progress bar support for `pmd check`
* Shell completion

![Demo]({{ baseurl }}images/userdocs/pmd-demo.gif)

For more information, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#revamped-command-line-interface).

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Full Antlr support

* [Antlr](https://www.antlr.org/) based grammars can now be used to build full-fledged PMD rules.
* Previously, Antlr grammar could only be used for CPD
* New supported languages: Swift and Kotlin

For more information, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#full-antlr-support).

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer. The designer artifact has been
renamed from "pmd-ui" to "pmd-designer". While the designer still works with Java 8, the
recommended Java Runtime is Java 11 (or later) with OpenJFX 17 (or later).

For the detailed changes, see
* [PMD Designer Changelog (7.0.0)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0).
* [PMD Designer Changelog (7.0.0-rc4)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc4).
* [PMD Designer Changelog (7.0.0-rc1)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc1).

#### New CPD report format cpdhtml-v2.xslt

Thanks to @mohan-chinnappan-n a new CPD report format has been added which features a data table.
It uses an XSLT stylesheet to convert CPD's XML format into HTML.

See [the example report]({{ baseurl }}report-examples/cpdhtml-v2.html).

Contributors: [Mohan Chinnappan](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)

### 🎉 Language Related Changes

Note that this is just a concise listing of the highlights.
For more information on the languages, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#-language-related-changes).

#### New: CPD support for Apache Velocity Template Language (VTL)

PMD supported Apache Velocity for a very long time, but the CPD integration never got finished.
This is now done and CPD supports Apache Velocity Template language for detecting copy and paste.
It is shipped in the module `pmd-velocity`.

#### New: CPD support for Coco

Thanks to a contribution, CPD now supports Coco, a modern programming language
designed specifically for building event-driven software. It is shipped in the new
module `pmd-coco`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: CPD support for Julia

Thanks to a contribution, CPD now supports the Julia language. It is shipped
in the new module `pmd-julia`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: CPD support for TypeScript

Thanks to a contribution, CPD now supports the TypeScript language. It is shipped
with the rest of the JavaScript support in the module `pmd-javascript`.

Contributors: [Paul Guyot](https://github.com/pguyot) (@pguyot)

#### New: Java 21 and 22 Support

This release of PMD brings support for Java 21 and 22. There are the following new standard language features,
that are supported now:

* [JEP 456: Unnamed Variables & Patterns](https://openjdk.org/jeps/456) (Java 22)
* [JEP 440: Record Patterns](https://openjdk.org/jeps/440) (Java 21)
* [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441) (Java 21)

PMD also supports the following preview language features:

* [JEP 447: Statements before super(...) (Preview)](https://openjdk.org/jeps/447) (Java 22)
* [JEP 459: String Templates (Second Preview)](https://openjdk.org/jeps/459) (Java 21 and 22)
* [JEP 463: Implicitly Declared Classes and Instance Main Methods (Second Preview)](https://openjdk.org/jeps/463) (Java 21 and 22)

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `22-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-22-preview ...

Note: Support for Java 19 and Java 20 preview language features have been removed. The versions "19-preview" and
"20-preview" are no longer available.

#### New: Kotlin support

* Use PMD to analyze Kotlin code with PMD rules.
* Support for Kotlin 1.8 grammar
* Initially 2 built-in rules
* Support for Kotlin was introduced with PMD 7.0.0-rc1 as an experimental feature. With PMD 7.0.0 this
  is now considered stable.

Contributors: [Jeroen Borgers](https://github.com/jborgers) (@jborgers),
[Peter Paul Bakker](https://github.com/stokpop) (@stokpop)

#### New: Swift support

* Use PMD to analyze Swift code with PMD rules.
* Limited support for Swift 5.9 (Macro Expansions)
* Initially 4 built-in rules

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

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
With the new Apex parser, the new language constructs like User Mode Database Operations
can be parsed now. PMD should be able to parse Apex code up to version 59.0 (Winter '23).

See [#3766](https://github.com/pmd/pmd/issues/3766) for details.

Contributors: [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google),
[Edward Klimoshenko](https://github.com/eklimo) (@eklimo)

#### Changed: CPP can now ignore identifiers in sequences (CPD)

* New command line option for CPD: `--ignore-sequences`.
* This option is used for CPP only: with the already existing option `--ignore-literal-sequences`, only
  literals were ignored. The new option additionally ignores identifiers as well in sequences.
* See [PR #4470](https://github.com/pmd/pmd/pull/4470) for details.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### Changed: Groovy Support (CPD)

* We now support parsing all Groovy features from Groovy 3 and 4.
* We now support [suppression]({{ baseurl }}pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs.
* See [PR #4726](https://github.com/pmd/pmd/pull/4726) for details.

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Changed: HTML support

Support for HTML was introduced in PMD 6.55.0 as an experimental feature. With PMD 7.0.0 this
is now considered stable.

#### Changed: JavaScript support

* Latest version supports ES6 and also some new constructs (see [Rhino](https://github.com/mozilla/rhino))
* Comments are retained

#### Changed: Language versions

* More predefined language versions for each supported language
* Can be used to limit rule execution for specific versions only with `minimumLanguageVersion` and
  `maximumLanguageVersion` attributes.

#### Changed: Rule properties

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  {% jdoc core::properties.PropertyFactory %} to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

#### Changed: Velocity Template Language (VTL)

The module was named just "vm" which was not a good name. Its module name, language id and
package names have been renamed to "velocity".

If you import rules, you also need to adjust the paths, e.g.

* `category/vm/...` ➡️ `category/velocity/...`

#### Changed: Visualforce

There was an inconsistency between the naming of the maven module and the language id. The language id
used the abbreviation "vf", while the maven module used the longer name "visualforce". This has been
solved by renaming the language module to its full name "visualforce". The java packages have
been renamed as well.

If you import rules, you also need to adjust the paths, e.g.

* `category/vf/security.xml` ➡️ `category/visualforce/security.xml`

### 🌟 New and changed rules

#### New Rules

**Apex**
* {% rule apex/performance/OperationWithHighCostInLoop %} finds Schema class methods called in a loop, which is a
  potential performance issue.
* {% rule apex/design/UnusedMethod %} finds unused methods in your code.

**Java**
* {% rule java/codestyle/UnnecessaryBoxing %} reports boxing and unboxing conversions that may be made implicit.
* {% rule java/codestyle/UseExplicitTypes %} reports usages of `var` keyword, which was introduced with Java 10.

**Kotlin**
* {% rule kotlin/bestpractices/FunctionNameTooShort %} finds functions with a too short name.
* {% rule kotlin/errorprone/OverrideBothEqualsAndHashcode %} finds classes with only
  either `equals` or `hashCode` overridden, but not both. This leads to unexpected behavior once instances
  of such classes are used in collections (Lists, HashMaps, ...).

**Swift**
* {% rule swift/errorprone/ForceCast %} flags all force casts, making sure you are
  defensively considering all types. Having the application crash shouldn't be an option.
* {% rule swift/errorprone/ForceTry %} flags all force tries, making sure you are
  defensively handling exceptions. Having the application crash shouldn't be an option.
* {% rule swift/bestpractices/ProhibitedInterfaceBuilder %} flags any usage of interface
  builder. Interface builder files are prone to merge conflicts, and are impossible to code review, so larger
  teams usually try to avoid it or reduce its usage.
* {% rule swift/bestpractices/UnavailableFunction %} flags any function throwing
  a `fatalError` not marked as `@available(*, unavailable)` to ensure no calls are actually performed in
  the codebase.

**XML**
* {% rule xml/bestpractices/MissingEncoding %} finds XML files without explicit encoding.

#### Other changes

The information about changed rules, removed rules and rulesets
can be found in the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#-new-and-changed-rules).

### 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Also, there are some improvement and changes in different areas. For the detailed description
of the changes listed here, see [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#-api).

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

See also [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#-compatibility-and-migration-notes).

### 🐛 Fixed Issues

More than 300 issues have been fixed in PMD 7.
See [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#-fixed-issues) for the
complete list of fixed issues.

### ✨ External Contributions

Many thanks to the following contributors:
@219sansim, @aaronhurst-google, @anastasiia-koba, @AndreyBozhko, @bergander, @ciufudean, @cyw3, @dague1,
@Debamoy, @eklimo, @ethauvin, @JerritEic, @joaodinissf, @kenji21, @krdabrowski, @lbovet, @lsoncini,
@LynnBroe, @marcindabrowski, @matifraga, @mohan-chinnappan-n, @mohui1999, @nawforce, @nirvikpatel,
@nwcm, @pguyot, @PimvanderLoos, @rcorfieldffdc, @sfdcsteve, @shai-bennathan, @tomidelucca,
@tprouvot, @wener-tiobe.

See [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#-external-contributions) for the
full list of PRs.

### 📈 Stats
* 5662 commits
* 796 closed tickets & PRs
* Days since last release (6.55.0): 377
* Days since last release (7.0.0-rc4): 160

{% if is_release_notes_processor %}
</details>
{% endif %}

{% endtocmaker %}
