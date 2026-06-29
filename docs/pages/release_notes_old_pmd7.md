---
title: Old Release Notes PMD 7.x
permalink: pmd_release_notes_old_pmd7.html
---

Previous versions of PMD can be downloaded here: [Releases - pmd/pmd (GitHub)](https://github.com/pmd/pmd/releases)



## 29-June-2026 - 7.26.0

The PMD team is pleased to announce PMD 7.26.0.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Swift Changes](#swift-changes)
    * [Updated PMD Designer](#updated-pmd-designer)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy
#### Swift Changes
The Swift parser now forwards syntax errors as usual processing errors. Before it just logged any errors and
tried to move on, resulting in an incomplete AST with error nodes. As part of this change, the grammar has been
slightly improved around macro declarations, generic parameters and parameter packs.  
This means that PMD might fail now on Swift files with processing errors, when it previously ran without
obvious problems. The Swift module in PMD now behaves like other modules in regard to error handling.

#### Updated PMD Designer
This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog (7.19.3)](https://github.com/pmd/pmd-designer/releases/tag/7.19.3).

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule [`WrongTestAnnotation`](https://docs.pmd-code.org/pmd-doc-7.26.0/pmd_rules_java_errorprone.html#wrongtestannotation) detects when test annotations from the wrong
  testing framework (JUnit 4, JUnit Jupiter, or TestNG) are used in your code, preventing tests from being silently
  skipped due to framework mismatches. This helps avoid the silent failure where tests compile but don't execute
  because the test runner doesn't recognize the annotation.
* The new Java rule [`AssertEqualsArgumentOrder`](https://docs.pmd-code.org/pmd-doc-7.26.0/pmd_rules_java_errorprone.html#assertequalsargumentorder) detects assertions
  where the expected and actual arguments were swapped. This helps find assertions
  that are producing a confusing error message when they fail.
* The new Kotlin rule [`LocalVariableShadowsParameter`](https://docs.pmd-code.org/pmd-doc-7.26.0/pmd_rules_kotlin_bestpractices.html#localvariableshadowsparameter) detects local variable
  declarations that use the same name as a parameter of the enclosing function. This shadows the parameter
  and may lead to confusion about which value is used.
* The new Apex rule [`InvocableClassNoArgConstructor`](https://docs.pmd-code.org/pmd-doc-7.26.0/pmd_rules_apex_errorprone.html#invocableclassnoargconstructor) detects classes that use
  `@InvocableVariable` properties, but that don't provide a no-arg constructor. Without such a constructor,
  runtime exception occur when Salesforce Flow tries to instantiate such classes.

#### Deprecated Rules
* The rule [`UseObjectForClearerAPI`](https://docs.pmd-code.org/pmd-doc-7.26.0/pmd_rules_java_design.html#useobjectforclearerapi) was deprecated. Use [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.26.0/pmd_rules_java_design.html#excessiveparameterlist)
  instead. The old rule name still works.

### 🐛️ Fixed Issues
* apex
  * [#6806](https://github.com/pmd/pmd/issues/6806): \[apex] ANTLR runtime mismatch 4.9.1 used for code generation does not match the current runtime version 4.13.2
* apex-errorprone
  * [#6793](https://github.com/pmd/pmd/issues/6793): \[apex] New Rule: Invocable Classes require a no argument constructor
* apex-security
  * [#2955](https://github.com/pmd/pmd/issues/2955): \[apex] ApexSOQLInjection: False positive when passing local var with concatenating strings
  * [#3877](https://github.com/pmd/pmd/issues/3877): \[apex] ApexCRUDViolation: False positive with Lists of Objects with getSObjectType().getDescribe()
* core
  * [#6764](https://github.com/pmd/pmd/issues/6764): \[core] ANTLR: Report syntax errors as processing errors
* cpp
  * [#6641](https://github.com/pmd/pmd/issues/6641): \[cpp]: IndexOutOfBoundsException in CPD when a duplication is at end of file with UTF8-BOM
* cli
  * [#6741](https://github.com/pmd/pmd/issues/6741): \[cli] Designer: Fix quotes in PMD_OPENJFX_MODULE_PATH setting
* java
  * [#6812](https://github.com/pmd/pmd/issues/6812): \[java] Rename ASTMethodDeclaration#isOverridden() to isOverride()
* java-bestpractices
  * [#6627](https://github.com/pmd/pmd/issues/6627): \[java] UnusedPrivateMethod: could not handle javax.annotation 
  * [#6692](https://github.com/pmd/pmd/issues/6692): \[java] ForLoopCanBeForeach: inconsistent detection between i += 1 and i = i + 1 update forms
  * [#6736](https://github.com/pmd/pmd/issues/6736): \[java] JUnitJupiterTestShouldBePackagePrivate: False negative when the only tests are in a @<!-- -->Nested class
  * [#6782](https://github.com/pmd/pmd/issues/6782): \[java] UseStandardCharsets: ArrayIndexOutOfBoundsException in line 81
* java-codestyle
  * [#6239](https://github.com/pmd/pmd/issues/6239): \[java] UseDiamondOperator: False positive with Guice TypeLiteral
  * [#6775](https://github.com/pmd/pmd/issues/6775): \[java] UselessParentheses: False negative when on the right-hand side of an assignment statement
* java-design
  * [#3741](https://github.com/pmd/pmd/issues/3741): \[java] Deprecate UseObjectForClearerAPI
  * [#6459](https://github.com/pmd/pmd/issues/6459): \[java] PublicMemberInNonPublicType: False positive for main(...) methods
  * [#6460](https://github.com/pmd/pmd/issues/6460): \[java] PublicMemberInNonPublicType: False negative for overridden methods
  * [#6814](https://github.com/pmd/pmd/issues/6814): \[java] AvoidDeepNestedIfStmts: count ifs properly in else branch
* java-errorprone
  * [#2846](https://github.com/pmd/pmd/issues/2846): \[java] New Rule: WrongTestAnnotation
  * [#5011](https://github.com/pmd/pmd/issues/5011): \[java] TestClassWithoutTestCases: False positive for test classes extending a class with tests (in nested classes)
  * [#6743](https://github.com/pmd/pmd/issues/6743): \[java] CloseResource: False positive for closeable initialized with (T) null
  * [#6781](https://github.com/pmd/pmd/issues/6781): \[java] UselessPureMethodCall: False positive for Stream.forEach
* java-performance
  * [#6740](https://github.com/pmd/pmd/issues/6740): \[java] OptimizableToArrayCall: False positive when new T\[0x0] is used instead of new T\[0]
* kotlin
  * [#6677](https://github.com/pmd/pmd/issues/6677): \[kotlin] Add auxClasspath language property
* kotlin-bestpractices
  * [#6732](https://github.com/pmd/pmd/issues/6732): \[kotlin] New Rule: LocalVariableShadowsParameter
* swift
  * [#6801](https://github.com/pmd/pmd/issues/6801): \[swift] Report syntax errors as processing errors

### 🚨️ API Changes
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.26.0/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrBaseParser.html#"><code>AntlrBaseParser</code></a> has been deprecated in favor of
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.26.0/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrBaseParserWithErrorHandling.html#"><code>AntlrBaseParserWithErrorHandling</code></a>, which converts ANTLR's parsing
    errors into PMD's processing errors by default.
* java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.26.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#isOverridden()"><code>ASTMethodDeclaration#isOverridden</code></a> has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.26.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#isOverride()"><code>isOverride</code></a>.
    The old name has been deprecated and will remain available until PMD 8.  
    The corresponding XPath attribute `@Overridden` is deprecated as well. Use `@Override` instead.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6678](https://github.com/pmd/pmd/pull/6678): \[kotlin] Fix #6677: Add auxClasspath language property - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6703](https://github.com/pmd/pmd/pull/6703): \[cpp] Fix #6641: CPD: IndexOutOfBoundsException when a duplication is at end of file with UTF8-BOM - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6713](https://github.com/pmd/pmd/pull/6713): \[java] New rule: AssertEqualsArgumentOrder - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6727](https://github.com/pmd/pmd/pull/6727): \[java] Fix #6692: ForLoopCanBeForeach detect i = i + 1 update form - [hyeonjune](https://github.com/qwerty7878) (@qwerty7878)
* [#6728](https://github.com/pmd/pmd/pull/6728): chore: Fix pmd test setup - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6730](https://github.com/pmd/pmd/pull/6730): \[core] RuleSetWriter: fix indent-number attribute - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6733](https://github.com/pmd/pmd/pull/6733): \[kotlin] Fix #6732: Add LocalVariableShadowsParameter rule - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6735](https://github.com/pmd/pmd/pull/6735): \[java] Fix #2846: New Rule: WrongTestAnnotation - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6738](https://github.com/pmd/pmd/pull/6738): \[java] Fix #6736: Add JTypeMirror.streamClasses() - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6741](https://github.com/pmd/pmd/pull/6741): \[cli] Designer: Fix quotes in PMD_OPENJFX_MODULE_PATH setting - [Philip Graf](https://github.com/acanda) (@acanda)
* [#6745](https://github.com/pmd/pmd/pull/6745): \[java] Fix #6239: UseDiamondOperator: Implement heuristic for Super Type Token Pattern - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6748](https://github.com/pmd/pmd/pull/6748): \[java] Fix #6743: CloseResource: False positive for closeable initialized with (T) null - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6761](https://github.com/pmd/pmd/pull/6761): chore: Fix PMD issues from new dogfood rules - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6763](https://github.com/pmd/pmd/pull/6763): \[doc] Split old release notes page - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6764](https://github.com/pmd/pmd/pull/6764): \[core] ANTLR: Report syntax errors as processing errors - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6765](https://github.com/pmd/pmd/pull/6765): \[apex] Fix #2955: ApexSOQLInjection: False positive when concatenating strings - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6767](https://github.com/pmd/pmd/pull/6767): \[chore] #6641: Remove comment from test-data to reproduce original issue - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6769](https://github.com/pmd/pmd/pull/6769): \[apex] Fix #3877: ApexCRUDViolation false positive on Lists of Objects with getSObjectType().getDescribe() - [Lukman Hakim](https://github.com/lukman48) (@lukman48)
* [#6776](https://github.com/pmd/pmd/pull/6776): \[java] Fix #6775: UselessParentheses: not reported on the right-hand side of an as… - [Subhadeep](https://github.com/dweep-js) (@dweep-js)
* [#6777](https://github.com/pmd/pmd/pull/6777): \[chore] #6641: Further improve the test - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6778](https://github.com/pmd/pmd/pull/6778): \[java] Fix examples in javadocs for InvocationMatcher - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6779](https://github.com/pmd/pmd/pull/6779): \[java] Fix #3741: Deprecate UseObjectForClearerApi - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6783](https://github.com/pmd/pmd/pull/6783): \[java] Fix #6782: Add missing check for varargs - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6791](https://github.com/pmd/pmd/pull/6791): chore: Keep .ci/tools/typos.sh version in sync with github actions - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6792](https://github.com/pmd/pmd/pull/6792): \[ci] chore: Improve publish-release job dependencies - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6794](https://github.com/pmd/pmd/pull/6794): \[apex] New rule: InvocableClassNoArgConstructor - [Daniel Ballinger](https://github.com/FishOfPrey) (@FishOfPrey)
* [#6796](https://github.com/pmd/pmd/pull/6796): \[java] Fix #6460: Fix false negative for overridden methods in PublicMemberInNonPublicType - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6797](https://github.com/pmd/pmd/pull/6797): \[java] Fix #6781: False positive in UselessPureMethodCall with unresolved types - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6801](https://github.com/pmd/pmd/pull/6801): \[swift] Report syntax errors as processing errors - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6802](https://github.com/pmd/pmd/pull/6802): \[java] #6461: PublicMemberInNonPublicType: Verify that we can suppress on the method - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6803](https://github.com/pmd/pmd/pull/6803): \[chore] git-commit-id-maven-plugin: Use native git - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6804](https://github.com/pmd/pmd/pull/6804): \[java] Fix #6459: special case main in PublicMemberInNonPublicType - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6805](https://github.com/pmd/pmd/pull/6805): \[java] #4960: Add regression test for UnusedAssignment - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6807](https://github.com/pmd/pmd/pull/6807): \[apex] Fix #6806: Upgrade vf-parser to 2.0.0-beta.1 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6809](https://github.com/pmd/pmd/pull/6809): \[java] Fix #5011: Fix FP in TestClassWithoutTestCases when the only tests are in a superclass - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6812](https://github.com/pmd/pmd/pull/6812): \[java] Rename ASTMethodDeclaration#isOverridden() to isOverride() - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6813](https://github.com/pmd/pmd/pull/6813): \[java] Fix #6740: Fix FP in OptimizableToArrayCall - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6814](https://github.com/pmd/pmd/pull/6814): \[java] AvoidDeepNestedIfStmts: count ifs properly in else branch - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6821](https://github.com/pmd/pmd/pull/6821): \[java] Fix #6627: UnusedPrivateMethod: add javax to ignored annotations - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6734](https://github.com/pmd/pmd/pull/6734): Bump PMD from 7.24.0 to 7.25.0
* [#6739](https://github.com/pmd/pmd/pull/6739): chore: Remove maven-shade-plugin from <pluginManagement>
* [#6751](https://github.com/pmd/pmd/pull/6751): chore(deps): bump crate-ci/typos from 1.46.3 to 1.47.2
* [#6752](https://github.com/pmd/pmd/pull/6752): chore(deps): bump actions/checkout from 6.0.2 to 6.0.3
* [#6754](https://github.com/pmd/pmd/pull/6754): chore(deps): bump org.checkerframework:checker-qual from 4.1.0 to 4.2.0
* [#6755](https://github.com/pmd/pmd/pull/6755): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.8 to 1.18.10
* [#6756](https://github.com/pmd/pmd/pull/6756): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.4.2 to 13.5.0
* [#6757](https://github.com/pmd/pmd/pull/6757): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.8 to 1.18.10
* [#6758](https://github.com/pmd/pmd/pull/6758): chore(deps): bump org.apache.maven.plugins:maven-dependency-plugin from 3.10.0 to 3.11.0
* [#6759](https://github.com/pmd/pmd/pull/6759): chore(deps): bump surefire.version from 3.5.5 to 3.5.6
* [#6760](https://github.com/pmd/pmd/pull/6760): chore: Keep versions of byte-buddy and byte-buddy-agent in sync
* [#6762](https://github.com/pmd/pmd/pull/6762): Bump build-tools from 38 to 39
* [#6770](https://github.com/pmd/pmd/pull/6770): chore(deps): bump org.jacoco:jacoco-maven-plugin from 0.8.14 to 0.8.15
* [#6771](https://github.com/pmd/pmd/pull/6771): chore(deps): bump ruby/setup-ruby from 1.310.0 to 1.312.0
* [#6772](https://github.com/pmd/pmd/pull/6772): chore(deps-dev): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.6.0.6792 to 5.7.0.6970
* [#6773](https://github.com/pmd/pmd/pull/6773): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.26.0 to 0.26.1
* [#6784](https://github.com/pmd/pmd/pull/6784): chore(deps): bump actions/setup-java from 5.2.0 to 5.3.0
* [#6785](https://github.com/pmd/pmd/pull/6785): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.5.0 to 13.6.0
* [#6786](https://github.com/pmd/pmd/pull/6786): chore(deps): bump ruby/setup-ruby from 1.312.0 to 1.313.0
* [#6787](https://github.com/pmd/pmd/pull/6787): chore(deps): bump com.google.protobuf:protobuf-java from 4.35.0 to 4.35.1
* [#6788](https://github.com/pmd/pmd/pull/6788): chore(deps): bump org.sonatype.central:central-publishing-maven-plugin from 0.10.0 to 0.11.0
* [#6789](https://github.com/pmd/pmd/pull/6789): chore(deps-dev): bump tmp from 0.2.6 to 0.2.7
* [#6798](https://github.com/pmd/pmd/pull/6798): chore(deps): bump concurrent-ruby from 1.3.6 to 1.3.7 in /docs
* [#6799](https://github.com/pmd/pmd/pull/6799): chore(deps): bump concurrent-ruby from 1.3.6 to 1.3.7 in /.ci/files
* [#6800](https://github.com/pmd/pmd/pull/6800): chore(deps): bump nokogiri from 1.19.3 to 1.19.4 in /.ci/files
* [#6810](https://github.com/pmd/pmd/pull/6810): Update Designer to 7.19.3
* [#6815](https://github.com/pmd/pmd/pull/6815): chore(deps): bump actions/checkout from 6.0.3 to 7.0.0
* [#6816](https://github.com/pmd/pmd/pull/6816): chore(deps): bump actions/cache from 5.0.5 to 6.0.0
* [#6817](https://github.com/pmd/pmd/pull/6817): chore(deps): bump ruby/setup-ruby from 1.313.0 to 1.314.0
* [#6819](https://github.com/pmd/pmd/pull/6819): chore(deps): bump org.cyclonedx:cyclonedx-maven-plugin from 2.9.1 to 2.9.2
* [#6820](https://github.com/pmd/pmd/pull/6820): chore(deps-dev): bump commons-logging:commons-logging from 1.3.6 to 1.4.0

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 151 commits
* 60 closed tickets & PRs
* Days since last release: 31



## 29-May-2026 - 7.25.0

The PMD team is pleased to announce PMD 7.25.0.

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
    * [Deprecations](#deprecations)
    * [Experimental API](#experimental-api)
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
* The new Java rule [`JUnitJupiterTestNoPrivateModifier`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_errorprone.html#junitjupitertestnoprivatemodifier) find JUnit test classes and
  methods that are private. Test classes, test methods, and lifecycle methods are not required to be public,
  but they must not be private. Otherwise, they won’t be found by the test framework.
* The new Java rule [`UnnecessaryBlock`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#unnecessaryblock) reports blocks that are unnecessary as
  they don't introduce a new scope. This rule helps simplify code structure by identifying and flagging
  redundant blocks that can make code harder to read and may be misleading.
* The new Java rule [`VariableDeclarationUsageDistance`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#variabledeclarationusagedistance) flags local variables that are declared
  far from their usage, which can make code harder to read. The rule has a property `maxDistance` that allows to
  configure the maximum allowed distance between declaration and usage.
* The new Java rule [`AssertStatementInTest`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_bestpractices.html#assertstatementintest) detects usages of `assert` statement in tests.
  These should be replaced by framework assertion methods such as `assertEquals`.
  Such methods provide better error messages and make test behave correctly when running without `-ea`.

#### Changed Rules
* The rule [`OnlyOneReturn`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#onlyonereturn) has a new property `allowGuardIfs`. When this property is
  true, then guard ifs at the beginning of a method are allowed their return statements don't count.
* The rules [`UseUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#useutilityclass) and [`ClassNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#classnamingconventions) now use the
  same definition of what a utility class is. The most significant change is, that classes with `main()` methods are
  no longer considered utility classes by `UseUtilityClass`.
* We are continuously working to improve the precision of violation reporting for various rules.
  The goal is to ensure that rules report issues on the correct line and highlight only the relevant lines.
  For example, instead of flagging an entire class declaration (including its body), we now generally report only
  the class name. For more details, see [[java] Single Line Warnings #730](https://github.com/pmd/pmd/issues/730)
  and [[java] Review reported locations of rules #3769](https://github.com/pmd/pmd/issues/3769). While this effort
  is still ongoing, the following Java rules have been updated in this release:
  * [`AbstractClassWithoutAbstractMethod`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_bestpractices.html#abstractclasswithoutabstractmethod)
  * [`AbstractClassWithoutAnyMethod`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#abstractclasswithoutanymethod)
  * [`AtLeastOneConstructor`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#atleastoneconstructor)
  * [`AvoidDollarSigns`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#avoiddollarsigns)
  * [`AvoidCatchingGenericException`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_errorprone.html#avoidcatchinggenericexception)
  * [`AvoidSynchronizedStatement`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_multithreading.html#avoidsynchronizedstatement) (now reports only on synchronized keyword and not the whole synchronized block)
  * [`ClassNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#classnamingconventions)
  * [`ClassWithOnlyPrivateConstructorsShouldBeFinal`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#classwithonlyprivateconstructorsshouldbefinal)
  * [`CommentDefaultAccessModifier`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier)
  * [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_documentation.html#commentrequired)
  * [`CouplingBetweenObjects`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#couplingbetweenobjects) (now reports only on class identifier and not whole compilation unit anymore)
  * [`CyclomaticComplexity`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#cyclomaticcomplexity)
  * [`DataClass`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#dataclass)
  * [`ExcessiveImports`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#excessiveimports) (now reports only on imports and not the whole compilation unit anymore)
  * [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#excessiveparameterlist)
  * [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#excessivepubliccount)
  * [`ExhaustiveSwitchHasDefault`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_bestpractices.html#exhaustiveswitchhasdefault) (now reports only on switch keyword and not the whole switch block)
  * [`GodClass`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#godclass)
  * [`ImplicitFunctionalInterface`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_bestpractices.html#implicitfunctionalinterface)
  * [`JUnit5TestShouldBePackagePrivate`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_bestpractices.html#junit5testshouldbepackageprivate)
  * [`LocalHomeNamingConvention`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#localhomenamingconvention)
  * [`LocalInterfaceSessionNamingConvention`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#localinterfacesessionnamingconvention)
  * [`MissingSerialVersionUID`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_errorprone.html#missingserialversionuid)
  * [`MissingStaticMethodInNonInstantiatableClass`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_errorprone.html#missingstaticmethodinnoninstantiatableclass)
  * [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#ncsscount)
  * [`NonExhaustiveSwitch`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_bestpractices.html#nonexhaustiveswitch) (now reports only on switch keyword and not the whole switch block)
  * [`NoPackage`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#nopackage)
  * [`PublicMemberInNonPublicType`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#publicmemberinnonpublictype)
  * [`ShortClassName`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#shortclassname)
  * [`SingleMethodSingleton`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_errorprone.html#singlemethodsingleton)
  * [`SwitchDensity`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#switchdensity) (now reports only on switch keyword and not the whole switch block)
  * [`TestClassWithoutTestCases`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_errorprone.html#testclasswithouttestcases)
  * [`TooFewBranchesForSwitch`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_performance.html#toofewbranchesforswitch) (now reports only on switch keyword and not the whole switch block)
  * [`TooManyFields`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#toomanyfields) (now reports only on class identifier and not the whole class body anymore)
  * [`TooManyMethods`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#toomanymethods) (now reports only on class identifier and not the whole class body anymore)
  * [`TooManyStaticImports`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#toomanystaticimports) (now reports only on the first static import and not the whole compilation unit anymore)
  * [`UnnecessaryModifier`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#unnecessarymodifier)
  * [`UseUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_design.html#useutilityclass)

#### Renamed rules and properties

* One rule and one property have been renamed to reflect the fact that they work for both JUnit 5 and 6:
  * The rule [`JUnitJupiterTestShouldBePackagePrivate`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_bestpractices.html#junitjupitertestshouldbepackageprivate) (Java Best Practices) was renamed from `JUnit5TestShouldBePackagePrivate`.
  * The property `junitJupiterTestPattern` of rule [`MethodNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.25.0/pmd_rules_java_codestyle.html#methodnamingconventions) (Java Code Style) was renamed from `junit5TestPattern`.

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
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.25.0/net/sourceforge/pmd/lang/java/rule/codestyle/FieldDeclarationsShouldBeAtStartOfClassRule.html#visit(net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration,java.lang.Object)"><code>FieldDeclarationsShouldBeAtStartOfClassRule#visit</code></a> is an implementation detail of <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.25.0/net/sourceforge/pmd/lang/java/rule/codestyle/FieldDeclarationsShouldBeAtStartOfClassRule.html#"><code>FieldDeclarationsShouldBeAtStartOfClassRule</code></a>. It will be removed in a later release.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.25.0/net/sourceforge/pmd/lang/java/rule/design/CyclomaticComplexityRule.html#visitTypeDecl(net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration,java.lang.Object)"><code>CyclomaticComplexityRule#visitTypeDecl</code></a> is an implementation detail of <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.25.0/net/sourceforge/pmd/lang/java/rule/design/CyclomaticComplexityRule.html#"><code>CyclomaticComplexityRule</code></a>. It will be removed in a later release.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.25.0/net/sourceforge/pmd/lang/java/rule/design/SwitchDensityRule.html#visitSwitchLike(net.sourceforge.pmd.lang.java.ast.ASTSwitchLike,java.lang.Object)"><code>SwitchDensityRule#visitSwitchLike</code></a> is an implementation detail of <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.25.0/net/sourceforge/pmd/lang/java/rule/design/SwitchDensityRule.html#"><code>SwitchDensityRule</code></a>. It will be removed in a later release.
* kotlin
  * The constructor <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/PmdKotlinParser.html#PmdKotlinParser()"><code>PmdKotlinParser#PmdKotlinParser</code></a> has been deprecated.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinLanguageModule.html#getInstance()"><code>KotlinLanguageModule#getInstance</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinLanguageModule.html#createProcessor(net.sourceforge.pmd.lang.LanguagePropertyBundle)"><code>createProcessor</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinLanguageProcessor.html#services()"><code>services</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinHandler.html#getParser()"><code>getParser</code></a> instead
    to retrieve a correctly configured parser instance.
  * The constructor <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinHandler.html#KotlinHandler()"><code>KotlinHandler#KotlinHandler</code></a> has been deprecated.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinLanguageModule.html#getInstance()"><code>getInstance</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinLanguageModule.html#createProcessor(net.sourceforge.pmd.lang.LanguagePropertyBundle)"><code>createProcessor</code></a> and
    <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinLanguageProcessor.html#services()"><code>services</code></a> instead to access the LanguageVersionHandler
    for Kotlin.
  * The methods <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KotlinInnerNode.html#getImage()"><code>KotlinInnerNode#getImage</code></a> and
    <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KotlinInnerNode.html#hasImageEqualTo(java.lang.String)"><code>KotlinInnerNode#hasImageEqualTo</code></a> have been deprecated.
    They have not been used yet in Kotlin and the long-term plan is to remove these methods on each node.
    Concrete nodes (subclasses of KotlinInnerNode) should provide a more specific attribute like
    "getName" or "getIdentifier" instead and not rely on "getImage".  
    The same deprecation has been done for <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KotlinTerminalNode.html#"><code>KotlinTerminalNode</code></a>.  
    See [#4787](https://github.com/pmd/pmd/issues/4787) for more information.

#### Experimental API
* kotlin
  * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinLanguageProperties.html#PARSE_TIMEOUT_SECONDS"><code>KotlinLanguageProperties#PARSE_TIMEOUT_SECONDS</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/KotlinLanguageProperties.html#getParseTimeoutSeconds()"><code>KotlinLanguageProperties#getParseTimeoutSeconds</code></a>
  * Multiple classes have been added that provide an experimental way to add custom attributes to nodes:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/AttributeView.html#"><code>AttributeView</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KtClassDeclarationAttributes.html#"><code>KtClassDeclarationAttributes</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KtClassParameterAttributes.html#"><code>KtClassParameterAttributes</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KtCompanionObjectAttributes.html#"><code>KtCompanionObjectAttributes</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KtFunctionDeclarationAttributes.html#"><code>KtFunctionDeclarationAttributes</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KtImportAliasAttributes.html#"><code>KtImportAliasAttributes</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KtImportHeaderAttributes.html#"><code>KtImportHeaderAttributes</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KtVariableDeclarationAttributes.html#"><code>KtVariableDeclarationAttributes</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/HasModifiers.html#"><code>HasModifiers</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/HasSimpleIdentifier.html#"><code>HasSimpleIdentifier</code></a>
  * Attributes can be accessed on each node in Java-based rules via <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.25.0/net/sourceforge/pmd/lang/kotlin/ast/KotlinInnerNode.html#attributes(java.lang.Class)"><code>KotlinInnerNode#attributes</code></a>.  
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



## 24-April-2026 - 7.24.0

The PMD team is pleased to announce PMD 7.24.0.

This is a minor release.

### Table Of Contents

* [🌟️ New Rules](#new-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🌟️ New Rules
* The new Apex rule [`AvoidInterfaceAsMapKey`](https://docs.pmd-code.org/pmd-doc-7.24.0/pmd_rules_apex_errorprone.html#avoidinterfaceasmapkey) reports `Map` declarations
  (fields, variables, parameters) whose key type is an interface that has at least one abstract implementing
  class defining `equals` or `hashCode`. Using such maps results in potentially duplicated map entries or
  not being able to get entries by key.
* The new Java rule [`OverridingThreadRun`](https://docs.pmd-code.org/pmd-doc-7.24.0/pmd_rules_java_multithreading.html#overridingthreadrun) finds overridden `Thread::run` methods.
  This is not recommended. Instead, implement `Runnable` and pass an instance to the thread constructor.

### 🐛️ Fixed Issues
* apex
  * [#5386](https://github.com/pmd/pmd/issues/5386): \[apex] Apex files ending in "Test" are skipped with a number of rules
* apex-errorprone
  * [#6492](https://github.com/pmd/pmd/issues/6492): \[apex] New rule: Prevent use of interface -&gt; abstract class with equals/hashCode as key in Map
* apex-security
  * [#5385](https://github.com/pmd/pmd/issues/5385): \[apex] ApexCRUDViolation not reported even if SOQL doesn't have permissions check on it
* java-bestpractices
  * [#4272](https://github.com/pmd/pmd/issues/4272): \[java] JUnitTestsShouldIncludeAssert: False positive with assert in lambda
* java-multithreading
  * [#595](https://github.com/pmd/pmd/issues/595): \[java] New rule: Implement Runnable instead of extending Thread
* kotlin
  * [#6003](https://github.com/pmd/pmd/issues/6003): \[kotlin] Support multidollar interpolation (Kotlin 2.2)

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6493](https://github.com/pmd/pmd/pull/6493): \[apex] New Rule: AvoidInterfaceAsMapKeyRule - [Jonny Alexander Power](https://github.com/JonnyPower) (@JonnyPower)
* [#6497](https://github.com/pmd/pmd/pull/6497): \[kotlin] Fix kotlin grammar for parsing multidollar interpolation - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6555](https://github.com/pmd/pmd/pull/6555): \[java] New rule: OverridingThreadRun to prefer using Runnable - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6556](https://github.com/pmd/pmd/pull/6556): \[java] Fix #4272: False positive in UnitTestShouldIncludeAssert when using assertion in lambda - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6563](https://github.com/pmd/pmd/pull/6563): \[apex] Remove class name suffix "Test" as indicator of test classes - [David Schach](https://github.com/dschach) (@dschach)
* [#6576](https://github.com/pmd/pmd/pull/6576): \[test] chore: Throw a TestAbortedException on disabled tests - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6577](https://github.com/pmd/pmd/pull/6577): \[dist] chore: Improve error message for missing JAVA_HOME in AntIT.java - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6607](https://github.com/pmd/pmd/pull/6607): \[doc] basic.xml has been gone for a long time - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6515](https://github.com/pmd/pmd/pull/6515): chore: bump pmd-regression-tester from 1.6.2 to 1.7.0
* [#6552](https://github.com/pmd/pmd/pull/6552): Bump PMD from 7.22.0 to 7.23.0
* [#6564](https://github.com/pmd/pmd/pull/6564): chore(deps): bump ruby/setup-ruby from 1.295.0 to 1.299.0
* [#6565](https://github.com/pmd/pmd/pull/6565): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.7 to 1.18.8
* [#6566](https://github.com/pmd/pmd/pull/6566): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.3.0 to 13.4.0
* [#6567](https://github.com/pmd/pmd/pull/6567): chore(deps-dev): bump log4j.version from 2.25.3 to 2.25.4
* [#6569](https://github.com/pmd/pmd/pull/6569): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.7 to 1.18.8
* [#6570](https://github.com/pmd/pmd/pull/6570): chore(deps): bump org.apache.groovy:groovy from 5.0.4 to 5.0.5
* [#6571](https://github.com/pmd/pmd/pull/6571): chore(deps-dev): bump io.github.git-commit-id:git-commit-id-maven-plugin from 9.0.2 to 9.1.0
* [#6572](https://github.com/pmd/pmd/pull/6572): chore(deps): bump bigdecimal from 4.0.1 to 4.1.0 in /docs
* [#6578](https://github.com/pmd/pmd/pull/6578): chore(deps): bump marocchino/sticky-pull-request-comment from 3.0.2 to 3.0.3
* [#6579](https://github.com/pmd/pmd/pull/6579): chore(deps): bump crate-ci/typos from 1.44.0 to 1.45.0
* [#6580](https://github.com/pmd/pmd/pull/6580): chore(deps): bump ruby/setup-ruby from 1.299.0 to 1.300.0
* [#6581](https://github.com/pmd/pmd/pull/6581): chore(deps-dev): bump io.github.git-commit-id:git-commit-id-maven-plugin from 9.1.0 to 10.0.0
* [#6582](https://github.com/pmd/pmd/pull/6582): chore(deps): bump org.checkerframework:checker-qual from 3.54.0 to 4.0.0
* [#6583](https://github.com/pmd/pmd/pull/6583): chore(deps-dev): bump ant.version from 1.10.15 to 1.10.16
* [#6584](https://github.com/pmd/pmd/pull/6584): chore(deps): bump bigdecimal from 4.1.0 to 4.1.1 in /docs
* [#6588](https://github.com/pmd/pmd/pull/6588): chore(deps): bump actions/cache from 5.0.4 to 5.0.5
* [#6589](https://github.com/pmd/pmd/pull/6589): chore(deps): bump marocchino/sticky-pull-request-comment from 3.0.3 to 3.0.4
* [#6590](https://github.com/pmd/pmd/pull/6590): chore(deps): bump crate-ci/typos from 1.45.0 to 1.45.1
* [#6591](https://github.com/pmd/pmd/pull/6591): chore(deps): bump actions/upload-artifact from 7.0.0 to 7.0.1
* [#6592](https://github.com/pmd/pmd/pull/6592): chore(deps): bump actions/create-github-app-token from 3.0.0 to 3.1.1
* [#6593](https://github.com/pmd/pmd/pull/6593): chore(deps): bump scalameta.version from 4.15.2 to 4.16.0
* [#6594](https://github.com/pmd/pmd/pull/6594): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.25.4 to 0.25.5
* [#6595](https://github.com/pmd/pmd/pull/6595): chore(deps-dev): bump com.google.guava:guava from 33.5.0-jre to 33.6.0-jre
* [#6596](https://github.com/pmd/pmd/pull/6596): chore(deps-dev): bump ant.version from 1.10.16 to 1.10.17
* [#6599](https://github.com/pmd/pmd/pull/6599): chore(deps-dev): Bump lodash from 4.17.23 to 4.18.1
* [#6600](https://github.com/pmd/pmd/pull/6600): chore(deps-dev): Bump addressable from 2.8.9 to 2.9.0
* [#6613](https://github.com/pmd/pmd/pull/6613): chore(deps): bump ruby/setup-ruby from 1.300.0 to 1.305.0
* [#6614](https://github.com/pmd/pmd/pull/6614): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.25.5 to 0.25.6
* [#6615](https://github.com/pmd/pmd/pull/6615): chore(deps): bump scalameta.version from 4.16.0 to 4.16.1
* [#6616](https://github.com/pmd/pmd/pull/6616): chore(deps-dev): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.5.0.6356 to 5.6.0.6792
* [#6617](https://github.com/pmd/pmd/pull/6617): chore(deps): bump org.jsoup:jsoup from 1.22.1 to 1.22.2
* [#6618](https://github.com/pmd/pmd/pull/6618): chore(deps): bump bigdecimal from 4.1.1 to 4.1.2 in /docs

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 82 commits
* 14 closed tickets & PRs
* Days since last release: 27



## 27-March-2026 - 7.23.0

The PMD team is pleased to announce PMD 7.23.0.

This is a minor release.

### Table Of Contents

* [🐛️ Fixed Issues](#fixed-issues)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🐛️ Fixed Issues
* core
  * [#6503](https://github.com/pmd/pmd/issues/6503): \[core] Links in HTML report are broken
* java-errorprone
  * [#6502](https://github.com/pmd/pmd/issues/6502): \[java] CloseResource: False positive for allowedResourceMethodPatterns entries when using unqualified method calls
* java-security
  * [#6531](https://github.com/pmd/pmd/issues/6531): \[java] InsecureCryptoIv: False negative with fixed IVs from array initializers

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6467](https://github.com/pmd/pmd/pull/6467): \[ci] Use typos gh-action - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6488](https://github.com/pmd/pmd/pull/6488): \[doc] Update security.md for CVE-2026-28338 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6489](https://github.com/pmd/pmd/pull/6489): \[doc] CPD: document --report-file parameter - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6504](https://github.com/pmd/pmd/pull/6504): \[core] Fix #6503: Don't escape externalInfoUrl in reports - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6505](https://github.com/pmd/pmd/pull/6505): \[java] Fix #6502: CloseResource should consider unqualified method calls - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6545](https://github.com/pmd/pmd/pull/6545): \[java] Fix #6531: False negative in InsecureCryptoIv with array initializers - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6476](https://github.com/pmd/pmd/pull/6476): Bump PMD from 7.21.0 to 7.22.0
* [#6479](https://github.com/pmd/pmd/pull/6479): chore(deps): bump actions/download-artifact from 7.0.0 to 8.0.0
* [#6480](https://github.com/pmd/pmd/pull/6480): chore(deps): bump actions/upload-artifact from 6.0.0 to 7.0.0
* [#6481](https://github.com/pmd/pmd/pull/6481): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.2.0 to 13.3.0
* [#6482](https://github.com/pmd/pmd/pull/6482): chore(deps): bump org.mockito:mockito-core from 5.21.0 to 5.22.0
* [#6483](https://github.com/pmd/pmd/pull/6483): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.5 to 1.18.7
* [#6484](https://github.com/pmd/pmd/pull/6484): chore(deps): bump org.yaml:snakeyaml from 2.5 to 2.6
* [#6485](https://github.com/pmd/pmd/pull/6485): chore(deps): bump org.checkerframework:checker-qual from 3.53.1 to 3.54.0
* [#6486](https://github.com/pmd/pmd/pull/6486): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.5 to 1.18.7
* [#6487](https://github.com/pmd/pmd/pull/6487): chore(deps): bump com.google.protobuf:protobuf-java from 4.33.5 to 4.34.0
* [#6490](https://github.com/pmd/pmd/pull/6490): chore: Update gems, remove github-pages
* [#6498](https://github.com/pmd/pmd/pull/6498): chore(deps): bump ruby/setup-ruby from 1.288.0 to 1.290.0
* [#6499](https://github.com/pmd/pmd/pull/6499): chore(deps-dev): bump commons-logging:commons-logging from 1.3.5 to 1.3.6
* [#6500](https://github.com/pmd/pmd/pull/6500): chore(deps-dev): bump org.apache.maven.plugins:maven-shade-plugin from 3.6.1 to 3.6.2
* [#6501](https://github.com/pmd/pmd/pull/6501): chore(deps): bump org.apache.maven.plugins:maven-resources-plugin from 3.4.0 to 3.5.0
* [#6506](https://github.com/pmd/pmd/pull/6506): chore(deps): bump actions/create-github-app-token from 2.2.1 to 3.0.0
* [#6507](https://github.com/pmd/pmd/pull/6507): chore(deps): bump actions/download-artifact from 8.0.0 to 8.0.1
* [#6508](https://github.com/pmd/pmd/pull/6508): chore(deps): bump marocchino/sticky-pull-request-comment from 2.9.4 to 3.0.2
* [#6509](https://github.com/pmd/pmd/pull/6509): chore(deps): bump ruby/setup-ruby from 1.290.0 to 1.295.0
* [#6511](https://github.com/pmd/pmd/pull/6511): chore(deps): bump org.mockito:mockito-core from 5.22.0 to 5.23.0
* [#6514](https://github.com/pmd/pmd/pull/6514): chore: bump maven from 3.9.12 to 3.9.14
* [#6516](https://github.com/pmd/pmd/pull/6516): chore: bump json from 2.19.0 to 2.19.2
* [#6548](https://github.com/pmd/pmd/pull/6548): chore(deps): bump actions/cache from 5.0.3 to 5.0.4
* [#6549](https://github.com/pmd/pmd/pull/6549): chore(deps): bump com.google.protobuf:protobuf-java from 4.34.0 to 4.34.1
* [#6551](https://github.com/pmd/pmd/pull/6551): chore: use ruby4

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 38 commits
* 9 closed tickets & PRs
* Days since last release: 27



## 27-February-2026 - 7.22.0

The PMD team is pleased to announce PMD 7.22.0.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Security fixes](#security-fixes)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

#### Security fixes
* This release fixes a stored XSS vulnerability in VBHTMLRenderer and YAHTMLRenderer via unescaped violation messages.  
  Affects CI/CD pipelines that run PMD with `--format vbhtml` or `--format yahtml` on untrusted source code
  (e.g. pull requests from external contributors) and expose the HTML report as a build artifact.
  JavaScript executes in the browser context of anyone who opens the report.  
  Note: The default `html` format is **not affected** by unescaped violation messages, but a similar problem
  existed with suppressed violation markers.  
  If you use these reports, it is recommended to upgrade PMD.  
  Reported by [Smaran Chand](https://github.com/smaranchand) (@smaranchand).

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule [`UnnecessaryInterfaceDeclaration`](https://docs.pmd-code.org/pmd-doc-7.22.0/pmd_rules_java_codestyle.html#unnecessaryinterfacedeclaration) detects classes that
  implement interfaces that are already implemented by its superclass, and interfaces
  that extend other interfaces already declared by their superinterfaces.  
  These declarations are redundant and can be removed to simplify the code.

#### Changed Rules
* The rule [`CloseResource`](https://docs.pmd-code.org/pmd-doc-7.22.0/pmd_rules_java_errorprone.html#closeresource) introduces a new property, `allowedResourceMethodPatterns`,
  which lets you specify method invocation patterns whose return values are resources managed externally.
  This is useful for ignoring managed resources - for example, `Reader`/`Writer` instances obtained from
  `HttpServletRequest`/`HttpServletResponse` - because the servlet container, not application code,
  is responsible for closing them. By default, the rule ignores `InputStream`/`OutputStream`/`Reader`/`Writer`
  resources returned by methods on `(Http)ServletRequest` and `(Http)ServletResponse`
  (both `javax.servlet` and `jakarta.servlet`).

### 🐛️ Fixed Issues
* core
  * [#6471](https://github.com/pmd/pmd/issues/6471): \[core] BaseAntlrTerminalNode should return type instead of index for getTokenKind()
  * [#6475](https://github.com/pmd/pmd/issues/6475): \[core] Fix stored XSS in VBHTMLRenderer and YAHTMLRenderer
* doc
  * [#6396](https://github.com/pmd/pmd/pull/6396): \[doc] Mention test-pmd-tool as alternative for testing
* java-bestpractices
  * [#6431](https://github.com/pmd/pmd/issues/6431): \[java] UnitTestShouldIncludeAssert: False positive with SoftAssertionsExtension on parent/grandparent classes
* java-codestyle
  * [#6458](https://github.com/pmd/pmd/pull/6458): \[java] New Rule: UnnecessaryInterfaceDeclaration
* java-errorprone
  * [#5787](https://github.com/pmd/pmd/issues/5787): \[java] InvalidLogMessageFormat: False positive with lombok @<!-- -->Value generated methods
  * [#6436](https://github.com/pmd/pmd/issues/6436): \[java] CloseResource: Allow to ignore managed resources

### 🚨️ API Changes

#### Deprecations
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.22.0/net/sourceforge/pmd/renderers/CodeClimateIssue.html#"><code>CodeClimateIssue</code></a>: This class is an implementation detail of
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.22.0/net/sourceforge/pmd/renderers/CodeClimateRenderer.html#"><code>CodeClimateRenderer</code></a>. It will be internalized in a future release.
* visualforce
  * <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.22.0/net/sourceforge/pmd/lang/visualforce/DataType.html#"><code>DataType</code></a>. The enum constants have been renamed to follow Java naming
    conventions. The old enum constants are deprecated and should no longer be used.  
    The method <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.22.0/net/sourceforge/pmd/lang/visualforce/DataType.html#fromString(java.lang.String)"><code>DataType#fromString</code></a> will return the new
    enum constants.  
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.22.0/net/sourceforge/pmd/lang/visualforce/DataType.html#fieldTypeNameOf()"><code>DataType#fieldTypeNameOf</code></a> to get the original field type name.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6396](https://github.com/pmd/pmd/pull/6396): \[doc] Mention test-pmd-tool as alternative for testing - [Beech Horn](https://github.com/metalshark) (@metalshark)
* [#6397](https://github.com/pmd/pmd/pull/6397): \[java] Add support for Lombok-generated getters in symbol resolution - [Anurag Agarwal](https://github.com/altaiezior) (@altaiezior)
* [#6420](https://github.com/pmd/pmd/pull/6420): \[ci] build: Add typos as spell checker - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6432](https://github.com/pmd/pmd/pull/6432): \[java] UnitTestShouldIncludeAssert: False positive with SoftAssertionsExtension on parent/grandparent classes - [Artur Kalimullin](https://github.com/kaliy) (@kaliy)
* [#6434](https://github.com/pmd/pmd/pull/6434): \[java] chore(style): Fix lambda argument indentation for checkstyle compliance - [Kai](https://github.com/aclfe) (@aclfe)
* [#6437](https://github.com/pmd/pmd/pull/6437): \[java] CloseResource: Allow to ignore managed resources - [Gildas Cuisinier](https://github.com/gcuisinier) (@gcuisinier)
* [#6445](https://github.com/pmd/pmd/pull/6445): chore: Fix FieldNamingConventions - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6446](https://github.com/pmd/pmd/pull/6446): \[doc] Add new IntelliJ Plugin "PMD X" - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6447](https://github.com/pmd/pmd/pull/6447): chore: Small release process fixes - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6458](https://github.com/pmd/pmd/pull/6458): \[java] New Rule: UnnecessaryInterfaceDeclaration - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6472](https://github.com/pmd/pmd/pull/6472): \[core] Fix BaseAntlrTerminalNode getTokenKind to return type instead of index - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6475](https://github.com/pmd/pmd/pull/6475): \[core] Fix stored XSS in VBHTMLRenderer and YAHTMLRenderer - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6433](https://github.com/pmd/pmd/pull/6433): Bump PMD from 7.20.0 to 7.21.0
* [#6438](https://github.com/pmd/pmd/pull/6438): chore(deps): bump actions/cache from 5.0.2 to 5.0.3
* [#6439](https://github.com/pmd/pmd/pull/6439): chore(deps): bump ruby/setup-ruby from 1.286.0 to 1.288.0
* [#6440](https://github.com/pmd/pmd/pull/6440): chore(deps): bump scalameta.version from 4.14.6 to 4.14.7
* [#6441](https://github.com/pmd/pmd/pull/6441): chore(deps): bump org.apache.maven.plugins:maven-compiler-plugin from 3.14.1 to 3.15.0
* [#6442](https://github.com/pmd/pmd/pull/6442): chore(deps): bump org.checkerframework:checker-qual from 3.53.0 to 3.53.1
* [#6443](https://github.com/pmd/pmd/pull/6443): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.0.0 to 13.1.0
* [#6444](https://github.com/pmd/pmd/pull/6444): chore(deps): bump com.google.protobuf:protobuf-java from 4.33.4 to 4.33.5
* [#6452](https://github.com/pmd/pmd/pull/6452): chore(deps): bump actions/checkout from 6.0.1 to 6.0.2
* [#6455](https://github.com/pmd/pmd/pull/6455): chore(deps): bump org.apache.maven.plugins:maven-dependency-plugin from 3.9.0 to 3.10.0
* [#6456](https://github.com/pmd/pmd/pull/6456): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.1.0 to 13.2.0
* [#6462](https://github.com/pmd/pmd/pull/6462): chore(deps): bump junit.version from 6.0.2 to 6.0.3
* [#6463](https://github.com/pmd/pmd/pull/6463): chore(deps): bump scalameta.version from 4.14.7 to 4.15.2
* [#6465](https://github.com/pmd/pmd/pull/6465): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.4 to 1.18.5
* [#6468](https://github.com/pmd/pmd/pull/6468): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.4 to 1.18.5
* [#6469](https://github.com/pmd/pmd/pull/6469): chore(deps): bump surefire.version from 3.5.4 to 3.5.5
* [#6470](https://github.com/pmd/pmd/pull/6470): chore(deps): bump org.jetbrains:annotations from 26.0.2-1 to 26.1.0
* [#6473](https://github.com/pmd/pmd/pull/6473): chore(deps): bump nokogiri to 1.19.1
* [#6474](https://github.com/pmd/pmd/pull/6474): chore(deps): bump faraday from 2.13.3 to 2.14.1

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 66 commits
* 16 closed tickets & PRs
* Days since last release: 28



## 30-January-2026 - 7.21.0

The PMD team is pleased to announce PMD 7.21.0.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [🚀️ New: Java 26 Support](#new-java-26-support)
    * [Build Requirement is Java 21](#build-requirement-is-java-21)
    * [CPD](#cpd)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

#### 🚀️ New: Java 26 Support
This release of PMD brings support for Java 26.

There are no new standard language features.

There is one preview language feature:
* [JEP 530: Primitive Types in Patterns, instanceof, and switch (Fourth Preview)](https://openjdk.org/jeps/530)

In order to analyze a project with PMD that uses these preview language features,
you'll need to select the new language version `26-preview`:

    pmd check --use-version java-26-preview ...

Note: Support for Java 24 preview language features have been removed. The version "24-preview"
is no longer available.

#### Build Requirement is Java 21
From now on, Java 21 or newer is required to build PMD. PMD itself still remains compatible with Java 8,
so that it still can be used in a pure Java 8 environment. This allows us to use the latest
checkstyle version during the build.

#### CPD
* The Apex module now supports [suppression](https://docs.pmd-code.org/latest/pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs. See [#6417](https://github.com/pmd/pmd/pull/6417)

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule [`PublicMemberInNonPublicType`](https://docs.pmd-code.org/pmd-doc-7.21.0/pmd_rules_java_design.html#publicmemberinnonpublictype) detects public members (such as methods
  or fields) within non-public types. Non-public types should not declare public members, as their effective
  visibility is limited, and using the `public` modifier can create confusion.
* The new Java rule [`UnsupportedJdkApiUsage`](https://docs.pmd-code.org/pmd-doc-7.21.0/pmd_rules_java_errorprone.html#unsupportedjdkapiusage) flags the use of unsupported and non-portable
  JDK APIs, including `sun.*` packages, `sun.misc.Unsafe`, and `jdk.internal.misc.Unsafe`. These APIs are unstable,
  intended for internal use, and may change or be removed. The rule complements Java compiler warnings by
  highlighting such usage during code reviews and encouraging migration to official APIs like VarHandle and
  the Foreign Function & Memory API.

#### Changed Rules
The following rules have been changed to use a consistent implementation of enum based
rule properties:
* The property `checkAddressTypes` of rule [`AvoidUsingHardCodedIP`](https://docs.pmd-code.org/pmd-doc-7.21.0/pmd_rules_java_bestpractices.html#avoidusinghardcodedip) has changed:
  * Instead of `IPv4` use `ipv4`
  * Instead of `IPv6` use `ipv6`
  * Instead of `IPv4 mapped IPv6` use `ipv4MappedIpv6`
  * The old values still work, but you'll see a deprecation warning.
* The property `nullCheckBranch` of rule [`ConfusingTernary`](https://docs.pmd-code.org/pmd-doc-7.21.0/pmd_rules_java_codestyle.html#confusingternary) has changed:
  * Instead of `Any` use `any`
  * Instead of `Then` use `then`
  * Instead of `Else` use `else`
  * The old values still work, but you'll see a deprecation warning.
* The property `typeAnnotations` of rule [`ModifierOrder`](https://docs.pmd-code.org/pmd-doc-7.21.0/pmd_rules_java_codestyle.html#modifierorder) has changed:
  * Instead of `ontype` use `onType`
  * Instead of `ondecl` use `onDecl`
  * The old values still work, but you'll see a deprecation warning.
* The values of the properties of rule [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.21.0/pmd_rules_java_documentation.html#commentrequired) have changed:
  * Instead of `Required` use `required`
  * Instead of `Ignored` use `ignored`
  * Instead of `Unwanted` use `unwanted`
  * The old values still work, but you'll see a deprecation warning.

#### Deprecated Rules
* The Java rule [`DontImportSun`](https://docs.pmd-code.org/pmd-doc-7.21.0/pmd_rules_java_errorprone.html#dontimportsun) has been deprecated. It is replaced by
  [`UnsupportedJdkApiUsage`](https://docs.pmd-code.org/pmd-doc-7.21.0/pmd_rules_java_errorprone.html#unsupportedjdkapiusage).

### 🐛️ Fixed Issues
* core
  * [#6184](https://github.com/pmd/pmd/issues/6184): \[core] Consistent implementation of enum properties
* apex
  * [#6417](https://github.com/pmd/pmd/issues/6417): \[apex] Support CPD suppression with "CPD-OFF" & "CPD-ON"
* apex-codestyle
  * [#6349](https://github.com/pmd/pmd/issues/6349): \[apex] FieldDeclarationsShouldBeAtStart: False positive with properties
* cli
  * [#6290](https://github.com/pmd/pmd/issues/6290): \[cli] Improve Designer start script
* java
  * [#5871](https://github.com/pmd/pmd/issues/5871): \[java] Support Java 26
  * [#6364](https://github.com/pmd/pmd/issues/6364): \[java] Parse error with yield lambda inside switch
* java-design
  * [#6231](https://github.com/pmd/pmd/issues/6231): \[java] New Rule: PublicMemberInNonPublicType
* java-errorprone
  * [#3601](https://github.com/pmd/pmd/issues/3601): \[java] InvalidLogMessageFormat: False positive when final parameter is Supplier&lt;Throwable&gt;
  * [#5882](https://github.com/pmd/pmd/issues/5882): \[java] UnconditionalIfStatement: False negative when true/false is not literal but local variable
  * [#5923](https://github.com/pmd/pmd/issues/5923): \[java] New Rule: Catch usages of sun.misc.Unsafe or jdk.internal.misc.Unsafe
* java-performance
  * [#3857](https://github.com/pmd/pmd/issues/3857): \[java] InsufficientStringBufferDeclaration: False negatives with String constants

### 🚨️ API Changes

#### Deprecations
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/lang/metrics/MetricOption.html#valueName()"><code>MetricOption#valueName</code></a>: When metrics are used for (rule) properties,
    then the conventional enum mapping (from SCREAMING_SNAKE_CASE to camelCase) will be used for the enum values.
    See <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumListProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumListProperty</code></a>.
  * In <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#"><code>PropertyFactory</code></a>:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumProperty(java.lang.String,java.util.Map)"><code>enumProperty(String, Map)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumProperty</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumProperty(java.lang.String,java.lang.Class)"><code>enumProperty(String, Class)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumProperty</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumProperty(java.lang.String,java.lang.Class,java.util.function.Function)"><code>enumProperty(String, Class, Function)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumProperty</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumListProperty(java.lang.String,java.util.Map)"><code>enumListProperty(String, Map)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumListProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumListProperty</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumListProperty(java.lang.String,java.lang.Class,java.util.function.Function)"><code>enumListProperty(String, Class, Function)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumListProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumListProperty</code></a> instead.
* java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_FOR"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_FOR</code></a>. This constant should
    have never been public.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_DO"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_DO</code></a>. This constant should
    have never been public.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_WHILE"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_WHILE</code></a>. This constant should
    have never been public.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_BREAK_LOOP_TYPES"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_BREAK_LOOP_TYPES</code></a>. This property
    descriptor should have been private. It won't be used anymore. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertyDescriptor(java.lang.String)"><code>getPropertyDescriptor</code></a>
    on the rule to retrieve the property descriptor.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_CONTINUE_LOOP_TYPES"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_CONTINUE_LOOP_TYPES</code></a>. This property
    descriptor should have been private. It won't be used anymore. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertyDescriptor(java.lang.String)"><code>getPropertyDescriptor</code></a>
    on the rule to retrieve the property descriptor.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_RETURN_LOOP_TYPES"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_RETURN_LOOP_TYPES</code></a>. This property
    descriptor should have been private. It won't be used anymore. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertyDescriptor(java.lang.String)"><code>getPropertyDescriptor</code></a>
    on the rule to retrieve the property descriptor.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#check(net.sourceforge.pmd.properties.PropertyDescriptor,net.sourceforge.pmd.lang.ast.Node,java.lang.Object)"><code>AvoidBranchingStatementAsLastInLoopRule#check</code></a>.
    This method should have been private and will be internalized.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#hasPropertyValue(net.sourceforge.pmd.properties.PropertyDescriptor,java.lang.String)"><code>AvoidBranchingStatementAsLastInLoopRule#hasPropertyValue</code></a>.
    This method should have been private and will be internalized.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#checksNothing()"><code>AvoidBranchingStatementAsLastInLoopRule#checksNothing</code></a>.
    This method should have been private and will be internalized.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.ClassFanOutOption.html#valueName()"><code>ClassFanOutOption#valueName</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.CycloOption.html#valueName()"><code>CycloOption#valueName</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.NcssOption.html#valueName()"><code>NcssOption#valueName</code></a>
* lang-test
  * <a href="https://docs.pmd-code.org/apidocs/pmd-lang-test/7.21.0/net/sourceforge/pmd/lang/test/AbstractMetricTestRule.html#optionMappings()"><code>AbstractMetricTestRule#optionMappings</code></a>. No extra mapping is required anymore.
    The <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0/net/sourceforge/pmd/lang/metrics/MetricOption.html#"><code>MetricOption</code></a> enum values are used. See 
    <a href="https://docs.pmd-code.org/apidocs/pmd-lang-test/7.21.0/net/sourceforge/pmd/lang/test/AbstractMetricTestRule.html#AbstractMetricTestRule(net.sourceforge.pmd.lang.metrics.Metric,java.lang.Class)"><code>AbstractMetricTestRule(Metric, Class)</code></a>
    to provide the enum at construction time.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6231](https://github.com/pmd/pmd/pull/6231): \[java] New Rule: PublicMemberInNonPublicType - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6232](https://github.com/pmd/pmd/pull/6232): \[java] New Rule: UnsupportedJdkApiUsage - [Thomas Leplus](https://github.com/thomasleplus) (@thomasleplus)
* [#6233](https://github.com/pmd/pmd/pull/6233): \[core] Fix #6184: More consistent enum properties - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6290](https://github.com/pmd/pmd/pull/6290): \[cli] Improve Designer start script - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6315](https://github.com/pmd/pmd/pull/6315): \[java] Fix #5882: UnconditionalIfStatement false-negative if true/false is not literal - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6362](https://github.com/pmd/pmd/pull/6362): chore: Fix typos - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6366](https://github.com/pmd/pmd/pull/6366): \[java] Fix #3857: InsufficientStringBufferDeclaration should consider constant Strings - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6373](https://github.com/pmd/pmd/pull/6373): \[java] Support Java 26 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6377](https://github.com/pmd/pmd/pull/6377): \[doc] chore: update last_updated - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6384](https://github.com/pmd/pmd/pull/6384): chore: helper script check-all-contributors.sh - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6386](https://github.com/pmd/pmd/pull/6386): \[core] chore: Bump minimum Java version required for building to 21 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6387](https://github.com/pmd/pmd/pull/6387): \[ci] publish-pull-requests: download latest build result - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6389](https://github.com/pmd/pmd/pull/6389): chore: update javadoc deprecated tags - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6390](https://github.com/pmd/pmd/pull/6390): chore: update javadoc experimental tags - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6391](https://github.com/pmd/pmd/pull/6391): chore: update javadoc internal API tags - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6392](https://github.com/pmd/pmd/pull/6392): \[doc] ADR 3: Clarify javadoc tags - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6394](https://github.com/pmd/pmd/pull/6394): \[apex] Fix #6349: FieldDeclarationsShouldBeAtStart false positive with properties - [Mohamed Hamed](https://github.com/mdhamed238) (@mdhamed238)
* [#6407](https://github.com/pmd/pmd/pull/6407): \[java]  Fix #3601: InvalidLogMessageFormat: False positive when final parameter is Supplier<Throwable>  - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6417](https://github.com/pmd/pmd/pull/6417): \[apex] Support CPD suppression with "CPD-OFF" & "CPD-ON" - [Jade](https://github.com/goto-dev-null) (@goto-dev-null)
* [#6428](https://github.com/pmd/pmd/pull/6428): \[ci] chore: run extensive integration tests under linux only - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6429](https://github.com/pmd/pmd/pull/6429): \[doc] chore: add keywords for auxclasspath in Java documentation - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6430](https://github.com/pmd/pmd/pull/6430): \[java] Fix #6364: Parse error with yield lambda - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6367](https://github.com/pmd/pmd/pull/6367): Bump PMD from 7.19.0 to 7.20.0
* [#6369](https://github.com/pmd/pmd/pull/6369): chore(deps): bump ruby/setup-ruby from 1.275.0 to 1.277.0
* [#6370](https://github.com/pmd/pmd/pull/6370): chore(deps): bump org.apache.groovy:groovy from 5.0.2 to 5.0.3
* [#6371](https://github.com/pmd/pmd/pull/6371): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.2 to 1.18.3
* [#6372](https://github.com/pmd/pmd/pull/6372): chore(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.6.2 to 3.6.3
* [#6375](https://github.com/pmd/pmd/pull/6375): chore: Bump maven from 3.9.11 to 3.9.12
* [#6378](https://github.com/pmd/pmd/pull/6378): chore(deps): bump ruby/setup-ruby from 1.277.0 to 1.279.0
* [#6379](https://github.com/pmd/pmd/pull/6379): chore(deps): bump scalameta.version from 4.14.2 to 4.14.4
* [#6380](https://github.com/pmd/pmd/pull/6380): chore(deps): bump junit.version from 6.0.1 to 6.0.2
* [#6381](https://github.com/pmd/pmd/pull/6381): chore(deps): bump org.jsoup:jsoup from 1.21.2 to 1.22.1
* [#6382](https://github.com/pmd/pmd/pull/6382): chore(deps): bump org.checkerframework:checker-qual from 3.52.1 to 3.53.0
* [#6383](https://github.com/pmd/pmd/pull/6383): chore(deps): bump com.puppycrawl.tools:checkstyle from 12.3.0 to 13.0.0
* [#6385](https://github.com/pmd/pmd/pull/6385): chore(deps): bump uri from 1.0.3 to 1.0.4 in /docs
* [#6399](https://github.com/pmd/pmd/pull/6399): chore(deps): bump ruby/setup-ruby from 1.279.0 to 1.282.0
* [#6400](https://github.com/pmd/pmd/pull/6400): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.25.1 to 0.25.4
* [#6401](https://github.com/pmd/pmd/pull/6401): chore(deps): bump org.sonatype.central:central-publishing-maven-plugin from 0.9.0 to 0.10.0
* [#6403](https://github.com/pmd/pmd/pull/6403): chore(deps): bump com.google.protobuf:protobuf-java from 4.33.2 to 4.33.4
* [#6410](https://github.com/pmd/pmd/pull/6410): chore(deps): bump ruby/setup-ruby from 1.282.0 to 1.285.0
* [#6411](https://github.com/pmd/pmd/pull/6411): chore(deps): bump actions/cache from 5.0.1 to 5.0.2
* [#6412](https://github.com/pmd/pmd/pull/6412): chore(deps): bump scalameta.version from 4.14.4 to 4.14.5
* [#6413](https://github.com/pmd/pmd/pull/6413): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.3 to 1.18.4
* [#6414](https://github.com/pmd/pmd/pull/6414): chore(deps-dev): bump org.codehaus.mojo:versions-maven-plugin from 2.20.1 to 2.21.0
* [#6415](https://github.com/pmd/pmd/pull/6415): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.3 to 1.18.4
* [#6419](https://github.com/pmd/pmd/pull/6419): chore(deps-dev): bump lodash from 4.17.21 to 4.17.23
* [#6421](https://github.com/pmd/pmd/pull/6421): chore(deps): bump actions/setup-java from 5.1.0 to 5.2.0
* [#6422](https://github.com/pmd/pmd/pull/6422): chore(deps): bump actions/checkout from 6.0.1 to 6.0.2
* [#6423](https://github.com/pmd/pmd/pull/6423): chore(deps): bump scalameta.version from 4.14.5 to 4.14.6
* [#6424](https://github.com/pmd/pmd/pull/6424): chore(deps-dev): bump org.assertj:assertj-core from 3.27.6 to 3.27.7
* [#6425](https://github.com/pmd/pmd/pull/6425): chore(deps): bump org.apache.groovy:groovy from 5.0.3 to 5.0.4

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 146 commits
* 30 closed tickets & PRs
* Days since last release: 30



## 30-December-2025 - 7.20.0

The PMD team is pleased to announce PMD 7.20.0.

This is a minor release.

### Table Of Contents

* [🌟️ Changed Rules](#changed-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Experimental API](#experimental-api)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🌟️ Changed Rules
* The Java rule [`OnlyOneReturn`](https://docs.pmd-code.org/pmd-doc-7.20.0/pmd_rules_java_codestyle.html#onlyonereturn) has a new property `ignoredMethodNames`. This property by
  default is set to `compareTo` and `equals`, thus this rule now by default allows multiple return statements
  for these methods. To restore the old behavior, simply set this property to an empty value.

### 🐛️ Fixed Issues
* core
  * [#6330](https://github.com/pmd/pmd/issues/6330): \[core] "Unable to create ValueRepresentation" when using @<!-- -->LiteralText (XPath)
* java
  * [#6234](https://github.com/pmd/pmd/issues/6234): \[java] Parser fails to parse switch expressions in super() constructor calls
  * [#6299](https://github.com/pmd/pmd/issues/6299): \[java] Fix grammar of switch label
* java-bestpractices
  * [#4282](https://github.com/pmd/pmd/issues/4282): \[java] GuardLogStatement: False positive when guard is not a direct parent
  * [#6028](https://github.com/pmd/pmd/issues/6028): \[java] UnusedPrivateMethod: False positive with raw type for generic method
  * [#6257](https://github.com/pmd/pmd/issues/6257): \[java] UnusedLocalVariable: False positive with instanceof pattern guard
  * [#6291](https://github.com/pmd/pmd/issues/6291): \[java] EnumComparison: False positive for any object when object.equals(null)
  * [#6328](https://github.com/pmd/pmd/issues/6328): \[java] UnusedLocalVariable: False positive for pattern variable in for-each without braces
* java-codestyle
  * [#4257](https://github.com/pmd/pmd/issues/4257): \[java] OnlyOneReturn: False positive with equals method
  * [#5043](https://github.com/pmd/pmd/issues/5043): \[java] LambdaCanBeMethodReference: False positive on overloaded methods
  * [#6237](https://github.com/pmd/pmd/issues/6237): \[java] UnnecessaryCast: ContextedRuntimeException when parsing switch expression with lambdas
  * [#6279](https://github.com/pmd/pmd/issues/6279): \[java] EmptyMethodInAbstractClassShouldBeAbstract: False positive for final empty methods
  * [#6284](https://github.com/pmd/pmd/issues/6284): \[java] UnnecessaryConstructor: False positive for JavaDoc-bearing constructor
* java-errorprone
  * [#6276](https://github.com/pmd/pmd/issues/6276): \[java] NullAssignment: False positive when assigning null to a final field in a constructor
  * [#6343](https://github.com/pmd/pmd/issues/6343): \[java] MissingStaticMethodInNonInstantiatableClass: False negative when method in nested class returns null
* java-performance
  * [#4158](https://github.com/pmd/pmd/issues/4158): \[java] BigIntegerInstantiation: False negative with compile-time constant
  * [#4910](https://github.com/pmd/pmd/issues/4910): \[java] ConsecutiveAppendsShouldReuse: False positive within if-statement without curly braces
  * [#5877](https://github.com/pmd/pmd/issues/5877): \[java] AvoidArrayLoops: False negative when break inside switch statement
* maintenance
  * [#6230](https://github.com/pmd/pmd/issues/6230): \[core] Single module snapshot build fails

### 🚨️ API Changes

#### Experimental API
* pmd-java: <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.20.0/net/sourceforge/pmd/lang/java/types/OverloadSelectionResult.html#hadSeveralApplicableOverloads()"><code>OverloadSelectionResult#hadSeveralApplicableOverloads</code></a>

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6262](https://github.com/pmd/pmd/pull/6262): \[java] UnusedLocalVariable: fix false positive with guard in switch - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6285](https://github.com/pmd/pmd/pull/6285): \[java] Fix #5043: FP in LambdaCanBeMethodReference when method ref would be ambiguous - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6287](https://github.com/pmd/pmd/pull/6287): \[doc] Explain how to build or pull snapshot dependencies for single module builds - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6288](https://github.com/pmd/pmd/pull/6288): \[java] Fix #6279: EmptyMethodInAbstractClassShouldBeAbstract should ignore final methods - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6292](https://github.com/pmd/pmd/pull/6292): \[java] Fix #6291: EnumComparison FP when comparing with null - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6293](https://github.com/pmd/pmd/pull/6293): \[java] Fix #6276: NullAssignment should not report assigning null to a final field in a constructor - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6294](https://github.com/pmd/pmd/pull/6294): \[java] Fix #6028: UnusedPrivateMethod FP - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6295](https://github.com/pmd/pmd/pull/6295): \[java] Fix #6237: UnnecessaryCast error with switch expr returning lambdas - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6296](https://github.com/pmd/pmd/pull/6296): \[java] Fix #4282: GuardLogStatement only detects guard methods immediately around it - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6299](https://github.com/pmd/pmd/pull/6299): \[java] Fix grammar of switch label - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6309](https://github.com/pmd/pmd/pull/6309): \[java] Fix #4257: Allow ignoring methods in OnlyOneReturn - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6311](https://github.com/pmd/pmd/pull/6311): \[java] Fix #6284: UnnecessaryConstructor reporting false-positive on JavaDoc-bearing constructor - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6313](https://github.com/pmd/pmd/pull/6313): \[java] Fix #4910: if-statement triggers ConsecutiveAppendsShouldReuse - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6316](https://github.com/pmd/pmd/pull/6316): \[java] Fix #5877: AvoidArrayLoops false-negative when break inside switch statement - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6342](https://github.com/pmd/pmd/pull/6342): \[core] Fix #6330: Cannot access Chars attribute from XPath - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6344](https://github.com/pmd/pmd/pull/6344): \[java] Fix #6328: UnusedLocalVariable should consider pattern variable in for-each without curly braces - [Mohamed Hamed](https://github.com/mdhamed238) (@mdhamed238)
* [#6348](https://github.com/pmd/pmd/pull/6348): \[jsp] Fix malformed Javadoc HTML in JspDocStyleTest - [Gianmarco](https://github.com/gianmarcoschifone) (@gianmarcoschifone)
* [#6359](https://github.com/pmd/pmd/pull/6359): \[java] Fix #6234: Parser fails to parse switch expressions in super() constructor calls - [Mohamed Hamed](https://github.com/mdhamed238) (@mdhamed238)
* [#6360](https://github.com/pmd/pmd/pull/6360): \[java] Fix #4158: BigIntegerInstantiation false-negative with compile-time constant - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6361](https://github.com/pmd/pmd/pull/6361): \[vf] Fix invalid Javadoc syntax in VfDocStyleTest - [Gianmarco](https://github.com/gianmarcoschifone) (@gianmarcoschifone)
* [#6363](https://github.com/pmd/pmd/pull/6363): \[apex] Add sca-extra ruleset for Salesforce Apex testing - [Beech Horn](https://github.com/metalshark) (@metalshark)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6286](https://github.com/pmd/pmd/pull/6286): Bump PMD from 7.18.0 to 7.19.0
* [#6300](https://github.com/pmd/pmd/pull/6300): chore(deps): bump actions/checkout from 6.0.0 to 6.0.1
* [#6301](https://github.com/pmd/pmd/pull/6301): chore(deps): bump org.checkerframework:checker-qual from 3.52.0 to 3.52.1
* [#6302](https://github.com/pmd/pmd/pull/6302): chore(deps): bump org.apache.maven.plugins:maven-resources-plugin from 3.3.1 to 3.4.0
* [#6303](https://github.com/pmd/pmd/pull/6303): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.1 to 1.18.2
* [#6304](https://github.com/pmd/pmd/pull/6304): chore(deps): bump com.puppycrawl.tools:checkstyle from 12.1.2 to 12.2.0
* [#6305](https://github.com/pmd/pmd/pull/6305): chore(deps): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.3.0.6276 to 5.4.0.6343
* [#6306](https://github.com/pmd/pmd/pull/6306): chore(deps): bump webrick from 1.9.1 to 1.9.2 in /docs
* [#6318](https://github.com/pmd/pmd/pull/6318): chore(deps): bump actions/create-github-app-token from 2.2.0 to 2.2.1
* [#6319](https://github.com/pmd/pmd/pull/6319): chore(deps): bump actions/setup-java from 5.0.0 to 5.1.0
* [#6320](https://github.com/pmd/pmd/pull/6320): chore(deps): bump ruby/setup-ruby from 1.268.0 to 1.269.0
* [#6321](https://github.com/pmd/pmd/pull/6321): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.1 to 1.18.2
* [#6323](https://github.com/pmd/pmd/pull/6323): chore(deps): bump com.google.protobuf:protobuf-java from 4.33.1 to 4.33.2
* [#6324](https://github.com/pmd/pmd/pull/6324): chore(deps): bump io.github.apex-dev-tools:apex-ls_2.13 from 6.0.1 to 6.0.2
* [#6325](https://github.com/pmd/pmd/pull/6325): chore(deps): bump org.apache.maven.plugins:maven-assembly-plugin from 3.7.1 to 3.8.0
* [#6329](https://github.com/pmd/pmd/pull/6329): chore(deps): bump org.mozilla:rhino from 1.7.15 to 1.7.15.1
* [#6331](https://github.com/pmd/pmd/pull/6331): chore(deps): bump actions/upload-artifact from 5.0.0 to 6.0.0
* [#6332](https://github.com/pmd/pmd/pull/6332): chore(deps): bump org.mockito:mockito-core from 5.20.0 to 5.21.0
* [#6333](https://github.com/pmd/pmd/pull/6333): chore(deps): bump actions/download-artifact from 6.0.0 to 7.0.0
* [#6334](https://github.com/pmd/pmd/pull/6334): chore(deps): bump ruby/setup-ruby from 1.269.0 to 1.270.0
* [#6335](https://github.com/pmd/pmd/pull/6335): chore(deps): bump com.puppycrawl.tools:checkstyle from 12.2.0 to 12.3.0
* [#6336](https://github.com/pmd/pmd/pull/6336): chore(deps): bump actions/cache from 4.3.0 to 5.0.1
* [#6337](https://github.com/pmd/pmd/pull/6337): chore(deps): bump bigdecimal from 3.3.1 to 4.0.0 in /docs
* [#6339](https://github.com/pmd/pmd/pull/6339): chore(deps): bump org.apache.maven.plugins:maven-release-plugin from 3.2.0 to 3.3.1
* [#6341](https://github.com/pmd/pmd/pull/6341): chore(deps): bump org.apache.maven.plugins:maven-source-plugin from 3.3.1 to 3.4.0
* [#6347](https://github.com/pmd/pmd/pull/6347): chore(deps-dev): bump org.apache.logging.log4j:log4j-core from 2.25.2 to 2.25.3 in /pmd-java
* [#6350](https://github.com/pmd/pmd/pull/6350): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.24.2 to 0.25.1
* [#6352](https://github.com/pmd/pmd/pull/6352): chore(deps): bump ruby/setup-ruby from 1.270.0 to 1.275.0
* [#6353](https://github.com/pmd/pmd/pull/6353): chore(deps): bump org.ow2.asm:asm from 9.9 to 9.9.1
* [#6354](https://github.com/pmd/pmd/pull/6354): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.2 to 1.18.3
* [#6356](https://github.com/pmd/pmd/pull/6356): chore(deps): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.4.0.6343 to 5.5.0.6356
* [#6357](https://github.com/pmd/pmd/pull/6357): chore(deps): bump org.apache.commons:commons-text from 1.14.0 to 1.15.0
* [#6358](https://github.com/pmd/pmd/pull/6358): chore(deps): bump bigdecimal from 4.0.0 to 4.0.1 in /docs

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 102 commits
* 39 closed tickets & PRs
* Days since last release: 32



## 28-November-2025 - 7.19.0

The PMD team is pleased to announce PMD 7.19.0.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Updated PMD Designer](#updated-pmd-designer)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog (7.19.0)](https://github.com/pmd/pmd-designer/releases/tag/7.19.0)
and [PMD Designer Changelog (7.19.1)](https://github.com/pmd/pmd-designer/releases/tag/7.19.1).

### 🌟️ New and Changed Rules
#### New Rules
* The new Apex rule [`AvoidFutureAnnotation`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_apex_bestpractices.html#avoidfutureannotation) finds usages of the `@Future`
  annotation. It is a legacy way to execute asynchronous Apex code. New code should implement
  the `Queueable` interface instead.
* The new Java rule [`EnumComparison`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_java_bestpractices.html#enumcomparison) finds usages of `equals()` on
  enum constants or values. Enums should be compared directly with `==` instead of `equals()` which
  has some advantages (e.g. static type checking at compile time).
* The new Apex rule [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_apex_design.html#ncsscount) replaces the four rules "ExcessiveClassLength",
  "NcssConstructorCount", "NcssMethodCount", and "NcssTypeCount". The new rule uses the metrics framework
  to achieve the same. It has two properties, to define the report level for method and class sizes separately.
  Constructors and methods are considered the same.  
  The rule has been added to the quickstart ruleset.  
  Note: The new metric is implemented more correct than in the old rules. E.g. it considers now also
  switch statements and correctly counts if-statements only once and ignores method calls that are
  part of an expression and not a statement on their own. This leads to different numbers. Keep in mind,
  that NCSS counts statements and not lines of code. Statements that are split on multiple lines are
  still counted as one.
* The new PL/SQL rule [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#ncsscount) replaces the rules "ExcessiveMethodLength",
  "ExcessiveObjectLength", "ExcessivePackageBodyLength", "ExcessivePackageSpecificationLength",
  "ExcessiveTypeLength", "NcssMethodCount" and "NcssObjectCount". The new rule uses the metrics framework
  to achieve the same. It has two properties, to define the report level for method and object sizes separately.  
  Note: the new metric is implemented more correct than in the old rules, so that the actual numbers of
  the NCSS metric from the old rules might be different from the new rule "NcssCount". Statements that are
  split on multiple lines are still counted as one.

#### Deprecated Rules
* The Apex rule [`ExcessiveClassLength`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_apex_design.html#excessiveclasslength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_apex_design.html#ncsscount) to
  find big classes or create a custom XPath based rule using
  `//ApexFile[UserClass][@EndLine - @BeginLine > 1000]`.
* The Apex rules [`NcssConstructorCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_apex_design.html#ncssconstructorcount), [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_apex_design.html#ncssmethodcount), and
  [`NcssTypeCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_apex_design.html#ncsstypecount) have been deprecated in favor or the new rule [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_apex_design.html#ncsscount).
* The PL/SQL rule [`ExcessiveMethodLength`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#excessivemethodlength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//(MethodDeclaration|ProgramUnit|TriggerTimingPointSection|TriggerUnit|TypeMethod)[@EndLine - @BeginLine > 100]`.
* The PL/SQL rule [`ExcessiveObjectLength`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#excessiveobjectlength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//(PackageBody|PackageSpecification|ProgramUnit|TriggerUnit|TypeSpecification)[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule [`ExcessivePackageBodyLength`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#excessivepackagebodylength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//PackageBody[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule [`ExcessivePackageSpecificationLength`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#excessivepackagespecificationlength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//PackageSpecification[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule [`ExcessiveTypeLength`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#excessivetypelength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//TypeSpecification[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rules [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#ncssmethodcount) and [`NcssObjectCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#ncssobjectcount) have been
  deprecated in favor of the new rule [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0/pmd_rules_plsql_design.html#ncsscount).

### 🐛️ Fixed Issues
* core
    * [#4767](https://github.com/pmd/pmd/issues/4767): \[core] Deprecate old symboltable API
* apex-bestpractices
    * [#6203](https://github.com/pmd/pmd/issues/6203): \[apex] New Rule: Avoid Future Annotation
* apex-design
    * [#2128](https://github.com/pmd/pmd/issues/2128): \[apex] Merge NCSS count rules for Apex
* java
    * [#5689](https://github.com/pmd/pmd/issues/5689): \[java] Members of record should be in scope in record header
    * [#6256](https://github.com/pmd/pmd/issues/6256): \[java] java.lang.IllegalArgumentException: Invalid target type of type annotation for method or ctor type annotation: 19
* java-bestpractices
    * [#5820](https://github.com/pmd/pmd/issues/5820): \[java] GuardLogStatement recognizes that a string is a compile-time constant expression only if at first position
    * [#6188](https://github.com/pmd/pmd/issues/6188): \[java] UnitTestShouldIncludeAssert false positive when TestNG @<!-- -->Test.expectedException present
    * [#6193](https://github.com/pmd/pmd/issues/6193): \[java] New Rule: Always compare enum values with ==
* java-codestyle
    * [#6053](https://github.com/pmd/pmd/issues/6053): \[java] ModifierOrder false-positives with type annotations and type parameters (typeAnnotations = anywhere)
* java-errorprone
    * [#4742](https://github.com/pmd/pmd/issues/4742): \[java] EmptyFinalizer should not trigger if finalize method is final and class is not
    * [#6072](https://github.com/pmd/pmd/issues/6072): \[java] OverrideBothEqualsAndHashCodeOnComparable should not be required for record classes
    * [#6092](https://github.com/pmd/pmd/issues/6092): \[java] AssignmentInOperand false positive in 7.17.0 for case blocks in switch statements
    * [#6096](https://github.com/pmd/pmd/issues/6096): \[java] OverrideBothEqualsAndHashCodeOnComparable on class with lombok.EqualsAndHashCode annotation
    * [#6199](https://github.com/pmd/pmd/issues/6199): \[java] AssignmentInOperand: description of property allowIncrementDecrement is unclear
    * [#6273](https://github.com/pmd/pmd/issues/6273): \[java] TestClassWithoutTestCases documentation does not mention test prefixes
* java-performance
    * [#4577](https://github.com/pmd/pmd/issues/4577): \[java] UseArraysAsList with condition in loop
    * [#5071](https://github.com/pmd/pmd/issues/5071): \[java] UseArraysAsList should not warn when elements are skipped in array
* plsql-design
    * [#4326](https://github.com/pmd/pmd/issues/4326): \[plsql] Merge NCSS count rules for PL/SQL
* maintenance
    * [#5701](https://github.com/pmd/pmd/issues/5701): \[core] net.sourceforge.pmd.cpd.SourceManager has public methods

### 🚨️ API Changes

#### Deprecations
* core
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.19.0/net/sourceforge/pmd/lang/symboltable/package-summary.html#"><code>net.sourceforge.pmd.lang.symboltable</code></a>: All classes in this package are deprecated.
      The symbol table and type resolution implementation for Java has been rewritten from scratch
      for PMD 7.0.0. This package is the remains of the old symbol table API, that is only used by
      PL/SQL. For PMD 8.0.0 all these classes will be removed from pmd-core.
* apex
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0/net/sourceforge/pmd/lang/apex/rule/design/ExcessiveClassLengthRule.html#"><code>ExcessiveClassLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0/net/sourceforge/pmd/lang/apex/rule/design/NcssConstructorCountRule.html#"><code>NcssConstructorCountRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0/net/sourceforge/pmd/lang/apex/rule/design/NcssMethodCountRule.html#"><code>NcssMethodCountRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0/net/sourceforge/pmd/lang/apex/rule/design/NcssTypeCountRule.html#"><code>NcssTypeCountRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0/net/sourceforge/pmd/lang/apex/ast/ASTStatement.html#"><code>ASTStatement</code></a>: This AST node is not used and doesn't appear in the tree.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0/net/sourceforge/pmd/lang/apex/ast/ApexVisitor.html#visit(net.sourceforge.pmd.lang.apex.ast.ASTStatement,P)"><code>ApexVisitor#visit(ASTStatement, P)</code></a>
* plsql
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0/net/sourceforge/pmd/lang/plsql/rule/design/ExcessiveMethodLengthRule.html#"><code>ExcessiveMethodLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0/net/sourceforge/pmd/lang/plsql/rule/design/ExcessiveObjectLengthRule.html#"><code>ExcessiveObjectLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0/net/sourceforge/pmd/lang/plsql/rule/design/ExcessivePackageBodyLengthRule.html#"><code>ExcessivePackageBodyLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0/net/sourceforge/pmd/lang/plsql/rule/design/ExcessivePackageSpecificationLengthRule.html#"><code>ExcessivePackageSpecificationLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0/net/sourceforge/pmd/lang/plsql/rule/design/ExcessiveTypeLengthRule.html#"><code>ExcessiveTypeLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0/net/sourceforge/pmd/lang/plsql/rule/design/NcssMethodCountRule.html#"><code>NcssMethodCountRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0/net/sourceforge/pmd/lang/plsql/rule/design/NcssObjectCountRule.html#"><code>NcssObjectCountRule</code></a>

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6081](https://github.com/pmd/pmd/pull/6081): \[java] Fix #6072: OverrideBothEqualsAndHashCodeOnComparable should not be required for record classes - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6192](https://github.com/pmd/pmd/pull/6192): \[java] Fix #6053: ModifierOrder - consider type params - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6194](https://github.com/pmd/pmd/pull/6194): chore: always place type annotations on the type - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6195](https://github.com/pmd/pmd/pull/6195): chore: always compare enums with == - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6196](https://github.com/pmd/pmd/pull/6196): \[java] New Rule: EnumComparison - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6198](https://github.com/pmd/pmd/pull/6198): \[apex] New rule NcssCount to replace old Ncss*Count rules - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6201](https://github.com/pmd/pmd/pull/6201): \[java] Fix #6199: AssignmentInOperandRule: Update description of allowIncrementDecrement property - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6202](https://github.com/pmd/pmd/pull/6202): \[java] Fix #6188: UnitTestsShouldIncludeAssert - FP when TestNG @<!-- -->Test.expectedException is present - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6204](https://github.com/pmd/pmd/pull/6204): \[apex] Add rule to limit usage of @<!-- -->Future annotation - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#6214](https://github.com/pmd/pmd/pull/6214): \[plsql] New rule NcssCount to replace old Ncss*Count rules - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6217](https://github.com/pmd/pmd/pull/6217): \[doc] Add Blue Cave to known tools using PMD - [Jude Pereira](https://github.com/judepereira) (@judepereira)
* [#6227](https://github.com/pmd/pmd/pull/6227): \[java] UseArraysAsList: check increment - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6228](https://github.com/pmd/pmd/pull/6228): \[java] UseArraysAsList: skip when if-statements - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6229](https://github.com/pmd/pmd/pull/6229): chore: remove public methods from SourceManager - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6238](https://github.com/pmd/pmd/pull/6238): \[java] Fix #6096: Detect Lombok generated equals/hashCode in Comparable - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6249](https://github.com/pmd/pmd/pull/6249): \[core] Deprecate old symboltable API - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6250](https://github.com/pmd/pmd/pull/6250): chore: fail build for compiler warnings - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6251](https://github.com/pmd/pmd/pull/6251): \[java] Fix #6092: AssignmentInOperand false positive in 7.17.0 for case statements - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6255](https://github.com/pmd/pmd/pull/6255): \[java] Fix #4742: EmptyFinalizer should not trigger if finalize method is final and class is not - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6258](https://github.com/pmd/pmd/pull/6258): \[java] Fix #5820: GuardLogStatement recognizes that a string is a compile-time constant expression only if at first position - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6259](https://github.com/pmd/pmd/pull/6259): \[java] Fix #5689: Issue with scoping of record members - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6277](https://github.com/pmd/pmd/pull/6277): \[doc] Add button to copy configuration snippet - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6278](https://github.com/pmd/pmd/pull/6278): \[doc] TestClassWithoutTestCases: Mention test prefixes - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6280](https://github.com/pmd/pmd/pull/6280): \[ci] Exclude build resources from spring-framework for regression tester - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6282](https://github.com/pmd/pmd/pull/6282): \[java] Fix #6256: ignore invalid annotation type - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6197](https://github.com/pmd/pmd/pull/6197): Bump PMD from 7.17.0 to 7.18.0
* [#6205](https://github.com/pmd/pmd/pull/6205): chore(deps): bump junit.version from 6.0.0 to 6.0.1
* [#6206](https://github.com/pmd/pmd/pull/6206): chore(deps): bump org.checkerframework:checker-qual from 3.51.1 to 3.52.0
* [#6207](https://github.com/pmd/pmd/pull/6207): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.17.7 to 1.17.8
* [#6208](https://github.com/pmd/pmd/pull/6208): chore(deps): bump com.google.protobuf:protobuf-java from 4.32.1 to 4.33.0
* [#6209](https://github.com/pmd/pmd/pull/6209): chore(deps): bump com.puppycrawl.tools:checkstyle from 12.0.1 to 12.1.1
* [#6210](https://github.com/pmd/pmd/pull/6210): chore(deps): bump org.jacoco:jacoco-maven-plugin from 0.8.13 to 0.8.14
* [#6219](https://github.com/pmd/pmd/pull/6219): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.17.8 to 1.18.0
* [#6220](https://github.com/pmd/pmd/pull/6220): chore(deps): bump org.apache.maven.plugins:maven-release-plugin from 3.1.1 to 3.2.0
* [#6221](https://github.com/pmd/pmd/pull/6221): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.17.8 to 1.18.0
* [#6222](https://github.com/pmd/pmd/pull/6222): chore(deps): bump com.puppycrawl.tools:checkstyle from 12.1.1 to 12.1.2
* [#6223](https://github.com/pmd/pmd/pull/6223): chore(deps): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.2.0.4988 to 5.3.0.6276
* [#6240](https://github.com/pmd/pmd/pull/6240): chore(deps): bump ruby/setup-ruby from 1.267.0 to 1.268.0
* [#6241](https://github.com/pmd/pmd/pull/6241): chore(deps): bump actions/checkout from 5.0.0 to 5.0.1
* [#6242](https://github.com/pmd/pmd/pull/6242): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.0 to 1.18.1
* [#6243](https://github.com/pmd/pmd/pull/6243): chore(deps): bump org.scala-lang:scala-library from 2.13.17 to 2.13.18
* [#6244](https://github.com/pmd/pmd/pull/6244): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.0 to 1.18.1
* [#6245](https://github.com/pmd/pmd/pull/6245): chore(deps): bump org.apache.maven.plugins:maven-jar-plugin from 3.4.2 to 3.5.0
* [#6246](https://github.com/pmd/pmd/pull/6246): chore(deps): bump org.scala-lang:scala-reflect from 2.13.17 to 2.13.18
* [#6247](https://github.com/pmd/pmd/pull/6247): chore(deps): bump com.google.protobuf:protobuf-java from 4.33.0 to 4.33.1
* [#6263](https://github.com/pmd/pmd/pull/6263): chore(deps): bump actions/checkout from 5.0.1 to 6.0.0
* [#6264](https://github.com/pmd/pmd/pull/6264): chore(deps): bump org.apache.commons:commons-lang3 from 3.19.0 to 3.20.0
* [#6265](https://github.com/pmd/pmd/pull/6265): chore(deps): bump actions/create-github-app-token from 2.1.4 to 2.2.0
* [#6266](https://github.com/pmd/pmd/pull/6266): chore(deps): bump scalameta.version from 4.14.1 to 4.14.2
* [#6267](https://github.com/pmd/pmd/pull/6267): chore(deps): bump org.codehaus.mojo:versions-maven-plugin from 2.19.1 to 2.20.1
* [#6281](https://github.com/pmd/pmd/pull/6281): Bump build-tools from 35 to 36
* [#6283](https://github.com/pmd/pmd/pull/6283): Bump PMD Designer from 7.10.0 to 7.19.1

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 122 commits
* 44 closed tickets & PRs
* Days since last release: 28



## 31-October-2025 - 7.18.0

The PMD team is pleased to announce PMD 7.18.0.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Build Requirement is Java 17](#build-requirement-is-java-17)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

#### Build Requirement is Java 17
From now on, Java 17 or newer is required to build PMD. PMD itself still remains compatible with Java 8,
so that it still can be used in a pure Java 8 environment. This allows us to use the latest
checkstyle version during the build.

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule [`IdenticalConditionalBranches`](https://docs.pmd-code.org/pmd-doc-7.18.0/pmd_rules_java_errorprone.html#identicalconditionalbranches) finds conditional statements
  that do the same thing when the condition is true and false. This is either incorrect or redundant.
* The new Java rule [`LabeledStatement`](https://docs.pmd-code.org/pmd-doc-7.18.0/pmd_rules_java_bestpractices.html#labeledstatement) finds labeled statements in code.
  Labels make control flow difficult to understand and should be avoided. By default, the rule allows labeled
  loops (do, while, for). But it has a property to flag also those labeled loops.
* The new Java rule [`UnusedLabel`](https://docs.pmd-code.org/pmd-doc-7.18.0/pmd_rules_java_bestpractices.html#unusedlabel) finds unused labels which are unnecessary and
  only make the code hard to read. This new rule will be part of the quickstart ruleset.

#### Changed Rules
* [`ConfusingTernary`](https://docs.pmd-code.org/pmd-doc-7.18.0/pmd_rules_java_codestyle.html#confusingternary) has a new property `nullCheckBranch` to control, whether null-checks
  should be allowed (the default case) or should lead to a violation.
* [`AvoidCatchingGenericException`](https://docs.pmd-code.org/pmd-doc-7.18.0/pmd_rules_java_errorprone.html#avoidcatchinggenericexception) is now configurable with the new property
  `typesThatShouldNotBeCaught`.  
  ⚠️ The rule has also been moved from category "Design" to category "Error Prone". If you are currently bulk-adding
  all the rules from the "Design" category into your custom ruleset, then you need to add the rule explicitly
  again (otherwise it won't be included anymore):
  ```xml
  <rule ref="category/java/errorprone.xml/AvoidCatchingGenericException" />
  ```

#### Deprecated Rules
* The Java rule [`AvoidCatchingNPE`](https://docs.pmd-code.org/pmd-doc-7.18.0/pmd_rules_java_errorprone.html#avoidcatchingnpe) has been deprecated in favor of the updated rule
  [`AvoidCatchingGenericException`](https://docs.pmd-code.org/pmd-doc-7.18.0/pmd_rules_java_errorprone.html#avoidcatchinggenericexception), which is now configurable.
* The Java rule [`AvoidCatchingThrowable`](https://docs.pmd-code.org/pmd-doc-7.18.0/pmd_rules_java_errorprone.html#avoidcatchingthrowable) has been deprecated in favor of the updated rule
  [`AvoidCatchingGenericException`](https://docs.pmd-code.org/pmd-doc-7.18.0/pmd_rules_java_errorprone.html#avoidcatchinggenericexception), which is now configurable.

### 🐛️ Fixed Issues
* general
  * [#4714](https://github.com/pmd/pmd/issues/4714): \[core] Allow trailing commas in multivalued properties
  * [#5873](https://github.com/pmd/pmd/issues/5873): \[ci] Run integration test with Java 25
  * [#6012](https://github.com/pmd/pmd/issues/6012): \[pmd-rulesets] Rulesets should be in alphabetical order
  * [#6073](https://github.com/pmd/pmd/issues/6073): \[doc] Search improvements
  * [#6097](https://github.com/pmd/pmd/issues/6097): \[doc] Add PMD versions dropdown
  * [#6098](https://github.com/pmd/pmd/issues/6098): \[doc] Add a copy URL button
  * [#6101](https://github.com/pmd/pmd/issues/6101): \[doc] Highlight current header in TOC
  * [#6149](https://github.com/pmd/pmd/issues/6149): \[doc] Reproducible Build Documentation is outdated - PMD is now built using Java 17
  * [#6150](https://github.com/pmd/pmd/issues/6150): \[core] Reduce memory usage of CPD's MatchCollector
* apex
  * [#5935](https://github.com/pmd/pmd/issues/5935): \[apex] @<!-- -->SuppressWarnings - allow whitespace around comma when suppressing multiple rules
* apex-design
  * [#6022](https://github.com/pmd/pmd/issues/6022): \[apex] ExcessiveClassLength/ExcessiveParameterList include the metric in the message
* apex-documentation
  * [#6189](https://github.com/pmd/pmd/issues/6189): \[apex] ApexDoc rule doesn't match published Salesforce ApexDoc specification
* java
  * [#4904](https://github.com/pmd/pmd/issues/4904): \[java] Renderers output wrong class qualified name for nested classes
  * [#6127](https://github.com/pmd/pmd/issues/6127): \[java] Incorrect variable name in violation
  * [#6132](https://github.com/pmd/pmd/issues/6132): \[java] Implement main method launch protocol priorities
  * [#6146](https://github.com/pmd/pmd/issues/6146): \[java] ClassCastException: class InferenceVarSym cannot be cast to class JClassSymbol
* java-bestpractices
  * [#2928](https://github.com/pmd/pmd/issues/2928): \[java] New rules about labeled statements
  * [#4122](https://github.com/pmd/pmd/issues/4122): \[java] CheckResultSet false-positive with local variable
  * [#6124](https://github.com/pmd/pmd/issues/6124): \[java] UnusedLocalVariable: fix false negatives in pattern matching
  * [#6169](https://github.com/pmd/pmd/issues/6169): \[java] AvoidUsingHardCodedIP: violation message should mention the hard coded address
  * [#6171](https://github.com/pmd/pmd/issues/6171): \[java] AvoidUsingHardCodedIP: fix false positive for IPv6
* java-codestyle
  * [#5919](https://github.com/pmd/pmd/issues/5919): \[java] ClassNamingConventions: Include integration tests in testClassPattern by default
  * [#6004](https://github.com/pmd/pmd/issues/6004): \[java] Make ConfusingTernary != null configurable
  * [#6029](https://github.com/pmd/pmd/issues/6029): \[java] Fix UnnecessaryCast false-negative in method calls
  * [#6057](https://github.com/pmd/pmd/issues/6057): \[java] ModifierOrder false positive on "abstract sealed class"
  * [#6079](https://github.com/pmd/pmd/issues/6079): \[java] IdenticalCatchBranches: False negative for overriden method calls
  * [#6123](https://github.com/pmd/pmd/issues/6123): \[java] UselessParentheses FP around switch expression
  * [#6131](https://github.com/pmd/pmd/issues/6131): \[java] ModifierOrder: wrong enum values documented, indirectly causing xml parse errors
* java-design
  * [#1499](https://github.com/pmd/pmd/issues/1499): \[java] AvoidDeeplyNestedIfStmts violations can be unintentionally undetected
  * [#5569](https://github.com/pmd/pmd/issues/5569): \[java] ExcessivePublicCount should report number of public "things"
* java-documentation
  * [#6058](https://github.com/pmd/pmd/issues/6058): \[java] DanglingJavadoc FP in module-info files
  * [#6103](https://github.com/pmd/pmd/issues/6103): \[java] DanglingJavadoc false positive on record compact constructors
* java-errorprone
  * [#5042](https://github.com/pmd/pmd/issues/5042): \[java] CloseResource false-positive on Pattern Matching with instanceof
  * [#5878](https://github.com/pmd/pmd/issues/5878): \[java] DontUseFloatTypeForLoopIndices false-negative if variable is declared before loop
  * [#6038](https://github.com/pmd/pmd/issues/6038): \[java] Merge AvoidCatchingNPE and AvoidCatchingThrowable into AvoidCatchingGenericException
  * [#6055](https://github.com/pmd/pmd/issues/6055): \[java] UselessPureMethodCall false positive with AtomicInteger::getAndIncrement
  * [#6060](https://github.com/pmd/pmd/issues/6060): \[java] UselessPureMethodCall false positive on ZipInputStream::getNextEntry
  * [#6075](https://github.com/pmd/pmd/issues/6075): \[java] AssignmentInOperand false positive with lambda expressions
  * [#6083](https://github.com/pmd/pmd/issues/6083): \[java] New rule IdenticalConditionalBranches
* java-multithreading
  * [#5880](https://github.com/pmd/pmd/issues/5880): \[java] DoubleCheckedLocking is not detected if more than 1 assignment or more than 2 if statements
* java-performance
  * [#6172](https://github.com/pmd/pmd/issues/6172): \[java] InefficientEmptyStringCheck should include String#strip
* java-security
  * [#6191](https://github.com/pmd/pmd/issues/6191): \[java] HardCodedCryptoKey: NPE when constants from parent class are used
* plsql-design
  * [#6077](https://github.com/pmd/pmd/issues/6077): \[plsql] Excessive\*/Ncss\*Count/NPathComplexity include the metric

### 🚨️ API Changes

#### Deprecations
* java
  * The following methods have been deprecated. Due to refactoring of the internal base class, these methods are not
    used anymore and are not required to be implemented anymore:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.18.0/net/sourceforge/pmd/lang/java/rule/design/ExcessiveImportsRule.html#isViolation(net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit,int)"><code>ExcessiveImportsRule#isViolation</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.18.0/net/sourceforge/pmd/lang/java/rule/design/ExcessiveParameterListRule.html#isViolation(net.sourceforge.pmd.lang.java.ast.ASTFormalParameters,int)"><code>ExcessiveParameterListRule#isViolation</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.18.0/net/sourceforge/pmd/lang/java/rule/design/ExcessivePublicCountRule.html#isViolation(net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration,int)"><code>ExcessivePublicCountRule#isViolation</code></a>

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6021](https://github.com/pmd/pmd/pull/6021): \[java] Fix #5569: ExcessiveImports/ExcessiveParameterList/ExcessivePublicCount include the metric in the message - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6022](https://github.com/pmd/pmd/pull/6022): \[apex] ExcessiveClassLength/ExcessiveParameterList include the metric in the message - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6023](https://github.com/pmd/pmd/pull/6023): \[test] Fix #6012: Alphabetically sort all default rules - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6024](https://github.com/pmd/pmd/pull/6024): \[java] Fix #5878: DontUseFloatTypeForLoopIndices now checks the UpdateStatement as well - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6029](https://github.com/pmd/pmd/pull/6029): \[java] Fix UnnecessaryCast false-negative in method calls - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6031](https://github.com/pmd/pmd/pull/6031): \[java] Fix #5880: False Negatives in DoubleCheckedLocking - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6039](https://github.com/pmd/pmd/pull/6039): \[core] Fix #4714: trim token before feeding it to the extractor - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6040](https://github.com/pmd/pmd/pull/6040): \[java,apex,plsql,velocity] Change description of "minimum" parameter - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6042](https://github.com/pmd/pmd/pull/6042): \[java] Fix #2928: New Rules UnusedLabel and LabeledStatement - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6043](https://github.com/pmd/pmd/pull/6043): \[java] Reactivate deactivated test in LocalVariableCouldBeFinal - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6051](https://github.com/pmd/pmd/pull/6051): \[java] Fix #6038: Make AvoidCatchingGenericException configurable - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6056](https://github.com/pmd/pmd/pull/6056): chore: fix dogfood issues from new rules - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6059](https://github.com/pmd/pmd/pull/6059): \[java] Fix #6058: DanglingJavadoc FP in module-info files - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6061](https://github.com/pmd/pmd/pull/6061): \[core] chore: Bump minimum Java version required for building to 17 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6071](https://github.com/pmd/pmd/pull/6071): \[java] Fix #5919: Add integration tests to ClassNamingConventions testClassRegex - [Anton Bobov](https://github.com/abobov) (@abobov)
* [#6073](https://github.com/pmd/pmd/pull/6073): \[doc] Search improvements - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6074](https://github.com/pmd/pmd/pull/6074): \[apex] Fix @<!-- -->SuppressWarnings with whitespace around comma - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#6077](https://github.com/pmd/pmd/pull/6077): \[plsql] Excessive*/Ncss*Count/NPathComplexity include the metric - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6078](https://github.com/pmd/pmd/pull/6078): \[java] Fix #6075: Fix FP in AssignmentInOperandRule - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6080](https://github.com/pmd/pmd/pull/6080): \[java] Fix #6079: IdenticalCatchBranches for overriden method calls - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6082](https://github.com/pmd/pmd/pull/6082): \[java] Fix false positives in UselessPureMethodCall for streams and atomics - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6083](https://github.com/pmd/pmd/pull/6083): \[java] New rule IdenticalConditionalBranches - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6085](https://github.com/pmd/pmd/pull/6085): \[java] Fix false positive for ModifierOrder - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6093](https://github.com/pmd/pmd/pull/6093): \[ci] Fix #5873: Run integration tests with Java 25 additionally - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6097](https://github.com/pmd/pmd/pull/6097): \[doc] Add PMD versions dropdown - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6098](https://github.com/pmd/pmd/pull/6098): \[doc] Add a copy URL button - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6100](https://github.com/pmd/pmd/pull/6100): \[java] AvoidDeeplyNestedIfStmts: fix false negative with if-else - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6101](https://github.com/pmd/pmd/pull/6101): \[doc] Highlight current header in TOC - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6112](https://github.com/pmd/pmd/pull/6112): \[java] DanglingJavadoc: fix false positive for compact constructors - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6114](https://github.com/pmd/pmd/pull/6114): \[java] Fix #4122: CheckResultSet false-positive with local variable - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6116](https://github.com/pmd/pmd/pull/6116): \[java] ConfusingTernary: add configuration property for null checks - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6124](https://github.com/pmd/pmd/pull/6124): \[java] UnusedLocalVariable: fix false negatives in pattern matching - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6128](https://github.com/pmd/pmd/pull/6128): \[java] Fix #4904: Correct class name in violation decorator - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6129](https://github.com/pmd/pmd/pull/6129): \[java] Fix #6127: Correct var name in violation decorator - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6130](https://github.com/pmd/pmd/pull/6130): \[java] UselessParentheses: fix false positives for switch expressions - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6132](https://github.com/pmd/pmd/pull/6132): \[java] Implement main method launch protocol priorities - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6133](https://github.com/pmd/pmd/pull/6133): \[java] Fix #5042: CloseResource: fix false positive with pattern matching - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6150](https://github.com/pmd/pmd/pull/6150): \[core] Reduce memory usage of CPD's MatchCollector - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6152](https://github.com/pmd/pmd/pull/6152): chore(deps): Update Saxon-HE from 12.5 to 12.9 - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6156](https://github.com/pmd/pmd/pull/6156): \[java] Fix #6146: ClassCastException in TypeTestUtil - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6164](https://github.com/pmd/pmd/pull/6164): \[doc] Update reproducible build info with Java 17 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6166](https://github.com/pmd/pmd/pull/6166): \[doc] Use emoji variants - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6170](https://github.com/pmd/pmd/pull/6170): \[java] Fix #6169: AvoidUsingHardCodedIP - mention address in message - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6171](https://github.com/pmd/pmd/pull/6171): \[java] AvoidUsingHardCodedIP: fix false positive for IPv6 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6172](https://github.com/pmd/pmd/pull/6172): \[java] InefficientEmptyStringCheck should include String#strip - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6185](https://github.com/pmd/pmd/pull/6185): \[java] Fix #6131: Correct enum values for ModifierOrder - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6190](https://github.com/pmd/pmd/pull/6190): \[apex] Update ApexDoc rule to match the published specification - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#6191](https://github.com/pmd/pmd/pull/6191): \[java] HardCodedCryptoKey: NPE when constants from parent class are used - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6034](https://github.com/pmd/pmd/pull/6034): chore(deps): bump com.puppycrawl.tools:checkstyle from 10.26.1 to 11.0.1
* [#6054](https://github.com/pmd/pmd/pull/6054): Bump PMD from 7.16.0 to 7.17.0
* [#6062](https://github.com/pmd/pmd/pull/6062): chore(deps): bump surefire.version from 3.5.3 to 3.5.4
* [#6063](https://github.com/pmd/pmd/pull/6063): chore(deps): bump ruby/setup-ruby from 1.257.0 to 1.258.0
* [#6064](https://github.com/pmd/pmd/pull/6064): chore(deps): bump com.google.code.gson:gson from 2.13.1 to 2.13.2
* [#6065](https://github.com/pmd/pmd/pull/6065): chore(deps): bump actions/create-github-app-token from 2.1.1 to 2.1.4
* [#6066](https://github.com/pmd/pmd/pull/6066): chore(deps): bump org.apache.groovy:groovy from 5.0.0 to 5.0.1
* [#6067](https://github.com/pmd/pmd/pull/6067): chore(deps): bump org.apache.maven.plugins:maven-shade-plugin from 3.6.0 to 3.6.1
* [#6068](https://github.com/pmd/pmd/pull/6068): chore(deps): bump scalameta.version from 4.13.9 to 4.13.10
* [#6076](https://github.com/pmd/pmd/pull/6076): Bump pmdtester from 1.6.0 to 1.6.1
* [#6086](https://github.com/pmd/pmd/pull/6086): chore(deps): bump ruby/setup-ruby from 1.258.0 to 1.263.0
* [#6087](https://github.com/pmd/pmd/pull/6087): chore(deps-dev): bump log4j.version from 2.25.1 to 2.25.2
* [#6088](https://github.com/pmd/pmd/pull/6088): chore(deps): bump org.mockito:mockito-core from 5.19.0 to 5.20.0
* [#6089](https://github.com/pmd/pmd/pull/6089): chore(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.11.3 to 3.12.0
* [#6090](https://github.com/pmd/pmd/pull/6090): chore(deps): bump org.codehaus.mojo:versions-maven-plugin from 2.19.0 to 2.19.1
* [#6091](https://github.com/pmd/pmd/pull/6091): chore(deps): bump org.apache.maven.plugins:maven-compiler-plugin from 3.14.0 to 3.14.1
* [#6094](https://github.com/pmd/pmd/pull/6094): chore(deps): Bump rexml from 3.4.1 to 3.4.4
* [#6104](https://github.com/pmd/pmd/pull/6104): chore(deps): bump actions/cache from 4.2.4 to 4.3.0
* [#6105](https://github.com/pmd/pmd/pull/6105): chore(deps): bump org.scala-lang:scala-library from 2.13.16 to 2.13.17
* [#6106](https://github.com/pmd/pmd/pull/6106): chore(deps): bump junit.version from 5.13.4 to 6.0.0
* [#6107](https://github.com/pmd/pmd/pull/6107): chore(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.5.1 to 3.6.0
* [#6108](https://github.com/pmd/pmd/pull/6108): chore(deps): bump com.puppycrawl.tools:checkstyle from 10.26.1 to 11.1.0
* [#6109](https://github.com/pmd/pmd/pull/6109): chore(deps-dev): bump org.assertj:assertj-core from 3.27.4 to 3.27.6
* [#6110](https://github.com/pmd/pmd/pull/6110): chore(deps): bump org.sonatype.central:central-publishing-maven-plugin from 0.8.0 to 0.9.0
* [#6118](https://github.com/pmd/pmd/pull/6118): chore(deps): bump com.google.protobuf:protobuf-java from 4.32.0 to 4.32.1
* [#6119](https://github.com/pmd/pmd/pull/6119): chore(deps): bump com.github.hazendaz.maven:coveralls-maven-plugin from 4.7.0 to 5.0.0
* [#6120](https://github.com/pmd/pmd/pull/6120): chore(deps): bump org.scala-lang:scala-reflect from 2.13.16 to 2.13.17
* [#6121](https://github.com/pmd/pmd/pull/6121): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.23.1 to 0.24.1
* [#6122](https://github.com/pmd/pmd/pull/6122): chore(deps): bump bigdecimal from 3.2.3 to 3.3.0 in /docs
* [#6136](https://github.com/pmd/pmd/pull/6136): chore(deps): bump ruby/setup-ruby from 1.263.0 to 1.265.0
* [#6137](https://github.com/pmd/pmd/pull/6137): chore(deps): bump org.apache.maven.plugins:maven-enforcer-plugin from 3.6.1 to 3.6.2
* [#6138](https://github.com/pmd/pmd/pull/6138): chore(deps): bump scalameta.version from 4.13.10 to 4.14.0
* [#6139](https://github.com/pmd/pmd/pull/6139): chore(deps): bump com.puppycrawl.tools:checkstyle from 11.1.0 to 12.0.1
* [#6140](https://github.com/pmd/pmd/pull/6140): chore(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.6.0 to 3.6.1
* [#6141](https://github.com/pmd/pmd/pull/6141): chore(deps): bump org.ow2.asm:asm from 9.8 to 9.9
* [#6142](https://github.com/pmd/pmd/pull/6142): chore(deps): bump org.apache.commons:commons-lang3 from 3.18.0 to 3.19.0
* [#6143](https://github.com/pmd/pmd/pull/6143): chore(deps): bump bigdecimal from 3.3.0 to 3.3.1 in /docs
* [#6152](https://github.com/pmd/pmd/pull/6152): chore(deps): Update Saxon-HE from 12.5 to 12.9
* [#6157](https://github.com/pmd/pmd/pull/6157): chore(deps-dev): bump com.google.guava:guava from 33.4.8-jre to 33.5.0-jre
* [#6158](https://github.com/pmd/pmd/pull/6158): chore(deps): bump scalameta.version from 4.14.0 to 4.14.1
* [#6159](https://github.com/pmd/pmd/pull/6159): chore(deps): bump org.apache.maven.plugins:maven-dependency-plugin from 3.8.1 to 3.9.0
* [#6160](https://github.com/pmd/pmd/pull/6160): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.24.1 to 0.24.2
* [#6161](https://github.com/pmd/pmd/pull/6161): chore(deps): bump org.apache.maven.plugins:maven-pmd-plugin from 3.27.0 to 3.28.0
* [#6162](https://github.com/pmd/pmd/pull/6162): chore(deps): bump org.apache.maven.plugins:maven-antrun-plugin from 3.1.0 to 3.2.0
* [#6165](https://github.com/pmd/pmd/pull/6165): Bump pmdtester from 1.6.1 to 1.6.2
* [#6173](https://github.com/pmd/pmd/pull/6173): chore(deps): bump actions/upload-artifact from 4.6.2 to 5.0.0
* [#6174](https://github.com/pmd/pmd/pull/6174): chore(deps): bump ruby/setup-ruby from 1.265.0 to 1.267.0
* [#6175](https://github.com/pmd/pmd/pull/6175): chore(deps): bump actions/download-artifact from 5.0.0 to 6.0.0
* [#6178](https://github.com/pmd/pmd/pull/6178): chore(deps): bump org.checkerframework:checker-qual from 3.49.5 to 3.51.1
* [#6179](https://github.com/pmd/pmd/pull/6179): chore(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.6.1 to 3.6.2
* [#6180](https://github.com/pmd/pmd/pull/6180): chore(deps): bump io.github.apex-dev-tools:apex-ls_2.13 from 5.10.0 to 6.0.1
* [#6181](https://github.com/pmd/pmd/pull/6181): chore(deps): bump org.apache.groovy:groovy from 5.0.1 to 5.0.2
* [#6182](https://github.com/pmd/pmd/pull/6182): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.17.7 to 1.17.8

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 233 commits
* 76 closed tickets & PRs
* Days since last release: 49



## 12-September-2025 - 7.17.0

The PMD team is pleased to announce PMD 7.17.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [✨ New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
    * [CPD: New Markdown Report Format](#cpd-new-markdown-report-format)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecations](#deprecations)
    * [Experimental API](#experimental-api)
    * [PMD Report Format CSV](#pmd-report-format-csv)
    * [Rule Test Schema](#rule-test-schema)
    * [Deprecations](#deprecations)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### ✨ New Rules

This release brings several new rules for both Java and Apex. Please try them out
and submit feedback on [our issue tracker](https://github.com/pmd/pmd/issues)!

* The new apex rule [`AnnotationsNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_apex_codestyle.html#annotationsnamingconventions) enforces that annotations
  are used consistently in PascalCase.  
  The rule is referenced in the quickstart.xml ruleset for Apex.
* The new java rule [`TypeParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_codestyle.html#typeparameternamingconventions) replaces the now deprecated rule
  GenericsNaming. The new rule is configurable and checks for naming conventions of type parameters in
  generic types and methods. It can be configured via a regular expression.  
  By default, this rule uses the standard Java naming convention (single uppercase letter).  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule [`OverrideBothEqualsAndHashCodeOnComparable`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_errorprone.html#overridebothequalsandhashcodeoncomparable) finds missing
  `hashCode()` and/or `equals()` methods on types that implement `Comparable`. This is important if
  instances of these classes are used in collections. Failing to do so can lead to unexpected behavior in sets
  which then do not conform to the `Set` interface. While the `Set` interface relies on
  `equals()` to determine object equality, sorted sets like `TreeSet` use
  `compareTo()` instead. The same issue can arise when such objects are used
  as keys in sorted maps.  
  This rule is very similar to [`OverrideBothEqualsAndHashcode`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_errorprone.html#overridebothequalsandhashcode) which has always been
  skipping `Comparable` and only reports if one of the two methods is missing. The new rule will also report,
  if both methods (hashCode and equals) are missing.  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule [`UselessPureMethodCall`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_errorprone.html#uselesspuremethodcall) finds method calls of pure methods
  whose result is not used. Ignoring the result of such method calls is likely as mistake as pure
  methods are side effect free.  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule [`RelianceOnDefaultCharset`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_bestpractices.html#relianceondefaultcharset) finds method calls that
  depend on the JVM's default charset. Using these method without specifying the charset explicitly
  can lead to unexpected behavior on different platforms.
* Thew new java rule [`VariableCanBeInlined`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_codestyle.html#variablecanbeinlined) finds local variables that are
  immediately returned or thrown. This rule replaces the old rule [`UnnecessaryLocalBeforeReturn`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_codestyle.html#unnecessarylocalbeforereturn)
  which only considered return statements. The new rule also finds unnecessary local variables
  before throw statements.  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule [`CollectionTypeMismatch`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_errorprone.html#collectiontypemismatch) detects calls to
  collection methods where we suspect the types are incompatible. This happens for instance
  when you try to remove a `String` from a `Collection<Integer>`: although it is allowed
  to write this because `remove` takes an `Object` parameter, it is most likely a mistake.  
  This rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule [`DanglingJavadoc`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_documentation.html#danglingjavadoc) finds Javadoc comments that
  do not belong to a class, method or field. These comments are ignored by the Javadoc tool
  and should either be corrected or removed.  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule [`ModifierOrder`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_codestyle.html#modifierorder) (`codestyle`) finds incorrectly ordered modifiers
  (e.g., `static public` instead of `public static`). It ensures modifiers appear in the correct order as
  recommended by the Java Language Specification.

#### Deprecated Rules
* The java rule [`GenericsNaming`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_codestyle.html#genericsnaming) has been deprecated for removal in favor
  of the new rule [`TypeParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_codestyle.html#typeparameternamingconventions).
* The java rule [`AvoidLosingExceptionInformation`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_errorprone.html#avoidlosingexceptioninformation) has been deprecated for removal
  in favor of the new rule [`UselessPureMethodCall`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_errorprone.html#uselesspuremethodcall).
* The java rule [`UselessOperationOnImmutable`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_errorprone.html#uselessoperationonimmutable) has been deprecated for removal
  in favor of the new rule [`UselessPureMethodCall`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_errorprone.html#uselesspuremethodcall).
* The java rule [`UnnecessaryLocalBeforeReturn`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_codestyle.html#unnecessarylocalbeforereturn) has been deprecated for removal
  in favor of the new rule [`VariableCanBeInlined`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_java_codestyle.html#variablecanbeinlined).

#### CPD: New Markdown Report Format
This PMD version ships with a simple Markdown based output format for CPD. It outputs all duplications
one after another including the code snippets as code blocks.  
See [Report formats for CPD](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_userdocs_cpd_report_formats.html#markdown).

### 🐛 Fixed Issues
* apex-codestyle
  * [#5650](https://github.com/pmd/pmd/issues/5650): \[apex] New Rule: AnnotationsNamingConventions
* core
  * [#4721](https://github.com/pmd/pmd/issues/4721): \[core] chore: Enable XML rule MissingEncoding in dogfood ruleset
  * [#5849](https://github.com/pmd/pmd/issues/5849): \[core] Support Markdown Output for CPD Reports
  * [#5958](https://github.com/pmd/pmd/issues/5958): \[core] CSVRenderer: Add begin and end for line and columns (default off)
* java
  * [#5874](https://github.com/pmd/pmd/issues/5874): \[java] Update java regression tests with Java 25 language features
  * [#5960](https://github.com/pmd/pmd/issues/5960): \[java] Avoid/reduce duplicate error messages for some rules
  * [#6014](https://github.com/pmd/pmd/issues/6014): \[java] Crash when encountering a java comment at the end of a file
* java-bestpractices
  * [#2186](https://github.com/pmd/pmd/issues/2186): \[java] New Rule: RelianceOnDefaultCharset
  * [#4500](https://github.com/pmd/pmd/issues/4500): \[java] AvoidReassigningLoopVariables - false negatives within for-loops and skip allowed
  * [#4770](https://github.com/pmd/pmd/issues/4770): \[java] UnusedFormalParameter should ignore public constructor as same as method
  * [#5198](https://github.com/pmd/pmd/issues/5198): \[java] CheckResultSet false-positive with local variable checked in a while loop
* java-codestyle
  * [#972](https://github.com/pmd/pmd/issues/972):   \[java] Improve naming conventions rules
  * [#4916](https://github.com/pmd/pmd/issues/4916): \[java] UseExplicitTypes: cases where 'var' should be unobjectionable
  * [#5601](https://github.com/pmd/pmd/issues/5601): \[java] New Rule: ModifierOrder
  * [#5770](https://github.com/pmd/pmd/issues/5770): \[java] New Rule: VariableCanBeInlined: Local variables should not be declared and then immediately returned or thrown
  * [#5922](https://github.com/pmd/pmd/issues/5922): \[java] New Rule: TypeParameterNamingConventions
  * [#5948](https://github.com/pmd/pmd/issues/5948): \[java] UnnecessaryBoxing false positive when calling `List.remove(int)`
  * [#5982](https://github.com/pmd/pmd/issues/5982): \[java] More detailed message for the UselessParentheses rule
* java-design
  * [#4911](https://github.com/pmd/pmd/issues/4911): \[java] AvoidRethrowingException should allow rethrowing exception subclasses
  * [#5023](https://github.com/pmd/pmd/issues/5023): \[java] UseUtilityClass implementation hardcodes a message instead of using the one defined in the XML
* java-documentation
  * [#5916](https://github.com/pmd/pmd/issues/5916): \[java] New Rule: DanglingJavadoc
* java-errorprone
  * [#3401](https://github.com/pmd/pmd/issues/3401): \[java] Improve AvoidUsingOctalValues documentation
  * [#3434](https://github.com/pmd/pmd/issues/3434): \[java] False negatives in AssignmentInOperand Rule
  * [#5837](https://github.com/pmd/pmd/issues/5837): \[java] New Rule: OverrideBothEqualsAndHashCodeOnComparable
  * [#5881](https://github.com/pmd/pmd/issues/5881): \[java] AvoidLosingExceptionInformation does not trigger when inside if-else
  * [#5907](https://github.com/pmd/pmd/issues/5907): \[java] New Rule: UselessPureMethodCall
  * [#5915](https://github.com/pmd/pmd/issues/5915): \[java] AssignmentInOperand not raised when inside do-while loop
  * [#5949](https://github.com/pmd/pmd/issues/5949): \[java] New Rule: CollectionTypeMismatch: for Collections methods that take Object as a parameter
  * [#5974](https://github.com/pmd/pmd/issues/5974): \[java] CloseResourceRule: NullPointerException while analyzing
* test
  * [#5973](https://github.com/pmd/pmd/issues/5973): \[test] Enable XML validation for rule tests

### 🚨 API Changes
#### Deprecations
* pmd-java:
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.17.0/net/sourceforge/pmd/lang/java/symbols/JClassSymbol.html#annotationAppliesTo(java.lang.annotation.ElementType)"><code>JClassSymbol#annotationAppliesTo</code></a>: Use
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.17.0/net/sourceforge/pmd/lang/java/symbols/JClassSymbol.html#annotationAppliesToContext(java.lang.annotation.ElementType,net.sourceforge.pmd.lang.LanguageVersion)"><code>JClassSymbol#annotationAppliesToContext</code></a>
    instead.
#### Experimental API
* pmd-core: <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.17.0/net/sourceforge/pmd/reporting/RuleContext.html#addViolationWithPosition(net.sourceforge.pmd.lang.ast.Node,net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken,java.lang.String,java.lang.Object...)"><code>RuleContext#addViolationWithPosition</code></a>

#### PMD Report Format CSV
The CSV report format for PMD as three new columns:

* End Line
* Begin Column
* End Column

These columns are not enabled by default, but can be activated via their respective renderer properties.
See [Report formats for PMD](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_userdocs_report_formats.html#csv).

#### Rule Test Schema
When executing rule tests, the rule test XML file will be validated against the schema and the tests will fail
if the XML file is invalid.

There was a small bug in the schema around verifying suppressed violations: If a test wanted to verify, that there
are _no_ suppressed violations, then this was not possible. Now the `<expected-suppression>` element may be
empty. This is available in version 1.1.1 of the schema.
See [Testing your rules](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_userdocs_extending_testing.html) for more information.

#### Deprecations
* test
  * The method <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.17.0/net/sourceforge/pmd/test/lang/rule/AbstractRuleSetFactoryTest.html#hasCorrectEncoding(java.lang.String)"><code>AbstractRuleSetFactoryTest#hasCorrectEncoding</code></a> will be removed.
    PMD has the rule [`MissingEncoding`](https://docs.pmd-code.org/pmd-doc-7.17.0/pmd_rules_xml_bestpractices.html#missingencoding) for XML files that should be used instead.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5601](https://github.com/pmd/pmd/pull/5601): \[java] New Rule: ModifierOrder - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5822](https://github.com/pmd/pmd/pull/5822): \[apex] Fix #5650: New Rule: AnnotationsNamingConventions - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5847](https://github.com/pmd/pmd/pull/5847): \[java] Fix #5770: New Rule: VariableCanBeInlined - [Vincent Potucek](https://github.com/Pankraz76) (@Pankraz76)
* [#5856](https://github.com/pmd/pmd/pull/5856): \[java] Fix #5837: New Rule: OverrideBothEqualsAndHashCodeOnComparable - [Vincent Potucek](https://github.com/Pankraz76) (@Pankraz76)
* [#5907](https://github.com/pmd/pmd/pull/5907): \[java] New Rule: UselessPureMethodCall - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5916](https://github.com/pmd/pmd/pull/5916): \[java] New Rule: DanglingJavadoc - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5922](https://github.com/pmd/pmd/pull/5922): \[java] Fix #972: Add a new rule TypeParameterNamingConventions - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5924](https://github.com/pmd/pmd/pull/5924): \[java] Fix #5915: Fix AssignmentInOperandRule to also work an do-while loops and switch statements - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5930](https://github.com/pmd/pmd/pull/5930): \[java] Fix #4500: Fix AvoidReassigningLoopVariablesRule to allow only simple assignments in the forReassign=skip case - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5931](https://github.com/pmd/pmd/pull/5931): \[java] Fix #5023: Fix UseUtilityClassRule to use the message provided in design.xml - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5932](https://github.com/pmd/pmd/pull/5932): \[ci] Reuse GitHub Pre-Releases - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5933](https://github.com/pmd/pmd/pull/5933): \[test] Fix QuickstartRulesetTests to detect deprecated rules again - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5934](https://github.com/pmd/pmd/pull/5934): \[java] Fix #2186: New Rule: RelianceOnDefaultCharset - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5938](https://github.com/pmd/pmd/pull/5938): \[doc] Update suppression docs to reflect PMD 7 changes - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5939](https://github.com/pmd/pmd/pull/5939): \[java] Fix #5198: CheckResultSet FP when local variable is checked - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5954](https://github.com/pmd/pmd/pull/5954): \[core] Fix #4721: Enable XML rule MissingEncoding in dogfood ruleset - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5955](https://github.com/pmd/pmd/pull/5955): chore: Fix LiteralsFirstInComparison violations in test code - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5957](https://github.com/pmd/pmd/pull/5957): \[java] Fix #3401: Improve message/description/examples for AvoidUsingOctalValues - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5958](https://github.com/pmd/pmd/pull/5958): \[core] CSVRenderer: Add begin and end for line and columns (default off) - [Jude Pereira](https://github.com/judepereira) (@judepereira)
* [#5959](https://github.com/pmd/pmd/pull/5959): \[java] Fix #5960: AddEmptyString: Improve report location - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5961](https://github.com/pmd/pmd/pull/5961): \[java] Fix #5960: Add details to the error message for some rules - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5965](https://github.com/pmd/pmd/pull/5965): \[java] Fix #5881: AvoidLosingException - Consider nested method calls - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5967](https://github.com/pmd/pmd/pull/5967): \[doc]\[java] ReplaceJavaUtilDate - improve doc to mention java.sql.Date - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5968](https://github.com/pmd/pmd/pull/5968): \[doc] Add logging page to sidebar under dev docs - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5969](https://github.com/pmd/pmd/pull/5969): \[doc] Add CSS in PMD's description - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5970](https://github.com/pmd/pmd/pull/5970): chore: CI improvements - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5971](https://github.com/pmd/pmd/pull/5971): \[java] Fix #5948: UnnecessaryBoxingRule: Check if unboxing is required for overload resolution - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5972](https://github.com/pmd/pmd/pull/5972): \[java] Fix #3434: False negatives in AssignmentInOperand rule - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5975](https://github.com/pmd/pmd/pull/5975): \[test] Fix #5973: Enable XML Validation for rule tests - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5979](https://github.com/pmd/pmd/pull/5979): \[java] Fix #5974: NPE in CloseResourceRule - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5980](https://github.com/pmd/pmd/pull/5980): chore: Fix typos - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5981](https://github.com/pmd/pmd/pull/5981): \[java] Fix #4911: AvoidRethrowingException consider supertypes in following catches - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5982](https://github.com/pmd/pmd/pull/5982): \[java] More detailed message for the UselessParentheses rule - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5989](https://github.com/pmd/pmd/pull/5989): \[java] Improve performance of RelianceOnDefaultCharset - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5994](https://github.com/pmd/pmd/pull/5994): \[java] Fix #4770: UnusedFormalParameter should ignore public constructor as same as method - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5995](https://github.com/pmd/pmd/pull/5995): \[html] Add test case that tests the end of a reported violation (test for #3951) - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5996](https://github.com/pmd/pmd/pull/5996): \[java] Fix #4916: UseExplicitTypes cases where 'var' should be unobjectionable - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6006](https://github.com/pmd/pmd/pull/6006): \[java] Fix #5949: New Rule: CollectionTypeMismatch - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6008](https://github.com/pmd/pmd/pull/6008): \[core] Fix #5849: Support Markdown Output for CPD Reports - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6009](https://github.com/pmd/pmd/pull/6009): \[java] More detailed message for AvoidInstanceofChecksInCatchClause - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6016](https://github.com/pmd/pmd/pull/6016): \[java] Fix #6014: Crash when encountering a java comment at the end of a file - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5936](https://github.com/pmd/pmd/pull/5936): Bump PMD from 7.15.0 to 7.16.0
* [#5937](https://github.com/pmd/pmd/pull/5937): Bump pmdtester from 1.5.5 to 1.6.0
* [#5941](https://github.com/pmd/pmd/pull/5941): chore(deps): bump org.apache.commons:commons-text from 1.13.1 to 1.14.0
* [#5944](https://github.com/pmd/pmd/pull/5944): chore(deps): bump io.github.apex-dev-tools:apex-ls_2.13 from 5.9.0 to 5.10.0
* [#5945](https://github.com/pmd/pmd/pull/5945): chore(deps): bump org.junit:junit-bom from 5.13.3 to 5.13.4
* [#5946](https://github.com/pmd/pmd/pull/5946): chore(deps): bump org.apache.groovy:groovy from 4.0.27 to 4.0.28
* [#5962](https://github.com/pmd/pmd/pull/5962): chore(deps): bump scalameta.version from 4.13.8 to 4.13.9
* [#5963](https://github.com/pmd/pmd/pull/5963): chore(deps-dev): bump org.apache.commons:commons-compress from 1.27.1 to 1.28.0
* [#5976](https://github.com/pmd/pmd/pull/5976): chore(deps-dev): bump all-contributors-cli from 6.20.0 to 6.26.1
* [#5978](https://github.com/pmd/pmd/pull/5978): chore(deps-dev): bump org.assertj:assertj-core from 3.27.3 to 3.27.4
* [#5984](https://github.com/pmd/pmd/pull/5984): chore(deps): bump actions/checkout from 4.2.2 to 5.0.0
* [#5985](https://github.com/pmd/pmd/pull/5985): chore(deps): bump ruby/setup-ruby from 1.254.0 to 1.255.0
* [#5986](https://github.com/pmd/pmd/pull/5986): chore(deps): bump actions/create-github-app-token from 2.0.6 to 2.1.1
* [#5988](https://github.com/pmd/pmd/pull/5988): chore(deps): Bump build-tools from 33 to 34
* [#5990](https://github.com/pmd/pmd/pull/5990): chore(deps): Update @<!-- -->babel/runtime from 7.16.7 to 7.28.7
* [#5991](https://github.com/pmd/pmd/pull/5991): chore(deps): Update tmp from 0.0.33 to 0.2.5
* [#5997](https://github.com/pmd/pmd/pull/5997): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.17.6 to 1.17.7
* [#5998](https://github.com/pmd/pmd/pull/5998): chore(deps): bump kotlin.version from 2.2.0 to 2.2.10
* [#5999](https://github.com/pmd/pmd/pull/5999): chore(deps): bump org.mockito:mockito-core from 5.18.0 to 5.19.0
* [#6000](https://github.com/pmd/pmd/pull/6000): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.17.6 to 1.17.7
* [#6001](https://github.com/pmd/pmd/pull/6001): chore(deps): bump com.google.protobuf:protobuf-java from 4.31.1 to 4.32.0
* [#6002](https://github.com/pmd/pmd/pull/6002): chore(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.11.2 to 3.11.3
* [#6013](https://github.com/pmd/pmd/pull/6013): chore(deps): bump actions/setup-java from 4.7.1 to 5.0.0
* [#6033](https://github.com/pmd/pmd/pull/6033): chore(deps): bump ruby/setup-ruby from 1.255.0 to 1.257.0
* [#6044](https://github.com/pmd/pmd/pull/6044): chore(deps): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.1.0.4751 to 5.2.0.4988
* [#6045](https://github.com/pmd/pmd/pull/6045): chore(deps): bump org.jetbrains:annotations from 26.0.2 to 26.0.2-1
* [#6046](https://github.com/pmd/pmd/pull/6046): chore(deps): bump org.apache.groovy:groovy from 4.0.28 to 5.0.0
* [#6047](https://github.com/pmd/pmd/pull/6047): chore(deps): bump org.yaml:snakeyaml from 2.4 to 2.5
* [#6048](https://github.com/pmd/pmd/pull/6048): chore(deps): bump org.codehaus.mojo:versions-maven-plugin from 2.18.0 to 2.19.0
* [#6049](https://github.com/pmd/pmd/pull/6049): chore(deps): bump org.jsoup:jsoup from 1.21.1 to 1.21.2
* [#6050](https://github.com/pmd/pmd/pull/6050): chore(deps): bump bigdecimal from 3.2.2 to 3.2.3 in /docs

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 188 commits
* 65 closed tickets & PRs
* Days since last release: 49



## 25-July-2025 - 7.16.0

The PMD team is pleased to announce PMD 7.16.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [🚀 New: Java 25 Support](#new-java-25-support)
    * [New: CPD support for CSS](#new-cpd-support-for-css)
    * [✨ New Rules](#new-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Experimental APIs that are now considered stable](#experimental-apis-that-are-now-considered-stable)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### 🚀 New: Java 25 Support
This release of PMD brings support for Java 25.

There are the following new standard language features:
* [JEP 511: Module Import Declarations](https://openjdk.org/jeps/511)
* [JEP 512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512)
* [JEP 513: Flexible Constructor Bodies](https://openjdk.org/jeps/513)

And one preview language feature:
* [JEP 507: Primitive Types in Patterns, instanceof, and switch (Third Preview)](https://openjdk.org/jeps/507)

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `25-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-25-preview ...

Note: Support for Java 23 preview language features have been removed. The version "23-preview"
is no longer available.

#### New: CPD support for CSS
CPD now supports CSS (Cascading Style Sheets), a language for describing the rendering of structured
documents (such as HTML) on screen, on paper etc.  
It is shipped with the new module `pmd-css`.

#### ✨ New Rules
* Two new rules have been added to Java's Error Prone category: [`ReplaceJavaUtilCalendar`](https://docs.pmd-code.org/pmd-doc-7.16.0/pmd_rules_java_errorprone.html#replacejavautilcalendar)
  and [`ReplaceJavaUtilDate`](https://docs.pmd-code.org/pmd-doc-7.16.0/pmd_rules_java_errorprone.html#replacejavautildate). These rules help to migrate away from old Java APIs around
  `java.util.Calendar` and `java.util.Date`. It is recommended to use the modern `java.time` API instead, which
  is available since Java 8.

### 🐛 Fixed Issues
* core
  * [#4328](https://github.com/pmd/pmd/issues/4328): \[ci] Improve Github Actions Workflows
  * [#5597](https://github.com/pmd/pmd/issues/5597): \[core] POM Incompatibility with Maven 4
* java
  * [#5344](https://github.com/pmd/pmd/issues/5344): \[java] IllegalArgumentException: Invalid type reference for method or ctor type annotation: 16
  * [#5478](https://github.com/pmd/pmd/issues/5478): \[java] Support Java 25
* java-codestyle
  * [#5892](https://github.com/pmd/pmd/issues/5892): \[java] ShortVariable false positive for java 22 unnamed variable `_`
* java-design
  * [#5858](https://github.com/pmd/pmd/issues/5858): \[java] FinalFieldCouldBeStatic false positive for array initializers
* java-errorprone
  * [#2862](https://github.com/pmd/pmd/issues/2862): \[java] New Rules: Avoid java.util.Date and Calendar classes

### 🚨 API Changes

#### Experimental APIs that are now considered stable
* pmd-java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.16.0/net/sourceforge/pmd/lang/java/ast/ASTImportDeclaration.html#isModuleImport()"><code>ASTImportDeclaration#isModuleImport</code></a> is now stable API.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.16.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#isCompact()"><code>ASTCompilationUnit#isCompact</code></a> is now stable API. Note, it was previously
    called `isSimpleCompilationUnit`.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.16.0/net/sourceforge/pmd/lang/java/ast/ASTImplicitClassDeclaration.html#"><code>ASTImplicitClassDeclaration</code></a> is now stable API.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.16.0/net/sourceforge/pmd/lang/java/ast/JavaVisitorBase.html#visit(net.sourceforge.pmd.lang.java.ast.ASTImplicitClassDeclaration,P)"><code>JavaVisitorBase#visit(ASTImplicitClassDeclaration, P)</code></a> is now
    stable API.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5733](https://github.com/pmd/pmd/pull/5733): \[css] Add new CPD language - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#5859](https://github.com/pmd/pmd/pull/5859): Fix #5858: \[java] Fix false positive in FinalFieldCouldBeStatic for array initializers - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5872](https://github.com/pmd/pmd/pull/5872): \[java] Add Support for Java 25 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5876](https://github.com/pmd/pmd/pull/5876): chore: license header cleanup - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5883](https://github.com/pmd/pmd/pull/5883): Fix #2862: \[java] Add rules discouraging the use of java.util.Calendar and java.util.Date - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5893](https://github.com/pmd/pmd/pull/5893): chore: Fix Mockito javaagent warning for Java 21+ - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5894](https://github.com/pmd/pmd/pull/5894): chore: Fix JUnit warning about invalid test factory - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5895](https://github.com/pmd/pmd/pull/5895): Fix #5597: Move dogfood profile to separate settings.xml - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5899](https://github.com/pmd/pmd/pull/5899): Fix #5344: \[java] Just log invalid annotation target type - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5909](https://github.com/pmd/pmd/pull/5909): \[ci] Create a pre-release for snapshot builds - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5911](https://github.com/pmd/pmd/pull/5911): \[doc] Reference CPD Capable Languages in CPD CLI docu - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5914](https://github.com/pmd/pmd/pull/5914): Fix #5892: \[java] ShortVariable FP for java 22 Unnamed Variable - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5918](https://github.com/pmd/pmd/pull/5918): chore: \[cli] Improve symbolic link tests for Windows - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5920](https://github.com/pmd/pmd/pull/5920): chore: \[scala] Fix javadoc config - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5857](https://github.com/pmd/pmd/pull/5857): Bump PMD from 7.14.0 to 7.15.0
* [#5861](https://github.com/pmd/pmd/pull/5861): Bump scalameta.version from 4.13.7 to 4.13.8
* [#5862](https://github.com/pmd/pmd/pull/5862): Bump com.puppycrawl.tools:checkstyle from 10.25.1 to 10.26.1
* [#5863](https://github.com/pmd/pmd/pull/5863): Bump org.apache.maven.plugins:maven-pmd-plugin from 3.26.0 to 3.27.0
* [#5864](https://github.com/pmd/pmd/pull/5864): Bump kotlin.version from 1.9.24 to 2.2.0
* [#5865](https://github.com/pmd/pmd/pull/5865): Bump org.junit:junit-bom from 5.13.1 to 5.13.2
* [#5866](https://github.com/pmd/pmd/pull/5866): Bump org.jsoup:jsoup from 1.20.1 to 1.21.1
* [#5884](https://github.com/pmd/pmd/pull/5884): Bump org.junit:junit-bom from 5.13.2 to 5.13.3
* [#5885](https://github.com/pmd/pmd/pull/5885): Bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.7 to 3.2.8
* [#5886](https://github.com/pmd/pmd/pull/5886): Bump org.checkerframework:checker-qual from 3.49.4 to 3.49.5
* [#5889](https://github.com/pmd/pmd/pull/5889): Bump org.apache.maven.plugins:maven-enforcer-plugin from 3.5.0 to 3.6.0
* [#5900](https://github.com/pmd/pmd/pull/5900): Bump org.apache.commons:commons-lang3 from 3.17.0 to 3.18.0
* [#5901](https://github.com/pmd/pmd/pull/5901): Bump io.github.apex-dev-tools:apex-parser from 4.4.0 to 4.4.1
* [#5902](https://github.com/pmd/pmd/pull/5902): Bump log4j.version from 2.25.0 to 2.25.1
* [#5910](https://github.com/pmd/pmd/pull/5910): Bump maven from 3.9.10 to 3.9.11
* [#5921](https://github.com/pmd/pmd/pull/5921): Bump build-tools from 32 to 33
* [#5926](https://github.com/pmd/pmd/pull/5926): chore(deps): bump org.apache.maven.plugins:maven-enforcer-plugin from 3.6.0 to 3.6.1
* [#5927](https://github.com/pmd/pmd/pull/5927): chore(deps): bump ostruct from 0.6.2 to 0.6.3 in /.ci/files in the all-gems group across 1 directory
* [#5928](https://github.com/pmd/pmd/pull/5928): chore(deps): bump marocchino/sticky-pull-request-comment from 2.9.3 to 2.9.4 in the all-actions group
* [#5929](https://github.com/pmd/pmd/pull/5929): chore(deps): Update gems

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 100 commits
* 21 closed tickets & PRs
* Days since last release: 27



## 27-June-2025 - 7.15.0

The PMD team is pleased to announce PMD 7.15.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [✨ New Rules](#new-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Rule Test Schema](#rule-test-schema)
* [💵 Financial Contributions](#financial-contributions)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### ✨ New Rules

* The new Apex rule [`AvoidBooleanMethodParameters`](https://docs.pmd-code.org/pmd-doc-7.15.0/pmd_rules_apex_design.html#avoidbooleanmethodparameters) finds methods that take a
  boolean parameter. This can make method calls difficult to understand and maintain as the method is clearly
  doing two things.

### 🐛 Fixed Issues
* apex-design
  * [#5427](https://github.com/pmd/pmd/issues/5427): \[apex] New Rule: Avoid Boolean Method Parameters
* apex-security
  * [#5788](https://github.com/pmd/pmd/issues/5788): \[apex] ApexCRUDViolation unable to detect insecure SOQL if it is a direct input argument
* doc
  * [#5790](https://github.com/pmd/pmd/issues/5790): \[doc] Website rule reference pages are returning 404
* java-bestpractices
  * [#5785](https://github.com/pmd/pmd/issues/5785): \[java] UnusedPrivateField doesn't play well with UnnecessaryWarningSuppression
  * [#5793](https://github.com/pmd/pmd/issues/5793): \[java] NonExhaustiveSwitch fails on exhaustive switch with sealed class
* java-codestyle
  * [#1639](https://github.com/pmd/pmd/issues/1639): \[java] UnnecessaryImport false positive for multiline @<!-- -->link Javadoc
  * [#2304](https://github.com/pmd/pmd/issues/2304): \[java] UnnecessaryImport false positive for on-demand imports in JavaDoc
  * [#5832](https://github.com/pmd/pmd/issues/5832): \[java] UnnecessaryImport false positive for multiline @<!-- -->see Javadoc
* java-design
  * [#5804](https://github.com/pmd/pmd/issues/5804): \[java] UselessOverridingMethod doesn't play well with UnnecessarySuppressWarning

### 🚨 API Changes

#### Rule Test Schema
The rule test schema has been extended to support verifying suppressed violations.
See [Testing your rules](https://docs.pmd-code.org/pmd-doc-7.15.0/pmd_userdocs_extending_testing.html) for more information.

Also note, the schema [rule-tests.xsd](https://github.com/pmd/pmd/blob/main/pmd-test-schema/src/main/resources/net/sourceforge/pmd/test/schema/rule-tests_1_1_0.xsd)
is now only in the module "pmd-test-schema". It has been removed from the old location from module "pmd-test".

### 💵 Financial Contributions

Many thanks to our sponsors:

* [Cybozu](https://github.com/cybozu) (@cybozu)

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5738](https://github.com/pmd/pmd/pull/5738): chore: Remove unused private methods in test classes - [Pankraz76](https://github.com/Pankraz76) (@Pankraz76)
* [#5745](https://github.com/pmd/pmd/pull/5745): \[ci] New "Publish Release" workflow - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5791](https://github.com/pmd/pmd/pull/5791): \[doc] Add a simple check whether generate rule doc pages exist - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5797](https://github.com/pmd/pmd/pull/5797): \[doc] Update sponsors - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5800](https://github.com/pmd/pmd/pull/5800): Fix #5793: \[java] NonExhaustiveSwitch should ignore "case null" - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5803](https://github.com/pmd/pmd/pull/5803): chore: Remove unnecessary suppress warnings - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5805](https://github.com/pmd/pmd/pull/5805): Fix #5804: \[java] UselessOverridingMethod needs to ignore SuppressWarnings - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5806](https://github.com/pmd/pmd/pull/5806): \[test] Verify suppressed violations in rule tests - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5814](https://github.com/pmd/pmd/pull/5814): Fix #5788: \[apex] ApexCRUDViolation - consider deeper nested Soql - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5815](https://github.com/pmd/pmd/pull/5815): Fix #5785: \[java] UnusedPrivateField should ignore SuppressWarnings - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5818](https://github.com/pmd/pmd/pull/5818): Fix #2304: \[java] UnnecessaryImport FP for on-demand imports in JavaDoc - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5821](https://github.com/pmd/pmd/pull/5821): \[apex] New Rule: Avoid boolean method parameters - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5823](https://github.com/pmd/pmd/pull/5823): \[doc] Fix javadoc plugin configuration - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5833](https://github.com/pmd/pmd/pull/5833): Fix #1639 #5832: Use filtered comment text for UnnecessaryImport - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5851](https://github.com/pmd/pmd/pull/5851): chore: \[java] ReplaceHashtableWithMap: Fix name of test - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5775](https://github.com/pmd/pmd/pull/5775): Bump PMD from 7.13.0 to 7.14.0
* [#5778](https://github.com/pmd/pmd/pull/5778): Bump the all-gems group across 2 directories with 3 updates
* [#5779](https://github.com/pmd/pmd/pull/5779): Bump org.codehaus.mojo:exec-maven-plugin from 3.5.0 to 3.5.1
* [#5780](https://github.com/pmd/pmd/pull/5780): Bump org.apache.maven.plugins:maven-clean-plugin from 3.4.1 to 3.5.0
* [#5781](https://github.com/pmd/pmd/pull/5781): Bump com.google.protobuf:protobuf-java from 4.31.0 to 4.31.1
* [#5782](https://github.com/pmd/pmd/pull/5782): Bump org.apache.groovy:groovy from 4.0.26 to 4.0.27
* [#5783](https://github.com/pmd/pmd/pull/5783): Bump com.puppycrawl.tools:checkstyle from 10.24.0 to 10.25.0
* [#5784](https://github.com/pmd/pmd/pull/5784): Bump org.junit:junit-bom from 5.12.2 to 5.13.0
* [#5807](https://github.com/pmd/pmd/pull/5807): Bump maven from 3.9.8 to 3.9.10
* [#5809](https://github.com/pmd/pmd/pull/5809): Bump org.codehaus.mojo:build-helper-maven-plugin from 3.6.0 to 3.6.1
* [#5810](https://github.com/pmd/pmd/pull/5810): Bump org.junit:junit-bom from 5.13.0 to 5.13.1
* [#5811](https://github.com/pmd/pmd/pull/5811): Bump junit5.platform.version from 1.13.0 to 1.13.1
* [#5812](https://github.com/pmd/pmd/pull/5812): Bump org.checkerframework:checker-qual from 3.49.3 to 3.49.4
* [#5813](https://github.com/pmd/pmd/pull/5813): Bump the all-gems group across 2 directories with 1 update
* [#5828](https://github.com/pmd/pmd/pull/5828): Bump scalameta.version from 4.13.6 to 4.13.7
* [#5829](https://github.com/pmd/pmd/pull/5829): Bump liquid from 5.8.6 to 5.8.7 in /.ci/files in the all-gems group across 1 directory
* [#5838](https://github.com/pmd/pmd/pull/5838): Bump marocchino/sticky-pull-request-comment from 2.9.2 to 2.9.3 in the all-actions group
* [#5839](https://github.com/pmd/pmd/pull/5839): Bump log4j.version from 2.24.3 to 2.25.0
* [#5840](https://github.com/pmd/pmd/pull/5840): Bump com.puppycrawl.tools:checkstyle from 10.25.0 to 10.25.1
* [#5841](https://github.com/pmd/pmd/pull/5841): Bump net.bytebuddy:byte-buddy-agent from 1.17.5 to 1.17.6
* [#5842](https://github.com/pmd/pmd/pull/5842): Bump net.bytebuddy:byte-buddy from 1.17.5 to 1.17.6
* [#5843](https://github.com/pmd/pmd/pull/5843): Bump org.sonatype.central:central-publishing-maven-plugin from 0.7.0 to 0.8.0
* [#5844](https://github.com/pmd/pmd/pull/5844): Bump ostruct from 0.6.1 to 0.6.2 in /.ci/files in the all-gems group across 1 directory
* [#5853](https://github.com/pmd/pmd/pull/5853): Bump build-tools from 30 to 32

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 91 commits
* 24 closed tickets & PRs
* Days since last release: 27



## 30-May-2025 - 7.14.0

The PMD team is pleased to announce PMD 7.14.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [PMD CLI now uses threaded execution by default](#pmd-cli-now-uses-threaded-execution-by-default)
    * [New Rule UnnecessaryWarningSuppression (experimental)](#new-rule-unnecessarywarningsuppression-experimental)
    * [Migrating to Central Publisher Portal](#migrating-to-central-publisher-portal)
    * [More CLI parameters shared between PMD and CPD](#more-cli-parameters-shared-between-pmd-and-cpd)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [CLI](#cli)
    * [Deprecations](#deprecations)
    * [Experimental](#experimental)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### PMD CLI now uses threaded execution by default

In the PMD CLI, the `--threads` (`-t`) option can now accept a thread
count given relative to the number of cores of the machine. For instance,
it is now possible to write `-t 1C` to spawn one thread per core, or `-t 0.5C`
to spawn one thread for every other core.

The thread count option now defaults to `1C`, meaning parallel execution
is used by default. You can disable this by using `-t 1`.

#### New Rule UnnecessaryWarningSuppression (experimental)

This new Java rule [`UnnecessaryWarningSuppression`](https://docs.pmd-code.org/pmd-doc-7.14.0/pmd_rules_java_bestpractices.html#unnecessarywarningsuppression) reports unused suppression
annotations and comments. Violations of this rule cannot be suppressed.

How to use it? Just include it in your ruleset:

```xml
<rule ref="category/java/bestpractices.xml/UnnecessaryWarningSuppression" />
```

Note: This rule is currently experimental. It is available for now only for Java.
The rule for now only reports annotations specific to PMD, like `@SuppressWarnings("PMD")`.
In the future we might be able to check for other common ones like `@SuppressWarnings("unchecked")` or `"fallthrough"`.
Since violations of this rule cannot be suppressed, we opted here on the side of false-negatives and
don't report every unused case yet.
However, suppressing specific PMD rules is working as expected.

#### Migrating to Central Publisher Portal

We've now migrated to [Central Publisher Portal](https://central.sonatype.org/publish/publish-portal-guide/).
Snapshots of PMD are still available, however the repository URL changed. To consume these with maven, you can
use the following snippet:

```xml
<repositories>
  <repository>
    <name>Central Portal Snapshots</name>
    <id>central-portal-snapshots</id>
    <url>https://central.sonatype.com/repository/maven-snapshots/</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

Releases of PMD are available on [Maven Central](https://central.sonatype.com/) as before without change.

#### More CLI parameters shared between PMD and CPD

When executing PMD or CPD, the same parameters are now understood for selecting which files should
be analyzed. See [File collection options](https://docs.pmd-code.org/pmd-doc-7.14.0/pmd_userdocs_cli_reference.html#file-collection-options)
for a list of common, shared parameters that are valid for both commands.

### 🐛 Fixed Issues
* core
  * [#648](https://github.com/pmd/pmd/issues/648): \[core] Warn on unneeded suppression
  * [#5700](https://github.com/pmd/pmd/pull/5700): \[core] Don't accidentally catch unexpected runtime exceptions in CpdAnalysis
  * [#5705](https://github.com/pmd/pmd/issues/5705): \[cli] PMD's start script fails if PMD_HOME is set
* java-bestpractices
  * [#5061](https://github.com/pmd/pmd/issues/5061): \[java] UnusedLocalVariable false positive when variable is read as side effect of an assignment
  * [#5621](https://github.com/pmd/pmd/issues/5621): \[java] UnusedPrivateMethod with method ref
  * [#5724](https://github.com/pmd/pmd/issues/5724): \[java] ImplicitFunctionalInterface should not be reported on sealed interfaces
* java-codestyle
  * [#2462](https://github.com/pmd/pmd/issues/2462): \[java] LinguisticNaming must ignore setters that returns current type (Builder pattern)
  * [#5634](https://github.com/pmd/pmd/issues/5634): \[java] CommentDefaultAccessModifier doesn't recognize /* package */ comment at expected location for constructors
* java-design
  * [#5568](https://github.com/pmd/pmd/issues/5568): \[java] High NPathComplexity in `switch` expression
  * [#5647](https://github.com/pmd/pmd/issues/5647): \[java] NPathComplexity does not account for `return`s
* java-errorprone
  * [#5702](https://github.com/pmd/pmd/issues/5702): \[java] InvalidLogMessageFormat: Lombok @<!-- -->Slf4j annotation is not interpreted by PMD
* java-performance
  * [#5711](https://github.com/pmd/pmd/issues/5711): \[java] UseArraysAsList false positive with Sets
* visualforce
  * [#5476](https://github.com/pmd/pmd/issues/5476): \[visualforce] NPE when analyzing standard field references in visualforce page

### 🚨 API Changes
#### CLI
* CPD now supports `--report-file` (-r) and `--exclude-file-list`.
* PMD now supports `--exclude` and `--non-recursive`.
* The option `--ignore-list` in PMD is renamed to `--exclude-file-list`.

#### Deprecations
* CLI
  * The option `--ignore-list` has been deprecated. Use `--exclude-file-list` instead.
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/AstInfo.html#getSuppressionComments()"><code>AstInfo#getSuppressionComments</code></a>: Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/AstInfo.html#getAllSuppressionComments()"><code>getAllSuppressionComments</code></a>
    or <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/AstInfo.html#getSuppressionComment(int)"><code>getSuppressionComment</code></a>.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/AstInfo.html#withSuppressMap()"><code>AstInfo#withSuppressMap</code></a>: Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/AstInfo.html#withSuppressionComments(java.util.Collection)"><code>withSuppressionComments</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/impl/javacc/AbstractTokenManager.html#suppressMap"><code>AbstractTokenManager#suppressMap</code></a>: Don't use this map directly anymore. Instead,
    use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/impl/javacc/AbstractTokenManager.html#getSuppressionComments()"><code>getSuppressionComments</code></a>.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/impl/javacc/AbstractTokenManager.html#getSuppressMap()"><code>AbstractTokenManager#getSuppressMap</code></a>: Use
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/impl/javacc/AbstractTokenManager.html#getSuppressionComments()"><code>getSuppressionComments</code></a> instead.
* pmd-java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.14.0/net/sourceforge/pmd/lang/java/ast/ASTCompactConstructorDeclaration.html#getDeclarationNode()"><code>ASTCompactConstructorDeclaration#getDeclarationNode</code></a>: This method just returns `this` and isn't useful.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.14.0/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.html#NPATH"><code>JavaMetrics#NPATH</code></a>: Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.14.0/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.html#NPATH_COMP"><code>NPATH_COMP</code></a>, which is available on more nodes,
    and uses Long instead of BigInteger.

#### Experimental
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/ast/impl/SuppressionCommentImpl.html#"><code>SuppressionCommentImpl</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/lang/rule/impl/UnnecessaryPmdSuppressionRule.html#"><code>UnnecessaryPmdSuppressionRule</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/reporting/RuleContext.html#addViolationNoSuppress(net.sourceforge.pmd.reporting.Reportable,net.sourceforge.pmd.lang.ast.AstInfo,java.lang.String,java.lang.Object...)"><code>RuleContext#addViolationNoSuppress</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0/net/sourceforge/pmd/reporting/ViolationSuppressor.SuppressionCommentWrapper.html#"><code>ViolationSuppressor.SuppressionCommentWrapper</code></a>
* pmd-java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.14.0/net/sourceforge/pmd/lang/java/types/OverloadSelectionResult.html#getTypeToSearch()"><code>OverloadSelectionResult#getTypeToSearch</code></a>

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5584](https://github.com/pmd/pmd/pull/5584): \[ci] New workflow "Publish Snapshot" - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5599](https://github.com/pmd/pmd/pull/5599): \[java] Rewrite NPath complexity metric - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5609](https://github.com/pmd/pmd/pull/5609): \[core] Add rule to report unnecessary suppression comments/annotations - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5699](https://github.com/pmd/pmd/pull/5699): Fix #5702: \[java] First-class support for lombok @<!-- -->Slf4j  - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5700](https://github.com/pmd/pmd/pull/5700): \[core] Don't accidentally catch unexpected runtime exceptions in CpdAnalysis - [Elliotte Rusty Harold](https://github.com/elharo) (@elharo)
* [#5712](https://github.com/pmd/pmd/pull/5712): Fix #5711: \[java] UseArrayAsList - only consider List.add - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5715](https://github.com/pmd/pmd/pull/5715): Fix #5476: \[visualforce] Resolve data types of standard object fields - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5716](https://github.com/pmd/pmd/pull/5716): Fix #5634: \[java] CommentDefaultAccessModifier: Comment between annotation and constructor not recognized - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5726](https://github.com/pmd/pmd/pull/5726): Fix #5724: \[java] Implicit functional interface FP with sealed interface - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5727](https://github.com/pmd/pmd/pull/5727): Fix #5621: \[java] Fix FPs with UnusedPrivateMethod - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5728](https://github.com/pmd/pmd/pull/5728): \[ci] Improvements for "Publish Pull Requests" - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5730](https://github.com/pmd/pmd/pull/5730): \[ci] Refactor git-repo-sync - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5731](https://github.com/pmd/pmd/pull/5731): \[cli] Share more CLI options between CPD and PMD - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5736](https://github.com/pmd/pmd/pull/5736): Fix #5061: \[java] UnusedLocalVariable FP when using compound assignment - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5741](https://github.com/pmd/pmd/pull/5741): \[cli] Make CLI default to multithreaded - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5742](https://github.com/pmd/pmd/pull/5742): \[ci] publish-snapshot/old build: migrate to central portal - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5743](https://github.com/pmd/pmd/pull/5743): \[ci] Make build a reusable workflow - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5744](https://github.com/pmd/pmd/pull/5744): Fix #5705: \[cli] Always determine PMD_HOME based on script location - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5748](https://github.com/pmd/pmd/pull/5748): \[core] Reformat SarifLog to comply to coding standards - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5763](https://github.com/pmd/pmd/pull/5763): \[java] Support annotated constructor return type in symbol API - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5764](https://github.com/pmd/pmd/pull/5764): Fix #2462: \[java] LinguisticNaming should ignore setters for Builders  - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5706](https://github.com/pmd/pmd/pull/5706): Bump PMD from 7.12.0 to 7.13.0
* [#5709](https://github.com/pmd/pmd/pull/5709): Bump com.google.code.gson:gson from 2.13.0 to 2.13.1
* [#5710](https://github.com/pmd/pmd/pull/5710): Bump com.puppycrawl.tools:checkstyle from 10.23.0 to 10.23.1
* [#5717](https://github.com/pmd/pmd/pull/5717): Bump scalameta.version from 4.13.4 to 4.13.5
* [#5718](https://github.com/pmd/pmd/pull/5718): Bump org.checkerframework:checker-qual from 3.49.2 to 3.49.3
* [#5719](https://github.com/pmd/pmd/pull/5719): Bump org.jsoup:jsoup from 1.19.1 to 1.20.1
* [#5751](https://github.com/pmd/pmd/pull/5751): Bump scalameta.version from 4.13.5 to 4.13.6
* [#5754](https://github.com/pmd/pmd/pull/5754): Bump com.google.protobuf:protobuf-java from 4.30.2 to 4.31.0
* [#5766](https://github.com/pmd/pmd/pull/5766): Bump io.github.git-commit-id:git-commit-id-maven-plugin from 9.0.1 to 9.0.2
* [#5767](https://github.com/pmd/pmd/pull/5767): Bump org.mockito:mockito-core from 5.17.0 to 5.18.0
* [#5768](https://github.com/pmd/pmd/pull/5768): Bump com.puppycrawl.tools:checkstyle from 10.23.1 to 10.24.0

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 165 commits
* 33 closed tickets & PRs
* Days since last release: 35



## 25-April-2025 - 7.13.0

The PMD team is pleased to announce PMD 7.13.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [Docker images](#docker-images)
    * [Experimental support for language dialects](#experimental-support-for-language-dialects)
    * [✨ New Rules](#new-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecations](#deprecations)
    * [Experimental API](#experimental-api)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### Docker images

PMD is now providing official docker images at <https://hub.docker.com/r/pmdcode/pmd> and
<https://github.com/pmd/docker/pkgs/container/pmd>.

You can now analyze your code with PMD by using docker like so:

```
docker run --rm --tty -v $PWD:/src pmdcode/pmd:latest check -d . -R rulesets/java/quickstart.xml`
```

More information is available at <https://github.com/pmd/docker>.

#### Experimental support for language dialects

A dialect is a particular form of another supported language. For example, an XSLT is a particular
form of an XML. Even though the dialect has its own semantics and uses, the contents are still readable
by any tool capable of understanding the base language.

In PMD, a dialect allows to set up completely custom rules, XPath functions, properties and metrics
for these files; while retaining the full support of the underlying base language including
already existing rules and XPath functions.

See [[core] Support language dialects #5438](https://github.com/pmd/pmd/pull/5438) and
[Adding a new dialect](https://docs.pmd-code.org/pmd-doc-7.13.0/pmd_devdocs_major_adding_dialect.html) for more information.

#### ✨ New Rules

* The new Apex rule [`TypeShadowsBuiltInNamespace`](https://docs.pmd-code.org/pmd-doc-7.13.0/pmd_rules_apex_errorprone.html#typeshadowsbuiltinnamespace) finds Apex classes, enums, and interfaces
  that have the same name as a class, enum, or interface in the `System` or `Schema` namespace.
  Shadowing these namespaces in this way can lead to confusion and unexpected behavior.

### 🐛 Fixed Issues
* core
  * [#5438](https://github.com/pmd/pmd/issues/5438): \[core] Support language dialects
  * [#5448](https://github.com/pmd/pmd/issues/5448): Maintain a public PMD docker image
  * [#5525](https://github.com/pmd/pmd/issues/5525): \[core] Add rule priority as level to Sarif report
  * [#5623](https://github.com/pmd/pmd/issues/5623): \[dist] Make pmd launch script compatible with /bin/sh
* apex-bestpractices
  * [#5667](https://github.com/pmd/pmd/issues/5667): \[apex] ApexUnitTestShouldNotUseSeeAllDataTrue false negative when seeAllData parameter is a string
* apex-errorprone
  * [#3184](https://github.com/pmd/pmd/issues/3184): \[apex] Prevent classes from shadowing System Namespace
* java
  * [#5645](https://github.com/pmd/pmd/issues/5645): \[java] Parse error on switch with yield
* java-bestpractices
  * [#5687](https://github.com/pmd/pmd/issues/5687): \[java] UnusedPrivateMethodRule: exclude serialization method readObjectNoData()
* plsql
  * [#5675](https://github.com/pmd/pmd/issues/5675): \[plsql] Parse error with TREAT function

### 🚨 API Changes

#### Deprecations
* <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.13.0/net/sourceforge/pmd/lang/xml/pom/PomLanguageModule.html#"><code>net.sourceforge.pmd.lang.xml.pom.PomLanguageModule</code></a> is deprecated. POM is now a dialect of XML.
  Use <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.13.0/net/sourceforge/pmd/lang/xml/pom/PomDialectModule.html#"><code>PomDialectModule</code></a> instead.
* <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.13.0/net/sourceforge/pmd/lang/xml/wsdl/WsdlLanguageModule.html#"><code>net.sourceforge.pmd.lang.xml.wsdl.WsdlLanguageModule</code></a> is deprecated. WSDL is now a dialect of XML.
  Use <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.13.0/net/sourceforge/pmd/lang/xml/wsdl/WsdlDialectModule.html#"><code>WsdlDialectModule</code></a> instead.
* <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.13.0/net/sourceforge/pmd/lang/xml/xsl/XslLanguageModule.html#"><code>net.sourceforge.pmd.lang.xml.xsl.XslLanguageModule</code></a> is deprecated. XSL is now a dialect of XML.
  Use <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.13.0/net/sourceforge/pmd/lang/xml/xsl/XslDialectModule.html#"><code>XslDialectModule</code></a> instead.

#### Experimental API
* The core API around support for language dialects:
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.13.0/net/sourceforge/pmd/lang/Language.html#getBaseLanguageId()"><code>Language#getBaseLanguageId</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.13.0/net/sourceforge/pmd/lang/Language.html#isDialectOf(net.sourceforge.pmd.lang.Language)"><code>Language#isDialectOf</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.13.0/net/sourceforge/pmd/lang/LanguageModuleBase.html#<init>(net.sourceforge.pmd.lang.LanguageModuleBase.DialectLanguageMetadata)"><code>LanguageModuleBase#&lt;init&gt;</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.13.0/net/sourceforge/pmd/lang/LanguageModuleBase.html#asDialectOf(java.lang.String)"><code>LanguageModuleBase#asDialectOf</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.13.0/net/sourceforge/pmd/lang/LanguageModuleBase.DialectLanguageMetadata.html#"><code>LanguageModuleBase.DialectLanguageMetadata</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.13.0/net/sourceforge/pmd/lang/impl/BasePmdDialectLanguageVersionHandler.html#"><code>BasePmdDialectLanguageVersionHandler</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.13.0/net/sourceforge/pmd/lang/impl/SimpleDialectLanguageModuleBase.html#"><code>SimpleDialectLanguageModuleBase</code></a>

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5438](https://github.com/pmd/pmd/pull/5438): \[core] Support language dialects - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5450](https://github.com/pmd/pmd/pull/5450): Fix #3184: \[apex] New Rule: TypeShadowsBuiltInNamespace - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5573](https://github.com/pmd/pmd/pull/5573): Fix #5525: \[core] Add Sarif Level Property - [julees7](https://github.com/julees7) (@julees7)
* [#5623](https://github.com/pmd/pmd/pull/5623): \[dist] Make pmd launch script compatible with /bin/sh - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5648](https://github.com/pmd/pmd/pull/5648): Fix #5645: \[java] Parse error with yield statement - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5652](https://github.com/pmd/pmd/pull/5652): \[java] Cleanup `AccessorClassGenerationRule` implementation - [Pankraz76](https://github.com/Pankraz76) (@Pankraz76)
* [#5672](https://github.com/pmd/pmd/pull/5672): \[doc] Fix its/it's and doable/double typos - [John Jetmore](https://github.com/jetmore) (@jetmore)
* [#5674](https://github.com/pmd/pmd/pull/5674): Fix #5448: \[ci] Maintain public Docker image - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5684](https://github.com/pmd/pmd/pull/5684): Fix #5667: \[apex] ApexUnitTestShouldNotUseSeeAllDataTrue false negative when seeAllDate parameter is a string - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#5685](https://github.com/pmd/pmd/pull/5685): \[doc] typo fix in PMD Designer reference - [Douglas Griffith](https://github.com/dwgrth) (@dwgrth)
* [#5686](https://github.com/pmd/pmd/pull/5686): Fix #5675: \[plsql] Support TREAT function with specified datatype - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5687](https://github.com/pmd/pmd/pull/5687): \[java] UnusedPrivateMethodRule: exclude serialization method readObjectNoData() - [Gili Tzabari](https://github.com/cowwoc) (@cowwoc)
* [#5688](https://github.com/pmd/pmd/pull/5688): \[java] Fix Double Literal for Java19+ compatibility - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5607](https://github.com/pmd/pmd/pull/5607): Bump org.junit:junit-bom from 5.11.4 to 5.12.1
* [#5641](https://github.com/pmd/pmd/pull/5641): Bump PMD from 7.11.0 to 7.12.0
* [#5653](https://github.com/pmd/pmd/pull/5653): Bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.0.0.4389 to 5.1.0.4751
* [#5654](https://github.com/pmd/pmd/pull/5654): Bump surefire.version from 3.5.2 to 3.5.3
* [#5655](https://github.com/pmd/pmd/pull/5655): Bump com.google.guava:guava from 33.4.5-jre to 33.4.6-jre
* [#5656](https://github.com/pmd/pmd/pull/5656): Bump org.ow2.asm:asm from 9.7.1 to 9.8
* [#5657](https://github.com/pmd/pmd/pull/5657): Bump com.google.protobuf:protobuf-java from 4.30.1 to 4.30.2
* [#5658](https://github.com/pmd/pmd/pull/5658): Bump logger from 1.6.6 to 1.7.0 in /.ci/files in the all-gems group across 1 directory
* [#5671](https://github.com/pmd/pmd/pull/5671): Bump checkstyle from 10.21.4 to 10.23.0
* [#5676](https://github.com/pmd/pmd/pull/5676): Bump org.checkerframework:checker-qual from 3.49.1 to 3.49.2
* [#5677](https://github.com/pmd/pmd/pull/5677): Bump junit5.platform.version from 1.12.1 to 1.12.2
* [#5678](https://github.com/pmd/pmd/pull/5678): Bump org.apache.commons:commons-text from 1.13.0 to 1.13.1
* [#5679](https://github.com/pmd/pmd/pull/5679): Bump com.google.guava:guava from 33.4.6-jre to 33.4.7-jre
* [#5680](https://github.com/pmd/pmd/pull/5680): Bump org.mockito:mockito-core from 5.16.1 to 5.17.0
* [#5681](https://github.com/pmd/pmd/pull/5681): Bump org.jacoco:jacoco-maven-plugin from 0.8.12 to 0.8.13
* [#5682](https://github.com/pmd/pmd/pull/5682): Bump net.bytebuddy:byte-buddy-agent from 1.17.4 to 1.17.5
* [#5683](https://github.com/pmd/pmd/pull/5683): Bump the all-gems group across 2 directories with 2 updates
* [#5691](https://github.com/pmd/pmd/pull/5691): Bump com.google.code.gson:gson from 2.12.1 to 2.13.0
* [#5692](https://github.com/pmd/pmd/pull/5692): Bump com.google.guava:guava from 33.4.7-jre to 33.4.8-jre
* [#5693](https://github.com/pmd/pmd/pull/5693): Bump net.bytebuddy:byte-buddy from 1.17.4 to 1.17.5
* [#5694](https://github.com/pmd/pmd/pull/5694): Bump org.junit:junit-bom from 5.12.1 to 5.12.2
* [#5696](https://github.com/pmd/pmd/pull/5696): Bump info.picocli:picocli from 4.7.6 to 4.7.7
* [#5697](https://github.com/pmd/pmd/pull/5697): Bump com.github.hazendaz.maven:coveralls-maven-plugin from 4.5.0-M6 to 4.7.0
* [#5704](https://github.com/pmd/pmd/pull/5704): Bump nokogiri from 1.18.5 to 1.18.8

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 117 commits
* 19 closed tickets & PRs
* Days since last release: 27

## 28-March-2025 - 7.12.0

The PMD team is pleased to announce PMD 7.12.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [✨ New Rules](#new-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### ✨ New Rules

* The new Java rule [`ImplicitFunctionalInterface`](https://docs.pmd-code.org/pmd-doc-7.12.0/pmd_rules_java_bestpractices.html#implicitfunctionalinterface) reports functional interfaces that were
  not explicitly declared as such with the annotation `@FunctionalInterface`. If an interface is accidentally a functional
  interface, then it should bear a `@SuppressWarnings("PMD.ImplicitFunctionalInterface")`
  annotation to make this clear.

### 🐛 Fixed Issues
* core
  * [#5593](https://github.com/pmd/pmd/issues/5593): \[core] Make renderers output files in deterministic order even when multithreaded
* apex
  * [#5567](https://github.com/pmd/pmd/issues/5567): \[apex] Provide type information for CastExpression
* apex-design
  * [#5616](https://github.com/pmd/pmd/issues/5616): \[apex] ExcessiveParameterList reports entire method instead of signature only
* java
  * [#5587](https://github.com/pmd/pmd/issues/5587): \[java] Thread deadlock during PMD analysis in ParseLock.getFinalStatus
* java-bestpractices
  * [#2849](https://github.com/pmd/pmd/issues/2849): \[java] New Rule: ImplicitFunctionalInterface
  * [#5369](https://github.com/pmd/pmd/issues/5369): \[java] UnusedPrivateMethod false positives with lombok.val
  * [#5590](https://github.com/pmd/pmd/issues/5590): \[java] LiteralsFirstInComparisonsRule not applied on constant
  * [#5592](https://github.com/pmd/pmd/issues/5592): \[java] UnusedAssignment false positive in record compact constructor
* java-codestyle
  * [#5079](https://github.com/pmd/pmd/issues/5079): \[java] LocalVariableCouldBeFinal false-positive with lombok.val
  * [#5452](https://github.com/pmd/pmd/issues/5452): \[java] PackageCase: Suppression comment has no effect due to finding at wrong position in case of JavaDoc comment
* plsql
  * [#4441](https://github.com/pmd/pmd/issues/4441): \[plsql] Parsing exception with XMLQUERY function in SELECT
  * [#5521](https://github.com/pmd/pmd/issues/5521): \[plsql] Long parse time and eventually parse error with XMLAGG order by clause

### 🚨 API Changes
#### Deprecations
* java
  * The method <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.12.0/net/sourceforge/pmd/lang/java/ast/AbstractJavaExpr.html#buildConstValue()"><code>buildConstValue</code></a> is deprecated for removal. It should
    have been package-private from the start. In order to get the (compile time) const value of an expression, use
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.12.0/net/sourceforge/pmd/lang/java/ast/ASTExpression.html#getConstValue()"><code>getConstValue</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.12.0/net/sourceforge/pmd/lang/java/ast/ASTExpression.html#getConstFoldingResult()"><code>getConstFoldingResult</code></a>
    instead.
  * For the same reason, the following methods are also deprecated for removal:
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.12.0/net/sourceforge/pmd/lang/java/ast/ASTNumericLiteral.html#buildConstValue()"><code>buildConstValue</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.12.0/net/sourceforge/pmd/lang/java/ast/ASTStringLiteral.html#buildConstValue()"><code>buildConstValue</code></a>.

- <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.12.0/net/sourceforge/pmd/lang/java/types/JTypeVar.html#withUpperbound(net.sourceforge.pmd.types.JTypeMirror)"><code>JTypeVar#withUpperbound</code></a> is deprecated. It was previously meant to be used
  internally and not needed anymore.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5550](https://github.com/pmd/pmd/pull/5550): Fix #5521: \[plsql] Improve parser performance by reducing lookaheads - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5554](https://github.com/pmd/pmd/pull/5554): Fix #5369: \[java] Consider that lombok.val and var are inferred - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5555](https://github.com/pmd/pmd/pull/5555): Fix #2849: \[java] Add rule ImplicitFunctionalInterface - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5556](https://github.com/pmd/pmd/pull/5556): \[ci] New workflow "Publish Results from Pull Requests" - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5574](https://github.com/pmd/pmd/pull/5574): Fix #5567: \[apex] Provide type info for CastExpression - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5583](https://github.com/pmd/pmd/pull/5583): \[java] Fix race condition in ClassStub for inner classes - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5586](https://github.com/pmd/pmd/pull/5586): \[java/core] Micro optimizations  - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5588](https://github.com/pmd/pmd/pull/5588): \[java] Fix crash when parsing class for anonymous class - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5591](https://github.com/pmd/pmd/pull/5591): Fix #5587: \[java] Fix deadlock while loading ClassStub - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5593](https://github.com/pmd/pmd/pull/5593): \[core] Make renderers output files in deterministic order even when multithreaded - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5595](https://github.com/pmd/pmd/pull/5595): Fix #5590: \[java] LiteralsFirstInComparisons with constant field - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5596](https://github.com/pmd/pmd/pull/5596): Fix #4441: \[plsql] XMLQuery - Support identifier as XQuery_string parameter - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5598](https://github.com/pmd/pmd/pull/5598): Fix #5592: \[java] Fix UnusedAssignment FP with compact record ctor - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5600](https://github.com/pmd/pmd/pull/5600): Fix #5079: \[java] LocalVariableCouldBeFinal false-positive with lombok.val - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5611](https://github.com/pmd/pmd/pull/5611): Fix #5452: \[java] PackageCase reported on wrong line - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5617](https://github.com/pmd/pmd/pull/5617): Fix #5616: \[apex] ExcessiveParameterList: Report only method signature - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5618](https://github.com/pmd/pmd/pull/5618): \[doc] Fix search index - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5558](https://github.com/pmd/pmd/pull/5558): Bump PMD from 7.10.0 to 7.11.0
* [#5561](https://github.com/pmd/pmd/pull/5561): Bump org.apache.groovy:groovy from 4.0.25 to 4.0.26
* [#5562](https://github.com/pmd/pmd/pull/5562): Bump org.junit.platform:junit-platform-suite from 1.11.4 to 1.12.0
* [#5564](https://github.com/pmd/pmd/pull/5564): Bump org.apache.maven.plugins:maven-clean-plugin from 3.4.0 to 3.4.1
* [#5565](https://github.com/pmd/pmd/pull/5565): Bump org.apache.maven.plugins:maven-deploy-plugin from 3.1.3 to 3.1.4
* [#5566](https://github.com/pmd/pmd/pull/5566): Bump io.github.apex-dev-tools:apex-ls_2.13 from 5.7.0 to 5.8.0
* [#5571](https://github.com/pmd/pmd/pull/5571): Bump nokogiri from 1.16.7 to 1.18.3
* [#5572](https://github.com/pmd/pmd/pull/5572): Bump uri from 0.13.1 to 1.0.3
* [#5575](https://github.com/pmd/pmd/pull/5575): Bump org.jsoup:jsoup from 1.18.3 to 1.19.1
* [#5576](https://github.com/pmd/pmd/pull/5576): Bump scalameta.version from 4.13.2 to 4.13.3
* [#5577](https://github.com/pmd/pmd/pull/5577): Bump org.yaml:snakeyaml from 2.3 to 2.4
* [#5578](https://github.com/pmd/pmd/pull/5578): Bump com.google.protobuf:protobuf-java from 4.29.3 to 4.30.0
* [#5580](https://github.com/pmd/pmd/pull/5580): Bump net.bytebuddy:byte-buddy from 1.17.1 to 1.17.2
* [#5581](https://github.com/pmd/pmd/pull/5581): Bump com.puppycrawl.tools:checkstyle from 10.21.3 to 10.21.4
* [#5582](https://github.com/pmd/pmd/pull/5582): Bump the gems liquid to 5.8.1 and logger to 1.6.6
* [#5602](https://github.com/pmd/pmd/pull/5602): Bump org.apache.maven.plugins:maven-install-plugin from 3.1.3 to 3.1.4
* [#5603](https://github.com/pmd/pmd/pull/5603): Bump net.bytebuddy:byte-buddy-agent from 1.17.1 to 1.17.2
* [#5604](https://github.com/pmd/pmd/pull/5604): Bump org.mockito:mockito-core from 5.15.2 to 5.16.1
* [#5605](https://github.com/pmd/pmd/pull/5605): Bump org.junit.platform:junit-platform-suite from 1.12.0 to 1.12.1
* [#5606](https://github.com/pmd/pmd/pull/5606): Bump org.checkerframework:checker-qual from 3.49.0 to 3.49.1
* [#5608](https://github.com/pmd/pmd/pull/5608): Bump com.google.protobuf:protobuf-java from 4.30.0 to 4.30.1
* [#5619](https://github.com/pmd/pmd/pull/5619): Bump nokogiri from 1.18.3 to 1.18.5
* [#5624](https://github.com/pmd/pmd/pull/5624): Bump scalameta.version from 4.13.3 to 4.13.4
* [#5627](https://github.com/pmd/pmd/pull/5627): Bump net.bytebuddy:byte-buddy-agent from 1.17.2 to 1.17.4
* [#5628](https://github.com/pmd/pmd/pull/5628): Bump io.github.apex-dev-tools:apex-ls_2.13 from 5.8.0 to 5.9.0
* [#5629](https://github.com/pmd/pmd/pull/5629): Bump com.google.guava:guava from 33.4.0-jre to 33.4.5-jre
* [#5630](https://github.com/pmd/pmd/pull/5630): Bump net.bytebuddy:byte-buddy from 1.17.2 to 1.17.4

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 114 commits
* 28 closed tickets & PRs
* Days since last release: 27

## 28-February-2025 - 7.11.0

The PMD team is pleased to announce PMD 7.11.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [✨ New Rules](#new-rules)
    * [Signed Releases](#signed-releases)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### ✨ New Rules
* The new Apex rule [`AvoidStatefulDatabaseResult`](https://docs.pmd-code.org/pmd-doc-7.11.0/pmd_rules_apex_errorprone.html#avoidstatefuldatabaseresult) detects `Database.Stateful` implementations
  that store database results in instance variables. This can cause serialization issues between successive batch
  iterations.

#### Signed Releases
We now not only sign the maven artifacts, but also our binary distribution files that you can
download from [GitHub Releases](https://github.com/pmd/pmd/releases).
See the page [Signed Releases](pmd_userdocs_signed_releases.html) in our documentation for how to verify the files.

### 🐛 Fixed Issues
* apex-errorprone
  * [#5305](https://github.com/pmd/pmd/issues/5305): \[apex] New Rule: Avoid Stateful Database Results
* java
  * [#5442](https://github.com/pmd/pmd/issues/5442): \[java] StackOverflowError with recursive generic types
  * [#5493](https://github.com/pmd/pmd/issues/5493): \[java] IllegalArgumentException: <?> cannot be a wildcard bound
  * [#5505](https://github.com/pmd/pmd/issues/5505): \[java] java.lang.StackOverflowError while executing a PmdRunnable
* java-bestpractices
  * [#3359](https://github.com/pmd/pmd/issues/3359): \[java] UnusedPrivateMethod does not recognize Lombok @<!-- -->EqualsAndHashCode.Include annotation
  * [#5486](https://github.com/pmd/pmd/issues/5486): \[java] UnusedPrivateMethod detected when class is referenced in another class
  * [#5504](https://github.com/pmd/pmd/issues/5504): \[java] UnusedAssignment false-positive in for-loop with continue
* java-codestyle
  * [#4822](https://github.com/pmd/pmd/issues/4822): \[java] UnnecessaryCast false-positive for raw types
  * [#5073](https://github.com/pmd/pmd/issues/5073): \[java] UnnecessaryCast false-positive for cast in return position of lambda
  * [#5440](https://github.com/pmd/pmd/issues/5440): \[java] UnnecessaryCast reported in stream chain map() call that casts to more generic interface
  * [#5523](https://github.com/pmd/pmd/issues/5523): \[java] UnnecessaryCast false-positive for integer operations in floating-point context
  * [#5541](https://github.com/pmd/pmd/pull/5541):   \[java] Fix IdenticalCatchBranch reporting branches that call different overloads
* java-design
  * [#5018](https://github.com/pmd/pmd/issues/5018): \[java] FinalFieldCouldBeStatic false-positive for access of super class field
* plsql
  * [#5522](https://github.com/pmd/pmd/issues/5522): \[plsql] Parse error for operator in TRIM function call

### 🚨 API Changes
#### Deprecations
* java
  * The method <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.11.0/net/sourceforge/pmd/lang/java/types/TypeOps.html#isContextDependent(net.sourceforge.pmd.lang.java.types.JMethodSig)"><code>TypeOps#isContextDependent(JMethodSig)</code></a> is deprecated for removal.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.11.0/net/sourceforge/pmd/lang/java/types/TypeOps.html#isContextDependent(net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol)"><code>isContextDependent(JExecutableSymbol)</code></a> instead which
    is more flexible.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5425](https://github.com/pmd/pmd/pull/5425): \[apex] New Rule: Avoid Stateful Database Results - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5491](https://github.com/pmd/pmd/pull/5491): \[docs] Call render_release_notes.rb within docs - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5492](https://github.com/pmd/pmd/pull/5492): \[docs] Add security page with known vulnerabilities - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5503](https://github.com/pmd/pmd/pull/5503): \[java] AvoidSynchronizedAtMethodLevel: Fixed error in code example - [Balazs Glatz](https://github.com/gbq6) (@gbq6)
* [#5507](https://github.com/pmd/pmd/pull/5507): Fix #5486: \[java] Fix UnusedPrivateMethod - always search decls in current AST - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5508](https://github.com/pmd/pmd/pull/5508): Fix #3359: \[java] UnusedPrivateMethod: Ignore lombok.EqualsAndHashCode.Include - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5510](https://github.com/pmd/pmd/pull/5510): \[ci] Add signed releases - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5524](https://github.com/pmd/pmd/pull/5524): \[ci] New optimized workflow for pull requests - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5526](https://github.com/pmd/pmd/pull/5526): Fix #5523: \[java] UnnecessaryCast FP with integer division - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5527](https://github.com/pmd/pmd/pull/5527): Fix #5522: \[plsql] Allow arbitrary expressions for TRIM - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5528](https://github.com/pmd/pmd/pull/5528): Fix #5442: \[java] Fix stackoverflow with recursive generic types - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5529](https://github.com/pmd/pmd/pull/5529): Fix #5493: \[java] IllegalArgumentException with wildcard bound - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5530](https://github.com/pmd/pmd/pull/5530): Fix #5073: \[java] UnnecessaryCast FP with lambdas - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5537](https://github.com/pmd/pmd/pull/5537): Fix #5504: \[java] UnusedAssignment FP with continue in foreach loop - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5538](https://github.com/pmd/pmd/pull/5538): Add project icon for IntelliJ IDEA - [Vincent Potucek](https://github.com/pankratz227) (@pankratz227)
* [#5539](https://github.com/pmd/pmd/pull/5539): \[plsql] Add OracleDBUtils as regression testing project - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5541](https://github.com/pmd/pmd/pull/5541): \[java] Fix IdenticalCatchBranch reporting branches that call different overloads - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5542](https://github.com/pmd/pmd/pull/5542): Add GitHub issue links in IDEA git log - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5544](https://github.com/pmd/pmd/pull/5544): \[javacc] Move grammar files into src/main/javacc - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5551](https://github.com/pmd/pmd/pull/5551): \[doc] Update contributors for 7.11.0 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5552](https://github.com/pmd/pmd/pull/5552): Fix #4822: \[java] UnnecessaryCast FP with unchecked cast - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5553](https://github.com/pmd/pmd/pull/5553): Fix #5018: \[java] FinalFieldCouldBeStatic FP with super field access - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5490](https://github.com/pmd/pmd/pull/5490): Bump PMD from 7.9.0 to 7.10.0
* [#5494](https://github.com/pmd/pmd/pull/5494): Bump liquid from 5.7.1 to 5.7.2 in the all-gems group across 1 directory
* [#5497](https://github.com/pmd/pmd/pull/5497): Bump net.bytebuddy:byte-buddy-agent from 1.16.1 to 1.17.0
* [#5498](https://github.com/pmd/pmd/pull/5498): Bump org.assertj:assertj-core from 3.25.3 to 3.27.3
* [#5499](https://github.com/pmd/pmd/pull/5499): Bump org.mockito:mockito-core from 5.14.2 to 5.15.2
* [#5500](https://github.com/pmd/pmd/pull/5500): Bump org.junit:junit-bom from 5.11.2 to 5.11.4
* [#5501](https://github.com/pmd/pmd/pull/5501): Bump org.scala-lang:scala-reflect from 2.13.15 to 2.13.16
* [#5516](https://github.com/pmd/pmd/pull/5516): Bump org.jetbrains:annotations from 26.0.1 to 26.0.2
* [#5517](https://github.com/pmd/pmd/pull/5517): Bump net.bytebuddy:byte-buddy from 1.15.11 to 1.17.0
* [#5518](https://github.com/pmd/pmd/pull/5518): Bump org.junit.platform:junit-platform-suite from 1.11.3 to 1.11.4
* [#5519](https://github.com/pmd/pmd/pull/5519): Bump org.checkerframework:checker-qual from 3.48.3 to 3.49.0
* [#5520](https://github.com/pmd/pmd/pull/5520): Bump com.google.guava:guava from 33.0.0-jre to 33.4.0-jre
* [#5532](https://github.com/pmd/pmd/pull/5532): Bump net.bytebuddy:byte-buddy-agent from 1.17.0 to 1.17.1
* [#5533](https://github.com/pmd/pmd/pull/5533): Bump log4j.version from 2.24.2 to 2.24.3
* [#5534](https://github.com/pmd/pmd/pull/5534): Bump com.google.code.gson:gson from 2.11.0 to 2.12.1
* [#5535](https://github.com/pmd/pmd/pull/5535): Bump scalameta.version from 4.12.7 to 4.13.1.1
* [#5536](https://github.com/pmd/pmd/pull/5536): Bump org.apache.groovy:groovy from 4.0.24 to 4.0.25
* [#5545](https://github.com/pmd/pmd/pull/5545): Bump commons-logging:commons-logging from 1.3.4 to 1.3.5
* [#5546](https://github.com/pmd/pmd/pull/5546): Bump scalameta.version from 4.13.1.1 to 4.13.2
* [#5547](https://github.com/pmd/pmd/pull/5547): Bump net.bytebuddy:byte-buddy from 1.17.0 to 1.17.1
* [#5548](https://github.com/pmd/pmd/pull/5548): Bump com.puppycrawl.tools:checkstyle from 10.21.2 to 10.21.3
* [#5549](https://github.com/pmd/pmd/pull/5549): Bump org.apache.maven.plugins:maven-compiler-plugin from 3.13.0 to 3.14.0

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 97 commits
* 35 closed tickets & PRs
* Days since last release: 28

## 31-January-2025 - 7.10.0

The PMD team is pleased to announce PMD 7.10.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [🚀 New: Java 24 Support](#new-java-24-support)
    * [New GPG Release Signing Key](#new-gpg-release-signing-key)
    * [Updated PMD Designer](#updated-pmd-designer)
* [🌟 New and changed rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Removed Experimental API](#removed-experimental-api)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### 🚀 New: Java 24 Support
This release of PMD brings support for Java 24. There are no new standard language features,
but a couple of preview language features:

* [JEP 488: Primitive Types in Patterns, instanceof, and switch (Second Preview)](https://openjdk.org/jeps/488)
* [JEP 492: Flexible Constructor Bodies (Third Preview)](https://openjdk.org/jeps/492)
* [JEP 494: Module Import Declarations (Second Preview)](https://openjdk.org/jeps/494)
* [JEP 495: Simple Source Files and Instance Main Methods (Fourth Preview)](https://openjdk.org/jeps/495)

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `24-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-24-preview ...

Note: Support for Java 22 preview language features have been removed. The version "22-preview"
is no longer available.

#### New GPG Release Signing Key

Since January 2025, we switched the GPG Key we use for signing releases in Maven Central to be
[A0B5CA1A4E086838](https://keyserver.ubuntu.com/pks/lookup?search=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838&fingerprint=on&op=index).
The full fingerprint is `2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838`.

This step was necessary, as the passphrase of the old key has been compromised and therefore the key is not
safe to use anymore. While the key itself is not compromised as far as we know, we still decided to generate a
new key, just to be safe. As until now (January 2025) we are not aware, that the key actually has been misused.
The previous releases of PMD in Maven Central can still be considered untampered, as Maven Central is read-only.

This unexpected issue was discovered while checking [Reproducible Builds](https://reproducible-builds.org/) by a
third party.

The security advisory about the compromised passphrase is tracked as
[GHSA-88m4-h43f-wx84](https://github.com/pmd/pmd/security/advisories/GHSA-88m4-h43f-wx84)
and [CVE-2025-23215](https://www.cve.org/CVERecord?id=CVE-2025-23215).

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog (7.10.0)](https://github.com/pmd/pmd-designer/releases/tag/7.10.0).

### 🌟 New and changed rules

#### New Rules

* The new Java rule [`ExhaustiveSwitchHasDefault`](https://docs.pmd-code.org/pmd-doc-7.10.0/pmd_rules_java_bestpractices.html#exhaustiveswitchhasdefault) finds switch statements and
  expressions, that cover already all cases but still have a default case. This default case is unnecessary
  and prevents getting compiler errors when e.g. new enum constants are added without extending the switch.

### 🐛 Fixed Issues
* apex
  * [#5388](https://github.com/pmd/pmd/issues/5388): \[apex] Parse error with time literal in SOQL query
  * [#5456](https://github.com/pmd/pmd/issues/5456): \[apex] Issue with java dependency apex-parser-4.3.1 but apex-parser-4.3.0 works
* apex-security
  * [#3158](https://github.com/pmd/pmd/issues/3158): \[apex] ApexSuggestUsingNamedCred false positive with Named Credential merge fields
* documentation
  * [#2492](https://github.com/pmd/pmd/issues/2492): \[doc] Promote wiki pages to standard doc pages
* java
  * [#5154](https://github.com/pmd/pmd/issues/5154): \[java] Support Java 24
* java-performance
  * [#5311](https://github.com/pmd/pmd/issues/5311): \[java] TooFewBranchesForSwitch false positive for exhaustive switches over enums without default case

### 🚨 API Changes

#### Removed Experimental API
* pmd-java
  * `net.sourceforge.pmd.lang.java.ast.ASTTemplate`, `net.sourceforge.pmd.lang.java.ast.ASTTemplateExpression`,
    `net.sourceforge.pmd.lang.java.ast.ASTTemplateFragment`: These nodes were introduced with Java 21 and 22
    Preview to support String Templates. However, the String Template preview feature was not finalized
    and has been removed from Java for now. We now cleaned up the PMD implementation of it.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5327](https://github.com/pmd/pmd/pull/5327): \[apex] Update apex-parser and summit-ast - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5412](https://github.com/pmd/pmd/pull/5412): \[java] Support exhaustive switches - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5449](https://github.com/pmd/pmd/pull/5449): Use new gpg key (A0B5CA1A4E086838) - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5458](https://github.com/pmd/pmd/pull/5458): \[doc] Move Wiki pages into main documentation, cleanups - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5471](https://github.com/pmd/pmd/pull/5471): \[java] Support Java 24 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5488](https://github.com/pmd/pmd/pull/5488): \[apex] Fix #3158: Recognize Named Credentials merge fields in ApexSuggestUsingNamedCredRule  - [William Brockhus](https://github.com/YodaDaCoda) (@YodaDaCoda)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5423](https://github.com/pmd/pmd/pull/5423): Bump PMD from 7.8.0 to 7.9.0
* [#5433](https://github.com/pmd/pmd/pull/5433): Bump org.codehaus.mojo:exec-maven-plugin from 3.2.0 to 3.5.0
* [#5434](https://github.com/pmd/pmd/pull/5434): Bump commons-logging:commons-logging from 1.3.0 to 1.3.4
* [#5435](https://github.com/pmd/pmd/pull/5435): Bump org.apache.maven.plugins:maven-enforcer-plugin from 3.4.1 to 3.5.0
* [#5436](https://github.com/pmd/pmd/pull/5436): Bump the all-gems group across 2 directories with 1 update
* [#5445](https://github.com/pmd/pmd/pull/5445): Bump org.junit.platform:junit-platform-commons from 1.11.2 to 1.11.4
* [#5446](https://github.com/pmd/pmd/pull/5446): Bump org.sonarsource.scanner.maven:sonar-maven-plugin from 3.10.0.2594 to 5.0.0.4389
* [#5459](https://github.com/pmd/pmd/pull/5459): Bump org.apache.maven.plugins:maven-gpg-plugin from 3.1.0 to 3.2.7
* [#5460](https://github.com/pmd/pmd/pull/5460): Bump org.apache.commons:commons-text from 1.12.0 to 1.13.0
* [#5461](https://github.com/pmd/pmd/pull/5461): Bump com.google.protobuf:protobuf-java from 4.29.1 to 4.29.3
* [#5472](https://github.com/pmd/pmd/pull/5472): Bump net.bytebuddy:byte-buddy-agent from 1.15.11 to 1.16.1
* [#5473](https://github.com/pmd/pmd/pull/5473): Bump org.sonatype.plugins:nexus-staging-maven-plugin from 1.6.13 to 1.7.0
* [#5474](https://github.com/pmd/pmd/pull/5474): Bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.23.0 to 0.23.1
* [#5475](https://github.com/pmd/pmd/pull/5475): Bump liquid from 5.6.0 to 5.7.0 in the all-gems group across 1 directory
* [#5479](https://github.com/pmd/pmd/pull/5479): Bump pmd-designer from 7.2.0 to 7.10.0
* [#5480](https://github.com/pmd/pmd/pull/5480): Bump scalameta.version from 4.9.1 to 4.12.7
* [#5481](https://github.com/pmd/pmd/pull/5481): Bump liquid from 5.7.0 to 5.7.1 in the all-gems group across 1 directory
* [#5482](https://github.com/pmd/pmd/pull/5482): Bump org.codehaus.mojo:versions-maven-plugin from 2.17.1 to 2.18.0
* [#5483](https://github.com/pmd/pmd/pull/5483): Bump org.jetbrains.dokka:dokka-maven-plugin from 1.9.20 to 2.0.0
* [#5484](https://github.com/pmd/pmd/pull/5484): Bump com.github.hazendaz.maven:coveralls-maven-plugin from 4.5.0-M5 to 4.5.0-M6
* [#5485](https://github.com/pmd/pmd/pull/5485): Bump com.puppycrawl.tools:checkstyle from 10.20.2 to 10.21.2

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 70 commits
* 13 closed tickets & PRs
* Days since last release: 34

## 27-December-2024 - 7.9.0

The PMD team is pleased to announce PMD 7.9.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [New: CPD support for Rust](#new-cpd-support-for-rust)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Experimental API](#experimental-api)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### New: CPD support for Rust

CPD now supports Rust, a blazingly fast and memory-efficient programming language.
It is shipped in the new module `pmd-rust`.

### 🐛 Fixed Issues
* cli
  * [#5399](https://github.com/pmd/pmd/issues/5399): \[cli] Windows: PMD fails to start with special characters in path names
  * [#5401](https://github.com/pmd/pmd/issues/5401): \[cli] Windows: Console output doesn't use unicode
* java
  * [#5096](https://github.com/pmd/pmd/issues/5096): \[java] StackOverflowError with recursively bound type variable
* java-bestpractices
  * [#4861](https://github.com/pmd/pmd/issues/4861): \[java] UnusedPrivateMethod - false positive with static methods in core JDK classes
* java-documentation
  * [#2996](https://github.com/pmd/pmd/issues/2996): \[java] CommentSize rule violation is not suppressed at method level

### 🚨 API Changes

#### Experimental API

* pmd-core: <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.9.0/net/sourceforge/pmd/reporting/RuleContext.html#addViolationWithPosition(net.sourceforge.pmd.reporting.Reportable,net.sourceforge.pmd.lang.ast.AstInfo,net.sourceforge.pmd.lang.document.FileLocation,java.lang.String,java.lang.Object...)"><code>RuleContext#addViolationWithPosition</code></a>

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#4939](https://github.com/pmd/pmd/pull/4939): \[java] Fix #2996 - CommentSize/CommentContent suppression - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5376](https://github.com/pmd/pmd/pull/5376): \[java] Fix #4861 - UnusedPrivateMethod FP in JDK classes - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5387](https://github.com/pmd/pmd/pull/5387): \[java] Fix #5096 - StackOverflowError with recursively bounded tvar - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5400](https://github.com/pmd/pmd/pull/5400): Fix #5399: \[cli] pmd.bat: Quote all variables when using SET - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5402](https://github.com/pmd/pmd/pull/5402): Fix #5401: \[cli] pmd.bat: set codepage to 65001 (UTF-8) - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5404](https://github.com/pmd/pmd/pull/5404): \[doc] Update tools / integrations / ide plugins / news pages - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5414](https://github.com/pmd/pmd/pull/5414): Add Rust CPD - [Julia Paluch](https://github.com/juliapaluch) (@juliapaluch)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5375](https://github.com/pmd/pmd/pull/5375): Bump pmd from 7.7.0 to 7.8.0
* [#5377](https://github.com/pmd/pmd/pull/5377): Bump com.puppycrawl.tools:checkstyle from 10.20.1 to 10.20.2
* [#5378](https://github.com/pmd/pmd/pull/5378): Bump net.bytebuddy:byte-buddy from 1.14.12 to 1.15.10
* [#5379](https://github.com/pmd/pmd/pull/5379): Bump io.github.git-commit-id:git-commit-id-maven-plugin from 7.0.0 to 9.0.1
* [#5380](https://github.com/pmd/pmd/pull/5380): Bump org.apache.maven.plugins:maven-shade-plugin from 3.5.2 to 3.6.0
* [#5384](https://github.com/pmd/pmd/pull/5384): Bump org.apache.groovy:groovy from 4.0.19 to 4.0.24
* [#5390](https://github.com/pmd/pmd/pull/5390): Bump com.google.protobuf:protobuf-java from 4.28.2 to 4.29.1
* [#5391](https://github.com/pmd/pmd/pull/5391): Bump org.hamcrest:hamcrest from 2.2 to 3.0
* [#5392](https://github.com/pmd/pmd/pull/5392): Bump org.codehaus.mojo:build-helper-maven-plugin from 3.5.0 to 3.6.0
* [#5393](https://github.com/pmd/pmd/pull/5393): Bump org.jsoup:jsoup from 1.17.2 to 1.18.3
* [#5394](https://github.com/pmd/pmd/pull/5394): Bump org.apache.maven.plugins:maven-jar-plugin from 3.3.0 to 3.4.2
* [#5395](https://github.com/pmd/pmd/pull/5395): Bump webrick from 1.9.0 to 1.9.1 in /docs in the all-gems group across 1 directory
* [#5405](https://github.com/pmd/pmd/pull/5405): Bump org.yaml:snakeyaml from 2.2 to 2.3
* [#5406](https://github.com/pmd/pmd/pull/5406): Bump io.github.apex-dev-tools:apex-ls_2.13 from 5.5.0 to 5.7.0
* [#5407](https://github.com/pmd/pmd/pull/5407): Bump net.bytebuddy:byte-buddy-agent from 1.14.19 to 1.15.11
* [#5409](https://github.com/pmd/pmd/pull/5409): Bump net.bytebuddy:byte-buddy from 1.15.10 to 1.15.11
* [#5410](https://github.com/pmd/pmd/pull/5410): Bump org.apache.maven.plugins:maven-javadoc-plugin from 3.6.3 to 3.11.2
* [#5411](https://github.com/pmd/pmd/pull/5411): Bump csv from 3.3.0 to 3.3.1 in /docs in the all-gems group across 1 directory
* [#5417](https://github.com/pmd/pmd/pull/5417): Bump org.cyclonedx:cyclonedx-maven-plugin from 2.7.11 to 2.9.1
* [#5418](https://github.com/pmd/pmd/pull/5418): Bump org.checkerframework:checker-qual from 3.48.1 to 3.48.3
* [#5419](https://github.com/pmd/pmd/pull/5419): Bump org.apache.maven.plugins:maven-checkstyle-plugin from 3.5.0 to 3.6.0
* [#5422](https://github.com/pmd/pmd/pull/5422): Bump the all-gems group across 2 directories with 2 updates

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 69 commits
* 12 closed tickets & PRs
* Days since last release: 28

## 29-November-2024 - 7.8.0

The PMD team is pleased to announce PMD 7.8.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
* [🌟 New and changed rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

### 🌟 New and changed rules

#### New Rules
* The new Apex rule [`QueueableWithoutFinalizer`](https://docs.pmd-code.org/pmd-doc-7.8.0/pmd_rules_apex_bestpractices.html#queueablewithoutfinalizer) detects when the Queueable interface
  is used but a Finalizer is not attached. Without attaching a Finalizer, there is no way of designing error
  recovery actions should the Queueable action fail.

### 🐛 Fixed Issues
* ant
  * [#1860](https://github.com/pmd/pmd/issues/1860): \[ant] Reflective access warnings on java > 9 and java < 17
* apex
  * [#5302](https://github.com/pmd/pmd/issues/5302): \[apex] New Rule: Queueable Should Attach Finalizer
  * [#5333](https://github.com/pmd/pmd/issues/5333): \[apex] Token recognition errors for string containing unicode escape sequence
* html
  * [#5322](https://github.com/pmd/pmd/issues/5322): \[html] CPD throws exception on when HTML file is missing closing tag
* java
  * [#5283](https://github.com/pmd/pmd/issues/5283): \[java] AssertionError "this should be unreachable" with scala library
  * [#5293](https://github.com/pmd/pmd/issues/5293): \[java] Deadlock when executing PMD in multiple threads
  * [#5324](https://github.com/pmd/pmd/issues/5324): \[java] Issue with type inference of nested lambdas
  * [#5329](https://github.com/pmd/pmd/issues/5329): \[java] Type inference issue with unknown method ref in call chain
  * [#5338](https://github.com/pmd/pmd/issues/5338): \[java] Unresolved target type for lambdas make overload resolution fail
* java-bestpractices
  * [#4113](https://github.com/pmd/pmd/issues/4113): \[java] JUnitTestsShouldIncludeAssert - false positive with SoftAssertionsExtension
  * [#5083](https://github.com/pmd/pmd/issues/5083): \[java] UnusedPrivateMethod false positive when method reference has no target type
  * [#5097](https://github.com/pmd/pmd/issues/5097): \[java] UnusedPrivateMethod FP with raw type missing from the classpath
  * [#5318](https://github.com/pmd/pmd/issues/5318): \[java] PreserveStackTraceRule: false-positive on Pattern Matching with instanceof
* java-codestyle
  * [#5214](https://github.com/pmd/pmd/issues/5214): \[java] Wrong message for LambdaCanBeMethodReference with method of enclosing class
  * [#5263](https://github.com/pmd/pmd/issues/5263): \[java] UnnecessaryFullyQualifiedName: false-positive in an enum that uses its own static variables
  * [#5315](https://github.com/pmd/pmd/issues/5315): \[java] UnnecessaryImport false positive for on-demand imports
* java-design
  * [#4763](https://github.com/pmd/pmd/issues/4763): \[java] SimplifyBooleanReturns - wrong suggested solution
* java-errorprone
  * [#5070](https://github.com/pmd/pmd/issues/5070): \[java] ConfusingArgumentToVarargsMethod FP when types are unresolved
* java-performance
  * [#5287](https://github.com/pmd/pmd/issues/5287): \[java] TooFewBranchesForSwitch false-positive with switch using list of case constants
  * [#5314](https://github.com/pmd/pmd/issues/5314): \[java] InsufficientStringBufferDeclarationRule: Lack of handling for char type parameters
  * [#5320](https://github.com/pmd/pmd/issues/5320): \[java] UseStringBufferLength: false-negative on StringBuffer of sb.toString().equals("")

### 🚨 API Changes

#### Deprecations
* pmd-coco
  * <a href="https://docs.pmd-code.org/apidocs/pmd-coco/7.8.0/net/sourceforge/pmd/lang/coco/ast/CocoBaseListener.html#"><code>CocoBaseListener</code></a> is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
   * <a href="https://docs.pmd-code.org/apidocs/pmd-coco/7.8.0/net/sourceforge/pmd/lang/coco/ast/CocoBaseVisitor.html#"><code>CocoBaseVisitor</code></a> is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
   * <a href="https://docs.pmd-code.org/apidocs/pmd-coco/7.8.0/net/sourceforge/pmd/lang/coco/ast/CocoListener.html#"><code>CocoListener</code></a> is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
   * <a href="https://docs.pmd-code.org/apidocs/pmd-coco/7.8.0/net/sourceforge/pmd/lang/coco/ast/CocoParser.html#"><code>CocoParser</code></a> is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
   * <a href="https://docs.pmd-code.org/apidocs/pmd-coco/7.8.0/net/sourceforge/pmd/lang/coco/ast/CocoVisitor.html#"><code>CocoVisitor</code></a> is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
* pmd-gherkin
  * <a href="https://docs.pmd-code.org/apidocs/pmd-gherkin/7.8.0/net/sourceforge/pmd/lang/gherkin/ast/GherkinBaseListener.html#"><code>GherkinBaseListener</code></a> is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-gherkin/7.8.0/net/sourceforge/pmd/lang/gherkin/ast/GherkinBaseVisitor.html#"><code>GherkinBaseVisitor</code></a> is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-gherkin/7.8.0/net/sourceforge/pmd/lang/gherkin/ast/GherkinListener.html#"><code>GherkinListener</code></a> is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-gherkin/7.8.0/net/sourceforge/pmd/lang/gherkin/ast/GherkinParser.html#"><code>GherkinParser</code></a> is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-gherkin/7.8.0/net/sourceforge/pmd/lang/gherkin/ast/GherkinVisitor.html#"><code>GherkinVisitor</code></a> is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
* pmd-julia
  * <a href="https://docs.pmd-code.org/apidocs/pmd-julia/7.8.0/net/sourceforge/pmd/lang/julia/ast/JuliaBaseListener.html#"><code>JuliaBaseListener</code></a> is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-julia/7.8.0/net/sourceforge/pmd/lang/julia/ast/JuliaBaseVisitor.html#"><code>JuliaBaseVisitor</code></a> is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-julia/7.8.0/net/sourceforge/pmd/lang/julia/ast/JuliaListener.html#"><code>JuliaListener</code></a> is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-julia/7.8.0/net/sourceforge/pmd/lang/julia/ast/JuliaParser.html#"><code>JuliaParser</code></a> is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-julia/7.8.0/net/sourceforge/pmd/lang/julia/ast/JuliaVisitor.html#"><code>JuliaVisitor</code></a> is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
* pmd-kotlin
  * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.8.0/net/sourceforge/pmd/lang/kotlin/ast/UnicodeClasses.html#"><code>UnicodeClasses</code></a> is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
* pmd-xml
  * <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.8.0/net/sourceforge/pmd/lang/xml/antlr4/XMLLexer.html#"><code>XMLLexer</code></a> is deprecated for removal. Use <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.8.0/net/sourceforge/pmd/lang/xml/ast/XMLLexer.html#"><code>net.sourceforge.pmd.lang.xml.ast.XMLLexer</code></a>
    instead (note different package `ast` instead of `antlr4`).

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5240](https://github.com/pmd/pmd/pull/5240): Release notes improvements - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5284](https://github.com/pmd/pmd/pull/5284): \[apex] Use case-insensitive input stream to avoid choking on Unicode escape sequences - [Willem A. Hajenius](https://github.com/wahajenius) (@wahajenius)
* [#5286](https://github.com/pmd/pmd/pull/5286): \[ant] Formatter: avoid reflective access to determine console encoding - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5289](https://github.com/pmd/pmd/pull/5289): \[java] TooFewBranchesForSwitch - allow list of case constants - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5296](https://github.com/pmd/pmd/pull/5296): \[xml] Have pmd-xml Lexer in line with other antlr grammars - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5300](https://github.com/pmd/pmd/pull/5300): Add rule test cases for issues fixed with PMD 7.0.0 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5303](https://github.com/pmd/pmd/pull/5303): \[apex] New Rule: Queueable Should Attach Finalizer - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5309](https://github.com/pmd/pmd/pull/5309): \[java] Fix #5293: Parse number of type parameters eagerly - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5310](https://github.com/pmd/pmd/pull/5310): \[java] Fix #5283 - inner class has public private modifiers - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5325](https://github.com/pmd/pmd/pull/5325): \[java] Fix inference dependency issue with nested lambdas - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5326](https://github.com/pmd/pmd/pull/5326): \[java] UseStringBufferLength - consider sb.toString().equals("") - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5328](https://github.com/pmd/pmd/pull/5328): \[html] Test for a closing tag when determining node positions - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5330](https://github.com/pmd/pmd/pull/5330): \[java] Propagate unknown type better when mref is unresolved - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5331](https://github.com/pmd/pmd/pull/5331): \[java] PreserveStackTrace - consider instance type patterns - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5332](https://github.com/pmd/pmd/pull/5332): \[java] InsufficientStringBufferDeclaration: Fix CCE for Character - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5334](https://github.com/pmd/pmd/pull/5334): \[java] UnitTestShouldIncludeAssert - consider SoftAssertionsExtension - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5335](https://github.com/pmd/pmd/pull/5335): \[kotlin] Prevent auxiliary grammars from generating lexers - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5336](https://github.com/pmd/pmd/pull/5336): \[gherkin] Remove generated gherkin code from coverage report - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5337](https://github.com/pmd/pmd/pull/5337): \[doc] Introducing PMD Guru on Gurubase.io - [Kursat Aktas](https://github.com/kursataktas) (@kursataktas)
* [#5339](https://github.com/pmd/pmd/pull/5339): \[java] Allow lambdas with unresolved target types to succeed inference - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5340](https://github.com/pmd/pmd/pull/5340): \[java] Fix #5097 - problem with unchecked conversion - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5341](https://github.com/pmd/pmd/pull/5341): \[java] Fix #5083 - UnusedPrivateMethod false positive with mref without target type but with exact method - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5342](https://github.com/pmd/pmd/pull/5342): \[julia] Ignore generated code in Julia module - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5345](https://github.com/pmd/pmd/pull/5345): \[coco] Remove generated coco files form coverage - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5346](https://github.com/pmd/pmd/pull/5346): \[typescript] Add cleanup after generating ts lexer - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5347](https://github.com/pmd/pmd/pull/5347): \[tsql] Flag generated lexer as generated - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5352](https://github.com/pmd/pmd/pull/5352): \[java] Add permitted subtypes to symbol API - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5353](https://github.com/pmd/pmd/pull/5353): \[java] Fix #5263 - UnnecessaryFullyQualifiedName FP with forward references - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5354](https://github.com/pmd/pmd/pull/5354): \[apex] Updated the docs for UnusedMethod as per discussion #5200 - [samc-gearset](https://github.com/sam-gearset) (@sam-gearset)
* [#5370](https://github.com/pmd/pmd/pull/5370): \[java] Fix #5214 - LambdaCanBeMethodReference issue with method of enclosing class - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5371](https://github.com/pmd/pmd/pull/5371): \[doc] Improve docs on adding Antlr languages - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5372](https://github.com/pmd/pmd/pull/5372): \[java] Fix #5315 - UnusedImport FP with import on demand - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5373](https://github.com/pmd/pmd/pull/5373): \[java] Fix #4763 - wrong message for SimplifyBooleanReturns - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5374](https://github.com/pmd/pmd/pull/5374): \[java] Fix #5070 - confusing argument to varargs method FP when types are unknown - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5285](https://github.com/pmd/pmd/pull/5285): Bump pmd from 7.5.0 to 7.7.0
* [#5288](https://github.com/pmd/pmd/pull/5288): Bump asm from 9.7 to 9.7.1
* [#5290](https://github.com/pmd/pmd/pull/5290): Bump org.apache.maven.plugins:maven-assembly-plugin from 3.6.0 to 3.7.1
* [#5301](https://github.com/pmd/pmd/pull/5301): Bump gems and bundler
* [#5307](https://github.com/pmd/pmd/pull/5307): Bump org.apache.maven.plugins:maven-clean-plugin from 3.3.2 to 3.4.0
* [#5308](https://github.com/pmd/pmd/pull/5308): Bump webrick from 1.8.2 to 1.9.0 in /docs in the all-gems group across 1 directory
* [#5312](https://github.com/pmd/pmd/pull/5312): Bump maven-pmd-plugin from 3.24.0 to 3.26.0
* [#5316](https://github.com/pmd/pmd/pull/5316): Bump rouge from 4.4.0 to 4.5.0 in the all-gems group across 1 directory
* [#5317](https://github.com/pmd/pmd/pull/5317): Bump org.apache.commons:commons-compress from 1.26.0 to 1.27.1
* [#5348](https://github.com/pmd/pmd/pull/5348): Bump rouge from 4.5.0 to 4.5.1 in the all-gems group across 1 directory
* [#5350](https://github.com/pmd/pmd/pull/5350): Bump org.apache.commons:commons-lang3 from 3.14.0 to 3.17.0
* [#5356](https://github.com/pmd/pmd/pull/5356): Bump build-tools to 28
* [#5357](https://github.com/pmd/pmd/pull/5357): Bump log4j.version from 2.23.0 to 2.24.2
* [#5358](https://github.com/pmd/pmd/pull/5358): Bump org.apache.maven.plugins:maven-dependency-plugin from 3.7.1 to 3.8.1
* [#5359](https://github.com/pmd/pmd/pull/5359): Bump org.apache.maven.plugins:maven-release-plugin from 3.0.1 to 3.1.1
* [#5360](https://github.com/pmd/pmd/pull/5360): Bump io.github.apex-dev-tools:apex-ls_2.13 from 5.2.0 to 5.5.0
* [#5361](https://github.com/pmd/pmd/pull/5361): Bump ant.version from 1.10.14 to 1.10.15
* [#5362](https://github.com/pmd/pmd/pull/5362): Bump org.jetbrains:annotations from 24.1.0 to 26.0.1
* [#5363](https://github.com/pmd/pmd/pull/5363): Bump com.puppycrawl.tools:checkstyle from 10.18.1 to 10.20.1
* [#5364](https://github.com/pmd/pmd/pull/5364): Bump info.picocli:picocli from 4.7.5 to 4.7.6
* [#5365](https://github.com/pmd/pmd/pull/5365): Bump com.github.hazendaz.maven:coveralls-maven-plugin from 4.5.0-M3 to 4.5.0-M5
* [#5366](https://github.com/pmd/pmd/pull/5366): Bump org.mockito:mockito-core from 4.11.0 to 5.14.2
* [#5367](https://github.com/pmd/pmd/pull/5367): Bump surefire.version from 3.2.5 to 3.5.2
* [#5368](https://github.com/pmd/pmd/pull/5368): Bump org.junit.platform:junit-platform-suite from 1.11.2 to 1.11.3

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 216 commits
* 55 closed tickets & PRs
* Days since last release: 35

## 25-October-2024 - 7.7.0

The PMD team is pleased to announce PMD 7.7.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [CPD can now ignore literals and identifiers in C++ code](#cpd-can-now-ignore-literals-and-identifiers-in-c-code)
* [🌟 Rule Changes](#rule-changes)
    * [Changed Rules](#changed-rules)
    * [Renamed Rules](#renamed-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### CPD can now ignore literals and identifiers in C++ code

When searching for duplicated code in C++ differences in literals or identifiers can be
ignored now (like in Java). This can be enabled via the command line options `--ignore-literal`
and `--ignore-identifiers`.  
See [PR #5040](https://github.com/pmd/pmd/pull/5040) for details.

### 🌟 Rule Changes

#### Changed Rules
* [`SwitchStmtsShouldHaveDefault`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#switchstmtsshouldhavedefault) (Java Best Practices) doesn't report empty switch statements anymore.
  To detect these, use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_codestyle.html#emptycontrolstatement).
* [`UnitTestShouldUseAfterAnnotation`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshoulduseafterannotation) (Java Best Practices) now also considers JUnit 5 and TestNG tests.
* [`UnitTestShouldUseBeforeAnnotation`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshouldusebeforeannotation) (Java Best Practices) now also considers JUnit 5 and TestNG tests.
* [`TooFewBranchesForSwitch`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_performance.html#toofewbranchesforswitch) (Java Performance) doesn't report empty switches anymore.
  To detect these, use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_codestyle.html#emptycontrolstatement).

#### Renamed Rules
* Several rules for unit testing have been renamed to better reflect their actual scope. Lots of them were called
  after JUnit / JUnit 4, even when they applied to JUnit 5 and / or TestNG.
  * [`UnitTestAssertionsShouldIncludeMessage`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestassertionsshouldincludemessage) (Java Best Practices) has been renamed from `JUnitAssertionsShouldIncludeMessage`.
  * [`UnitTestContainsTooManyAsserts`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestcontainstoomanyasserts) (Java Best Practices) has been renamed from `JUnitTestContainsTooManyAsserts`.
  * [`UnitTestShouldIncludeAssert`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshouldincludeassert) (Java Best Practices) has been renamed from `JUnitTestsShouldIncludeAssert`.
  * [`UnitTestShouldUseAfterAnnotation`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshoulduseafterannotation) (Java Best Practices) has been renamed from `JUnit4TestShouldUseAfterAnnotation`.
  * [`UnitTestShouldUseBeforeAnnotation`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshouldusebeforeannotation) (Java Best Practices) has been renamed from `JUnit4TestShouldUseBeforeAnnotation`.
  * [`UnitTestShouldUseTestAnnotation`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshouldusetestannotation) (Java Best Practices) has been renamed from `JUnit4TestShouldUseTestAnnotation`.
* Several rules about switch statements and switch expression have been renamed, as they apply both to Switch Statements
  and to Switch Expressions:
  * [`DefaultLabelNotLastInSwitch`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#defaultlabelnotlastinswitch) (Java Best Practices) has been renamed from `DefaultLabelNotLastInSwitchStmt`.
  * [`NonCaseLabelInSwitch`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_errorprone.html#noncaselabelinswitch) (Java Error Prone) has been renamed from `NonCaseLabelInSwitchStatement`.
  * [`TooFewBranchesForSwitch`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_performance.html#toofewbranchesforswitch) (Java Performance) has been renamed from `TooFewBranchesForASwitchStatement`.
  * [`NonExhaustiveSwitch`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#nonexhaustiveswitch) (Java Best Practices) has been renamed from `SwitchStmtsShouldHaveDefault`.

The old rule names still work but are deprecated.

### 🐛 Fixed Issues
* apex-performance
  * [#5270](https://github.com/pmd/pmd/issues/5270): \[apex] AvoidNonRestrictiveQueries when LIMIT is followed by bind expression
* java
  * [#4532](https://github.com/pmd/pmd/issues/4532): \[java] Rule misnomer for JUnit* rules
  * [#5261](https://github.com/pmd/pmd/issues/5261): \[java] Record patterns with empty deconstructor lists lead to NPE
* java-bestpractices
  * [#4286](https://github.com/pmd/pmd/issues/4286): \[java] Rename rule SwitchStmtsShouldHaveDefault to NonExhaustiveSwitch
  * [#4813](https://github.com/pmd/pmd/issues/4813): \[java] SwitchStmtsShouldHaveDefault false positive with pattern matching
* java-codestyle
  * [#5253](https://github.com/pmd/pmd/issues/5253): \[java] BooleanGetMethodName: False-negatives with `Boolean` wrapper
* java-design
  * [#5030](https://github.com/pmd/pmd/issues/5030): \[java] SwitchDensity false positive with pattern matching
* java-errorprone
  * [#3362](https://github.com/pmd/pmd/issues/3362): \[java] ImplicitSwitchFallThrough should consider switch expressions
  * [#5067](https://github.com/pmd/pmd/issues/5067): \[java] CloseResource: False positive for FileSystems.getDefault()
  * [#5244](https://github.com/pmd/pmd/issues/5244): \[java] UselessOperationOnImmutable should detect java.time types
  * [#5257](https://github.com/pmd/pmd/issues/5257): \[java] NonCaseLabelInSwitch should consider switch expressions
* java-performance
  * [#5249](https://github.com/pmd/pmd/issues/5249): \[java] TooFewBranchesForASwitchStatement false positive for Pattern Matching
  * [#5250](https://github.com/pmd/pmd/issues/5250): \[java] TooFewBranchesForASwitchStatement should consider Switch Expressions

### 🚨 API Changes
* java-bestpractices
  * The old rule name `JUnit4TestShouldUseAfterAnnotation` has been deprecated. Use the new name [`UnitTestShouldUseAfterAnnotation`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshoulduseafterannotation) instead.
  * The old rule name `JUnit4TestShouldUseBeforeAnnotation` has been deprecated. Use the new name [`UnitTestShouldUseBeforeAnnotation`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshouldusebeforeannotation) instead.
  * The old rule name `JUnit4TestShouldUseTestAnnotation` has been deprecated. Use the new name [`UnitTestShouldUseTestAnnotation`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshouldusetestannotation) instead.
  * The old rule name `JUnitAssertionsShouldIncludeMessage` has been deprecated. Use the new name [`UnitTestAssertionsShouldIncludeMessage`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestassertionsshouldincludemessage) instead.
  * The old rule name `JUnitTestContainsTooManyAsserts` has been deprecated. Use the new name [`UnitTestContainsTooManyAsserts`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestcontainstoomanyasserts) instead.
  * The old rule name `JUnitTestsShouldIncludeAssert` has been deprecated. Use the new name [`UnitTestShouldIncludeAssert`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#unittestshouldincludeassert) instead.
  * The old rule name `DefaultLabelNotLastInSwitchStmt` has been deprecated. Use the new name [`DefaultLabelNotLastInSwitch`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#defaultlabelnotlastinswitch) instead.
  * The old rule name `SwitchStmtsShouldHaveDefault` has been deprecated. USe the new name [`NonExhaustiveSwitch`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_bestpractices.html#nonexhaustiveswitch) instead.
* java-errorprone
  * The old rule name  `NonCaseLabelInSwitchStatement` has been deprecated. Use the new name [`NonCaseLabelInSwitch`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_errorprone.html#noncaselabelinswitch) instead.
* java-performance
  * The old rule name `TooFewBranchesForASwitchStatement` has been deprecated. Use the new name [`TooFewBranchesForSwitch`](https://docs.pmd-code.org/pmd-doc-7.7.0/pmd_rules_java_performance.html#toofewbranchesforswitch) instead.

### ✨ Merged pull requests
* [#4965](https://github.com/pmd/pmd/pull/4965): Fix #4532: \[java] Rename JUnit rules with overly restrictive names - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5040](https://github.com/pmd/pmd/pull/5040): \[cpp] Ignore literals and ignore identifiers capability to C++ CPD - [Jakub Dupak](https://github.com/jdupak) (@jdupak)
* [#5225](https://github.com/pmd/pmd/pull/5225): Fix #5067: \[java] CloseResource: False positive for FileSystems.getDefault() - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5241](https://github.com/pmd/pmd/pull/5241): Ignore javacc code in coverage report - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5245](https://github.com/pmd/pmd/pull/5245): \[java] Improve UnitTestShouldUse{After,Before}Annotation rules to support JUnit5 and TestNG - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5247](https://github.com/pmd/pmd/pull/5247): Fix #5030: \[java] SwitchDensity false positive with pattern matching - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5248](https://github.com/pmd/pmd/pull/5248): Fix #3362: \[java] ImplicitSwitchFallThrough should consider switch expressions - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5251](https://github.com/pmd/pmd/pull/5251): Fix #5249 and #5250: \[java] TooFewBranchesForSwitch ignore pattern matching and support switch expressions - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5252](https://github.com/pmd/pmd/pull/5252): Fix #4813: \[java] SwitchStmtsShouldHaveDefault false positive with pattern matching - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5255](https://github.com/pmd/pmd/pull/5255): \[java] Rename rule DefaultLabelNotLastInSwitch - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5256](https://github.com/pmd/pmd/pull/5256): Fix #5257: \[java] NonCaseLabelInSwitch - support switch expressions - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5258](https://github.com/pmd/pmd/pull/5258): Ignore generated antlr classes in coverage reports - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5264](https://github.com/pmd/pmd/pull/5264): Fix #5261: \[java] Fix NPE with empty pattern list - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5267](https://github.com/pmd/pmd/pull/5267): \[java] Rename rule SwitchStmtsShouldHaveDefault to NonExhaustiveSwitch - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5269](https://github.com/pmd/pmd/pull/5269): Fix #5253: \[java] Support Boolean wrapper class for BooleanGetMethodName rule - [Aryant Tripathi](https://github.com/Aryant-Tripathi) (@Aryant-Tripathi)
* [#5273](https://github.com/pmd/pmd/pull/5273): Fix #5270: \[apex] AvoidNonRestrictiveQueries: Fix regex for detecting LIMIT clause - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5275](https://github.com/pmd/pmd/pull/5275): Use plugin-classpath to simplify javacc-wrapper.xml - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5278](https://github.com/pmd/pmd/pull/5278): \[java] CouplingBetweenObjects: improve violation message - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5279](https://github.com/pmd/pmd/pull/5279): Fix #5244: \[java] UselessOperationOnImmutable: consider java.time.* types - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦 Dependency updates
* [#5234](https://github.com/pmd/pmd/issues/5234): Bump com.google.protobuf:protobuf-java from 3.25.3 to 4.28.2
* [#5274](https://github.com/pmd/pmd/issues/5274): Bump org.junit from 5.8.2 to 5.11.2
* [#5276](https://github.com/pmd/pmd/issues/5276): Bump org.checkerframework:checker-qual from 2.11.1 to 3.48.1
* [#5280](https://github.com/pmd/pmd/issues/5280): Bump danger from 9.5.0 to 9.5.1 in the all-gems group across 1 directory
* [#5281](https://github.com/pmd/pmd/issues/5281): Bump org.scala-lang:scala-reflect from 2.13.13 to 2.13.15

### 📈 Stats
* 98 commits
* 32 closed tickets & PRs
* Days since last release: 27

## 27-September-2024 - 7.6.0

The PMD team is pleased to announce PMD 7.6.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [New Git default branch - "main"](#new-git-default-branch---main)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### New Git default branch - "main"

We are joining the Git community and updating "master" to "main". Using the term "master" for the main
development branch can be offensive to some people. Existing versions of Git have been always capable of
working with any branch name and since 2.28.0 (July 2020) the default initial branch is configurable
(`init.defaultBranch`). Since October 2020, the default branch for new repositories on GitHub
is "main". Finally, PMD will also use this new name for the main branch in all our own repositories.

Why "main"? PMD uses a very simple branching model - pull requests with feature branches and one main development
branch, from which releases are created. That's why "main" is currently the best fitting name.

More information:
- <https://sfconservancy.org/news/2020/jun/23/gitbranchname/>
- <https://github.blog/changelog/2020-10-01-the-default-branch-for-newly-created-repositories-is-now-main/>

What changes?
- We change the default branch on GitHub, so that pull requests are automatically created against `main` from
  now on.
- If you have already a local clone of PMD's repository, you'll need to rename the old master branch locally:
  ```
  git branch --move master main
  git fetch origin
  git branch --set-upstream-to=origin/main main
  git remote set-head origin --auto
  ```
  
  More info:
  <https://git-scm.com/book/en/v2/Git-Branching-Branch-Management#_changing_master> and
  <https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-branches-in-your-repository/renaming-a-branch#updating-a-local-clone-after-a-branch-name-changes>
- If you created a fork on GitHub, you'll need to change the default branch in your fork to `main` as
  well (Settings > Default Branch).
- Some time after this release, we'll delete the old master branch on GitHub. Then only `main` can be used.
- This change is expanded to the other PMD repositories as well, e.g. pmd-designer and pmd-regression-tester.

### 🐛 Fixed Issues
* apex
  * [#5138](https://github.com/pmd/pmd/issues/5138): \[apex] Various false-negatives since 7.3.0 when using triggers
    (ApexCRUDViolation, CognitiveComplexity, OperationWithLimitsInLoop)
  * [#5163](https://github.com/pmd/pmd/issues/5163): \[apex] Parser error when using toLabel in SOSL query
  * [#5182](https://github.com/pmd/pmd/issues/5182): \[apex] Parser error when using GROUPING in a SOQL query
  * [#5218](https://github.com/pmd/pmd/issues/5218): \[apex] Parser error when using nested subqueries in SOQL
  * [#5228](https://github.com/pmd/pmd/issues/5228): \[apex] Parser error when using convertCurrency() in SOQL
* core
  * [#5059](https://github.com/pmd/pmd/issues/5059): \[core] xml output doesn't escape CDATA inside its own CDATA
  * [#5201](https://github.com/pmd/pmd/issues/5201): \[core] PMD sarif schema file points to nonexistent location
  * [#5222](https://github.com/pmd/pmd/issues/5222): \[core] RuleReference/RuleSetWriter don't handle changed default property values correctly
  * [#5229](https://github.com/pmd/pmd/issues/5229): \[doc] CLI flag `--show-suppressed` needs to mention xml, html, summaryhtml
* java
  * [#5190](https://github.com/pmd/pmd/issues/5190): \[java] NPE in type inference
* java-codestyle
  * [#5046](https://github.com/pmd/pmd/issues/5046): \[java] LocalVariableCouldBeFinal false positive with try/catch
* java-errorprone
  * [#5068](https://github.com/pmd/pmd/issues/5068): \[java] MissingStaticMethodInNonInstantiatableClass: false positive with builder pattern
  * [#5207](https://github.com/pmd/pmd/issues/5207): \[java] CheckSkipResult: false positve for a private method `void skip(int)` in a subclass of FilterInputStream

### 🚨 API Changes

No changes.

### ✨ Merged pull requests
* [#5186](https://github.com/pmd/pmd/pull/5186): \[java] Cleanup things about implicit classes - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5188](https://github.com/pmd/pmd/pull/5188): \[apex] Use new apex-parser 4.2.0 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5191](https://github.com/pmd/pmd/pull/5191): \[java] Fix #5046 - FPs in LocalVariableCouldBeFinal - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5192](https://github.com/pmd/pmd/pull/5192): \[java] Fix #5190 - NPE in type inference caused by null type - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5195](https://github.com/pmd/pmd/pull/5195): \[apex] Fix various FNs when using triggers - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5202](https://github.com/pmd/pmd/pull/5202): \[core] Sarif format: refer to schemastore.org - [David Schach](https://github.com/dschach) (@dschach)
* [#5208](https://github.com/pmd/pmd/pull/5208): \[doc] Added Codety to "Tools / Integrations" - [Tony](https://github.com/random1223) (@random1223)
* [#5210](https://github.com/pmd/pmd/pull/5210): \[core] Fix PMD's XMLRenderer to escape CDATA - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5211](https://github.com/pmd/pmd/pull/5211): Change branch master to main - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5212](https://github.com/pmd/pmd/pull/5212): \[java] Adjust signature matching in CheckSkipResultRule - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5223](https://github.com/pmd/pmd/pull/5223): \[core] Fix RuleReference / RuleSetWriter handling of properties - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5224](https://github.com/pmd/pmd/pull/5224): \[java] Fix #5068: Class incorrectly identified as non-instantiatable - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5230](https://github.com/pmd/pmd/pull/5230): \[doc] Documentation update for --show-suppressed flag - [David Schach](https://github.com/dschach) (@dschach)
* [#5237](https://github.com/pmd/pmd/pull/5237): \[apex] Support convertCurrency() in SOQL/SOSL - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦 Dependency updates
* [#5185](https://github.com/pmd/pmd/issues/5185): Bump checkstyle from 10.14.0 to 10.18.1
* [#5187](https://github.com/pmd/pmd/issues/5187): Bump org.apache.maven.plugins:maven-install-plugin from 3.1.1 to 3.1.3
* [#5199](https://github.com/pmd/pmd/issues/5199): Bump org.apache.maven.plugins:maven-deploy-plugin from 3.1.1 to 3.1.3
* [#5216](https://github.com/pmd/pmd/issues/5216): Bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.20.0 to 0.23.0
* [#5226](https://github.com/pmd/pmd/issues/5226): Bump rouge from 4.3.0 to 4.4.0 in the all-gems group across 1 directory
* [#5227](https://github.com/pmd/pmd/issues/5227): Bump com.google.code.gson:gson from 2.10.1 to 2.11.0
* [#5232](https://github.com/pmd/pmd/issues/5232): Bump com.google.protobuf:protobuf-java from 3.25.3 to 3.25.5
* [#5233](https://github.com/pmd/pmd/issues/5233): Bump webrick from 1.8.1 to 1.8.2 in /docs

### 📈 Stats
* 60 commits
* 27 closed tickets & PRs
* Days since last release: 27

## 30-August-2024 - 7.5.0

The PMD team is pleased to announce PMD 7.5.0.

This is a minor release.

### Table Of Contents

* [🚀 New: Java 23 Support](#new-java-23-support)
* [🌟 New Rules](#new-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecations](#deprecations)
    * [Experimental](#experimental)
* [✨ External Contributions](#external-contributions)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New: Java 23 Support
This release of PMD brings support for Java 23. There are no new standard language features,
but a couple of preview language features:

* [JEP 455: Primitive Types in Patterns, instanceof, and switch (Preview)](https://openjdk.org/jeps/455)
* [JEP 476: Module Import Declarations (Preview)](https://openjdk.org/jeps/476)
* [JEP 477: Implicitly Declared Classes and Instance Main Methods (Third Preview)](https://openjdk.org/jeps/477)
* [JEP 482: Flexible Constructor Bodies (Second Preview)](https://openjdk.org/jeps/482)

Note that String Templates (introduced as preview in Java 21 and 22) are not supported anymore in Java 23,
see [JDK-8329949](https://bugs.openjdk.org/browse/JDK-8329949) for details.

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `23-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-23-preview ...

Note: Support for Java 21 preview language features have been removed. The version "21-preview"
are no longer available.

### 🌟 New Rules
* The new Java rule [`AvoidSynchronizedStatement`](https://docs.pmd-code.org/pmd-doc-7.5.0/pmd_rules_java_multithreading.html#avoidsynchronizedstatement) finds synchronization blocks that
  could cause performance issues with virtual threads due to pinning.
* The new JavaScript rule [`AvoidConsoleStatements`](https://docs.pmd-code.org/pmd-doc-7.5.0/pmd_rules_ecmascript_performance.html#avoidconsolestatements) finds any function calls
  on the Console API (e.g. `console.log`). Using these in production code might negatively impact performance.

### 🐛 Fixed Issues
* apex-performance
  * [#5139](https://github.com/pmd/pmd/issues/5139): \[apex] OperationWithHighCostInLoop: false negative for triggers
* java
  * [#5062](https://github.com/pmd/pmd/issues/5062): \[java] Support Java 23
  * [#5167](https://github.com/pmd/pmd/issues/5167): \[java] java.lang.IllegalArgumentException: \<?\> cannot be a wildcard bound
* java-bestpractices
  * [#3602](https://github.com/pmd/pmd/issues/3602): \[java] GuardLogStatement: False positive when compile-time constant is created from external constants
  * [#4731](https://github.com/pmd/pmd/issues/4731): \[java] GuardLogStatement: Documentation is unclear why getters are flagged
  * [#5145](https://github.com/pmd/pmd/issues/5145): \[java] UnusedPrivateMethod: False positive with method calls inside lambda
  * [#5151](https://github.com/pmd/pmd/issues/5151): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is a constant from another class
  * [#5152](https://github.com/pmd/pmd/issues/5152): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is "this"
  * [#5153](https://github.com/pmd/pmd/issues/5153): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is an array element
* java-design
  * [#5048](https://github.com/pmd/pmd/issues/5084): \[java] CognitiveComplexity: Exception when using Map.of()
  * [#5162](https://github.com/pmd/pmd/issues/5162): \[java] SingularField: False-positive when preceded by synchronized block
* java-multithreading
  * [#5175](https://github.com/pmd/pmd/issues/5175): \[java] Update AvoidSynchronizedAtMethodLevel message to mention ReentrantLock, new rule AvoidSynchronizedStatement
* javascript-performance
  * [#5105](https://github.com/pmd/pmd/issues/5105): \[javascript] Prohibit any console methods
* plsql
  * [#5125](https://github.com/pmd/pmd/pull/5125): \[plsql] Improve merge statement (order of merge insert/update flexible, allow prefixes in column names)
* plsql-bestpractices
  * [#5132](https://github.com/pmd/pmd/issues/5132): \[plsql] TomKytesDespair: XPathException for more complex exception handler

### 🚨 API Changes
#### Deprecations
* pmd-jsp
  * <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/7.5.0/net/sourceforge/pmd/lang/jsp/ast/JspParserImpl.html#"><code>JspParserImpl</code></a> is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-plsql
  * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.5.0/net/sourceforge/pmd/lang/plsql/ast/PLSQLParserImpl.html#MergeUpdateClausePrefix()"><code>MergeUpdateClausePrefix</code></a> is deprecated. This production is
    not used anymore and will be removed. Note: The whole parser implementation class has been deprecated since 7.3.0,
    as it is supposed to be internalized.
* pmd-velocity
  * <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.5.0/net/sourceforge/pmd/lang/velocity/ast/VtlParserImpl.html#"><code>VtlParserImpl</code></a> is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-visualforce
  * <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.5.0/net/sourceforge/pmd/lang/visualforce/ast/VfParserImpl.html#"><code>VfParserImpl</code></a> is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.

#### Experimental
* pmd-java
  * Renamed `isUnnamedClass()` to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.5.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#isSimpleCompilationUnit()"><code>ASTCompilationUnit#isSimpleCompilationUnit</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.5.0/net/sourceforge/pmd/lang/java/ast/ASTImplicitClassDeclaration.html#"><code>ASTImplicitClassDeclaration</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.5.0/net/sourceforge/pmd/lang/java/ast/ASTImportDeclaration.html#isModuleImport()"><code>ASTImportDeclaration#isModuleImport</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.5.0/net/sourceforge/pmd/lang/java/ast/JavaVisitorBase.html#visit(net.sourceforge.pmd.lang.java.ast.ASTImplicitClassDeclaration,P)"><code>JavaVisitorBase#visit(ASTImplicitClassDeclaration, P)</code></a>

### ✨ External Contributions
* [#5125](https://github.com/pmd/pmd/pull/5125): \[plsql] Improve merge statement (order of merge insert/update flexible, allow prefixes in column names) - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5175](https://github.com/pmd/pmd/pull/5175): \[java] Update AvoidSynchronizedAtMethodLevel message to mention ReentrantLock, new rule AvoidSynchronizedStatement - [Chas Honton](https://github.com/chonton) (@chonton)

### 📦 Dependency updates
* [#5100](https://github.com/pmd/pmd/issues/5100): Enable Dependabot
* [#5141](https://github.com/pmd/pmd/issues/5141): Bump org.apache.maven.plugins:maven-checkstyle-plugin from 3.3.1 to 3.4.0
* [#5142](https://github.com/pmd/pmd/issues/5142): Bump org.apache.maven.plugins:maven-compiler-plugin from 3.12.1 to 3.13.0
* [#5144](https://github.com/pmd/pmd/issues/5144): Bump org.codehaus.mojo:versions-maven-plugin from 2.16.2 to 2.17.1
* [#5148](https://github.com/pmd/pmd/issues/5148): Bump org.apache.commons:commons-text from 1.11.0 to 1.12.0
* [#5149](https://github.com/pmd/pmd/issues/5149): Bump org.apache.maven.plugins:maven-site-plugin from 4.0.0-M13 to 4.0.0-M16
* [#5160](https://github.com/pmd/pmd/issues/5160): Bump org.pcollections:pcollections from 3.2.0 to 4.0.2
* [#5161](https://github.com/pmd/pmd/issues/5161): Bump danger from 9.4.3 to 9.5.0 in the all-gems group across 1 directory
* [#5164](https://github.com/pmd/pmd/issues/5164): Bump org.apache.maven.plugins:maven-dependency-plugin from 3.6.1 to 3.7.1
* [#5165](https://github.com/pmd/pmd/issues/5165): Bump the all-gems group across 1 directory with 2 updates
* [#5171](https://github.com/pmd/pmd/issues/5171): Bump net.bytebuddy:byte-buddy-agent from 1.14.12 to 1.14.19
* [#5180](https://github.com/pmd/pmd/issues/5180): Bump net.sf.saxon:Saxon-HE from 12.4 to 12.5

### 📈 Stats
* 87 commits
* 25 closed tickets & PRs
* Days since last release: 35

## 26-July-2024 - 7.4.0

The PMD team is pleased to announce PMD 7.4.0.

This is a minor release.

### Table Of Contents

* [🌟 New and changed rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed rules](#changed-rules)
    * [Renamed rules](#renamed-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
* [✨ External Contributions](#external-contributions)
* [📈 Stats](#stats)

### 🌟 New and changed rules

#### New Rules
* The new Apex rule [`AvoidNonRestrictiveQueries`](https://docs.pmd-code.org/pmd-doc-7.4.0/pmd_rules_apex_performance.html#avoidnonrestrictivequeries) finds SOQL and SOSL queries without a where
  or limit statement. This can quickly cause governor limit exceptions.

#### Changed rules
* [`ClassNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.4.0/pmd_rules_apex_codestyle.html#classnamingconventions) (Apex Codestyle): Two new properties to configure different patterns
  for inner classes and interfaces: `innerClassPattern` and `innerInterfacePattern`.

#### Renamed rules
* [`InaccurateNumericLiteral`](https://docs.pmd-code.org/pmd-doc-7.4.0/pmd_rules_ecmascript_errorprone.html#inaccuratenumericliteral) (JavaScript Error Prone) has been renamed from `InnaccurateNumericLiteral`.
  The old rule name still works but is deprecated.

### 🐛 Fixed Issues
* apex
  * [#5094](https://github.com/pmd/pmd/issues/5094): \[apex] "No adapter exists for type" error message printed to stdout instead of stderr
* apex-bestpractices
  * [#5095](https://github.com/pmd/pmd/issues/5095): \[apex] ApexUnitTestShouldNotUseSeeAllDataTrue false negative due to casing (regression in PMD 7)
* apex-codestyle
  * [#4800](https://github.com/pmd/pmd/issues/4800): \[apex] ClassNamingConvention: Support naming convention for *inner* classes
* apex-performance
  * [#635](https://github.com/pmd/pmd/issues/635): \[apex] New Rule: Avoid soql/sosl queries without a where clause or limit statement
* java-bestpractices
  * [#5106](https://github.com/pmd/pmd/issues/5106): \[java] AccessorClassGeneration: Node was null for default constructor
  * [#5110](https://github.com/pmd/pmd/issues/5110): \[java] UnusedPrivateMethod for method referenced by lombok.Builder.ObtainVia
  * [#5117](https://github.com/pmd/pmd/issues/5117): \[java] UnusedPrivateMethod for methods annotated with jakarta.annotation.PostConstruct or PreDestroy
* java-errorprone
  * [#1488](https://github.com/pmd/pmd/issues/1488): \[java] MissingStaticMethodInNonInstantiatableClass: False positive with Lombok Builder on Constructor
* javascript-errorprone
  * [#2367](https://github.com/pmd/pmd/issues/2367): \[javascript] InnaccurateNumericLiteral is misspelled
  * [#4716](https://github.com/pmd/pmd/issues/4716): \[javascript] InaccurateNumericLiteral with number 259200000
* plsql
  * [#5086](https://github.com/pmd/pmd/pull/5086): \[plsql] Fixed issue with missing optional table alias in MERGE usage
  * [#5087](https://github.com/pmd/pmd/pull/5087): \[plsql] Add support for SQL_MACRO
  * [#5088](https://github.com/pmd/pmd/pull/5088): \[plsql] Add support for 'DEFAULT' clause on the arguments of some oracle functions
  * [#5133](https://github.com/pmd/pmd/issues/5133): \[plsql] AssertionError: Root of the tree should implement RootNode for a PL/SQL type declaration
* cli
  * [#5120](https://github.com/pmd/pmd/issues/5120): \[cli] Can't start designer under Windows
* core
  * [#5091](https://github.com/pmd/pmd/issues/5091): \[core] PMD CPD v7.3.0 gives deprecation warning for skipLexicalErrors even when not used

### 🚨 API Changes
* javascript
  * The old rule name `InnaccurateNumericLiteral` has been deprecated. Use the new name
    [`InaccurateNumericLiteral`](https://docs.pmd-code.org/pmd-doc-7.4.0/pmd_rules_ecmascript_errorprone.html#inaccuratenumericliteral) instead.

### ✨ External Contributions
* [#5048](https://github.com/pmd/pmd/pull/5048): \[apex] Added Inner Classes to Apex Class Naming Conventions Rule - [Justin Stroud](https://github.com/justinstroudbah) (@justinstroudbah / @sgnl-labs)
* [#5086](https://github.com/pmd/pmd/pull/5086): \[plsql] Fixed issue with missing optional table alias in MERGE usage - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5087](https://github.com/pmd/pmd/pull/5087): \[plsql] Add support for SQL_MACRO - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5088](https://github.com/pmd/pmd/pull/5088): \[plsql] Add support for 'DEFAULT' clause on the arguments of some oracle functions - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5107](https://github.com/pmd/pmd/pull/5107): \[doc] Update maven.md - Typo fixed for maven target - [karthikaiyasamy](https://github.com/karthikaiyasamy) (@karthikaiyasamy)
* [#5109](https://github.com/pmd/pmd/pull/5109): \[java] Exclude constructor with lombok.Builder for MissingStaticMethodInNonInstantiatableClass - [Krzysztof Debski](https://github.com/kdebski85) (@kdebski85)
* [#5111](https://github.com/pmd/pmd/pull/5111): \[java] Fix UnusedPrivateMethod for @<!-- -->lombok.Builder.ObtainVia - [Krzysztof Debski](https://github.com/kdebski85) (@kdebski85)
* [#5118](https://github.com/pmd/pmd/pull/5118): \[java] FP for UnusedPrivateMethod with Jakarta @<!-- -->PostConstruct/PreDestroy annotations - [Krzysztof Debski](https://github.com/kdebski85) (@kdebski85)
* [#5121](https://github.com/pmd/pmd/pull/5121): \[plsql] Fixed issue with missing optional table alias in MERGE usage - [Arjen Duursma](https://github.com/duursma) (@duursma)

### 📈 Stats
* 81 commits
* 32 closed tickets & PRs
* Days since last release: 27



## 28-June-2024 - 7.3.0

The PMD team is pleased to announce PMD 7.3.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [✨ New Rules](#new-rules)
    * [💥 pmd-compat6 removed (breaking)](#pmd-compat6-removed-breaking)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [CPD Report Format XML](#cpd-report-format-xml)
    * [CLI](#cli)
    * [Ant](#ant)
    * [Deprecated API](#deprecated-api)
    * [Breaking changes: pmd-compat6 removed](#breaking-changes-pmd-compat6-removed)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### ✨ New Rules

* The new Java rule [`UseEnumCollections`](https://docs.pmd-code.org/pmd-doc-7.3.0/pmd_rules_java_bestpractices.html#useenumcollections) reports usages for `HashSet` and `HashMap`
  when the keys are of an enum type. The specialized enum collections are more space- and time-efficient.

#### 💥 pmd-compat6 removed (breaking)

The already deprecated PMD 6 compatibility module (pmd-compat6) has been removed. It was intended to be used with
older versions of the maven-pmd-plugin, but since maven-pmd-plugin 3.22.0, PMD 7 is supported directly and this
module is not needed anymore.

If you currently use this dependency (`net.sourceforge.pmd:pmd-compat6`), remove it and upgrade maven-pmd-plugin
to the latest version (3.23.0 or newer).

See also [Maven PMD Plugin](https://docs.pmd-code.org/pmd-doc-7.3.0/pmd_userdocs_tools_maven.html).

### 🐛 Fixed Issues

* cli
  * [#2827](https://github.com/pmd/pmd/issues/2827): \[cli] Consider processing errors in exit status
* core
  * [#4396](https://github.com/pmd/pmd/issues/4396): \[core] CPD is always case sensitive
  * [#4992](https://github.com/pmd/pmd/pull/4992): \[core] CPD: Include processing errors in XML report
  * [#5066](https://github.com/pmd/pmd/issues/5066): \[core] CPD throws java.lang.OutOfMemoryError: Java heap space (since 7.1.0)
* apex
  * [#4922](https://github.com/pmd/pmd/issues/4922): \[apex] SOQL syntax error with TYPEOF in sub-query
  * [#5053](https://github.com/pmd/pmd/issues/5053): \[apex] CPD fails to parse string literals with escaped characters
  * [#5055](https://github.com/pmd/pmd/issues/5055): \[apex] SOSL syntax error with WITH USER_MODE or WITH SYSTEM_MODE
* apex-bestpractices
  * [#5000](https://github.com/pmd/pmd/issues/5000): \[apex] UnusedLocalVariable FP with binds in SOSL / SOQL
* java
  * [#4885](https://github.com/pmd/pmd/issues/4885): \[java] AssertionError: Method should be accessible
  * [#5050](https://github.com/pmd/pmd/issues/5050): \[java] Problems with pattern variables in switch branches
* java-bestpractices
  * [#577](https://github.com/pmd/pmd/issues/577): \[java] New Rule: Check that Map<K,V> is an EnumMap if K is an enum value
  * [#5047](https://github.com/pmd/pmd/issues/5047): \[java] UnusedPrivateMethod FP for Generics & Overloads
* plsql
  * [#1934](https://github.com/pmd/pmd/issues/1934): \[plsql] ParseException with MERGE statement in anonymous block
  * [#2779](https://github.com/pmd/pmd/issues/2779): \[plsql] Error while parsing statement with (Oracle) DML Error Logging
  * [#4270](https://github.com/pmd/pmd/issues/4270): \[plsql] Parsing exception COMPOUND TRIGGER with EXCEPTION handler

### 🚨 API Changes

#### CPD Report Format XML

There are some important changes:

1. The XML format will now use an XSD schema, that is available at <https://pmd.github.io/schema/cpd-report_1_0_0.xsd>.
   This schema defines the valid elements and attributes that one can expect from a CPD report.
2. The root element `pmd-cpd` contains the new attributes `pmdVersion`, `timestamp` and `version`. The latter is
   the schema version and is currently "1.0.0".
3. The CPD XML report will now also contain recoverable errors as additional `<error>` elements.

See [Report formats for CPD](pmd_userdocs_cpd_report_formats.html#xml) for an example.

The XML format should be compatible as only attributes and elements have been added. However, if you parse
the document with a namespace aware parser, you might encounter some issues like no elements being found.
In case the new format doesn't work for you (e.g. namespaces, unexpected error elements), you can
go back using the old format with the renderer "xmlold" (<a href="https://docs.pmd-code.org/apidocs/pmd-core/7.3.0/net/sourceforge/pmd/cpd/XMLOldRenderer.html#"><code>XMLOldRenderer</code></a>). Note, that
this old renderer is deprecated and only there for compatibility reasons. Whatever tooling is used to
read the XML format should be updated.

#### CLI

* New exit code 5 introduced. PMD and CPD will exit now by default with exit code 5, if any recoverable error
  (e.g. parsing exception, lexing exception or rule exception) occurred. PMD will still create a report with
  all detected violations or duplications if recoverable errors occurred. Such errors mean, that the report
  might be incomplete, as either violations or duplications for an entire file or for a specific rule are missing.
  These cases can be considered as false-negatives.

  In any case, the root cause should be investigated. If it's a problem in PMD itself, please create a bug report.

* New CLI parameter `--no-fail-on-error` to ignore such errors and not exit with code 5. By default,
  a build with errors will now fail and with that parameter, the previous behavior can be restored.
  This parameter is available for both PMD and CPD.

* The CLI parameter `--skip-lexical-errors` is deprecated. By default, lexical errors are skipped but the
  build is failed. Use the new parameter `--[no-]fail-on-error` instead to control whether to fail the build or not.

#### Ant

* CPDTask has a new parameter `failOnError`. It controls, whether to fail the build if any recoverable error occurred.
  By default, the build will fail. CPD will still create a report with all detected duplications, but the report might
  be incomplete.
* The parameter `skipLexicalError` in CPDTask is deprecated and ignored. Lexical errors are now always skipped.
  Use the new parameter `failOnError` instead to control whether to fail the build or not.

#### Deprecated API

* pmd-ant
  * <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.3.0/net/sourceforge/pmd/ant/CPDTask.html#setSkipLexicalErrors(boolean)"><code>CPDTask#setSkipLexicalErrors</code></a>: Use <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.3.0/net/sourceforge/pmd/ant/CPDTask.html#setFailOnError(boolean)"><code>setFailOnError</code></a>
  instead to control, whether to ignore errors or fail the build.
* pmd-core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.3.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#isSkipLexicalErrors()"><code>CPDConfiguration#isSkipLexicalErrors</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.3.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#setSkipLexicalErrors(boolean)"><code>setSkipLexicalErrors</code></a>:
  Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.3.0/net/sourceforge/pmd/AbstractConfiguration.html#setFailOnError(boolean)"><code>setFailOnError</code></a> to control whether to ignore errors or fail the build.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.3.0/net/sourceforge/pmd/cpd/XMLOldRenderer.html#"><code>net.sourceforge.pmd.cpd.XMLOldRenderer</code></a> (the CPD format "xmlold").
  * The constructor
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.3.0/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrToken.html#AntlrToken(org.antlr.v4.runtime.Token,net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken,net.sourceforge.pmd.lang.document.TextDocument)"><code>AntlrToken#AntlrToken</code></a>
    shouldn't be used directly. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.3.0/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrTokenManager.html#"><code>AntlrTokenManager</code></a> instead.
* pmd-java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.3.0/net/sourceforge/pmd/lang/java/ast/ASTResource.html#getStableName()"><code>ASTResource#getStableName</code></a> and the corresponding attribute `@StableName`.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.3.0/net/sourceforge/pmd/lang/java/ast/ASTRecordPattern.html#getVarId()"><code>ASTRecordPattern#getVarId</code></a> This method was added here by mistake. Record
    patterns don't declare a pattern variable for the whole pattern, but rather for individual record
    components, which can be accessed via <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.3.0/net/sourceforge/pmd/lang/java/ast/ASTRecordPattern.html#getComponentPatterns()"><code>getComponentPatterns</code></a>.
* pmd-plsql
  * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.3.0/net/sourceforge/pmd/lang/plsql/ast/PLSQLParserImpl.html#"><code>PLSQLParserImpl</code></a> is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
  * The node <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.3.0/net/sourceforge/pmd/lang/plsql/ast/ASTKEYWORD_UNRESERVED.html#"><code>ASTKEYWORD_UNRESERVED</code></a> is deprecated and is now removed from the AST.

#### Breaking changes: pmd-compat6 removed

The already deprecated PMD 6 compatibility module (pmd-compat6) has been removed.
See above for details.

### 📈 Stats
* 88 commits
* 32 closed tickets & PRs
* Days since last release: 27

## 31-May-2024 - 7.2.0

The PMD team is pleased to announce PMD 7.2.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [Collections exposed as XPath attributes](#collections-exposed-as-xpath-attributes)
    * [Updated PMD Designer](#updated-pmd-designer)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
* [✨ External Contributions](#external-contributions)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### Collections exposed as XPath attributes

Up to now, all AST node getters would be exposed to XPath, as long as the return type was a primitive (boxed or unboxed), String or Enum. That meant that collections, even of these basic types, were not exposed, so for instance accessing Apex's `ASTUserClass.getInterfaceNames()` to list the interfaces implemented by a class was impossible from XPath, and would require writing a Java rule to check it.

Since this release, PMD will also expose any getter returning a collection of any supported type as a sequence through an XPath attribute. They would require to use apropriate XQuery functions to manipulate the sequence. So for instance, to detect any given `ASTUserClass` in Apex that implements `Queueable`, it is now possible to write:

```xml
/UserClass[@InterfaceNames = 'Queueable']
```

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog (7.2.0)](https://github.com/pmd/pmd-designer/releases/tag/7.2.0).

### 🐛 Fixed Issues
* core
  * [#4467](https://github.com/pmd/pmd/issues/4467): \[core] Expose collections from getters as XPath sequence attributes
  * [#4978](https://github.com/pmd/pmd/issues/4978): \[core] Referenced Rulesets do not emit details on validation errors
  * [#4983](https://github.com/pmd/pmd/pull/4983): \[cpd] Fix CPD crashes about unicode escapes
  * [#5009](https://github.com/pmd/pmd/issues/5009): \[core] Kotest tests aren't picked up by surefire
* java
  * [#4912](https://github.com/pmd/pmd/issues/4912): \[java] Unable to parse some Java9+ resource references
  * [#4973](https://github.com/pmd/pmd/pull/4973): \[java] Stop parsing Java for CPD
  * [#4980](https://github.com/pmd/pmd/issues/4980): \[java] Bad intersection, unrelated class types java.lang.Object\[] and java.lang.Number
  * [#4988](https://github.com/pmd/pmd/pull/4988): \[java] Fix impl of ASTVariableId::isResourceDeclaration / VariableId/@<!-- -->ResourceDeclaration
  * [#4990](https://github.com/pmd/pmd/issues/4990): \[java] Add an attribute @<!-- -->PackageQualifier to ASTClassType
  * [#5006](https://github.com/pmd/pmd/issues/5006): \[java] Bad intersection, unrelated class types Child and Parent<? extends Child>
  * [#5029](https://github.com/pmd/pmd/issues/5029): \[java] PMD 7.x throws stack overflow in TypeOps$ProjectionVisitor while parsing a Java class
* java-bestpractices
  * [#4278](https://github.com/pmd/pmd/issues/4278): \[java] UnusedPrivateMethod FP with Junit 5 @MethodSource and default factory method name
  * [#4852](https://github.com/pmd/pmd/issues/4852): \[java] ReplaceVectorWithList false-positive (neither Vector nor List usage) 
  * [#4975](https://github.com/pmd/pmd/issues/4975): \[java] UnusedPrivateMethod false positive when using @MethodSource on a @Nested test
  * [#4985](https://github.com/pmd/pmd/issues/4985): \[java] UnusedPrivateMethod false-positive / method reference in combination with custom object
* java-codestyle
  * [#1619](https://github.com/pmd/pmd/issues/1619): \[java] LocalVariableCouldBeFinal on 'size' variable in for loop
  * [#3122](https://github.com/pmd/pmd/issues/3122): \[java] LocalVariableCouldBeFinal should consider blank local variables
  * [#4903](https://github.com/pmd/pmd/issues/4903): \[java] UnnecessaryBoxing, but explicit conversion is necessary
  * [#4924](https://github.com/pmd/pmd/issues/4924): \[java] UnnecessaryBoxing false positive in PMD 7.0.0 in lambda
  * [#4930](https://github.com/pmd/pmd/issues/4930): \[java] EmptyControlStatement should not allow empty try with concise resources
  * [#4954](https://github.com/pmd/pmd/issues/4954): \[java] LocalVariableNamingConventions should allow unnamed variables by default
  * [#5028](https://github.com/pmd/pmd/issues/5028): \[java] FormalParameterNamingConventions should accept unnamed parameters by default
* java-errorprone
  * [#4042](https://github.com/pmd/pmd/issues/4042): \[java] A false negative about the rule StringBufferInstantiationWithChar
  * [#5007](https://github.com/pmd/pmd/issues/5007): \[java] AvoidUsingOctalValues triggers on non-octal double literals with a leading 0
* java-multithreading
  * [#2368](https://github.com/pmd/pmd/issues/2368): \[java] False positive UnsynchronizedStaticFormatter in static initializer

### 🚨 API Changes

#### Deprecated API

* pmd-java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.2.0/net/sourceforge/pmd/lang/java/ast/ASTResource.html#getStableName()"><code>ASTResource#getStableName</code></a> and the corresponding attribute `@StableName`

### ✨ External Contributions

* [#5020](https://github.com/pmd/pmd/issues/5020): \[java] Fix AvoidUsingOctalValues false-positive - [Gold856](https://github.com/Gold856) (@Gold856)

### 📈 Stats
* 152 commits
* 46 closed tickets & PRs
* Days since last release: 35

## 26-April-2024 - 7.1.0

The PMD team is pleased to announce PMD 7.1.0.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [More robust CPD reports](#more-robust-cpd-reports)
    * [✨ New Rules](#new-rules)
    * [🌟 Rule Changes](#rule-changes)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecated methods](#deprecated-methods)
* [✨ External Contributions](#external-contributions)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### More robust CPD reports
There were a number of circumstances, specially around (but not limited to) literal sequences, were CPD would
report duplicate overlapping or partially overlapping matches. These have now been fixed, and CPD will report
only the longest non-overlapping duplicate.

These improvements apply to all supported languages, irrespective of supported flags.

#### ✨ New Rules
- The new Java rule [`UnnecessaryVarargsArrayCreation`](https://docs.pmd-code.org/pmd-doc-7.1.0/pmd_rules_java_bestpractices.html#unnecessaryvarargsarraycreation) reports explicit array creation
  when a varargs is expected. This is more heavy to read and could be simplified.
- The new Java rule [`ConfusingArgumentToVarargsMethod`](https://docs.pmd-code.org/pmd-doc-7.1.0/pmd_rules_java_errorprone.html#confusingargumenttovarargsmethod) reports some confusing situations
  where a varargs method is called with an inexact argument type. These may end up in a mismatch between the expected
  parameter type and the actual value.
- The new Java rule [`LambdaCanBeMethodReference`](https://docs.pmd-code.org/pmd-doc-7.1.0/pmd_rules_java_codestyle.html#lambdacanbemethodreference) reports lambda expressions that can be replaced
  with a method reference. Please read the documentation of the rule for more info. This rule is now part of the Quickstart
  ruleset.

#### 🌟 Rule Changes
* [`JUnitTestsShouldIncludeAssert`](https://docs.pmd-code.org/pmd-doc-7.1.0/pmd_rules_java_bestpractices.html#junittestsshouldincludeassert) and [`JUnitTestContainsTooManyAsserts`](https://docs.pmd-code.org/pmd-doc-7.1.0/pmd_rules_java_bestpractices.html#junittestcontainstoomanyasserts)
  have a new property named `extraAssertMethodNames`. With this property, you can configure which additional static
  methods should be considered as valid verification methods. This allows to use custom mocking or assertion libraries.

### 🐛 Fixed Issues
* core
  * [#494](https://github.com/pmd/pmd/issues/494): \[core] Adopt JApiCmp to enforce control over API changes
  * [#4942](https://github.com/pmd/pmd/issues/4942): \[core] CPD: `--skip-duplicate-files` has no effect (7.0.0 regression)
  * [#4959](https://github.com/pmd/pmd/pull/4959): \[core] Upgrade saxon to 12.4
* cli
  * [#4791](https://github.com/pmd/pmd/issues/4791): \[cli] Could not find or load main class
  * [#4913](https://github.com/pmd/pmd/issues/4913): \[cli] cpd-gui closes immediately
* doc
  * [#4901](https://github.com/pmd/pmd/issues/4901): \[doc] Improve documentation on usage of violationSuppressXPath
* apex
  * [#4418](https://github.com/pmd/pmd/issues/4418): \[apex] ASTAnnotation.getImage() does not return value as written in the class
* apex-errorprone
  * [#3953](https://github.com/pmd/pmd/issues/3953): \[apex] EmptyCatchBlock false positive with formal (doc) comments
* cpp
  * [#2438](https://github.com/pmd/pmd/issues/2438): \[cpp] Repeated Duplication blocks
* java
  * [#4899](https://github.com/pmd/pmd/issues/4899): \[java] Parsing failed in ParseLock#doParse() java.io.IOException: Stream closed
  * [#4902](https://github.com/pmd/pmd/issues/4902): \[java] "Bad intersection, unrelated class types" for Constable\[] and Enum\[]
  * [#4947](https://github.com/pmd/pmd/issues/4947): \[java] Broken TextBlock parser
* java-bestpractices
  * [#1084](https://github.com/pmd/pmd/issues/1084): \[java] Allow JUnitTestsShouldIncludeAssert to configure verification methods
  * [#3216](https://github.com/pmd/pmd/issues/3216): \[java] New rule: UnnecessaryVarargsArrayCreation
  * [#4435](https://github.com/pmd/pmd/issues/4435): \[java] \[7.0-rc1] UnusedAssignment for used field
  * [#4569](https://github.com/pmd/pmd/issues/4569): \[java] ForLoopCanBeForeach reports on loop `for (int i = 0; i < list.size(); i += 2)`
  * [#4618](https://github.com/pmd/pmd/issues/4618): \[java] UnusedAssignment false positive with conditional assignments of fields
* java-codestyle
  * [#4602](https://github.com/pmd/pmd/issues/4602): \[java] UnnecessaryImport: false positives with static imports
  * [#4785](https://github.com/pmd/pmd/issues/4785): \[java] False Positive: PMD Incorrectly report violation for UnnecessaryImport
  * [#4779](https://github.com/pmd/pmd/issues/4779): \[java] Examples in documentation of MethodArgumentCanBeFinal do not trigger the rule
  * [#4881](https://github.com/pmd/pmd/issues/4881): \[java] ClassNamingConventions: interfaces are identified as abstract classes (regression in 7.0.0)
* java-design
  * [#2440](https://github.com/pmd/pmd/issues/2440): \[java] FinalFieldCouldBeStatic FN when the right side of the assignment is a constant expression
  * [#3694](https://github.com/pmd/pmd/issues/3694): \[java] SingularField ignores static variables
  * [#4873](https://github.com/pmd/pmd/issues/4873): \[java] AvoidCatchingGenericException: Can no longer suppress on the exception itself
* java-errorprone
  * [#2056](https://github.com/pmd/pmd/issues/2056): \[java] CloseResource false-positive with URLClassLoader in cast expression
  * [#4751](https://github.com/pmd/pmd/issues/4751): \[java] PMD crashes when analyzing CloseResource Rule
  * [#4928](https://github.com/pmd/pmd/issues/4928): \[java] EmptyCatchBlock false negative when allowCommentedBlocks=true
  * [#4948](https://github.com/pmd/pmd/issues/4948): \[java] ImplicitSwitchFallThrough: False-positive with nested switch statements
* java-performance
  * [#3845](https://github.com/pmd/pmd/issues/3845): \[java] InsufficientStringBufferDeclaration should consider literal expression
  * [#4874](https://github.com/pmd/pmd/issues/4874): \[java] StringInstantiation: False-positive when using `new String(charArray)`
  * [#4886](https://github.com/pmd/pmd/issues/4886): \[java] BigIntegerInstantiation: False Positive with Java 17 and BigDecimal.TWO
* pom-errorprone
  * [#4388](https://github.com/pmd/pmd/issues/4388): \[pom] InvalidDependencyTypes doesn't consider dependencies at all
* misc
  * [#4967](https://github.com/pmd/pmd/pull/4967): Fix reproducible build issues with 7.0.0

### 🚨 API Changes
#### Deprecated methods
* pmd-java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.1.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#getBlock()"><code>ASTLambdaExpression#getBlock</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.1.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#getExpression()"><code>ASTLambdaExpression#getExpression</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.1.0/net/sourceforge/pmd/lang/java/rule/design/SingularFieldRule.html#mayBeSingular(net.sourceforge.pmd.lang.java.ast.ModifierOwner)"><code>SingularFieldRule#mayBeSingular</code></a> has been deprecated for
    removal. The method is only useful for the rule itself and shouldn't be used otherwise.

### ✨ External Contributions
* [#4864](https://github.com/pmd/pmd/pull/4864): Fix #1084 \[Java] add extra assert method names to Junit rules - [Erwan Moutymbo](https://github.com/emouty) (@emouty)
* [#4894](https://github.com/pmd/pmd/pull/4894): Fix #4791 Error caused by space in JDK path - [Scrates1](https://github.com/Scrates1) (@Scrates1)

### 📈 Stats
* 205 commits
* 71 closed tickets & PRs
* Days since last release: 34



## 22-March-2024 - 7.0.0

🎉 After a long time, we're excited to bring you now the next major version of PMD! 🎉

Since this is a big release, we provide here only a concise version of the release notes. We prepared a separate
page with the full [Detailed Release Notes for PMD 7.0.0](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html).

🤝🙏 Many thanks to all users and contributors who were testing the release candidates and
provided feedback and/or PRs!

✨ PMD 7...

* ...has a new logo
* ...analyzes Java 21 and Java 22 projects with even better type resolution and symbol table support
* ...analyzes Kotlin and Swift
* ...analyzes Apex with a new parser
* ...finds duplicated code in Coco, Julia, TypeScript
* ...ships 11 new rules and tons of improvements for existing rules
* ...provides a new CLI interface with progress bar
* ...supports Antlr based languages
* ...and many more enhancements

💥 Note: Since PMD 7 is a major release, it is not a drop-in replacement for PMD 6.55.0.
A detailed documentation of required changes are available in the [Migration Guide for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_userdocs_migrating_to_pmd7.html).


<details markdown="block" class="no-background">
<summary markdown="span">Expand to see Release Notes
</summary>


### Table Of Contents

* [Changes since 7.0.0-rc4](#changes-since-700-rc4)
    * [New and Noteworthy](#new-and-noteworthy)
        * [Maven PMD Plugin compatibility with PMD 7](#maven-pmd-plugin-compatibility-with-pmd-7)
        * [Java 22 Support](#java-22-support)
        * [Swift Support](#swift-support)
        * [Groovy Support (CPD)](#groovy-support-cpd)
        * [Updated PMD Designer](#updated-pmd-designer)
        * [Apex Support: Replaced Jorje with fully open source front-end](#apex-support-replaced-jorje-with-fully-open-source-front-end)
        * [Changed: Visualforce](#changed-visualforce)
        * [Changed: HTML support](#changed-html-support)
        * [Changed: Kotlin support](#changed-kotlin-support)
        * [Changed: Velocity Template Language (VTL)](#changed-velocity-template-language-vtl)
    * [Rule Changes](#rule-changes)
    * [Fixed issues](#fixed-issues)
    * [API Changes](#api-changes)
    * [External Contributions](#external-contributions)
* [🚀 Major Features and Enhancements](#major-features-and-enhancements)
    * [New official logo](#new-official-logo)
    * [Revamped Java module](#revamped-java-module)
    * [Revamped Command Line Interface](#revamped-command-line-interface)
    * [Full Antlr support](#full-antlr-support)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [New CPD report format cpdhtml-v2.xslt](#new-cpd-report-format-cpdhtml-v2xslt)
* [🎉 Language Related Changes](#language-related-changes)
    * [New: CPD support for Apache Velocity Template Language (VTL)](#new-cpd-support-for-apache-velocity-template-language-vtl)
    * [New: CPD support for Coco](#new-cpd-support-for-coco)
    * [New: CPD support for Julia](#new-cpd-support-for-julia)
    * [New: CPD support for TypeScript](#new-cpd-support-for-typescript)
    * [New: Java 21 and 22 Support](#new-java-21-and-22-support)
    * [New: Kotlin support](#new-kotlin-support)
    * [New: Swift support](#new-swift-support)
    * [Changed: Apex Support: Replaced Jorje with fully open source front-end](#changed-apex-support-replaced-jorje-with-fully-open-source-front-end)
    * [Changed: CPP can now ignore identifiers in sequences (CPD)](#changed-cpp-can-now-ignore-identifiers-in-sequences-cpd)
    * [Changed: Groovy Support (CPD)](#changed-groovy-support-cpd)
    * [Changed: HTML support](#changed-html-support)
    * [Changed: JavaScript support](#changed-javascript-support)
    * [Changed: Language versions](#changed-language-versions)
    * [Changed: Rule properties](#changed-rule-properties)
    * [Changed: Velocity Template Language (VTL)](#changed-velocity-template-language-vtl)
    * [Changed: Visualforce](#changed-visualforce)
* [🌟 New and changed rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Other changes](#other-changes)
* [🚨 API](#api)
* [💥 Compatibility and migration notes](#compatibility-and-migration-notes)
* [🐛 Fixed Issues](#fixed-issues)
* [✨ External Contributions](#external-contributions)
* [📈 Stats](#stats)

### Changes since 7.0.0-rc4

This section lists the most important changes from the last release candidate.
The remaining section describes the complete release notes for 7.0.0.

#### New and Noteworthy

##### Maven PMD Plugin compatibility with PMD 7

In order to use PMD 7 with [maven-pmd-plugin](https://maven.apache.org/plugins/maven-pmd-plugin/) a new
compatibility module has been created. This allows to use PMD 7 by simply adding one additional dependency:

1. Follow the guide [Upgrading PMD at Runtime](https://maven.apache.org/plugins/maven-pmd-plugin/examples/upgrading-PMD-at-runtime.html)
2. Add additionally the following dependency:

```xml
<dependency>
  <groupId>net.sourceforge.pmd</groupId>
  <artifactId>pmd-compat6</artifactId>
  <version>${pmdVersion}</version>
</dependency>
```

It is important to add this dependency as the **first** in the list, so that maven-pmd-plugin sees the (old)
compatible versions of some classes.

This module is available beginning with version 7.0.0-rc4 and will be there at least for the first
final version PMD 7 (7.0.0). It's not decided yet, whether we will keep updating it, after PMD 7 is finally
released.

Note: This compatibility module only works for the built-in rules, that are still available in PMD 7. E.g. you need
to review your rulesets and look out for deprecated rules and such. See the use case
[I'm using only built-in rules](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_userdocs_migrating_to_pmd7.html#im-using-only-built-in-rules)
in the [Migration Guide for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_userdocs_migrating_to_pmd7.html).

As PMD 7 revamped the Java module, if you have custom rules, you need to migrate these rules.
See the use case [I'm using custom rules](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_userdocs_migrating_to_pmd7.html#im-using-custom-rules)
in the Migration Guide.

Note: Once the default version of PMD is upgraded to PMD7 in maven-pmd-plugin
(see [MPMD-379](https://issues.apache.org/jira/projects/MPMD/issues/MPMD-379)), this
compatibility module is no longer needed. The module pmd-compat6 might not be maintained then
any further, hence it is already declared as deprecated.

No guarantee is given, that the (deprecated) module pmd-compat6 is being maintained over the
whole lifetime of PMD 7.

##### Java 22 Support

This release of PMD brings support for Java 22. There are the following new standard language features,
that are supported now:

* [JEP 456: Unnamed Variables & Patterns](https://openjdk.org/jeps/456)

PMD also supports the following preview language features:

* [JEP 447: Statements before super(...) (Preview)](https://openjdk.org/jeps/447)
* [JEP 459: String Templates (Second Preview)](https://openjdk.org/jeps/459)
* [JEP 463: Implicitly Declared Classes and Instance Main Methods (Second Preview)](https://openjdk.org/jeps/463)

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `22-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-22-preview ...

Note: Support for Java 20 preview language features have been removed. The version "20-preview" is no longer available.

##### Swift Support

* limited support for Swift 5.9 (Macro Expansions)

##### Groovy Support (CPD)

* We now support parsing all Groovy features from Groovy 3 and 4.
* We now support [suppression](pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs.
* See [PR #4726](https://github.com/pmd/pmd/pull/4726) for details.

##### Updated PMD Designer

This PMD release ships a new version of the pmd-designer. The designer artifact has been
renamed from "pmd-ui" to "pmd-designer". While the designer still works with Java 8, the
recommended Java Runtime is Java 11 (or later) with OpenJFX 17 (or later).

For the detailed changes, see [PMD Designer Changelog (7.0.0)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0).

##### Apex Support: Replaced Jorje with fully open source front-end

When PMD added Apex support with version 5.5.0, it utilized the Apex Jorje library to parse Apex source
and generate an AST. This library is however a binary-blob provided as part of the
[Salesforce Extensions for VS Code](https://github.com/forcedotcom/salesforcedx-vscode), and it is closed-source.

This causes problems, if binary blobs are not allowed by e.g. a company-wide policy. In that case, the Jorje
library prevented that PMD Apex could be used at all.

Also having access to the source code, enhancements and modifications are easier to do.

Under the hood, we use two open source libraries instead:

* [apex-parser](https://github.com/apex-dev-tools/apex-parser) originally by
  [Kevin Jones](https://github.com/nawforce) (@nawforce).
  This project provides the grammar for a ANTLR based parser.
* [Summit-AST](https://github.com/google/summit-ast) by [Google](https://github.com/google) (@google)
  This project translates the ANTLR parse tree into an AST, that is similar to the AST Jorje provided.
  Note: This is not an official Google product.

Although the parser is completely switched, there are only little known changes to the AST.
These are documented in the [Migration Guide for PMD 7: Apex AST](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_userdocs_migrating_to_pmd7.html#apex-ast).

With the new Apex parser, the new language constructs like
[User Mode Database Operations](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_enforce_usermode.htm)
and the new [Null Coalescing Operator `??`](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/langCon_apex_NullCoalescingOperator.htm)
can be parsed now. PMD should be able to parse Apex code up to version 60.0 (Spring '24).

See [#3766](https://github.com/pmd/pmd/issues/3766) for details.

Contributors: [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google),
  [Edward Klimoshenko](https://github.com/eklimo) (@eklimo)

##### Changed: Visualforce

There was an inconsistency between the naming of the maven module and the language id. The language id
used the abbreviation "vf", while the maven module used the longer name "visualforce". This has been
solved by renaming the language module to its full name "visualforce". The java packages have
been renamed as well.

If you import rules, you also need to adjust the paths, e.g.

* `category/vf/security.xml` ➡️ `category/visualforce/security.xml`

##### Changed: HTML support

Support for HTML was introduced in PMD 6.55.0 as an experimental feature. With PMD 7.0.0 this
is now considered stable.

##### Changed: Kotlin support

Support for Kotlin was introduced with PMD 7.0.0-rc1 as an experimental feature. With PMD 7.0.0 this
is now considered stable.

##### Changed: Velocity Template Language (VTL)

The module was named just "vm" which was not a good name. Its module name, language id and
package names have been renamed to "velocity".

If you import rules, you also need to adjust the paths, e.g.

* `category/vm/...` ➡️ `category/velocity/...`

#### Rule Changes

**New Rules**

* [`OperationWithHighCostInLoop`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_performance.html#operationwithhighcostinloop) finds Schema class methods called in a loop, which is a
  potential performance issue.
* [`UseExplicitTypes`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#useexplicittypes) reports usages of `var` keyword, which was introduced with Java 10.
* [`MissingEncoding`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_xml_bestpractices.html#missingencoding) finds XML files without explicit encoding.

**Changed Rules**

* [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement): The rule has a new property to allow empty blocks when
  they contain a comment (`allowCommentedBlocks`).
* [`MethodNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_codestyle.html#methodnamingconventions): The deprecated rule property `skipTestMethodUnderscores` has
  been removed. It was actually deprecated since PMD 6.15.0, but was not mentioned in the release notes
  back then. Use the property `testPattern` instead to configure valid names for test methods.
* [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_documentation.html#commentrequired): The deprecated property `headerCommentRequirement` has been removed.
  Use the property `classCommentRequirement` instead.
* [`NonSerializableClass`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#nonserializableclass): The deprecated property `prefix` has been removed
  without replacement. In a serializable class all fields have to be serializable regardless of the name.

**Renamed Rulesets**

* `category/vf/security.xml` ➡️ `category/visualforce/security.xml`
* `category/vm/bestpractices.xml` ➡️ `category/velocity/bestpractices.xml`
* `category/vm/design.xml` ➡️ `category/velocity/design.xml`
* `category/vm/errorprone.xml` ➡️ `category/velocity/errorprone.xml`

**Removed Rules**

The following previously deprecated rules have been finally removed:

* Apex
  * performance.xml/AvoidSoqlInLoops&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`OperationWithLimitsInLoop`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_performance.html#operationwithlimitsinloop)
  * performance.xml/AvoidSoslInLoops&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`OperationWithLimitsInLoop`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_performance.html#operationwithlimitsinloop)
  * performance.xml/AvoidDmlStatementsInLoops&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`OperationWithLimitsInLoop`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_performance.html#operationwithlimitsinloop)
* Java
  * design.xml/ExcessiveClassLength&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#ncsscount)
  * design.xml/ExcessiveMethodLength&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#ncsscount)
  * errorprone.xml/BeanMembersShouldSerialize&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`NonSerializableClass`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#nonserializableclass)
  * errorprone.xml/EmptyFinallyBlock&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
  * errorprone.xml/EmptyIfStmt&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
  * errorprone.xml/EmptyInitializer&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
  * errorprone.xml/EmptyStatementBlock&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
  * errorprone.xml/EmptyStatementNotInLoop&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessarySemicolon`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessarysemicolon)
  * errorprone.xml/EmptySwitchStatements&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
  * errorprone.xml/EmptySynchronizedBlock&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
  * errorprone.xml/EmptyTryBlock&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
  * errorprone.xml/EmptyWhileStmt&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)

**Removed deprecated rulesets**

The following previously deprecated rulesets have been removed. These were the left-over rulesets from PMD 5.
The rules have been moved into categories with PMD 6.

<details markdown="block">
<summary markdown="span">List of deprecated rulesets
</summary>

* rulesets/apex/apexunit.xml
* rulesets/apex/braces.xml
* rulesets/apex/complexity.xml
* rulesets/apex/empty.xml
* rulesets/apex/metrics.xml
* rulesets/apex/performance.xml
* rulesets/apex/ruleset.xml
* rulesets/apex/securty.xml
* rulesets/apex/style.xml
* rulesets/java/android.xml
* rulesets/java/basic.xml
* rulesets/java/clone.xml
* rulesets/java/codesize.xml
* rulesets/java/comments.xml
* rulesets/java/controversial.xml
* rulesets/java/coupling.xml
* rulesets/java/design.xml
* rulesets/java/empty.xml
* rulesets/java/finalizers.xml
* rulesets/java/imports.xml
* rulesets/java/j2ee.xml
* rulesets/java/javabeans.xml
* rulesets/java/junit.xml
* rulesets/java/logging-jakarta-commons.xml
* rulesets/java/logging-java.xml
* rulesets/java/metrics.xml
* rulesets/java/migrating.xml
* rulesets/java/migrating_to_13.xml
* rulesets/java/migrating_to_14.xml
* rulesets/java/migrating_to_15.xml
* rulesets/java/migrating_to_junit4.xml
* rulesets/java/naming.xml
* rulesets/java/optimizations.xml
* rulesets/java/strictexception.xml
* rulesets/java/strings.xml
* rulesets/java/sunsecure.xml
* rulesets/java/typeresolution.xml
* rulesets/java/unnecessary.xml
* rulesets/java/unusedcode.xml
* rulesets/ecmascript/basic.xml
* rulesets/ecmascript/braces.xml
* rulesets/ecmascript/controversial.xml
* rulesets/ecmascript/unnecessary.xml
* rulesets/jsp/basic.xml
* rulesets/jsp/basic-jsf.xml
* rulesets/plsql/codesize.xml
* rulesets/plsql/dates.xml
* rulesets/plsql/strictsyntax.xml
* rulesets/plsql/TomKytesDespair.xml
* rulesets/vf/security.xml
* rulesets/vm/basic.xml
* rulesets/pom/basic.xml
* rulesets/xml/basic.xml
* rulesets/xsl/xpath.xml
* rulesets/releases/*

</details>


#### Fixed issues

* cli
  * [#4594](https://github.com/pmd/pmd/pull/4594):   \[cli] Change completion generation to runtime
  * [#4685](https://github.com/pmd/pmd/pull/4685):   \[cli] Clarify CPD documentation, fix positional parameter handling
  * [#4723](https://github.com/pmd/pmd/issues/4723): \[cli] Launch fails for "bash pmd"
* core
  * [#1027](https://github.com/pmd/pmd/issues/1027): \[core] Apply the new PropertyDescriptor&lt;Pattern&gt; type where applicable
  * [#3903](https://github.com/pmd/pmd/issues/3903): \[core] Consolidate `n.s.pmd.reporting` package
  * [#3905](https://github.com/pmd/pmd/issues/3905): \[core] Stabilize tree export API
  * [#3917](https://github.com/pmd/pmd/issues/3917): \[core] Consolidate `n.s.pmd.lang.rule` package
  * [#4065](https://github.com/pmd/pmd/issues/4065): \[core] Rename TokenMgrError to LexException, Tokenizer to CpdLexer
  * [#4309](https://github.com/pmd/pmd/issues/4309): \[core] Cleanups in XPath area
  * [#4312](https://github.com/pmd/pmd/issues/4312): \[core] Remove unnecessary property `color` and system property `pmd.color` in `TextColorRenderer`
  * [#4313](https://github.com/pmd/pmd/issues/4313): \[core] Remove support for &lt;lang&gt;-&lt;ruleset&gt; hyphen notation for ruleset references
  * [#4314](https://github.com/pmd/pmd/issues/4314): \[core] Remove ruleset compatibility filter (RuleSetFactoryCompatibility) and CLI option `--no-ruleset-compatibility`
  * [#4348](https://github.com/pmd/pmd/issues/4348): \[core] Consolidate @<!-- -->InternalApi classes
  * [#4349](https://github.com/pmd/pmd/issues/4349): \[core] Cleanup remaining experimental and deprecated API
  * [#4378](https://github.com/pmd/pmd/issues/4378): \[core] Ruleset loading processes commented rules
  * [#4674](https://github.com/pmd/pmd/issues/4674): \[core] WARNING: Illegal reflective access by org.codehaus.groovy.reflection.CachedClass
  * [#4694](https://github.com/pmd/pmd/pull/4694):   \[core] Fix line/col numbers in TokenMgrError
  * [#4717](https://github.com/pmd/pmd/issues/4717): \[core] XSLTRenderer doesn't close report file
  * [#4750](https://github.com/pmd/pmd/pull/4750):   \[core] Fix flaky SummaryHTMLRenderer
  * [#4782](https://github.com/pmd/pmd/pull/4782):   \[core] Avoid using getImage/@<!-- -->Image
* doc
  * [#995](https://github.com/pmd/pmd/issues/995):   \[doc] Document API evolution principles as ADR
  * [#2511](https://github.com/pmd/pmd/issues/2511): \[doc] Review guides for writing java/xpath rules for correctness with PMD 7
  * [#3175](https://github.com/pmd/pmd/issues/3175): \[doc] Document language module features
  * [#4308](https://github.com/pmd/pmd/issues/4308): \[doc] Document XPath API @<!-- ->NoAttribute and @<!-- -->DeprecatedAttribute
  * [#4319](https://github.com/pmd/pmd/issues/4319): \[doc] Document TypeRes API and Symbols API
  * [#4659](https://github.com/pmd/pmd/pull/4659):   \[doc] Improve ant documentation
  * [#4669](https://github.com/pmd/pmd/pull/4669):   \[doc] Add bld PMD Extension to Tools / Integrations
  * [#4676](https://github.com/pmd/pmd/issues/4676): \[doc] Clarify how CPD `--ignore-literals` and `--ignore-identifiers` work
  * [#4704](https://github.com/pmd/pmd/issues/4704): \[doc] Multivalued properties do not accept \| as a separator
* miscellaneous
  * [#4699](https://github.com/pmd/pmd/pull/4699):   Make PMD buildable with java 21
  * [#4586](https://github.com/pmd/pmd/pull/4586):   Use explicit encoding in ruleset xml files
  * [#4642](https://github.com/pmd/pmd/issues/4642): Update regression tests with Java 21 language features
  * [#4736](https://github.com/pmd/pmd/issues/4736): \[ci] Improve build procedure
  * [#4741](https://github.com/pmd/pmd/pull/4741):   Add pmd-compat6 module for maven-pmd-plugin
  * [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6
  * [#4776](https://github.com/pmd/pmd/issues/4776): \[ci] Upgrade to ruby 3
  * [#4796](https://github.com/pmd/pmd/pull/4796):   Remove deprecated and release rulesets
  * [#4823](https://github.com/pmd/pmd/pull/4823):   Update to use renamed pmd-designer
  * [#4827](https://github.com/pmd/pmd/pull/4827):   \[compat6] Support config errors and cpd for csharp
  * [#4830](https://github.com/pmd/pmd/issues/4830): Consolidate packages in each maven module
  * [#4867](https://github.com/pmd/pmd/issues/4867): \[dist] ./mvnw command not found in dist-src
* apex
  * [#3766](https://github.com/pmd/pmd/issues/3766): \[apex] Replace Jorje with fully open source front-end
  * [#4828](https://github.com/pmd/pmd/issues/4828): \[apex] Support null coalescing operator ?? (apex 60)
  * [#4845](https://github.com/pmd/pmd/issues/4845): \[apex] Use same ANLTR version for apex-parser
* apex-bestpractices
  * [#4556](https://github.com/pmd/pmd/issues/4556): \[apex] UnusedLocalVariable flags for variables which are using in SOQL/SOSL binds
* apex-documentation
  * [#4774](https://github.com/pmd/pmd/issues/4774): \[apex] ApexDoc false-positive for the first method of an annotated Apex class
* apex-performance
  * [#4675](https://github.com/pmd/pmd/issues/4675): \[apex] New Rule: OperationWithHighCostInLoop
* groovy
  * [#4726](https://github.com/pmd/pmd/pull/4726):   \[groovy] Support Groovy to 3 and 4 and CPD suppressions
* java
  * [#1307](https://github.com/pmd/pmd/issues/1307): \[java] AccessNode API changes
  * [#3751](https://github.com/pmd/pmd/issues/3751): \[java] Rename some node types
  * [#4628](https://github.com/pmd/pmd/pull/4628):   \[java] Support loading classes from java runtime images
  * [#4753](https://github.com/pmd/pmd/issues/4753): \[java] PMD crashes while using generics and wildcards
  * [#4757](https://github.com/pmd/pmd/issues/4757): \[java] Intermittent NPEs while analyzing Java code
  * [#4794](https://github.com/pmd/pmd/issues/4794): \[java] Support JDK 22
* java-bestpractices
  * [#4603](https://github.com/pmd/pmd/issues/4603): \[java] UnusedAssignment false positive in record compact constructor
  * [#4625](https://github.com/pmd/pmd/issues/4625): \[java] UnusedPrivateMethod false positive: Autoboxing into Number
  * [#4817](https://github.com/pmd/pmd/issues/4817): \[java] UnusedPrivateMethod false-positive used in lambda
* java-codestyle
  * [#2847](https://github.com/pmd/pmd/issues/2847): \[java] New Rule: Use Explicit Types
  * [#4239](https://github.com/pmd/pmd/issues/4239): \[java] UnnecessaryLocalBeforeReturn - false positive with catch clause
  * [#4578](https://github.com/pmd/pmd/issues/4578): \[java] CommentDefaultAccessModifier comment needs to be before annotation if present
  * [#4631](https://github.com/pmd/pmd/issues/4631): \[java] UnnecessaryFullyQualifiedName fails to recognize illegal self reference in enums
  * [#4645](https://github.com/pmd/pmd/issues/4645): \[java] CommentDefaultAccessModifier - False Positive with JUnit5's ParameterizedTest
  * [#4754](https://github.com/pmd/pmd/pull/4754):   \[java] EmptyControlStatementRule: Add allowCommentedBlocks property
  * [#4816](https://github.com/pmd/pmd/issues/4816): \[java] UnnecessaryImport false-positive on generic method call with on lambda
* java-design
  * [#174](https://github.com/pmd/pmd/issues/174):   \[java] SingularField false positive with switch in method that both assigns and reads field
* java-errorprone
  * [#718](https://github.com/pmd/pmd/issues/718):   \[java] BrokenNullCheck false positive with parameter/field confusion
  * [#932](https://github.com/pmd/pmd/issues/932):   \[java] SingletonClassReturningNewInstance false positive with double assignment
  * [#1831](https://github.com/pmd/pmd/issues/1831): \[java] DetachedTestCase reports abstract methods
  * [#4719](https://github.com/pmd/pmd/pull/4719):   \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string
* javascript
  * [#2305](https://github.com/pmd/pmd/issues/2305): \[javascript] UnnecessaryBlock - false positives with destructuring assignments
  * [#4673](https://github.com/pmd/pmd/pull/4673):   \[javascript] CPD: Added support for decorator notation
* plsql
  * [#4820](https://github.com/pmd/pmd/issues/4820): \[plsql] WITH clause is ignored for SELECT INTO statements
* swift
  * [#4697](https://github.com/pmd/pmd/issues/4697): \[swift] Support Swift 5.9 features (mainly macros expansion expressions)
* xml-bestpractices
  * [#4592](https://github.com/pmd/pmd/pull/4592):   \[xml] Add MissingEncoding rule

#### API Changes

See [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#700).

#### External Contributions
* [#4093](https://github.com/pmd/pmd/pull/4093): \[apex] Summit-AST Apex module - Part 1 - [Edward Klimoshenko](https://github.com/eklimo) (@eklimo)
* [#4151](https://github.com/pmd/pmd/pull/4151): \[apex] Summit-AST Apex module - Part 2 - expression nodes - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4171](https://github.com/pmd/pmd/pull/4171): \[apex] Summit-AST Apex module - Part 3 - initializers - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4206](https://github.com/pmd/pmd/pull/4206): \[apex] Summit-AST Apex module - Part 4 - statements - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4219](https://github.com/pmd/pmd/pull/4219): \[apex] Summit-AST Apex module - Part 5 - annotations, triggers, misc. - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4242](https://github.com/pmd/pmd/pull/4242): \[apex] Merge 6.52 into experimental-apex-parser - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4251](https://github.com/pmd/pmd/pull/4251): \[apex] Summit-AST Apex module - Part 6 Passing testsuite - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4448](https://github.com/pmd/pmd/pull/4448): \[apex] Bump summit-ast to new release 2.1.0 (and remove workaround) - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4479](https://github.com/pmd/pmd/pull/4479): \[apex] Merge main (7.x) branch into experimental-apex-parser and fix tests - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4562](https://github.com/pmd/pmd/pull/4562): \[apex] Fixes #4556 - Update Apex bind regex match for all possible combinations - [nwcm](https://github.com/nwcm) (@nwcm)
* [#4640](https://github.com/pmd/pmd/pull/4640): \[cli] Launch script fails if run via "bash pmd" - [Shai Bennathan](https://github.com/shai-bennathan) (@shai-bennathan)
* [#4673](https://github.com/pmd/pmd/pull/4673): \[javascript] CPD: Added support for decorator notation - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4677](https://github.com/pmd/pmd/pull/4677): \[apex] Add new rule: OperationWithHighCostInLoop - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#4698](https://github.com/pmd/pmd/pull/4698): \[swift] Add macro expansion support for swift 5.9 - [Richard B.](https://github.com/kenji21) (@kenji21)
* [#4706](https://github.com/pmd/pmd/pull/4706): \[java] DetachedTestCase should not report on abstract methods - [Debamoy Datta](https://github.com/Debamoy) (@Debamoy)
* [#4719](https://github.com/pmd/pmd/pull/4719): \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string - [ciufudean](https://github.com/ciufudean) (@ciufudean)
* [#4738](https://github.com/pmd/pmd/pull/4738): \[doc] Added reference to the PMD extension for bld - [Erik C. Thauvin](https://github.com/ethauvin) (@ethauvin)
* [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6 - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4750](https://github.com/pmd/pmd/pull/4750): \[core] Fix flaky SummaryHTMLRenderer - [219sansim](https://github.com/219sansim) (@219sansim)
* [#4752](https://github.com/pmd/pmd/pull/4752): \[core] Fix flaky LatticeRelationTest - [219sansim](https://github.com/219sansim) (@219sansim)
* [#4754](https://github.com/pmd/pmd/pull/4754): \[java] EmptyControlStatementRule: Add allowCommentedBlocks property - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4759](https://github.com/pmd/pmd/pull/4759): \[java] fix: remove delimiter attribute from ruleset category/java/errorprone.xml - [Marcin Dąbrowski](https://github.com/marcindabrowski) (@marcindabrowski)
* [#4825](https://github.com/pmd/pmd/pull/4825): \[plsql] Fix ignored WITH clause for SELECT INTO statements - [Laurent Bovet](https://github.com/lbovet) (@lbovet)
* [#4857](https://github.com/pmd/pmd/pull/4857): \[javascript] Fix UnnecessaryBlock issues with empty statements - [Oleksandr Shvets](https://github.com/oleksandr-shvets) (@oleksandr-shvets)

### 🚀 Major Features and Enhancements

#### New official logo

The new official logo of PMD:

![New PMD Logo](https://docs.pmd-code.org/pmd-doc-7.0.0/images/logo/pmd-logo-300px.png)

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#new-official-logo).

#### Revamped Java module

* Java grammar substantially refactored - more correct regarding the Java Language Specification (JLS)
* Built-in rules have been upgraded for the changed AST
* Rewritten type resolution framework and symbol table correctly implements the JLS
* AST exposes more semantic information (method calls, field accesses)

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#revamped-java).

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Revamped Command Line Interface

* Unified and consistent Command Line Interface for both Linux/Unix and Windows across our different utilities
* Single script `pmd` (`pmd.bat` for Windows) to launch the different utilities:
    * `pmd check` to run PMD rules and analyze a project
    * `pmd cpd` to run CPD (copy paste detector)
    * `pmd designer` to run the PMD Rule Designer
* Progress bar support for `pmd check`
* Shell completion

![Demo](https://docs.pmd-code.org/pmd-doc-7.0.0/images/userdocs/pmd-demo.gif)

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#revamped-command-line-interface).

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Full Antlr support

* [Antlr](https://www.antlr.org/) based grammars can now be used to build full-fledged PMD rules.
* Previously, Antlr grammar could only be used for CPD
* New supported languages: Swift and Kotlin

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#full-antlr-support).

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer. The designer artifact has been
renamed from "pmd-ui" to "pmd-designer". While the designer still works with Java 8, the
recommended Java Runtime is Java 11 (or later) with OpenJFX 17 (or later).

For the detailed changes, see
* [PMD Designer Changelog (7.0.0)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0).
* [PMD Designer Changelog (7.0.0-rc4)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc4).
* [PMD Designer Changelog (7.0.0-rc1)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc1).

#### New CPD report format cpdhtml-v2.xslt

Thanks to @mohan-chinnappan-n a new CPD report format has been added which features a data table.
It uses an XSLT stylesheet to convert CPD's XML format into HTML.

See [the example report](https://docs.pmd-code.org/pmd-doc-7.0.0/report-examples/cpdhtml-v2.html).

Contributors: [Mohan Chinnappan](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)

### 🎉 Language Related Changes

Note that this is just a concise listing of the highlights.
For more information on the languages, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#-language-related-changes).

#### New: CPD support for Apache Velocity Template Language (VTL)

PMD supported Apache Velocity for a very long time, but the CPD integration never got finished.
This is now done and CPD supports Apache Velocity Template language for detecting copy and paste.
It is shipped in the module `pmd-velocity`.

#### New: CPD support for Coco

Thanks to a contribution, CPD now supports Coco, a modern programming language
designed specifically for building event-driven software. It is shipped in the new
module `pmd-coco`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: CPD support for Julia

Thanks to a contribution, CPD now supports the Julia language. It is shipped
in the new module `pmd-julia`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: CPD support for TypeScript

Thanks to a contribution, CPD now supports the TypeScript language. It is shipped
with the rest of the JavaScript support in the module `pmd-javascript`.

Contributors: [Paul Guyot](https://github.com/pguyot) (@pguyot)

#### New: Java 21 and 22 Support

This release of PMD brings support for Java 21 and 22. There are the following new standard language features,
that are supported now:

* [JEP 456: Unnamed Variables & Patterns](https://openjdk.org/jeps/456) (Java 22)
* [JEP 440: Record Patterns](https://openjdk.org/jeps/440) (Java 21)
* [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441) (Java 21)

PMD also supports the following preview language features:

* [JEP 447: Statements before super(...) (Preview)](https://openjdk.org/jeps/447) (Java 22)
* [JEP 459: String Templates (Second Preview)](https://openjdk.org/jeps/459) (Java 21 and 22)
* [JEP 463: Implicitly Declared Classes and Instance Main Methods (Second Preview)](https://openjdk.org/jeps/463) (Java 21 and 22)

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `22-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-22-preview ...

Note: Support for Java 19 and Java 20 preview language features have been removed. The versions "19-preview" and
"20-preview" are no longer available.

#### New: Kotlin support

* Use PMD to analyze Kotlin code with PMD rules.
* Support for Kotlin 1.8 grammar
* Initially 2 built-in rules
* Support for Kotlin was introduced with PMD 7.0.0-rc1 as an experimental feature. With PMD 7.0.0 this
  is now considered stable.

Contributors: [Jeroen Borgers](https://github.com/jborgers) (@jborgers),
[Peter Paul Bakker](https://github.com/stokpop) (@stokpop)

#### New: Swift support

* Use PMD to analyze Swift code with PMD rules.
* Limited support for Swift 5.9 (Macro Expansions)
* Initially 4 built-in rules

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### Changed: Apex Support: Replaced Jorje with fully open source front-end

When PMD added Apex support with version 5.5.0, it utilized the Apex Jorje library to parse Apex source
and generate an AST. This library is however a binary-blob provided as part of the
[Salesforce Extensions for VS Code](https://github.com/forcedotcom/salesforcedx-vscode), and it is closed-source.

This causes problems, if binary blobs are not allowed by e.g. a company-wide policy. In that case, the Jorje
library prevented that PMD Apex could be used at all.

Also having access to the source code, enhancements and modifications are easier to do.

Under the hood, we use two open source libraries instead:

* [apex-parser](https://github.com/nawforce/apex-parser) by [Kevin Jones](https://github.com/nawforce) (@nawforce)
  This project provides the grammar for a ANTLR based parser.
* [Summit-AST](https://github.com/google/summit-ast) by [Google](https://github.com/google) (@google)
  This project translates the ANTLR parse tree into an AST, that is similar to the AST Jorje provided.
  Note: This is not an official Google product.

Although the parsers is completely switched, there are only little known changes to the AST.
These are documented in the [Migration Guide for PMD 7: Apex AST](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_userdocs_migrating_to_pmd7.html#apex-ast).
With the new Apex parser, the new language constructs like
[User Mode Database Operations](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_enforce_usermode.htm)
and the new [Null Coalescing Operator `??`](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/langCon_apex_NullCoalescingOperator.htm)
can be parsed now. PMD should be able to parse Apex code up to version 60.0 (Spring '24).

See [#3766](https://github.com/pmd/pmd/issues/3766) for details.

Contributors: [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google),
[Edward Klimoshenko](https://github.com/eklimo) (@eklimo)

#### Changed: CPP can now ignore identifiers in sequences (CPD)

* New command line option for CPD: `--ignore-sequences`.
* This option is used for CPP only: with the already existing option `--ignore-literal-sequences`, only
  literals were ignored. The new option additionally ignores identifiers as well in sequences.
* See [PR #4470](https://github.com/pmd/pmd/pull/4470) for details.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### Changed: Groovy Support (CPD)

* We now support parsing all Groovy features from Groovy 3 and 4.
* We now support [suppression](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs.
* See [PR #4726](https://github.com/pmd/pmd/pull/4726) for details.

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Changed: HTML support

Support for HTML was introduced in PMD 6.55.0 as an experimental feature. With PMD 7.0.0 this
is now considered stable.

#### Changed: JavaScript support

* Latest version supports ES6 and also some new constructs (see [Rhino](https://github.com/mozilla/rhino))
* Comments are retained

#### Changed: Language versions

* More predefined language versions for each supported language
* Can be used to limit rule execution for specific versions only with `minimumLanguageVersion` and
  `maximumLanguageVersion` attributes.

#### Changed: Rule properties

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#"><code>PropertyFactory</code></a> to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

#### Changed: Velocity Template Language (VTL)

The module was named just "vm" which was not a good name. Its module name, language id and
package names have been renamed to "velocity".

If you import rules, you also need to adjust the paths, e.g.

* `category/vm/...` ➡️ `category/velocity/...`

#### Changed: Visualforce

There was an inconsistency between the naming of the maven module and the language id. The language id
used the abbreviation "vf", while the maven module used the longer name "visualforce". This has been
solved by renaming the language module to its full name "visualforce". The java packages have
been renamed as well.

If you import rules, you also need to adjust the paths, e.g.

* `category/vf/security.xml` ➡️ `category/visualforce/security.xml`

### 🌟 New and changed rules

#### New Rules

**Apex**
* [`OperationWithHighCostInLoop`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_performance.html#operationwithhighcostinloop) finds Schema class methods called in a loop, which is a
  potential performance issue.
* [`UnusedMethod`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_design.html#unusedmethod) finds unused methods in your code.

**Java**
* [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryboxing) reports boxing and unboxing conversions that may be made implicit.
* [`UseExplicitTypes`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#useexplicittypes) reports usages of `var` keyword, which was introduced with Java 10.

**Kotlin**
* [`FunctionNameTooShort`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_kotlin_bestpractices.html#functionnametooshort) finds functions with a too short name.
* [`OverrideBothEqualsAndHashcode`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_kotlin_errorprone.html#overridebothequalsandhashcode) finds classes with only
  either `equals` or `hashCode` overridden, but not both. This leads to unexpected behavior once instances
  of such classes are used in collections (Lists, HashMaps, ...).

**Swift**
* [`ForceCast`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_errorprone.html#forcecast) flags all force casts, making sure you are
  defensively considering all types. Having the application crash shouldn't be an option.
* [`ForceTry`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_errorprone.html#forcetry) flags all force tries, making sure you are
  defensively handling exceptions. Having the application crash shouldn't be an option.
* [`ProhibitedInterfaceBuilder`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_bestpractices.html#prohibitedinterfacebuilder) flags any usage of interface
  builder. Interface builder files are prone to merge conflicts, and are impossible to code review, so larger
  teams usually try to avoid it or reduce its usage.
* [`UnavailableFunction`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_bestpractices.html#unavailablefunction) flags any function throwing
  a `fatalError` not marked as `@available(*, unavailable)` to ensure no calls are actually performed in
  the codebase.

**XML**
* [`MissingEncoding`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_xml_bestpractices.html#missingencoding) finds XML files without explicit encoding.

#### Other changes

The information about changed rules, removed rules and rulesets
can be found in the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#-new-and-changed-rules).

### 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Also, there are some improvement and changes in different areas. For the detailed description
of the changes listed here, see [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#-api).

* Miscellaneous smaller changes and cleanups
* XPath 3.1 support for XPath-based rules
* Node stream API for AST traversal
* Metrics framework
* Testing framework
* Language Lifecycle and Language Properties
* Rule Properties
* New Programmatic API for CPD

### 💥 Compatibility and migration notes

A detailed documentation of required changes are available in the
[Migration Guide for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_userdocs_migrating_to_pmd7.html).

See also [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#-compatibility-and-migration-notes).

### 🐛 Fixed Issues

More than 300 issues have been fixed in PMD 7.
See [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#-fixed-issues) for the
complete list of fixed issues.

### ✨ External Contributions

Many thanks to the following contributors:
@219sansim, @aaronhurst-google, @anastasiia-koba, @AndreyBozhko, @bergander, @ciufudean, @cyw3, @dague1,
@Debamoy, @eklimo, @ethauvin, @JerritEic, @joaodinissf, @kenji21, @krdabrowski, @lbovet, @lsoncini,
@LynnBroe, @marcindabrowski, @matifraga, @mohan-chinnappan-n, @mohui1999, @nawforce, @nirvikpatel,
@nwcm, @oleksandr-shvets, @pguyot, @PimvanderLoos, @rcorfieldffdc, @sfdcsteve, @shai-bennathan, @tomidelucca,
@tprouvot, @wener-tiobe.

See [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_release_notes_pmd7.html#-external-contributions) for the
full list of PRs.

### 📈 Stats
* 5741 commits
* 849 closed tickets & PRs
* Days since last release (6.55.0): 390
* Days since last release (7.0.0-rc4): 173




</details>



## 30-September-2023 - 7.0.0-rc4

We're excited to bring you the next major version of PMD!

Since this is a big release, we provide here only a concise version of the release notes. We prepared a separate
page with the full [Detailed Release Notes for PMD 7.0.0](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_release_notes_pmd7.html).

<div style="border: 1px solid; border-radius: .25rem; padding: .75rem 1.25rem;" role="alert">
<strong>ℹ️ Release Candidates</strong>
<p>PMD 7.0.0 is finally almost ready. In order to gather feedback, we are going to ship a couple of release candidates.
These are officially available on GitHub and Maven Central and can be used as usual (e.g. as a dependency).
We encourage you to try out the new features, but keep in mind that we may introduce API breaking changes between
the release candidates. It should be stable enough if you don't use custom rules.</p>

<p>We have still some tasks planned for the next release candidates.
You can see the progress in <a href="https://github.com/pmd/pmd/issues/3898">PMD 7 Tracking Issue #3898</a>.</p>

<p>If you find any problem or difficulty while updating from PMD 6, please provide feedback via our
<a href="https://github.com/pmd/pmd/issues/new/choose">issue tracker</a>. That way we can improve the experience
for all.</p>
</div>

### Table Of Contents

* [Changes since 7.0.0-rc3](#changes-since-7.0.0-rc3)
    * [New and Noteworthy](#new-and-noteworthy)
        * [Migration Guide for PMD 7](#migration-guide-for-pmd-7)
        * [Apex Jorje Updated](#apex-jorje-updated)
        * [Java 21 Support](#java-21-support)
    * [Fixed issues](#fixed-issues)
    * [API Changes](#api-changes)
        * [pmd-java](#pmd-java)
        * [Rule properties](#rule-properties)
        * [New Programmatic API for CPD](#new-programmatic-api-for-cpd)
        * [Removed classes and methods](#removed-classes-and-methods)
        * [Moved packages](#moved-packages)
        * [Changed types and other changes](#changed-types-and-other-changes)
        * [Internal APIs](#internal-apis)
        * [Deprecated API](#deprecated-api)
        * [Experimental APIs](#experimental-apis)
    * [External Contributions](#external-contributions)
* [🚀 Major Features and Enhancements](#🚀-major-features-and-enhancements)
    * [New official logo](#new-official-logo)
    * [Revamped Java module](#revamped-java-module)
    * [Revamped Command Line Interface](#revamped-command-line-interface)
    * [Full Antlr support](#full-antlr-support)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [New CPD report format cpdhtml-v2.xslt](#new-cpd-report-format-cpdhtml-v2.xslt)
* [🎉 Language Related Changes](#🎉-language-related-changes)
    * [New: Swift support](#new:-swift-support)
    * [New: Kotlin support (experimental)](#new:-kotlin-support-(experimental))
    * [New: CPD support for TypeScript](#new:-cpd-support-for-typescript)
    * [New: CPD support for Julia](#new:-cpd-support-for-julia)
    * [New: CPD support for Coco](#new:-cpd-support-for-coco)
    * [New: Java 21 Support](#new:-java-21-support)
    * [Changed: JavaScript support](#changed:-javascript-support)
    * [Changed: Language versions](#changed:-language-versions)
    * [Changed: CPP can now ignore identifiers in sequences (CPD)](#changed:-cpp-can-now-ignore-identifiers-in-sequences-(cpd))
    * [Changed: Apex Jorje Updated](#changed:-apex-jorje-updated)
    * [Changed: Rule properties](#changed:-rule-properties)
* [🌟 New and changed rules](#🌟-new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
    * [Removed Rules](#removed-rules)
* [🚨 API](#🚨-api)
* [💥 Compatibility and migration notes](#💥-compatibility-and-migration-notes)
* [🐛 Fixed Issues](#🐛-fixed-issues)
* [✨ External Contributions](#✨-external-contributions)
* [📈 Stats](#📈-stats)

### Changes since 7.0.0-rc3

This section lists the most important changes from the last release candidate.
The remaining section describes the complete release notes for 7.0.0.

#### New and Noteworthy

##### Migration Guide for PMD 7

A detailed documentation of required changes are available in the
[Migration Guide for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_userdocs_migrating_to_pmd7.html).

##### Apex Jorje Updated

With the new version of Apex Jorje, the new language constructs like User Mode Database Operations
can be parsed now. PMD should now be able to parse Apex code up to version 59.0 (Winter '23).

##### Java 21 Support

This release of PMD brings support for Java 21. There are the following new standard language features,
that are supported now:

* [JEP 440: Record Patterns](https://openjdk.org/jeps/440)
* [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)

PMD also supports the following preview language features:

* [JEP 430: String Templates (Preview)](https://openjdk.org/jeps/430)
* [JEP 443: Unnamed Patterns and Variables (Preview)](https://openjdk.org/jeps/443)
* [JEP 445: Unnamed Classes and Instance Main Methods (Preview)](https://openjdk.org/jeps/445)

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `21-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-21-preview ...

Note: Support for Java 19 preview language features have been removed. The version "19-preview" is no longer available.

#### Fixed issues

* miscellaneous
  * [#4582](https://github.com/pmd/pmd/issues/4582): \[dist] Download link broken
  * [#4691](https://github.com/pmd/pmd/issues/4691): \[CVEs] Critical and High CEVs reported on PMD and PMD dependencies
* core
  * [#1204](https://github.com/pmd/pmd/issues/1204): \[core] Allow numeric properties in XML to be within an unbounded range
  * [#3919](https://github.com/pmd/pmd/issues/3919): \[core] Merge CPD and PMD language
  * [#4204](https://github.com/pmd/pmd/issues/4204): \[core] Provide a CpdAnalysis class as a programmatic entry point into CPD
  * [#4301](https://github.com/pmd/pmd/issues/4301): \[core] Remove deprecated property concrete classes
  * [#4302](https://github.com/pmd/pmd/issues/4302): \[core] Migrate Property Framework API to Java 8
  * [#4323](https://github.com/pmd/pmd/issues/4323): \[core] Refactor CPD integration
  * [#4397](https://github.com/pmd/pmd/pull/4397):   \[core] Refactor CPD
  * [#4611](https://github.com/pmd/pmd/pull/4611):   \[core] Fix loading language properties from env vars
  * [#4621](https://github.com/pmd/pmd/issues/4621): \[core] Make `ClasspathClassLoader::getResource` child first
* cli
  * [#4423](https://github.com/pmd/pmd/pull/4423):   \[cli] Fix NPE when only `--file-list` is specified
* doc
  * [#4294](https://github.com/pmd/pmd/issues/4294): \[doc] Migration Guide for upgrading PMD 6 ➡️ 7
  * [#4303](https://github.com/pmd/pmd/issues/4303): \[doc] Document new property framework
  * [#4521](https://github.com/pmd/pmd/issues/4521): \[doc] Website is not mobile friendly
* apex
  * [#3973](https://github.com/pmd/pmd/issues/3973): \[apex] Update parser to support new 'as user' keywords (User Mode for Database Operations)
  * [#4453](https://github.com/pmd/pmd/issues/4453): \[apex] \[7.0-rc1] Exception while initializing Apexlink (Index 34812 out of bounds for length 34812)
* apex-design
  * [#4596](https://github.com/pmd/pmd/issues/4596): \[apex] ExcessivePublicCount ignores properties
* apex-security
  * [#4646](https://github.com/pmd/pmd/issues/4646): \[apex] ApexSOQLInjection does not recognise SObjectType or SObjectField as safe variable types
* java
  * [#4401](https://github.com/pmd/pmd/issues/4401): \[java] PMD 7 fails to build under Java 19
  * [#4583](https://github.com/pmd/pmd/issues/4583): \[java] Support JDK 21 (LTS)
* java-bestpractices
  * [#4634](https://github.com/pmd/pmd/issues/4634): \[java] JUnit4TestShouldUseTestAnnotation false positive with TestNG

#### API Changes

##### pmd-java

* Support for Java 19 preview language features have been removed. The version "19-preview" is no longer available.

##### Rule properties

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/properties/PropertyFactory.html#"><code>PropertyFactory</code></a> to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

##### New Programmatic API for CPD

See [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_release_notes_pmd7.html#new-programmatic-api-for-cpd)
and [PR #4397](https://github.com/pmd/pmd/pull/4397) for details.

##### Removed classes and methods

The following previously deprecated classes have been removed:

* pmd-core
  * `net.sourceforge.pmd.cpd.AbstractTokenizer` ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/AnyTokenizer.html#"><code>AnyTokenizer</code></a> instead
  * `net.sourceforge.pmd.cpd.CPD` ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-cli/7.0.0-rc4/net/sourceforge/pmd/cli/PmdCli.html#"><code>PmdCli</code></a> from `pmd-cli` module for CLI support or use
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/CpdAnalysis.html#"><code>CpdAnalysis</code></a> for programmatic API
  * `net.sourceforge.pmd.cpd.GridBagHelper` (now package private)
  * `net.sourceforge.pmd.cpd.TokenEntry.State`
  * `net.sourceforge.pmd.lang.document.CpdCompat`
  * `net.sourceforge.pmd.properties.BooleanMultiProperty`
  * `net.sourceforge.pmd.properties.BooleanProperty`
  * `net.sourceforge.pmd.properties.CharacterMultiProperty`
  * `net.sourceforge.pmd.properties.CharacterProperty`
  * `net.sourceforge.pmd.properties.DoubleMultiProperty`
  * `net.sourceforge.pmd.properties.DoubleProperty`
  * `net.sourceforge.pmd.properties.EnumeratedMultiProperty`
  * `net.sourceforge.pmd.properties.EnumeratedProperty`
  * `net.sourceforge.pmd.properties.EnumeratedPropertyDescriptor`
  * `net.sourceforge.pmd.properties.FileProperty` (note: without replacement)
  * `net.sourceforge.pmd.properties.FloatMultiProperty`
  * `net.sourceforge.pmd.properties.FloatProperty`
  * `net.sourceforge.pmd.properties.IntegerMultiProperty`
  * `net.sourceforge.pmd.properties.IntegerProperty`
  * `net.sourceforge.pmd.properties.LongMultiProperty`
  * `net.sourceforge.pmd.properties.LongProperty`
  * `net.sourceforge.pmd.properties.MultiValuePropertyDescriptor`
  * `net.sourceforge.pmd.properties.NumericPropertyDescriptor`
  * `net.sourceforge.pmd.properties.PropertyDescriptorField`
  * `net.sourceforge.pmd.properties.RegexProperty`
  * `net.sourceforge.pmd.properties.SingleValuePropertyDescriptor`
  * `net.sourceforge.pmd.properties.StringMultiProperty`
  * `net.sourceforge.pmd.properties.StringProperty`
  * `net.sourceforge.pmd.properties.ValueParser`
  * `net.sourceforge.pmd.properties.ValueParserConstants`
  * `net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.MultiPackagedPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.MultiValuePropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilder`
  * `net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper`
  * `net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder`
  * `net.sourceforge.pmd.properties.builders.SingleNumericPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.SinglePackagedPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.SingleValuePropertyBuilder`
  * `net.sourceforge.pmd.properties.modules.EnumeratedPropertyModule`
  * `net.sourceforge.pmd.properties.modules.NumericPropertyModule`

The following previously deprecated methods have been removed:

* pmd-core
  * `net.sourceforge.pmd.properties.PropertyBuilder.GenericCollectionPropertyBuilder#delim(char)`
  * `net.sourceforge.pmd.properties.PropertySource#setProperty(...)`
  * `net.sourceforge.pmd.properties.internal.PropertyTypeId#factoryFor(...)`
  * `net.sourceforge.pmd.properties.internal.PropertyTypeId#typeIdFor(...)`
  * `net.sourceforge.pmd.properties.PropertyDescriptor`: removed methods errorFor, type, isMultiValue,
     uiOrder, compareTo, isDefinedExternally, valueFrom, asDelimitedString

The following methods have been removed:

* pmd-core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/CPDConfiguration.html#"><code>CPDConfiguration</code></a>
    * `#sourceCodeFor(File)`, `#postConstruct()`, `#tokenizer()`, `#filenameFilter()` removed
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/Mark.html#"><code>Mark</code></a>
    * `#getSourceSlice()`, `#setLineCount(int)`, `#getLineCount()`, `#setSourceCode(SourceCode)` removed
    * `#getBeginColumn()`, `#getBeginLine()`, `#getEndLine()`, `#getEndColumn()` removed
      ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/Mark.html#getLocation()"><code>getLocation</code></a> instead
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/Match.html#"><code>Match</code></a>
    * `#LABEL_COMPARATOR` removed
    * `#setMarkSet(...)`, `#setLabel(...)`, `#getLabel()`, `#addTokenEntry(...)` removed
    * `#getSourceCodeSlice()` removed
      ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/CPDReport.html#getSourceCodeSlice(net.sourceforge.pmd.cpd.Mark)"><code>CPDReport#getSourceCodeSlice</code></a> instead
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/TokenEntry.html#"><code>TokenEntry</code></a>
    * `#getEOF()`, `#clearImages()`, `#getIdentifier()`, `#getIndex()`, `#setHashCode(int)` removed
    * `#EOF` removed ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/TokenEntry.html#isEof()"><code>isEof</code></a> instead
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/lang/ast/Parser.ParserTask.html#"><code>Parser.ParserTask</code></a>
    * `#getFileDisplayName()` removed ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/lang/ast/Parser.ParserTask.html#getFileId()"><code>getFileId</code></a> instead
      (`getFileId().getAbsolutePath()`)

The following classes have been removed:

* pmd-core
  * `net.sourceforge.pmd.cpd.AbstractLanguage`
  * `net.sourceforge.pmd.cpd.AnyLanguage`
  * `net.sourceforge.pmd.cpd.Language`
  * `net.sourceforge.pmd.cpd.LanguageFactory`
  * `net.sourceforge.pmd.cpd.MatchAlgorithm` (now package private)
  * `net.sourceforge.pmd.cpd.MatchCollector` (now package private)
  * `net.sourceforge.pmd.cpd.SourceCode` (and all inner classes like `FileCodeLoader`, ...)
  * `net.sourceforge.pmd.cpd.token.TokenFilter`

##### Moved packages

* pmd-core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/net/sourceforge/pmd/properties/NumericConstraints.html#"><code>NumericConstraints</code></a> (old package: `net.sourceforge.pmd.properties.constraints.NumericConstraints`)
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/net/sourceforge/pmd/properties/PropertyConstraint.html#"><code>PropertyConstraint</code></a> (old package: `net.sourceforge.pmd.properties.constraints.PropertyConstraint`)
    * not experimental anymore
  * <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.0.0-rc4/net/sourceforge/pmd/ant/ReportException.html#"><code>ReportException</code></a> (old package: `net.sourceforge.pmd.cpd`, moved to module `pmd-ant`)
    * it is now a RuntimeException
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/CPDReportRenderer.html#"><code>CPDReportRenderer</code></a> (old package: `net.sourceforge.pmd.cpd.renderer`)
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/impl/AntlrTokenFilter.html#"><code>AntlrTokenFilter</code></a> (old package: `net.sourceforge.pmd.cpd.token`)
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/impl/BaseTokenFilter.html#"><code>BaseTokenFilter</code></a> (old package: `net.sourceforge.pmd.cpd.token.internal`)
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/impl/JavaCCTokenFilter.html#"><code>JavaCCTokenFilter</code></a> (old package: `net.sourceforge.pmd.cpd.token`)

##### Changed types and other changes

* pmd-core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/net/sourceforge/pmd/properties/PropertyDescriptor.html#"><code>PropertyDescriptor</code></a> is now a class (was an interface)
    and it is not comparable anymore.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/AbstractConfiguration.html#setSourceEncoding(java.nio.charset.Charset)"><code>AbstractConfiguration#setSourceEncoding</code></a>
    * previously this method took a simple String for the encoding.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/PmdConfiguration.html#"><code>PmdConfiguration</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/CPDConfiguration.html#"><code>CPDConfiguration</code></a>
    * many getters and setters have been moved to the parent class <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/AbstractConfiguration.html#"><code>AbstractConfiguration</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/CPDListener.html#addedFile(int)"><code>CPDListener#addedFile</code></a>
    * no `File` parameter anymore
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/CPDReport.html#getNumberOfTokensPerFile()"><code>CPDReport#getNumberOfTokensPerFile</code></a> returns a `Map` of `FileId,Integer` instead of `String`
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/CPDReport.html#filterMatches(java.util.function.Predicate)"><code>CPDReport#filterMatches</code></a> now takes a `java.util.function.Predicate`
    as parameter
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/Tokenizer.html#"><code>Tokenizer</code></a>
    * constants are now <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/properties/PropertyDescriptor.html#"><code>PropertyDescriptor</code></a> instead of `String`,
      to be used as language properties
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/Tokenizer.html#tokenize(net.sourceforge.pmd.lang.document.TextDocument,net.sourceforge.pmd.cpd.TokenFactory)"><code>tokenize</code></a>
      changed parameters. Now takes a <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/lang/document/TextDocument.html#"><code>TextDocument</code></a> and a <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/TokenFactory.html#"><code>TokenFactory</code></a>
      (instead of `SourceCode` and `Tokens`)
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/lang/Language.html#"><code>Language</code></a>
    * method `#createProcessor(LanguagePropertyBundle)` moved to <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/lang/PmdCapableLanguage.html#"><code>PmdCapableLanguage</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/util/StringUtil.html#linesWithTrimIndent(net.sourceforge.pmd.lang.document.Chars)"><code>StringUtil#linesWithTrimIndent</code></a> now takes a `Chars`
    instead of a `String`.
* All language modules (like pmd-apex, pmd-cpp, ...)
  * consistent package naming: `net.sourceforge.pmd.lang.<langId>.cpd`
  * adapted to use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/CpdCapableLanguage.html#"><code>CpdCapableLanguage</code></a>
  * consistent static method `#getInstance()`
  * removed constants like `ID`, `TERSE_NAME` or `NAME`. Use `getInstance().getName()` etc. instead

##### Internal APIs

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/cpd/Tokens.html#"><code>Tokens</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/net/sourceforge/pmd/properties/PropertyTypeId.html#"><code>PropertyTypeId</code></a>

##### Deprecated API

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/lang/Language.html#getTerseName()"><code>Language#getTerseName</code></a> ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/lang/Language.html#getId()"><code>getId</code></a> instead

* The method <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0-rc4/net/sourceforge/pmd/lang/java/ast/ASTPattern.html#getParenthesisDepth()"><code>ASTPattern#getParenthesisDepth</code></a> has been deprecated and will be removed.
  It was introduced for supporting parenthesized patterns, but that was removed with Java 21. It is only used when
  parsing code as java-19-preview.

##### Experimental APIs

* To support the Java preview language features "String Templates" and "Unnamed Patterns and Variables", the following
  AST nodes have been introduced as experimental:
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0-rc4/net/sourceforge/pmd/lang/java/ast/ASTTemplateExpression.html#"><code>ASTTemplateExpression</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0-rc4/net/sourceforge/pmd/lang/java/ast/ASTTemplate.html#"><code>ASTTemplate</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0-rc4/net/sourceforge/pmd/lang/java/ast/ASTTemplateFragment.html#"><code>ASTTemplateFragment</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0-rc4/net/sourceforge/pmd/lang/java/ast/ASTUnnamedPattern.html#"><code>ASTUnnamedPattern</code></a>
* The AST nodes for supporting "Record Patterns" and "Pattern Matching for switch" are not experimental anymore:
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0-rc4/net/sourceforge/pmd/lang/jast/ast/ASTRecordPattern.html#"><code>ASTRecordPattern</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0-rc4/net/sourceforge/pmd/lang/jast/ast/ASTPatternList.html#"><code>ASTPatternList</code></a> (Note: it was renamed from `ASTComponentPatternList`)
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0-rc4/net/sourceforge/pmd/lang/jast/ast.html#"><code>ast</code></a> (Note: it was renamed from `ASTSwitchGuard`)


#### External Contributions
* [#4528](https://github.com/pmd/pmd/pull/4528): \[apex] Update to apexlink - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#4637](https://github.com/pmd/pmd/pull/4637): \[java] fix #4634 - JUnit4TestShouldUseTestAnnotation false positive with TestNG - [Krystian Dabrowski](https://github.com/krdabrowski) (@krdabrowski)
* [#4649](https://github.com/pmd/pmd/pull/4649): \[apex] Add SObjectType and SObjectField to list of injectable SOQL variable types - [Richard Corfield](https://github.com/rcorfieldffdc) (@rcorfieldffdc)
* [#4651](https://github.com/pmd/pmd/pull/4651): \[doc] Add "Tencent Cloud Code Analysis" in Tools / Integrations - [yale](https://github.com/cyw3) (@cyw3)
* [#4664](https://github.com/pmd/pmd/pull/4664): \[cli] CPD: Fix NPE when only `--file-list` is specified - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4665](https://github.com/pmd/pmd/pull/4665): \[java] Doc: Fix references AutoClosable -> AutoCloseable - [Andrey Bozhko](https://github.com/AndreyBozhko) (@AndreyBozhko)

### 🚀 Major Features and Enhancements

#### New official logo

The new official logo of PMD:

![New PMD Logo](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/images/logo/pmd-logo-300px.png)

#### Revamped Java module

* Java grammar substantially refactored - more correct regarding the Java Language Specification (JLS)
* Built-in rules have been upgraded for the changed AST
* Rewritten type resolution framework and symbol table correctly implements the JLS
* AST exposes more semantic information (method calls, field accesses)

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_release_notes_pmd7.html#revamped-java).

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Revamped Command Line Interface

* unified and consistent Command Line Interface for both Linux/Unix and Windows across our different utilities
* single script `pmd` (`pmd.bat` for Windows) to launch the different utilities:
    * `pmd check` to run PMD rules and analyze a project
    * `pmd cpd` to run CPD (copy paste detector)
    * `pmd designer` to run the PMD Rule Designer
* progress bar support for `pmd check`
* shell completion

![Demo](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/images/userdocs/pmd-demo.gif)

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_release_notes_pmd7.html).

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Full Antlr support

* [Antlr](https://www.antlr.org/) based grammars can now be used to build full-fledged PMD rules.
* Previously, Antlr grammar could only be used for CPD
* New supported languages: Swift and Kotlin

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_release_notes_pmd7.html).

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc1).

#### New CPD report format cpdhtml-v2.xslt

Thanks to @mohan-chinnappan-n a new CPD report format has been added which features a data table.
It uses an XSLT stylesheet to convert CPD's XML format into HTML.

See [the example report](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/report-examples/cpdhtml-v2.html).

### 🎉 Language Related Changes

Note that this is just a concise listing of the highlight.
For more information on the languages, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_release_notes_pmd7.html).

#### New: Swift support

* use PMD to analyze Swift code with PMD rules
* initially 4 built-in rules

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### New: Kotlin support (experimental)

* use PMD to analyze Kotlin code with PMD rules
* Support for Kotlin 1.8 grammar
* initially 2 built-in rules

#### New: CPD support for TypeScript

Thanks to a contribution, CPD now supports the TypeScript language. It is shipped
with the rest of the JavaScript support in the module `pmd-javascript`.

Contributors: [Paul Guyot](https://github.com/pguyot) (@pguyot)

#### New: CPD support for Julia

Thanks to a contribution, CPD now supports the Julia language. It is shipped
in the new module `pmd-julia`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: CPD support for Coco

Thanks to a contribution, CPD now supports Coco, a modern programming language
designed specifically for building event-driven software. It is shipped in the new
module `pmd-coco`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: Java 21 Support

This release of PMD brings support for Java 21. There are the following new standard language features,
that are supported now:

* [JEP 440: Record Patterns](https://openjdk.org/jeps/440)
* [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)

PMD also supports the following preview language features:

* [JEP 430: String Templates (Preview)](https://openjdk.org/jeps/430)
* [JEP 443: Unnamed Patterns and Variables (Preview)](https://openjdk.org/jeps/443)
* [JEP 445: Unnamed Classes and Instance Main Methods (Preview)](https://openjdk.org/jeps/445)

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `21-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-21-preview ...

Note: Support for Java 19 preview language features have been removed. The version "19-preview" is no longer available.

#### Changed: JavaScript support

* latest version supports ES6 and also some new constructs (see [Rhino](https://github.com/mozilla/rhino)])
* comments are retained

#### Changed: Language versions

* more predefined language versions for each supported language
* can be used to limit rule execution for specific versions only with `minimumLanguageVersion` and
  `maximumLanguageVersion` attributes.

#### Changed: CPP can now ignore identifiers in sequences (CPD)

* new command line option for CPD: `--ignore-sequences`.
* This option is used for CPP only: with the already existing option `--ignore-literal-sequences`, only
  literals were ignored. The new option additional ignores identifiers as well in sequences.
* See [PR #4470](https://github.com/pmd/pmd/pull/4470) for details.

#### Changed: Apex Jorje Updated

With the new version of Apex Jorje, the new language constructs like User Mode Database Operations
can be parsed now. PMD should now be able to parse Apex code up to version 59.0 (Winter '23).

#### Changed: Rule properties

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/properties/PropertyFactory.html#"><code>PropertyFactory</code></a> to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

### 🌟 New and changed rules

#### New Rules

**Apex**
* [`UnusedMethod`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_apex_design.html#unusedmethod) finds unused methods in your code.

**Java**
* [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_codestyle.html#unnecessaryboxing) reports boxing and unboxing conversions that may be made implicit.

**Kotlin**
* [`FunctionNameTooShort`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_kotlin_bestpractices.html#functionnametooshort)
* [`OverrideBothEqualsAndHashcode`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_kotlin_errorprone.html#overridebothequalsandhashcode)

**Swift**
* [`ProhibitedInterfaceBuilder`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_swift_bestpractices.html#prohibitedinterfacebuilder)
* [`UnavailableFunction`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_swift_bestpractices.html#unavailablefunction)
* [`ForceCast`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_swift_errorprone.html#forcecast)
* [`ForceTry`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_swift_errorprone.html#forcetry)

#### Changed Rules

**General changes**

* All statistical rules (like ExcessiveClassLength, ExcessiveParameterList) have been simplified and unified.
  The properties `topscore` and `sigma` have been removed. The property `minimum` is still there, however the type is not
  a decimal number anymore but has been changed to an integer. This affects rules in the languages Apex, Java, PLSQL
  and Velocity Template Language (vm):
    * Apex: [`ExcessiveClassLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_apex_design.html#excessiveclasslength), [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_apex_design.html#excessiveparameterlist),
      [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_apex_design.html#excessivepubliccount), [`NcssConstructorCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_apex_design.html#ncssconstructorcount),
      [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_apex_design.html#ncssmethodcount), [`NcssTypeCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_apex_design.html#ncsstypecount)
    * Java: [`ExcessiveImports`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#excessiveimports), [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#excessiveparameterlist),
      [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#excessivepubliccount), [`SwitchDensity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#switchdensity)
    * PLSQL: [`ExcessiveMethodLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_plsql_design.html#excessivemethodlength), [`ExcessiveObjectLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_plsql_design.html#excessiveobjectlength),
      [`ExcessivePackageBodyLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_plsql_design.html#excessivepackagebodylength), [`ExcessivePackageSpecificationLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_plsql_design.html#excessivepackagespecificationlength),
      [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_plsql_design.html#excessiveparameterlist), [`ExcessiveTypeLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_plsql_design.html#excessivetypelength),
      [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_plsql_design.html#ncssmethodcount), [`NcssObjectCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_plsql_design.html#ncssobjectcount),
      [`NPathComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_plsql_design.html#npathcomplexity)
    * VM: [`ExcessiveTemplateLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_vm_design.html#excessivetemplatelength)

* The general property `violationSuppressXPath` which is available for all rules to
  [suppress warnings](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_userdocs_suppressing_warnings.html) now uses XPath version 3.1 by default.
  This version of the XPath language is mostly identical to XPath 2.0. In PMD 6, XPath 1.0 has been used.
  If you upgrade from PMD 6, you need to verify your `violationSuppressXPath` properties.

**Apex General changes**

* The properties `cc_categories`, `cc_remediation_points_multiplier`, `cc_block_highlighting` have been removed
  from all rules. These properties have been deprecated since PMD 6.13.0.
  See [issue #1648](https://github.com/pmd/pmd/issues/1648) for more details.

**Java General changes**

* Violations reported on methods or classes previously reported the line range of the entire method
  or class. With PMD 7.0.0, the reported location is now just the identifier of the method or class.
  This affects various rules, e.g. [`CognitiveComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#cognitivecomplexity).

  The report location is controlled by the overrides of the method <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc4/net/sourceforge/pmd/lang/ast/Node.html#getReportLocation()"><code>getReportLocation</code></a>
  in different node types.

  See [issue #4439](https://github.com/pmd/pmd/issues/4439) and [issue #730](https://github.com/pmd/pmd/issues/730)
  for more details.

**Java Best Practices**

* [`ArrayIsStoredDirectly`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_bestpractices.html#arrayisstoreddirectly): Violations are now reported on the assignment and not
  anymore on the formal parameter. The reported line numbers will probably move.
* [`AvoidReassigningLoopVariables`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_bestpractices.html#avoidreassigningloopvariables): This rule might not report anymore all
  reassignments of the control variable in for-loops when the property `forReassign` is set to `skip`.
  See [issue #4500](https://github.com/pmd/pmd/issues/4500) for more details.
* [`LooseCoupling`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_bestpractices.html#loosecoupling): The rule has a new property to allow some types to be coupled
  to (`allowedTypes`).
* [`UnusedLocalVariable`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_bestpractices.html#unusedlocalvariable): This rule has some important false-negatives fixed
  and finds many more cases now. For details see issues [#2130](https://github.com/pmd/pmd/issues/2130),
  [#4516](https://github.com/pmd/pmd/issues/4516), and [#4517](https://github.com/pmd/pmd/issues/4517).

**Java Codestyle**

* [`MethodNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_codestyle.html#methodnamingconventions): The property `checkNativeMethods` has been removed. The
  property was deprecated since PMD 6.3.0. Use the property `nativePattern` to control whether native methods
  should be considered or not.
* [`ShortVariable`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_codestyle.html#shortvariable): This rule now also reports short enum constant names.
* [`UseDiamondOperator`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_codestyle.html#usediamondoperator): The property `java7Compatibility` has been removed. The rule now
  handles Java 7 properly without a property.
* [`UnnecessaryFullyQualifiedName`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_codestyle.html#unnecessaryfullyqualifiedname): The rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved
  to be more precise.
* [`UselessParentheses`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_codestyle.html#uselessparentheses): The rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.

**Java Design**

* [`CyclomaticComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#cyclomaticcomplexity): The property `reportLevel` has been removed. The property was
  deprecated since PMD 6.0.0. The report level can now be configured separated for classes and methods using
  `classReportLevel` and `methodReportLevel` instead.
* [`ImmutableField`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#immutablefield): The property `ignoredAnnotations` has been removed. The property was
  deprecated since PMD 6.52.0.
* [`LawOfDemeter`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#lawofdemeter): The rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.
* [`NPathComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#npathcomplexity): The property `minimum` has been removed. It was deprecated since PMD 6.0.0.
  Use the property `reportLevel` instead.
* [`SingularField`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#singularfield): The properties `checkInnerClasses` and `disallowNotAssignment` have been removed.
  The rule is now more precise and will check these cases properly.
* [`UseUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_design.html#useutilityclass): The property `ignoredAnnotations` has been removed.

**Java Documentation**

* [`CommentContent`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_documentation.html#commentcontent): The properties `caseSensitive` and `disallowedTerms` are removed. The
  new property `forbiddenRegex` can be used now to define the disallowed terms with a single regular
  expression.
* [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_documentation.html#commentrequired):
    * Overridden methods are now detected even without the `@Override`
      annotation. This is relevant for the property `methodWithOverrideCommentRequirement`.
      See also [pull request #3757](https://github.com/pmd/pmd/pull/3757).
    * Elements in annotation types are now detected as well. This might lead to an increased number of violations
      for missing public method comments.
* [`CommentSize`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_documentation.html#commentsize): When determining the line-length of a comment, the leading comment
  prefix markers (e.g. `*` or `//`) are ignored and don't add up to the line-length.
  See also [pull request #4369](https://github.com/pmd/pmd/pull/4369).

**Java Error Prone**

* [`AvoidDuplicateLiterals`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_errorprone.html#avoidduplicateliterals): The property `exceptionfile` has been removed. The property was
  deprecated since PMD 6.10.0. Use the property `exceptionList` instead.
* [`DontImportSun`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_errorprone.html#dontimportsun): `sun.misc.Signal` is not special-cased anymore.
* [`EmptyCatchBlock`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_errorprone.html#emptycatchblock): `CloneNotSupportedException` and `InterruptedException` are not
  special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* [`ImplicitSwitchFallThrough`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_rules_java_errorprone.html#implicitswitchfallthrough): Violations are now reported on the case statements
  rather than on the switch statements. This is more accurate but might result in more violations now.

#### Removed Rules

Many rules, that were previously deprecated have been finally removed.
See [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_release_notes_pmd7.html) for the complete list.

### 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Also, there are some improvement and changes in different areas. For the detailed description
of the changes listed here, see [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_release_notes_pmd7.html).

* Miscellaneous smaller changes and cleanups
* XPath 3.1 support for XPath-based rules
* Node stream API for AST traversal
* Metrics framework
* Testing framework
* Language Lifecycle and Language Properties
* Rule Properties
* New Programmatic API for CPD

### 💥 Compatibility and migration notes

A detailed documentation of required changes are available in the
[Migration Guide for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_userdocs_migrating_to_pmd7.html).

See also [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc4/pmd_release_notes_pmd7.html).

### 🐛 Fixed Issues

* miscellaneous
    * [#881](https://github.com/pmd/pmd/issues/881):   \[all] Breaking API changes for 7.0.0
    * [#896](https://github.com/pmd/pmd/issues/896):   \[all] Use slf4j
    * [#1431](https://github.com/pmd/pmd/pull/1431):   \[ui] Remove old GUI applications (designerold, bgastviewer)
    * [#1451](https://github.com/pmd/pmd/issues/1451): \[core] RulesetFactoryCompatibility stores the whole ruleset file in memory as a string
    * [#2496](https://github.com/pmd/pmd/issues/2496): Update PMD 7 Logo on landing page
    * [#2497](https://github.com/pmd/pmd/issues/2497): PMD 7 Logo page
    * [#2498](https://github.com/pmd/pmd/issues/2498): Update PMD 7 Logo in documentation
    * [#3797](https://github.com/pmd/pmd/issues/3797): \[all] Use JUnit5
    * [#4462](https://github.com/pmd/pmd/issues/4462): Provide Software Bill of Materials (SBOM)
    * [#4460](https://github.com/pmd/pmd/pull/4460):   Fix assembly-plugin warnings
    * [#4582](https://github.com/pmd/pmd/issues/4582): \[dist] Download link broken
    * [#4691](https://github.com/pmd/pmd/issues/4691): \[CVEs] Critical and High CEVs reported on PMD and PMD dependencies
* ant
    * [#4080](https://github.com/pmd/pmd/issues/4080): \[ant] Split off Ant integration into a new submodule
* core
    * [#880](https://github.com/pmd/pmd/issues/880):   \[core] Make visitors generic
    * [#1204](https://github.com/pmd/pmd/issues/1204): \[core] Allow numeric properties in XML to be within an unbounded range
    * [#1622](https://github.com/pmd/pmd/pull/1622):   \[core] NodeStream API
    * [#1687](https://github.com/pmd/pmd/issues/1687): \[core] Deprecate and Remove XPath 1.0 support
    * [#1785](https://github.com/pmd/pmd/issues/1785): \[core] Allow abstract node types to be valid rulechain visits
    * [#1825](https://github.com/pmd/pmd/pull/1825):   \[core] Support NoAttribute for XPath
    * [#2038](https://github.com/pmd/pmd/issues/2038): \[core] Remove DCD
    * [#2218](https://github.com/pmd/pmd/issues/2218): \[core] `isFindBoundary` should not be an attribute
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#2239](https://github.com/pmd/pmd/issues/2239): \[core] Merging Javacc build scripts
    * [#2500](https://github.com/pmd/pmd/issues/2500): \[core] Clarify API for ANTLR based languages
    * [#2518](https://github.com/pmd/pmd/issues/2518): \[core] Language properties
    * [#2602](https://github.com/pmd/pmd/issues/2602): \[core] Remove ParserOptions
    * [#2614](https://github.com/pmd/pmd/pull/2614):   \[core] Upgrade Saxon, add XPath 3.1, remove Jaxen
    * [#2696](https://github.com/pmd/pmd/pull/2696):   \[core] Remove DFA
    * [#2821](https://github.com/pmd/pmd/issues/2821): \[core] Rule processing error filenames are missing paths
    * [#2873](https://github.com/pmd/pmd/issues/2873): \[core] Utility classes in pmd 7
    * [#2885](https://github.com/pmd/pmd/issues/2885): \[core] Error recovery mode
    * [#3203](https://github.com/pmd/pmd/issues/3203): \[core] Replace RuleViolationFactory implementations with ViolationDecorator
    * [#3692](https://github.com/pmd/pmd/pull/3692):   \[core] Analysis listeners
    * [#3782](https://github.com/pmd/pmd/issues/3782): \[core] Language lifecycle
    * [#3815](https://github.com/pmd/pmd/issues/3815): \[core] Update Saxon HE to 10.7
    * [#3893](https://github.com/pmd/pmd/pull/3893):   \[core] Text documents
    * [#3902](https://github.com/pmd/pmd/issues/3902): \[core] Violation decorators
    * [#3918](https://github.com/pmd/pmd/issues/3918): \[core] Make LanguageRegistry non static
    * [#3919](https://github.com/pmd/pmd/issues/3919): \[core] Merge CPD and PMD language
    * [#3922](https://github.com/pmd/pmd/pull/3922):   \[core] Better error reporting for the ruleset parser
    * [#4035](https://github.com/pmd/pmd/issues/4035): \[core] ConcurrentModificationException in DefaultRuleViolationFactory
    * [#4120](https://github.com/pmd/pmd/issues/4120): \[core] Explicitly name all language versions
    * [#4204](https://github.com/pmd/pmd/issues/4204): \[core] Provide a CpdAnalysis class as a programmatic entry point into CPD
    * [#4301](https://github.com/pmd/pmd/issues/4301): \[core] Remove deprecated property concrete classes
    * [#4302](https://github.com/pmd/pmd/issues/4302): \[core] Migrate Property Framework API to Java 8
    * [#4323](https://github.com/pmd/pmd/issues/4323): \[core] Refactor CPD integration
    * [#4353](https://github.com/pmd/pmd/pull/4353):   \[core] Micro optimizations for Node API
    * [#4365](https://github.com/pmd/pmd/pull/4365):   \[core] Improve benchmarking
    * [#4397](https://github.com/pmd/pmd/pull/4397):   \[core] Refactor CPD
    * [#4420](https://github.com/pmd/pmd/pull/4420):   \[core] Remove PMD.EOL
    * [#4425](https://github.com/pmd/pmd/pull/4425):   \[core] Replace TextFile::pathId
    * [#4454](https://github.com/pmd/pmd/issues/4454): \[core] "Unknown option: '-min'" but is referenced in documentation
    * [#4611](https://github.com/pmd/pmd/pull/4611):   \[core] Fix loading language properties from env vars
    * [#4621](https://github.com/pmd/pmd/issues/4621): \[core] Make `ClasspathClassLoader::getResource` child first
* cli
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#3828](https://github.com/pmd/pmd/issues/3828): \[core] Progress reporting
    * [#4079](https://github.com/pmd/pmd/issues/4079): \[cli] Split off CLI implementation into a pmd-cli submodule
    * [#4423](https://github.com/pmd/pmd/pull/4423):   \[cli] Fix NPE when only `--file-list` is specified
    * [#4482](https://github.com/pmd/pmd/issues/4482): \[cli] pmd.bat can only be executed once
    * [#4484](https://github.com/pmd/pmd/issues/4484): \[cli] ast-dump with no properties produce an NPE
* doc
    * [#2501](https://github.com/pmd/pmd/issues/2501): \[doc] Verify ANTLR Documentation
    * [#4294](https://github.com/pmd/pmd/issues/4294): \[doc] Migration Guide for upgrading PMD 6 ➡️ 7
    * [#4303](https://github.com/pmd/pmd/issues/4303): \[doc] Document new property framework
    * [#4438](https://github.com/pmd/pmd/issues/4438): \[doc] Documentation links in VS Code are outdated
    * [#4521](https://github.com/pmd/pmd/issues/4521): \[doc] Website is not mobile friendly
* testing
    * [#2435](https://github.com/pmd/pmd/issues/2435): \[test] Remove duplicated Dummy language module
    * [#4234](https://github.com/pmd/pmd/issues/4234): \[test] Tests that change the logging level do not work

Language specific fixes:

* apex
    * [#1937](https://github.com/pmd/pmd/issues/1937): \[apex] Apex should only have a single RootNode
    * [#1648](https://github.com/pmd/pmd/issues/1648): \[apex,vf] Remove CodeClimate dependency
    * [#1750](https://github.com/pmd/pmd/pull/1750):   \[apex] Remove apex statistical rules
    * [#2836](https://github.com/pmd/pmd/pull/2836):   \[apex] Remove Apex ProjectMirror
    * [#3973](https://github.com/pmd/pmd/issues/3973): \[apex] Update parser to support new 'as user' keywords (User Mode for Database Operations)
    * [#4427](https://github.com/pmd/pmd/issues/4427): \[apex] ApexBadCrypto test failing to detect inline code
    * [#4453](https://github.com/pmd/pmd/issues/4453): \[apex] \[7.0-rc1] Exception while initializing Apexlink (Index 34812 out of bounds for length 34812)
* apex-design
    * [#2667](https://github.com/pmd/pmd/issues/2667): \[apex] Integrate nawforce/ApexLink to build robust Unused rule
    * [#4509](https://github.com/pmd/pmd/issues/4509): \[apex] ExcessivePublicCount doesn't consider inner classes correctly
    * [#4596](https://github.com/pmd/pmd/issues/4596): \[apex] ExcessivePublicCount ignores properties
* apex-security
    * [#4646](https://github.com/pmd/pmd/issues/4646): \[apex] ApexSOQLInjection does not recognise SObjectType or SObjectField as safe variable types
* java
    * [#520](https://github.com/pmd/pmd/issues/520):   \[java] Allow `@SuppressWarnings` with constants instead of literals
    * [#864](https://github.com/pmd/pmd/issues/864):   \[java] Similar/duplicated implementations for determining FQCN
    * [#905](https://github.com/pmd/pmd/issues/905):   \[java] Add new node for anonymous class declaration
    * [#910](https://github.com/pmd/pmd/issues/910):   \[java] AST inconsistency between primitive and reference type arrays
    * [#997](https://github.com/pmd/pmd/issues/997):   \[java] Java8 parsing corner case with annotated array types
    * [#998](https://github.com/pmd/pmd/issues/998):   \[java] AST inconsistencies around FormalParameter
    * [#1019](https://github.com/pmd/pmd/issues/1019): \[java] Breaking Java Grammar changes for PMD 7.0.0
    * [#1124](https://github.com/pmd/pmd/issues/1124): \[java] ImmutableList implementation in the qname codebase
    * [#1128](https://github.com/pmd/pmd/issues/1128): \[java] Improve ASTLocalVariableDeclaration
    * [#1150](https://github.com/pmd/pmd/issues/1150): \[java] ClassOrInterfaceType AST improvements
    * [#1207](https://github.com/pmd/pmd/issues/1207): \[java] Resolve explicit types using FQCNs, without hitting the classloader
    * [#1367](https://github.com/pmd/pmd/issues/1367): \[java] Parsing error on annotated inner class
    * [#1661](https://github.com/pmd/pmd/issues/1661): \[java] About operator nodes
    * [#2366](https://github.com/pmd/pmd/pull/2366):   \[java] Remove qualified names
    * [#2819](https://github.com/pmd/pmd/issues/2819): \[java] GLB bugs in pmd 7
    * [#3642](https://github.com/pmd/pmd/issues/3642): \[java] Parse error on rare extra dimensions on method return type on annotation methods
    * [#3763](https://github.com/pmd/pmd/issues/3763): \[java] Ambiguous reference error in valid code
    * [#3749](https://github.com/pmd/pmd/issues/3749): \[java] Improve `isOverridden` in ASTMethodDeclaration
    * [#3750](https://github.com/pmd/pmd/issues/3750): \[java] Make symbol table support instanceof pattern bindings
    * [#3752](https://github.com/pmd/pmd/issues/3752): \[java] Expose annotations in symbol API
    * [#4237](https://github.com/pmd/pmd/pull/4237):   \[java] Cleanup handling of Java comments
    * [#4317](https://github.com/pmd/pmd/issues/4317): \[java] Some AST nodes should not be TypeNodes
    * [#4359](https://github.com/pmd/pmd/issues/4359): \[java] Type resolution fails with NPE when the scope is not a type declaration
    * [#4367](https://github.com/pmd/pmd/issues/4367): \[java] Move testrule TypeResTest into internal
    * [#4383](https://github.com/pmd/pmd/issues/4383): \[java] IllegalStateException: Object is not an array type!
    * [#4401](https://github.com/pmd/pmd/issues/4401): \[java] PMD 7 fails to build under Java 19
    * [#4405](https://github.com/pmd/pmd/issues/4405): \[java] Processing error with ArrayIndexOutOfBoundsException
    * [#4583](https://github.com/pmd/pmd/issues/4583): \[java] Support JDK 21 (LTS)
* java-bestpractices
    * [#342](https://github.com/pmd/pmd/issues/342):   \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
    * [#755](https://github.com/pmd/pmd/issues/755):   \[java] AccessorClassGeneration false positive for private constructors
    * [#770](https://github.com/pmd/pmd/issues/770):   \[java] UnusedPrivateMethod yields false positive for counter-variant arguments
    * [#807](https://github.com/pmd/pmd/issues/807):   \[java] AccessorMethodGeneration false positive with overloads
    * [#833](https://github.com/pmd/pmd/issues/833):   \[java] ForLoopCanBeForeach should consider iterating on this
    * [#1189](https://github.com/pmd/pmd/issues/1189): \[java] UnusedPrivateMethod false positive from inner class via external class
    * [#1205](https://github.com/pmd/pmd/issues/1205): \[java] Improve ConstantsInInterface message to mention alternatives
    * [#1212](https://github.com/pmd/pmd/issues/1212): \[java] Don't raise JUnitTestContainsTooManyAsserts on JUnit 5's assertAll
    * [#1422](https://github.com/pmd/pmd/issues/1422): \[java] JUnitTestsShouldIncludeAssert false positive with inherited @<!-- -->Rule field
    * [#1455](https://github.com/pmd/pmd/issues/1455): \[java] JUnitTestsShouldIncludeAssert: False positives for assert methods named "check" and "verify"
    * [#1563](https://github.com/pmd/pmd/issues/1563): \[java] ForLoopCanBeForeach false positive with method call using index variable
    * [#1565](https://github.com/pmd/pmd/issues/1565): \[java] JUnitAssertionsShouldIncludeMessage false positive with AssertJ
    * [#1747](https://github.com/pmd/pmd/issues/1747): \[java] PreserveStackTrace false-positive
    * [#1969](https://github.com/pmd/pmd/issues/1969): \[java] MissingOverride false-positive triggered by package-private method overwritten in another package by extending class
    * [#1998](https://github.com/pmd/pmd/issues/1998): \[java] AccessorClassGeneration false-negative: subclass calls private constructor
    * [#2130](https://github.com/pmd/pmd/issues/2130): \[java] UnusedLocalVariable: false-negative with array
    * [#2147](https://github.com/pmd/pmd/issues/2147): \[java] JUnitTestsShouldIncludeAssert - false positives with lambdas and static methods
    * [#2464](https://github.com/pmd/pmd/issues/2464): \[java] LooseCoupling must ignore class literals: ArrayList.class
    * [#2542](https://github.com/pmd/pmd/issues/2542): \[java] UseCollectionIsEmpty can not detect the case `foo.bar().size()`
    * [#2650](https://github.com/pmd/pmd/issues/2650): \[java] UseTryWithResources false positive when AutoCloseable helper used
    * [#2796](https://github.com/pmd/pmd/issues/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
    * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
    * [#2822](https://github.com/pmd/pmd/issues/2822): \[java] LooseCoupling rule: Extend to cover user defined implementations and interfaces
    * [#2843](https://github.com/pmd/pmd/pull/2843):   \[java] Fix UnusedAssignment FP with field accesses
    * [#2882](https://github.com/pmd/pmd/issues/2882): \[java] UseTryWithResources - false negative for explicit close
    * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
    * [#2890](https://github.com/pmd/pmd/issues/2890): \[java] UnusedPrivateMethod false positive with generics
    * [#2946](https://github.com/pmd/pmd/issues/2946): \[java] SwitchStmtsShouldHaveDefault false positive on enum inside enums
    * [#3672](https://github.com/pmd/pmd/pull/3672):   \[java] LooseCoupling - fix false positive with generics
    * [#3675](https://github.com/pmd/pmd/pull/3675):   \[java] MissingOverride - fix false positive with mixing type vars
    * [#3858](https://github.com/pmd/pmd/issues/3858): \[java] UseCollectionIsEmpty should infer local variable type from method invocation
    * [#4433](https://github.com/pmd/pmd/issues/4433): \[java] \[7.0-rc1] ReplaceHashtableWithMap on java.util.Properties
    * [#4492](https://github.com/pmd/pmd/issues/4492): \[java] GuardLogStatement gives false positive when argument is a Java method reference
    * [#4503](https://github.com/pmd/pmd/issues/4503): \[java] JUnitTestsShouldIncludeAssert: false negative with TestNG
    * [#4516](https://github.com/pmd/pmd/issues/4516): \[java] UnusedLocalVariable: false-negative with try-with-resources
    * [#4517](https://github.com/pmd/pmd/issues/4517): \[java] UnusedLocalVariable: false-negative with compound assignments
    * [#4518](https://github.com/pmd/pmd/issues/4518): \[java] UnusedLocalVariable: false-positive with multiple for-loop indices
    * [#4634](https://github.com/pmd/pmd/issues/4634): \[java] JUnit4TestShouldUseTestAnnotation false positive with TestNG
* java-codestyle
    * [#1208](https://github.com/pmd/pmd/issues/1208): \[java] PrematureDeclaration rule false-positive on variable declared to measure time
    * [#1429](https://github.com/pmd/pmd/issues/1429): \[java] PrematureDeclaration as result of method call (false positive)
    * [#1480](https://github.com/pmd/pmd/issues/1480): \[java] IdenticalCatchBranches false positive with return expressions
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2134](https://github.com/pmd/pmd/issues/2134): \[java] PreserveStackTrace not handling `Throwable.addSuppressed(...)`
    * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
    * [#2391](https://github.com/pmd/pmd/issues/2391): \[java] UseDiamondOperator FP when expected type and constructed type have a different parameterization
    * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support ParameterizedTest
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
    * [#2748](https://github.com/pmd/pmd/issues/2748): \[java] UnnecessaryCast false positive with unchecked cast
    * [#2973](https://github.com/pmd/pmd/issues/2973): \[java] New rule: UnnecessaryBoxing
    * [#3195](https://github.com/pmd/pmd/pull/3195):   \[java] Improve rule UnnecessaryReturn to detect more cases
    * [#3218](https://github.com/pmd/pmd/pull/3218):   \[java] Generalize UnnecessaryCast to flag all unnecessary casts
    * [#3221](https://github.com/pmd/pmd/issues/3221): \[java] PrematureDeclaration false positive for unused variables
    * [#3238](https://github.com/pmd/pmd/issues/3238): \[java] Improve ExprContext, fix FNs of UnnecessaryCast
    * [#3500](https://github.com/pmd/pmd/pull/3500):   \[java] UnnecessaryBoxing - check for Integer.valueOf(String) calls
    * [#4268](https://github.com/pmd/pmd/issues/4268): \[java] CommentDefaultAccessModifier: false positive with TestNG annotations
    * [#4273](https://github.com/pmd/pmd/issues/4273): \[java] CommentDefaultAccessModifier ignoredAnnotations should include "org.junit.jupiter.api.extension.RegisterExtension" by default
    * [#4357](https://github.com/pmd/pmd/pull/4357):   \[java] Fix IllegalStateException in UseDiamondOperator rule
    * [#4432](https://github.com/pmd/pmd/issues/4432): \[java] \[7.0-rc1] UnnecessaryImport - Unused static import is being used
    * [#4455](https://github.com/pmd/pmd/issues/4455): \[java] FieldNamingConventions: false positive with lombok's @<!-- -->UtilityClass
    * [#4487](https://github.com/pmd/pmd/issues/4487): \[java] UnnecessaryConstructor: false-positive with @<!-- -->Inject and @<!-- -->Autowired
    * [#4511](https://github.com/pmd/pmd/issues/4511): \[java] LocalVariableCouldBeFinal shouldn't report unused variables
    * [#4512](https://github.com/pmd/pmd/issues/4512): \[java] MethodArgumentCouldBeFinal shouldn't report unused parameters
    * [#4557](https://github.com/pmd/pmd/issues/4557): \[java] UnnecessaryImport FP with static imports of overloaded methods
* java-design
    * [#1014](https://github.com/pmd/pmd/issues/1014): \[java] LawOfDemeter: False positive with lambda expression
    * [#1605](https://github.com/pmd/pmd/issues/1605): \[java] LawOfDemeter: False positive for standard UTF-8 charset name
    * [#2160](https://github.com/pmd/pmd/issues/2160): \[java] Issues with Law of Demeter
    * [#2175](https://github.com/pmd/pmd/issues/2175): \[java] LawOfDemeter: False positive for chained methods with generic method call
    * [#2179](https://github.com/pmd/pmd/issues/2179): \[java] LawOfDemeter: False positive with static property access - should treat class-level property as global object, not dot-accessed property
    * [#2180](https://github.com/pmd/pmd/issues/2180): \[java] LawOfDemeter: False positive with Thread and ThreadLocalRandom
    * [#2182](https://github.com/pmd/pmd/issues/2182): \[java] LawOfDemeter: False positive with package-private access
    * [#2188](https://github.com/pmd/pmd/issues/2188): \[java] LawOfDemeter: False positive with fields assigned to local vars
    * [#2536](https://github.com/pmd/pmd/issues/2536): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal can't detect inner class
    * [#3668](https://github.com/pmd/pmd/pull/3668):   \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - fix FP with inner private classes
    * [#3754](https://github.com/pmd/pmd/issues/3754): \[java] SingularField false positive with read in while condition
    * [#3786](https://github.com/pmd/pmd/issues/3786): \[java] SimplifyBooleanReturns should consider operator precedence
    * [#3840](https://github.com/pmd/pmd/issues/3840): \[java] LawOfDemeter disallows method call on locally created object
    * [#4238](https://github.com/pmd/pmd/pull/4238):   \[java] Make LawOfDemeter not use the rulechain
    * [#4254](https://github.com/pmd/pmd/issues/4254): \[java] ImmutableField - false positive with Lombok @<!-- -->Setter
    * [#4434](https://github.com/pmd/pmd/issues/4434): \[java] \[7.0-rc1] ExceptionAsFlowControl when simply propagating
    * [#4456](https://github.com/pmd/pmd/issues/4456): \[java] FinalFieldCouldBeStatic: false positive with lombok's @<!-- -->UtilityClass
    * [#4477](https://github.com/pmd/pmd/issues/4477): \[java] SignatureDeclareThrowsException: false-positive with TestNG annotations
    * [#4490](https://github.com/pmd/pmd/issues/4490): \[java] ImmutableField - false negative with Lombok @<!-- -->Getter
    * [#4549](https://github.com/pmd/pmd/pull/4549):   \[java] Make LawOfDemeter results deterministic
* java-documentation
    * [#4369](https://github.com/pmd/pmd/pull/4369):   \[java] Improve CommentSize
    * [#4416](https://github.com/pmd/pmd/pull/4416):   \[java] Fix reported line number in CommentContentRule
* java-errorprone
    * [#659](https://github.com/pmd/pmd/issues/659):   \[java] MissingBreakInSwitch - last default case does not contain a break
    * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
    * [#1669](https://github.com/pmd/pmd/issues/1669): \[java] NullAssignment - FP with ternay and null as constructor argument
    * [#1899](https://github.com/pmd/pmd/issues/1899): \[java] Recognize @<!-- -->SuppressWanings("fallthrough") for MissingBreakInSwitch
    * [#2320](https://github.com/pmd/pmd/issues/2320): \[java] NullAssignment - FP with ternary and null as method argument
    * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case `new BigDecimal(Expression)`
    * [#2579](https://github.com/pmd/pmd/issues/2579): \[java] MissingBreakInSwitch detects the lack of break in the last case
    * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
    * [#2893](https://github.com/pmd/pmd/issues/2893): \[java] Remove special cases from rule EmptyCatchBlock
    * [#2894](https://github.com/pmd/pmd/issues/2894): \[java] Improve MissingBreakInSwitch
    * [#3071](https://github.com/pmd/pmd/issues/3071): \[java] BrokenNullCheck FP with PMD 6.30.0
    * [#3087](https://github.com/pmd/pmd/issues/3087): \[java] UnnecessaryBooleanAssertion overlaps with SimplifiableTestAssertion
    * [#3100](https://github.com/pmd/pmd/issues/3100): \[java] UseCorrectExceptionLogging FP in 6.31.0
    * [#3173](https://github.com/pmd/pmd/issues/3173): \[java] UseProperClassLoader false positive
    * [#3351](https://github.com/pmd/pmd/issues/3351): \[java] ConstructorCallsOverridableMethod ignores abstract methods
    * [#3400](https://github.com/pmd/pmd/issues/3400): \[java] AvoidUsingOctalValues FN with underscores
    * [#3843](https://github.com/pmd/pmd/issues/3843): \[java] UseEqualsToCompareStrings should consider return type
    * [#4063](https://github.com/pmd/pmd/issues/4063): \[java] AvoidBranchingStatementAsLastInLoop: False-negative about try/finally block
    * [#4356](https://github.com/pmd/pmd/pull/4356):   \[java] Fix NPE in CloseResourceRule
    * [#4449](https://github.com/pmd/pmd/issues/4449): \[java] AvoidAccessibilityAlteration: Possible false positive in AvoidAccessibilityAlteration rule when using Lambda expression
    * [#4457](https://github.com/pmd/pmd/issues/4457): \[java] OverrideBothEqualsAndHashcode: false negative with anonymous classes
    * [#4493](https://github.com/pmd/pmd/issues/4493): \[java] MissingStaticMethodInNonInstantiatableClass: false-positive about @<!-- -->Inject
    * [#4505](https://github.com/pmd/pmd/issues/4505): \[java] ImplicitSwitchFallThrough NPE in PMD 7.0.0-rc1
    * [#4510](https://github.com/pmd/pmd/issues/4510): \[java] ConstructorCallsOverridableMethod: false positive with lombok's @<!-- -->Value
    * [#4513](https://github.com/pmd/pmd/issues/4513): \[java] UselessOperationOnImmutable various false negatives with String
    * [#4514](https://github.com/pmd/pmd/issues/4514): \[java] AvoidLiteralsInIfCondition false positive and negative for String literals when ignoreExpressions=true
    * [#4546](https://github.com/pmd/pmd/issues/4546): \[java] OverrideBothEqualsAndHashCode ignores records
* java-multithreading
    * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
    * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
    * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
    * [#4483](https://github.com/pmd/pmd/issues/4483): \[java] NonThreadSafeSingleton false positive with double-checked locking
* java-performance
    * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
    * [#2587](https://github.com/pmd/pmd/issues/2587): \[java] AvoidArrayLoops could also check for list copy through iterated List.add()
    * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ
    * [#3486](https://github.com/pmd/pmd/pull/3486):   \[java] InsufficientStringBufferDeclaration: Fix NPE
    * [#3848](https://github.com/pmd/pmd/issues/3848): \[java] StringInstantiation: false negative when using method result
    * [#4070](https://github.com/pmd/pmd/issues/4070): \[java] A false positive about the rule RedundantFieldInitializer
    * [#4458](https://github.com/pmd/pmd/issues/4458): \[java] RedundantFieldInitializer: false positive with lombok's @<!-- -->Value
* kotlin
    * [#419](https://github.com/pmd/pmd/issues/419):   \[kotlin] Add support for Kotlin
    * [#4389](https://github.com/pmd/pmd/pull/4389):   \[kotlin] Update grammar to version 1.8
* swift
    * [#1877](https://github.com/pmd/pmd/pull/1877):   \[swift] Feature/swift rules
    * [#1882](https://github.com/pmd/pmd/pull/1882):   \[swift] UnavailableFunction Swift rule
* xml
    * [#1800](https://github.com/pmd/pmd/pull/1800):   \[xml] Unimplement org.w3c.dom.Node from the XmlNodeWrapper

### ✨ External Contributions

* [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1881](https://github.com/pmd/pmd/pull/1881): \[doc] Add ANTLR documentation - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca) (@tomidelucca)
* [#2830](https://github.com/pmd/pmd/pull/2830): \[apex] Apexlink POC - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#3866](https://github.com/pmd/pmd/pull/3866): \[core] Add CLI Progress Bar - [@JerritEic](https://github.com/JerritEic) (@JerritEic)
* [#4402](https://github.com/pmd/pmd/pull/4402): \[javascript] CPD: add support for Typescript using antlr4 grammar - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4403](https://github.com/pmd/pmd/pull/4403): \[julia] CPD: Add support for Julia code duplication  - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4412](https://github.com/pmd/pmd/pull/4412): \[doc] Added new error msg to ConstantsInInterface - [David Ljunggren](https://github.com/dague1) (@dague1)
* [#4426](https://github.com/pmd/pmd/pull/4426): \[cpd] New XML to HTML XLST report format for PMD CPD - [mohan-chinnappan-n](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)
* [#4428](https://github.com/pmd/pmd/pull/4428): \[apex] ApexBadCrypto bug fix for #4427 - inline detection of hard coded values - [Steven Stearns](https://github.com/sfdcsteve) (@sfdcsteve)
* [#4431](https://github.com/pmd/pmd/pull/4431): \[coco] CPD: Coco support for code duplication detection - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4444](https://github.com/pmd/pmd/pull/4444): \[java] CommentDefaultAccessModifier - ignore org.junit.jupiter.api.extension.RegisterExtension by default - [Nirvik Patel](https://github.com/nirvikpatel) (@nirvikpatel)
* [#4450](https://github.com/pmd/pmd/pull/4450): \[java] Fix #4449 AvoidAccessibilityAlteration: Correctly handle Lambda expressions in PrivilegedAction scenarios - [Seren](https://github.com/mohui1999) (@mohui1999)
* [#4452](https://github.com/pmd/pmd/pull/4452): \[doc] Update PMD_APEX_ROOT_DIRECTORY documentation reference - [nwcm](https://github.com/nwcm) (@nwcm)
* [#4470](https://github.com/pmd/pmd/pull/4470): \[cpp] CPD: Added strings as literal and ignore identifiers in sequences - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4474](https://github.com/pmd/pmd/pull/4474): \[java] ImmutableField: False positive with lombok (fixes #4254) - [Pim van der Loos](https://github.com/PimvanderLoos) (@PimvanderLoos)
* [#4488](https://github.com/pmd/pmd/pull/4488): \[java] Fix #4477: A false-positive about SignatureDeclareThrowsException - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4494](https://github.com/pmd/pmd/pull/4494): \[java] Fix #4487: A false-positive about UnnecessaryConstructor and @<!-- -->Inject and @<!-- -->Autowired - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4495](https://github.com/pmd/pmd/pull/4495): \[java] Fix #4493: false-positive about MissingStaticMethodInNonInstantiatableClass and @<!-- -->Inject - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4507](https://github.com/pmd/pmd/pull/4507): \[java] Fix #4503: A false negative about JUnitTestsShouldIncludeAssert and testng - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4520](https://github.com/pmd/pmd/pull/4520): \[doc] Fix typo: missing closing quotation mark after CPD-END - [João Dinis Ferreira](https://github.com/joaodinissf) (@joaodinissf)
* [#4528](https://github.com/pmd/pmd/pull/4528): \[apex] Update to apexlink - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#4533](https://github.com/pmd/pmd/pull/4533): \[java] Fix #4063: False-negative about try/catch block in Loop - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4536](https://github.com/pmd/pmd/pull/4536): \[java] Fix #4268: CommentDefaultAccessModifier - false positive with TestNG's @<!-- -->Test annotation - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4537](https://github.com/pmd/pmd/pull/4537): \[java] Fix #4455: A false positive about FieldNamingConventions and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4538](https://github.com/pmd/pmd/pull/4538): \[java] Fix #4456: A false positive about FinalFieldCouldBeStatic and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4540](https://github.com/pmd/pmd/pull/4540): \[java] Fix #4457: false negative about OverrideBothEqualsAndHashcode - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4541](https://github.com/pmd/pmd/pull/4541): \[java] Fix #4458: A false positive about RedundantFieldInitializer and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4542](https://github.com/pmd/pmd/pull/4542): \[java] Fix #4510: A false positive about ConstructorCallsOverridableMethod and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4553](https://github.com/pmd/pmd/pull/4553): \[java] Fix #4492: GuardLogStatement gives false positive when argument is a Java method reference - [Anastasiia Koba](https://github.com/anastasiia-koba) (@anastasiia-koba)
* [#4637](https://github.com/pmd/pmd/pull/4637): \[java] fix #4634 - JUnit4TestShouldUseTestAnnotation false positive with TestNG - [Krystian Dabrowski](https://github.com/krdabrowski) (@krdabrowski)
* [#4649](https://github.com/pmd/pmd/pull/4649): \[apex] Add SObjectType and SObjectField to list of injectable SOQL variable types - [Richard Corfield](https://github.com/rcorfieldffdc) (@rcorfieldffdc)
* [#4651](https://github.com/pmd/pmd/pull/4651): \[doc] Add "Tencent Cloud Code Analysis" in Tools / Integrations - [yale](https://github.com/cyw3) (@cyw3)
* [#4664](https://github.com/pmd/pmd/pull/4664): \[cli] CPD: Fix NPE when only `--file-list` is specified - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4665](https://github.com/pmd/pmd/pull/4665): \[java] Doc: Fix references AutoClosable -> AutoCloseable - [Andrey Bozhko](https://github.com/AndreyBozhko) (@AndreyBozhko)

### 📈 Stats
* 5007 commits
* 658 closed tickets & PRs
* Days since last release: 122



## 30-May-2023 - 7.0.0-rc3

We're excited to bring you the next major version of PMD!

Since this is a big release, we provide here only a concise version of the release notes. We prepared a separate
page with the full [Detailed Release Notes for PMD 7.0.0](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_release_notes_pmd7.html).

<div style="border: 1px solid; border-radius: .25rem; padding: .75rem 1.25rem;" role="alert">
<strong>ℹ️ Release Candidates</strong>
<p>PMD 7.0.0 is finally almost ready. In order to gather feedback, we are going to ship a couple of release candidates.
These are officially available on GitHub and Maven Central and can be used as usual (e.g. as a dependency).
We encourage you to try out the new features, but keep in mind that we may introduce API breaking changes between
the release candidates. It should be stable enough if you don't use custom rules.</p>

<p>We have still some tasks planned for the next release candidates.
You can see the progress in <a href="https://github.com/pmd/pmd/issues/3898">PMD 7 Tracking Issue #3898</a>.</p>

<p>If you find any problem or difficulty while updating from PMD 6, please provide feedback via our
<a href="https://github.com/pmd/pmd/issues/new/choose">issue tracker</a>. That way we can improve the experience
for all.</p>
</div>

### Table Of Contents

* [Changes since 7.0.0-rc2](#changes-since-7.0.0-rc2)
    * [New CPD report format cpdhtml-v2.xslt](#new-cpd-report-format-cpdhtml-v2.xslt)
    * [Fixed issues](#fixed-issues)
    * [API Changes](#api-changes)
    * [External Contributions](#external-contributions)
* [🚀 Major Features and Enhancements](#🚀-major-features-and-enhancements)
    * [New official logo](#new-official-logo)
    * [Revamped Java module](#revamped-java-module)
    * [Revamped Command Line Interface](#revamped-command-line-interface)
    * [Full Antlr support](#full-antlr-support)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [New CPD report format cpdhtml-v2.xslt](#new-cpd-report-format-cpdhtml-v2.xslt)
* [🎉 Language Related Changes](#🎉-language-related-changes)
    * [New: Swift support](#new:-swift-support)
    * [New: Kotlin support (experimental)](#new:-kotlin-support-(experimental))
    * [New: CPD support for TypeScript](#new:-cpd-support-for-typescript)
    * [New: CPD support for Julia](#new:-cpd-support-for-julia)
    * [New: CPD support for Coco](#new:-cpd-support-for-coco)
    * [Changed: JavaScript support](#changed:-javascript-support)
    * [Changed: Language versions](#changed:-language-versions)
    * [Changed: CPP can now ignore identifiers in sequences (CPD)](#changed:-cpp-can-now-ignore-identifiers-in-sequences-(cpd))
* [🌟 New and changed rules](#🌟-new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
    * [Removed Rules](#removed-rules)
* [🚨 API](#🚨-api)
* [💥 Compatibility and migration notes](#💥-compatibility-and-migration-notes)
* [🐛 Fixed Issues](#🐛-fixed-issues)
* [✨ External Contributions](#✨-external-contributions)
* [📈 Stats](#📈-stats)

### Changes since 7.0.0-rc2

This section lists the most important changes from the last release candidate.
The remaining section describes the complete release notes for 7.0.0.

#### New CPD report format cpdhtml-v2.xslt

Thanks to @mohan-chinnappan-n a new CPD report format has been added which features a data table.
It uses an XSLT stylesheet to convert CPD's XML format into HTML.

See [the example report](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/report-examples/cpdhtml-v2.html).

#### Fixed issues

* miscellaneous
  * [#4460](https://github.com/pmd/pmd/pull/4460):   Fix assembly-plugin warnings
* core
  * [#4425](https://github.com/pmd/pmd/pull/4425):   \[core] Replace TextFile::pathId
  * [#4454](https://github.com/pmd/pmd/issues/4454): \[core] "Unknown option: '-min'" but is referenced in documentation
* java-bestpractices
  * [#4433](https://github.com/pmd/pmd/issues/4433): \[java] \[7.0-rc1] ReplaceHashtableWithMap on java.util.Properties
  * [#4492](https://github.com/pmd/pmd/issues/4492): \[java] GuardLogStatement gives false positive when argument is a Java method reference
  * [#4503](https://github.com/pmd/pmd/issues/4503): \[java] JUnitTestsShouldIncludeAssert: false negative with TestNG
* java-codestyle
  * [#4268](https://github.com/pmd/pmd/issues/4268): \[java] CommentDefaultAccessModifier: false positive with TestNG annotations
  * [#4432](https://github.com/pmd/pmd/issues/4432): \[java] \[7.0-rc1] UnnecessaryImport - Unused static import is being used
  * [#4455](https://github.com/pmd/pmd/issues/4455): \[java] FieldNamingConventions: false positive with lombok's @<!-- -->UtilityClass
  * [#4557](https://github.com/pmd/pmd/issues/4557): \[java] UnnecessaryImport FP with static imports of overloaded methods
* java-design
  * [#4434](https://github.com/pmd/pmd/issues/4434): \[java] \[7.0-rc1] ExceptionAsFlowControl when simply propagating
  * [#4456](https://github.com/pmd/pmd/issues/4456): \[java] FinalFieldCouldBeStatic: false positive with lombok's @<!-- -->UtilityClass
  * [#4549](https://github.com/pmd/pmd/pull/4549):   \[java] Make LawOfDemeter results deterministic
* java-errorprone
  * [#4063](https://github.com/pmd/pmd/issues/4063): \[java] AvoidBranchingStatementAsLastInLoop: False-negative about try/finally block
  * [#4457](https://github.com/pmd/pmd/issues/4457): \[java] OverrideBothEqualsAndHashcode: false negative with anonymous classes
  * [#4510](https://github.com/pmd/pmd/issues/4510): \[java] ConstructorCallsOverridableMethod: false positive with lombok's @<!-- -->Value
  * [#4546](https://github.com/pmd/pmd/issues/4546): \[java] OverrideBothEqualsAndHashCode ignores records
* java-performance
  * [#4458](https://github.com/pmd/pmd/issues/4458): \[java] RedundantFieldInitializer: false positive with lombok's @<!-- -->Value

#### API Changes

* The following previously deprecated classes have been removed:
  * pmd-core
    * `net.sourceforge.pmd.PMD`
    * `net.sourceforge.pmd.cli.PMDCommandLineInterface`
    * `net.sourceforge.pmd.cli.PMDParameters`
    * `net.sourceforge.pmd.cli.PmdParametersParseResult`
* The asset filenames of PMD on [GitHub Releases](https://github.com/pmd/pmd/releases) are
  now `pmd-dist-<version>-bin.zip`, `pmd-dist-<version>-src.zip` and `pmd-dist-<version>-doc.zip`.
  Keep that in mind, if you have an automated download script.

  The structure inside the ZIP files stay the same, e.g. we still provide inside the binary distribution
  ZIP file the base directory `pmd-bin-<version>`.
* The CLI option `--stress` (or `-stress`) has been removed without replacement.
* The CLI option `--minimum-priority` was changed with 7.0.0-rc1 to only take the following values:
  High, Medium High, Medium, Medium Low, Low. With 7.0.0-rc2 compatibility has been restored, so that the equivalent
  integer values (1 to 5) are supported as well.
* Replaced `RuleViolation::getFilename` with new <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc3/net/sourceforge/pmd/RuleViolation.html#getFileId()"><code>RuleViolation#getFileId</code></a>, that returns a
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc3/net/sourceforge/pmd/lang/document/FileId.html#"><code>FileId</code></a>. This is an identifier for a <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc3/net/sourceforge/pmd/lang/document/TextFile.html#"><code>TextFile</code></a>
  and could represent a path name. This allows to have a separate display name, e.g. renderers use
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc3/net/sourceforge/pmd/reporting/FileNameRenderer.html#"><code>FileNameRenderer</code></a> to either display the full path name or a relative path name
  (see <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc3/net/sourceforge/pmd/renderers/Renderer.html#setFileNameRenderer(net.sourceforge.pmd.reporting.FileNameRenderer)"><code>Renderer#setFileNameRenderer</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc3/net/sourceforge/pmd/reporting/ConfigurableFileNameRenderer.html#"><code>ConfigurableFileNameRenderer</code></a>). Many places where we used a simple String for
  a path-like name before have been adapted to use the new <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc3/net/sourceforge/pmd/lang/document/FileId.html#"><code>FileId</code></a>.

  See [PR #4425](https://github.com/pmd/pmd/pull/4425) for details.

#### External Contributions

* [#4426](https://github.com/pmd/pmd/pull/4426): \[cpd] New XML to HTML XLST report format for PMD CPD - [mohan-chinnappan-n](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)
* [#4431](https://github.com/pmd/pmd/pull/4431): \[coco] CPD: Coco support for code duplication detection - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4470](https://github.com/pmd/pmd/pull/4470): \[cpp] CPD: Added strings as literal and ignore identifiers in sequences - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4507](https://github.com/pmd/pmd/pull/4507): \[java] Fix #4503: A false negative about JUnitTestsShouldIncludeAssert and testng - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4533](https://github.com/pmd/pmd/pull/4533): \[java] Fix #4063: False-negative about try/catch block in Loop - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4536](https://github.com/pmd/pmd/pull/4536): \[java] Fix #4268: CommentDefaultAccessModifier - false positive with TestNG's @<!-- -->Test annotation - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4537](https://github.com/pmd/pmd/pull/4537): \[java] Fix #4455: A false positive about FieldNamingConventions and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4538](https://github.com/pmd/pmd/pull/4538): \[java] Fix #4456: A false positive about FinalFieldCouldBeStatic and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4540](https://github.com/pmd/pmd/pull/4540): \[java] Fix #4457: false negative about OverrideBothEqualsAndHashcode - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4541](https://github.com/pmd/pmd/pull/4541): \[java] Fix #4458: A false positive about RedundantFieldInitializer and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4542](https://github.com/pmd/pmd/pull/4542): \[java] Fix #4510: A false positive about ConstructorCallsOverridableMethod and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4553](https://github.com/pmd/pmd/pull/4553): \[java] Fix #4492: GuardLogStatement gives false positive when argument is a Java method reference - [Anastasiia Koba](https://github.com/anastasiia-koba) (@anastasiia-koba)

### 🚀 Major Features and Enhancements

#### New official logo

The new official logo of PMD:

![New PMD Logo](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/images/logo/pmd-logo-300px.png)

#### Revamped Java module

* Java grammar substantially refactored - more correct regarding the Java Language Specification (JLS)
* Built-in rules have been upgraded for the changed AST
* Rewritten type resolution framework and symbol table correctly implements the JLS
* AST exposes more semantic information (method calls, field accesses)

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_release_notes_pmd7.html).

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Revamped Command Line Interface

* unified and consistent Command Line Interface for both Linux/Unix and Windows across our different utilities
* single script `pmd` (`pmd.bat` for Windows) to launch the different utilities:
    * `pmd check` to run PMD rules and analyze a project
    * `pmd cpd` to run CPD (copy paste detector)
    * `pmd designer` to run the PMD Rule Designer
* progress bar support for `pmd check`
* shell completion

![Demo](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/images/userdocs/pmd-demo.gif)

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_release_notes_pmd7.html).

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Full Antlr support

* [Antlr](https://www.antlr.org/) based grammars can now be used to build full-fledged PMD rules.
* Previously, Antlr grammar could only be used for CPD
* New supported languages: Swift and Kotlin

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_release_notes_pmd7.html).

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc1).

#### New CPD report format cpdhtml-v2.xslt

Thanks to @mohan-chinnappan-n a new CPD report format has been added which features a data table.
It uses an XSLT stylesheet to convert CPD's XML format into HTML.

See [the example report](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/report-examples/cpdhtml-v2.html).

### 🎉 Language Related Changes

Note that this is just a concise listing of the highlight.
For more information on the languages, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_release_notes_pmd7.html).

#### New: Swift support

* use PMD to analyze Swift code with PMD rules
* initially 4 built-in rules

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### New: Kotlin support (experimental)

* use PMD to analyze Kotlin code with PMD rules
* Support for Kotlin 1.8 grammar
* initially 2 built-in rules

#### New: CPD support for TypeScript

Thanks to a contribution, CPD now supports the TypeScript language. It is shipped
with the rest of the JavaScript support in the module `pmd-javascript`.

Contributors: [Paul Guyot](https://github.com/pguyot) (@pguyot)

#### New: CPD support for Julia

Thanks to a contribution, CPD now supports the Julia language. It is shipped
in the new module `pmd-julia`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: CPD support for Coco

Thanks to a contribution, CPD now supports Coco, a modern programming language
designed specifically for building event-driven software. It is shipped in the new
module `pmd-coco`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### Changed: JavaScript support

* latest version supports ES6 and also some new constructs (see [Rhino](https://github.com/mozilla/rhino)])
* comments are retained

#### Changed: Language versions

* more predefined language versions for each supported language
* can be used to limit rule execution for specific versions only with `minimumLanguageVersion` and
  `maximumLanguageVersion` attributes.

#### Changed: CPP can now ignore identifiers in sequences (CPD)

* new command line option for CPD: `--ignore-sequences`.
* This option is used for CPP only: with the already existing option `--ignore-literal-sequences`, only
  literals were ignored. The new option additional ignores identifiers as well in sequences.
* See [PR #4470](https://github.com/pmd/pmd/pull/4470) for details.

### 🌟 New and changed rules

#### New Rules

**Apex**
* [`UnusedMethod`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_apex_design.html#unusedmethod) finds unused methods in your code.

**Java**
* [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_codestyle.html#unnecessaryboxing) reports boxing and unboxing conversions that may be made implicit.

**Kotlin**
* [`FunctionNameTooShort`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_kotlin_bestpractices.html#functionnametooshort)
* [`OverrideBothEqualsAndHashcode`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_kotlin_errorprone.html#overridebothequalsandhashcode)

**Swift**
* [`ProhibitedInterfaceBuilder`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_swift_bestpractices.html#prohibitedinterfacebuilder)
* [`UnavailableFunction`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_swift_bestpractices.html#unavailablefunction)
* [`ForceCast`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_swift_errorprone.html#forcecast)
* [`ForceTry`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_swift_errorprone.html#forcetry)

#### Changed Rules

**General changes**

* All statistical rules (like ExcessiveClassLength, ExcessiveParameterList) have been simplified and unified.
  The properties `topscore` and `sigma` have been removed. The property `minimum` is still there, however the type is not
  a decimal number anymore but has been changed to an integer. This affects rules in the languages Apex, Java, PLSQL
  and Velocity Template Language (vm):
    * Apex: [`ExcessiveClassLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_apex_design.html#excessiveclasslength), [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_apex_design.html#excessiveparameterlist),
      [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_apex_design.html#excessivepubliccount), [`NcssConstructorCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_apex_design.html#ncssconstructorcount),
      [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_apex_design.html#ncssmethodcount), [`NcssTypeCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_apex_design.html#ncsstypecount)
    * Java: [`ExcessiveImports`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#excessiveimports), [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#excessiveparameterlist),
      [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#excessivepubliccount), [`SwitchDensity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#switchdensity)
    * PLSQL: [`ExcessiveMethodLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_plsql_design.html#excessivemethodlength), [`ExcessiveObjectLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_plsql_design.html#excessiveobjectlength),
      [`ExcessivePackageBodyLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_plsql_design.html#excessivepackagebodylength), [`ExcessivePackageSpecificationLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_plsql_design.html#excessivepackagespecificationlength),
      [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_plsql_design.html#excessiveparameterlist), [`ExcessiveTypeLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_plsql_design.html#excessivetypelength),
      [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_plsql_design.html#ncssmethodcount), [`NcssObjectCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_plsql_design.html#ncssobjectcount),
      [`NPathComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_plsql_design.html#npathcomplexity)
    * VM: [`ExcessiveTemplateLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_vm_design.html#excessivetemplatelength)

* The general property `violationSuppressXPath` which is available for all rules to
  [suppress warnings](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_userdocs_suppressing_warnings.html) now uses XPath version 3.1 by default.
  This version of the XPath language is mostly identical to XPath 2.0. In PMD 6, XPath 1.0 has been used.
  If you upgrade from PMD 6, you need to verify your `violationSuppressXPath` properties.

**Apex General changes**

* The properties `cc_categories`, `cc_remediation_points_multiplier`, `cc_block_highlighting` have been removed
  from all rules. These properties have been deprecated since PMD 6.13.0.
  See [issue #1648](https://github.com/pmd/pmd/issues/1648) for more details.

**Java General changes**

* Violations reported on methods or classes previously reported the line range of the entire method
  or class. With PMD 7.0.0, the reported location is now just the identifier of the method or class.
  This affects various rules, e.g. [`CognitiveComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#cognitivecomplexity).

  The report location is controlled by the overrides of the method <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc3/net/sourceforge/pmd/lang/ast/Node.html#getReportLocation()"><code>getReportLocation</code></a>
  in different node types.

  See [issue #4439](https://github.com/pmd/pmd/issues/4439) and [issue #730](https://github.com/pmd/pmd/issues/730)
  for more details.

**Java Best Practices**

* [`ArrayIsStoredDirectly`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_bestpractices.html#arrayisstoreddirectly): Violations are now reported on the assignment and not
  anymore on the formal parameter. The reported line numbers will probably move.
* [`AvoidReassigningLoopVariables`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_bestpractices.html#avoidreassigningloopvariables): This rule might not report anymore all
  reassignments of the control variable in for-loops when the property `forReassign` is set to `skip`.
  See [issue #4500](https://github.com/pmd/pmd/issues/4500) for more details.
* [`LooseCoupling`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_bestpractices.html#loosecoupling): The rule has a new property to allow some types to be coupled
  to (`allowedTypes`).
* [`UnusedLocalVariable`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_bestpractices.html#unusedlocalvariable): This rule has some important false-negatives fixed
  and finds many more cases now. For details see issues [#2130](https://github.com/pmd/pmd/issues/2130),
  [#4516](https://github.com/pmd/pmd/issues/4516), and [#4517](https://github.com/pmd/pmd/issues/4517).

**Java Codestyle**

* [`MethodNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_codestyle.html#methodnamingconventions): The property `checkNativeMethods` has been removed. The
  property was deprecated since PMD 6.3.0. Use the property `nativePattern` to control whether native methods
  should be considered or not.
* [`ShortVariable`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_codestyle.html#shortvariable): This rule now also reports short enum constant names.
* [`UseDiamondOperator`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_codestyle.html#usediamondoperator): The property `java7Compatibility` has been removed. The rule now
  handles Java 7 properly without a property.
* [`UnnecessaryFullyQualifiedName`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_codestyle.html#unnecessaryfullyqualifiedname): The rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved
  to be more precise.
* [`UselessParentheses`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_codestyle.html#uselessparentheses): The rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.

**Java Design**

* [`CyclomaticComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#cyclomaticcomplexity): The property `reportLevel` has been removed. The property was
  deprecated since PMD 6.0.0. The report level can now be configured separated for classes and methods using
  `classReportLevel` and `methodReportLevel` instead.
* [`ImmutableField`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#immutablefield): The property `ignoredAnnotations` has been removed. The property was
  deprecated since PMD 6.52.0.
* [`LawOfDemeter`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#lawofdemeter): The rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.
* [`NPathComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#npathcomplexity): The property `minimum` has been removed. It was deprecated since PMD 6.0.0.
  Use the property `reportLevel` instead.
* [`SingularField`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#singularfield): The properties `checkInnerClasses` and `disallowNotAssignment` have been removed.
  The rule is now more precise and will check these cases properly.
* [`UseUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_design.html#useutilityclass): The property `ignoredAnnotations` has been removed.

**Java Documentation**

* [`CommentContent`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_documentation.html#commentcontent): The properties `caseSensitive` and `disallowedTerms` are removed. The
  new property `forbiddenRegex` can be used now to define the disallowed terms with a single regular
  expression.
* [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_documentation.html#commentrequired):
    * Overridden methods are now detected even without the `@Override`
      annotation. This is relevant for the property `methodWithOverrideCommentRequirement`.
      See also [pull request #3757](https://github.com/pmd/pmd/pull/3757).
    * Elements in annotation types are now detected as well. This might lead to an increased number of violations
      for missing public method comments.
* [`CommentSize`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_documentation.html#commentsize): When determining the line-length of a comment, the leading comment
  prefix markers (e.g. `*` or `//`) are ignored and don't add up to the line-length.
  See also [pull request #4369](https://github.com/pmd/pmd/pull/4369).

**Java Error Prone**

* [`AvoidDuplicateLiterals`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_errorprone.html#avoidduplicateliterals): The property `exceptionfile` has been removed. The property was
  deprecated since PMD 6.10.0. Use the property `exceptionList` instead.
* [`DontImportSun`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_errorprone.html#dontimportsun): `sun.misc.Signal` is not special-cased anymore.
* [`EmptyCatchBlock`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_errorprone.html#emptycatchblock): `CloneNotSupportedException` and `InterruptedException` are not
  special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* [`ImplicitSwitchFallThrough`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_rules_java_errorprone.html#implicitswitchfallthrough): Violations are now reported on the case statements
  rather than on the switch statements. This is more accurate but might result in more violations now.

#### Removed Rules

Many rules, that were previously deprecated have been finally removed.
See [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_release_notes_pmd7.html) for the complete list.

### 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Also, there are some improvement and changes in different areas. For the detailed description
of the changes listed here, see [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_release_notes_pmd7.html).

* Miscellaneous smaller changes and cleanups
* XPath 3.1 support for XPath-based rules
* Node stream API for AST traversal
* Metrics framework
* Testing framework
* Language Lifecycle and Language Properties

### 💥 Compatibility and migration notes
See [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc3/pmd_release_notes_pmd7.html).

### 🐛 Fixed Issues

* miscellaneous
    * [#881](https://github.com/pmd/pmd/issues/881):   \[all] Breaking API changes for 7.0.0
    * [#896](https://github.com/pmd/pmd/issues/896):   \[all] Use slf4j
    * [#1431](https://github.com/pmd/pmd/pull/1431):   \[ui] Remove old GUI applications (designerold, bgastviewer)
    * [#1451](https://github.com/pmd/pmd/issues/1451): \[core] RulesetFactoryCompatibility stores the whole ruleset file in memory as a string
    * [#2496](https://github.com/pmd/pmd/issues/2496): Update PMD 7 Logo on landing page
    * [#2497](https://github.com/pmd/pmd/issues/2497): PMD 7 Logo page
    * [#2498](https://github.com/pmd/pmd/issues/2498): Update PMD 7 Logo in documentation
    * [#3797](https://github.com/pmd/pmd/issues/3797): \[all] Use JUnit5
    * [#4462](https://github.com/pmd/pmd/issues/4462): Provide Software Bill of Materials (SBOM)
    * [#4460](https://github.com/pmd/pmd/pull/4460):   Fix assembly-plugin warnings
* ant
    * [#4080](https://github.com/pmd/pmd/issues/4080): \[ant] Split off Ant integration into a new submodule
* core
    * [#880](https://github.com/pmd/pmd/issues/880):   \[core] Make visitors generic
    * [#1622](https://github.com/pmd/pmd/pull/1622):   \[core] NodeStream API
    * [#1687](https://github.com/pmd/pmd/issues/1687): \[core] Deprecate and Remove XPath 1.0 support
    * [#1785](https://github.com/pmd/pmd/issues/1785): \[core] Allow abstract node types to be valid rulechain visits
    * [#1825](https://github.com/pmd/pmd/pull/1825):   \[core] Support NoAttribute for XPath
    * [#2038](https://github.com/pmd/pmd/issues/2038): \[core] Remove DCD
    * [#2218](https://github.com/pmd/pmd/issues/2218): \[core] `isFindBoundary` should not be an attribute
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#2239](https://github.com/pmd/pmd/issues/2239): \[core] Merging Javacc build scripts
    * [#2500](https://github.com/pmd/pmd/issues/2500): \[core] Clarify API for ANTLR based languages
    * [#2518](https://github.com/pmd/pmd/issues/2518): \[core] Language properties
    * [#2602](https://github.com/pmd/pmd/issues/2602): \[core] Remove ParserOptions
    * [#2614](https://github.com/pmd/pmd/pull/2614):   \[core] Upgrade Saxon, add XPath 3.1, remove Jaxen
    * [#2696](https://github.com/pmd/pmd/pull/2696):   \[core] Remove DFA
    * [#2821](https://github.com/pmd/pmd/issues/2821): \[core] Rule processing error filenames are missing paths
    * [#2873](https://github.com/pmd/pmd/issues/2873): \[core] Utility classes in pmd 7
    * [#2885](https://github.com/pmd/pmd/issues/2885): \[core] Error recovery mode
    * [#3203](https://github.com/pmd/pmd/issues/3203): \[core] Replace RuleViolationFactory implementations with ViolationDecorator
    * [#3692](https://github.com/pmd/pmd/pull/3692):   \[core] Analysis listeners
    * [#3782](https://github.com/pmd/pmd/issues/3782): \[core] Language lifecycle
    * [#3815](https://github.com/pmd/pmd/issues/3815): \[core] Update Saxon HE to 10.7
    * [#3893](https://github.com/pmd/pmd/pull/3893):   \[core] Text documents
    * [#3902](https://github.com/pmd/pmd/issues/3902): \[core] Violation decorators
    * [#3918](https://github.com/pmd/pmd/issues/3918): \[core] Make LanguageRegistry non static
    * [#3922](https://github.com/pmd/pmd/pull/3922):   \[core] Better error reporting for the ruleset parser
    * [#4035](https://github.com/pmd/pmd/issues/4035): \[core] ConcurrentModificationException in DefaultRuleViolationFactory
    * [#4120](https://github.com/pmd/pmd/issues/4120): \[core] Explicitly name all language versions
    * [#4353](https://github.com/pmd/pmd/pull/4353):   \[core] Micro optimizations for Node API
    * [#4365](https://github.com/pmd/pmd/pull/4365):   \[core] Improve benchmarking
    * [#4420](https://github.com/pmd/pmd/pull/4420):   \[core] Remove PMD.EOL
    * [#4425](https://github.com/pmd/pmd/pull/4425):   \[core] Replace TextFile::pathId
    * [#4454](https://github.com/pmd/pmd/issues/4454): \[core] "Unknown option: '-min'" but is referenced in documentation
* cli
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#3828](https://github.com/pmd/pmd/issues/3828): \[core] Progress reporting
    * [#4079](https://github.com/pmd/pmd/issues/4079): \[cli] Split off CLI implementation into a pmd-cli submodule
    * [#4482](https://github.com/pmd/pmd/issues/4482): \[cli] pmd.bat can only be executed once
    * [#4484](https://github.com/pmd/pmd/issues/4484): \[cli] ast-dump with no properties produce an NPE
* doc
    * [#2501](https://github.com/pmd/pmd/issues/2501): \[doc] Verify ANTLR Documentation
    * [#4438](https://github.com/pmd/pmd/issues/4438): \[doc] Documentation links in VS Code are outdated
* testing
    * [#2435](https://github.com/pmd/pmd/issues/2435): \[test] Remove duplicated Dummy language module
    * [#4234](https://github.com/pmd/pmd/issues/4234): \[test] Tests that change the logging level do not work

Language specific fixes:

* apex
    * [#1937](https://github.com/pmd/pmd/issues/1937): \[apex] Apex should only have a single RootNode
    * [#1648](https://github.com/pmd/pmd/issues/1648): \[apex,vf] Remove CodeClimate dependency
    * [#1750](https://github.com/pmd/pmd/pull/1750):   \[apex] Remove apex statistical rules
    * [#2836](https://github.com/pmd/pmd/pull/2836):   \[apex] Remove Apex ProjectMirror
    * [#4427](https://github.com/pmd/pmd/issues/4427): \[apex] ApexBadCrypto test failing to detect inline code
* apex-design
    * [#2667](https://github.com/pmd/pmd/issues/2667): \[apex] Integrate nawforce/ApexLink to build robust Unused rule
    * [#4509](https://github.com/pmd/pmd/issues/4509): \[apex] ExcessivePublicCount doesn't consider inner classes correctly
* java
    * [#520](https://github.com/pmd/pmd/issues/520):   \[java] Allow `@SuppressWarnings` with constants instead of literals
    * [#864](https://github.com/pmd/pmd/issues/864):   \[java] Similar/duplicated implementations for determining FQCN
    * [#905](https://github.com/pmd/pmd/issues/905):   \[java] Add new node for anonymous class declaration
    * [#910](https://github.com/pmd/pmd/issues/910):   \[java] AST inconsistency between primitive and reference type arrays
    * [#997](https://github.com/pmd/pmd/issues/997):   \[java] Java8 parsing corner case with annotated array types
    * [#998](https://github.com/pmd/pmd/issues/998):   \[java] AST inconsistencies around FormalParameter
    * [#1019](https://github.com/pmd/pmd/issues/1019): \[java] Breaking Java Grammar changes for PMD 7.0.0
    * [#1124](https://github.com/pmd/pmd/issues/1124): \[java] ImmutableList implementation in the qname codebase
    * [#1128](https://github.com/pmd/pmd/issues/1128): \[java] Improve ASTLocalVariableDeclaration
    * [#1150](https://github.com/pmd/pmd/issues/1150): \[java] ClassOrInterfaceType AST improvements
    * [#1207](https://github.com/pmd/pmd/issues/1207): \[java] Resolve explicit types using FQCNs, without hitting the classloader
    * [#1367](https://github.com/pmd/pmd/issues/1367): \[java] Parsing error on annotated inner class
    * [#1661](https://github.com/pmd/pmd/issues/1661): \[java] About operator nodes
    * [#2366](https://github.com/pmd/pmd/pull/2366):   \[java] Remove qualified names
    * [#2819](https://github.com/pmd/pmd/issues/2819): \[java] GLB bugs in pmd 7
    * [#3642](https://github.com/pmd/pmd/issues/3642): \[java] Parse error on rare extra dimensions on method return type on annotation methods
    * [#3763](https://github.com/pmd/pmd/issues/3763): \[java] Ambiguous reference error in valid code
    * [#3749](https://github.com/pmd/pmd/issues/3749): \[java] Improve `isOverridden` in ASTMethodDeclaration
    * [#3750](https://github.com/pmd/pmd/issues/3750): \[java] Make symbol table support instanceof pattern bindings
    * [#3752](https://github.com/pmd/pmd/issues/3752): \[java] Expose annotations in symbol API
    * [#4237](https://github.com/pmd/pmd/pull/4237):   \[java] Cleanup handling of Java comments
    * [#4317](https://github.com/pmd/pmd/issues/4317): \[java] Some AST nodes should not be TypeNodes
    * [#4359](https://github.com/pmd/pmd/issues/4359): \[java] Type resolution fails with NPE when the scope is not a type declaration
    * [#4367](https://github.com/pmd/pmd/issues/4367): \[java] Move testrule TypeResTest into internal
    * [#4383](https://github.com/pmd/pmd/issues/4383): \[java] IllegalStateException: Object is not an array type!
    * [#4405](https://github.com/pmd/pmd/issues/4405): \[java] Processing error with ArrayIndexOutOfBoundsException
* java-bestpractices
    * [#342](https://github.com/pmd/pmd/issues/342):   \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
    * [#755](https://github.com/pmd/pmd/issues/755):   \[java] AccessorClassGeneration false positive for private constructors
    * [#770](https://github.com/pmd/pmd/issues/770):   \[java] UnusedPrivateMethod yields false positive for counter-variant arguments
    * [#807](https://github.com/pmd/pmd/issues/807):   \[java] AccessorMethodGeneration false positive with overloads
    * [#833](https://github.com/pmd/pmd/issues/833):   \[java] ForLoopCanBeForeach should consider iterating on this
    * [#1189](https://github.com/pmd/pmd/issues/1189): \[java] UnusedPrivateMethod false positive from inner class via external class
    * [#1205](https://github.com/pmd/pmd/issues/1205): \[java] Improve ConstantsInInterface message to mention alternatives
    * [#1212](https://github.com/pmd/pmd/issues/1212): \[java] Don't raise JUnitTestContainsTooManyAsserts on JUnit 5's assertAll
    * [#1422](https://github.com/pmd/pmd/issues/1422): \[java] JUnitTestsShouldIncludeAssert false positive with inherited @<!-- -->Rule field
    * [#1455](https://github.com/pmd/pmd/issues/1455): \[java] JUnitTestsShouldIncludeAssert: False positives for assert methods named "check" and "verify"
    * [#1563](https://github.com/pmd/pmd/issues/1563): \[java] ForLoopCanBeForeach false positive with method call using index variable
    * [#1565](https://github.com/pmd/pmd/issues/1565): \[java] JUnitAssertionsShouldIncludeMessage false positive with AssertJ
    * [#1747](https://github.com/pmd/pmd/issues/1747): \[java] PreserveStackTrace false-positive
    * [#1969](https://github.com/pmd/pmd/issues/1969): \[java] MissingOverride false-positive triggered by package-private method overwritten in another package by extending class
    * [#1998](https://github.com/pmd/pmd/issues/1998): \[java] AccessorClassGeneration false-negative: subclass calls private constructor
    * [#2130](https://github.com/pmd/pmd/issues/2130): \[java] UnusedLocalVariable: false-negative with array
    * [#2147](https://github.com/pmd/pmd/issues/2147): \[java] JUnitTestsShouldIncludeAssert - false positives with lambdas and static methods
    * [#2464](https://github.com/pmd/pmd/issues/2464): \[java] LooseCoupling must ignore class literals: ArrayList.class
    * [#2542](https://github.com/pmd/pmd/issues/2542): \[java] UseCollectionIsEmpty can not detect the case `foo.bar().size()`
    * [#2650](https://github.com/pmd/pmd/issues/2650): \[java] UseTryWithResources false positive when AutoCloseable helper used
    * [#2796](https://github.com/pmd/pmd/issues/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
    * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
    * [#2822](https://github.com/pmd/pmd/issues/2822): \[java] LooseCoupling rule: Extend to cover user defined implementations and interfaces
    * [#2843](https://github.com/pmd/pmd/pull/2843):   \[java] Fix UnusedAssignment FP with field accesses
    * [#2882](https://github.com/pmd/pmd/issues/2882): \[java] UseTryWithResources - false negative for explicit close
    * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
    * [#2890](https://github.com/pmd/pmd/issues/2890): \[java] UnusedPrivateMethod false positive with generics
    * [#2946](https://github.com/pmd/pmd/issues/2946): \[java] SwitchStmtsShouldHaveDefault false positive on enum inside enums
    * [#3672](https://github.com/pmd/pmd/pull/3672):   \[java] LooseCoupling - fix false positive with generics
    * [#3675](https://github.com/pmd/pmd/pull/3675):   \[java] MissingOverride - fix false positive with mixing type vars
    * [#3858](https://github.com/pmd/pmd/issues/3858): \[java] UseCollectionIsEmpty should infer local variable type from method invocation
    * [#4433](https://github.com/pmd/pmd/issues/4433): \[java] \[7.0-rc1] ReplaceHashtableWithMap on java.util.Properties
    * [#4492](https://github.com/pmd/pmd/issues/4492): \[java] GuardLogStatement gives false positive when argument is a Java method reference
    * [#4503](https://github.com/pmd/pmd/issues/4503): \[java] JUnitTestsShouldIncludeAssert: false negative with TestNG
    * [#4516](https://github.com/pmd/pmd/issues/4516): \[java] UnusedLocalVariable: false-negative with try-with-resources
    * [#4517](https://github.com/pmd/pmd/issues/4517): \[java] UnusedLocalVariable: false-negative with compound assignments
    * [#4518](https://github.com/pmd/pmd/issues/4518): \[java] UnusedLocalVariable: false-positive with multiple for-loop indices
* java-codestyle
    * [#1208](https://github.com/pmd/pmd/issues/1208): \[java] PrematureDeclaration rule false-positive on variable declared to measure time
    * [#1429](https://github.com/pmd/pmd/issues/1429): \[java] PrematureDeclaration as result of method call (false positive)
    * [#1480](https://github.com/pmd/pmd/issues/1480): \[java] IdenticalCatchBranches false positive with return expressions
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2134](https://github.com/pmd/pmd/issues/2134): \[java] PreserveStackTrace not handling `Throwable.addSuppressed(...)`
    * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
    * [#2391](https://github.com/pmd/pmd/issues/2391): \[java] UseDiamondOperator FP when expected type and constructed type have a different parameterization
    * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support ParameterizedTest
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
    * [#2748](https://github.com/pmd/pmd/issues/2748): \[java] UnnecessaryCast false positive with unchecked cast
    * [#2973](https://github.com/pmd/pmd/issues/2973): \[java] New rule: UnnecessaryBoxing
    * [#3195](https://github.com/pmd/pmd/pull/3195):   \[java] Improve rule UnnecessaryReturn to detect more cases
    * [#3218](https://github.com/pmd/pmd/pull/3218):   \[java] Generalize UnnecessaryCast to flag all unnecessary casts
    * [#3221](https://github.com/pmd/pmd/issues/3221): \[java] PrematureDeclaration false positive for unused variables
    * [#3238](https://github.com/pmd/pmd/issues/3238): \[java] Improve ExprContext, fix FNs of UnnecessaryCast
    * [#3500](https://github.com/pmd/pmd/pull/3500):   \[java] UnnecessaryBoxing - check for Integer.valueOf(String) calls
    * [#4268](https://github.com/pmd/pmd/issues/4268): \[java] CommentDefaultAccessModifier: false positive with TestNG annotations
    * [#4273](https://github.com/pmd/pmd/issues/4273): \[java] CommentDefaultAccessModifier ignoredAnnotations should include "org.junit.jupiter.api.extension.RegisterExtension" by default
    * [#4357](https://github.com/pmd/pmd/pull/4357):   \[java] Fix IllegalStateException in UseDiamondOperator rule
    * [#4432](https://github.com/pmd/pmd/issues/4432): \[java] \[7.0-rc1] UnnecessaryImport - Unused static import is being used
    * [#4455](https://github.com/pmd/pmd/issues/4455): \[java] FieldNamingConventions: false positive with lombok's @<!-- -->UtilityClass
    * [#4487](https://github.com/pmd/pmd/issues/4487): \[java] UnnecessaryConstructor: false-positive with @<!-- -->Inject and @<!-- -->Autowired
    * [#4511](https://github.com/pmd/pmd/issues/4511): \[java] LocalVariableCouldBeFinal shouldn't report unused variables
    * [#4512](https://github.com/pmd/pmd/issues/4512): \[java] MethodArgumentCouldBeFinal shouldn't report unused parameters
    * [#4557](https://github.com/pmd/pmd/issues/4557): \[java] UnnecessaryImport FP with static imports of overloaded methods
* java-design
    * [#1014](https://github.com/pmd/pmd/issues/1014): \[java] LawOfDemeter: False positive with lambda expression
    * [#1605](https://github.com/pmd/pmd/issues/1605): \[java] LawOfDemeter: False positive for standard UTF-8 charset name
    * [#2160](https://github.com/pmd/pmd/issues/2160): \[java] Issues with Law of Demeter
    * [#2175](https://github.com/pmd/pmd/issues/2175): \[java] LawOfDemeter: False positive for chained methods with generic method call
    * [#2179](https://github.com/pmd/pmd/issues/2179): \[java] LawOfDemeter: False positive with static property access - should treat class-level property as global object, not dot-accessed property
    * [#2180](https://github.com/pmd/pmd/issues/2180): \[java] LawOfDemeter: False positive with Thread and ThreadLocalRandom
    * [#2182](https://github.com/pmd/pmd/issues/2182): \[java] LawOfDemeter: False positive with package-private access
    * [#2188](https://github.com/pmd/pmd/issues/2188): \[java] LawOfDemeter: False positive with fields assigned to local vars
    * [#2536](https://github.com/pmd/pmd/issues/2536): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal can't detect inner class
    * [#3668](https://github.com/pmd/pmd/pull/3668):   \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - fix FP with inner private classes
    * [#3754](https://github.com/pmd/pmd/issues/3754): \[java] SingularField false positive with read in while condition
    * [#3786](https://github.com/pmd/pmd/issues/3786): \[java] SimplifyBooleanReturns should consider operator precedence
    * [#3840](https://github.com/pmd/pmd/issues/3840): \[java] LawOfDemeter disallows method call on locally created object
    * [#4238](https://github.com/pmd/pmd/pull/4238):   \[java] Make LawOfDemeter not use the rulechain
    * [#4254](https://github.com/pmd/pmd/issues/4254): \[java] ImmutableField - false positive with Lombok @<!-- -->Setter
    * [#4434](https://github.com/pmd/pmd/issues/4434): \[java] \[7.0-rc1] ExceptionAsFlowControl when simply propagating
    * [#4456](https://github.com/pmd/pmd/issues/4456): \[java] FinalFieldCouldBeStatic: false positive with lombok's @<!-- -->UtilityClass
    * [#4477](https://github.com/pmd/pmd/issues/4477): \[java] SignatureDeclareThrowsException: false-positive with TestNG annotations
    * [#4490](https://github.com/pmd/pmd/issues/4490): \[java] ImmutableField - false negative with Lombok @<!-- -->Getter
    * [#4549](https://github.com/pmd/pmd/pull/4549):   \[java] Make LawOfDemeter results deterministic
* java-documentation
    * [#4369](https://github.com/pmd/pmd/pull/4369):   \[java] Improve CommentSize
    * [#4416](https://github.com/pmd/pmd/pull/4416):   \[java] Fix reported line number in CommentContentRule
* java-errorprone
    * [#659](https://github.com/pmd/pmd/issues/659):   \[java] MissingBreakInSwitch - last default case does not contain a break
    * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
    * [#1669](https://github.com/pmd/pmd/issues/1669): \[java] NullAssignment - FP with ternay and null as constructor argument
    * [#1899](https://github.com/pmd/pmd/issues/1899): \[java] Recognize @<!-- -->SuppressWanings("fallthrough") for MissingBreakInSwitch
    * [#2320](https://github.com/pmd/pmd/issues/2320): \[java] NullAssignment - FP with ternary and null as method argument
    * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case `new BigDecimal(Expression)`
    * [#2579](https://github.com/pmd/pmd/issues/2579): \[java] MissingBreakInSwitch detects the lack of break in the last case
    * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
    * [#2893](https://github.com/pmd/pmd/issues/2893): \[java] Remove special cases from rule EmptyCatchBlock
    * [#2894](https://github.com/pmd/pmd/issues/2894): \[java] Improve MissingBreakInSwitch
    * [#3071](https://github.com/pmd/pmd/issues/3071): \[java] BrokenNullCheck FP with PMD 6.30.0
    * [#3087](https://github.com/pmd/pmd/issues/3087): \[java] UnnecessaryBooleanAssertion overlaps with SimplifiableTestAssertion
    * [#3100](https://github.com/pmd/pmd/issues/3100): \[java] UseCorrectExceptionLogging FP in 6.31.0
    * [#3173](https://github.com/pmd/pmd/issues/3173): \[java] UseProperClassLoader false positive
    * [#3351](https://github.com/pmd/pmd/issues/3351): \[java] ConstructorCallsOverridableMethod ignores abstract methods
    * [#3400](https://github.com/pmd/pmd/issues/3400): \[java] AvoidUsingOctalValues FN with underscores
    * [#3843](https://github.com/pmd/pmd/issues/3843): \[java] UseEqualsToCompareStrings should consider return type
    * [#4063](https://github.com/pmd/pmd/issues/4063): \[java] AvoidBranchingStatementAsLastInLoop: False-negative about try/finally block
    * [#4356](https://github.com/pmd/pmd/pull/4356):   \[java] Fix NPE in CloseResourceRule
    * [#4449](https://github.com/pmd/pmd/issues/4449): \[java] AvoidAccessibilityAlteration: Possible false positive in AvoidAccessibilityAlteration rule when using Lambda expression
    * [#4457](https://github.com/pmd/pmd/issues/4457): \[java] OverrideBothEqualsAndHashcode: false negative with anonymous classes
    * [#4493](https://github.com/pmd/pmd/issues/4493): \[java] MissingStaticMethodInNonInstantiatableClass: false-positive about @<!-- -->Inject
    * [#4505](https://github.com/pmd/pmd/issues/4505): \[java] ImplicitSwitchFallThrough NPE in PMD 7.0.0-rc1
    * [#4510](https://github.com/pmd/pmd/issues/4510): \[java] ConstructorCallsOverridableMethod: false positive with lombok's @<!-- -->Value
    * [#4513](https://github.com/pmd/pmd/issues/4513): \[java] UselessOperationOnImmutable various false negatives with String
    * [#4514](https://github.com/pmd/pmd/issues/4514): \[java] AvoidLiteralsInIfCondition false positive and negative for String literals when ignoreExpressions=true
    * [#4546](https://github.com/pmd/pmd/issues/4546): \[java] OverrideBothEqualsAndHashCode ignores records
* java-multithreading
    * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
    * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
    * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
    * [#4483](https://github.com/pmd/pmd/issues/4483): \[java] NonThreadSafeSingleton false positive with double-checked locking
* java-performance
    * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
    * [#2587](https://github.com/pmd/pmd/issues/2587): \[java] AvoidArrayLoops could also check for list copy through iterated List.add()
    * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ
    * [#3486](https://github.com/pmd/pmd/pull/3486):   \[java] InsufficientStringBufferDeclaration: Fix NPE
    * [#3848](https://github.com/pmd/pmd/issues/3848): \[java] StringInstantiation: false negative when using method result
    * [#4070](https://github.com/pmd/pmd/issues/4070): \[java] A false positive about the rule RedundantFieldInitializer
    * [#4458](https://github.com/pmd/pmd/issues/4458): \[java] RedundantFieldInitializer: false positive with lombok's @<!-- -->Value
* kotlin
    * [#419](https://github.com/pmd/pmd/issues/419):   \[kotlin] Add support for Kotlin
    * [#4389](https://github.com/pmd/pmd/pull/4389):   \[kotlin] Update grammar to version 1.8
* swift
    * [#1877](https://github.com/pmd/pmd/pull/1877):   \[swift] Feature/swift rules
    * [#1882](https://github.com/pmd/pmd/pull/1882):   \[swift] UnavailableFunction Swift rule
* xml
    * [#1800](https://github.com/pmd/pmd/pull/1800):   \[xml] Unimplement org.w3c.dom.Node from the XmlNodeWrapper

###  ✨ External Contributions

* [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1881](https://github.com/pmd/pmd/pull/1881): \[doc] Add ANTLR documentation - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca) (@tomidelucca)
* [#2830](https://github.com/pmd/pmd/pull/2830): \[apex] Apexlink POC - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#3866](https://github.com/pmd/pmd/pull/3866): \[core] Add CLI Progress Bar - [@JerritEic](https://github.com/JerritEic) (@JerritEic)
* [#4402](https://github.com/pmd/pmd/pull/4402): \[javascript] CPD: add support for Typescript using antlr4 grammar - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4403](https://github.com/pmd/pmd/pull/4403): \[julia] CPD: Add support for Julia code duplication  - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4412](https://github.com/pmd/pmd/pull/4412): \[doc] Added new error msg to ConstantsInInterface - [David Ljunggren](https://github.com/dague1) (@dague1)
* [#4426](https://github.com/pmd/pmd/pull/4426): \[cpd] New XML to HTML XLST report format for PMD CPD - [mohan-chinnappan-n](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)
* [#4428](https://github.com/pmd/pmd/pull/4428): \[apex] ApexBadCrypto bug fix for #4427 - inline detection of hard coded values - [Steven Stearns](https://github.com/sfdcsteve) (@sfdcsteve)
* [#4431](https://github.com/pmd/pmd/pull/4431): \[coco] CPD: Coco support for code duplication detection - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4444](https://github.com/pmd/pmd/pull/4444): \[java] CommentDefaultAccessModifier - ignore org.junit.jupiter.api.extension.RegisterExtension by default - [Nirvik Patel](https://github.com/nirvikpatel) (@nirvikpatel)
* [#4450](https://github.com/pmd/pmd/pull/4450): \[java] Fix #4449 AvoidAccessibilityAlteration: Correctly handle Lambda expressions in PrivilegedAction scenarios - [Seren](https://github.com/mohui1999) (@mohui1999)
* [#4452](https://github.com/pmd/pmd/pull/4452): \[doc] Update PMD_APEX_ROOT_DIRECTORY documentation reference - [nwcm](https://github.com/nwcm) (@nwcm)
* [#4470](https://github.com/pmd/pmd/pull/4470): \[cpp] CPD: Added strings as literal and ignore identifiers in sequences - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4474](https://github.com/pmd/pmd/pull/4474): \[java] ImmutableField: False positive with lombok (fixes #4254) - [Pim van der Loos](https://github.com/PimvanderLoos) (@PimvanderLoos)
* [#4488](https://github.com/pmd/pmd/pull/4488): \[java] Fix #4477: A false-positive about SignatureDeclareThrowsException - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4494](https://github.com/pmd/pmd/pull/4494): \[java] Fix #4487: A false-positive about UnnecessaryConstructor and @<!-- -->Inject and @<!-- -->Autowired - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4495](https://github.com/pmd/pmd/pull/4495): \[java] Fix #4493: false-positive about MissingStaticMethodInNonInstantiatableClass and @<!-- -->Inject - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4507](https://github.com/pmd/pmd/pull/4507): \[java] Fix #4503: A false negative about JUnitTestsShouldIncludeAssert and testng - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4520](https://github.com/pmd/pmd/pull/4520): \[doc] Fix typo: missing closing quotation mark after CPD-END - [João Dinis Ferreira](https://github.com/joaodinissf) (@joaodinissf)
* [#4533](https://github.com/pmd/pmd/pull/4533): \[java] Fix #4063: False-negative about try/catch block in Loop - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4536](https://github.com/pmd/pmd/pull/4536): \[java] Fix #4268: CommentDefaultAccessModifier - false positive with TestNG's @<!-- -->Test annotation - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4537](https://github.com/pmd/pmd/pull/4537): \[java] Fix #4455: A false positive about FieldNamingConventions and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4538](https://github.com/pmd/pmd/pull/4538): \[java] Fix #4456: A false positive about FinalFieldCouldBeStatic and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4540](https://github.com/pmd/pmd/pull/4540): \[java] Fix #4457: false negative about OverrideBothEqualsAndHashcode - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4541](https://github.com/pmd/pmd/pull/4541): \[java] Fix #4458: A false positive about RedundantFieldInitializer and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4542](https://github.com/pmd/pmd/pull/4542): \[java] Fix #4510: A false positive about ConstructorCallsOverridableMethod and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4553](https://github.com/pmd/pmd/pull/4553): \[java] Fix #4492: GuardLogStatement gives false positive when argument is a Java method reference - [Anastasiia Koba](https://github.com/anastasiia-koba) (@anastasiia-koba)

### 📈 Stats
* 4694 commits
* 617 closed tickets & PRs
* Days since last release: 30



## 29-April-2023 - 7.0.0-rc2

We're excited to bring you the next major version of PMD!

Since this is a big release, we provide here only a concise version of the release notes. We prepared a separate
page with the full [Detailed Release Notes for PMD 7.0.0](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_release_notes_pmd7.html).

<div style="border: 1px solid; border-radius: .25rem; padding: .75rem 1.25rem;" role="alert">
<strong>ℹ️ Release Candidates</strong>
<p>PMD 7.0.0 is finally almost ready. In order to gather feedback, we are going to ship a couple of release candidates.
These are officially available on GitHub and Maven Central and can be used as usual (e.g. as a dependency).
We encourage you to try out the new features, but keep in mind that we may introduce API breaking changes between
the release candidates. It should be stable enough if you don't use custom rules.</p>

<p>We have still some tasks planned for the next release candidates.
You can see the progress in <a href="https://github.com/pmd/pmd/issues/3898">PMD 7 Tracking Issue #3898</a>.</p>

<p>If you find any problem or difficulty while updating from PMD 6, please provide feedback via our
<a href="https://github.com/pmd/pmd/issues/new/choose">issue tracker</a>. That way we can improve the experience
for all.</p>
</div>

### Table Of Contents

* [Changes since 7.0.0-rc1](#changes-since-7.0.0-rc1)
    * [API Changes](#api-changes)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [Language Related Changes](#language-related-changes)
    * [Rule Changes](#rule-changes)
    * [Fixed Issues](#fixed-issues)
    * [External contributions](#external-contributions)
* [🚀 Major Features and Enhancements](#🚀-major-features-and-enhancements)
    * [New official logo](#new-official-logo)
    * [Revamped Java module](#revamped-java-module)
    * [Revamped Command Line Interface](#revamped-command-line-interface)
    * [Full Antlr support](#full-antlr-support)
    * [Updated PMD Designer](#updated-pmd-designer)
* [🎉 Language Related Changes](#🎉-language-related-changes)
    * [New: Swift support](#new:-swift-support)
    * [New: Kotlin support (experimental)](#new:-kotlin-support-(experimental))
    * [New: CPD support for TypeScript](#new:-cpd-support-for-typescript)
    * [New: CPD support for Julia](#new:-cpd-support-for-julia)
    * [Changed: JavaScript support](#changed:-javascript-support)
    * [Changed: Language versions](#changed:-language-versions)
* [🌟 New and changed rules](#🌟-new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
    * [Removed Rules](#removed-rules)
* [🚨 API](#🚨-api)
* [💥 Compatibility and migration notes](#💥-compatibility-and-migration-notes)
* [🐛 Fixed Issues](#🐛-fixed-issues)
* [✨ External Contributions](#✨-external-contributions)
* [📈 Stats](#📈-stats)

### Changes since 7.0.0-rc1

This section lists the most important changes from the last release candidate.
The remaining section describes the complete release notes for 7.0.0.

#### API Changes
* Moved the two classes <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc2/net/sourceforge/pmd/cpd/impl/AntlrTokenizer.html#"><code>AntlrTokenizer</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc2/net/sourceforge/pmd/cpd/impl/JavaCCTokenizer.html#"><code>JavaCCTokenizer</code></a> from
  `internal` package into package <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc2/net/sourceforge/pmd/cpd/impl/package-summary.html#"><code>net.sourceforge.pmd.cpd.impl</code></a>. These two classes are part of the API and
  are base classes for CPD language implementations.
* `AntlrBaseRule` is gone in favor of <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc2/net/sourceforge/pmd/lang/rule/AbstractVisitorRule.html#"><code>AbstractVisitorRule</code></a>.
* The classes <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.0.0-rc2/net/sourceforge/pmd/lang/kotlin/ast/KotlinInnerNode.html#"><code>KotlinInnerNode</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-swift/7.0.0-rc2/net/sourceforge/pmd/lang/swift/ast/SwiftInnerNode.html#"><code>SwiftInnerNode</code></a>
  are package-private now.
* The parameter order of <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc2/net/sourceforge/pmd/lang/document/FileCollector.html#addSourceFile(String,String)"><code>addSourceFile</code></a> has been swapped
  in order to have the same meaning as in 6.55.0. That will make it easier if you upgrade from 6.55.0 to 7.0.0.
  However, that means, that you need to change these method calls if you have migrated to 7.0.0-rc1 already.

#### Updated PMD Designer
This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc1).

#### Language Related Changes
* New: CPD support for TypeScript
* New: CPD support for Julia

#### Rule Changes
* [`ImmutableField`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#immutablefield): the property `ignoredAnnotations` has been removed. The property was
  deprecated since PMD 6.52.0.
* [`SwitchDensity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#switchdensity): the type of the property `minimum` has been changed from decimal to integer
  for consistency with other statistical rules.

#### Fixed Issues
* cli
  * [#4482](https://github.com/pmd/pmd/issues/4482): \[cli] pmd.bat can only be executed once
  * [#4484](https://github.com/pmd/pmd/issues/4484): \[cli] ast-dump with no properties produce an NPE
* core
  * [#2500](https://github.com/pmd/pmd/issues/2500): \[core] Clarify API for ANTLR based languages
* doc
  * [#2501](https://github.com/pmd/pmd/issues/2501): \[doc] Verify ANTLR Documentation
  * [#4438](https://github.com/pmd/pmd/issues/4438): \[doc] Documentation links in VS Code are outdated
* java-codestyle
  * [#4273](https://github.com/pmd/pmd/issues/4273): \[java] CommentDefaultAccessModifier ignoredAnnotations should include "org.junit.jupiter.api.extension.RegisterExtension" by default
  * [#4487](https://github.com/pmd/pmd/issues/4487): \[java] UnnecessaryConstructor: false-positive with @<!-- -->Inject and @<!-- -->Autowired
* java-design
  * [#4254](https://github.com/pmd/pmd/issues/4254): \[java] ImmutableField - false positive with Lombok @<!-- -->Setter
  * [#4477](https://github.com/pmd/pmd/issues/4477): \[java] SignatureDeclareThrowsException: false-positive with TestNG annotations
  * [#4490](https://github.com/pmd/pmd/issues/4490): \[java] ImmutableField - false negative with Lombok @<!-- -->Getter
* java-errorprone
  * [#4449](https://github.com/pmd/pmd/issues/4449): \[java] AvoidAccessibilityAlteration: Possible false positive in AvoidAccessibilityAlteration rule when using Lambda expression
  * [#4493](https://github.com/pmd/pmd/issues/4493): \[java] MissingStaticMethodInNonInstantiatableClass: false-positive about @<!-- -->Inject
  * [#4505](https://github.com/pmd/pmd/issues/4505): \[java] ImplicitSwitchFallThrough NPE in PMD 7.0.0-rc1
* java-multithreading
  * [#4483](https://github.com/pmd/pmd/issues/4483): \[java] NonThreadSafeSingleton false positive with double-checked locking
* miscellaneous
  * [#4462](https://github.com/pmd/pmd/issues/4462): Provide Software Bill of Materials (SBOM)

#### External contributions
* [#4402](https://github.com/pmd/pmd/pull/4402): \[javascript] CPD: add support for Typescript using antlr4 grammar - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4403](https://github.com/pmd/pmd/pull/4403): \[julia] CPD: Add support for Julia code duplication  - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4444](https://github.com/pmd/pmd/pull/4444): \[java] CommentDefaultAccessModifier - ignore org.junit.jupiter.api.extension.RegisterExtension by default - [Nirvik Patel](https://github.com/nirvikpatel) (@nirvikpatel)
* [#4450](https://github.com/pmd/pmd/pull/4450): \[java] Fix #4449 AvoidAccessibilityAlteration: Correctly handle Lambda expressions in PrivilegedAction scenarios - [Seren](https://github.com/mohui1999) (@mohui1999)
* [#4452](https://github.com/pmd/pmd/pull/4452): \[doc] Update PMD_APEX_ROOT_DIRECTORY documentation reference - [nwcm](https://github.com/nwcm) (@nwcm)
* [#4474](https://github.com/pmd/pmd/pull/4474): \[java] ImmutableField: False positive with lombok (fixes #4254) - [Pim van der Loos](https://github.com/PimvanderLoos) (@PimvanderLoos)
* [#4488](https://github.com/pmd/pmd/pull/4488): \[java] Fix #4477: A false-positive about SignatureDeclareThrowsException - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4494](https://github.com/pmd/pmd/pull/4494): \[java] Fix #4487: A false-positive about UnnecessaryConstructor and @<!-- -->Inject and @<!-- -->Autowired - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4495](https://github.com/pmd/pmd/pull/4495): \[java] Fix #4493: false-positive about MissingStaticMethodInNonInstantiatableClass and @<!-- -->Inject - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4520](https://github.com/pmd/pmd/pull/4520): \[doc] Fix typo: missing closing quotation mark after CPD-END - [João Dinis Ferreira](https://github.com/joaodinissf) (@joaodinissf)

### 🚀 Major Features and Enhancements

#### New official logo

The new official logo of PMD:

![New PMD Logo](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/images/logo/pmd-logo-300px.png)

#### Revamped Java module

* Java grammar substantially refactored - more correct regarding the Java Language Specification (JLS)
* Built-in rules have been upgraded for the changed AST
* Rewritten type resolution framework and symbol table correctly implements the JLS
* AST exposes more semantic information (method calls, field accesses)

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_release_notes_pmd7.html).

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Revamped Command Line Interface

* unified and consistent Command Line Interface for both Linux/Unix and Windows across our different utilities
* single script `pmd` (`pmd.bat` for Windows) to launch the different utilities:
    * `pmd check` to run PMD rules and analyze a project
    * `pmd cpd` to run CPD (copy paste detector)
    * `pmd designer` to run the PMD Rule Designer
* progress bar support for `pmd check`
* shell completion

![Demo](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/images/userdocs/pmd-demo.gif)

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_release_notes_pmd7.html).

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Full Antlr support

* [Antlr](https://www.antlr.org/) based grammars can now be used to build full-fledged PMD rules.
* Previously, Antlr grammar could only be used for CPD
* New supported languages: Swift and Kotlin

For more information, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_release_notes_pmd7.html).

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc1).

### 🎉 Language Related Changes

Note that this is just a concise listing of the highlight.
For more information on the languages, see the [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_release_notes_pmd7.html).

#### New: Swift support

* use PMD to analyze Swift code with PMD rules
* initially 4 built-in rules

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### New: Kotlin support (experimental)

* use PMD to analyze Kotlin code with PMD rules
* Support for Kotlin 1.8 grammar
* initially 2 built-in rules

#### New: CPD support for TypeScript

Thanks to a contribution, CPD now supports the TypeScript language. It is shipped
with the rest of the JavaScript support in the module `pmd-javascript`.

Contributors: [Paul Guyot](https://github.com/pguyot) (@pguyot)

#### New: CPD support for Julia

Thanks to a contribution, CPD now supports the Julia language. It is shipped
in the new module `pmd-julia`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### Changed: JavaScript support

* latest version supports ES6 and also some new constructs (see [Rhino](https://github.com/mozilla/rhino)])
* comments are retained

#### Changed: Language versions

* more predefined language versions for each supported language
* can be used to limit rule execution for specific versions only with `minimumLanguageVersion` and
  `maximumLanguageVersion` attributes.

### 🌟 New and changed rules

#### New Rules

**Apex**
* [`UnusedMethod`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_apex_design.html#unusedmethod) finds unused methods in your code.

**Java**
* [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_codestyle.html#unnecessaryboxing) reports boxing and unboxing conversions that may be made implicit.

**Kotlin**
* [`FunctionNameTooShort`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_kotlin_bestpractices.html#functionnametooshort)
* [`OverrideBothEqualsAndHashcode`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_kotlin_errorprone.html#overridebothequalsandhashcode)

**Swift**
* [`ProhibitedInterfaceBuilder`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_swift_bestpractices.html#prohibitedinterfacebuilder)
* [`UnavailableFunction`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_swift_bestpractices.html#unavailablefunction)
* [`ForceCast`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_swift_errorprone.html#forcecast)
* [`ForceTry`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_swift_errorprone.html#forcetry)

#### Changed Rules

**General changes**

* All statistical rules (like ExcessiveClassLength, ExcessiveParameterList) have been simplified and unified.
  The properties `topscore` and `sigma` have been removed. The property `minimum` is still there, however the type is not
  a decimal number anymore but has been changed to an integer. This affects rules in the languages Apex, Java, PLSQL
  and Velocity Template Language (vm):
  * Apex: [`ExcessiveClassLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_apex_design.html#excessiveclasslength), [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_apex_design.html#excessiveparameterlist),
    [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_apex_design.html#excessivepubliccount), [`NcssConstructorCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_apex_design.html#ncssconstructorcount),
    [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_apex_design.html#ncssmethodcount), [`NcssTypeCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_apex_design.html#ncsstypecount)
  * Java: [`ExcessiveImports`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#excessiveimports), [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#excessiveparameterlist),
    [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#excessivepubliccount), [`SwitchDensity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#switchdensity)
  * PLSQL: [`ExcessiveMethodLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_plsql_design.html#excessivemethodlength), [`ExcessiveObjectLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_plsql_design.html#excessiveobjectlength),
    [`ExcessivePackageBodyLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_plsql_design.html#excessivepackagebodylength), [`ExcessivePackageSpecificationLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_plsql_design.html#excessivepackagespecificationlength),
    [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_plsql_design.html#excessiveparameterlist), [`ExcessiveTypeLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_plsql_design.html#excessivetypelength),
    [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_plsql_design.html#ncssmethodcount), [`NcssObjectCount`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_plsql_design.html#ncssobjectcount),
    [`NPathComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_plsql_design.html#npathcomplexity)
  * VM: [`ExcessiveTemplateLength`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_vm_design.html#excessivetemplatelength)

* The general property `violationSuppressXPath` which is available for all rules to
  [suppress warnings](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_userdocs_suppressing_warnings.html) now uses XPath version 3.1 by default.
  This version of the XPath language is mostly identical to XPath 2.0. In PMD 6, XPath 1.0 has been used.
  If you upgrade from PMD 6, you need to verify your `violationSuppressXPath` properties.

**Apex General changes**

* The properties `cc_categories`, `cc_remediation_points_multiplier`, `cc_block_highlighting` have been removed
  from all rules. These properties have been deprecated since PMD 6.13.0.
  See [issue #1648](https://github.com/pmd/pmd/issues/1648) for more details.

**Java General changes**

* Violations reported on methods or classes previously reported the line range of the entire method
  or class. With PMD 7.0.0, the reported location is now just the identifier of the method or class.
  This affects various rules, e.g. [`CognitiveComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#cognitivecomplexity).
  
  The report location is controlled by the overrides of the method <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0-rc2/net/sourceforge/pmd/lang/ast/Node.html#getReportLocation()"><code>getReportLocation</code></a>
  in different node types.
  
  See [issue #4439](https://github.com/pmd/pmd/issues/4439) and [issue #730](https://github.com/pmd/pmd/issues/730)
  for more details.

**Java Best Practices**

* [`ArrayIsStoredDirectly`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_bestpractices.html#arrayisstoreddirectly): Violations are now reported on the assignment and not
  anymore on the formal parameter. The reported line numbers will probably move.
* [`AvoidReassigningLoopVariables`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_bestpractices.html#avoidreassigningloopvariables): This rule might not report anymore all
  reassignments of the control variable in for-loops when the property `forReassign` is set to `skip`.
  See [issue #4500](https://github.com/pmd/pmd/issues/4500) for more details.
* [`LooseCoupling`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_bestpractices.html#loosecoupling): The rule has a new property to allow some types to be coupled
  to (`allowedTypes`).
* [`UnusedLocalVariable`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_bestpractices.html#unusedlocalvariable): This rule has some important false-negatives fixed
  and finds many more cases now. For details see issues [#2130](https://github.com/pmd/pmd/issues/2130),
  [#4516](https://github.com/pmd/pmd/issues/4516), and [#4517](https://github.com/pmd/pmd/issues/4517).

**Java Codestyle**

* [`MethodNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_codestyle.html#methodnamingconventions): The property `checkNativeMethods` has been removed. The
  property was deprecated since PMD 6.3.0. Use the property `nativePattern` to control whether native methods
  should be considered or not.
* [`ShortVariable`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_codestyle.html#shortvariable): This rule now also reports short enum constant names.
* [`UseDiamondOperator`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_codestyle.html#usediamondoperator): The property `java7Compatibility` has been removed. The rule now
  handles Java 7 properly without a property.
* [`UnnecessaryFullyQualifiedName`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_codestyle.html#unnecessaryfullyqualifiedname): The rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved
  to be more precise.
* [`UselessParentheses`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_codestyle.html#uselessparentheses): The rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.

**Java Design**

* [`CyclomaticComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#cyclomaticcomplexity): The property `reportLevel` has been removed. The property was
  deprecated since PMD 6.0.0. The report level can now be configured separated for classes and methods using
  `classReportLevel` and `methodReportLevel` instead.
* [`ImmutableField`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#immutablefield): The property `ignoredAnnotations` has been removed. The property was
  deprecated since PMD 6.52.0.
* [`LawOfDemeter`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#lawofdemeter): The rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.
* [`NPathComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#npathcomplexity): The property `minimum` has been removed. It was deprecated since PMD 6.0.0.
  Use the property `reportLevel` instead.
* [`SingularField`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#singularfield): The properties `checkInnerClasses` and `disallowNotAssignment` have been removed.
  The rule is now more precise and will check these cases properly.
* [`UseUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_design.html#useutilityclass): The property `ignoredAnnotations` has been removed.

**Java Documentation**

* [`CommentContent`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_documentation.html#commentcontent): The properties `caseSensitive` and `disallowedTerms` are removed. The
  new property `forbiddenRegex` can be used now to define the disallowed terms with a single regular
  expression.
* [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_documentation.html#commentrequired):
  * Overridden methods are now detected even without the `@Override`
    annotation. This is relevant for the property `methodWithOverrideCommentRequirement`.
    See also [pull request #3757](https://github.com/pmd/pmd/pull/3757).
  * Elements in annotation types are now detected as well. This might lead to an increased number of violations
    for missing public method comments.
* [`CommentSize`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_documentation.html#commentsize): When determining the line-length of a comment, the leading comment
  prefix markers (e.g. `*` or `//`) are ignored and don't add up to the line-length.
  See also [pull request #4369](https://github.com/pmd/pmd/pull/4369).

**Java Error Prone**

* [`AvoidDuplicateLiterals`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_errorprone.html#avoidduplicateliterals): The property `exceptionfile` has been removed. The property was
  deprecated since PMD 6.10.0. Use the property `exceptionList` instead.
* [`DontImportSun`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_errorprone.html#dontimportsun): `sun.misc.Signal` is not special-cased anymore.
* [`EmptyCatchBlock`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_errorprone.html#emptycatchblock): `CloneNotSupportedException` and `InterruptedException` are not
  special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* [`ImplicitSwitchFallThrough`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_rules_java_errorprone.html#implicitswitchfallthrough): Violations are now reported on the case statements
  rather than on the switch statements. This is more accurate but might result in more violations now.

#### Removed Rules

Many rules, that were previously deprecated have been finally removed.
See [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_release_notes_pmd7.html) for the complete list.

### 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Also, there are some improvement and changes in different areas. For the detailed description
of the changes listed here, see [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_release_notes_pmd7.html).

* Miscellaneous smaller changes and cleanups
* XPath 3.1 support for XPath-based rules
* Node stream API for AST traversal
* Metrics framework
* Testing framework
* Language Lifecycle and Language Properties

### 💥 Compatibility and migration notes
See [Detailed Release Notes for PMD 7](https://docs.pmd-code.org/pmd-doc-7.0.0-rc2/pmd_release_notes_pmd7.html).

### 🐛 Fixed Issues

* miscellaneous
    * [#881](https://github.com/pmd/pmd/issues/881):   \[all] Breaking API changes for 7.0.0
    * [#896](https://github.com/pmd/pmd/issues/896):   \[all] Use slf4j
    * [#1431](https://github.com/pmd/pmd/pull/1431):   \[ui] Remove old GUI applications (designerold, bgastviewer)
    * [#1451](https://github.com/pmd/pmd/issues/1451): \[core] RulesetFactoryCompatibility stores the whole ruleset file in memory as a string
    * [#2496](https://github.com/pmd/pmd/issues/2496): Update PMD 7 Logo on landing page
    * [#2497](https://github.com/pmd/pmd/issues/2497): PMD 7 Logo page
    * [#2498](https://github.com/pmd/pmd/issues/2498): Update PMD 7 Logo in documentation
    * [#3797](https://github.com/pmd/pmd/issues/3797): \[all] Use JUnit5
    * [#4462](https://github.com/pmd/pmd/issues/4462): Provide Software Bill of Materials (SBOM)
* ant
    * [#4080](https://github.com/pmd/pmd/issues/4080): \[ant] Split off Ant integration into a new submodule
* core
    * [#880](https://github.com/pmd/pmd/issues/880):   \[core] Make visitors generic
    * [#1622](https://github.com/pmd/pmd/pull/1622):   \[core] NodeStream API
    * [#1687](https://github.com/pmd/pmd/issues/1687): \[core] Deprecate and Remove XPath 1.0 support
    * [#1785](https://github.com/pmd/pmd/issues/1785): \[core] Allow abstract node types to be valid rulechain visits
    * [#1825](https://github.com/pmd/pmd/pull/1825):   \[core] Support NoAttribute for XPath
    * [#2038](https://github.com/pmd/pmd/issues/2038): \[core] Remove DCD
    * [#2218](https://github.com/pmd/pmd/issues/2218): \[core] `isFindBoundary` should not be an attribute
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#2239](https://github.com/pmd/pmd/issues/2239): \[core] Merging Javacc build scripts
    * [#2500](https://github.com/pmd/pmd/issues/2500): \[core] Clarify API for ANTLR based languages
    * [#2518](https://github.com/pmd/pmd/issues/2518): \[core] Language properties
    * [#2602](https://github.com/pmd/pmd/issues/2602): \[core] Remove ParserOptions
    * [#2614](https://github.com/pmd/pmd/pull/2614):   \[core] Upgrade Saxon, add XPath 3.1, remove Jaxen
    * [#2696](https://github.com/pmd/pmd/pull/2696):   \[core] Remove DFA
    * [#2821](https://github.com/pmd/pmd/issues/2821): \[core] Rule processing error filenames are missing paths
    * [#2873](https://github.com/pmd/pmd/issues/2873): \[core] Utility classes in pmd 7
    * [#2885](https://github.com/pmd/pmd/issues/2885): \[core] Error recovery mode
    * [#3203](https://github.com/pmd/pmd/issues/3203): \[core] Replace RuleViolationFactory implementations with ViolationDecorator
    * [#3692](https://github.com/pmd/pmd/pull/3692):   \[core] Analysis listeners
    * [#3782](https://github.com/pmd/pmd/issues/3782): \[core] Language lifecycle
    * [#3815](https://github.com/pmd/pmd/issues/3815): \[core] Update Saxon HE to 10.7
    * [#3893](https://github.com/pmd/pmd/pull/3893):   \[core] Text documents
    * [#3902](https://github.com/pmd/pmd/issues/3902): \[core] Violation decorators
    * [#3918](https://github.com/pmd/pmd/issues/3918): \[core] Make LanguageRegistry non static
    * [#3922](https://github.com/pmd/pmd/pull/3922):   \[core] Better error reporting for the ruleset parser
    * [#4035](https://github.com/pmd/pmd/issues/4035): \[core] ConcurrentModificationException in DefaultRuleViolationFactory
    * [#4120](https://github.com/pmd/pmd/issues/4120): \[core] Explicitly name all language versions
    * [#4353](https://github.com/pmd/pmd/pull/4353):   \[core] Micro optimizations for Node API
    * [#4365](https://github.com/pmd/pmd/pull/4365):   \[core] Improve benchmarking
    * [#4420](https://github.com/pmd/pmd/pull/4420):   \[core] Remove PMD.EOL
* cli
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#3828](https://github.com/pmd/pmd/issues/3828): \[core] Progress reporting
    * [#4079](https://github.com/pmd/pmd/issues/4079): \[cli] Split off CLI implementation into a pmd-cli submodule
    * [#4482](https://github.com/pmd/pmd/issues/4482): \[cli] pmd.bat can only be executed once
    * [#4484](https://github.com/pmd/pmd/issues/4484): \[cli] ast-dump with no properties produce an NPE
* doc
    * [#2501](https://github.com/pmd/pmd/issues/2501): \[doc] Verify ANTLR Documentation
    * [#4438](https://github.com/pmd/pmd/issues/4438): \[doc] Documentation links in VS Code are outdated
* testing
    * [#2435](https://github.com/pmd/pmd/issues/2435): \[test] Remove duplicated Dummy language module
    * [#4234](https://github.com/pmd/pmd/issues/4234): \[test] Tests that change the logging level do not work

Language specific fixes:

* apex
    * [#1937](https://github.com/pmd/pmd/issues/1937): \[apex] Apex should only have a single RootNode
    * [#1648](https://github.com/pmd/pmd/issues/1648): \[apex,vf] Remove CodeClimate dependency
    * [#1750](https://github.com/pmd/pmd/pull/1750):   \[apex] Remove apex statistical rules
    * [#2836](https://github.com/pmd/pmd/pull/2836):   \[apex] Remove Apex ProjectMirror
    * [#4427](https://github.com/pmd/pmd/issues/4427): \[apex] ApexBadCrypto test failing to detect inline code
* apex-design
    * [#2667](https://github.com/pmd/pmd/issues/2667): \[apex] Integrate nawforce/ApexLink to build robust Unused rule
    * [#4509](https://github.com/pmd/pmd/issues/4509): \[apex] ExcessivePublicCount doesn't consider inner classes correctly
* java
    * [#520](https://github.com/pmd/pmd/issues/520):   \[java] Allow `@SuppressWarnings` with constants instead of literals
    * [#864](https://github.com/pmd/pmd/issues/864):   \[java] Similar/duplicated implementations for determining FQCN
    * [#905](https://github.com/pmd/pmd/issues/905):   \[java] Add new node for anonymous class declaration
    * [#910](https://github.com/pmd/pmd/issues/910):   \[java] AST inconsistency between primitive and reference type arrays
    * [#997](https://github.com/pmd/pmd/issues/997):   \[java] Java8 parsing corner case with annotated array types
    * [#998](https://github.com/pmd/pmd/issues/998):   \[java] AST inconsistencies around FormalParameter
    * [#1019](https://github.com/pmd/pmd/issues/1019): \[java] Breaking Java Grammar changes for PMD 7.0.0
    * [#1124](https://github.com/pmd/pmd/issues/1124): \[java] ImmutableList implementation in the qname codebase
    * [#1128](https://github.com/pmd/pmd/issues/1128): \[java] Improve ASTLocalVariableDeclaration
    * [#1150](https://github.com/pmd/pmd/issues/1150): \[java] ClassOrInterfaceType AST improvements
    * [#1207](https://github.com/pmd/pmd/issues/1207): \[java] Resolve explicit types using FQCNs, without hitting the classloader
    * [#1367](https://github.com/pmd/pmd/issues/1367): \[java] Parsing error on annotated inner class
    * [#1661](https://github.com/pmd/pmd/issues/1661): \[java] About operator nodes
    * [#2366](https://github.com/pmd/pmd/pull/2366):   \[java] Remove qualified names
    * [#2819](https://github.com/pmd/pmd/issues/2819): \[java] GLB bugs in pmd 7
    * [#3642](https://github.com/pmd/pmd/issues/3642): \[java] Parse error on rare extra dimensions on method return type on annotation methods
    * [#3763](https://github.com/pmd/pmd/issues/3763): \[java] Ambiguous reference error in valid code
    * [#3749](https://github.com/pmd/pmd/issues/3749): \[java] Improve `isOverridden` in ASTMethodDeclaration
    * [#3750](https://github.com/pmd/pmd/issues/3750): \[java] Make symbol table support instanceof pattern bindings
    * [#3752](https://github.com/pmd/pmd/issues/3752): \[java] Expose annotations in symbol API
    * [#4237](https://github.com/pmd/pmd/pull/4237):   \[java] Cleanup handling of Java comments
    * [#4317](https://github.com/pmd/pmd/issues/4317): \[java] Some AST nodes should not be TypeNodes
    * [#4359](https://github.com/pmd/pmd/issues/4359): \[java] Type resolution fails with NPE when the scope is not a type declaration
    * [#4367](https://github.com/pmd/pmd/issues/4367): \[java] Move testrule TypeResTest into internal
* java-bestpractices
    * [#342](https://github.com/pmd/pmd/issues/342):   \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
    * [#755](https://github.com/pmd/pmd/issues/755):   \[java] AccessorClassGeneration false positive for private constructors
    * [#770](https://github.com/pmd/pmd/issues/770):   \[java] UnusedPrivateMethod yields false positive for counter-variant arguments
    * [#807](https://github.com/pmd/pmd/issues/807):   \[java] AccessorMethodGeneration false positive with overloads
    * [#833](https://github.com/pmd/pmd/issues/833):   \[java] ForLoopCanBeForeach should consider iterating on this
    * [#1189](https://github.com/pmd/pmd/issues/1189): \[java] UnusedPrivateMethod false positive from inner class via external class
    * [#1205](https://github.com/pmd/pmd/issues/1205): \[java] Improve ConstantsInInterface message to mention alternatives
    * [#1212](https://github.com/pmd/pmd/issues/1212): \[java] Don't raise JUnitTestContainsTooManyAsserts on JUnit 5's assertAll
    * [#1422](https://github.com/pmd/pmd/issues/1422): \[java] JUnitTestsShouldIncludeAssert false positive with inherited @<!-- -->Rule field
    * [#1455](https://github.com/pmd/pmd/issues/1455): \[java] JUnitTestsShouldIncludeAssert: False positives for assert methods named "check" and "verify"
    * [#1563](https://github.com/pmd/pmd/issues/1563): \[java] ForLoopCanBeForeach false positive with method call using index variable
    * [#1565](https://github.com/pmd/pmd/issues/1565): \[java] JUnitAssertionsShouldIncludeMessage false positive with AssertJ
    * [#1747](https://github.com/pmd/pmd/issues/1747): \[java] PreserveStackTrace false-positive
    * [#1969](https://github.com/pmd/pmd/issues/1969): \[java] MissingOverride false-positive triggered by package-private method overwritten in another package by extending class
    * [#1998](https://github.com/pmd/pmd/issues/1998): \[java] AccessorClassGeneration false-negative: subclass calls private constructor
    * [#2130](https://github.com/pmd/pmd/issues/2130): \[java] UnusedLocalVariable: false-negative with array
    * [#2147](https://github.com/pmd/pmd/issues/2147): \[java] JUnitTestsShouldIncludeAssert - false positives with lambdas and static methods
    * [#2464](https://github.com/pmd/pmd/issues/2464): \[java] LooseCoupling must ignore class literals: ArrayList.class
    * [#2542](https://github.com/pmd/pmd/issues/2542): \[java] UseCollectionIsEmpty can not detect the case `foo.bar().size()`
    * [#2650](https://github.com/pmd/pmd/issues/2650): \[java] UseTryWithResources false positive when AutoCloseable helper used
    * [#2796](https://github.com/pmd/pmd/issues/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
    * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
    * [#2822](https://github.com/pmd/pmd/issues/2822): \[java] LooseCoupling rule: Extend to cover user defined implementations and interfaces
    * [#2843](https://github.com/pmd/pmd/pull/2843):   \[java] Fix UnusedAssignment FP with field accesses
    * [#2882](https://github.com/pmd/pmd/issues/2882): \[java] UseTryWithResources - false negative for explicit close
    * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
    * [#2890](https://github.com/pmd/pmd/issues/2890): \[java] UnusedPrivateMethod false positive with generics
    * [#2946](https://github.com/pmd/pmd/issues/2946): \[java] SwitchStmtsShouldHaveDefault false positive on enum inside enums
    * [#3672](https://github.com/pmd/pmd/pull/3672):   \[java] LooseCoupling - fix false positive with generics
    * [#3675](https://github.com/pmd/pmd/pull/3675):   \[java] MissingOverride - fix false positive with mixing type vars
    * [#3858](https://github.com/pmd/pmd/issues/3858): \[java] UseCollectionIsEmpty should infer local variable type from method invocation
    * [#4516](https://github.com/pmd/pmd/issues/4516): \[java] UnusedLocalVariable: false-negative with try-with-resources
    * [#4517](https://github.com/pmd/pmd/issues/4517): \[java] UnusedLocalVariable: false-negative with compound assignments
    * [#4518](https://github.com/pmd/pmd/issues/4518): \[java] UnusedLocalVariable: false-positive with multiple for-loop indices
* java-codestyle
    * [#1208](https://github.com/pmd/pmd/issues/1208): \[java] PrematureDeclaration rule false-positive on variable declared to measure time
    * [#1429](https://github.com/pmd/pmd/issues/1429): \[java] PrematureDeclaration as result of method call (false positive)
    * [#1480](https://github.com/pmd/pmd/issues/1480): \[java] IdenticalCatchBranches false positive with return expressions
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2134](https://github.com/pmd/pmd/issues/2134): \[java] PreserveStackTrace not handling `Throwable.addSuppressed(...)`
    * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
    * [#2391](https://github.com/pmd/pmd/issues/2391): \[java] UseDiamondOperator FP when expected type and constructed type have a different parameterization
    * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support ParameterizedTest
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
    * [#2748](https://github.com/pmd/pmd/issues/2748): \[java] UnnecessaryCast false positive with unchecked cast
    * [#2973](https://github.com/pmd/pmd/issues/2973): \[java] New rule: UnnecessaryBoxing
    * [#3195](https://github.com/pmd/pmd/pull/3195):   \[java] Improve rule UnnecessaryReturn to detect more cases
    * [#3218](https://github.com/pmd/pmd/pull/3218):   \[java] Generalize UnnecessaryCast to flag all unnecessary casts
    * [#3221](https://github.com/pmd/pmd/issues/3221): \[java] PrematureDeclaration false positive for unused variables
    * [#3238](https://github.com/pmd/pmd/issues/3238): \[java] Improve ExprContext, fix FNs of UnnecessaryCast
    * [#3500](https://github.com/pmd/pmd/pull/3500):   \[java] UnnecessaryBoxing - check for Integer.valueOf(String) calls
    * [#4273](https://github.com/pmd/pmd/issues/4273): \[java] CommentDefaultAccessModifier ignoredAnnotations should include "org.junit.jupiter.api.extension.RegisterExtension" by default
    * [#4357](https://github.com/pmd/pmd/pull/4357):   \[java] Fix IllegalStateException in UseDiamondOperator rule
    * [#4487](https://github.com/pmd/pmd/issues/4487): \[java] UnnecessaryConstructor: false-positive with @<!-- -->Inject and @<!-- -->Autowired
    * [#4511](https://github.com/pmd/pmd/issues/4511): \[java] LocalVariableCouldBeFinal shouldn't report unused variables
    * [#4512](https://github.com/pmd/pmd/issues/4512): \[java] MethodArgumentCouldBeFinal shouldn't report unused parameters
* java-design
    * [#1014](https://github.com/pmd/pmd/issues/1014): \[java] LawOfDemeter: False positive with lambda expression
    * [#1605](https://github.com/pmd/pmd/issues/1605): \[java] LawOfDemeter: False positive for standard UTF-8 charset name
    * [#2160](https://github.com/pmd/pmd/issues/2160): \[java] Issues with Law of Demeter
    * [#2175](https://github.com/pmd/pmd/issues/2175): \[java] LawOfDemeter: False positive for chained methods with generic method call
    * [#2179](https://github.com/pmd/pmd/issues/2179): \[java] LawOfDemeter: False positive with static property access - should treat class-level property as global object, not dot-accessed property
    * [#2180](https://github.com/pmd/pmd/issues/2180): \[java] LawOfDemeter: False positive with Thread and ThreadLocalRandom
    * [#2182](https://github.com/pmd/pmd/issues/2182): \[java] LawOfDemeter: False positive with package-private access
    * [#2188](https://github.com/pmd/pmd/issues/2188): \[java] LawOfDemeter: False positive with fields assigned to local vars
    * [#2536](https://github.com/pmd/pmd/issues/2536): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal can't detect inner class
    * [#3668](https://github.com/pmd/pmd/pull/3668):   \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - fix FP with inner private classes
    * [#3754](https://github.com/pmd/pmd/issues/3754): \[java] SingularField false positive with read in while condition
    * [#3786](https://github.com/pmd/pmd/issues/3786): \[java] SimplifyBooleanReturns should consider operator precedence
    * [#4238](https://github.com/pmd/pmd/pull/4238):   \[java] Make LawOfDemeter not use the rulechain
    * [#4254](https://github.com/pmd/pmd/issues/4254): \[java] ImmutableField - false positive with Lombok @<!-- -->Setter
    * [#4477](https://github.com/pmd/pmd/issues/4477): \[java] SignatureDeclareThrowsException: false-positive with TestNG annotations
    * [#4490](https://github.com/pmd/pmd/issues/4490): \[java] ImmutableField - false negative with Lombok @<!-- -->Getter
* java-documentation
    * [#4369](https://github.com/pmd/pmd/pull/4369):   \[java] Improve CommentSize
    * [#4416](https://github.com/pmd/pmd/pull/4416):   \[java] Fix reported line number in CommentContentRule
* java-errorprone
    * [#659](https://github.com/pmd/pmd/issues/659):   \[java] MissingBreakInSwitch - last default case does not contain a break
    * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
    * [#1669](https://github.com/pmd/pmd/issues/1669): \[java] NullAssignment - FP with ternay and null as constructor argument
    * [#1899](https://github.com/pmd/pmd/issues/1899): \[java] Recognize @<!-- -->SuppressWanings("fallthrough") for MissingBreakInSwitch
    * [#2320](https://github.com/pmd/pmd/issues/2320): \[java] NullAssignment - FP with ternary and null as method argument
    * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case `new BigDecimal(Expression)`
    * [#2579](https://github.com/pmd/pmd/issues/2579): \[java] MissingBreakInSwitch detects the lack of break in the last case
    * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
    * [#2893](https://github.com/pmd/pmd/issues/2893): \[java] Remove special cases from rule EmptyCatchBlock
    * [#2894](https://github.com/pmd/pmd/issues/2894): \[java] Improve MissingBreakInSwitch
    * [#3071](https://github.com/pmd/pmd/issues/3071): \[java] BrokenNullCheck FP with PMD 6.30.0
    * [#3087](https://github.com/pmd/pmd/issues/3087): \[java] UnnecessaryBooleanAssertion overlaps with SimplifiableTestAssertion
    * [#3100](https://github.com/pmd/pmd/issues/3100): \[java] UseCorrectExceptionLogging FP in 6.31.0
    * [#3173](https://github.com/pmd/pmd/issues/3173): \[java] UseProperClassLoader false positive
    * [#3351](https://github.com/pmd/pmd/issues/3351): \[java] ConstructorCallsOverridableMethod ignores abstract methods
    * [#3400](https://github.com/pmd/pmd/issues/3400): \[java] AvoidUsingOctalValues FN with underscores
    * [#3843](https://github.com/pmd/pmd/issues/3843): \[java] UseEqualsToCompareStrings should consider return type
    * [#4356](https://github.com/pmd/pmd/pull/4356):   \[java] Fix NPE in CloseResourceRule
    * [#4449](https://github.com/pmd/pmd/issues/4449): \[java] AvoidAccessibilityAlteration: Possible false positive in AvoidAccessibilityAlteration rule when using Lambda expression
    * [#4493](https://github.com/pmd/pmd/issues/4493): \[java] MissingStaticMethodInNonInstantiatableClass: false-positive about @<!-- -->Inject
    * [#4505](https://github.com/pmd/pmd/issues/4505): \[java] ImplicitSwitchFallThrough NPE in PMD 7.0.0-rc1
    * [#4513](https://github.com/pmd/pmd/issues/4513): \[java] UselessOperationOnImmutable various false negatives with String
    * [#4514](https://github.com/pmd/pmd/issues/4514): \[java] AvoidLiteralsInIfCondition false positive and negative for String literals when ignoreExpressions=true
* java-multithreading
    * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
    * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
    * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
    * [#4483](https://github.com/pmd/pmd/issues/4483): \[java] NonThreadSafeSingleton false positive with double-checked locking
* java-performance
    * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
    * [#2587](https://github.com/pmd/pmd/issues/2587): \[java] AvoidArrayLoops could also check for list copy through iterated List.add()
    * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ
    * [#3486](https://github.com/pmd/pmd/pull/3486):   \[java] InsufficientStringBufferDeclaration: Fix NPE
    * [#3848](https://github.com/pmd/pmd/issues/3848): \[java] StringInstantiation: false negative when using method result
* kotlin
    * [#419](https://github.com/pmd/pmd/issues/419):   \[kotlin] Add support for Kotlin
    * [#4389](https://github.com/pmd/pmd/pull/4389):   \[kotlin] Update grammar to version 1.8
* swift
    * [#1877](https://github.com/pmd/pmd/pull/1877):   \[swift] Feature/swift rules
    * [#1882](https://github.com/pmd/pmd/pull/1882):   \[swift] UnavailableFunction Swift rule
* xml
    * [#1800](https://github.com/pmd/pmd/pull/1800):   \[xml] Unimplement org.w3c.dom.Node from the XmlNodeWrapper

###  ✨ External Contributions

* [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1881](https://github.com/pmd/pmd/pull/1881): \[doc] Add ANTLR documentation - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca) (@tomidelucca)
* [#2830](https://github.com/pmd/pmd/pull/2830): \[apex] Apexlink POC - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#3866](https://github.com/pmd/pmd/pull/3866): \[core] Add CLI Progress Bar - [@JerritEic](https://github.com/JerritEic) (@JerritEic)
* [#4402](https://github.com/pmd/pmd/pull/4402): \[javascript] CPD: add support for Typescript using antlr4 grammar - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4403](https://github.com/pmd/pmd/pull/4403): \[julia] CPD: Add support for Julia code duplication  - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4412](https://github.com/pmd/pmd/pull/4412): \[doc] Added new error msg to ConstantsInInterface - [David Ljunggren](https://github.com/dague1) (@dague1)
* [#4428](https://github.com/pmd/pmd/pull/4428): \[apex] ApexBadCrypto bug fix for #4427 - inline detection of hard coded values - [Steven Stearns](https://github.com/sfdcsteve) (@sfdcsteve)
* [#4444](https://github.com/pmd/pmd/pull/4444): \[java] CommentDefaultAccessModifier - ignore org.junit.jupiter.api.extension.RegisterExtension by default - [Nirvik Patel](https://github.com/nirvikpatel) (@nirvikpatel)
* [#4450](https://github.com/pmd/pmd/pull/4450): \[java] Fix #4449 AvoidAccessibilityAlteration: Correctly handle Lambda expressions in PrivilegedAction scenarios - [Seren](https://github.com/mohui1999) (@mohui1999)
* [#4452](https://github.com/pmd/pmd/pull/4452): \[doc] Update PMD_APEX_ROOT_DIRECTORY documentation reference - [nwcm](https://github.com/nwcm) (@nwcm)
* [#4474](https://github.com/pmd/pmd/pull/4474): \[java] ImmutableField: False positive with lombok (fixes #4254) - [Pim van der Loos](https://github.com/PimvanderLoos) (@PimvanderLoos)
* [#4488](https://github.com/pmd/pmd/pull/4488): \[java] Fix #4477: A false-positive about SignatureDeclareThrowsException - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4494](https://github.com/pmd/pmd/pull/4494): \[java] Fix #4487: A false-positive about UnnecessaryConstructor and @<!-- -->Inject and @<!-- -->Autowired - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4495](https://github.com/pmd/pmd/pull/4495): \[java] Fix #4493: false-positive about MissingStaticMethodInNonInstantiatableClass and @<!-- -->Inject - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4520](https://github.com/pmd/pmd/pull/4520): \[doc] Fix typo: missing closing quotation mark after CPD-END - [João Dinis Ferreira](https://github.com/joaodinissf) (@joaodinissf)

### 📈 Stats
* 4557 commits
* 572 closed tickets & PRs
* Days since last release: 35



## 25-March-2023 - 7.0.0-rc1

We're excited to bring you the next major version of PMD!

Since this is a big release, we provide here only a concise version of the release notes. We prepared a separate
page with the full [Detailed Release Notes for PMD 7.0.0](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_release_notes_pmd7.html).

<div style="border: 1px solid; border-radius: .25rem; padding: .75rem 1.25rem;" role="alert">
<strong>ℹ️ Release Candidates</strong>
<p>PMD 7.0.0 is finally almost ready. In order to gather feedback, we are going to ship a couple of release candidates.
These are officially available on GitHub and Maven Central and can be used as usual (e.g. as a dependency).
We encourage you to try out the new features, but keep in mind that we may introduce API breaking changes between
the release candidates. It should be stable enough if you don't use custom rules.</p>

<p>We have still some tasks planned for the next release candidates.
You can see the progress in <a href="https://github.com/pmd/pmd/issues/3898">PMD 7 Tracking Issue #3898</a>.</p>

<p>If you find any problem or difficulty while updating from PMD 6, please provide feedback via our
<a href="https://github.com/pmd/pmd/issues/new/choose">issue tracker</a>. That way we can improve the experience
for all.</p>
</div>

### Table Of Contents

* [🚀 Major Features and Enhancements](#🚀-major-features-and-enhancements)
    * [New official logo](#new-official-logo)
    * [Revamped Java module](#revamped-java-module)
    * [Revamped Command Line Interface](#revamped-command-line-interface)
    * [Full Antlr support](#full-antlr-support)
* [🎉 Language Related Changes](#🎉-language-related-changes)
    * [New: Swift support](#new:-swift-support)
    * [New: Kotlin support (experimental)](#new:-kotlin-support-(experimental))
    * [Changed: JavaScript support](#changed:-javascript-support)
    * [Changed: Language versions](#changed:-language-versions)
* [🌟 New and changed rules](#🌟-new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
    * [Removed Rules](#removed-rules)
* [🚨 API](#🚨-api)
* [💥 Compatibility and migration notes](#💥-compatibility-and-migration-notes)
* [🐛 Fixed Issues](#🐛-fixed-issues)
* [✨ External Contributions](#✨-external-contributions)
* [📈 Stats](#📈-stats)

### 🚀 Major Features and Enhancements

#### New official logo

The new official logo of PMD:

![New PMD Logo](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/images/logo/pmd-logo-300px.png)

#### Revamped Java module

* Java grammar substantially refactored - more correct regarding the Java Language Specification (JLS)
* Built-in rules have been upgraded for the changed AST
* Rewritten type resolution framework and symbol table correctly implements the JLS
* AST exposes more semantic information (method calls, field accesses)

For more information, see the [Detailed Release Notes for PMD 7](pmd_release_notes_old_pmd700_detail.html).

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Revamped Command Line Interface

* unified and consistent Command Line Interface for both Linux/Unix and Windows across our different utilities
* single script `pmd` (`pmd.bat` for Windows) to launch the different utilities:
  * `pmd check` to run PMD rules and analyze a project
  * `pmd cpd` to run CPD (copy paste detector)
  * `pmd designer` to run the PMD Rule Designer
* progress bar support for `pmd check`
* shell completion

![Demo](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/images/userdocs/pmd-demo.gif)

For more information, see the [Detailed Release Notes for PMD 7](pmd_release_notes_old_pmd700_detail.html).

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Full Antlr support

* [Antlr](https://www.antlr.org/) based grammars can now be used to build full-fledged PMD rules.
* Previously, Antlr grammar could only be used for CPD
* New supported languages: Swift and Kotlin

For more information, see the [Detailed Release Notes for PMD 7](pmd_release_notes_old_pmd700_detail.html).

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

### 🎉 Language Related Changes

Note that this is just a concise listing of the highlight.
For more information on the languages, see the [Detailed Release Notes for PMD 7](pmd_release_notes_old_pmd700_detail.html).

#### New: Swift support

* use PMD to analyze Swift code with PMD rules
* initially 4 built-in rules

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### New: Kotlin support (experimental)

* use PMD to analyze Kotlin code with PMD rules
* Support for Kotlin 1.8 grammar
* initially 2 built-in rules

#### Changed: JavaScript support

* latest version supports ES6 and also some new constructs (see [Rhino](https://github.com/mozilla/rhino)])
* comments are retained

#### Changed: Language versions

* more predefined language versions for each supported language
* can be used to limit rule execution for specific versions only with `minimumLanguageVersion` and
  `maximumLanguageVersion` attributes.

### 🌟 New and changed rules

#### New Rules

**Apex**
* [`UnusedMethod`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_apex_design.html#unusedmethod) finds unused methods in your code.

**Java**
* [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_codestyle.html#unnecessaryboxing) reports boxing and unboxing conversions that may be made implicit.

**Kotlin**
* [`FunctionNameTooShort`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_kotlin_bestpractices.html#functionnametooshort)
* [`OverrideBothEqualsAndHashcode`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_kotlin_errorprone.html#overridebothequalsandhashcode)

**Swift**
* [`ProhibitedInterfaceBuilder`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_swift_bestpractices.html#prohibitedinterfacebuilder)
* [`UnavailableFunction`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_swift_bestpractices.html#unavailablefunction)
* [`ForceCast`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_swift_errorprone.html#forcecast)
* [`ForceTry`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_swift_errorprone.html#forcetry)

#### Changed Rules

**Java**

* [`UnnecessaryFullyQualifiedName`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_codestyle.html#unnecessaryfullyqualifiedname): the rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved
  to be more precise.
* [`UselessParentheses`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_codestyle.html#uselessparentheses): the rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.
* [`LooseCoupling`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_bestpractices.html#loosecoupling): the rule has a new property to allow some types to be coupled
  to (`allowedTypes`).
* [`EmptyCatchBlock`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_errorprone.html#emptycatchblock): `CloneNotSupportedException` and `InterruptedException` are not
  special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* [`DontImportSun`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_errorprone.html#dontimportsun): `sun.misc.Signal` is not special-cased anymore.
* [`UseDiamondOperator`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_codestyle.html#usediamondoperator): the property `java7Compatibility` is removed. The rule now
  handles Java 7 properly without a property.
* [`SingularField`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_design.html#singularfield): Properties `checkInnerClasses` and `disallowNotAssignment` are removed.
  The rule is now more precise and will check these cases properly.
* [`UseUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_design.html#useutilityclass): The property `ignoredAnnotations` has been removed.
* [`LawOfDemeter`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_design.html#lawofdemeter): the rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.
* [`CommentContent`](https://docs.pmd-code.org/pmd-doc-7.0.0-rc1/pmd_rules_java_documentation.html#commentcontent): The properties `caseSensitive` and `disallowedTerms` are removed. The
  new property `fobiddenRegex` can be used now to define the disallowed terms with a single regular
  expression.

#### Removed Rules

Many rules, that were previously deprecated have been finally removed.
See [Detailed Release Notes for PMD 7](pmd_release_notes_old_pmd700_detail.html) for the complete list.

### 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Also, there are some improvement and changes in different areas. For the detailed description
of the changes listed here, see [Detailed Release Notes for PMD 7](pmd_release_notes_old_pmd700_detail.html).

* Miscellaneous smaller changes and cleanups
* XPath 3.1 support for XPath-based rules
* Node stream API for AST traversal
* Metrics framework
* Testing framework
* Language Lifecycle and Language Properties

### 💥 Compatibility and migration notes
See [Detailed Release Notes for PMD 7](pmd_release_notes_old_pmd700_detail.html).

### 🐛 Fixed Issues

* miscellaneous
    * [#881](https://github.com/pmd/pmd/issues/881):   \[all] Breaking API changes for 7.0.0
    * [#896](https://github.com/pmd/pmd/issues/896):   \[all] Use slf4j
    * [#1431](https://github.com/pmd/pmd/pull/1431):   \[ui] Remove old GUI applications (designerold, bgastviewer)
    * [#1451](https://github.com/pmd/pmd/issues/1451): \[core] RulesetFactoryCompatibility stores the whole ruleset file in memory as a string
    * [#2496](https://github.com/pmd/pmd/issues/2496): Update PMD 7 Logo on landing page
    * [#2497](https://github.com/pmd/pmd/issues/2497): PMD 7 Logo page
    * [#2498](https://github.com/pmd/pmd/issues/2498): Update PMD 7 Logo in documentation
    * [#3797](https://github.com/pmd/pmd/issues/3797): \[all] Use JUnit5
* ant
    * [#4080](https://github.com/pmd/pmd/issues/4080): \[ant] Split off Ant integration into a new submodule
* core
    * [#880](https://github.com/pmd/pmd/issues/880):   \[core] Make visitors generic
    * [#1622](https://github.com/pmd/pmd/pull/1622):   \[core] NodeStream API
    * [#1687](https://github.com/pmd/pmd/issues/1687): \[core] Deprecate and Remove XPath 1.0 support
    * [#1785](https://github.com/pmd/pmd/issues/1785): \[core] Allow abstract node types to be valid rulechain visits
    * [#1825](https://github.com/pmd/pmd/pull/1825):   \[core] Support NoAttribute for XPath
    * [#2038](https://github.com/pmd/pmd/issues/2038): \[core] Remove DCD
    * [#2218](https://github.com/pmd/pmd/issues/2218): \[core] `isFindBoundary` should not be an attribute
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#2239](https://github.com/pmd/pmd/issues/2239): \[core] Merging Javacc build scripts
    * [#2518](https://github.com/pmd/pmd/issues/2518): \[core] Language properties
    * [#2602](https://github.com/pmd/pmd/issues/2602): \[core] Remove ParserOptions
    * [#2614](https://github.com/pmd/pmd/pull/2614):   \[core] Upgrade Saxon, add XPath 3.1, remove Jaxen
    * [#2696](https://github.com/pmd/pmd/pull/2696):   \[core] Remove DFA
    * [#2821](https://github.com/pmd/pmd/issues/2821): \[core] Rule processing error filenames are missing paths
    * [#2873](https://github.com/pmd/pmd/issues/2873): \[core] Utility classes in pmd 7
    * [#2885](https://github.com/pmd/pmd/issues/2885): \[core] Error recovery mode
    * [#3203](https://github.com/pmd/pmd/issues/3203): \[core] Replace RuleViolationFactory implementations with ViolationDecorator
    * [#3692](https://github.com/pmd/pmd/pull/3692):   \[core] Analysis listeners
    * [#3782](https://github.com/pmd/pmd/issues/3782): \[core] Language lifecycle
    * [#3815](https://github.com/pmd/pmd/issues/3815): \[core] Update Saxon HE to 10.7
    * [#3893](https://github.com/pmd/pmd/pull/3893):   \[core] Text documents
    * [#3902](https://github.com/pmd/pmd/issues/3902): \[core] Violation decorators
    * [#3918](https://github.com/pmd/pmd/issues/3918): \[core] Make LanguageRegistry non static
    * [#3922](https://github.com/pmd/pmd/pull/3922):   \[core] Better error reporting for the ruleset parser
    * [#4035](https://github.com/pmd/pmd/issues/4035): \[core] ConcurrentModificationException in DefaultRuleViolationFactory
    * [#4120](https://github.com/pmd/pmd/issues/4120): \[core] Explicitly name all language versions
    * [#4353](https://github.com/pmd/pmd/pull/4353):   \[core] Micro optimizations for Node API
    * [#4365](https://github.com/pmd/pmd/pull/4365):   \[core] Improve benchmarking
    * [#4420](https://github.com/pmd/pmd/pull/4420):   \[core] Remove PMD.EOL
* cli
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#3828](https://github.com/pmd/pmd/issues/3828): \[core] Progress reporting
    * [#4079](https://github.com/pmd/pmd/issues/4079): \[cli] Split off CLI implementation into a pmd-cli submodule
* testing
    * [#2435](https://github.com/pmd/pmd/issues/2435): \[test] Remove duplicated Dummy language module
    * [#4234](https://github.com/pmd/pmd/issues/4234): \[test] Tests that change the logging level do not work

Language specific fixes:

* apex
    * [#1937](https://github.com/pmd/pmd/issues/1937): \[apex] Apex should only have a single RootNode
    * [#1648](https://github.com/pmd/pmd/issues/1648): \[apex,vf] Remove CodeClimate dependency
    * [#1750](https://github.com/pmd/pmd/pull/1750):   \[apex] Remove apex statistical rules
    * [#2836](https://github.com/pmd/pmd/pull/2836):   \[apex] Remove Apex ProjectMirror
    * [#4427](https://github.com/pmd/pmd/issues/4427): \[apex] ApexBadCrypto test failing to detect inline code
* apex-design
    * [#2667](https://github.com/pmd/pmd/issues/2667): \[apex] Integrate nawforce/ApexLink to build robust Unused rule
* java
    * [#520](https://github.com/pmd/pmd/issues/520):   \[java] Allow `@SuppressWarnings` with constants instead of literals
    * [#864](https://github.com/pmd/pmd/issues/864):   \[java] Similar/duplicated implementations for determining FQCN
    * [#905](https://github.com/pmd/pmd/issues/905):   \[java] Add new node for anonymous class declaration
    * [#910](https://github.com/pmd/pmd/issues/910):   \[java] AST inconsistency between primitive and reference type arrays
    * [#997](https://github.com/pmd/pmd/issues/997):   \[java] Java8 parsing corner case with annotated array types
    * [#998](https://github.com/pmd/pmd/issues/998):   \[java] AST inconsistencies around FormalParameter
    * [#1019](https://github.com/pmd/pmd/issues/1019): \[java] Breaking Java Grammar changes for PMD 7.0.0
    * [#1124](https://github.com/pmd/pmd/issues/1124): \[java] ImmutableList implementation in the qname codebase
    * [#1128](https://github.com/pmd/pmd/issues/1128): \[java] Improve ASTLocalVariableDeclaration
    * [#1150](https://github.com/pmd/pmd/issues/1150): \[java] ClassOrInterfaceType AST improvements
    * [#1207](https://github.com/pmd/pmd/issues/1207): \[java] Resolve explicit types using FQCNs, without hitting the classloader
    * [#1367](https://github.com/pmd/pmd/issues/1367): \[java] Parsing error on annotated inner class
    * [#1661](https://github.com/pmd/pmd/issues/1661): \[java] About operator nodes
    * [#2366](https://github.com/pmd/pmd/pull/2366):   \[java] Remove qualified names
    * [#2819](https://github.com/pmd/pmd/issues/2819): \[java] GLB bugs in pmd 7
    * [#3763](https://github.com/pmd/pmd/issues/3763): \[java] Ambiguous reference error in valid code
    * [#3749](https://github.com/pmd/pmd/issues/3749): \[java] Improve `isOverridden` in ASTMethodDeclaration
    * [#3750](https://github.com/pmd/pmd/issues/3750): \[java] Make symbol table support instanceof pattern bindings
    * [#3752](https://github.com/pmd/pmd/issues/3752): \[java] Expose annotations in symbol API
    * [#4237](https://github.com/pmd/pmd/pull/4237):   \[java] Cleanup handling of Java comments
    * [#4317](https://github.com/pmd/pmd/issues/4317): \[java] Some AST nodes should not be TypeNodes
    * [#4359](https://github.com/pmd/pmd/issues/4359): \[java] Type resolution fails with NPE when the scope is not a type declaration
    * [#4367](https://github.com/pmd/pmd/issues/4367): \[java] Move testrule TypeResTest into internal
* java-bestpractices
    * [#342](https://github.com/pmd/pmd/issues/342):   \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
    * [#755](https://github.com/pmd/pmd/issues/755):   \[java] AccessorClassGeneration false positive for private constructors
    * [#770](https://github.com/pmd/pmd/issues/770):   \[java] UnusedPrivateMethod yields false positive for counter-variant arguments
    * [#807](https://github.com/pmd/pmd/issues/807):   \[java] AccessorMethodGeneration false positive with overloads
    * [#833](https://github.com/pmd/pmd/issues/833):   \[java] ForLoopCanBeForeach should consider iterating on this
    * [#1189](https://github.com/pmd/pmd/issues/1189): \[java] UnusedPrivateMethod false positive from inner class via external class
    * [#1205](https://github.com/pmd/pmd/issues/1205): \[java] Improve ConstantsInInterface message to mention alternatives
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
    * [#2796](https://github.com/pmd/pmd/issues/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
    * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
    * [#2822](https://github.com/pmd/pmd/issues/2822): \[java] LooseCoupling rule: Extend to cover user defined implementations and interfaces
    * [#2843](https://github.com/pmd/pmd/pull/2843):   \[java] Fix UnusedAssignment FP with field accesses
    * [#2882](https://github.com/pmd/pmd/issues/2882): \[java] UseTryWithResources - false negative for explicit close
    * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
    * [#2890](https://github.com/pmd/pmd/issues/2890): \[java] UnusedPrivateMethod false positive with generics
    * [#2946](https://github.com/pmd/pmd/issues/2946): \[java] SwitchStmtsShouldHaveDefault false positive on enum inside enums
    * [#3672](https://github.com/pmd/pmd/pull/3672):   \[java] LooseCoupling - fix false positive with generics
    * [#3675](https://github.com/pmd/pmd/pull/3675):   \[java] MissingOverride - fix false positive with mixing type vars
* java-codestyle
    * [#1208](https://github.com/pmd/pmd/issues/1208): \[java] PrematureDeclaration rule false-positive on variable declared to measure time
    * [#1429](https://github.com/pmd/pmd/issues/1429): \[java] PrematureDeclaration as result of method call (false positive)
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2134](https://github.com/pmd/pmd/issues/2134): \[java] PreserveStackTrace not handling `Throwable.addSuppressed(...)`
    * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
    * [#2391](https://github.com/pmd/pmd/issues/2391): \[java] UseDiamondOperator FP when expected type and constructed type have a different parameterization
    * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support ParameterizedTest
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
    * [#2748](https://github.com/pmd/pmd/issues/2748): \[java] UnnecessaryCast false positive with unchecked cast
    * [#2973](https://github.com/pmd/pmd/issues/2973): \[java] New rule: UnnecessaryBoxing
    * [#3195](https://github.com/pmd/pmd/pull/3195):   \[java] Improve rule UnnecessaryReturn to detect more cases
    * [#3218](https://github.com/pmd/pmd/pull/3218):   \[java] Generalize UnnecessaryCast to flag all unnecessary casts
    * [#3221](https://github.com/pmd/pmd/issues/3221): \[java] PrematureDeclaration false positive for unused variables
    * [#3238](https://github.com/pmd/pmd/issues/3238): \[java] Improve ExprContext, fix FNs of UnnecessaryCast
    * [#3500](https://github.com/pmd/pmd/pull/3500):   \[java] UnnecessaryBoxing - check for Integer.valueOf(String) calls
    * [#4357](https://github.com/pmd/pmd/pull/4357):   \[java] Fix IllegalStateException in UseDiamondOperator rule
* java-design
    * [#1014](https://github.com/pmd/pmd/issues/1014): \[java] LawOfDemeter: False positive with lambda expression
    * [#1605](https://github.com/pmd/pmd/issues/1605): \[java] LawOfDemeter: False positive for standard UTF-8 charset name
    * [#2175](https://github.com/pmd/pmd/issues/2175): \[java] LawOfDemeter: False positive for chained methods with generic method call
    * [#2179](https://github.com/pmd/pmd/issues/2179): \[java] LawOfDemeter: False positive with static property access - should treat class-level property as global object, not dot-accessed property
    * [#2180](https://github.com/pmd/pmd/issues/2180): \[java] LawOfDemeter: False positive with Thread and ThreadLocalRandom
    * [#2182](https://github.com/pmd/pmd/issues/2182): \[java] LawOfDemeter: False positive with package-private access
    * [#2188](https://github.com/pmd/pmd/issues/2188): \[java] LawOfDemeter: False positive with fields assigned to local vars
    * [#2536](https://github.com/pmd/pmd/issues/2536): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal can't detect inner class
    * [#3668](https://github.com/pmd/pmd/pull/3668):   \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - fix FP with inner private classes
    * [#3754](https://github.com/pmd/pmd/issues/3754): \[java] SingularField false positive with read in while condition
    * [#3786](https://github.com/pmd/pmd/issues/3786): \[java] SimplifyBooleanReturns should consider operator precedence
    * [#4238](https://github.com/pmd/pmd/pull/4238):   \[java] Make LawOfDemeter not use the rulechain
* java-documentation
    * [#4369](https://github.com/pmd/pmd/pull/4369):   \[java] Improve CommentSize
    * [#4416](https://github.com/pmd/pmd/pull/4416):   \[java] Fix reported line number in CommentContentRule
* java-errorprone
    * [#659](https://github.com/pmd/pmd/issues/659):   \[java] MissingBreakInSwitch - last default case does not contain a break
    * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
    * [#1669](https://github.com/pmd/pmd/issues/1669): \[java] NullAssignment - FP with ternay and null as constructor argument
    * [#1899](https://github.com/pmd/pmd/issues/1899): \[java] Recognize @SuppressWanings("fallthrough") for MissingBreakInSwitch
    * [#2320](https://github.com/pmd/pmd/issues/2320): \[java] NullAssignment - FP with ternary and null as method argument
    * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case `new BigDecimal(Expression)`
    * [#2579](https://github.com/pmd/pmd/issues/2579): \[java] MissingBreakInSwitch detects the lack of break in the last case
    * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
    * [#2893](https://github.com/pmd/pmd/issues/2893): \[java] Remove special cases from rule EmptyCatchBlock
    * [#2894](https://github.com/pmd/pmd/issues/2894): \[java] Improve MissingBreakInSwitch
    * [#3071](https://github.com/pmd/pmd/issues/3071): \[java] BrokenNullCheck FP with PMD 6.30.0
    * [#3087](https://github.com/pmd/pmd/issues/3087): \[java] UnnecessaryBooleanAssertion overlaps with SimplifiableTestAssertion
    * [#3100](https://github.com/pmd/pmd/issues/3100): \[java] UseCorrectExceptionLogging FP in 6.31.0
    * [#3173](https://github.com/pmd/pmd/issues/3173): \[java] UseProperClassLoader false positive
    * [#3351](https://github.com/pmd/pmd/issues/3351): \[java] ConstructorCallsOverridableMethod ignores abstract methods
    * [#3400](https://github.com/pmd/pmd/issues/3400): \[java] AvoidUsingOctalValues FN with underscores
    * [#4356](https://github.com/pmd/pmd/pull/4356):   \[java] Fix NPE in CloseResourceRule
* java-multithreading
    * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
    * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
    * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
* java-performance
    * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
    * [#2587](https://github.com/pmd/pmd/issues/2587): \[java] AvoidArrayLoops could also check for list copy through iterated List.add()
    * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ
    * [#3486](https://github.com/pmd/pmd/pull/3486):   \[java] InsufficientStringBufferDeclaration: Fix NPE
* kotlin
    * [#419](https://github.com/pmd/pmd/issues/419):   \[kotlin] Add support for Kotlin
    * [#4389](https://github.com/pmd/pmd/pull/4389):   \[kotlin] Update grammar to version 1.8
* swift
    * [#1877](https://github.com/pmd/pmd/pull/1877):   \[swift] Feature/swift rules
    * [#1882](https://github.com/pmd/pmd/pull/1882):   \[swift] UnavailableFunction Swift rule
* xml
    * [#1800](https://github.com/pmd/pmd/pull/1800):   \[xml] Unimplement org.w3c.dom.Node from the XmlNodeWrapper

###  ✨ External Contributions

* [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1881](https://github.com/pmd/pmd/pull/1881): \[doc] Add ANTLR documentation - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca) (@tomidelucca)
* [#2830](https://github.com/pmd/pmd/pull/2830): \[apex] Apexlink POC - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#3866](https://github.com/pmd/pmd/pull/3866): \[core] Add CLI Progress Bar - [@JerritEic](https://github.com/JerritEic) (@JerritEic)
* [#4412](https://github.com/pmd/pmd/pull/4412): \[doc] Added new error msg to ConstantsInInterface - [David Ljunggren](https://github.com/dague1) (@dague1)
* [#4428](https://github.com/pmd/pmd/pull/4428): \[apex] ApexBadCrypto bug fix for #4427 - inline detection of hard coded values - [Steven Stearns](https://github.com/sfdcsteve) (@sfdcsteve)

### 📈 Stats
* 4416 commits
* 464 closed tickets & PRs
* Days since last release: 28
