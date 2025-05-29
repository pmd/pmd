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

* The new Apex rule {% rule apex/errorprone/TypeShadowsBuiltInNamespace %} finds Apex classes, enums, and interfaces
  that have the same name as a class, enum, or interface in the `System` or `Schema` namespace.
  Shadowing these namespaces in this way can lead to confusion and unexpected behavior.

### 🐛 Fixed Issues
* core
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
* java-design
  * [#5568](https://github.com/pmd/pmd/issues/5568): \[java] High NPathComplexity in `switch` expression
  * [#5647](https://github.com/pmd/pmd/issues/5647): \[java] NPathComplexity does not account for `return`s

### 🚨 API Changes

#### Deprecations
* pmd-java
  * {% jdoc !!java::lang.java.ast.ASTCompactConstructorDeclaration#getDeclarationNode() %}: This method just returns `this` and isn't useful.
  * {% jdoc !!java::lang.java.metrics.JavaMetrics#NPATH %}: Use {% jdoc java::lang.java.metrics.JavaMetrics#NPATH_COMP %}, which is available on more nodes,
    and uses Long instead of BigInteger.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5450](https://github.com/pmd/pmd/pull/5450): Fix #3184: \[apex] New Rule: TypeShadowsBuiltInNamespace - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5573](https://github.com/pmd/pmd/pull/5573): Fix #5525: \[core] Add Sarif Level Property - [julees7](https://github.com/julees7) (@julees7)
* [#5672](https://github.com/pmd/pmd/pull/5672): \[doc] Fix its/it's and doable/double typos - [John Jetmore](https://github.com/jetmore) (@jetmore)
* [#5684](https://github.com/pmd/pmd/pull/5684): Fix #5667: \[apex] ApexUnitTestShouldNotUseSeeAllDataTrue false negative when seeAllDate parameter is a string - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#5685](https://github.com/pmd/pmd/pull/5685): \[doc] typo fix in PMD Designer reference - [Douglas Griffith](https://github.com/dwgrth) (@dwgrth)
* [#5687](https://github.com/pmd/pmd/pull/5687): \[java] UnusedPrivateMethodRule: exclude serialization method readObjectNoData() - [Gili Tzabari](https://github.com/cowwoc) (@cowwoc)
* [#5599](https://github.com/pmd/pmd/pull/5599): \[java] Rewrite NPath complexity metric - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

