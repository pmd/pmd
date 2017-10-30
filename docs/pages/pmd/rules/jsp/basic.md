---
title: Basic JSP
summary: Rules concerning basic JSP guidelines.
permalink: pmd_rules_jsp_basic.html
folder: pmd/rules/jsp
sidebaractiveurl: /pmd_rules_jsp.html
editmepath: ../pmd-jsp/src/main/resources/rulesets/jsp/basic.xml
keywords: Basic JSP, NoLongScripts, NoScriptlets, NoInlineStyleInformation, NoClassAttribute, NoJspForward, IframeMissingSrcAttribute, NoHtmlComments, DuplicateJspImports, JspEncoding, NoInlineScript, NoUnsanitizedJSPExpression
---
## DuplicateJspImports

**Since:** PMD 3.7

**Priority:** Medium (3)

Avoid duplicate import statements inside JSP's.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.jsp.rule.basic.DuplicateJspImportsRule](https://github.com/pmd/pmd/blob/master/pmd-jsp/src/main/java/net/sourceforge/pmd/lang/jsp/rule/basic/DuplicateJspImportsRule.java)

**Example(s):**

``` jsp
<%@ page import=\"com.foo.MyClass,com.foo.MyClass\"%><html><body><b><img src=\"<%=Some.get()%>/foo\">xx</img>text</b></body></html>
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/DuplicateJspImports" />
```

## IframeMissingSrcAttribute

**Since:** PMD 3.6

**Priority:** Medium High (2)

IFrames which are missing a src element can cause security information popups in IE if you are accessing the page
through SSL. See http://support.microsoft.com/default.aspx?scid=kb;EN-US;Q261188

```
//Element[upper-case(@Name)="IFRAME"][count(Attribute[upper-case(@Name)="SRC" ]) = 0]
```

**Example(s):**

``` jsp
<HTML><title>bad example><BODY>
<iframe></iframe>
</BODY> </HTML>

<HTML><title>good example><BODY>
<iframe src="foo"></iframe>
</BODY> </HTML>
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/IframeMissingSrcAttribute" />
```

## JspEncoding

**Since:** PMD 4.0

**Priority:** Medium (3)

A missing 'meta' tag or page directive will trigger this rule, as well as a non-UTF-8 charset.

```
//CompilationUnit/Content[
not(Element[@Name="meta"][
   Attribute[@Name="content"]/AttributeValue[contains(lower-case(@Image),"charset=utf-8")]
]) 
and 
    not(JspDirective[@Name='page']/JspDirectiveAttribute[@Name='contentType'][contains(lower-case(@Value),"charset=utf-8")])
]
```

**Example(s):**

``` jsp
Most browsers should be able to interpret the following headers:

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<meta http-equiv="Content-Type"  content="text/html; charset=UTF-8" />
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/JspEncoding" />
```

## NoClassAttribute

**Since:** PMD 3.6

**Priority:** Medium High (2)

Do not use an attribute called 'class'. Use "styleclass" for CSS styles.

```
//Attribute[ upper-case(@Name)="CLASS" ]
```

**Example(s):**

``` jsp
<HTML> <BODY>
<P class="MajorHeading">Some text</P>
</BODY> </HTML>
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/NoClassAttribute" />
```

## NoHtmlComments

**Since:** PMD 3.6

**Priority:** Medium High (2)

In a production system, HTML comments increase the payload
between the application server to the client, and serve
little other purpose. Consider switching to JSP comments.

```
//CommentTag
```

**Example(s):**

``` jsp
<HTML><title>bad example><BODY>
<!-- HTML comment -->
</BODY> </HTML>

<HTML><title>good example><BODY>
<%-- JSP comment --%>
</BODY> </HTML>
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/NoHtmlComments" />
```

## NoInlineScript

**Since:** PMD 4.0

**Priority:** Medium (3)

