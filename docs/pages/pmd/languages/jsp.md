---
title: JSP Support
permalink: pmd_languages_jsp.html
author: Pieter Vanraemdonck
---

## What is currently supported and what is not

In short, JSP files that are XHTML-compliant, are supported.
Except for files that contain inline DTDs; only references to external
DTD files are supported (having inline DTD will result in a parsing
error).

The XHTML support means that:

*   opening tags must be accompanied by corresponding *closing tags*
    (or they must be empty tags). This means that currently a "&lt;HR&gt;"
    tag without corresponding closing tag will result in a parsing error.

*   *attribute values* must be *surrounded by* single or double *quotes*. This means that the following syntax
    will result in a parsing error:

    &lt;MyTag myAttr1=true myAttr2=1024/&gt;

*   &lt; and &gt; characters must be *escaped*, or put inside a CDATA section.

    PMD creates a "Abstract Syntax Tree" representation of source code; the rules use such a tree as input.
    For JSP files, the following constructs are parsed into nodes of the tree:

    *   XML-elements, XML-attributes, XML-comments, doctype-declarations, CDATA
    *   JSP-directives, JSP-declarations, JSP-comments, JSP-scriptlets, JSP-expressions,
        Expression Language expressions, JSF value bindings
    *   everything else is seen as flat text nodes.

*   Java code (e.g. in JSP-scriptlets) and EL expressions are not parsed or
    further broken down. If you want to create rules that check the code
    inside EL expressions or JSP scriptlets (a.o.), you currently would
    have to do "manual" string manipulation (e.g. using regular expressions).

## How to use it

Using the command-line interface, two new options can be used in the arguments string:

*   "-jsp" : this triggers checking JSP files (they are not checked by default)
*   "-nojava" : this tells PMD not to check java source files (they are checked by default)

Using the Ant task, you decide if PMD must check JSP files by choosing
what files are given to the PMD task. If you use a fileset that
contains only ".java" files, JSP files obviously will not be checked.

If you want to call the PMD API for checking JSP files, you should investigate the javadoc of PMD.
