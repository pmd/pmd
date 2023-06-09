---
title: Migration Guide for PMD 7
tags: [pmd, userdocs]
summary: "Migrating to PMD 7 from PMD 6.x"
permalink: pmd_userdocs_migrating_to_pmd7.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
---

## Before you update

Before updating to PMD 7, you should first update to the latest PMD 6 version 6.55.0 and try to fix all
deprecation warnings.

There are a couple of deprecated things in PMD 6, you might encounter:

* Properties: In order to define property descriptors, you should use {% jdoc core::properties.PropertyFactory %} now.
  This factory can create properties of any type. E.g. instead of `StringProperty.named(...)` use
  `PropertyFactory.stringProperty(...)`.

  Also note, that `uiOrder` is gone. You can just remove it.

  See also [Defining rule properties](pmd_userdocs_extending_defining_properties.html)

* When reporting a violation, you might see a deprecation of the `addViolation` methods. These methods have been moved
  to {% jdoc core::RuleContext %}. E.g. instead of `addViolation(data, node, ...)` use `asCtx(data).addViolation(node, ...)`.

* When you are calling PMD from CLI, you need to stop using deprecated CLI params, e.g.
  * `-no-cache` --> `--no-cache`
  * `-failOnViolation` --> `--fail-on-violation`
  * `-reportfile` --> `--report-file`
  * `-language` --> `--use-version`

* If you have written custom XPath rule, look out for warning about deprecated XPath attributes. These warnings
  might look like
  ```
  WARNING: Use of deprecated attribute 'VariableDeclaratorId/@Image' by XPath rule 'VariableNaming' (in ruleset 'VariableNamingRule'), please use @Name instead
  ```
  and often suggest already an alternative.

## Use cases

### I'm using only built-in rules

When you are using only built-in rules, then you should check, whether you use any deprecated rule. With PMD 7
many deprecated rules are finally removed. You can see a complete list of the [removed rules](pmd_release_notes_pmd7.html#removed-rules)
in the release notes for PMD 7.
The release notes also mention the replacement rule, that should be used instead. For some rules, there is no
replacement.

Then many rules have been changed or improved. New properties have been added to make the further configurable or
properties have been removed, if they are not necessary anymore. See [changed rules](pmd_release_notes_pmd7.html#changed-rules)
in the release notes for PMD 7.

A handful rules are new with PMD 7. You might want to check these out: [new rules](pmd_release_notes_pmd7.html#new-rules).

Once you have reviewed your ruleset(s), you can switch to PMD 7.

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