Avoid inlining HTML script content.  Consider externalizing the HTML script using the 'src' attribute on the "script" element.
Externalized script could be reused between pages.  Browsers can also cache the script, reducing overall download bandwidth.

```
//HtmlScript[@Image != '']
```

**Example(s):**

``` jsp
Most browsers should be able to interpret the following headers:

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<meta http-equiv="Content-Type"  content="text/html; charset=UTF-8" />
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/NoInlineScript" />
```

## NoInlineStyleInformation

**Since:** PMD 3.6

**Priority:** Medium (3)

Style information should be put in CSS files, not in JSPs. Therefore, don't use <B> or <FONT>
tags, or attributes like "align='center'".

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.jsp.rule.basic.NoInlineStyleInformationRule](https://github.com/pmd/pmd/blob/master/pmd-jsp/src/main/java/net/sourceforge/pmd/lang/jsp/rule/basic/NoInlineStyleInformationRule.java)

**Example(s):**

``` jsp
<html><body><p align='center'><b>text</b></p></body></html>
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/NoInlineStyleInformation" />
```

## NoJspForward

**Since:** PMD 3.6

**Priority:** Medium (3)

Do not do a forward from within a JSP file.

```
//Element[ @Name="jsp:forward" ]
```

**Example(s):**

``` jsp
<jsp:forward page='UnderConstruction.jsp'/>
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/NoJspForward" />
```

## NoLongScripts

**Since:** PMD 3.6

**Priority:** Medium High (2)

Scripts should be part of Tag Libraries, rather than part of JSP pages.

```
//HtmlScript[(@EndLine - @BeginLine > 10)]
```

**Example(s):**

``` jsp
<HTML>
<BODY>
<!--Java Script-->
<SCRIPT language="JavaScript" type="text/javascript">
<!--
function calcDays(){
  var date1 = document.getElementById('d1').lastChild.data;
  var date2 = document.getElementById('d2').lastChild.data;
  date1 = date1.split("-");
  date2 = date2.split("-");
  var sDate = new Date(date1[0]+"/"+date1[1]+"/"+date1[2]);
  var eDate = new Date(date2[0]+"/"+date2[1]+"/"+date2[2]);
  var daysApart = Math.abs(Math.round((sDate-eDate)/86400000));
  document.getElementById('diffDays').lastChild.data = daysApart;
}

onload=calcDays;
//-->
</SCRIPT>
</BODY>
</HTML>
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/NoLongScripts" />
```

## NoScriptlets

**Since:** PMD 3.6

**Priority:** Medium (3)

Scriptlets should be factored into Tag Libraries or JSP	declarations, rather than being part of JSP pages.

```
//JspScriptlet
|
//Element[ upper-case(@Name)="JSP:SCRIPTLET" ]
```

**Example(s):**

``` jsp
<HTML>
<HEAD>
<%
response.setHeader("Pragma", "No-cache");
%>
</HEAD>
    <BODY>
        <jsp:scriptlet>String title = "Hello world!";</jsp:scriptlet>
    </BODY>
</HTML>
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/NoScriptlets" />
```

## NoUnsanitizedJSPExpression

**Since:** PMD 5.1.4

**Priority:** Medium (3)

Avoid using expressions without escaping / sanitizing. This could lead to cross site scripting - as the expression
would be interpreted by the browser directly (e.g. "<script>alert('hello');</script>").

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.jsp.rule.basic.NoUnsanitizedJSPExpressionRule](https://github.com/pmd/pmd/blob/master/pmd-jsp/src/main/java/net/sourceforge/pmd/lang/jsp/rule/basic/NoUnsanitizedJSPExpressionRule.java)

**Example(s):**

``` jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
${expression}                    <!-- don't use this -->
${fn:escapeXml(expression)}      <!-- instead, escape it -->
<c:out value="${expression}" />  <!-- or use c:out -->
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/jsp/basic.xml/NoUnsanitizedJSPExpression" />
```

