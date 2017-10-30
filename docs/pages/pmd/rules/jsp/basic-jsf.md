---
title: Basic JSF
summary: Rules concerning basic JSF guidelines.
permalink: pmd_rules_jsp_basic-jsf.html
folder: pmd/rules/jsp
sidebaractiveurl: /pmd_rules_jsp.html
editmepath: ../pmd-jsp/src/main/resources/rulesets/jsp/basic-jsf.xml
keywords: Basic JSF, DontNestJsfInJstlIteration
---
## DontNestJsfInJstlIteration

**Since:** PMD 3.6

**Priority:** Medium (3)

Do not nest JSF component custom actions inside a custom action that iterates over its body.

```
//Element[ @Name="c:forEach" ] // Element[ @NamespacePrefix="h" or @NamespacePrefix="f" ]
```

**Example(s):**

``` jsp
<html>
  <body>
    <ul>
      <c:forEach items='${books}' var='b'>
        <li> <h:outputText value='#{b}' /> </li>
      </c:forEach>
    </ul>
  </body>
</html>
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic-jsf.xml/DontNestJsfInJstlIteration" />
```

