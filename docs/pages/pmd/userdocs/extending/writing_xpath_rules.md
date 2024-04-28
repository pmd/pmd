---
title:  Writing XPath rules
tags: [extending, userdocs]
summary: "This page describes XPath rule support in more details"
last_updated: December 2023 (7.0.0)
permalink: pmd_userdocs_extending_writing_xpath_rules.html
author: Miguel Griffa <mikkey@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---


{% jdoc_nspace :coremx core::lang.metrics %}
{% jdoc_nspace :coreast core::lang.ast %}
{% jdoc_nspace :jmx java::lang.java.metrics %}
{% jdoc_nspace :jast java::lang.java.ast %}


This page describes some points of XPath rule support in more details. See
also [the tutorial about how to write a first (XPath) rule](pmd_userdocs_extending_your_first_rule.html).

<!-- Later we can document the specific subset of XPath features our wrappers support -->

## XPath version

PMD uses XPath 3.1 for its XPath rules since PMD 7. Before then, the default version was XPath 1.0,
with opt-in support for XPath 2.0.

See [the Saxonica documentation](https://www.saxonica.com/html/documentation/expressions/xpath31new.html)
for an introduction to new features in XPath 3.1.

The property `version` of {% jdoc core::lang.rule.xpath.XPathRule %} is deprecated and
has been removed with PMD 7.


## DOM representation of ASTs

XPath rules view the AST as an XML-like DOM, which is what the XPath language is
defined on. Concretely, this means:
* Every AST node is viewed as an XML element
  * The element has for local name the value of {% jdoc core::lang.ast.Node#getXPathNodeName() %}
  for the given node
* Some Java getters are exposed as XML attributes on those elements
  * This means, that documentation for attributes can be found in our Javadocs. For
  example, the attribute `@SimpleName` of the Java node `EnumDeclaration` is backed
  by the Java getter {% jdoc java::lang.java.ast.ASTTypeDeclaration#getSimpleName() %}.

### Value conversion

To represent attributes, we must map Java values to [XPath Data Model (XDM)](https://www.w3.org/TR/xpath-datamodel/)
values. In the following table we refer to the type conversion function as `conv`, a function from Java types
to XDM types.

| Java type `T`     | XSD type `conv(T)`                    |
|-------------------|---------------------------------------|
| `int`             | `xs:integer`                          |
| `long`            | `xs:integer`                          |
| `double`          | `xs:decimal`                          |
| `float`           | `xs:decimal`                          |
| `boolean`         | `xs:boolean`                          |
| `String`          | `xs:string`                           |
| `Character`       | `xs:string`                           |
| `Enum<E>`         | `xs:string` (uses `Object::toString`) |
| `Collection<E>`   | `conv(E)*` (a sequence type)          |

The same `conv` function is used to translate rule property values to XDM values.

Additionaly, PMD's own `net.sourceforge.pmd.lang.document.Chars` is also translated to a `xs:string`


## Rule properties

**See [Defining rule properties](pmd_userdocs_extending_defining_properties.html#for-xpath-rules)**


## PMD extension functions

PMD provides some language-specific XPath functions to access semantic
information from the AST.

The namespace of custom PMD functions must be explicitly mentioned.

{% render %}
{% include custom/xpath_fun_doc.html %}
{% endrender %}

