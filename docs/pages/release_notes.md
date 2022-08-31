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

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.49.0).

### Fixed Issues

* apex
    * [#4096](https://github.com/pmd/pmd/issues/4096): \[apex] ApexAssertionsShouldIncludeMessage and ApexUnitTestClassShouldHaveAsserts: support new Assert class (introduced with Apex v56.0)
* core
    * [#3970](https://github.com/pmd/pmd/issues/3970): \[core] FileCollector.addFile ignores language parameter
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
* [#4092](https://github.com/pmd/pmd/pull/4092): \[apex] Implement ApexQualifiableNode for ASTUserEnum - [@aaronhurst-google](https://github.com/aaronhurst-google)
* [#4095](https://github.com/pmd/pmd/pull/4095): \[core] CPD: Added begin and end token to XML reports - [@pacvz](https://github.com/pacvz)
* [#4097](https://github.com/pmd/pmd/pull/4097): \[apex] ApexUnitTestClassShouldHaveAssertsRule: Support new Assert class (Apex v56.0) - [@tprouvot](https://github.com/tprouvot)
* [#4104](https://github.com/pmd/pmd/pull/4104): \[doc] Add MegaLinter in the list of integrations - [@nvuillam](https://github.com/nvuillam)

### Stats
* 49 commits
* 10 closed tickets & PRs
* Days since last release: 32

{% endtocmaker %}

