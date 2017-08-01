---
title: Adding support for metrics to a language
short_title: Implement a metrics framework
tags: [customizing]
summary: "PMD's Java module has an extensive framework for the calculation of metrics, which allows rule developers 
to implement and use new code metrics very simply. Most of the functionality of this framework is abstracted in such 
a way that any PMD supported language can implement such a framework without too much trouble. Here's how."
last_updated: August 2017
sidebar: pmd_sidebar
permalink: pmd_devdocs_adding_metrics_support_to_language.html
folder: pmd/devdocs
---

{% include warning.html content="WIP" %}

## Internal architecture of the metrics framework

### Overview of the Java framework

The framework has several subsystems, the two most easily identifiable being:
* The project mirror (`PackageStats`). This data structure gathers information about the classes, methods and fields of 
  the analysed project. It allows metrics to know about classes outside the current one, the files being processed one
  by one. It's filled by a visitor before rules apply.

  The contents of the structure are indexed with fully qualified names (`JavaQualifiedName`), which must identify 
  unambiguously classes and methods. The information stored in this data structure that's accessible to metrics is 
  mainly comprised of method and field signatures (e.g. `JavaOperationSignature`), which describes concisely the 
  characteristics of the method or field (roughly, its modifiers).

  The project mirror is also responsible for the memoisation of metrics. When a metric is computed, it's stored back 
  in this structure and can be reused later. This reduces the overhead on the calculation of e.g. aggregate results 
  (`ResultOption` calculations).
 
* The façade. The static end-user façade (`JavaMetrics`) is backed by an instance of a `JavaMetricsFaçade`. This 
  allows us to abstract the functionality of the façade into `pmd-core` for other frameworks to use. The façade 
  instance contains a project mirror, representing the analysed project, and a metrics computer 
  (`JavaMetricsComputer`). It's this last object which really computes the metric and stores back its result in the 
  project mirror, while the façade only handles parameters.

Metrics (`Metric<N>`) plug in to this static system and only provide behaviour that's executed by the metrics computer. 
Internally, metric keys (`MetricKey<N>`) are parameterized to their version (`MetricVersion`) to index memoisation maps 
(see `ParameterizedMetricKey<N>`). This allows us to memoise several versions of the same metric without conflict. 

### Abstraction layer

As you may have seen, most of the functionality of the façade components has been abstracted into `pmd-core`. This 
allows us to implement new metrics frameworks quite quickly. These abstract components are parameterized by the 
node types of the class and operation AST nodes. 

The rest of the framework is framed by generic interfaces, but it can't really be abstracted more than that. For 
instance, the project mirror is very language specific. Java's implementation uses the natural structure provided by 
the language's package system to structure the project's content. Apex, on the other, has no package system and thus 
can't use the same mechanism. That explains why the interfaces framing the project mirror are very loose. Their main 
goal is to provide type safety through generics.

Signature matching is another feature that couldn't be abstracted. For now, usage resolution depends on the availability
 of type resolution for the given language, which is only implemented in java. We can however match signatures on the
  class' own methods or nested classes, which offers limited interest, but may be useful. <!-- TODO:cf that's for data class -->

Despite these limitations, once the project mirror is implemented, it's very straightforward to get a working 
framework. Additionnally, the external behaviour of the framework is very stable across languages, yet each component
 can easily be customized by adding methods or overriding existing ones.

## Implementation of a new framework

### 1. Groundwork

* Create a class implementing `QualifiedName`. This implementation must be tailored to the target language so 
that it can indentify unambiguously any class and operation in the analysed project (see JavaQualifiedName).
* Determine the AST nodes that correspond to class and method declaration in your language. These types are 
referred hereafter as `T` and `O`, respectively. Both these types must implement the interface `QualifiableNode`, which 
means they must provide a `getQualifiedName` method to give access to their qualified name.

### 2. Implement the project mirror
* Create a class implementing `Memoizer<T>` and one `Memoizer<O>`. An abstract base class is available. Instances of 
these classes each represent a class or operation, respectively. They are used to store the results of metrics that 
are already computed. 
* Create a class implementing `ProjectMirror<T, O>`. This class will store the memoizers for all the classes and 
interfaces of the analysed project. This class must be able to fetch and return a memoizer given the qualified name 
of the resource it represents. As it stores the memoizers, it's a good idea to implement some signature matching 
utilities in this class. What's signature matching? (See write custom metrics -- TODO)

### 3. Implement the façade
* Create a class extending `AbstractMetricsComputer<T, O>`. This object will be responsible for calculating metrics 
given a memoizer, a node and info about the metric. Typically, this object is stateless so you might as well make it 
a singleton.
* Create a class extending `AbstractMetricsFacade<T, O>`. This class needs a reference to your `ProjectMirror` and 
your `MetricsComputer`. It backs the real end user façade, and handles user provided parameters before delegating to 
your `MetricsComputer`.
* Create the static façade of your framework. This one has an instance of your `MetricsFaçade` object and delegates 
static methods to that instance.
* Create classes `AbstractOperationMetric` and `AbstractClassMetric`. These must implement `Metric<T>` and 
`Metric<O>`, respectively. They typically provide defaults for the `supports` method of each metric. 
* Create enums `ClassMetricKey` and `OperationMetricKey`. These must implement `MetricKey<T>` and `MetricKey<O>`. The
 enums list all available metric keys for your language.
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
* Typically, the project mirror stores the signatures, so you have to implement it in a way that makes it possible to
 associate a signature with the qualified name of its node. 

{% include important.html 
content="Writing this, it seems dumb. If signature matching is optional, it should not require reimplementing
         the project mirror. We need to work on dissociating the two. The project mirror would be reduce to a
         collection of memoizers, which could be abstracted into pmd-core." %}


* If you want to implement signature matching, create an `AbstractMetric` class, which gives access to a 
`SignatureMatcher` to your metrics. Typically, your implementation of `ProjectMirror` implements a 
custom `SignatureMatcher` interface, and your façade can give back its instance of the project mirror.

