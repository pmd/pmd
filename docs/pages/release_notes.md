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
* The new Apex rule {% rule apex/errorprone/AvoidStatefulDatabaseResult %} detects `Database.Stateful` implementations
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
  * The method {%jdoc !ca!java::lang.java.types.TypeOps#isContextDependent(java::lang.java.types.JMethodSig) %} is deprecated for removal.
    Use {%jdoc !a!java::lang.java.types.TypeOps#isContextDependent(java::lang.java.symbols.JExecutableSymbol) %} instead which
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

{% endtocmaker %}
