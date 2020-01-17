---
title:  Writing a custom rule
tags: [extending, userdocs]
summary: "Learn how to write a custom rule for PMD"
last_updated: July 3, 2016
permalink: pmd_userdocs_extending_writing_pmd_rules.html
author: Tom Copeland <tomcopeland@users.sourceforge.net>
---

# How to write a PMD rule

Writing PMD rules is cool because you don’t have to wait for us to get around to implementing feature requests.

## Get a development environment set up first
	[Here’s some initial information on compiling PMD]

## Java or XPath?

There are two way to write rules:

*   Write a rule using Java
*   Write an XPath expression

We’ll cover the Java way first and the XPath way second. Most of this documentation is applicable to both methods, too, so read on.

## Figure out what you want to look for

Lets’s figure out what problem we want to spot. We can use “While loops must use braces” as an example. In the source code below, it’s easy to get lost visually - it’s kind of hard to tell what the curly braces belong to.

```java
class Example {
	void bar() {
		while (baz)
		buz.doSomething();
	}
}
```

So we know what an example in source code looks like, which is half the battle.

## Write a test-data example and look at the AST

PMD doesn’t use the source code directly; it uses a `JavaCC` generated parser to parse the source code and produce an AST (Abstract Syntax Tree). The AST for the code above looks like this:

```
CompilationUnit
 TypeDeclaration
  ClassDeclaration:(package private)
   UnmodifiedClassDeclaration(Example)
    ClassBody
     ClassBodyDeclaration
      MethodDeclaration:(package private)
       ResultType
       MethodDeclarator(bar)
        FormalParameters
       Block
        BlockStatement
         Statement
          WhileStatement
           Expression
            PrimaryExpression
             PrimaryPrefix
              Name:baz
           Statement
            StatementExpression:null
             PrimaryExpression
              PrimaryPrefix
               Name:buz.doSomething
              PrimarySuffix
               Arguments
```

You can generate this yourself by:

*   Run the batch file `bin/designer.bat`
*   Paste the code into the left text area and click the “Go” button
*   Note that there’s another panel and a textfield to test out XPath expressions; more on that later.
*   Here’s a screenshot: {% include image.html file="devdocs/designer_screenshot.png" alt="Designer Screenshot" %}

So you can see in the example above that the AST for a `WhileStatement` looks kind of like this (excluding that expression gibberish for clarity):

```
WhileStatement
 Expression
 Statement
  StatementExpression
```

If you were to add curly braces around the call to `buz.doSomething()` and click “Go” again, you’d see that the AST would change a bit. It’d look like this:

```
WhileStatement
 Expression
 Statement
  Block
   BlockStatement
    Statement
     StatementExpression
```

Ah ha! We see that the curly braces add a couple more AST nodes - a `Block` and a `BlockStatement`. So all we have to do is write a rule to detect a `WhileStatement` that has a `Statement` that’s not followed by a `Block`, and we’ve got a rule violation.

