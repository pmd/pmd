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

#### CPD's AnyTokenizer has been improved

The AnyTokenizer is used for languages, that don't have an own lexer/grammar based tokenizer.
AnyTokenizer now handles string literals and end-of-line comments. Fortran, Perl and Ruby have
been updated to use AnyTokenizer instead of their old custom tokenizer based on AbstractTokenizer.
See [#2758](https://github.com/pmd/pmd/pull/2758) for details.

AbstractTokenizer and the custom tokenizers of Fortran, Perl and Ruby are deprecated now.

### Fixed Issues

* cpd
    * [#2758](https://github.com/pmd/pmd/pull/2758): \[cpd] Improve AnyTokenizer
    * [#2760](https://github.com/pmd/pmd/issues/2760): \[cpd] AnyTokenizer doesn't count columns correctly

* apex-security
    * [#2774](https://github.com/pmd/pmd/issues/2774): \[apex] ApexSharingViolations does not correlate sharing settings with class that contains data access

* pmd-java
    * [#2708](https://github.com/pmd/pmd/issues/2708): \[java] False positive FinalFieldCouldBeStatic when using lombok Builder.Default
    * [#2738](https://github.com/pmd/pmd/issues/2738): \[java] Custom rule with @ExhaustiveEnumSwitch throws NPE
    * [#2756](https://github.com/pmd/pmd/issues/2756): \[java] TypeTestUtil fails with NPE for anonymous class
    * [#2759](https://github.com/pmd/pmd/issues/2759): \[java] False positive in UnusedAssignment
    * [#2767](https://github.com/pmd/pmd/issues/2767): \[java] IndexOutOfBoundsException when parsing an initializer BlockStatement
    * [#2783](https://github.com/pmd/pmd/issues/2783): \[java] Error while parsing with lambda of custom interface


### API Changes

#### Deprecated API

##### For removal

* {% jdoc !!core::RuleViolationComparator %}. Use {% jdoc !!core::RuleViolation#DEFAULT_COMPARATOR %} instead.
* {% jdoc !!core::cpd.AbstractTokenizer %}. Use {% jdoc !!core::cpd.AnyTokenizer %} instead.
* {% jdoc !!fortran::cpd.FortranTokenizer %}. Was replaced by an {% jdoc core::cpd.AnyTokenizer %}. Use {% jdoc !!fortran::cpd.FortranLanguage#getTokenizer() %} anyway.
* {% jdoc !!perl::cpd.PerlTokenizer %}. Was replaced by an {% jdoc core::cpd.AnyTokenizer %}. Use {% jdoc !!perl::cpd.PerlLanguage#getTokenizer() %} anyway.
* {% jdoc !!ruby::cpd.RubyTokenizer %}. Was replaced by an {% jdoc core::cpd.AnyTokenizer %}. Use {% jdoc !!ruby::cpd.RubyLanguage#getTokenizer() %} anyway.
* {% jdoc !!core::lang.rule.RuleReference#getOverriddenLanguage() %} and
  {% jdoc !!core::lang.rule.RuleReference#setLanguage(net.sourceforge.pmd.lang.Language) %}
* Antlr4 generated lexers:
    * {% jdoc !!cs::lang.cs.antlr4.CSharpLexer %} will be moved to package `net.sourceforge.pmd.lang.cs.ast` with PMD 7.
    * {% jdoc !!dart::lang.dart.antlr4.Dart2Lexer %} will be renamed to `DartLexer` and moved to package 
      `net.sourceforge.pmd.lang.dart.ast` with PMD 7. All other classes in the old package will be removed.
    * {% jdoc !!go::lang.go.antlr4.GolangLexer %} will be moved to package
      `net.sourceforge.pmd.lang.go.ast` with PMD 7. All other classes in the old package will be removed.
    * {% jdoc !!kotlin::lang.kotlin.antlr4.Kotlin %} will be renamed to `KotlinLexer` and moved to package 
      `net.sourceforge.pmd.lang.kotlin.ast` with PMD 7.
    * {% jdoc !!lua::lang.lua.antlr4.LuaLexer %} will be moved to package
      `net.sourceforge.pmd.lang.lua.ast` with PMD 7. All other classes in the old package will be removed.


### External Contributions

* [#2735](https://github.com/pmd/pmd/pull/2735): \[ci] Add github actions for a fast view of pr succeed/not - [XenoAmess](https://github.com/XenoAmess)
* [#2747](https://github.com/pmd/pmd/pull/2747): \[java] Don't trigger FinalFieldCouldBeStatic when field is annotated with lombok @Builder.Default - [Ollie Abbey](https://github.com/ollieabbey)
* [#2773](https://github.com/pmd/pmd/pull/2773): \[java] issue-2738: Adding null check to avoid npe when switch case is default - [Nimit Patel](https://github.com/nimit-patel)
* [#2789](https://github.com/pmd/pmd/pull/2789): Add badge for reproducible build - [Dan Rollo](https://github.com/bhamail)
* [#2791](https://github.com/pmd/pmd/pull/2791): \[apex] Analyze inner classes for sharing violations - [Jeff Bartolotta](https://github.com/jbartolotta-sfdc)

{% endtocmaker %}

