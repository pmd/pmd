


## 29-May-2026 - 7.25.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.25.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Updated ANTLR library to 4.13.2](#updated-antlr-library-to-4132)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
    * [Renamed rules and properties](#renamed-rules-and-properties)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy
#### Updated ANTLR library to 4.13.2
We have updated the ANTLR library (parser generator) from 4.9.3 to the latest version 4.13.2,
in order to be able to use the latest version of Apex parser library.

This is an incompatible update: In case you use custom language modules based on ANTLR, you
need to make sure to regenerate all of your lexers and parsers with the new ANTLR version.

For the ANTLR based language modules, that PMD ships (kotlin and swift and various CPD modules),
this is already done.

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule [`JUnitJupiterTestNoPrivateModifier`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_errorprone.html#junitjupitertestnoprivatemodifier) find JUnit test classes and
  methods that are private. Test classes, test methods, and lifecycle methods are not required to be public,
  but they must not be private. Otherwise, they won’t be found by the test framework.
* The new Java rule [`UnnecessaryBlock`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#unnecessaryblock) reports blocks that are unnecessary as
  they don't introduce a new scope. This rule helps simplify code structure by identifying and flagging
  redundant blocks that can make code harder to read and may be misleading.
* The new Java rule [`VariableDeclarationUsageDistance`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#variabledeclarationusagedistance) flags local variables that are declared
  far from their usage, which can make code harder to read. The rule has a property `maxDistance` that allows to
  configure the maximum allowed distance between declaration and usage.
* The new Java rule [`AssertStatementInTest`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_bestpractices.html#assertstatementintest) detects usages of `assert` statement in tests.
  These should be replaced by framework assertion methods such as `assertEquals`.
  Such methods provide better error messages and make test behave correctly when running without `-ea`.

#### Changed Rules
* The rule [`OnlyOneReturn`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#onlyonereturn) has a new property `allowGuardIfs`. When this property is
  true, then guard ifs at the beginning of a method are allowed their return statements don't count.
* We are continuously working to improve the precision of violation reporting for various rules.
  The goal is to ensure that rules report issues on the correct line and highlight only the relevant lines.
  For example, instead of flagging an entire class declaration (including its body), we now generally report only
  the class name. For more details, see [[java] Single Line Warnings #730](https://github.com/pmd/pmd/issues/730)
  and [[java] Review reported locations of rules #3769](https://github.com/pmd/pmd/issues/3769). While this effort
  is still ongoing, the following Java rules have been updated in this release:
  * [`AbstractClassWithoutAbstractMethod`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_bestpractices.html#abstractclasswithoutabstractmethod)
  * [`AbstractClassWithoutAnyMethod`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#abstractclasswithoutanymethod)
  * [`AtLeastOneConstructor`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#atleastoneconstructor)
  * [`AvoidDollarSigns`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#avoiddollarsigns)
  * [`AvoidCatchingGenericException`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_errorprone.html#avoidcatchinggenericexception)
  * [`AvoidSynchronizedStatement`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_multithreading.html#avoidsynchronizedstatement) (now reports only on synchronized keyword and not the whole synchronized block)
  * [`ClassNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#classnamingconventions)
  * [`ClassWithOnlyPrivateConstructorsShouldBeFinal`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#classwithonlyprivateconstructorsshouldbefinal)
  * [`CommentDefaultAccessModifier`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier)
  * [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_documentation.html#commentrequired)
  * [`CouplingBetweenObjects`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#couplingbetweenobjects) (now reports only on class identifier and not whole compilation unit anymore)
  * [`CyclomaticComplexity`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#cyclomaticcomplexity)
  * [`DataClass`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#dataclass)
  * [`ExcessiveImports`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#excessiveimports) (now reports only on imports and not the whole compilation unit anymore)
  * [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#excessiveparameterlist)
  * [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#excessivepubliccount)
  * [`ExhaustiveSwitchHasDefault`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_bestpractices.html#exhaustiveswitchhasdefault) (now reports only on switch keyword and not the whole switch block)
  * [`GodClass`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#godclass)
  * [`ImplicitFunctionalInterface`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_bestpractices.html#implicitfunctionalinterface)
  * [`JUnit5TestShouldBePackagePrivate`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_bestpractices.html#junit5testshouldbepackageprivate)
  * [`LocalHomeNamingConvention`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#localhomenamingconvention)
  * [`LocalInterfaceSessionNamingConvention`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#localinterfacesessionnamingconvention)
  * [`MissingSerialVersionUID`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_errorprone.html#missingserialversionuid)
  * [`MissingStaticMethodInNonInstantiatableClass`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_errorprone.html#missingstaticmethodinnoninstantiatableclass)
  * [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#ncsscount)
  * [`NonExhaustiveSwitch`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_bestpractices.html#nonexhaustiveswitch) (now reports only on switch keyword and not the whole switch block)
  * [`NoPackage`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#nopackage)
  * [`PublicMemberInNonPublicType`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#publicmemberinnonpublictype)
  * [`ShortClassName`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#shortclassname)
  * [`SingleMethodSingleton`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_errorprone.html#singlemethodsingleton)
  * [`SwitchDensity`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#switchdensity) (now reports only on switch keyword and not the whole switch block)
  * [`TestClassWithoutTestCases`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_errorprone.html#testclasswithouttestcases)
  * [`TooFewBranchesForSwitch`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_performance.html#toofewbranchesforswitch) (now reports only on switch keyword and not the whole switch block)
  * [`TooManyFields`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#toomanyfields) (now reports only on class identifier and not the whole class body anymore)
  * [`TooManyMethods`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#toomanymethods) (now reports only on class identifier and not the whole class body anymore)
  * [`TooManyStaticImports`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#toomanystaticimports) (now reports only on the first static import and not the whole compilation unit anymore)
  * [`UnnecessaryModifier`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#unnecessarymodifier)
  * [`UseUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_design.html#useutilityclass)

#### Renamed rules and properties

* One rule and one property have been renamed to reflect the fact that they work for both JUnit 5 and 6:
  * The rule [`JUnitJupiterTestShouldBePackagePrivate`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_bestpractices.html#junitjupitertestshouldbepackageprivate) (Java Best Practices) was renamed from `JUnit5TestShouldBePackagePrivate`.
  * The property `junitJupiterTestPattern` of rule [`MethodNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.25.0-SNAPSHOT/pmd_rules_java_codestyle.html#methodnamingconventions) (Java Code Style) was renamed from `junit5TestPattern`.

The old names still work but are deprecated.

### 🐛️ Fixed Issues
* core
  * [#4972](https://github.com/pmd/pmd/issues/4972): \[core] Update ANTLR to 4.13.2
  * [#6308](https://github.com/pmd/pmd/issues/6308): \[core] CPD Markdown format: Add syntax highlighting
* java
  * [#5721](https://github.com/pmd/pmd/issues/5721): \[java] StackOverflowError in 7.17.0 with nested wildcard generics
  * [#5746](https://github.com/pmd/pmd/issues/5746): \[java] Separate test sources and resources
  * [#6688](https://github.com/pmd/pmd/issues/6688): \[java] LocalVariableCouldBeFinalRule API changed
  * [#6704](https://github.com/pmd/pmd/issues/6704): \[java] Rename rules and properties with JUnit5 in the name
* java-bestpractices
  * [#3212](https://github.com/pmd/pmd/issues/3212): \[java] Enhance UseStandardCharsets to flag some constructors of IO-related classes
  * [#3777](https://github.com/pmd/pmd/issues/3777): \[java] New rule: AssertStatementInTest
  * [#5477](https://github.com/pmd/pmd/issues/5477): \[java] JUnit5TestShouldBePackagePrivate is not applied when @Test method is only present in parent class
  * [#6606](https://github.com/pmd/pmd/issues/6606): \[java] UnusedPrivateField: False positive on JUnit Jupiter `@FieldSource`
  * [#6681](https://github.com/pmd/pmd/issues/6681): \[java] UnitTestShouldIncludeAssert: False positive with JUnitSoftAssertions Rule (JUnit 4)
* java-codestyle
  * [#2801](https://github.com/pmd/pmd/issues/2801): \[java] OnlyOneReturn should have a property to allow early exits (guard clauses)
  * [#6427](https://github.com/pmd/pmd/issues/6427): \[java] UnnecessaryCast: False positive for long cast before bit-shift operations on int/byte
  * [#6602](https://github.com/pmd/pmd/issues/6602): \[java] LocalVariableCouldBeFinal: False negative when multiple variables are declared at once
  * [#6622](https://github.com/pmd/pmd/issues/6622): \[java] New rule: UnnecessaryBlock
  * [#6640](https://github.com/pmd/pmd/issues/6640): \[java] New rule: VariableDeclarationUsageDistance
* java-design
  * [#559](https://github.com/pmd/pmd/issues/559): \[java] UseUtilityClass: False negative for constant only classes
* java-errorprone
  * [#3288](https://github.com/pmd/pmd/issues/3288): \[java] New Rule: JUnit5TestNoPrivateModifier
  * [#4288](https://github.com/pmd/pmd/issues/4288): \[java] Document that CallSuperFirst/CallSuperLast are Android specific
  * [#6163](https://github.com/pmd/pmd/issues/6163): \[java] ConstructorCallsOverridableMethod: False positive when method is from enclosing class
  * [#6517](https://github.com/pmd/pmd/issues/6517): \[java] UselessPureMethodCall: False negative for methods on IntStream/LongStream/DoubleStream
  * [#6652](https://github.com/pmd/pmd/issues/6652): \[java] AvoidInstanceofChecksInCatchClause: false negative when pattern-matching instanceof
* java-multithreading
  * [#6520](https://github.com/pmd/pmd/issues/6520): \[java] DoNotUseThreads: False positive on legitimate java.lang.Thread.onSpinWait() call
  * [#6636](https://github.com/pmd/pmd/issues/6636): \[java] OverridingThreadRun: Fix false negatives with other methods and anonymous classes
* kotlin
  * [#6608](https://github.com/pmd/pmd/issues/6608): \[kotlin] Lexer or parse errors are reported to stderr only without file context
  * [#6648](https://github.com/pmd/pmd/issues/6648): \[kotlin] Multi-dollar interpolation parse error in annotations

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6084](https://github.com/pmd/pmd/pull/6084): \[java] Shrink reported locations for some rules - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6522](https://github.com/pmd/pmd/pull/6522): \[java] Fix #6520: DoNotUseThreads: fix false positive on Thread.onSpinWait()  - [leemeii](https://github.com/leemeii) (@leemeii)
* [#6524](https://github.com/pmd/pmd/pull/6524): \[java] Fix #6517: UselessPureMethodCall: fix false negative for primitive streams - [leemeii](https://github.com/leemeii) (@leemeii)
* [#6553](https://github.com/pmd/pmd/pull/6553): \[java] Fix StackOverflowError in TypeOps projection of cyclic captured type vars - [Sebastian Lövdahl](https://github.com/slovdahl) (@slovdahl)
* [#6557](https://github.com/pmd/pmd/pull/6557): \[java] New rule: AssertStatementInTest - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6561](https://github.com/pmd/pmd/pull/6561): \[java] Fix #6163: ConstructorCallsOverridableMethod: False positive with call to enclosing class - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6573](https://github.com/pmd/pmd/pull/6573): \[java] Fix #6427: Add bitwise and/or/xor to BINARY_PROMOTED_OPS - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6587](https://github.com/pmd/pmd/pull/6587): \[java] Fix #2801: Add a property to OnlyOneReturnRule to allow guard ifs - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6597](https://github.com/pmd/pmd/pull/6597): \[java] Fix #3212: Enhance UseStandardCharsets - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6601](https://github.com/pmd/pmd/pull/6601): \[java] Fix #4288: Document that CallSuperFirst and CallSuperLast are android only - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6603](https://github.com/pmd/pmd/pull/6603): \[java] Fix #6602: Fix false negative in LocalVariableCouldBeFinalRule - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6604](https://github.com/pmd/pmd/pull/6604): \[java] Fix #3288: New rule JUnit5TestNoPrivateModifierRule - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6605](https://github.com/pmd/pmd/pull/6605): \[java] Fix #6308: Add syntax highlighting to MarkdownRenderer - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6619](https://github.com/pmd/pmd/pull/6619): \[java] Fix #5746: Separate test sources and resources - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6621](https://github.com/pmd/pmd/pull/6621): \[core] Fix #4972: Update ANTLR from 4.9.3 to 4.13.2 - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6623](https://github.com/pmd/pmd/pull/6623): \[java] Cleanup: Remove TODO from ModifierOwner.getVisibility() - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6636](https://github.com/pmd/pmd/pull/6636): \[java] OverridingThreadRun: Fix false negatives with other methods and anonymous classes - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6638](https://github.com/pmd/pmd/pull/6638): \[java] Fix #559: Improve UseUtilityClassRule to trigger also on static members - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6639](https://github.com/pmd/pmd/pull/6639): \[java] New rule: UnnecessaryBlock - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6640](https://github.com/pmd/pmd/pull/6640): \[java] New rule: VariableDeclarationUsageDistance - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6646](https://github.com/pmd/pmd/pull/6646): \[test] Split up AbstractRuleSetFactoryTest.testAllPMDBuiltInRulesMeetConventions() - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6650](https://github.com/pmd/pmd/pull/6650): \[kotlin] Fix #6608: Improve kotlin parser error handling - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6653](https://github.com/pmd/pmd/pull/6653): \[kotlin] Fix #6648: Multi-dollar interpolation for regular strings - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6654](https://github.com/pmd/pmd/pull/6654): \[swift] Fix invalid swift token OSXApplicationExtension - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6658](https://github.com/pmd/pmd/pull/6658): \[doc] Fix capitalization of ANTLR in release notes - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6661](https://github.com/pmd/pmd/pull/6661): \[java] Fix #6652: Support new-style instanceof (with pattern matching) in AvoidInstanceofChecksInCatchClause - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6680](https://github.com/pmd/pmd/pull/6680): \[java] Fix #5477: JUnit5TestShouldBePackagePrivate is not applied when @Test method is only present in parent class - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



