---
title: Basic JSP
summary: Rules concerning basic JSP guidelines.
permalink: pmd_rules_jsp_basic.html
folder: pmd/rules/jsp
sidebaractiveurl: /pmd_rules_jsp.html
editmepath: ../pmd-jsp/src/main/resources/rulesets/jsp/basic.xml
---
## NoLongScripts
**Since:** 3.6

**Priority:** Medium High (2)

Scripts should be part of Tag Libraries, rather than part of JSP pages.

**Example(s):**
```
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

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## NoScriptlets
**Since:** 3.6

**Priority:** Medium (3)

Scriptlets should be factored into Tag Libraries or JSP	declarations, rather than being part of JSP pages.

**Example(s):**
```
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

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## NoInlineStyleInformation
**Since:** 3.6

**Priority:** Medium (3)

Style information should be put in CSS files, not in JSPs. Therefore, don't use &lt;B> or &lt;FONT> tags, or attributes like "align='center'".

**Example(s):**
```
<html><body><p align='center'><b>text</b></p></body></html>
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|

## NoClassAttribute
**Since:** 3.6

**Priority:** Medium High (2)

Do not use an attribute called 'class'. Use "styleclass" for CSS styles.

**Example(s):**
```
<HTML> <BODY>
<P class="MajorHeading">Some text</P>
</BODY> </HTML>
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## NoJspForward
**Since:** 3.6

**Priority:** Medium (3)

Do not do a forward from within a JSP file.

**Example(s):**
```
<jsp:forward page='UnderConstruction.jsp'/>
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## IframeMissingSrcAttribute
**Since:** 3.6

**Priority:** Medium High (2)

IFrames which are missing a src element can cause security information popups in IE if you are accessing the page
through SSL. See http://support.microsoft.com/default.aspx?scid=kb;EN-US;Q261188

**Example(s):**
```
<HTML><title>bad example><BODY>
<iframe></iframe>
</BODY> </HTML>

<HTML><title>good example><BODY>
<iframe src="foo"></iframe>
</BODY> </HTML>
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## NoHtmlComments
**Since:** 3.6

**Priority:** Medium High (2)

In a production system, HTML comments increase the payload
			between the application server to the client, and serve
			little other purpose. Consider switching to JSP comments.

**Example(s):**
```
<HTML><title>bad example><BODY>
<!-- HTML comment -->
</BODY> </HTML>

<HTML><title>good example><BODY>
<%-- JSP comment --%>
</BODY> </HTML>
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## DuplicateJspImports
**Since:** 3.7

**Priority:** Medium (3)

Avoid duplicate import statements inside JSP's.

**Example(s):**
```
<%@ page import=\"com.foo.MyClass,com.foo.MyClass\"%><html><body><b><img src=\"<%=Some.get()%>/foo\">xx</img>text</b></body></html>
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|

## JspEncoding
**Since:** 4.0

**Priority:** Medium (3)

A missing 'meta' tag or page directive will trigger this rule, as well as a non-UTF-8 charset.

**Example(s):**
```
Most browsers should be able to interpret the following headers:
                
                <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
                    
                <meta http-equiv="Content-Type"  content="text/html; charset=UTF-8" />
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## NoInlineScript
**Since:** 4.0

**Priority:** Medium (3)

Avoid inlining HTML script content.  Consider externalizing the HTML script using the 'src' attribute on the "script" element.
Externalized script could be reused between pages.  Browsers can also cache the script, reducing overall download bandwidth.

**Example(s):**
```
Most browsers should be able to interpret the following headers:
                
                <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
                    
                <meta http-equiv="Content-Type"  content="text/html; charset=UTF-8" />
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## NoUnsanitizedJSPExpression
**Since:** 5.1.4

**Priority:** Medium (3)

Avoid using expressions without escaping / sanitizing. This could lead to cross site scripting - as the expression
would be interpreted by the browser directly (e.g. "<script>alert('hello');</script>").

**Example(s):**
```
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
${expression}                    <!-- don't use this -->
${fn:escapeXml(expression)}      <!-- instead, escape it -->
<c:out value="${expression}" />  <!-- or use c:out -->
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|

