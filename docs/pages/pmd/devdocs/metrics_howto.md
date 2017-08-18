---
title:  Using code metrics in custom rules
tags: [customizing]
summary: "PMD was recently enhanced with the ability to compute code metrics on Java and Apex source (the so-called
Metrics Framework). This framework provides developers with a straightforward interface to use code metrics in their
rules, and to extend the framework with their own custom metrics."
last_updated: July 20, 2017
permalink: pmd_devdocs_metrics_howto.html
author: Clément Fournier <clement.fournier76@gmail.com>
---
# Using code metrics in custom rules

## Using the metrics framework

{%include note.html content="Using the metrics framework is for now restricted to Java rules (with plans to support
XPath rules later)." %}

To use the metrics framework in a custom rule, the first thing to do would be to enable metrics by adding the
`metrics="true"` attribute to your rule's XML element.

{%include note.html content="The following explains how to use the Java metrics framework. The Apex framework
differs only by the name of its classes." %}

In PMD's Metrics framework, a metric is an operation that can be carried out on nodes of a certain type and produces
a numeric result. In the Java framework, metrics can be computed on operation declaration nodes (constructor and
method declaration), and type declaration nodes (class, interface, enum, and annotation declarations). A metric
object in the framework can only handle either types or operations, but not both.

The framework provides a library of already implemented metrics. These metrics are referenced by `MetricKey` objects,
which are listed in two public enums: `JavaClassMetricKey` and `JavaOperationMetricKey`. Metric keys wrap a metric, and
know which type of node their metric can be computed on. That way, you cannot compute an operation metric on a class
declaration node. Metrics that can be computed on operation and type declarations (e.g. NCSS) have one metric key in
each enum.

The static façade class `JavaMetrics` is the only entry point to compute metrics in the Java framework.
This class provides the method `get` and its overloads. The following sections describes the interface of this class.

### Basic usage

The simplest overloads of `JavaMetrics.get` take two parameters: **a `MetricKey` and a node of the corresponding type.**
Say you want to write a rule to report methods that have a high cyclomatic complexity. In your rule's visitor, you
can get the value of Cyclo for a method node like so:
```java
public Object visit(ASTMethodDeclaration node, Object data) {
  int cyclo = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, node);
  if (cyclo > 10) {
    // add violation
  }
  return data;
}
```

The same goes for class metrics: you select one among `JavaClassMetricKey`'s constants and pass it along with the node
 to `JavaMetrics.get`.

### Capability checking

Metrics are not necessarily computable on any node of the type they handle. For example, Cyclo cannot be computed on
abstract methods. Metric keys provides a `supports(Node)` boolean method to find out if the metric can be computed on
the specified node. **If the metric cannot be computed on the given node, `JavaMetrics.get` will return `Double.NaN` .**
If you're concerned about that, you can condition your call on whether the node is supported or not:
```java
public Object visit(ASTMethodDeclaration node, Object data) {
  if (JavaOperationMetricKey.CYCLO.supports(node)) {
    int cyclo = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, node);
    if (cyclo > 10) {
      // add violation
    }
    return data;
  }
}
```

### Metric versions

{%include important.html content="Metric versions are about to be revamped into options that can be combined
together." %}


### Result options

## Writing custom metrics

{%include warning.html content="WIP" %}
