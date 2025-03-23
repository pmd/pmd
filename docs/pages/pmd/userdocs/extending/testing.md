---
title: Testing your rules
tags: [extending, userdocs]
summary: "Learn how to use PMD's simple test framework for unit testing rules."
permalink: pmd_userdocs_extending_testing.html
last_updated: December 2023 (7.0.0)
author: Andreas Dangel <andreas.dangel@adangel.org>
---

## Introduction

Good rules have tests. At least a positive test case - a code example, that triggers the rule and reports
a violation - and a negative test case - a code example, that doesn't trigger the rule - should be created.
Of course, the more tests, the better the rule is verified. If the rule is more complex or defines properties,
with which the behavior can be modified, then these different cases can also be tested.

And if there is a bug fix for a rule, be it a false positive or a false negative case, it should be accompanied
by an additional test case, so that the bug is not accidentally reintroduced later on.

## How it works

PMD's built-in rules are organized in rulesets, where all rules belonging to the same category are placed
in a single ruleset, such as "category/java/bestpractices.xml".
Each category-ruleset has a single abstract base test class, from which the individual test classes inherit.
We have one test class per rule, which executes all test cases for a single rule. The actual test cases are
stored in separate XML files, for each rule a separate file is used.

All the test classes inherit from {% jdoc test::test.PmdRuleTst %},
which provides the seamless integration with JUnit5. This base class determines the language, the category name
and the rule name from the concrete test class. It then searches the test code on its own.
E.g. the individual rule test class
`net.sourceforge.pmd.lang.java.rule.bestpractices.AbstractClassWithoutAbstractMethodTest` tests the
rule with the name "AbstractClassWithoutAbstractMethod", which is in the category "bestpractices" for the
language "java".

