---
title: Sample
summary: Sample ruleset to test rule doc generation.
permalink: pmd_rules_java_sample.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../rulesets/ruledoctest/sample.xml
keywords: Sample, OverrideBothEqualsAndHashcode, JumbledIncrementer, DeprecatedSample, RenamedRule, MovedRule
language: Java
---
## DeprecatedSample

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.0

**Priority:** Medium (3)

Just some description of a deprecated rule.

**This rule is defined by the following XPath expression:**
``` xpath
//ForStatement
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/sample.xml/DeprecatedSample" />
```

## JumbledIncrementer

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.

**This rule is defined by the following XPath expression:**
``` xpath
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|sampleAdditionalProperty|the value|This is a additional property for tests|no|
|sampleMultiStringProperty|Value1 \| Value2|Test property with multiple strings|yes. Delimiter is '\|'.|
|sampleDeprecatedProperty|test|<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated</span>  This is a sample deprecated property for tests|no|
|sampleRegexProperty1|\\/\\\*\\s+(default\|package)\\s+\\\*\\/|The property is of type regex|no|
|sampleRegexProperty2|\[a-z\]\*|The property is of type regex|no|
|sampleRegexProperty3|\\s+|The property is of type regex|no|
|sampleRegexProperty4|\_dd\_|The property is of type regex|no|
|sampleRegexProperty5|\[0-9\]{1,3}|The property is of type regex|no|
|sampleRegexProperty6|\\b|The property is of type regex|no|
|sampleRegexProperty7|\\n|The property is of type regex|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/sample.xml/JumbledIncrementer" />
```

## MovedRule

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [JumbledIncrementer](pmd_rules_java_sample2.html#jumbledincrementer)

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.

**This rule is defined by the following XPath expression:**
``` xpath
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
<rule ref="category/java/sample.xml/MovedRule" />
```

## OverrideBothEqualsAndHashcode

**Since:** PMD 0.4

**Priority:** Medium (3)

**Minimum Language Version:** Java 1.5

Override both `public boolean Object.equals(Object other)`, and `public int Object.hashCode()`, or override neither.
Even if you are inheriting a `hashCode()` from a parent class, consider implementing hashCode and explicitly
delegating to your superclass.

Second paragraph.

    Code sample

Third paragraph.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.errorprone.OverrideBothEqualsAndHashcodeRule](https://github.com/pmd/pmd/blob/master/net/sourceforge/pmd/lang/java/rule/errorprone/OverrideBothEqualsAndHashcodeRule.java)

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
<rule ref="category/java/sample.xml/OverrideBothEqualsAndHashcode" />
```

## RenamedRule

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

This rule has been renamed. Use instead: [JumbledIncrementer](#jumbledincrementer)

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.

**This rule is defined by the following XPath expression:**
``` xpath
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|sampleAdditionalProperty|the value|This is a additional property for tests|no|
|sampleMultiStringProperty|Value1 \| Value2|Test property with multiple strings|yes. Delimiter is '\|'.|
|sampleDeprecatedProperty|test|<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated</span>  This is a sample deprecated property for tests|no|
|sampleRegexProperty1|\\/\\\*\\s+(default\|package)\\s+\\\*\\/|The property is of type regex|no|
|sampleRegexProperty2|\[a-z\]\*|The property is of type regex|no|
|sampleRegexProperty3|\\s+|The property is of type regex|no|
|sampleRegexProperty4|\_dd\_|The property is of type regex|no|
|sampleRegexProperty5|\[0-9\]{1,3}|The property is of type regex|no|
|sampleRegexProperty6|\\b|The property is of type regex|no|
|sampleRegexProperty7|\\n|The property is of type regex|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/sample.xml/RenamedRule" />
```
