---
title: PMD Release Notes
permalink: pmd_release_notes7.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FOR 7.0.0 -->
<!-- It must be used instead of release_notes.md when adding 7.0.0 changes -->
<!-- to avoid merge conflicts with master -->
<!-- It must replace release_notes.md when releasing 7.0.0 -->

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Revamped Command Line Interface

PMD now ships with a unified Command Line Interface for both Linux/Unix and Windows. Instead of having a collection of scripts 
for the different utilities shipped with PMD, a single script `pmd` (`pmd.bat` for Windows) can now launch all
utilities using subcommands, e.g. `pmd check`, `pmd designer`. All commands and options are thoroughly documented in the help,
with full color support where available. Moreover, efforts were made to provide consistency in the usage of all PMD utilities.

```shell
$ Usage: pmd [-hV] [COMMAND]
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  check     The PMD standard source code analyzer
  cpd       Copy/Paste Detector - find duplicate code
  designer  The PMD visual rule designer
  cpd-gui   GUI for the Copy/Paste Detector
              Warning: May not support the full CPD feature set
  ast-dump  Experimental: dumps the AST of parsing source code
Exit Codes:
  0   Succesful analysis, no violations found
  1   An unexpected error occurred during execution
  2   Usage error, please refer to the command help
  4   Successful analysis, at least 1 violation found
```

For instance, where you previously would have run
```shell
run.sh pmd -d src -R ruleset.xml
```
you should now use
```shell
pmd check -d src -R ruleset.xml
```
or even better, omit using `-d` / `--dir` and simply pass the sources at the end of the parameter list

```shell
pmd check -R ruleset.xml src
```

Multiple source directories can passed, such as:
```shell
pmd check -R ruleset.xml src/main/java src/test/java
```

And the exact same applies to CPD:
```shell
pmd cpd --minimum-tokens 100 src/main/java
```

Additionally, the CLI for the `check` command has been enhanced with a progress bar, which interactively displays the
current progress of the analysis.

TODO screenshot (take it right before releasing, because other changes to the CLI will occur until then)

This can be disabled with the `--no-progress` flag.


Finally, we now provide a completion script for Bash/Zsh to further help daily usage.
This script can be found under `shell/pmd-completion.sh` in the binary distribution.
To use it, edit your `~/.bashrc` / `~/.zshrc` file and add the following line:

```
source *path_to_pmd*/shell/pmd-completion.sh
```

#### Full Antlr support

Languages backed by an Antlr grammar are now fully supported. This means, it's now possible not only to use Antlr grammars for CPD,
but we can actually build full-fledged PMD rules for them as well. Both the traditional Java visitor rules, and the simpler
XPath rules are available to users.

We expect this to enable both our dev team and external contributors to largely extend PMD usage for more languages.

#### Swift support

Given the full Antlr support, PMD now fully supports Swift. We are pleased to announce we are shipping a number of rules starting with PMD 7.

* {% rule "swift/errorprone/ForceCast" %} (`swift-errorprone`) flags all force casts, making sure you are defensively considering all types.
  Having the application crash shouldn't be an option.
* {% rule "swift/errorprone/ForceTry" %} (`swift-errorprone`) flags all force tries, making sure you are defensively handling exceptions.
  Having the application crash shouldn't be an option.
* {% rule "swift/bestpractices/ProhibitedInterfaceBuilder" %} (`swift-bestpractices`) flags any usage of interface builder. Interface builder
  files are prone to merge conflicts, and are impossible to code review, so larger teams usually try to avoid it or reduce it's usage.
* {% rule "swift/bestpractices/UnavailableFunction" %} (`swift-bestpractices`) flags any function throwing a `fatalError` not marked as
  `@available(*, unavailable)` to ensure no calls are actually performed in the codebase.