The test code (see below [Test XML Reference](#test-xml-reference)) describes the test case completely with
the expected behavior like number of expected rule violations, where the violations are expected, and so on.

When you are running the test class in your IDE (e.g. Eclipse or IntelliJ IDEA) you can also select a single
test case and just execute this one.

## Where to place the test code

The {% jdoc test::test.PmdRuleTst %} class searches the XML file, that describes the test cases
for a certain rule using the following convention:

The XML file is a test resource, so it is searched in the tree under `src/test/resources`.

The sub package `xml` of the test class's package should contain a file with the same name as the rule's name
which is under test.

For example, to test the rule "AbstractClassWithoutAbstractMethod", the fully qualified test class is:

    net.sourceforge.pmd.lang.java.rule.bestpractices.AbstractClassWithoutAbstractMethodTest

The test code for the rule can be found in the file:

    src/test/resources/net/sourceforge/pmd/lang/java/rule/bestpractices/xml/AbstractClassWithoutAbstractMethod.xml

In general, the class name and file name pattern for the test class and data is this:

    net.sourceforge.pmd.lang.<Language Id>.rule.<Category Name>.<Rule Name>Test
    src/test/resources/net/sourceforge/pmd/lang/<Language Id>/rule/<Category Name>/xml/<Rule Name>.xml

Note: Language Id is the id defined by the language module, see {% jdoc core::lang.Language#getId() %}.

{%include tip.html content="This convention allows you to quickly find the test cases for a given rule:
Just search in the project for a file `<Rule Name>.xml`. Search for a class `<Rule Name>Test` to find the
unit test class for the given rule. And if the rule is a Java-based rule, the search for `<Rule Name>Rule`
finds the rule implementation class." %}

{%include note.html content="If you want to use the test framework with a different package structure,
see [Using the test framework externally](#using-the-test-framework-externally)." %}

## Simple example

### Test Class: AbstractClassWithoutAbstractMethodTest

This class inherits from {% jdoc test::test.PmdRuleTst %} and is located in the package "bestpractices",
since the rule belongs to the category "Best Practices":

``` java
package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.test.PmdRuleTst;

class AbstractClassWithoutAbstractMethodTest extends PmdRuleTst {
    // no additional unit tests
}
```

{%include note.html content="You can also add additionally standard JUnit test methods annotated with `@Test` to
this test class." %}

### Test Data: AvoidBranchingStatementAsLastInLoop.xml

This is a stripped down example which just contains two test cases.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<test-data
    xmlns="http://pmd.sourceforge.net/rule-tests"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/rule-tests https://pmd.sourceforge.io/rule-tests_1_0_0.xsd">

    <test-code>
        <description>concrete class</description>
        <expected-problems>0</expected-problems>
        <code><![CDATA[
public class Foo {}
    ]]></code>
    </test-code>

    <test-code>
        <description>failure case</description>
        <expected-problems>1</expected-problems>
        <expected-linenumbers>1</expected-linenumbers>
        <code><![CDATA[
public abstract class Foo {}
    ]]></code>
    </test-code>
</test-data>
```

Each test case is in an own `<test-code>` element. The first defines 0 expected problems, means this code doesn't
trigger the rule. The second test case expects 1 problem. Since the rule violations also report the exact AST node,
you can verify the line number, too.

## Test XML Reference

The root element is `<test-data>`. It can contain one or more `<test-code>` and `<code-fragment>` elements.
Each `<test-code>` element defines a single test case. `<code-fragment>` elements are used to share code snippets
between different test cases.

{%include note.html content="The XML schema is available at [rule-tests.xsd](https://github.com/pmd/pmd/blob/main/pmd-test-schema/src/main/resources/net/sourceforge/pmd/test/schema/rule-tests_1_0_0.xsd)." %}

### `<test-code>` attributes

The `<test-code>` elements understands the following optional attributes:

* **disabled**: By default, it's `false`. Set it to `true`, to ignore and skip a test case.

* **focused**: By default, it's `false`. Set it to `true`, to ignore all other test cases. This is useful while
  debugging a rule and you want to focus only on one specific case.

### `<test-code>` children

*   **`<description>`**: Short description of the test case. This will be the JUnit test name in the report.
    If applicable, this description should contain a reference to the bug number, this test case reproduces.

*   **`<rule-property>`**: Optional rule properties, if the rule is configurable. Just add multiple elements, to
    set multiple properties for one test case. For an example, see below.

*   **`<expected-problems>`**: The raw number of expected rule violations, that this rule is expected to report.
    For false-positive test cases, this is always "0". For false-negative test cases, it can be any positive number.

*   **`<expected-linenumbers>`**: Optional element. It's a comma separated list of line numbers.
    If there are rule violations reported, then this allows you to
    assert the line numbers. Useful if multiple violations should be detected and to be sure that
    false positives and negatives don't erase each other.

*   **`<expected-messages>`**: Optional element, with `<message>` elements as children.
    Can be used to validate the correct error message, e.g. if the error message references a variable name.

*   **`<code>`**: Either the `<code>` element or the `<code-ref>` element is required. It provides the actual code
    snippet on which the rule is executed. The code itself is usually wrapped in a "CDATA" section, so that no
    further XML escapes (entity references such as &amp;lt;) are necessary.

*   **`<code-ref id=...>`**: Alternative to `<code>`. References a `<code-fragment>` defined earlier in the file.
    This allows you to share the same code snippet with several test cases. The attribute `id` must match the
    id of the references code fragment.

*   **`<source-type>`**: Optional element that specifies a specific language version. This can be used
    to select a specific parser version for parsing the code snippet. If not given, the default version of
    the rule's language is used. This element can almost always be omitted.

### `<code-fragment>`

The code fragment has just one required attribute: **id**. This is used to reference it via a `<code-ref>` element
inside a `<test-code>`. Similar like the `<code>` element, the content of `<code-fragment>` is usually wrapped
in a "CDATA" section, so that no further XML escapes (entity references such as &amp;lt;) are necessary.

### Complete XML example

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<test-data
    xmlns="http://pmd.sourceforge.net/rule-tests"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/rule-tests https://pmd.sourceforge.io/rule-tests_1_0_0.xsd">

    <test-code disabled="false">
        <description>Just a description, will be used as the test name for JUnit in the reports</description>
        <rule-property name="somePropName">propValue</rule-property>    <!-- optional -->
        <expected-problems>2</expected-problems>
        <expected-linenumbers>5,14</expected-linenumbers>               <!-- optional -->
        <expected-messages>                                             <!-- optional -->
            <message>Violation message 1</message>
            <message>Violation message 2</message>
        </expected-messages>
        <code><![CDATA[
public class ConsistentReturn {
    public Boolean foo() {
    }
}
        ]]></code>
        <source-type>java 1.5</source-type>                             <!-- optional -->
    </test-code>

    <code-fragment id="codeSnippet1"><![CDATA[
public class ConsistentReturn {
    public Boolean foo() {
    }
}
    ]]></code-fragment>

    <test-code>
        <description>test case using a code fragment</description>
        <expected-problems>0</expected-problems>
        <code-ref id="codeSnippet1"/>
    </test-code>
</test-data>
```

{% include note.html content="For better readability, the indentation should be 4 for spaces and no tabs.
Each test-code should be separated by a blank line. CDATA
sections are only required for the code snippets which should be formatted with indentation for
better readability. The description can be written directly without a CDATA section." %}

## Using the test framework externally

It is also possible to use the test framework for custom rules developed outside the PMD source base.
In order to use the test framework you just need to reference the dependency `net.sourceforge.pmd:pmd-test`.

For maven, you can use this snippet:

```xml
<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-test</artifactId>
    <version>{{site.pmd.version}}</version>
    <scope>test</scope>
</dependency>
```

Then proceed as described earlier: create your test class, create your test cases and run the unit test.

There is one difference however: Since your package structure is probably different, you'll need to register
the rule test manually, as SimpleAggregatorTst will fail to determine it correctly from the package and class names:

``` java
package com.example.pmd.rules;

import net.sourceforge.pmd.test.SimpleAggregatorTst;

class CustomRuleTest extends SimpleAggregatorTst {
    @Override
    protected void setUp() {
        addRule("com/example/pmd/ruleset.xml", "CustomRule");
    }
}
```

This will then search for a rule named "CustomRule" in the ruleset, that is located in "src/main/resources" under
the path "com/example/pmd/ruleset.xml".

The test data should be placed in an XML file located in "src/test/resources" under the path
"com/example/pmd/rules/xml/CustomRule.xml".

## How the test framework is implemented

The framework uses the [dynamic test feature](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests)
of JUnit5 under the hood, among a couple of utility classes:

* {% jdoc test::test.PmdRuleTst %}: This is the base class for tests in PMD's code base. It is a subclass of
  {% jdoc test::test.RuleTst %} and just
  contains the logic to determine the test resources based on the test class name.

* {% jdoc test::test.SimpleAggregatorTst %}: This is a more generic base class for the test classes.
  It doesn't register any test cases on its own. You can register your own rule tests.
  It itself is a subclass of {% jdoc test::test.RuleTst %}.

* The maven module "pmd-test-schema" contains the logic to parse the XML files and provides a
  {% jdoc test-schema::test.schema.RuleTestCollection %}. This in turn contains a list of
  {% jdoc test-schema::test.schema.RuleTestDescriptor %}s. Each rule test descriptor describes a single test case.

* {% jdoc test::test.RuleTst %}: uses the {%jdoc test-schema::test.schema.TestSchemaParser %}
  from module "pmd-test-schema" to parse the test cases, executes each
  rule test descriptor and asserts the results. It defines a test method `ruleTests()` which is a test factory
  and returns one dynamic test per rule test.

## Example projects

See <https://github.com/pmd/pmd-examples> for a couple of example projects, that
create custom PMD rules for different languages including tests.
