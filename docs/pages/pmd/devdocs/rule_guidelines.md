---
title: PMD Rule Guidelines
tags: [customizing]
summary: Rule Guidelines
last_updated: July 3, 2016
permalink: pmd_devdocs_rule_guidelines.html
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

We try to keep a consistent code formatting through out PMD code base to ensure an easier maintenance and also make the diff send to the mailing list as readable as possible.

In order to ensure this, we use a PMD specific Eclipse formatter configuration: **tools/config/eclipse-code-formatter-settings.xml**. Please do not forget to uses it before committing or any source code!

## Correctness

You should try to run the rule on a large code base, like the jdk source code for instance. This will help ensure that the rule does not raise exceptions when dealing with unusual constructs.

If your rule is stateful, make sure that it is reinitialized correctly. The “-stress” command line option can be used as the files will then not be ordered but processed randomly. Running pmd with the “-stress” option several times and sorting the text output should produce identical results if the state information is correctly reset.

## Performance issues

When writing a new rule, using command line option “-benchmark” on a few rules can give an indication on how the rule compares to others. To get the full picture, use the rulesets/internal/all-java.xml ruleset with “-benchmark”.

Rules which use the RuleChain to visit the AST are faster than rules which perform manual visitation of the AST. The difference is small for an individual Java rule, but when running 100s of rules, it is measurable. For XPath rules, the difference is extremely noticeable due to Jaxen overhead for AST navigation. Make sure your XPath rules using the RuleChain.

(TODO How does one know except by running in a debugger or horrendous performance?).

## Adding test cases

### … for a rule I want to submit (in a patch)

In the test directory (src/test/java), you’ll find all the unit tests for PMD. In the proper package (for instance, net.sourceforge.pmd.lang.java.rule.basic, for rules from the basic rulesets), you’ll find a test suite such as this:

```java
public class BasicRulesTest extends SimpleAggregatorTst {
   @Before
   public void setUp() {
       addRule("java-basic", "AvoidDecimalLiteralsInBigDecimalConstructor");
       addRule("java-basic", "AvoidMultipleUnaryOperators");
       addRule("java-basic", "AvoidThreadGroup");
       ...
```

Suppose you want to add tests for a new rule of yours, called “MyNewRule”. Just add the proper line in this suite:

```java
public void setUp() {
    ...
    addRule("java-basic", "MyNewRule");
    ...
}
```
And add in the appropriate xml subpackage in src/test/resources (for instance, net.sourceforge.pmd.lang.java.rule.basic.xml). There you should find a XML file for each rule, the syntax is pretty obvious:

```xml
<test-data>
    <test-code>
        <description>call super</description>
        <expected-problems>1</expected-problems>
        <code><![CDATA[
            public class Foo extends Bar {
                public void foo() {
                super.foo();
                }
            }
        ]]></code>
    </test-code>
</test-data>
```

### … for something too specific, that I won’t be able to submit

In this case, you can still use the PMD test framework, as it is shipped in PMD. Follow the previous instructions to right your test case, and simply create our own RulesTest using the SimpleAggregatorTst:

```java
package too.specific.to.submit;

import org.junit.Before;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SpecificRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("specific-rules.xml", "MySpecificRule");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(SpecificRulesTest.class);
    }
}
```

>Note the following PMD dependencies are required to run the test:
>* asm
>* jaxen

## Code quality

If you want to contribute a java rule to PMD, you should run PMD on it (Using the dogfood rulesets), to ensure that you rule follow the rules defined by the PMD community.

Also note, that if this is not a strong policy, most developers uses the berkeley braces syntax.

## Committing

Before committing changes, make sure the verify phase of a maven build succeeds without test failures. Drink a beer while you wait for it to finish.

Then read the output to make sure no fatal errors are present.
