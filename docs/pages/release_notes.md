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

#### New Rules

*   The new Java rule {% rule "java/bestpractices/AvoidReassigningCatchVariables" %} (`java-bestpractices`) finds
    cases where the variable of the caught exception is reassigned. This practice is surprising and prevents
    further evolution of the code like multi-catch.

#### Deprecated Rules

*   The Java rule {% rule "java/errorprone/DataflowAnomalyAnalysis" %} (`java-errorprone`)
    is deprecated in favour of {% rule "java/bestpractices/UnusedAssignment" %} (`java-bestpractices`),
    which was introduced in PMD 6.26.0.

### Fixed Issues

*   core
    *   [#724](https://github.com/pmd/pmd/issues/724): \[core] Avoid parsing rulesets multiple times
    *   [#1962](https://github.com/pmd/pmd/issues/1962): \[core] Simplify Report API
    *   [#2653](https://github.com/pmd/pmd/issues/2653): \[lang-test] Upgrade kotlintest to Kotest
*   java-bestpractices
    *   [#2471](https://github.com/pmd/pmd/issues/2471): \[java] New Rule: AvoidReassigningCatchVariables
    *   [#2668](https://github.com/pmd/pmd/issues/2668): \[java] UnusedAssignment false positives
*   java-errorprone
    *   [#2410](https://github.com/pmd/pmd/issues/2410): \[java] ProperCloneImplementation not valid for final class
    *   [#2431](https://github.com/pmd/pmd/issues/2431): \[java] InvalidLogMessageFormatRule throws IndexOutOfBoundsException when only logging exception message
    *   [#2439](https://github.com/pmd/pmd/issues/2439): \[java] AvoidCatchingThrowable can not detect the case: catch (java.lang.Throwable t)
    *   [#2647](https://github.com/pmd/pmd/issues/2647): \[java] Deprecate rule DataFlowAnomalyAnalysis
*   java-performance
    *   [#2441](https://github.com/pmd/pmd/issues/2441): \[java] RedundantFieldInitializer can not detect a special case for char initialize: `char foo = '\0';`

### API Changes

*   XML rule definition in rulesets: In PMD 7, the `language` attribute will be required on all `rule`
    elements that declare a new rule. Some base rule classes set the language implicitly in their
    constructor, and so this is not required in all cases for the rule to work. But this
    behavior will be discontinued in PMD 7, so missing `language` attributes are now
    reported as a forward compatibility warning.

#### Deprecated API

*   {% jdoc !!core::Rule#getParserOptions() %}
*   {% jdoc !!core::lang.Parser#getParserOptions() %}
*   {% jdoc !!core::lang.AbstractParser %}
*   {% jdoc !!core::RuleContext#removeAttribute(java.lang.String) %}
*   {% jdoc !!core::RuleContext#getAttribute(java.lang.String) %}
*   {% jdoc !!core::RuleContext#setAttribute(java.lang.String, java.lang.Object) %}
*   {% jdoc apex::lang.apex.ApexParserOptions %}
*   {% jdoc !!java::lang.java.ast.ASTThrowStatement#getFirstClassOrInterfaceTypeImage() %}
*   {% jdoc javascript::lang.ecmascript.EcmascriptParserOptions %}
*   {% jdoc javascript::lang.ecmascript.rule.EcmascriptXPathRule %}
*   {% jdoc xml::lang.xml.XmlParserOptions %}
*   {% jdoc xml::lang.xml.rule.XmlXPathRule %}
*   Properties of {% jdoc xml::lang.xml.rule.AbstractXmlRule %}

*   {% jdoc !!core::Report.ReadableDuration %}
*   Many methods of {% jdoc !!core::Report %}. They are replaced by accessors
  that produce a List. For example, {% jdoc !a!core::Report#iterator() %} 
  (and implementing Iterable) and {% jdoc !a!core::Report#isEmpty() %} are both
  replaced by {% jdoc !a!core::Report#getViolations() %}.

*   The dataflow codebase is deprecated for removal in PMD 7. This
    includes all code in the following packages, and their subpackages:
    *   {% jdoc_package plsql::lang.plsql.dfa %}
    *   {% jdoc_package java::lang.java.dfa %}
    *   {% jdoc_package core::lang.dfa %}
    *   and the class {% jdoc plsql::lang.plsql.PLSQLDataFlowHandler %}

### External Contributions

*   [#2677](https://github.com/pmd/pmd/pull/2677): \[java] RedundantFieldInitializer can not detect a special case for char initialize: `char foo = '\0';` - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2678](https://github.com/pmd/pmd/pull/2678): \[java] AvoidCatchingThrowable can not detect the case: catch (java.lang.Throwable t) - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2679](https://github.com/pmd/pmd/pull/2679): \[java] InvalidLogMessageFormatRule throws IndexOutOfBoundsException when only logging exception message - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2682](https://github.com/pmd/pmd/pull/2682): \[java] New Rule: AvoidReassigningCatchVariables - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2699](https://github.com/pmd/pmd/pull/2699): \[java] ProperCloneImplementation not valid for final class - [Mykhailo Palahuta](https://github.com/Drofff)


{% endtocmaker %}

