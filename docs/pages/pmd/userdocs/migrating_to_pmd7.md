---
title: Migration Guide for PMD 7
tags: [pmd, userdocs]
summary: "Migrating to PMD 7 from PMD 6.x"
permalink: pmd_userdocs_migrating_to_pmd7.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
---

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
  to {% jdoc core::RuleContext %}. E.g. instead of `addViolation(data, node, ...)` use `asCtx(data).addViolation(node, ...)`.

* When you are calling PMD from CLI, you need to stop using deprecated CLI params, e.g.
  * `-no-cache` ➡️ `--no-cache`
  * `-failOnViolation` ➡️ `--fail-on-violation`
  * `-reportfile` ➡️ `--report-file`
  * `-language` ➡️ `--use-version`

* If you have written custom XPath rule, look out for warning about deprecated XPath attributes. These warnings
  might look like
  ```
  WARNING: Use of deprecated attribute 'VariableDeclaratorId/@Image' by XPath rule 'VariableNaming' (in ruleset 'VariableNamingRule'), please use @Name instead
  ```
  and often suggest already an alternative.

## Use cases

### I'm using only built-in rules

When you are using only built-in rules, then you should check, whether you use any deprecated rule. With PMD 7
many deprecated rules are finally removed. You can see a complete list of the [removed rules](pmd_release_notes_pmd7.html#removed-rules)
in the release notes for PMD 7.
The release notes also mention the replacement rule, that should be used instead. For some rules, there is no
replacement.

Then many rules have been changed or improved. New properties have been added to make the further configurable or
properties have been removed, if they are not necessary anymore. See [changed rules](pmd_release_notes_pmd7.html#changed-rules)
in the release notes for PMD 7.

A handful rules are new with PMD 7. You might want to check these out: [new rules](pmd_release_notes_pmd7.html#new-rules).

Once you have reviewed your ruleset(s), you can switch to PMD 7.

### I'm using custom rules

Ideally, you have written good tests already for your custom rules - see [Testing your rules](pmd_userdocs_extending_testing.html).
This helps to identify problems early on.

If you have **XPath based** rules, the first step will be to migrate to XPath 2.0, which is available in PMD 6 already.
With PMD 7, XPath 1.0 won't be supported anymore and the default XPath version is actually 3.1. But the difference
from XPath 2.0 and XPath 3.1 is not big. So the migration path is to simply migrate to XPath 2.0.
After you have migrated your XPath rules to XPath 2.0, remove the "version" property, since that will be removed
with PMD 7. PMD 7 by default uses XPath 3.1.
See below [XPath](#xpath-migrating-from-10-to-20) for details.

If you have **Java based rules**, and you are using rulechain, this works a bit different now. The RuleChain API
has changed, see [\[core] Simplify the rulechain #2490](https://github.com/pmd/pmd/pull/2490) for the full details.
But in short, you don't call `addRuleChainVisit(...)` in the rule's constructor anymore. Instead, you
override the method {% jdoc core::lang.rule.AbstractRule#buildTargetSelector %}:

```java
    protected RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTVariableDeclaratorId.class);
    }
```

The API to **navigate the AST** also changed significantly:
* Tree traversal using [Node API](#node-api)
* Consider using the new [NodeStream API](#nodestream-api) to navigate with null-safety. This is optional.

Additionally, if you have created rules for **Java** - regardless whether it is a XPath based rule or a Java based
rule - you might need to adjust your queries or visitor methods. The Java AST has been refactored substantially.
The easiest way is to use the [PMD Rule Designer](pmd_userdocs_extending_designer_reference.html) to see the structure
of the AST. See the section [Java AST](#java-ast) below for details.

### I've extended PMD with a custom language...

### I've extended PMD with a custom feature...

## Special topics

### CLI Changes

run.sh pmd -> pmd check

Message: [main] ERROR net.sourceforge.pmd.cli.commands.internal.PmdCommand - No such file false
--> comes from "--fail-on-violation false" -> "--no-fail-on-violation"

### Custom distribution packages
needs pmd-cli dependencies
needs cyclonedx plugin
additional config needed to include conf/simpelogger.properties


### Rule tests

Nice to have - not immediately required:
Should replace junit4 with junit5
But both would work.

### Endcolumn

CPD: End Columns of Tokens are exclusive on PMD 7,
but inclusive on PMD 6.x. See 5b7ed58

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
                        .map(c -> c.isInstance(n))
                        .anyMatch(Boolean::booleanValue))
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
               .filterIs(ASTVariableDeclaratorId.class)// the stream here is empty if the node was not a variable declarator id
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

* The custom function "pmd:matches" has been removed, since there is a built-in function available since XPath 2.0
  which can be used instead.

### Java AST
* See also [Java Clean Changes](https://github.com/pmd/pmd/wiki/Java_clean_changes)

### Language versions

For some languages, that previously hadn't any version, now there are versions, e.g. plsql.

### Build Tools

maven...
