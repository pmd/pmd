---
title:  Using and defining code metrics for custom rules
tags: [extending, userdocs, metrics]
summary: "Since version 6.0.0, PMD is enhanced with the ability to compute code metrics on Java and Apex source (the so-called
Metrics Framework). This framework provides developers with a straightforward interface to use code metrics in their
rules, and to extend the framework with their own custom metrics."
last_updated: December 18, 2017
permalink: pmd_userdocs_extending_metrics_howto.html
author: Clément Fournier <clement.fournier76@gmail.com>
---

{% jdoc_nspace :coremx core::lang.metrics %}
{% jdoc_nspace :coreast core::lang.ast %}
{% jdoc_nspace :jmx java::lang.java.metrics %}
{% jdoc_nspace :jast java::lang.java.ast %}


## Using the metrics framework

{%include note.html content="The following explains how to use the Java metrics framework. The Apex framework 
differs only by the name of its classes." %}

In PMD's Metrics framework, a metric is an operation that can be carried out on nodes of a certain type and produces
a numeric result. In the Java framework, metrics can be computed on operation declaration nodes (constructor and
method declaration), and type declaration nodes (class, interface, enum, and annotation declarations). A metric
object in the framework can only handle either types or operations, but not both.

PMD ships with a library of already implemented metrics. These metrics are referenced by {% jdoc coremx::MetricKey %} objects,
which are listed in two public enums: {% jdoc jmx::api.JavaClassMetricKey %} and {% jdoc jmx::api.JavaOperationMetricKey %}.
Metric keys wrap a metric, and know which type of node their metric can be computed on. That way, you cannot compute an operation metric on a class
declaration node. Metrics that can be computed on both operation and type declarations (e.g. NCSS) have one metric key in
each enum.

## For XPath rules

XPath rules can compute metrics using the `metric` function. This function takes a single **string argument**,
which is the name of the metric key as defined in  `JavaClassMetricKey` or `JavaOperationMetricKey`. The metric
 will be **computed on the context node**.

The function will throw an exception in the following cases:
* The context node is neither an instance of {% jdoc jast::ASTAnyTypeDeclaration %} or {% jdoc jast::MethodLikeNode %}, that is,
it's not one of {% jdoc jast::ASTClassOrInterfaceDeclaration %}, {% jdoc jast::ASTEnumDeclaration},
{% jdoc jast::ASTAnnotationDeclaration %}, {% jdoc jast::ASTMethodDeclaration %},
{% jdoc jast::ASTConstructorDeclaration %}, or {% jdoc jast::ASTLambdaExpression %}.
* The metric key does not exist (the name is case insensitive) or is not defined for the type of the context node.

{%include note.html
  content="More advanced features of the API are not accessible yet, but may be supported in the future.
           The API is thus subject to change." %}

### Examples

* `//ClassOrInterfaceDeclaration[metric('NCSS') > 200]`
* `//MethodDeclaration[metric('CYCLO') > 10 and metric('NCSS') > 20]`
* `//ClassOrInterfaceDeclaration[metric('CYCLO') > 50]`: IllegalArgumentException!
  CYCLO's only defined for methods and constructors.

## For Java Rules

The static façade class {% jdoc jmx::JavaMetrics %} is the single entry point to compute metrics in the Java framework.

This class provides the method `get` and its overloads. The following sections describes the interface of this class.

### Basic usage

The simplest overloads of `JavaMetrics.get` take two parameters: **a `MetricKey` and a node of the corresponding type.**
Say you want to write a rule to report methods that have a high cyclomatic complexity. In your rule's visitor, you
can get the value of Cyclo for a method node like so:
```java
public Object visit(ASTMethodDeclaration method, Object data) {
  int cyclo = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, method);
  if (cyclo > 10) {
    // add violation
  }
  return data;
}
```

The same goes for class metrics: you select one among `JavaClassMetricKey`'s constants and pass it along with the node
to `JavaMetrics.get`.

{%include tip.html
           content="A specific base rule class (`AbstractJavaMetricsRule`) exists
           to e.g. check constructors and method nodes completely alike. This comes
           in handy for metrics, as they usually don't make the distinction" %}

### Capability checking

Metrics are not necessarily computable on any node of the type they handle. For example, Cyclo cannot be computed on
abstract methods. Metric keys provides a {% jdoc !a!coremx::MetricKey#supports(coreast::Node) %} boolean method
to find out if the metric can be computed on
the specified node. **If the metric cannot be computed on the given node, `JavaMetrics.get` will return `Double.NaN` .**
If you're concerned about that, you can condition your call on whether the node is supported or not:
```java
public Object visit(ASTMethodDeclaration method, Object data) {
  if (JavaOperationMetricKey.CYCLO.supports(node)) {
    int cyclo = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, method);
    if (cyclo > 10) {
      // add violation
    }
    return data;
  }
}
```

### Metric options

Some metrics define options that can be used to slightly modify the computation. You'll typically see these options
gathered inside an enum in the implementation class of the metric, for example `CycloMetric.CycloOption`. They're
also documented on the [index of metrics](pmd_java_metrics_index.html).

To use options with a metric, you must first bundle them into a {% jdoc coremx::MetricOptions %} object. `MetricOptions` provides the
utility method `ofOptions` to get a `MetricOptions` bundle from a collection or with varargs parameters. You can then
pass this bundle as a parameter to `JavaMetrics.get`:
```java
public Object visit(ASTMethodDeclaration method, Object data) {
  int cyclo = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, method,
                                    MetricOptions.ofOptions(CycloOptions.IGNORE_BOOLEAN_PATHS));
  if (cyclo > 10) {
      // add violation
  }
    return data;
}
```

The version of `MetricOptions.ofOptions` using a collection is useful when you're building a `MetricOptions` from eg
the value of an `EnumeratedMultiProperty`, which gives users control of the options they use. See
[CyclomaticComplexityRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/CyclomaticComplexityRule.java#L35)
for an example usage.

### Result options

The Metrics API also gives you the possibility to aggregate the result of an operation metric on all operations of a
class very simply. You can for example get the highest value of the metric over a class that way:
```java
public Object visit(ASTClassOrInterfaceDeclaration clazz, Object data) {
  int highest = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, clazz,
                                      ResultOption.HIGHEST);
  if (highest > 10) {
      // add violation
  }
    return data;
}
```

Notice that **we use an operation metric and a class node**. The `ResultOption` parameter controls what result will be
computed: you can choose among `HIGHEST`, `SUM` and `AVERAGE`. You can use metric options together with a result
option too.

### Complete use case

The following is a sample code for a rule reporting methods with a cyclomatic
complexity over 10 and classes with a total cyclo over 50. A metric option can be
user-configured with a rule property. More complete examples can be found in
[CyclomaticComplexityRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/CyclomaticComplexityRule.java#L35),
[NcssCountRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/NcssCountRule.java#L30),
or [GodClassRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/GodClassRule.java#L24).


```java
public class CycloRule extends AbstractJavaMetricsRule {

  public static final BooleanProperty COUNT_BOOLEAN_PATHS
      = BooleanProperty.named("countBooleanPaths")
                       .desc("Count boolean paths")
                       .defaultValue(true).build();

  private static final MetricOptions options;

  public CycloRule() {
    definePropertyDescriptor(COUNT_BOOLEAN_PATHS);
  }

  @Override
  public Object visit(ASTCompilationUnit node, Object data) {
    options = getProperty(COUNT_BOOLEAN_PATHS)
              ? MetricOptions.ofOptions(CycloOptions.IGNORE_BOOLEAN_PATHS)
              : MetricOptions.emptyOptions();
  }

  @Override
  public Object visit(ASTAnyTypeDeclaration clazz, Object data) {
    int total = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, clazz,
                                      options, ResultOption.SUM);

    if (total > 50) {
     // add violation
    }

    return data;
  }

  @Override
  public Object visit(ASTMethodDeclaration method, Object data) {
    int cyclo = (int) JavaMetrics.get(JavaOperationMetricKey.CYCLO, method,
                                      options);
    if (cyclo > 10) { // this is safe if the node is not supported, as (Double.NaN > 10) == false
      // add violation
    }
    return data;
  }
}
```

## Available metrics

There are already many metrics ready to use. We maintain the following documentation
pages to describe them all, including their usage and options:
* [Java metrics](pmd_java_metrics_index.html)
* [Apex metrics](pmd_apex_metrics_index.html)


## Writing custom metrics

You can use the framework to customize the existing metrics at will, or define
new ones quite easily. Here's some info to get you started. Again, the examples are for
the Java framework but it's symmetrical in the Apex framework.

### The really short guide

1. Determine whether your metric is an operation metric or a class metric and
   **extend the correct base class** (`AbstractJavaClassMetric` or
   `AbstractJavaOperationMetric`)
1. You're immediately prompted by your IDE to **implement the `computeFor` method**.
   This method takes a node of the type you want to handle, a bundle of options,
   and returns the result of the metric.
1. Optionally specify a predicate to check if a node can be handled by **overriding
   the `supports` method**.
1. Optionally define options (implementing [`MetricOption`](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/lang/metrics/MetricOption.java))
   and handle them as you see fit in your `computeFor` method
1. **Create a metric key** using `MetricKeyUtil`'s `of` method, specifying a name
   for your metric and an instance of your metric. You're done and can use your
   metric key as if it were a standard one.

### Best practices

* **Metrics should be stateless**. In any case, instances of the same metric class
  are considered `equals`. The same instance of your metric will be used to
  compute the metric on the AST of different nodes so it should really be
  "functionnally pure". That rule also makes you keep it simple and understandable
  which is nice.
* **Implementation patterns:** You can implement your `computeFor` method as you
  like it. But most metrics in our library are implemented following a few
  patterns you may want to look at:
  * *Visitor metrics:* Those metrics use one or more AST visitor to compute their
    value. That's especially good to implement metrics that count some kind of node,
    e.g. [NPath complexity](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/impl/NpathMetric.java)
    or [NCSS](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/impl/NcssMetric.java).
    Additionnally, it makes your metric more easily generalisable to other node types.

  * *Signature matching metrics:* That's even more straightforward when you want
    to count the number of methods or fields that match a specific signature, e.g.
    public static final fields. Basically a signature is an object that describes
    a field or method, with info about its modifers and other node-specific info.
     `AbstractJavaClassMetric` has a few methods that allow you to count signatures
      directly, see e.g. the metrics [NOPA](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/impl/NopaMetric.java)
      and [WOC](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/impl/WocMetric.java).


### Capability checking

You may have noticed that when you extend e.g. `AbstractJavaClassMetric`, the
`computeFor` method you're prompted to implement takes a node of type
`ASTAnyTypeDeclaration` as a parameter. That's not a concrete node type, but
an interface, implemented by several concrete node types. Basically that's done
so that class metrics are given the ability to be computed on any type
declaration, and operation metrics on constructors and methods. Here are the
concrete node types you can target with class and operation metrics, by language:


Language   | Java | Apex |
-----------|------|------|
Operation declaration|`ASTMethodOrConstructorDeclaration`<br/>>: `ASTMethodDeclaration`, `ASTConstructorDeclaration`| `ASTMethod`*
Type declaration|`ASTAnyTypeDeclaration` >: `ASTEnumDeclaration`, <br> `ASTAnnotationDeclaration`, `ASTClassOrInterfaceDeclaration`| `ASTUserClassOrInterface` >: `ASTUserClass`, `ASTUserInterface`

*Apex method metrics are also applied to triggers by default (see [#771](https://github.com/pmd/pmd/pull/771)). Finer capability checking is not available out of the box for now.

What if you don't want such a generalisation? The `supports` method lets you
define a predicate to check that the node is supported by your metric. For example,
if your metric can only be computed on classes, you may override the default behaviour
like so:
```java
@Override
public boolean supports(ASTAnyTypeDeclaration node) {
  return node.getTypeKind() == TypeKind.CLASS;
}
```

{%include tip.html
  content="You can be sure that if your `supports` method returns `false` on a node, then
           that node will never be passed as a parameter to `computeFor`. That allows you
           to write your `computeFor` method without worrying about unsupported nodes." %}

The `supports` method already has a default implementation in the abstract base
classes. Here's the default behaviour by language and type of metric:

Language   | Java | Apex |
-----------|------|------|
Operation metrics| supports constructors and non abstract methods| supports any non abstract method (including triggers), except `<init>`, `<clinit>`, and `clone`
Type declaration|supports classes and enums|supports classes

