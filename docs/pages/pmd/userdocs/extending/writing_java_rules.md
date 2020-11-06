---
title:  Writing a custom rule
tags: [extending, userdocs]
summary: "Learn how to write a custom rule for PMD"
last_updated: February 2020 (6.22.0)
permalink: pmd_userdocs_extending_writing_java_rules.html
author: Tom Copeland <tomcopeland@users.sourceforge.net>
---

{% jdoc_nspace :coremx core::lang.metrics %}
{% jdoc_nspace :coreast core::lang.ast %}
{% jdoc_nspace :jmx java::lang.java.metrics %}
{% jdoc_nspace :jast java::lang.java.ast %}
{% jdoc_nspace :jrule java::lang.java.rule %}

{% include note.html content="TODO All that should be written in the Javadocs,
not sure we even need a doc page. Would be simpler to maintain too" %}
{% include warning.html content="WIP lots of stuff missing" %}

This page covers the specifics of writing a rule in Java. The basic development
process is very similar to the process for XPath rules, which is described in
[Your First Rule](pmd_userdocs_extending_your_first_rule.html#rule-development-process).

Basically, you open the designer, look at the structure of the AST, and refine
your rule as you add test cases.

In this page we'll talk about rules for the Java language, but the process is
very similar for other languages.


## Basics

To write a rule in Java you'll have to:
 1. write a Java class that implements the interface {% jdoc core::Rule %}. Each
language implementation provides a base rule class to ease your pain,
e.g. {% jdoc jrule::AbstractJavaRule %}.
 2. compile this class, linking it to PMD APIs (eg using PMD as a maven dependency)
 3. bundle this into a JAR and add it to the execution classpath of PMD
 4. declare the rule in your ruleset XML

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

        if (node.getType() == short.class) {
            // reports a violation at the position of the node
            // the "data" parameter is a context object handed to by your rule
            // the message for the violation is the message defined in the rule declaration XML element
            addViolation(data, node);
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
independent way is to override the method `start` of the rule.
The start method is called exactly once per file.

<!-- We don't support language-independent rules anyway... -->



## Rule lifecycle reference

### Construction

Exactly once:

1. The rule's no-arg constructor is called when loading the ruleset.
The rule's constructor must define:
  * [Rulechain visits](#economic-traversal-the-rulechain)
  * [Property descriptors](pmd_userdocs_extending_defining_properties.html#for-java-rules)
2. If the rule was included in the ruleset as a rule reference,
some properties [may be overridden](pmd_userdocs_configuring_rules.html#rule-properties).
If an overridden property is unknown, an error is reported.
3. Misconfigured rules are removed from the ruleset

### Execution

For each thread, a deep copy of the rule is created. Each thread is given
a different set of files to analyse. Then, for each such file, for each
rule copy:

3. {% jdoc core::Rule#start(core::RuleContext) %} is called once, before parsing
4. {% jdoc core::Rule#apply(java.util.List,core::RuleContext) %} is called with the root
of the AST. That method performs the AST traversal that ultimately calls visit methods.
It's not called for RuleChain rules.
5. {% jdoc core::Rule#end(core::RuleContext) %} is called when the rule is done processing
the file
