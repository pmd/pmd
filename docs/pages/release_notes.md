---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

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
[Adding a new dialect]({{ baseurl }}pmd_devdocs_major_adding_dialect.html) for more information.

#### ✨ New Rules

* The new Apex rule {% rule apex/errorprone/TypeShadowsBuiltInNamespace %} finds Apex classes, enums, and interfaces
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
* {%jdoc !!xml::lang.xml.pom.PomLanguageModule %} is deprecated. POM is now a dialect of XML.
  Use {%jdoc xml::lang.xml.pom.PomDialectModule %} instead.
* {%jdoc !!xml::lang.xml.wsdl.WsdlLanguageModule %} is deprecated. WSDL is now a dialect of XML.
  Use {%jdoc xml::lang.xml.wsdl.WsdlDialectModule %} instead.
* {%jdoc !!xml::lang.xml.xsl.XslLanguageModule %} is deprecated. XSL is now a dialect of XML.
  Use {%jdoc xml::lang.xml.xsl.XslDialectModule %} instead.

#### Experimental API
* The core API around support for language dialects:
  * {%jdoc !!core::lang.Language#getBaseLanguageId() %}
  * {%jdoc !!core::lang.Language#isDialectOf(core::lang.Language) %}
  * {%jdoc !!core::lang.LanguageModuleBase#<init>(core::lang.LanguageModuleBase.DialectLanguageMetadata) %}
  * {%jdoc !!core::lang.LanguageModuleBase#asDialectOf(java.lang.String) %}
  * {%jdoc core::lang.LanguageModuleBase.DialectLanguageMetadata %}
  * {%jdoc core::lang.impl.BasePmdDialectLanguageVersionHandler %}
  * {%jdoc core::lang.impl.SimpleDialectLanguageModuleBase %}

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

{% endtocmaker %}
