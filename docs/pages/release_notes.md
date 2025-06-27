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

#### ✨ New Rules

* The new Apex rule {% rule apex/design/AvoidBooleanMethodParameters %} finds methods that take a
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
See [Testing your rules]({{ baseurl }}pmd_userdocs_extending_testing.html) for more information.

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

{% endtocmaker %}
