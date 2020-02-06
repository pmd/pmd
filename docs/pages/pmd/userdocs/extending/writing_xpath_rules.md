---
title:  Writing XPath rules
tags: [extending, userdocs]
summary: "This page describes XPath rule support in more details"
last_updated: February 2020 (6.22.0)
permalink: pmd_userdocs_extending_writing_xpath_rules.html
author: Miguel Griffa <mikkey@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---


{% jdoc_nspace :coremx core::lang.metrics %}
{% jdoc_nspace :coreast core::lang.ast %}
{% jdoc_nspace :jmx java::lang.java.metrics %}
{% jdoc_nspace :jast java::lang.java.ast %}


This page describes some points of XPath rule support in more details. See
also [the tutorial about how to write an XPath rule](pmd_userdocs_extending_your_first_rule.html).

<!-- Later we can document the specific subset of XPath features our wrappers support -->

## XPath version

PMD supports three XPath versions for now: 1.0, 2.0, and 1.0 compatibility mode.
The version can be specified with the `version` property in the rule definition, like so:

```xml
<property version="2.0" /> <!-- or "1.0", or "1.0 compatibility" -->
```

The default has always been version 1.0.

**As of PMD version 6.22.0, XPath versions 1.0 and the 1.0 compatibility mode are
deprecated**. XPath 2.0 is superior in many ways, for example for its support for
type checking, sequence values, or quantified expressions. For a detailed
but approachable review of the features of XPath 2.0 and above, see [the Saxon documentation](https://www.saxonica.com/documentation/index.html#!expressions).

It is recommended that you migrate to 2.0 before 7.0.0, but we expect
to be able to provide an automatic migration tool when releasing 7.0.0.
See [the migration guide](#migrating-from-10-to-20) below.


## DOM representation of ASTs

XPath rules view the AST as an XML-like DOM, which is what the XPath language is
defined on. Concretely, this means:
* Every AST node is viewed as an XML element
  * The element has for local name the value of {% jdoc core::lang.ast.Node#getXPathNodeName() %}
  for the given node
* Some Java getters are exposed as XML attributes on those elements
  * This means, that documentation for attributes can be found in our Javadocs. For
  example, the attribute `@SimpleName` of the Java node `EnumDeclaration` is backed
  by the Java getter {% jdoc java::lang.java.ast.ASTAnyTypeDeclaration#getSimpleName() %}.

### Value conversion

To represent attributes, we must map Java values to [XPath Data Model (XDM)](https://www.w3.org/TR/xpath-datamodel/) values. The conversion
depends on the XPath version used.

#### XPath 1.0

On XPath 1.0 we map every Java value to an `xs:string` value by using the `toString`
of the object. Since XPath 1.0 allows many implicit conversions this works, but it
causes some incompatibilities with XPath 2.0 (see the section about migration further
 down).

#### XPath 2.0

XPath 2.0 is a strongly typed language, and so we use more precise type annotations.
In the following table we refer to the type conversion function as `conv`, a
function from Java types to XDM types.

| Java type `T` | XSD type `conv(T)`
|-----------------|---------------------|
|`int`            | `xs:integer`
|`long`           | `xs:integer`
|`double`         | `xs:decimal`
|`float`          | `xs:decimal`
|`boolean`        | `xs:boolean`
|`String`         | `xs:string`
|`Character`      | `xs:string`
|`Enum<E>`        | `xs:string` (uses `Object::toString`)
|`List<E>`        | `conv(E)*` (a sequence type)


The same `conv` function is used to translate rule property values to XDM values.


## Migrating from 1.0 to 2.0

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

## Rule properties

**See [Defining rule properties](pmd_userdocs_extending_defining_properties.html#for-xpath-rules)**


## PMD extension functions

PMD provides some language-specific XPath functions to access semantic
information from the AST.

On XPath 2.0, the namespace of custom PMD function must be explicitly mentioned.

{% render %}
{% include custom/xpath_fun_doc.html %}
{% endrender %}

{% include note.html content='There is also a `typeOf` function which is
deprecated and whose usages should be replaced with uses of `typeIs` or
`typeIsExactly`. That one will be removed with PMD 7.0.0.' %}


