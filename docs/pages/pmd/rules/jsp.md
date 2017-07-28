---
title: Java Server Pages Rules
permalink: pmd_rules_jsp.html
folder: pmd/rules
---
List of rulesets and rules contained in each ruleset.

*   [Basic JSF](pmd_rules_jsp_basic-jsf.html): Rules concerning basic JSF guidelines.
*   [Basic JSP](pmd_rules_jsp_basic.html): Rules concerning basic JSP guidelines.

## Basic JSF
*   [DontNestJsfInJstlIteration](pmd_rules_jsp_basic-jsf.html#dontnestjsfinjstliteration): Do not nest JSF component custom actions inside a custom action that iterates over its body.

## Basic JSP
*   [NoLongScripts](pmd_rules_jsp_basic.html#nolongscripts): Scripts should be part of Tag Libraries, rather than part of JSP pages.
*   [NoScriptlets](pmd_rules_jsp_basic.html#noscriptlets): Scriptlets should be factored into Tag Libraries or JSP	declarations, rather than being part of J...
*   [NoInlineStyleInformation](pmd_rules_jsp_basic.html#noinlinestyleinformation): Style information should be put in CSS files, not in JSPs. Therefore, don't use &lt;B> or &lt;FON...
*   [NoClassAttribute](pmd_rules_jsp_basic.html#noclassattribute): Do not use an attribute called 'class'. Use "styleclass" for CSS styles.
*   [NoJspForward](pmd_rules_jsp_basic.html#nojspforward): Do not do a forward from within a JSP file.
*   [IframeMissingSrcAttribute](pmd_rules_jsp_basic.html#iframemissingsrcattribute): IFrames which are missing a src element can cause security information popups in IE if you are ac...
*   [NoHtmlComments](pmd_rules_jsp_basic.html#nohtmlcomments): In a production system, HTML comments increase the payload			between the application server to th...
*   [DuplicateJspImports](pmd_rules_jsp_basic.html#duplicatejspimports): Avoid duplicate import statements inside JSP's.
*   [JspEncoding](pmd_rules_jsp_basic.html#jspencoding): A missing 'meta' tag or page directive will trigger this rule, as well as a non-UTF-8 charset.
*   [NoInlineScript](pmd_rules_jsp_basic.html#noinlinescript): Avoid inlining HTML script content.  Consider externalizing the HTML script using the 'src' attri...
*   [NoUnsanitizedJSPExpression](pmd_rules_jsp_basic.html#nounsanitizedjspexpression): Avoid using expressions without escaping / sanitizing. This could lead to cross site scripting - ...

