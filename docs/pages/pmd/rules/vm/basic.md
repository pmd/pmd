---
title: Basic Velocity
summary: The Basic Velocity ruleset contains basic rules for Apache Velocity pages.
permalink: pmd_rules_vm_basic.html
folder: pmd/rules/vm
sidebaractiveurl: /pmd_rules_vm.html
editmepath: ../pmd-vm/src/main/resources/rulesets/vm/basic.xml
keywords: Basic Velocity, AvoidDeeplyNestedIfStmts, CollapsibleIfStatements, ExcessiveTemplateLength, AvoidReassigningParameters, EmptyIfStmt, EmptyForeachStmt, UnusedMacroParameter, NoInlineJavaScript, NoInlineStyles
---
## AvoidDeeplyNestedIfStmts

**Since:** PMD 5.1

**Priority:** Medium (3)

Avoid creating deeply nested if-then statements since they are harder to read and error-prone to maintain.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.vm.rule.basic.AvoidDeeplyNestedIfStmtsRule](https://github.com/pmd/pmd/blob/master/pmd-vm/src/main/java/net/sourceforge/pmd/lang/vm/rule/basic/AvoidDeeplyNestedIfStmtsRule.java)

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|problemDepth|3|The if statement depth reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/vm/basic.xml/AvoidDeeplyNestedIfStmts" />
```

## AvoidReassigningParameters

**Since:** PMD 5.1

**Priority:** Medium High (2)

Reassigning values to incoming parameters is not recommended.  Use temporary local variables instead.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.vm.rule.basic.AvoidReassigningParametersRule](https://github.com/pmd/pmd/blob/master/pmd-vm/src/main/java/net/sourceforge/pmd/lang/vm/rule/basic/AvoidReassigningParametersRule.java)

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/vm/basic.xml/AvoidReassigningParameters" />
```

## CollapsibleIfStatements

**Since:** PMD 5.1

**Priority:** Medium (3)

Sometimes two consecutive 'if' statements can be consolidated by separating their conditions with a boolean short-circuit operator.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.vm.rule.basic.CollapsibleIfStatementsRule](https://github.com/pmd/pmd/blob/master/pmd-vm/src/main/java/net/sourceforge/pmd/lang/vm/rule/basic/CollapsibleIfStatementsRule.java)

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/vm/basic.xml/CollapsibleIfStatements" />
```

## EmptyForeachStmt

**Since:** PMD 5.1

**Priority:** Medium High (2)

Empty foreach statements should be deleted.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.vm.rule.basic.EmptyForeachStmtRule](https://github.com/pmd/pmd/blob/master/pmd-vm/src/main/java/net/sourceforge/pmd/lang/vm/rule/basic/EmptyForeachStmtRule.java)

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/vm/basic.xml/EmptyForeachStmt" />
```

## EmptyIfStmt

**Since:** PMD 5.1

**Priority:** Medium High (2)

Empty if statements should be deleted.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.vm.rule.basic.EmptyIfStmtRule](https://github.com/pmd/pmd/blob/master/pmd-vm/src/main/java/net/sourceforge/pmd/lang/vm/rule/basic/EmptyIfStmtRule.java)

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/vm/basic.xml/EmptyIfStmt" />
```

## ExcessiveTemplateLength

**Since:** PMD 5.1

**Priority:** Medium (3)

The template is too long. It should be broken up into smaller pieces.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.vm.rule.basic.ExcessiveTemplateLengthRule](https://github.com/pmd/pmd/blob/master/pmd-vm/src/main/java/net/sourceforge/pmd/lang/vm/rule/basic/ExcessiveTemplateLengthRule.java)

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/vm/basic.xml/ExcessiveTemplateLength" />
```

## NoInlineJavaScript

**Since:** PMD 5.1

**Priority:** Medium High (2)

Avoid inline JavaScript. Import .js files instead.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.vm.rule.basic.NoInlineJavaScriptRule](https://github.com/pmd/pmd/blob/master/pmd-vm/src/main/java/net/sourceforge/pmd/lang/vm/rule/basic/NoInlineJavaScriptRule.java)

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/vm/basic.xml/NoInlineJavaScript" />
```

## NoInlineStyles

**Since:** PMD 5.1

**Priority:** Medium High (2)

Avoid inline styles. Use css classes instead.

```
//Text[matches(@literal, "<[^>]+\s[sS][tT][yY][lL][eE]\s*=")]
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/vm/basic.xml/NoInlineStyles" />
```

## UnusedMacroParameter

**Since:** PMD 5.1

**Priority:** Medium High (2)

Avoid unused macro parameters. They should be deleted.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.vm.rule.basic.UnusedMacroParameterRule](https://github.com/pmd/pmd/blob/master/pmd-vm/src/main/java/net/sourceforge/pmd/lang/vm/rule/basic/UnusedMacroParameterRule.java)

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/vm/basic.xml/UnusedMacroParameter" />
```

