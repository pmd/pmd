---
title: Adding support for metrics to a language
short_title: Implement a metrics framework
tags: [customizing]
summary: "PMD's Java module has an extensive framework for the calculation of metrics, which allows rule developers
to implement and use new code metrics very simply. Most of the functionality of this framework is abstracted in such
a way that any PMD supported language can implement such a framework without too much trouble. Here's how."
last_updated: August 2017
permalink: pmd_devdocs_adding_metrics_support_to_language.html
author: Clément Fournier <clement.fournier76@gmail.com>
---

{% include warning.html content="WIP, unstable API" %}

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

{% include important.html content="The following will be moved when multifile analysis and metrics are separated" %}
<!-- We should probably create a dedicated page about the architecture of multifile analysis/ signature matching and how
 to implement that -->

At the very least, a metrics framework has those two components and is just a convenient way to compute and memoize
metrics on a single file. Yet, one of the goals of the metrics framework is to allow for **multi-file analysis**, which
make it possible, for instance, to compute the coupling between two classes. This feature uses two major
components:
* A **project mirror**. This data structure that stores info about all classes and operations (and other relevant
  entities, such as fields, packages, etc.) of the analysed project. This is implemented by `PackageStats` in the Java
  framework. The role of this structure is to make info about other files available to rules. It's filled by a visitor before rules apply.

  The information stored in this data structure that's accessible to metrics is mainly comprised of method and field
  signatures (e.g. `JavaOperationSignature`), which describes concisely the characteristics of the method or field
  (roughly, its modifiers).

* Some kind of method and field **usage resolution**, i.e. some way to find the fully qualified name of a method from a
  method call expression node. This is the trickiest part to implement. In Java it depends on type resolution.

### Abstraction layer

As you may have seen, most of the functionality of the first two components are abstracted into `pmd-core`. This
allows us to implement new metrics frameworks quite quickly. These abstract components are parameterized by the
node types of the class and operation AST nodes. Moreover, it makes the external behaviour of the framework is very
stable across languages, yet each component can easily be customized by adding methods or overriding existing ones.

The signature matching aspect is framed by generic interfaces, but it can't really be abstracted more
than that. For instance, the project mirror is very language specific. Java's implementation uses the natural structure
provided by the language's package system to structure the project's content. Apex, on the other, has no package
system and thus can't use the same mechanism. That explains why the interfaces framing the project mirror are very
loose. Their main goal is to provide type safety through generics.

Moreover, usage resolution depends on the availability of type resolution for the given language, which is only implemented in
Java. For these reasons, signature matching is considered an optional feature of the metrics framework. But despite
this limitation, signature matching still provides a elegant way to find information about the class we're in. This
feature requires no usage resolution and can be used to implement sophisticated metrics, that already give access to
detection strategies.

## Implementation of a new framework

### 1. Groundwork

* Create a class implementing `QualifiedName`. This implementation must be tailored to the target language so
  that it can indentify unambiguously any class and operation in the analysed project. You
  must implement `equals`, `hashCode` and `toString`.
  [Example](https://github.com/pmd/pmd/blob/52d78d2fa97913cf73814d0307a1c1ae6125a437/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/ast/JavaQualifiedName.java)
* Determine the AST nodes that correspond to class and method declaration in your language. These types are
  referred hereafter as `T` and `O`, respectively. Both these types must implement the interface `QualifiableNode`,
  which means they must expose a `getQualifiedName` method to give access to their qualified name.

### 2. Implement and wire the project memoizer
* Create a class extending `BasicProjectMemoizer<T, O>`. There's no abstract functionality to implement.
  [Example](https://github.com/pmd/pmd/blob/52d78d2fa97913cf73814d0307a1c1ae6125a437/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaProjectMemoizer.java)
* Create an AST visitor that fills the project memoizer with memoizers. For that, you use `BasicProjectMemoizer`'s
  `addClassMemoizer` and `addOperationMemoizer` methods with a qualified name.
  [Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaMetricsVisitor.java)
* Create a façade class for your visitor. This class extends a `*ParserVisitorAdapter` class and only overrides the
  `initializeWith(Node)` method. It's supposed to make your real visitor accept the node in parameter.
  [Example](https://github.com/pmd/pmd/blob/52d78d2fa97913cf73814d0307a1c1ae6125a437/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaMetricsVisitorFacade.java)
* Override the `getMetricsVisitorFacade()` method in your language's handler (e.g. `ApexHandler`). This method gives
  back a `VisitorStarter` which initializes your façade with a `Node`.
  [Example](https://github.com/pmd/pmd/blob/52d78d2fa97913cf73814d0307a1c1ae6125a437/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/AbstractJavaHandler.java#L100-L108)
* Your project memoizer should now get filled when the `metrics` attribute is set to `true` in the rule XML.

### 3. Implement the façade
* Create a class extending `AbstractMetricsComputer<T, O>`. This object will be responsible for calculating metrics
  given a memoizer, a node and info about the metric. Typically, this object is stateless so you might as well make it
  a singleton.
  [Example](https://github.com/pmd/pmd/blob/52d78d2fa97913cf73814d0307a1c1ae6125a437/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaMetricsComputer.java)
* Create a class extending `AbstractMetricsFacade<T, O>`. This class needs a reference to your `ProjectMemoizer` and
  your `MetricsComputer`. It backs the real end user façade, and handles user provided parameters before delegating to
  your `MetricsComputer`.
  [Example](https://github.com/pmd/pmd/blob/52d78d2fa97913cf73814d0307a1c1ae6125a437/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaMetricsFacade.java)
* Create the static façade of your framework. This one has an instance of your `MetricsFaçade` object and delegates
  static methods to that instance.
  [Example](https://github.com/pmd/pmd/blob/52d78d2fa97913cf73814d0307a1c1ae6125a437/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.java)
* Create classes `AbstractOperationMetric` and `AbstractClassMetric`. These must implement `Metric<T>` and
  `Metric<O>`, respectively. They typically provide defaults for the `supports` method of each metric.
  [Example](https://github.com/pmd/pmd/blob/52d78d2fa97913cf73814d0307a1c1ae6125a437/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/impl/AbstractJavaOperationMetric.java)
* Create enums `ClassMetricKey` and `OperationMetricKey`. These must implement `MetricKey<T>` and `MetricKey<O>`. The
  enums list all available metric keys for your language.
  [Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/api/JavaOperationMetricKey.java)
* Create metrics by extending your base classes, reference them in your enums, and you can start using them with your
  façade!

{% include important.html content="The following section will be moved when multifile analysis and metrics are separated" %}

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
* Typically, the project mirror stores the signatures, so you have to implement it in a way that makes it possible to
 associate a signature with the qualified name of its node.
* If you want to implement signature matching, create an `AbstractMetric` class, which gives access to a
`SignatureMatcher` to your metrics. Typically, your implementation of `ProjectMirror` implements a
custom `SignatureMatcher` interface, and your façade can give back its instance of the project mirror.
