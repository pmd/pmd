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
*   The new Java rule {% rule java/documentation/MarkdownJavadocBeforeJava23 %}
    reports comments starting with `///`, so they can be reviewed before
    switching to Java 23. The rule stays off for Java 23 and later.

### 🐛️ Fixed Issues
* java
    * [#7392](https://github.com/pmd/pmd/issues/7392): \[java\] PMD 7.29.0 fails to resolve a switch pattern variable passed to an anonymous class constructor
* java-bestpractices
    * [#4870](https://github.com/pmd/pmd/issues/4870): \[java] ArrayIsStoredDirectly: False negative for an array stored in an enum constructor
    * [#7308](https://github.com/pmd/pmd/issues/7308): \[java\] MethodReturnsInternalArray: document that private methods are not checked 
    * [#7310](https://github.com/pmd/pmd/issues/7310): \[java] UnusedAssignment: i++ in a do-while condition reported as never used, unlike in a while
* java-design
    * [#7328](https://github.com/pmd/pmd/issues/7328): \[java] CollapsibleIfStatements: not reported when a bare block separates the two if statements
* java-documentation
    * [#7057](https://github.com/pmd/pmd/issues/7057): \[java\] New rule: MarkdownJavadocBeforeJava23
* java-performance
    * [#3775](https://github.com/pmd/pmd/issues/3775): \[java\] ConsecutiveAppendsShouldReuse: False Negative when .append is wrapped by brace.

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

