---
title: Best Practices
tags: [userdocs]
permalink: pmd_userdocs_best_practices.html
author: Tom Copeland <tom@infoether.com>
last_updated: November 2017
---

## Choose the rules that are right for you

Running every existing rule will result in a huge number of rule violations, most of which will be unimportant.
Having to sort through a thousand line report to find the few you're really interested in takes
all the fun out of things.

Instead, start with some selected specific rules, e.g. the rules that detect unused code from
the category [Best Practices](pmd_rules_java_bestpractices.html) and fix any unused locals and fields.

Then, run rules, that detect empty `if` statements and such-like. You can find these rules in the category
[Error Prone](pmd_rules_java_errorprone.html).

After that, look at all the categories and select the rules, that are useful for your project.
You can find an overview of the rules on the [Rule Index](pmd_rules_java.html).

Use the rules you like [via a custom ruleset](pmd_userdocs_making_rulesets.html).

## PMD rules are not set in stone

Generally, pick the ones you like, and ignore or [suppress](pmd_userdocs_suppressing_warnings.html)
the warnings you don't like. It's just a tool.

## PMD IDE plugins are nice

Using PMD within your IDE is much more enjoyable than flipping back and forth
between an HTML report and your IDE. Most IDE plugins have the "click on the rule
violation and jump to that line of code" feature. Find the PMD plugin for your IDE, install it,
and soon you'll be fixing problems much faster.

Suggestions?  Comments?  Post them [here](https://github.com/pmd/pmd/issues). Thanks!
