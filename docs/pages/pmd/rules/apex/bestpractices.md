---
title: Best Practices
summary: Rules which enforce generally accepted best practices.
permalink: pmd_rules_apex_bestpractices.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/category/apex/bestpractices.xml
keywords: Best Practices, ApexUnitTestClassShouldHaveAsserts, ApexUnitTestShouldNotUseSeeAllDataTrue, AvoidGlobalModifier, AvoidHardcodingId, AvoidLogicInTrigger
---
## ApexUnitTestClassShouldHaveAsserts

**Since:** PMD 5.5.1

**Priority:** Medium (3)

Apex unit tests should include at least one assertion.  This makes the tests more robust, and using assert
with messages provide the developer a clearer idea of what the test does.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.bestpractices.ApexUnitTestClassShouldHaveAssertsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/bestpractices/ApexUnitTestClassShouldHaveAssertsRule.java)

**Example(s):**

``` java
@isTest
public class Foo {
   public static testMethod void testSomething() {
      Account a = null;
   // This is better than having a NullPointerException
   // System.assertNotEquals(a, null, 'account not found');
   a.toString();
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
<rule ref="rulesets/apex/bestpractices.xml/ApexUnitTestClassShouldHaveAsserts" />
```

## ApexUnitTestShouldNotUseSeeAllDataTrue

**Since:** PMD 5.5.1

**Priority:** Medium (3)

Apex unit tests should not use @isTest(seeAllData=true) because it opens up the existing database data for unexpected modification by tests.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.bestpractices.ApexUnitTestShouldNotUseSeeAllDataTrueRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/bestpractices/ApexUnitTestShouldNotUseSeeAllDataTrueRule.java)

**Example(s):**

``` java
@isTest(seeAllData = true)
public class Foo {
   public static testMethod void testSomething() {
      Account a = null;
   // This is better than having a NullPointerException
   // System.assertNotEquals(a, null, 'account not found');
   a.toString();
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
<rule ref="rulesets/apex/bestpractices.xml/ApexUnitTestShouldNotUseSeeAllDataTrue" />
```

## AvoidGlobalModifier

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Global classes should be avoided (especially in managed packages) as they can never be deleted or changed in signature. Always check twice if something needs to be global.
Many interfaces (e.g. Batch) required global modifiers in the past but don't require this anymore. Don't lock yourself in.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.bestpractices.AvoidGlobalModifierRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/bestpractices/AvoidGlobalModifierRule.java)

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
<rule ref="rulesets/apex/bestpractices.xml/AvoidGlobalModifier" />
```

## AvoidHardcodingId

**Since:** PMD 6.0.0

**Priority:** Medium (3)

When deploying Apex code between sandbox and production environments, or installing Force.com AppExchange packages,
it is essential to avoid hardcoding IDs in the Apex code. By doing so, if the record IDs change between environments,
the logic can dynamically identify the proper data to operate against and not fail.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.bestpractices.AvoidHardcodingIdRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/bestpractices/AvoidHardcodingIdRule.java)

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
<rule ref="rulesets/apex/bestpractices.xml/AvoidHardcodingId" />
```

## AvoidLogicInTrigger

**Since:** PMD 5.5.0

**Priority:** Medium (3)

As triggers do not allow methods like regular classes they are less flexible and suited to apply good encapsulation style.
Therefore delegate the triggers work to a regular class (often called Trigger handler class).

See more here: https://developer.salesforce.com/page/Trigger_Frameworks_and_Apex_Trigger_Best_Practices

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.bestpractices.AvoidLogicInTriggerRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/bestpractices/AvoidLogicInTriggerRule.java)

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
<rule ref="rulesets/apex/bestpractices.xml/AvoidLogicInTrigger" />
```

