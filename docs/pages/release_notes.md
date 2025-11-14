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

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### New Rules
* The new PL/SQL rule {% rule plsql/design/NcssCount %} replaces the rules "ExcessiveMethodLength",
  "ExcessiveObjectLength", "ExcessivePackageBodyLength", "ExcessivePackageSpecificationLength",
  "ExcessiveTypeLength", "NcssMethodCount" and "NcssObjectCount". The new rule uses the metrics framework
  to achieve the same. It has two properties, to define the report level for method and object sizes separately.  
  Note: the new metric is implemented more correct than in the old rules, so that the actual numbers of
  the NCSS metric from the old rules might be different from the new rule "NcssCount". Statements that are
  split on multiple lines are still counted as one.

#### Deprecated Rules
* The PL/SQL rule {% rule plsql/design/ExcessiveMethodLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//(MethodDeclaration|ProgramUnit|TriggerTimingPointSection|TriggerUnit|TypeMethod)[@EndLine - @BeginLine > 100]`.
* The PL/SQL rule {% rule plsql/design/ExcessiveObjectLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//(PackageBody|PackageSpecification|ProgramUnit|TriggerUnit|TypeSpecification)[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule {% rule plsql/design/ExcessivePackageBodyLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//PackageBody[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule {% rule plsql/design/ExcessivePackageSpecificationLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//PackageSpecification[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule {% rule plsql/design/ExcessiveTypeLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//TypeSpecification[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rules {% rule plsql/design/NcssMethodCount %} and {% rule plsql/design/NcssObjectCount %} have been
  deprecated in favor of the new rule {% rule plsql/design/NcssCount %}.

### 🐛️ Fixed Issues
* plsql-design
  * [#4326](https://github.com/pmd/pmd/issues/4326): \[plsql] Merge NCSS count rules for PL/SQL

### 🚨️ API Changes

#### Deprecations
* plsql
  * {% jdoc plsql::lang.plsql.rule.design.ExcessiveMethodLengthRule %}
  * {% jdoc plsql::lang.plsql.rule.design.ExcessiveObjectLengthRule %}
  * {% jdoc plsql::lang.plsql.rule.design.ExcessivePackageBodyLengthRule %}
  * {% jdoc plsql::lang.plsql.rule.design.ExcessivePackageSpecificationLengthRule %}
  * {% jdoc plsql::lang.plsql.rule.design.ExcessiveTypeLengthRule %}
  * {% jdoc plsql::lang.plsql.rule.design.NcssMethodCountRule %}
  * {% jdoc plsql::lang.plsql.rule.design.NcssObjectCountRule %}

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6214](https://github.com/pmd/pmd/pull/6214): \[plsql] New rule NcssCount to replace old Ncss*Count rules - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

