---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% comment %}
This allows to use links e.g. [Basic CLI usage]({{ baseurl }}pmd_userdocs_installation.html) that work both
in the release notes on GitHub (as an absolute url) and on the rendered documentation page (as a relative url).
{% endcomment %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule {% rule java/bestpractices/ControlFlowInTest %}
    reports control flow in test methods that can obscure which assertions
    are executed. It checks conditionals, switch statements and expressions,
    explicit throw statements, try statements with catch clauses,
    and plain try-finally statements.
    Loops are allowed by default and can be reported by setting
    `allowLoops` to `false`.

### 🐛️ Fixed Issues
* apex
    * [#7182](https://github.com/pmd/pmd/issues/7182): \[apex] Expose the referenced type name on ClassRefExpression for XPath rules
* apex-design
    * [#4863](https://github.com/pmd/pmd/issues/4863): \[apex] CyclomaticComplexity: False negative with switch statements
* java
    * [#7144](https://github.com/pmd/pmd/issues/7144): \[java] Lambda block body that cannot complete normally is not treated as value-compatible
    * [#7240](https://github.com/pmd/pmd/issues/7240): \[java] "Not a Java binary name" when importing a class with non-ASCII (e.g. Japanese) letters in its name; the whole file is skipped 
    * [#7313](https://github.com/pmd/pmd/issues/7313): \[java] Casts to boolean and boolean equality are not folded as constant expressions
* java-bestpractices
    * [#7116](https://github.com/pmd/pmd/issues/7116): \[java] New rule: ControlFlowInTest
    * [#7216](https://github.com/pmd/pmd/issues/7216): \[java] GuardLogStatement: false positive on SLF4J parameterised calls whose arguments are wrapped with logstash value(...)
* java-design
    * [#7279](https://github.com/pmd/pmd/issues/7279): \[java] InternalApiUsage: false positive when static filed is used in constructor
    * [#7277](https://github.com/pmd/pmd/issues/7277): \[java] AvoidRethrowingException: false negative if rethrow is wrapped in a block, ExceptionAsFlowControl false positive instead
* java-errorprone
    * [#4531](https://github.com/pmd/pmd/issues/4531): \[java] MissingStaticMethodInNonInstantiatableClass: False positive with multiple top-level classes
    * [#7275](https://github.com/pmd/pmd/issues/7275): \[java] AvoidBranchingStatementAsLastInLoop: False negative when the statement is in a bare block
* kotlin
    * [#7168](https://github.com/pmd/pmd/pull/7168): \[kotlin] Add @<!-- -->TypeName to VariableDeclaration, fix destructuring type leak
    * [#7171](https://github.com/pmd/pmd/issues/7171): \[kotlin] Support the ..< range operator (Kotlin 1.9)

### 🚨️ API Changes
#### Experimental API
* kotlin
    * {% jdoc kotlin::lang.kotlin.ast.KtVariableDeclarationAttributes %} now implements
      {% jdoc kotlin::lang.kotlin.ast.HasTypeName %}, so `VariableDeclaration` nodes have a `@TypeName` attribute.
      This covers property and for-loop variables, lambda parameters, and each component of a
      destructuring declaration (`val (a, b) = ...`, `for ((a, b) in ...)`, `{ (a, b) -> ... }`).
      The XPath functions `pmd-kotlin:typeIs`, `pmd-kotlin:typeIsExactly` and `pmd-kotlin:isNullable`
      support `VariableDeclaration` nodes as well.
    * A destructuring `PropertyDeclaration` (e.g. `val (a, b) = Pair(1, "x")`) no longer has a `@TypeName`.
      Before, it wrongly got the type of the first component. To find destructured variables
      by type, query the component nodes instead: `//VariableDeclaration[pmd-kotlin:typeIs('kotlin.Int')]`.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

