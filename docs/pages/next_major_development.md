---
title: PMD 7.0.0 development
permalink: pmd_next_major_development.html
keywords: changelog, release notes, deprecation, api changes
---

We're excited to bring you the next major version of PMD! Here are the major features and changes we're working on.
To give us feedback or to suggest a new feature, drop us a line on [Gitter](https://gitter.im/pmd/pmd)!

## New Features

TODO

## Java grammar changes

{% include note.html content="Current plans are listed [here](https://github.com/pmd/pmd/labels/in%3Aast) and in particular [here](https://github.com/pmd/pmd/issues/1019)" %}



## New API support guidelines

### What's new

Until now, all released public members and types were implicitly considered part
of PMD's public API, including inheritance-specific members (protected members, abstract methods).
We have maintained those APIs with the goal to preserve full binary compatibility between minor releases,
only breaking those APIs infrequently, for major releases.

In order to allow PMD to move forward at a faster pace, this implicit contract will
be invalidated with PMD 7.0.0. We now introduce more fine-grained distinctions between
the type of compatibility support we guarantee for our libraries, and ways to make
them explicit to clients of PMD.

#### `.internal` packages and `@InternalApi` annotation

*Internal API* is meant for use *only* by the main PMD codebase. Internal types and methods
may be modified in any way, or even removed, at any time.

Any API in a package that contains an `.internal` segment is considered internal.
The `@InternalApi` annotation will be used for APIs that have to live outside of
these packages, e.g. methods of a public type that shouldn't be used outside of PMD (again,
these can be removed anytime).

#### `@ReservedSubclassing`

Types marked with the `@ReservedSubclassing` annotation are only meant to be subclassed
by classes within PMD. As such, we may add new abstract methods, or remove protected methods,
at any time. All published public members remain supported. The annotation is *not* inherited, which
means a reserved interface doesn't prevent its implementors to be subclassed.

#### `@Experimental`

APIs marked with the `@Experimental` annotation at the class or method level are subject to change.
They can be modified in any way, or even removed, at any time. You should not use or rely
 on them in any production code. They are purely to allow broad testing and feedback.

#### `@Deprecated`

APIs marked with the `@Deprecated` annotation at the class or method level will remain supported
until the next major release but it is recommended to stop using them.

### The transition

*All currently supported APIs will remain so until 7.0.0*. All APIs that are to be moved to
`.internal` packages or hidden will be tagged `@InternalApi` before that major release, and
the breaking API changes will be performed in 7.0.0.

## Planned API removals

### List of currently deprecated APIs

