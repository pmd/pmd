---
title: Complexity
summary: The Complexity ruleset contains rules that find problems related to code size or complexity.
permalink: pmd_rules_apex_complexity.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/rulesets/apex/complexity.xml
keywords: Complexity, AvoidDeeplyNestedIfStmts, ExcessiveParameterList, ExcessiveClassLength, NcssMethodCount, NcssTypeCount, NcssConstructorCount, StdCyclomaticComplexity, TooManyFields, ExcessivePublicCount
---
## AvoidDeeplyNestedIfStmts

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Avoid creating deeply nested if-then statements since they are harder to read and error-prone to maintain.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.complexity.AvoidDeeplyNestedIfStmtsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/AvoidDeeplyNestedIfStmtsRule.java)

**Example(s):**

``` java
public class Foo {
    public void bar(Integer x, Integer y, Integer z) {
        if (x>y) {
            if (y>z) {
                if (z==x) {
                    // !! too deep
                }
            }
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|problemDepth|3|The if statement depth reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/complexity.xml/AvoidDeeplyNestedIfStmts" />
```

## ExcessiveClassLength

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Excessive class file lengths are usually indications that the class may be burdened with excessive 
responsibilities that could be provided by external classes or functions. In breaking these methods
apart the code becomes more managable and ripe for reuse.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.complexity.ExcessiveClassLengthRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/ExcessiveClassLengthRule.java)

**Example(s):**

``` java
public class Foo {
    public void bar1() {
        // 1000 lines of code
    }
    public void bar2() {
        // 1000 lines of code
    }
    public void bar3() {
        // 1000 lines of code
    }
    public void barN() {
        // 1000 lines of code
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/complexity.xml/ExcessiveClassLength" />
```

## ExcessiveParameterList

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Methods with numerous parameters are a challenge to maintain, especially if most of them share the
same datatype. These situations usually denote the need for new objects to wrap the numerous parameters.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.complexity.ExcessiveParameterListRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/ExcessiveParameterListRule.java)

**Example(s):**

``` java
// too many arguments liable to be mixed up
public void addPerson(int birthYear, int birthMonth, int birthDate, int height, int weight, int ssn) {
    // ...
}
// preferred approach 
public void addPerson(Date birthdate, BodyMeasurements measurements, int ssn) {
    // ...
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/complexity.xml/ExcessiveParameterList" />
```

## ExcessivePublicCount

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Classes with large numbers of public methods and attributes require disproportionate testing efforts
since combinational side effects grow rapidly and increase risk. Refactoring these classes into
smaller ones not only increases testability and reliability but also allows new variations to be
developed easily.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.complexity.ExcessivePublicCountRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/ExcessivePublicCountRule.java)

**Example(s):**

``` java
public class Foo {
    public String value;
    public Bar something;
    public Variable var;
    // [... more more public attributes ...]

    public void doWork() {}
    public void doMoreWork() {}
    public void doWorkAgain() {}
    // [... more more public methods ...]
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/complexity.xml/ExcessivePublicCount" />
```

## NcssConstructorCount

**Since:** PMD 5.5.0

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines
of code for a given constructor. NCSS ignores comments, and counts actual statements. Using this algorithm,
lines of code that are split are counted as one.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.complexity.NcssConstructorCountRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/NcssConstructorCountRule.java)

**Example(s):**

``` java
public class Foo extends Bar {
    //this constructor only has 1 NCSS lines
    public Foo() {
        super();




        super.foo();
}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/complexity.xml/NcssConstructorCount" />
```

## NcssMethodCount

**Since:** PMD 5.5.0

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines
of code for a given method. NCSS ignores comments, and counts actual statements. Using this algorithm,
lines of code that are split are counted as one.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.complexity.NcssMethodCountRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/NcssMethodCountRule.java)

**Example(s):**

``` java
public class Foo extends Bar {
    //this method only has 1 NCSS lines
    public Integer methd() {
        super.methd();



        return 1;
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/complexity.xml/NcssMethodCount" />
```

## NcssTypeCount

**Since:** PMD 5.5.0

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines
of code for a given type. NCSS ignores comments, and counts actual statements. Using this algorithm,
lines of code that are split are counted as one.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.complexity.NcssTypeCountRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/NcssTypeCountRule.java)

**Example(s):**

``` java
//this class only has 6 NCSS lines
public class Foo extends Bar {
    public Foo() {
        super();





        super.foo();
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/complexity.xml/NcssTypeCount" />
```

## StdCyclomaticComplexity

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Complexity directly affects maintenance costs is determined by the number of decision points in a method 
plus one for the method entry.  The decision points include 'if', 'while', 'for', and 'case labels' calls.  
Generally, numbers ranging from 1-4 denote low complexity, 5-7 denote moderate complexity, 8-10 denote
high complexity, and 11+ is very high complexity.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.complexity.StdCyclomaticComplexityRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/StdCyclomaticComplexityRule.java)

**Example(s):**

``` java
// This has a Cyclomatic Complexity = 12
public class Foo {
1   public void example() {
2   if (a == b || (c == d && e == f)) {
3       if (a1 == b1) {
            fiddle();
4       } else if a2 == b2) {
            fiddle();
        }  else {
            fiddle();
        }
5   } else if (c == d) {
6       while (c == d) {
            fiddle();
        }
7   } else if (e == f) {
8       for (int n = 0; n < h; n++) {
            fiddle();
        }
    } else {
        switch (z) {
9           case 1:
                fiddle();
                break;
10          case 2:
                fiddle();
                break;
11          case 3:
                fiddle();
                break;
12          default:
                fiddle();
                break;
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|showMethodsComplexity|true|Add method average violations to the report|
|showClassesComplexity|true|Add class average violations to the report|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|reportLevel|10|Cyclomatic Complexity reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/complexity.xml/StdCyclomaticComplexity" />
```

## TooManyFields

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Classes that have too many fields can become unwieldy and could be redesigned to have fewer fields,
possibly through grouping related fields in new objects.  For example, a class with individual 
city/state/zip fields could park them within a single Address field.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.complexity.TooManyFieldsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/TooManyFieldsRule.java)

**Example(s):**

``` java
public class Person {
    // too many separate fields
    int birthYear;
    int birthMonth;
    int birthDate;
    float height;
    float weight;
}

public class Person {
    // this is more manageable
    Date birthDate;
    BodyMeasurements measurements;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|
|maxfields|15|Max allowable fields|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/complexity.xml/TooManyFields" />
```

