---
title: Suppressing warnings
keywords: [suppressing, warnings, suppresswarnings, nopmd, violationSuppressXPath, violationSuppressRegex]
tags: [userdocs]
summary: "Learn how to suppress some rule violations, from the source code using annotations or comments, or globally from the ruleset"
permalink: pmd_userdocs_suppressing_warnings.html
author: Tom Copeland <tom@infoether.com>
---

PMD provides several methods by which Rule violations can be suppressed.
Follow these steps to help you determine which expression method works best
for you:

1.  Is the thing you need to suppress universally appealing to other
    users of PMD, or is it a false positive? Can you modify the Rule to
    support this specific suppression via a configuration property, or to
    fix the false positive?  If you can do this, then please do so, and
    submit a patch back to the PMD project.  Since PMD is built by users
    for users, your help would be greatly appreciated by everyone.  If you
    cannot...

2.  Can you use Annotations or the NOPMD marker to work around your
    particular issue on a case by case basis?  If not...

3.  Can a regular expression matching the violation message work
    around your particular issue?  If not...

4.  Can a XPath query on the violation node work around your particular
    issue? If not...

5.  Your last and final option is to see the first point about
    changing the Rule, but you do not need to submit a patch back to the
    PMD project.

If you need to modify the Rule, see [How to write a rule](pmd_devdocs_writing_pmd_rules.html).
Otherwise, the other suppression methods are explained in the following sections.

## Annotations

When using Java 1.5 or later, you can use annotations to suppress PMD warnings, like this:

```java
// This will suppress all the PMD warnings in this class
@SuppressWarnings("PMD")
public class Bar {
    void bar() {
        int foo;
    }
}
```

When using Apex make sure to use single quotes instead of double quotes

```java
// This will suppress all the PMD warnings in this class
@SuppressWarnings('PMD')
```


Or you can suppress one rule with an annotation like this:

```java
// This will suppress UnusedLocalVariable warnings in this class
@SuppressWarnings("PMD.UnusedLocalVariable")
public class Bar {
    void bar() {
        int foo;
    }
}
```

Multiple rules can be suppressed by providing multiple values, ie:

```java
@SuppressWarnings({"PMD.UnusedLocalVariable", "PMD.UnusedPrivateMethod"})
```

For Apex, the syntax for this is slightly different:

```java
@SuppressWarnings('PMD.UnusedLocalVariable, PMD.UnusedPrivateMethod')
```

PMD Java also obeys the JDK annotation @SuppressWarnings("unused"), which will apply to all rules in the unused ruleset.

```java
// This will suppress UnusedLocalVariable and UnusedPrivateMethod warnings in this class
@SuppressWarnings("unused")
public class Bar {
    void bar() {
        int foo;
    }
    private void foobar(){}
}
```

## NOPMD comment

Alternatively, you can tell PMD to ignore a specific line by using the "NOPMD" marker in a comment, like this:

```java
public class Bar {
    // 'bar' is accessed by a native method, so we want to suppress warnings for it
    private int bar; //NOPMD
}
```

You can use whatever text string you want to suppress warnings, by using the `-suppressmarker` CLI option.
For example, here's how to use `TURN_OFF_WARNINGS` as the suppressor:

```java
$ cat Foo.java
public class Foo {
    void bar() {
        int x = 2; // TURN_OFF_WARNINGS
    }
}

$ ./run.sh pmd -d Foo.java -f text -R java-unusedcode -suppressmarker TURN_OFF_WARNINGS
No problems found!
UnusedLocalVariable rule violation suppressed by //NOPMD in /home/tom/pmd/pmd/bin/Foo.java
```
Note that PMD expects the //NOPMD marker to be on the same line as the violation. So, for
example, if you want to suppress an "empty `if` statement" warning, you'll need to place it on
the line containing the `if` keyword, e.g.:

```java
$ cat ~/tmp/Foo.java
public class Foo {
    void bar() {
        int x = 42;
        if (x &gt; 5) { // NOPMD
        }
    }
}
$ java net.sourceforge.pmd.PMD -d ~/tmp/Foo.java -f text -R java-basic
No problems found!
```

A message placed after the NOPMD marker will get placed in the report, e.g.:

```java
public class Foo {
    void bar() {
        try {
            bar();
        } catch (FileNotFoundException e) {} // NOPMD - this surely will never happen
    }
}
```

## XPath and Regex message suppression

If a particular rule consistently reports a warning in a particular context, you can fall
back to disabling the rule for all these contexts using one of two rule properties
that are defined on every rule. Depending on what you're after, you can suppress
violations for **specific messages** using regular expressions, or **specific nodes**
using an XPath expression.


### The property `violationSuppressRegex`

This property defines a regular expression to match against the
message of the violation.  If the regular expression matches,
then the violation will be suppressed.

For example, to suppress reporting specifically named parameters which
are unused:

```xml
<rule ref="rulesets/java/unusedcode.xml/UnusedFormalParameter">
  <properties>
    <property name="violationSuppressRegex" value=".*'mySpecialParameterName'.*"/>
  </properties>
</rule>
```

Note for message based suppression to work, you must know how to write
a regular expression that matches the message of violations you wish to
suppress. Regular expressions are explained in the JavaDoc for standard
Java class java.util.regex.Pattern.

This technique of course relies on how the rule's message is implemented.
Some rules always report the same message, in which case this property is
of no use.

###  The property `violationSuppressXPath`

This property defines an XPath query to be executed *using the
violation node as the starting point*.  If the XPath query matches anything,
then the violation will be suppressed.

For example, to suppress reporting specifically typed parameters which are unused:

```xml
<rule ref="rulesets/java/unusedcode.xml/UnusedFormalParameter">
  <properties>
    <property name="violationSuppressXPath" value=".[typeIs('java.lang.String')]"/>
  </properties>
</rule>
```

Note for XPath based suppression to work, you must know how to write
an XPath query that matches the AST structure of the nodes of the
violations you wish to suppress.  XPath queries are explained in
[XPath Rule tutorial](pmd_userdocs_extending_writing_xpath_rules.html).
