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
* The new Apex rule {%rule apex/bestpractices/AvoidFutureAnnotation %} finds usages of the `@Future`
  annotation. It is a legacy way to execute asynchronous Apex code. New code should implement
  the `Queueable` interface instead.
* The new Java rule {%rule java/bestpractices/EnumComparison %} finds usages of `equals()` on
  enum constants or values. Enums should be compared directly with `==` instead of `equals()` which
  has some advantages (e.g. static type checking at compile time).
* The new Apex rule {% rule apex/design/NcssCount %} replaces the four rules "ExcessiveClassLength",
  "NcssConstructorCount", "NcssMethodCount", and "NcssTypeCount". The new rule uses the metrics framework
  to achieve the same. It has two properties, to define the report level for method and class sizes separately.
  Constructors and methods are considered the same.  
  The rule has been added to the quickstart ruleset.  
  Note: The new metric is implemented more correct than in the old rules. E.g. it considers now also
  switch statements and correctly counts if-statements only once and ignores method calls that are
  part of an expression and not a statement on their own. This leads to different numbers. Keep in mind,
  that NCSS counts statements and not lines of code. Statements that are split on multiple lines are
  still counted as one.
* The new PL/SQL rule {% rule plsql/design/NcssCount %} replaces the rules "ExcessiveMethodLength",
  "ExcessiveObjectLength", "ExcessivePackageBodyLength", "ExcessivePackageSpecificationLength",
  "ExcessiveTypeLength", "NcssMethodCount" and "NcssObjectCount". The new rule uses the metrics framework
  to achieve the same. It has two properties, to define the report level for method and object sizes separately.  
  Note: the new metric is implemented more correct than in the old rules, so that the actual numbers of
  the NCSS metric from the old rules might be different from the new rule "NcssCount". Statements that are
  split on multiple lines are still counted as one.

#### Deprecated Rules
* The Apex rule {% rule apex/design/ExcessiveClassLength %} has been deprecated. Use {%rule apex/design/NcssCount %} to
  find big classes or create a custom XPath based rule using
  `//ApexFile[UserClass][@EndLine - @BeginLine > 1000]`.
* The Apex rules {% rule apex/design/NcssConstructorCount %}, {%rule apex/design/NcssMethodCount %}, and
  {% rule apex/design/NcssTypeCount %} have been deprecated in favor or the new rule {%rule apex/design/NcssCount %}.
