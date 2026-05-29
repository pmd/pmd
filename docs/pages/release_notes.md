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
#### Updated ANTLR library to 4.13.2
We have updated the ANTLR library (parser generator) from 4.9.3 to the latest version 4.13.2,
in order to be able to use the latest version of Apex parser library.

This is an incompatible update: In case you use custom language modules based on ANTLR, you
need to make sure to regenerate all of your lexers and parsers with the new ANTLR version.

For the ANTLR based language modules, that PMD ships (kotlin and swift and various CPD modules),
this is already done.

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule {% rule java/errorprone/JUnitJupiterTestNoPrivateModifier %} find JUnit test classes and
  methods that are private. Test classes, test methods, and lifecycle methods are not required to be public,
  but they must not be private. Otherwise, they won’t be found by the test framework.
* The new Java rule {% rule java/codestyle/UnnecessaryBlock %} reports blocks that are unnecessary as
  they don't introduce a new scope. This rule helps simplify code structure by identifying and flagging
  redundant blocks that can make code harder to read and may be misleading.
* The new Java rule {% rule java/codestyle/VariableDeclarationUsageDistance %} flags local variables that are declared
  far from their usage, which can make code harder to read. The rule has a property `maxDistance` that allows to
  configure the maximum allowed distance between declaration and usage.
* The new Java rule {% rule java/bestpractices/AssertStatementInTest %} detects usages of `assert` statement in tests.
  These should be replaced by framework assertion methods such as `assertEquals`.
  Such methods provide better error messages and make test behave correctly when running without `-ea`.

#### Changed Rules
* The rule {% rule java/codestyle/OnlyOneReturn %} has a new property `allowGuardIfs`. When this property is
  true, then guard ifs at the beginning of a method are allowed their return statements don't count.
* The rules {% rule java/design/UseUtilityClass %} and {% rule java/codestyle/ClassNamingConventions %} now use the
  same definition of what a utility class is. The most significant change is, that classes with `main()` methods are
  no longer considered utility classes by `UseUtilityClass`.
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

#### Renamed rules and properties

* One rule and one property have been renamed to reflect the fact that they work for both JUnit 5 and 6:
  * The rule {%rule java/bestpractices/JUnitJupiterTestShouldBePackagePrivate %} (Java Best Practices) was renamed from `JUnit5TestShouldBePackagePrivate`.
  * The property `junitJupiterTestPattern` of rule {% rule java/codestyle/MethodNamingConventions %} (Java Code Style) was renamed from `junit5TestPattern`.

The old names still work but are deprecated.

