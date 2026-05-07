---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% comment %}
This allows to use links e.g. [Basic CLI usage]({{ baseurl }}pmd_userdocs_installation.html) that work both
in the release notes on GitHub (as an absolute url) and on the rendered documentation page (as a relative url).
{% endcomment %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀️ New and noteworthy
#### 🌟️ Changed Rules
* We are continuously working to improve the precision of violation reporting for various rules.
  The goal is to ensure that rules report issues on the correct line and highlight only the relevant lines.
  For example, instead of flagging an entire class declaration (including its body), we now generally report only
  the class name. For more details, see [[java] Single Line Warnings #730](https://github.com/pmd/pmd/issues/730)
  and [[java] Review reported locations of rules #3769](https://github.com/pmd/pmd/issues/3769). While this effort
  is still ongoing, the following Java rules have been updated in this release:
  * {% rule java/bestpractices/AbstractClassWithoutAbstractMethod %}
  * {% rule java/design/AbstractClassWithoutAnyMethod %}
  * {% rule java/codestyle/AtLeastOneConstructor %}
  * {% rule java/codestyle/AvoidDollarSigns %}
  * {% rule java/errorprone/AvoidCatchingGenericException %}
  * {% rule java/multithreading/AvoidSynchronizedStatement %} (now reports only on synchronized keyword and not the whole synchronized block)
  * {% rule java/codestyle/ClassNamingConventions %}
  * {% rule java/design/ClassWithOnlyPrivateConstructorsShouldBeFinal %}
  * {% rule java/codestyle/CommentDefaultAccessModifier %}
  * {% rule java/documentation/CommentRequired %}
  * {% rule java/design/CouplingBetweenObjects %} (now reports only on class identifier and not whole compilation unit anymore)
  * {% rule java/design/CyclomaticComplexity %}
  * {% rule java/design/DataClass %}
  * {% rule java/design/ExcessiveImports %} (now reports only on imports and not the whole compilation unit anymore)
  * {% rule java/design/ExcessiveParameterList %}
  * {% rule java/design/ExcessivePublicCount %}
  * {% rule java/bestpractices/ExhaustiveSwitchHasDefault %} (now reports only on switch keyword and not the whole switch block)
  * {% rule java/design/GodClass %}
  * {% rule java/bestpractices/ImplicitFunctionalInterface %}
  * {% rule java/bestpractices/JUnit5TestShouldBePackagePrivate %}
  * {% rule java/codestyle/LocalHomeNamingConvention %}
  * {% rule java/codestyle/LocalInterfaceSessionNamingConvention %}
  * {% rule java/errorprone/MissingSerialVersionUID %}
  * {% rule java/errorprone/MissingStaticMethodInNonInstantiatableClass %}
  * {% rule java/design/NcssCount %}
  * {% rule java/bestpractices/NonExhaustiveSwitch %} (now reports only on switch keyword and not the whole switch block)
  * {% rule java/codestyle/NoPackage %}
  * {% rule java/design/PublicMemberInNonPublicType %}
  * {% rule java/codestyle/ShortClassName %}
  * {% rule java/errorprone/SingleMethodSingleton %}
  * {% rule java/design/SwitchDensity %} (now reports only on switch keyword and not the whole switch block)
  * {% rule java/errorprone/TestClassWithoutTestCases %}
  * {% rule java/performance/TooFewBranchesForSwitch %} (now reports only on switch keyword and not the whole switch block)
  * {% rule java/design/TooManyFields %} (now reports only on class identifier and not the whole class body anymore)
  * {% rule java/design/TooManyMethods %} (now reports only on class identifier and not the whole class body anymore)
  * {% rule java/codestyle/TooManyStaticImports %} (now reports only on the first static import and not the whole compilation unit anymore)
  * {% rule java/codestyle/UnnecessaryModifier %}
  * {% rule java/design/UseUtilityClass %}

### 🐛️ Fixed Issues
* java
  * [#5721](https://github.com/pmd/pmd/issues/5721): \[java] StackOverflowError in 7.17.0 with nested wildcard generics
* java-codestyle
  * [#6427](https://github.com/pmd/pmd/issues/6427): \[java] UnnecessaryCast: False positive for long cast before bit-shift operations on int/byte
* java-errorprone
  * [#6163](https://github.com/pmd/pmd/issues/6163): \[java] ConstructorCallsOverridableMethod: False positive when method is from enclosing class
  * [#6517](https://github.com/pmd/pmd/issues/6517): \[java] UselessPureMethodCall: False negative for methods on IntStream/LongStream/DoubleStream
* java-multithreading
  * [#6520](https://github.com/pmd/pmd/issues/6520): \[java] DoNotUseThreads: False positive on legitimate java.lang.Thread.onSpinWait() call

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6084](https://github.com/pmd/pmd/pull/6084): \[java] Shrink reported locations for some rules - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6522](https://github.com/pmd/pmd/pull/6522): \[java] Fix #6520: DoNotUseThreads: fix false positive on Thread.onSpinWait()  - [leemeii](https://github.com/leemeii) (@leemeii)
* [#6524](https://github.com/pmd/pmd/pull/6524): \[java] Fix #6517: UselessPureMethodCall: fix false negative for primitive streams - [leemeii](https://github.com/leemeii) (@leemeii)
* [#6553](https://github.com/pmd/pmd/pull/6553): \[java] Fix StackOverflowError in TypeOps projection of cyclic captured type vars - [Sebastian Lövdahl](https://github.com/slovdahl) (@slovdahl)
* [#6561](https://github.com/pmd/pmd/pull/6561): \[java] Fix #6163: ConstructorCallsOverridableMethod: False positive with call to enclosing class - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6573](https://github.com/pmd/pmd/pull/6573): \[java] Fix #6427: Add bitwise and/or/xor to BINARY_PROMOTED_OPS - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

