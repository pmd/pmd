---
title: Java support
permalink: pmd_languages_java.html
author: Clément Fournier
last_updated: September 2023 (7.0.0)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
summary: "Java-specific features and guidance"
---

{% include language_info.html name='Java' id='java' implementation='java::lang.java.JavaLanguageModule' supports_pmd=true supports_cpd=true since='1.0.0' %}

{% include warning.html content="WIP, todo for pmd 7" %}

## Overview of supported Java language versions

Usually the latest non-preview Java Version is the default version.

| Java Version | Alias | Supported by PMD since |
|--------------|-------|------------------------|
| 20-preview   |       | 6.55.0                 |
| 20 (default) |       | 6.55.0                 |
| 19-preview   |       | 6.48.0                 |
| 19           |       | 6.48.0                 |
| 18           |       | 6.44.0                 |
| 17           |       | 6.37.0                 |
| 16           |       | 6.32.0                 |
| 15           |       | 6.27.0                 |
| 14           |       | 6.22.0                 |
| 13           |       | 6.18.0                 |
| 12           |       | 6.13.0                 |
| 11           |       | 6.6.0                  |
| 10           | 1.10  | 6.4.0                  |
| 9            | 1.9   | 6.0.0                  |
| 8            | 1.8   | 5.1.0                  |
| 7            | 1.7   | 5.0.0                  |
| 6            | 1.6   | 3.9                    |
| 5            | 1.5   | 3.0                    |
| 1.4          |       | 1.2.2                  |
| 1.3          |       | 1.0.0                  |

## Using Java preview features

In order to analyze a project with PMD that uses preview language features, you'll need to enable
it via the environment variable `PMD_JAVA_OPTS` and select the new language version, e.g. `20-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-20-preview ...

Note: we only support preview language features for the latest two java versions.

## Language Properties

See [Java language properties](pmd_languages_configuration.html#java-language-properties)

## Type and symbol resolution

Java being a statically typed language, a Java program contains more information that just its syntax tree; for instance, every expression has a static type, and every method call is bound to a method overload statically (even if that overload is virtual). In PMD, much of this information is resolved from the AST by additional passes, which run after parsing, and before rules can inspect the tree. 

The semantic analysis roughly works like so:
1. The first passes resolve *symbols*, which are a model of the named entities that Java programs declare, like classes, methods, and variables.
2. Then, each name in the tree is resolved to a symbol, according to the language's scoping rules. This may modify the tree to remove *ambiguous names* (names which could be either a type, package, or variable).
3. The last pass resolves the types of expressions, which performs overload resolution on method calls, and type inference.

TODO describe 
* why we need auxclasspath
* how disambiguation can fail

## Type and symbol APIs

TODO describe APIs: see #4319 and #2689

## Metrics framework

In order to use code metrics in Java, use the metrics constants in {% jdoc java::lang.java.metrics.JavaMetrics %},
together with {% jdoc core::lang.metrics.MetricsUtil %}. For instance:

```java
@Override
public Object visit(ASTMethodDeclaration node, Object data) {
    if (JavaMetrics.NCSS.supports(node)) {
        int methodSize = MetricsUtil.computeMetric(JavaMetrics.NCSS, node, ncssOptions);
        if (methodSize >= level) {
            addViolation(data, node);
        }
    }
    return null;
}
```

The Javadocs are the reference documentation.

## Violation Decorators

Violations reported are the same for all languages, but languages can opt in to provide more details.
Java does this by adding the following additional information for each reported violation:

* {% jdoc core::RuleViolation#VARIABLE_NAME %}
* {% jdoc core::RuleViolation#METHOD_NAME %}
* {% jdoc core::RuleViolation#CLASS_NAME %}
* {% jdoc core::RuleViolation#PACKAGE_NAME %}

You can access these via {% jdoc core::RuleViolation#getAdditionalInfo() %}
