---
title: Style
summary: The Style Ruleset contains rules regarding preferred usage of names and identifiers.
permalink: pmd_rules_apex_style.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/rulesets/apex/style.xml
keywords: Style, VariableNamingConventions, MethodNamingConventions, ClassNamingConventions, MethodWithSameNameAsEnclosingClass, AvoidLogicInTrigger, AvoidGlobalModifier, AvoidDirectAccessTriggerMap, AvoidHardcodingId
---
## AvoidDirectAccessTriggerMap

**Since:** PMD 6.0.0

**Priority:** Medium (3)

Avoid directly accessing Trigger.old and Trigger.new as it can lead to a bug. Triggers should be bulkified and iterate through the map to handle the actions for each item separately.

```
//ArrayLoadExpression/TriggerVariableExpression | //ArrayLoadExpression/LiteralExpression
```

**Example(s):**

``` java
trigger AccountTrigger on Account (before insert, before update) {
   Account a = Trigger.new[0]; //Bad: Accessing the trigger array directly is not recommended.
   
   foreach ( Account a : Trigger.new ){   
        //Good: Iterate through the trigger.new array instead.
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
<rule ref="rulesets/apex/style.xml/AvoidDirectAccessTriggerMap" />
```

## AvoidGlobalModifier

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Global classes should be avoided (especially in managed packages) as they can never be deleted or changed in signature. Always check twice if something needs to be global.
Many interfaces (e.g. Batch) required global modifiers in the past but don't require this anymore. Don't lock yourself in.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.style.AvoidGlobalModifierRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/style/AvoidGlobalModifierRule.java)

**Example(s):**

``` java
global class Unchangeable {
    global UndeletableType unchangable(UndeletableType param) {
        // ...
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
<rule ref="rulesets/apex/style.xml/AvoidGlobalModifier" />
```

## AvoidHardcodingId

**Since:** PMD 6.0.0

**Priority:** Medium (3)

When deploying Apex code between sandbox and production environments, or installing Force.com AppExchange packages,
it is essential to avoid hardcoding IDs in the Apex code. By doing so, if the record IDs change between environments,
the logic can dynamically identify the proper data to operate against and not fail.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.style.AvoidHardcodingIdRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/style/AvoidHardcodingIdRule.java)

**Example(s):**

``` java
public without sharing class Foo {
    void foo() {
        //Error - hardcoded the record type id
        if(a.RecordTypeId == '012500000009WAr'){     	  	
            //do some logic here.....
        } else if(a.RecordTypeId == '0123000000095Km'){
            //do some logic here for a different record type...
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/style.xml/AvoidHardcodingId" />
```

## AvoidLogicInTrigger

**Since:** PMD 5.5.0

**Priority:** Medium (3)

As triggers do not allow methods like regular classes they are less flexible and suited to apply good encapsulation style.
Therefore delegate the triggers work to a regular class (often called Trigger handler class).

See more here: https://developer.salesforce.com/page/Trigger_Frameworks_and_Apex_Trigger_Best_Practices

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.style.AvoidLogicInTriggerRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/style/AvoidLogicInTriggerRule.java)

**Example(s):**

``` java
trigger Accounts on Account (before insert, before update, before delete, after insert, after update, after delete, after undelete) {
    for(Account acc : Trigger.new) {
        if(Trigger.isInsert) {
            // ...
        }

        // ...

        if(Trigger.isDelete) {
            // ...
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/style.xml/AvoidLogicInTrigger" />
```

## ClassNamingConventions

**Since:** PMD 5.5.0

**Priority:** High (1)

Class names should always begin with an upper case character.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.style.ClassNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/style/ClassNamingConventionsRule.java)

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
<rule ref="rulesets/apex/style.xml/ClassNamingConventions" />
```

## MethodNamingConventions

**Since:** PMD 5.5.0

**Priority:** High (1)

Method names should always begin with a lower case character, and should not contain underscores.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.style.MethodNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/style/MethodNamingConventionsRule.java)

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
<rule ref="rulesets/apex/style.xml/MethodNamingConventions" />
```

## MethodWithSameNameAsEnclosingClass

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Non-constructor methods should not have the same name as the enclosing class.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.style.MethodWithSameNameAsEnclosingClassRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/style/MethodWithSameNameAsEnclosingClassRule.java)

**Example(s):**

``` java
public class MyClass {
    // this is OK because it is a constructor
    public MyClass() {}
    // this is bad because it is a method
    public void MyClass() {}
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
<rule ref="rulesets/apex/style.xml/MethodWithSameNameAsEnclosingClass" />
```

## VariableNamingConventions

**Since:** PMD 5.5.0

**Priority:** High (1)

A variable naming conventions rule - customize this to your liking.  Currently, it
checks for final variables that should be fully capitalized and non-final variables
that should not include underscores.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.style.VariableNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/style/VariableNamingConventionsRule.java)

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
<rule ref="rulesets/apex/style.xml/VariableNamingConventions" />
```

