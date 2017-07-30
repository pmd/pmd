---
title: How to implement a metrics framework for an existing language
short_title: Implement a metrics framework
tags: [customizing]
summary: "PMD's Java module has an extensive framework for the calculation of metrics, which allows rule developers 
to implement and use new code metrics very simply. Most of the functionality of this framework is abstracted in such 
a way that any PMD supported language can implement such a framework without too much trouble. Here's how."
last_updated: July 3, 2016
sidebar: pmd_sidebar
permalink: pmd_devdocs_adding_new_cpd_language.html
folder: pmd/devdocs
---

## Basic steps
* Implement the interface `QualifiedName` in a class. This implementation must be tailored to the target language so 
that it can indentify unambiguously any class and operation in the analysed project (see JavaQualifiedName).
* Determine the AST nodes that correspond to class and method declaration in your language. These types are 
referred hereafter as `T` and `O`, respectively. Both these types must implement the interface `QualifiableNode`, which 
means they must provide a `getQualifiedName` method to give access to their qualified name.
* Implement the interface `Signature<O>`, parameterized with the type of the method AST nodes. Method signatures 
describe basic information about a method, which typically includes most of the modifiers they declare (eg 
visibility, abstract or virtual, etc.). It's up to you to define the right level of detail, depending on the accuracy
 of the pattern matching required.
* Make type `O` implement `SignedNode<O>`. This makes the node capable of giving its signature.
* Create a class implementing `Memoizer<T>` and one `Memoizer<O>`. An abstract base class is available. Instances of 
these classes each represent a class or operation, respectively. They are used to store the results of metrics that 
are already computed. 
* Create a class implementing `ProjectMirror<T, O>`. This class will store the memoizers for all the classes and 
interfaces of the analysed project. This class must be able to fetch and return a memoizer given the qualified name 
of the resource it represents. As it stores the memoizers, it's a good idea to implement some signature matching 
utilities in this class. What's signature matching? (See write custom metrics -- TODO)
* Create a class extending `AbstractMetricsComputer<T, O>`. This object will be responsible for calculating metrics 
given a memoizer, a node and info about the metric. Typically, this object is stateless so you might as well make it 
a singleton.
* Create a class extending `AbstractMetricsFacade<T, O>`. This class needs a reference to your `ProjectMirror` and 
your `MetricsComputer`. It backs the real end user façade, and handles user provided parameters before delegating to 
your `MetricsComputer`.
* Create the static façade of your framework. This one has an instance of your `MetricsFaçade` object and delegates 
static methods to that instance.
* If you want to implement signature matching, create an `AbstractMetric` class, which gives access to a 
`SignatureMatcher` to your metrics. Typically, your implementation of `ProjectMirror` implements a 
custom `SignatureMatcher` interface, and your façade can give back its instance of the project mirror.
* Create classes `AbstractOperationMetric` and `AbstractClassMetric`. These must implement `Metric<T>` and 
`Metric<O>`, respectively. They typically provide defaults for the `supports` method of each metric. 
* Create enums `ClassMetricKey` and `OperationMetricKey`. These must implement `MetricKey<T>` and `MetricKey<O>`. The
 enums list all available metric keys for your language.
* Create metrics by extending your base classes, reference them in your enums, and you can start using them with your
 façade!