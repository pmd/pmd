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

#### Kotlin XPath functions and type attributes
Type data is now accessible in XPath rules via new attributes and helper functions (see [Kotlin XPath rule support](pmd_languages_kotlin.html#xpath-rule-support)):

* **Attributes**: `@TypeName`, `@ReturnTypeName`, `@AnnotationFqNames`, `@Modifiers`, `@Identifier`
  are exposed on declaration nodes (property, function, class, parameter, catch, for-loop, delegation specifier,
  annotation nodes).
* **`pmd-kotlin:typeIs(typeName)`**: matches if the node's type is `typeName` or a subtype.
* **`pmd-kotlin:typeIsExactly(typeName)`**: matches the exact declared type only (no subtypes).
* **`pmd-kotlin:hasAnnotation(name)`**: matches if the node has an annotation with the given simple or FQN.
* **`pmd-kotlin:modifiers()`**: returns the modifier keywords of a declaration as a sequence.
* **`pmd-kotlin:isNullable()`**: returns `true` if the node's declared type is nullable (has `?`).
* **`pmd-kotlin:hasUnresolvedReference()`**: returns `true` if the node contains an unresolved reference.
* **`pmd-kotlin:matchesSig(signature)`**: matches call sites by method signature pattern (supports wildcards).

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule {% rule java/bestpractices/OnDemandImport %} reports on-demand imports, also known as wildcard imports.
    By default, static imports from JUnit and TestNG are allowed. The allowed static and type import packages
    can be configured with `allowStaticImportsFrom` and `allowTypeImportsFrom`.
*   The new java rule  {% rule java/errorprone/LongLiteralEndingWithLowercaseL %} finds long literals ending with l.
    That helps to avoid confusion between numbers ending with 1 and l. Capital L should be used to define long literals.
*   The new java rule  {% rule java/bestpractices/TypeNameMismatch %} finds types that are not defined in a .java file
    with the same name. Enforcing a match between source file name and type name makes it easier to
    find source code for given type.
*   The new Java rule {% rule java/codestyle/CStyleArrayDeclaration %} finds C-style declarations of arrays (e.g. `int numbers[]`).
    That helps you use Java-style declarations (e.g. `int[] numbers`) consistently throughout the codebase.
#### Changed Rules
*   The property `checkNonStaticMethods` of the rule {% rule java/multithreading/NonThreadSafeSingleton %} is now
    deprecated and no longer has any effect. Its implementation did the opposite of what the documentation described.
    The rule now always reports both static and non-static methods; previously it reported only static methods
    by default.  
    This may result in additional violations being reported.  
    If you want to suppress violations for non-static methods, you can use
    [suppression via XPath]({{ baseurl }}pmd_userdocs_suppressing_warnings.html#the-property-violationsuppressxpath), e.g.
    ```xml
    <property name="violationSuppressXPath" value=".[ancestor-or-self::MethodDeclaration[1][@Static = false()]]" />
    ```

### 🐛️ Fixed Issues
* html
    * [#6135](https://github.com/pmd/pmd/issues/6135): \[html] HtmlCpdLexer giving IndexOutOfBoundsException when script contains unescaped closing tag
* java
    * [#6926](https://github.com/pmd/pmd/issues/6926): \[java] IllegalArgumentException (Mismatched list sizes) with inconsistent unresolved generic arity
* java-bestpractices
    * [#5940](https://github.com/pmd/pmd/issues/5940): \[java] False positive in UnusedAssignment when assignment is in conditional statement
* java-codestyle
    * [#5732](https://github.com/pmd/pmd/issues/5732): \[java] UnnecessaryCast false positive with package private methods
* java-design
    * [#6694](https://github.com/pmd/pmd/issues/6694): \[java] SimplifyBooleanReturns triggers inconsistently depending on redundant parentheses in return expression
* java-errorprone
    * [#6693](https://github.com/pmd/pmd/issues/6693): \[java] CloneMethodMustImplementCloneable fires inconsistently between inline `throw new` and throw-via-local forms
    * [#7009](https://github.com/pmd/pmd/issues/7009): \[java] ReplaceJavaUtilDate is suppressed by using pattern variable
* java-multithreading
    * [#6297](https://github.com/pmd/pmd/issues/6297): \[java] AvoidUsingVolatile: Update documentation
    * [#6780](https://github.com/pmd/pmd/issues/6780): \[java] NonThreadSafeSingleton: False negative with property checkNonStaticMethods
* java-security
    * [#7007](https://github.com/pmd/pmd/issues/7007): \[java] HardCodedCryptoKey: False negative when a hard-coded key is constructed via new String(char[])
* kotlin
    * [#6677](https://github.com/pmd/pmd/issues/6677): \[kotlin] Add XPath functions and type attributes for type-aware XPath rules

### 🚨️ API Changes
#### Experimental API
* kotlin
    * {%jdoc kotlin::lang.kotlin.ast.HasTypeName %}: Marker interface for Kotlin AST nodes that expose
      a `@TypeName` XPath attribute.
    * New XPath functions `pmd-kotlin:typeIs`, `pmd-kotlin:typeIsExactly`, `pmd-kotlin:hasAnnotation`,
      `pmd-kotlin:modifiers`, `pmd-kotlin:matchesSig`, `pmd-kotlin:isNullable`, `pmd-kotlin:hasUnresolvedReference`
      in package `net.sourceforge.pmd.lang.kotlin.rule.xpath.internal`.
    * New AST attribute view classes in package `net.sourceforge.pmd.lang.kotlin.ast` (e.g.
      `KtFunctionDeclarationAttributes`, `KtPropertyDeclarationAttributes`, etc.).

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}
