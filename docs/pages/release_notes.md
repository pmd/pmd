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

#### Enhanced Matlab support

Thanks to the contributions from [Maikel Steneker](https://github.com/maikelsteneker) CPD for Matlab can
now parse Matlab programs which use the question mark operator to specify access to
class members:

```
classdef Class1
properties (SetAccess = ?Class2)
```

CPD also understands now double quoted strings, which are supported since version R2017a of Matlab:

```
str = "This is a string"
```

#### Enhanced C++ support

CPD now supports digit separators in C++ (language module "cpp"). This is a C++14 feature.

Example: `auto integer_literal = 1'000'000;`

The single quotes can be used to add some structure to large numbers.

CPD also parses raw string literals now correctly (see [#1784](https://github.com/pmd/pmd/issues/1784)).

#### New Rules

*   The new Apex rule {% rule "apex/codestyle/FieldNamingConventions" %} (`apex-codestyle`) checks the naming
    conventions for field declarations. By default this rule uses the standard Apex naming convention (Camel case),
    but it can be configured through properties.

*   The new Apex rule {% rule "apex/codestyle/FormalParameterNamingConventions" %} (`apex-codestyle`) checks the
    naming conventions for formal parameters of methods. By default this rule uses the standard Apex naming
    convention (Camel case), but it can be configured through properties.

*   The new Apex rule {% rule "apex/codestyle/LocalVariableNamingConventions" %} (`apex-codestyle`) checks the
    naming conventions for local variable declarations. By default this rule uses the standard Apex naming
    convention (Camel case), but it can be configured through properties.

*   The new Apex rule {% rule "apex/codestyle/PropertyNamingConventions" %} (`apex-codestyle`) checks the naming
    conventions for property declarations. By default this rule uses the standard Apex naming convention (Camel case),
    but it can be configured through properties.

#### Modified Rules

*   The Apex rule {% rule "apex/codestyle/ClassNamingConventions" %} (`apex-codestyle`) can now be configured
    using various properties for the specific kind of type declarations (e.g. class, interface, enum).
    As before, this rule uses by default the standard Apex naming convention (Pascal case).

*   The Apex rule {% rule "apex/codestyle/MethodNamingConventions" %} (`apex-codestyle`) can now be configured
    using various properties to differenciate e.g. static methods and test methods.
    As before, this rule uses by default the standard Apex naming convention (Camel case).

*   The Java rule {% rule "java/codestyle/FieldNamingConventions" %} (`java-codestyle`) now by default ignores
    the field `serialPersistentFields`. Since this is a field which needs to have this special name, no
    field naming conventions can be applied here. It is excluded the same way like `serialVersionUID` via the
    property `exclusions`.

*   The Java rule {% rule "java/documentation/CommentRequired" %} (`java-documentation`) has a new property
    `serialPersistentFieldsCommentRequired` with the default value "Ignored". This means that from now
    on comments for the field `serialPersistentFields` are not required anymore. You can change the property
    to restore the old behavior.

*   The Java rule {% rule "java/codestyle/CommentDefaultAccessModifier" %} (`java-codestyle`) now reports also
    missing comments for top-level classes and annotations, that are package-private.

#### Deprecated Rules

*   The Apex rule {% rule "apex/codestyle/VariableNamingConventions" %} (`apex-codestyle`) has been deprecated and
    will be removed with PMD 7.0.0. The rule is replaced by the more general rules
    {% rule "apex/codestyle/FieldNamingConventions" %},
    {% rule "apex/codestyle/FormalParameterNamingConventions" %},
    {% rule "apex/codestyle/LocalVariableNamingConventions" %}, and
    {% rule "apex/codestyle/PropertyNamingConventions" %}.

### Fixed Issues

*   apex
    *   [#1321](https://github.com/pmd/pmd/issues/1321): \[apex] Should VariableNamingConventions require properties to start with a lowercase letter?
    *   [#1783](https://github.com/pmd/pmd/issues/1783): \[apex] comments on constructor not recognized when the Class has inner class
*   cpp
    *   [#1784](https://github.com/pmd/pmd/issues/1784): \[cpp] Improve support for raw string literals
*   dart
    *   [#1809](https://github.com/pmd/pmd/issues/1809): \[dart] \[cpd] Parse error with escape sequences
*   java
    *   [#1842](https://github.com/pmd/pmd/issues/1842): \[java] Annotated module declarations cause parse error
*   java-bestpractices
    *   [#1738](https://github.com/pmd/pmd/issues/1738): \[java] MethodReturnsInternalArray does not work in inner classes
*   java-codestyle
    *   [#1684](https://github.com/pmd/pmd/issues/1684): \[java] Properly whitelist serialPersistentFields
    *   [#1804](https://github.com/pmd/pmd/issues/1804): \[java] NPE in UnnecessaryLocalBeforeReturnRule
*   python
    *   [#1810](https://github.com/pmd/pmd/issues/1810): \[python] \[cpd] Parse error when using Python 2 backticks
*   matlab
    *   [#1830](https://github.com/pmd/pmd/issues/1830): \[matlab] \[cpd] Parse error with comments
    *   [#1793](https://github.com/pmd/pmd/issues/1793): \[java] CommentDefaultAccessModifier not working for classes

### API Changes

#### Deprecated APIs

##### For removal

*   The `DumpFacades` in all languages, that could be used to transform a AST into a textual representation,
    will be removed with PMD 7. The rule designer is a better way to inspect nodes.
    *   {% jdoc !q!apex::lang.apex.ast.DumpFacade %}
    *   {% jdoc !q!java::lang.java.ast.DumpFacade %}
    *   {% jdoc !q!javascript::lang.ecmascript.ast.DumpFacade %}
    *   {% jdoc !q!jsp::lang.jsp.ast.DumpFacade %}
    *   {% jdoc !q!plsql::lang.plsql.ast.DumpFacade %}
    *   {% jdoc !q!visualforce::lang.vf.ast.DumpFacade %}
    *   {% jdoc !q!vm::lang.vm.ast.AbstractVmNode#dump(String, boolean, Writer) %}
    *   {% jdoc !q!xml::lang.xml.ast.DumpFacade %}
*   The method {% jdoc !c!core::lang.LanguageVersionHandler#getDumpFacade(Writer, String, boolean) %} will be
    removed as well. It is deprecated, along with all its implementations in the subclasses of {% jdoc core::lang.LanguageVersionHandler %}.

### External Contributions

*   [#1798](https://github.com/pmd/pmd/pull/1798): \[java] Make CommentDefaultAccessModifier work for top-level classes - [Boris Petrov](https://github.com/boris-petrov)
*   [#1799](https://github.com/pmd/pmd/pull/1799): \[java] MethodReturnsInternalArray does not work in inner classes - Fixed #1738 - [Srinivasan Venkatachalam](https://github.com/Srini1993)
*   [#1802](https://github.com/pmd/pmd/pull/1802): \[python] \[cpd] Add support for Python 2 backticks - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1803](https://github.com/pmd/pmd/pull/1803): \[dart] \[cpd] Dart escape sequences - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1807](https://github.com/pmd/pmd/pull/1807): \[ci] Fix missing local branch issues when executing pmd-regression-tester - [BBG](https://github.com/djydewang)
*   [#1813](https://github.com/pmd/pmd/pull/1813): \[matlab] \[cpd] Matlab comments - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1816](https://github.com/pmd/pmd/pull/1816): \[apex] Fix ApexDoc handling with inner classes - [Jeff Hube](https://github.com/jeffhube)
*   [#1817](https://github.com/pmd/pmd/pull/1817): \[apex] Add configurable naming convention rules - [Jeff Hube](https://github.com/jeffhube)
*   [#1819](https://github.com/pmd/pmd/pull/1819): \[cpp] \[cpd] Add support for digit separators - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1820](https://github.com/pmd/pmd/pull/1820): \[cpp] \[cpd] Improve support for raw string literals - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1821](https://github.com/pmd/pmd/pull/1821): \[matlab] \[cpd] Matlab question mark token - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1822](https://github.com/pmd/pmd/pull/1822): \[matlab] \[cpd] Double quoted string - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1837](https://github.com/pmd/pmd/pull/1837): \[core] Minor performance improvements - [Michael Hausegger](https://github.com/TheRealHaui)
*   [#1838](https://github.com/pmd/pmd/pull/1838): \[dart] [cpd] Improved string tokenization - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1840](https://github.com/pmd/pmd/pull/1840): \[java] Whitelist serialPersistentFields - [Marcel Härle](https://github.com/marcelhaerle)

{% endtocmaker %}

