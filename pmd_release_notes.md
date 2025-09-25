


## 31-October-2025 - 7.18.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.18.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

### 🐛 Fixed Issues
* core
  * [#4714](https://github.com/pmd/pmd/issues/4714): \[core] Allow trailing commas in multivalued properties
* apex-design
  * [#6022](https://github.com/pmd/pmd/issues/6022): \[apex] ExcessiveClassLength/ExcessiveParameterList include the metric in the message
* java-codestyle
  * [#6029](https://github.com/pmd/pmd/issues/6029): \[java] Fix UnnecessaryCast false-negative in method calls
* java-design
  * [#5569](https://github.com/pmd/pmd/issues/5569): \[java] ExcessivePublicCount should report number of public "things"
* java-errorprone
  * [#5878](https://github.com/pmd/pmd/issues/5878): \[java] DontUseFloatTypeForLoopIndices false-negative if variable is declared before loop
* java-multithreading
  * [#5880](https://github.com/pmd/pmd/issues/5880): \[java] DoubleCheckedLocking is not detected if more than 1 assignment or more than 2 if statements
* misc
  * [#6012](https://github.com/pmd/pmd/issues/6012): \[pmd-rulesets] Rulesets should be in alphabetical order

### 🚨 API Changes

#### Deprecations
* java
  * The following methods have been deprecated. Due to refactoring of the internal base class, these methods are not
    used anymore and are not required to be implemented anymore:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.18.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/design/ExcessiveImportsRule.html#isViolation(net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit,int)"><code>ExcessiveImportsRule#isViolation</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.18.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/design/ExcessiveParameterListRule.html#isViolation(net.sourceforge.pmd.lang.java.ast.ASTFormalParameters,int)"><code>ExcessiveParameterListRule#isViolation</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.18.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/design/ExcessivePublicCountRule.html#isViolation(net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration,int)"><code>ExcessivePublicCountRule#isViolation</code></a>

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6021](https://github.com/pmd/pmd/pull/6021): \[java] Fix #5569: ExcessiveImports/ExcessiveParameterList/ExcessivePublicCount include the metric in the message - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6022](https://github.com/pmd/pmd/pull/6022): \[apex] ExcessiveClassLength/ExcessiveParameterList include the metric in the message - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6023](https://github.com/pmd/pmd/pull/6023): \[test] Fix #6012: Alphabetically sort all default rules - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6024](https://github.com/pmd/pmd/pull/6024): \[java] Fix #5878: DontUseFloatTypeForLoopIndices now checks the UpdateStatement as well - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6029](https://github.com/pmd/pmd/pull/6029): \[java] Fix UnnecessaryCast false-negative in method calls - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6031](https://github.com/pmd/pmd/pull/6031): \[java] Fix #5880: False Negatives in DoubleCheckedLocking - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6039](https://github.com/pmd/pmd/pull/6039): \[core] Fix #4714: trim token before feeding it to the extractor - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6040](https://github.com/pmd/pmd/pull/6040): \[java,apex,plsql,velocity] Change description of "minimum" parameter - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6043](https://github.com/pmd/pmd/pull/6043): \[java] Reactivate deactivated test in LocalVariableCouldBeFinal - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->



