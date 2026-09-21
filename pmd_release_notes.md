


## 25-September-2026 - 7.28.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.28.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Kotlin XPath functions and type attributes](#kotlin-xpath-functions-and-type-attributes)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Experimental API](#experimental-api)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy
#### Kotlin XPath functions and type attributes
Type data is now accessible in XPath rules via new attributes and helper functions (see [Kotlin XPath rule support](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_languages_kotlin.html#xpath-rule-support)):

* **Attributes**: `@TypeName`, `@ReturnTypeName`, `@AnnotationFqNames`, `@Modifiers`, `@Identifier`
  are exposed on declaration nodes (property, function, class, parameter, catch, for-loop, delegation specifier,
  annotation nodes).
* **`pmd-kotlin:typeIs(typeName)`**: matches if the node's type is `typeName` or a subtype.
* **`pmd-kotlin:typeIsExactly(typeName)`**: matches the exact declared type only (no subtypes).
* **`pmd-kotlin:hasAnnotation(name)`**: matches if the node has an annotation with the given simple or FQN.
* **`pmd-kotlin:modifiers()`**: returns the modifier keywords of a declaration as a sequence.
* **`pmd-kotlin:isNullable()`**: returns `true` if the node's declared type is nullable (has `?`).
* **`pmd-kotlin:hasUnresolvedReference()`**: returns `true` if the node contains an unresolved reference.
* **`pmd-kotlin:matchesSig(signature)`**: matches call sites by method signature pattern (supports wildcards).

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule [`OnDemandImport`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_bestpractices.html#ondemandimport) reports on-demand imports, also known as wildcard imports.
    By default, static imports from JUnit and TestNG are allowed. The allowed static and type import packages
    can be configured with `allowStaticImportsFrom` and `allowTypeImportsFrom`.
*   The new java rule  [`LongLiteralEndingWithLowercaseL`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_errorprone.html#longliteralendingwithlowercasel) finds long literals ending with l.
    That helps to avoid confusion between numbers ending with 1 and l. Capital L should be used to define long literals.
*   The new java rule  [`TypeNameMismatch`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_bestpractices.html#typenamemismatch) finds types that are not defined in a .java file
    with the same name. Enforcing a match between source file name and type name makes it easier to
    find source code for given type.
*   The new Java rule [`CStyleArrayDeclaration`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_codestyle.html#cstylearraydeclaration) finds C-style declarations of arrays (e.g. `int numbers[]`).
    That helps you use Java-style declarations (e.g. `int[] numbers`) consistently throughout the codebase.
*   The new Apex rule [`ApexUnitTestClassShouldHaveRunRelevantTestsAnnotation`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_apex_bestpractices.html#apexunittestclassshouldhaverunrelevanttestsannotation) finds unit tests
    that do not use the new `@IsTest(critical=true)` or `@IsTest(testFor='...')` annotation parameters for tests.
    These parameters help to identify which tests should be executed during a `RunRelevantTests` deployment.
#### Changed Rules
*   The property `checkNonStaticMethods` of the rule [`NonThreadSafeSingleton`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_multithreading.html#nonthreadsafesingleton) is now
    deprecated and no longer has any effect. Its implementation did the opposite of what the documentation described.
    The rule now always reports both static and non-static methods; previously it reported only static methods
    by default.  
    This may result in additional violations being reported.  
    If you want to suppress violations for non-static methods, you can use
    [suppression via XPath](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_userdocs_suppressing_warnings.html#the-property-violationsuppressxpath), e.g.
    ```xml
    <property name="violationSuppressXPath" value=".[ancestor-or-self::MethodDeclaration[1][@Static = false()]]" />
    ```
*   The property `statementOrderMatters` of the rule [`VariableCanBeInlined`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_codestyle.html#variablecanbeinlined) is now deprecated.
    Setting it to false only risks false negatives, therefore, the property will be removed in PMD 8.0.0.

### 🐛️ Fixed Issues
* apex-bestpractices
    * [#6988](https://github.com/pmd/pmd/issues/6988): \[apex] New rule: Detect usage of @<!-- -->IsTest(critical=true) / @<!-- -->IsTest(testFor='...') annotations (RunRelevantTests, Beta, API 66.0+)
* html
    * [#6135](https://github.com/pmd/pmd/issues/6135): \[html] HtmlCpdLexer giving IndexOutOfBoundsException when script contains unescaped closing tag
* java
    * [#6926](https://github.com/pmd/pmd/issues/6926): \[java] IllegalArgumentException (Mismatched list sizes) with inconsistent unresolved generic arity
* java-bestpractices
    * [#5940](https://github.com/pmd/pmd/issues/5940): \[java] False positive in UnusedAssignment when assignment is in conditional statement
* java-codestyle
    * [#3124](https://github.com/pmd/pmd/issues/3124): \[java] UnnecessaryLocalBeforeReturn/VariableCanBeInlined - remove property statementOrderMatters
    * [#5732](https://github.com/pmd/pmd/issues/5732): \[java] UnnecessaryCast false positive with package private methods
* java-design
    * [#6513](https://github.com/pmd/pmd/issues/6513): \[java] SimplifyConditional: False negative when null check and instanceof are separated by other && conditions
    * [#6694](https://github.com/pmd/pmd/issues/6694): \[java] SimplifyBooleanReturns triggers inconsistently depending on redundant parentheses in return expression
* java-documentation
    * [#6450](https://github.com/pmd/pmd/issues/6450): \[java] DanglingJavadoc: False positive on /// comments for Java < 23
* java-errorprone
    * [#1050](https://github.com/pmd/pmd/issues/1050): \[java] NullAssignment: False positive inside if statement for first assignment
    * [#6693](https://github.com/pmd/pmd/issues/6693): \[java] CloneMethodMustImplementCloneable fires inconsistently between inline `throw new` and throw-via-local forms
    * [#7009](https://github.com/pmd/pmd/issues/7009): \[java] ReplaceJavaUtilDate is suppressed by using pattern variable
    * [#7068](https://github.com/pmd/pmd/issues/7068): \[java] UnusedReturnValue reports calls made on Mockito.verify(mock)
* java-multithreading
    * [#6297](https://github.com/pmd/pmd/issues/6297): \[java] AvoidUsingVolatile: Update documentation
    * [#6780](https://github.com/pmd/pmd/issues/6780): \[java] NonThreadSafeSingleton: False negative with property checkNonStaticMethods
* java-security
    * [#7007](https://github.com/pmd/pmd/issues/7007): \[java] HardCodedCryptoKey: False negative when a hard-coded key is constructed via new String(char[])
    * [#7008](https://github.com/pmd/pmd/issues/7008): \[java] HardCodedCryptoKey: False positive when the key comes from System.getProperty()
* kotlin
    * [#6677](https://github.com/pmd/pmd/issues/6677): \[kotlin] Add XPath functions and type attributes for type-aware XPath rules

### 🚨️ API Changes
#### Experimental API
* kotlin
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/types/KotlinNodeTypeData.html#"><code>KotlinNodeTypeData</code></a>: Provides the initial API to access type information
      on Kotlin AST nodes. It's part of the new Kotlin type-aware analysis.
    * `KotlinNodeTypeData.getTypeName(KotlinNode)` and `KotlinNodeTypeData.getReturnTypeName(KotlinNode)`
      have been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/types/KotlinNodeTypeData.html#getType(KotlinNode)"><code>getType</code></a>
      and <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/types/KotlinNodeTypeData.html#getReturnType(KotlinNode)"><code>getReturnType</code></a>, and
      now return <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/types/KotlinTypeName.html#"><code>KotlinTypeName</code></a> instead of `String`.
      The corresponding <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/types/InternalApiBridge.html#"><code>InternalApiBridge</code></a> setters
      `setTypeName`/`setReturnTypeName` were renamed to `setType`/`setReturnType` the same way.
      This is a breaking change to this experimental API.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/HasTypeName.html#"><code>HasTypeName</code></a>: Marker interface for Kotlin AST nodes that expose
      a `@TypeName` XPath attribute.
    * New XPath functions `pmd-kotlin:typeIs`, `pmd-kotlin:typeIsExactly`, `pmd-kotlin:hasAnnotation`,
      `pmd-kotlin:modifiers`, `pmd-kotlin:matchesSig`, `pmd-kotlin:isNullable`, `pmd-kotlin:hasUnresolvedReference`
      in package `net.sourceforge.pmd.lang.kotlin.rule.xpath.internal`.
    * New AST attribute view classes in package `net.sourceforge.pmd.lang.kotlin.ast`:
      <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KtCatchBlockAttributes.html#"><code>KtCatchBlockAttributes</code></a>,
      <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KtDelegationSpecifierAttributes.html#"><code>KtDelegationSpecifierAttributes</code></a>,
      <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KtForStatementAttributes.html#"><code>KtForStatementAttributes</code></a>,
      <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KtFunctionValueParameterAttributes.html#"><code>KtFunctionValueParameterAttributes</code></a>,
      <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KtPropertyDeclarationAttributes.html#"><code>KtPropertyDeclarationAttributes</code></a>,
      <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KtSingleAnnotationAttributes.html#"><code>KtSingleAnnotationAttributes</code></a>, and
      <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.28.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KtUnescapedAnnotationAttributes.html#"><code>KtUnescapedAnnotationAttributes</code></a>.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->


