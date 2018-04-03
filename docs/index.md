---
title: PMD Introduction
keywords: java
tags: [getting_started]
permalink: index.html
toc: false
summary: >
    Welcome to PMD, an extensible cross-language static code analyzer.
    It finds common programming flaws like unused variables, empty catch blocks, unnecessary object creation,
    and so forth. Additionally it includes CPD, the copy-paste-detector. CPD finds duplicated code.
last_updated: August 2017
author: Jeff Jensen <jjensen@apache.org>, Andreas Dangel <andreas.dangel@adangel.org>
---

{% include image.html file="pmd-logo-big.png" alt="PMD Logo" %}

<br>

**PMD** scans source code in Java and other languages and looks for potential problems like:

*   Possible bugs - empty try/catch/finally/switch statements
*   Dead code - unused local variables, parameters and private methods
*   Suboptimal code - wasteful String/StringBuffer usage
*   Overcomplicated expressions - unnecessary if statements, for loops that could be while loops

**CPD**, the copy-paste-detector, finds duplicated code in many languages:

*   Duplicate code is often just copied and pasted. This means, the bugs are also copied and pasted. Fixing
    them means, fix all duplicated code locations.

## Features

{::options parse_block_html="true" /}

<div class="row"><div class="col-lg-6">
### PMD

Features:

*   Supporting 8 languages
*   Many ready-to-use built-in rules.
*   Custom rules can be written in Java
*   Custom rules can be written using XPath expression that query the AST of the sources
*   Many output formats
*   Many integrations into IDEs, build tools

Supported Languages:

*   [Java](pmd_rules_java.html)
*   [JSP](pmd_rules_jsp.html)
*   [JavaScript](pmd_rules_ecmascript.html)
*   [Salesforce.com Apex](pmd_rules_apex.html) and [Visualforce](pmd_rules_vf.html)
*   [PLSQL](pmd_rules_plsql.html)
*   [Apache Velocity](pmd_rules_vm.html)
*   [XML](pmd_rules_xml.html) and [Maven POM](pmd_rules_pom.html)
*   [XSL](pmd_rules_xsl.html)

</div><div class="col-lg-6">
### CPD

Features:

*   Supporting 19 languages
*   Simple GUI
*   Fast
*   Many integrations

Supported Languages:

*   Java
*   C, C++
*   C#
*   Groovy
*   PHP
*   Ruby
*   Fortran
*   JavaScript
*   PLSQL
*   Apache Velocity
*   Scala
*   Objective C
*   Matlab
*   Python
*   Go
*   Swift
*   Salesforce.com Apex and Visualforce

</div></div>

## Download PMD {{ site.pmd.version }}

Latest Version: {{ site.pmd.version }} ({{ site.pmd.date }})

*   [Release Notes](pmd_release_notes.html)
*   [Download](https://github.com/pmd/pmd/releases)



{% include links.html %}
