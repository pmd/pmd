---
title: Comments
summary: Rules intended to catch errors related to code comments
permalink: pmd_rules_java_comments.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/comments.xml
---
## CommentContent

**Since:** PMD 5.0

**Priority:** Medium (3)

A rule for the politically correct... we don't want to offend anyone.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.comments.CommentContentRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/comments/CommentContentRule.java)

**Example(s):**

```
//	OMG, this is horrible, Bob is an idiot !!!
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|disallowedTerms|[idiot, jerk]|Illegal terms or phrases|
|caseSensitive|false|Case sensitive|
|wordsAreRegex|false|Use regular expressions|

## CommentDefaultAccessModifier

**Since:** PMD 5.4.0

**Priority:** Medium (3)

To avoid mistakes if we want that a Method, Field or Nested class have a default access modifier
        we must add a comment at the beginning of the Method, Field or Nested class.
        By default the comment must be /* default */, if you want another, you have to provide a regex.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.comments.CommentDefaultAccessModifierRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/comments/CommentDefaultAccessModifierRule.java)

**Example(s):**

```
public class Foo {
            final String stringValue = "some string";
            String getString() {
               return stringValue;
            }

            class NestedFoo {
            }
        }

        // should be
        public class Foo {
            /* default */ final String stringValue = "some string";
            /* default */ String getString() {
               return stringValue;
            }

            /* default */ class NestedFoo {
            }
        }
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|regex||Regular expression|

## CommentRequired

**Since:** PMD 5.1

**Priority:** Medium (3)

Denotes whether comments are required (or unwanted) for specific language elements.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.comments.CommentRequiredRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/comments/CommentRequiredRule.java)

**Example(s):**

```
/**
* 
*
* @author George Bush
*/
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|serialVersionUIDCommentRequired|Ignored|serial version UID commts. Possible values: [Required, Ignored, Unwanted]|
|enumCommentRequirement|Required|Enum comments. Possible values: [Required, Ignored, Unwanted]|
|protectedMethodCommentRequirement|Required|Protected method constructor comments. Possible values: [Required, Ignored, Unwanted]|
|publicMethodCommentRequirement|Required|Public method and constructor comments. Possible values: [Required, Ignored, Unwanted]|
|fieldCommentRequirement|Required|Field comments. Possible values: [Required, Ignored, Unwanted]|
|headerCommentRequirement|Required|Header comments. Possible values: [Required, Ignored, Unwanted]|

## CommentSize

**Since:** PMD 5.0

**Priority:** Medium (3)

Determines whether the dimensions of non-header comments found are within the specified limits.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.comments.CommentSizeRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/comments/CommentSizeRule.java)

**Example(s):**

```
/**
*
*	too many lines!
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

