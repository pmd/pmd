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

#### Call For Logo

We are still searching for a new logo for PMD for the next major release.

Learn more about how to participate on [github issue 1663](https://github.com/pmd/pmd/issues/1663).

#### Java 12 Support

This release of PMD brings support for Java 12. PMD can parse the new [Switch Expressions](http://openjdk.java.net/jeps/325)
and resolve the type of such an expression.

Note: The Switch Expressions are a preview language feature of OpenJDK 12 and are not enabled by default. In order to
analyze a project with PMD that uses these language features, you'll need to enable it via the new environment
variable `PMD_JAVA_OPTS`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd ...

#### Quickstart Ruleset for Apex

PMD provides now a quickstart ruleset for Salesforce.com Apex, which you can use as a base ruleset to
get your custom ruleset started. You can reference it with `rulesets/apex/quickstart.xml`.
You are strongly encouraged to [create your own ruleset](https://pmd.github.io/pmd-6.12.0/pmd_userdocs_making_rulesets.html)
though.

The quickstart ruleset has the intention, to be useful out-of-the-box for many projects. Therefore it
references only rules, that are most likely to apply everywhere.

Any feedback would be greatly appreciated.

#### PMD Designer

The rule designer's codebase has been moved out of the main repository and
will be developed at [pmd/pmd-designer](https://github.com/pmd/pmd-designer)
from now on. The maven coordinates will stay the same for the time being.
The designer will still be shipped with PMD's binaries.

#### Improved Apex Support

*   Many AST nodes now expose more information which makes it easier to write XPath-based rules for Apex. Here are
    some examples:
    *   `Annotation[@Resolved = false()]` finds unsupported annotations.
    *   `AnnotationParameter[@Name='RestResource'][@Value='/myurl']` gives access to
        annotation parameters.
    *   `CatchBlockStatement[@ExceptionType='Exception'][@VariableName='e']` finds catch
        block for specific exception types.
    *   `Field[@Type='String']` find all String fields, `Field[string-length(@Name) < 5]`
        finds all fields with short names and `Field[@Value='a']` find alls fields, that are
        initialized with a specific value.
    *   `LiteralExpression[@String = true()]` finds all String literals. There are attributes
        for each type: `@Boolean`, `@Integer`, `@Double`, `@Long`, `@Decimal`, `@Null`.
    *   `Method[@Constructor = true()]` selects all constructors. `Method[@ReturnType = 'String']`
        selects all methods that return a String.
    *   The `ModifierNode` node has a couple of attributes to check for the existence of specific
        modifiers: `@Test`, `@TestOrTestSetup`, `@WithSharing`, `@WithoutSharing`, `@InheritedSharing`,
        `@WebService`, `@Global`, `@Override`.
    *   Many nodes now expose their type. E.g. with `Parameter[@Type='Integer']` you can find all
        method parameters of type Integer. The same attribute `Type` exists as well for:
        `NewObjectExpression`, `Property`, `VariableDeclaration`.
    *   `VariableExpression[@Image='i']` finds all variable usages of the variable "i".

#### New Rules

*   The new Java rule {% rule "java/design/AvoidUncheckedExceptionsInSignatures" %} (`java-design`) finds methods or constructors
    that declare unchecked exceptions in their `throws` clause. This forces the caller to handle the exception,
    even though it is a runtime exception.

*   The new Java rule {% rule "java/errorprone/DetachedTestCase" %} (`java-errorprone`) searches for public
    methods in test classes, which are not annotated with `@Test`. These methods might be test cases where
    the annotation has been forgotten. Because of that those test cases are never executed.

*   The new Java rule {% rule "java/bestpractices/WhileLoopWithLiteralBoolean" %} (`java-bestpractices`) finds
    Do-While-Loops and While-Loops that can be simplified since they use simply `true` or `false` as their
    loop condition.

*   The new Apex rule {% rule "apex/bestpractices/ApexAssertionsShouldIncludeMessage" %} (`apex-bestpractices`)
    searches for assertions in unit tests and checks, whether they use a message argument.

*   The new Apex rule {% rule "apex/bestpractices/ApexUnitTestMethodShouldHaveIsTestAnnotation" %} (`apex-bestpractices`)
    searches for methods in test classes, which are missing the `@IsTest` annotation.

*   The new PLSQL rule {% rule "plsql/codestyle/AvoidTabCharacter" %} (`plsql-codestyle`) checks, that there are
    no tab characters ("\t") in the source file.

*   The new PLSQL rule {% rule "plsql/codestyle/LineLength" %} (`plsql-codestyle`) helps to enforce a maximum
    line length.

### Modified Rules

*   The Java rule {% rule "java/errorprone/AssignmentToNonFinalStatic" %} (`java-errorprone`) will now report on each
    assignment made within a constructor rather than on the field declaration. This makes it easier for developers to
    find the offending statements.

### Fixed Issues

*   doc
    *   [#1721](https://github.com/pmd/pmd/issues/1721): \[doc] Documentation provides an invalid property configuration example
*   java
    *   [#1537](https://github.com/pmd/pmd/issues/1537): \[java] Java 12 support
*   java-bestpractices
    *   [#1701](https://github.com/pmd/pmd/issues/1701): \[java] UseTryWithResources does not handle multiple argument close methods
*   java-codestyle
    *   [#1527](https://github.com/pmd/pmd/issues/1527): \[java] UseUnderscoresInNumericLiterals false positive on floating point numbers
    *   [#1674](https://github.com/pmd/pmd/issues/1674): \[java] documentation of CommentDefaultAccessModifier is wrong
*   java-errorprone
    *   [#1570](https://github.com/pmd/pmd/issues/1570): \[java] AvoidDuplicateLiterals warning about deprecated separator property when not used
*   plsql
    *   [#1510](https://github.com/pmd/pmd/issues/1510): \[plsql] Support XMLTABLE functions
    *   [#1716](https://github.com/pmd/pmd/issues/1716): \[plsql] Support access to whole plsql code
    *   [#1731](https://github.com/pmd/pmd/issues/1731): \[pslql] ParseException when parsing ELSIF
    *   [#1733](https://github.com/pmd/pmd/issues/1733): \[plsql] % not supported in "TestSearch%notfound"
    *   [#1734](https://github.com/pmd/pmd/issues/1734): \[plsql] TooManyMethods false-negative
    *   [#1735](https://github.com/pmd/pmd/issues/1735): \[plsql] False-negatives for TO_DATE_TO_CHAR, TO_DATEWithoutDateFormat, TO_TIMESTAMPWithoutDateFormat

### API Changes

#### Command Line Interface

The start scripts `run.sh`, `pmd.bat` and `cpd.bat` support the new environment variable `PMD_JAVA_OPTS`.
This can be used to set arbitrary JVM options for running PMD, such as memory settings (e.g. `PMD_JAVA_OPTS=-Xmx512m`)
or enable preview language features (e.g. `PMD_JAVA_OPTS=--enable-preview`).

The previously available variables such as `OPTS` or `HEAPSIZE` are deprecated and will be removed with PMD 7.0.0.

#### Deprecated API

*   {% jdoc core::renderers.CodeClimateRule %} is deprecated in 7.0.0 because it was unused for 2 years and
    created an unwanted dependency.
    Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" will also be removed.
    See [#1702](https://github.com/pmd/pmd/pull/1702) for more.

*   The Apex ruleset `rulesets/apex/ruleset.xml` has been deprecated and will be removed in 7.0.0. Please use the new
    quickstart ruleset `rulesets/apex/quickstart.xml` instead.

### External Contributions

*   [#1694](https://github.com/pmd/pmd/pull/1694): \[apex] New rules for test method and assert statements - [triandicAnt](https://github.com/triandicAnt)
*   [#1697](https://github.com/pmd/pmd/pull/1697): \[doc] Update CPD documentation - [Matías Fraga](https://github.com/matifraga)
*   [#1704](https://github.com/pmd/pmd/pull/1704): \[java] Added AvoidUncheckedExceptionsInSignatures Rule - [Bhanu Prakash Pamidi](https://github.com/pamidi99)
*   [#1706](https://github.com/pmd/pmd/pull/1706): \[java] Add DetachedTestCase rule - [David Burström](https://github.com/davidburstromspotify)
*   [#1709](https://github.com/pmd/pmd/pull/1709): \[java] Detect while loops with literal booleans conditions - [David Burström](https://github.com/davidburstromspotify)
*   [#1717](https://github.com/pmd/pmd/pull/1717): \[java] Fix false positive in useTryWithResources when using a custom close method with multiple arguments - [Rishabh Jain](https://github.com/jainrish)
*   [#1724](https://github.com/pmd/pmd/pull/1724): \[doc] Correct property override example - [Felix W. Dekker](https://github.com/FWDekker)
*   [#1737](https://github.com/pmd/pmd/pull/1737): \[java] fix escaping of CommentDefaultAccessModifier documentation - [itaigilo](https://github.com/itaigilo)
*   [#1781](https://github.com/pmd/pmd/pull/1781): \[java] Location change in AssignmentToNonFinalStatic - [Maikel Steneker](https://github.com/maikelsteneker)

{% endtocmaker %}