Contributors: [@lsoncini](https://github.com/lsoncini), [@matifraga](https://github.com/matifraga), [@tomidelucca](https://github.com/tomidelucca)

#### Kotlin support (experimental)

PMD now supports Kotlin as an additional language for analyzing source code. It is based on
the official kotlin Antlr grammar. Java-based rules and XPath-based rules are supported.

Kotlin support has **experimental** stability level, meaning no compatibility should
be expected between even incremental releases. Any functionality can be added, removed or changed without
warning.

We are shipping the following rules:

* {% rule kotlin/bestpractices/FunctionNameTooShort %} (`kotlin-bestpractices`) finds functions with a too short name.
* {% rule kotlin/errorprone/OverrideBothEqualsAndHashcode %} (`kotlin-errorprone`) finds classes with only either `equals`
  or `hashCode` overridden, but not both. This leads to unexpected behavior once instances of such classes
  are used in collections (Lists, HashMaps, ...).

Contributors: [@jborgers](https://github.com/jborgers), [@stokpop](https://github.com/stokpop)

#### XPath 3.1 support

Support for XPath versions 1.0, 1.0-compatibility was removed, support for XPath 2.0 is deprecated. The default (and only) supported XPath version is now XPath 3.1. This version of the XPath language is mostly identical to XPath 2.0. Notable changes:
 * The deprecated support for sequence-valued attributes is removed. Sequence-valued properties are still supported.
 * Refer to [the Saxonica documentation](https://www.saxonica.com/html/documentation/expressions/xpath31new.html) for an introduction to new features in XPath 3.1.


#### Node stream API

This version includes a powerful API to navigate trees, similar in usage to the Java 8 Stream API:
```java
node.descendants(ASTMethodCall.class)
    .filter(m -> "toString".equals(m.getMethodName()))
    .map(m -> m.getQualifier())
    .filter(q -> TypeTestUtil.isA(String.class, q))
    .foreach(System.out::println);
```

A pipeline like shown here traverses the tree lazily, which is more efficient than traversing eagerly to put all descendants in a list. It is also much easier to change than the old imperative way.

To make this API as accessible as possible, the {% jdoc core::lang.ast.Node %} interface has been fitted with new methods producing node streams. Those methods replace previous tree traversal methods like `Node#findDescendantsOfType`. In all cases, they should be more efficient and more convenient.

See {% jdoc core::lang.ast.NodeStream %} for more details.


#### JavaScript support

The JS specific parser options have been removed. The parser now always retains comments and uses version ES6.
The language module registers only one version (as before), now correctly with version "ES6" instead of "3".
Since there is only one version available for JavaScript there is actually no need to selected a specific version.
The default version is always ES6.

#### New Rules

##### Apex

*   The Apex rule {% rule "apex/design/UnusedMethod" %} finds unused methods in your code.

##### Java

*   {% rule "java/codestyle/UnnecessaryBoxing" %} reports boxing and unboxing
conversions that may be made implicit.

#### Changed Rules

##### Java

* {% rule "java/codestyle/UnnecessaryFullyQualifiedName" %}: the rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved to be more precise.
* {% rule "java/codestyle/UselessParentheses" %}: the rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.
* {% rule "java/bestpractices/LooseCoupling" %}: the rule has a new property to allow some types to be coupled to (`allowedTypes`).
* {% rule "java/errorprone/EmptyCatchBlock" %}: `CloneNotSupportedException` and `InterruptedException` are not special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* {% rule "java/errorprone/DontImportSun" %}: `sun.misc.Signal` is not special-cased anymore.
* {% rule "java/codestyle/UseDiamondOperator" %}: the property `java7Compatibility` is removed. The rule now handles Java 7
  properly without a property.
* {% rule "java/design/SingularField" %}: Properties `checkInnerClasses` and `disallowNotAssignment` are removed. The rule is now more precise and will check these cases properly.
* {% rule "java/design/UseUtilityClass" %}: The property `ignoredAnnotations` has been removed.
* {% rule "java/design/LawOfDemeter" %}: the rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.

#### Deprecated Rules


#### Removed Rules

The following previously deprecated rules have been finally removed:

*   AbstractNaming (java-codestyle) -> use {% rule "java/codestyle/ClassNamingConventions" %}
*   AvoidFinalLocalVariable (java-codestyle) -> not replaced
*   AvoidPrefixingMethodParameters (java-codestyle) -> use {% rule "java/codestyle/FormalParameterNamingConventions" %}
*   AvoidUsingShortType (java-performance) -> not replaced
*   BadComparison (java-errorprone) -> use {% rule "java/errorprone/ComparisonWithNaN" %}
*   BooleanInstantiation (java-performance) -> use {% rule "java/codestyle/UnnecessaryBoxing" %} and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
*   ByteInstantiation (java-performance) -> use {% rule "java/codestyle/UnnecessaryBoxing" %} and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
*   CloneThrowsCloneNotSupportedException (java-errorprone) -> not replaced
*   DataflowAnomalyAnalysis (java-errorprone) -> not replaced
*   DefaultPackage (java-codestyle) -> use {% rule "java/codestyle/CommentDefaultAccessModifier" %}
*   DoNotCallSystemExit (java-errorprone) -> use {% rule "java/errorprone/DoNotTerminateVM" %}
*   ForLoopsMustUseBraces (java-codestyle) -> use {% rule "java/codestyle/ControlStatementBraces" %}
*   IfElseStmtsMustUseBraces (java-codestyle) -> use {% rule "java/codestyle/ControlStatementBraces" %}
*   IfStmtsMustUseBraces (java-codestyle) -> use {% rule "java/codestyle/ControlStatementBraces" %}
*   IntegerInstantiation (java-performance) -> use {% rule "java/codestyle/UnnecessaryBoxing" %} and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
*   InvalidSlf4jMessageFormat (java-errorprone) ->  use {% rule "java/errorprone/InvalidLogMessageFormat" %}
*   LoggerIsNotStaticFinal (java-errorprone)
*   LongInstantiation (java-performance) -> use {% rule "java/codestyle/UnnecessaryBoxing" %} and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
*   MIsLeadingVariableName (java-codestyle) -> use {% rule "java/codestyle/FieldNamingConventions" %}
*   MissingBreakInSwitch (java-errorprone) ->  use {% rule "java/errorprone/ImplicitSwitchFallThrough" %}
*   ModifiedCyclomaticComplexity (java-design) -> use {% rule "java/design/CyclomaticComplexity" %}
*   PositionLiteralsFirstInCaseInsensitiveComparisons (java-bestpractices) -> use {% rule "java/bestpractices/LiteralsFirstInComparisons" %}
*   PositionLiteralsFirstInComparisons (java-bestpractices) -> use {% rule "java/bestpractices/LiteralsFirstInComparisons" %}
*   ReturnEmptyArrayRatherThanNull (java-errorprone) ->  use {% rule "java/errorprone/ReturnEmptyCollectionRatherThanNull" %}
*   ShortInstantiation (java-performance) -> use {% rule "java/codestyle/UnnecessaryBoxing" %} and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
*   SimplifyBooleanAssertion (java-design) -> use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
*   SimplifyStartsWith (java-performance) -> not replaced
*   StdCyclomaticComplexity (java-design) -> use {% rule "java/design/CyclomaticComplexity" %}
*   SuspiciousConstantFieldName (java-codestyle)
*   UnnecessaryWrapperObjectCreation (java-performance) -> use the new rule {% rule "java/codestyle/UnnecessaryBoxing" %}
*   UnsynchronizedStaticDateFormatter (java-multithreading)
*   UseAssertEqualsInsteadOfAssertTrue (java-bestpractices) -> use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
*   UseAssertNullInsteadOfAssertEquals (java-bestpractices) -> use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
*   UseAssertSameInsteadOfAssertEquals (java-bestpractices) -> use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
*   UseAssertTrueInsteadOfAssertEquals (java-bestpractices) -> use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
*   VariableNamingConventions (apex-codestyle)
*   VariableNamingConventions (java-codestyle) -> use {% rule "java/codestyle/FieldNamingConventions" %} and such
*   WhileLoopsMustUseBraces (java-codestyle) -> use {% rule "java/codestyle/ControlStatementBraces" %}

### Fixed Issues

* miscellaneous
    *   [#896](https://github.com/pmd/pmd/issues/896): \[all] Use slf4j
    *   [#1451](https://github.com/pmd/pmd/issues/1451): \[core] RulesetFactoryCompatibility stores the whole ruleset file in memory as a string
* ant
    * [#4080](https://github.com/pmd/pmd/issues/4080): \[ant] Split off Ant integration into a new submodule 
* core
    * [#4035](https://github.com/pmd/pmd/issues/4035): \[core] ConcurrentModificationException in DefaultRuleViolationFactory
* cli
    *   [#3828](https://github.com/pmd/pmd/issues/3828): \[core] Progress reporting
* apex-design
    *   [#2667](https://github.com/pmd/pmd/issues/2667): \[apex] Integrate nawforce/ApexLink to build robust Unused rule
* java-bestpractices
    * [#342](https://github.com/pmd/pmd/issues/342): \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
    * [#755](https://github.com/pmd/pmd/issues/755): \[java] AccessorClassGeneration false positive for private constructors
    * [#770](https://github.com/pmd/pmd/issues/770): \[java] UnusedPrivateMethod yields false positive for counter-variant arguments
    * [#807](https://github.com/pmd/pmd/issues/807): \[java] AccessorMethodGeneration false positive with overloads
    * [#833](https://github.com/pmd/pmd/issues/833): \[java] ForLoopCanBeForeach should consider iterating on this
    * [#1189](https://github.com/pmd/pmd/issues/1189): \[java] UnusedPrivateMethod false positive from inner class via external class
    * [#1212](https://github.com/pmd/pmd/issues/1212): \[java] Don't raise JUnitTestContainsTooManyAsserts on JUnit 5's assertAll
    * [#1422](https://github.com/pmd/pmd/issues/1422): \[java] JUnitTestsShouldIncludeAssert false positive with inherited @Rule field
    * [#1565](https://github.com/pmd/pmd/issues/1565): \[java] JUnitAssertionsShouldIncludeMessage false positive with AssertJ
    * [#1747](https://github.com/pmd/pmd/issues/1747): \[java] PreserveStackTrace false-positive
    * [#1969](https://github.com/pmd/pmd/issues/1969): \[java] MissingOverride false-positive triggered by package-private method overwritten in another package by extending class
    * [#1998](https://github.com/pmd/pmd/issues/1998): \[java] AccessorClassGeneration false-negative: subclass calls private constructor
    * [#2130](https://github.com/pmd/pmd/issues/2130): \[java] UnusedLocalVariable: false-negative with array
    * [#2147](https://github.com/pmd/pmd/issues/2147): \[java] JUnitTestsShouldIncludeAssert - false positives with lambdas and static methods
    * [#2464](https://github.com/pmd/pmd/issues/2464): \[java] LooseCoupling must ignore class literals: ArrayList.class
    * [#2542](https://github.com/pmd/pmd/issues/2542): \[java] UseCollectionIsEmpty can not detect the case `foo.bar().size()`
    * [#2650](https://github.com/pmd/pmd/issues/2650): \[java] UseTryWithResources false positive when AutoCloseable helper used
    * [#2796](https://github.com/pmd/pmd/issue/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
    * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
    * [#2822](https://github.com/pmd/pmd/issues/2822): \[java] LooseCoupling rule: Extend to cover user defined implementations and interfaces
    * [#2882](https://github.com/pmd/pmd/issues/2882): \[java] UseTryWithResources - false negative for explicit close
    * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
    * [#2890](https://github.com/pmd/pmd/issues/2890): \[java] UnusedPrivateMethod false positive with generics
* java-codestyle
    * [#1208](https://github.com/pmd/pmd/issues/1208): \[java] PrematureDeclaration rule false-positive on variable declared to measure time
    * [#1429](https://github.com/pmd/pmd/issues/1429): \[java] PrematureDeclaration as result of method call (false positive)
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2134](https://github.com/pmd/pmd/issues/2134): \[java] PreserveStackTrace not handling `Throwable.addSuppressed(...)`
    * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
    * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support `@ParameterizedTest`
    * [#2391](https://github.com/pmd/pmd/issues/2391): \[java] UseDiamondOperator FP when expected type and constructed type have a different parameterization
    * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support ParameterizedTest
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
    * [#2748](https://github.com/pmd/pmd/issues/2748): \[java] UnnecessaryCast false positive with unchecked cast
    * [#3195](https://github.com/pmd/pmd/pull/3195): \[java] Improve rule UnnecessaryReturn to detect more cases
    * [#3218](https://github.com/pmd/pmd/pull/3218): \[java] Generalize UnnecessaryCast to flag all unnecessary casts
    * [#3221](https://github.com/pmd/pmd/issues/3221): \[java] PrematureDeclaration false positive for unused variables
    * [#3238](https://github.com/pmd/pmd/issues/3238): \[java] Improve ExprContext, fix FNs of UnnecessaryCast
* java-design
    * [#1014](https://github.com/pmd/pmd/issues/1014): \[java] LawOfDemeter: False positive with lambda expression
    * [#1605](https://github.com/pmd/pmd/issues/1605): \[java] LawOfDemeter: False positive for standard UTF-8 charset name
    * [#2175](https://github.com/pmd/pmd/issues/2175): \[java] LawOfDemeter: False positive for chained methods with generic method call
    * [#2179](https://github.com/pmd/pmd/issues/2179): \[java] LawOfDemeter: False positive with static property access - should treat class-level property as global object, not dot-accessed property
    * [#2180](https://github.com/pmd/pmd/issues/2180): \[java] LawOfDemeter: False positive with Thread and ThreadLocalRandom
    * [#2182](https://github.com/pmd/pmd/issues/2182): \[java] LawOfDemeter: False positive with package-private access
    * [#2188](https://github.com/pmd/pmd/issues/2188): \[java] LawOfDemeter: False positive with fields assigned to local vars
    * [#2536](https://github.com/pmd/pmd/issues/2536): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal can't detect inner class
    * [#3786](https://github.com/pmd/pmd/issues/3786): \[java] SimplifyBooleanReturns should consider operator precedence
* java-errorprone
    * [#659](https://github.com/pmd/pmd/issues/659): \[java] MissingBreakInSwitch - last default case does not contain a break
    * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
    * [#1669](https://github.com/pmd/pmd/issues/1669): \[java] NullAssignment - FP with ternay and null as constructor argument
    * [#1899](https://github.com/pmd/pmd/issues/1899): \[java] Recognize @SuppressWanings("fallthrough") for MissingBreakInSwitch
    * [#2320](https://github.com/pmd/pmd/issues/2320): \[java] NullAssignment - FP with ternary and null as method argument
    * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case `new BigDecimal(Expression)`
    * [#2579](https://github.com/pmd/pmd/issues/2579): \[java] MissingBreakInSwitch detects the lack of break in the last case
    * [#2716](https://github.com/pmd/pmd/issues/2716): \[java] CompareObjectsWithEqualsRule: False positive with Enums
    * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
    * [#2894](https://github.com/pmd/pmd/issues/2894): \[java] Improve MissingBreakInSwitch
    * [#3071](https://github.com/pmd/pmd/issues/3071): \[java] BrokenNullCheck FP with PMD 6.30.0
    * [#3087](https://github.com/pmd/pmd/issues/3087): \[java] UnnecessaryBooleanAssertion overlaps with SimplifiableTestAssertion
    * [#3100](https://github.com/pmd/pmd/issues/3100): \[java] UseCorrectExceptionLogging FP in 6.31.0
    * [#3173](https://github.com/pmd/pmd/issues/3173): \[java] UseProperClassLoader false positive
    * [#3351](https://github.com/pmd/pmd/issues/3351): \[java] ConstructorCallsOverridableMethod ignores abstract methods
* java-multithreading
    * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
    * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
    * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
* java-performance
    * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
    * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ
* kotlin
    * [#419](https://github.com/pmd/pmd/issues/419): \[kotlin] Add support for Kotlin

### API Changes

* [#1648](https://github.com/pmd/pmd/pull/1702): \[apex,vf] Remove CodeClimate dependency - [Robert Sösemann](https://github.com/rsoesemann)
  Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" can no longer be overridden in rulesets.
  They were deprecated without replacement.

* The old GUI applications accessible through `run.sh designerold` and `run.sh bgastviewer` (and corresponding Batch scripts)
  have been removed from the PMD distribution. Please use the newer rule designer with `run.sh designer`.
  The corresponding classes in packages `java.net.sourceforge.pmd.util.viewer` and `java.net.sourceforge.pmd.util.designer` have
  all been removed.

* All API related to XPath support has been moved to the package {% jdoc_package core::lang.rule.xpath %}.
  This includes API that was previously dispersed over `net.sourceforge.pmd.lang`, `net.sourceforge.pmd.lang.ast.xpath`,
  `net.sourceforge.pmd.lang.rule.xpath`, `net.sourceforge.pmd.lang.rule`, and various language-specific packages 
  (which were made internal).

* The implementation of the Ant integration has been moved from the module `pmd-core` to a new module `pmd-ant`.
  This involves classes in package {% jdoc_package ant::ant %}. The ant CPDTask class `net.sourceforge.pmd.cpd.CPDTask`
  has been moved into the same package {% jdoc_package ant::ant %}. You'll need to update your taskdef entries in your
  build.xml files with the FQCN {% jdoc !!ant::ant.CPDTask %} if you use it anywhere.

#### Metrics framework

The metrics framework has been made simpler and more general.

* The metric interface takes an additional type parameter, representing the result type of the metric. This is usually `Integer` or `Double`. It avoids widening the result to a `double` just to narrow it down.

  This makes it so, that `Double.NaN` is not an appropriate sentinel value to represent "not supported" anymore. Instead, `computeFor` may return `null` in that case (or a garbage value). The value `null` may have caused problems with the narrowing casts, which through unboxing, might have thrown an NPE. But when we deprecated the language-specific metrics façades to replace them with the generic `MetricsUtil`, we took care of making the new methods throw an exception if the metric cannot be computed on the parameter. This forces you to guard calls to `MetricsUtil::computeMetric` with something like `if (metric.supports(node))`. If you're following this pattern, then you won't observe the undefined behavior.

* The `MetricKey` interface is not so useful and has been merged into the `Metric` interface and removed. So the `Metric` interface has the new method `String name()`.

* The framework is not tied to at most 2 node types per language anymore. Previously those were nodes for classes and for methods/constructors. Instead, many metrics support more node types. For example, NCSS can be computed on any code block.

  For that reason, keeping around a hard distinction between "class metrics" and "operation metrics" is not useful. So in the Java framework for example, we removed the interfaces `JavaClassMetric`, `JavaOperationMetric`,  abstract classes for those, `JavaClassMetricKey`, and `JavaOperationMetricKey`. Metric constants are now all inside the `JavaMetrics` utility class. The same was done in the Apex framework.

  We don't really need abstract classes for metrics now. So `AbstractMetric` is also removed from pmd-core. There is a factory method on the `Metric` interface to create a metric easily.

* This makes it so, that {% jdoc core::lang.metrics.LanguageMetricsProvider %} does not need type parameters. It can just return a `Set<Metric<?, ?>>` to list available metrics.

* {% jdoc_old core::lang.metrics.Signature %}s, their implementations, and the interface `SignedNode` have been removed. Node streams allow replacing their usages very easily.

### External Contributions

*   [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga)
*   [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini)
*   [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini)
*   [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matías Fraga](https://github.com/matifraga)
*   [#1881](https://github.com/pmd/pmd/pull/1881): \[doc] Add ANTLR documentation - [Matías Fraga](https://github.com/matifraga)
*   [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca)
*   [#2830](https://github.com/pmd/pmd/pull/2830): \[apex] Apexlink POC - [Kevin Jones](https://github.com/nawforce)
*   [#3866](https://github.com/pmd/pmd/pull/3866): \[core] Add CLI Progress Bar - [@JerritEic](https://github.com/JerritEic)

{% endtocmaker %}

