---
title: PMD How it Works
tags: [customizing]
summary: How PMD Works
last_updated: July 3, 2016
permalink: pmd_devdocs_how_pmd_works.html
author: Tom Copeland
---

# How it works

PMD checks source code against rules and produces a report. Like this:

*   Something passes a file name and a RuleSet into PMD.
*   PMD hands an InputStream of the source file to a JavaCC-generated parser.
*   PMD gets a reference to an Abstract Syntax Tree back from the parser.
*   PMD hands the AST off to the symbol table layer which builds scopes, finds declarations, and find usages.
*   If any rules need data flow analysis, PMD hands the AST over to the DFA layer for building control flow graphs and data flow nodes.
*   Each Rule in the RuleSet gets to traverse the AST and check for problems. The rules can also poke around the symbol table and DFA nodes.
*   The Report is now filled with RuleViolations, and those get printed out in XML or HTML or whatever.

Not much detail here… if you think this document can be improved, please post [here](http://sourceforge.net/p/pmd/discussion/188192) and let us know how. Thanks!
