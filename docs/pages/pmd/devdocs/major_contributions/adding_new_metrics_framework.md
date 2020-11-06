---
title: Adding support for metrics to a language
short_title: Implement a metrics framework
tags: [devdocs, extending, metrics]
summary: "PMD's Java module has an extensive framework for the calculation of metrics, which allows rule developers
to implement and use new code metrics very simply. Most of the functionality of this framework is abstracted in such
a way that any PMD supported language can implement such a framework without too much trouble. Here's how."
last_updated: February 2020
permalink: pmd_devdocs_major_adding_new_metrics_framework.html
author: Clément Fournier <clement.fournier76@gmail.com>
---

## Internal architecture of the metrics framework

The framework is pretty simple. On a high level, a `Metric<N>` describes some numeric computation on a node of type `N`.
You should wrap it into a `MetricKey<N>`, so that it can be cached on nodes (implemented by {%jdoc core::lang.metrics.MetricsUtil %}).

At the very least, a metrics framework has those two components and is just a convenient way to compute and memoize
metrics on a single file. The expressive power of metrics can be improved by implementing *signature matching* capabilities,
which allows a metric to count signatures matching a specific pattern (a mask) over a whole class. This was originally
designed to work across files, given a working usage resolution. However, making that work with incremental analysis is
harder than it looks, and has been rescheduled to another project.

## Implementation of a new framework

* Implement metrics (typically in an internal package)
* Create some public enums/ utility classes to expose metric keys
* Implement a {%jdoc core::lang.metrics.LanguageMetricsProvider %}, to expose your metrics to the designer
* Use your metric keys in rules with {%jdoc core::lang.metrics.MetricsUtil %}

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
[Example](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/impl/AbstractJavaClassMetric.java#L52)

