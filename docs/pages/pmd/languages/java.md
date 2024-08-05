---
title: Java support
permalink: pmd_languages_java.html
author: Clément Fournier
last_updated: July 2024 (7.5.0)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
summary: "Java-specific features and guidance"
---

{% include language_info.html name='Java' id='java' implementation='java::lang.java.JavaLanguageModule' supports_pmd=true supports_cpd=true since='1.0.0' %}

## Overview of supported Java language versions

Usually the latest non-preview Java Version is the default version.

| Java Version | Alias | Supported by PMD since |
|--------------|-------|------------------------|
| 23-preview   |       | 7.5.0                  |
| 23 (default) |       | 7.5.0                  |
| 22-preview   |       | 7.0.0                  |
| 22           |       | 7.0.0                  |
| 21           |       | 7.0.0                  |
| 20           |       | 6.55.0                 |
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
it via the environment variable `PMD_JAVA_OPTS` and select the new language version, e.g. `22-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-22-preview ...

Note: we only support preview language features for the latest two java versions.

## Language Properties

See [Java language properties](pmd_languages_configuration.html#java-language-properties)

## Type and symbol resolution

Java being a statically typed language, a Java program contains more information than just its syntax tree;
for instance, every expression has a static type, and every method call is bound to a method overload
statically (even if that overload is virtual). In PMD, much of this information is resolved from the AST
by additional passes, which run after parsing, and before rules can inspect the tree. 

The semantic analysis roughly works like so:
1. The first passes resolve *symbols*, which are a model of the named entities that Java programs declare,
   like classes, methods, and variables.
2. Then, each name in the tree is resolved to a symbol, according to the language's scoping rules. This may
   modify the tree to remove *ambiguous names* (names which could be either a type, package, or variable).
3. The last pass resolves the types of expressions, which performs overload resolution on method calls,
   and type inference.

The analyzed code might reference types from other places of the project or even from external
dependencies. If e.g. the code extends a class from an external dependency, then PMD needs to know
this external dependency in order to figure out, that a method is actually an override.

In order to resolve such types, a complete so-called auxiliary classpath need to be provided.
Technically, PMD uses the [ASM framework](https://asm.ow2.io/index.html) to read the bytecode and build
up its own representation to resolve names and types. It also reads the bytecode of the Java runtime
in order to resolve Java API references.

## Providing the auxiliary classpath

The auxiliary classpath (or short "auxClasspath") is configured via the
[Language Property "auxClasspath"](pmd_languages_configuration.html#java-language-properties).
It is a string containing multiple paths separated by either a colon (`:`) under Linux/MacOS
or a semicolon (`;`) under Windows. This property can be provided on the CLI with parameter
[`--aux-classpath`](pmd_userdocs_cli_reference.html#-aux-classpath).

In order to resolve the types of the Java API correctly, the Java Runtime must be on the
auxClasspath as well. As the Java API and Runtime evolves from version to version, it is important
to use the correct Java version, that is being analyzed. This might not necessarily be the
same Java runtime version that is being used to run PMD.

Until Java 8, there exists the jar file `rt.jar` in `${JAVA_HOME}/jre/lib`. It is enough, to include
this jar file in the auxClasspath. Usually, you would add this as the first entry in the auxClasspath.

Beginning with Java 9, the Java platform has been modularized and [Modular Run-Time Images](https://openjdk.org/jeps/220)
have been introduced. The file `${JAVA_HOME}/lib/modules` contains now all the classes, but it is not a jar file
anymore. However, each Java installation provides an implementation to read such Run-Time Images in
`${JAVA_HOME}/lib/jrt-fs.jar`. This is an implementation of the `jrt://` filesystem and through this, the bytecode
of the Java runtime classes can be loaded. In order to use this with PMD, the file `${JAVA_HOME}/lib/jrt-fs.jar`
needs to be added to the auxClasspath as the first entry. PMD will make sure, to load the Java runtime classes
using the jrt-filesystem.

