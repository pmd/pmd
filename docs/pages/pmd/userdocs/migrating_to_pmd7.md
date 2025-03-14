---
title: Migration Guide for PMD 7
tags: [pmd, userdocs]
summary: "Migrating to PMD 7 from PMD 6.x"
permalink: pmd_userdocs_migrating_to_pmd7.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: June 2024 (7.3.0)
---

{% include important.html content="
This document might be incomplete and doesn't answer all questions. In that case please reach out to us
by opening a [discussion](https://github.com/pmd/pmd/discussions) so that we can improve this guide.
" %}

## Before you update

Before updating to PMD 7, you should first update to the latest PMD 6 version 6.55.0 and try to fix all
deprecation warnings.

There are a couple of deprecated things in PMD 6, you might encounter:

* Properties: In order to define property descriptors, you should use {% jdoc core::properties.PropertyFactory %} now.
  This factory can create properties of any type. E.g. instead of `StringProperty.named(...)` use
  `PropertyFactory.stringProperty(...)`.

  Also note, that `uiOrder` is gone. You can just remove it.

  See also [Defining rule properties](pmd_userdocs_extending_defining_properties.html)

* When reporting a violation, you might see a deprecation of the `addViolation` methods. These methods have been moved
  to {% jdoc core::reporting.RuleContext %}. E.g. instead of `addViolation(data, node, ...)` use `asCtx(data).addViolation(node, ...)`.

* When you are calling PMD from CLI, you need to stop using deprecated CLI params, e.g.
  * `-no-cache` ➡️ `--no-cache`
  * `-failOnViolation` ➡️ `--fail-on-violation`
  * `-reportfile` ➡️ `--report-file`
  * `-language` ➡️ `--use-version`

* If you have written custom XPath rule, look out for warnings about deprecated XPath attributes. These warnings
  might look like
  ```
  WARNING: Use of deprecated attribute 'VariableId/@Image' by XPath rule 'VariableNaming' (in ruleset 'VariableNamingRule'), please use @Name instead
  ```
  and often already suggest an alternative.

* If you still reference rulesets or rules the old way which has been deprecated since 6.46.0:
  - `<lang-name>-<ruleset-name>`, eg `java-basic`, which resolves to `rulesets/java/basic.xml`
  - the internal release number, eg `600`, which resolves to `rulesets/releases/600.xml`

  Such usages produce deprecation warnings that should be easy to spot, e.g.
  ```
  Ruleset reference 'java-basic' uses a deprecated form, use 'rulesets/java/basic.xml' instead
  ```

  Use the explicit forms of these references to be compatible with PMD 7.

  Note: Since PMD 6, all rules are sorted into categories (such as "Best Practices", "Design", "Error Prone")
  and the old rulesets like `basic.xml` have been deprecated and have been removed with PMD 7.
  It is about time to create a [custom ruleset](pmd_userdocs_making_rulesets.html).

## Approaching 7.0.0

After that, migrate to the release candidates, and fix any problems you encounter. Start with 7.0.0-rc1 via
7.0.0-rc2, 7.0.0-rc3 and 7.0.0-rc4 until you finally use 7.0.0.

You might encounter additionally the following types of problems:

* If you use any programmatic API of PMD, first avoid any usage of deprecated or internal classes/methods. These
  are marked with one of these annotations: `@Deprecated`, `@DeprecatedUtil700`, `@InternalApi`.
  * Some of these classes are available until 7.0.0-rc4 but are finally removed with 7.0.0.
  * See [API changes](pmd_release_notes_pmd7.html#api-changes) for details.
* Some rules have been removed, because they have been deprecated. See [Removed Rules](pmd_release_notes_pmd7.html#removed-rules).
* Some rule properties have been removed or changed. See [Changed Rules](pmd_release_notes_pmd7.html#changed-rules).
* The filenames of the assets of a release (the "binary distribution zip file") have changed,
  see [Release downloads](#release-downloads).
* Some CLI options have been removed, because they have been deprecated. See [CLI Changes](#cli-changes) for details.
* If you call CPD programmatically, the API has changed, see [New Programmatic API for CPD](pmd_release_notes_pmd7.html#new-programmatic-api-for-cpd).
* If you use Visualforce, then you need to change "vf" to "visualforce", e.g. `category/vf/security.xml` ➡️ `category/visualforce/security.xml`
* If you use Velocity, then you need to change "vm" to "velocity", e.g. `category/vm/...` ➡️ `category/velocity/...`

The following topics describe well known migration challenges in more detail.

## Use cases

### I'm using only built-in rules

When you are using only built-in rules, then you should check, whether you use any deprecated rule. With PMD 7
many deprecated rules are finally removed. You can see a complete list of the [removed rules](pmd_release_notes_pmd7.html#removed-rules)
in the release notes for PMD 7.
The release notes also mention the replacement rule, that should be used instead. For some rules, there is no
replacement.

Then many rules have been changed or improved. New properties have been added to make them more versatile or
properties have been removed, if they are not necessary anymore. See [changed rules](pmd_release_notes_pmd7.html#changed-rules)
in the release notes for PMD 7.

All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped 
with a backslash. This affects the following rules:
{% rule  java/bestpractices/AvoidUsingHardCodedIP %},
{% rule  java/bestpractices/LooseCoupling %},
{% rule  java/bestpractices/UnusedPrivateField %},
{% rule  java/bestpractices/UnusedPrivateMethod %},
{% rule  java/codestyle/AtLeastOneConstructor %},
{% rule  java/codestyle/CommentDefaultAccessModifier %},
{% rule  java/codestyle/FieldNamingConventions %},
{% rule  java/codestyle/LinguisticNaming %},
{% rule  java/codestyle/UnnecessaryConstructor %},
{% rule  java/design/CyclomaticComplexity %},
{% rule  java/design/NcssCount %},
{% rule  java/design/SingularField %},
{% rule  java/errorprone/AvoidBranchingStatementAsLastInLoop %},
{% rule  java/errorprone/CloseResource %}.

A handful of rules are new to PMD 7. You might want to check these out: [new rules](pmd_release_notes_pmd7.html#new-rules).

Once you have reviewed your ruleset(s), you can switch to PMD 7.

### I'm using custom rules

#### Testing
Ideally, you have written good tests already for your custom rules - see [Testing your rules](pmd_userdocs_extending_testing.html).
This helps to identify problems early on.

The base test classes {%jdoc test::test.PmdRuleTst %} and {%jdoc test::test.SimpleAggregatorTst %} have been moved out
of package `net.sourceforge.pmd.testframework`. You'll need to adjust your imports.

#### Ruleset XML
The `<rule>` tag, that defines your custom rule, is required to have a `language` attribute now. This was always the
case for XPath rules, but is now a requirement for Java rules.

#### XPath rules
If you have **XPath based** rules, the first step will be to migrate to XPath 2.0 and then to XPath 3.1.
XPath 2.0 is available in PMD 6 already and can be used right away. PMD 7 will use by default XPath 3.1 and
won't support XPath 1.0 anymore. The difference between XPath 2.0 and XPath 3.1 is not big, so your XPath 2.0
can be expected to work in PMD 7 without any further changes. So the migration path is to simply migrate to XPath 2.0.

After you have migrated your XPath rules to XPath 2.0, remove the "version" property, since that has been removed
with PMD 7. PMD 7 by default uses XPath 3.1. See below [XPath](#xpath-migrating-from-10-to-20) for details.

Then change the `class` attribute of your rule to `net.sourceforge.pmd.lang.rule.xpath.XPathRule` - because the
class {%jdoc core::lang.rule.xpath.XPathRule %} has been moved into subpackage {% jdoc_package core::lang.rule.xpath %}.

There are some general changes for AST nodes regarding the `@Image` attribute.
See below [General AST Changes to avoid @Image](#general-ast-changes-to-avoid-image).

Additional infos:
* The custom XPath function `typeOf` has been removed (deprecated since 6.4.0).
  Use the function `pmd-java:typeIs` or `pmd-java:typeIsExactly` instead.
  See [PMD extension functions](pmd_userdocs_extending_writing_xpath_rules.html#pmd-extension-functions) for available
  functions.

#### Java rules
If you have **Java based rules**, and you are using rulechain, this works a bit different now. The RuleChain API
has changed, see [[core] Simplify the rulechain (#2490)](https://github.com/pmd/pmd/pull/2490) for the full details.
But in short, you don't call `addRuleChainVisit(...)` in the rule's constructor anymore. Instead, you
override the method {% jdoc core::lang.rule.AbstractRule#buildTargetSelector %}:

```java
    protected RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTVariableId.class);
    }
```

#### Java AST changes
The API to **navigate the AST** also changed significantly:
* Tree traversal using [Node API](#node-api)
* Consider using the new [NodeStream API](#nodestream-api) to navigate with null-safety. This is optional.

Additionally, if you have created rules for **Java** - regardless whether it is a XPath based rule or a Java based
rule - you might need to adjust your queries or visitor methods. The Java AST has been refactored substantially.
The easiest way is to use the [PMD Rule Designer](pmd_userdocs_extending_designer_reference.html) to see the structure
of the AST. See the section [Java AST](#java-ast) below for details.

### I've extended PMD with a custom language...

The guides for [Adding a new language with JavaCC](pmd_devdocs_major_adding_new_language_javacc.html) and
[Adding a new CPD language](pmd_devdocs_major_adding_new_cpd_language.html) have been updated.

Most notable changes are:

* As an alternative, PMD 7 now supports ANTLR in addition to JavaCC:
  [Adding a new language with ANTLR](pmd_devdocs_major_adding_new_language_antlr.html).
* There is a shared ant script that wraps the calls to javacc: `javacc-wrapper.xml`. This should be used now.
* PMD's parser adapter for JavaCC generated parsers is called now
  {% jdoc core::lang.ast.impl.javacc.JjtreeParserAdapter %}. This is the class that needs to be implemented now.
* There is no need anymore to write a custom `TokenManager` - we have now a common base class for JavaCC generated
  token managers. This base class is {% jdoc core::lang.ast.impl.javacc.AbstractTokenManager %}.
* A rule violation factory is not needed anymore. For language specific information on rule violations, there is
  now a {% jdoc core::reporting.ViolationDecorator %} that a language can implement. These ViolationDecorators
  are called when a violation is reported and they can provide the additional information. This information can be
  used by renderers via {% jdoc !!core::reporting.RuleViolation#getAdditionalInfo() %}.
* A parser visitor adapter is not needed anymore. The visitor interface now provides a default implementation.
  Instead, a base visitor for the language should be created, which extends {% jdoc core::lang.ast.AstVisitorBase %}.
* A rule chain visitor is not needed anymore. PMD provides a common implementation that fits all languages.

### I've extended PMD with a custom feature...

In that case we can't provide a general guide unless we know the specific custom feature. If you are having difficulties
finding your way around the PMD source code and javadocs and you don't see the aspect of PMD documented you are
using, we are probably missing documentation. Please reach out to us by opening a
[discussion](https://github.com/pmd/pmd/discussions). We then can enhance the documentation and/or the PMD API.

## Special topics

### Release downloads

* The asset filenames of PMD on [GitHub Releases](https://github.com/pmd/pmd/releases) are
  now `pmd-dist-<version>-bin.zip`, `pmd-dist-<version>-src.zip` and `pmd-dist-<version>-doc.zip`.
  Keep that in mind, if you have an automated download script.
* The structure inside the ZIP files stay the same, e.g. we still provide inside the binary distribution
  ZIP file the base directory `pmd-bin-<version>`.

### CLI Changes

The CLI has been revamped completely
(see [Release Notes: Revamped Command Line Interface](pmd_release_notes_pmd7.html#revamped-command-line-interface)).

Most notable changes:

* Unified start script on all platforms for all commands (PMD, CPD, Designer). Instead of `run.sh` and `pmd.bat`,
  we now have `pmd` only (technically on Windows, there is still a `pmd.bat`, but it behaves the same). 
  * Executing PMD from CLI now means: `run.sh pmd` / `pmd.bat` ➡️ `pmd check`
  * Executing CPD: `run.sh cpd` / `cpd.bat` ➡️ `pmd cpd`
  * Executing Designer: `run.sh designer` / `designer.bat` ➡️ `pmd designer`
  * Executing CPD GUI: `run.sh cpd-gui` / `cpdgui.bat` ➡️ `pmd cpd-gui`
* There are some changes to the CLI arguments:
  * `--fail-on-violation false` ➡️ `--no-fail-on-violation`
    
    If you don't replace this argument, then "false" will be interpreted as a file to analyze. You might see then
    an error message such as `[main] ERROR net.sourceforge.pmd.cli.commands.internal.PmdCommand - No such file false`.
  * PMD tries to display a progress bar. If you don't want this (e.g. on a CI build server), you can disable this
    with `--no-progress`.
  * `--no-ruleset-compatibility` has been removed without replacement.
  * `--stress` (or `-stress`) has been removed without replacement.

### Custom distribution packages

When creating a custom distribution which only integrates the languages you need, there are some changes to apply:

* In addition to the language dependencies you want, you also need add a dependency to
  `net.sourceforge.pmd:pmd-cli` in order to get the CLI classes.
* When fetching the scripts for the CLI with "maven-dependency-plugin", you need to additionally fetch the
  logging configuration. That means, the line
  `<includes>scripts/**,LICENSE</includes>` needs to be changed to `<includes>scripts/**,LICENSE,conf/**</includes>`.
* Since the assembly descriptor `pmd-bin` includes now optionally also a BOM (bill of material). If you want to
  create this for your custom distribution, simply add the following plugin configuration:
  ```xml
     <plugin>
        <groupId>org.cyclonedx</groupId>
        <artifactId>cyclonedx-maven-plugin</artifactId>
        <version>2.7.11</version>
        <configuration>
          <outputName>pmd-${project.version}-cyclonedx</outputName>
        </configuration>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>makeAggregateBom</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
  ```
* The artifact name for PMD Designer has been renamed, you need to use now `net.sourceforge.pmd:pmd-designer`
  instead of "pmd-ui".

{% include note.html content="
The examples on <https://github.com/pmd/pmd-examples> have been updated.
" %}


### Rule tests are now using JUnit5

When you have custom rules, and you have written rule tests according to the guide
[Testing your rules](pmd_userdocs_extending_testing.html), you might want to consider upgrading your other tests to
[JUnit 5](https://junit.org/junit5/). The tests in PMD 7 have been migrated to JUnit5 - including the rule tests
for the built-in rules.

When executing the rule tests, you need to make sure to have JUnit5 on the classpath - which you automatically
get when you depend on `net.sourceforge.pmd:pmd-test`. If you also have JUnit4 tests, you need to make sure
to have a [junit-vintage-engine](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4-running)
as well on the test classpath, so that all tests are executed. That means, you might
need to add now a dependency to JUnit4 explicitly if needed.

### CPD: Reported endcolumn is now exclusive

In PMD 6, the reported position of the duplicated tokens in CPD where always including, e.g. the following
described a duplication of length 4 in PMD 6: beginLine=1, endLine=1, beginColumn=1, endColumn=4 - these are
the first 4 character in the first line. With PMD 7, the endColumn is now **excluding**. The same duplication
will be reported in PMD 7 as: beginLine=1, endLine=1, beginColumn=1, endColumn=5.

The reported positions in a file follow now the usual meaning: line numbering starts from 1, begin line and end line
are inclusive, begin column is inclusive and end column is exclusive. This is the usual behavior of the most
common text editors and the PMD part already used that meaning in RuleViolations for a long time in PMD 6 already.

This only affects the XML report format as the others don't provide column information.

### Node API

Starting from one node in the AST, you can navigate to children or parents with the following methods. This is
the "traditional" way for simple cases. For more complex cases, consider to use the new [NodeStream API](#nodestream-api).

Many methods available in PMD 6 have been deprecated and removed for a slicker API with consistent naming,
that also integrates tightly with the NodeStream API.

* `getNthParent(n)` ➡️ `ancestors().get(n - 1)`
* `getFirstParentOfType(parentType)` ➡️ `ancestors(parentType).first()`
* `getParentsOfType(parentType)` ➡️ `ancestors(parentType).toList()`
* `findChildrenOfType(childType)` ➡️ `children(childType).toList()`
* `findDescendantsOfType(targetType)` ➡️ `descendants(targetType).toList()`
* `getFirstChildOfType(childType)` ➡️ `firstChild(childType)`
* `getFirstDescendantOfType(descendantType)` ➡️ `descendants(descendantType).first()`
* `hasDescendantOfType(type)` ➡️ `descendants(type).nonEmpty()`

{% include tip.html content="First use PMD 7.0.0-rc3, which still has these methods. These methods are marked as
deprecated, so you can then start to change them. The replacement method is usually provided in the javadocs.
That way you avoid being confronted with just compile errors." %}

Unchanged methods that work as before:
* {% jdoc core::lang.ast.Node#getParent() %}
* {% jdoc core::lang.ast.Node#getChild(int) %}
* {% jdoc core::lang.ast.Node#getNumChildren() %}
* {% jdoc core::lang.ast.Node#getIndexInParent() %}

New methods:
* {% jdoc core::lang.ast.Node#getFirstChild() %}
* {% jdoc core::lang.ast.Node#getLastChild() %}
* {% jdoc core::lang.ast.Node#getPreviousSibling() %}
* {% jdoc core::lang.ast.Node#getNextSibling() %}
* {% jdoc core::lang.ast.Node#getRoot() %}

New methods that integrate with NodeStream:
* {% jdoc core::lang.ast.Node#children() %} - returns a NodeStream containing all the children of this node.
  Note: in PMD 6, this method returned an `Iterable`
* {% jdoc core::lang.ast.Node#descendants() %}
* {% jdoc core::lang.ast.Node#descendantsOrSelf() %}
* {% jdoc core::lang.ast.Node#ancestors() %}
* {% jdoc core::lang.ast.Node#ancestorsOrSelf() %}
* {% jdoc core::lang.ast.Node#children(java.lang.Class) %}
* {% jdoc core::lang.ast.Node#firstChild(java.lang.Class) %}
* {% jdoc core::lang.ast.Node#descendants(java.lang.Class) %}
* {% jdoc core::lang.ast.Node#ancestors(java.lang.Class) %}

Methods removed completely:
* `getFirstParentOfAnyType(parentTypes)`:️ There is no direct replacement, but something along the lines:
  ```java
        ancestors()
                .filter(n -> Arrays.stream(classes)
                        .anyMatch(c -> c.isInstance(n)))
                .first();
  ```
* `findChildNodesWithXPath`: Has been removed, because it is very inefficient. Use NodeStream instead.
* `hasDescendantMatchingXPath`: Has been removed, because it is very inefficient. Use NodeStream instead.
* `jjt*` like `jjtGetParent`. These methods were implementation specific. Use the equivalent methods like `getParent()`.

See {% jdoc core::lang.ast.Node %} for the details.

### NodeStream API

In java rule implementations, you often need to navigate the AST to find the interesting nodes. In PMD 6, this
was often done by calling `jjtGetChild(int)` or `jjtGetParent(int)` and then checking the node type
with `instanceof`. There are also helper methods available, like `getFirstChildOfType(Class)` or
`findDescendantsOfType(Class)`. These methods might return `null` and you need to check this for every
level.

The new **NodeStream API** provides easy to use methods that follow the Java Stream API (`java.util.stream`).

Many complex predicates about nodes can be expressed by testing the emptiness of a node stream.
E.g. the following tests if the node is a variable declarator id initialized to the value `0`:

Example:

```java
     NodeStream.of(someNode)                           // the stream here is empty if the node is null
               .filterIs(ASTVariableId.class)          // the stream here is empty if the node was not a variable id
               .followingSiblings()                    // the stream here contains only the siblings, not the original node
               .children(ASTNumericLiteral.class)
               .filter(ASTNumericLiteral::isIntLiteral)
               .filterMatching(ASTNumericLiteral::getValueAsInt, 0)
               .nonEmpty(); // If the stream is non empty here, then all the pipeline matched
```

See {% jdoc core::lang.ast.NodeStream %} for the details.
Note: This was implemented via [PR #1622 [core] NodeStream API](https://github.com/pmd/pmd/pull/1622)

### XPath: Migrating from 1.0 to 2.0

XPath 1.0 and 2.0 have some incompatibilities. The [XPath 2.0 specification](https://www.w3.org/TR/xpath20/#id-incompat-in-false-mode)
describes them precisely. Those are however mostly corner cases and XPath
rules usually don't feature any of them.

The incompatibilities that are most relevant to migrating your rules are not
caused by the specification, but by the different engines we use to run
XPath 1.0 and 2.0 queries. Here's a list of known incompatibilities:

* The namespace prefixes `fn:` and `string:` should not be mentioned explicitly.
  In XPath 2.0 mode, the engine will complain about an undeclared namespace, but
  the functions are in the default namespace. Removing the namespace prefixes fixes it.
  * <code><b style="color:red">fn:</b>substring("Foo", 1)</code> &rarr; `substring("Foo", 1)`
* Conversely, calls to custom PMD functions like `typeIs` *must* be prefixed
  with the namespace of the declaring module (`pmd-java`).
  * `typeIs("Foo")` &rarr; <code><b style="color:green">pmd-java:</b>typeIs("Foo")</code>
* Boolean attribute values on our 1.0 engine are represented as the string values
  `"true"` and `"false"`. In 2.0 mode though, boolean values are truly represented
  as boolean values, which in XPath may only be obtained through the functions
  `true()` and `false()`.
  If your XPath 1.0 rule tests an attribute like `@Private="true"`, then it just
  needs to be changed to `@Private=true()` when migrating. A type error will warn
  you that you must update the comparison. More is explained on [issue #1244](https://github.com/pmd/pmd/issues/1244).
  * `"true"`, `'true'` &rarr; `true()`
  * `"false"`, `'false'` &rarr; `false()`

* In XPath 1.0, comparing a number to a string coerces the string to a number.
  In XPath 2.0, a type error occurs. Like for boolean values, numeric values are
  represented by our 1.0 implementation as strings, meaning that `@BeginLine > "1"`
  worked ---that's not the case in 2.0 mode.
  * <code>@ArgumentCount > <b style="color:red">'</b>1<b style="color:red">'</b></code> &rarr; `@ArgumentCount > 1`

* In XPath 1.0, the expression `/Foo` matches the *children* of the root named `Foo`.
  In XPath 2.0, that expression matches the root, if it is named `Foo`. Consider the following tree:
  ```java
  Foo
  └─ Foo
  └─ Foo
  ```
  Then `/Foo` will match the root in XPath 2.0, and the other nodes (but not the root) in XPath 1.0.
  See e.g. [an issue caused by this](https://github.com/pmd/pmd/issues/1919#issuecomment-512865434) in Apex,
  with nested classes.

* The custom function "pmd:matches" which checks a regular expression against a string has been removed,
  since there is a built-in function available since XPath 2.0 which can be used instead. If you use "pmd:matches"
  simply remove the "pmd:" prefix.

### General AST Changes to avoid @Image

An abstract syntax tree should be abstract, but in the same time, should not be too abstract. One of the
base interfaces for PMD's AST for all languages is {% jdoc core::lang.ast.Node %}, which provides
the methods {% jdoc core::lang.ast.Node#getImage() %} and {% jdoc core::lang.ast.Node#hasImageEqualTo(java.lang.String) %}.
However, these methods don't necessarily make sense for all nodes in all contexts. That's why `getImage()`
often returns just `null`. Also, the name is not very describing. AST nodes should try to use more specific
names, such as `getValue()` or `getName()`.

For PMD 7, most languages have been adapted. And when writing XPath rules, you need to replace `@Image` with
whatever is appropriate now (e.g. `@Name`). See below for details.

#### Apex and Visualforce

There are many usages of `@Image`. These will be refactored after PMD 7 is released
by deprecating the attribute and providing alternatives.

See also issue [Deprecate getImage/@Image #4787](https://github.com/pmd/pmd/issues/4787).

#### Html

* {% jdoc html::lang.html.ast.ASTHtmlTextNode %}: `@Image` ➡️ `@Text`, `@NormalizedText` ➡️ `@Text`, `@Text` ➡️ `@WholeText`.

#### Java

There are still many usages of `@Image` which are not refactored yet. This will be done after PMD 7 is released
by deprecating the attribute and providing alternatives.

See also issue [Deprecate getImage/@Image #4787](https://github.com/pmd/pmd/issues/4787).

Some nodes have already the image attribute (and others) deprecated. These deprecated attributes are removed now:

* {% jdoc java::lang.java.ast.ASTAnnotationTypeDeclaration %}: `@Image` ➡️ `@SimpleName`
* {% jdoc java::lang.java.ast.ASTAnonymousClassDeclaration %}: `@Image` ➡️ `@SimpleName`
* {% jdoc java::lang.java.ast.ASTClassDeclaration %} (previously "ASTClassOrInterfaceDeclaration"): `@Image` ➡️ `@SimpleName`
* {% jdoc java::lang.java.ast.ASTEnumDeclaration %}: `@Image` ➡️ `@SimpleName`
* {% jdoc java::lang.java.ast.ASTFieldDeclaration %}: `@VariableName` ➡️ `VariableId/@Name`
* {% jdoc java::lang.java.ast.ASTMethodDeclaration %}: `@Image` ➡️ `@Name`
* {% jdoc java::lang.java.ast.ASTMethodDeclaration %}: `@MethodName` ➡️ `@Name`
* {% jdoc java::lang.java.ast.ASTRecordDeclaration %}: `@Image` ➡️ `@SimpleName`
* {% jdoc java::lang.java.ast.ASTVariableId %} (previously "ASTVariableDeclaratorId"): `@Image` ➡️ `@Name`
* {% jdoc java::lang.java.ast.ASTVariableId %} (previously "ASTVariableDeclaratorId"): `@VariableName` ➡️ `@Name`
* {% jdoc java::lang.java.ast.ASTVariableId %} (previously "ASTVariableDeclaratorId"): `@Array` ➡️ `@ArrayType`

#### JavaScript

* {% jdoc javascript::lang.ecmascript.ast.ASTAssignment %}: `@Image` ➡️ `@Operator`
* {% jdoc javascript::lang.ecmascript.ast.ASTBigIntLiteral %}: `@Image` ➡️ `@Value`
* {% jdoc javascript::lang.ecmascript.ast.ASTBreakStatement %}: `@Image` ➡️ `Name/@Identifier`
* {% jdoc javascript::lang.ecmascript.ast.ASTContinueStatement %}: `@Image` ➡️ `Name/@Identifier`
* {% jdoc javascript::lang.ecmascript.ast.ASTErrorNode %}: `@Image` ➡️ `@Message`
* {% jdoc javascript::lang.ecmascript.ast.ASTFunctionNode %}: `@Image` ➡️ `Name/@Identifier`
* {% jdoc javascript::lang.ecmascript.ast.ASTInfixExpression %}: `@Image` ➡️ `@Operator`
* {% jdoc javascript::lang.ecmascript.ast.ASTKeywordLiteral %}: `@Image` ➡️ `@Literal`
* {% jdoc javascript::lang.ecmascript.ast.ASTLabel %}: `@Image` ➡️ `@Name`
* {% jdoc javascript::lang.ecmascript.ast.ASTName %}: `@Image` ➡️ `@Identifier`
* {% jdoc javascript::lang.ecmascript.ast.ASTNumberLiteral %}: `@Image` ➡️ `@Value`
* {% jdoc javascript::lang.ecmascript.ast.ASTObjectProperty %}: `@Image` ➡️ `@Operator`
* {% jdoc javascript::lang.ecmascript.ast.ASTPropertyGet %}: `@Image` ➡️ `@Operator`
* {% jdoc javascript::lang.ecmascript.ast.ASTRegExpLiteral %}: `@Image` ➡️ `@Value`
* {% jdoc javascript::lang.ecmascript.ast.ASTStringLiteral %}: `@Image` ➡️ `@Value`
* {% jdoc javascript::lang.ecmascript.ast.ASTUnaryExpression %}: `@Image` ➡️ `@Operator`
* {% jdoc javascript::lang.ecmascript.ast.ASTUpdateExpression %}: `@Image` ➡️ `@Operator`
* {% jdoc javascript::lang.ecmascript.ast.ASTXmlDotQuery %}: `@Image` ➡️ `@Operator`
* {% jdoc javascript::lang.ecmascript.ast.ASTXmlMemberGet %}: `@Image` ➡️ `@Operator`
* {% jdoc javascript::lang.ecmascript.ast.ASTXmlPropRef %}: `@Image` ➡️ `Name[last()]/@Identifier`
* {% jdoc javascript::lang.ecmascript.ast.ASTXmlString %}: `@Image` ➡️ `@Xml`

#### JSP

* {% jdoc jsp::lang.jsp.ast.ASTAttributeValue %}: `@Image` ➡️ `@Value`
* {% jdoc jsp::lang.jsp.ast.ASTCData %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTCommentTag %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTElExpression %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTHtmlScript %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTJspComment %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTJspDeclaration %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTJspExpression %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTJspExpressionInAttribute %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTJspScriptlet %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTText %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTUnparsedText %}: `@Image` ➡️ `@Content`
* {% jdoc jsp::lang.jsp.ast.ASTValueBinding %}: `@Image` ➡️ `@Content`

#### Modelica

* {% jdoc modelica::lang.modelica.ast.ASTAddOp %}: `@Image` ➡️ `@Operator`
* {% jdoc modelica::lang.modelica.ast.ASTDerClassSpecifier %}: `@Image` ➡️ `@SimpleClassName`
* {% jdoc modelica::lang.modelica.ast.ASTEnumerationShortClassSpecifier %}: `@Image` ➡️ `@SimpleClassName`
* {% jdoc modelica::lang.modelica.ast.ASTExtendingLongClassSpecifier %}: `@Image` ➡️ `@SimpleClassName`
* {% jdoc modelica::lang.modelica.ast.ASTFactor %}: `@Image` ➡️ `@Operator`
* {% jdoc modelica::lang.modelica.ast.ASTLanguageSpecification %}: `@Image` ➡️ `@ExternalLanguage`
* {% jdoc modelica::lang.modelica.ast.ASTMulOp %}: `@Image` ➡️ `@Operator`
* {% jdoc modelica::lang.modelica.ast.ASTName %}: `@Image` ➡️ `@Name`
* {% jdoc modelica::lang.modelica.ast.ASTNumberLiteral %}: `@Image` ➡️ `@Value`
* {% jdoc modelica::lang.modelica.ast.ASTRelOp %}: `@Image` ➡️ `@Operator`
* {% jdoc modelica::lang.modelica.ast.ASTSimpleLongClassSpecifier %}: `@Image` ➡️ `@SimpleClassName`
* {% jdoc modelica::lang.modelica.ast.ASTSimpleName %}: `@Image` ➡️ `@Name`
* {% jdoc modelica::lang.modelica.ast.ASTSimpleShortClassSpecifier %}: `@Image` ➡️ `@SimpleClassName`
* {% jdoc modelica::lang.modelica.ast.ASTStoredDefinition %}: `@Image` ➡️ `@Name`
* {% jdoc modelica::lang.modelica.ast.ASTStringComment %}: `@Image` ➡️ `@Comment`
* {% jdoc modelica::lang.modelica.ast.ASTStringLiteral %}: `@Image` ➡️ `@Value`
* {% jdoc modelica::lang.modelica.ast.ASTWithinClause %}: `@Image` ➡️ `@Name`

#### PLSQL

There are many usages of `@Image`. These will be refactored after PMD 7 is released
by deprecating the attribute and providing alternatives.

See also issue [Deprecate getImage/@Image #4787](https://github.com/pmd/pmd/issues/4787).

#### Scala

* {% jdoc scala::lang.scala.ast.ASTLitBoolean %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitByte %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitChar %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitDouble %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitFloat %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitInt %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitLong %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitNull %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitShort %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitString %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitSymbol %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTLitUnit %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTNameAnonymous %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTNameIndeterminate %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTTermName %}: `@Image` ➡️ `@Value`
* {% jdoc scala::lang.scala.ast.ASTTypeName %}: `@Image` ➡️ `@Value`

#### XML (and POM)

When using {% jdoc core::lang.rule.xpath.XPathRule %}, text of text nodes was exposed as `@Image` of
normal element type nodes. Now the attribute is called `@Text`.

Note: In general, it is recommended to use {% jdoc xml::lang.xml.rule.DomXPathRule %} instead,
which exposes text nodes as real XPath/XML text nodes which conforms to the XPath spec.
There is no difference, text of text nodes can be selected using `text()`.

### Java AST

The Java grammar has been refactored substantially in order to make it easier to maintain and more correct
regarding the Java Language Specification.

Here you can see the most important changes as a comparison between the PMD 6 AST ("Old AST") and
PMD 7 AST ("New AST") and with some background info about the changes.

When in doubt, it is recommended to use the [PMD Designer](pmd_userdocs_extending_designer_reference.html)
which can also display the AST.

{% jdoc_nspace :jast java::lang.java.ast %}

#### Renamed classes / interfaces

* AccessNode ➡️ {% jdoc jast::ModifierOwner %}
* ClassOrInterfaceType ➡️ ClassType ({% jdoc jast::ASTClassType %})
* ClassOrInterfaceDeclaration ➡️ ClassDeclaration ({% jdoc jast::ASTClassDeclaration %})
* AnyTypeDeclaration ➡️ TypeDeclaration ({% jdoc jast::ASTTypeDeclaration %})
* MethodOrConstructorDeclaration ➡️ ExecutableDeclaration ({% jdoc jast::ASTExecutableDeclaration %})
* VariableDeclaratorId ➡️ VariableId ({% jdoc jast::ASTVariableId %})
* ClassOrInterfaceBody ➡️ ClassBody ({% jdoc jast::ASTClassBody %})

#### Annotations

* What: Annotations are consolidated into a single node. `SingleMemberAnnotation`, `NormalAnnotation` and `MarkerAnnotation`
  are removed in favour of {% jdoc jast::ASTAnnotation %}. The Name node is removed, replaced by a
  {% jdoc jast::ASTClassType %}.
* Why: Those different node types implement a syntax-only distinction, that only makes semantically equivalent annotations
  have different possible representations. For example, `@A` and `@A()` are semantically equivalent, yet they were
  parsed as MarkerAnnotation resp. NormalAnnotation. Similarly, `@A("")` and `@A(value="")` were parsed as
  SingleMemberAnnotation resp. NormalAnnotation. This also makes parsing much simpler. The nested ClassOrInterface
  type is used to share the disambiguation logic.
* Related issue: [[java] Use single node for annotations (#2282)](https://github.com/pmd/pmd/pull/2282)

<details>
  <summary markdown="span">Annotation AST Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
@A
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Annotation "A"
   └─ MarkerAnnotation "A"
      └─ Name "A"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Annotation "A"
   └─ ClassOrInterfaceType "A"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
@A()
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Annotation "A"
   └─ NormalAnnotation "A"
      └─ Name "A"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Annotation "A"
   ├─ ClassType "A"
   └─ AnnotationMemberList
{% endhighlight %}
</td>
</tr>

<tr><td>
{% highlight java %}
@A(value="v")
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Annotation "A"
   └─ NormalAnnotation "A"
      ├─ Name "A"
      └─ MemberValuePairs
         └─ MemberValuePair "value"
            └─ MemberValue
               └─ PrimaryExpression
                  └─ PrimaryPrefix
                     └─ Literal '"v"'
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Annotation "A"
   ├─ ClassType "A"
   └─ AnnotationMemberList
      └─ MemberValuePair "value" [ @Shorthand = false() ]
         └─ StringLiteral '"v"'
{% endhighlight %}
</td>
</tr>

<tr><td>
{% highlight java %}
@A("v")
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Annotation "A"
   └─ SingleMemberAnnotation "A"
      ├─ Name "A"
      └─ MemberValue
         └─ PrimaryExpression
            └─ PrimaryPrefix
               └─ Literal '"v"'
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Annotation "A"
   ├─ ClassType "A"
   └─ AnnotationMemberList
      └─ MemberValuePair "value" [ @Shorthand = true() ]
         └─ StringLiteral '"v"'
{% endhighlight %}
</td>
</tr>

<tr><td>
{% highlight java %}
@A(value="v", on=true)
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Annotation "A"
   └─ NormalAnnotation "A"
      ├─ Name "A"
      └─ MemberValuePairs
         ├─ MemberValuePair "value"
         │  └─ MemberValue
         │     └─ PrimaryExpression
         │        └─ PrimaryPrefix
         │           └─ Literal '"v"'
         └─ MemberValuePair "on"
            └─ MemberValue
               └─ PrimaryExpression
                  └─ PrimaryPrefix
                     └─ Literal
                        └─ BooleanLiteral [ @True = true() ]
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Annotation "A"
   ├─ ClassType "A"
   └─ AnnotationMemberList
      ├─ MemberValuePair "value" [ @Shorthand = false() ]
      │  └─ StringLiteral '"v"'
      └─ MemberValuePair "on"
         └─ BooleanLiteral [ @True = true() ]
{% endhighlight %}
</td>
</tr>
</table>

</details>

##### Annotation nesting

* What: {% jdoc jast::ASTAnnotation %}s are now nested within the node, to which they are applied to.
  E.g. if a method is annotated, the Annotation node is now a child of a {% jdoc jast::ASTModifierList %},
  inside the {% jdoc jast::ASTMethodDeclaration %}.
* Why: Fixes a lot of inconsistencies, where sometimes the annotations were inside the node, and sometimes just
  somewhere in the parent, with no real structure.
* Related issue: [[java] Move annotations inside the node they apply to (#1875)](https://github.com/pmd/pmd/pull/1875)

<details>
  <summary markdown="span">Annotation nesting Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
Method

{% highlight java %}
@A
public void set(int x) { }
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ClassOrInterfaceBodyDeclaration
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   └─ MethodDeclaration
      ├─ ResultType[ @Void = true ]
      ├─ ...
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration
   ├─ ModifierList
   │  └─ Annotation "A"
   │     └─ ClassType "A"
   ├─ VoidType
   ├─ ...
{% endhighlight %}
</td></tr>

<tr><td>
Top-level type declaration

{% highlight java %}
@A class C {}
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ TypeDeclaration
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   └─ ClassOrInterfaceDeclaration "C"
      └─ ClassOrInterfaceBody
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassDeclaration
    ├─ ModifierList
    │  └─ Annotation "A"
    │     └─ ClassType "A"
    └─ ClassBody
{% endhighlight %}
</td>
</tr>

<tr><td>
Cast expression

{% highlight java %}
var x = (@A T.@B S) expr;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CastExpression
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   ├─ Type
   │  └─ ReferenceType
   │     └─ ClassOrInterfaceType "T.S"
   │        └─ Annotation "B"
   │           └─ MarkerAnnotation "B"
   │              └─ Name "B"
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ Name "expr"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ CastExpression
   ├─ ClassType "S"
   │  ├─ ClassType "T"
   │  │  └─ Annotation "A"
   │  │     └─ ClassType "A"
   │  └─ Annotation "B"
   │     └─ ClassType "B"
   └─ VariableAccess "expr"
{% endhighlight %}
</td></tr>

<tr><td>
Cast expression with intersection

{% highlight java %}
var x = (@A T & S) expr;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CastExpression
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   ├─ Type
   │  └─ ReferenceType
   │     └─ ClassOrInterfaceType "T"
   ├─ ReferenceType
   │  └─ ClassOrInterfaceType "S"
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ Name "expr"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ CastExpression
  ├─ IntersectionType
  │  ├─ ClassType "T"
  │  │  └─ Annotation "A"
  │  │     └─ ClassType "A"
  │  └─ ClassType "S"
  └─ VariableAccess "expr"
{% endhighlight %}

Notice <code>@A</code> binds to <code>T</code>, not <code>T & S</code>

</td></tr>

<tr><td>
Constructor call

{% highlight java %}
new @A T()
{% endhighlight %}
</td><td>
{% highlight js %}
└─ AllocationExpression
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   ├─ ClassOrInterfaceType "T"
   └─ Arguments
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ConstructorCall
   ├─ ClassType "T"
   │  └─ Annotation "A"
   │     └─ ClassType "A"
   └─ ArgumentList
{% endhighlight %}
</td></tr>

<tr><td>
Array allocation

{% highlight java %}
new @A int[0]
{% endhighlight %}
</td><td>
{% highlight js %}
└─ AllocationExpression
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   ├─ PrimitiveType "int"
   └─ ArrayDimsAndInits
      └─ Expression
         └─ PrimaryExpression
            └─ PrimaryPrefix
               └─ Literal "0"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ArrayAllocation
   └─ ArrayType
      ├─ PrimitiveType "int"
      │  └─ Annotation "A"
      │     └─ ClassType "A"
      └─ ArrayDimensions
         └─ ArrayDimExpr
            └─ NumericLiteral "0"
{% endhighlight %}
</td></tr>

<tr><td>
Array type

{% highlight java %}
@A int @B[] x;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ LocalVariableDeclaration
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   ├─ Type[ @ArrayType = true() ]
   │  └─ ReferenceType
   │     ├─ PrimitiveType "int"
   │     └─ Annotation "B"
   │        └─ MarkerAnnotation "B"
   │           └─ Name "B"
   └─ VariableDeclarator
      └─ VariableDeclaratorId "x"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ LocalVariableDeclaration
  ├─ ModifierList
  │  └─ Annotation "A"
  │     └─ ClassType "A"
  ├─ ArrayType
  │  ├─ PrimitiveType "int"
  │  └─ ArrayDimensions
  │     └─ ArrayTypeDim
  │        └─ Annotation "B"
  │           └─ ClassType "B"
  └─ VariableDeclarator
     └─ VariableId "x"
{% endhighlight %}

</td></tr>

<tr><td>
Type parameters

{% highlight java %}
<@A T, @B S extends @C Object>
{% endhighlight %}
</td><td>
{% highlight js %}
└─ TypeParameters
   ├─ TypeParameter "T"
   │  └─ Annotation "A"
   │     └─ MarkerAnnotation "A"
   │        └─ Name "A"
   └─ TypeParameter "S"
      ├─ Annotation "B"
      │  └─ MarkerAnnotation "B"
      │     └─ Name "B"
      └─ TypeBound
         ├─ Annotation "C"
         │  └─ MarkerAnnotation "C"
         │     └─ Name "C"
         └─ ClassOrInterfaceType "Object"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ TypeParameters
   ├─ TypeParameter "T"
   │  └─ Annotation "A"
   │     └─ ClassType "A"
   └─ TypeParameter "S" [ @TypeBound = true() ]
      ├─ Annotation "B"
      │  └─ ClassType "B"
      └─ ClassType "Object"
         └─ Annotation "C"
            └─ ClassType "C"
{% endhighlight %}

<ul>
  <li>TypeParameter<em>s</em> now only can have TypeParameter as a child</li>
  <li>Annotations that apply to the param are <em>in</em> the param</li>
  <li>Annotations that apply to the bound are <em>in</em> the type</li>
  <li>This removes the need for TypeBound, because annotations are cleanly placed.</li>
</ul>

</td></tr>

<tr><td>
Enum constants

{% highlight java %}
enum E {
  @A E1, @B E2;
}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ EnumBody
  ├─ Annotation "A"
  │  └─ MarkerAnnotation "A"
  │     └─ Name "A"
  ├─ EnumConstant "E1"
  ├─ Annotation "B"
  │  └─ MarkerAnnotation "B"
  │     └─ Name "B"
  └─ EnumConstant "E2"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ EnumBody
   ├─ EnumConstant "E1"
   │  ├─ ModifierList
   │  │  └─ Annotation "A"
   │  │     └─ ClassType "A"
   │  └─ VariableId "E1"
   └─ EnumConstant "E2"
      ├─ ModifierList
      │  └─ Annotation "B"
      │     └─ ClassType "B"
      └─ VariableId "E2"
{% endhighlight %}

<ul>
  <li>Annotations are not just randomly in the enum body anymore</li>
</ul>
</td></tr>
</table>

</details>

#### Types

##### Type and ReferenceType

* What:
  * {% jdoc jast::ASTType %} and {% jdoc jast::ASTReferenceType %} have been turned into
    interfaces, implemented by {% jdoc jast::ASTPrimitiveType %}, {% jdoc jast::ASTClassType %},
    and the new node {% jdoc jast::ASTArrayType %}. This reduces the depth of the relevant
    subtrees, and allows to explore them more easily and consistently.
* Why:
  * some syntactic contexts only allow reference types, other allow any kind of type. If you want to match all types
    of a program, then matching Type would be the intuitive solution. But in 6.0.x, it wouldn't have sufficed,
    since in some contexts, no Type node was pushed, only a ReferenceType
  * Regardless of the original syntactic context, any reference type *is* a type, and searching for ASTType should
    yield all the types in the tree.
  * Using interfaces allows to abstract behaviour and make a nicer and safer API.
* **Migrating**
  * There is currently no way to match abstract types (or interfaces) with XPath, so `Type`
    and `ReferenceType` name tests won't match anything anymore.
  * `Type/ReferenceType/ClassOrInterfaceType` ➡️ `ClassType`
  * `Type/PrimitiveType` ➡️ `PrimitiveType`.
  * `Type/ReferenceType[@ArrayDepth > 1]/ClassOrInterfaceType` ➡️ `ArrayType/ClassType`.
  * `Type/ReferenceType/PrimitiveType` ➡️ `ArrayType/PrimitiveType`.
  * Note that in most cases you should check the type of a variable with e.g.
    `VariableId[pmd-java:typeIs("java.lang.String[]")]` because it
    considers the additional dimensions on declarations like `String foo[];`.
    The Java equivalent is `TypeHelper.isA(id, String[].class);`

<details>
  <summary markdown="span">Type and ReferenceType Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
// in the context of a variable declaration
List<String> strs;
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Type (1)
   └─ ReferenceType
      └─ ClassOrInterfaceType "List"
         └─ TypeArguments
            └─ TypeArgument
               └─ ReferenceType (2)
                  └─ ClassOrInterfaceType "String"
{% endhighlight %}

<ol>
  <li>Notice that there is a Type node here, since a local var can have a primitive type.</li>
  <li>In contrast, notice that there is no Type here, since only reference types are allowed as type arguments.</li>
</ol>

</td>
<td>
{% highlight js %}
└─ ClassType "List"
   └─ TypeArguments
      └─ ClassType "String"
{% endhighlight %}

<ul>
  <li>ClassType implements ASTReferenceType, which implements ASTType.</li>
</ul>

</td>
</tr>
</table>

</details>

##### Array changes

* What: Additional nodes {% jdoc jast::ASTArrayType %}, {% jdoc jast::ASTArrayTypeDim %},
  {% jdoc jast::ASTArrayDimensions %}, {% jdoc jast::ASTArrayAllocation %}.
* Why: Support annotated array types ([[java] Java8 parsing corner case with annotated array types (#997)](https://github.com/pmd/pmd/issues/997))
* Related issue: [[java] Simplify array allocation expressions (#1981)](https://github.com/pmd/pmd/pull/1981)

<details>
  <summary markdown="span">Array Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
String[][] myArray;
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Type[ @ArrayType = true() ]
   └─ ReferenceType
      └─ ClassOrInterfaceType[ @Array = true() ][ @ArrayDepth = 2 ] "String"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ArrayType[ @ArrayDepth = 2 ]
   ├─ ClassType "String"
   └─ ArrayDimensions[ @Size = 2 ]
      ├─ ArrayTypeDim
      └─ ArrayTypeDim
{% endhighlight %}
</td>
</tr>

<tr><td>
{% highlight java %}
String @Annotation1[] @Annotation2[] myArray;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Type[ @ArrayType = true() ]
   └─ ReferenceType
      ├─ ClassOrInterfaceType[ @Array = true() ][ @ArrayDepth = 2 ] "String"
      ├─ Annotation "Annotation1"
      │  └─ MarkerAnnotation "Annotation1"
      │     └─ Name "Annotation1"
      └─ Annotation "Annotation2"
         └─ MarkerAnnotation "Annotation2"
            └─ Name "Annotation2"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ArrayType[ @ArrayDepth = 2 ]
   ├─ ClassType "String"
   └─ ArrayDimensions[ @Size = 2 ]
      ├─ ArrayTypeDim
      │  └─ Annotation "Annotation1"
      │     └─ ClassType "Annotation1"
      └─ ArrayTypeDim
         └─ Annotation "Annotation2"
            └─ ClassType "Annotation2"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
new int[2][];
new @Bar int[3][2];
new Foo[] { f, g };
{% endhighlight %}
</td><td>
{% highlight js %}
└─ AllocationExpression
   ├─ PrimitiveType "int"
   └─ ArrayDimsAndInits[ @ArrayDepth = 2 ]
      └─ Expression
         └─ PrimaryExpression
            └─ PrimaryPrefix
               └─ Literal "2"

└─ AllocationExpression
   ├─ Annotation "Bar"
   │  └─ MarkerAnnotation "Bar"
   │     └─ Name "Bar"
   ├─ PrimitiveType "int"
   └─ ArrayDimsAndInits[ @ArrayDepth = 2 ]
      ├─ Expression
      │  └─ PrimaryExpression
      │     └─ PrimaryPrefix
      │        └─ Literal "3"
      └─ Expression
         └─ PrimaryExpression
            └─ PrimaryPrefix
               └─ Literal "2"

└─ AllocationExpression
   ├─ ClassOrInterfaceType "Foo"
   └─ ArrayDimsAndInits[ @ArrayDepth = 1 ]
      └─ ArrayInitializer
         ├─ VariableInitializer
         │  └─ Expression
         │     └─ PrimaryExpression
         │        └─ PrimaryPrefix
         │           └─ Name "f"
         └─ VariableInitializer
            └─ Expression
               └─ PrimaryExpression
                  └─ PrimaryPrefix
                     └─ Name "g"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ArrayAllocation[ @ArrayDepth = 2 ]
   └─ ArrayType[ @ArrayDepth = 2 ]
      ├─ PrimitiveType "int"
      └─ ArrayDimensions[ @Size = 2]
         ├─ ArrayDimExpr
         │  └─ NumericLiteral "2"
         └─ ArrayTypeDim

└─ ArrayAllocation[ @ArrayDepth = 2 ]
   └─ ArrayType[ @Array Depth = 2 ]
      ├─ PrimitiveType "int"
      │  └─ Annotation "Bar"
      │     └─ ClassType "Bar"
      └─ ArrayDimensions[ @Size = 2 ]
         ├─ ArrayDimExpr
         │  └─ NumericLiteral "3"
         └─ ArrayDimExpr
            └─ NumericLiteral "2"

└─ ArrayAllocation[ @ArrayDepth = 1 ]
   └─ ArrayType[ @ArrayDepth = 1 ]
   │  ├─ ClassType "Foo"
   │  └─ ArrayDimensions[ @Size = 1 ]
   │     └─ ArrayTypeDim
   └─ ArrayInitializer[ @Length = 2 ]
      ├─ VariableAccess "f"
      └─ VariableAccess "g"
{% endhighlight %}
</td></tr>
</table>

</details>

##### ClassType nesting

* What: {% jdoc jast::ASTClassType %} (formerly ASTClassOrInterfaceType) appears to be left recursive now,
  and encloses its qualifying type.
* Why: To preserve the position of annotations and type arguments
* Related issue: [[java] ClassOrInterfaceType AST improvements (#1150)](https://github.com/pmd/pmd/issues/1150)

<details>
  <summary markdown="span">ClassType Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
Map.Entry<K,V>
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassOrInterfaceType "Map.Entry"
   └─ TypeArguments
      ├─ TypeArgument
      │  └─ ReferenceType
      │     └─ ClassOrInterfaceType "K"
      └─ TypeArgument
         └─ ReferenceType
            └─ ClassOrInterfaceType "V"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassType "Entry"
   ├─ ClassType "Map"
   └─ TypeArguments[ @Size = 2 ]
      ├─ ClassType "K"
      └─ ClassType "V"
{% endhighlight %}
</td>
</tr>

<tr><td>
{% highlight java %}
First<K>.Second.Third<V>
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ClassOrInterfaceType "First.Second.Third"
   ├─ TypeArguments
   │  └─ TypeArgument
   │     └─ ReferenceType
   │        └─ ClassOrInterfaceType "K"
   └─ TypeArguments
      └─ TypeArgument
         └─ ReferenceType
            └─ ClassOrInterfaceType "V"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ClassType "Third"
   ├─  ClassType "Second"
   │   └─ ClassType "First"
   │      └─ TypeArguments[ @Size = 1]
   │         └─ ClassType "K"
   └─ TypeArguments[ @Size = 1 ]
      └─ ClassType "V"
{% endhighlight %}
</td></tr>
</table>

</details>

##### TypeArgument and WildcardType

* What:
  * {% jdoc_old jast::ASTTypeArgument %} is removed. Instead, the {% jdoc jast::ASTTypeArguments %} node contains directly
    a sequence of {% jdoc jast::ASTType %} nodes. To support this, the new node type {% jdoc jast::ASTWildcardType %}
    captures the syntax previously parsed as a TypeArgument.
  * The {% jdoc_old jast::ASTWildcardBounds %} node is removed. Instead, the bound is a direct child of the WildcardType.
* Why: Because wildcard types are types in their own right, and having a node to represent them skims several levels
  of nesting off.

<details>
  <summary markdown="span">TypeArgument and WildcardType Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
Entry<String, ? extends Node>
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassOrInterfaceType "Entry"
   └─ TypeArguments
      ├─ TypeArgument
      │  └─ ReferenceType
      │     └─ ClassOrInterfaceType "String"
      └─ TypeArgument[ @Wildcard = true() ]
         └─ WildcardBounds[ @UpperBound = true() ]
            └─ ReferenceType
               └─ ClassOrInterfaceType "Node"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassType "Entry"
   └─ TypeArguments[ @Size = 2 ]
      ├─ ClassType "String"
      └─ WildcardType[ @UpperBound = true() ]
         └─ ClassType "Node"
{% endhighlight %}
</td>
</tr>

<tr><td>
{% highlight java %}
List<?>
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassOrInterfaceType "List"
   └─ TypeArguments
      └─ TypeArgument[ @Wildcard = true() ]
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassType "List"
   └─ TypeArguments[ @Size = 1 ]
      └─ WildcardType[ @UpperBound = true() ]
{% endhighlight %}
</td>
</tr>
</table>

</details>

#### Declarations

##### Import and Package declarations

* What: Remove the Name node in imports and package declaration nodes.
* Why: Name is a TypeNode, but it's equivalent to {% jdoc jast::ASTAmbiguousName %} in that it describes nothing
  about what it represents. The name in an import may represent a method name, a type name, a field name...
  It's too ambiguous to treat in the parser and could just be the image of the import, or package, or module.
* Related issue: [[java] Remove Name nodes in Import- and PackageDeclaration (#1888)](https://github.com/pmd/pmd/pull/1888)

<details>
  <summary markdown="span">Import and Package declarations Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
import java.util.ArrayList;
import static java.util.Comparator.reverseOrder;
import java.util.*;
{% endhighlight %}
</td><td>
{% highlight js %}
├─ ImportDeclaration
│  └─ Name "java.util.ArrayList"
├─ ImportDeclaration[ @Static=true() ]
│  └─ Name "java.util.Comparator.reverseOrder"
└─ ImportDeclaration[ @ImportOnDemand = true() ]
   └─ Name "java.util"
{% endhighlight %}
</td><td>
{% highlight js %}
├─ ImportDeclaration "java.util.ArrayList"
├─ ImportDeclaration[ @Static = true() ] "java.util.Comparator.reverseOrder"
└─ ImportDeclaration[ @ImportOnDemand = true() ] "java.util"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
package com.example.tool;
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ PackageDeclaration
   └─ Name "com.example.tool"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ PackageDeclaration "com.example.tool"
   └─ ModifierList
{% endhighlight %}
</td></tr>
</table>

</details>

##### Modifier lists

* What: {% jdoc jast::ModifierOwner %} (formerly AccessNode) is now based on a node: {% jdoc jast::ASTModifierList %}.
  That node represents
  modifiers occurring before a declaration. It provides a flexible API to query modifiers, both explicit and
  implicit. All declaration nodes now have such a modifier list, even if it's implicit (no explicit modifiers).
* Why: ModifierOwner (formerly AccessNode) gave a lot of irrelevant methods to its subtypes.
  E.g. `ASTFieldDeclaration::isSynchronized`
  makes no sense. Now, these irrelevant methods don't clutter the API. The API of ModifierList is both more
  general and flexible.
* Related issue: [[java] Rework AccessNode (#2259)](https://github.com/pmd/pmd/pull/2259)

<details>
  <summary markdown="span">Modifier lists Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
Method

{% highlight java %}
@A
public void set(final int x, int y) { }
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ClassOrInterfaceBodyDeclaration
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   └─ MethodDeclaration[ @Public = true() ] "set"
      ├─ ResultType[ @Void = true() ]
      └─ MethodDeclarator
         └─ FormalParameters[ @Size = 2 ]
            ├─ FormalParameter[ @Final = true() ]
            │  ├─ Type
            │  │  └─ PrimitiveType "int"
            │  └─ VariableDeclaratorId "x"
            └─ FormalParameter[ @Final = false() ]
               ├─ Type
               │  └─ PrimitiveType "int"
               └─ VariableDeclaratorId "y"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration[ pmd-java:modifiers() = 'public' ] "set"
   ├─ ModifierList
   │  └─ Annotation "A"
   │     └─ ClassType "A"
   ├─ VoidType
   └─ FormalParameters
      ├─ FormalParameter[ pmd-java:modifiers() = 'final' ]
      │  ├─ ModifierList
      │  └─ VariableId "x"
      └─ FormalParameter[ pmd-java:modifiers() = () ]
         ├─ ModifierList
         └─ VariableId "y"
{% endhighlight %}
</td></tr>

<tr><td>
Top-level type declaration

{% highlight java %}
public @A class C {}
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ TypeDeclaration
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   └─ ClassOrInterfaceDeclaration[ @Public = true() ] "C"
      └─ ClassOrInterfaceBody
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassDeclaration[ pmd-java:modifiers() = 'public' ] "C"
   ├─ ModifierList
   │  └─ Annotation "A"
   │     └─ ClassType "A"
   └─ ClassBody
{% endhighlight %}
</td>
</tr>
</table>

</details>

##### Flattened body declarations

* What: Removes {% jdoc_old jast::ASTClassOrInterfaceBodyDeclaration %}, {% jdoc_old jast::ASTTypeDeclaration %},
  and {% jdoc_old jast::ASTAnnotationTypeMemberDeclaration %}.
  These were unnecessary since annotations are nested (see above [Annotation nesting](#annotation-nesting)).
* Why: This flattens the tree, makes it less verbose and simpler.
* Related issue: [[java] Flatten body declarations (#2300)](https://github.com/pmd/pmd/pull/2300)

<details>
  <summary markdown="span">Flattened body declarations Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
public class Flat {
    private int f;
}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CompilationUnit
   └─ TypeDeclaration
      └─ ClassOrInterfaceDeclaration "Flat"
         └─ ClassOrInterfaceBody
            └─ ClassOrInterfaceBodyDeclaration
               └─ FieldDeclaration
                  ├─ Type
                  │  └─ PrimitiveType "int"
                  └─ VariableDeclarator
                     └─ VariableDeclaratorId "f"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CompilationUnit
   └─ ClassDeclaration "Flat"
      ├─ ModifierList
      └─ ClassBody
         └─ FieldDeclaration
            ├─ ModifierList
            ├─ PrimitiveType "int"
            └─ VariableDeclarator
               └─ VariableId "f"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
public @interface FlatAnnotation {
    String value() default "";
}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CompilationUnit
   └─ TypeDeclaration
      └─ AnnotationTypeDeclaration "FlatAnnotation"
         └─ AnnotationTypeBody
            └─ AnnotationTypeMemberDeclaration
               └─ AnnotationMethodDeclaration "value"
                  ├─ Type
                  │  └─ ReferenceType
                  │     └─ ClassOrInterfaceType "String"
                  └─ DefaultValue
                     └─ MemberValue
                        └─ PrimaryExpression
                           └─ PrimaryPrefix
                              └─ Literal "\"\""
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CompilationUnit
   └─ AnnotationTypeDeclaration "FlatAnnotation"
      ├─ ModifierList
      └─ AnnotationTypeBody
         └─ MethodDeclaration "value"
            ├─ ModifierList
            ├─ ClassType "String"
            ├─ FormalParameters
            └─ DefaultValue
               └─ StringLiteral "\"\""
{% endhighlight %}
</td></tr>
</table>

</details>

##### Module declarations

* What: Removes the generic Name node and uses instead {% jdoc jast::ASTClassType %} where appropriate. Also
  uses specific node types for different directives (requires, exports, uses, provides).
* Why: Simplify queries, support type resolution
* Related issue: [[java] Improve module grammar (#3890)](https://github.com/pmd/pmd/pull/3890)

<details>
  <summary markdown="span">Module declarations Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
open module com.example.foo {
    requires com.example.foo.http;
    requires java.logging;
    requires transitive com.example.foo.network;

    exports com.example.foo.bar;
    exports com.example.foo.internal to com.example.foo.probe;

    uses com.example.foo.spi.Intf;

    provides com.example.foo.spi.Intf with com.example.foo.Impl;
}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CompilationUnit
   └─ ModuleDeclaration[ @Image = 'com.example.foo' ][ @Open = true() ]
      ├─ ModuleDirective[ @Type = 'REQUIRES' ]
      │  └─ ModuleName[ @Image = 'com.example.foo.http' ]
      ├─ ModuleDirective[ @Type = 'REQUIRES' ]
      │  └─ ModuleName[ @Image = 'java.logging' ]
      ├─ ModuleDirective[ @Type = 'REQUIRES' ][ @RequiresModifier = 'TRANSITIVE' ]
      │  └─ ModuleName[ @Image = 'com.example.foo.network' ]
      ├─ ModuleDirective[ @Type = 'EXPORTS' ]
      │  └─ Name[ @Image = 'com.example.foo.bar' ]
      ├─ ModuleDirective[ @Type = 'EXPORTS' ]
      │  ├─ Name[ @Image = 'com.example.foo.internal' ]
      │  └─ ModuleName[ @Image = 'com.example.foo.probe' ]
      ├─ ModuleDirective[ @Type = 'USES' ]
      │  └─ Name[ @Image = 'com.example.foo.spi.Intf' ]
      └─ ModuleDirective[ @Type = 'PROVIDES' ]
         ├─ Name[ @Image = 'com.example.foo.spi.Intf' ]
         └─ Name[ @Image = 'com.example.foo.Impl' ]
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ CompilationUnit
   └─ ModuleDeclaration[ @Name = 'com.example.foo' ][ @Open = true() ]
      ├─ ModuleName[ @Name = 'com.example.foo' ]
      ├─ ModuleRequiresDirective
      │  └─ ModuleName[ @Name = 'com.example.foo.http' ]
      ├─ ModuleRequiresDirective
      │  └─ ModuleName[ @Name = 'java.logging' ]
      ├─ ModuleRequiresDirective[ @Transitive = true ]
      │  └─ ModuleName[ @Name = 'com.example.foo.network' ]
      ├─ ModuleExportsDirective[ @PackageName = 'com.example.foo.bar' ]
      ├─ ModuleExportsDirective[ @PackageName = 'com.example.foo.internal' ]
      │  └─ ModuleName [ @Name = 'com.example.foo.probe' ]
      ├─ ModuleUsesDirective
      │  └─ ClassType[ pmd-java:typeIs("com.example.foo.spi.Intf") ]
      └─ ModuleProvidesDirective
         ├─ ClassType[ pmd-java:typeIs("com.example.foo.spi.Intf") ]
         └─ ClassType[ pmd-java:typeIs("com.example.foo.Impl") ]
{% endhighlight %}
</td></tr>
</table>

</details>

##### Anonymous class declarations

* What: A separate node type {% jdoc jast::ASTAnonymousClassDeclaration %} is introduced for anonymous classes.
* Why: Unify the AST for type declarations including anonymous class declaration in constructor calls
  and enums.
* Related issues:
  * [[java] Add new node for anonymous class declaration (#905)](https://github.com/pmd/pmd/issues/905)
  * [[java] New expression and type grammar (#1759)](https://github.com/pmd/pmd/pull/1759)

<details>
  <summary markdown="span">Anonymous class declarations Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
Object anonymous = new Object() {  };
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ LocalVariableDeclaration
   ├─ Type
   │  └─ ReferenceType
   │     └─ ClassOrInterfaceType[ @Image = 'Object' ]
   └─ VariableDeclarator
      ├─ VariableDeclaratorId "anonymous"
      └─ VariableInitializer
         └─ Expression
            └─ PrimaryExpression
               └─ PrimaryPrefix
                  └─ AllocationExpression
                     ├─ ClassOrInterfaceType[ @AnonymousClass = true() ][ @Image = 'Object' ]
                     ├─ Arguments
                     └─ ClassOrInterfaceBody
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ LocalVariableDeclaration
   ├─ ModifierList
   ├─ ClassType[ @SimpleName = 'Object' ]
   └─ VariableDeclarator
      ├─ VariableId[ @Name = 'anonymous' ]
      └─ ConstructorCall
         ├─ ClassType[ @SimpleName = 'Object' ]
         ├─ ArgumentList
         └─ AnonymousClassDeclaration
            ├─ ModifierList
            └─ ClassBody
{% endhighlight %}
</td></tr>
</table>

</details>

#### Method and Constructor declarations

##### Method grammar simplification

* What: Simplify and align the grammar used for method and constructor declarations. The methods in an annotation
  type are now also method declarations.
* Why: The method declaration had a nested node "MethodDeclarator", which was not available for constructor
  declarations. This made it difficult to write rules, that concern both methods and constructors without
  explicitly differentiate between these two.
* Related issue: [[java] Align method and constructor declaration grammar (#2034)](https://github.com/pmd/pmd/pull/2034)

<details>
  <summary markdown="span">Method grammar Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
public class Sample {
    public Sample(int arg) throws Exception {
        super();
        greet(arg);
    }
    public void greet(int arg) throws Exception {
        System.out.println("Hello");
    }
}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ClassOrInterfaceBody
   ├─ ClassOrInterfaceBodyDeclaration
   │  └─ ConstructorDeclaration[ @Image = 'Sample' ]
   │     ├─ FormalParameters
   │     │  └─ FormalParameter
   │     │     ├─ ...
   │     ├─ NameList
   │     │  └─ Name[ @Image = 'Exception' ]
   │     ├─ ExplicitConstructorInvocation
   │     │  └─ Arguments
   │     └─ BlockStatement
   │        └─ Statement
   │           └─ ...
   └─ ClassOrInterfaceBodyDeclaration
      └─ MethodDeclaration[ @Name = 'greet' ]
         ├─ ResultType
         ├─ MethodDeclarator[ @Image = 'greet' ]
         │  └─ FormalParameters
         │     └─ FormalParameter
         │        ├─ ...
         ├─ NameList
         │  └─ Name[ @Image = 'Exception' ]
         └─ Block
            └─ BlockStatement
               └─ Statement
                  └─ ...
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassBody
   ├─ ConstructorDeclaration[ @Name = 'Sample' ]
   │  ├─ ModifierList
   │  ├─ FormalParameters
   │  │  └─ FormalParameter
   │  │     ├─ ...
   │  ├─ ThrowsList
   │  │  └─ ClassType[ @SimpleName = 'Exception' ]
   │  └─ Block
   │     ├─ ExplicitConstructorInvocation
   │     │  └─ ArgumentList
   │     └─ ExpressionStatement
   │        └─ ...
   └─ MethodDeclaration[ @Name = 'greet' ]
      ├─ ModifierList
      ├─ VoidType
      ├─ FormalParameters
      │  └─ FormalParameter
      │     ├─ ...
      ├─ ThrowsList
      │  └─ ClassType[ @SimpleName = 'Exception' ]
      └─ Block
         └─ ExpressionStatement
            └─ ...
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
public @interface MyAnnotation {
    int value() default 1;
}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ AnnotationTypeDeclaration[ @SimpleName = 'MyAnnotation' ]
   └─ AnnotationTypeBody
      └─ AnnotationTypeMemberDeclaration
         └─ AnnotationMethodDeclaration[ @Image = 'value' ]
            ├─ Type ...
            └─ DefaultValue ...
{% endhighlight %}
</td><td>
{% highlight js %}
└─ AnnotationTypeDeclaration[ @SimpleName = 'MyAnnotation' ]
   ├─ ModifierList
   └─ AnnotationTypeBody
      └─ MethodDeclaration[ @Name = 'value' ]
         ├─ ModifierList
         ├─ PrimitiveType
         ├─ FormalParameters
         └─ DefaultValue ...
{% endhighlight %}
</td></tr>
</table>

</details>

##### Formal parameters

* What: Use {% jdoc jast::ASTFormalParameter %} only for method and constructor declaration. Lambdas use
  {% jdoc jast::ASTLambdaParameter %}, catch clauses use {% jdoc jast::ASTCatchParameter %}.
* Why: FormalParameter's API is different from the other ones.
  * FormalParameter must mention a type node.
  * LambdaParameter can be inferred
  * CatchParameter cannot be varargs
  * CatchParameter can have multiple exception types (a {% jdoc jast::ASTUnionType %} now)

<details>
  <summary markdown="span">Formal parameters Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
try {

} catch (@A IOException | IllegalArgumentException e) {

}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ TryStatement
   ├─ Block
   └─ CatchStatement
      ├─ FormalParameter
      │  ├─ Annotation[ @AnnotationName = 'A' ]
      │  │  └─ MarkerAnnotation[ @AnnotationName = 'A' ]
      │  │     └─ Name[ @Image = 'A' ]
      │  ├─ Type
      │  │  └─ ReferenceType
      │  │     └─ ClassOrInterfaceType[ @Image = 'IOException' ]
      │  ├─ Type
      │  │  └─ ReferenceType
      │  │     └─ ClassOrInterfaceType[ @Image = 'IllegalArgumentException' ]
      │  └─ VariableDeclaratorId[ @Name = 'e' ]
      └─ Block
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ TryStatement
   ├─ Block
   └─ CatchClause
      ├─ CatchParameter
      │  ├─ ModifierList
      │  │  └─ Annotation[ @SimpleName = 'A' ]
      │  │     └─ ClassType[ @SimpleName = 'A' ]
      │  ├─ UnionType
      │  │  ├─ ClassType[ @SimpleName = 'IOException' ]
      │  │  └─ ClassType[ @SimpleName = 'IllegalArgumentException' ]
      │  └─ VariableId[ @Name = 'e' ]
      └─ Block
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
(a, b) -> {};
c -> {};
(@A var d) -> {};
(@A int e) -> {};
{% endhighlight %}
</td><td>
{% highlight js %}
└─ StatementExpression
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ LambdaExpression
            ├─ VariableDeclaratorId[ @Name = 'a' ]
            ├─ VariableDeclaratorId[ @Name = 'b' ]
            └─ Block

└─ StatementExpression
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ LambdaExpression
            ├─ VariableDeclaratorId[ @Name = 'c' ]
            └─ Block

└─ StatementExpression
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ LambdaExpression
            ├─ FormalParameters
            │  └─ FormalParameter
            │     ├─ Annotation[ @AnnotationName = 'A' ]
            │     │  └─ MarkerAnnotation[ @AnnotationName = 'A' ]
            │     │     └─ Name[ @Image = 'A' ]
            │     └─ VariableDeclaratorId[ @Name = 'd' ]
            └─ Block

└─ StatementExpression
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ LambdaExpression
            ├─ FormalParameters
            │  └─ FormalParameter
            │     ├─ Annotation[ @AnnotationName = 'A' ]
            │     │  └─ MarkerAnnotation[ @AnnotationName = 'A' ]
            │     │     └─ Name[ @Image = 'A' ]
            │     ├─ Type
            │     │  └─ PrimitiveType[ @Image = 'int' ]
            │     └─ VariableDeclaratorId[ @Name = 'e' ]
            └─ Block
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ExpressionStatement
   └─ LambdaExpression
      ├─ LambdaParameterList
      │  ├─ LambdaParameter
      │  │  ├─ ModifierList
      │  │  └─ VariableId[ @Name = 'a' ]
      │  └─ LambdaParameter
      │     ├─ ModifierList
      │     └─ VariableId[ @Name = 'b' ]
      └─ Block

└─ ExpressionStatement
   └─ LambdaExpression
      ├─ LambdaParameterList
      │  └─ LambdaParameter
      │     ├─ ModifierList
      │     └─ VariableId[ @Name = 'c' ]
      └─ Block

└─ ExpressionStatement
   └─ LambdaExpression
      ├─ LambdaParameterList
      │  └─ LambdaParameter
      │     ├─ ModifierList
      │     │  └─ Annotation[ @SimpleName = 'A' ]
      │     │     └─ ClassType[ @SimpleName = 'A' ]
      │     └─ VariableId[ @Name = 'd' ]
      └─ Block

└─ ExpressionStatement
   └─ LambdaExpression
      ├─ LambdaParameterList
      │  └─ LambdaParameter
      │     ├─ ModifierList
      │     │  └─ Annotation[ @SimpleName = 'A' ]
      │     │     └─ ClassType[ @SimpleName = 'A' ]
      │     ├─ PrimitiveType[ @Kind = 'int' ]
      │     └─ VariableId[ @Name = 'e' ]
      └─ Block
{% endhighlight %}
</td></tr>
</table>

</details>

##### New node for explicit receiver parameter

* What: A separate node type {% jdoc jast::ASTReceiverParameter %} is introduced to differentiate it from formal parameters.
* Why: A receiver parameter is not a formal parameter, even though it looks like one: it doesn't declare a variable,
  and doesn't affect the arity of the method or constructor. It's so rarely used that giving it its own node avoids
  matching it by mistake and simplifies the API and grammar of the ubiquitous {% jdoc jast::ASTFormalParameter %}
  and {% jdoc jast::ASTVariableId %}.
* Related issue: [[java] Separate receiver parameter from formal parameter (#1980)](https://github.com/pmd/pmd/pull/1980)

<details>
  <summary markdown="span">explicit receiver parameter Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
void myMethod(@A Foo this, Foo other) {}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ FormalParameters (1)
   ├─ FormalParameter[ @ExplicitReceiverParameter = true() ]
   │  ├─ Annotation "A"
   │  │  └─ MarkerAnnotation "A"
   │  │     └─ Name "A"
   │  ├─ Type
   │  │  └─ ReferenceType
   │  │     └─ ClassOrInterfaceType "Foo"
   │  └─ VariableDeclaratorId[ @ExplicitReceiverParameter = true() ] "this"
   └─ FormalParameter
      ├─ Type
      │  └─ ReferenceType
      │     └─ ClassOrInterfaceType "Foo"
      └─ VariableDeclaratorId "other"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ FormalParameters (1)
   ├─ ReceiverParameter
   │  └─ ClassType "Foo"
   │     └─ Annotation "A"
   │        └─ ClassType "A"
   └─ FormalParameter
      ├─ ModifierList
      ├─ ClassType "Foo"
      └─ VariableId "other"
{% endhighlight %}
</td></tr>
</table>

</details>

##### Varargs

* What: parse the varargs ellipsis as an {% jdoc jast::ASTArrayType %}.
* Why: this improves regularity of the grammar, and allows type annotations to be added to the ellipsis

<details>
  <summary markdown="span">Varargs Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
void myMethod(int... is) {}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ FormalParameter[ @Varargs = true() ]
   ├─ Type
   │  └─ PrimitiveType "int"
   └─ VariableDeclaratorId "is"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ FormalParameter[ @Varargs = true() ]
   ├─ ModifierList
   ├─ ArrayType
   │  ├─ PrimitiveType "int"
   │  └─ ArrayDimensions
   │     └─ ArrayTypeDim[ @Varargs = true() ]
   └─ VariableId "is"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
void myMethod(int @A ... is) {}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ FormalParameter[ @Varargs = true() ]
   ├─ Type
   │  └─ PrimitiveType "int"
   ├─ Annotation "A"
   │  └─ MarkerAnnotation "A"
   │     └─ Name "A"
   └─ VariableDeclaratorId "is"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ FormalParameter[ @Varargs = true() ]
   ├─ ModifierList
   ├─ ArrayType
   │  ├─ PrimitiveType "int"
   │  └─ ArrayDimensions
   │     └─ ArrayTypeDim[ @Varargs = true() ]
   │        └─ Annotation "A"
   │           └─ ClassType "A"
   └─ VariableId "is"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
void myMethod(int[]... is) {}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ FormalParameter[ @Varargs = true() ]
   ├─ Type[ @ArrayType = true() ]
   │  └─ ReferenceType
   │     └─ PrimitiveType "int"
   └─ VariableDeclaratorId "is"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ FormalParameter[ @Varargs = true() ]
   ├─ ModifierList
   ├─ ArrayType (2)
   │  ├─ PrimitiveType "int"
   │  └─ ArrayDimensions (2)
   │     ├─ ArrayTypeDim
   │     └─ ArrayTypeDim[ @Varargs = true() ]
   └─ VariableId "is"
{% endhighlight %}
</td></tr>
</table>

</details>

##### Add void type node to replace ResultType

* What: Add a {% jdoc jast::ASTVoidType %} node to replace {% jdoc_old jast::ASTResultType %}.
* Why: This means we don't need the ResultType wrapper when the method is not void, and the result type node is never null.
* Related issue: [[java] Add void type node to replace ResultType (#2715)](https://github.com/pmd/pmd/pull/2715)

<details>
  <summary markdown="span">Void Type Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
void foo();
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration "foo"
   ├─ ResultType[ @Void = true() ]
   └─ MethodDeclarator
      └─ FormalParameters
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration "foo"
   ├─ ModifierList
   ├─ VoidType
   └─ FormalParameters
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
int foo();
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration "foo"
   ├─ ResultType[ @Void = false() ]
   │  └─ Type
   │     └─ PrimitiveType "int"
   └─ MethodDeclarator
      └─ FormalParameters
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration "foo"
   ├─ ModifierList
   ├─ PrimitiveType "int"
   └─ FormalParameters
{% endhighlight %}
</td></tr>
</table>

</details>

#### Statements

##### Statements are flattened

* What: Statements are flattened. There are no superfluous BlockStatement and Statement nodes anymore.
  All children of a {% jdoc jast::ASTBlock %} are by definition
  {% jdoc jast::ASTStatement %}s, which is now an interface implemented by all statements.
* Why: This simplifies the tree traversal. The removed nodes BlockStatement and Statement didn't add any
  additional information. We only need a Statement abstraction. BlockStatement was used to enforce, that no
  variable or local class declaration is found alone as the child of e.g. an unbraced if, else, for, etc.
  This is a parser-only distinction that's not that useful for analysis later on.
* Related issue: [[java] Improve statement grammar (#2164)](https://github.com/pmd/pmd/pull/2164)

<details>
  <summary markdown="span">Statements Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
int i;
i = 1;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Block
   ├─ BlockStatement
   │  └─ LocalVariableDeclaration
   │     ├─ Type
   │     │  └─ PrimitiveType "int"
   │     └─ VariableDeclarator
   │        └─ VariableDeclaratorId "i"
   └─ BlockStatement
      └─ Statement
         └─ StatementExpression
            ├─ PrimaryExpression
            │  └─ PrimaryPrefix
            │     └─ Name "i"
            ├─ AssignmentOperator "="
            └─ Expression
               └─ PrimaryExpression
                  └─ PrimaryPrefix
                     └─ Literal "1"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Block
   ├─ LocalVariableDeclaration
   │  ├─ ModifierList
   │  ├─ PrimitiveType "int"
   │  └─ VariableDeclarator
   │     └─ VariableId "i"
   └─ ExpressionStatement
      └─ AssignmentExpression "="
         ├─ VariableAccess "i"
         └─ NumericLiteral "1"
{% endhighlight %}
</td></tr>
</table>

</details>

##### New node for For-each statements

* What: New node for For-each statements: {% jdoc jast::ASTForeachStatement %} instead of ForStatement.
* Why: This makes it a lot easier to distinguish in the AST between For-loops and For-Each-loops. E.g. some
  rules only apply to one or the other, and it was complicated to write a rule that works with both different
  subtrees (for loops have additional children ForInit and ForUpdate)
* Related issue: [[java] Improve statement grammar (#2164)](https://github.com/pmd/pmd/pull/2164)

<details>
  <summary markdown="span">For-each statement Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
for (String s : List.of("a", "b")) { }
{% endhighlight %}
</td><td>
{% highlight js %}
└─ BlockStatement
   └─ Statement
      └─ ForStatement[ @Foreach = true() ]
         ├─ LocalVariableDeclaration
         │  ├─ Type
         │  │  └─ ReferenceType
         │  │     └─ ClassOrInterfaceType "String"
         │  └─ VariableDeclarator
         │     └─ VariableDeclaratorId "s"
         ├─ Expression
         │  └─ PrimaryExpression
         │     ├─ PrimaryPrefix
         │     │  └─ Name "List.of"
         │     └─ PrimarySuffix
         │        └─ Arguments (2)
         │           └─ ArgumentList (2)
         │              ├─ Expression
         │              │  └─ PrimaryExpression
         │              │     └─ PrimaryPrefix
         │              │        └─ Literal[ @StringLiteral = true() ][ @Image = '"a"' ]
         │              └─ Expression
         │                 └─ PrimaryExpression
         │                    └─ PrimaryPrefix
         │                       └─ Literal[ @StringLiteral = true() ][ @Image = '"b"' ]
         └─ Statement
            └─ Block
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Block
   └─ ForeachStatement
      ├─ LocalVariableDeclaration
      │  ├─ ModifierList
      │  ├─ ClassType "String"
      │  └─ VariableDeclarator "s"
      │     └─ VariableId "s"
      ├─ MethodCall "of"
      │  ├─ TypeExpression
      │  │  └─ ClassType "List"
      │  └─ ArgumentList (2)
      │     ├─ StringLiteral[ @Image = '"a"' ]
      │     └─ StringLiteral[ @Image = '"b"' ]
      └─ Block
{% endhighlight %}
</td></tr>
</table>

</details>

##### New nodes for ExpressionStatement, LocalClassStatement

* What: Renamed StatementExpression to {% jdoc jast::ASTExpressionStatement %}.
  Added new node {% jdoc jast::ASTLocalClassStatement %}.
* Why: ExpressionStatement is now a {% jdoc jast::ASTStatement %}, that can be used as a child in a
  block. It itself has only one child, which is some kind of {% jdoc jast::ASTExpression %},
  which can be really any kind of expression (like assignment).
  In order to allow local class declarations as part of a block, we introduced {% jdoc jast::ASTLocalClassStatement %}
  which is a statement that carries a type declaration. Now blocks are just a list of statements.
  This allows us to have two distinct hierarchies for expressions and statements.
* Related issue: [[java] Improve statement grammar (#2164)](https://github.com/pmd/pmd/pull/2164)

<details>
  <summary markdown="span">ExpressionStatement, LocalClassStatement Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
i++;
class LocalClass {}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Block
   ├─ BlockStatement
   │  └─ Statement
   │     └─ StatementExpression
   │        └─ PostfixExpression "++"
   │           └─ PrimaryExpression
   │              └─ PrimaryPrefix
   │                 └─ Name "i"
   └─ BlockStatement
      └─ ClassOrInterfaceDeclaration[ @Local = true() ] "LocalClass"
         └─ ClassOrInterfaceBody
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Block
   ├─ ExpressionStatement
   │  └─ UnaryExpression "++"
   │     └─ VariableAccess "i"
   └─ LocalClassStatement
      └─ ClassDeclaration "LocalClass"
         ├─ ModifierList
         └─ ClassBody
{% endhighlight %}
</td></tr>
</table>

</details>

##### Improve try-with-resources grammar

* What: The AST representation of a try-with-resources statement has been simplified.
  It uses now {% jdoc jast::ASTLocalVariableDeclaration %} unless it is a concise try-with-resources.
* Why: Simpler integration try-with-resources into symboltable and type resolution.
* Related issue: [[java] Improve try-with-resources grammar (#1897)](https://github.com/pmd/pmd/pull/1897)

<details>
  <summary markdown="span">Try-With-Resources Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
try (InputStream in = new FileInputStream(); OutputStream out = new FileOutputStream();) { }
{% endhighlight %}
</td><td>
{% highlight js %}
└─ TryStatement
   └─ ResourceSpecification
      └─ Resources
         ├─ Resource
         │  ├─ Type
         │  │  └─ ReferenceType
         │  │     └─ ClassOrInterfaceType "InputStream"
         │  ├─ VariableDeclaratorId "in"
         │  └─ Expression
         │     └─ ...
         └─ Resource
            ├─ Type
            │  └─ ReferenceType
            │     └─ ClassOrInterfaceType "OutputStream"
            ├─ VariableDeclaratorId "out"
            └─ Expression
               └─ ...
{% endhighlight %}
</td><td>
{% highlight js %}
└─ TryStatement
   └─ ResourceList[ @TrailingSemiColon = true() ] (2)
      ├─ Resource[ @ConciseResource = false() ] "in"
      │  └─ LocalVariableDeclaration
      │     ├─ ModifierList
      │     ├─ ClassType "InputStream"
      │     └─ VariableDeclarator
      │        ├─ VariableId "in"
      │        └─ ConstructorCall
      │           ├─ ClassType "FileInputStream"
      │           └─ ArgumentList (0)
      └─ Resource[ @ConciseResource = false() ] "out"
         └─ LocalVariableDeclaration
            ├─ ModifierList
            ├─ ClassType "OutputStream"
            └─ VariableDeclarator
               ├─ VariableId "out"
               └─ ConstructorCall
                  ├─ ClassType "FileOutputStream"
                  └─ ArgumentList (0)
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
InputStream in = new FileInputStream();
try (in) {}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ TryStatement
   └─ ResourceSpecification
      └─ Resources
         └─ Resource "in"
            └─ Name "in"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ TryStatement
   └─ ResourceList[ @TrailingSemiColon = false() ] (1)
      └─ Resource[ @ConciseResource = true() ] "in"
         └─ VariableAccess "in"
{% endhighlight %}
</td></tr>
</table>

</details>

#### Expressions

* {% jdoc jast::ASTExpression %} and {% jdoc jast::ASTPrimaryExpression %} have
  been turned into interfaces. These added no information to the AST and increased
  its depth unnecessarily. All expressions implement the first interface. Both of
  those nodes can no more be found in ASTs.

* **Migrating**:
  * Basically, `Expression/X` or `Expression/PrimaryExpression/X`, just becomes `X`
  * There is currently no way to match abstract or interface types with XPath, so `Expression` or `PrimaryExpression`
    name tests won't match anything anymore. However, the axis step *[@Expression=true()] matches any expression.

##### New nodes for different literals types

* What:
  * {% jdoc jast::ASTLiteral %} has been turned into an interface. 
  * {% jdoc jast::ASTNumericLiteral %}, {% jdoc jast::ASTCharLiteral %}, {% jdoc jast::ASTStringLiteral %},
    and {% jdoc jast::ASTClassLiteral %} are new nodes that implement that interface.
  * ASTLiteral implements {% jdoc jast::ASTPrimaryExpression %}
* Why: The fact that {% jdoc jast::ASTNullLiteral %}
  and {% jdoc jast::ASTBooleanLiteral %} were nested within it but other literals types were all directly represented
  by it was inconsistent, and ultimately that level of nesting was unnecessary.
* Related issue: [[java] New expression and type grammar (#1759)](https://github.com/pmd/pmd/pull/1759)
* **Migrating**:
  * Remove all `/Literal/` segments from your XPath expressions
  * If you tested several types of literals, you can e.g. do it like `/*[self::StringLiteral or self::CharLiteral]/`
  * As usual, use the designer to explore the new AST structure

<details>
  <summary markdown="span">Literals Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
char c = 'c';
boolean b = true;
int i = 1;
double d = 1.0;
String s = "s";
Object n = null;
{% endhighlight %}</td><td>
{% highlight js %}
└─ Literal[ @CharLiteral = true() ] "'c'"
└─ Literal
   └─ BooleanLiteral[ @True = true() ]
└─ Literal[ @IntLiteral = true() ] "1"
└─ Literal[ @DoubleLiteral = true() ] "1.0"
└─ Literal[ @StringLiteral = true() ] "\"s\""
└─ Literal
   └─ NullLiteral
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CharLiteral "'c'"
└─ BooleanLiteral[ @True = true() ]
└─ NumericLiteral[ @IntLiteral = true() ] "1"
└─ NumericLiteral[ @DoubleLiteral = true() ] "1.0"
└─ StringLiteral "\"s\""
└─ NullLiteral
{% endhighlight %}
</td></tr></table>

</details>

##### Method calls, constructor calls, array allocations

* What: Extra nodes dedicated for method and constructor calls and array allocations
  * {% jdoc jast::ASTConstructorCall %}
  * {% jdoc jast::ASTMethodCall %}
  * {% jdoc jast::ASTArrayAllocation %}
* Why: It was extremely difficult to identify method calls in PMD 6 - these consisted of multiple nodes with
  primary prefix, suffix and expressions. This was too low level to be easy to be used.
* Related issue: [[java] New expression and type grammar (#1759)](https://github.com/pmd/pmd/pull/1759)

<details>
  <summary markdown="span">Method calls, constructor calls, array allocations Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
o.myMethod("a");
new Object("b");
new int[10];
new int[] { 1, 2, 3 };
{% endhighlight %}
</td><td>
{% highlight js %}
└─ PrimaryExpression
   ├─ PrimaryPrefix
   │  └─ Name "o.myMethod"
   └─ PrimarySuffix
      └─ Arguments
         └─ ArgumentList (1)
            └─ Expression
               └─ PrimaryExpression
                  └─ PrimaryPrefix
                     └─ Literal "\"a\""

└─ PrimaryExpression
   └─ PrimaryPrefix
      └─ AllocationExpression
         ├─ ClassOrInterfaceType "Object"
         └─ Arguments
            └─ ArgumentList
               └─ Expression
                  └─ PrimaryExpression
                     └─ PrimaryPrefix
                        └─ Literal "\"b\""

└─ PrimaryExpression
   └─ PrimaryPrefix
      └─ AllocationExpression
         ├─ PrimitiveType "int"
         └─ ArrayDimsAndInits
            └─ Expression
               └─ PrimaryExpression
                  └─ PrimaryPrefix
                     └─ Literal "10"

└─ PrimaryPrefix
   └─ AllocationExpression
      ├─ PrimitiveType "int"
      └─ ArrayDimsAndInits
         └─ ArrayInitializer
            ├─ VariableInitializer
            │  └─ Expression
            │     └─ PrimaryExpression
            │        └─ PrimaryPrefix
            │           └─ Literal "1"
            ├─ VariableInitializer
            │  └─ Expression
            │     └─ PrimaryExpression
            │        └─ PrimaryPrefix
            │           └─ Literal "2"
            └─ VariableInitializer
               └─ Expression
                  └─ PrimaryExpression
                     └─ PrimaryPrefix
                        └─ Literal "3"

{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodCall "myMethod"
   ├─ VariableAccess "o"
   └─ ArgumentList (1)
      └─ StringLiteral "\"a\""

└─ ConstructorCall
   ├─ ClassType "Object"
   └─ ArgumentList (1)
      └─ StringLiteral "\"b\""

└─ ArrayAllocation[ @ArrayDepth = 1 ]
   └─ ArrayType
      ├─ PrimitiveType "int"
      └─ ArrayDimensions (1)
         └─ ArrayDimExpr
            └─ NumericLiteral "10"

└─ ArrayAllocation[ @ArrayDepth = 1 ]
   ├─ ArrayType
   │  ├─ PrimitiveType "int"
   │  └─ ArrayDimensions (1)
   │     └─ ArrayTypeDim
   └─ ArrayInitializer[ @Length = 3 ]
      ├─ NumericLiteral "1"
      ├─ NumericLiteral "2"
      └─ NumericLiteral "3"
{% endhighlight %}
</td></tr></table>

</details>

##### Method call chains are left-recursive

* What: The nodes {% jdoc_old jast::ASTPrimaryPrefix %} and {% jdoc_old jast::ASTPrimarySuffix %} are removed from the
  grammar. Subtrees for primary expressions appear to be left-recursive now.
* Why: Allows to reuse abstractions like method calls without introducing a new artificial node (like method chain).
* Related issue: [[java] New expression and type grammar (#1759)](https://github.com/pmd/pmd/pull/1759)

<details>
  <summary markdown="span">Method call chain Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
new Foo().bar.foo(1);
{% endhighlight %}
</td><td>
{% highlight js %}
└─ StatementExpression
   └─ PrimaryExpression
      ├─ PrimaryPrefix
      │  └─ AllocationExpression
      │     ├─ ClassOrInterfaceType "Foo"
      │     └─ Arguments (0)
      ├─ PrimarySuffix "bar"
      ├─ PrimarySuffix "foo"
      └─ PrimarySuffix[ @Arguments = true() ]
         └─ Arguments (1)
            └─ ArgumentList
               └─ Expression
                  └─ PrimaryExpression
                     └─ PrimaryPrefix
                        └─ Literal "1"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ExpressionStatement
   └─ MethodCall "foo"
      ├─ FieldAccess "bar"
      │  └─ ConstructorCall
      │     ├─ ClassType "Foo"
      │     └─ ArgumentList (0)
      └─ ArgumentList (1)
         └─ NumericLiteral "1"
{% endhighlight %}
</td></tr></table>

</details>

Instead of being flat, the subexpressions are now nested within one another.
The nesting follows the naturally recursive structure of expressions:

```java
new Foo().bar.foo(1)
└───────┘   │      │ ConstructorCall
└───────────┘      │ FieldAccess
└──────────────────┘ MethodCall
```

This makes the AST more regular and easier to navigate. Each node contains
the other nodes that are relevant to it (e.g. arguments) instead of them
being spread out over several siblings. The API of all nodes has been
enriched with high-level accessors to query the AST in a semantic way,
without bothering with the placement details.

The amount of changes in the grammar that this change entails is enormous,
but hopefully firing up the designer to inspect the new structure should
give you the information you need quickly.

Note: this also affect binary expressions like {% jdoc jast::ASTInfixExpression %}.
E.g. `a+b+c` is not parsed as
```
AdditiveExpression
+ (a)
+ (b)
+ (c)
```

But it is now (note: AdditiveExpression is now InfixExpression)
```
InfixExpression
+ InfixExpression
  + (a)
  + (b)
+ (c)
```

##### Field access, array access, variable access

* What: New nodes dedicated to accessing field, variables and referencing arrays.
  Also provide info about the access type, like whether a variable is read or written.
  * {% jdoc jast::ASTFieldAccess %}
  * {% jdoc jast::ASTVariableAccess %}
  * {% jdoc jast::ASTArrayAccess %}
* Why: Like MethodCalls, this was a missing abstraction in the AST that has been added now.
* Related issue: [[java] New expression and type grammar (#1759)](https://github.com/pmd/pmd/pull/1759)

<details>
  <summary markdown="span">Field access, array access, variable access Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
field = 1;
localVar = 1;
array[0] = 1;
Foo.staticField = localVar;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ BlockStatement
   └─ Statement
      └─ StatementExpression
         ├─ PrimaryExpression
         │  └─ PrimaryPrefix
         │     └─ Name "field"
         ├─ AssignmentOperator "="
         └─ Expression
            └─ PrimaryExpression
               └─ PrimaryPrefix
                  └─ Literal "1"

└─ BlockStatement
   └─ Statement
      └─ StatementExpression
         ├─ PrimaryExpression
         │  └─ PrimaryPrefix
         │     └─ Name "localVar"
         ├─ AssignmentOperator "="
         └─ Expression
            └─ PrimaryExpression
               └─ PrimaryPrefix
                  └─ Literal "1"

└─ BlockStatement
   └─ Statement
      └─ StatementExpression
         ├─ PrimaryExpression
         │  ├─ PrimaryPrefix
         │  │  └─ Name "array"
         │  └─ PrimarySuffix[ @ArrayDereference = true() ]
         │     └─ Expression
         │        └─ PrimaryExpression
         │           └─ PrimaryPrefix
         │              └─ Literal "0"
         ├─ AssignmentOperator "="
         └─ Expression
            └─ PrimaryExpression
               └─ PrimaryPrefix
                  └─ Literal "1"

└─ BlockStatement
   └─ Statement
      └─ StatementExpression
         ├─ PrimaryExpression
         │  └─ PrimaryPrefix
         │     └─ Name "Foo.staticField"
         ├─ AssignmentOperator "="
         └─ Expression
            └─ PrimaryExpression
               └─ PrimaryPrefix
                  └─ Name "localVar"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ExpressionStatement
   └─ AssignmentExpression "="
      ├─ VariableAccess "field"
      └─ NumericLiteral "1"

└─ ExpressionStatement
   └─ AssignmentExpression "="
      ├─ VariableAccess "localVar"
      └─ NumericLiteral "1"

└─ ExpressionStatement
   └─ AssignmentExpression "="
      ├─ ArrayAccess[ @AccessType = "WRITE" ]
      │  ├─ VariableAccess "array"
      │  └─ NumericLiteral "0"
      └─ NumericLiteral "1"

└─ ExpressionStatement
   └─ AssignmentExpression "="
      ├─ FieldAccess[ @AccessType = "WRITE" ] "staticField"
      │  └─ TypeExpression
      │     └─ ClassType "Foo"
      └─ VariableAccess[ @AccessType = "READ" ] "localVar"
{% endhighlight %}

<ul>
  <li>As seen above, an unqualified field access currently shows up as a VariableAccess. This may be fixed
      future versions of PMD.</li>
</ul>

</td></tr></table>

</details>

##### Explicit nodes for this/super expressions

* What: `this` and `super` are now explicit nodes instead of PrimaryPrefix.
  * {% jdoc jast::ASTThisExpression %}
  * {% jdoc jast::ASTSuperExpression %}
* Why: That way these nodes can qualify other nodes like FieldAccess.
* Related issue: [[java] New expression and type grammar (#1759)](https://github.com/pmd/pmd/pull/1759)

<details>
  <summary markdown="span">this/super expressions Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
this.field = 1;
super.field = 1;

this.method();
super.method();
{% endhighlight %}
</td><td>
{% highlight js %}
└─ BlockStatement
   └─ Statement
      └─ StatementExpression
         ├─ PrimaryExpression
         │  ├─ PrimaryPrefix[ @ThisModifier = true() ]
         │  └─ PrimarySuffix "field"
         ├─ AssignmentOperator "="
         └─ Expression
            └─ PrimaryExpression
               └─ PrimaryPrefix
                  └─ Literal "1"

└─ BlockStatement
   └─ Statement
      └─ StatementExpression
         ├─ PrimaryExpression
         │  ├─ PrimaryPrefix[ @SuperModifier = true() ]
         │  └─ PrimarySuffix "field"
         ├─ AssignmentOperator "="
         └─ Expression
            └─ PrimaryExpression
               └─ PrimaryPrefix
                  └─ Literal "1"

└─ BlockStatement
   └─ Statement
      └─ StatementExpression
         └─ PrimaryExpression
            ├─ PrimaryPrefix[ @ThisModifier = true() ]
            ├─ PrimarySuffix "method"
            └─ PrimarySuffix[ @Arguments = true() ]
               └─ Arguments (0)

└─ BlockStatement
   └─ Statement
      └─ StatementExpression
         └─ PrimaryExpression
            ├─ PrimaryPrefix[ @SuperModifier = true() ]
            ├─ PrimarySuffix "method"
            └─ PrimarySuffix[ @Arguments = true() ]
               └─ Arguments (0)
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ExpressionStatement
   └─ AssignmentExpression "="
      ├─ FieldAccess[ @AccessType = "WRITE" ] "field"
      │  └─ ThisExpression
      └─ NumericLiteral "1"

└─ ExpressionStatement
   └─ AssignmentExpression "="
      ├─ FieldAccess[ @AcessType = "WRITE" ] "field"
      │  └─ SuperExpression
      └─ NumericLiteral "1"

└─ ExpressionStatement
   └─ MethodCall "method"
      ├─ ThisExpression
      └─ ArgumentList (0)

└─ ExpressionStatement
   └─ MethodCall "method"
      ├─ SuperExpression
      └─ ArgumentList (0)
{% endhighlight %}
</td></tr></table>

</details>

##### Type expressions

* What: The node {% jdoc jast::ASTTypeExpression %} wraps a {% jdoc jast::ASTType %} node (such as
  {% jdoc jast::ASTClassType %}) and is used to qualify a method call or field access or method reference.
* Why: Simplify the qualifier of method calls, treat instanceof as infix expression.
* Related issue: [[java] Grammar type expr (#2039)](https://github.com/pmd/pmd/pull/2039)

<details>
  <summary markdown="span">Type expressions Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
Foo.staticMethod();
if (x instanceof Foo) {}
var x = Foo::method;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ BlockStatement
   └─ Statement
      └─ StatementExpression
         └─ PrimaryExpression
            ├─ PrimaryPrefix
            │  └─ Name "Foo.staticMethod"
            └─ PrimarySuffix[ @Arguments = true() ]
               └─ Arguments (0)

└─ BlockStatement
   └─ Statement
      └─ IfStatement
         ├─ Expression
         │  └─ InstanceOfExpression
         │     ├─ PrimaryExpression
         │     │  └─ PrimaryPrefix
         │     │     └─ Name "x"
         │     └─ Type
         │        └─ ReferenceType
         │           └─ ClassOrInterfaceType "Foo"
         └─ Statement
            └─ Block

└─ BlockStatement
   └─ LocalVariableDeclaration
      └─ VariableDeclarator
         ├─ VariableDeclaratorId "x"
         └─ VariableInitializer
            └─ Expression
               └─ PrimaryExpression
                  ├─ PrimaryPrefix
                  │  └─ Name "Foo"
                  └─ PrimarySuffix
                     └─ MemberSelector
                        └─ MethodReference "method"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ExpressionStatement
   └─ MethodCall "staticMethod"
      ├─ TypeExpression
      │  └─ ClassType "Foo"
      └─ ArgumentList (0)

└─ IfStatement
   ├─ InfixExpression "instanceof"
   │  ├─ VariableAccess[ @AccessType = "READ" ] "x"
   │  └─ TypeExpression
   │     └─ ClassType "Foo"
   └─ Block

└─ LocalVariableDeclaration
   ├─ ModifierList
   └─ VariableDeclarator
      ├─ VariableId "x"
      └─ MethodReference "method"
         └─ TypeExpression
            └─ ClassType "Foo"
{% endhighlight %}
</td></tr></table>

</details>

##### Merge unary expressions

* What: Merge AST nodes for postfix and prefix expressions into the single {% jdoc jast::ASTUnaryExpression %} node.
  The merged nodes are:
  * PreIncrementExpression
  * PreDecrementExpression
  * UnaryExpression
  * UnaryExpressionNotPlusMinus
* Why: Those nodes were asymmetric, and inconsistently nested within UnaryExpression. By definition, they're all unary,
  so that using a single node is appropriate.
* Related issues:
  * [[java] Merge different increment/decrement expressions (#1890)](https://github.com/pmd/pmd/pull/1890)
  * [[java] Merge prefix/postfix expressions into one node (#2155)](https://github.com/pmd/pmd/pull/2155)

<details>
  <summary markdown="span">Unary Expressions Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
++a;
--b;
c++;
d--;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ StatementExpression
   └─ PreIncrementExpression
      └─ PrimaryExpression
         └─ PrimaryPrefix
            └─ Name "a"

└─ StatementExpression
   └─ PreDecrementExpression
      └─ PrimaryExpression
         └─ PrimaryPrefix
            └─ Name "b"

└─ StatementExpression
   └─ PostfixExpression "++"
      └─ PrimaryExpression
         └─ PrimaryPrefix
            └─ Name "c"

└─ StatementExpression
   └─ PostfixExpression "--"
      └─ PrimaryExpression
         └─ PrimaryPrefix
            └─ Name "d"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ExpressionStatement
   └─ UnaryExpression[ @Prefix = true() ][ @Operator = '++' ]
      └─ VariableAccess[ @AccessType = "WRITE" ] "a"

└─ ExpressionStatement
   └─ UnaryExpression[ @Prefix = true() ][ @Operator = '--' ]
      └─ VariableAccess[ @AccessType = "WRITE" ] "b"

└─ ExpressionStatement
   └─ UnaryExpression[ @Prefix = false() ][ @Operator = '++' ]
      └─ VariableAccess[ @AccessType = "WRITE" ] "c"

└─ ExpressionStatement
   └─ UnaryExpression[ @Prefix = false() ][ @Operator = '--' ]
      └─ VariableAccess[ @AccessType = "WRITE" ] "d"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
x = ~a;
x = +a;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ UnaryExpressionNotPlusMinus "~"
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ Name "a"

└─ UnaryExpression "+"
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ Name "a"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ UnaryExpression[ @Prefix = true() ] "~"
   └─ VariableAccess "a"

└─ UnaryExpression[ @Prefix = true() ] "+"
   └─ VariableAccess "a"
{% endhighlight %}
</td></tr>
</table>

</details>

##### Binary operators are left-recursive

* What: For each operator, there were separate AST nodes (like AdditiveExpression, AndExpression, ...).
  These are now unified into a `InfixExpression`, which gives access to the operator via `getOperator()`
  and to the operands (`getLhs()`, `getRhs()`). Additionally, the resulting AST is not flat anymore,
  but a more structured tree.
* Why: Having different AST node types doesn't add information, that the operator doesn't already provide.
  The new structure as a result, that the expressions are now parsed left recursive, makes the AST more JLS-like.
  This makes it easier for the type mapping algorithms. It also provides the information, which operands are
  used with which operator. This information was lost if more than 2 operands where used and the tree was
  flattened with PMD 6.
* Related issue: [[java] Make binary operators left-recursive (#1979)](https://github.com/pmd/pmd/pull/1979)

<details>
  <summary markdown="span">Binary operators Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
int i = 1 * 2 * 3 % 4;
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Expression
   └─ MultiplicativeExpression "%"
      ├─ PrimaryExpression
      │  └─ PrimaryPrefix
      │     └─ Literal "1"
      ├─ PrimaryExpression
      │  └─ PrimaryPrefix
      │     └─ Literal "2"
      ├─ PrimaryExpression
      │  └─ PrimaryPrefix
      │     └─ Literal "3"
      └─ PrimaryExpression
         └─ PrimaryPrefix
            └─ Literal "4"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ InfixExpression[ @Operator = '%' ]
   ├─ InfixExpression[@Operator='*']
   │  ├─ InfixExpression[@Operator='*']
   │  │  ├─ NumericLiteral[@ValueAsInt=1]
   │  │  └─ NumericLiteral[@ValueAsInt=2]
   │  └─ NumericLiteral[@ValueAsInt=3]
   └─ NumericLiteral[@ValueAsInt=4]
{% endhighlight %}
</td></tr>
</table>

</details>

##### Parenthesized expressions

* What: Parentheses are not modelled in the AST anymore, but can be checked with the attributes `@Parenthesized`
  and `@ParenthesisDepth`
* Why: This keeps the tree flat while still preserving the information. The tree is the same in case of unnecessary
  parenthesis, which makes it harder to fool rules that look at the structure of the tree.
* Related issue: [[java] Remove ParenthesizedExpr (#1872)](https://github.com/pmd/pmd/pull/1872)

<details>
  <summary markdown="span">Parenthesized expressions Examples</summary>

<table>
<tr><th>Code</th><th>Old AST (PMD 6)</th><th>New AST (PMD 7)</th></tr>
<tr><td>
{% highlight java %}
a = (((1)));
{% endhighlight %}
</td><td>
{% highlight js %}
└─ StatementExpression
   ├─ PrimaryExpression
   │  └─ PrimaryPrefix
   │     └─ Name "a"
   ├─ AssignmentOperator "="
   └─ Expression
      └─ PrimaryExpression
         └─ PrimaryPrefix
            └─ Expression
               └─ PrimaryExpression
                  └─ PrimaryPrefix
                     └─ Expression
                        └─ PrimaryExpression
                           └─ PrimaryPrefix
                              └─ Expression
                                 └─ PrimaryExpression
                                    └─ PrimaryPrefix
                                       └─ Literal "1"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ExpressionStatement
   └─ AssignmentExpression
      ├─ VariableAccess "a"
      └─ NumericLiteral[ @Parenthesized = true() ][ @ParenthesisDepth = 3 ] "1"
{% endhighlight %}
</td></tr></table>

</details>

### Apex AST

PMD 7.0.0 switched the underlying parser for Apex code from Jorje to [Summit AST](https://github.com/google/summit-ast),
which is based on an open source grammar for Apex: [apex-parser](https://github.com/nawforce/apex-parser).

The produced AST is mostly compatible, there are some unavoidable changes however:

* Node `Method` ({%jdoc apex::lang.apex.ast.ASTMethod %})
  * No attribute `@Synthetic` anymore. Unlike Jorje, Summit AST doesn't generate synthetic methods anymore, so
    this attribute would have been always false and is of no use. Therefore it has been removed completely.
  * There will be no methods anymore with the name `<clinit>`, `<init>`.
* There is no node `BridgeMethodCreator` anymore. This was an artificially generated node by Jorje. Since the
  new parser doesn't generate synthetic methods anymore, this node is not needed anymore.
* There is in general no attribute `@Namespace` anymore. The attribute has been removed, as it was never fully
  implemented. It always returned an empty string.
* Node `ReferenceExpression` ({%jdoc apex::lang.apex.ast.ASTReferenceExpression %})
  * No attribute `@Context` anymore. It was not used and always returned `null`.

### Language versions

* Since all languages now have defined language versions, you could now write rules that apply only for specific
  versions (using `minimumLanguageVersion` and `maximumLanguageVersion`).
* All languages have a default version. If no specific version on the CLI is given using `--use-version`, then
  this default version will be used. Usually the latest version is the default version.
* The available versions for each language can be seen in the help message of the CLI `pmd check --help`.
* See also [Changed: Language versions](pmd_release_notes_pmd7.html#changed-language-versions)

### Migrating custom CPD language modules

This is only relevant, if you are maintaining a CPD language module for a custom language.

* Instead of `AbstractLanguage` extend now {% jdoc core::lang.impl.CpdOnlyLanguageModuleBase %}.
* Instead of `AntlrTokenManager` use now {% jdoc core::lang.TokenManager %}
* Instead of `AntlrTokenFilter` also use now {% jdoc core::lang.TokenManager %}
* Instead of `AntlrTokenFilter` extend now {% jdoc core::cpd.impl.BaseTokenFilter %}
* CPD Module discovery change. The service loader won't load anymore `src/main/resources/META-INF/services/net.sourceforge.pmd.cpd.Language`
  but instead `src/main/resources/META-INF/services/net.sourceforge.pmd.lang.Language`. This is the unified
  language interface for both PMD and CPD capable languages. See also the subinterfaces
  {% jdoc core::cpd.CpdCapableLanguage %} and {% jdoc core::lang.PmdCapableLanguage %}.
* The documentation [How to add a new CPD language](pmd_devdocs_major_adding_new_cpd_language.html) has been updated
  to reflect these changes.

### Build Tools

{% include note.html content="
When you switch from PMD 6.x to PMD 7 in your build tools, you most likely need to review your
ruleset(s) as well and check for removed rules.
See the use case [I'm using only built-in rules](#im-using-only-built-in-rules) above.
" %}

#### Ant

* The Ant tasks {% jdoc ant::ant.PMDTask %} and {% jdoc ant::ant.CPDTask %} have been moved from the module
  `pmd-core` into the new module `pmd-ant`.
* You need to add this dependency/jar file onto the class path (`net.sourceforge.pmd:pmd-ant`) in order to
  import the tasks into your build file.
* When using the guide [Ant Task Usage](pmd_userdocs_tools_ant.html) then no change is needed, since
  the pmd-ant jar file is included in the binary distribution of PMD. It is part of PMD's lib folder.

#### Maven

* Since maven-pmd-plugin 3.22.0, PMD 7 is supported directly.
* See [MPMD-379](https://issues.apache.org/jira/browse/MPMD-379)
* See [Using PMD 7 with maven-pmd-plugin](pmd_userdocs_tools_maven.html#using-pmd-7-with-maven-pmd-plugin)

#### Gradle

* Gradle uses internally PMD's Ant task to execute PMD
* Gradle 8.6 supports PMD 7 out of the box, but does not yet use PMD 7 by default.
* You can set `toolVersion = "{{site.pmd.version}}"`.
* Only for older gradle versions you need to configure the dependencies manually for now, since
  the ant task is in an own dependency with PMD 7:
  ```groovy
  pmd 'net.sourceforge.pmd:pmd-ant:{{site.pmd.version}}'
  pmd 'net.sourceforge.pmd:pmd-java:{{site.pmd.version}}'
  ```
* See [Support for PMD 7.0](https://github.com/gradle/gradle/issues/24502)

### XML Report Format

The [XML Report format](pmd_userdocs_report_formats.html#xml) supports rendering [suppressed violations](pmd_userdocs_suppressing_warnings.html).

The content of the attribute `suppressiontype` is changed in PMD 7.0.0:
* `nopmd` ➡️ `//nopmd`
* `annotation` ➡️ `@suppresswarnings`
* `xpath` - new value. Suppressed via property "violationSuppressXPath".
* `regex` - new value. Suppressed via property "violationSuppressRegex".
