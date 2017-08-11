---
title: Style
summary: The Style Ruleset contains rules regarding preferred usage of names and identifiers.
permalink: pmd_rules_apex_style.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/rulesets/apex/style.xml
---
## AvoidGlobalModifier

**Since:** 5.5.0

**Priority:** Medium (3)

Global classes should be avoided (especially in managed packages) as they can never be deleted or changed in signature. Always check twice if something needs to be global.
Many interfaces (e.g. Batch) required global modifiers in the past but don't require this anymore. Don't lock yourself in.

**Example(s):**

```
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

## AvoidLogicInTrigger

**Since:** 5.5.0

**Priority:** Medium (3)

As triggers do not allow methods like regular classes they are less flexible and suited to apply good encapsulation style.
Therefore delegate the triggers work to a regular class (often called Trigger handler class).

See more here: https://developer.salesforce.com/page/Trigger_Frameworks_and_Apex_Trigger_Best_Practices

**Example(s):**

```
trigger Accounts on Account (before insert, before update, before delete, after insert, after update, after delete, after undelete) {
	for(Account acc : Trigger.new) {           
		if(Trigger.isInsert) {
			...
		}
		
		...
		
		if(Trigger.isDelete) {
			...
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

## ClassNamingConventions

**Since:** 5.5.0

**Priority:** High (1)

Class names should always begin with an upper case character.

**Example(s):**

```
public class Foo {}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

## MethodNamingConventions

**Since:** 5.5.0

**Priority:** High (1)

Method names should always begin with a lower case character, and should not contain underscores.

**Example(s):**

```
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

## MethodWithSameNameAsEnclosingClass

**Since:** 5.5.0

**Priority:** Medium (3)

Non-constructor methods should not have the same name as the enclosing class.

**Example(s):**

```
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

## VariableNamingConventions

**Since:** 5.5.0

**Priority:** High (1)

A variable naming conventions rule - customize this to your liking.  Currently, it
checks for final variables that should be fully capitalized and non-final variables
that should not include underscores.

**Example(s):**

```
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

