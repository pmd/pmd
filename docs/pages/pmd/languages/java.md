---
title: Java support
permalink: pmd_languages_java.html
author: Clément Fournier
---

{% include warning.html content="WIP, todo for pmd 7" %}

### Type and symbol resolution

Java being a statically typed language, a Java program contains more information that just its syntax tree; for instance, every expression has a static type, and every method call is bound to a method overload statically (even if that overload is virtual). In PMD, much of this information is resolved from the AST by additional passes, which run after parsing, and before rules can inspect the tree. 

The semantic analysis roughly works like so:
1. The first passes resolve *symbols*, which are a model of the named entities that Java programs declare, like classes, methods, and variables.
2. Then, each name in the tree is resolved to a symbol, according to the language's scoping rules. This may modify the tree to remove *ambiguous names* (names which could be either a type, package, or variable).
3. The last pass resolves the types of expressions, which performs overload resolution on method calls, and type inference.

TODO describe 
* why we need auxclasspath
* how disambiguation can fail

### Type and symbol APIs

TODO describe APIs 

### Metrics framework

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
