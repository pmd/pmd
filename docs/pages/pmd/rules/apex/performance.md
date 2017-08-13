---
title: Performance
summary: The Performance ruleset contains a collection of good practices which should be followed.
permalink: pmd_rules_apex_performance.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/rulesets/apex/performance.xml
---
## AvoidDmlStatementsInLoops

**Since:** PMD 5.5.0

**Priority:** Medium (3)

Avoid DML statements inside loops to avoid hitting the DML governor limit. Instead, try to batch up the data into a list and invoke your DML once on that list of data outside the loop.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.performance.AvoidDmlStatementsInLoopsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/performance/AvoidDmlStatementsInLoopsRule.java)

**Example(s):**

```
public class Something {
	public void foo() {  
		for (Integer i = 0; i < 151; i++) {
		    Account account;
		    ...
		    insert account;
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

## AvoidSoqlInLoops

**Since:** PMD 5.5.0

**Priority:** Medium (3)

New objects created within loops should be checked to see if they can created outside them and reused.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.apex.rule.performance.AvoidSoqlInLoopsRule](https://github.com/pmd/pmd/blob/master/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/performance/AvoidSoqlInLoopsRule.java)

**Example(s):**

```
public class Something {
	public static void main( String as[] ) {  
		for (Integer i = 0; i < 10; i++) {
			List<Account> accounts = [SELECT Id FROM Account];
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

