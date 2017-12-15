---
title: Code Style
summary: Rules which enforce a specific coding style.
permalink: pmd_rules_apex_codestyle.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/category/apex/codestyle.xml
keywords: Code Style, ClassNamingConventions, IfElseStmtsMustUseBraces, IfStmtsMustUseBraces, ForLoopsMustUseBraces, MethodNamingConventions, VariableNamingConventions, WhileLoopsMustUseBraces
---
## ClassNamingConventions

**Since:** PMD 5.5.0

**Priority:** High (1)

Class names should always begin with an upper case character.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.codestyle.ClassNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/codestyle/ClassNamingConventionsRule.java)

**Example(s):**

``` java
public class Foo {}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/codestyle.xml/ClassNamingConventions" />
```

## ForLoopsMustUseBraces

**Since:** PMD 5.6.0

**Priority:** Medium (3)

Avoid using 'for' statements without using surrounding braces. If the code formatting or
indentation is lost then it becomes difficult to separate the code being controlled
from the rest.

```
//ForLoopStatement/BlockStatement[@CurlyBrace='false']
|
//ForEachStatement/BlockStatement[@CurlyBrace='false']
```

**Example(s):**

``` java
for (int i = 0; i < 42; i++) // not recommended
    foo();

for (int i = 0; i < 42; i++) { // preferred approach
    foo();
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/codestyle.xml/ForLoopsMustUseBraces" />
```

## IfElseStmtsMustUseBraces

**Since:** PMD 5.6.0

**Priority:** Medium (3)

Avoid using if..else statements without using surrounding braces. If the code formatting
or indentation is lost then it becomes difficult to separate the code being controlled
from the rest.

```
//IfBlockStatement/BlockStatement[@CurlyBrace='false'][count(child::*) > 0]
|
//IfElseBlockStatement/BlockStatement[@CurlyBrace='false'][count(child::*) > 0]
```

**Example(s):**

``` java
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
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/codestyle.xml/IfElseStmtsMustUseBraces" />
```

## IfStmtsMustUseBraces

**Since:** PMD 5.6.0

**Priority:** Medium (3)

Avoid using if statements without using braces to surround the code block. If the code
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

```
//IfBlockStatement/BlockStatement[@CurlyBrace='false']
```

**Example(s):**

``` java
if (foo)    // not recommended
    x++;

if (foo) {  // preferred approach
    x++;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/codestyle.xml/IfStmtsMustUseBraces" />
```

## MethodNamingConventions

**Since:** PMD 5.5.0

**Priority:** High (1)

Method names should always begin with a lower case character, and should not contain underscores.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.codestyle.MethodNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/codestyle/MethodNamingConventionsRule.java)

**Example(s):**

``` java
public class Foo {
    public void fooStuff() {
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/codestyle.xml/MethodNamingConventions" />
```

## VariableNamingConventions

**Since:** PMD 5.5.0

**Priority:** High (1)

A variable naming conventions rule - customize this to your liking.  Currently, it
checks for final variables that should be fully capitalized and non-final variables
that should not include underscores.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.codestyle.VariableNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/codestyle/VariableNamingConventionsRule.java)

**Example(s):**

``` java
public class Foo {
    public static final Integer MY_NUM = 0;
    public String myTest = '';
    DataModule dmTest = new DataModule();
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|parameterSuffix|[]|Method parameter variable suffixes|
|parameterPrefix|[]|Method parameter variable prefixes|
|localSuffix|[]|Local variable suffixes|
|localPrefix|[]|Local variable prefixes|
|memberSuffix|[]|Member variable suffixes|
|memberPrefix|[]|Member variable prefixes|
|staticSuffix|[]|Static variable suffixes|
|staticPrefix|[]|Static variable prefixes|
|checkParameters|true|Check constructor and method parameter variables|
|checkLocals|true|Check local variables|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|checkMembers|true|Check member variables|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/codestyle.xml/VariableNamingConventions" />
```

## WhileLoopsMustUseBraces

**Since:** PMD 5.6.0

**Priority:** Medium (3)

Avoid using 'while' statements without using braces to surround the code block. If the code
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

```
//WhileLoopStatement/BlockStatement[@CurlyBrace='false']
```

**Example(s):**

``` java
while (true)    // not recommended
    x++;

while (true) {  // preferred approach
    x++;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/codestyle.xml/WhileLoopsMustUseBraces" />
```

