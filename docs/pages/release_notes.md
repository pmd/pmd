---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### ✨ New Rules

* The new Java rule {% rule java/bestpractices/ImplicitFunctionalInterface %} reports functional interfaces that were
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
  * The method {%jdoc java::lang.java.ast.AbstractJavaExpr#buildConstValue() %} is deprecated for removal. It should
    have been package-private from the start. In order to get the (compile time) const value of an expression, use
    {%jdoc java::lang.java.ast.ASTExpression#getConstValue() %} or {%jdoc java::lang.java.ast.ASTExpression#getConstFoldingResult() %}
    instead.
  * For the same reason, the following methods are also deprecated for removal:
    {%jdoc java::lang.java.ast.ASTNumericLiteral#buildConstValue() %} and {%jdoc java::lang.java.ast.ASTStringLiteral#buildConstValue() %}.

- {% jdoc !!java::lang.java.types.JTypeVar#withUpperbound(java::types.JTypeMirror) %} is deprecated. It was previously meant to be used
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

{% endtocmaker %}
