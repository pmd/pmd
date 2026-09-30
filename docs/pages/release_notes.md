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
*   The Java rule {% rule java/design/FinalFieldCouldBeStatic %} no longer reports casts or conditional expressions
    whose constant classification previously depended on a boxed static final field. Direct static field references
    are still reported.
*   The Java rule {% rule java/errorprone/UnconditionalIfStatement %} now excludes final local boolean constants,
    consistently with its existing exclusion of named compile-time constants used for conditional compilation.
*   The Java rule {% rule java/errorprone/UnusedNullCheckInEquals %} now recognizes final local String constants
    and unqualified instance String constants as non-null receivers, avoiding unnecessary reports.
*   The Java rule {% rule java/bestpractices/AvoidReassigningLoopVariables %}, with `forReassign=skip`, now accepts
    a final local constant equal to one as the increment of a conditional skip.
*   The Java rule {% rule java/bestpractices/UnusedAssignment %} now recognizes final local boolean constants
    when analyzing short-circuit conditions, avoiding false positives caused by assignments that cannot execute.
*   The Java rule {% rule java/bestpractices/LiteralsFirstInComparisons %} now recognizes final local String constants
    and unqualified references to non-static final String constants. This may add violations when such a constant
    is the argument of a comparison, or remove them when it is already the receiver.

### 🐛️ Fixed Issues
* groovy
    * [#7110](https://github.com/pmd/pmd/issues/7110): \[groovy] Fix #7100: CPD fails on GStrings ending in an interpolated variable
* java
    * [#7060](https://github.com/pmd/pmd/issues/7060): \[java] getConstValue() returns null for constant expressions referencing final local variables
* java-bestpractices
    * [#5159](https://github.com/pmd/pmd/issues/5159): \[java] UnusedAssignment false positive when using assert
* java-design
    * [#4815](https://github.com/pmd/pmd/issues/4815): \[java] ExceptionAsFlowControl false-positive on Lambda/asynchronous (7.0.0-rc4)
    * [#7117](https://github.com/pmd/pmd/issues/7117): \[java] ExceptionAsFlowControl: false negative when the lambda is invoked by the method it is passed to

### 🚨️ API Changes

*   Java constant folding now recognizes final primitive and String variables initialized with constant expressions,
    including local variables and unqualified instance fields. Numeric references are converted to their declared type.
    Boxed fields and field accesses qualified by expressions (such as `this.CONSTANT`) are not compile-time constants.
    These changes affect `ASTExpression.getConstValue()`, `isCompileTimeConstant()`, and the XPath attribute
    `@CompileTimeConstant`; custom Java and XPath rules relying on them may report different results.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

