


## 29-May-2026 - 7.25.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.25.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [🌟️ Changed Rules](#changed-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy
#### 🌟️ Changed Rules
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
* java-multithreading
  * [#6520](https://github.com/pmd/pmd/issues/6520): \[java] DoNotUseThreads: False positive on legitimate java.lang.Thread.onSpinWait() call

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6084](https://github.com/pmd/pmd/pull/6084): \[java] Shrink reported locations for some rules - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6522](https://github.com/pmd/pmd/pull/6522): \[java] Fix #6520: DoNotUseThreads: fix false positive on Thread.onSpinWait()  - [leemeii](https://github.com/leemeii) (@leemeii)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



