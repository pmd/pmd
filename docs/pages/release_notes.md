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

#### PMD CLI now uses threaded execution by default

In the PMD CLI, the `--threads` (`-t`) option can now accept a thread
count given relative to the number of cores of the machine. For instance,
it is now possible to write `-t 1C` to spawn one thread per core, or `-t 0.5C`
to spawn one thread for every other core.

The thread count option now defaults to `1C`, meaning parallel execution
is used by default. You can disable this by using `-t 1`.

### 🐛 Fixed Issues
* core
  * [#5700](https://github.com/pmd/pmd/pull/5700): \[core] Don't accidentally catch unexpected runtime exceptions in CpdAnalysis
* java-bestpractices
  * [#5724](https://github.com/pmd/pmd/issues/5724): \[java] ImplicitFunctionalInterface should not be reported on sealed interfaces
* java-codestyle
  * [#5634](https://github.com/pmd/pmd/issues/5634): \[java] CommentDefaultAccessModifier doesn't recognize /* package */ comment at expected location for constructors
* java-errorprone
  * [#5702](https://github.com/pmd/pmd/issues/5702): \[java] InvalidLogMessageFormat: Lombok @<!-- -->Slf4j annotation is not interpreted by PMD

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5700](https://github.com/pmd/pmd/pull/5700): \[core] Don't accidentally catch unexpected runtime exceptions in CpdAnalysis - [Elliotte Rusty Harold](https://github.com/elharo) (@elharo)
* [#5716](https://github.com/pmd/pmd/pull/5716): Fix #5634: \[java] CommentDefaultAccessModifier: Comment between annotation and constructor not recognized - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