* The PL/SQL rule {% rule plsql/design/ExcessiveMethodLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//(MethodDeclaration|ProgramUnit|TriggerTimingPointSection|TriggerUnit|TypeMethod)[@EndLine - @BeginLine > 100]`.
* The PL/SQL rule {% rule plsql/design/ExcessiveObjectLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//(PackageBody|PackageSpecification|ProgramUnit|TriggerUnit|TypeSpecification)[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule {% rule plsql/design/ExcessivePackageBodyLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//PackageBody[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule {% rule plsql/design/ExcessivePackageSpecificationLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//PackageSpecification[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule {% rule plsql/design/ExcessiveTypeLength %} has been deprecated. Use {% rule plsql/design/NcssCount %}
  instead or create a custom XPath based rule using
  `//TypeSpecification[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rules {% rule plsql/design/NcssMethodCount %} and {% rule plsql/design/NcssObjectCount %} have been
  deprecated in favor of the new rule {% rule plsql/design/NcssCount %}.

### 🐛️ Fixed Issues
* core
    * [#4767](https://github.com/pmd/pmd/issues/4767): \[core] Deprecate old symboltable API
* apex-bestpractices
    * [#6203](https://github.com/pmd/pmd/issues/6203): \[apex] New Rule: Avoid Future Annotation
* apex-design
    * [#2128](https://github.com/pmd/pmd/issues/2128): \[apex] Merge NCSS count rules for Apex
* java
    * [#5689](https://github.com/pmd/pmd/issues/5689): \[java] Members of record should be in scope in record header
* java-bestpractices
    * [#5820](https://github.com/pmd/pmd/issues/5820): \[java] GuardLogStatement recognizes that a string is a compile-time constant expression only if at first position
    * [#6188](https://github.com/pmd/pmd/issues/6188): \[java] UnitTestShouldIncludeAssert false positive when TestNG @<!-- -->Test.expectedException present
    * [#6193](https://github.com/pmd/pmd/issues/6193): \[java] New Rule: Always compare enum values with ==
* java-codestyle
    * [#6053](https://github.com/pmd/pmd/issues/6053): \[java] ModifierOrder false-positives with type annotations and type parameters (typeAnnotations = anywhere)
* java-errorprone
    * [#4742](https://github.com/pmd/pmd/issues/4742): \[java] EmptyFinalizer should not trigger if finalize method is final and class is not
    * [#6072](https://github.com/pmd/pmd/issues/6072): \[java] OverrideBothEqualsAndHashCodeOnComparable should not be required for record classes
    * [#6092](https://github.com/pmd/pmd/issues/6092): \[java] AssignmentInOperand false positive in 7.17.0 for case blocks in switch statements
    * [#6096](https://github.com/pmd/pmd/issues/6096): \[java] OverrideBothEqualsAndHashCodeOnComparable on class with lombok.EqualsAndHashCode annotation
    * [#6199](https://github.com/pmd/pmd/issues/6199): \[java] AssignmentInOperand: description of property allowIncrementDecrement is unclear
    * [#6273](https://github.com/pmd/pmd/issues/6273): \[java] TestClassWithoutTestCases documentation does not mention test prefixes
* java-performance
    * [#4577](https://github.com/pmd/pmd/issues/4577): \[java] UseArraysAsList with condition in loop
    * [#5071](https://github.com/pmd/pmd/issues/5071): \[java] UseArraysAsList should not warn when elements are skipped in array
* plsql-design
    * [#4326](https://github.com/pmd/pmd/issues/4326): \[plsql] Merge NCSS count rules for PL/SQL
* maintenance
    * [#5701](https://github.com/pmd/pmd/issues/5701): \[core] net.sourceforge.pmd.cpd.SourceManager has public methods

### 🚨️ API Changes

#### Deprecations
* core
    * {%jdoc_package core::lang.symboltable %}: All classes in this package are deprecated.
      The symbol table and type resolution implementation for Java has been rewritten from scratch
      for PMD 7.0.0. This package is the remains of the old symbol table API, that is only used by
      PL/SQL. For PMD 8.0.0 all these classes will be removed from pmd-core.
* apex
    * {% jdoc apex::lang.apex.rule.design.ExcessiveClassLengthRule %}
    * {% jdoc apex::lang.apex.rule.design.NcssConstructorCountRule %}
    * {% jdoc apex::lang.apex.rule.design.NcssMethodCountRule %}
    * {% jdoc apex::lang.apex.rule.design.NcssTypeCountRule %}
    * {% jdoc apex::lang.apex.ast.ASTStatement %}: This AST node is not used and doesn't appear in the tree.
    * {% jdoc !ac!apex::lang.apex.ast.ApexVisitor#visit(apex::lang.apex.ast.ASTStatement,P) %}
* plsql
    * {% jdoc plsql::lang.plsql.rule.design.ExcessiveMethodLengthRule %}
    * {% jdoc plsql::lang.plsql.rule.design.ExcessiveObjectLengthRule %}
    * {% jdoc plsql::lang.plsql.rule.design.ExcessivePackageBodyLengthRule %}
    * {% jdoc plsql::lang.plsql.rule.design.ExcessivePackageSpecificationLengthRule %}
    * {% jdoc plsql::lang.plsql.rule.design.ExcessiveTypeLengthRule %}
    * {% jdoc plsql::lang.plsql.rule.design.NcssMethodCountRule %}
    * {% jdoc plsql::lang.plsql.rule.design.NcssObjectCountRule %}

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6081](https://github.com/pmd/pmd/pull/6081): \[java] Fix #6072: OverrideBothEqualsAndHashCodeOnComparable should not be required for record classes - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6192](https://github.com/pmd/pmd/pull/6192): \[java] Fix #6053: ModifierOrder - consider type params - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6194](https://github.com/pmd/pmd/pull/6194): chore: always place type annotations on the type - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6195](https://github.com/pmd/pmd/pull/6195): chore: always compare enums with == - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6196](https://github.com/pmd/pmd/pull/6196): \[java] New Rule: EnumComparison - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6198](https://github.com/pmd/pmd/pull/6198): \[apex] New rule NcssCount to replace old Ncss*Count rules - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6201](https://github.com/pmd/pmd/pull/6201): \[java] Fix #6199: AssignmentInOperandRule: Update description of allowIncrementDecrement property - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6202](https://github.com/pmd/pmd/pull/6202): \[java] Fix #6188: UnitTestsShouldIncludeAssert - FP when TestNG @<!-- -->Test.expectedException is present - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6204](https://github.com/pmd/pmd/pull/6204): \[apex] Add rule to limit usage of @<!-- -->Future annotation - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#6217](https://github.com/pmd/pmd/pull/6217): \[doc] Add Blue Cave to known tools using PMD - [Jude Pereira](https://github.com/judepereira) (@judepereira)
* [#6214](https://github.com/pmd/pmd/pull/6214): \[plsql] New rule NcssCount to replace old Ncss*Count rules - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6227](https://github.com/pmd/pmd/pull/6227): \[java] UseArraysAsList: check increment - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6228](https://github.com/pmd/pmd/pull/6228): \[java] UseArraysAsList: skip when if-statements - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6229](https://github.com/pmd/pmd/pull/6229): chore: remove public methods from SourceManager - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6238](https://github.com/pmd/pmd/pull/6238): \[java] Fix #6096: Detect Lombok generated equals/hashCode in Comparable - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6249](https://github.com/pmd/pmd/pull/6249): \[core] Deprecate old symboltable API - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6250](https://github.com/pmd/pmd/pull/6250): chore: fail build for compiler warnings - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6251](https://github.com/pmd/pmd/pull/6251): \[java] Fix #6092: AssignmentInOperand false positive in 7.17.0 for case statements - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6255](https://github.com/pmd/pmd/pull/6255): \[java] Fix #4742: EmptyFinalizer should not trigger if finalize method is final and class is not - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6258](https://github.com/pmd/pmd/pull/6258): \[java] Fix #5820: GuardLogStatement recognizes that a string is a compile-time constant expression only if at first position - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6259](https://github.com/pmd/pmd/pull/6259): \[java] Fix #5689: Issue with scoping of record members - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6277](https://github.com/pmd/pmd/pull/6277): \[doc] Add button to copy configuration snippet - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6278](https://github.com/pmd/pmd/pull/6278): \[doc] TestClassWithoutTestCases: Mention test prefixes - [Marcel](https://github.com/mrclmh) (@mrclmh)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

