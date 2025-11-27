


## 28-November-2025 - 7.19.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.19.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### New Rules
* The new Apex rule [`AvoidFutureAnnotation`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_apex_bestpractices.html#avoidfutureannotation) finds usages of the `@Future`
  annotation. It is a legacy way to execute asynchronous Apex code. New code should implement
  the `Queueable` interface instead.
* The new Java rule [`EnumComparison`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_java_bestpractices.html#enumcomparison) finds usages of `equals()` on
  enum constants or values. Enums should be compared directly with `==` instead of `equals()` which
  has some advantages (e.g. static type checking at compile time).
* The new Apex rule [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_apex_design.html#ncsscount) replaces the four rules "ExcessiveClassLength",
  "NcssConstructorCount", "NcssMethodCount", and "NcssTypeCount". The new rule uses the metrics framework
  to achieve the same. It has two properties, to define the report level for method and class sizes separately.
  Constructors and methods are considered the same.  
  The rule has been added to the quickstart ruleset.  
  Note: The new metric is implemented more correct than in the old rules. E.g. it considers now also
  switch statements and correctly counts if-statements only once and ignores method calls that are
  part of an expression and not a statement on their own. This leads to different numbers. Keep in mind,
  that NCSS counts statements and not lines of code. Statements that are split on multiple lines are
  still counted as one.
* The new PL/SQL rule [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#ncsscount) replaces the rules "ExcessiveMethodLength",
  "ExcessiveObjectLength", "ExcessivePackageBodyLength", "ExcessivePackageSpecificationLength",
  "ExcessiveTypeLength", "NcssMethodCount" and "NcssObjectCount". The new rule uses the metrics framework
  to achieve the same. It has two properties, to define the report level for method and object sizes separately.  
  Note: the new metric is implemented more correct than in the old rules, so that the actual numbers of
  the NCSS metric from the old rules might be different from the new rule "NcssCount". Statements that are
  split on multiple lines are still counted as one.

#### Deprecated Rules
* The Apex rule [`ExcessiveClassLength`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_apex_design.html#excessiveclasslength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_apex_design.html#ncsscount) to
  find big classes or create a custom XPath based rule using
  `//ApexFile[UserClass][@EndLine - @BeginLine > 1000]`.
* The Apex rules [`NcssConstructorCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_apex_design.html#ncssconstructorcount), [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_apex_design.html#ncssmethodcount), and
  [`NcssTypeCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_apex_design.html#ncsstypecount) have been deprecated in favor or the new rule [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_apex_design.html#ncsscount).
* The PL/SQL rule [`ExcessiveMethodLength`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#excessivemethodlength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//(MethodDeclaration|ProgramUnit|TriggerTimingPointSection|TriggerUnit|TypeMethod)[@EndLine - @BeginLine > 100]`.
* The PL/SQL rule [`ExcessiveObjectLength`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#excessiveobjectlength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//(PackageBody|PackageSpecification|ProgramUnit|TriggerUnit|TypeSpecification)[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule [`ExcessivePackageBodyLength`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#excessivepackagebodylength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//PackageBody[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule [`ExcessivePackageSpecificationLength`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#excessivepackagespecificationlength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//PackageSpecification[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rule [`ExcessiveTypeLength`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#excessivetypelength) has been deprecated. Use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#ncsscount)
  instead or create a custom XPath based rule using
  `//TypeSpecification[@EndLine - @BeginLine > 1000]`.
* The PL/SQL rules [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#ncssmethodcount) and [`NcssObjectCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#ncssobjectcount) have been
  deprecated in favor of the new rule [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_plsql_design.html#ncsscount).

### 🐛️ Fixed Issues
* core
    * [#4767](https://github.com/pmd/pmd/issues/4767): \[core] Deprecate old symboltable API
* apex-bestpractices
    * [#6203](https://github.com/pmd/pmd/issues/6203): \[apex] New Rule: Avoid Future Annotation
* apex-design
    * [#2128](https://github.com/pmd/pmd/issues/2128): \[apex] Merge NCSS count rules for Apex
* java-bestpractices
    * [#6188](https://github.com/pmd/pmd/issues/6188): \[java] UnitTestShouldIncludeAssert false positive when TestNG @<!-- -->Test.expectedException present
    * [#6193](https://github.com/pmd/pmd/issues/6193): \[java] New Rule: Always compare enum values with ==
* java-codestyle
    * [#6053](https://github.com/pmd/pmd/issues/6053): \[java] ModifierOrder false-positives with type annotations and type parameters (typeAnnotations = anywhere)
* java-errorprone
    * [#6072](https://github.com/pmd/pmd/issues/6072): \[java] OverrideBothEqualsAndHashCodeOnComparable should not be required for record classes
    * [#6092](https://github.com/pmd/pmd/issues/6092): \[java] AssignmentInOperand false positive in 7.17.0 for case blocks in switch statements
    * [#6096](https://github.com/pmd/pmd/issues/6096): \[java] OverrideBothEqualsAndHashCodeOnComparable on class with lombok.EqualsAndHashCode annotation
    * [#6199](https://github.com/pmd/pmd/issues/6199): \[java] AssignmentInOperand: description of property allowIncrementDecrement is unclear
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
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/symboltable/package-summary.html#"><code>net.sourceforge.pmd.lang.symboltable</code></a>: All classes in this package are deprecated.
      The symbol table and type resolution implementation for Java has been rewritten from scratch
      for PMD 7.0.0. This package is the remains of the old symbol table API, that is only used by
      PL/SQL. For PMD 8.0.0 all these classes will be removed from pmd-core.
* apex
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/apex/rule/design/ExcessiveClassLengthRule.html#"><code>ExcessiveClassLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/apex/rule/design/NcssConstructorCountRule.html#"><code>NcssConstructorCountRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/apex/rule/design/NcssMethodCountRule.html#"><code>NcssMethodCountRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/apex/rule/design/NcssTypeCountRule.html#"><code>NcssTypeCountRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/apex/ast/ASTStatement.html#"><code>ASTStatement</code></a>: This AST node is not used and doesn't appear in the tree.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/apex/ast/ApexVisitor.html#visit(net.sourceforge.pmd.lang.apex.ast.ASTStatement,P)"><code>ApexVisitor#visit(ASTStatement, P)</code></a>
* plsql
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/plsql/rule/design/ExcessiveMethodLengthRule.html#"><code>ExcessiveMethodLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/plsql/rule/design/ExcessiveObjectLengthRule.html#"><code>ExcessiveObjectLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/plsql/rule/design/ExcessivePackageBodyLengthRule.html#"><code>ExcessivePackageBodyLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/plsql/rule/design/ExcessivePackageSpecificationLengthRule.html#"><code>ExcessivePackageSpecificationLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/plsql/rule/design/ExcessiveTypeLengthRule.html#"><code>ExcessiveTypeLengthRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/plsql/rule/design/NcssMethodCountRule.html#"><code>NcssMethodCountRule</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.19.0-SNAPSHOT/net/sourceforge/pmd/lang/plsql/rule/design/NcssObjectCountRule.html#"><code>NcssObjectCountRule</code></a>

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

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



