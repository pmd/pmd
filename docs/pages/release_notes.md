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

### 🚀 New and noteworthy

#### Build Requirement is Java 17
From now on, Java 17 or newer is required to build PMD. PMD itself still remains compatible with Java 8,
so that it still can be used in a pure Java 8 environment. This allows us to use the latest
checkstyle version during the build.

### 🐛 Fixed Issues
* core
  * [#4714](https://github.com/pmd/pmd/issues/4714): \[core] Allow trailing commas in multivalued properties
* apex
  * [#5935](https://github.com/pmd/pmd/issues/5935): \[apex] @<!-- -->SuppressWarnings - allow whitespace around comma when suppressing multiple rules
* apex-design
  * [#6022](https://github.com/pmd/pmd/issues/6022): \[apex] ExcessiveClassLength/ExcessiveParameterList include the metric in the message
* java-bestpractices
  * [#4122](https://github.com/pmd/pmd/issues/4122): \[java] CheckResultSet false-positive with local variable
* java-codestyle
  * [#5919](https://github.com/pmd/pmd/issues/5919): \[java] ClassNamingConventions: Include integration tests in testClassPattern by default
  * [#6029](https://github.com/pmd/pmd/issues/6029): \[java] Fix UnnecessaryCast false-negative in method calls
* java-design
  * [#1499](https://github.com/pmd/pmd/issues/1499): \[java] AvoidDeeplyNestedIfStmts violations can be unintentionally undetected
  * [#5569](https://github.com/pmd/pmd/issues/5569): \[java] ExcessivePublicCount should report number of public "things"
* java-documentation
  * [#6058](https://github.com/pmd/pmd/issues/6058): \[java] DanglingJavadoc FP in module-info files
* java-errorprone
  * [#5878](https://github.com/pmd/pmd/issues/5878): \[java] DontUseFloatTypeForLoopIndices false-negative if variable is declared before loop
  * [#6075](https://github.com/pmd/pmd/issues/6075): \[java] AssignmentInOperand false positive with lambda expressions
* java-multithreading
  * [#5880](https://github.com/pmd/pmd/issues/5880): \[java] DoubleCheckedLocking is not detected if more than 1 assignment or more than 2 if statements
* misc
  * [#6012](https://github.com/pmd/pmd/issues/6012): \[pmd-rulesets] Rulesets should be in alphabetical order

### 🚨 API Changes

#### Deprecations
* java
  * The following methods have been deprecated. Due to refactoring of the internal base class, these methods are not
    used anymore and are not required to be implemented anymore:
    * {%jdoc !!java::lang.java.rule.design.ExcessiveImportsRule#isViolation(java::lang.java.ast.ASTCompilationUnit,int) %}
    * {%jdoc !!java::lang.java.rule.design.ExcessiveParameterListRule#isViolation(java::lang.java.ast.ASTFormalParameters,int) %}
    * {%jdoc !!java::lang.java.rule.design.ExcessivePublicCountRule#isViolation(java::lang.java.ast.ASTTypeDeclaration,int) %}

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
* [#6056](https://github.com/pmd/pmd/pull/6056): chore: fix dogfood issues from new rules - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6059](https://github.com/pmd/pmd/pull/6059): \[java] Fix #6058: DanglingJavadoc FP in module-info files - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6061](https://github.com/pmd/pmd/pull/6061): \[core] chore: Bump minimum Java version required for building to 17 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6071](https://github.com/pmd/pmd/pull/6071): \[java] Fix #5919: Add integration tests to ClassNamingConventions testClassRegex - [Anton Bobov](https://github.com/abobov) (@abobov)
* [#6074](https://github.com/pmd/pmd/pull/6074): \[apex] Fix @<!-- -->SuppressWarnings with whitespace around comma - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#6078](https://github.com/pmd/pmd/pull/6078): \[java] Fix #6075: Fix FP in AssignmentInOperandRule - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

