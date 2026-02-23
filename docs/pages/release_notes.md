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
#### Changed Rules
* The rule {% rule java/errorprone/CloseResource %} introduces a new property, `allowedResourceMethodPatterns`,
  which lets you specify method invocation patterns whose return values are resources managed externally.
  This is useful for ignoring managed resources - for example, `Reader`/`Writer` instances obtained from
  `HttpServletRequest`/`HttpServletResponse` - because the servlet container, not application code,
  is responsible for closing them. By default, the rule ignores `InputStream`/`OutputStream`/`Reader`/`Writer`
  resources returned by methods on `(Http)ServletRequest` and `(Http)ServletResponse`
  (both `javax.servlet` and `jakarta.servlet`).

### 🐛️ Fixed Issues
* java-errorprone
  * [#6436](https://github.com/pmd/pmd/issues/6436): \[java] CloseResource: Allow to ignore managed resources

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}
