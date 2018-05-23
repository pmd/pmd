---
title: Adding support for metrics to a language
short_title: Implement a metrics framework
tags: [devdocs, extending, metrics]
summary: "PMD's Java module has an extensive framework for the calculation of metrics, which allows rule developers
to implement and use new code metrics very simply. Most of the functionality of this framework is abstracted in such
a way that any PMD supported language can implement such a framework without too much trouble. Here's how."
last_updated: December 2017
permalink: pmd_devdocs_major_adding_new_metrics_framework.html
author: Clément Fournier <clement.fournier76@gmail.com>
---


## Internal architecture of the metrics framework

### Overview of the Java framework

The framework has several subsystems, the two most easily identifiable being:
* A **project memoizer** (`ProjectMemoizer`). When a metric is computed, it's stored back in this structure and can be
reused later. This
 reduces the overhead on the calculation of e.g. aggregate results (`ResultOption` calculations). The contents of
 this data structure are indexed with fully qualified names (`JavaQualifiedName`), which must identify unambiguously
 classes and methods.

* The **façade**. The static end-user façade (`JavaMetrics`) is backed by an instance of a `JavaMetricsFaçade`. This
  allows us to abstract the functionality of the façade into `pmd-core` for other frameworks to use. The façade
  instance contains a project memoizer for the analysed project, and a metrics computer
  (`JavaMetricsComputer`). It's this last object which really computes the metric and stores back its result in the
  project mirror, while the façade only handles parameters.

Metrics (`Metric<N>`) plug in to this static system and only provide behaviour that's executed by the metrics computer.
Internally, metric keys (`MetricKey<N>`) are parameterized with their version (`MetricVersion`) to index memoisation
maps (see `ParameterizedMetricKey<N>`). This allows us to memoise several versions of the same metric without conflict.

At the very least, a metrics framework has those two components and is just a convenient way to compute and memoize
metrics on a single file. The expressive power of metrics can be improved by implementing *signature matching* capabilities,
which allows a metric to count signatures matching a specific pattern (a mask) over a whole class. This was originally
designed to work across files, given a working usage resolution. However, making that work with incremental analysis is
harder than it looks, and has been rescheduled to another project.


### Abstraction layer

As you may have seen, most of the functionality of the first two components are abstracted into `pmd-core`. This
allows us to implement new metrics frameworks quite quickly. These abstract components are parameterized by the
node types of the class and operation AST nodes. Moreover, it makes the external behaviour of the framework very
stable across languages, yet each component can easily be customized by adding methods or overriding existing ones.

The signature matching aspect is framed by generic interfaces, but it can't really be abstracted more
than that. The info given in the signatures is usually very language specific, as it includes info about e.g.
visibility modifiers. So more work is required to implement that, but it can already be used to implement
sophisticated metrics, that already give access to detection strategies.

## Implementation of a new framework

### 1. Groundwork

* Create a class implementing `QualifiedName`. This implementation must be tailored to the target language so
  that it can indentify unambiguously any class and operation in the analysed project. You
  must implement `equals`, `hashCode` and `toString`.
  [Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/ast/JavaQualifiedName.java)
* Determine the AST nodes that correspond to class and method declaration in your language. These types are
  referred hereafter as `T` and `O`, respectively. Both these types must implement the interface `QualifiableNode`,
  which means they must expose a `getQualifiedName` method to give access to their qualified name.

### 2. Implement the façade
* Create a class extending `AbstractMetricsComputer<T, O>`. This object will be responsible for calculating metrics
  given a memoizer, a node and info about the metric. Typically, this object is stateless so you might as well make it
  a singleton.
* Create a class extending `BasicProjectMemoizer<T, O>`. There's no abstract functionality to implement.
  [Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaProjectMemoizer.java)
  [Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaMetricsComputer.java)
* Create a class extending `AbstractMetricsFacade<T, O>`. This class needs a reference to your `ProjectMemoizer` and
  your `MetricsComputer`. It backs the real end user façade, and handles user provided parameters before delegating to
  your `MetricsComputer`.
  [Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaMetricsFacade.java)
* Create the static façade of your framework. This one has an instance of your `MetricsFaçade` object and delegates
  static methods to that instance.
  [Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.java)
* Create classes `AbstractOperationMetric` and `AbstractClassMetric`. These must implement `Metric<T>` and
  `Metric<O>`, respectively. They typically provide defaults for the `supports` method of each metric.
  [Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/impl/AbstractJavaOperationMetric.java)
* Create enums `ClassMetricKey` and `OperationMetricKey`. These must implement `MetricKey<T>` and `MetricKey<O>`. The
  enums list all available metric keys for your language.
  [Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/api/JavaOperationMetricKey.java)
* Create metrics by extending your base classes, reference them in your enums, and you can start using them with your
  façade!

### Optional: Signature matching

You can match the signature of anything: method, field, class, package... It depends on what's useful for you.
Suppose you want to be able to match signatures for nodes of type `N`. What you have to do then is the following:

* Create a class implementing the interface `Signature<N>`. Signatures describe basic information about the node,
which typically includes most of the modifiers they declare (eg visibility, abstract or virtual, etc.).
It's up to you to define the right level of detail, depending on the accuracy of the pattern matching required.
* Make type `N` implement `SignedNode<N>`. This makes the node capable of giving its signature. Factory methods to
build a `Signature<N>` from a `N` are a good idea.
* Create signature masks. A mask is an object that matches some signatures based on their features. For example, with
 the Java framework, you can build a `JavaOperationSigMask` that matches all method signatures with visibility
 `public`. A sigmask implements `SigMask<S>`, where `S` is the type of signature your mask handles.
* Create utility methods in your abstract class metric class to count signatures matching a specific mask.
[Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/AbstractJavaClassMetric.java#L50)

