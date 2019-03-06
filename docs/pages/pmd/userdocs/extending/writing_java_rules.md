---
title:  Writing a custom rule
tags: [extending, userdocs]
summary: "Learn how to write a custom rule for PMD"
last_updated: July 3, 2016
permalink: pmd_userdocs_extending_writing_java_rules.html
author: Tom Copeland <tomcopeland@users.sourceforge.net>
---

{% jdoc_nspace :coremx core::lang.metrics %}
{% jdoc_nspace :coreast core::lang.ast %}
{% jdoc_nspace :jmx java::lang.java.metrics %}
{% jdoc_nspace :jast java::lang.java.ast %}
{% jdoc_nspace :jrule java::lang.java.rule %}

{% include note.html content="All that should be written in the Javadocs.." %}

This page covers the specifics of writing a rule in Java. The basic development
process is very similar to the process for XPath rules, which is described in
[Using the Designer](pmd_userdocs_extending_designer_intro.html#rule-development-process).

Basically, you open the designer, look at the structure of the AST, and refine
your rule as you add test cases.

All rules ultimately implement the interface {% jdoc core::Rule %}. Each
language implementation provides a specific base rule class to ease your pain,
e.g. {% jdoc jrule::AbstractJavaRule %}.

In this page we'll talk about rules for the Java language, but the process is
very similar for other languages.

## Rule execution

### Tree traversal

When a rule is applied to a file, it's handed the root of the AST and told
to traverse all the tree to look for violations. Each rule defines a specific
`visit` method for each type of node that can be found in the Java AST, which
by default just visits the children.

So the following rule would traverse the whole tree and do nothing:

```java
public class MyRule extends AbstractJavaRule {
    // all methods are default implementations!
}
```

Generally, a rule wants to check for only some node types. In our XPath example
in [Using the Designer](pmd_userdocs_extending_designer_intro.html#a-simple-rule),
we wanted to check for some `VariableDeclaratorId` nodes. That's the XPath name,
but in Java, you'll get access to the {% jdoc jast::ASTVariableDeclaratorId %}
full API.

If you want to check for some specific node types, you can override the
corresponding `visit` method:

```java
public class MyRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        // This method is called on each node of type ASTVariableDeclaratorId
        // in the AST


        return super.visit(node, data);
    }
}
```

The `super.visit(node, data)` call is super common in rule implementations,
because it makes the traversal continue by visiting all the descendants of the
current node.

#### Stopping the traversal

Sometimes you have checked all you needed and you're sure that the descendants
of a node may not contain violations. In that case, you can avoid calling the
`super` implementation and the traversal will not continue further down. This
means that your callbacks (`visit` implementations) won't be called on the rest
of the subtree. The siblings of the current node may be visited
recursively nevertheless.

#### Economic traversal: the rulechain

If you don't care about the order in which the nodes are traversed (e.g. your
rule doesn't maintain any state between visits), then you can monumentally
speed-up your rule by using the **rulechain**.

That mechanism doesn't recurse on all the tree, instead, your rule will only be
passed the nodes it is interested in. To use the rulechain correctly:
* Your rule must register those node types by calling {% jdoc core::Rule#addRuleChainVisit(java.lang.Class) %}
in its constructor.
* Your visit methods **must not recurse!** In effect, you should call never
call `super.visit` in the methods.

### Execution across files, thread-safety and statefulness

When starting execution, PMD will instantiate a new instance of your rule.
If PMD is executed in multiple threads, then each thread is using its own
instance of the rule. This means, that the rule implementation **does not need to care about
threading issues**, as PMD makes sure, that a single instance is not used concurrently
by multiple threads.

However, for performance reasons, the rule instances are used for multiple files.
This means, that the constructor of the rule is only executed once (per thread)
and the rule instance is reused. If you rely on a proper initialization of instance
properties, you can do the initialization e.g. in the visit-method of the {% jdoc jast::ASTCompilationUnit %}
node - which is visited first and only once per file. However, this
solution would only work for rules written for the Java language. A language
independent way is to override the method `apply` of the rule (and call super).
The apply method is called exactly once per file.

<!-- We don't support language-independent rules anyway... -->



## Rule lifecycle reference

### Construction

Exactly once:

1. The rule's no-arg constructor is called when loading the ruleset.
The rule's constructor must define:
  * [Rulechain visits](#economic-traversal-the-rulechain)
  * [Property descriptors](pmd_userdocs_extending_defining_properties#for-java-rules)
2. If the rule was included in the ruleset as a rule reference,
some properties [may be overridden](pmd_userdocs_configuring_rules.html#rule-properties).
If an overridden property is unknown, an error is reported.
3. Misconfigured rules are removed from the ruleset

### Execution

For each thread, a deep copy of the rule is created.
Then, for each thread, for each analysed file:

3. {% jdoc core::Rule#start(core::RuleContext) %} is called once, before parsing
4. {% jdoc core::Rule#apply(java.util.List,core::RuleContext) %} is called with the root 
of the AST. That method performs the AST traversal that ultimately calls visit methods. 
It's not called for RuleChain rules.
5. {% jdoc core::Rule#end(core::RuleContext) %} is called when the rule is done processing 
the file

### FIXME

without specific order:
* There's no hook for "after construction", or "after analysis" we only have "before file"
and "after file"
* What's the point of having `Rule#start` ? The globally accepted pattern is to override
the visit method for the root node. We have `visit(<root type>)`, `apply`, and `start`
that are called at the same lifecycle point...
* Rule#apply is not called for RuleChain visits
* Why is Rule#apply overridable? Anyone can break it
* Sooo much code is duplicated everywhere in rulechain visitor implementations, which are 
all the same modulo a cast...
* Converting a rule to the rulechain shouldn't force removing `super.visit` calls... We 
could just provide a base class for rulechain rules that overrides eg `visit(JavaNode)`
to be a noop
* RuleSets is unnecessary, we should just rely on a good RuleSet implementation
* Initializer.initialize() is called for each file instead of once per language module loading
* For each file all the rules in all rulesets are checked to use typeres or other 
analysis passes






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
        return (node.jjtGetNumChildren() != 0 && (node.jjtGetChild(0) instanceof ASTBlock));
    }
}
```

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

## Bundle it up

To use your rules as part of a nightly build or whatever, it’s helpful to bundle up both the rule and the ruleset.xml file in a jar file. Then you can put that jar file on the CLASSPATH of your build. Setting up a script or an Ant task to do this can save you some tedious typing.

## Repeat as necessary

I’ve found that my rules usually don’t work the first time, and so I have to go back and tweak them a couple times. That’s OK, if we were perfect programmers PMD would be useless anyhow :-).

As an acceptance test of sorts, I usually run a rule on the JDK 1.4 source code and make sure that a random sampling of the problems found are in fact legitimate rule violations. This also ensures that the rule doesn’t get confused by nested inner classes or any of the other oddities that appear at various points in the JDK source.

You’re rolling now. If you think a rule would benefit the Java development community as a whole, post a message to [the forum](http://sourceforge.net/p/pmd/discussion/188192) so we can get the rule moved into one of the core rulesets.

Or, if you can improve one of the existing rules, that’d be great too! Thanks!

Finally, for many more details on writing rules, pick up [PMD Applied](http://pmdapplied.com/)!
