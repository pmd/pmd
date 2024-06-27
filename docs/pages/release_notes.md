---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### ✨ New Rules
* The new Java rule {%rule java/bestpractices/UseEnumCollections %} reports usages for `HashSet` and `HashMap`
  when the keys are of an enum type. The specialized enum collections are more space- and time-efficient.

#### 💥 pmd-compat6 removed (breaking)

The already deprecated PMD 6 compatibility module (pmd-compat6) has been removed. It was intended to be used with
older versions of the maven-pmd-plugin, but since maven-pmd-plugin 3.22.0, PMD 7 is supported directly and this
module is not needed anymore.

If you currently use this dependency (`net.sourceforge.pmd:pmd-compat6`), remove it and upgrade maven-pmd-plugin
to the latest version (3.23.0 or newer).

See also [Maven PMD Plugin]({{ baseurl }}pmd_userdocs_tools_maven.html).

### 🐛 Fixed Issues
* cli
  * [#2827](https://github.com/pmd/pmd/issues/2827): \[cli] Consider processing errors in exit status
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
  * [#577](https://github.com/pmd/pmd/issues/577): \[java] New Rule: Check that Map<K,V> is an EnumMap if K is an enum value
  * [#5047](https://github.com/pmd/pmd/issues/5047): \[java] UnusedPrivateMethod FP for Generics & Overloads
* plsql
  * [#1934](https://github.com/pmd/pmd/issues/1934): \[plsql] ParseException with MERGE statement in anonymous block
  * [#2779](https://github.com/pmd/pmd/issues/2779): \[plsql] Error while parsing statement with (Oracle) DML Error Logging
  * [#4270](https://github.com/pmd/pmd/issues/4270): \[plsql] Parsing exception COMPOUND TRIGGER with EXCEPTION handler

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

* The CLI parameter `--skip-lexical-errors` is deprecated. By default, lexical errors are skipped but the
  build is failed. Use the new parameter `--[no-]fail-on-error` instead to control whether to fail the build or not.

#### Ant

* CPDTask has a new parameter `failOnError`. It controls, whether to fail the build if any recoverable error occurred.
  By default, the build will fail. CPD will still create a report with all detected duplications, but the report might
  be incomplete.
* The parameter `skipLexicalError` in CPDTask is deprecated and ignored. Lexical errors are now always skipped.
  Use the new parameter `failOnError` instead to control whether to fail the build or not.

#### Deprecated API

* pmd-ant
  * {% jdoc !!ant::ant.CPDTask#setSkipLexicalErrors(boolean) %}: Use {% jdoc ant::ant.CPDTask#setFailOnError(boolean) %}
  instead to control, whether to ignore errors or fail the build.
* pmd-core
  * {% jdoc !!core::cpd.CPDConfiguration#isSkipLexicalErrors() %} and {% jdoc core::cpd.CPDConfiguration#setSkipLexicalErrors(boolean) %}:
  Use {%jdoc core::AbstractConfiguration#setFailOnError(boolean) %} to control whether to ignore errors or fail the build.
  * {%jdoc !!core::cpd.XMLOldRenderer %} (the CPD format "xmlold").
* pmd-java
  * {% jdoc !!java::lang.java.ast.ASTResource#getStableName() %} and the corresponding attribute `@StableName`.
  * {%jdoc !!java::lang.java.ast.ASTRecordPattern#getVarId() %} This method was added here by mistake. Record
    patterns don't declare a pattern variable for the whole pattern, but rather for individual record
    components, which can be accessed via {%jdoc java::lang.java.ast.ASTRecordPattern#getComponentPatterns() %}.

#### Breaking changes: pmd-compat6 removed

The already deprecated PMD 6 compatibility module (pmd-compat6) has been removed.
See above for details.

### ✨ External Contributions

{% endtocmaker %}

