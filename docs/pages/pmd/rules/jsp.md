---
title: Java Server Pages Rules
permalink: pmd_rules_jsp.html
folder: pmd/rules
---
List of rulesets and rules contained in each ruleset.

*   [Best Practices](pmd_rules_jsp_bestpractices.html): Rules which enforce generally accepted best practices.
*   [Codestyle](pmd_rules_jsp_codestyle.html): Rules which enforce a specific coding style.
*   [Design](pmd_rules_jsp_design.html): Rules that help you discover design issues.
*   [Error Prone](pmd_rules_jsp_errorprone.html): Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.
*   [Security](pmd_rules_jsp_security.html): Rules that flag potential security flaws.

## Best Practices
*   [DontNestJsfInJstlIteration](pmd_rules_jsp_bestpractices.html#dontnestjsfinjstliteration): Do not nest JSF component custom actions inside a custom action that iterates over its body.
*   [NoClassAttribute](pmd_rules_jsp_bestpractices.html#noclassattribute): Do not use an attribute called 'class'. Use "styleclass" for CSS styles.
*   [NoHtmlComments](pmd_rules_jsp_bestpractices.html#nohtmlcomments): In a production system, HTML comments increase the payloadbetween the application server to the c...
*   [NoJspForward](pmd_rules_jsp_bestpractices.html#nojspforward): Do not do a forward from within a JSP file.

## Codestyle
*   [DuplicateJspImports](pmd_rules_jsp_codestyle.html#duplicatejspimports): Avoid duplicate import statements inside JSP's.

## Design
*   [NoInlineScript](pmd_rules_jsp_design.html#noinlinescript): Avoid inlining HTML script content.  Consider externalizing the HTML script using the 'src' attri...
*   [NoInlineStyleInformation](pmd_rules_jsp_design.html#noinlinestyleinformation): Style information should be put in CSS files, not in JSPs. Therefore, don't use <B> or <FONT>tags...
*   [NoLongScripts](pmd_rules_jsp_design.html#nolongscripts): Scripts should be part of Tag Libraries, rather than part of JSP pages.
*   [NoScriptlets](pmd_rules_jsp_design.html#noscriptlets): Scriptlets should be factored into Tag Libraries or JSP declarations, rather than being part of J...

## Error Prone
*   [JspEncoding](pmd_rules_jsp_errorprone.html#jspencoding): A missing 'meta' tag or page directive will trigger this rule, as well as a non-UTF-8 charset.

## Security
*   [IframeMissingSrcAttribute](pmd_rules_jsp_security.html#iframemissingsrcattribute): IFrames which are missing a src element can cause security information popups in IE if you are ac...
*   [NoUnsanitizedJSPExpression](pmd_rules_jsp_security.html#nounsanitizedjspexpression): Avoid using expressions without escaping / sanitizing. This could lead to cross site scripting - ...