By the way, all this structural information - i.e., the fact that a Statement may be followed a Block - is concisely defined in the [EBNF grammar](https://github.com/pmd/pmd/blob/master/pmd-java/etc/grammar/Java.jjt). So, for example, the Statement definition looks like this:

```
void Statement() :
{}
{
  LOOKAHEAD( { isNextTokenAnAssert() } ) AssertStatement()
| LOOKAHEAD(2) LabeledStatement()
| Block()
| EmptyStatement()
| StatementExpression() ";"
| SwitchStatement()
| IfStatement()
| WhileStatement()
| DoStatement()
| ForStatement()
| BreakStatement()
| ContinueStatement()
| ReturnStatement()
| ThrowStatement()
| SynchronizedStatement()
| TryStatement()
}
```

showing that a Statement may be followed by all sorts of stuff.

## Write a rule class

Create a new Java class that extends `net.sourceforge.pmd.lang.java.rule.AbstractJavaRule`:

```java
import net.sourceforge.pmd.lang.java.rule.*;
public class WhileLoopsMustUseBracesRule extends AbstractJavaRule {
}
```

That was easy. PMD works by creating the AST and then traverses it recursively so a rule can get a callback for any type it’s interested in. So let’s make sure our rule gets called whenever the AST traversal finds a `WhileStatement`:

```java
import net.sourceforge.pmd.lang.java.rule.*;
import net.sourceforge.pmd.lang.java.ast.*;
public class WhileLoopsMustUseBracesRule extends AbstractJavaRule {
    public Object visit(ASTWhileStatement node, Object data) {
        System.out.println("hello world");
        return data;
    }
}
```

We stuck a `println()` in there for now so we can see when our rule gets hit.

## Put the WhileLoopsMustUseBracesRule rule in a ruleset file

Now our rule is written - at least, the shell of it is - and now we need to tell PMD about it. We need to add it to a ruleset XML file. Look at `pmd-java/src/main/resources/category/java/bestpractices.xml`; it’s got lots of rule definitions in it. Copy and paste one of these rules into a new ruleset - call it `mycustomrules.xml` or something. Then fill in the elements and attributes:

*   name - WhileLoopsMustUseBracesRule
*   message - Use braces for while loops
*   class - Wherever you put the rule. Note this doesn’t have to be in `net.sourceforge.pmd`; it can be in `com.yourcompany.util.pmd` or whereever you want
*   description - Use braces for while loops
*   example - A little code snippet in CDATA tags that shows a rule violation

The whole ruleset file should look something like this:

```xml
<?xml version="1.0"?>
<ruleset name="My custom rules"
		xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">
	<rule name="WhileLoopsMustUseBracesRule"
			message="Avoid using 'while' statements without curly braces"
			class="WhileLoopsMustUseBracesRule">
		<description>
			Avoid using 'while' statements without using curly braces
		</description>
		<priority>3</priority>

		<example>
			<![CDATA[
				public void doSomething() {
					while (true)
					x++;
				}
			]]>
		</example>
	</rule>
</ruleset>
```

## Run PMD using your new ruleset

OK, let’s run the new rule so we can see something work. Like this:

```DOS
pmd.bat c:\path\to\my\src xml c:\path\to\mycustomrules.xml
```


This time your “hello world” will show up right after the AST gets printed out. If it doesn’t, post a message to [the forum](http://sourceforge.net/p/pmd/discussion/188192) so we can improve this document :-)

## Write code to add rule violations where appropriate

Now that we’ve identified our problem, recognized the AST pattern that illustrates the problem, written a new rule, and plugged it into a ruleset, we need to actually make our rule find the problem, create a `RuleViolation`, and put it in the `Report`, which is attached to the `RuleContext`. Like this:

```java
import net.sourceforge.pmd.lang.ast.*;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.*;

public class WhileLoopsMustUseBracesRule extends AbstractJavaRule {
    public Object visit(ASTWhileStatement node, Object data) {
        Node firstStmt = node.jjtGetChild(1);
        if (!hasBlockAsFirstChild(firstStmt)) {
            addViolation(data, node);
        }
        return super.visit(node,data);
    }
    private boolean hasBlockAsFirstChild(Node node) {
        return (node.getNumChildren() != 0 && (node.jjtGetChild(0) instanceof ASTBlock));
    }
}
```

TODO - if you don’t understand the code for the rule, post a message to [the forum](http://sourceforge.net/p/pmd/discussion/188192) so we can improve this document :-)

## Writing a rule as an XPath expression

Daniel Sheppard integrated an XPath engine into PMD, so now you can write rules as XPath expressions. For example, the XPath expression for our WhileLoopsMustUseBracesRule looks like this:

`//WhileStatement[not(Statement/Block)]`

Concise, eh?

Note that for XPath rules you’ll need to set the `class` attribute in the rule definition to `net.sourceforge.pmd.lang.rule.XPathRule.` Like this:

```xml
<rule name="EmptyCatchBlock"
      message="Avoid empty catch blocks"
      class="net.sourceforge.pmd.lang.rule.XPathRule">
  <description>
  etc., etc.
```

Note that access modifiers are held as attributes, so, for example,

`//FieldDeclaration[@Private='true']`

finds all private fields. You can see the code that determines all the attributes [here](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/lang/ast/xpath/AttributeAxisIterator.java)

More information about writing XPath rules is [available here](pmd_userdocs_extending_writing_xpath_rules.html).

## I need some kind of Type Resolution for my rule!

### Inside an XPath query

PMD's XPath extensions include two functions called `typeIs` and `typeIsExactly`,
which determine if a node is of a specific type (either any subtype or exactly,
respectively).

Here a an example of use, inside an XPath query:

```ruby
//ClassOrInterfaceDeclaration/ExtendsList/ClassOrInterfaceType[typeIs('junit.framework.TestCase')]
```

This query will for instance match the following class declaration:

```java
import junit.framework.TestCase;

public class Foo extends TestCase { }
```

It will also match against classes which extend a *subtype* of `junit.framework.TestCase`,
i.e. a base class itself extending `TestCase` transitively. If you don't want this behaviour,
then use `typeIsExactly` instead of `typeIs`.

Checking against an array type is possible with the double bracket syntax.
An array type is denoted by just appending `[]` to the fully qualified class name
of the component type. These can be repeated for arrays of arrays
(e.g. `byte[][]` or `java.lang.String[]`).


### With Java code

Below an other sample of use of type resolution inside a java code:

```java
/**
 * A simple to detect the use of the class 'com.forbidden.class'.
 */
@SuppressWarnings("unchecked")
public Object visit(ASTClassOrInterfaceType type, Object ruleCtx) {
    Class clazz = type.getType();
    if ("com.forbidden.class".equals(clazz.getName())) {
        addViolation(ruleCtx,type);
    }
    return super.visit(type, ruleCtx);
}
````

>Note, that this will only work, if the auxiliary classpath for PMD is setup correctly, so that PMD can actually find the (compiled) class “com.forbidden.class” and you get the actual Class instance by calling getType().

Otherwise, you’ll have to string-compare the image, e.g. `"com.forbidden.class".equals(node.getImage())`

## Thread safety, concurrency issues and reuse of rule instances

When executing the rule, PMD will instantiate a new instance of your rule. If PMD is executed in multiple threads, then each thread is using its own instance of the rule. This means, that the rule implementation **does not need to care about threading issues**, as PMD makes sure, that a single instance is not used concurrently by multiple threads.

However, for performance reasons, the rule instances are used for multiple files. This means, that the constructor of the rule is only executed once (per thread) and the rule instance is reused. If you rely on a proper initialization of instance properties, you can do the initialization e.g. in the visit-method of the `ASTCompilationUnit` AST node - which is visited as first node and only once per file. However, this solution would only work for rules written for the Java language. A language independent way is to override the method `apply` of the rule (and call super). The apply method is called exactly once per file.

If you want to share data across multiple files, see the above section “I want to implement a rule that analyze more than the class”.

## Bundle it up

To use your rules as part of a nightly build or whatever, it’s helpful to bundle up both the rule and the ruleset.xml file in a jar file. Then you can put that jar file on the CLASSPATH of your build. Setting up a script or an Ant task to do this can save you some tedious typing.

## Repeat as necessary

I’ve found that my rules usually don’t work the first time, and so I have to go back and tweak them a couple times. That’s OK, if we were perfect programmers PMD would be useless anyhow :-).

As an acceptance test of sorts, I usually run a rule on the JDK 1.4 source code and make sure that a random sampling of the problems found are in fact legitimate rule violations. This also ensures that the rule doesn’t get confused by nested inner classes or any of the other oddities that appear at various points in the JDK source.

You’re rolling now. If you think a rule would benefit the (Java) development community as a whole,
create a [issue on github](https://github.com/pmd/pmd/issues) so we can get the rule moved into one of the core rulesets.

Or, if you can improve one of the existing rules, that’d be great too! Thanks!
