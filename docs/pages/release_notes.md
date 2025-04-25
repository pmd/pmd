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
* [#5450](https://github.com/pmd/pmd/pull/5450): Fix #3184: \[apex] New Rule: TypeShadowsBuiltInNamespace - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5573](https://github.com/pmd/pmd/pull/5573): Fix #5525: \[core] Add Sarif Level Property - [julees7](https://github.com/julees7) (@julees7)
* [#5672](https://github.com/pmd/pmd/pull/5672): \[doc] Fix its/it's and doable/double typos - [John Jetmore](https://github.com/jetmore) (@jetmore)
* [#5684](https://github.com/pmd/pmd/pull/5684): Fix #5667: \[apex] ApexUnitTestShouldNotUseSeeAllDataTrue false negative when seeAllDate parameter is a string - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#5685](https://github.com/pmd/pmd/pull/5685): \[doc] typo fix in PMD Designer reference - [Douglas Griffith](https://github.com/dwgrth) (@dwgrth)
* [#5687](https://github.com/pmd/pmd/pull/5687): \[java] UnusedPrivateMethodRule: exclude serialization method readObjectNoData() - [Gili Tzabari](https://github.com/cowwoc) (@cowwoc)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

