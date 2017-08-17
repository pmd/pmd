---
title: Controversial Ecmascript
summary: The Controversial ruleset contains rules that, for whatever reason, are considered controversial. They are held here to allow people to include them as they see fit within their custom rulesets.
permalink: pmd_rules_ecmascript_controversial.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/rulesets/ecmascript/controversial.xml
keywords: Controversial Ecmascript, AvoidWithStatement
---
## AvoidWithStatement

**Since:** PMD 5.0.1

**Priority:** High (1)

Avoid using with - it's bad news

```
//WithStatement
```

**Example(s):**

``` javascript
with (object) {
    property = 3; // Might be on object, might be on window: who knows.
}
```

