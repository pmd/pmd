---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### Collections exposed as XPath attributes

Up to now, all AST node getters would be exposed to XPath, as long as the return type was a primitive (boxed or unboxed), String or Enum. That meant that collections, even of these basic types, were not exposed, so for instance accessing Apex's `ASTUserClass.getInterfaceNames()` to list the interfaces implemented by a class was impossible from XPath, and would require writing a Java rule to check it.

Since this release, PMD will also expose any getter returning a collection of any supported type as a sequence through an XPath attribute. They would require to use apropriate XQuery functions to manipulate the sequence. So for instance, to detect any given `ASTUserClass` in Apex that implements `Queueable`, it is now possible to write:

```xml
/UserClass[@InterfaceNames = 'Queueable']
```

### 🐛 Fixed Issues

* java-bestpractices
  * [#4278](https://github.com/pmd/pmd/issues/4278): \[java] UnusedPrivateMethod FP with Junit 5 @MethodSource and default factory method name
  * [#4975](https://github.com/pmd/pmd/issues/4975): \[java] UnusedPrivateMethod false positive when using @MethodSource on a @Nested test

### 🚨 API Changes

### ✨ External Contributions

{% endtocmaker %}

