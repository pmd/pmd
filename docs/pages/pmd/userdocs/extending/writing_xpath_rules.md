---
title:  Writing XPath rules
tags: [extending, userdocs]
summary: "This page describes XPath rule support in more details"
last_updated: July 2018 (6.6.0)
permalink: pmd_userdocs_extending_writing_xpath_rules.html
author: Miguel Griffa <mikkey@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---


{% jdoc_nspace :coremx core::lang.metrics %}
{% jdoc_nspace :coreast core::lang.ast %}
{% jdoc_nspace :jmx java::lang.java.metrics %}
{% jdoc_nspace :jast java::lang.java.ast %}


This page describes some points of XPath rule support in more details. See
also [the tutorial about how to write an XPath rule](pmd_userdocs_extending_designer_intro.html).

<!-- Later we can document the specific subset of XPath features our wrappers support -->

## XPath version

PMD supports three XPath versions for now: 1.0, 2.0, and 1.0 compatibility mode.
The version can be specified with the `version` property in the rule definition, like so:

```xml
<property version="2.0" /> <!-- or "1.0", or "1.0 compatibility" -->
```

As of PMD version 6.13.0, XPath versions 1.0 and the 1.0 compatibility mode are
deprecated. XPath 2.0 is superior in many ways, for example for its support for
type checking, sequence values, or quantified expressions. For a detailed
but approachable review of the features of XPath 2.0 and above, see [the Saxon documentation](https://www.saxonica.com/documentation/index.html#!expressions).


It is recommended that you migrate to 2.0 before 7.0.0, but we expect
to be able to provide an automatic migration tool when releasing 7.0.0. The
following section describes incompatibilities between 1.0 and 2.0 for PMD rules.

### Migrating

TODO

## PMD extension functions

PMD provides some language-specific XPath functions to access semantic
information from the AST.

On XPath 2.0, the namespace of custom PMD function must be explicitly mentioned.

{% render %}
{% include custom/xpath_fun_doc.html %}
{% endrender %}

There is also a `typeOf` function which is deprecated and whose usages
should be replaced with uses of `typeIs` or `typeIsExactly`. That one will
be removed with PMD 7.0.0.


