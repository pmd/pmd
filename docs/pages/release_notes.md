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

* The new Java rule {%rule java/bestpractices/ExhaustiveSwitchHasDefault %} finds switch statements and
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

{% endtocmaker %}
