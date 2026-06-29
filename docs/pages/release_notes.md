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

### 🐛️ Fixed Issues

### 🚨️ API Changes
{% jdoc_nspace :coreast core::lang.ast %}


### New reporting API

New APIs have been introduced to report violations in rules written in Java.
They  use a 2-stage builder pattern to simplify the API and extend its
functionality. Example usages:
```java
ctx.at(node).report(); // report with default message
ctx.at(node).reportWithArgs("arg", 2); // report with default message and format arguments
ctx.at(node).reportWithMessage("message"); // report with non-default message
ctx.at(node).reportWithMessage("message", "arg", 2); // report with non-default message and format arguments
```
Use those new forms instead of the deprecated equivalent forms:
```java
ctx.addViolation(node);
ctx.addViolation(node, "arg", 2);
ctx.addViolationWithMessage(node, "message");
ctx.addViolationWithMessage(node, "message", "arg", 2);
```

The new API allows more flexibility about where violations are reported.
For instance, you can easily report on a specific token within a node:
```java
ctx.at(node.atToken(node.getFirstToken())).report();
```
The previous way to do this required using complex overloads.

See {% jdoc core::reporting.RuleContext#at(core::reporting.Reportable) %} for more information.

### New Experimental APIs

* core
    * {% jdoc coreast::Node#atLocation(core::lang.document.FileLocation) %}
    * {% jdoc coreast::Node#atToken(coreast::GenericToken) %}
    * {% jdoc core::lang.rule.impl.CannotBeSuppressed %}

### Removed Experimental APIs

* core
    * {% jdoc_old core::reporting.RuleContext#addViolationWithPosition(coreast::Node,coreast::impl.javacc.JavaccToken,java.lang.String,java.lang.Object...) %} (introduced in 7.17.0)
    * {% jdoc_old core::reporting.RuleContext#addViolationWithPosition(coreast::reporting.Reportable,coreast::AstInfo,core::lang.document.FileLocation,java.lang.String,java.lang.Object...) %} (introduced in 7.9.0)
    * {% jdoc_old core::reporting.RuleContext#addViolationNoSuppress(coreast::reporting.Reportable,coreast::AstInfo,java.lang.String,java.lang.Object...) %} (introduced in 7.14.0)


### Deprecated APIs

* core
    * {% jdoc core::reporting.RuleContext#addViolationWithPosition(coreast::Node,int,int,java.lang.String,java.lang.Object...) %}

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

