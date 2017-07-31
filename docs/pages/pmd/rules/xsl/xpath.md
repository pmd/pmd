---
title: XPath in XSL
summary: This ruleset regroups a collection of good practices regarding XPath querying and functions inside an XSL.
permalink: pmd_rules_xsl_xpath.html
folder: pmd/rules/xsl
sidebaractiveurl: /pmd_rules_xsl.html
editmepath: ../pmd-xml/src/main/resources/rulesets/xsl/xpath.xml
---
## AvoidAxisNavigation
**Since:** 5.0

**Priority:** Medium (3)

Avoid using the 'following' or 'preceeding' axes whenever possible, as these can cut through 100% of the document in the worst case.  Also, try to avoid using 'descendant' or 'descendant-self' axes, as if you're at the top of the Document, it necessarily means cutting through 100% of the document.

**Example(s):**
```
<xsl:variable name="var" select="//item/descendant::child"/>
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkSelfDescendantAbreviation|false|descendant::self abreviation, '//', will also trigger this rule.|

## UseConcatOnce
**Since:** 5.0

**Priority:** Medium (3)

The XPath concat() functions accepts as many arguments as required so you can have "concat($a,'b',$c)" rather than "concat($a,concat('b',$c)".

**Example(s):**
```
<xsl:variable name="var" select="concat("Welcome",concat("to you ",$name))"/>
 <xsl:variable name="var" select="concat("Welcome","to you ",$name))">
```

