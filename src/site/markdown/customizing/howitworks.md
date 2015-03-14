<!--
    <author>Tom Copeland</author>
-->

# How it works

PMD checks source code against rules and produces a report.  Like this:

*   Something passes a file name and a RuleSet into PMD
*   PMD hands an InputStream to the file off to a JavaCC-generated parser
*   PMD gets a reference to an Abstract Syntax Tree back from the parser
*   PMD hands the AST off to the symbol table layer which builds scopes, finds declarations, and find usages.
*   If any rules need data flow analysis, PMD hands the AST over to the DFA layer for building control flow
    graphs and data flow nodes.
*   Each Rule in the RuleSet gets to traverse the AST and check for problems.  The rules can also poke around the
    symbol table and DFA nodes.
*   The Report is now filled with RuleViolations, and those get printed out in XML or HTML or whatever

Not much detail here... if you think this document can be
improved, please post [here][forum] and let me know how.  Thanks!

[forum]: http://sourceforge.net/p/pmd/discussion/188192
