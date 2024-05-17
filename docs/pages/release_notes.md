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
* cli
  * [#2827](https://github.com/pmd/pmd/issues/2827): \[cli] Consider processing errors in exit status
* core
  * [#4467](https://github.com/pmd/pmd/issues/4467): \[core] Expose collections from getters as XPath sequence attributes
  * [#4978](https://github.com/pmd/pmd/issues/4978): \[core] Referenced Rulesets do not emit details on validation errors
  * [#4983](https://github.com/pmd/pmd/pull/4983): \[cpd] Fix CPD crashes about unicode escapes
* java
  * [#4912](https://github.com/pmd/pmd/issues/4912): \[java] Unable to parse some Java9+ resource references
  * [#4973](https://github.com/pmd/pmd/pull/4973): \[java] Stop parsing Java for CPD
  * [#4980](https://github.com/pmd/pmd/issues/4980): \[java] Bad intersection, unrelated class types java.lang.Object\[] and java.lang.Number
  * [#4988](https://github.com/pmd/pmd/pull/4988): \[java] Fix impl of ASTVariableId::isResourceDeclaration / VariableId/@<!-- -->ResourceDeclaration
  * [#5006](https://github.com/pmd/pmd/issues/5006): \[java] Bad intersection, unrelated class types Child and Parent<? extends Child>
* java-bestpractices
  * [#4278](https://github.com/pmd/pmd/issues/4278): \[java] UnusedPrivateMethod FP with Junit 5 @MethodSource and default factory method name
  * [#4852](https://github.com/pmd/pmd/issues/4852): \[java] ReplaceVectorWithList false-positive (neither Vector nor List usage) 
  * [#4975](https://github.com/pmd/pmd/issues/4975): \[java] UnusedPrivateMethod false positive when using @MethodSource on a @Nested test
  * [#4985](https://github.com/pmd/pmd/issues/4985): \[java] UnusedPrivateMethod false-positive / method reference in combination with custom object
* java-codestyle
  * [#4930](https://github.com/pmd/pmd/issues/4930): \[java] EmptyControlStatement should not allow empty try with concise resources
* java-errorprone
  * [#4042](https://github.com/pmd/pmd/issues/4042): \[java] A false negative about the rule StringBufferInstantiationWithChar
* java-multithreading
  * [#2368](https://github.com/pmd/pmd/issues/2368): \[java] False positive UnsynchronizedStaticFormatter in static initializer

### 🚨 API Changes

#### CLI

* New exit code 5 introduced. PMD and CPD will exit now by default with exit code 5, if any recoverable error
  (e.g. parsing exception, lexing exception or rule exception) occurred. PMD will still create a report with
  all detected violations or duplications if recoverable errors occurred. Such errors mean, that the report
  might be incomplete, as either violations or duplications for an entire file or for a specific rule are missing.
  These cases can be considered as false-negatives.

  In any case, the root cause should be investigated. If it's a problem in PMD itself, please create a bug report.

* New CLI parameter `--no-fail-on-error` to ignore such errors and not exit with code 5. By default,
  a build with errors will now fail and with that parameter, the previous behavior can be restored.
  This parameter is available for both PMD and CPD.

* The CLI parameter `--skip-lexical-errors` is deprecated. Use the new parameter `--[no-]--fail-on-error` instead.

##### Ant

* CPDTask has a new parameter `failOnError`. In controls, whether to fail the build if any recoverable errors occurred.
  By default, the build will fail. CPD will still create a report with all detected duplications, but the report might
  be incomplete.
* The parameter `skipLexicalError` in CPDTask is deprecated. Use the new parameter `failOnError` instead.

#### Deprecated API

* pmd-java
  * {% jdoc !!java::lang.java.ast.ASTResource#getStableName() %} and the corresponding attribute `@StableName`

### ✨ External Contributions

{% endtocmaker %}

