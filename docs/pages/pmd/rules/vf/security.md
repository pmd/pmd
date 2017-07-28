---
title: Basic VF
summary: Rules concerning basic VF guidelines.
permalink: pmd_rules_vf_security.html
folder: pmd/rules/vf
sidebaractiveurl: /pmd_rules_vf.html
editmepath: ../pmd-visualforce/src/main/resources/rulesets/vf/security.xml
---
## VfUnescapeEl
**Since:** 5.6.0

**Priority:** Medium (3)

Avoid unescaped user controlled content in EL as it results in XSS.

**Example(s):**
```
<apex:outputText value="Potential XSS is {! here }" escape="false" />
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|

## VfCsrf
**Since:** 5.6.0

**Priority:** Medium (3)

Avoid calling VF action upon page load as the action becomes vulnerable to CSRF.

**Example(s):**
```
<apex:page controller="AcRestActionsController" action="{!csrfInitMethod}" >
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|

