---
title: Rule guidelines
tags: [extending, userdocs]
summary: "Rule Guidelines, or the last touches to a rule"
last_updated: December 2023 (7.0.0)
permalink: pmd_userdocs_extending_rule_guidelines.html
author: Xavier Le Vourch, Ryan Gustafson, Romain Pelisse
---


Here is a bunch of things to do you may consider once your rule is “up and running”.

## How to define rules priority

Rule priority may, of course, change a lot depending on the context of the project. However, you can use the
following guidelines to assert the legitimate priority of your rule:

1.  **High: Change absolutely required.** Behavior is critically broken/buggy.
2.  **Medium High: Change highly recommended.** Behavior is quite likely to be broken/buggy.
3.  **Medium: Change recommended.** Behavior is confusing, perhaps buggy, and/or against standards/best practices.
4.  **Medium Low: Change optional.** Behavior is not likely to be buggy, but more just flies in the face of
    standards/style/good taste.
5.  **Low: Change highly optional.** Nice to have, such as a consistent naming policy for package/class/fields…

For instance, let's take the rule {% rule java/errorprone/DoNotCallGarbageCollectionExplicitly %}
(“Do not explicitly trigger a garbage collection.”). Calling GC is
a bad idea, but it doesn't break the application. So we skip priority one. However, as explicit call to gc may really
hinder application performances, we settle for priority 2 ("Medium High").

## Correctness

You should try to run the rule on a large code base, like the jdk source code for instance. This will help ensure
that the rule does not raise exceptions when dealing with unusual constructs.

If your rule is stateful, make sure that it is reinitialized correctly for each file.

## Performance issues

When writing a new rule, using command line option [`--benchmark`](pmd_userdocs_cli_reference.html#-benchmark)
on a few rules can give an indication on how the rule compares to others. To get the full picture
use the `rulesets/internal/all-java.xml` ruleset with `--benchmark`.

Rules which use the [RuleChain](pmd_userdocs_extending_writing_java_rules.html#economic-traversal-the-rulechain)
to visit the AST are faster than rules which perform manual visitation of the AST.
The difference is small for an individual Java rule, but when running 100s of rules, it is measurable.
For XPath rules, the difference is extremely noticeable due to the overhead for AST navigation.
Make sure your XPath rules using the RuleChain. If RuleChain can't be used for your XPath rule, then this fact
is logged as a debug message.
