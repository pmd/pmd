---
title: Documentation
summary: Rules that are related to code documentation.
permalink: pmd_rules_java_documentation.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/category/java/documentation.xml
keywords: Documentation, CommentContent, CommentRequired, CommentSize, UncommentedEmptyConstructor, UncommentedEmptyMethodBody
language: Java
---
## CommentContent

**Since:** PMD 5.0

**Priority:** Medium (3)

A rule for the politically correct... we don't want to offend anyone.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.documentation.CommentContentRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/documentation/CommentContentRule.java)

**Example(s):**

``` java
//OMG, this is horrible, Bob is an idiot !!!
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|disallowedTerms|[idiot, jerk]|Illegal terms or phrases|
|caseSensitive|false|Case sensitive|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/documentation.xml/CommentContent" />
```

## CommentRequired

**Since:** PMD 5.1

**Priority:** Medium (3)

Denotes whether comments are required (or unwanted) for specific language elements.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.documentation.CommentRequiredRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/documentation/CommentRequiredRule.java)

**Example(s):**

``` java
/**
* 
*
* @author Jon Doe
*/
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|serialVersionUIDCommentRequired|Ignored|Serial version UID comments. Possible values: [Required, Ignored, Unwanted]|
|enumCommentRequirement|Required|Enum comments. Possible values: [Required, Ignored, Unwanted]|
|protectedMethodCommentRequirement|Required|Protected method constructor comments. Possible values: [Required, Ignored, Unwanted]|
|publicMethodCommentRequirement|Required|Public method and constructor comments. Possible values: [Required, Ignored, Unwanted]|
|fieldCommentRequirement|Required|Field comments. Possible values: [Required, Ignored, Unwanted]|
|headerCommentRequirement|Required|Header comments. Possible values: [Required, Ignored, Unwanted]|
|methodWithOverrideCommentRequirement|Ignored|Comments on @Override methods. Possible values: [Required, Ignored, Unwanted]|
|accessorCommentRequirement|Ignored|Comments on getters and setters". Possible values: [Required, Ignored, Unwanted]|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/documentation.xml/CommentRequired" />
```

## CommentSize

**Since:** PMD 5.0

**Priority:** Medium (3)

Determines whether the dimensions of non-header comments found are within the specified limits.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.documentation.CommentSizeRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/documentation/CommentSizeRule.java)

**Example(s):**

``` java
/**
*
*   too many lines!
*
*
*
*
*
*
*
*
*
*
*
*
*/
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|maxLines|6|Maximum lines|
|maxLineLength|80|Maximum line length|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/documentation.xml/CommentSize" />
```

## UncommentedEmptyConstructor

**Since:** PMD 3.4

**Priority:** Medium (3)

Uncommented Empty Constructor finds instances where a constructor does not
contain statements, but there is no comment. By explicitly commenting empty
constructors it is easier to distinguish between intentional (commented)
and unintentional empty constructors.

```
//ConstructorDeclaration[@Private='false']
                        [count(BlockStatement) = 0 and ($ignoreExplicitConstructorInvocation = 'true' or not(ExplicitConstructorInvocation)) and @containsComment = 'false']
                        [not(../Annotation/MarkerAnnotation/Name[typeof(@Image, 'javax.inject.Inject', 'Inject')])]
```

**Example(s):**

``` java
public Foo() {
  // This constructor is intentionally empty. Nothing special is needed here.
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|ignoreExplicitConstructorInvocation|false|Ignore explicit constructor invocation when deciding whether constructor is empty or not|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/documentation.xml/UncommentedEmptyConstructor" />
```

## UncommentedEmptyMethodBody

**Since:** PMD 3.4

**Priority:** Medium (3)

Uncommented Empty Method Body finds instances where a method body does not contain
statements, but there is no comment. By explicitly commenting empty method bodies
it is easier to distinguish between intentional (commented) and unintentional
empty methods.

```
//MethodDeclaration/Block[count(BlockStatement) = 0 and @containsComment = 'false']
```

**Example(s):**

``` java
public void doSomething() {
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/documentation.xml/UncommentedEmptyMethodBody" />
```

