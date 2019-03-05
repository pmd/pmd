---
title: Intro to writing PMD rules
tags: [extending, userdocs]
summary: "Writing your own PMD rules TODO"
last_updated: July 2018 (6.6.0)
permalink: pmd_userdocs_extending_writing_rules_intro.html
author: Clément Fournier <clement.fournier76@gmail.com>
---


## Why write custom rules?

TODO

## How rules work: the AST

Before running rules, PMD parses the source file into a data structure called an
*abstract syntax tree* (AST). This tree represents the syntactic structure of the
code, and encodes syntactic relations between source code elements. For instance,
in Java, method declarations belong to a class: in the AST, the nodes representing
method declarations will be descendants of a node representing the declaration of
their enclosing class. This representation is thus much richer than the original
source code (which, for a program, is just a chain of characters), or the token
chain produced by a lexer (which is e.g. what Checkstyle works on).

Conceptually, PMD rules work by *matching a "pattern" against the AST* of a file.
Rules explore the AST and find nodes that satisfy some conditions that are characteristic
of the specific thing the rule is trying to flag. Rules then report a violation on these nodes.

## Defining rules

PMD supports two ways to define rules: using an **XPath query**, or using a
**Java visitor**. XPath rules are much easier to set up, since they're defined
directly in your ruleset XML, and are expressive enough for most tasks.

On the other hand, some parts of PMD's API are only accessible from Java, e.g.
accessing the usages of a declaration. And Java rules allow you to do some
complicated processing, to which an XPath rule couldn't scale.

In the end, choosing one strategy or the other depends on the difficulty of what
your rule does. I'd advise to keep to XPath unless you have no other choice.


## Testing rules

TODO link to the page