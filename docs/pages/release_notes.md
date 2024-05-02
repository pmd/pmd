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

### 🐛 Fixed Issues

* core
  * [#4978](https://github.com/pmd/pmd/issues/4978): \[core] Referenced Rulesets do not emit details on validation errors
  * [#4983](https://github.com/pmd/pmd/pull/4983): \[cpd] Fix CPD crashes about unicode escapes
* java
  * [#4912](https://github.com/pmd/pmd/issues/4912): \[java] Unable to parse some Java9+ resource references
  * [#4973](https://github.com/pmd/pmd/pull/4973): \[java] Stop parsing Java for CPD
  * [#4988](https://github.com/pmd/pmd/pull/4988): \[java] Fix impl of ASTVariableId::isResourceDeclaration / VariableId/@<!-- -->ResourceDeclaration
* java-bestpractices
  * [#4278](https://github.com/pmd/pmd/issues/4278): \[java] UnusedPrivateMethod FP with Junit 5 @MethodSource and default factory method name
  * [#4852](https://github.com/pmd/pmd/issues/4852): \[java] ReplaceVectorWithList false-positive (neither Vector nor List usage) 
  * [#4975](https://github.com/pmd/pmd/issues/4975): \[java] UnusedPrivateMethod false positive when using @MethodSource on a @Nested test
  * [#4985](https://github.com/pmd/pmd/issues/4985): \[java] UnusedPrivateMethod false-positive / method reference in combination with custom object
* java-codestyle
  * [#4930](https://github.com/pmd/pmd/issues/4930): \[java] EmptyControlStatement should not allow empty try with concise resources

### 🚨 API Changes

#### Deprecated API

* pmd-java
  * {% jdoc !!java::lang.java.ast.ASTResource#getStableName() %} and the corresponding attribute `@StableName`

### ✨ External Contributions

{% endtocmaker %}

