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
#### Kotlin XPath functions and type attributes
Type data is now accessible in XPath rules via new attributes and helper functions (see [Kotlin XPath rule support]({{ baseurl }}pmd_languages_kotlin.html#xpath-rule-support)):

* **Type-info Attributes**: `@TypeName`, `@ReturnTypeName`, `@AnnotationFqNames`
  are exposed on declaration nodes (property, function, class, parameter, catch, for-loop, delegation specifier,
  annotation nodes). These attributes depend on type resolution: they are only available when `auxClasspath`
  is configured and the kotlin-type-mapper analysis has resolved the types.
* **General Attributes**: `@Mutable`, `@Identifier`, `@Name`
  are exposed on declaration and import related nodes. These attributes don't depend on type resolution, so
  they're always present regardless of `auxClasspath`.
* **XPath functions**:
    * `pmd-kotlin:typeIs(typeName)`: matches if the node's type is `typeName` or a subtype.
    * `pmd-kotlin:typeIsExactly(typeName)`: matches the exact declared type only (no subtypes).
    * `pmd-kotlin:hasAnnotation(name)`: matches if the node has an annotation with the given simple or FQN.
    * `pmd-kotlin:modifiers()`: returns the modifier keywords of a declaration as a sequence.
    * `pmd-kotlin:isNullable()`: returns `true` if the node's declared type is nullable (has `?`).
    * `pmd-kotlin:hasUnresolvedReference()`: returns `true` if the node contains an unresolved reference.
    * `pmd-kotlin:matchesSig(signature)`: matches call sites by method signature pattern (supports wildcards).

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule {% rule java/bestpractices/OnDemandImport %} reports on-demand imports, also known as wildcard imports.
    By default, static imports from JUnit and TestNG are allowed. The allowed static and type import packages
    can be configured with `allowStaticImportsFrom` and `allowTypeImportsFrom`.
*   The new Java rule  {% rule java/errorprone/LongLiteralEndingWithLowercaseL %} finds long literals ending with a lowercase `l`.
    That helps to avoid confusion between numbers ending with `1` and `l`. Uppercase `L` should be used to define long literals.
*   The new Java rule  {% rule java/bestpractices/TypeNameMismatch %} finds types that are not defined in a .java file
    with the same name. Enforcing a match between source file name and type name makes it easier to
    find source code for given type.
*   The new Java rule {% rule java/codestyle/CStyleArrayDeclaration %} finds C-style declarations of arrays (e.g. `int numbers[]`).
    That helps you use Java-style declarations (e.g. `int[] numbers`) consistently throughout the codebase.
*   The new Java rule {% rule java/design/InternalApiUsage %} reports usages of internal or test-only APIs
    (e.g. annotated with `@VisibleForTesting`, `@TestOnly`, `@API(status=INTERNAL)` or `@ApiStatus.Internal`)
    from code that shouldn't depend on them.
*   The new Apex rule {% rule apex/bestpractices/ApexUnitTestClassShouldHaveRunRelevantTestsAnnotation %} finds unit tests
    that do not use the new `@IsTest(critical=true)` or `@IsTest(testFor='...')` annotation parameters for tests.
    These parameters help to identify which tests should be executed during a `RunRelevantTests` deployment.  
    Note: These annotation parameters are Beta and require Salesforce API 66.0+.

#### Changed Rules
*   The property `checkNonStaticMethods` of the rule {% rule java/multithreading/NonThreadSafeSingleton %} is now
    deprecated and no longer has any effect. Its implementation did the opposite of what the documentation described.
    The rule now always reports both static and non-static methods; previously it reported only static methods
    by default.  
    This may result in additional violations being reported.  
    If you want to suppress violations for non-static methods, you can use
    [suppression via XPath]({{ baseurl }}pmd_userdocs_suppressing_warnings.html#the-property-violationsuppressxpath), e.g.
    ```xml
    <property name="violationSuppressXPath" value=".[ancestor-or-self::MethodDeclaration[1][@Static = false()]]" />
    ```
*   The property `statementOrderMatters` of the rule {% rule java/codestyle/VariableCanBeInlined %} is now deprecated.
    Setting it to `false` relaxes the rule under the unsafe assumption that intervening statements have no side
    effects, which can lead to false positives. The property will be removed in PMD 8.0.0.

### 🐛️ Fixed Issues
* apex-bestpractices
    * [#6988](https://github.com/pmd/pmd/issues/6988): \[apex] New rule: Detect usage of @<!-- -->IsTest(critical=true) / @<!-- -->IsTest(testFor='...') annotations (RunRelevantTests, Beta, API 66.0+)
* core
    * [#7013](https://github.com/pmd/pmd/issues/7013): \[core] PMDConfiguration - "Can't mix setClasspath with getAuxClasspath!"
* cli
    * [#7090](https://github.com/pmd/pmd/issues/7090): \[cli] Add the missing exit code 5 to the CLI help
* html
    * [#6135](https://github.com/pmd/pmd/issues/6135): \[html] HtmlCpdLexer giving IndexOutOfBoundsException when script contains unescaped closing tag
* java
    * [#6926](https://github.com/pmd/pmd/issues/6926): \[java] IllegalArgumentException (Mismatched list sizes) with inconsistent unresolved generic arity
    * [#7056](https://github.com/pmd/pmd/issues/7056): \[java] Provide ability to disable auxClasspath warning added in 7.27.0
    * [#7081](https://github.com/pmd/pmd/issues/7081): \[java] NoSuchFileException when auxClasspath is given as a classpath file (file: URL) (since 7.27.0)
    * [#7101](https://github.com/pmd/pmd/issues/7101): \[java] ZipException when auxClasspath contains a non-jar file (since 7.27.0)
* java-bestpractices
    * [#5940](https://github.com/pmd/pmd/issues/5940): \[java] UnusedAssignment: False positive when assignment is in conditional statement
    * [#6901](https://github.com/pmd/pmd/issues/6901): \[java] MethodReturnsInternalArray: Various false negatives with local aliases and conditional expressions
    * [#7033](https://github.com/pmd/pmd/issues/7033): \[java] New rule: TypeNameMismatch
    * [#7047](https://github.com/pmd/pmd/issues/7047): \[java] New rule: OnDemandImport
* java-codestyle
    * [#3124](https://github.com/pmd/pmd/issues/3124): \[java] UnnecessaryLocalBeforeReturn/VariableCanBeInlined: deprecate property statementOrderMatters
    * [#5732](https://github.com/pmd/pmd/issues/5732): \[java] UnnecessaryCast false positive with package private methods
    * [#7026](https://github.com/pmd/pmd/issues/7026): \[java] New rule: CStyleArrayDeclaration
* java-design
    * [#6513](https://github.com/pmd/pmd/issues/6513): \[java] SimplifyConditional: False negative when null check and instanceof are separated by other && conditions
    * [#6694](https://github.com/pmd/pmd/issues/6694): \[java] SimplifyBooleanReturns triggers inconsistently depending on redundant parentheses in return expression
    * [#6889](https://github.com/pmd/pmd/issues/6889): \[java] New rule: InternalApiUsage
* java-documentation
    * [#6450](https://github.com/pmd/pmd/issues/6450): \[java] DanglingJavadoc: False positive on /// comments for Java < 23
* java-errorprone
    * [#1050](https://github.com/pmd/pmd/issues/1050): \[java] NullAssignment: False positive inside if statement for first assignment
    * [#6693](https://github.com/pmd/pmd/issues/6693): \[java] CloneMethodMustImplementCloneable: False positive with throw-via-local
    * [#7009](https://github.com/pmd/pmd/issues/7009): \[java] ReplaceJavaUtilDate: False negative when using pattern matching
    * [#7027](https://github.com/pmd/pmd/issues/7027): \[java] New rule: LongLiteralEndingWithLowercaseL
    * [#7068](https://github.com/pmd/pmd/issues/7068): \[java] UnusedReturnValue: False positive for calls made on Mockito.verify(mock)
* java-multithreading
    * [#6297](https://github.com/pmd/pmd/issues/6297): \[java] AvoidUsingVolatile: Update documentation
    * [#6780](https://github.com/pmd/pmd/issues/6780): \[java] NonThreadSafeSingleton: False negative with property checkNonStaticMethods
* java-security
    * [#7007](https://github.com/pmd/pmd/issues/7007): \[java] HardCodedCryptoKey: False negative when a hard-coded key is constructed via new String(char[])
    * [#7008](https://github.com/pmd/pmd/issues/7008): \[java] HardCodedCryptoKey: False positive when the key comes from System.getProperty()
* kotlin
    * [#6893](https://github.com/pmd/pmd/issues/6893): \[kotlin] Add XPath functions and type attributes
* miscellaneous
    * [#6961](https://github.com/pmd/pmd/issues/6961): \[doc] When a rule's description has a link to another rule in the exact wrong position, the doc generation crashes

### 🚨️ API Changes
#### Experimental API
* kotlin
    * {%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData %}: Provides the API to access type information
      on Kotlin AST nodes. It's part of the new Kotlin type-aware analysis.
    * `KotlinNodeTypeData.getTypeName(KotlinNode)` and `KotlinNodeTypeData.getReturnTypeName(KotlinNode)`
      have been renamed to {%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData#getType(KotlinNode) %}
      and {%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData#getReturnType(KotlinNode) %}, and
      now return {%jdoc kotlin::lang.kotlin.types.KotlinTypeName %} instead of `String`.
      The corresponding {%jdoc kotlin::lang.kotlin.types.InternalApiBridge %} setters
      `setTypeName`/`setReturnTypeName` were renamed to `setType`/`setReturnType` the same way.
      This is a breaking change to this experimental API.
    * {%jdoc kotlin::lang.kotlin.ast.HasTypeName %}: Marker interface for Kotlin AST nodes that expose
      a `@TypeName` XPath attribute.
    * New XPath functions `pmd-kotlin:typeIs`, `pmd-kotlin:typeIsExactly`, `pmd-kotlin:hasAnnotation`,
      `pmd-kotlin:modifiers`, `pmd-kotlin:matchesSig`, `pmd-kotlin:isNullable`, `pmd-kotlin:hasUnresolvedReference`
    * New AST attribute view classes in package `net.sourceforge.pmd.lang.kotlin.ast`:
      {%jdoc kotlin::lang.kotlin.ast.KtCatchBlockAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtDelegationSpecifierAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtForStatementAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtFunctionValueParameterAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtPropertyDeclarationAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtSingleAnnotationAttributes %}, and
      {%jdoc kotlin::lang.kotlin.ast.KtUnescapedAnnotationAttributes %}.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6864](https://github.com/pmd/pmd/pull/6864): \[java] Fix #6780: NonThreadSafeSingleton checkNonStaticMethods contradicting impl - [Subhadeep](https://github.com/dweep-js) (@dweep-js)
* [#6885](https://github.com/pmd/pmd/pull/6885): \[java] Fix #6693: CloneMethodMustImplementCloneable false positive for local-var throw - [hexonal](https://github.com/hexonal) (@hexonal)
* [#6893](https://github.com/pmd/pmd/pull/6893): \[kotlin] Add XPath functions and type attributes - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6922](https://github.com/pmd/pmd/pull/6922): \[java] Fix #6694: Trigger SimplifyBooleanReturns when expressions require parentheses - [Will-6543](https://github.com/Will-6543) (@Will-6543)
* [#6935](https://github.com/pmd/pmd/pull/6935): \[java] Fix #6926: skip file on unresolved generic arity mismatch - [Burak Kalaycı](https://github.com/kalayciburak) (@kalayciburak)
* [#6940](https://github.com/pmd/pmd/pull/6940): \[java] Fix #6901: MethodReturnsInternalArray: Track internal array escapes through expressions - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6946](https://github.com/pmd/pmd/pull/6946): \[java] Fix #6513: SimplifyConditional detects null check separated from instanceof by a && chain - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6951](https://github.com/pmd/pmd/pull/6951): \[java] Fix #6450: DanglingJavadoc should ignore markdown javadoc (///) before Java 23 - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6963](https://github.com/pmd/pmd/pull/6963): \[doc] Fix #6961: dont choke on cut off links - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6975](https://github.com/pmd/pmd/pull/6975): \[java] Prepare deprecation of asCtx in java-codestyle (part of #4814) - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6991](https://github.com/pmd/pmd/pull/6991): \[apex] Fix #6988: Add ApexUnitTestClassShouldHaveRunRelevantTestsAnnotation rule - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#6992](https://github.com/pmd/pmd/pull/6992): \[java] Fix #6297: Update AvoidUsingVolatile description - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6993](https://github.com/pmd/pmd/pull/6993): \[java] Fix #5732: UnnecessaryCast false positive for package-private members - [Tanvir Alam](https://github.com/tanvir-ux) (@tanvir-ux)
* [#6994](https://github.com/pmd/pmd/pull/6994): \[java] New rule: InternalApiUsage - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#7005](https://github.com/pmd/pmd/pull/7005): \[java] Fix rule reference from `ProtectedMemberInFinalField` to `ProtectedMemberInFinalClass` - [Piotrek Żygieło](https://github.com/pzygielo) (@pzygielo)
* [#7010](https://github.com/pmd/pmd/pull/7010): \[java] Fix #7009: ReplaceJavaUtilDate/Calendar miss pattern variables and record components - [renechoi](https://github.com/renechoi) (@renechoi)
* [#7011](https://github.com/pmd/pmd/pull/7011): \[doc] chore: Improve RuleTagChecker to find invalid in-ruleset references - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7012](https://github.com/pmd/pmd/pull/7012): \[java] Fix #7008: HardCodedCryptoKey: False positive when a default value of System.getProperty() is treated as a hard-coded key - [MakerYuichi](https://github.com/MakerYuichi) (@MakerYuichi)
* [#7016](https://github.com/pmd/pmd/pull/7016): \[java] Fix #5940: UnusedAssignment FP when constant operand short-circuits the condition - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#7019](https://github.com/pmd/pmd/pull/7019): \[core] Fix #7013: Only access PMDConfiguration.auxClasspath if it is actually set. - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7025](https://github.com/pmd/pmd/pull/7025): \[ci] gh-actions: Update environment variables for setup-java 6.0.0 - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7028](https://github.com/pmd/pmd/pull/7028): \[html] Fix #6135: HtmlCpdLexer giving IndexOutOfBoundsException when script contains unescaped closing tag - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#7029](https://github.com/pmd/pmd/pull/7029): \[java] Fix #7007: HardCodedCryptoKey detection for String(char\[]) - [suhanrain](https://github.com/suhanrain) (@suhanrain)
* [#7030](https://github.com/pmd/pmd/pull/7030): \[java] Fix #7027: New rule: LongLiteralEndingWithLowercaseL - [Copilot](https://github.com/Copilot) (@Copilot)
* [#7031](https://github.com/pmd/pmd/pull/7031): \[java] New rule: CStyleArrayDeclaration - [Copilot](https://github.com/Copilot) (@Copilot)
* [#7032](https://github.com/pmd/pmd/pull/7032): \[doc] Strip rule description when generating docs - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#7034](https://github.com/pmd/pmd/pull/7034): \[java] New rule: TypeNameMismatch - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#7035](https://github.com/pmd/pmd/pull/7035): \[java] Fix #1050: NullAssignment false positive inside if statement for first assignment - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#7052](https://github.com/pmd/pmd/pull/7052): \[java] New rule: OnDemandImport - [suhanrain](https://github.com/suhanrain) (@suhanrain)
* [#7061](https://github.com/pmd/pmd/pull/7061): \[core] Fix off-by-one error in AbstractJjtreeNode.fitTokensToChildren() - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7064](https://github.com/pmd/pmd/pull/7064): chore: Remove protobuf-java from <dependencyManagement> - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7065](https://github.com/pmd/pmd/pull/7065): \[java] Fix #3124: Deprecate statementOrderMatters property of VariableCanBeInlined - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#7069](https://github.com/pmd/pmd/pull/7069): \[java] Fix #7068: UnusedReturnValue false positive after Mockito.verify - [Burak Kalaycı](https://github.com/kalayciburak) (@kalayciburak)
* [#7084](https://github.com/pmd/pmd/pull/7084): \[java] Followup for #7008 (HardCodedCryptoKey): Fix FN when hard-coded fallback value is present - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#7085](https://github.com/pmd/pmd/pull/7085): \[java] Fix #7081: Exception when auxClasspath is a file URL - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7086](https://github.com/pmd/pmd/pull/7086): \[java] Fix #7056: Add JavaLanguageProperty to disable auxClasspath warnings - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7089](https://github.com/pmd/pmd/pull/7089): \[doc] TOC highlighting improvements - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#7090](https://github.com/pmd/pmd/pull/7090): \[cli] Add the missing exit code 5 to the CLI help - [Iain](https://github.com/NotAFlightRisk) (@NotAFlightRisk)
* [#7102](https://github.com/pmd/pmd/pull/7102): \[java] Fix #7101: Skip non-archive files on the auxclasspath - [Burak Kalaycı](https://github.com/kalayciburak) (@kalayciburak)
* [#7106](https://github.com/pmd/pmd/pull/7106): \[doc] Update release notes for 7.28.0 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7111](https://github.com/pmd/pmd/pull/7111): \[doc] Add gradle environment vars example - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5421](https://github.com/pmd/pmd/pull/5421): chore(deps): bump me.tongfei:progressbar from 0.9.5 to 0.10.2
* [#7004](https://github.com/pmd/pmd/pull/7004): Bump PMD from 7.26.0 to 7.27.0
* [#7021](https://github.com/pmd/pmd/pull/7021): chore(deps): bump org.apache.groovy:groovy from 5.1.0 to 5.1.1
* [#7022](https://github.com/pmd/pmd/pull/7022): chore(deps): bump crate-ci/typos from 1.49.0 to 1.50.0
* [#7023](https://github.com/pmd/pmd/pull/7023): chore(deps): bump actions/setup-java from 5.7.0 to 6.0.0
* [#7024](https://github.com/pmd/pmd/pull/7024): chore(deps): bump org.jsoup:jsoup from 1.23.1 to 1.23.2
* [#7037](https://github.com/pmd/pmd/pull/7037): chore(deps-dev): bump byte.buddy.version from 1.18.12 to 1.18.13
* [#7038](https://github.com/pmd/pmd/pull/7038): chore(deps-dev): bump org.projectlombok:lombok from 1.18.46 to 1.18.48
* [#7039](https://github.com/pmd/pmd/pull/7039): chore(deps-dev): bump ant.version from 1.10.17 to 1.10.18
* [#7040](https://github.com/pmd/pmd/pull/7040): chore(deps): bump surefire.version from 3.5.6 to 3.6.0
* [#7041](https://github.com/pmd/pmd/pull/7041): chore(deps): bump org.yaml:snakeyaml from 2.6 to 2.7
* [#7042](https://github.com/pmd/pmd/pull/7042): chore(deps-dev): bump io.github.git-commit-id:git-commit-id-maven-plugin from 10.0.0 to 10.0.1
* [#7043](https://github.com/pmd/pmd/pull/7043): chore(deps): bump com.puppycrawl.tools:checkstyle from 14.0.0 to 14.1.0
* [#7044](https://github.com/pmd/pmd/pull/7044): chore(deps): bump com.google.protobuf:protobuf-java from 4.36.0 to 4.36.1
* [#7045](https://github.com/pmd/pmd/pull/7045): chore(deps): bump org.apache.maven.plugins:maven-compiler-plugin from 3.15.0 to 3.16.0
* [#7046](https://github.com/pmd/pmd/pull/7046): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.26.1 to 0.26.2
* [#7049](https://github.com/pmd/pmd/pull/7049): chore(deps): bump crate-ci/typos from 1.50.0 to 1.50.1
* [#7051](https://github.com/pmd/pmd/pull/7051): chore(deps): bump org.apache.groovy:groovy from 5.1.1 to 5.1.2
* [#7058](https://github.com/pmd/pmd/pull/7058): chore(deps-dev): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.7.0.6970 to 5.8.0.7211
* [#7074](https://github.com/pmd/pmd/pull/7074): chore(deps-dev): bump org.codehaus.mojo:versions-maven-plugin from 2.21.0 to 2.22.0
* [#7075](https://github.com/pmd/pmd/pull/7075): chore(deps-dev): bump org.apache.maven.plugins:maven-install-plugin from 3.1.4 to 3.2.0
* [#7076](https://github.com/pmd/pmd/pull/7076): chore(deps): bump scalameta.version from 4.17.3 to 4.17.4
* [#7077](https://github.com/pmd/pmd/pull/7077): chore(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.6.3 to 3.6.4
* [#7078](https://github.com/pmd/pmd/pull/7078): chore(deps): bump org.codehaus.mojo:extra-enforcer-rules from 1.12.0 to 1.12.1
* [#7079](https://github.com/pmd/pmd/pull/7079): chore(deps-dev): bump org.apache.maven.plugins:maven-deploy-plugin from 3.1.4 to 3.2.0
* [#7080](https://github.com/pmd/pmd/pull/7080): chore(deps): bump org.codehaus.mojo:build-helper-maven-plugin from 3.6.1 to 3.6.2
* [#7082](https://github.com/pmd/pmd/pull/7082): chore(deps): bump actions/setup-java from 6.0.0 to 6.0.1
* [#7096](https://github.com/pmd/pmd/pull/7096): chore(deps-dev): bump byte.buddy.version from 1.18.13 to 1.18.14
* [#7095](https://github.com/pmd/pmd/pull/7095): chore(deps): bump crate-ci/typos from 1.50.1 to 1.50.2
* [#7097](https://github.com/pmd/pmd/pull/7097): chore(deps): bump bigdecimal from 4.1.2 to 4.1.3 in /docs
* [#7098](https://github.com/pmd/pmd/pull/7098): chore(deps): bump ruby/setup-ruby from 1.321.0 to 1.324.0

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 74 commits
* 69 closed tickets & PRs
* Days since last release: 27

{% endtocmaker %}
