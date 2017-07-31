---
title: ApexUnit
summary: These rules deal with different problems that can occur with Apex unit tests.
permalink: pmd_rules_apex_apexunit.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/rulesets/apex/apexunit.xml
---
## ApexUnitTestClassShouldHaveAsserts
**Since:** 5.5.1

**Priority:** Medium (3)

Apex unit tests should include at least one assertion.  This makes the tests more robust, and using assert
            with messages provide the developer a clearer idea of what the test does.

**Example(s):**
```
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

## ApexUnitTestShouldNotUseSeeAllDataTrue
**Since:** 5.5.1

**Priority:** Medium (3)

Apex unit tests should not use @isTest(seeAllData=true) because it opens up the existing database data for unexpected modification by tests.

**Example(s):**
```
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

