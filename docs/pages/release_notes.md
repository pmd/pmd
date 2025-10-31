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
* The new Apex rule {% rule apex/design/NcssCount %} replaces the four rules "ExcessiveClassLength",
  "NcssConstructorCount", "NcssMethodCount", and "NcssTypeCount". The new rule uses the metrics framework
  to achieve the same. It has two properties, to define the report level for method and class sizes separately.
  Constructors and methods are considered the same.  
  The rule has been added to the quickstart ruleset.

#### Deprecated Rules
* The Apex rule {% rule apex/design/ExcessiveClassLength %} has been deprecated. Use {%rule apex/design/NcssCount %} to
  find big classes or create a custom XPath based rule using
  `//ApexFile[UserClass][@EndLine - @BeginLine > 1000]`.
* The Apex rules {% rule apex/design/NcssConstructorCount %}, {%rule apex/design/NcssMethodCount %}, and
  {% rule apex/design/NcssTypeCount %} have been deprecated in favor or the new rule {%rule apex/design/NcssCount %}.

### 🐛️ Fixed Issues
* apex-design
  * [#2128](https://github.com/pmd/pmd/issues/2128): \[apex] Merge NCSS count rules for Apex

### 🚨️ API Changes

#### Deprecations
* apex
  * {% jdoc apex::lang.apex.rule.design.ExcessiveClassLengthRule %}
  * {% jdoc apex::lang.apex.rule.design.NcssConstructorCountRule %}
  * {% jdoc apex::lang.apex.rule.design.NcssMethodCountRule %}
  * {% jdoc apex::lang.apex.rule.design.NcssTypeCountRule %}
  * {% jdoc apex::lang.apex.ast.ASTStatement %}: This AST node is not used and doesn't appear in the tree.
  * {% jdoc !ac!apex::lang.apex.ast.ApexVisitor#visit(apex::lang.apex.ast.ASTStatement,P) %}

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

