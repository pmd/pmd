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
* The new Java rule {% rule java/errorprone/WrongTestAnnotation %} detects when test annotations from the wrong
  testing framework (JUnit 4, JUnit Jupiter, or TestNG) are used in your code, preventing tests from being silently
  skipped due to framework mismatches. This helps avoid the silent failure where tests compile but don't execute
  because the test runner doesn't recognize the annotation.
* The new Java rule {% rule java/errorprone/AssertEqualsArgumentOrder %} detects assertions
  where the expected and actual arguments were swapped. This helps find assertions
  that are producing a confusing error message when they fail.
* The new Kotlin rule {% rule kotlin/bestpractices/LocalVariableShadowsParameter %} detects local variable
  declarations that use the same name as a parameter of the enclosing function. This shadows the parameter
  and may lead to confusion about which value is used.
* The new Apex rule {% rule apex/errorprone/InvocableClassNoArgConstructor %} detects classes that use
  `@InvocableVariable` properties, but that don't provide a no-arg constructor. Without such a constructor,
  runtime exception occur when Salesforce Flow tries to instantiate such classes.

#### Deprecated Rules
* The rule {% rule java/design/UseObjectForClearerAPI %} was deprecated. Use {% rule java/design/ExcessiveParameterList %}
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
  * {% jdoc core::lang.ast.impl.antlr4.AntlrBaseParser %} has been deprecated in favor of
    {% jdoc core::lang.ast.impl.antlr4.AntlrBaseParserWithErrorHandling %}, which converts ANTLR's parsing
    errors into PMD's processing errors by default.
* java
  * {% jdoc !!java::lang.java.ast.ASTMethodDeclaration#isOverridden() %} has been renamed to {% jdoc java::lang.java.ast.ASTMethodDeclaration#isOverride() %}.
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

{% endtocmaker %}
