---
title: Basic XML
summary: The Basic XML Ruleset contains a collection of good practices which everyone should follow.
permalink: pmd_rules_xml_basic.html
folder: pmd/rules/xml
sidebaractiveurl: /pmd_rules_xml.html
editmepath: ../pmd-xml/src/main/resources/rulesets/xml/basic.xml
---
## MistypedCDATASection
**Since:** 5.0

**Priority:** Medium (3)

An XML CDATA section begins with a <!CDATA[ marker, which has only one [, and ends with a ]]> marker, which has only two ].

**Example(s):**
```
An extra [ looks like &lt;!CDATA[[]]&gt;, and an extra ] looks like &lt;!CDATA[]]]&gt;.
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

