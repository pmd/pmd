---
title: Detailed Release Notes for PMD 7
summary: "These are the detailed release notes for PMD 7."
permalink: pmd_release_notes_pmd7.html
keywords: changelog, release notes
---

## 🚀 Major Features and Enhancements

### New official logo

Many of you probably have already seen the new logo, but now it's time to actually ship it. The new logo
was long ago decided (see [#1663](https://github.com/pmd/pmd/issues/1663)).

We decided it's time to have a modernized logo and get rid of the gun. This allows to include
the logo anywhere without offense.

The official logo is also without a tagline (such as "Code Quality Matters!") as the tagline created some
controversies. Without a tagline, we are not limited in the direction of future development of PMD.

![New PMD Logo](images/logo/pmd-logo-300px.png)

The new logo is available from the [Logo Project Page](pmd_projectdocs_logo.html).

### Revamped Java

The Java grammar has been refactored substantially in order to make it easier to maintain and more correct
regarding the Java Language Specification. It supports now also the edge-cases where PMD 6 was failing
(e.g. annotations were not supported everywhere). Changing the grammar entails a changed AST and therefore changed
rules. The PMD built-in rules have all been upgraded and many bugs have been fixed on the way.
Unfortunately, if you are using custom rules, you will most probably need to accommodate these changes yourself.

The type resolution framework has been rewritten from scratch and should now cover the entire Java spec correctly.
The same is true for the symbol table.
PMD 6 on the other hand has always had problems with advanced type inference, e.g. with lambdas and call chains.
Since it was built on the core reflection API, it also was prone to linkage errors and classloader leaks for instance.
PMD 7 does not need to load classes, and does not have these problems.

The AST exposes much more semantic information now. For instance, you can jump from a method call to
the declaration of the method being called, or from a field access to the field declaration. These
improvements allow interesting rules to be written that need precise knowledge of the types
in the program, for instance to detect {% rule java/codestyle/UnnecessaryBoxing %}
or {% rule java/codestyle/UseDiamondOperator %}.
These are just a small preview of the new rules we will be adding in the PMD 7 release cycle.

Overall, the changes to the parser, AST, type resolution and symbol table code has made PMD for
Java **significantly faster**. On average, we have seen ~2-3X faster analysis, but as usual, this may change
depending on your workload, configuration and ruleset.

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

{% include note.html content="
The full detailed documentation of the changes to the Java AST are available in the
[Migration Guide for PMD 7](pmd_userdocs_migrating_to_pmd7.html#java-ast)
" %}

### Revamped Command Line Interface

PMD now ships with a unified Command Line Interface for both Linux/Unix and Windows. Instead of having a collection
of scripts for the different utilities shipped with PMD, a single script `pmd` (`pmd.bat` for Windows) can now
launch all utilities using subcommands, e.g. `pmd check`, `pmd designer`. All commands and options are thoroughly
documented in the help, with full color support where available. Moreover, efforts were made to provide consistency
in the usage of all PMD utilities.

```shell
$ Usage: pmd [-hV] [COMMAND]
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  check     The PMD standard source code analyzer
  cpd       Copy/Paste Detector - find duplicate code
  designer  The PMD visual rule designer
  cpd-gui   GUI for the Copy/Paste Detector
              Warning: May not support the full CPD feature set
  ast-dump  Experimental: dumps the AST of parsing source code
Exit Codes:
  0   Successful analysis, no violations found
  1   An unexpected error occurred during execution
  2   Usage error, please refer to the command help
  4   Successful analysis, at least 1 violation found
```

For instance, where you previously would have run
```shell
run.sh pmd -d src -R ruleset.xml
```
you should now use
```shell
pmd check -d src -R ruleset.xml
```
or even better, omit using `-d` / `--dir` and simply pass the sources at the end of the parameter list

```shell
pmd check -R ruleset.xml src
```

Multiple source directories can be passed, such as:
```shell
pmd check -R ruleset.xml src/main/java src/test/java
```

And the exact same applies to CPD:
```shell
pmd cpd --minimum-tokens 100 src/main/java
```

Additionally, the CLI for the `check` command has been enhanced with a progress bar, which interactively displays the
current progress of the analysis.

![Demo](images/userdocs/pmd-demo.gif)

This can be disabled with the `--no-progress` flag.

Finally, we now provide a completion script for Bash/Zsh to further help daily usage.
To use it, edit your `~/.bashrc` / `~/.zshrc` file and add the following line:

```
source <(pmd generate-completion)
```

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

### Full Antlr support

PMD 6 only supported JavaCC based grammars, but with [Antlr](https://www.antlr.org/) parsers
can be generated as well. Languages backed by an Antlr grammar are now fully supported. This means, it's now
possible not only to use Antlr grammars for CPD, but we can actually build full-fledged PMD rules for them as well.
Both the traditional Java visitor rules, and the simpler XPath rules are available to users. This allows
to leverage existing grammars.

We expect this to enable both our dev team and external contributors to largely extend PMD usage for more languages.

Two languages (Swift and Kotlin) already use this new possibility.

See the documentation page [Adding a new language with ANTLR](pmd_devdocs_major_adding_new_language_antlr.html)
for instructions on how to use this new feature.

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

### Updated PMD Designer

This PMD release ships a new version of the pmd-designer. The designer artifact has been
renamed from "pmd-ui" to "pmd-designer". While the designer still works with Java 8, the
recommended Java Runtime is Java 11 (or later) with OpenJFX 17 (or later).

For the detailed changes, see
* [PMD Designer Changelog (7.0.0)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0).
* [PMD Designer Changelog (7.0.0-rc4)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc4).
* [PMD Designer Changelog (7.0.0-rc1)](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc1).

### New CPD report format cpdhtml-v2.xslt

Thanks to @mohan-chinnappan-n a new CPD report format has been added which features a data table.
It uses an XSLT stylesheet to convert CPD's XML format into HTML.

See [the example report](report-examples/cpdhtml-v2.html).

Contributors: [Mohan Chinnappan](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)

## 🎉 Language Related Changes

### New: CPD support for Apache Velocity Template Language (VTL)

PMD supported Apache Velocity for a very long time, but the CPD integration never got finished.
This is now done and CPD supports Apache Velocity Template language for detecting copy and paste.
It is shipped in the module `pmd-velocity`.

### New: CPD support for Coco

Thanks to a contribution, CPD now supports Coco, a modern programming language
designed specifically for building event-driven software. It is shipped in the new
module `pmd-coco`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

### New: CPD support for Julia

Thanks to a contribution, CPD now supports the Julia language. It is shipped
in the new module `pmd-julia`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

### New: CPD support for TypeScript

Thanks to a contribution, CPD now supports the TypeScript language. It is shipped
with the rest of the JavaScript support in the module `pmd-javascript`.

Contributors: [Paul Guyot](https://github.com/pguyot) (@pguyot)

### New: Java 21 and 22 Support

This release of PMD brings support for Java 21 and 22. There are the following new standard language features,
that are supported now:

* [JEP 456: Unnamed Variables & Patterns](https://openjdk.org/jeps/456) (Java 22)
* [JEP 440: Record Patterns](https://openjdk.org/jeps/440) (Java 21)
* [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441) (Java 21)

PMD also supports the following preview language features:

* [JEP 447: Statements before super(...) (Preview)](https://openjdk.org/jeps/447) (Java 22)
* [JEP 459: String Templates (Second Preview)](https://openjdk.org/jeps/459) (Java 21 and 22)
* [JEP 463: Implicitly Declared Classes and Instance Main Methods (Second Preview)](https://openjdk.org/jeps/463) (Java 21 and 22)

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `22-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-22-preview ...

Note: Support for Java 19 and Java 20 preview language features have been removed. The versions "19-preview" and
"20-preview" are no longer available.

### New: Kotlin support

PMD now supports Kotlin as an additional language for analyzing source code. It is based on
the official kotlin Antlr grammar for Kotlin 1.8. Java-based rules and XPath-based rules are supported.

We are shipping the following rules:

* {% rule kotlin/bestpractices/FunctionNameTooShort %} finds functions with a too
  short name.
* {% rule kotlin/errorprone/OverrideBothEqualsAndHashcode %} finds classes with only
  either `equals` or `hashCode` overridden, but not both. This leads to unexpected behavior once instances
  of such classes are used in collections (Lists, HashMaps, ...).

Contributors: [Jeroen Borgers](https://github.com/jborgers) (@jborgers),
[Peter Paul Bakker](https://github.com/stokpop) (@stokpop)

### New: Swift support

Given the full Antlr support, PMD now fully supports Swift for creating rules. Previously only CPD was supported.

Note: There is only limited support for newer Swift language features in the parser, e.g. Swift 5.9 (Macro Expansions)
are supported, but other features are not.

We are pleased to announce we are shipping a number of rules starting with PMD 7.

* {% rule "swift/errorprone/ForceCast" %} flags all force casts, making sure you are
  defensively considering all types. Having the application crash shouldn't be an option.
* {% rule "swift/errorprone/ForceTry" %} flags all force tries, making sure you are
  defensively handling exceptions. Having the application crash shouldn't be an option.
* {% rule "swift/bestpractices/ProhibitedInterfaceBuilder" %} flags any usage of interface
  builder. Interface builder files are prone to merge conflicts, and are impossible to code review, so larger
  teams usually try to avoid it or reduce its usage.
* {% rule "swift/bestpractices/UnavailableFunction" %} flags any function throwing
  a `fatalError` not marked as `@available(*, unavailable)` to ensure no calls are actually performed in
  the codebase.

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

### Changed: Apex Support: Replaced Jorje with fully open source front-end

When PMD added Apex support with version 5.5.0, it utilized the Apex Jorje library to parse Apex source
and generate an AST. This library is however a binary-blob provided as part of the
[Salesforce Extensions for VS Code](https://github.com/forcedotcom/salesforcedx-vscode), and it is closed-source.

This causes problems, if binary blobs are not allowed by e.g. a company-wide policy. In that case, the Jorje
library prevented that PMD Apex could be used at all.

Also having access to the source code, enhancements and modifications are easier to do.

Under the hood, we use two open source libraries instead:

* [apex-parser](https://github.com/apex-dev-tools/apex-parser) originally by
  [Kevin Jones](https://github.com/nawforce) (@nawforce).
  This project provides the grammar for a ANTLR based parser.
* [Summit-AST](https://github.com/google/summit-ast) by [Google](https://github.com/google) (@google)
  This project translates the ANTLR parse tree into an AST, that is similar to the AST Jorje provided.
  Note: This is not an official Google product.

Although the parsers is completely switched, there are only little known changes to the AST.
These are documented in the [Migration Guide for PMD 7: Apex AST](pmd_userdocs_migrating_to_pmd7.html#apex-ast).
With the new Apex parser, the new language constructs like User Mode Database Operations
can be parsed now. PMD should be able to parse Apex code up to version 59.0 (Winter '23).

See [#3766](https://github.com/pmd/pmd/issues/3766) for details.

Contributors: [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google),
[Edward Klimoshenko](https://github.com/eklimo) (@eklimo)

### Changed: CPP can now ignore identifiers in sequences (CPD)

* New command line option for CPD: `--ignore-sequences`.
* This option is used for CPP only: with the already existing option `--ignore-literal-sequences`, only
  literals were ignored. The new option additionally ignores identifiers as well in sequences.
* See [PR #4470](https://github.com/pmd/pmd/pull/4470) for details.

### Changed: Groovy Support (CPD)

* We now support parsing all Groovy features from Groovy 3 and 4.
* We now support [suppression](pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs.
* See [PR #4726](https://github.com/pmd/pmd/pull/4726) for details.

### Changed: HTML support

Support for HTML was introduced in PMD 6.55.0 as an experimental feature. With PMD 7.0.0 this
is now considered stable.

### Changed: JavaScript support

The JS specific parser options have been removed. The parser now always retains comments and uses version ES6.
The language module registers a couple of different versions. The latest version, which supports ES6 and also some
new constructs (see [Rhino](https://github.com/mozilla/rhino)), is the default. This should be fine for most
use cases.

### Changed: Language versions

We revisited the versions that were defined by each language module. Now many more versions are defined for each
language. In general, you can expect that PMD can parse all these different versions. There might be situations
where this fails and this can be considered a bug. Usually the latest version is selected as the default
language version.

The language versions can be used to mark rules to be useful only for a specific language version via
the `minimumLanguageVersion` and `maximumLanguageVersion` attributes. While this feature is currently only used by
the Java module, listing all possible versions enables other languages as well to use this feature.

Related issue: [[core] Explicitly name all language versions (#4120)](https://github.com/pmd/pmd/issues/4120)

### Changed: Rule properties

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  {% jdoc core::properties.PropertyFactory %} to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

### Changed: Velocity Template Language (VTL)

The module was named just "vm" which was not a good name. Its module name, language id and
package names have been renamed to "velocity".

If you import rules, you also need to adjust the paths, e.g.

* `category/vm/...` ➡️ `category/velocity/...`

### Changed: Visualforce

There was an inconsistency between the naming of the maven module and the language id. The language id
used the abbreviation "vf", while the maven module used the longer name "visualforce". This has been
solved by renaming the language module to its full name "visualforce". The java packages have
been renamed as well.

If you import rules, you also need to adjust the paths, e.g.

* `category/vf/security.xml` ➡️ `category/visualforce/security.xml`

## 🌟 New and changed rules

### New Rules

**Apex**
* {% rule apex/performance/OperationWithHighCostInLoop %} finds Schema class methods called in a loop, which is a
  potential performance issue.
* {% rule apex/design/UnusedMethod %} finds unused methods in your code.

**Java**
* {% rule java/codestyle/UnnecessaryBoxing %} reports boxing and unboxing conversions that may be made implicit.
* {% rule java/codestyle/UseExplicitTypes %} reports usages of `var` keyword, which was introduced with Java 10.

**Kotlin**
* {% rule kotlin/bestpractices/FunctionNameTooShort %} finds functions with a too short name.
* {% rule kotlin/errorprone/OverrideBothEqualsAndHashcode %} finds classes with only
  either `equals` or `hashCode` overridden, but not both. This leads to unexpected behavior once instances
  of such classes are used in collections (Lists, HashMaps, ...).

**Swift**
* {% rule swift/errorprone/ForceCast %} flags all force casts, making sure you are
  defensively considering all types. Having the application crash shouldn't be an option.
* {% rule swift/errorprone/ForceTry %} flags all force tries, making sure you are
  defensively handling exceptions. Having the application crash shouldn't be an option.
* {% rule swift/bestpractices/ProhibitedInterfaceBuilder %} flags any usage of interface
  builder. Interface builder files are prone to merge conflicts, and are impossible to code review, so larger
  teams usually try to avoid it or reduce its usage.
* {% rule swift/bestpractices/UnavailableFunction %} flags any function throwing
  a `fatalError` not marked as `@available(*, unavailable)` to ensure no calls are actually performed in
  the codebase.

**XML**
* {% rule xml/bestpractices/MissingEncoding %} finds XML files without explicit encoding.

### Changed Rules

**General changes**

* All statistical rules (like ExcessiveClassLength, ExcessiveParameterList) have been simplified and unified.
  The properties `topscore` and `sigma` have been removed. The property `minimum` is still there, however the type is not
  a decimal number anymore but has been changed to an integer. This affects rules in the languages Apex, Java, PLSQL
  and Velocity Template Language (velocity):
    * Apex: {% rule apex/design/ExcessiveClassLength %}, {% rule apex/design/ExcessiveParameterList %},
      {% rule apex/design/ExcessivePublicCount %}, {% rule apex/design/NcssConstructorCount %},
      {% rule apex/design/NcssMethodCount %}, {% rule apex/design/NcssTypeCount %}
    * Java: {% rule java/design/ExcessiveImports %}, {% rule java/design/ExcessiveParameterList %},
      {% rule java/design/ExcessivePublicCount %}, {% rule java/design/SwitchDensity %}
    * PLSQL: {% rule plsql/design/ExcessiveMethodLength %}, {% rule plsql/design/ExcessiveObjectLength %},
      {% rule plsql/design/ExcessivePackageBodyLength %}, {% rule plsql/design/ExcessivePackageSpecificationLength %},
      {% rule plsql/design/ExcessiveParameterList %}, {% rule plsql/design/ExcessiveTypeLength %},
      {% rule plsql/design/NcssMethodCount %}, {% rule plsql/design/NcssObjectCount %},
      {% rule plsql/design/NPathComplexity %}
    * Velocity: {% rule velocity/design/ExcessiveTemplateLength %}

* The general property `violationSuppressXPath` which is available for all rules to
  [suppress warnings](pmd_userdocs_suppressing_warnings.html) now uses XPath version 3.1 by default.
  This version of the XPath language is mostly identical to XPath 2.0. In PMD 6, XPath 1.0 has been used.
  If you upgrade from PMD 6, you need to verify your `violationSuppressXPath` properties.

**Apex General changes**

* The properties `cc_categories`, `cc_remediation_points_multiplier`, `cc_block_highlighting` have been removed
  from all rules. These properties have been deprecated since PMD 6.13.0.
  See [issue #1648](https://github.com/pmd/pmd/issues/1648) for more details.

**Apex Codestyle**

* {% rule apex/codestyle/MethodNamingConventions %}: The deprecated rule property `skipTestMethodUnderscores` has
  been removed. It was actually deprecated since PMD 6.15.0, but was not mentioned in the release notes
  back then. Use the property `testPattern` instead to configure valid names for test methods.

**Java General changes**

* Violations reported on methods or classes previously reported the line range of the entire method
  or class. With PMD 7.0.0, the reported location is now just the identifier of the method or class.
  This affects various rules, e.g. {% rule java/design/CognitiveComplexity %}.

  The report location is controlled by the overrides of the method {% jdoc !!core::lang.ast.Node#getReportLocation() %}
  in different node types.

  See [issue #4439](https://github.com/pmd/pmd/issues/4439) and [issue #730](https://github.com/pmd/pmd/issues/730)
  for more details.

**Java Best Practices**

* {% rule java/bestpractices/ArrayIsStoredDirectly %}: Violations are now reported on the assignment and not
  anymore on the formal parameter. The reported line numbers will probably move.
* {% rule java/bestpractices/AvoidReassigningLoopVariables %}: This rule might not report anymore all
  reassignments of the control variable in for-loops when the property `forReassign` is set to `skip`.
  See [issue #4500](https://github.com/pmd/pmd/issues/4500) for more details.
* {% rule java/bestpractices/LooseCoupling %}: The rule has a new property to allow some types to be coupled
  to (`allowedTypes`).
* {% rule java/bestpractices/UnusedLocalVariable %}: This rule has some important false-negatives fixed
  and finds many more cases now. For details see issues [#2130](https://github.com/pmd/pmd/issues/2130),
  [#4516](https://github.com/pmd/pmd/issues/4516), and [#4517](https://github.com/pmd/pmd/issues/4517).

**Java Codestyle**

* {% rule java/codestyle/MethodNamingConventions %}: The property `checkNativeMethods` has been removed. The
  property was deprecated since PMD 6.3.0. Use the property `nativePattern` to control whether native methods
  should be considered or not.
* {% rule java/codestyle/ShortVariable %}: This rule now also reports short enum constant names.
* {% rule java/codestyle/UseDiamondOperator %}: The property `java7Compatibility` has been removed. The rule now
  handles Java 7 properly without a property.
* {% rule java/codestyle/UnnecessaryFullyQualifiedName %}: The rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved
  to be more precise.
* {% rule java/codestyle/UselessParentheses %}: The rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.
* {% rule java/codestyle/EmptyControlStatement %}: The rule has a new property to allow empty blocks when
  they contain a comment (`allowCommentedBlocks`).

**Java Design**

* {% rule java/design/CyclomaticComplexity %}: The property `reportLevel` has been removed. The property was
  deprecated since PMD 6.0.0. The report level can now be configured separated for classes and methods using
  `classReportLevel` and `methodReportLevel` instead.
* {% rule java/design/ImmutableField %}: The property `ignoredAnnotations` has been removed. The property was
  deprecated since PMD 6.52.0.
* {% rule java/design/LawOfDemeter %}: The rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.
* {% rule java/design/NPathComplexity %}: The property `minimum` has been removed. It was deprecated since PMD 6.0.0.
  Use the property `reportLevel` instead.
* {% rule java/design/SingularField %}: The properties `checkInnerClasses` and `disallowNotAssignment` have been removed.
  The rule is now more precise and will check these cases properly.
* {% rule java/design/UseUtilityClass %}: The property `ignoredAnnotations` has been removed.

**Java Documentation**

* {% rule java/documentation/CommentContent %}: The properties `caseSensitive` and `disallowedTerms` are removed. The
  new property `forbiddenRegex` can be used now to define the disallowed terms with a single regular
  expression.
* {% rule java/documentation/CommentRequired %}:
  * Overridden methods are now detected even without the `@Override`
    annotation. This is relevant for the property `methodWithOverrideCommentRequirement`.
    See also [pull request #3757](https://github.com/pmd/pmd/pull/3757).
  * Elements in annotation types are now detected as well. This might lead to an increased number of violations
    for missing public method comments.
  * The deprecated property `headerCommentRequirement` has been removed. Use the property `classCommentRequirement`
    instead.
* {% rule java/documentation/CommentSize %}: When determining the line-length of a comment, the leading comment
  prefix markers (e.g. `*` or `//`) are ignored and don't add up to the line-length.
  See also [pull request #4369](https://github.com/pmd/pmd/pull/4369).

**Java Error Prone**

* {% rule java/errorprone/AvoidDuplicateLiterals %}: The property `exceptionfile` has been removed. The property was
  deprecated since PMD 6.10.0. Use the property `exceptionList` instead.
* {% rule java/errorprone/DontImportSun %}: `sun.misc.Signal` is not special-cased anymore.
* {% rule java/errorprone/EmptyCatchBlock %}: `CloneNotSupportedException` and `InterruptedException` are not
  special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* {% rule java/errorprone/ImplicitSwitchFallThrough %}: Violations are now reported on the case statements
  rather than on the switch statements. This is more accurate but might result in more violations now.
* {% rule java/errorprone/NonSerializableClass %}: The deprecated property `prefix` has been removed
  without replacement. In a serializable class all fields have to be serializable regardless of the name.

### Deprecated Rules

In PMD 7.0.0, there are no deprecated rules.

### Removed Rules

The following previously deprecated rules have been finally removed:

**Apex**

* {% deleted_rule apex/performance/AvoidSoqlInLoops %} ➡️ use {% rule apex/performance/OperationWithLimitsInLoop %}
* {% deleted_rule apex/performance/AvoidSoslInLoops %} ➡️ use {% rule apex/performance/OperationWithLimitsInLoop %}
* {% deleted_rule apex/performance/AvoidDmlStatementsInLoops %} ➡️ use {% rule apex/performance/OperationWithLimitsInLoop %}
* {% deleted_rule apex/codestyle/VariableNamingConventions %} ➡️ use {% rule apex/codestyle/FieldNamingConventions %},
  {% rule apex/codestyle/FormalParameterNamingConventions %}, {% rule apex/codestyle/LocalVariableNamingConventions %},
  or {% rule apex/codestyle/PropertyNamingConventions %}
* {% deleted_rule apex/security/ApexCSRF %} ➡️ use {% rule apex/errorprone/ApexCSRF %}

**Java**

* {% deleted_rule java/codestyle/AbstractNaming %} ➡️ use {% rule java/codestyle/ClassNamingConventions %}
* {% deleted_rule java/codestyle/AvoidFinalLocalVariable %} ➡️ not replaced
* {% deleted_rule java/codestyle/AvoidPrefixingMethodParameters %} ➡️ use {% rule "java/codestyle/FormalParameterNamingConventions" %}
* {% deleted_rule java/performance/AvoidUsingShortType %} ➡️ not replaced
* {% deleted_rule java/errorprone/BadComparison %} ➡️ use {% rule "java/errorprone/ComparisonWithNaN" %}
* {% deleted_rule java/errorprone/BeanMembersShouldSerialize %} ➡️ use {% rule java/errorprone/NonSerializableClass %}
* {% deleted_rule java/performance/BooleanInstantiation %} ➡️ use {% rule "java/codestyle/UnnecessaryBoxing" %}
  and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
* {% deleted_rule java/performance/ByteInstantiation %} ➡️ use {% rule "java/codestyle/UnnecessaryBoxing" %}
  and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
* {% deleted_rule java/errorprone/CloneThrowsCloneNotSupportedException %} ➡️ not replaced
* {% deleted_rule java/errorprone/DataflowAnomalyAnalysis %} ➡️ use {% rule java/bestpractices/UnusedAssignment %}
* {% deleted_rule java/codestyle/DefaultPackage %} ➡️ use {% rule "java/codestyle/CommentDefaultAccessModifier" %}
* {% deleted_rule java/errorprone/DoNotCallSystemExit %} ➡️ use {% rule "java/errorprone/DoNotTerminateVM" %}
* {% deleted_rule java/codestyle/DontImportJavaLang %} ➡️ use {% rule java/codestyle/UnnecessaryImport %}
* {% deleted_rule java/codestyle/DuplicateImports %} ➡️ use {% rule java/codestyle/UnnecessaryImport %}
* {% deleted_rule java/errorprone/EmptyFinallyBlock %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
* {% deleted_rule java/errorprone/EmptyIfStmt %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
* {% deleted_rule java/errorprone/EmptyInitializer %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
* {% deleted_rule java/errorprone/EmptyStatementBlock %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
* {% deleted_rule java/errorprone/EmptyStatementNotInLoop %} ➡️ use {% rule java/codestyle/UnnecessarySemicolon %}
* {% deleted_rule java/errorprone/EmptySwitchStatements %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
* {% deleted_rule java/errorprone/EmptySynchronizedBlock %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
* {% deleted_rule java/errorprone/EmptyTryBlock %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
* {% deleted_rule java/errorprone/EmptyWhileStmt %} ➡️ use {% rule java/codestyle/EmptyControlStatement %}
* {% deleted_rule java/design/ExcessiveClassLength %} ➡️ use {% rule java/design/NcssCount %}
* {% deleted_rule java/design/ExcessiveMethodLength %} ➡️ use {% rule java/design/NcssCount %}
* {% deleted_rule java/codestyle/ForLoopsMustUseBraces %} ➡️ use {% rule java/codestyle/ControlStatementBraces %}
* {% deleted_rule java/codestyle/IfElseStmtsMustUseBraces %} ➡️ use {% rule java/codestyle/ControlStatementBraces %}
* {% deleted_rule java/codestyle/IfStmtsMustUseBraces %} ➡️ use {% rule java/codestyle/ControlStatementBraces %}
* {% deleted_rule java/errorprone/ImportFromSamePackage %} ➡️ use {% rule java/codestyle/UnnecessaryImport %}
* {% deleted_rule java/performance/IntegerInstantiation %} ➡️ use {% rule java/codestyle/UnnecessaryBoxing %}
  and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
* {% deleted_rule java/errorprone/InvalidSlf4jMessageFormat %} ➡️  use {% rule "java/errorprone/InvalidLogMessageFormat" %}
* {% deleted_rule java/errorprone/LoggerIsNotStaticFinal %} ➡️ use {% rule java/errorprone/ProperLogger %}
* {% deleted_rule java/performance/LongInstantiation %} ➡️ use {% rule "java/codestyle/UnnecessaryBoxing" %}
  and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
* {% deleted_rule java/codestyle/MIsLeadingVariableName %} ➡️ use {% rule java/codestyle/FieldNamingConventions %},
  {% rule java/codestyle/FormalParameterNamingConventions %},
  or {% rule java/codestyle/LocalVariableNamingConventions %}
* {% deleted_rule java/errorprone/MissingBreakInSwitch %} ➡️  use {% rule "java/errorprone/ImplicitSwitchFallThrough" %}
* {% deleted_rule java/design/ModifiedCyclomaticComplexity %} ➡️ use {% rule "java/design/CyclomaticComplexity" %}
* {% deleted_rule java/design/NcssConstructorCount %} ➡️ use {% rule java/design/NcssCount %}
* {% deleted_rule java/design/NcssMethodCount %} ➡️ use {% rule java/design/NcssCount %}
* {% deleted_rule java/design/NcssTypeCount %} ➡️ use {% rule java/design/NcssCount %}
* {% deleted_rule java/bestpractices/PositionLiteralsFirstInCaseInsensitiveComparisons %} ➡️
  use {% rule "java/bestpractices/LiteralsFirstInComparisons" %}
* {% deleted_rule java/bestpractices/PositionLiteralsFirstInComparisons %} ➡️
  use {% rule "java/bestpractices/LiteralsFirstInComparisons" %}
* {% deleted_rule java/errorprone/ReturnEmptyArrayRatherThanNull %} ➡️
  use {% rule "java/errorprone/ReturnEmptyCollectionRatherThanNull" %}
* {% deleted_rule java/performance/ShortInstantiation %} ➡️ use {% rule "java/codestyle/UnnecessaryBoxing" %}
  and {% rule "java/bestpractices/PrimitiveWrapperInstantiation" %}
* {% deleted_rule java/design/SimplifyBooleanAssertion %} ➡️ use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
* {% deleted_rule java/performance/SimplifyStartsWith %} ➡️ not replaced
* {% deleted_rule java/design/StdCyclomaticComplexity %} ➡️ use {% rule "java/design/CyclomaticComplexity" %}
* {% deleted_rule java/codestyle/SuspiciousConstantFieldName %} ➡️ use {% rule java/codestyle/FieldNamingConventions %}
* {% deleted_rule java/performance/UnnecessaryWrapperObjectCreation %} ➡️ use the new rule {% rule "java/codestyle/UnnecessaryBoxing" %}
* {% deleted_rule java/multithreading/UnsynchronizedStaticDateFormatter %} ➡️ use {% rule java/multithreading/UnsynchronizedStaticFormatter %}
* {% deleted_rule java/bestpractices/UnusedImports %} ➡️ use {% rule java/codestyle/UnnecessaryImport %}
* {% deleted_rule java/bestpractices/UseAssertEqualsInsteadOfAssertTrue %} ➡️ use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
* {% deleted_rule java/bestpractices/UseAssertNullInsteadOfAssertEquals %} ➡️ use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
* {% deleted_rule java/bestpractices/UseAssertSameInsteadOfAssertEquals %} ➡️ use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
* {% deleted_rule java/bestpractices/UseAssertTrueInsteadOfAssertEquals %} ➡️ use {% rule "java/bestpractices/SimplifiableTestAssertion" %}
* {% deleted_rule java/codestyle/VariableNamingConventions %} ➡️ use {% rule java/codestyle/FieldNamingConventions %},
  {% rule java/codestyle/FormalParameterNamingConventions %}, or {% rule java/codestyle/LocalVariableNamingConventions %}
* {% deleted_rule java/codestyle/WhileLoopsMustUseBraces %} ➡️ use {% rule "java/codestyle/ControlStatementBraces" %}

### Removed rulesets

The following previously deprecated rulesets have been removed. These were the left-over rulesets from PMD 5.
The rules have been moved into categories with PMD 6.

* rulesets/apex/apexunit.xml
* rulesets/apex/braces.xml
* rulesets/apex/complexity.xml
* rulesets/apex/empty.xml
* rulesets/apex/metrics.xml
* rulesets/apex/performance.xml
* rulesets/apex/ruleset.xml
* rulesets/apex/securty.xml
* rulesets/apex/style.xml
* rulesets/java/android.xml
* rulesets/java/basic.xml
* rulesets/java/clone.xml
* rulesets/java/codesize.xml
* rulesets/java/comments.xml
* rulesets/java/controversial.xml
* rulesets/java/coupling.xml
* rulesets/java/design.xml
* rulesets/java/empty.xml
* rulesets/java/finalizers.xml
* rulesets/java/imports.xml
* rulesets/java/j2ee.xml
* rulesets/java/javabeans.xml
* rulesets/java/junit.xml
* rulesets/java/logging-jakarta-commons.xml
* rulesets/java/logging-java.xml
* rulesets/java/metrics.xml
* rulesets/java/migrating.xml
* rulesets/java/migrating_to_13.xml
* rulesets/java/migrating_to_14.xml
* rulesets/java/migrating_to_15.xml
* rulesets/java/migrating_to_junit4.xml
* rulesets/java/naming.xml
* rulesets/java/optimizations.xml
* rulesets/java/strictexception.xml
* rulesets/java/strings.xml
* rulesets/java/sunsecure.xml
* rulesets/java/typeresolution.xml
* rulesets/java/unnecessary.xml
* rulesets/java/unusedcode.xml
* rulesets/ecmascript/basic.xml
* rulesets/ecmascript/braces.xml
* rulesets/ecmascript/controversial.xml
* rulesets/ecmascript/unnecessary.xml
* rulesets/jsp/basic.xml
* rulesets/jsp/basic-jsf.xml
* rulesets/plsql/codesize.xml
* rulesets/plsql/dates.xml
* rulesets/plsql/strictsyntax.xml
* rulesets/plsql/TomKytesDespair.xml
* rulesets/vf/security.xml
* rulesets/vm/basic.xml
* rulesets/pom/basic.xml
* rulesets/xml/basic.xml
* rulesets/xsl/xpath.xml
* rulesets/releases/*

## 💥 Compatibility and Migration Notes

{% include note.html content="
The full detailed documentation of the changes are available in the
[Migration Guide for PMD 7](pmd_userdocs_migrating_to_pmd7.html)
" %}

### For endusers

* PMD 7 requires Java 8 or above to execute.
* CLI changed: Custom scripts need to be updated (`run.sh pmd ...` ➡️ `pmd check ...`, `run.sh cpd ...` ➡️ `pmd cpd ...`).
* Java module revamped: Custom rules need to be updated.
* Removed rules: Custom rulesets need to be reviewed. See above for a list of new and removed rules.
* XPath 1.0 and 2.0 support is removed, `violationSuppressXPath` now requires XPath 3.1: Custom rulesets need
  to be reviewed.
* Custom rules using rulechains: Need to override {% jdoc !!core::lang.rule.AbstractRule#buildTargetSelector() %}
  using {% jdoc !!core::lang.rule.RuleTargetSelector#forTypes(java.lang.Class,java.lang.Class...) %}.
* The asset filenames of PMD on [GitHub Releases](https://github.com/pmd/pmd/releases) are
  now `pmd-dist-<version>-bin.zip`, `pmd-dist-<version>-src.zip` and `pmd-dist-<version>-doc.zip`.
  Keep that in mind, if you have an automated download script.

  The structure inside the ZIP files stay the same, e.g. we still provide inside the binary distribution
  ZIP file the base directory `pmd-bin-<version>`.
* For maven-pmd-plugin usage, see [Using PMD 7 with maven-pmd-plugin](pmd_userdocs_tools_maven.html#using-pmd-7-with-maven-pmd-plugin).
* For gradle users, at least gradle 8.6 is required for PMD 7.

### For integrators

* PMD 7 is a major release where many things have been moved or rewritten.
* All integrators will require some level of change to adapt to the change in the API.
* For more details look at the deprecations notes of the past PMD 6 releases. These are collected below
  under [API Changes](#api-changes).
* The PMD Ant tasks, which were previously in the module `pmd-core` has been moved into its own module `pmd-ant`,
  which needs to be added explicitly now as an additional dependency.
* The CLI classes have also been moved out of `pmd-core` into its own module `pmd-cli`. The old entry point, the
  main class {%jdoc_old core::PMD %} is gone.

## 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

This however entails some incompatibilities and deprecations.

See [ADR 3 - API evolution principles](pmd_projectdocs_decisions_adr_3.html) and
[API changes](#api-changes) below.

### Small Changes and cleanups

* [#1648](https://github.com/pmd/pmd/issues/1648): \[apex,vf] Remove CodeClimate dependency - [Robert Sösemann](https://github.com/rsoesemann)
  Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" can no longer be overridden in rulesets.
  They were deprecated without replacement.

* The old GUI applications accessible through `run.sh designerold` and `run.sh bgastviewer`
  (and corresponding Batch scripts) have been removed from the PMD distribution. Please use the newer rule designer
  with `pmd designer`. The corresponding classes in packages `java.net.sourceforge.pmd.util.viewer` and
  `java.net.sourceforge.pmd.util.designer` have all been removed.

* All API related to XPath support has been moved to the package {% jdoc_package core::lang.rule.xpath %}.
  This includes API that was previously dispersed over `net.sourceforge.pmd.lang`, `net.sourceforge.pmd.lang.ast.xpath`,
  `net.sourceforge.pmd.lang.rule.xpath`, `net.sourceforge.pmd.lang.rule`, and various language-specific packages
  (which were made internal).

* The implementation of the Ant integration has been moved from the module `pmd-core` to a new module `pmd-ant`.
  This involves classes in package {% jdoc_package ant::ant %}. The ant CPDTask class `net.sourceforge.pmd.cpd.CPDTask`
  has been moved into the same package {% jdoc_package ant::ant %}. You'll need to update your taskdef entries in your
  build.xml files with the FQCN {% jdoc !!ant::ant.CPDTask %} if you use it anywhere.

* Utility classes in {% jdoc_package core::util %}, that have previously marked as `@InternalApi` have been finally
  moved to an internal sub package and are now longer available.
  This includes ClasspathClassLoader, FileFinder, FileUtil, and IOUtil.

* The following utility classes in {% jdoc_package core::util %} are now considered public API:
  * {% jdoc core::util.AssertionUtil %}
  * {% jdoc core::util.CollectionUtil %}
  * {% jdoc core::util.ContextedAssertionError %}
  * {% jdoc core::util.ContextedStackOverflowError %}
  * {% jdoc core::util.GraphUtil %}
  * {% jdoc core::util.IteratorUtil %}
  * {% jdoc core::util.StringUtil %}

* Moved the two classes {% jdoc core::cpd.impl.AntlrCpdLexer %} and {% jdoc core::cpd.impl.JavaccCpdLexer %} from
  `internal` package into package {% jdoc_package core::cpd.impl %}. These two classes are part of the API and
  are base classes for CPD language implementations. Since 7.0.0-rc2.
  Note: These two classes have been previously called "AntlrTokenizer" and "JavaCCTokenizer".
* `AntlrBaseRule` is gone in favor of {% jdoc core::lang.rule.AbstractVisitorRule %}. Since 7.0.0-rc2.
* The classes `net.sourceforge.pmd.lang.kotlin.ast.KotlinInnerNode` and
  `net.sourceforge.pmd.lang.swift.ast.SwiftInnerNode` are package-private now. Since 7.0.0-rc2.

### XPath 3.1 support

Support for XPath versions 1.0, 1.0-compatibility, 2.0 was removed. The default
(and only) supported XPath version is now XPath 3.1. This version of the XPath language is mostly identical to
XPath 2.0.

Notable changes:
* The deprecated support for sequence-valued attributes is removed. Sequence-valued properties are still supported.
* Refer to [the Saxonica documentation](https://www.saxonica.com/html/documentation/expressions/xpath31new.html) for
  an introduction to new features in XPath 3.1.

### Node stream API for AST traversal

This version includes a powerful API to navigate trees, similar in usage to the Java 8 Stream API:
```java
node.descendants(ASTMethodCall.class)
    .filter(m -> "toString".equals(m.getMethodName()))
    .map(m -> m.getQualifier())
    .filter(q -> TypeTestUtil.isA(String.class, q))
    .foreach(System.out::println);
```

A pipeline like shown here traverses the tree lazily, which is more efficient than traversing eagerly to put all
descendants in a list. It is also much easier to change than the old imperative way.

To make this API as accessible as possible, the {% jdoc core::lang.ast.Node %} interface has been fitted with new
methods producing node streams. Those methods replace previous tree traversal methods like `Node#findDescendantsOfType`.
In all cases, they should be more efficient and more convenient.

See {% jdoc core::lang.ast.NodeStream %} for more details.

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala)

### Metrics framework

The metrics framework has been made simpler and more general.

* The metric interface takes an additional type parameter, representing the result type of the metric. This is
  usually `Integer` or `Double`. It avoids widening the result to a `double` just to narrow it down.

  This makes it so, that `Double.NaN` is not an appropriate sentinel value to represent "not supported" anymore.
  Instead, `computeFor` may return `null` in that case (or a garbage value). The value `null` may have caused
  problems with the narrowing casts, which through unboxing, might have thrown an NPE. But when we deprecated
  the language-specific metrics façades to replace them with the generic {%jdoc core::lang.metrics.MetricsUtil %},
  we took care of making
  the new methods throw an exception if the metric cannot be computed on the parameter. This forces you to guard
  calls to {%jdoc !!core::lang.metrics.MetricsUtil#computeMetric(core::lang.metrics.Metric,N) %} (and other overloads)
  with something like `if (metric.supports(node))`. If you're following
  this pattern, then you won't observe the undefined behavior.

* The `MetricKey` interface is not so useful and has been merged into the {%jdoc core::lang.metrics.Metric %}
  interface and removed. So the {%jdoc core::lang.metrics.Metric %} interface has the new method
  {%jdoc core::lang.metrics.Metric#displayName() %}.

* The framework is not tied to at most 2 node types per language anymore. Previously those were nodes for
  classes and for methods/constructors. Instead, many metrics support more node types. For example, NCSS can
  be computed on any code block.

  For that reason, keeping around a hard distinction between "class metrics" and "operation metrics" is not
  useful. So in the Java framework for example, we removed the interfaces `JavaClassMetric`, `JavaOperationMetric`,
  abstract classes for those, `JavaClassMetricKey`, and `JavaOperationMetricKey`. Metric constants are now all
  inside the {%jdoc java::lang.java.metrics.JavaMetrics %} utility class. The same was done in the Apex framework.

  We don't really need abstract classes for metrics now. So `AbstractMetric` is also removed from pmd-core.
  There is a factory method on the {%jdoc core::lang.metrics.Metric %} interface to create a metric easily.

* This makes it so, that {% jdoc core::lang.metrics.LanguageMetricsProvider %} does not need type parameters.
  It can just return a `Set<Metric<?, ?>>` to list available metrics.

* {% jdoc_old core::lang.metrics.Signature %}s, their implementations, and the interface `SignedNode` have been
  removed. Node streams allow replacing their usages very easily.

### Testing framework

* PMD 7 has been upgraded to use JUnit 5 only. That means, that JUnit4 related classes have been removed, namely
  * `net.sourceforge.pmd.testframework.PMDTestRunner`
  * `net.sourceforge.pmd.testframework.RuleTestRunner`
  * `net.sourceforge.pmd.testframework.TestDescriptor`
* Rule tests, that use {% jdoc test::test.SimpleAggregatorTst %} or
  {% jdoc test::test.PmdRuleTst %} work as before without change, but use
  now JUnit5 under the hood. If you added additional JUnit4 tests to your rule test classes, then you'll
  need to upgrade them to use JUnit5.

### Language Lifecycle and Language Properties

* Language modules now provide a proper lifecycle and can store global information. This enables the implementation
  of multifile analysis.
* Language modules can define [custom language properties](pmd_languages_configuration.html)
  which can be set via environment variables. This allows to add and use language specific configuration options
  without the need to change pmd-core.

The documentation page has been updated:
[Adding a new language with JavaCC](pmd_devdocs_major_adding_new_language_javacc.html)
and [Adding a new language with ANTLR](pmd_devdocs_major_adding_new_language_antlr.html)

Related issue: [[core] Language lifecycle (#3782)](https://github.com/pmd/pmd/issues/3782)

### Rule properties

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  {% jdoc core::properties.PropertyFactory %} to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

### New Programmatic API for CPD

This release introduces a new programmatic API to replace the old class {%jdoc_old core::cpd.CPD %}. The new API uses a similar model to
{% jdoc core::PmdAnalysis %} and is called {% jdoc core::cpd.CpdAnalysis %}. Programmatic execution of CPD should now be
done with a {% jdoc core::cpd.CPDConfiguration %} and a {% jdoc core::cpd.CpdAnalysis %}, for instance:

```java
CPDConfiguration config = new CPDConfiguration();
config.setMinimumTileSize(100);
config.setOnlyRecognizeLanguage(config.getLanguageRegistry().getLanguageById("java"));
config.setSourceEncoding(StandardCharsets.UTF_8);
config.addInputPath(Path.of("src/main/java")

config.setIgnoreAnnotations(true);
config.setIgnoreLiterals(false);

config.setRendererName("text");

try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
   // note: don't use `config` once a CpdAnalysis has been created.
   // optional: add more files
   cpd.files().addFile(Paths.get("src", "main", "more-java", "ExtraSource.java"));

   cpd.performAnalysis();
}
```

CPD can of course still be called via command line or using the module `pmd-cli`. But for tight integration
this new programmatic API is recommended.

See [PR #4397](https://github.com/pmd/pmd/pull/4397) for details.

### API changes

#### 7.0.0

These are the changes between 7.0.0-rc4 and final 7.0.0.

**pmd-java**

* Support for Java 20 preview language features have been removed. The version "20-preview" is no longer available.
* {%jdoc java::lang.java.ast.ASTPattern %}, {%jdoc java::lang.java.ast.ASTRecordPattern %},
  {%jdoc java::lang.java.ast.ASTTypePattern %}, {%jdoc java::lang.java.ast.ASTUnnamedPattern %}
  - method `getParenthesisDepth()` has been removed.
* {%jdoc java::lang.java.ast.ASTTemplateFragment %}: To get the content of the template, use now
  {%jdoc java::lang.java.ast.ASTTemplateFragment#getContent() %} or `@Content` instead of `getImage()`/`@Image`.
* {%jdoc java::lang.java.ast.ASTUnnamedPattern %} is not experimental anymore. The language feature
  has been standardized with Java 22.

**New API**

The API around {%jdoc core::util.treeexport.TreeRenderer %} has been declared as stable. It was previously
experimental. It can be used via the CLI subcommand `ast-dump` or programmatically, as described
on [Creating XML dump of the AST](pmd_userdocs_extending_ast_dump.html).

**General AST Changes to avoid `@Image`**

See [General AST Changes to avoid @Image](pmd_userdocs_migrating_to_pmd7.html#general-ast-changes-to-avoid-image)
in the migration guide for details.

**XPath Rules**

* The property `version` was already deprecated and has finally been removed. Please don't define the version
  property anymore in your custom XPath rules. By default, the latest XPath version will be used, which
  is XPath 3.1.

**Moved classes/consolidated packages**

* pmd-core
  * Many types have been moved from the base package `net.sourceforge.pmd` into subpackage {% jdoc_package core::lang.rule %}
    * {%jdoc core::lang.rule.Rule %}
    * {%jdoc core::lang.rule.RulePriority %}
    * {%jdoc core::lang.rule.RuleSet %}
    * {%jdoc core::lang.rule.RuleSetFactory %}
    * {%jdoc core::lang.rule.RuleSetLoader %}
    * {%jdoc core::lang.rule.RuleSetLoadException %}
    * {%jdoc core::lang.rule.RuleSetWriter %}
  * Many types have been moved from the base package `net.sourceforge.pmd` into subpackage {% jdoc_package core::reporting %}
    * {%jdoc core::reporting.Report %}
    * {%jdoc core::reporting.RuleContext %}
    * {%jdoc core::reporting.RuleViolation %}
    * {%jdoc core::reporting.ViolationSuppressor %}
  * {%jdoc core::lang.rule.xpath.XPathRule %} has been moved into subpackage {% jdoc_package core::lang.rule.xpath %}.
* pmd-html
  * `net.sourceforge.pmd.lang.html.ast.HtmlCpdLexer` moved into package `cpd`: {%jdoc html::lang.html.cpd.HtmlCpdLexer %}.
* pmd-lang-test: All types have been moved under the new base package {%jdoc_package lang-test::lang.test %}:
  * {%jdoc lang-test::lang.test.AbstractMetricTestRule %} (moved from `net.sourceforge.pmd.test.AbstractMetricTestRule`)
  * {%jdoc lang-test::lang.test.BaseTextComparisonTest %} (moved from `net.sourceforge.pmd.test.BaseTextComparisonTest`)
  * {%jdoc lang-test::lang.test.cpd.CpdTextComparisonTest %} (moved from `net.sourceforge.pmd.cpd.test.CpdTextComparisonTest`)
  * {%jdoc lang-test::lang.test.ast.BaseTreeDumpTest %} (moved from `net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest`)
  * And many other types have been moved from `net.sourceforge.pmd.lang.ast.test` to `net.sourceforge.pmd.lang.test`.
* pmd-scala
  * {%jdoc scala::lang.scala.cpd.ScalaCpdLexer %} (moved from `net.sourceforge.pmd.lang.scala.cpd.ScalaCpdLexer`)
  * {%jdoc scala::lang.scala.cpd.ScalaTokenAdapter %} (moved from `net.sourceforge.pmd.lang.scala.cpd.ScalaTokenAdapter`)
* pmd-test
  * {%jdoc test::test.lang.rule.AbstractRuleSetFactoryTest %} (moved from `net.sourceforge.pmd.lang.rule.AbstractRuleSetFactoryTest`)
  * {%jdoc test::test.AbstractAntTestHelper %} (moved from `net.sourceforge.pmd.ant.AbstractAntTestHelper`)
  * {%jdoc test::test.AbstractLanguageVersionTest %} (moved from `net.sourceforge.pmd.AbstractLanguageVersionTest`)
  * {%jdoc test::test.PmdRuleTst %} (moved from `net.sourceforge.pmd.testframework.PmdRuleTst`)
  * {%jdoc test::test.RuleTst %} (moved from `net.sourceforge.pmd.testframework.RuleTst`)
  * {%jdoc test::test.SimpleAggregatorTst %} (moved from `net.sourceforge.pmd.testframework.SimpleAggregatorTst`)
* pmd-xml
  * {%jdoc xml::lang.xml.pom.PomLanguageModule %} (moved from `net.sourceforge.pmd.lang.pom.PomLanguageModule`)
  * {%jdoc xml::lang.xml.wsdl.WsdlLanguageModule %} (moved from `net.sourceforge.pmd.lang.wsdl.WsdlLanguageModule`)
  * {%jdoc xml::lang.xml.xsl.XslLanguageModule %} (moved from `net.sourceforge.pmd.lang.xsl.XslLanguageModule`)
* pmd-visualforce
  * The package `net.sourceforge.pmd.lang.vf` has been renamed to {%jdoc_package visualforce::lang.visualforce %}.
  * The language id of visualforce has been changed to `visualforce` (it was previously just "vf")
  * The ruleset changed: `category/vf/security.xml` ➡️ `category/visualforce/security.xml`
* pmd-velocity (renamed from pmd-vm)
  * The package `net.sourceforge.pmd.lang.vm` has been renamed to {%jdoc_package velocity::lang.velocity %}.
  * The language id of the Velocity module has been changed to `velocity` (it was previously just "vm")
  * The rulesets changed: `category/vm/...` ➡️ `category/velocity/...`
  * Many classes used the prefix `Vm`, e.g. `VmLanguageModule`. This has been changed to be `Vtl`:
    * {%jdoc velocity::lang.velocity.VtlLanguageModule %}
    * {%jdoc velocity::lang.velocity.ast.VtlNode %}
    * {%jdoc velocity::lang.velocity.ast.VtlParser %}
    * {%jdoc velocity::lang.velocity.cpd.VtlCpdLexer %}
    * {%jdoc velocity::lang.velocity.rule.AbstractVtlRule %}

**Internalized classes and interfaces and methods**

The following classes/methods have been marked as @<!-- -->InternalApi before and are now moved into a `internal`
package or made (package) private and are _not accessible_ anymore.

* pmd-core
  * `net.sourceforge.pmd.cache.AbstractAnalysisCache` (moved to internal, now package private)
  * `net.sourceforge.pmd.cache.AnalysisCache` (moved to internal)
  * `net.sourceforge.pmd.cache.AnalysisCacheListener` (moved to internal)
  * `net.sourceforge.pmd.cache.AnalysisResult` (moved to internal)
  * `net.sourceforge.pmd.cache.CachedRuleMapper` (moved to internal, now package private)
  * `net.sourceforge.pmd.cache.CachedRuleViolation` (moved to internal, now package private)
  * `net.sourceforge.pmd.cache.ChecksumAware` (moved to internal)
  * `net.sourceforge.pmd.cache.FileAnalysisCache` (moved to internal)
  * `net.sourceforge.pmd.cache.NoopAnalysisCache` (moved to internal)
  * `net.sourceforge.pmd.util.ResourceLoader` (moved to internal)
  * {%jdoc !!core::cpd.Tokens %}
    * Constructor is now package private.
  * {%jdoc !!core::lang.LanguageProcessor.AnalysisTask %}
    * Constructor is now package private.
    * Method `withFiles(java.util.List)` is now package private. Note: it was not previously marked with @<!-- -->InternalApi.
  * {%jdoc !!core::lang.rule.RuleTargetSelector %}
    * Method `isRuleChain()` has been removed.
  * {%jdoc !!core::renderers.AbstractAccumulatingRenderer %}
    * {%jdoc core::renderers.AbstractAccumulatingRenderer#renderFileReport(core::reporting.Report) %} - this method is now final
      and can't be overridden anymore.
  * {%jdoc !!core::reporting.Report %}
    * Constructor as well as the methods `addRuleViolation`, `addConfigError`, `addError` are now private.
  * {%jdoc !!core::reporting.RuleContext %}
    * Method `getRule()` is now package private.
    * Method `create(FileAnalysisListener listener, Rule rule)` has been removed.
  * `net.sourceforge.pmd.rules.RuleFactory`: moved into subpackage `lang.rule` and made package private.
    It has now been hidden completely from public API.
  * Many types have been moved from into subpackage `lang.rule.internal`.
    * `net.sourceforge.pmd.RuleSetReference`
    * `net.sourceforge.pmd.RuleSetReferenceId`
    * `net.sourceforge.pmd.RuleSets`
  * `net.sourceforge.pmd.lang.rule.ParametricRuleViolation` is now package private and moved to `net.sourceforge.pmd.reporting.ParametricRuleViolation`.
    The only public API is {%jdoc core::reporting.RuleViolation %}.
  * {%jdoc !!core::lang.rule.RuleSet %}
    * Method `applies(Rule,LanguageVersion)` is now package private.
    * Method `applies(TextFile)` has been removed.
    * Method `applies(FileId)` is now package private.
  * {%jdoc !!core::lang.rule.RuleSetLoader %}
    * Method `loadRuleSetsWithoutException(java.util.List)` is now package private.
  * {%jdoc !!core::lang.rule.RuleSetLoadException %}
    * All constructors are package private now.
  * {%jdoc !!core::lang.ast.LexException %} - the constructor `LexException(boolean, String, int, int, String, char)` is now package private.
    It is only used by JavaCC-generated token managers.
  * {%jdoc !!core::PMDConfiguration %}
    * Method `setAnalysisCache(AnalysisCache)` is now package private. Use {%jdoc core::PMDConfiguration#setAnalysisCacheLocation(java.lang.String) %} instead.
    * Method `getAnalysisCache()` is now package private.
  * {%jdoc !!core::lang.document.FileCollector %}
    * Method `newCollector(LanguageVersionDiscoverer, PmdReporter)` is now package private.
    * Method `newCollector(PmdReporter)` is now package private.
    * In order to create a FileCollector, use {%jdoc core::PmdAnalysis#files() %} instead.
  * {%jdoc !!core::lang.rule.xpath.Attribute %}
    * Method `replacementIfDeprecated()` is now package private.
  * `net.sourceforge.pmd.properties.PropertyTypeId` - moved in subpackage `internal`.
  * {%jdoc !!core::properties.PropertyDescriptor %} - method `getTypeId()` is now package private.
* pmd-doc
  * The whole maven module `pmd-doc` is now considered internal API even though it was not declared so before.
    It's used to generate the rule documentation for the built-in rules.
  * All the classes have been moved into package `net.sourceforge.pmd.doc.internal`.
* pmd-ant
  * {%jdoc !!ant::ant.Formatter %}
    * Method `getRenderer()` has been removed.
    * Method `start(String)` is private now.
    * Method `end(Report)` has been removed.
    * Method `isNoOutputSupplied()` is now package private.
    * Method `newListener(Project)` is now package private.
  * {%jdoc !!ant::ant.PMDTask %}
    * Method `getRelativizeRoots()` has been removed.
  * `net.sourceforge.pmd.ant.ReportException` is now package private. Note: It was not marked with @<!-- -->InternalApi before.
* pmd-apex
  * {%jdoc !!apex::lang.apex.ast.ApexNode %}
    * Method `getNode()` has been removed. It was only deprecated before and not marked with @<!-- -->InternalApi.
      However, it gave access to the wrapped Jorje node and was thus internal API.
  * {%jdoc !!apex::lang.apex.ast.AbstractApexNode %}
    * Method `getNode()` is now package private.
  * {%jdoc !!apex::lang.apex.multifile.ApexMultifileAnalysis %}
    * Constructor is now package private.
  * `net.sourceforge.pmd.lang.apex.rule.design.AbstractNcssCountRule` (now package private)
  * `net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule` (moved to package `net.sourceforge.pmd.apex.rule.bestpractices`, now package private)
* pmd-java
  * `net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule` (moved to internal)
  * `net.sourceforge.pmd.lang.java.types.ast.LazyTypeResolver` (moved to internal)
  * {%jdoc !!java::lang.java.types.JMethodSig %}
    * Method `internalApi()` has been removed.
  * {%jdoc !!java::lang.java.types.TypeOps %}
    * Method `isSameTypeInInference(JTypeMirror,JTypeMirror)` is now package private.
* pmd-jsp
  * {%jdoc !!jsp::lang.jsp.ast.JspParser %}
    * Method `getTokenBehavior()` has been removed.
* pmd-modelica
  * {%jdoc !!modelica::lang.modelica.ast.InternalApiBridge %} renamed from `InternalModelicaNodeApi`.
  * {%jdoc !!modelica::lang.modelica.resolver.InternalApiBridge %} renamed from `InternalModelicaResolverApi`.
  * `net.sourceforge.pmd.lang.modelica.resolver.ModelicaSymbolFacade` has been removed.
  * `net.sourceforge.pmd.lang.modelica.resolver.ResolutionContext` (moved to internal)
  * `net.sourceforge.pmd.lang.modelica.resolver.ResolutionState` (moved to internal). Note: it was not previously marked with @<!-- -->InternalApi.
  * `net.sourceforge.pmd.lang.modelica.resolver.Watchdog` (moved to internal). Note: it was not previously marked with @<!-- -->InternalApi.
* pmd-plsql
  * `net.sourceforge.pmd.lang.plsql.rule.design.AbstractNcssCountRule` is now package private.
* pmd-scala
  * {%jdoc !!scala::lang.scala.ScalaLanguageModule %}
    * Method `dialectOf(LanguageVersion)` has been removed.

**Removed classes and members (previously deprecated)**

The annotation `@DeprecatedUntil700` has been removed.

* pmd-core
  * {%jdoc !!core::cpd.CpdLanguageProperties %}. The field `DEFAULT_SKIP_BLOCKS_PATTERN` has been removed.
  * {%jdoc !!core::lang.ast.impl.antlr4.BaseAntlrNode %} - method `joinTokenText()` has been removed.
  * {%jdoc !!core::lang.ast.Node %} - many methods have been removed:
    * `getNthParent(int)` - Use {%jdoc core::lang.ast.Node#ancestors() %} instead, e.g. `node.ancestors().get(n-1)`
    * `getFirstParentOfType(Class)` - Use {%jdoc core::lang.ast.Node#ancestors(java.lang.Class) %} instead, e.g. `node.ancestors(parentType).first()`
    * `getParentsOfType(Class)` - Use {%jdoc core::lang.ast.Node#ancestors(java.lang.Class) %} instead, e.g. `node.ancestors(parentType).toList()`
    * `findChildrenOfType(Class)` - Use {%jdoc core::lang.ast.Node#children(java.lang.Class) %} instead, e.g. `node.children(childType).toList()`
    * `findDescendantsOfType(Class)` - Use {%jdoc core::lang.ast.Node#descendants(java.lang.Class) %} instead, e.g. `node.descendants(targetType).toList()`
    * `findDescendantsOfType(Class,boolean)` - Use {%jdoc core::lang.ast.Node#descendants(java.lang.Class) %} instead, e.g. `node.descendants(targetType).crossFindBoundaries(b).toList()`
    * `getFirstChildOfType(Class)` - Use {%jdoc core::lang.ast.Node#firstChild(java.lang.Class) %} instead
    * `getFirstDescendantOfType(Class)` - Use {%jdoc core::lang.ast.Node#descendants(java.lang.Class) %} instead, e.g. `node.descendants(targetType).first()`
    * `hasDescendantOfType(Class)` - Use {%jdoc core::lang.ast.Node#descendants(java.lang.Class) %} instead, e.g. `node.descendants(targetType).nonEmpty()`
    * `findChildNodesWithXPath(String)` - Use the {%jdoc core::lang.ast.NodeStream %} API instead.
  * {%jdoc !!core::lang.ast.impl.GenericNode %} - method `getNthParent(int)` has been removed. Use {%jdoc core::lang.ast.Node#ancestors() %} instead, e.g. `node.ancestors().get(n-1)`
  * {%jdoc !!core::lang.document.FileCollector %} - method `addZipFile(java.nio.file.Path)` has been removed. Use {%jdoc core::lang.document.FileCollector#addZipFileWithContent(java.nio.file.Path) %} instead
  * {%jdoc !!core::lang.document.TextDocument %} - method `readOnlyString(CharSequence,String,LanguageVersion)` has been removed.
    Use {%jdoc core::lang.document.TextDocument#readOnlyString(java.lang.CharSequence,core::lang.document.FileId,core::lang.LanguageVersion) %} instead.
  * {%jdoc !!core::lang.document.TextFile %} - method `dataSourceCompat(DataSource,PMDConfiguration)` has been removed.
    Use {%jdoc core::lang.document.TextFile %} directly, e.g. {%jdoc core::lang.document.TextFile.forPath(java.nio.file.Path,java.nio.charset.Charset,core::lang.LanguageVersion) %}
  * {%jdoc !!core::lang.rule.xpath.XPathVersion %}
    * `XPATH_1_0`
    * `XPATH_1_0_COMPATIBILITY`
    * `XPATH_2_0`
    * Only XPath version 3.1 is now supported.  This version of the XPath language is mostly identical to
      XPath 2.0. XPath rules by default use now {%jdoc core::lang.rule.xpath.XPathVersion#XPATH_3_1 %}.
  * `net.sourceforge.pmd.lang.rule.AbstractDelegateRule` removed. It has been merged with {%jdoc core::lang.rule.RuleReference %}.
  * {%jdoc !!core::lang.rule.AbstractRule %} - the following methods have been removed:
    * `deepCopyValuesTo(AbstractRule)` - use {%jdoc core::lang.rule.AbstractRule#deepCopy() %} instead.
    * `addRuleChainVisit(Class)` - override {%jdoc core::lang.rule.AbstractRule#buildTargetSelector() %} in order to register nodes for rule chain visits.
    * `addViolation(...)` - use {%jdoc core::reporting.RuleContext#addViolation(core::lang.ast.Node) %} instead, e.g. via `asCtx(data).addViolation(...)`.
      Note: These methods were only marked as deprecated in javadoc.
    * `addViolationWithMessage(...)` - use {%jdoc core::reporting.RuleContext#addViolationWithMessage(core::lang.ast.Node,java.lang.String) %} instead, e.g. via
      `asCtx(data).addViolationWithMessage(...)`. Note: These methods were only marked as deprecated in javadoc.
  * {%jdoc !!core::lang.rule.RuleReference %} - the following methods have been removed:
    * `setRuleSetReference(RuleSetReference)` - without replacement. Just construct new {%jdoc core::lang.rule.RuleReference %} instead.
    * `hasOverriddenProperty(PropertyDescriptor)` - use {%jdoc core::lang.rule.RuleReference#isPropertyOverridden(core::properties.PropertyDescriptor) %} instead.
  * {%jdoc !!core::lang.rule.xpath.XPathRule %}
    * The constant `XPATH_DESCRIPTOR` has been made private and is not accessible anymore.
  * {%jdoc !!core::lang.Language %} - method `getTerseName()` removed. Use {%jdoc core::lang.Language#getId() %} instead.
  * {%jdoc !!core::lang.LanguageModuleBase %} - method `getTerseName()` removed. Use {%jdoc core::lang.LanguageModuleBase#getId() %} instead.
  * {%jdoc !!core::lang.LanguageRegistry %} - the following methods have been removed:
    * `getLanguage(String)` - use {%jdoc core::lang.LanguageRegistry.getLanguageByFullName(java.lang.String) %}
      via {%jdoc core::lang.LanguageRegistry#PMD %} or {%jdoc core::lang.LanguageRegistry#CPD %} instead.
    * `findLanguageByTerseName(String)` - use {%jdoc core::lang.LanguageRegistry#getLanguageById(java.lang.String) %}
      via {%jdoc core::lang.LanguageRegistry#PMD %} or {%jdoc core::lang.LanguageRegistry#CPD %} instead.
    * `findByExtension(String)` - removed without replacement.
  * {%jdoc !!core::lang.LanguageVersionDiscoverer %} - method `getLanguagesForFile(java.io.File)` removed.
    Use {%jdoc core::lang.LanguageVersionDiscoverer#getLanguagesForFile(java.lang.String) %} instead.
  * {%jdoc !!core::properties.AbstractPropertySource %}
    * field `propertyDescriptors` has been made private and is not accessible anymore.
      Use {%jdoc core::properties.AbstractPropertySource#getPropertyDescriptors() %} instead.
    * field `propertyValuesByDescriptor` has been made private and is not accessible anymore.
      Use {%jdoc core::properties.AbstractPropertySource#getPropertiesByPropertyDescriptor() %}
      or {%jdoc core::properties.AbstractPropertySource#getOverriddenPropertiesByPropertyDescriptor() %} instead.
    * method `copyPropertyDescriptors()` has been removed. Use {%jdoc core::properties.AbstractPropertySource#getPropertyDescriptors() %} instead.
    * method `copyPropertyValues()` has been removed. Use {%jdoc core::properties.AbstractPropertySource#getPropertiesByPropertyDescriptor() %}
      or {%jdoc core::properties.AbstractPropertySource#getOverriddenPropertiesByPropertyDescriptor() %} instead.
  * {%jdoc !!core::reporting.Reportable %} - the following methods have been removed. Use {%jdoc core::reporting.Reportable#getReportLocation() %} instead
    * `getBeginLine()`
    * `getBeginColumn()`
    * `getEndLine()`
    * `getEndColumn()`
  * `net.sourceforge.pmd.util.datasource.DataSource` - use {%jdoc core::lang.document.TextFile %} instead.
  * `net.sourceforge.pmd.util.datasource.FileDataSource`
  * `net.sourceforge.pmd.util.datasource.ReaderDataSource`
  * `net.sourceforge.pmd.util.datasource.ZipDataSource`
  * {%jdoc !!core::util.CollectionUtil %}
    * method `invertedMapFrom(...)` has been removed.
    * method `mapFrom(...)` has been removed.
  * {%jdoc !!core::AbstractConfiguration %} - the following methods have been removed:
    * `setIgnoreFilePath(String)` - use {%jdoc core::AbstractConfiguration#setIgnoreFilePath(java.nio.file.Path) %} instead.
    * `setInputFilePath(String)` - use {%jdoc core::AbstractConfiguration#setInputFilePath(java.nio.file.Path) %} instead.
    * `setInputPaths(String)` - use {%jdoc core::AbstractConfiguration#setInputPathList(java.util.List) %} or
      {%jdoc core::AbstractConfiguration#addInputPath(java.nio.file.Path) %} instead.
    * `setInputUri(String)` - use {%jdoc core::AbstractConfiguration#setInputUri(java.net.URI) %} instead.
  * {%jdoc !!core::PMDConfiguration %} - the following methods have been removed
    * `prependClasspath(String)` - use {%jdoc core::PMDConfiguration#prependAuxClasspath(java.lang.String) %} instead.
    * `getRuleSets()` - use {%jdoc core::PMDConfiguration#getRuleSetPaths() %} instead.
    * `setRuleSets(String)` - use {%jdoc core::PMDConfiguration#setRuleSets(java.util.List) %} or
      {%jdoc core::PMDConfiguration#addRuleSet(java.lang.String) %} instead.
    * `setReportFile(String)` - use {%jdoc core::PMDConfiguration#setReportFile(java.nio.file.Path) %} instead.
    * `getReportFile()` - use {%jdoc core::PMDConfiguration#getReportFilePath() %} instead.
  * {%jdoc !!core::reporting.Report %} - method `merge(Report)` has been removed. Use {%jdoc core::reporting.Report#union(core::Report) %} instead.
  * {%jdoc !!core::lang.rule.RuleSetLoader %} - method `toFactory()` has been made package private and is not accessible anymore.
  * {%jdoc !!core::reporting.RuleViolation %} - the following methods have been removed:
    * `getPackageName()` - use {%jdoc core::reporting.RuleViolation#getAdditionalInfo() %} with {%jdoc core::reporting.RuleViolation#PACKAGE_NAME %} instead, e.g. `getAdditionalInfo().get(PACKAGE_NAME)`.
    * `getClassName()` - use {%jdoc core::reporting.RuleViolation#getAdditionalInfo() %} with {%jdoc core::reporting.RuleViolation#CLASS_NAME %} instead, e.g. `getAdditionalInfo().get(CLASS_NAME)`.
    * `getMethodName()` - use {%jdoc core::reporting.RuleViolation#getAdditionalInfo() %} with {%jdoc core::reporting.RuleViolation#METHOD_NAME %} instead, e.g. `getAdditionalInfo().get(METHOD_NAME)`.
    * `getVariableName()` - use {%jdoc core::reporting.RuleViolation#getAdditionalInfo() %} with {%jdoc core::reporting.RuleViolation#VARIABLE_NAME %} instead, e.g. `getAdditionalInfo().get(VARIABLE_NAME)`.
* pmd-apex
  * {%jdoc apex::lang.apex.ast.ApexNode %} and {% jdoc apex::lang.apex.ast.ASTApexFile %}
    * `#getApexVersion()`: In PMD 6, this method has been deprecated but was defined in the class `ApexRootNode`.
      The version returned is always "Version.CURRENT", as the apex compiler integration
      doesn't use additional information which Apex version actually is used. Therefore, this method can't be
      used to determine the Apex version of the project that is being analyzed.

      If the current version is needed, then `Node.getTextDocument().getLanguageVersion()` can be used. This
      is the version that has been selected via CLI `--use-version` parameter.
  * {%jdoc !!apex::lang.apex.ast.ApexNode %}
    * method `jjtAccept()` has been removed.
      Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
    * method `getNode()` has been removed. The underlying node is only available in AST nodes, but not in rule implementations.
  * {%jdoc !!apex::lang.apex.ast.AbstractApexNode %} - method `getNode()` is now package private.
    AST nodes still have access to the underlying Jorje node via the protected property `node`.
  * `net.sourceforge.pmd.lang.apex.ast.ApexParserVisitor`
    Use {%jdoc apex::lang.apex.ast.ApexVisitor %} or {%jdoc apex::lang.apex.ast.ApexVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter`
  * {%jdoc !!apex::lang.apex.ast.ASTAssignmentExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTAssignmentExpression#getOp() %} instead.
  * {%jdoc !!apex::lang.apex.ast.ASTBinaryExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTBinaryExpression#getOp() %} instead.
  * {%jdoc !!apex::lang.apex.ast.ASTBooleanExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTBooleanExpression#getOp() %} instead.
  * {%jdoc !!apex::lang.apex.ast.ASTPostfixExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTPostfixExpression#getOp() %} instead.
  * {%jdoc !!apex::lang.apex.ast.ASTPrefixExpression %} - method `getOperator()` removed.
    Use {%jdoc apex::lang.apex.ast.ASTPrefixExpression#getOp() %} instead.
  * `net.sourceforge.pmd.lang.apex.rule.security.Helper` removed. This was actually internal API.
* pmd-java
  * {%jdoc !!java::lang.java.ast.AbstractPackageNameModuleDirective %} - method `getImage()` has been removed.
    Use {%jdoc java::lang.java.ast.AbstractPackageNameModuleDirective#getPackageName() %} instead.
  * {%jdoc !!java::lang.java.ast.AbstractTypeDeclaration %} - method `getImage()` has been removed.
    Use {%jdoc java::lang.java.ast.AbstractTypeDeclaration#getSimpleName() %} instead.
  * {%jdoc !!java::lang.java.ast.ASTAnnotation %} - method `getAnnotationName()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTClassType %}
    * constructor `ASTClassType(java.lang.String)` has been removed.
    * method `getImage()` has been removed.
    * method `isReferenceToClassSameCompilationUnit()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTFieldDeclaration %} - method `getVariableName()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTLiteral %} - the following methods have been removed:
    * `isStringLiteral()` - use `node instanceof ASTStringLiteral` instead.
    * `isCharLiteral()` - use `node instanceof ASTCharLiteral` instead.
    * `isNullLiteral()` - use `node instanceof ASTNullLiteral` instead.
    * `isBooleanLiteral()` - use `node instanceof ASTBooleanLiteral` instead.
    * `isNumericLiteral()` - use `node instanceof ASTNumericLiteral` instead.
    * `isIntLiteral()` - use {%jdoc java::lang.java.ast.ASTNumericLiteral#isIntLiteral() %} instead.
    * `isLongLiteral()` - use {%jdoc java::lang.java.ast.ASTNumericLiteral#isLongLiteral() %} instead.
    * `isFloatLiteral()` - use {%jdoc java::lang.java.ast.ASTNumericLiteral#isFloatLiteral() %} instead.
    * `isDoubleLiteral()` - use {%jdoc java::lang.java.ast.ASTNumericLiteral#isDoubleLiteral() %} instead.
  * {%jdoc !!java::lang.java.ast.ASTMethodDeclaration %} - methods `getImage()` and `getMethodName()` have been removed.
    Use {%jdoc java::lang.java.ast.ASTMethodDeclaration#getName() %} instead.
  * {%jdoc !!java::lang.java.ast.ASTMethodReference %} - method `getImage()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTModuleName %} - method `getImage()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTPrimitiveType %} - method `getImage()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTType %}
    * `getTypeImage()` has been removed.
    * `getArrayDepth()` has been removed. It's only available for arrays: {%jdoc java::lang.java.ast.ASTArrayType#getArrayDepth() %}.
    * `isPrimitiveType()` - use `node instanceof ASTPrimitiveType` instead.
    * `isArrayType()` - use `node instanceof ASTArrayType` instead.
    * `isClassOrInterfaceType()` - use `node instanceof ASTClassType` instead.
  * {%jdoc !!java::lang.java.ast.ASTTypeDeclaration %} - method `getImage()` has been removed.
  * {%jdoc !!java::lang.java.ast.ASTUnaryExpression %} - method `isPrefix()` has been removed.
    Use {%jdoc java::lang.java.ast.ASTUnaryExpression#getOperator() %}`.isPrefix()` instead.
  * {%jdoc !!java::lang.java.ast.ASTVariableId %} - methods `getImage()` and `getVariableName()` have been removed.
    Use {%jdoc java::lang.java.ast.ASTVariableId#getName() %} instead.
  * {%jdoc !!java::lang.java.ast.JavaComment %} - method `getImage()` has been removed.
    Use {%jdoc java::lang.java.ast.JavaComment#getText() %} instead.
  * {%jdoc !!java::lang.java.ast.JavaNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
  * `net.sourceforge.pmd.lang.java.ast.JavaParserVisitor`
    Use {%jdoc java::lang.java.ast.JavaVisitor %} or {%jdoc java::lang.java.ast.JavaVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter`
  * {%jdoc !!java::lang.java.ast.ModifierOwner %}
    * `isFinal()` - This is still available in various subtypes, where it makes sense, e.g. {%jdoc java::lang.java.ast.ASTLocalVariableDeclaration#isFinal() %}.
    * `isAbstract()` - This is still available in subtypes, e.g. {%jdoc java::lang.java.ast.ASTTypeDeclaration#isAbstract() %}.
    * `isStrictfp()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(STRICTFP)`.
    * `isSynchronized()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(SYNCHRONIZED)`.
    * `isNative()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(NATIVE)`.
    * `isStatic()` - This is still available in subtypes, e.g. {%jdoc java::lang.java.ast.ASTMethodDeclaration#isStatic() %}.
    * `isVolatile()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(VOLATILE)`.
    * `isTransient()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasModifiers(TRANSIENT)`.
    * `isPrivate()` - Use {%jdoc java::lang.java.ast.ModifierOwner#getVisibility() %} instead, e.g. `getVisibility() == Visibility.V_PRIVATE`.
    * `isPublic()` - Use {%jdoc java::lang.java.ast.ModifierOwner#getVisibility() %} instead, e.g. `getVisibility() == Visibility.V_PUBLIC`.
    * `isProtected()` - Use {%jdoc java::lang.java.ast.ModifierOwner#getVisibility() %} instead, e.g. `getVisibility() == Visibility.V_PROTECTED`.
    * `isPackagePrivate()` - Use {%jdoc java::lang.java.ast.ModifierOwner#getVisibility() %} instead, e.g. `getVisibility() == Visibility.V_PACKAGE`.
    * `isSyntacticallyAbstract()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasExplicitModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasExplicitModifiers(ABSTRACT)`.
    * `isSyntacticallyPublic()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasExplicitModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasExplicitModifiers(PUBLIC)`.
    * `isSyntacticallyStatic()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasExplicitModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasExplicitModifiers(STATIC)`.
    * `isSyntacticallyFinal()` - Use {%jdoc java::lang.java.ast.ModifierOwner#hasExplicitModifiers(java::lang.java.ast.JModifier,java::lang.java.ast.JModifier...) %} instead, e.g. `hasExplicitModifiers(FINAL)`.
  * {%jdoc !!java::lang.java.ast.TypeNode %} - method `getType()` has been removed. Use {%jdoc java::lang.java.ast.TypeNode#getTypeMirror() %} instead.
* pmd-javascript
  * {%jdoc javascript::lang.ecmascript.ast.AbstractEcmascriptNode %} - method `getNode()` has been removed.
    AST nodes still have access to the underlying Rhino node via the protected property `node`.
  * {%jdoc javascript::lang.ecmascript.ast.ASTFunctionNode %} - method `getBody(int)` removed.
    Use {%jdoc javascript::lang.ecmascript.ast.ASTFunctionNode#getBody() %} instead.
  * {%jdoc javascript::lang.ecmascript.ast.ASTTryStatement %}
    * method `isCatch()` has been removed. Use {%jdoc javascript::lang.ecmascript.ast.ASTTryStatement#hasCatch() %} instead.
    * method `isFinally()` has been removed. USe {%jdoc javascript::lang.ecmascript.ast.ASTTryStatement#hasFinally() %} instead.
  * {%jdoc javascript::lang.ecmascript.ast.EcmascriptNode %}
    * method `jjtAccept()` has been removed. Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
    * method `getNode()` has been removed.  The underlying node is only available in AST nodes, but not in rule implementations.
  * `net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserVisitor`
    Use {%jdoc javascript::lang.ecmascript.ast.EcmascriptVisitor %} or {%jdoc javascript::lang.ecmascript.ast.EcmascriptVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserVisitorAdapter`
* pmd-jsp
  * `net.sourceforge.pmd.lang.jsp.ast.JspParserVisitor`
    Use {%jdoc jsp::lang.jsp.ast.JspVisitor %} or {%jdoc jsp::lang.jsp.ast.JspVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.jsp.ast.JspParserVisitorAdapter`
  * {%jdoc !!jsp::lang.jsp.ast.JspNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
* pmd-modelica
  * `net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitor`
    Use {%jdoc modelica::lang.modelica.ast.ModelicaVisitor %} or {%jdoc modelica::lang.modelica.ast.ModelicaVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitorAdapter`
  * {%jdoc !!modelica::lang.modelica.ast.ModelicaNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
  * `net.sourceforge.pmd.lang.modelica.rule.AmbiguousResolutionRule`
    Use {%jdoc modelica::lang.modelica.rule.bestpractices.AmbiguousResolutionRule %} instead.
  * `net.sourceforge.pmd.lang.modelica.rule.ConnectUsingNonConnector`
    Use {%jdoc modelica::lang.modelica.rule.bestpractices.ConnectUsingNonConnectorRule %}
* pmd-plsql
  * `net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitor`
    Use {%jdoc plsql::lang.plsql.ast.PlsqlVisitor %} or {% jdoc plsql::lang.plsql.ast.PlsqlVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter`
  * {%jdoc !!plsql::lang.plsql.ast.PLSQLNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
* pmd-scala
  * The maven module `pmd-scala` has been removed. Use `pmd-scala_2.13` or `pmd-scala_2.12` instead.
  * {%jdoc !!scala::lang.scala.ast.ScalaNode %}
    * Method `accept()` has been removed. Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
    * Method `getNode()` has been removed. The underlying node is only available in AST nodes, but not in rule implementations.
  * {%jdoc !!scala::lang.scala.ast.AbstractScalaNode %} - method `getNode()` has been removed. AST nodes still have access
    to the underlying Scala node via the protected property `node`.
* pmd-visualforce
  * {%jdoc !!visualforce::lang.visualforce.ast.VfNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
  * `net.sourceforge.pmd.lang.vf.ast.VfParserVisitor`
    Use {%jdoc visualforce::lang.visualforce.ast.VfVisitor %} or {%jdoc visualforce::lang.visualforce.ast.VfVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.vf.ast.VfParserVisitorAdapter`
  * {%jdoc !!visualforce::lang.visualforce.DataType %} - method `fromBasicType(BasicType)` has been removed.
    Use {%jdoc visualforce::lang.visualforce.DataType#fromTypeName(java.lang.String) %} instead.
* pmd-velocity (previously pmd-vm)
  * {%jdoc !!velocity::lang.velocity.ast.VtlNode %} - method `jjtAccept()` has been removed.
    Use {%jdoc core::lang.ast.Node#acceptVisitor(core::lang.ast.AstVisitor,P) %} instead.
  * `net.sourceforge.pmd.lang.vm.ast.VmParserVisitor`
    Use {%jdoc velocity::lang.velocity.ast.VtlVisitor %} or {%jdoc velocity::lang.velocity.ast.VtlVisitorBase %} instead.
  * `net.sourceforge.pmd.lang.vm.ast.VmParserVisitorAdapter`

**Removed classes, interfaces and methods (not previously deprecated)**

* pmd-apex
  * The method `isSynthetic()` in {%jdoc apex::lang.apex.ast.ASTMethod %} has been removed.
    With the switch from Jorje to Summit AST as underlying parser, no synthetic methods are generated by the
    parser anymore. This also means, that there is no XPath attribute `@Synthetic` anymore.
  * The constant `STATIC_INITIALIZER_METHOD_NAME` in {%jdoc apex::lang.apex.rule.codestyle.FieldDeclarationsShouldBeAtStartRule %}
    has been removed. It was used to filter out synthetic methods, but these are not generated anymore with the
    new parser.
  * The method `getContext()` in {%jdoc apex::lang.apex.ast.ASTReferenceExpression %} has been removed.
    It was not used and always returned `null`.
  * The method `getNamespace()` in all AST nodes (defined in {%jdoc apex::lang.apex.ast.ApexNode %}) has
    been removed, as it was never fully implemented. It always returned an empty string.
  * The method `getNameSpace()` in {%jdoc apex::lang.apex.ast.ApexQualifiedName %} has been removed.
  * The class `net.sourceforge.pmd.lang.apex.ast.ASTBridgeMethodCreator` has been removed. This was a node that has
    been generated by the old Jorje parser only.
* pmd-apex-jorje
  * With the switch from Jorje to Summit AST, this maven module is no longer needed and has been removed.
* pmd-core
  * `net.sourceforge.pmd.util.Predicate` has been removed. It was marked as Experimental before. Use
    `java.util.function.Predicate` instead.
* pmd-java
  * The interface `FinalizableNode` (introduced in 7.0.0-rc1) has been removed.
    Its method `isFinal()` has been moved down to the
    nodes where needed, e.g. {% jdoc !!java::lang.java.ast.ASTLocalVariableDeclaration#isFinal() %}.
  * The method `isPackagePrivate()` in {% jdoc java::lang.java.ast.ASTClassDeclaration %} (formerly ASTClassOrInterfaceDeclaration)
    has been removed.
    Use {% jdoc java::lang.java.ast.ModifierOwner#hasVisibility(java::lang.java.ast.ModifierOwner.Visibility) %} instead,
    which can correctly differentiate between local and package private classes.

**Renamed classes, interfaces, methods**

* pmd-core
  * {%jdoc core::util.log.PmdReporter %} - has been renamed from `net.sourceforge.pmd.util.log.MessageReporter`
  * {%jdoc_old core::lang.ast.TokenMgrError %} has been renamed to {% jdoc core::lang.ast.LexException %}
  * {%jdoc_old core::cpd.Tokenizer %} has been renamed to {% jdoc core::cpd.CpdLexer %}. Along with this rename,
    all the implementations have been renamed as well (`Tokenizer` -> `CpdLexer`), e.g. "CppCpdLexer", "JavaCpdLexer".
    This affects all language modules.
  * {%jdoc_old core::cpd.AnyTokenizer %} has been renamed to {% jdoc core::cpd.AnyCpdLexer %}.

* pmd-java
  * The interface `AccessNode` has been renamed to {% jdoc java::lang.java.ast.ModifierOwner %}. This is only relevant
    for Java rules, which use that type directly e.g. through downcasting.
    Or when using the XPath function `pmd-java:nodeIs()`.
  * The node `ASTClassOrInterfaceType` has been renamed to {% jdoc java::lang.java.ast.ASTClassType %}. XPath rules
    need to be adjusted.
  * The node `ASTClassOrInterfaceDeclaration` has been renamed to {% jdoc java::lang.java.ast.ASTClassDeclaration %}.
    XPath rules need to be adjusted.
  * The interface `ASTAnyTypeDeclaration` has been renamed to {% jdoc java::lang.java.ast.ASTTypeDeclaration %}.
    This is only relevant for Java rules, which use that type directly, e.g. through downcasting.
    Or when using the XPath function `pmd-java:nodeIs()`.
  * The interface `ASTMethodOrConstructorDeclaration` has been renamed to
    {% jdoc java::lang.java.ast.ASTExecutableDeclaration %}. This is only relevant for Java rules, which use that type
    directly, e.g. through downcasting. Or when using the XPath function `pmd-java:nodeIs()`.
  * The node `ASTVariableDeclaratorId` has been renamed to {% jdoc java::lang.java.ast.ASTVariableId %}. XPath rules
    need to be adjusted.
  * The node `ASTClassOrInterfaceBody` has been renamed to {% jdoc java::lang.java.ast.ASTClassBody %}. XPath rules
    need to be adjusted.
* pmd-scala
  * The interface `ScalaParserVisitor` has been renamed to {%jdoc scala::lang.scala.ast.ScalaVisitor %} in order
    to align the naming scheme for the different language modules.
  * The class `ScalaParserVisitorAdapter` has been renamed to {%jdoc scala::lang.scala.ast.ScalaVisitorBase %} in order
    to align the naming scheme for the different language modules.

**New API**

These were annotated with `@Experimental`, but can now be considered stable.

* pmd-apex
  * {%jdoc !!apex::lang.apex.ast.ASTCommentContainer %}
  * {%jdoc !!apex::lang.apex.multifile.ApexMultifileAnalysis %}
* pmd-core
  * {%jdoc !!core::cpd.CPDReport#filterMatches(java.util.function.Predicate) %}
  * {%jdoc !!core::lang.ast.impl.antlr4.AntlrToken.getKind() %}
  * {%jdoc !!core::lang.ast.impl.javacc.AbstractJjtreeNode %}
  * {%jdoc !!core::lang.ast.impl.TokenDocument %}
  * {%jdoc !!core::lang.ast.AstInfo.getSuppressionComments() %}
  * {%jdoc !!core::lang.ast.AstInfo.withSuppressMap(java.util.Map) %}
  * {%jdoc !!core::lang.ast.GenericToken.getKind() %}
  * {%jdoc !!core::lang.document.FileCollector.addZipFileWithContent(java.nio.file.Path) %}
  * {%jdoc_package core::lang.document %}
  * {%jdoc !!core::lang.LanguageVersionHandler.getLanguageMetricsProvider() %}
  * {%jdoc !!core::lang.LanguageVersionHandler.getDesignerBindings() %}
  * {%jdoc !!core::lang.PlainTextLanguage %}
  * {%jdoc !!core::properties.PropertyConstraint.getXmlConstraint() %}
  * {%jdoc !!core::properties.PropertyConstraint.toOptionalConstraint() %}
  * {%jdoc !!core::properties.PropertyConstraint.fromPredicate(java.util.function.Predicate,java.lang.String) %}
  * {%jdoc !!core::properties.PropertyConstraint.fromPredicate(java.util.function.Predicate,java.lang.String,java.util.Map) %}
  * {%jdoc !!core::renderers.AbstractRenderer.setReportFile(java.lang.String) %}
  * {%jdoc !!core::renderers.Renderer.setReportFile(java.lang.String) %}
  * {%jdoc !!core::util.designerbindings.DesignerBindings %}
  * {%jdoc !!core::util.designerbindings.DesignerBindings.TreeIconId %}
  * {%jdoc !!core::util.designerbindings.RelatedNodesSelector %}
  * {%jdoc !!core::reporting.Report.filterViolations(java.util.function.Predicate) %}
  * {%jdoc !!core::reporting.Report.union(core::Report) %}
* pmd-groovy
  * {%jdoc !!groovy::lang.groovy.ast.impl.antlr4.GroovyToken.getKind() %}
* pmd-html
  * {%jdoc_package html::lang.html %}
* pmd-java
  * {%jdoc !!java::lang.java.ast.ASTExpression#getConversionContext() %}
  * {%jdoc !!java::lang.java.rule.AbstractJavaRulechainRule#AbstractJavaRulechainRule(java.lang.Class,java.lang.Class...) %}
  * {%jdoc !!java::lang.java.symbols.table.JSymbolTable %}
  * {%jdoc !!java::lang.java.symbols.JElementSymbol %}
  * {%jdoc_package java::lang.java.symbols %}
  * {%jdoc !!java::lang.java.types.ast.ExprContext %}
  * {%jdoc !!java::lang.java.types.JIntersectionType#getInducedClassType() %}
  * {%jdoc !!java::lang.java.types.JTypeMirror#streamMethods(java.util.function.Predicate) %}
  * {%jdoc !!java::lang.java.types.JTypeMirror#streamDeclaredMethods(java.util.function.Predicate) %}
  * {%jdoc !!java::lang.java.types.JTypeMirror#getConstructors() %}
* pmd-kotlin
  * {%jdoc !!kotlin::lang.kotlin.KotlinLanguageModule %}
* pmd-test-schema
  * {%jdoc !!test-schema::test.schema.TestSchemaParser %}

**Removed functionality**

* The CLI parameter `--no-ruleset-compatibility` has been removed. It was only used to allow loading
  some rulesets originally written for PMD 5 also in PMD 6 without fixing the rulesets.
* The class {% jdoc_old core::RuleSetFactoryCompatibility %} has been removed without replacement.
  The different ways to enable/disable this filter in {% jdoc core::PMDConfiguration %}
  (Property "RuleSetFactoryCompatibilityEnabled") and
  {% jdoc ant::ant.PMDTask %} (Property "noRuleSetCompatibility") have been removed as well.
* `textcolor` renderer ({%jdoc core::renderers.TextColorRenderer %}) now renders always in color.
  The property `color` has been removed. The possibility to override this with the system property `pmd.color`
  has been removed as well. If you don't want colors, use `text` renderer ({%jdoc core::renderers.TextRenderer %}).

#### 7.0.0-rc4

**pmd-java**

* Support for Java 19 preview language features have been removed. The version "19-preview" is no longer available.

**Rule properties**

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  {% jdoc core::properties.PropertyFactory %} to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

**New Programmatic API for CPD**

See [Detailed Release Notes for PMD 7](pmd_release_notes_pmd7.html#new-programmatic-api-for-cpd)
and [PR #4397](https://github.com/pmd/pmd/pull/4397) for details.

**Removed classes and methods**

The following previously deprecated classes have been removed:

* pmd-core
  * `net.sourceforge.pmd.cpd.AbstractTokenizer` ➡️ use {%jdoc core::cpd.AnyCpdLexer %} instead (previously known as AnyTokenizer)
  * `net.sourceforge.pmd.cpd.CPD` ➡️ use {% jdoc cli::cli.PmdCli %} from `pmd-cli` module for CLI support or use
    {%jdoc core::cpd.CpdAnalysis %} for programmatic API
  * `net.sourceforge.pmd.cpd.GridBagHelper` (now package private)
  * `net.sourceforge.pmd.cpd.TokenEntry.State`
  * `net.sourceforge.pmd.lang.document.CpdCompat`
  * `net.sourceforge.pmd.properties.BooleanMultiProperty`
  * `net.sourceforge.pmd.properties.BooleanProperty`
  * `net.sourceforge.pmd.properties.CharacterMultiProperty`
  * `net.sourceforge.pmd.properties.CharacterProperty`
  * `net.sourceforge.pmd.properties.DoubleMultiProperty`
  * `net.sourceforge.pmd.properties.DoubleProperty`
  * `net.sourceforge.pmd.properties.EnumeratedMultiProperty`
  * `net.sourceforge.pmd.properties.EnumeratedProperty`
  * `net.sourceforge.pmd.properties.EnumeratedPropertyDescriptor`
  * `net.sourceforge.pmd.properties.FileProperty` (note: without replacement)
  * `net.sourceforge.pmd.properties.FloatMultiProperty`
  * `net.sourceforge.pmd.properties.FloatProperty`
  * `net.sourceforge.pmd.properties.IntegerMultiProperty`
  * `net.sourceforge.pmd.properties.IntegerProperty`
  * `net.sourceforge.pmd.properties.LongMultiProperty`
  * `net.sourceforge.pmd.properties.LongProperty`
  * `net.sourceforge.pmd.properties.MultiValuePropertyDescriptor`
  * `net.sourceforge.pmd.properties.NumericPropertyDescriptor`
  * `net.sourceforge.pmd.properties.PropertyDescriptorField`
  * `net.sourceforge.pmd.properties.RegexProperty`
  * `net.sourceforge.pmd.properties.SingleValuePropertyDescriptor`
  * `net.sourceforge.pmd.properties.StringMultiProperty`
  * `net.sourceforge.pmd.properties.StringProperty`
  * `net.sourceforge.pmd.properties.ValueParser`
  * `net.sourceforge.pmd.properties.ValueParserConstants`
  * `net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.MultiPackagedPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.MultiValuePropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilder`
  * `net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper`
  * `net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder`
  * `net.sourceforge.pmd.properties.builders.SingleNumericPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.SinglePackagedPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.SingleValuePropertyBuilder`
  * `net.sourceforge.pmd.properties.modules.EnumeratedPropertyModule`
  * `net.sourceforge.pmd.properties.modules.NumericPropertyModule`

The following previously deprecated methods have been removed:

* pmd-core
  * `net.sourceforge.pmd.properties.PropertyBuilder.GenericCollectionPropertyBuilder#delim(char)`
  * `net.sourceforge.pmd.properties.PropertySource#setProperty(...)`
  * `net.sourceforge.pmd.properties.internal.PropertyTypeId#factoryFor(...)`
  * `net.sourceforge.pmd.properties.internal.PropertyTypeId#typeIdFor(...)`
  * `net.sourceforge.pmd.properties.PropertyDescriptor`: removed methods errorFor, type, isMultiValue,
    uiOrder, compareTo, isDefinedExternally, valueFrom, asDelimitedString

The following methods have been removed:

* pmd-core
  * {%jdoc core::cpd.CPDConfiguration %}
    * `#sourceCodeFor(File)`, `#postConstruct()`, `#tokenizer()`, `#filenameFilter()` removed
  * {%jdoc core::cpd.Mark %}
    * `#getSourceSlice()`, `#setLineCount(int)`, `#getLineCount()`, `#setSourceCode(SourceCode)` removed
    * `#getBeginColumn()`, `#getBeginLine()`, `#getEndLine()`, `#getEndColumn()` removed
      ➡️ use {%jdoc core::cpd.Mark#getLocation() %} instead
  * {%jdoc core::cpd.Match %}
    * `#LABEL_COMPARATOR` removed
    * `#setMarkSet(...)`, `#setLabel(...)`, `#getLabel()`, `#addTokenEntry(...)` removed
    * `#getSourceCodeSlice()` removed
      ➡️ use {%jdoc !!core::cpd.CPDReport#getSourceCodeSlice(net.sourceforge.pmd.cpd.Mark) %} instead
  * {%jdoc core::cpd.TokenEntry %}
    * `#getEOF()`, `#clearImages()`, `#getIdentifier()`, `#getIndex()`, `#setHashCode(int)` removed
    * `#EOF` removed ➡️ use {%jdoc core::cpd.TokenEntry#isEof() %} instead
  * {%jdoc core::lang.ast.Parser.ParserTask %}
    * `#getFileDisplayName()` removed ➡️ use {%jdoc core::lang.ast.Parser.ParserTask#getFileId() %} instead
      (`getFileId().getAbsolutePath()`)

The following classes have been removed:

* pmd-core
  * `net.sourceforge.pmd.cpd.AbstractLanguage`
  * `net.sourceforge.pmd.cpd.AnyLanguage`
  * `net.sourceforge.pmd.cpd.Language`
  * `net.sourceforge.pmd.cpd.LanguageFactory`
  * `net.sourceforge.pmd.cpd.MatchAlgorithm` (now package private)
  * `net.sourceforge.pmd.cpd.MatchCollector` (now package private)
  * `net.sourceforge.pmd.cpd.SourceCode` (and all inner classes like `FileCodeLoader`, ...)
  * `net.sourceforge.pmd.cpd.token.TokenFilter`

**Moved packages**

* pmd-core
  * {%jdoc core::properties.NumericConstraints %} (old package: `net.sourceforge.pmd.properties.constraints.NumericConstraints`)
  * {%jdoc core::properties.PropertyConstraint %} (old package: `net.sourceforge.pmd.properties.constraints.PropertyConstraint`)
    * not experimental anymore
  * {%jdoc ant::ant.ReportException %} (old package: `net.sourceforge.pmd.cpd`, moved to module `pmd-ant`)
    * it is now a RuntimeException
  * {%jdoc core::cpd.CPDReportRenderer %} (old package: `net.sourceforge.pmd.cpd.renderer`)
  * {%jdoc core::cpd.impl.AntlrTokenFilter %} (old package: `net.sourceforge.pmd.cpd.token`)
  * {%jdoc core::cpd.impl.BaseTokenFilter %} (old package: `net.sourceforge.pmd.cpd.token.internal`)
  * {%jdoc core::cpd.impl.JavaCCTokenFilter %} (old package: `net.sourceforge.pmd.cpd.token`)

**Changed types and other changes**

* pmd-core
  * {%jdoc core::properties.PropertyDescriptor %} is now a class (was an interface)
    and it is not comparable anymore.
  * {%jdoc !!core::AbstractConfiguration#setSourceEncoding(java.nio.charset.Charset) %}
    * previously this method took a simple String for the encoding.
  * {%jdoc core::PMDConfiguration %} and {%jdoc core::cpd.CPDConfiguration %}
    * many getters and setters have been moved to the parent class {%jdoc core::AbstractConfiguration %}
  * {%jdoc !!core::cpd.CPDListener#addedFile(int) %}
    * no `File` parameter anymore
  * {%jdoc !!core::cpd.CPDReport#getNumberOfTokensPerFile() %} returns a `Map` of `FileId,Integer` instead of `String`
  * {%jdoc !!core::cpd.CPDReport#filterMatches(java.util.function.Predicate) %} now takes a `java.util.function.Predicate`
    as parameter
  * {%jdoc core::cpd.CpdLexer %}
    * Note: CpdLexer was previously named Tokenizer.
    * constants are now {%jdoc core::properties.PropertyDescriptor %} instead of `String`,
      to be used as language properties
    * {%jdoc core::cpd.CpdLexer#tokenize(core::lang.document.TextDocument,core::cpd.TokenFactory) %}
      changed parameters. Now takes a {%jdoc core::lang.document.TextDocument %} and a {%jdoc core::cpd.TokenFactory %}
      (instead of `SourceCode` and `Tokens`).
  * {% jdoc core::lang.Language %}
    * method `#createProcessor(LanguagePropertyBundle)` moved to {%jdoc core::lang.PmdCapableLanguage %}
  * {% jdoc !!core::util.StringUtil#linesWithTrimIndent(net.sourceforge.pmd.lang.document.Chars) %} now takes a `Chars`
    instead of a `String`.
* All language modules (like pmd-apex, pmd-cpp, ...)
  * consistent package naming: `net.sourceforge.pmd.lang.<langId>.cpd`
  * adapted to use {% jdoc core::cpd.CpdCapableLanguage %}
  * consistent static method `#getInstance()`
  * removed constants like `ID`, `TERSE_NAME` or `NAME`. Use `getInstance().getName()` etc. instead

**Internal APIs**

* `net.sourceforge.pmd.properties.internal.PropertyTypeId`

**Deprecated API**

* {% jdoc !!core::lang.Language#getTerseName() %} ➡️ use {% jdoc core::lang.Language#getId() %} instead

* The method {%jdoc !!java::lang.java.ast.ASTPattern#getParenthesisDepth() %} has been deprecated and will be removed.
  It was introduced for supporting parenthesized patterns, but that was removed with Java 21. It is only used when
  parsing code as java-19-preview.

**Experimental APIs**

* To support the Java preview language features "String Templates" and "Unnamed Patterns and Variables", the following
  AST nodes have been introduced as experimental:
  * {% jdoc java::lang.java.ast.ASTTemplateExpression %}
  * {% jdoc java::lang.java.ast.ASTTemplate %}
  * {% jdoc java::lang.java.ast.ASTTemplateFragment %}
  * {% jdoc java::lang.java.ast.ASTUnnamedPattern %}
* The AST nodes for supporting "Record Patterns" and "Pattern Matching for switch" are not experimental anymore:
  * {% jdoc java::lang.java.ast.ASTRecordPattern %}
  * {% jdoc java::lang.java.ast.ASTPatternList %} (Note: it was renamed from `ASTComponentPatternList`)
  * {% jdoc java::lang.java.ast.ASTGuard %} (Note: it was renamed from `ASTSwitchGuard`)

#### 7.0.0-rc3

**PMD Distribution**

* The asset filenames of PMD on [GitHub Releases](https://github.com/pmd/pmd/releases) are
  now `pmd-dist-<version>-bin.zip`, `pmd-dist-<version>-src.zip` and `pmd-dist-<version>-doc.zip`.
  Keep that in mind, if you have an automated download script.

  The structure inside the ZIP files stay the same, e.g. we still provide inside the binary distribution
  ZIP file the base directory `pmd-bin-<version>`.

**CLI**

* The CLI option `--stress` (or `-stress`) has been removed without replacement.
* The CLI option `--minimum-priority` was changed with 7.0.0-rc1 to only take the following values:
  High, Medium High, Medium, Medium Low, Low. With 7.0.0-rc2 compatibility has been restored, so that the equivalent
  integer values (1 to 5) are supported as well.

**pmd-core**

* Replaced `RuleViolation::getFilename` with new {% jdoc !!core::reporting.RuleViolation#getFileId() %}, that returns a
  {% jdoc core::lang.document.FileId %}. This is an identifier for a {% jdoc core::lang.document.TextFile %}
  and could represent a path name. This allows to have a separate display name, e.g. renderers use
  {% jdoc core::reporting.FileNameRenderer %} to either display the full path name or a relative path name
  (see {% jdoc !!core::renderers.Renderer#setFileNameRenderer(net.sourceforge.pmd.reporting.FileNameRenderer) %} and
  {%jdoc core::reporting.ConfigurableFileNameRenderer %}). Many places where we used a simple String for
  a path-like name before have been adapted to use the new {% jdoc core::lang.document.FileId %}.

  See [PR #4425](https://github.com/pmd/pmd/pull/4425) for details.

#### 7.0.0-rc2

**Removed classes and methods**

The following previously deprecated classes have been removed:

* pmd-core 
  * `net.sourceforge.pmd.PMD`
  * `net.sourceforge.pmd.cli.PMDCommandLineInterface`
  * `net.sourceforge.pmd.cli.PMDParameters`
  * `net.sourceforge.pmd.cli.PmdParametersParseResult`

**CLI**

* The CLI option `--minimum-priority` was changed with 7.0.0-rc1 to only take the following values:
  High, Medium High, Medium, Medium Low, Low. With 7.0.0-rc2 compatibility has been restored, so that the equivalent
  integer values (1 to 5) are supported as well.

#### 7.0.0-rc1

**CLI**

* The CLI option `--stress` (or `-stress`) has been removed without replacement.
* The CLI option `--minimum-priority` now takes one of the following values instead of an integer:
  High, Medium High, Medium, Medium Low, Low.

#### 6.55.0

**Go**

* The LanguageModule of Go, that only supports CPD execution, has been deprecated. This language
  is not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
  not affected by this change. The following class has been deprecated and will be removed with PMD 7.0.0:
  * {% jdoc go::lang.go.GoLanguageModule %}

**Java**
* Support for Java 18 preview language features have been removed. The version "18-preview" is no longer available.
* The experimental class `net.sourceforge.pmd.lang.java.ast.ASTGuardedPattern` has been removed.

#### 6.54.0

**PMD CLI**

* PMD now supports a new `--relativize-paths-with` flag (or short `-z`), which replaces `--short-names`.
  It serves the same purpose: Shortening the pathnames in the reports. However, with the new flag it's possible
  to explicitly define one or more pathnames that should be used as the base when creating relative paths.
  The old flag `--short-names` is deprecated.

**Deprecated APIs**

**For removal**

* `net.sourceforge.pmd.lang.apex.ast.ApexRootNode#getApexVersion()` has been deprecated for removal. The version returned is
  always `Version.CURRENT`, as the apex compiler integration doesn't use additional information which Apex version
  actually is used. Therefore, this method can't be used to determine the Apex version of the project
  that is being analyzed.
* {% jdoc !!core::cpd.CPDConfiguration#setEncoding(java.lang.String) %} and
  {% jdoc !!core::cpd.CPDConfiguration#getEncoding() %}. Use the methods
  {% jdoc core::AbstractConfiguration#getSourceEncoding() %} and
  {% jdoc core::AbstractConfiguration#setSourceEncoding(java.lang.String) %} instead. Both are available
  for `CPDConfiguration` which extends `AbstractConfiguration`.
* {% jdoc_old test::cli.BaseCLITest %} and {% jdoc_old test::cli.BaseCPDCLITest %} have been deprecated for removal without
  replacement. CLI tests should be done in pmd-core only (and in PMD7 in pmd-cli). Individual language modules
  shouldn't need to test the CLI integration logic again. Instead, the individual language modules should test their
  functionality as unit tests.
* {% jdoc core::cpd.CPDConfiguration.LanguageConverter %}

* {% jdoc !!core::lang.document.FileCollector#addZipFile(java.nio.file.Path) %} has been deprecated. It is replaced
  by {% jdoc !!core::lang.document.FileCollector#addZipFileWithContent(java.nio.file.Path) %} which directly adds the
  content of the zip file for analysis.

* {% jdoc !!core::PMDConfiguration#setReportShortNames(boolean) %} and
  {% jdoc !!core::PMDConfiguration#isReportShortNames() %} have been deprecated for removal.
  Use {% jdoc !!core::PMDConfiguration#addRelativizeRoot(java.nio.file.Path) %} instead.

**Internal APIs**

* {% jdoc core::renderers.CSVWriter %}
* Some fields in {% jdoc test::test.AbstractAntTestHelper %}

**Experimental APIs**

* CPDReport has a new method which limited mutation of a given report:
  * {%jdoc core::cpd.CPDReport#filterMatches(net.sourceforge.pmd.util.Predicate) %} creates a new CPD report
    with some matches removed with a given predicate based filter.

#### 6.53.0

**Deprecated APIs**

**For removal**

These classes / APIs have been deprecated and will be removed with PMD 7.0.0.

* {% jdoc_old java::lang.java.rule.design.ExcessiveLengthRule %} (Java)

#### 6.52.0

**PMD CLI**

* PMD now supports a new `--use-version` flag, which receives a language-version pair (such as `java-8` or `apex-54`).
  This supersedes the usage of `-language` / `-l` and `-version` / `-v`, allowing for multiple versions to be set in a single run.
  PMD 7 will completely remove support for `-language` and `-version` in favor of this new flag.

* Support for `-V` is being deprecated in favor of `--verbose` in preparation for PMD 7.
  In PMD 7, `-v` will enable verbose mode and `-V` will show the PMD version for consistency with most Unix/Linux tools.

* Support for `-min` is being deprecated in favor of `--minimum-priority` for consistency with most Unix/Linux tools, where `-min` would be equivalent to `-m -i -n`.

**CPD CLI**

* CPD now supports using `-d` or `--dir` as an alias to `--files`, in favor of consistency with PMD.
  PMD 7 will remove support for `--files` in favor of these new flags.

**Linux run.sh parameters**

* Using `run.sh cpdgui` will now warn about it being deprecated. Use `run.sh cpd-gui` instead.

* The old designer (`run.sh designerold`) is completely deprecated and will be removed in PMD 7. Switch to the new JavaFX designer: `run.sh designer`.

* The old visual AST viewer (`run.sh bgastviewer`) is completely deprecated and will be removed in PMD 7. Switch to the new JavaFX designer: `run.sh designer` for a visual tool, or use `run.sh ast-dump` for a text-based alternative.

**Deprecated API**

* The following core APIs have been marked as deprecated for removal in PMD 7:
  - {% jdoc_old core::PMD %} and `PMD.StatusCode` - PMD 7 will ship with a revamped CLI split from pmd-core. To programmatically launch analysis you can use {% jdoc core::PmdAnalysis %}.
  - {% jdoc !!core::PMDConfiguration#getAllInputPaths() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getInputPathList() %}
  - {% jdoc !!core::PMDConfiguration#setInputPaths(List) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setInputPathList(List) %}
  - {% jdoc !!core::PMDConfiguration#addInputPath(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#addInputPath(Path) %}
  - {% jdoc !!core::PMDConfiguration#getInputFilePath() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getInputFile() %}
  - {% jdoc !!core::PMDConfiguration#getIgnoreFilePath() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getIgnoreFile() %}
  - {% jdoc !!core::PMDConfiguration#setInputFilePath(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setInputFilePath(Path) %}
  - {% jdoc !!core::PMDConfiguration#setIgnoreFilePath(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setIgnoreFilePath(Path) %}
  - {% jdoc !!core::PMDConfiguration#getInputUri() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getUri() %}
  - {% jdoc !!core::PMDConfiguration#setInputUri(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setInputUri(URI) %}
  - {% jdoc !!core::PMDConfiguration#getReportFile() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getReportFilePath() %}
  - {% jdoc !!core::PMDConfiguration#setReportFile(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setReportFile(Path) %}
  - {% jdoc !!core::PMDConfiguration#isStressTest() %} and {% jdoc !!core::PMDConfiguration#setStressTest(boolean) %} - Will be removed with no replacement.
  - {% jdoc !!core::PMDConfiguration#isBenchmark() %} and {% jdoc !!core::PMDConfiguration#setBenchmark(boolean) %} - Will be removed with no replacement, the CLI will still support it.
  - {% jdoc_old core::cpd.CPD %} and `CPD.StatusCode` - PMD 7 will ship with a revamped CLI split from pmd-core. An alternative to programmatically launch CPD analysis will be added in due time.

* In order to reduce the dependency on Apex Jorje classes, the method {% jdoc_old !!visualforce::lang.vf.DataType#fromBasicType(apex.jorje.semantic.symbol.type.BasicType) %}
  has been deprecated. The equivalent method {% jdoc_old visualforce::lang.vf.DataType#fromTypeName(java.lang.String) %} should be used instead.

#### 6.51.0

No changes.

#### 6.50.0

**CPD CLI**

* CPD now supports the `--ignore-literal-sequences` argument when analyzing Lua code.

#### 6.49.0

**Deprecated API**

* In order to reduce the dependency on Apex Jorje classes, the following methods have been deprecated.
  These methods all leaked internal Jorje enums. These enums have been replaced now by enums the
  PMD's AST package.
  * {% jdoc !!apex::lang.apex.ast.ASTAssignmentExpression#getOperator() %}
  * {% jdoc !!apex::lang.apex.ast.ASTBinaryExpression#getOperator() %}
  * {% jdoc !!apex::lang.apex.ast.ASTBooleanExpression#getOperator() %}
  * {% jdoc !!apex::lang.apex.ast.ASTPostfixExpression#getOperator() %}
  * {% jdoc !!apex::lang.apex.ast.ASTPrefixExpression#getOperator() %}

  All these classes have now a new `getOp()` method. Existing code should be refactored to use this method instead.
  It returns the new enums, like {% jdoc apex::lang.apex.ast.AssignmentOperator %}, and avoids
  the dependency to Jorje.

#### 6.48.0

**CPD CLI**

* CPD has a new CLI option `--debug`. This option has the same behavior as in PMD. It enables more verbose
  logging output.

**Rule Test Framework**

* The module "pmd-test", which contains support classes to write rule tests, now **requires Java 8**. If you depend on
  this module for testing your own custom rules, you'll need to make sure to use at least Java 8.
* The new module "pmd-test-schema" contains now the XSD schema and the code to parse the rule test XML files. The
  schema has been extracted in order to easily share it with other tools like the Rule Designer or IDE plugins.
* Test schema changes:
  * The attribute `isRegressionTest` of `test-code` is deprecated. The new
    attribute `disabled` should be used instead for defining whether a rule test should be skipped or not.
  * The attributes `reinitializeRule` and `useAuxClasspath` of `test-code` are deprecated and assumed true.
    They will not be replaced.
  * The new attribute `focused` of `test-code` allows disabling all tests except the focused one temporarily.
* More information about the rule test framework can be found in the documentation:
  [Testing your rules](pmd_userdocs_extending_testing.html)

**Deprecated API**

* The experimental Java AST class `net.sourceforge.pmd.lang.java.ast.ASTGuardedPattern` has been deprecated and
  will be removed. It was introduced for Java 17 and Java 18 Preview as part of pattern matching for switch,
  but it is no longer supported with Java 19 Preview.
* The interface {% jdoc_old core::cpd.renderer.CPDRenderer %} is deprecated. For custom CPD renderers
  the new interface {% jdoc core::cpd.CPDReportRenderer %} should be used.
* The class {% jdoc_old test::testframework.TestDescriptor %} is deprecated, replaced with {% jdoc test-schema::test.schema.RuleTestDescriptor %}.
* Many methods of {% jdoc test::test.RuleTst %} have been deprecated as internal API.

**Experimental APIs**

* To support the Java preview language features "Pattern Matching for Switch" and "Record Patterns", the following
  AST nodes have been introduced as experimental:
  * {% jdoc_old java::lang.java.ast.ASTSwitchGuard %}
  * {% jdoc_old java::lang.java.ast.ASTRecordPattern %}
  * {% jdoc_old java::lang.java.ast.ASTComponentPatternList %}

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* {%jdoc_old !!core::cpd.CPDConfiguration#setRenderer(net.sourceforge.pmd.cpd.Renderer) %}
* {%jdoc_old !!core::cpd.CPDConfiguration#setCPDRenderer(net.sourceforge.pmd.cpd.renderer.CPDRenderer) %}
* {%jdoc_old !!core::cpd.CPDConfiguration#getRenderer() %}
* {%jdoc_old !!core::cpd.CPDConfiguration#getCPDRenderer() %}
* {%jdoc_old !!core::cpd.CPDConfiguration#getRendererFromString(java.lang.String,java.lang.String) %}
* {%jdoc_old !!core::cpd.CPDConfiguration#getCPDRendererFromString(java.lang.String,java.lang.String) %}
* {%jdoc_old core::cpd.renderer.CPDRendererAdapter %}

#### 6.47.0

No changes.

#### 6.46.0

**Deprecated ruleset references**

Ruleset references with the following formats are now deprecated and will produce a warning
when used on the CLI or in a ruleset XML file:
- `<lang-name>-<ruleset-name>`, eg `java-basic`, which resolves to `rulesets/java/basic.xml`
- the internal release number, eg `600`, which resolves to `rulesets/releases/600.xml`

Use the explicit forms of these references to be compatible with PMD 7.

**Deprecated API**

- {% jdoc_old core::RuleSetReferenceId#toString() %} is now deprecated. The format of this
  method will remain the same until PMD 7. The deprecation is intended to steer users
  away from relying on this format, as it may be changed in PMD 7.
- {% jdoc_old core::PMDConfiguration#getInputPaths() %} and
  {% jdoc_old core::PMDConfiguration#setInputPaths(java.lang.String) %} are now deprecated.
  A new set of methods have been added, which use lists and do not rely on comma splitting.

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

- {% jdoc_old core::cpd.CPDCommandLineInterface %} has been internalized. In order to execute CPD either
  {% jdoc_old !!core::cpd.CPD#run(java.lang.String...) %} or {% jdoc_old !!core::cpd.CPD#main(java.lang.String[]) %}
  should be used.
- Several members of {% jdoc_old test::cli.BaseCPDCLITest %} have been deprecated with replacements.
- The methods {% jdoc_old !!core::ant.Formatter#start(java.lang.String) %},
  {% jdoc_old !!core::ant.Formatter#end(net.sourceforge.pmd.reporting.Report) %}, {% jdoc_old !!core::ant.Formatter#getRenderer() %},
  and {% jdoc_old !!core::ant.Formatter#isNoOutputSupplied() %} have been internalized.

#### 6.45.0

**Experimental APIs**

* Report has two new methods which allow limited mutations of a given report:
  * {% jdoc !!core::reporting.Report#filterViolations(java.util.function.Predicate) %} creates a new report with
    some violations removed with a given predicate based filter.
  * {% jdoc !!core::reporting.Report#union(net.sourceforge.pmd.reporting.Report) %} can combine two reports into a single new Report.
* {% jdoc_old !!core::util.Predicate %} will be replaced in PMD7 with the standard Predicate interface from java8.
* The module `pmd-html` is entirely experimental right now. Anything in the package
  `net.sourceforge.pmd.lang.html` should be used cautiously.

#### 6.44.0

**Deprecated API**

* Several members of {% jdoc_old core::PMD %} have been newly deprecated, including:
  - `PMD#EOL`: use `System#lineSeparator()`
  - `PMD#SUPPRESS_MARKER`: use {% jdoc core::PMDConfiguration#DEFAULT_SUPPRESS_MARKER %}
  - `PMD#processFiles`: use the new programmatic API
  - `PMD#getApplicableFiles`: is internal
* {% jdoc !!core::PMDConfiguration#prependClasspath(java.lang.String) %} is deprecated
  in favour of {% jdoc core::PMDConfiguration#prependAuxClasspath(java.lang.String) %}.
* {% jdoc !!core::PMDConfiguration#setRuleSets(java.lang.String) %} and
  {% jdoc core::PMDConfiguration#getRuleSets() %} are deprecated. Use instead
  {% jdoc core::PMDConfiguration#setRuleSets(java.util.List) %},
  {% jdoc core::PMDConfiguration#addRuleSet(java.lang.String) %},
  and {% jdoc core::PMDConfiguration#getRuleSetPaths() %}.
* Several members of {% jdoc_old test::cli.BaseCLITest %} have been deprecated with replacements.
* Several members of {% jdoc_old core::cli.PMDCommandLineInterface %} have been explicitly deprecated.
  The whole class however was deprecated long ago already with 6.30.0. It is internal API and should
  not be used.

* In modelica, the rule classes {% jdoc_old modelica::lang.modelica.rule.AmbiguousResolutionRule %}
  and {% jdoc_old modelica::lang.modelica.rule.ConnectUsingNonConnector %} have been deprecated,
  since they didn't comply to the usual rule class naming conventions yet.
  The replacements are in the subpackage `bestpractices`.

**Experimental APIs**

*   Together with the new programmatic API the interface
    {% jdoc core::lang.document.TextFile %} has been added as *experimental*. It intends
    to replace {% jdoc_old core::util.datasource.DataSource %} and {% jdoc_old core::cpd.SourceCode %} in the long term.

    This interface will change in PMD 7 to support read/write operations
    and other things. You don't need to use it in PMD 6, as {% jdoc core::lang.document.FileCollector %}
    decouples you from this. A file collector is available through {% jdoc !!core::PmdAnalysis#files() %}.

#### 6.43.0

**Deprecated API**

Some API deprecations were performed in core PMD classes, to improve compatibility with PMD 7.
- {% jdoc_old core::Report %}: the constructor and other construction methods like addViolation or createReport
- {% jdoc_old core::RuleContext %}: all constructors, getters and setters. A new set
  of stable methods, matching those in PMD 7, was added to replace the `addViolation`
  overloads of {% jdoc_old core::lang.rule.AbstractRule %}. In PMD 7, `RuleContext` will
  be the API to report violations, and it can already be used as such in PMD 6.
- The field {% jdoc_old core::PMD#configuration %} is unused and will be removed.

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

- {% jdoc_old core::RuleSet %}: methods that serve to apply rules, including `apply`, `start`, `end`, `removeDysfunctionalRules`
- {% jdoc_old !!core::renderers.AbstractAccumulatingRenderer#renderFileReport(Report) %} is internal API
  and should not be overridden in own renderers.

**Changed API**

It is now forbidden to report a violation:
- With a `null` node
- With a `null` message
- With a `null` set of format arguments (prefer a zero-length array)

Note that the message is set from the XML rule declaration, so this is only relevant
if you instantiate rules manually.

{% jdoc_old core::RuleContext %} now requires setting the current rule before calling
{% jdoc_old core::Rule#apply(java.util.List, core::RuleContext) %}. This is
done automatically by `RuleSet#apply` and such. Creating and configuring a
`RuleContext` manually is strongly advised against, as the lifecycle of `RuleContext`
will change drastically in PMD 7.

#### 6.42.0

No changes.

#### 6.41.0

**Command Line Interface**

The command line options for PMD and CPD now use GNU-syle long options format. E.g. instead of `-rulesets` the
preferred usage is now `--rulesets`. Alternatively one can still use the short option `-R`.
Some options also have been renamed to a more consistent casing pattern at the same time
(`--fail-on-violation` instead of `-failOnViolation`).
The old single-dash options are still supported but are deprecated and will be removed with PMD 7.
This change makes the command line interface more consistent within PMD and also less surprising
compared to other cli tools.

The changes in detail for PMD:

| old option                | new option                   |
|---------------------------|------------------------------|
| `-rulesets`               | `--rulesets` (or `-R`)       |
| `-uri`                    | `--uri`                      |
| `-dir`                    | `--dir` (or `-d`)            |
| `-filelist`               | `--file-list`                |
| `-ignorelist`             | `--ignore-list`              |
| `-format`                 | `--format` (or `-f`)         |
| `-debug`                  | `--debug`                    |
| `-verbose`                | `--verbose`                  |
| `-help`                   | `--help`                     |
| `-encoding`               | `--encoding`                 |
| `-threads`                | `--threads`                  |
| `-benchmark`              | `--benchmark`                |
| `-stress`                 | `--stress`                   |
| `-shortnames`             | `--short-names`              |
| `-showsuppressed`         | `--show-suppressed`          |
| `-suppressmarker`         | `--suppress-marker`          |
| `-minimumpriority`        | `--minimum-priority`         |
| `-property`               | `--property`                 |
| `-reportfile`             | `--report-file`              |
| `-force-language`         | `--force-language`           |
| `-auxclasspath`           | `--aux-classpath`            |
| `-failOnViolation`        | `--fail-on-violation`        |
| `--failOnViolation`       | `--fail-on-violation`        |
| `-norulesetcompatibility` | `--no-ruleset-compatibility` |
| `-cache`                  | `--cache`                    |
| `-no-cache`               | `--no-cache`                 |

The changes in detail for CPD:

| old option          | new option            |
|---------------------|-----------------------|
| `--failOnViolation` | `--fail-on-violation` |
| `-failOnViolation`  | `--fail-on-violation` |
| `--filelist`        | `--file-list`         |

#### 6.40.0

**Experimental APIs**

*   The interface {% jdoc apex::lang.apex.ast.ASTCommentContainer %} has been added to the Apex AST.
    It provides a way to check whether a node contains at least one comment. Currently this is only implemented for
    {% jdoc apex::lang.apex.ast.ASTCatchBlockStatement %} and used by the rule
    {% rule apex/errorprone/EmptyCatchBlock %}.
    This information is also available via XPath attribute `@ContainsComment`.

#### 6.39.0

No changes.

#### 6.38.0

No changes.

#### 6.37.0

**PMD CLI**

*   PMD has a new CLI option `-force-language`. With that a language can be forced to be used for all input files,
    irrespective of filenames. When using this option, the automatic language selection by extension is disabled
    and all files are tried to be parsed with the given language. Parsing errors are ignored and unparsable files
    are skipped.

    This option allows to use the xml language for files, that don't use xml as extension.
    See also the examples on [PMD CLI reference](pmd_userdocs_cli_reference.html#analyze-other-xml-formats).

**Experimental APIs**

*   The AST types and APIs around Sealed Classes are not experimental anymore:
  *   {% jdoc !!java::lang.java.ast.ASTClassDeclaration#isSealed() %},
      {% jdoc !!java::lang.java.ast.ASTClassDeclaration#isNonSealed() %},
      {% jdoc !!java::lang.java.ast.ASTClassDeclaration#getPermittedSubclasses() %}
  *   {% jdoc java::lang.java.ast.ASTPermitsList %}

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   The inner class {% jdoc_old !!core::cpd.TokenEntry.State %} is considered to be internal API.
    It will probably be moved away with PMD 7.

#### 6.36.0

No changes.

#### 6.35.0

**Deprecated API**

*   {% jdoc_old !!core::PMD#doPMD(net.sourceforge.pmd.PMDConfiguration) %} is deprecated.
    Use {% jdoc_old !!core::PMD#runPMD(net.sourceforge.pmd.PMDConfiguration) %} instead.
*   {% jdoc_old !!core::PMD#run(java.lang.String[]) %} is deprecated.
    Use {% jdoc_old !!core::PMD#runPMD(java.lang.String...) %} instead.
*   {% jdoc_old core::ThreadSafeReportListener %} and the methods to use them in {% jdoc_old core::Report %}
    ({% jdoc_old core::Report#addListener(net.sourceforge.pmd.ThreadSafeReportListener) %},
    {% jdoc_old core::Report#getListeners() %}, {% jdoc_old core::Report#addListeners(java.util.List) %})
    are deprecated. This functionality will be replaced by another TBD mechanism in PMD 7.

#### 6.34.0

No changes.

#### 6.33.0

No changes.

#### 6.32.0

**Experimental APIs**

*   The experimental class `ASTTypeTestPattern` has been renamed to {% jdoc java::lang.java.ast.ASTTypePattern %}
    in order to align the naming to the JLS.
*   The experimental class `ASTRecordConstructorDeclaration` has been renamed to {% jdoc java::lang.java.ast.ASTCompactConstructorDeclaration %}
    in order to align the naming to the JLS.
*   The AST types and APIs around Pattern Matching and Records are not experimental anymore:
  *   {% jdoc !!java::lang.java.ast.ASTVariableId#isPatternBinding() %}
  *   {% jdoc java::lang.java.ast.ASTPattern %}
  *   {% jdoc java::lang.java.ast.ASTTypePattern %}
  *   {% jdoc java::lang.java.ast.ASTRecordDeclaration %}
  *   {% jdoc java::lang.java.ast.ASTRecordComponentList %}
  *   {% jdoc java::lang.java.ast.ASTRecordComponent %}
  *   {% jdoc java::lang.java.ast.ASTRecordBody %}
  *   {% jdoc java::lang.java.ast.ASTCompactConstructorDeclaration %}

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   The protected or public member of the Java rule {% jdoc java::lang.java.rule.bestpractices.AvoidUsingHardCodedIPRule %}
    are deprecated and considered to be internal API. They will be removed with PMD 7.

#### 6.31.0

**Deprecated API**

*   {% jdoc_old xml::lang.xml.rule.AbstractDomXmlRule %}
*   {% jdoc_old xml::lang.wsdl.rule.AbstractWsdlRule %}
*   A few methods of {% jdoc_old xml::lang.xml.rule.AbstractXmlRule %}

**Experimental APIs**

*   The method {% jdoc !!core::lang.ast.GenericToken#getKind() %} has been added as experimental. This
    unifies the token interface for both JavaCC and Antlr. The already existing method
    {% jdoc !!core::lang.ast.impl.antlr4.AntlrToken#getKind() %} is therefore experimental as well. The
    returned constant depends on the actual language and might change whenever the grammar
    of the language is changed.

#### 6.30.0

**Deprecated API**

**Around RuleSet parsing**

* {% jdoc_old core::RuleSetFactory %} and {% jdoc_old core::RulesetsFactoryUtils %} have been deprecated in favor of {% jdoc core::lang.rule.RuleSetLoader %}. This is easier to configure, and more maintainable than the multiple overloads of `RulesetsFactoryUtils`.
* Some static creation methods have been added to {% jdoc core::lang.rule.RuleSet %} for simple cases, eg {% jdoc core::lang.rule.RuleSet#forSingleRule(core::Rule) %}. These replace some counterparts in {% jdoc_old core::RuleSetFactory %}
* Since {% jdoc_old core::RuleSets %} is also deprecated, many APIs that require a RuleSets instance now are deprecated, and have a counterpart that expects a `List<RuleSet>`.
* {% jdoc_old core::RuleSetReferenceId %}, {% jdoc_old core::RuleSetReference %}, {% jdoc_old core::RuleSetFactoryCompatibility %} are deprecated. They are most likely not relevant outside of the implementation of pmd-core.

**Around the `PMD` class**

Many classes around PMD's entry point ({% jdoc_old core::PMD %}) have been deprecated as internal, including:
* The contents of the packages `net.sourceforge.pmd.cli` in pmd-core, `net.sourceforge.pmd.processor`
* {% jdoc_old core::SourceCodeProcessor %}
* The constructors of {% jdoc_old core::PMD %} (the class will be made a utility class)

**Miscellaneous**

*   {% jdoc_old !!java::lang.java.ast.ASTPackageDeclaration#getPackageNameImage() %},
    {% jdoc_old !!java::lang.java.ast.ASTTypeParameter#getParameterName() %}
    and the corresponding XPath attributes. In both cases they're replaced with a new method `getName`,
    the attribute is `@Name`.
*   {% jdoc_old !!java::lang.java.ast.ASTClassOrInterfaceBody#isAnonymousInnerClass() %},
    and {% jdoc_old !!java::lang.java.ast.ASTClassOrInterfaceBody#isEnumChild() %},
    refs [#905](https://github.com/pmd/pmd/issues/905)

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc_old !!javascript::lang.ecmascript.Ecmascript3Handler %}
*   {% jdoc_old !!javascript::lang.ecmascript.Ecmascript3Parser %}
*   {% jdoc_old !!javascript::lang.ecmascript.ast.EcmascriptParser#parserOptions %}
*   {% jdoc_old !!javascript::lang.ecmascript.ast.EcmascriptParser#getSuppressMap() %}
*   {% jdoc_old !!core::lang.rule.ParametricRuleViolation %}
*   {% jdoc_old !!core::lang.ParserOptions#suppressMarker %}
*   {% jdoc_old !!modelica::lang.modelica.rule.ModelicaRuleViolationFactory %}

#### 6.29.0

No changes.

#### 6.28.0

**Deprecated API**

**For removal**

* {% jdoc_old !!core::RuleViolationComparator %}. Use {% jdoc !!core::reporting.RuleViolation#DEFAULT_COMPARATOR %} instead.
* {% jdoc_old !!core::cpd.AbstractTokenizer %}. Use {% jdoc !!core::cpd.AnyCpdLexer %} instead (previously called AnyTokenizer).
* {% jdoc_old !!fortran::cpd.FortranTokenizer %}. Was replaced by an {% jdoc core::cpd.AnyCpdLexer %}. Use {% jdoc !!fortran::lang.fortran.FortranLanguageModule#createCpdLexer(core::lang.LanguagePropertyBundle) %} anyway.
* {% jdoc_old !!perl::cpd.PerlTokenizer %}. Was replaced by an {% jdoc core::cpd.AnyCpdLexer %}. Use {% jdoc !!perl::lang.perl.PerlLanguageModule#createCpdLexer(core::lang.LanguagePropertyBundle) %} anyway.
* {% jdoc_old !!ruby::cpd.RubyTokenizer %}. Was replaced by an {% jdoc core::cpd.AnyCpdLexer %}. Use {% jdoc !!ruby::lang.ruby.RubyLanguageModule#createCpdLexer(core::lang.LanguagePropertyBundle) %} anyway.
* {% jdoc_old !!core::lang.rule.RuleReference#getOverriddenLanguage() %} and
  {% jdoc_old !!core::lang.rule.RuleReference#setLanguage(net.sourceforge.pmd.lang.Language) %}
* Antlr4 generated lexers:
  * {% jdoc_old !!cs::lang.cs.antlr4.CSharpLexer %} will be moved to package `net.sourceforge.pmd.lang.cs.ast` with PMD 7.
  * {% jdoc_old !!dart::lang.dart.antlr4.Dart2Lexer %} will be renamed to `DartLexer` and moved to package
    `net.sourceforge.pmd.lang.dart.ast` with PMD 7. All other classes in the old package will be removed.
  * {% jdoc_old !!go::lang.go.antlr4.GolangLexer %} will be moved to package
    `net.sourceforge.pmd.lang.go.ast` with PMD 7. All other classes in the old package will be removed.
  * {% jdoc_old !!kotlin::lang.kotlin.antlr4.Kotlin %} will be renamed to `KotlinLexer` and moved to package
    `net.sourceforge.pmd.lang.kotlin.ast` with PMD 7.
  * {% jdoc_old !!lua::lang.lua.antlr4.LuaLexer %} will be moved to package
    `net.sourceforge.pmd.lang.lua.ast` with PMD 7. All other classes in the old package will be removed.

#### 6.27.0

*   XML rule definition in rulesets: In PMD 7, the `language` attribute will be required on all `rule`
    elements that declare a new rule. Some base rule classes set the language implicitly in their
    constructor, and so this is not required in all cases for the rule to work. But this
    behavior will be discontinued in PMD 7, so missing `language` attributes are now
    reported as a forward compatibility warning.

**Deprecated API**

**For removal**

*   {% jdoc_old !!core::Rule#getParserOptions() %}
*   {% jdoc_old !!core::lang.Parser#getParserOptions() %}
*   {% jdoc_old core::lang.AbstractParser %}
*   {% jdoc_old !!core::RuleContext#removeAttribute(java.lang.String) %}
*   {% jdoc_old !!core::RuleContext#getAttribute(java.lang.String) %}
*   {% jdoc_old !!core::RuleContext#setAttribute(java.lang.String, java.lang.Object) %}
*   {% jdoc_old apex::lang.apex.ApexParserOptions %}
*   {% jdoc_old !!java::lang.java.ast.ASTThrowStatement#getFirstClassOrInterfaceTypeImage() %}
*   {% jdoc_old javascript::lang.ecmascript.EcmascriptParserOptions %}
*   {% jdoc_old javascript::lang.ecmascript.rule.EcmascriptXPathRule %}
*   {% jdoc_old xml::lang.xml.XmlParserOptions %}
*   {% jdoc_old xml::lang.xml.rule.XmlXPathRule %}
*   Properties of {% jdoc_old xml::lang.xml.rule.AbstractXmlRule %}

*   {% jdoc_old !!core::Report.ReadableDuration %}
*   Many methods of {% jdoc_old !!core::Report %}. They are replaced by accessors
    that produce a List. For example, {% jdoc_old !a!core::Report#iterator() %}
    (and implementing Iterable) and {% jdoc_old !a!core::Report#isEmpty() %} are both
    replaced by {% jdoc !a!core::reporting.Report#getViolations() %}.

*   The dataflow codebase is deprecated for removal in PMD 7. This
    includes all code in the following packages, and their subpackages:
  *   `net.sourceforge.pmd.lang.plsql.dfa`
  *   `net.sourceforge.pmd.lang.java.dfa`
  *   `net.sourceforge.pmd.lang.dfa`
  *   and the class {% jdoc_old plsql::lang.plsql.PLSQLDataFlowHandler %}

*   {% jdoc_old visualforce::lang.vf.VfSimpleCharStream %}

*   {% jdoc_old jsp::lang.jsp.ast.ASTJspDeclarations %}
*   {% jdoc_old jsp::lang.jsp.ast.ASTJspDocument %}
*   {% jdoc_old !!scala::lang.scala.ast.ScalaParserVisitorAdapter#zero() %}
*   {% jdoc_old !!scala::lang.scala.ast.ScalaParserVisitorAdapter#combine(Object, Object) %}
*   {% jdoc_old apex::lang.apex.ast.ApexParserVisitorReducedAdapter %}
*   {% jdoc_old java::lang.java.ast.JavaParserVisitorReducedAdapter %}

* {% jdoc_old java::lang.java.typeresolution.TypeHelper %} is deprecated in
  favor of {% jdoc java::lang.java.types.TypeTestUtil %}, which has the
  same functionality, but a slightly changed API.
* Many of the classes in `net.sourceforge.pmd.lang.java.symboltable`
  are deprecated as internal API.

#### 6.26.0

**Deprecated API**

**For removal**

* {% jdoc_old core::lang.rule.RuleChainVisitor %} and all implementations in language modules
* {% jdoc_old core::lang.rule.AbstractRuleChainVisitor %}
* {% jdoc_old !!core::lang.Language#getRuleChainVisitorClass() %}
* {% jdoc_old !!core::lang.BaseLanguageModule#<init>(java.lang.String,java.lang.String,java.lang.String,java.lang.Class,java.lang.String...) %}
* {% jdoc_old core::lang.rule.ImportWrapper %}

#### 6.25.0

*   The maven module `net.sourceforge.pmd:pmd-scala` is deprecated. Use `net.sourceforge.pmd:pmd-scala_2.13`
    or `net.sourceforge.pmd:pmd-scala_2.12` instead.

*   Rule implementation classes are internal API and should not be used by clients directly.
    The rules should only be referenced via their entry in the corresponding category ruleset
    (e.g. `<rule ref="category/java/bestpractices.xml/AbstractClassWithoutAbstractMethod" />`).

    While we definitely won't move or rename the rule classes in PMD 6.x, we might consider changes
    in PMD 7.0.0 and onwards.

**Deprecated APIs**

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc_old java::lang.java.rule.AbstractIgnoredAnnotationRule %} (Java)
*   {% jdoc_old java::lang.java.rule.AbstractInefficientZeroCheck %} (Java)
*   {% jdoc_old java::lang.java.rule.AbstractJUnitRule %} (Java)
*   {% jdoc_old java::lang.java.rule.AbstractJavaMetricsRule %} (Java)
*   {% jdoc_old java::lang.java.rule.AbstractLombokAwareRule %} (Java)
*   {% jdoc_old java::lang.java.rule.AbstractPoorMethodCall %} (Java)
*   {% jdoc_old java::lang.java.rule.bestpractices.AbstractSunSecureRule %} (Java)
*   {% jdoc_old java::lang.java.rule.design.AbstractNcssCountRule %} (Java)
*   {% jdoc_old java::lang.java.rule.documentation.AbstractCommentRule %} (Java)
*   {% jdoc_old java::lang.java.rule.performance.AbstractOptimizationRule %} (Java)
*   {% jdoc_old java::lang.java.rule.regex.RegexHelper %} (Java)
*   {% jdoc_old apex::lang.apex.rule.AbstractApexUnitTestRule %} (Apex)
*   {% jdoc_old apex::lang.apex.rule.design.AbstractNcssCountRule %} (Apex)
*   {% jdoc_old plsql::lang.plsql.rule.design.AbstractNcssCountRule %} (PLSQL)
*   {% jdoc_old apex::lang.apex.ApexParser %}
*   {% jdoc_old apex::lang.apex.ApexHandler %}
*   {% jdoc_old core::RuleChain %}
*   {% jdoc_old core::RuleSets %}
*   {% jdoc_old !!core::RulesetsFactoryUtils#getRuleSets(java.lang.String, net.sourceforge.pmd.RuleSetFactory) %}

**For removal**

*   {% jdoc_old !!core::cpd.TokenEntry#TokenEntry(java.lang.String, java.lang.String, int) %}
*   {% jdoc_old test::testframework.AbstractTokenizerTest %}. Use CpdTextComparisonTest in module pmd-lang-test instead.
    For details see
    [Testing your implementation](pmd_devdocs_major_adding_new_cpd_language.html#testing-your-implementation)
    in the developer documentation.
*   {% jdoc_old !!apex::lang.apex.ast.ASTAnnotation#suppresses(core::Rule) %} (Apex)
*   {% jdoc_old apex::lang.apex.rule.ApexXPathRule %} (Apex)
*   {% jdoc_old java::lang.java.rule.SymbolTableTestRule %} (Java)
*   {% jdoc_old !!java::lang.java.rule.performance.InefficientStringBufferingRule#isInStringBufferOperation(net.sourceforge.pmd.lang.ast.Node, int, java.lang.String) %}

#### 6.24.0

**Deprecated APIs**

*   {% jdoc_old !ca!core::lang.BaseLanguageModule#addVersion(String, LanguageVersionHandler, boolean) %}
*   Some members of {% jdoc_old core::lang.ast.TokenMgrError %}, in particular, a new constructor is available
    that should be preferred to the old ones
*   {% jdoc_old core::lang.antlr.AntlrTokenManager.ANTLRSyntaxError %}

**Experimental APIs**

**Note:** Experimental APIs are identified with the annotation {% jdoc core::annotation.Experimental %},
see its javadoc for details

* The experimental methods in {% jdoc_old !ca!core::lang.BaseLanguageModule %} have been replaced by a
  definitive API.

#### 6.23.0

**Deprecated APIs**

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc_old core::lang.rule.xpath.AbstractXPathRuleQuery %}
*   {% jdoc_old core::lang.rule.xpath.JaxenXPathRuleQuery %}
*   {% jdoc_old core::lang.rule.xpath.SaxonXPathRuleQuery %}
*   {% jdoc_old core::lang.rule.xpath.XPathRuleQuery %}

**In ASTs**

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated in the **Apex**, **Javascript**, **PL/SQL**, **Scala** and **Visualforce** ASTs:

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
  *   In the meantime you should use interfaces like {% jdoc visualforce::lang.visualforce.ast.VfNode %} or
      {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package,
      to refer to nodes generically.
  *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The implementation classes of {% jdoc core::lang.ast.Parser %} (eg {% jdoc_old visualforce::lang.vf.VfParser %}) are deprecated and should not be used directly.
    Use {% jdoc !!core::lang.LanguageVersionHandler#getParser(ParserOptions) %} instead.
*   The implementation classes of {% jdoc core::lang.TokenManager %} (eg {% jdoc_old visualforce::lang.vf.VfTokenManager %}) are deprecated and should not be used outside of our implementation.
    **This also affects CPD-only modules**.

These deprecations are added to the following language modules in this release.
Please look at the package documentation to find out the full list of deprecations.
* Apex: `net.sourceforge.pmd.lang.apex.ast`
* Javascript: `net.sourceforge.pmd.lang.ecmascript.ast`
* PL/SQL: `net.sourceforge.pmd.lang.plsql.ast`
* Scala: `net.sourceforge.pmd.lang.scala.ast`
* Visualforce: `net.sourceforge.pmd.lang.vf.ast`

These deprecations have already been rolled out in a previous version for the
following languages:
* Java: `net.sourceforge.pmd.lang.java.ast`
* Java Server Pages: `net.sourceforge.pmd.lang.jsp.ast`
* Velocity Template Language: `net.sourceforge.pmd.lang.vm.ast`

Outside of these packages, these changes also concern the following TokenManager
implementations, and their corresponding Parser if it exists (in the same package):

*   {% jdoc_old cpp::lang.cpp.CppTokenManager %}
*   {% jdoc_old java::lang.java.JavaTokenManager %}
*   {% jdoc_old javascript::lang.ecmascript5.Ecmascript5TokenManager %}
*   {% jdoc_old jsp::lang.jsp.JspTokenManager %}
*   {% jdoc_old matlab::lang.matlab.MatlabTokenManager %}
*   {% jdoc_old modelica::lang.modelica.ModelicaTokenManager %}
*   {% jdoc_old objectivec::lang.objectivec.ObjectiveCTokenManager %}
*   {% jdoc_old plsql::lang.plsql.PLSQLTokenManager %}
*   {% jdoc_old python::lang.python.PythonTokenManager %}
*   {% jdoc_old visualforce::lang.vf.VfTokenManager %}
*   {% jdoc_old velocity::lang.vm.VmTokenManager %}


In the **Java AST** the following attributes are deprecated and will issue a warning when used in XPath rules:

*   {% jdoc_old !!java::lang.java.ast.ASTAdditiveExpression#getImage() %} - use `getOperator()` instead
*   {% jdoc_old !!java::lang.java.ast.ASTVariableDeclaratorId#getImage() %} - use `getName()` instead
*   {% jdoc_old !!java::lang.java.ast.ASTVariableDeclaratorId#getVariableName() %} - use `getName()` instead

**For removal**

*   {% jdoc_old !!core::lang.Parser#getTokenManager(java.lang.String,java.io.Reader) %}
*   {% jdoc_old !!core::lang.TokenManager#setFileName(java.lang.String) %}
*   {% jdoc_old !!core::lang.ast.AbstractTokenManager#setFileName(java.lang.String) %}
*   {% jdoc_old !!core::lang.ast.AbstractTokenManager#getFileName(java.lang.String) %}
*   {% jdoc_old !!core::cpd.token.AntlrToken#getType() %} - use `getKind()` instead.
*   {% jdoc_old core::lang.rule.ImmutableLanguage %}
*   {% jdoc_old core::lang.rule.MockRule %}
*   {% jdoc_old !!core::lang.ast.Node#getFirstParentOfAnyType(java.lang.Class[]) %}
*   {% jdoc_old !!core::lang.ast.Node#getAsDocument() %}
*   {% jdoc_old !!core::lang.ast.AbstractNode#hasDescendantOfAnyType(java.lang.Class[]) %}
*   {% jdoc_old !!java::lang.java.ast.ASTRecordDeclaration#getComponentList() %}
*   Multiple fields, constructors and methods in {% jdoc_old core::lang.rule.XPathRule %}. See javadoc for details.

#### 6.22.0

**Deprecated APIs**

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* {% jdoc_old java::lang.java.JavaLanguageHandler %}
* {% jdoc_old java::lang.java.JavaLanguageParser %}
* {% jdoc_old java::lang.java.JavaDataFlowHandler %}
* Implementations of {% jdoc_old core::lang.rule.RuleViolationFactory %} in each
  language module, eg {% jdoc_old java::lang.java.rule.JavaRuleViolationFactory %}.
  See javadoc of {% jdoc_old core::lang.rule.RuleViolationFactory %}.
* Implementations of {% jdoc_old core::RuleViolation %} in each language module,
  eg {% jdoc_old java::lang.java.rule.JavaRuleViolation %}. See javadoc of
  {% jdoc_old core::RuleViolation %}.

* {% jdoc_old core::rules.RuleFactory %}
* {% jdoc_old core::rules.RuleBuilder %}
* Constructors of {% jdoc_old core::RuleSetFactory %}, use factory methods from {% jdoc_old core::RulesetsFactoryUtils %} instead
* {% jdoc_old core::RulesetsFactoryUtils#getRulesetFactory(core::PMDConfiguration, core::util.ResourceLoader) %}

* {% jdoc_old apex::lang.apex.ast.AbstractApexNode %}
* {% jdoc_old apex::lang.apex.ast.AbstractApexNodeBase %}, and the related `visit`
  methods on {% jdoc_old apex::lang.apex.ast.ApexParserVisitor %} and its implementations.
  Use {% jdoc apex::lang.apex.ast.ApexNode %} instead, now considers comments too.

**For removal**

* pmd-core
  * {% jdoc_old core::lang.dfa.DFAGraphRule %} and its implementations
  * {% jdoc_old core::lang.dfa.DFAGraphMethod %}
  * Many methods on the {% jdoc_old core::lang.ast.Node %} interface
    and {% jdoc_old core::lang.ast.AbstractNode %} base class. See their javadoc for details.
  * {% jdoc !!core::lang.ast.Node#isFindBoundary() %} is deprecated for XPath queries.
  * Many APIs of `net.sourceforge.pmd.lang.metrics`, though most of them were internal and
    probably not used directly outside of PMD. Use {% jdoc core::lang.metrics.MetricsUtil %} as
    a replacement for the language-specific façades too.
  * {% jdoc_old core::lang.ast.QualifiableNode %}, {% jdoc_old core::lang.ast.QualifiedName %}
* pmd-java
  * {% jdoc_old java::lang.java.AbstractJavaParser %}
  * {% jdoc_old java::lang.java.AbstractJavaHandler %}
  * [`ASTAnyTypeDeclaration.TypeKind`](https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
  * {% jdoc_old !!java::lang.java.ast.ASTAnyTypeDeclaration#getKind() %}
  * {% jdoc_old java::lang.java.ast.JavaQualifiedName %}
  * {% jdoc_old !!java::lang.java.ast.ASTCatchStatement#getBlock() %}
  * {% jdoc_old !!java::lang.java.ast.ASTCompilationUnit#declarationsAreInDefaultPackage() %}
  * {% jdoc_old java::lang.java.ast.JavaQualifiableNode %}
    * {% jdoc_old !!java::lang.java.ast.ASTAnyTypeDeclaration#getQualifiedName() %}
    * {% jdoc_old !!java::lang.java.ast.ASTMethodOrConstructorDeclaration#getQualifiedName() %}
    * {% jdoc_old !!java::lang.java.ast.ASTLambdaExpression#getQualifiedName() %}
  * [`net.sourceforge.pmd.lang.java.qname`](https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/qname/package-summary.html) and its contents
  * {% jdoc_old java::lang.java.ast.MethodLikeNode %}
    * Its methods will also be removed from its implementations,
      {% jdoc_old java::lang.java.ast.ASTMethodOrConstructorDeclaration %},
      {% jdoc_old java::lang.java.ast.ASTLambdaExpression %}.
  * {% jdoc_old !!java::lang.java.ast.ASTAnyTypeDeclaration#getImage() %} will be removed. Please use `getSimpleName()`
    instead. This affects {% jdoc_old !!java::lang.java.ast.ASTAnnotationTypeDeclaration#getImage() %},
    {% jdoc_old !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getImage() %}, and
    {% jdoc_old !!java::lang.java.ast.ASTEnumDeclaration#getImage() %}.
  * Several methods of {% jdoc_old java::lang.java.ast.ASTTryStatement %}, replacements with other names
    have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
  * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
    following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
  * {% jdoc_old java::lang.java.ast.ASTYieldStatement %} will not implement {% jdoc_old java::lang.java.ast.TypeNode %}
    anymore come 7.0.0. Test the type of the expression nested within it.
  * {% jdoc_old java::lang.java.metrics.JavaMetrics %}, {% jdoc_old java::lang.java.metrics.JavaMetricsComputer %}
  * {% jdoc_old !!java::lang.java.ast.ASTArguments#getArgumentCount() %}.
    Use {% jdoc_old java::lang.java.ast.ASTArguments#size() %} instead.
  * {% jdoc_old !!java::lang.java.ast.ASTFormalParameters#getParameterCount() %}.
    Use {% jdoc_old java::lang.java.ast.ASTFormalParameters#size() %} instead.
* pmd-apex
  * {% jdoc_old apex::lang.apex.metrics.ApexMetrics %}, {% jdoc_old apex::lang.apex.metrics.ApexMetricsComputer %}

**In ASTs (JSP)**

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the JSP AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
  *   In the meantime you should use interfaces like {% jdoc jsp::lang.jsp.ast.JspNode %} or
      {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package,
      to refer to nodes generically.
  *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The class {% jdoc_old jsp::lang.jsp.JspParser %} is deprecated and should not be used directly.
    Use {% jdoc !!core::lang.LanguageVersionHandler#getParser(ParserOptions) %} instead.

Please look at [`net.sourceforge.pmd.lang.jsp.ast`](https://docs.pmd-code.org/apidocs/pmd-jsp/6.55.0/net/sourceforge/pmd/lang/jsp/ast/package-summary.html) to find out the full list of deprecations.

**In ASTs (Velocity)**

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the VM AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
  *   In the meantime you should use interfaces like {% jdoc velocity::lang.velocity.ast.VtlNode %} or
      {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package,
      to refer to nodes generically.
  *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The package [`net.sourceforge.pmd.lang.vm.directive`](https://docs.pmd-code.org/apidocs/pmd-vm/6.55.0/net/sourceforge/pmd/lang/vm/directive/package-summary.html) as well as the classes
    {% jdoc_old velocity::lang.vm.util.DirectiveMapper %} and {% jdoc_old velocity::lang.vm.util.LogUtil %} are deprecated
    for removal. They were only used internally during parsing.
*   The class {% jdoc_old velocity::lang.vm.VmParser %} is deprecated and should not be used directly.
    Use {% jdoc !!core::lang.LanguageVersionHandler#getParser(ParserOptions) %} instead.

Please look at {% jdoc_package velocity::lang.vm.ast %} to find out the full list of deprecations.

**PLSQL AST**

The production and node `ASTCursorBody` was unnecessary, not used and has been removed. Cursors have been already
parsed as `ASTCursorSpecification`.

#### 6.21.0

**Deprecated APIs**

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* {% jdoc java::lang.java.JavaLanguageHandler %}
* {% jdoc java::lang.java.JavaLanguageParser %}
* {% jdoc java::lang.java.JavaDataFlowHandler %}
* Implementations of {% jdoc core::lang.rule.RuleViolationFactory %} in each
  language module, eg {% jdoc java::lang.java.rule.JavaRuleViolationFactory %}.
  See javadoc of {% jdoc core::lang.rule.RuleViolationFactory %}.
* Implementations of {% jdoc core::RuleViolation %} in each language module,
  eg {% jdoc java::lang.java.rule.JavaRuleViolation %}. See javadoc of
  {% jdoc core::RuleViolation %}.

* {% jdoc core::rules.RuleFactory %}
* {% jdoc core::rules.RuleBuilder %}
* Constructors of {% jdoc core::RuleSetFactory %}, use factory methods from {% jdoc core::RulesetsFactoryUtils %} instead
* {% jdoc core::RulesetsFactoryUtils#getRulesetFactory(core::PMDConfiguration, core::util.ResourceLoader) %}

* {% jdoc apex::lang.apex.ast.AbstractApexNode %}
* {% jdoc apex::lang.apex.ast.AbstractApexNodeBase %}, and the related `visit`
  methods on {% jdoc apex::lang.apex.ast.ApexParserVisitor %} and its implementations.
  Use {% jdoc apex::lang.apex.ast.ApexNode %} instead, now considers comments too.

* {% jdoc core::lang.ast.CharStream %}, {% jdoc core::lang.ast.JavaCharStream %},
  {% jdoc core::lang.ast.SimpleCharStream %}: these are APIs used by our JavaCC
  implementations and that will be moved/refactored for PMD 7.0.0. They should not
  be used, extended or implemented directly.
* All classes generated by JavaCC, eg {% jdoc java::lang.java.ast.JJTJavaParserState %}.
  This includes token classes, which will be replaced with a single implementation, and
  subclasses of {% jdoc core::lang.ast.ParseException %}, whose usages will be replaced
  by just that superclass.

**For removal**

* pmd-core
  * Many methods on the {% jdoc core::lang.ast.Node %} interface
    and {% jdoc core::lang.ast.AbstractNode %} base class. See their javadoc for details.
  * {% jdoc !!core::lang.ast.Node#isFindBoundary() %} is deprecated for XPath queries.
* pmd-java
  * {% jdoc java::lang.java.AbstractJavaParser %}
  * {% jdoc java::lang.java.AbstractJavaHandler %}
  * [`ASTAnyTypeDeclaration.TypeKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
  * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getKind() %}
  * {% jdoc java::lang.java.ast.JavaQualifiedName %}
  * {% jdoc !!java::lang.java.ast.ASTCatchStatement#getBlock() %}
  * {% jdoc !!java::lang.java.ast.ASTCompilationUnit#declarationsAreInDefaultPackage() %}
  * {% jdoc java::lang.java.ast.JavaQualifiableNode %}
    * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getQualifiedName() %}
    * {% jdoc !!java::lang.java.ast.ASTMethodOrConstructorDeclaration#getQualifiedName() %}
    * {% jdoc !!java::lang.java.ast.ASTLambdaExpression#getQualifiedName() %}
  * {% jdoc_package java::lang.java.qname %} and its contents
  * {% jdoc java::lang.java.ast.MethodLikeNode %}
    * Its methods will also be removed from its implementations,
      {% jdoc java::lang.java.ast.ASTMethodOrConstructorDeclaration %},
      {% jdoc java::lang.java.ast.ASTLambdaExpression %}.
  * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getImage() %} will be removed. Please use `getSimpleName()`
    instead. This affects {% jdoc !!java::lang.java.ast.ASTAnnotationTypeDeclaration#getImage() %},
    {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getImage() %}, and
    {% jdoc !!java::lang.java.ast.ASTEnumDeclaration#getImage() %}.
  * Several methods of {% jdoc java::lang.java.ast.ASTTryStatement %}, replacements with other names
    have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
  * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
    following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
  * {% jdoc java::lang.java.ast.ASTYieldStatement %} will not implement {% jdoc java::lang.java.ast.TypeNode %}
    anymore come 7.0.0. Test the type of the expression nested within it.

#### 6.20.0

No changes.

#### 6.19.0

**Deprecated APIs**

**For removal**

* pmd-core
  * All the package {% jdoc_package core::dcd %} and its subpackages. See {% jdoc core::dcd.DCD %}.
  * In {% jdoc core::lang.LanguageRegistry %}:
    * {% jdoc core::lang.LanguageRegistry#commaSeparatedTerseNamesForLanguageVersion(List) %}
    * {% jdoc core::lang.LanguageRegistry#commaSeparatedTerseNamesForLanguage(List) %}
    * {% jdoc core::lang.LanguageRegistry#findAllVersions() %}
    * {% jdoc core::lang.LanguageRegistry#findLanguageVersionByTerseName(String) %}
    * {% jdoc core::lang.LanguageRegistry#getInstance() %}
  * {% jdoc !!core::RuleSet#getExcludePatterns() %}. Use the new method {% jdoc core::RuleSet#getFileExclusions() %} instead.
  * {% jdoc !!core::RuleSet#getIncludePatterns() %}. Use the new method {% jdoc core::RuleSet#getFileInclusions() %} instead.
  * {% jdoc !!core::lang.Parser#canParse() %}
  * {% jdoc !!core::lang.Parser#getSuppressMap() %}
  * {% jdoc !!core::rules.RuleBuilder#RuleBuilder(String,String,String) %}. Use the new constructor with the correct ResourceLoader instead.
  * {% jdoc !!core::rules.RuleFactory#RuleFactory() %}. Use the new constructor with the correct ResourceLoader instead.
* pmd-java
  * {% jdoc java::lang.java.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc java::lang.java.rule.AbstractJavaRule#isSuppressed(Node) %}
  * {% jdoc java::lang.java.rule.AbstractJavaRule#getDeclaringType(Node) %}.
  * {% jdoc java::lang.java.rule.JavaRuleViolation#isSupressed(Node,Rule) %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclarator %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclaration#getMethodName() %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclaration#getBlock() %}
  * {% jdoc java::lang.java.ast.ASTConstructorDeclaration#getParameterCount() %}
* pmd-apex
  * {% jdoc apex::lang.apex.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc apex::lang.apex.rule.ApexRuleViolation#isSupressed(Node,Rule) %}

**Internal APIs**

* pmd-core
  * All the package {% jdoc_package core::util %} and its subpackages,
    except {% jdoc_package core::util.datasource %} and {% jdoc_package core::util.database %}.
  * {% jdoc core::cpd.GridBagHelper %}
  * {% jdoc core::renderers.ColumnDescriptor %}


#### 6.18.0

**Changes to Renderer**

*   Each renderer has now a new method {% jdoc !!core::renderers.Renderer#setUseShortNames(List) %} which
    is used for implementing the "shortnames" CLI option. The method is automatically called by PMD, if this
    CLI option is in use. When rendering filenames to the report, the new helper method
    {% jdoc !!core::renderers.AbstractRenderer#determineFileName(String) %} should be used. This will change
    the filename to a short name, if the CLI option "shortnames" is used.

    Not adjusting custom renderers will make them render always the full file names and not honoring the
    CLI option "shortnames".

**Deprecated APIs**

**For removal**

*   The methods {% jdoc java::lang.java.ast.ASTImportDeclaration#getImportedNameNode() %} and
    {% jdoc java::lang.java.ast.ASTImportDeclaration#getPackage() %} have been deprecated and
    will be removed with PMD 7.0.0.
*   The method {% jdoc !!core::RuleContext#setSourceCodeFilename(String) %} has been deprecated
    and will be removed. The already existing method {% jdoc !!core::RuleContext#setSourceCodeFile(File) %}
    should be used instead. The method {% jdoc !!core::RuleContext#getSourceCodeFilename() %} still
    exists and returns just the filename without the full path.
*   The method {% jdoc !!core::processor.AbstractPMDProcessor#filenameFrom(DataSource) %} has been
    deprecated. It was used to determine a "short name" of the file being analyzed, so that the report
    can use short names. However, this logic has been moved to the renderers.
*   The method {% jdoc !!core::Report#metrics() %} and {% jdoc core::Report::hasMetrics() %} have
    been deprecated. They were leftovers from a previous deprecation round targeting
    {% jdoc core::lang.rule.stat.StatisticalRule %}.

**Internal APIs**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0. You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* pmd-core
  * {% jdoc_package core::cache %}
* pmd-java
  * {% jdoc_package java::lang.java.typeresolution %}: Everything, including
    subpackages, except {% jdoc java::lang.java.typeresolution.TypeHelper %} and
    {% jdoc java::lang.java.typeresolution.typedefinition.JavaTypeDefinition %}.
  * {% jdoc !c!java::lang.java.ast.ASTCompilationUnit#getClassTypeResolver() %}


#### 6.17.0

No changes.

#### 6.16.0

**Deprecated APIs**

> Reminder: Please don't use members marked with the annotation {% jdoc core::annotation.InternalApi %}, as they will likely be removed, hidden, or otherwise intentionally broken with 7.0.0.


**In ASTs**

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the Java AST** (with other languages to come):

* Manual instantiation of nodes. **Constructors of node classes are deprecated** and marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser, which for rules, means that never need to instantiate node themselves. Those constructors will be made package private with 7.0.0.
* **Subclassing of abstract node classes, or usage of their type**. Version 7.0.0 will bring a new set of abstractions that will be public API, but the base classes are and will stay internal. You should not couple your code to them.
  * In the meantime you should use interfaces like {% jdoc java::lang.java.ast.JavaNode %} or  {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package, to refer to nodes generically.
  * Concrete node classes will **be made final** with 7.0.0.
* Setters found in any node class or interface. **Rules should consider the AST immutable**. We will make those setters package private with 7.0.0.

Please look at {% jdoc_package java::lang.java.ast %} to find out the full list
of deprecations.


#### 6.15.0

**Deprecated APIs**

**For removal**

*   The `DumpFacades` in all languages, that could be used to transform a AST into a textual representation,
    will be removed with PMD 7. The rule designer is a better way to inspect nodes.
  *   {% jdoc !q!apex::lang.apex.ast.DumpFacade %}
  *   {% jdoc !q!java::lang.java.ast.DumpFacade %}
  *   {% jdoc !q!javascript::lang.ecmascript.ast.DumpFacade %}
  *   {% jdoc !q!jsp::lang.jsp.ast.DumpFacade %}
  *   {% jdoc !q!plsql::lang.plsql.ast.DumpFacade %}
  *   {% jdoc !q!visualforce::lang.vf.ast.DumpFacade %}
  *   {% jdoc !q!velocity::lang.vm.ast.AbstractVmNode#dump(String, boolean, Writer) %}
  *   {% jdoc !q!xml::lang.xml.ast.DumpFacade %}
*   The method {% jdoc !c!core::lang.LanguageVersionHandler#getDumpFacade(Writer, String, boolean) %} will be
    removed as well. It is deprecated, along with all its implementations in the subclasses of {% jdoc core::lang.LanguageVersionHandler %}.

#### 6.14.0

No changes.

#### 6.13.0

**Command Line Interface**

The start scripts `run.sh`, `pmd.bat` and `cpd.bat` support the new environment variable `PMD_JAVA_OPTS`.
This can be used to set arbitrary JVM options for running PMD, such as memory settings (e.g. `PMD_JAVA_OPTS=-Xmx512m`)
or enable preview language features (e.g. `PMD_JAVA_OPTS=--enable-preview`).

The previously available variables such as `OPTS` or `HEAPSIZE` are deprecated and will be removed with PMD 7.0.0.

**Deprecated API**

*   {% jdoc core::renderers.CodeClimateRule %} is deprecated in 7.0.0 because it was unused for 2 years and
    created an unwanted dependency.
    Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" will also be removed.
    See [#1702](https://github.com/pmd/pmd/pull/1702) for more.

*   The Apex ruleset `rulesets/apex/ruleset.xml` has been deprecated and will be removed in 7.0.0. Please use the new
    quickstart ruleset `rulesets/apex/quickstart.xml` instead.

#### 6.12.0

No changes.

#### 6.11.0

* {% jdoc core::lang.rule.stat.StatisticalRule %} and the related helper classes and base rule classes
  are deprecated for removal in 7.0.0. This includes all of {% jdoc_package core::stat %} and {% jdoc_package core::lang.rule.stat %},
  and also {% jdoc java::lang.java.rule.AbstractStatisticalJavaRule %}, {% jdoc apex::lang.apex.rule.AbstractStatisticalApexRule %} and the like.
  The methods {% jdoc !c!core::Report#addMetric(core::stat.Metric) %} and {% jdoc core::ThreadSafeReportListener#metricAdded(core::stat.Metric) %}
  will also be removed.
* {% jdoc core::properties.PropertySource#setProperty(core::properties.MultiValuePropertyDescriptor, Object[]) %} is deprecated,
  because {% jdoc core::properties.MultiValuePropertyDescriptor %} is deprecated as well

#### 6.10.0

**Properties framework**

{% jdoc_nspace :props core::properties %}
{% jdoc_nspace :PDr props::PropertyDescriptor %}
{% jdoc_nspace :PF props::PropertyFactory %}

The properties framework is about to get a lifting, and for that reason, we need to deprecate a lot of APIs
to remove them in 7.0.0. The proposed changes to the API are described [on the wiki](https://github.com/pmd/pmd/wiki/Property-framework-7-0-0)

**Changes to how you define properties**

* Construction of property descriptors has been possible through builders since 6.0.0. The 7.0.0 API will only allow
  construction through builders. The builder hierarchy, currently found in the package {% jdoc_package props::builders %},
  is being replaced by the simpler {% jdoc props::PropertyBuilder %}. Their APIs enjoy a high degree of source compatibility.

* Concrete property classes like {% jdoc props::IntegerProperty %} and {% jdoc props::StringMultiProperty %} will gradually
  all be deprecated until 7.0.0. Their usages should be replaced by direct usage of the {% jdoc props::PropertyDescriptor %}
  interface, e.g. `PropertyDescriptor<Integer>` or `PropertyDescriptor<List<String>>`.

* Instead of spreading properties across countless classes, the utility class {% jdoc :PF %} will become
  from 7.0.0 on the only provider for property descriptor builders. Each current property type will be replaced
  by a corresponding method on `PropertyFactory`:
  * {% jdoc props::IntegerProperty %} is replaced by {% jdoc !c!:PF#intProperty(java.lang.String) %}
    * {% jdoc props::IntegerMultiProperty %} is replaced by {% jdoc !c!:PF#intListProperty(java.lang.String) %}

  * {% jdoc props::FloatProperty %} and {% jdoc props::DoubleProperty %} are both replaced by {% jdoc !c!:PF#doubleProperty(java.lang.String) %}.
    Having a separate property for floats wasn't that useful.
    * Similarly, {% jdoc props::FloatMultiProperty %} and {% jdoc props::DoubleMultiProperty %} are replaced by {% jdoc !c!:PF#doubleListProperty(java.lang.String) %}.

  * {% jdoc props::StringProperty %} is replaced by {% jdoc !c!:PF#stringProperty(java.lang.String) %}
    * {% jdoc props::StringMultiProperty %} is replaced by {% jdoc !c!:PF#stringListProperty(java.lang.String) %}

  * {% jdoc props::RegexProperty %} is replaced by {% jdoc !c!:PF#regexProperty(java.lang.String) %}

  * {% jdoc props::EnumeratedProperty %} is replaced by {% jdoc !c!:PF#enumProperty(java.lang.String,java.util.Map) %}
    * {% jdoc props::EnumeratedProperty %} is replaced by {% jdoc !c!:PF#enumListProperty(java.lang.String,java.util.Map) %}

  * {% jdoc props::BooleanProperty %} is replaced by {% jdoc !c!:PF#booleanProperty(java.lang.String) %}
    * Its multi-valued counterpart, {% jdoc props::BooleanMultiProperty %}, is not replaced, because it doesn't have a use case.

  * {% jdoc props::CharacterProperty %} is replaced by {% jdoc !c!:PF#charProperty(java.lang.String) %}
    * {% jdoc props::CharacterMultiProperty %} is replaced by {% jdoc !c!:PF#charListProperty(java.lang.String) %}

  * {% jdoc props::LongProperty %} is replaced by {% jdoc !c!:PF#longIntProperty(java.lang.String) %}
    * {% jdoc props::LongMultiProperty %} is replaced by {% jdoc !c!:PF#longIntListProperty(java.lang.String) %}

  * {% jdoc props::MethodProperty %}, {% jdoc props::FileProperty %}, {% jdoc props::TypeProperty %} and their multi-valued counterparts
    are discontinued for lack of a use-case, and have no planned replacement in 7.0.0 for now.
    <!-- TODO complete that as we proceed. -->


Here's an example:
```java
// Before 7.0.0, these are equivalent:
IntegerProperty myProperty = new IntegerProperty("score", "Top score value", 1, 100, 40, 3.0f);
IntegerProperty myProperty = IntegerProperty.named("score").desc("Top score value").range(1, 100).defaultValue(40).uiOrder(3.0f);

// They both map to the following in 7.0.0
PropertyDescriptor<Integer> myProperty = PropertyFactory.intProperty("score").desc("Top score value").require(inRange(1, 100)).defaultValue(40);
```

You're highly encouraged to migrate to using this new API as soon as possible, to ease your migration to 7.0.0.



**Architectural simplifications**

* {% jdoc props::EnumeratedPropertyDescriptor %}, {% jdoc props::NumericPropertyDescriptor %}, {% jdoc props::PackagedPropertyDescriptor %},
  and the related builders (in {% jdoc_package props::builders %}) will be removed.
  These specialized interfaces allowed additional constraints to be enforced on the
  value of a property, but made the property class hierarchy very large and impractical
  to maintain. Their functionality will be mapped uniformly to {% jdoc props::constraints.PropertyConstraint %}s,
  which will allow virtually any constraint to be defined, and improve documentation and error reporting. The
  related methods {% jdoc !c!props::PropertyTypeId#isPropertyNumeric() %} and
  {% jdoc !c!props::PropertyTypeId#isPropertyPackaged() %} are also deprecated.

* {% jdoc props::MultiValuePropertyDescriptor %} and {% jdoc props::SingleValuePropertyDescriptor %}
  are deprecated. 7.0.0 will introduce a new XML syntax which will remove the need for such a divide
  between single- and multi-valued properties. The method {% jdoc !c!:PDr#isMultiValue() %} will be removed
  accordingly.

**Changes to the PropertyDescriptor interface**

* {% jdoc :PDr#preferredRowCount() %} is deprecated with no intended replacement. It was never implemented, and does not belong
  in this interface. The methods {% jdoc :PDr#uiOrder() %} and `compareTo(PropertyDescriptor)` are deprecated for the
  same reason. These methods mix presentation logic with business logic and are not necessary for PropertyDescriptors to work.
  `PropertyDescriptor` will not extend `Comparable<PropertyDescriptor>` anymore come 7.0.0.
* The method {% jdoc :PDr#propertyErrorFor(core::Rule) %} is deprecated and will be removed with no intended
  replacement. It's really just a shortcut for `prop.errorFor(rule.getProperty(prop))`.
* `T `{% jdoc !a!:PDr#valueFrom(java.lang.String) %} and `String `{% jdoc :PDr#asDelimitedString(java.lang.Object) %}`(T)` are deprecated and will be removed. These were
  used to serialize and deserialize properties to/from a string, but 7.0.0 will introduce a more flexible
  XML syntax which will make them obsolete.
* {% jdoc :PDr#isMultiValue() %} and {% jdoc :PDr#type() %} are deprecated and won't be replaced. The new XML syntax will remove the need
  for a divide between multi- and single-value properties, and will allow arbitrary types to be represented.
  Since arbitrary types may be represented, `type` will become obsolete as it can't represent generic types,
  which will nevertheless be representable with the XML syntax. It was only used for documentation, but a
  new way to document these properties exhaustively will be added with 7.0.0.
* {% jdoc :PDr#errorFor(java.lang.Object) %} is deprecated as its return type will be changed to `Optional<String>` with the shift to Java 8.

**Deprecated APIs**

{% jdoc_nspace :xpath core::lang.ast.xpath %}
{% jdoc_nspace :jast java::lang.java.ast %}
{% jdoc_nspace :rule core::Rule %}
{% jdoc_nspace :lvh core::lang.LanguageVersionHandler %}
{% jdoc_nspace :rset core::RuleSet %}
{% jdoc_nspace :rsets core::RuleSets %}

**For internalization**

*   The implementation of the adapters for the XPath engines Saxon and Jaxen (package {% jdoc_package :xpath %})
    are now deprecated. They'll be moved to an internal package come 7.0.0. Only {% jdoc xpath::Attribute %} remains public API.

*   The classes {% jdoc props::PropertyDescriptorField %}, {% jdoc props::builders.PropertyDescriptorBuilderConversionWrapper %}, and the methods
    {% jdoc !c!:PDr#attributeValuesById %}, {% jdoc !c!:PDr#isDefinedExternally() %} and {% jdoc !c!props::PropertyTypeId#getFactory() %}.
    These were used to read and write properties to and from XML, but were not intended as public API.

*   The class {% jdoc props::ValueParserConstants %} and the interface {% jdoc props::ValueParser %}.

*   All classes from {% jdoc_package java::lang.java.metrics.impl.visitors %} are now considered internal API. They're deprecated
    and will be moved into an internal package with 7.0.0. To implement your own metrics visitors,
    {% jdoc jast::JavaParserVisitorAdapter %} should be directly subclassed.

*   {% jdoc !ac!:lvh#getDataFlowHandler() %}, {% jdoc !ac!:lvh#getDFAGraphRule() %}

*   {% jdoc core::lang.VisitorStarter %}

**For removal**

*   All classes from {% jdoc_package props::modules %} will be removed.

*   The interface {% jdoc jast::Dimensionable %} has been deprecated.
    It gets in the way of a grammar change for 7.0.0 and won't be needed anymore (see [#997](https://github.com/pmd/pmd/issues/997)).

*   Several methods from {% jdoc jast::ASTLocalVariableDeclaration %} and {% jdoc jast::ASTFieldDeclaration %} have
    also been deprecated:

  *   {% jdoc jast::ASTFieldDeclaration %} won't be a {% jdoc jast::TypeNode %} come 7.0.0, so
      {% jdoc jast::ASTFieldDeclaration#getType() %} and
      {% jdoc jast::ASTFieldDeclaration#getTypeDefinition() %} are deprecated.

  *   The method `getVariableName` on those two nodes will be removed, too.

    All these are deprecated because those nodes may declare several variables at once, possibly
    with different types (and obviously with different names). They both implement `Iterator<`{% jdoc jast::ASTVariableDeclaratorId %}`>`
    though, so you should iterate on each declared variable. See [#910](https://github.com/pmd/pmd/issues/910).

*   Visitor decorators are now deprecated and will be removed in PMD 7.0.0. They were originally a way to write
    composable visitors, used in the metrics framework, but they didn't prove cost-effective.

  *   In {% jdoc_package :jast %}: {% jdoc jast::JavaParserDecoratedVisitor %}, {% jdoc jast::JavaParserControllessVisitor %},
      {% jdoc jast::JavaParserControllessVisitorAdapter %}, and {% jdoc jast::JavaParserVisitorDecorator %} are deprecated with no intended replacement.


*   The LanguageModules of several languages, that only support CPD execution, have been deprecated. These languages
    are not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
    not affected by this change. The following classes have been deprecated and will be removed with PMD 7.0.0:

  *   {% jdoc cpp::lang.cpp.CppHandler %}
  *   {% jdoc cpp::lang.cpp.CppLanguageModule %}
  *   {% jdoc cpp::lang.cpp.CppParser %}
  *   {% jdoc cs::lang.cs.CsLanguageModule %}
  *   {% jdoc fortran::lang.fortran.FortranLanguageModule %}
  *   {% jdoc groovy::lang.groovy.GroovyLanguageModule %}
  *   {% jdoc matlab::lang.matlab.MatlabHandler %}
  *   {% jdoc matlab::lang.matlab.MatlabLanguageModule %}
  *   {% jdoc matlab::lang.matlab.MatlabParser %}
  *   {% jdoc objectivec::lang.objectivec.ObjectiveCHandler %}
  *   {% jdoc objectivec::lang.objectivec.ObjectiveCLanguageModule %}
  *   {% jdoc objectivec::lang.objectivec.ObjectiveCParser %}
  *   {% jdoc php::lang.php.PhpLanguageModule %}
  *   {% jdoc python::lang.python.PythonHandler %}
  *   {% jdoc python::lang.python.PythonLanguageModule %}
  *   {% jdoc python::lang.python.PythonParser %}
  *   {% jdoc ruby::lang.ruby.RubyLanguageModule %}
  *   {% jdoc scala::lang.scala.ScalaLanguageModule %}
  *   {% jdoc swift::lang.swift.SwiftLanguageModule %}


* Optional AST processing stages like symbol table, type resolution or data-flow analysis will be reified
  in 7.0.0 to factorise common logic and make them extensible. Further explanations about this change can be
  found on [#1426](https://github.com/pmd/pmd/pull/1426). Consequently, the following APIs are deprecated for
  removal:
  * In {% jdoc :rule %}: {% jdoc !a!:rule#isDfa() %}, {% jdoc !a!:rule#isTypeResolution() %}, {% jdoc !a!:rule#isMultifile() %} and their
    respective setters.
  * In {% jdoc :rset %}: {% jdoc !a!:rset#usesDFA(core::lang.Language) %}, {% jdoc !a!:rset#usesTypeResolution(core::lang.Language) %}, {% jdoc !a!:rset#usesMultifile(core::lang.Language) %}
  * In {% jdoc :rsets %}: {% jdoc !a!:rsets#usesDFA(core::lang.Language) %}, {% jdoc !a!:rsets#usesTypeResolution(core::lang.Language) %}, {% jdoc !a!:rsets#usesMultifile(core::lang.Language) %}
  * In {% jdoc :lvh %}: {% jdoc !a!:lvh#getDataFlowFacade() %}, {% jdoc !a!:lvh#getSymbolFacade() %}, {% jdoc !a!:lvh#getSymbolFacade(java.lang.ClassLoader) %},
    {% jdoc !a!:lvh#getTypeResolutionFacade(java.lang.ClassLoader) %}, {% jdoc !a!:lvh#getQualifiedNameResolutionFacade(java.lang.ClassLoader) %}

#### 6.9.0

No changes.

#### 6.8.0

*   A couple of methods and fields in `net.sourceforge.pmd.properties.AbstractPropertySource` have been
    deprecated, as they are replaced by already existing functionality or expose internal implementation
    details: `propertyDescriptors`, `propertyValuesByDescriptor`,
    `copyPropertyDescriptors()`, `copyPropertyValues()`, `ignoredProperties()`, `usesDefaultValues()`,
    `useDefaultValueFor()`.

*   Some methods in `net.sourceforge.pmd.properties.PropertySource` have been deprecated as well:
    `usesDefaultValues()`, `useDefaultValueFor()`, `ignoredProperties()`.

*   The class `net.sourceforge.pmd.lang.rule.AbstractDelegateRule` has been deprecated and will
    be removed with PMD 7.0.0. It is internally only in use by RuleReference.

*   The default constructor of `net.sourceforge.pmd.lang.rule.RuleReference` has been deprecated
    and will be removed with PMD 7.0.0. RuleReferences should only be created by providing a Rule and
    a RuleSetReference. Furthermore the following methods are deprecated: `setRuleReference()`,
    `hasOverriddenProperty()`, `usesDefaultValues()`, `useDefaultValueFor()`.

#### 6.7.0

*   All classes in the package `net.sourceforge.pmd.lang.dfa.report` have been deprecated and will be removed
    with PMD 7.0.0. This includes the class `net.sourceforge.pmd.lang.dfa.report.ReportTree`. The reason is,
    that this class is very specific to Java and not suitable for other languages. It has only been used for
    `YAHTMLRenderer`, which has been rewritten to work without these classes.

*   The nodes RUNSIGNEDSHIFT and RSIGNEDSHIFT are deprecated and will be removed from the AST with PMD 7.0.0.
    These represented the operator of ShiftExpression in two cases out of three, but they're not needed and
    make ShiftExpression inconsistent. The operator of a ShiftExpression is now accessible through
    ShiftExpression#getOperator.

#### 6.5.0

*   The utility class `net.sourceforge.pmd.lang.java.ast.CommentUtil` has been deprecated and will be removed
    with PMD 7.0.0. Its methods have been intended to parse javadoc tags. A more useful solution will be added
    around the AST node `FormalComment`, which contains as children `JavadocElement` nodes, which in
    turn provide access to the `JavadocTag`.

    All comment AST nodes (`FormalComment`, `MultiLineComment`, `SingleLineComment`) have a new method
    `getFilteredComment()` which provide access to the comment text without the leading `/*` markers.

*   The method `AbstractCommentRule.tagsIndicesIn()` has been deprecated and will be removed with
    PMD 7.0.0. It is not very useful, since it doesn't extract the information
    in a useful way. You would still need check, which tags have been found, and with which
    data they might be accompanied.

#### 6.4.0

* The following classes in package `net.sourceforge.pmd.benchmark` have been deprecated: `Benchmark`, `Benchmarker`,
  `BenchmarkReport`, `BenchmarkResult`, `RuleDuration`, `StringBuilderCR` and `TextReport`. Their API is not supported anymore
  and is disconnected from the internals of PMD. Use the newer API based around `TimeTracker` instead, which can be found
  in the same package.
* The class `net.sourceforge.pmd.lang.java.xpath.TypeOfFunction` has been deprecated. Use the newer `TypeIsFunction` in the same package.
* The `typeof` methods in `net.sourceforge.pmd.lang.java.xpath.JavaFunctions` have been deprecated.
  Use the newer `typeIs` method in the same class instead..
* The methods `isA`, `isEither` and `isNeither` of `net.sourceforge.pmd.lang.java.typeresolution.TypeHelper`.
  Use the new `isExactlyAny` and `isExactlyNone` methods in the same class instead.

#### 6.2.0

*   The static method `PMDParameters.transformParametersIntoConfiguration(PMDParameters)` is now deprecated,
    for removal in 7.0.0. The new instance method `PMDParameters.toConfiguration()` replaces it.

*   The method `ASTConstructorDeclaration.getParameters()` has been deprecated in favor of the new method
    `getFormalParameters()`. This method is available for both `ASTConstructorDeclaration` and
    `ASTMethodDeclaration`.

#### 6.1.0

* The method `getXPathNodeName` is added to the `Node` interface, which removes the
  use of the `toString` of a node to get its XPath element name (see [#569](https://github.com/pmd/pmd/issues/569)).
  * The default implementation provided in  `AbstractNode`, will
    be removed with 7.0.0
  * With 7.0.0, the `Node.toString` method will not necessarily provide its XPath node
    name anymore.

* The interface `net.sourceforge.pmd.cpd.Renderer` has been deprecated. A new interface
  `net.sourceforge.pmd.cpd.renderer.CPDRenderer` has been introduced to replace it. The main
  difference is that the new interface is meant to render directly to a `java.io.Writer`
  rather than to a String. This allows to greatly reduce the memory footprint of CPD, as on
  large projects, with many duplications, it was causing `OutOfMemoryError`s (see [#795](https://github.com/pmd/pmd/issues/795)).

  `net.sourceforge.pmd.cpd.FileReporter` has also been deprecated as part of this change, as it's no longer needed.

#### 6.0.1

*   The constant `net.sourceforge.pmd.PMD.VERSION` has been deprecated and will be removed with PMD 7.0.0.
    Please use `net.sourceforge.pmd.PMDVersion.VERSION` instead.


## 🐛 Fixed Issues

* miscellaneous
  * [#881](https://github.com/pmd/pmd/issues/881):   \[all] Breaking API changes for 7.0.0
  * [#896](https://github.com/pmd/pmd/issues/896):   \[all] Use slf4j
  * [#1431](https://github.com/pmd/pmd/pull/1431):   \[ui] Remove old GUI applications (designerold, bgastviewer)
  * [#1451](https://github.com/pmd/pmd/issues/1451): \[core] RulesetFactoryCompatibility stores the whole ruleset file in memory as a string
  * [#2496](https://github.com/pmd/pmd/issues/2496): Update PMD 7 Logo on landing page
  * [#2497](https://github.com/pmd/pmd/issues/2497): PMD 7 Logo page
  * [#2498](https://github.com/pmd/pmd/issues/2498): Update PMD 7 Logo in documentation
  * [#3797](https://github.com/pmd/pmd/issues/3797): \[all] Use JUnit5
  * [#4462](https://github.com/pmd/pmd/issues/4462): Provide Software Bill of Materials (SBOM)
  * [#4460](https://github.com/pmd/pmd/pull/4460):   Fix assembly-plugin warnings
  * [#4582](https://github.com/pmd/pmd/issues/4582): \[dist] Download link broken
  * [#4586](https://github.com/pmd/pmd/pull/4586):   Use explicit encoding in ruleset xml files
  * [#4642](https://github.com/pmd/pmd/issues/4642): Update regression tests with Java 21 language features
  * [#4691](https://github.com/pmd/pmd/issues/4691): \[CVEs] Critical and High CEVs reported on PMD and PMD dependencies
  * [#4699](https://github.com/pmd/pmd/pull/4699):   Make PMD buildable with java 21
  * [#4736](https://github.com/pmd/pmd/issues/4736): \[ci] Improve build procedure
  * [#4741](https://github.com/pmd/pmd/pull/4741):   Add pmd-compat6 module for maven-pmd-plugin
  * [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6
  * [#4776](https://github.com/pmd/pmd/issues/4776): \[ci] Upgrade to ruby 3
  * [#4796](https://github.com/pmd/pmd/pull/4796):   Remove deprecated and release rulesets
  * [#4823](https://github.com/pmd/pmd/pull/4823):   Update to use renamed pmd-designer
  * [#4827](https://github.com/pmd/pmd/pull/4827):   \[compat6] Support config errors and cpd for csharp
  * [#4830](https://github.com/pmd/pmd/issues/4830): Consolidate packages in each maven module
* ant
  * [#4080](https://github.com/pmd/pmd/issues/4080): \[ant] Split off Ant integration into a new submodule
* core
  * [#880](https://github.com/pmd/pmd/issues/880):   \[core] Make visitors generic
  * [#1027](https://github.com/pmd/pmd/issues/1027): \[core] Apply the new PropertyDescriptor&lt;Pattern&gt; type where applicable
  * [#1204](https://github.com/pmd/pmd/issues/1204): \[core] Allow numeric properties in XML to be within an unbounded range
  * [#1622](https://github.com/pmd/pmd/pull/1622):   \[core] NodeStream API
  * [#1687](https://github.com/pmd/pmd/issues/1687): \[core] Deprecate and Remove XPath 1.0 support
  * [#1785](https://github.com/pmd/pmd/issues/1785): \[core] Allow abstract node types to be valid rulechain visits
  * [#1825](https://github.com/pmd/pmd/pull/1825):   \[core] Support NoAttribute for XPath
  * [#2038](https://github.com/pmd/pmd/issues/2038): \[core] Remove DCD
  * [#2218](https://github.com/pmd/pmd/issues/2218): \[core] `isFindBoundary` should not be an attribute
  * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
  * [#2239](https://github.com/pmd/pmd/issues/2239): \[core] Merging Javacc build scripts
  * [#2500](https://github.com/pmd/pmd/issues/2500): \[core] Clarify API for ANTLR based languages
  * [#2518](https://github.com/pmd/pmd/issues/2518): \[core] Language properties
  * [#2602](https://github.com/pmd/pmd/issues/2602): \[core] Remove ParserOptions
  * [#2614](https://github.com/pmd/pmd/pull/2614):   \[core] Upgrade Saxon, add XPath 3.1, remove Jaxen
  * [#2696](https://github.com/pmd/pmd/pull/2696):   \[core] Remove DFA
  * [#2821](https://github.com/pmd/pmd/issues/2821): \[core] Rule processing error filenames are missing paths
  * [#2873](https://github.com/pmd/pmd/issues/2873): \[core] Utility classes in pmd 7
  * [#2885](https://github.com/pmd/pmd/issues/2885): \[core] Error recovery mode
  * [#3203](https://github.com/pmd/pmd/issues/3203): \[core] Replace RuleViolationFactory implementations with ViolationDecorator
  * [#3692](https://github.com/pmd/pmd/pull/3692):   \[core] Analysis listeners
  * [#3782](https://github.com/pmd/pmd/issues/3782): \[core] Language lifecycle
  * [#3815](https://github.com/pmd/pmd/issues/3815): \[core] Update Saxon HE to 10.7
  * [#3893](https://github.com/pmd/pmd/pull/3893):   \[core] Text documents
  * [#3902](https://github.com/pmd/pmd/issues/3902): \[core] Violation decorators
  * [#3903](https://github.com/pmd/pmd/issues/3903): \[core] Consolidate `n.s.pmd.reporting` package
  * [#3905](https://github.com/pmd/pmd/issues/3905): \[core] Stabilize tree export API
  * [#3917](https://github.com/pmd/pmd/issues/3917): \[core] Consolidate `n.s.pmd.lang.rule` package
  * [#3918](https://github.com/pmd/pmd/issues/3918): \[core] Make LanguageRegistry non static
  * [#3919](https://github.com/pmd/pmd/issues/3919): \[core] Merge CPD and PMD language
  * [#3922](https://github.com/pmd/pmd/pull/3922):   \[core] Better error reporting for the ruleset parser
  * [#4035](https://github.com/pmd/pmd/issues/4035): \[core] ConcurrentModificationException in DefaultRuleViolationFactory
  * [#4065](https://github.com/pmd/pmd/issues/4065): \[core] Rename TokenMgrError to LexException, Tokenizer to CpdLexer
  * [#4120](https://github.com/pmd/pmd/issues/4120): \[core] Explicitly name all language versions
  * [#4204](https://github.com/pmd/pmd/issues/4204): \[core] Provide a CpdAnalysis class as a programmatic entry point into CPD
  * [#4301](https://github.com/pmd/pmd/issues/4301): \[core] Remove deprecated property concrete classes
  * [#4302](https://github.com/pmd/pmd/issues/4302): \[core] Migrate Property Framework API to Java 8
  * [#4309](https://github.com/pmd/pmd/issues/4309): \[core] Cleanups in XPath area
  * [#4312](https://github.com/pmd/pmd/issues/4312): \[core] Remove unnecessary property `color` and system property `pmd.color` in `TextColorRenderer`
  * [#4313](https://github.com/pmd/pmd/issues/4313): \[core] Remove support for &lt;lang&gt;-&lt;ruleset&gt; hyphen notation for ruleset references
  * [#4314](https://github.com/pmd/pmd/issues/4314): \[core] Remove ruleset compatibility filter (RuleSetFactoryCompatibility) and CLI option `--no-ruleset-compatibility`
  * [#4323](https://github.com/pmd/pmd/issues/4323): \[core] Refactor CPD integration
  * [#4348](https://github.com/pmd/pmd/issues/4348): \[core] Consolidate @<!-- -->InternalApi classes
  * [#4349](https://github.com/pmd/pmd/issues/4349): \[core] Cleanup remaining experimental and deprecated API
  * [#4353](https://github.com/pmd/pmd/pull/4353):   \[core] Micro optimizations for Node API
  * [#4365](https://github.com/pmd/pmd/pull/4365):   \[core] Improve benchmarking
  * [#4397](https://github.com/pmd/pmd/pull/4397):   \[core] Refactor CPD
  * [#4378](https://github.com/pmd/pmd/issues/4378): \[core] Ruleset loading processes commented rules
  * [#4420](https://github.com/pmd/pmd/pull/4420):   \[core] Remove PMD.EOL
  * [#4425](https://github.com/pmd/pmd/pull/4425):   \[core] Replace TextFile::pathId
  * [#4454](https://github.com/pmd/pmd/issues/4454): \[core] "Unknown option: '-min'" but is referenced in documentation
  * [#4611](https://github.com/pmd/pmd/pull/4611):   \[core] Fix loading language properties from env vars
  * [#4621](https://github.com/pmd/pmd/issues/4621): \[core] Make `ClasspathClassLoader::getResource` child first
  * [#4674](https://github.com/pmd/pmd/issues/4674): \[core] WARNING: Illegal reflective access by org.codehaus.groovy.reflection.CachedClass
  * [#4694](https://github.com/pmd/pmd/pull/4694):   \[core] Fix line/col numbers in TokenMgrError
  * [#4717](https://github.com/pmd/pmd/issues/4717): \[core] XSLTRenderer doesn't close report file
  * [#4750](https://github.com/pmd/pmd/pull/4750):   \[core] Fix flaky SummaryHTMLRenderer
  * [#4782](https://github.com/pmd/pmd/pull/4782):   \[core] Avoid using getImage/@<!-- -->Image
* cli
  * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
  * [#3828](https://github.com/pmd/pmd/issues/3828): \[core] Progress reporting
  * [#4079](https://github.com/pmd/pmd/issues/4079): \[cli] Split off CLI implementation into a pmd-cli submodule
  * [#4423](https://github.com/pmd/pmd/pull/4423):   \[cli] Fix NPE when only `--file-list` is specified
  * [#4482](https://github.com/pmd/pmd/issues/4482): \[cli] pmd.bat can only be executed once
  * [#4484](https://github.com/pmd/pmd/issues/4484): \[cli] ast-dump with no properties produce an NPE
  * [#4594](https://github.com/pmd/pmd/pull/4594):   \[cli] Change completion generation to runtime
  * [#4685](https://github.com/pmd/pmd/pull/4685):   \[cli] Clarify CPD documentation, fix positional parameter handling
  * [#4723](https://github.com/pmd/pmd/issues/4723): \[cli] Launch fails for "bash pmd"
* doc
  * [#995](https://github.com/pmd/pmd/issues/995):   \[doc] Document API evolution principles as ADR
  * [#2501](https://github.com/pmd/pmd/issues/2501): \[doc] Verify ANTLR Documentation
  * [#2511](https://github.com/pmd/pmd/issues/2511): \[doc] Review guides for writing java/xpath rules for correctness with PMD 7
  * [#3175](https://github.com/pmd/pmd/issues/3175): \[doc] Document language module features
  * [#4294](https://github.com/pmd/pmd/issues/4294): \[doc] Migration Guide for upgrading PMD 6 ➡️ 7
  * [#4303](https://github.com/pmd/pmd/issues/4303): \[doc] Document new property framework
  * [#4308](https://github.com/pmd/pmd/issues/4308): \[doc] Document XPath API @<!-- ->NoAttribute and @<!-- -->DeprecatedAttribute
  * [#4319](https://github.com/pmd/pmd/issues/4319): \[doc] Document TypeRes API and Symbols API
  * [#4438](https://github.com/pmd/pmd/issues/4438): \[doc] Documentation links in VS Code are outdated
  * [#4521](https://github.com/pmd/pmd/issues/4521): \[doc] Website is not mobile friendly
  * [#4676](https://github.com/pmd/pmd/issues/4676): \[doc] Clarify how CPD `--ignore-literals` and `--ignore-identifiers` work
  * [#4659](https://github.com/pmd/pmd/pull/4659):   \[doc] Improve ant documentation
  * [#4669](https://github.com/pmd/pmd/pull/4669):   \[doc] Add bld PMD Extension to Tools / Integrations
  * [#4704](https://github.com/pmd/pmd/issues/4704): \[doc] Multivalued properties do not accept \| as a separator
* testing
  * [#2435](https://github.com/pmd/pmd/issues/2435): \[test] Remove duplicated Dummy language module
  * [#4234](https://github.com/pmd/pmd/issues/4234): \[test] Tests that change the logging level do not work

Language specific fixes:

* apex
  * [#1937](https://github.com/pmd/pmd/issues/1937): \[apex] Apex should only have a single RootNode
  * [#1648](https://github.com/pmd/pmd/issues/1648): \[apex,vf] Remove CodeClimate dependency
  * [#1750](https://github.com/pmd/pmd/pull/1750):   \[apex] Remove apex statistical rules
  * [#2836](https://github.com/pmd/pmd/pull/2836):   \[apex] Remove Apex ProjectMirror
  * [#3766](https://github.com/pmd/pmd/issues/3766): \[apex] Replace Jorje with fully open source front-end
  * [#3973](https://github.com/pmd/pmd/issues/3973): \[apex] Update parser to support new 'as user' keywords (User Mode for Database Operations)
  * [#4427](https://github.com/pmd/pmd/issues/4427): \[apex] ApexBadCrypto test failing to detect inline code
  * [#4453](https://github.com/pmd/pmd/issues/4453): \[apex] \[7.0-rc1] Exception while initializing Apexlink (Index 34812 out of bounds for length 34812)
* apex-design
  * [#2667](https://github.com/pmd/pmd/issues/2667): \[apex] Integrate nawforce/ApexLink to build robust Unused rule
  * [#4509](https://github.com/pmd/pmd/issues/4509): \[apex] ExcessivePublicCount doesn't consider inner classes correctly
  * [#4596](https://github.com/pmd/pmd/issues/4596): \[apex] ExcessivePublicCount ignores properties
* apex-documentation
  * [#4774](https://github.com/pmd/pmd/issues/4774): \[apex] ApexDoc false-positive for the first method of an annotated Apex class
* apex-performance
  * [#4675](https://github.com/pmd/pmd/issues/4675): \[apex] New Rule: OperationWithHighCostInLoop
* apex-security
  * [#4646](https://github.com/pmd/pmd/issues/4646): \[apex] ApexSOQLInjection does not recognise SObjectType or SObjectField as safe variable types
* groovy
  * [#4726](https://github.com/pmd/pmd/pull/4726):   \[groovy] Support Groovy to 3 and 4 and CPD suppressions
* java
  * [#520](https://github.com/pmd/pmd/issues/520):   \[java] Allow `@SuppressWarnings` with constants instead of literals
  * [#864](https://github.com/pmd/pmd/issues/864):   \[java] Similar/duplicated implementations for determining FQCN
  * [#905](https://github.com/pmd/pmd/issues/905):   \[java] Add new node for anonymous class declaration
  * [#910](https://github.com/pmd/pmd/issues/910):   \[java] AST inconsistency between primitive and reference type arrays
  * [#997](https://github.com/pmd/pmd/issues/997):   \[java] Java8 parsing corner case with annotated array types
  * [#998](https://github.com/pmd/pmd/issues/998):   \[java] AST inconsistencies around FormalParameter
  * [#1019](https://github.com/pmd/pmd/issues/1019): \[java] Breaking Java Grammar changes for PMD 7.0.0
  * [#1124](https://github.com/pmd/pmd/issues/1124): \[java] ImmutableList implementation in the qname codebase
  * [#1128](https://github.com/pmd/pmd/issues/1128): \[java] Improve ASTLocalVariableDeclaration
  * [#1150](https://github.com/pmd/pmd/issues/1150): \[java] ClassOrInterfaceType AST improvements
  * [#1207](https://github.com/pmd/pmd/issues/1207): \[java] Resolve explicit types using FQCNs, without hitting the classloader
  * [#1307](https://github.com/pmd/pmd/issues/1307): \[java] AccessNode API changes
  * [#1367](https://github.com/pmd/pmd/issues/1367): \[java] Parsing error on annotated inner class
  * [#1661](https://github.com/pmd/pmd/issues/1661): \[java] About operator nodes
  * [#2366](https://github.com/pmd/pmd/pull/2366):   \[java] Remove qualified names
  * [#2819](https://github.com/pmd/pmd/issues/2819): \[java] GLB bugs in pmd 7
  * [#3642](https://github.com/pmd/pmd/issues/3642): \[java] Parse error on rare extra dimensions on method return type on annotation methods
  * [#3763](https://github.com/pmd/pmd/issues/3763): \[java] Ambiguous reference error in valid code
  * [#3749](https://github.com/pmd/pmd/issues/3749): \[java] Improve `isOverridden` in ASTMethodDeclaration
  * [#3750](https://github.com/pmd/pmd/issues/3750): \[java] Make symbol table support instanceof pattern bindings
  * [#3751](https://github.com/pmd/pmd/issues/3751): \[java] Rename some node types
  * [#3752](https://github.com/pmd/pmd/issues/3752): \[java] Expose annotations in symbol API
  * [#4237](https://github.com/pmd/pmd/pull/4237):   \[java] Cleanup handling of Java comments
  * [#4317](https://github.com/pmd/pmd/issues/4317): \[java] Some AST nodes should not be TypeNodes
  * [#4359](https://github.com/pmd/pmd/issues/4359): \[java] Type resolution fails with NPE when the scope is not a type declaration
  * [#4367](https://github.com/pmd/pmd/issues/4367): \[java] Move testrule TypeResTest into internal
  * [#4383](https://github.com/pmd/pmd/issues/4383): \[java] IllegalStateException: Object is not an array type!
  * [#4401](https://github.com/pmd/pmd/issues/4401): \[java] PMD 7 fails to build under Java 19
  * [#4405](https://github.com/pmd/pmd/issues/4405): \[java] Processing error with ArrayIndexOutOfBoundsException
  * [#4583](https://github.com/pmd/pmd/issues/4583): \[java] Support JDK 21 (LTS)
  * [#4628](https://github.com/pmd/pmd/pull/4628):   \[java] Support loading classes from java runtime images
  * [#4753](https://github.com/pmd/pmd/issues/4753): \[java] PMD crashes while using generics and wildcards
  * [#4794](https://github.com/pmd/pmd/issues/4794): \[java] Support JDK 22
* java-bestpractices
  * [#342](https://github.com/pmd/pmd/issues/342):   \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
  * [#755](https://github.com/pmd/pmd/issues/755):   \[java] AccessorClassGeneration false positive for private constructors
  * [#770](https://github.com/pmd/pmd/issues/770):   \[java] UnusedPrivateMethod yields false positive for counter-variant arguments
  * [#807](https://github.com/pmd/pmd/issues/807):   \[java] AccessorMethodGeneration false positive with overloads
  * [#833](https://github.com/pmd/pmd/issues/833):   \[java] ForLoopCanBeForeach should consider iterating on this
  * [#1189](https://github.com/pmd/pmd/issues/1189): \[java] UnusedPrivateMethod false positive from inner class via external class
  * [#1205](https://github.com/pmd/pmd/issues/1205): \[java] Improve ConstantsInInterface message to mention alternatives
  * [#1212](https://github.com/pmd/pmd/issues/1212): \[java] Don't raise JUnitTestContainsTooManyAsserts on JUnit 5's assertAll
  * [#1422](https://github.com/pmd/pmd/issues/1422): \[java] JUnitTestsShouldIncludeAssert false positive with inherited @<!-- -->Rule field
  * [#1455](https://github.com/pmd/pmd/issues/1455): \[java] JUnitTestsShouldIncludeAssert: False positives for assert methods named "check" and "verify"
  * [#1563](https://github.com/pmd/pmd/issues/1563): \[java] ForLoopCanBeForeach false positive with method call using index variable
  * [#1565](https://github.com/pmd/pmd/issues/1565): \[java] JUnitAssertionsShouldIncludeMessage false positive with AssertJ
  * [#1747](https://github.com/pmd/pmd/issues/1747): \[java] PreserveStackTrace false-positive
  * [#1969](https://github.com/pmd/pmd/issues/1969): \[java] MissingOverride false-positive triggered by package-private method overwritten in another package by extending class
  * [#1998](https://github.com/pmd/pmd/issues/1998): \[java] AccessorClassGeneration false-negative: subclass calls private constructor
  * [#2130](https://github.com/pmd/pmd/issues/2130): \[java] UnusedLocalVariable: false-negative with array
  * [#2147](https://github.com/pmd/pmd/issues/2147): \[java] JUnitTestsShouldIncludeAssert - false positives with lambdas and static methods
  * [#2464](https://github.com/pmd/pmd/issues/2464): \[java] LooseCoupling must ignore class literals: ArrayList.class
  * [#2542](https://github.com/pmd/pmd/issues/2542): \[java] UseCollectionIsEmpty can not detect the case `foo.bar().size()`
  * [#2650](https://github.com/pmd/pmd/issues/2650): \[java] UseTryWithResources false positive when AutoCloseable helper used
  * [#2796](https://github.com/pmd/pmd/issues/2796): \[java] UnusedAssignment false positive with call chains
  * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
  * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
  * [#2822](https://github.com/pmd/pmd/issues/2822): \[java] LooseCoupling rule: Extend to cover user defined implementations and interfaces
  * [#2843](https://github.com/pmd/pmd/pull/2843):   \[java] Fix UnusedAssignment FP with field accesses
  * [#2882](https://github.com/pmd/pmd/issues/2882): \[java] UseTryWithResources - false negative for explicit close
  * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
  * [#2890](https://github.com/pmd/pmd/issues/2890): \[java] UnusedPrivateMethod false positive with generics
  * [#2946](https://github.com/pmd/pmd/issues/2946): \[java] SwitchStmtsShouldHaveDefault false positive on enum inside enums
  * [#3672](https://github.com/pmd/pmd/pull/3672):   \[java] LooseCoupling - fix false positive with generics
  * [#3675](https://github.com/pmd/pmd/pull/3675):   \[java] MissingOverride - fix false positive with mixing type vars
  * [#3858](https://github.com/pmd/pmd/issues/3858): \[java] UseCollectionIsEmpty should infer local variable type from method invocation
  * [#4433](https://github.com/pmd/pmd/issues/4433): \[java] \[7.0-rc1] ReplaceHashtableWithMap on java.util.Properties
  * [#4492](https://github.com/pmd/pmd/issues/4492): \[java] GuardLogStatement gives false positive when argument is a Java method reference
  * [#4503](https://github.com/pmd/pmd/issues/4503): \[java] JUnitTestsShouldIncludeAssert: false negative with TestNG
  * [#4516](https://github.com/pmd/pmd/issues/4516): \[java] UnusedLocalVariable: false-negative with try-with-resources
  * [#4517](https://github.com/pmd/pmd/issues/4517): \[java] UnusedLocalVariable: false-negative with compound assignments
  * [#4518](https://github.com/pmd/pmd/issues/4518): \[java] UnusedLocalVariable: false-positive with multiple for-loop indices
  * [#4603](https://github.com/pmd/pmd/issues/4603): \[java] UnusedAssignment false positive in record compact constructor
  * [#4625](https://github.com/pmd/pmd/issues/4625): \[java] UnusedPrivateMethod false positive: Autoboxing into Number
  * [#4634](https://github.com/pmd/pmd/issues/4634): \[java] JUnit4TestShouldUseTestAnnotation false positive with TestNG
* java-codestyle
  * [#1208](https://github.com/pmd/pmd/issues/1208): \[java] PrematureDeclaration rule false-positive on variable declared to measure time
  * [#1429](https://github.com/pmd/pmd/issues/1429): \[java] PrematureDeclaration as result of method call (false positive)
  * [#1480](https://github.com/pmd/pmd/issues/1480): \[java] IdenticalCatchBranches false positive with return expressions
  * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
  * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
  * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
  * [#2134](https://github.com/pmd/pmd/issues/2134): \[java] PreserveStackTrace not handling `Throwable.addSuppressed(...)`
  * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
  * [#2391](https://github.com/pmd/pmd/issues/2391): \[java] UseDiamondOperator FP when expected type and constructed type have a different parameterization
  * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support ParameterizedTest
  * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
  * [#2748](https://github.com/pmd/pmd/issues/2748): \[java] UnnecessaryCast false positive with unchecked cast
  * [#2847](https://github.com/pmd/pmd/issues/2847): \[java] New Rule: Use Explicit Types
  * [#2973](https://github.com/pmd/pmd/issues/2973): \[java] New rule: UnnecessaryBoxing
  * [#3195](https://github.com/pmd/pmd/pull/3195):   \[java] Improve rule UnnecessaryReturn to detect more cases
  * [#3218](https://github.com/pmd/pmd/pull/3218):   \[java] Generalize UnnecessaryCast to flag all unnecessary casts
  * [#3221](https://github.com/pmd/pmd/issues/3221): \[java] PrematureDeclaration false positive for unused variables
  * [#3238](https://github.com/pmd/pmd/issues/3238): \[java] Improve ExprContext, fix FNs of UnnecessaryCast
  * [#3500](https://github.com/pmd/pmd/pull/3500):   \[java] UnnecessaryBoxing - check for Integer.valueOf(String) calls
  * [#4239](https://github.com/pmd/pmd/issues/4239): \[java] UnnecessaryLocalBeforeReturn - false positive with catch clause
  * [#4268](https://github.com/pmd/pmd/issues/4268): \[java] CommentDefaultAccessModifier: false positive with TestNG annotations
  * [#4273](https://github.com/pmd/pmd/issues/4273): \[java] CommentDefaultAccessModifier ignoredAnnotations should include "org.junit.jupiter.api.extension.RegisterExtension" by default
  * [#4357](https://github.com/pmd/pmd/pull/4357):   \[java] Fix IllegalStateException in UseDiamondOperator rule
  * [#4432](https://github.com/pmd/pmd/issues/4432): \[java] \[7.0-rc1] UnnecessaryImport - Unused static import is being used
  * [#4455](https://github.com/pmd/pmd/issues/4455): \[java] FieldNamingConventions: false positive with lombok's @<!-- -->UtilityClass
  * [#4487](https://github.com/pmd/pmd/issues/4487): \[java] UnnecessaryConstructor: false-positive with @<!-- -->Inject and @<!-- -->Autowired
  * [#4511](https://github.com/pmd/pmd/issues/4511): \[java] LocalVariableCouldBeFinal shouldn't report unused variables
  * [#4512](https://github.com/pmd/pmd/issues/4512): \[java] MethodArgumentCouldBeFinal shouldn't report unused parameters
  * [#4557](https://github.com/pmd/pmd/issues/4557): \[java] UnnecessaryImport FP with static imports of overloaded methods
  * [#4578](https://github.com/pmd/pmd/issues/4578): \[java] CommentDefaultAccessModifier comment needs to be before annotation if present
  * [#4631](https://github.com/pmd/pmd/issues/4631): \[java] UnnecessaryFullyQualifiedName fails to recognize illegal self reference in enums
  * [#4645](https://github.com/pmd/pmd/issues/4645): \[java] CommentDefaultAccessModifier - False Positive with JUnit5's ParameterizedTest
  * [#4754](https://github.com/pmd/pmd/pull/4754):   \[java] EmptyControlStatementRule: Add allowCommentedBlocks property
  * [#4816](https://github.com/pmd/pmd/issues/4816): \[java] UnnecessaryImport false-positive on generic method call with on lambda
* java-design
  * [#174](https://github.com/pmd/pmd/issues/174):   \[java] SingularField false positive with switch in method that both assigns and reads field
  * [#1014](https://github.com/pmd/pmd/issues/1014): \[java] LawOfDemeter: False positive with lambda expression
  * [#1605](https://github.com/pmd/pmd/issues/1605): \[java] LawOfDemeter: False positive for standard UTF-8 charset name
  * [#2160](https://github.com/pmd/pmd/issues/2160): \[java] Issues with Law of Demeter
  * [#2175](https://github.com/pmd/pmd/issues/2175): \[java] LawOfDemeter: False positive for chained methods with generic method call
  * [#2179](https://github.com/pmd/pmd/issues/2179): \[java] LawOfDemeter: False positive with static property access - should treat class-level property as global object, not dot-accessed property
  * [#2180](https://github.com/pmd/pmd/issues/2180): \[java] LawOfDemeter: False positive with Thread and ThreadLocalRandom
  * [#2182](https://github.com/pmd/pmd/issues/2182): \[java] LawOfDemeter: False positive with package-private access
  * [#2188](https://github.com/pmd/pmd/issues/2188): \[java] LawOfDemeter: False positive with fields assigned to local vars
  * [#2536](https://github.com/pmd/pmd/issues/2536): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal can't detect inner class
  * [#3668](https://github.com/pmd/pmd/pull/3668):   \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - fix FP with inner private classes
  * [#3754](https://github.com/pmd/pmd/issues/3754): \[java] SingularField false positive with read in while condition
  * [#3786](https://github.com/pmd/pmd/issues/3786): \[java] SimplifyBooleanReturns should consider operator precedence
  * [#3840](https://github.com/pmd/pmd/issues/3840): \[java] LawOfDemeter disallows method call on locally created object
  * [#4238](https://github.com/pmd/pmd/pull/4238):   \[java] Make LawOfDemeter not use the rulechain
  * [#4254](https://github.com/pmd/pmd/issues/4254): \[java] ImmutableField - false positive with Lombok @<!-- -->Setter
  * [#4434](https://github.com/pmd/pmd/issues/4434): \[java] \[7.0-rc1] ExceptionAsFlowControl when simply propagating
  * [#4456](https://github.com/pmd/pmd/issues/4456): \[java] FinalFieldCouldBeStatic: false positive with lombok's @<!-- -->UtilityClass
  * [#4477](https://github.com/pmd/pmd/issues/4477): \[java] SignatureDeclareThrowsException: false-positive with TestNG annotations
  * [#4490](https://github.com/pmd/pmd/issues/4490): \[java] ImmutableField - false negative with Lombok @<!-- -->Getter
  * [#4549](https://github.com/pmd/pmd/pull/4549):   \[java] Make LawOfDemeter results deterministic
* java-documentation
  * [#4369](https://github.com/pmd/pmd/pull/4369):   \[java] Improve CommentSize
  * [#4416](https://github.com/pmd/pmd/pull/4416):   \[java] Fix reported line number in CommentContentRule
* java-errorprone
  * [#659](https://github.com/pmd/pmd/issues/659):   \[java] MissingBreakInSwitch - last default case does not contain a break
  * [#718](https://github.com/pmd/pmd/issues/718):   \[java] BrokenNullCheck false positive with parameter/field confusion
  * [#932](https://github.com/pmd/pmd/issues/932):   \[java] SingletonClassReturningNewInstance false positive with double assignment
  * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
  * [#1669](https://github.com/pmd/pmd/issues/1669): \[java] NullAssignment - FP with ternay and null as constructor argument
  * [#1831](https://github.com/pmd/pmd/issues/1831): \[java] DetachedTestCase reports abstract methods
  * [#1899](https://github.com/pmd/pmd/issues/1899): \[java] Recognize @<!-- -->SuppressWanings("fallthrough") for MissingBreakInSwitch
  * [#2320](https://github.com/pmd/pmd/issues/2320): \[java] NullAssignment - FP with ternary and null as method argument
  * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case `new BigDecimal(Expression)`
  * [#2579](https://github.com/pmd/pmd/issues/2579): \[java] MissingBreakInSwitch detects the lack of break in the last case
  * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
  * [#2893](https://github.com/pmd/pmd/issues/2893): \[java] Remove special cases from rule EmptyCatchBlock
  * [#2894](https://github.com/pmd/pmd/issues/2894): \[java] Improve MissingBreakInSwitch
  * [#3071](https://github.com/pmd/pmd/issues/3071): \[java] BrokenNullCheck FP with PMD 6.30.0
  * [#3087](https://github.com/pmd/pmd/issues/3087): \[java] UnnecessaryBooleanAssertion overlaps with SimplifiableTestAssertion
  * [#3100](https://github.com/pmd/pmd/issues/3100): \[java] UseCorrectExceptionLogging FP in 6.31.0
  * [#3173](https://github.com/pmd/pmd/issues/3173): \[java] UseProperClassLoader false positive
  * [#3351](https://github.com/pmd/pmd/issues/3351): \[java] ConstructorCallsOverridableMethod ignores abstract methods
  * [#3400](https://github.com/pmd/pmd/issues/3400): \[java] AvoidUsingOctalValues FN with underscores
  * [#3843](https://github.com/pmd/pmd/issues/3843): \[java] UseEqualsToCompareStrings should consider return type
  * [#4063](https://github.com/pmd/pmd/issues/4063): \[java] AvoidBranchingStatementAsLastInLoop: False-negative about try/finally block
  * [#4356](https://github.com/pmd/pmd/pull/4356):   \[java] Fix NPE in CloseResourceRule
  * [#4449](https://github.com/pmd/pmd/issues/4449): \[java] AvoidAccessibilityAlteration: Possible false positive in AvoidAccessibilityAlteration rule when using Lambda expression
  * [#4457](https://github.com/pmd/pmd/issues/4457): \[java] OverrideBothEqualsAndHashcode: false negative with anonymous classes
  * [#4493](https://github.com/pmd/pmd/issues/4493): \[java] MissingStaticMethodInNonInstantiatableClass: false-positive about @<!-- -->Inject
  * [#4505](https://github.com/pmd/pmd/issues/4505): \[java] ImplicitSwitchFallThrough NPE in PMD 7.0.0-rc1
  * [#4510](https://github.com/pmd/pmd/issues/4510): \[java] ConstructorCallsOverridableMethod: false positive with lombok's @<!-- -->Value
  * [#4513](https://github.com/pmd/pmd/issues/4513): \[java] UselessOperationOnImmutable various false negatives with String
  * [#4514](https://github.com/pmd/pmd/issues/4514): \[java] AvoidLiteralsInIfCondition false positive and negative for String literals when ignoreExpressions=true
  * [#4546](https://github.com/pmd/pmd/issues/4546): \[java] OverrideBothEqualsAndHashCode ignores records
  * [#4719](https://github.com/pmd/pmd/pull/4719):   \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string
* java-multithreading
  * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
  * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
  * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
  * [#4483](https://github.com/pmd/pmd/issues/4483): \[java] NonThreadSafeSingleton false positive with double-checked locking
* java-performance
  * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
  * [#2587](https://github.com/pmd/pmd/issues/2587): \[java] AvoidArrayLoops could also check for list copy through iterated List.add()
  * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ
  * [#3486](https://github.com/pmd/pmd/pull/3486):   \[java] InsufficientStringBufferDeclaration: Fix NPE
  * [#3848](https://github.com/pmd/pmd/issues/3848): \[java] StringInstantiation: false negative when using method result
  * [#4070](https://github.com/pmd/pmd/issues/4070): \[java] A false positive about the rule RedundantFieldInitializer
  * [#4458](https://github.com/pmd/pmd/issues/4458): \[java] RedundantFieldInitializer: false positive with lombok's @<!-- -->Value
* javascript
  * [#4673](https://github.com/pmd/pmd/pull/4673):   \[javascript] CPD: Added support for decorator notation
* kotlin
  * [#419](https://github.com/pmd/pmd/issues/419):   \[kotlin] Add support for Kotlin
  * [#4389](https://github.com/pmd/pmd/pull/4389):   \[kotlin] Update grammar to version 1.8
* plsql
  * [#4820](https://github.com/pmd/pmd/issues/4820): \[plsql] WITH clause is ignored for SELECT INTO statements
* swift
  * [#1877](https://github.com/pmd/pmd/pull/1877):   \[swift] Feature/swift rules
  * [#1882](https://github.com/pmd/pmd/pull/1882):   \[swift] UnavailableFunction Swift rule
  * [#4697](https://github.com/pmd/pmd/issues/4697): \[swift] Support Swift 5.9 features (mainly macros expansion expressions)
* xml
  * [#1800](https://github.com/pmd/pmd/pull/1800):   \[xml] Unimplement org.w3c.dom.Node from the XmlNodeWrapper
* xml-bestpractices
  * [#4592](https://github.com/pmd/pmd/pull/4592):   \[xml] Add MissingEncoding rule

## ✨ External Contributions

* [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1881](https://github.com/pmd/pmd/pull/1881): \[doc] Add ANTLR documentation - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca) (@tomidelucca)
* [#2830](https://github.com/pmd/pmd/pull/2830): \[apex] Apexlink POC - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#3866](https://github.com/pmd/pmd/pull/3866): \[core] Add CLI Progress Bar - [@JerritEic](https://github.com/JerritEic) (@JerritEic)
* [#4093](https://github.com/pmd/pmd/pull/4093): \[apex] Summit-AST Apex module - Part 1 - [Edward Klimoshenko](https://github.com/eklimo) (@eklimo)
* [#4151](https://github.com/pmd/pmd/pull/4151): \[apex] Summit-AST Apex module - Part 2 - expression nodes - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4171](https://github.com/pmd/pmd/pull/4171): \[apex] Summit-AST Apex module - Part 3 - initializers - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4206](https://github.com/pmd/pmd/pull/4206): \[apex] Summit-AST Apex module - Part 4 - statements - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4219](https://github.com/pmd/pmd/pull/4219): \[apex] Summit-AST Apex module - Part 5 - annotations, triggers, misc. - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4242](https://github.com/pmd/pmd/pull/4242): \[apex] Merge 6.52 into experimental-apex-parser - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4251](https://github.com/pmd/pmd/pull/4251): \[apex] Summit-AST Apex module - Part 6 Passing testsuite - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4402](https://github.com/pmd/pmd/pull/4402): \[javascript] CPD: add support for Typescript using antlr4 grammar - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4403](https://github.com/pmd/pmd/pull/4403): \[julia] CPD: Add support for Julia code duplication  - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4412](https://github.com/pmd/pmd/pull/4412): \[doc] Added new error msg to ConstantsInInterface - [David Ljunggren](https://github.com/dague1) (@dague1)
* [#4426](https://github.com/pmd/pmd/pull/4426): \[cpd] New XML to HTML XLST report format for PMD CPD - [mohan-chinnappan-n](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)
* [#4428](https://github.com/pmd/pmd/pull/4428): \[apex] ApexBadCrypto bug fix for #4427 - inline detection of hard coded values - [Steven Stearns](https://github.com/sfdcsteve) (@sfdcsteve)
* [#4431](https://github.com/pmd/pmd/pull/4431): \[coco] CPD: Coco support for code duplication detection - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4444](https://github.com/pmd/pmd/pull/4444): \[java] CommentDefaultAccessModifier - ignore org.junit.jupiter.api.extension.RegisterExtension by default - [Nirvik Patel](https://github.com/nirvikpatel) (@nirvikpatel)
* [#4448](https://github.com/pmd/pmd/pull/4448): \[apex] Bump summit-ast to new release 2.1.0 (and remove workaround) - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4450](https://github.com/pmd/pmd/pull/4450): \[java] Fix #4449 AvoidAccessibilityAlteration: Correctly handle Lambda expressions in PrivilegedAction scenarios - [Seren](https://github.com/mohui1999) (@mohui1999)
* [#4452](https://github.com/pmd/pmd/pull/4452): \[doc] Update PMD_APEX_ROOT_DIRECTORY documentation reference - [nwcm](https://github.com/nwcm) (@nwcm)
* [#4470](https://github.com/pmd/pmd/pull/4470): \[cpp] CPD: Added strings as literal and ignore identifiers in sequences - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4474](https://github.com/pmd/pmd/pull/4474): \[java] ImmutableField: False positive with lombok (fixes #4254) - [Pim van der Loos](https://github.com/PimvanderLoos) (@PimvanderLoos)
* [#4479](https://github.com/pmd/pmd/pull/4479): \[apex] Merge main (7.x) branch into experimental-apex-parser and fix tests - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4488](https://github.com/pmd/pmd/pull/4488): \[java] Fix #4477: A false-positive about SignatureDeclareThrowsException - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4494](https://github.com/pmd/pmd/pull/4494): \[java] Fix #4487: A false-positive about UnnecessaryConstructor and @<!-- -->Inject and @<!-- -->Autowired - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4495](https://github.com/pmd/pmd/pull/4495): \[java] Fix #4493: false-positive about MissingStaticMethodInNonInstantiatableClass and @<!-- -->Inject - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4507](https://github.com/pmd/pmd/pull/4507): \[java] Fix #4503: A false negative about JUnitTestsShouldIncludeAssert and testng - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4520](https://github.com/pmd/pmd/pull/4520): \[doc] Fix typo: missing closing quotation mark after CPD-END - [João Dinis Ferreira](https://github.com/joaodinissf) (@joaodinissf)
* [#4528](https://github.com/pmd/pmd/pull/4528): \[apex] Update to apexlink - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#4533](https://github.com/pmd/pmd/pull/4533): \[java] Fix #4063: False-negative about try/catch block in Loop - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4536](https://github.com/pmd/pmd/pull/4536): \[java] Fix #4268: CommentDefaultAccessModifier - false positive with TestNG's @<!-- -->Test annotation - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4537](https://github.com/pmd/pmd/pull/4537): \[java] Fix #4455: A false positive about FieldNamingConventions and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4538](https://github.com/pmd/pmd/pull/4538): \[java] Fix #4456: A false positive about FinalFieldCouldBeStatic and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4540](https://github.com/pmd/pmd/pull/4540): \[java] Fix #4457: false negative about OverrideBothEqualsAndHashcode - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4541](https://github.com/pmd/pmd/pull/4541): \[java] Fix #4458: A false positive about RedundantFieldInitializer and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4542](https://github.com/pmd/pmd/pull/4542): \[java] Fix #4510: A false positive about ConstructorCallsOverridableMethod and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4553](https://github.com/pmd/pmd/pull/4553): \[java] Fix #4492: GuardLogStatement gives false positive when argument is a Java method reference - [Anastasiia Koba](https://github.com/anastasiia-koba) (@anastasiia-koba)
* [#4637](https://github.com/pmd/pmd/pull/4637): \[java] fix #4634 - JUnit4TestShouldUseTestAnnotation false positive with TestNG - [Krystian Dabrowski](https://github.com/krdabrowski) (@krdabrowski)
* [#4640](https://github.com/pmd/pmd/pull/4640): \[cli] Launch script fails if run via "bash pmd" - [Shai Bennathan](https://github.com/shai-bennathan) (@shai-bennathan)
* [#4649](https://github.com/pmd/pmd/pull/4649): \[apex] Add SObjectType and SObjectField to list of injectable SOQL variable types - [Richard Corfield](https://github.com/rcorfieldffdc) (@rcorfieldffdc)
* [#4651](https://github.com/pmd/pmd/pull/4651): \[doc] Add "Tencent Cloud Code Analysis" in Tools / Integrations - [yale](https://github.com/cyw3) (@cyw3)
* [#4664](https://github.com/pmd/pmd/pull/4664): \[cli] CPD: Fix NPE when only `--file-list` is specified - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4665](https://github.com/pmd/pmd/pull/4665): \[java] Doc: Fix references AutoClosable -> AutoCloseable - [Andrey Bozhko](https://github.com/AndreyBozhko) (@AndreyBozhko)
* [#4673](https://github.com/pmd/pmd/pull/4673): \[javascript] CPD: Added support for decorator notation - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4677](https://github.com/pmd/pmd/pull/4677): \[apex] Add new rule: OperationWithHighCostInLoop - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#4698](https://github.com/pmd/pmd/pull/4698): \[swift] Add macro expansion support for swift 5.9 - [Richard B.](https://github.com/kenji21) (@kenji21)
* [#4706](https://github.com/pmd/pmd/pull/4706): \[java] DetachedTestCase should not report on abstract methods - [Debamoy Datta](https://github.com/Debamoy) (@Debamoy)
* [#4719](https://github.com/pmd/pmd/pull/4719): \[java] UnnecessaryCaseChange: example doc toUpperCase() should compare to a capitalized string - [ciufudean](https://github.com/ciufudean) (@ciufudean)
* [#4738](https://github.com/pmd/pmd/pull/4738): \[doc] Added reference to the PMD extension for bld - [Erik C. Thauvin](https://github.com/ethauvin) (@ethauvin)
* [#4749](https://github.com/pmd/pmd/pull/4749):   Fixes NoSuchMethodError on processing errors in pmd-compat6 - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4750](https://github.com/pmd/pmd/pull/4750): \[core] Fix flaky SummaryHTMLRenderer - [219sansim](https://github.com/219sansim) (@219sansim)
* [#4754](https://github.com/pmd/pmd/pull/4754): \[java] EmptyControlStatementRule: Add allowCommentedBlocks property - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4759](https://github.com/pmd/pmd/pull/4759): \[java] fix: remove delimiter attribute from ruleset category/java/errorprone.xml - [Marcin Dąbrowski](https://github.com/marcindabrowski) (@marcindabrowski)
* [#4825](https://github.com/pmd/pmd/pull/4825): \[plsql] Fix ignored WITH clause for SELECT INTO statements - [Laurent Bovet](https://github.com/lbovet) (@lbovet)
