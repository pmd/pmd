


## 29-May-2026 - 7.25.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.25.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Updated antlr library to 4.13.2](#updated-antlr-library-to-4132)
    * [🌟️ Changed Rules](#changed-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy
#### Updated antlr library to 4.13.2
We have updated the antlr library (parser generator) from 4.9.3 to the latest version 4.13.2,
in order to be able to use the latest version of Apex parser library.

This is an incompatible update: In case you use custom language modules based on antlr, you
need to make sure to regenerate all of your lexers and parsers with the new antlr version.

For the antlr based language modules, that PMD ships (kotlin and swift and various CPD modules),
this is already done.

#### 🌟️ Changed Rules
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

### 🐛️ Fixed Issues
* core
  * [#4972](https://github.com/pmd/pmd/issues/4972): \[core] Update antlr to 4.13.2
  * [#6308](https://github.com/pmd/pmd/issues/6308): \[core] CPD Markdown format: Add syntax highlighting
* java
  * [#5721](https://github.com/pmd/pmd/issues/5721): \[java] StackOverflowError in 7.17.0 with nested wildcard generics
* java-bestpractices
  * [#3212](https://github.com/pmd/pmd/issues/3212): \[java] Enhance UseStandardCharsets to flag some constructors of IO-related classes
* java-codestyle
  * [#2801](https://github.com/pmd/pmd/issues/2801): \[java] OnlyOneReturn should have a property to allow early exits (guard clauses)
  * [#6427](https://github.com/pmd/pmd/issues/6427): \[java] UnnecessaryCast: False positive for long cast before bit-shift operations on int/byte
  * [#6602](https://github.com/pmd/pmd/issues/6602): \[java] LocalVariableCouldBeFinal: False negative when multiple variables are declared at once
* java-errorprone
  * [#4288](https://github.com/pmd/pmd/issues/4288): \[java] Document that CallSuperFirst/CallSuperLast are Android specific
  * [#6163](https://github.com/pmd/pmd/issues/6163): \[java] ConstructorCallsOverridableMethod: False positive when method is from enclosing class
  * [#6517](https://github.com/pmd/pmd/issues/6517): \[java] UselessPureMethodCall: False negative for methods on IntStream/LongStream/DoubleStream
* java-multithreading
  * [#6520](https://github.com/pmd/pmd/issues/6520): \[java] DoNotUseThreads: False positive on legitimate java.lang.Thread.onSpinWait() call
* kotlin
  * [#6648](https://github.com/pmd/pmd/issues/6648): \[kotlin] Multi-dollar interpolation parse error in annotations

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6084](https://github.com/pmd/pmd/pull/6084): \[java] Shrink reported locations for some rules - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6522](https://github.com/pmd/pmd/pull/6522): \[java] Fix #6520: DoNotUseThreads: fix false positive on Thread.onSpinWait()  - [leemeii](https://github.com/leemeii) (@leemeii)
* [#6524](https://github.com/pmd/pmd/pull/6524): \[java] Fix #6517: UselessPureMethodCall: fix false negative for primitive streams - [leemeii](https://github.com/leemeii) (@leemeii)
* [#6553](https://github.com/pmd/pmd/pull/6553): \[java] Fix StackOverflowError in TypeOps projection of cyclic captured type vars - [Sebastian Lövdahl](https://github.com/slovdahl) (@slovdahl)
* [#6561](https://github.com/pmd/pmd/pull/6561): \[java] Fix #6163: ConstructorCallsOverridableMethod: False positive with call to enclosing class - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6573](https://github.com/pmd/pmd/pull/6573): \[java] Fix #6427: Add bitwise and/or/xor to BINARY_PROMOTED_OPS - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6587](https://github.com/pmd/pmd/pull/6587): \[java] Fix #2801: Add a property to OnlyOneReturnRule to allow guard ifs - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6597](https://github.com/pmd/pmd/pull/6597): \[java] Fix #3212: Enhance UseStandardCharsets - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6601](https://github.com/pmd/pmd/pull/6601): \[java] Fix #4288: Document that CallSuperFirst and CallSuperLast are android only - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6605](https://github.com/pmd/pmd/pull/6605): \[java] Fix #6308: Add syntax highlighting to MarkdownRenderer - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6621](https://github.com/pmd/pmd/pull/6621): \[core] Fix #4972: Update antlr from 4.9.3 to 4.13.2 - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6623](https://github.com/pmd/pmd/pull/6623): \[java] Cleanup: Remove TODO from ModifierOwner.getVisibility() - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6646](https://github.com/pmd/pmd/pull/6646): \[test] Split up AbstractRuleSetFactoryTest.testAllPMDBuiltInRulesMeetConventions() - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6653](https://github.com/pmd/pmd/pull/6653): \[kotlin] Fix #6648: Multi-dollar interpolation for regular strings - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6654](https://github.com/pmd/pmd/pull/6654): \[swift] Fix invalid swift token OSXApplicationExtension - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6603](https://github.com/pmd/pmd/pull/6603): \[java] Fix #6602: Fix false negative in LocalVariableCouldBeFinalRule - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



