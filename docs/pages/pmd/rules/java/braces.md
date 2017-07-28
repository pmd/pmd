---
title: Braces
summary: The Braces ruleset contains rules regarding the use and placement of braces.
permalink: pmd_rules_java_braces.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/braces.xml
---
## IfStmtsMustUseBraces
**Since:** 1.0

**Priority:** Medium (3)

Avoid using if statements without using braces to surround the code block. If the code 
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

**Example(s):**
```
if (foo)	// not recommended
	x++;

if (foo) {	// preferred approach
	x++;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## WhileLoopsMustUseBraces
**Since:** 0.7

**Priority:** Medium (3)

Avoid using 'while' statements without using braces to surround the code block. If the code 
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

**Example(s):**
```
while (true)	// not recommended
      x++;
      
while (true) {	// preferred approach
      x++;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## IfElseStmtsMustUseBraces
**Since:** 0.2

**Priority:** Medium (3)

Avoid using if..else statements without using surrounding braces. If the code formatting 
or indentation is lost then it becomes difficult to separate the code being controlled 
from the rest.

**Example(s):**
```
// this is OK
if (foo) x++;
   
   // but this is not
if (foo)
       x = x+1;
   else
       x = x-1;
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## ForLoopsMustUseBraces
**Since:** 0.7

**Priority:** Medium (3)

Avoid using 'for' statements without using curly braces. If the code formatting or 
indentation is lost then it becomes difficult to separate the code being controlled 
from the rest.

**Example(s):**
```
for (int i = 0; i < 42; i++)
   foo();
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

