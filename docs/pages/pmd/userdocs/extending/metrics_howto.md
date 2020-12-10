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


In PMD's Metrics framework, a metric `Metric<N,R>` is an operation that can be carried out on nodes of a certain type `N` and produces
a numeric result of type `R`.

PMD ships with a library of "standard" metrics. Like cyclomatic complexity measures. These are documented in the following pages:
* [Java metrics](pmd_java_metrics_index.html)
* [Apex metrics](pmd_apex_metrics_index.html)

Usage and implementation documentation may be found in our Javadocs: {% jdoc coremx::Metric %} and {% jdoc coremx::MetricsUtil %}. In the Java module, XPath rules may additionally use the [pmd-java:metric](pmd_userdocs_extending_writing_xpath_rules.html#pmd-java-metric) function.
