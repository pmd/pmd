---
title: Basic XML
summary: The Basic XML Ruleset contains a collection of good practices which everyone should follow.
permalink: pmd_rules_xml_basic.html
folder: pmd/rules/xml
sidebaractiveurl: /pmd_rules_xml.html
editmepath: ../pmd-xml/src/main/resources/rulesets/xml/basic.xml
---
## MistypedCDATASection

**Since:** PMD 5.0

**Priority:** Medium (3)

An XML CDATA section begins with a <!CDATA[ marker, which has only one [, and ends with a ]]> marker, which has only two ].

```
//cdata-section[starts-with(@Image,'[') or ends-with(@Image,']')]
```

**Example(s):**

```
An extra [ looks like &lt;!CDATA[[]]&gt;, and an extra ] looks like &lt;!CDATA[]]]&gt;.
```

