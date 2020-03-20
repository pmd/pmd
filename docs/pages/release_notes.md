---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Performance improvements for XPath 2.0 rules

XPath rules written with XPath 2.0 now support conversion to a rulechain rule, which
improves their performance. The rulechain is a mechanism that allows several rules
to be executed in a single tree traversal. Conversion to the rulechain is possible if
your XPath expression looks like `//someNode/... | //someOtherNode/...  | ...`, that
is, a union of one or more path expressions that start with `//`. Instead of traversing
the whole tree once per path expression (and per rule), a single traversal executes all
rules in your ruleset as needed.

This conversion is performed automatically and cannot be disabled. *The conversion should
not change the result of your rules*, if it does, please report a bug at https://github.com/pmd/pmd/issues

Note that XPath 1.0 support, the default XPath version, is deprecated since PMD 6.22.0.
**We highly recommend that you upgrade your rules to XPath 2.0**. Please refer to the [migration guide](https://pmd.github.io/latest/pmd_userdocs_extending_writing_xpath_rules.html#migrating-from-10-to-20).



### Fixed Issues

### API Changes

#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc vm::lang.vm.VmTokenManager %}
*   {% jdoc java::lang.java.JavaTokenManager %}
*   {% jdoc python::lang.python.PythonTokenManager %}

### External Contributions

{% endtocmaker %}

