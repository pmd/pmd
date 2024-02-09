---
title:  Writing a custom rule
tags: [extending, userdocs]
summary: "Learn how to write a custom rule for PMD"
last_updated: December 2023 (7.0.0)
permalink: pmd_userdocs_extending_writing_java_rules.html
author: Tom Copeland <tomcopeland@users.sourceforge.net>
---

{% jdoc_nspace :coremx core::lang.metrics %}
{% jdoc_nspace :coreast core::lang.ast %}
{% jdoc_nspace :jmx java::lang.java.metrics %}
{% jdoc_nspace :jast java::lang.java.ast %}
{% jdoc_nspace :jrule java::lang.java.rule %}

{% include note.html content="Ideally most of what is written in this document would be directly
in the Javadocs of the relevant classes. This is not the case yet." %}

This page covers the specifics of writing a rule in Java. The basic development
process is very similar to the process for XPath rules, which is described in
[Your First Rule](pmd_userdocs_extending_your_first_rule.html#rule-development-process).

Basically, you open the designer, look at the structure of the AST, and refine
your rule as you add test cases.

In this page we'll talk about rules for the Java language, but the process is
very similar for other languages.

{% include note.html content="[Please find an index of language-specific documentation here](tag_languages.html)" %}

## Basics

To write a rule in Java you'll have to:

1. Write a Java class that implements the interface {% jdoc core::lang.rule.Rule %}. Each
language implementation provides a base rule class to ease your pain,
e.g. {% jdoc jrule::AbstractJavaRule %}.
2. Compile this class, linking it to PMD APIs (e.g. using PMD as a Maven dependency)
3. Bundle this into a JAR and add it to the execution classpath of PMD
4. Declare the rule in your ruleset XML

## Rule execution

Most base rule classes use a [Visitor pattern](https://sourcemaking.com/design_patterns/visitor)
to explore the AST.

### Tree traversal

When a rule is applied to a file, it's handed the root of the AST and told
to traverse all the tree to look for violations. Each rule defines a specific
`visit` method for each type of node for of the language, which
by default just visits the children.

So the following rule would traverse the whole tree and do nothing:

```java
public class MyRule extends AbstractJavaRule {
    // all methods are default implementations!
}
```

Generally, a rule wants to check for only some node types. In our XPath example
in [Your First Rule](pmd_userdocs_extending_your_first_rule.html),
we wanted to check for some `VariableId` nodes. That's the XPath name,
but in Java, you'll get access to the {% jdoc jast::ASTVariableId %}
full API.

If you want to check for some specific node types, you can override the
corresponding `visit` method:

```java
public class MyRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTVariableId node, Object data) {
        // This method is called on each node of type ASTVariableId
        // in the AST

        if (node.getType() == short.class) {
            // reports a violation at the position of the node
            // the "data" parameter is a context object handed to by your rule
            // the message for the violation is the message defined in the rule declaration XML element
            asCtx(data).addViolation(node);
        }

        // this calls back to the default implementation, which recurses further down the subtree
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
* Your rule must override the method {% jdoc core::lang.rule.AbstractRule#buildTargetSelector() %}. This method
  should return a target selector, that selects all the node types you are interested in. E.g. the factory
  method {% jdoc core::lang.rule.RuleTargetSelector#forTypes(java.lang.Class,java.lang.Class...) %} can be used
  to create such a selector.
* For the Java language, there is another base class, to make it easier:
  {% jdoc java::lang.java.rule.AbstractJavaRulechainRule %}. You'll need to call the super constructor and
  provide the node types you are interested in.
* Your visit methods **must not recurse!** In effect, you should call never
  call `super.visit` in the methods.

#### Manual AST navigation

In Java rule implementations, you often need to navigate the AST to find the interesting nodes.
In your `visit` implementation, you can start navigating the AST from the given node.

The {% jdoc core::lang.ast.Node %} interface provides a couple of useful methods
that return a {%jdoc core::lang.ast.NodeStream %} and can be used to query the AST:

* {% jdoc core::lang.ast.Node#ancestors() %}
* {% jdoc core::lang.ast.Node#ancestorsOrSelf() %}
* {% jdoc core::lang.ast.Node#children() %}
* {% jdoc core::lang.ast.Node#descendants() %}
* {% jdoc core::lang.ast.Node#descendantsOrSelf() %}
* {% jdoc core::lang.ast.Node#ancestors(java.lang.Class) %}
* {% jdoc core::lang.ast.Node#children(java.lang.Class) %}
* {% jdoc core::lang.ast.Node#descendants(java.lang.Class) %}

The returned NodeStream API provides easy to use methods that follow the Java Stream API (`java.util.stream`).

Example:

```java
NodeStream.of(someNode)                           // the stream here is empty if the node is null
          .filterIs(ASTVariableDeclaratorId.class)// the stream here is empty if the node was not a variable declarator id
          .followingSiblings()                    // the stream here contains only the siblings, not the original node
          .filterIs(ASTVariableInitializer.class)
          .children(ASTExpression.class)
          .children(ASTPrimaryExpression.class)
          .children(ASTPrimaryPrefix.class)
          .children(ASTLiteral.class)
          .filterMatching(Node::getImage, "0")
          .filterNot(ASTLiteral::isStringLiteral)
          .nonEmpty(); // If the stream is non empty here, then all the pipeline matched
```

The {% jdoc core::lang.ast.Node %} interface provides also an alternative way to navigate the AST for convenience:

* {% jdoc core::lang.ast.Node#getParent() %}
* {% jdoc core::lang.ast.Node#getNumChildren() %}
* {% jdoc core::lang.ast.Node#getChild(int) %}
* {% jdoc core::lang.ast.Node#getFirstChild() %}
* {% jdoc core::lang.ast.Node#getLastChild() %}
* {% jdoc core::lang.ast.Node#getPreviousSibling() %}
* {% jdoc core::lang.ast.Node#getNextSibling() %}
* {% jdoc core::lang.ast.Node#firstChild(java.lang.Class) %}

Depending on the AST of the language, there might also be more specific methods that can be used to
navigate. E.g. in Java there exists the method {% jdoc !!java::lang.java.ast.ASTIfStatement#getCondition() %}
to get the condition of an If-statement.

### Reporting violations

In your visit method, you have access to the {% jdoc core::reporting.RuleContext %} which is the entry point into
reporting back during the analysis.

* {% jdoc core::reporting.RuleContext#addViolation(core::lang.ast.Node) %} reports a rule violation at
  the position of the given node with the message defined in the rule declaration XML element.
* The message defined in the rule declaration XML element might contain **placeholder**, such as `{0}`.
  In that case, you need to call {% jdoc core::reporting.RuleContext#addViolation(core::lang.ast.Node,java.lang.Object...) %}
  and provide the values for the placeholders. The message is actually processed as a `java.text.MessageFormat`.
* Sometimes a rule might want to differentiate between different cases of a violation and use different
  messages. This is possible by calling the methods
  {% jdoc core::reporting.RuleContext#addViolationWithMessage(core::lang.ast.Node,java.lang.String) %} or
  {% jdoc core::reporting.RuleContext#addViolationWithMessage(core::lang.ast.Node,java.lang.String,java.lang.Object...) %}.
  Using these methods, the message defined in the rule declaration XML element is _not used_.
* Rules can be customized using properties and sometimes you want to include the actual value of a property
  in the message, e.g. if the rule enforces a specific limit.
  The syntax for such placeholders is: `${propertyName}`.
* Some languages support additional placeholder variables. E.g. for Java, you can use `${methodName}` to insert
  the name of the method in which the violation occurred.
  See [Java-specific features and guidance](pmd_languages_java.html#violation-decorators).


### Execution across files, thread-safety and statefulness

When starting execution, PMD will instantiate a new instance of your rule.
If PMD is executed in multiple threads, then each thread is using its own
instance of the rule. This means, that the rule implementation **does not need to care about
threading issues**, as PMD makes sure, that a single instance is not used concurrently
by multiple threads.

However, for performance reasons, the rule instances are reused for multiple files.
This means, that the constructor of the rule is only executed once (per thread)
and the rule instance is reused. If you rely on a proper initialization of instance
properties, you can do the initialization in the `start` method of the rule
(you need to override this method).
The start method is called exactly once per file.

### Using metrics

Some languages might support metrics.

* [Apex-specific features and guidance](pmd_languages_apex.html#metrics-framework)
* [Java-specific features and guidance](pmd_languages_java.html#metrics-framework)

### Using symbol table

Some languages might support symbol table.

* [Java-specific features and guidance](pmd_languages_java.html#symbol-table-apis)

### Using type resolution

Some languages might support type resolution.

* [Java-specific features and guidance](pmd_languages_java.html#type-resolution-apis)

## Rule lifecycle reference

### Construction

Exactly once (per thread):

1. The rule's no-arg constructor is called when loading the ruleset.
The rule's constructor must define already any
[Property descriptors](pmd_userdocs_extending_defining_properties.html#for-java-rules) the rule wants to use.
2. If the rule was included in the ruleset as a rule reference,
some properties [may be overridden](pmd_userdocs_configuring_rules.html#rule-properties).
If an overridden property is unknown, an error is reported.
3. Misconfigured rules are removed from the ruleset

### Execution

For each thread, a deep copy of the rule is created. Each thread is given
a different set of files to analyse. Then, for each such file and for each
rule copy:

1. {% jdoc core::lang.rule.Rule#start(core::reporting.RuleContext) %} is called once, before parsing
2. {% jdoc core::lang.rule.Rule#apply(core::lang.ast.Node,core::reporting.RuleContext) %} is called with the root
of the AST. That method performs the AST traversal that ultimately calls visit methods.
It's not called for RuleChain rules.
3. {% jdoc core::lang.rule.Rule#end(core::reporting.RuleContext) %} is called when the rule is done processing
the file

## Example projects

See <https://github.com/pmd/pmd-examples> for a couple of example projects, that
create custom PMD rules for different languages.
