---
title: Migration Guide for PMD 7
tags: [pmd, userdocs]
summary: "Migrating to PMD 7 from PMD 6.x"
permalink: pmd_userdocs_migrating_to_pmd7.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
---

## In general

Update to the latest PMD 6.x version and try to fix deprecation warnings

Deprecations in PMD 6:
* Properties:
  StringProperty.named(...) -> PropertyFactory.stringProperty(...)
  uiOrder is gone
* addViolation(data, node, ...) -> asCtx(data).addViolation(node, ...)
* deprecated cli params
  -no-cache --> --no-cache
  -failOnViolation --> --fail-on-violation
  -reportfile --> --report-file
  -language --> --use-version
* deprecated XPath attributes
WARNING: Use of deprecated attribute 'VariableDeclaratorId/@Image' by XPath rule 'VariableNaming' (in ruleset 'VariableNamingRule'), please use @Name instead



## Use cases

### I'm using only built-in rules

check whether the ruleset/rules are still available,
have the same properties, etc.

### I'm using custom rules

make sure to have good test coverage for your custom rules first.
if XPath, need to migrate to XPath 2.0. if Java, some APIs/ASTs might have changed.

### I've extended PMD with a custom language...

### I've extended PMD with a custom feature...

## Special topics

### CLI Changes

run.sh pmd -> pmd check

Message: [main] ERROR net.sourceforge.pmd.cli.commands.internal.PmdCommand - No such file false
--> comes from "--fail-on-violation false" -> "--no-fail-on-violation"

### Custom distribution packages
needs pmd-cli dependencies
needs cyclonedx plugin
additional config needed to include conf/simpelogger.properties

### Migrate rulechain rules:

* RuleChain API changes, see [core] Simplify the rulechain #2490
* addRuleChainVisit() -> buildTargetSelector()

### Rule tests

Nice to have - not immediately required:
Should replace junit4 with junit5
But both would work.

### Endcolumn

CPD: End Columns of Tokens are exclusive on PMD 7,
but inclusive on PMD 6.x. See 5b7ed58


### XPath

Differences between XPath 1.0 and 2.0 (focused on practicality, eg how to transition)

This is already in place: [Writing XPath rules - Migrating from 1.0 to 2.0](pmd_userdocs_extending_writing_xpath_rules.html#migrating-from-10-to-20)
That means: The section migrating from XPath 1.0 -> XPath 2.0 should be moved to the migration guide PMD6->7
since only XPath 2.0 (actually XPath 3.1) will be supported with PMD 7.

Custom function "pmd:matches" has been removed, use the built in function from XPath 2.0+.

### AST Navigation in general

Methods like Node::getFirstChildOfType... use replacement either, NodeStream or in that case Node::firstChild(...).

### Java AST
* See also [Java Clean Changes](https://github.com/pmd/pmd/wiki/Java_clean_changes)

### Language versions

For some languages, that previously hadn't any version, now there are versions, e.g. plsql.

### Build Tools

maven...
