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
* core
  * [#4467](https://github.com/pmd/pmd/issues/4467): \[core] Expose collections from getters as XPath sequence attributes
  * [#4978](https://github.com/pmd/pmd/issues/4978): \[core] Referenced Rulesets do not emit details on validation errors
  * [#4983](https://github.com/pmd/pmd/pull/4983): \[cpd] Fix CPD crashes about unicode escapes
* java
  * [#4912](https://github.com/pmd/pmd/issues/4912): \[java] Unable to parse some Java9+ resource references
  * [#4973](https://github.com/pmd/pmd/pull/4973): \[java] Stop parsing Java for CPD
  * [#4980](https://github.com/pmd/pmd/issues/4980): \[java] Bad intersection, unrelated class types java.lang.Object\[] and java.lang.Number
  * [#4988](https://github.com/pmd/pmd/pull/4988): \[java] Fix impl of ASTVariableId::isResourceDeclaration / VariableId/@<!-- -->ResourceDeclaration
  * [#4990](https://github.com/pmd/pmd/issues/4990): \[java] Add an attribute @<!-- -->PackageQualifier to ASTClassType
  * [#5006](https://github.com/pmd/pmd/issues/5006): \[java] Bad intersection, unrelated class types Child and Parent<? extends Child>
  * [#5029](https://github.com/pmd/pmd/issues/5029): \[java] PMD 7.x throws stack overflow in TypeOps$ProjectionVisitor while parsing a Java class
* java-bestpractices
  * [#4278](https://github.com/pmd/pmd/issues/4278): \[java] UnusedPrivateMethod FP with Junit 5 @MethodSource and default factory method name
  * [#4852](https://github.com/pmd/pmd/issues/4852): \[java] ReplaceVectorWithList false-positive (neither Vector nor List usage) 
  * [#4975](https://github.com/pmd/pmd/issues/4975): \[java] UnusedPrivateMethod false positive when using @MethodSource on a @Nested test
  * [#4985](https://github.com/pmd/pmd/issues/4985): \[java] UnusedPrivateMethod false-positive / method reference in combination with custom object
* java-codestyle
  * [#4930](https://github.com/pmd/pmd/issues/4930): \[java] EmptyControlStatement should not allow empty try with concise resources
  * [#4954](https://github.com/pmd/pmd/issues/4954): \[java] LocalVariableNamingConventions should allow unnamed variables by default
  * [#5028](https://github.com/pmd/pmd/issues/5028): \[java] FormalParameterNamingConventions should accept unnamed parameters by default
* java-errorprone
  * [#4042](https://github.com/pmd/pmd/issues/4042): \[java] A false negative about the rule StringBufferInstantiationWithChar
  * [#5007](https://github.com/pmd/pmd/issues/5007): \[java] AvoidUsingOctalValues triggers on non-octal double literals with a leading 0
* java-multithreading
  * [#2368](https://github.com/pmd/pmd/issues/2368): \[java] False positive UnsynchronizedStaticFormatter in static initializer

### 🚨 API Changes

#### Deprecated API

* pmd-java
  * {% jdoc !!java::lang.java.ast.ASTResource#getStableName() %} and the corresponding attribute `@StableName`

### ✨ External Contributions

* [#5020](https://github.com/pmd/pmd/issues/5020): \[java] Fix AvoidUsingOctalValues false-positive - [Gold856](https://github.com/Gold856) (@Gold856)

{% endtocmaker %}

