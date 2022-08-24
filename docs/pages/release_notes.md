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

### Fixed Issues

* java-codestyle
    * [#4082](https://github.com/pmd/pmd/issues/4082): \[java] UnnecessaryImport false positive for on-demand imports of nested classes

### API Changes

#### Deprecated API

* In order to reduce the dependency on Apex Jorje classes, the following methods have been deprecated.
  These methods all leaked internal Jorje enums. These enums have been replaced now by enums the
  PMD's AST package.
    * {% jdoc !!apex::lang.apex.ast.ASTAssignmentExpression#getOperator() %}
    * {% jdoc !!apex::lang.apex.ast.ASTBinaryExpression#getOperator() %}
    * {% jdoc !!apex::lang.apex.ast.ASTBooleanExpression#getOperator() %}
    * {% jdoc !!apex::lang.apex.ast.ASTPostfixExpression#getOperator() %}
    * {% jdoc !!apex::lang.apex.ast.ASTPrefixExpression#getOperator() %}

  All these classes have now a new `getOp()` method. Existing code should be refactored to use this method instead.
  It returns the new enums, like {% jdoc apex::lang.apex.ast.AssignmentOperator %}, and avoids
  the dependency to Jorje.

### External Contributions

* [#4081](https://github.com/pmd/pmd/pull/4081): \[apex] Remove Jorje leaks outside `ast` package - [@eklimo](https://github.com/eklimo)
* [#4083](https://github.com/pmd/pmd/pull/4083): \[java] UnnecessaryImport false positive for on-demand imports of nested classes (fix for #4082) - [@abyss638](https://github.com/abyss638)

{% endtocmaker %}

