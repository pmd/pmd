---
title: Rule guidelines
tags: [extending, userdocs]
summary: Rule Guidelines
last_updated: July 3, 2016
permalink: pmd_userdocs_extending_rule_guidelines.html
author: Xavier Le Vourch, Ryan Gustafson, Romain Pelisse
---

# Rule Guidelines

Or - Last touches to a rules

Here is a bunch of thing to do you may consider once your rule is “up and running”.

## How to define rules priority

Rule priority may, of course, changes a lot depending on the context of the project. However, you can use the following guidelines to assert the legitimate priority of your rule:

1.  **Change absolutely required.** Behavior is critically broken/buggy.
2.  **Change highly recommended.** Behavior is quite likely to be broken/buggy.
3.  **Change recommended.** Behavior is confusing, perhaps buggy, and/or against standards/best practices.
4.  **Change optional.** Behavior is not likely to be buggy, but more just flies in the face of standards/style/good taste.
5.  **Change highly optional.** Nice to have, such as a consistent naming policy for package/class/fields…

For instance, let’s take the ExplicitCallToGC rule (“Do not explicitly trigger a garbage collection.”). Calling GC is a bad idea, but it doesn’t break the application. So we skip priority one. However, as explicit call to gc may really hinder application performances, we set for the priority 2.

## Code formatting

We try to keep a consistent code formatting through out PMD code base to ensure an easier maintenance and also make
the pull request as readable as possible.

In order to ensure this, we use a PMD specific Eclipse formatter configuration, which is maintained in a
separate project - "build-tools": [eclipse-code-formatter.xml](https://github.com/pmd/build-tools/blob/master/eclipse/pmd-eclipse-code-formatter.xml).
Please do not forget to use it before committing or any source code!

## Correctness

You should try to run the rule on a large code base, like the jdk source code for instance. This will help ensure that the rule does not raise exceptions when dealing with unusual constructs.

If your rule is stateful, make sure that it is reinitialized correctly. The “-stress” command line option can be used as the files will then not be ordered but processed randomly. Running pmd with the “-stress” option several times and sorting the text output should produce identical results if the state information is correctly reset.

## Performance issues

When writing a new rule, using command line option “-benchmark” on a few rules can give an indication on how the rule compares to others. To get the full picture, use the rulesets/internal/all-java.xml ruleset with “-benchmark”.

Rules which use the RuleChain to visit the AST are faster than rules which perform manual visitation of the AST. The difference is small for an individual Java rule, but when running 100s of rules, it is measurable. For XPath rules, the difference is extremely noticeable due to Jaxen overhead for AST navigation. Make sure your XPath rules using the RuleChain.

(TODO How does one know except by running in a debugger or horrendous performance?).

## Adding test cases

See [Test Framework](pmd_devdocs_testing.html) for the general documentation

### … for a rule I want to submit (in a patch)

Figure out the category to which you want to the rule. Then add your rule to the appropriate test class for
the category and add the XML test data in the correct xml subpackage.

### … for something too specific, that I won’t be able to submit

See [Using the test framework externally](pmd_devdocs_testing.html#using-the-test-framework-externally)

## Code quality

If you want to contribute a java rule to PMD, you should run PMD on it (Using the dogfood rulesets), to ensure that you rule follow the rules defined by the PMD community.

Also note, that if this is not a strong policy, most developers uses the berkeley braces syntax.

## Committing

Before committing changes, make sure the verify phase of a maven build succeeds without test failures. Drink a beer while you wait for it to finish.

Then read the output to make sure no fatal errors are present.
