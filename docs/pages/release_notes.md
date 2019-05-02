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

*   [#1803](https://github.com/pmd/pmd/pull/1803): \[dart] \[cpd] Dart escape sequences - [Maikel Steneker](https://github.com/maikelsteneker)

{% endtocmaker %}

