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

#### Annotations

* What: Annotations are consolidated into a single node. SingleMemberAnnotation, NormalAnnotation and MarkerAnnotation
  are removed in favour of Annotation. The Name node is removed, replaced by a ClassOrInterfaceType.
* Why: Those different node types implement a syntax-only distinction, that only makes semantically equivalent
  annotations have different possible representations. For example, `@A` and `@A()` are semantically equivalent,
  yet they were parsed as MarkerAnnotation resp. NormalAnnotation. Similarly, `@A("")` and `@A(value="")` were parsed
  as SingleMemberAnnotation resp. NormalAnnotation. This also makes parsing much simpler. The nested ClassOrInterface
  type is used to share the disambiguation logic.
* [#2282 [java] Use single node for annotations](https://github.com/pmd/pmd/pull/2282)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
<tr><td>
{% highlight java %}
@A
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Annotation
   └─ MarkerAnnotation
      └─ Name "A"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Annotation
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
└─ Annotation
   └─ NormalAnnotation
      └─ Name "A"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Annotation "A"
   ├─ ClassOrInterfaceType "A"
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
└─ Annotation
   └─ NormalAnnotation
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
   ├─ ClassOrInterfaceType "A"
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
└─ Annotation
   └─ SingleMemberAnnotation
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
   ├─ ClassOrInterfaceType "A"
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
└─ Annotation
   └─ NormalAnnotation
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
   ├─ ClassOrInterfaceType "A"
   └─ AnnotationMemberList
      ├─ MemberValuePair "value" [ @Shorthand = false() ]
      │  └─ StringLiteral '"v"'
      └─ MemberValuePair "on"
         └─ BooleanLiteral [ @True = true() ]
{% endhighlight %}
</td>
</tr>
</table>

##### Annotation nesting

* What: Annotations are now nested within the node, to which they are applied to. E.g. if a method is annotated, the Annotation node is now a child of a ModifierList, inside the MethodDeclaration.
* Why: Fixes a lot of inconsistencies, where sometimes the annotations were inside the node, and sometimes just somewhere in the parent, with no real structure.
* [#1875 [java] Move annotations inside the node they apply to](https://github.com/pmd/pmd/pull/1875)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
<tr><td>
Method

{% highlight java %}
@A
public void set(int x) { }
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ClassOrInterfaceBodyDeclaration
   ├─ Annotation
   │  └─ MarkerAnnotation
   │     └─ Name "A"
   └─ MethodDeclaration
      ├─ ResultType[@Void=true]
      ├─ ...
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration
   ├─ ModifierList
   │  └─ Annotation "A"
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
   ├─ Annotation
   │  └─ MarkerAnnotation
   │     └─ Name "A"
   └─ ClassOrInterfaceDeclaration
      └─ ClassOrInterfaceBody
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ TypeDeclaration
   └─ ClassOrInterfaceDeclaration
       ├─ ModifierList
       │  └─ Annotation "A"
       └─ ClassOrInterfaceBody
{% endhighlight %}
</td>
</tr>

<tr><td>
Cast expression

{% highlight java %}
(@A T.@B S) expr
{% endhighlight %}
</td><td>
N/A (Parse error)
</td>
<td>
{% highlight js %}
└─ CastExpression
   ├─ ClassOrInterfaceType "S"
   │  └─ Annotation "B"
   │  └─ ClassOrInterfaceType "T"
   │     └─ Annotation "A"
   └─ (Expression `expr`)
{% endhighlight %}
</td></tr>

<tr><td>
Cast expression with intersection

{% highlight java %}
(@A T & S) expr
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CastExpression
   ├─ MarkerAnnotation "A"
   ├─ ClassOrInterfaceType "T"
   ├─ ClassOrInterfaceType "S"
   └─ (Expression `expr`)
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ CastExpression
   ├─ IntersectionType
   │  ├─ ClassOrInterfaceType "T"
   │  │  └─ Annotation "A"
   │  └─ ClassOrInterfaceType "S"
   └─ (Expression `expr`)
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
   ├─ MarkerAnnotation "A"
   ├─ Type
   │  └─ ReferenceType
   │     └─ ClassOrInterfaceType "T"
   └─ Arguments
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ConstructorCall
   ├─ ClassOrInterfaceType "T"
   │  └─ Annotation "A"
   └─ ArgumentsList
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
   ├─ MarkerAnnotation "A"
   ├─ Type
   │  └─ PrimitiveType "int"
   └─ ArrayDimsAndInits
      └─ Expression
         └─ PrimaryExpression
            └─ Literal "0"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ArrayAllocation
   ├─ PrimitiveType "int"
   │  └─ Annotation "A"
   └─ ArrayAllocationDims
      └─ ArrayDimExpr
         └─ NumericLiteral "0"
{% endhighlight %}
</td></tr>

<tr><td>
Array type

{% highlight java %}
@A int @B[]
{% endhighlight %}
</td><td>
N/A (parse error)
</td>
<td>
{% highlight js %}
└─ ArrayType
   ├─ PrimitiveType "int"
   │  └─ Annotation "A"
   └─ ArrayTypeDims
      └─ ArrayTypeDim
         └─ Annotation "B"
{% endhighlight %}

Notice <code>@A</code> binds to <code>int</code>, not <code>int[]</code>

</td></tr>

<tr><td>
Type parameters

{% highlight java %}
<@A T, @B S extends @C Object>
{% endhighlight %}
</td><td>
{% highlight js %}
└─ TypeParameters
   ├─ MarkerAnnotation "A"
   ├─ TypeParameter "T"
   ├─ MarkerAnnotation "B"
   └─ TypeParameter "S"
      ├─ MarkerAnnotation "C"
      └─ TypeBound
         └─ ReferenceType
            └─ ClassOrInterfaceType "Object"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ TypeParameters
   ├─ TypeParameter "T"
   │  └─ Annotation "A"
   └─ TypeParameter "S"
      ├─ Annotation "B"
      └─ ClassOrInterfaceType "Object"
         └─ Annotation "C"
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
enum {
 @A E1, @B E2   
}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ EnumBody
   ├─ MarkerAnnotation "A"
   ├─ EnumConstant "E1"
   ├─ MarkerAnnotation "B"
   └─ EnumConstant "E2"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ EnumBody
   ├─ EnumConstant "E1"
   │  ├─ ModifierList
   │  │  └─ Annotation "A"
   │  └─ VariableDeclaratorId "E1"
   └─ EnumConstant "E2"
      ├─ ModifierList
      │  └─ Annotation "B"
      └─ VariableDeclaratorId "E1"
{% endhighlight %}

<ul>
  <li>Annotations are not just randomly in the enum body anymore</li>
</ul>
</td></tr>
</table>

#### Types

##### Type and ReferenceType

* What: those two nodes are turned into interfaces, implemented by concrete syntax nodes. See their javadoc for exactly what nodes implement them.
* Why:
  * some syntactic contexts only allow reference types, other allow any kind of type. If you want to match all types of a program, then matching Type would be the intuitive solution. But in 6.0.x, it wouldn't have sufficed, since in some contexts, no Type node was pushed, only a ReferenceType
  * Regardless of the original syntactic context, any reference type *is* a type, and searching for ASTType should yield all the types in the tree.
  * Using interfaces allows to abstract behaviour and make a nicer and safer API.

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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
<li><small>Notice that there is a Type node here, since a local var can have a primitive type</small></li>
<li><small>In contrast, notice that there is no Type here, since only reference types are allowed as type arguments</small></li>
</ol>
</td>
<td>
{% highlight js %}
└─ ClassOrInterfaceType "List"
   └─ TypeArguments
      └─ ClassOrInterfaceType "String"
{% endhighlight %}
<small>ClassOrInterfaceType implements ASTReferenceType, which implements ASTType.</small>
</td>
</tr>
</table>

##### Array changes

What: Additional nodes `ArrayType`, `ArrayTypeDim`, `ArrayTypeDims`, `ArrayAllocation`.
Why: Support annotated array types ([#997 Java8 parsing corner case with annotated array types](https://github.com/pmd/pmd/issues/997))
* [#1981 [java] Simplify array allocation expressions](https://github.com/pmd/pmd/pull/1981)

Examples:

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
<tr><td>
{% highlight java %}
String[][] myArray;
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ Type
   └─ ReferenceType[ @Array = true() ][ @ArrayDepth = 2 ]
      └─ ClassOrInterfaceType
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ArrayType[ @ArrayDepth = 2 ]
   ├─ ClassOrInterfaceType
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
n/a (parse error)
</td><td>
{% highlight js %}
└─ ArrayType
   ├─ ClassOrInterfaceType
   └─ ArrayDimensions
      ├─ ArrayTypeDim
      │  └─ Annotation[ @AnnotationName = 'Annotation1' ]
      └─ ArrayTypeDim
         └─ Annotation[ @AnnotationName = 'Annotation2' ]
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
new int[2][]
new @Bar int[3][2]
new Foo[] { f, g }
{% endhighlight %}
</td><td>
{% highlight js %}
├─ AllocationExpression
│  ├─ PrimitiveType "int"
│  └─ ArrayDimsAndInits
│     └─ Expression
│        └─ PrimaryExpression
│           └─ PrimaryPrefix
│              └─ Literal "2"
├─ AllocationExpression
│  ├─ Annotation
│  │  └─ MarkerAnnotation
│  │     └─ Name "Bar"
│  ├─ PrimitiveType "int"
│  └─ ArrayDimsAndInits
│     ├─ Expression
│     │  └─ PrimaryExpression
│     │     └─ PrimaryPrefix
│     │        └─ Literal "3"
│     └─ Expression
│        └─ PrimaryExpression
│           └─ PrimaryPrefix
│              └─ Literal "2"
└─ AllocationExpression
   ├─ ClassOrInterfaceType "Foo"
   └─ ArrayDimsAndInits
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
├─ ArrayAllocation
│  └─ ArrayType
│     ├─ PrimitiveType "int"
│     └─ ArrayDimensions
│        ├─ ArrayDimExpr
│        │  └─ NumericLiteral "2"
│        └─ ArrayTypeDim
├─ ArrayAllocation
│  └─ ArrayType
│     ├─ PrimitiveType "int"
│     │  └─ MarkerAnnotation "Bar"
│     └─ ArrayDimensions
│        ├─ ArrayDimExpr
│        │  └─ NumericLiteral "3"
│        └─ ArrayDimExpr
│           └─ NumericLiteral "2"
└─ ArrayAllocation
   └─ ArrayType
   │  ├─ ClassOrInterfaceType "Foo"
   │  └─ ArrayDimensions
   │     └─ ArrayTypeDim
   └─ ArrayInitializer
      ├─ VariableAccess "f"
      └─ VariableAccess "g"
{% endhighlight %}
</td></tr>
</table>

##### ClassOrInterfaceType nesting

* What: ClassOrInterfaceType is now left-recursive, and encloses its qualifying type.
* Why: To preserve the position of annotations and type arguments
  * [#1150 ClassOrInterfaceType AST improvements](https://github.com/pmd/pmd/issues/1150)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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
└─ ClassOrInterfaceType "Entry"
   ├─ ClassOrInterfaceType "Map"
   └─ TypeArguments
      ├─ ClassOrInterfaceType "K"
      └─ ClassOrInterfaceType "V"
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
└─ ClassOrInterfaceType "Third"
   ├─  ClassOrInterfaceType "Second"
   │   └─ ClassOrInterfaceType "First"
   │      └─ TypeArguments
   │         └─ ClassOrInterfaceType "K"
   └─ TypeArguments
      └─ ClassOrInterfaceType "V"
{% endhighlight %}
</td></tr>
</table>

##### TypeArgument and WildcardType

* What:
  * TypeArgument is removed. Instead, the TypeArguments node contains directly a sequence of Type nodes. To support this, the new node type WildcardType captures the syntax previously parsed as a TypeArgument.
  * The WildcardBounds node is removed. Instead, the bound is a direct child of the WildcardType.
* Why: Because wildcard types are types in their own right, and having a node to represent them skims several levels of nesting off.

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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
      └─ TypeArgument[ @UpperBound = true() ]
         └─ WildcardBounds
            └─ ReferenceType
               └─ ClassOrInterfaceType "Node"
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassOrInterfaceType "Entry"
   └─ TypeArguments
      ├─ ClassOrInterfaceType "String"
      └─ WildcardType[ @UpperBound = true() ]
         └─ ClassOrInterfaceType "Node"
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
      └─ TypeArgument
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ ClassOrInterfaceType "List"
   └─ TypeArguments
      └─ WildcardType
{% endhighlight %}
</td>
</tr>
</table>

#### Declarations

##### Import and Package declarations

* What: Remove the Name node in imports and package declaration nodes.
* Why: Name is a TypeNode, but it's equivalent to AmbiguousName in that it describes nothing about what it represents. The name in an import may represent a method name, a type name, a field name... It's too ambiguous to treat in the parser and could just be the image of the import, or package, or module.
* [#1888 [java] Remove Name nodes in Import- and PackageDeclaration](https://github.com/pmd/pmd/pull/1888)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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

##### Modifier lists

* What: AccessNode is now based on a node: ModifierList. That node represents modifiers occurring before
  a declaration. It provides a flexible API to query modifiers, both explicit and implicit. All declaration
  nodes now have such a modifier list, even if it's implicit (no explicit modifiers).
* Why: AccessNode gave a lot of irrelevant methods to its subtypes. E.g. `ASTFieldDeclaration::isSynchronized`
  makes no sense. Now, these irrelevant methods don't clutter the API. The API of ModifierList is both more
  general and flexible
* See [#2259 [java] Rework AccessNode](https://github.com/pmd/pmd/pull/2259)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
<tr><td>
Method

{% highlight java %}
@A
public void set(final int x, int y) { }
{% endhighlight %}
</td><td>
{% highlight js %}
└─ ClassOrInterfaceBodyDeclaration
   ├─ Annotation
   │  └─ MarkerAnnotation
   │     └─ Name "A"
   └─ MethodDeclaration[ @Public = true() ]
      ├─ ResultType[@Void=true]
      └─ MethodDeclarator
         └─ FormalParameters
            ├─ FormalParameter[ @Final = true() ]
            │  └─ VariableDeclaratorId "x"
            └─ FormalParameter[ @Final = false() ]
               └─ VariableDeclaratorId "y"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration
   ├─ ModifierList[ @Modifiers = ( "public" ) ]
   │  └─ Annotation "A"
   ├─ VoidType
   └─ FormalParameters
      ├─ FormalParameter
      │  ├─ ModifierList[ @Modifiers = ("final") ]
      │  └─ VariableDeclaratorId "x"
      └─ FormalParameter
         ├─ ModifierList[ @Modifiers = () ]
         └─ VariableDeclaratorId "y"
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
   ├─ Annotation
   │  └─ MarkerAnnotation
   │     └─ Name "A"
   └─ ClassOrInterfaceDeclaration[ @Public = true() ]
      └─ ClassOrInterfaceBody
{% endhighlight %}
</td>
<td>
{% highlight js %}
└─ TypeDeclaration
   └─ ClassOrInterfaceDeclaration
      ├─ ModifierList[ @Modifiers = ( "public" ) ]
      │  └─ MarkerAnnotation "A"
      └─ ClassOrInterfaceBody
{% endhighlight %}
</td>
</tr>
</table>

##### Flattened body declarations

* What: Removes ClassOrInterfaceBodyDeclaration, TypeDeclaration, and AnnotationTypeMemberDeclaration.
  These were unnecessary since annotations are nested (see above [Annotation nesting](#annotation-nesting)).
* Why: This flattens the tree, makes it less verbose and simpler.
* [#2300 [java] Flatten body declarations](https://github.com/pmd/pmd/pull/2300)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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
      └─ ClassOrInterfaceDeclaration[ @SimpleName = 'Flat' ]
         └─ ClassOrInterfaceBody
            └─ ClassOrInterfaceBodyDeclaration
               └─ FieldDeclaration
                  ├─ Type
                  │  └─ PrimitiveType
                  └─ VariableDeclarator
                     └─ VariableDeclaratorId[ @VariableName = 'f' ]
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CompilationUnit
   └─ ClassOrInterfaceDeclaration[ @SimpleName = 'Flat' ]
      ├─ ModifierList
      └─ ClassOrInterfaceBody
         └─ FieldDeclaration
            ├─ ModifierList
            ├─ PrimitiveType
            └─ VariableDeclarator
               └─ VariableDeclaratorId[ @VariableName = 'f' ]
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
      └─ AnnotationTypeDeclaration
         └─ AnnotationTypeBody
            └─ AnnotationTypeMemberDeclaration
               └─ AnnotationMethodDeclaration
                  ├─ Type
                  │  └─ ReferenceType
                  │     └─ ClassOrInterfaceType
                  └─ DefaultValue
                     └─ MemberValue
                        └─ PrimaryExpression
                           └─ PrimaryPrefix
                              └─ Literal
{% endhighlight %}
</td><td>
{% highlight js %}
└─ CompilationUnit
   └─ AnnotationTypeDeclaration
      ├─ ModifierList
      └─ AnnotationTypeBody
         └─ MethodDeclaration
            ├─ ModifierList
            ├─ ClassOrInterfaceType
            ├─ FormalParameters
            └─ DefaultValue
               └─ StringLiteral
{% endhighlight %}
</td></tr>
</table>

##### Module declarations

* What: Removes the generic Name node and uses instead ClassOrInterfaceType where appropriate. Also
  uses specific node types for different directives (requires, exports, uses, provides).
* Why: Simplify queries, support type resolution
* [#3890 [java] Improve module grammar](https://github.com/pmd/pmd/pull/3890)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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
      │  └─ ClassOrInterfaceType[ pmd-java:typeIs("com.example.foo.spi.Intf") ]
      └─ ModuleProvidesDirective
         ├─ ClassOrInterfaceType[ pmd-java:typeIs("com.example.foo.spi.Intf") ]
         └─ ClassOrInterfaceType[ pmd-java:typeIs("com.example.foo.Impl") ]
{% endhighlight %}
</td></tr>
</table>

##### TODO: new node for anonymous class

#### Method and Constructor declarations

##### Method grammar simplification

* What: Simplify and align the grammar used for method and constructor declarations. The methods in an annotation
  type are now also method declarations.
* Why: The method declaration had an nested node "MethodDeclarator", which was not available for constructor
  declarations. This made it difficult to write rules, that concern both methods and constructors without
  explicitly differentiate between these two.
* [#2034 [java] Align method and constructor declaration grammar](https://github.com/pmd/pmd/pull/2034)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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
├─ ConstructorDeclaration "Sample"
│  ├─ FormalParameters
│  │  └─ FormalParameter ...
│  ├─ NameList
│  │  └─ Name "Exception"
│  ├─ ExplicitConstructorInvocation
│  │  └─ Arguments
│  └─ BlockStatement
│     └─ Statement ...
└─ MethodDeclaration
   ├─ ResultType
   ├─ MethodDeclarator "greet"
   │  └─ FormatParameters
   │     └─ FormalParameter ...
   ├─ NameList
   │  └─ Name "Exception"
   └─ Block
      └─ BlockStatement
         └─ Statement ...
{% endhighlight %}
</td>
<td>
{% highlight js %}
├─ ConstructorDeclaration "Sample"
│  ├─ ModifierList
│  ├─ FormalParameters
│  │  └─ FormalParameter ...
│  ├─ ThrowsList
│  │  └─ ClassOrInterfaceType ...
│  └─ Block
│     ├─ ExplicitConstructorInvocation
│     │  └─ ArgumentList
│     └─ ExpressionStatement
└─ MethodDeclaration "greet"
   ├─ ModifierList
   ├─ VoidType
   ├─ FormalParameters
   │  └─ FormalParameter ...
   ├─ ThrowsList
   │  └─ ClassOrInterfaceType ...
   └─ Block
      └─ ExpressionStatement
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
└─ AnnotationTypeDeclaration "MyAnnotation"
   └─ AnnotationTypeBody
      └─ AnnotationTypeMemberDeclaration
         └─ AnnotationMethodDeclaration "value"
            ├─ Type ...
            └─ DefaultValue ...
{% endhighlight %}
</td><td>
{% highlight js %}
└─ AnnotationTypeDeclaration "MyAnnotation"
   └─ AnnotationTypeBody
      └─ AnnotationTypeMemberDeclaration
         └─ MethodDeclaration
            ├─ ModifierList
            ├─ PrimitiveType
            ├─ FormalParameters ...
            └─ DefaultValue ...
{% endhighlight %}
</td></tr>
</table>

##### Formal parameters

* What: Use FormalParameter only for method and constructor declaration. Lambdas use LambdaParameter, catch clauses use CatchParameter
* Why: FormalParameter's API is different from the other ones.
  * FormalParameter must mention a type node.
  * LambdaParameter can be inferred
  * CatchParameter cannot be varargs
  * CatchParameter can have multiple exception types (a UnionType now)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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
      │  ├─ Annotation "A"
      │  ├─ Type
      │  │  └─ ReferenceType
      │  │     └─ ClassOrInterfaceType "IOException"
      │  ├─ Type
      │  │  └─ ReferenceType
      │  │     └─ ClassOrInterfaceType "IllegalArgumentException"
      │  └─ VariableDeclaratorId
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
      │  │  └─ Annotation "A"
      │  ├─ UnionType
      │  │  └─ ClassOrInterfaceType "IOException"
      │  │  └─ ClassOrInterfaceType "IllegalArgumentException"
      │  └─ VariableDeclaratorId
      └─ Block
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
(a, b) -> {}
c -> {} 
(@A var d) -> {}
(@A int e) -> {}
{% endhighlight %}
</td><td>
{% highlight js %}
└─ Expression
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ LambdaExpression
            ├─ VariableDeclaratorId "a"
            ├─ VariableDeclaratorId "b"
            └─ Block

└─ Expression
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ LambdaExpression
            ├─ VariableDeclaratorId "c"
            └─ Block

└─ Expression
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ LambdaExpression
            ├─ FormalParameters
            │  └─ FormalParameter
            │     ├─ Annotation "A"
            │     │  └─ ...
            │     └─ VariableDeclaratorId "d"
            └─ Block

└─ Expression
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ LambdaExpression
            ├─ FormalParameters
            │  └─ FormalParameter
            │     ├─ Annotation "A"
            │     │  └─ ...
            │     ├─ Type
            │     │  └─ PrimitiveType
            │     └─ VariableDeclaratorId "e"
            └─ Block
{% endhighlight %}
</td><td>
{% highlight js %}
└─ LambdaExpression
   ├─ LambdaParameters
   │  ├─ LambdaParameter
   │  │  ├─ ModifierList
   │  │  └─ VariableDeclaratorId "a"
   │  └─ LambdaParameter
   │     ├─ ModifierList
   │     └─ VariableDeclaratorId "b"
   └─ Block

└─ LambdaExpression
   ├─ LambdaParameters
   │  └─ LambdaParameter
   │     ├─ ModifierList
   │     └─ VariableDeclaratorId "c"
   └─ Block


└─ LambdaExpression
   ├─ LambdaParameters
   │  └─ LambdaParameter
   │     ├─ ModifierList
   │     │  └─ Annotation "A"
   │     └─ VariableDeclaratorId "d"
   └─ Block

└─ LambdaExpression
   ├─ LambdaParameters
   │  └─ LambdaParameter
   │     ├─ ModifierList
   │     │  └─ Annotation "A"
   │     ├─ PrimitiveType "int"
   │     └─ VariableDeclaratorId "e"
   └─ Block
{% endhighlight %}
</td></tr>
</table>



##### New node for explicit receiver parameter

* What: A separate node type `ReceiverParameter` is introduced to differentiate it from formal parameters.
* Why: A receiver parameter is not a formal parameter, even though it looks like one: it doesn't declare a variable,
  and doesn't affect the arity of the method or constructor. It's so rarely used that giving it its own node avoids
  matching it by mistake and simplifies the API and grammar of the ubiquitous FormalParameter and VariableDeclaratorId.
* [#1980 [java] Separate receiver parameter from formal parameter](https://github.com/pmd/pmd/pull/1980)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
<tr><td>
{% highlight java %}
(@A Foo this, Foo other)
{% endhighlight %}
</td><td>
{% highlight js %}
└─ FormalParameters[ @ParameterCount = 1 ]
   ├─ FormalParameter[ @ReceiverParameter = true() ]
   │  ├─ ClassOrInterfaceType
   │  │  └─ Annotation "A"
   │  └─ VariableDeclaratorId[ @Image = "this" ][ @ReceiverParameter = true() ]
   └─ FormalParameter
      ├─ ClassOrInterfaceType
      └─ VariableDeclaratorId "other"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ FormalParameters[ @ParameterCount = 1 ]
   ├─ ReceiverParameter
   │  └─ ClassOrInterfaceType
   │     └─ Annotation "A"
   └─ FormalParameter
      ├─ ModifierList
      ├─ ClassOrInterfaceType
      └─ VariableDeclaratorId "other"
{% endhighlight %}
</td></tr>
</table>

##### Varargs

* What: parse the varargs ellipsis as an ArrayType
* Why: this improves regularity of the grammar, and allows type annotations to be added to the ellipsis

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
<tr><td>
{% highlight java %}
(int... is)
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
   ├─ ArrayType
   │  ├─ PrimitiveType "int"
   │  └─ ArrayDimensions
   │     └─ ArrayTypeDim[ @Varargs = true() ]
   └─ VariableDeclaratorId "is"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
(int @A ... is)
{% endhighlight %}
</td><td>
n/a (parse error)
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
   └─ VariableDeclaratorId "is"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
(int[]... is)
{% endhighlight %}
</td><td>
{% highlight js %}
└─ FormalParameter[ @Varargs = true() ]
   ├─ ModifierList
   ├─ Type
   │  └─ ReferenceType
   │     └─ PrimitiveType "int"
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
   │     ├─ ArrayTypeDim
   │     └─ ArrayTypeDim[ @Varargs = true() ]
   │        └─ Annotation "A"
   └─ VariableDeclaratorId "is"
{% endhighlight %}
</td></tr>
</table>


##### Add void type node to replace ResultType

* What: Add a VoidType node to replace ResultType.
* Why: This means we don't need the ResultType wrapper when the method is not void, and the result type node is never null.
* [[java] Add void type node to replace ResultType #2715](https://github.com/pmd/pmd/pull/2715)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
<tr><td>
{% highlight java %}
void foo();
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration
   └─ ResultType[ @Void = true() ]
   └─ MethodDeclarator
      └─ FormalParameters
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration
   └─ ModifierList
   └─ VoidType
   └─ FormalParameters
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
int foo();
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration
   └─ ResultType[ @Void = false() ]
      └─ Type
         └─ PrimitiveType
   └─ MethodDeclarator
      └─ FormalParameters
{% endhighlight %}
</td><td>
{% highlight js %}
└─ MethodDeclaration
   └─ ModifierList
   └─ PrimitiveType
   └─ FormalParameters
{% endhighlight %}
</td></tr>
</table>

#### Statements

##### TODO: statements are flattened (no BlockStatement, Statement nodes)
##### TODO: new node for ForeachStatement
##### TODO: New nodes for ExpressionStatement, LocalClassStatement

##### Improve try-with-resources grammar
* What: The AST representation of a try-with-resources statement has been simplified.
  It uses now LocalVariableDeclaration unless it is a concise try-with-resources grammar.
* Why: Simpler integration try-with-resources into symboltable and type resolution.
* [#1897 [java] Improve try-with-resources grammar](https://github.com/pmd/pmd/pull/1897)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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
            ├─ VariableDeclaratorId "in"
            └─ Expression
               └─ ...
{% endhighlight %}
</td><td>
{% highlight js %}
└─ TryStatement
   └─ ResourceList[ @TrailingSemiColon = true() ]
      ├─ Resource[ @ConciseResource = false() ]
      │  └─ LocalVariableDeclaration
      │     ├─ ModifierList
      │     ├─ Type
      │     └─ VariableDeclarator
      │        ├─ VariableDeclaratorId "in"
      │        └─ ConstructorCall
      │           ├─ ClassOrInterfaceType
      │           └─ ArgumentList
      └─ Resource[ @ConciseResource = false() ]
         └─ LocalVariableDeclaration
            ├─ ModifierList
            ├─ Type
            └─ VariableDeclarator
               ├─ VariableDeclaratorId "in"
               └─ ConstructorCall
                  ├─ ClassOrInterfaceType
                  └─ ArgumentList
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
         └─ Resource
            └─ Name "in"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ TryStatement
   └─ ResourceList[ @TrailingSemiColon = false() ]
      └─ Resource[ @ConciseResource = true() ]
         └─ VariableAccess[ @VariableName = 'in' ]
{% endhighlight %}
</td></tr>
</table>

#### Expressions

##### TODO: Literals
##### TODO: Method calls, constructor call, array allocation
##### TODO: Field access, array access, variable access
##### TODO: this/super expression
##### TODO: TypeExpression

##### Merge unary expressions

* What: Merge AST nodes for postfix and prefix expressions into the single UnaryExpression node. The merged nodes are:
  * PreIncrementExpression
  * PreDecrementExpression
  * UnaryExpression
  * UnaryExpressionNotPlusMinus
* Why: Those nodes were asymmetric, and inconsistently nested within UnaryExpression. By definition they're all unary, so that using a single node is appropriate.
* [#1890 [java] Merge different increment/decrement expressions](https://github.com/pmd/pmd/pull/1890)
* [#2155 [java] Merge prefix/postfix expressions into one node](https://github.com/pmd/pmd/pull/2155)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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
└─ StatementExpression
   └─ UnaryExpression[ @Prefix = true() ][ @Operator = '++' ]
      └─ VariableAccess "a"

└─ StatementExpression
   └─ UnaryExpression[ @Prefix = true() ][ @Operator = '--' ]
      └─ VariableAccess "b"

└─ StatementExpression
   └─ UnaryExpression[ @Prefix = false() ][ @Operator = '++' ]
      └─ VariableAccess "c"

└─ StatementExpression
   └─ UnaryExpression[ @Prefix = false() ][ @Operator = '--' ]
      └─ VariableAccess "d"
{% endhighlight %}
</td></tr>

<tr><td>
{% highlight java %}
~a
+a
{% endhighlight %}
</td><td>
{% highlight js %}
└─ UnaryExpression[ @Image = null ]
   └─ UnaryExpressionNotPlusMinus[ @Image = '~' ]
      └─ PrimaryExpression
         └─ PrimaryPrefix
            └─ Name "a"

└─ UnaryExpression[ @Image = '+' ]
   └─ PrimaryExpression
      └─ PrimaryPrefix
         └─ Name "a"
{% endhighlight %}
</td><td>
{% highlight js %}
└─ UnaryExpression[ @Operator = '~' ]
   └─ VariableAccess "a"

└─ UnaryExpression[ @Operator = '+' ]
   └─ VariableAccess "a"
{% endhighlight %}
</td></tr>
</table>

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
* [#1979 [java] Make binary operators left-recursive](https://github.com/pmd/pmd/pull/1979)

<table>
<tr><th>Code</th><th>Old AST</th><th>New AST</th></tr>
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


### Language versions

For some languages, that previously hadn't any version, now there are versions, e.g. plsql.

### Build Tools

maven...
