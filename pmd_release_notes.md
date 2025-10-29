


## 31-October-2025 - 7.18.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.18.0-SNAPSHOT.

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
* The new Java rule [`IdenticalConditionalBranches`](https://docs.pmd-code.org/pmd-doc-7.18.0-SNAPSHOT/pmd_rules_java_errorprone.html#identicalconditionalbranches) finds conditional statements
  that do the same thing when the condition is true and false. This is either incorrect or redundant.
* The new Java rule [`LabeledStatement`](https://docs.pmd-code.org/pmd-doc-7.18.0-SNAPSHOT/pmd_rules_java_bestpractices.html#labeledstatement) finds labeled statements in code.
  Labels make control flow difficult to understand and should be avoided. By default, the rule allows labeled
  loops (do, while, for). But it has a property to flag also those labeled loops.
* The new Java rule [`UnusedLabel`](https://docs.pmd-code.org/pmd-doc-7.18.0-SNAPSHOT/pmd_rules_java_bestpractices.html#unusedlabel) finds unused labels which are unnecessary and
  only make the code hard to read. This new rule will be part of the quickstart ruleset.

#### Changed Rules
* [`ConfusingTernary`](https://docs.pmd-code.org/pmd-doc-7.18.0-SNAPSHOT/pmd_rules_java_codestyle.html#confusingternary) has a new property `nullCheckBranch` to control, whether null-checks
  should be allowed (the default case) or should lead to a violation.
* [`AvoidCatchingGenericException`](https://docs.pmd-code.org/pmd-doc-7.18.0-SNAPSHOT/pmd_rules_java_errorprone.html#avoidcatchinggenericexception) is now configurable with the new property
  `typesThatShouldNotBeCaught`.  
  ⚠️ The rule has also been moved from category "Design" to category "Error Prone". If you are currently bulk-adding
  all the rules from the "Design" category into your custom ruleset, then you need to add the rule explicitly
  again (otherwise it won't be included anymore):
  ```xml
  <rule ref="category/java/errorprone.xml/AvoidCatchingGenericException" />
  ```

#### Deprecated Rules
* The Java rule [`AvoidCatchingNPE`](https://docs.pmd-code.org/pmd-doc-7.18.0-SNAPSHOT/pmd_rules_java_errorprone.html#avoidcatchingnpe) has been deprecated in favor of the updated rule
  [`AvoidCatchingGenericException`](https://docs.pmd-code.org/pmd-doc-7.18.0-SNAPSHOT/pmd_rules_java_errorprone.html#avoidcatchinggenericexception), which is now configurable.
* The Java rule [`AvoidCatchingThrowable`](https://docs.pmd-code.org/pmd-doc-7.18.0-SNAPSHOT/pmd_rules_java_errorprone.html#avoidcatchingthrowable) has been deprecated in favor of the updated rule
  [`AvoidCatchingGenericException`](https://docs.pmd-code.org/pmd-doc-7.18.0-SNAPSHOT/pmd_rules_java_errorprone.html#avoidcatchinggenericexception), which is now configurable.

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
* java
  * [#4904](https://github.com/pmd/pmd/issues/4904): \[java] Renderers output wrong class qualified name for nested classes
  * [#6127](https://github.com/pmd/pmd/issues/6127): \[java] Incorrect variable name in violation
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
* plsql-design
  * [#6077](https://github.com/pmd/pmd/issues/6077): \[plsql] Excessive\*/Ncss\*Count/NPathComplexity include the metric

### 🚨️ API Changes

#### Deprecations
* java
  * The following methods have been deprecated. Due to refactoring of the internal base class, these methods are not
    used anymore and are not required to be implemented anymore:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.18.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/design/ExcessiveImportsRule.html#isViolation(net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit,int)"><code>ExcessiveImportsRule#isViolation</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.18.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/design/ExcessiveParameterListRule.html#isViolation(net.sourceforge.pmd.lang.java.ast.ASTFormalParameters,int)"><code>ExcessiveParameterListRule#isViolation</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.18.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/design/ExcessivePublicCountRule.html#isViolation(net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration,int)"><code>ExcessivePublicCountRule#isViolation</code></a>

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
* [#6129](https://github.com/pmd/pmd/pull/6129): \[java] Fix #6127: Correct var name in violation decorator - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6130](https://github.com/pmd/pmd/pull/6130): \[java] UselessParentheses: fix false positives for switch expressions - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6133](https://github.com/pmd/pmd/pull/6133): \[java] Fix #5042: CloseResource: fix false positive with pattern matching - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6150](https://github.com/pmd/pmd/pull/6150): \[core] Reduce memory usage of CPD's MatchCollector - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6152](https://github.com/pmd/pmd/pull/6152): chore(deps): Update Saxon-HE from 12.5 to 12.9 - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6156](https://github.com/pmd/pmd/pull/6156): \[java] Fix #6146: ClassCastException in TypeTestUtil - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6164](https://github.com/pmd/pmd/pull/6164): \[doc] Update reproducible build info with Java 17 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6166](https://github.com/pmd/pmd/pull/6166): \[doc] Use emoji variants - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6170](https://github.com/pmd/pmd/pull/6170): \[java] Fix #6169: AvoidUsingHardCodedIP - mention address in message - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6171](https://github.com/pmd/pmd/pull/6171): \[java] AvoidUsingHardCodedIP: fix false positive for IPv6 - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