### 🐛️ Fixed Issues
* core
  * [#4972](https://github.com/pmd/pmd/issues/4972): \[core] Update ANTLR to 4.13.2
  * [#6308](https://github.com/pmd/pmd/issues/6308): \[core] CPD Markdown format: Add syntax highlighting
* doc
  * [#6708](https://github.com/pmd/pmd/issues/6708): \[doc] Update minimal Java version for building PMD in documentation
* java
  * [#1102](https://github.com/pmd/pmd/issues/1102): \[java] Improve consistency of utility class detection across rules
  * [#5721](https://github.com/pmd/pmd/issues/5721): \[java] StackOverflowError in 7.17.0 with nested wildcard generics
  * [#5746](https://github.com/pmd/pmd/issues/5746): \[java] Separate test sources and resources
  * [#6688](https://github.com/pmd/pmd/issues/6688): \[java] LocalVariableCouldBeFinalRule API changed
  * [#6704](https://github.com/pmd/pmd/issues/6704): \[java] Rename rules and properties with JUnit5 in the name
* java-bestpractices
  * [#3212](https://github.com/pmd/pmd/issues/3212): \[java] Enhance UseStandardCharsets to flag some constructors of IO-related classes
  * [#3777](https://github.com/pmd/pmd/issues/3777): \[java] New rule: AssertStatementInTest
  * [#5477](https://github.com/pmd/pmd/issues/5477): \[java] JUnit5TestShouldBePackagePrivate is not applied when @<!-- -->Test method is only present in parent class
  * [#6606](https://github.com/pmd/pmd/issues/6606): \[java] UnusedPrivateField: False positive on JUnit Jupiter @<!-- -->FieldSource
  * [#6681](https://github.com/pmd/pmd/issues/6681): \[java] UnitTestShouldIncludeAssert: False positive with JUnitSoftAssertions Rule (JUnit 4)
  * [#6710](https://github.com/pmd/pmd/issues/6710): \[java] UseStandardCharsets: False negative when using lowercase standard charset names
  * [#6719](https://github.com/pmd/pmd/issues/6719): \[java] UseStandardCharsets: False negative with Java 22+ and UTF-32 charsets
* java-codestyle
  * [#2801](https://github.com/pmd/pmd/issues/2801): \[java] OnlyOneReturn should have a property to allow early exits (guard clauses)
  * [#4350](https://github.com/pmd/pmd/issues/4350): \[java] ClassNamingConventions: testClassPattern not applied to class that inherits all its @<!-- -->Test methods
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
  * [#6712](https://github.com/pmd/pmd/issues/6712): \[java] UnnecessaryBooleanAssertion: Use InvocationMatcher to find assertions
* java-multithreading
  * [#6520](https://github.com/pmd/pmd/issues/6520): \[java] DoNotUseThreads: False positive on legitimate java.lang.Thread.onSpinWait() call
  * [#6636](https://github.com/pmd/pmd/issues/6636): \[java] OverridingThreadRun: Fix false negatives with other methods and anonymous classes
* kotlin
  * [#6608](https://github.com/pmd/pmd/issues/6608): \[kotlin] Lexer or parse errors are reported to stderr only without file context
  * [#6648](https://github.com/pmd/pmd/issues/6648): \[kotlin] Multi-dollar interpolation parse error in annotations
  * [#6659](https://github.com/pmd/pmd/issues/6659): \[kotlin] Parser hangs on complex files due to unbounded ATN prediction loop
  * [#6669](https://github.com/pmd/pmd/issues/6669): \[kotlin] Add AST improvements, KotlinAstUtil

### 🚨️ API Changes
#### Deprecations
* java
    * {% jdoc !!java::lang.java.rule.codestyle.FieldDeclarationsShouldBeAtStartOfClassRule#visit(java::lang.java.ast.ASTTypeDeclaration,java.lang.Object) %} is an implementation detail of {% jdoc java::lang.java.rule.codestyle.FieldDeclarationsShouldBeAtStartOfClassRule %}. It will be removed in a later release.
    * {% jdoc !!java::lang.java.rule.design.CyclomaticComplexityRule#visitTypeDecl(java::lang.java.ast.ASTTypeDeclaration,java.lang.Object) %} is an implementation detail of {% jdoc java::lang.java.rule.design.CyclomaticComplexityRule %}. It will be removed in a later release.
    * {% jdoc !!java::lang.java.rule.design.SwitchDensityRule#visitSwitchLike(java::lang.java.ast.ASTSwitchLike,java.lang.Object) %} is an implementation detail of {% jdoc java::lang.java.rule.design.SwitchDensityRule %}. It will be removed in a later release.
* kotlin
  * The constructor {%jdoc !!kotlin::lang.kotlin.ast.PmdKotlinParser#PmdKotlinParser() %} has been deprecated.
    Use {%jdoc !!kotlin::lang.kotlin.KotlinLanguageModule#getInstance() %},
    {%jdoc kotlin::lang.kotlin.KotlinLanguageModule#createProcessor(core::lang.LanguagePropertyBundle) %},
    {%jdoc kotlin::lang.kotlin.KotlinLanguageProcessor#services() %} and {%jdoc kotlin::lang.kotlin.KotlinHandler#getParser() %} instead
    to retrieve a correctly configured parser instance.
  * The constructor {%jdoc !!kotlin::lang.kotlin.KotlinHandler#KotlinHandler() %} has been deprecated.
    Use {%jdoc kotlin::lang.kotlin.KotlinLanguageModule#getInstance() %},
    {%jdoc kotlin::lang.kotlin.KotlinLanguageModule#createProcessor(core::lang.LanguagePropertyBundle) %} and
    {%jdoc kotlin::lang.kotlin.KotlinLanguageProcessor#services() %} instead to access the LanguageVersionHandler
    for Kotlin.
  * The methods {%jdoc !!kotlin::lang.kotlin.ast.KotlinInnerNode#getImage() %} and
    {%jdoc !!kotlin::lang.kotlin.ast.KotlinInnerNode#hasImageEqualTo(java.lang.String) %} have been deprecated.
    They have not been used yet in Kotlin and the long-term plan is to remove these methods on each node.
    Concrete nodes (subclasses of KotlinInnerNode) should provide a more specific attribute like
    "getName" or "getIdentifier" instead and not rely on "getImage".  
    The same deprecation has been done for {% jdoc kotlin::lang.kotlin.ast.KotlinTerminalNode %}.  
    See [#4787](https://github.com/pmd/pmd/issues/4787) for more information.

#### Experimental API
* kotlin
  * {%jdoc !!kotlin::lang.kotlin.KotlinLanguageProperties#PARSE_TIMEOUT_SECONDS %}
  * {%jdoc !!kotlin::lang.kotlin.KotlinLanguageProperties#getParseTimeoutSeconds() %}
  * Multiple classes have been added that provide an experimental way to add custom attributes to nodes:
    * {%jdoc kotlin::lang.kotlin.ast.AttributeView %}
    * {%jdoc kotlin::lang.kotlin.ast.KtClassDeclarationAttributes %}
    * {%jdoc kotlin::lang.kotlin.ast.KtClassParameterAttributes %}
    * {%jdoc kotlin::lang.kotlin.ast.KtCompanionObjectAttributes %}
    * {%jdoc kotlin::lang.kotlin.ast.KtFunctionDeclarationAttributes %}
    * {%jdoc kotlin::lang.kotlin.ast.KtImportAliasAttributes %}
    * {%jdoc kotlin::lang.kotlin.ast.KtImportHeaderAttributes %}
    * {%jdoc kotlin::lang.kotlin.ast.KtVariableDeclarationAttributes %}
    * {%jdoc kotlin::lang.kotlin.ast.HasModifiers %}
    * {%jdoc kotlin::lang.kotlin.ast.HasSimpleIdentifier %}
  * Attributes can be accessed on each node in Java-based rules via {%jdoc !!kotlin::lang.kotlin.ast.KotlinInnerNode#attributes(java.lang.Class) %}.  
    The attributes are also automatically exposed for XPath rules.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6084](https://github.com/pmd/pmd/pull/6084): \[java] Shrink reported locations for some rules - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6522](https://github.com/pmd/pmd/pull/6522): \[java] Fix #6520: DoNotUseThreads: fix false positive on Thread.onSpinWait()  - [leemeii](https://github.com/leemeii) (@leemeii)
* [#6524](https://github.com/pmd/pmd/pull/6524): \[java] Fix #6517: UselessPureMethodCall: fix false negative for primitive streams - [leemeii](https://github.com/leemeii) (@leemeii)
* [#6553](https://github.com/pmd/pmd/pull/6553): \[java] Fix StackOverflowError in TypeOps projection of cyclic captured type vars - [Sebastian Lövdahl](https://github.com/slovdahl) (@slovdahl)
* [#6557](https://github.com/pmd/pmd/pull/6557): \[java] New rule: AssertStatementInTest - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6561](https://github.com/pmd/pmd/pull/6561): \[java] Fix #6163: ConstructorCallsOverridableMethod: False positive with call to enclosing class - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6573](https://github.com/pmd/pmd/pull/6573): \[java] Fix #6427: Add bitwise and/or/xor to BINARY_PROMOTED_OPS - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6587](https://github.com/pmd/pmd/pull/6587): \[java] Fix #2801: Add a property to OnlyOneReturnRule to allow guard ifs - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6597](https://github.com/pmd/pmd/pull/6597): \[java] Fix #3212: Enhance UseStandardCharsets - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6601](https://github.com/pmd/pmd/pull/6601): \[java] Fix #4288: Document that CallSuperFirst and CallSuperLast are android only - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6603](https://github.com/pmd/pmd/pull/6603): \[java] Fix #6602: Fix false negative in LocalVariableCouldBeFinalRule - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6604](https://github.com/pmd/pmd/pull/6604): \[java] Fix #3288: New rule JUnit5TestNoPrivateModifierRule - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6605](https://github.com/pmd/pmd/pull/6605): \[java] Fix #6308: Add syntax highlighting to MarkdownRenderer - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6619](https://github.com/pmd/pmd/pull/6619): \[java] Fix #5746: Separate test sources and resources - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6623](https://github.com/pmd/pmd/pull/6623): \[java] Cleanup: Remove TODO from ModifierOwner.getVisibility() - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6636](https://github.com/pmd/pmd/pull/6636): \[java] OverridingThreadRun: Fix false negatives with other methods and anonymous classes - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6638](https://github.com/pmd/pmd/pull/6638): \[java] Fix #559: Improve UseUtilityClassRule to trigger also on static members - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6639](https://github.com/pmd/pmd/pull/6639): \[java] New rule: UnnecessaryBlock - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6640](https://github.com/pmd/pmd/pull/6640): \[java] New rule: VariableDeclarationUsageDistance - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6646](https://github.com/pmd/pmd/pull/6646): \[test] Split up AbstractRuleSetFactoryTest.testAllPMDBuiltInRulesMeetConventions() - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6650](https://github.com/pmd/pmd/pull/6650): \[kotlin] Fix #6608: Improve kotlin parser error handling - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6653](https://github.com/pmd/pmd/pull/6653): \[kotlin] Fix #6648: Multi-dollar interpolation for regular strings - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6654](https://github.com/pmd/pmd/pull/6654): \[swift] Fix invalid swift token OSXApplicationExtension - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6657](https://github.com/pmd/pmd/pull/6657): \[java] AvoidSynchronizedStatement: Improve rule doc - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6658](https://github.com/pmd/pmd/pull/6658): \[doc] Fix capitalization of ANTLR in release notes - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6660](https://github.com/pmd/pmd/pull/6660): \[kotlin] Fix #6659: Prevent parser hang via InterruptibleParserATNSimulator and parse timeout - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6661](https://github.com/pmd/pmd/pull/6661): \[java] Fix #6652: Support new-style instanceof (with pattern matching) in AvoidInstanceofChecksInCatchClause - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6670](https://github.com/pmd/pmd/pull/6670): \[kotlin] Add AST improvements, KotlinAstUtil - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6671](https://github.com/pmd/pmd/pull/6671): \[java] Part of #4841: Deprecate unnecessary public methods in FieldDeclarationsShouldBeAtStartOfClassRule/CyclomaticComplexityRule/SwitchDensityRule - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6679](https://github.com/pmd/pmd/pull/6679): \[chore] Fix typos in comments and documentation - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6680](https://github.com/pmd/pmd/pull/6680): \[java] Fix #5477: JUnit5TestShouldBePackagePrivate is not applied when @<!-- -->Test method is only present in parent class - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6683](https://github.com/pmd/pmd/pull/6683): Fix duplicated "the" in Java and Visualforce comments - [vip892766gma](https://github.com/vip892766gma) (@vip892766gma)
* [#6686](https://github.com/pmd/pmd/pull/6686): \[java] False positive for UnusedPrivateField referenced by @<!-- -->FieldSource - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6687](https://github.com/pmd/pmd/pull/6687): LocalVariableCouldBeFinalRule: Revert API change - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6689](https://github.com/pmd/pmd/pull/6689): \[java] Fix #4350: Fix ClassNamingConventions by teaching TestFrameworkUtil about type resolution - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6691](https://github.com/pmd/pmd/pull/6691): \[java] Fix #1102: improve consistency of utility class detection across rules - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6705](https://github.com/pmd/pmd/pull/6705): \[java] Fix #6681: UnitTestShouldIncludeAssert: False positive with JUnitSoftAssertions Rule (JUnit 4) - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6707](https://github.com/pmd/pmd/pull/6707): \[java] Fix #6704: rename rules and properties with JUnit5 in the name - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6708](https://github.com/pmd/pmd/pull/6708): \[doc] Update minimal Java version for building PMD in documentation - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6712](https://github.com/pmd/pmd/pull/6712): \[java] UnnecessaryBooleanAssertion: Use InvocationMatcher to find assertions - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6716](https://github.com/pmd/pmd/pull/6716): \[java] Fix #6710: Case insensitive comparison in UseStandardCharsets - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6726](https://github.com/pmd/pmd/pull/6726): \[java] Fix #6719: UseStandardCharsets UTF-32 on Java >= 22 - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6612](https://github.com/pmd/pmd/pull/6612): chore(deps): bump io.github.apex-dev-tools:apex-parser from 4.4.1 to 5.0.0
* [#6620](https://github.com/pmd/pmd/pull/6620): Bump PMD from 7.23.0 to 7.24.0
* [#6621](https://github.com/pmd/pmd/pull/6621): \[core] Fix #4972: Update ANTLR from 4.9.3 to 4.13.2
* [#6631](https://github.com/pmd/pmd/pull/6631): chore(deps): bump ruby/setup-ruby from 1.305.0 to 1.306.0
* [#6635](https://github.com/pmd/pmd/pull/6635): chore(deps): bump com.google.code.gson:gson from 2.13.2 to 2.14.0
* [#6642](https://github.com/pmd/pmd/pull/6642): chore(deps): bump crate-ci/typos from 1.45.1 to 1.46.0
* [#6643](https://github.com/pmd/pmd/pull/6643): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.4.0 to 13.4.2
* [#6644](https://github.com/pmd/pmd/pull/6644): chore(deps): bump org.checkerframework:checker-qual from 4.0.0 to 4.1.0
* [#6656](https://github.com/pmd/pmd/pull/6656): chore(deps): bump nokogiri from 1.19.2 to 1.19.3 in /.ci/files
* [#6662](https://github.com/pmd/pmd/pull/6662): chore(deps): bump actions/create-github-app-token from 3.1.1 to 3.2.0
* [#6663](https://github.com/pmd/pmd/pull/6663): chore(deps): bump scalameta.version from 4.16.1 to 4.17.0
* [#6664](https://github.com/pmd/pmd/pull/6664): chore(deps): bump ruby/setup-ruby from 1.306.0 to 1.307.0
* [#6666](https://github.com/pmd/pmd/pull/6666): chore(deps): bump crate-ci/typos from 1.46.0 to 1.46.1
* [#6665](https://github.com/pmd/pmd/pull/6665): chore(deps-dev): bump log4j.version from 2.25.4 to 2.26.0
* [#6667](https://github.com/pmd/pmd/pull/6667): chore(deps): bump org.apache.groovy:groovy from 5.0.5 to 5.0.6
* [#6668](https://github.com/pmd/pmd/pull/6668): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.25.6 to 0.25.7
* [#6697](https://github.com/pmd/pmd/pull/6697): chore(deps): bump ruby/setup-ruby from 1.307.0 to 1.308.0
* [#6698](https://github.com/pmd/pmd/pull/6698): chore(deps): bump junit.version from 6.0.3 to 6.1.0
* [#6699](https://github.com/pmd/pmd/pull/6699): chore(deps): bump crate-ci/typos from 1.46.1 to 1.46.2
* [#6700](https://github.com/pmd/pmd/pull/6700): chore(deps): bump org.apache.maven.plugins:maven-enforcer-plugin from 3.6.2 to 3.6.3
* [#6701](https://github.com/pmd/pmd/pull/6701): chore(deps): bump org.ow2.asm:asm from 9.9.1 to 9.10
* [#6702](https://github.com/pmd/pmd/pull/6702): chore(deps): bump com.google.protobuf:protobuf-java from 4.34.1 to 4.35.0
* [#6720](https://github.com/pmd/pmd/pull/6720): chore(deps): bump crate-ci/typos from 1.46.2 to 1.46.3
* [#6721](https://github.com/pmd/pmd/pull/6721): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.25.7 to 0.26.0
* [#6722](https://github.com/pmd/pmd/pull/6722): chore(deps): bump ruby/setup-ruby from 1.308.0 to 1.310.0
* [#6723](https://github.com/pmd/pmd/pull/6723): chore(deps): bump org.ow2.asm:asm from 9.10 to 9.10.1
* [#6724](https://github.com/pmd/pmd/pull/6724): chore(deps-dev): bump com.github.hazendaz.maven:coveralls-maven-plugin from 5.0.0 to 5.1.0
* [#6725](https://github.com/pmd/pmd/pull/6725): chore(deps-dev): Update tmp from 0.2.5 to 0.2.6
* [#6729](https://github.com/pmd/pmd/pull/6729): chore(deps-dev): bump build-tools from 37 to 38

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 308 commits
* 72 closed tickets & PRs
* Days since last release: 34

{% endtocmaker %}
