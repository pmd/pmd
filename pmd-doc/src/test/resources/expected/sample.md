---
title: Sample
summary: Sample ruleset to test rule doc generation.
permalink: pmd_rules_java_sample.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../rulesets/ruledoctest/sample.xml
keywords: Sample, OverrideBothEqualsAndHashcode, JumbledIncrementer, DeprecatedSample, RenamedRule, MovedRule
---
## DeprecatedSample

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.0

**Priority:** Medium (3)

Just some description of a deprecated rule.

```
//ForStatement
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/sample.xml/DeprecatedSample" />
```

## JumbledIncrementer

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.

```
//ForStatement
 [
  ForUpdate/StatementExpressionList/StatementExpression/PostfixExpression/PrimaryExpression/PrimaryPrefix/Name/@Image
  =
  ancestor::ForStatement/ForInit//VariableDeclaratorId/@Image
 ]
```

**Example(s):**

``` java
public class JumbledIncrementerRule1 {
    public void foo() {
        for (int i = 0; i < 10; i++) {          // only references 'i'
            for (int k = 0; k < 20; i++) {      // references both 'i' and 'k'
                System.out.println("Hello");
            }
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|sampleAdditionalProperty|the value|This is a additional property for tests|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/sample.xml/JumbledIncrementer" />
```

## MovedRule

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [JumbledIncrementer](pmd_rules_java_sample2.html#jumbledincrementer)

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.

```
//ForStatement
  [
    ForUpdate/StatementExpressionList/StatementExpression/PostfixExpression/PrimaryExpression/PrimaryPrefix/Name/@Image
    =
    ancestor::ForStatement/ForInit//VariableDeclaratorId/@Image
  ]
```

**Example(s):**

``` java
public class JumbledIncrementerRule1 {
    public void foo() {
        for (int i = 0; i < 10; i++) {          // only references 'i'
            for (int k = 0; k < 20; i++) {      // references both 'i' and 'k'
                System.out.println("Hello");
            }
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/sample.xml/MovedRule" />
```

## OverrideBothEqualsAndHashcode

**Since:** PMD 0.4

**Priority:** Medium (3)

**Minimum Language Version:** Java 1.5

Override both `public boolean Object.equals(Object other)`, and `public int Object.hashCode()`, or override neither.
Even if you are inheriting a `hashCode()` from a parent class, consider implementing hashCode and explicitly
delegating to your superclass.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.OverrideBothEqualsAndHashcodeRule](https://github.com/pmd/pmd/blob/master/net/sourceforge/pmd/lang/java/rule/basic/OverrideBothEqualsAndHashcodeRule.java)

**Example(s):**

``` java
public class Bar {      // poor, missing a hashcode() method
    public boolean equals(Object o) {
      // do some comparison
    }
}

public class Baz {      // poor, missing an equals() method
    public int hashCode() {
      // return some hash value
    }
}

public class Foo {      // perfect, both methods provided
    public boolean equals(Object other) {
      // do some comparison
    }
    public int hashCode() {
      // return some hash value
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/sample.xml/OverrideBothEqualsAndHashcode" />
```

## RenamedRule

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

This rule has been renamed. Use instead: [JumbledIncrementer](#jumbledincrementer)

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.

```
//ForStatement
 [
  ForUpdate/StatementExpressionList/StatementExpression/PostfixExpression/PrimaryExpression/PrimaryPrefix/Name/@Image
  =
  ancestor::ForStatement/ForInit//VariableDeclaratorId/@Image
 ]
```

**Example(s):**

``` java
public class JumbledIncrementerRule1 {
    public void foo() {
        for (int i = 0; i < 10; i++) {          // only references 'i'
            for (int k = 0; k < 20; i++) {      // references both 'i' and 'k'
                System.out.println("Hello");
            }
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|sampleAdditionalProperty|the value|This is a additional property for tests|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/sample.xml/RenamedRule" />
```