{% include warning.html content="This list is not exhaustive. The ultimate reference is whether
an API is tagged as `@Deprecated` or not in the latest minor release. During the development of 7.0.0,
we may decide to remove some APIs that were not tagged as deprecated, though we'll try to avoid it." %}

#### 6.8.0

*   A couple of methods and fields in `net.sourceforge.pmd.properties.AbstractPropertySource` have been
    deprecated, as they are replaced by already existing functionality or expose internal implementation
    details: `propertyDescriptors`, `propertyValuesByDescriptor`,
    `copyPropertyDescriptors()`, `copyPropertyValues()`, `ignoredProperties()`, `usesDefaultValues()`,
    `useDefaultValueFor()`.

*   Some methods in `net.sourceforge.pmd.properties.PropertySource` have been deprecated as well:
    `usesDefaultValues()`, `useDefaultValueFor()`, `ignoredProperties()`.

*   The class `net.sourceforge.pmd.lang.rule.AbstractDelegateRule` has been deprecated and will
    be removed with PMD 7.0.0. It is internally only in use by RuleReference.

*   The default constructor of `net.sourceforge.pmd.lang.rule.RuleReference` has been deprecated
    and will be removed with PMD 7.0.0. RuleReferences should only be created by providing a Rule and
    a RuleSetReference. Furthermore the following methods are deprecated: `setRuleReference()`,
    `hasOverriddenProperty()`, `usesDefaultValues()`, `useDefaultValueFor()`.

#### 6.7.0

*   All classes in the package `net.sourceforge.pmd.lang.dfa.report` have been deprecated and will be removed
    with PMD 7.0.0. This includes the class `net.sourceforge.pmd.lang.dfa.report.ReportTree`. The reason is,
    that this class is very specific to Java and not suitable for other languages. It has only been used for
    `YAHTMLRenderer`, which has been rewritten to work without these classes.

*   The nodes RUNSIGNEDSHIFT and RSIGNEDSHIFT are deprecated and will be removed from the AST with PMD 7.0.0.
    These represented the operator of ShiftExpression in two cases out of three, but they're not needed and
    make ShiftExpression inconsistent. The operator of a ShiftExpression is now accessible through
    ShiftExpression#getOperator.

#### 6.5.0

*   The utility class `net.sourceforge.pmd.lang.java.ast.CommentUtil` has been deprecated and will be removed
    with PMD 7.0.0. Its methods have been intended to parse javadoc tags. A more useful solution will be added
    around the AST node `FormalComment`, which contains as children `JavadocElement` nodes, which in
    turn provide access to the `JavadocTag`.

    All comment AST nodes (`FormalComment`, `MultiLineComment`, `SingleLineComment`) have a new method
    `getFilteredComment()` which provide access to the comment text without the leading `/*` markers.

*   The method `AbstractCommentRule.tagsIndicesIn()` has been deprecated and will be removed with
    PMD 7.0.0. It is not very useful, since it doesn't extract the information
    in a useful way. You would still need check, which tags have been found, and with which
    data they might be accompanied.

#### 6.4.0

* The following classes in package `net.sourceforge.pmd.benchmark` have been deprecated: `Benchmark`, `Benchmarker`,
  `BenchmarkReport`, `BenchmarkResult`, `RuleDuration`, `StringBuilderCR` and `TextReport`. Their API is not supported anymore
  and is disconnected from the internals of PMD. Use the newer API based around `TimeTracker` instead, which can be found
  in the same package.
* The class `net.sourceforge.pmd.lang.java.xpath.TypeOfFunction` has been deprecated. Use the newer `TypeIsFunction` in the same package.
* The `typeof` methods in `net.sourceforge.pmd.lang.java.xpath.JavaFunctions` have been deprecated.
  Use the newer `typeIs` method in the same class instead..
* The methods `isA`, `isEither` and `isNeither` of `net.sourceforge.pmd.lang.java.typeresolution.TypeHelper`.
  Use the new `isExactlyAny` and `isExactlyNone` methods in the same class instead.

#### 6.2.0

*   The static method `PMDParameters.transformParametersIntoConfiguration(PMDParameters)` is now deprecated,
    for removal in 7.0.0. The new instance method `PMDParameters.toConfiguration()` replaces it.

*   The method `ASTConstructorDeclaration.getParameters()` has been deprecated in favor of the new method
    `getFormalParameters()`. This method is available for both `ASTConstructorDeclaration` and
    `ASTMethodDeclaration`.

#### 6.1.0

* The method `getXPathNodeName` is added to the `Node` interface, which removes the
use of the `toString` of a node to get its XPath element name (see [#569](https://github.com/pmd/pmd/issues/569)).
  * The default implementation provided in  `AbstractNode`, will
  be removed with 7.0.0
  * With 7.0.0, the `Node.toString` method will not necessarily provide its XPath node
  name anymore.

* The interface `net.sourceforge.pmd.cpd.Renderer` has been deprecated. A new interface
`net.sourceforge.pmd.cpd.renderer.CPDRenderer` has been introduced to replace it. The main
difference is that the new interface is meant to render directly to a `java.io.Writer`
rather than to a String. This allows to greatly reduce the memory footprint of CPD, as on
large projects, with many duplications, it was causing `OutOfMemoryError`s (see [#795](https://github.com/pmd/pmd/issues/795)).

  `net.sourceforge.pmd.cpd.FileReporter` has also been deprecated as part of this change, as it's no longer needed.

#### 6.0.1

*   The constant `net.sourceforge.pmd.PMD.VERSION` has been deprecated and will be removed with PMD 7.0.0.
    Please use `net.sourceforge.pmd.PMDVersion.VERSION` instead.

### List of currently deprecated rules

*   The Java rules {% rule java/codestyle/VariableNamingConventions %}, {% rule java/codestyle/MIsLeadingVariableName %},
    {% rule java/codestyle/SuspiciousConstantFieldName %}, and {% rule java/codestyle/AvoidPrefixingMethodParameters %} are
    now deprecated, and will be removed with version 7.0.0. They are replaced by the more general
    {% rule java/codestyle/FieldNamingConventions %}, {% rule java/codestyle/FormalParameterNamingConventions %}, and
    {% rule java/codestyle/LocalVariableNamingConventions %}.

*   The Java rule {% rule java/codestyle/AbstractNaming %} is deprecated
    in favour of {% rule java/codestyle/ClassNamingConventions %}.

*   The Java rules {% rule java/codestyle/WhileLoopsMustUseBraces %}, {% rule java/codestyle/ForLoopMustUseBraces %}, {% rule java/codestyle/IfStmtMustUseBraces %}, and {% rule java/codestyle/IfElseStmtMustUseBraces %}
    are deprecated. They will be replaced by the new rule {% rule java/codestyle/ControlStatementBraces %}

*   The Java rules {% rule java/codestyle/NcssConstructorCount %}, {% rule java/codestyle/NcssMethodCount %}, and {% rule java/codestyle/NcssTypeCount %} have been
    deprecated. They will be replaced by the new rule {% rule java/design/NcssCount %} in the category `design`.

*   The Java rule `LooseCoupling` in ruleset `java-typeresolution` is deprecated. Use the rule with the same name from category `bestpractices` instead.

*   The Java rule `CloneMethodMustImplementCloneable` in ruleset `java-typeresolution` is deprecated. Use the rule with the same name from category `errorprone` instead.

*   The Java rule `UnusedImports` in ruleset `java-typeresolution` is deprecated. Use the rule with
    the same name from category `bestpractices` instead.

*   The Java rule `SignatureDeclareThrowsException` in ruleset `java-typeresolution` is deprecated. Use the rule with the same name from category `design` instead.

*   The Java rule `EmptyStaticInitializer` in ruleset `java-empty` is deprecated. Use the rule {% rule java/errorprone/EmptyInitializer %}, which covers both static and non-static empty initializers.`

*   The Java rules `GuardDebugLogging` (ruleset `java-logging-jakarta-commons`) and `GuardLogStatementJavaUtil`
    (ruleset `java-logging-java`) have been deprecated. Use the rule {% rule java/bestpractices/GuardLogStatement %}, which covers all cases regardless of the logging framework.












