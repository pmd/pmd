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

{% include note.html content="TODO All that should be written in the Javadocs, 
not sure we even need a doc page. Would be simpler to maintain too" %}
{% include warning.html content="WIP lots of stuff missing" %}

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

