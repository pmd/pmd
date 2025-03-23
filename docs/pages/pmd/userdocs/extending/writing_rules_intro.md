---
title: Introduction to writing PMD rules
tags: [extending, userdocs, getting_started]
summary: "Writing your own PMD rules"
last_updated: December 2023 (7.0.0)
permalink: pmd_userdocs_extending_writing_rules_intro.html
author: Clément Fournier <clement.fournier76@gmail.com>
---

PMD is a framework to perform code analysis. You can create your own rules to
check for patterns specific to your codebase, or the coding practices of your
team.

## How rules work: the AST

Before running rules, PMD parses the source file into a data structure called an
**[abstract syntax tree (AST)](https://en.wikipedia.org/wiki/Abstract_syntax_tree)**.
This tree represents the syntactic structure of the
code, and encodes syntactic relations between source code elements. For instance,
in Java, method declarations belong to a class: in the AST, the nodes representing
method declarations will be descendants of a node representing the declaration of
their enclosing class. This representation is thus much richer than the original
source code (which, for a program, is just a chain of characters), or the token
chain produced by a lexer. For example:

<table>
<thead>
<tr class="header">
<th>Sample code (Java)</th>
<th>AST</th>
</tr>
</thead>
<tbody>
<tr>
<td markdown="block">

```java
class Foo extends Object {

}
```

</td>
<td markdown="block">

```java
└─ CompilationUnit
   └─ TypeDeclaration
      └─ ClassDeclaration "Foo"
         ├─ ModifierList
         ├─ ExtendsList
         │  └─ ClassType "Object"
         └─ ClassBody
```

</td>
</tr>
</tbody>
</table>

Conceptually, PMD rules work by **matching a "pattern" against the AST** of a
file.
Rules explore the AST and find nodes that satisfy some conditions that are characteristic
of the specific thing the rule is trying to flag. Rules then report a violation on these nodes.

### Discovering the AST


ASTs are represented by Java classes deriving from {% jdoc core::lang.ast.Node %}.
Each PMD language has its own set of such classes, and its own rules about how
these classes relate to one another, based on the grammar of the language. For
example, all Java AST nodes extend {% jdoc java::lang.java.ast.JavaNode %}.

The structure of the AST can be discovered through
 * the [Rule Designer](pmd_userdocs_extending_designer_reference.html#ast-inspection)
 * the [AST dump feature](pmd_userdocs_extending_ast_dump.html)






## Writing new rules

PMD supports two ways to define rules: using an **XPath query**, or using a
**Java visitor**. XPath rules are much easier to set up, since they're defined
directly in your ruleset XML, and are expressive enough for nearly any task.

On the other hand, some parts of PMD's API are only accessible from Java, e.g.
accessing the usages of a declaration. And Java rules allow you to do some
complicated processing, to which an XPath rule couldn't scale.

In the end, choosing one strategy or the other depends on the difficulty of what
your rule does. I'd advise to keep to XPath unless you have no other choice.

Note: Despite that fact, the Java rules are written in Java, any language that PMD supports
can be analyzed. E.g. you can write a Java rule to analyze Apex source code.

## XML rule definition

New rules must be declared in a ruleset before they're referenced. This is the
case for both XPath and Java rules. To do this, the `rule` element is used, but
instead of mentioning the `ref` attribute, it mentions the `class` attribute,
with the implementation class of your rule.

* **For Java rules:** this is the concrete class extending AbstractRule (transitively)
* **For XPath rules:** this is `net.sourceforge.pmd.lang.rule.xpath.XPathRule`.
* **For XPath rules analyzing XML-based languages:** this is `net.sourceforge.pmd.lang.xml.rule.DomXPathRule`.
  See [XPath rules in XML](pmd_languages_xml.html#xpath-rules-in-xml) for more info.

Example for Java rule:

```xml
<rule name="MyJavaRule"
      language="java"
      message="Violation!"
      class="com.me.MyJavaRule">
    <description>
        Description
    </description>
    <priority>3</priority>
</rule>
```

Example for XPath rule:

```xml
<rule name="MyXPathRule"
      language="java"
      message="Violation!"
      class="net.sourceforge.pmd.lang.rule.xpath.XPathRule">
    <description>
        Description
    </description>
    <priority>3</priority>
    <properties>
        <property name="xpath">
            <value><![CDATA[
//ClassOrInterfaceDeclaration
]]></value>
        </property>
    </properties>
</rule>
```


{% include note.html content="Since PMD 7, the `language` attribute is required on all `rule`
    elements that declare a new rule. In PMD 6, this was optional, as the base rule classes sometimes set
    the language implicitly in their constructor." %}

## Resource index

To learn how to write a rule:

* [Your First Rule](pmd_userdocs_extending_your_first_rule.html)
introduces the basic development process of a rule with a running example
* [Writing XPath Rules](pmd_userdocs_extending_writing_xpath_rules.html)
explains a bit more about XPath rules and our XPath API
* [Writing Java Rules](pmd_userdocs_extending_writing_java_rules.html)
describes how to write a rule in Java

To go further:
* [Defining Properties](pmd_userdocs_extending_defining_properties.html)
describes how to make your rules more configurable with rule properties
* [Testing your Rules](pmd_userdocs_extending_testing.html) introduces
our testing framework and how you can use it to safeguard the quality of
your rule

