---
title:  Your first rule XPath
tags: [extending, userdocs]
summary: "Introduction to rule writing through an example."
last_updated: February 2020 (6.22.0)
permalink: pmd_userdocs_extending_your_first_rule.html
author: Miguel Griffa <mikkey@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---

This page is a gentle introduction to rule writing, and the Rule Designer.

Using the designer is useful both to write Java
rules and XPath rules, but it's more specifically geared towards XPath rules.
This page uses a simple XPath rule to illustrate the common workflow. We assume
here that you already know what XPath is and how to read basic XPath queries. W3C
has a good tutorial [here](https://www.w3schools.com/xml/xpath_syntax.asp) if
you don't (in the context of XML only), and [the Saxon documentation](https://www.saxonica.com/documentation/index.html#!expressions)
features a comprehensive but approachable description of the syntax of XPath
expressions.

## The Rule Designer

The rule designer is a tool that packs a lot of features to help you develop XPath
rules quickly and painlessly. Basically, it allows you to examine the AST of a code
snippet and evaluate an XPath expression against it.

Like for PMD and CPD, you can launch it using `run.sh designer` on Linux/Unix
and `designer.bat` on Windows. The interface looks like the following:

{% include image.html file="userdocs/designer-overview-with-nums.png" alt="Designer overview" %}

The zone (2) is the **main editor**. When you write a code snippet in the
 code area to the left, you'll see that the tree to the right will be updated
 automatically: it's the AST of the code.
 Note that the code snippet must be a syntactically valid compilation unit for the
 language you've chosen, e.g. for Java, a compilation unit necessarily has a top-level
 type declaration.

If you select a node in the AST, its specific properties will also be displayed
in the panel (1): they're the XPath attributes of the node. More on that later.

The zone (3) is the **XPath editor**. If you enter an XPath query in that area,
it will be evaluated on the current AST and the results will be displayed in the
list to the bottom right.

### Rule development process


The basic development process is straightforward:

1.  Write a code snippet in the main editor that features the offending code you're looking for
2.  Examine the AST and determine what node the violation should be reported on
3.  Write an XPath expression matching that node in the XPath editor
4.  Refine the XPath expression iteratively using different code snippets, so that
    it matches violation cases, but no other node
5.  Export your XPath expression to an XML rule element, and place it in your ruleset

Each time you test your rule against a different snippet, it's a good idea to
save it to [make test cases](pmd_userdocs_extending_testing.html).

In the following sections, we walk through several examples to refine your rule.

## A simple rule

Let's say you want to prevent your coding team from naming variables of type
`short` after your boss, whose name is Bill. You try the designer on the following
 offending code snippet:

```java

public class KeepingItSerious {

    public void method() {
        short bill; // LocalVariableDeclaration
    }

}

```

Examining the AST, you find out that the LocalVariableDeclaration has a VariableDeclaratorId
descendant, whose `Image` XPath attribute is exactly `bill`. You thus write your first attempt
in the XPath editor:
```xpath
//VariableDeclaratorId[@Image = "bill"]
```

You can see the XPath result list is updated with the variable declarator.
If you try the query against the following updated snippet though, you can
see that the field declaration id is matched even though it's not of type `short`.

```java
public class KeepingItSerious {

    Delegator bill; // FieldDeclaration

    public void method() {
        short bill; // LocalVariableDeclaration
    }

}
```


You thus refine your XPath expression with an additional predicate,
based on your examination of the Type node of the field and local variable
declaration nodes.

```xpath
//VariableDeclaratorId[@Image = "bill" and ../../Type[@TypeImage = "short"]]
```

### Exporting to XML

You estimate that your rule is now production ready, and you'd like to use it in your ruleset.
The `File > Export XPath to rule...` allows you to do that in a few clicks: just enter some
additional metadata for your rule, and the popup will generate an XML element that you can
copy-paste into your ruleset XML. The resulting element looks like so:

```xml
<rule name="DontCallBossShort"
      language="java"
      message="Boss wants to talk to you."
      class="net.sourceforge.pmd.lang.rule.XPathRule" >
    <description>
TODO
    </description>
    <priority>3</priority>
    <properties>
        <property name="xpath">
            <value>
<![CDATA[
//VariableDeclaratorId[../../Type[@TypeImage="short"] and @Image = "bill"]
]]>
            </value>
        </property>
    </properties>
</rule>
```

You can notice that your XPath expression ends up inside a [property](pmd_userdocs_configuring_rules.html#rule-properties)
of a rule of type XPathRule, which is how XPath rules are implemented.
