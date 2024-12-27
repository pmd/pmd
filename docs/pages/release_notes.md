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

* pmd-core: {%jdoc !!core::reporting.RuleContext#addViolationWithPosition(core::reporting.Reportable,core::lang.ast.AstInfo,core::lang.document.FileLocation,java.lang.String,java.lang.Object...) %}

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

{% endtocmaker %}
