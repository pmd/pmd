


## 12-September-2025 - 7.17.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.17.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [✨ New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### ✨ New Rules
* The new apex rule [`AnnotationsNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_apex_codestyle.html#annotationsnamingconventions) enforces that annotations
  are used consistently in PascalCase.  
  The rule is referenced in the quickstart.xml ruleset for Apex.
* The new java rule [`TypeParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_codestyle.html#typeparameternamingconventions) replaces the now deprecated rule
  GenericsNaming. The new rule is configurable and checks for naming conventions of type parameters in
  generic types and methods. It can be configured via a regular expression.  
  By default, this rule uses the standard Java naming convention (single uppercase letter).  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule [`OverrideBothEqualsAndHashCodeOnComparable`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_errorprone.html#overridebothequalsandhashcodeoncomparable) finds missing
  `hashCode()` and/or `equals()` methods on types that implement `Comparable`. This is important if
  instances of these classes are used in collections. Failing to do so can lead to unexpected behavior in sets
  which then do not conform to the `Set` interface. While the `Set` interface relies on
  `equals()` to determine object equality, sorted sets like `TreeSet` use
  `compareTo()` instead. The same issue can arise when such objects are used
  as keys in sorted maps.  
  This rule is very similar to [`OverrideBothEqualsAndHashcode`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_errorprone.html#overridebothequalsandhashcode) which has always been
  skipping `Comparable` and only reports if one of the two methods is missing. The new rule will also report,
  if both methods (hashCode and equals) are missing.  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule [`UselessPureMethodCall`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_errorprone.html#uselesspuremethodcall) finds method calls of pure methods
  whose result is not used. Ignoring the result of such method calls is likely as mistake as pure
  methods are side effect free.  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule [`RelianceOnDefaultCharset`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_bestpractices.html#relianceondefaultcharset) finds method calls that
  depend on the JVM's default charset. Using these method without specifying the charset explicitly
  can lead to unexpected behavior on different platforms.

#### Deprecated Rules
* The java rule [`GenericsNaming`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_codestyle.html#genericsnaming) has been deprecated for removal in favor
  of the new rule [`TypeParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_codestyle.html#typeparameternamingconventions).
* The java rule [`AvoidLosingExceptionInformation`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_errorprone.html#avoidlosingexceptioninformation) has been deprecated for removal
  in favor of the new rule [`UselessPureMethodCall`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_errorprone.html#uselesspuremethodcall).
* The java rule [`UselessOperationOnImmutable`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_errorprone.html#uselessoperationonimmutable) has been deprecated for removal
  in favor of the new rule [`UselessPureMethodCall`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_errorprone.html#uselesspuremethodcall).

### 🐛 Fixed Issues
* apex-codestyle
  * [#5650](https://github.com/pmd/pmd/issues/5650): \[apex] New Rule: AnnotationsNamingConventions
* core
  * [#4721](https://github.com/pmd/pmd/issues/4721): chore: \[core] Enable XML rule MissingEncoding in dogfood ruleset
* java
  * [#5874](https://github.com/pmd/pmd/issues/5874): \[java] Update java regression tests with Java 25 language features
  * [#5960](https://github.com/pmd/pmd/issues/5960): \[java] Avoid/reduce duplicate error messages for some rules
* java-bestpractices
  * [#2186](https://github.com/pmd/pmd/issues/2186): \[java] New rule: Reliance on default charset
  * [#4500](https://github.com/pmd/pmd/issues/4500): \[java] AvoidReassigningLoopVariables - false negatives within for-loops and skip allowed
  * [#5198](https://github.com/pmd/pmd/issues/5198): \[java] CheckResultSet false-positive with local variable checked in a while loop
* java-codestyle
  * [#972](https://github.com/pmd/pmd/issues/972): \[java] Improve naming conventions rules
  * [#5948](https://github.com/pmd/pmd/issues/5948): \[java] UnnecessaryBoxing false positive when calling `List.remove(int)`
* java-design
  * [#5023](https://github.com/pmd/pmd/issues/5023): \[java] UseUtilityClass implementation hardcodes a message instead of using the one defined in the XML
* java-errorprone
  * [#3401](https://github.com/pmd/pmd/issues/3401): \[java] Improve AvoidUsingOctalValues documentation
  * [#5837](https://github.com/pmd/pmd/issues/5837): \[java] New Rule OverrideBothEqualsAndHashCodeOnComparable
  * [#5881](https://github.com/pmd/pmd/issues/5881): \[java] AvoidLosingExceptionInformation does not trigger when inside if-else
  * [#5915](https://github.com/pmd/pmd/issues/5915): \[java] AssignmentInOperand not raised when inside do-while loop

### 🚨 API Changes

#### Deprecations
* test
  * The method <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.17.0-SNAPSHOT/net/sourceforge/pmd/test/lang/rule/AbstractRuleSetFactoryTest.html#hasCorrectEncoding(java.lang.String)"><code>AbstractRuleSetFactoryTest#hasCorrectEncoding</code></a> will be removed.
    PMD has the rule [`MissingEncoding`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_xml_bestpractices.html#missingencoding) for XML files that should be used instead.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5822](https://github.com/pmd/pmd/pull/5822): Fix #5650: \[apex] New Rule: AnnotationsNamingConventions - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5856](https://github.com/pmd/pmd/pull/5856): Fix #5837: \[java] New Rule OverrideBothEqualsAndHashCodeOnComparable - [Vincent Potucek](https://github.com/Pankraz76) (@Pankraz76)
* [#5907](https://github.com/pmd/pmd/pull/5907): \[java] New rule: UselessPureMethodCall - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5922](https://github.com/pmd/pmd/pull/5922): Fix #972: \[java] Add a new rule TypeParameterNamingConventions - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5924](https://github.com/pmd/pmd/pull/5924): Fix #5915: \[java] Fix AssignmentInOperandRule to also work an do-while loops and switch statements - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5930](https://github.com/pmd/pmd/pull/5930): Fix #4500: \[java] Fix AvoidReassigningLoopVariablesRule to allow only simple assignments in the forReassign=skip case - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5931](https://github.com/pmd/pmd/pull/5931): Fix #5023: \[java] Fix UseUtilityClassRule to use the message provided in design.xml - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5932](https://github.com/pmd/pmd/pull/5932): \[ci] Reuse GitHub Pre-Releases - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5934](https://github.com/pmd/pmd/pull/5934): Fix #2186: \[java] New Rule: RelianceOnDefaultCharset - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5938](https://github.com/pmd/pmd/pull/5938): \[doc] Update suppression docs to reflect PMD 7 changes - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5939](https://github.com/pmd/pmd/pull/5939): Fix #5198: \[java] CheckResultSet FP when local variable is checked - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5954](https://github.com/pmd/pmd/pull/5954): Fix #4721: \[core] Enable XML rule MissingEncoding in dogfood ruleset - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5955](https://github.com/pmd/pmd/pull/5955): chore: Fix LiteralsFirstInComparison violations in test code - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5957](https://github.com/pmd/pmd/pull/5957): Fix #3401: \[java] Improve message/description/examples for AvoidUsingOctalValues - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5959](https://github.com/pmd/pmd/pull/5959): Fix #5960: \[java] AddEmptyString: Improve report location - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5961](https://github.com/pmd/pmd/pull/5961): Fix #5960: \[java] Add details to the error message for some rules - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5965](https://github.com/pmd/pmd/pull/5965): Fix #5881: AvoidLosingException - Consider nested method calls - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5967](https://github.com/pmd/pmd/pull/5967): \[doc]\[java] ReplaceJavaUtilDate - improve doc to mention java.sql.Date - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5970](https://github.com/pmd/pmd/pull/5970): chore: CI improvements - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5971](https://github.com/pmd/pmd/pull/5971): Fix #5948: \[java] UnnecessaryBoxingRule: Check if unboxing is required for overload resolution - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->



