---
title: Unused Code
summary: The Unused Code ruleset contains rules that find unused or ineffective code.
permalink: pmd_rules_java_unusedcode.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/unusedcode.xml
keywords: Unused Code, UnusedPrivateField, UnusedLocalVariable, UnusedPrivateMethod, UnusedFormalParameter
---
## UnusedFormalParameter

**Since:** PMD 0.8

**Priority:** Medium (3)

Avoid passing parameters to methods or constructors without actually referencing them in the method body.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unusedcode.UnusedFormalParameterRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unusedcode/UnusedFormalParameterRule.java)

**Example(s):**

``` java
public class Foo {
    private void bar(String howdy) {
        // howdy is not used
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkAll|false|Check all methods, including non-private ones|

## UnusedLocalVariable

**Since:** PMD 0.1

**Priority:** Medium (3)

Detects when a local variable is declared and/or assigned, but not used.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unusedcode.UnusedLocalVariableRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unusedcode/UnusedLocalVariableRule.java)

**Example(s):**

``` java
public class Foo {
    public void doSomething() {
        int i = 5; // Unused
    }
}
```

## UnusedPrivateField

**Since:** PMD 0.1

**Priority:** Medium (3)

Detects when a private field is declared and/or assigned a value, but not used.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unusedcode.UnusedPrivateFieldRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unusedcode/UnusedPrivateFieldRule.java)

**Example(s):**

``` java
public class Something {
    private static int FOO = 2; // Unused
    private int i = 5; // Unused
    private int j = 6;
    public int addOne() {
        return j++;
    }
}
```

## UnusedPrivateMethod

**Since:** PMD 0.7

**Priority:** Medium (3)

Unused Private Method detects when a private method is declared but is unused.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unusedcode.UnusedPrivateMethodRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unusedcode/UnusedPrivateMethodRule.java)

**Example(s):**

``` java
public class Something {
    private void foo() {} // unused
}
```

