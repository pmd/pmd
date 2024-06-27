---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

### 🐛 Fixed Issues
* core
  * [#4992](https://github.com/pmd/pmd/pull/4992): \[core] CPD: Include processing errors in XML report
* apex
  * [#4922](https://github.com/pmd/pmd/issues/4922): \[apex] SOQL syntax error with TYPEOF in sub-query
  * [#5053](https://github.com/pmd/pmd/issues/5053): \[apex] CPD fails to parse string literals with escaped characters
  * [#5055](https://github.com/pmd/pmd/issues/5055): \[apex] SOSL syntax error with WITH USER_MODE or WITH SYSTEM_MODE
* apex-bestpractices
  * [#5000](https://github.com/pmd/pmd/issues/5000): \[apex] UnusedLocalVariable FP with binds in SOSL / SOQL
* java
  * [#5050](https://github.com/pmd/pmd/issues/5050): \[java] Problems with pattern variables in switch branches
* java-bestpractices
  * [#5047](https://github.com/pmd/pmd/issues/5047): \[java] UnusedPrivateMethod FP for Generics & Overloads
* plsql
  * [#1934](https://github.com/pmd/pmd/issues/1934): \[plsql] ParseException with MERGE statement in anonymous block
  * [#2779](https://github.com/pmd/pmd/issues/2779): \[plsql] Error while parsing statement with (Oracle) DML Error Logging

### 🚨 API Changes

#### CPD Report Format XML

There are some important changes:

1. The XML format will now use an XSD schema, that is available at <https://pmd.github.io/schema/cpd-report_1_0_0.xsd>.
   This schema defines the valid elements and attributes that one can expect from a CPD report.
2. The root element `pmd-cpd` contains the new attributes `pmdVersion`, `timestamp` and `version`. The latter is
   the schema version and is currently "1.0.0".
3. The CPD XML report will now also contain recoverable errors as additional `<error>` elements.

See [Report formats for CPD](pmd_userdocs_cpd_report_formats.html#xml) for an example.

The XML format should be compatible as only attributes and elements have been added. However, if you parse
the document with a namespace aware parser, you might encounter some issues like no elements being found.
In case the new format doesn't work for you (e.g. namespaces, unexpected error elements), you can
go back using the old format with the renderer "xmlold" ({%jdoc core::cpd.XMLOldRenderer %}). Note, that
this old renderer is deprecated and only there for compatibility reasons. Whatever tooling is used to
read the XML format should be updated.

#### Deprecated for removal

* core
  * {%jdoc !!core::cpd.XMLOldRenderer %} (the CPD format "xmlold").
* pmd-java
  * {%jdoc !!java::lang.java.ast.ASTRecordPattern#getVarId() %} This method was added here by mistake. Record
    patterns don't declare a pattern variable for the whole pattern, but rather for individual record
    components, which can be accessed via {%jdoc java::lang.java.ast.ASTRecordPattern#getComponentPatterns() %}.

### ✨ External Contributions

{% endtocmaker %}

