---
title: How PMD Works
tags: [devdocs]
summary: Processing overview of the different steps taken by PMD.
last_updated: September 2017
permalink: pmd_devdocs_how_pmd_works.html
author: Tom Copeland, Andreas Dangel <andreas.dangel@adangel.org>
---

## Overview

The processing starts e.g. with the main class: `net.sourceforge.pmd.PMD`

{%include note.html content="This is the command line interface. There are many other means, who
PMD can be invoked. E.g. via ant, maven, gradle..." %}

*   Parse command line parameters (see net.sourceforge.pmd.cli.PMDParameters)
    Also load the incremental analysis cache file
*   Load rulesets/rules
*   Determine languages (rules of different languages might be mixed in rulesets)
*   Determine files (uses the given source directory, filter by the language's file extensions)
*   Prepare the renderer
*   Sort the files by name
*   Check whether we can use the incremental analysis cache (if the rulesets changed, it will be invalid)
*   Prepare the SourceCodeProcessor based on the configuration
*   Analyze the files. Either single threaded or multi-threaded parallel. This task is encapsulated
    in `net.sourceforge.pmd.processor.PMDRunnable`:
    *   Create input stream
    *   Call source code processor (`net.sourceforge.pmd.SourceCodeProcessor`):
        1.  Determine the language
        2.  Check whether the file is already analyzed and a result is available from the analysis cache
        3.  Parse the source code. Result is the root AST node.
        4.  Always run the SymbolFacade visitor. It builds scopes, finds declarations and usages.
        5.  Run DFA (data flow analysis) visitor (if at least one rule requires it) for building
            control flow graphs and data flow nodes.
        6.  Run TypeResolution visitor (if at least one rule requires it)
        7.  FUTURE: Run multifile analysis (if at least one rule requires it)
        8.  Execute the rules:
            *   First run the rules that opted in for the rule chain mechanism
            *   Run all the other rules and let them traverse the AST. The rules can use the symbol table,
                type resolution information and DFA nodes.
            *   The rules will report found problems as RuleViolations.
*   Render the found violations into the wanted format (XML, text, HTML, ...)
*   Store the incremental analysis cache
*   Depending on the number of violations found, exit with code 0 or 4.