If neither `${JAVA_HOME}/jre/lib/rt.jar` nor `${JAVA_HOME}/lib/jrt-fs.jar` is added to the auxClasspath, PMD falls
back to load the Java runtime classes **from the current runtime**, that is the runtime that was used to
execute PMD. This might not be the correct version, e.g. you might run PMD with Java 8, but analyze code
written for Java 21. In that case, you have to provide "jrt-fs.jar" on the auxClasspath.

Not providing the correct auxClasspath might result in false positives or negatives for some rules,
such as {% rule java/bestpractices/MissingOverride %}.
This rule needs to figure out, whether a method is defined already in the super class or interface. E.g. the method
[Collection#toArray(IntFunction)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Collection.html#toArray(java.util.function.IntFunction))
has been added in Java 11, and it does not exist yet in Java 8. Given a simple subclass of ArrayList, that overrides
this method without adding `@Override`, then PMD won't be able to detect this missing override annotation, if
it is executed with a Java 8 runtime but without the correct auxClasspath. Providing the correct `jrt-fs.jar` from
Java 11 (or later) for the auxClasspath allows PMD to correctly identify the missing annotation.

Example command line:

```
pmd check -d src/main/java \
  --aux-classpath=path/to/java17/lib/jrt-fs.jar:target/classes/ \
  -f xml -r pmd-report.xml -R rulesets/java/quickstart.xml
```

## Symbol table APIs

{% jdoc_nspace :ast java::lang.java.ast %}
{% jdoc_nspace :symbols java::lang.java.symbols %}

Symbol table API related classes are in the package {% jdoc_package :symbols %}.
The root interface for symbols is {%jdoc symbols::JElementSymbol %}.

The symbol table can be requested on any node with the method {% jdoc ast::AbstractJavaNode#getSymbolTable() %}.
This returns a {% jdoc symbols::table.JSymbolTable %} which gives you access to variables, methods and types that are
within scope.

A {% jdoc ast::ASTExpression %} might represent a {% jdoc ast::ASTAssignableExpr.ASTNamedReferenceExpr %}
if it e.g. references a variable name. In that case, you can access the referenced variable symbol
with the method {% jdoc ast::ASTAssignableExpr.ASTNamedReferenceExpr#getReferencedSym() %}.

Declaration nodes, such as {% jdoc ast::ASTVariableId %} implement the interface
{%jdoc ast::SymbolDeclaratorNode %}. Through the method
{% jdoc ast::SymbolDeclaratorNode#getSymbol() %} you can also access the symbol.

To find usages, you can call {% jdoc ast::ASTVariableId#getLocalUsages() %}.

## Type resolution APIs

{% jdoc_nspace :types java::lang.java.types %}

Type resolution API related classes are in the package {% jdoc_package :types %}.

The core of the framework is a set of interfaces to represent types. The root interface is
{% jdoc types::JTypeMirror %}. Type mirrors are created by a
{% jdoc types::TypeSystem %} object. This object is analysis-global.

The utility class {% jdoc types::TypeTestUtil %} provides simple methods to check types,
e.g. `TypeTestUtil.isA(String.class, variableDeclaratorIdNode)` tests, whether the given
variableDeclaratorId is of type "String".

Any {% jdoc ast::TypeNode %} provides access to the type with the method {% jdoc ast::TypeNode#getTypeMirror() %}.
E.g. this can be called on {% jdoc ast::ASTMethodCall %} to retrieve the return type of the called method.

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

* {% jdoc core::reporting.RuleViolation#VARIABLE_NAME %}
* {% jdoc core::reporting.RuleViolation#METHOD_NAME %}
* {% jdoc core::reporting.RuleViolation#CLASS_NAME %}
* {% jdoc core::reporting.RuleViolation#PACKAGE_NAME %}

You can access these via {% jdoc core::reporting.RuleViolation#getAdditionalInfo() %}

## Dataflow

There is no API yet for dataflow analysis. However, some rules such as {% rule java/bestpractices/UnusedAssignment %}
or {% rule java/design/ImmutableField %} are using an internal implementation of an additional
AST pass that adds dataflow information. The implementation can be found in
[net.sourceforge.pmd.lang.java.rule.internal.DataflowPass](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/internal/DataflowPass.java).
