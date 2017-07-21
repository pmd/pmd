---
title: PMD Introduction
keywords: java
tags: [getting_started]
sidebar: pmd_sidebar
permalink: index.html
summary: Welcome to PMD, an extensible cross-language static code analyzer.
---

![PMD Logo](images/pmd-logo-big.png)

# Welcome to PMD

PMD scans source code in Java and other languages and looks for potential problems like:

*   Possible bugs - empty try/catch/finally/switch statements
*   Dead code - unused local variables, parameters and private methods
*   Suboptimal code - wasteful String/StringBuffer usage
*   Overcomplicated expressions - unnecessary if statements, for loops that could be while loops
*   Duplicate code - copied/pasted code means copied/pasted bugs

## Download

You can [download everything from here](https://github.com/pmd/pmd/releases).

You can get an overview of all the rules for e.g. Java at the [rulesets index](pmd_rules_java.html) page.

PMD is [integrated](usage/integrations.html) with JDeveloper, Eclipse, JEdit, JBuilder, BlueJ, CodeGuide, NetBeans/Sun Java Studio Enterprise/Creator, IntelliJ IDEA, TextPad, Maven, Ant, Gel, JCreator, and Emacs.


## Future Releases

The next version of PMD will be developed in parallel with this release. We will release additional bugfix versions as needed.

A [snapshot](http://pmd.sourceforge.net/snapshot) of the web site for the new version is generated daily by our continuous integration server.

Maven packages are also generated regularly and uploaded to [Sonatypes OSS snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/net/sourceforge/pmd/pmd/).

{% include links.html %}
