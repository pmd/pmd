---
title:  Writing XPath rules
tags: [extending, userdocs]
summary: "Writing XPath rules for PMD"
last_updated: July 3, 2016
permalink: pmd_userdocs_extending_writing_xpath_rules.html
author: Miguel Griffa <mikkey@users.sourceforge.net>
---

# XPath Rule tutorial

{% include note.html content="For a translation to Georgian, see [webhostinggeeks.com/science/xpath-sourceforge-ka](http://webhostinggeeks.com/science/xpath-sourceforge-ka)" %}


Writing PMD rules with XPath can be a bit easier than writing rules with Java code. Here’s an introduction on how to do that.

## Introduction

PMD provides a very handy method for writing rules by writing an XPath query. When the XPath query finds a match, a violation is added to the report. This document focuses on XPath rules. You can go [here](howtowritearule.html) for more information about writing a rule.

## What is the Abstract Syntax Tree (AST)?

From [FOLDOC](http://foldoc.org/abstract+syntax+tree) an AST is

> A data structure representing something which has been parsed, often used as a compiler or interpreter’s internal representation of a program while it is being optimised and from which code generation is performed.

In our context, this means that we basically have a tree representation of the Java source file. This tree can viewed as a structured document - just like XML. And since it’s conceptually similar to XML, it can be queried with XPath to find a pattern.

## Using Designer

PMD comes with a handy tool that you will love if you want to write an XPath rule. Designer, runnable from a script in `bin/`, is a very simple and useful utility for writing rules.

The basic steps involved in writing XPath rules are these:

1.  Write a simple Java example source snippet in Designer
2.  See the AST for the class you wrote
3.  Write an XPath expression that matches the violation you are searching
4.  Modify the Java class and go back to previous step to refine the XPath expression

## Simple XPath expressions

This section provides hands-on examples of XPath queries over the AST. You will probably find this section more useful if you follow it with Designer and copy/paste the examples.

Copy the following Java source code to Designer:

```java
public class a {
    int fOne;
    int fTwo;

    private void run() {
        int one;
        int two;
    }
}
```

Let’s assume you want to match something on class variable names. You see in the ASTVviewer that VariableDeclaratorId contains the variable name - in XML terms, the name is in the `@Image` attribute. So you try an XPath expression as follows:

`//VariableDeclaratorId`

If you try this expression you’ll see that variables declared in methods are also matched. A more precise expression for matching field declarations is, well, using the FieldDeclaration node. This expression matches only the two fields declared in the class:

`//FieldDeclaration`

In a similar way, you can match only local variables with this expression

`//LocalVariableDeclaration`

With local variables we need to be more careful. Consider the following class:

```java
public class a {
    private void run() {
        final int one;
        int two;

        {
            int a;
        }
    }
}
```

Local variable declarations will match ‘a’, since it is a perfectly legal Java local variable. Now, a more interesting expression is to match variables declared in a method, and not on an internal block, nor in the class. Maybe you’ll start with an expression like this:

`//MethodDeclaration//LocalVariableDeclaration`

You’ll quickly see that all three local variables are matched. A possible solution for this is to request that the parent of the local variable declaration is the MethodDeclaration node:

`//LocalVariableDeclaration[name(../../..) = 'MethodDeclaration']`

## Matching variables by name

Let’s consider that we are writing rules for logger. Let’s assume we use the Java logging API and we want to find all classes that have more than one logger. The following expression returns all variable declarations whose type is ‘Logger’.

`//VariableDeclarator[../Type/ReferenceType/ClassOrInterfaceType[@Image='Logger']]`

Finding a class with more than one logger is quite easy now. This expression matches the classes we are looking for.

```xpath
TypeDeclaration[count(//VariableDeclarator[../Type/ReferenceType/ClassOrInterfaceType[@Image='Logger']])>1
```

But let’s refine this expression a little bit more. Consider the following class:

```java
public class a {
    Logger log = null;
    Logger log = null;
    int b;

    void myMethod() {
        Logger log = null;
        int a;
    }
    class c {
        Logger a;
        Logger a;
    }
}
```

With this class we will only be matching one violation, when we probably would have wanted to produce two violations (one for each class). The following refined expression matches classes that contain more than one logger.

```xpath
//ClassOrInterfaceBodyDeclaration[count(//VariableDeclarator[../Type/ReferenceType/ClassOrInterfaceType[@Image='Logger']])>1]
```

Let’s assume we have a Factory class, that could be always declared final. We’ll search an xpath expression that matches all declarations of Factory and reports a violation if it is not declared final. Consider the following class:

```java
public class a {
    Factory f1;

    void myMethod() {
        Factory f2;
        int a;
    }
}
```

The following expression does the magic we need:

```xpath
//VariableDeclarator
    [../Type/ReferenceType/ClassOrInterfaceType
        [@Image = 'Factory'] and ..[@Final='false']]
```

We recommend at this point that you experiment with Designer putting the final modifier to the Factory and verifying that the results produced are those expected.

## Creating a new rule definition

To actually use your new XPath rule, it needs to be in a ruleset. You can create a new custom ruleset which just
contains your new XPath rule. You can use the following template. Just make sure, to replace the `xpath` property,
the example code and give your rule a useful name and message.

``` xml
<?xml version="1.0"?>

<ruleset name="Custom Rules"
    xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">
    <description>
Custom rules
    </description>

    <rule name="My Rule"
          language="java"
          message="violation message"
          class="net.sourceforge.pmd.lang.rule.XPathRule">
        <description>
Rule Description
         </description>
         <priority>3</priority>
         <properties>
             <property name="xpath">
                 <value><![CDATA[
--- here comes your XPath expression
]]></value>
             </property>
         </properties>
         <example>
 <![CDATA[
public class ExampleCode {
    public void foo() {
    }
}
]]>
        </example>
    </rule>
</ruleset>
```



Finally, for many more details on writing XPath rules, pick up [PMD Applied](http://pmdapplied.com/)!
