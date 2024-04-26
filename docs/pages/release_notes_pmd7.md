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
in the program, for instance to detect [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryboxing)
or [`UseDiamondOperator`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#usediamondoperator).
These are just a small preview of the new rules we will be adding in the PMD 7 release cycle.

Overall, the changes to the parser, AST, type resolution and symbol table code has made PMD for
Java **significantly faster**. On average, we have seen ~2-3X faster analysis, but as usual, this may change
depending on your workload, configuration and ruleset.

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

<div class="alert alert-info" role="alert" markdown="block"><i class="fas fa-info-circle"></i> <b>Note:</b>
The full detailed documentation of the changes to the Java AST are available in the
[Migration Guide for PMD 7](pmd_userdocs_migrating_to_pmd7.html#java-ast)
</div>

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

* [`FunctionNameTooShort`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_kotlin_bestpractices.html#functionnametooshort) finds functions with a too
  short name.
* [`OverrideBothEqualsAndHashcode`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_kotlin_errorprone.html#overridebothequalsandhashcode) finds classes with only
  either `equals` or `hashCode` overridden, but not both. This leads to unexpected behavior once instances
  of such classes are used in collections (Lists, HashMaps, ...).

Contributors: [Jeroen Borgers](https://github.com/jborgers) (@jborgers),
[Peter Paul Bakker](https://github.com/stokpop) (@stokpop)

### New: Swift support

Given the full Antlr support, PMD now fully supports Swift for creating rules. Previously only CPD was supported.

Note: There is only limited support for newer Swift language features in the parser, e.g. Swift 5.9 (Macro Expansions)
are supported, but other features are not.

We are pleased to announce we are shipping a number of rules starting with PMD 7.

* [`ForceCast`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_errorprone.html#forcecast) flags all force casts, making sure you are
  defensively considering all types. Having the application crash shouldn't be an option.
* [`ForceTry`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_errorprone.html#forcetry) flags all force tries, making sure you are
  defensively handling exceptions. Having the application crash shouldn't be an option.
* [`ProhibitedInterfaceBuilder`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_bestpractices.html#prohibitedinterfacebuilder) flags any usage of interface
  builder. Interface builder files are prone to merge conflicts, and are impossible to code review, so larger
  teams usually try to avoid it or reduce its usage.
* [`UnavailableFunction`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_bestpractices.html#unavailablefunction) flags any function throwing
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
With the new Apex parser, the new language constructs like
[User Mode Database Operations](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_enforce_usermode.htm)
and the new [Null Coalescing Operator `??`](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/langCon_apex_NullCoalescingOperator.htm)
can be parsed now. PMD should be able to parse Apex code up to version 60.0 (Spring '24).

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
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#"><code>PropertyFactory</code></a> to create properties.
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
* [`OperationWithHighCostInLoop`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_performance.html#operationwithhighcostinloop) finds Schema class methods called in a loop, which is a
  potential performance issue.
* [`UnusedMethod`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_design.html#unusedmethod) finds unused methods in your code.

**Java**
* [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryboxing) reports boxing and unboxing conversions that may be made implicit.
* [`UseExplicitTypes`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#useexplicittypes) reports usages of `var` keyword, which was introduced with Java 10.

**Kotlin**
* [`FunctionNameTooShort`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_kotlin_bestpractices.html#functionnametooshort) finds functions with a too short name.
* [`OverrideBothEqualsAndHashcode`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_kotlin_errorprone.html#overridebothequalsandhashcode) finds classes with only
  either `equals` or `hashCode` overridden, but not both. This leads to unexpected behavior once instances
  of such classes are used in collections (Lists, HashMaps, ...).

**Swift**
* [`ForceCast`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_errorprone.html#forcecast) flags all force casts, making sure you are
  defensively considering all types. Having the application crash shouldn't be an option.
* [`ForceTry`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_errorprone.html#forcetry) flags all force tries, making sure you are
  defensively handling exceptions. Having the application crash shouldn't be an option.
* [`ProhibitedInterfaceBuilder`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_bestpractices.html#prohibitedinterfacebuilder) flags any usage of interface
  builder. Interface builder files are prone to merge conflicts, and are impossible to code review, so larger
  teams usually try to avoid it or reduce its usage.
* [`UnavailableFunction`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_swift_bestpractices.html#unavailablefunction) flags any function throwing
  a `fatalError` not marked as `@available(*, unavailable)` to ensure no calls are actually performed in
  the codebase.

**XML**
* [`MissingEncoding`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_xml_bestpractices.html#missingencoding) finds XML files without explicit encoding.

### Changed Rules

**General changes**

* All statistical rules (like ExcessiveClassLength, ExcessiveParameterList) have been simplified and unified.
  The properties `topscore` and `sigma` have been removed. The property `minimum` is still there, however the type is not
  a decimal number anymore but has been changed to an integer. This affects rules in the languages Apex, Java, PLSQL
  and Velocity Template Language (velocity):
    * Apex: [`ExcessiveClassLength`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_design.html#excessiveclasslength), [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_design.html#excessiveparameterlist),
      [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_design.html#excessivepubliccount), [`NcssConstructorCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_design.html#ncssconstructorcount),
      [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_design.html#ncssmethodcount), [`NcssTypeCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_design.html#ncsstypecount)
    * Java: [`ExcessiveImports`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#excessiveimports), [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#excessiveparameterlist),
      [`ExcessivePublicCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#excessivepubliccount), [`SwitchDensity`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#switchdensity)
    * PLSQL: [`ExcessiveMethodLength`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_plsql_design.html#excessivemethodlength), [`ExcessiveObjectLength`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_plsql_design.html#excessiveobjectlength),
      [`ExcessivePackageBodyLength`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_plsql_design.html#excessivepackagebodylength), [`ExcessivePackageSpecificationLength`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_plsql_design.html#excessivepackagespecificationlength),
      [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_plsql_design.html#excessiveparameterlist), [`ExcessiveTypeLength`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_plsql_design.html#excessivetypelength),
      [`NcssMethodCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_plsql_design.html#ncssmethodcount), [`NcssObjectCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_plsql_design.html#ncssobjectcount),
      [`NPathComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_plsql_design.html#npathcomplexity)
    * Velocity: [`ExcessiveTemplateLength`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_velocity_design.html#excessivetemplatelength)

* The general property `violationSuppressXPath` which is available for all rules to
  [suppress warnings](pmd_userdocs_suppressing_warnings.html) now uses XPath version 3.1 by default.
  This version of the XPath language is mostly identical to XPath 2.0. In PMD 6, XPath 1.0 has been used.
  If you upgrade from PMD 6, you need to verify your `violationSuppressXPath` properties.

**Apex General changes**

* The properties `cc_categories`, `cc_remediation_points_multiplier`, `cc_block_highlighting` have been removed
  from all rules. These properties have been deprecated since PMD 6.13.0.
  See [issue #1648](https://github.com/pmd/pmd/issues/1648) for more details.

**Apex Codestyle**

* [`MethodNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_codestyle.html#methodnamingconventions): The deprecated rule property `skipTestMethodUnderscores` has
  been removed. It was actually deprecated since PMD 6.15.0, but was not mentioned in the release notes
  back then. Use the property `testPattern` instead to configure valid names for test methods.

**Java General changes**

* Violations reported on methods or classes previously reported the line range of the entire method
  or class. With PMD 7.0.0, the reported location is now just the identifier of the method or class.
  This affects various rules, e.g. [`CognitiveComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#cognitivecomplexity).

  The report location is controlled by the overrides of the method <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#getReportLocation()"><code>Node#getReportLocation</code></a>
  in different node types.

  See [issue #4439](https://github.com/pmd/pmd/issues/4439) and [issue #730](https://github.com/pmd/pmd/issues/730)
  for more details.

**Java Best Practices**

* [`ArrayIsStoredDirectly`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#arrayisstoreddirectly): Violations are now reported on the assignment and not
  anymore on the formal parameter. The reported line numbers will probably move.
* [`AvoidReassigningLoopVariables`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#avoidreassigningloopvariables): This rule might not report anymore all
  reassignments of the control variable in for-loops when the property `forReassign` is set to `skip`.
  See [issue #4500](https://github.com/pmd/pmd/issues/4500) for more details.
* [`LooseCoupling`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#loosecoupling): The rule has a new property to allow some types to be coupled
  to (`allowedTypes`).
* [`UnusedLocalVariable`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#unusedlocalvariable): This rule has some important false-negatives fixed
  and finds many more cases now. For details see issues [#2130](https://github.com/pmd/pmd/issues/2130),
  [#4516](https://github.com/pmd/pmd/issues/4516), and [#4517](https://github.com/pmd/pmd/issues/4517).

**Java Codestyle**

* [`MethodNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#methodnamingconventions): The property `checkNativeMethods` has been removed. The
  property was deprecated since PMD 6.3.0. Use the property `nativePattern` to control whether native methods
  should be considered or not.
* [`ShortVariable`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#shortvariable): This rule now also reports short enum constant names.
* [`UseDiamondOperator`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#usediamondoperator): The property `java7Compatibility` has been removed. The rule now
  handles Java 7 properly without a property.
* [`UnnecessaryFullyQualifiedName`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryfullyqualifiedname): The rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved
  to be more precise.
* [`UselessParentheses`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#uselessparentheses): The rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.
* [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement): The rule has a new property to allow empty blocks when
  they contain a comment (`allowCommentedBlocks`).

**Java Design**

* [`CyclomaticComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#cyclomaticcomplexity): The property `reportLevel` has been removed. The property was
  deprecated since PMD 6.0.0. The report level can now be configured separated for classes and methods using
  `classReportLevel` and `methodReportLevel` instead.
* [`ImmutableField`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#immutablefield): The property `ignoredAnnotations` has been removed. The property was
  deprecated since PMD 6.52.0.
* [`LawOfDemeter`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#lawofdemeter): The rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.
* [`NPathComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#npathcomplexity): The property `minimum` has been removed. It was deprecated since PMD 6.0.0.
  Use the property `reportLevel` instead.
* [`SingularField`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#singularfield): The properties `checkInnerClasses` and `disallowNotAssignment` have been removed.
  The rule is now more precise and will check these cases properly.
* [`UseUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#useutilityclass): The property `ignoredAnnotations` has been removed.

**Java Documentation**

* [`CommentContent`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_documentation.html#commentcontent): The properties `caseSensitive` and `disallowedTerms` are removed. The
  new property `forbiddenRegex` can be used now to define the disallowed terms with a single regular
  expression.
* [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_documentation.html#commentrequired):
    * Overridden methods are now detected even without the `@Override`
      annotation. This is relevant for the property `methodWithOverrideCommentRequirement`.
      See also [pull request #3757](https://github.com/pmd/pmd/pull/3757).
    * Elements in annotation types are now detected as well. This might lead to an increased number of violations
      for missing public method comments.
    * The deprecated property `headerCommentRequirement` has been removed. Use the property `classCommentRequirement`
      instead.
* [`CommentSize`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_documentation.html#commentsize): When determining the line-length of a comment, the leading comment
  prefix markers (e.g. `*` or `//`) are ignored and don't add up to the line-length.
  See also [pull request #4369](https://github.com/pmd/pmd/pull/4369).

**Java Error Prone**

* [`AvoidDuplicateLiterals`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#avoidduplicateliterals): The property `exceptionfile` has been removed. The property was
  deprecated since PMD 6.10.0. Use the property `exceptionList` instead.
* [`DontImportSun`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#dontimportsun): `sun.misc.Signal` is not special-cased anymore.
* [`EmptyCatchBlock`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#emptycatchblock): `CloneNotSupportedException` and `InterruptedException` are not
  special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* [`ImplicitSwitchFallThrough`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#implicitswitchfallthrough): Violations are now reported on the case statements
  rather than on the switch statements. This is more accurate but might result in more violations now.
* [`NonSerializableClass`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#nonserializableclass): The deprecated property `prefix` has been removed
  without replacement. In a serializable class all fields have to be serializable regardless of the name.

### Deprecated Rules

In PMD 7.0.0, there are no deprecated rules.

### Removed Rules

The following previously deprecated rules have been finally removed:

**Apex**

* performance.xml/AvoidSoqlInLoops&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`OperationWithLimitsInLoop`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_performance.html#operationwithlimitsinloop)
* performance.xml/AvoidSoslInLoops&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`OperationWithLimitsInLoop`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_performance.html#operationwithlimitsinloop)
* performance.xml/AvoidDmlStatementsInLoops&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`OperationWithLimitsInLoop`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_performance.html#operationwithlimitsinloop)
* codestyle.xml/VariableNamingConventions&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`FieldNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_codestyle.html#fieldnamingconventions),
  [`FormalParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_codestyle.html#formalparameternamingconventions), [`LocalVariableNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_codestyle.html#localvariablenamingconventions),
  or [`PropertyNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_codestyle.html#propertynamingconventions)
* security.xml/ApexCSRF&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`ApexCSRF`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_errorprone.html#apexcsrf)

**Java**

* codestyle.xml/AbstractNaming&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`ClassNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#classnamingconventions)
* codestyle.xml/AvoidFinalLocalVariable&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ not replaced
* codestyle.xml/AvoidPrefixingMethodParameters&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`FormalParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#formalparameternamingconventions)
* performance.xml/AvoidUsingShortType&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ not replaced
* errorprone.xml/BadComparison&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`ComparisonWithNaN`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#comparisonwithnan)
* errorprone.xml/BeanMembersShouldSerialize&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`NonSerializableClass`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#nonserializableclass)
* performance.xml/BooleanInstantiation&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryboxing)
  and [`PrimitiveWrapperInstantiation`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#primitivewrapperinstantiation)
* performance.xml/ByteInstantiation&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryboxing)
  and [`PrimitiveWrapperInstantiation`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#primitivewrapperinstantiation)
* errorprone.xml/CloneThrowsCloneNotSupportedException&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ not replaced
* errorprone.xml/DataflowAnomalyAnalysis&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnusedAssignment`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#unusedassignment)
* codestyle.xml/DefaultPackage&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`CommentDefaultAccessModifier`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier)
* errorprone.xml/DoNotCallSystemExit&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`DoNotTerminateVM`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#donotterminatevm)
* codestyle.xml/DontImportJavaLang&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessaryImport`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryimport)
* codestyle.xml/DuplicateImports&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessaryImport`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryimport)
* errorprone.xml/EmptyFinallyBlock&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
* errorprone.xml/EmptyIfStmt&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
* errorprone.xml/EmptyInitializer&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
* errorprone.xml/EmptyStatementBlock&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
* errorprone.xml/EmptyStatementNotInLoop&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessarySemicolon`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessarysemicolon)
* errorprone.xml/EmptySwitchStatements&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
* errorprone.xml/EmptySynchronizedBlock&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
* errorprone.xml/EmptyTryBlock&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
* errorprone.xml/EmptyWhileStmt&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`EmptyControlStatement`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#emptycontrolstatement)
* design.xml/ExcessiveClassLength&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#ncsscount)
* design.xml/ExcessiveMethodLength&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#ncsscount)
* codestyle.xml/ForLoopsMustUseBraces&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`ControlStatementBraces`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#controlstatementbraces)
* codestyle.xml/IfElseStmtsMustUseBraces&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`ControlStatementBraces`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#controlstatementbraces)
* codestyle.xml/IfStmtsMustUseBraces&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`ControlStatementBraces`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#controlstatementbraces)
* errorprone.xml/ImportFromSamePackage&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessaryImport`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryimport)
* performance.xml/IntegerInstantiation&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryboxing)
  and [`PrimitiveWrapperInstantiation`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#primitivewrapperinstantiation)
* errorprone.xml/InvalidSlf4jMessageFormat&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️  use [`InvalidLogMessageFormat`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#invalidlogmessageformat)
* errorprone.xml/LoggerIsNotStaticFinal&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`ProperLogger`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#properlogger)
* performance.xml/LongInstantiation&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryboxing)
  and [`PrimitiveWrapperInstantiation`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#primitivewrapperinstantiation)
* codestyle.xml/MIsLeadingVariableName&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`FieldNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#fieldnamingconventions),
  [`FormalParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#formalparameternamingconventions),
  or [`LocalVariableNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#localvariablenamingconventions)
* errorprone.xml/MissingBreakInSwitch&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️  use [`ImplicitSwitchFallThrough`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#implicitswitchfallthrough)
* design.xml/ModifiedCyclomaticComplexity&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`CyclomaticComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#cyclomaticcomplexity)
* design.xml/NcssConstructorCount&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#ncsscount)
* design.xml/NcssMethodCount&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#ncsscount)
* design.xml/NcssTypeCount&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`NcssCount`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#ncsscount)
* bestpractices.xml/PositionLiteralsFirstInCaseInsensitiveComparisons&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️
  use [`LiteralsFirstInComparisons`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#literalsfirstincomparisons)
* bestpractices.xml/PositionLiteralsFirstInComparisons&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️
  use [`LiteralsFirstInComparisons`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#literalsfirstincomparisons)
* errorprone.xml/ReturnEmptyArrayRatherThanNull&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️
  use [`ReturnEmptyCollectionRatherThanNull`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_errorprone.html#returnemptycollectionratherthannull)
* performance.xml/ShortInstantiation&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryboxing)
  and [`PrimitiveWrapperInstantiation`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#primitivewrapperinstantiation)
* design.xml/SimplifyBooleanAssertion&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`SimplifiableTestAssertion`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#simplifiabletestassertion)
* performance.xml/SimplifyStartsWith&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ not replaced
* design.xml/StdCyclomaticComplexity&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`CyclomaticComplexity`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_design.html#cyclomaticcomplexity)
* codestyle.xml/SuspiciousConstantFieldName&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`FieldNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#fieldnamingconventions)
* performance.xml/UnnecessaryWrapperObjectCreation&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use the new rule [`UnnecessaryBoxing`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryboxing)
* multithreading.xml/UnsynchronizedStaticDateFormatter&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnsynchronizedStaticFormatter`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_multithreading.html#unsynchronizedstaticformatter)
* bestpractices.xml/UnusedImports&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`UnnecessaryImport`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#unnecessaryimport)
* bestpractices.xml/UseAssertEqualsInsteadOfAssertTrue&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`SimplifiableTestAssertion`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#simplifiabletestassertion)
* bestpractices.xml/UseAssertNullInsteadOfAssertEquals&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`SimplifiableTestAssertion`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#simplifiabletestassertion)
* bestpractices.xml/UseAssertSameInsteadOfAssertEquals&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`SimplifiableTestAssertion`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#simplifiabletestassertion)
* bestpractices.xml/UseAssertTrueInsteadOfAssertEquals&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`SimplifiableTestAssertion`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_bestpractices.html#simplifiabletestassertion)
* codestyle.xml/VariableNamingConventions&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`FieldNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#fieldnamingconventions),
  [`FormalParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#formalparameternamingconventions), or [`LocalVariableNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#localvariablenamingconventions)
* codestyle.xml/WhileLoopsMustUseBraces&nbsp;<span style='font-size: small;'>(deleted)</span> ➡️ use [`ControlStatementBraces`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_java_codestyle.html#controlstatementbraces)

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

<div class="alert alert-info" role="alert" markdown="block"><i class="fas fa-info-circle"></i> <b>Note:</b>
The full detailed documentation of the changes are available in the
[Migration Guide for PMD 7](pmd_userdocs_migrating_to_pmd7.html)
</div>

### For endusers

* PMD 7 requires Java 8 or above to execute.
* CLI changed: Custom scripts need to be updated (`run.sh pmd ...` ➡️ `pmd check ...`, `run.sh cpd ...` ➡️ `pmd cpd ...`).
* Java module revamped: Custom rules need to be updated.
* Removed rules: Custom rulesets need to be reviewed. See above for a list of new and removed rules.
* XPath 1.0 and 2.0 support is removed, `violationSuppressXPath` now requires XPath 3.1: Custom rulesets need
  to be reviewed.
* Custom rules using rulechains: Need to override <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/AbstractRule.html#buildTargetSelector()"><code>AbstractRule#buildTargetSelector</code></a>
  using <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleTargetSelector.html#forTypes(java.lang.Class,java.lang.Class...)"><code>RuleTargetSelector#forTypes</code></a>.
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
  main class <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a> is gone.

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

* All API related to XPath support has been moved to the package <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/xpath/package-summary.html#"><code>net.sourceforge.pmd.lang.rule.xpath</code></a>.
  This includes API that was previously dispersed over `net.sourceforge.pmd.lang`, `net.sourceforge.pmd.lang.ast.xpath`,
  `net.sourceforge.pmd.lang.rule.xpath`, `net.sourceforge.pmd.lang.rule`, and various language-specific packages
  (which were made internal).

* The implementation of the Ant integration has been moved from the module `pmd-core` to a new module `pmd-ant`.
  This involves classes in package <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.0.0/net/sourceforge/pmd/ant/package-summary.html#"><code>net.sourceforge.pmd.ant</code></a>. The ant CPDTask class `net.sourceforge.pmd.cpd.CPDTask`
  has been moved into the same package <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.0.0/net/sourceforge/pmd/ant/package-summary.html#"><code>net.sourceforge.pmd.ant</code></a>. You'll need to update your taskdef entries in your
  build.xml files with the FQCN <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.0.0/net/sourceforge/pmd/ant/CPDTask.html#"><code>net.sourceforge.pmd.ant.CPDTask</code></a> if you use it anywhere.

* Utility classes in <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/package-summary.html#"><code>net.sourceforge.pmd.util</code></a>, that have previously marked as `@InternalApi` have been finally
  moved to an internal sub package and are now longer available.
  This includes ClasspathClassLoader, FileFinder, FileUtil, and IOUtil.

* The following utility classes in <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/package-summary.html#"><code>net.sourceforge.pmd.util</code></a> are now considered public API:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/AssertionUtil.html#"><code>AssertionUtil</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/CollectionUtil.html#"><code>CollectionUtil</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/ContextedAssertionError.html#"><code>ContextedAssertionError</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/ContextedStackOverflowError.html#"><code>ContextedStackOverflowError</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/GraphUtil.html#"><code>GraphUtil</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/IteratorUtil.html#"><code>IteratorUtil</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/StringUtil.html#"><code>StringUtil</code></a>

* Moved the two classes <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/impl/AntlrCpdLexer.html#"><code>AntlrCpdLexer</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/impl/JavaccCpdLexer.html#"><code>JavaccCpdLexer</code></a> from
  `internal` package into package <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/impl/package-summary.html#"><code>net.sourceforge.pmd.cpd.impl</code></a>. These two classes are part of the API and
  are base classes for CPD language implementations. Since 7.0.0-rc2.
  Note: These two classes have been previously called "AntlrTokenizer" and "JavaCCTokenizer".
* `AntlrBaseRule` is gone in favor of <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/AbstractVisitorRule.html#"><code>AbstractVisitorRule</code></a>. Since 7.0.0-rc2.
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

To make this API as accessible as possible, the <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a> interface has been fitted with new
methods producing node streams. Those methods replace previous tree traversal methods like `Node#findDescendantsOfType`.
In all cases, they should be more efficient and more convenient.

See <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/NodeStream.html#"><code>NodeStream</code></a> for more details.

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala)

### Metrics framework

The metrics framework has been made simpler and more general.

* The metric interface takes an additional type parameter, representing the result type of the metric. This is
  usually `Integer` or `Double`. It avoids widening the result to a `double` just to narrow it down.

  This makes it so, that `Double.NaN` is not an appropriate sentinel value to represent "not supported" anymore.
  Instead, `computeFor` may return `null` in that case (or a garbage value). The value `null` may have caused
  problems with the narrowing casts, which through unboxing, might have thrown an NPE. But when we deprecated
  the language-specific metrics façades to replace them with the generic <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/metrics/MetricsUtil.html#"><code>MetricsUtil</code></a>,
  we took care of making
  the new methods throw an exception if the metric cannot be computed on the parameter. This forces you to guard
  calls to <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/metrics/MetricsUtil.html#computeMetric(net.sourceforge.pmd.lang.metrics.Metric,N)"><code>MetricsUtil#computeMetric</code></a> (and other overloads)
  with something like `if (metric.supports(node))`. If you're following
  this pattern, then you won't observe the undefined behavior.

* The `MetricKey` interface is not so useful and has been merged into the <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/metrics/Metric.html#"><code>Metric</code></a>
  interface and removed. So the <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/metrics/Metric.html#"><code>Metric</code></a> interface has the new method
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/metrics/Metric.html#displayName()"><code>displayName</code></a>.

* The framework is not tied to at most 2 node types per language anymore. Previously those were nodes for
  classes and for methods/constructors. Instead, many metrics support more node types. For example, NCSS can
  be computed on any code block.

  For that reason, keeping around a hard distinction between "class metrics" and "operation metrics" is not
  useful. So in the Java framework for example, we removed the interfaces `JavaClassMetric`, `JavaOperationMetric`,
  abstract classes for those, `JavaClassMetricKey`, and `JavaOperationMetricKey`. Metric constants are now all
  inside the <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.html#"><code>JavaMetrics</code></a> utility class. The same was done in the Apex framework.

  We don't really need abstract classes for metrics now. So `AbstractMetric` is also removed from pmd-core.
  There is a factory method on the <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/metrics/Metric.html#"><code>Metric</code></a> interface to create a metric easily.

* This makes it so, that <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/metrics/LanguageMetricsProvider.html#"><code>LanguageMetricsProvider</code></a> does not need type parameters.
  It can just return a `Set<Metric<?, ?>>` to list available metrics.

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/metrics/Signature.html#"><code>Signature</code></a>s, their implementations, and the interface `SignedNode` have been
  removed. Node streams allow replacing their usages very easily.

### Testing framework

* PMD 7 has been upgraded to use JUnit 5 only. That means, that JUnit4 related classes have been removed, namely
    * `net.sourceforge.pmd.testframework.PMDTestRunner`
    * `net.sourceforge.pmd.testframework.RuleTestRunner`
    * `net.sourceforge.pmd.testframework.TestDescriptor`
* Rule tests, that use <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.0.0/net/sourceforge/pmd/test/SimpleAggregatorTst.html#"><code>SimpleAggregatorTst</code></a> or
  <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.0.0/net/sourceforge/pmd/test/PmdRuleTst.html#"><code>PmdRuleTst</code></a> work as before without change, but use
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
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#"><code>PropertyFactory</code></a> to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

### New Programmatic API for CPD

This release introduces a new programmatic API to replace the old class <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPD.html#"><code>CPD</code></a>. The new API uses a similar model to
<a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PmdAnalysis.html#"><code>PmdAnalysis</code></a> and is called <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CpdAnalysis.html#"><code>CpdAnalysis</code></a>. Programmatic execution of CPD should now be
done with a <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#"><code>CPDConfiguration</code></a> and a <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CpdAnalysis.html#"><code>CpdAnalysis</code></a>, for instance:

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
* <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTPattern.html#"><code>ASTPattern</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTRecordPattern.html#"><code>ASTRecordPattern</code></a>,
  <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTypePattern.html#"><code>ASTTypePattern</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTUnnamedPattern.html#"><code>ASTUnnamedPattern</code></a>
    - method `getParenthesisDepth()` has been removed.
* <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTemplateFragment.html#"><code>ASTTemplateFragment</code></a>: To get the content of the template, use now
  <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTemplateFragment.html#getContent()"><code>getContent</code></a> or `@Content` instead of `getImage()`/`@Image`.
* <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTUnnamedPattern.html#"><code>ASTUnnamedPattern</code></a> is not experimental anymore. The language feature
  has been standardized with Java 22.

**New API**

The API around <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/treeexport/TreeRenderer.html#"><code>TreeRenderer</code></a> has been declared as stable. It was previously
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
    * Many types have been moved from the base package `net.sourceforge.pmd` into subpackage <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/package-summary.html#"><code>net.sourceforge.pmd.lang.rule</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/Rule.html#"><code>Rule</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RulePriority.html#"><code>RulePriority</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSet.html#"><code>RuleSet</code></a>
        * `RuleSetFactory`
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSetLoader.html#"><code>RuleSetLoader</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSetLoadException.html#"><code>RuleSetLoadException</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSetWriter.html#"><code>RuleSetWriter</code></a>
    * Many types have been moved from the base package `net.sourceforge.pmd` into subpackage <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/package-summary.html#"><code>net.sourceforge.pmd.reporting</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Report.html#"><code>Report</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleContext.html#"><code>RuleContext</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#"><code>RuleViolation</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/ViolationSuppressor.html#"><code>ViolationSuppressor</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/xpath/XPathRule.html#"><code>XPathRule</code></a> has been moved into subpackage <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/xpath/package-summary.html#"><code>net.sourceforge.pmd.lang.rule.xpath</code></a>.
* pmd-html
    * `net.sourceforge.pmd.lang.html.ast.HtmlCpdLexer` moved into package `cpd`: <a href="https://docs.pmd-code.org/apidocs/pmd-html/7.0.0/net/sourceforge/pmd/lang/html/cpd/HtmlCpdLexer.html#"><code>HtmlCpdLexer</code></a>.
* pmd-lang-test: All types have been moved under the new base package <a href="https://docs.pmd-code.org/apidocs/pmd-lang-test/7.0.0/net/sourceforge/pmd/lang/test/package-summary.html#"><code>net.sourceforge.pmd.lang.test</code></a>:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-lang-test/7.0.0/net/sourceforge/pmd/lang/test/AbstractMetricTestRule.html#"><code>AbstractMetricTestRule</code></a> (moved from `net.sourceforge.pmd.test.AbstractMetricTestRule`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-lang-test/7.0.0/net/sourceforge/pmd/lang/test/BaseTextComparisonTest.html#"><code>BaseTextComparisonTest</code></a> (moved from `net.sourceforge.pmd.test.BaseTextComparisonTest`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-lang-test/7.0.0/net/sourceforge/pmd/lang/test/cpd/CpdTextComparisonTest.html#"><code>CpdTextComparisonTest</code></a> (moved from `net.sourceforge.pmd.cpd.test.CpdTextComparisonTest`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-lang-test/7.0.0/net/sourceforge/pmd/lang/test/ast/BaseTreeDumpTest.html#"><code>BaseTreeDumpTest</code></a> (moved from `net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest`)
    * And many other types have been moved from `net.sourceforge.pmd.lang.ast.test` to `net.sourceforge.pmd.lang.test`.
* pmd-scala
    * <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/7.0.0/net/sourceforge/pmd/lang/scala/cpd/ScalaCpdLexer.html#"><code>ScalaCpdLexer</code></a> (moved from `net.sourceforge.pmd.lang.scala.cpd.ScalaCpdLexer`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/7.0.0/net/sourceforge/pmd/lang/scala/cpd/ScalaTokenAdapter.html#"><code>ScalaTokenAdapter</code></a> (moved from `net.sourceforge.pmd.lang.scala.cpd.ScalaTokenAdapter`)
* pmd-test
    * <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.0.0/net/sourceforge/pmd/test/lang/rule/AbstractRuleSetFactoryTest.html#"><code>AbstractRuleSetFactoryTest</code></a> (moved from `net.sourceforge.pmd.lang.rule.AbstractRuleSetFactoryTest`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.0.0/net/sourceforge/pmd/test/AbstractAntTestHelper.html#"><code>AbstractAntTestHelper</code></a> (moved from `net.sourceforge.pmd.ant.AbstractAntTestHelper`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.0.0/net/sourceforge/pmd/test/AbstractLanguageVersionTest.html#"><code>AbstractLanguageVersionTest</code></a> (moved from `net.sourceforge.pmd.AbstractLanguageVersionTest`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.0.0/net/sourceforge/pmd/test/PmdRuleTst.html#"><code>PmdRuleTst</code></a> (moved from `net.sourceforge.pmd.testframework.PmdRuleTst`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.0.0/net/sourceforge/pmd/test/RuleTst.html#"><code>RuleTst</code></a> (moved from `net.sourceforge.pmd.testframework.RuleTst`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-test/7.0.0/net/sourceforge/pmd/test/SimpleAggregatorTst.html#"><code>SimpleAggregatorTst</code></a> (moved from `net.sourceforge.pmd.testframework.SimpleAggregatorTst`)
* pmd-xml
    * <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.0.0/net/sourceforge/pmd/lang/xml/pom/PomLanguageModule.html#"><code>PomLanguageModule</code></a> (moved from `net.sourceforge.pmd.lang.pom.PomLanguageModule`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.0.0/net/sourceforge/pmd/lang/xml/wsdl/WsdlLanguageModule.html#"><code>WsdlLanguageModule</code></a> (moved from `net.sourceforge.pmd.lang.wsdl.WsdlLanguageModule`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.0.0/net/sourceforge/pmd/lang/xml/xsl/XslLanguageModule.html#"><code>XslLanguageModule</code></a> (moved from `net.sourceforge.pmd.lang.xsl.XslLanguageModule`)
* pmd-visualforce
    * The package `net.sourceforge.pmd.lang.vf` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.0.0/net/sourceforge/pmd/lang/visualforce/package-summary.html#"><code>net.sourceforge.pmd.lang.visualforce</code></a>.
    * The language id of visualforce has been changed to `visualforce` (it was previously just "vf")
    * The ruleset changed: `category/vf/security.xml` ➡️ `category/visualforce/security.xml`
* pmd-velocity (renamed from pmd-vm)
    * The package `net.sourceforge.pmd.lang.vm` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/package-summary.html#"><code>net.sourceforge.pmd.lang.velocity</code></a>.
    * The language id of the Velocity module has been changed to `velocity` (it was previously just "vm")
    * The rulesets changed: `category/vm/...` ➡️ `category/velocity/...`
    * Many classes used the prefix `Vm`, e.g. `VmLanguageModule`. This has been changed to be `Vtl`:
        * <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/VtlLanguageModule.html#"><code>VtlLanguageModule</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/ast/VtlNode.html#"><code>VtlNode</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/ast/VtlParser.html#"><code>VtlParser</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/cpd/VtlCpdLexer.html#"><code>VtlCpdLexer</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/rule/AbstractVtlRule.html#"><code>AbstractVtlRule</code></a>

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
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/Tokens.html#"><code>net.sourceforge.pmd.cpd.Tokens</code></a>
        * Constructor is now package private.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageProcessor.AnalysisTask.html#"><code>net.sourceforge.pmd.lang.LanguageProcessor.AnalysisTask</code></a>
        * Constructor is now package private.
        * Method `withFiles(java.util.List)` is now package private. Note: it was not previously marked with @<!-- -->InternalApi.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleTargetSelector.html#"><code>net.sourceforge.pmd.lang.rule.RuleTargetSelector</code></a>
        * Method `isRuleChain()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/renderers/AbstractAccumulatingRenderer.html#"><code>net.sourceforge.pmd.renderers.AbstractAccumulatingRenderer</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/renderers/AbstractAccumulatingRenderer.html#renderFileReport(net.sourceforge.pmd.reporting.Report)"><code>renderFileReport</code></a> - this method is now final
          and can't be overridden anymore.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Report.html#"><code>net.sourceforge.pmd.reporting.Report</code></a>
        * Constructor as well as the methods `addRuleViolation`, `addConfigError`, `addError` are now private.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleContext.html#"><code>net.sourceforge.pmd.reporting.RuleContext</code></a>
        * Method `getRule()` is now package private.
        * Method `create(FileAnalysisListener listener, Rule rule)` has been removed.
    * `net.sourceforge.pmd.rules.RuleFactory`: moved into subpackage `lang.rule` and made package private.
      It has now been hidden completely from public API.
    * Many types have been moved from into subpackage `lang.rule.internal`.
        * `net.sourceforge.pmd.RuleSetReference`
        * `net.sourceforge.pmd.RuleSetReferenceId`
        * `net.sourceforge.pmd.RuleSets`
    * `net.sourceforge.pmd.lang.rule.ParametricRuleViolation` is now package private and moved to `net.sourceforge.pmd.reporting.ParametricRuleViolation`.
      The only public API is <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#"><code>RuleViolation</code></a>.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSet.html#"><code>net.sourceforge.pmd.lang.rule.RuleSet</code></a>
        * Method `applies(Rule,LanguageVersion)` is now package private.
        * Method `applies(TextFile)` has been removed.
        * Method `applies(FileId)` is now package private.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSetLoader.html#"><code>net.sourceforge.pmd.lang.rule.RuleSetLoader</code></a>
        * Method `loadRuleSetsWithoutException(java.util.List)` is now package private.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSetLoadException.html#"><code>net.sourceforge.pmd.lang.rule.RuleSetLoadException</code></a>
        * All constructors are package private now.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/LexException.html#"><code>net.sourceforge.pmd.lang.ast.LexException</code></a> - the constructor `LexException(boolean, String, int, int, String, char)` is now package private.
      It is only used by JavaCC-generated token managers.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#"><code>net.sourceforge.pmd.PMDConfiguration</code></a>
        * Method `setAnalysisCache(AnalysisCache)` is now package private. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#setAnalysisCacheLocation(java.lang.String)"><code>setAnalysisCacheLocation</code></a> instead.
        * Method `getAnalysisCache()` is now package private.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/FileCollector.html#"><code>net.sourceforge.pmd.lang.document.FileCollector</code></a>
        * Method `newCollector(LanguageVersionDiscoverer, PmdReporter)` is now package private.
        * Method `newCollector(PmdReporter)` is now package private.
        * In order to create a FileCollector, use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PmdAnalysis.html#files()"><code>files</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/xpath/Attribute.html#"><code>net.sourceforge.pmd.lang.rule.xpath.Attribute</code></a>
        * Method `replacementIfDeprecated()` is now package private.
    * `net.sourceforge.pmd.properties.PropertyTypeId` - moved in subpackage `internal`.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#"><code>net.sourceforge.pmd.properties.PropertyDescriptor</code></a> - method `getTypeId()` is now package private.
* pmd-doc
    * The whole maven module `pmd-doc` is now considered internal API even though it was not declared so before.
      It's used to generate the rule documentation for the built-in rules.
    * All the classes have been moved into package `net.sourceforge.pmd.doc.internal`.
* pmd-ant
    * <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.0.0/net/sourceforge/pmd/ant/Formatter.html#"><code>net.sourceforge.pmd.ant.Formatter</code></a>
        * Method `getRenderer()` has been removed.
        * Method `start(String)` is private now.
        * Method `end(Report)` has been removed.
        * Method `isNoOutputSupplied()` is now package private.
        * Method `newListener(Project)` is now package private.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.0.0/net/sourceforge/pmd/ant/PMDTask.html#"><code>net.sourceforge.pmd.ant.PMDTask</code></a>
        * Method `getRelativizeRoots()` has been removed.
    * `net.sourceforge.pmd.ant.ReportException` is now package private. Note: It was not marked with @<!-- -->InternalApi before.
* pmd-apex
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ApexNode.html#"><code>net.sourceforge.pmd.lang.apex.ast.ApexNode</code></a>
        * Method `getNode()` has been removed. It was only deprecated before and not marked with @<!-- -->InternalApi.
          However, it gave access to the wrapped Jorje node and was thus internal API.
    * `net.sourceforge.pmd.lang.apex.ast.AbstractApexNode`
        * Method `getNode()` is now package private.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/multifile/ApexMultifileAnalysis.html#"><code>net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis</code></a>
        * Constructor is now package private.
    * `net.sourceforge.pmd.lang.apex.rule.design.AbstractNcssCountRule` (now package private)
    * `net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule` (moved to package `net.sourceforge.pmd.apex.rule.bestpractices`, now package private)
* pmd-java
    * `net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule` (moved to internal)
    * `net.sourceforge.pmd.lang.java.types.ast.LazyTypeResolver` (moved to internal)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/types/JMethodSig.html#"><code>net.sourceforge.pmd.lang.java.types.JMethodSig</code></a>
        * Method `internalApi()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/types/TypeOps.html#"><code>net.sourceforge.pmd.lang.java.types.TypeOps</code></a>
        * Method `isSameTypeInInference(JTypeMirror,JTypeMirror)` is now package private.
* pmd-jsp
    * <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/7.0.0/net/sourceforge/pmd/lang/jsp/ast/JspParser.html#"><code>net.sourceforge.pmd.lang.jsp.ast.JspParser</code></a>
        * Method `getTokenBehavior()` has been removed.
* pmd-modelica
    * <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/7.0.0/net/sourceforge/pmd/lang/modelica/ast/InternalApiBridge.html#"><code>net.sourceforge.pmd.lang.modelica.ast.InternalApiBridge</code></a> renamed from `InternalModelicaNodeApi`.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/7.0.0/net/sourceforge/pmd/lang/modelica/resolver/InternalApiBridge.html#"><code>net.sourceforge.pmd.lang.modelica.resolver.InternalApiBridge</code></a> renamed from `InternalModelicaResolverApi`.
    * `net.sourceforge.pmd.lang.modelica.resolver.ModelicaSymbolFacade` has been removed.
    * `net.sourceforge.pmd.lang.modelica.resolver.ResolutionContext` (moved to internal)
    * `net.sourceforge.pmd.lang.modelica.resolver.ResolutionState` (moved to internal). Note: it was not previously marked with @<!-- -->InternalApi.
    * `net.sourceforge.pmd.lang.modelica.resolver.Watchdog` (moved to internal). Note: it was not previously marked with @<!-- -->InternalApi.
* pmd-plsql
    * `net.sourceforge.pmd.lang.plsql.rule.design.AbstractNcssCountRule` is now package private.
* pmd-scala
    * <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/7.0.0/net/sourceforge/pmd/lang/scala/ScalaLanguageModule.html#"><code>net.sourceforge.pmd.lang.scala.ScalaLanguageModule</code></a>
        * Method `dialectOf(LanguageVersion)` has been removed.

**Removed classes and members (previously deprecated)**

The annotation `@DeprecatedUntil700` has been removed.

* pmd-core
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CpdLanguageProperties.html#"><code>CpdLanguageProperties</code></a>. The field `DEFAULT_SKIP_BLOCKS_PATTERN` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/impl/antlr4/BaseAntlrNode.html#"><code>BaseAntlrNode</code></a> - method `joinTokenText()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a> - many methods have been removed:
        * `getNthParent(int)` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#ancestors()"><code>ancestors</code></a> instead, e.g. `node.ancestors().get(n-1)`
        * `getFirstParentOfType(Class)` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#ancestors(java.lang.Class)"><code>ancestors</code></a> instead, e.g. `node.ancestors(parentType).first()`
        * `getParentsOfType(Class)` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#ancestors(java.lang.Class)"><code>ancestors</code></a> instead, e.g. `node.ancestors(parentType).toList()`
        * `findChildrenOfType(Class)` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#children(java.lang.Class)"><code>children</code></a> instead, e.g. `node.children(childType).toList()`
        * `findDescendantsOfType(Class)` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#descendants(java.lang.Class)"><code>descendants</code></a> instead, e.g. `node.descendants(targetType).toList()`
        * `findDescendantsOfType(Class,boolean)` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#descendants(java.lang.Class)"><code>descendants</code></a> instead, e.g. `node.descendants(targetType).crossFindBoundaries(b).toList()`
        * `getFirstChildOfType(Class)` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#firstChild(java.lang.Class)"><code>firstChild</code></a> instead
        * `getFirstDescendantOfType(Class)` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#descendants(java.lang.Class)"><code>descendants</code></a> instead, e.g. `node.descendants(targetType).first()`
        * `hasDescendantOfType(Class)` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#descendants(java.lang.Class)"><code>descendants</code></a> instead, e.g. `node.descendants(targetType).nonEmpty()`
        * `findChildNodesWithXPath(String)` - Use the <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/NodeStream.html#"><code>NodeStream</code></a> API instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/impl/GenericNode.html#"><code>GenericNode</code></a> - method `getNthParent(int)` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#ancestors()"><code>ancestors</code></a> instead, e.g. `node.ancestors().get(n-1)`
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/FileCollector.html#"><code>FileCollector</code></a> - method `addZipFile(java.nio.file.Path)` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/FileCollector.html#addZipFileWithContent(java.nio.file.Path)"><code>addZipFileWithContent</code></a> instead
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/TextDocument.html#"><code>TextDocument</code></a> - method `readOnlyString(CharSequence,String,LanguageVersion)` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/TextDocument.html#readOnlyString(java.lang.CharSequence,net.sourceforge.pmd.lang.document.FileId,net.sourceforge.pmd.lang.LanguageVersion)"><code>readOnlyString</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/TextFile.html#"><code>TextFile</code></a> - method `dataSourceCompat(DataSource,PMDConfiguration)` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/TextFile.html#"><code>TextFile</code></a> directly, e.g. <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/TextFile.html#forPath(java.nio.file.Path,java.nio.charset.Charset,net.sourceforge.pmd.lang.LanguageVersion)"><code>forPath</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/xpath/XPathVersion.html#"><code>XPathVersion</code></a>
        * `XPATH_1_0`
        * `XPATH_1_0_COMPATIBILITY`
        * `XPATH_2_0`
        * Only XPath version 3.1 is now supported.  This version of the XPath language is mostly identical to
          XPath 2.0. XPath rules by default use now <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/xpath/XPathVersion.html#XPATH_3_1"><code>XPATH_3_1</code></a>.
    * `net.sourceforge.pmd.lang.rule.AbstractDelegateRule` removed. It has been merged with <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleReference.html#"><code>RuleReference</code></a>.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/AbstractRule.html#"><code>AbstractRule</code></a> - the following methods have been removed:
        * `deepCopyValuesTo(AbstractRule)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/AbstractRule.html#deepCopy()"><code>deepCopy</code></a> instead.
        * `addRuleChainVisit(Class)` - override <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/AbstractRule.html#buildTargetSelector()"><code>buildTargetSelector</code></a> in order to register nodes for rule chain visits.
        * `addViolation(...)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleContext.html#addViolation(net.sourceforge.pmd.lang.ast.Node)"><code>addViolation</code></a> instead, e.g. via `asCtx(data).addViolation(...)`.
          Note: These methods were only marked as deprecated in javadoc.
        * `addViolationWithMessage(...)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleContext.html#addViolationWithMessage(net.sourceforge.pmd.lang.ast.Node,java.lang.String)"><code>addViolationWithMessage</code></a> instead, e.g. via
          `asCtx(data).addViolationWithMessage(...)`. Note: These methods were only marked as deprecated in javadoc.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleReference.html#"><code>RuleReference</code></a> - the following methods have been removed:
        * `setRuleSetReference(RuleSetReference)` - without replacement. Just construct new <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleReference.html#"><code>RuleReference</code></a> instead.
        * `hasOverriddenProperty(PropertyDescriptor)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleReference.html#isPropertyOverridden(net.sourceforge.pmd.properties.PropertyDescriptor)"><code>isPropertyOverridden</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/xpath/XPathRule.html#"><code>XPathRule</code></a>
        * The constant `XPATH_DESCRIPTOR` has been made private and is not accessible anymore.
        * The default constructor has been made package-private and is not accessible anymore.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/Language.html#"><code>Language</code></a> - method `getTerseName()` removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/Language.html#getId()"><code>getId</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageModuleBase.html#"><code>LanguageModuleBase</code></a> - method `getTerseName()` removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageModuleBase.html#getId()"><code>getId</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageRegistry.html#"><code>LanguageRegistry</code></a> - the following methods have been removed:
        * `getLanguage(String)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageRegistry.html#getLanguageByFullName(java.lang.String)"><code>getLanguageByFullName</code></a>
          via <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageRegistry.html#PMD"><code>PMD</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageRegistry.html#CPD"><code>CPD</code></a> instead.
        * `findLanguageByTerseName(String)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageRegistry.html#getLanguageById(java.lang.String)"><code>getLanguageById</code></a>
          via <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageRegistry.html#PMD"><code>PMD</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageRegistry.html#CPD"><code>CPD</code></a> instead.
        * `findByExtension(String)` - removed without replacement.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageVersionDiscoverer.html#"><code>LanguageVersionDiscoverer</code></a> - method `getLanguagesForFile(java.io.File)` removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageVersionDiscoverer.html#getLanguagesForFile(java.lang.String)"><code>getLanguagesForFile</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#"><code>AbstractPropertySource</code></a>
        * field `propertyDescriptors` has been made private and is not accessible anymore.
          Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertyDescriptors()"><code>getPropertyDescriptors</code></a> instead.
        * field `propertyValuesByDescriptor` has been made private and is not accessible anymore.
          Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertiesByPropertyDescriptor()"><code>getPropertiesByPropertyDescriptor</code></a>
          or <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#getOverriddenPropertiesByPropertyDescriptor()"><code>getOverriddenPropertiesByPropertyDescriptor</code></a> instead.
        * method `copyPropertyDescriptors()` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertyDescriptors()"><code>getPropertyDescriptors</code></a> instead.
        * method `copyPropertyValues()` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertiesByPropertyDescriptor()"><code>getPropertiesByPropertyDescriptor</code></a>
          or <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#getOverriddenPropertiesByPropertyDescriptor()"><code>getOverriddenPropertiesByPropertyDescriptor</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Reportable.html#"><code>Reportable</code></a> - the following methods have been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Reportable.html#getReportLocation()"><code>getReportLocation</code></a> instead
        * `getBeginLine()`
        * `getBeginColumn()`
        * `getEndLine()`
        * `getEndColumn()`
    * `net.sourceforge.pmd.util.datasource.DataSource` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/TextFile.html#"><code>TextFile</code></a> instead.
    * `net.sourceforge.pmd.util.datasource.FileDataSource`
    * `net.sourceforge.pmd.util.datasource.ReaderDataSource`
    * `net.sourceforge.pmd.util.datasource.ZipDataSource`
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/CollectionUtil.html#"><code>CollectionUtil</code></a>
        * method `invertedMapFrom(...)` has been removed.
        * method `mapFrom(...)` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#"><code>AbstractConfiguration</code></a> - the following methods have been removed:
        * `setIgnoreFilePath(String)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#setIgnoreFilePath(java.nio.file.Path)"><code>setIgnoreFilePath</code></a> instead.
        * `setInputFilePath(String)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#setInputFilePath(java.nio.file.Path)"><code>setInputFilePath</code></a> instead.
        * `setInputPaths(String)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#setInputPathList(java.util.List)"><code>setInputPathList</code></a> or
          <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#addInputPath(java.nio.file.Path)"><code>addInputPath</code></a> instead.
        * `setInputUri(String)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#setInputUri(java.net.URI)"><code>setInputUri</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#"><code>PMDConfiguration</code></a> - the following methods have been removed
        * `prependClasspath(String)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#prependAuxClasspath(java.lang.String)"><code>prependAuxClasspath</code></a> instead.
        * `getRuleSets()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#getRuleSetPaths()"><code>getRuleSetPaths</code></a> instead.
        * `setRuleSets(String)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#setRuleSets(java.util.List)"><code>setRuleSets</code></a> or
          <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#addRuleSet(java.lang.String)"><code>addRuleSet</code></a> instead.
        * `setReportFile(String)` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#setReportFile(java.nio.file.Path)"><code>setReportFile</code></a> instead.
        * `getReportFile()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#getReportFilePath()"><code>getReportFilePath</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Report.html#"><code>Report</code></a> - method `merge(Report)` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Report.html#union(net.sourceforge.pmd.reporting.Report)"><code>union</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSetLoader.html#"><code>RuleSetLoader</code></a> - method `toFactory()` has been made package private and is not accessible anymore.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#"><code>RuleViolation</code></a> - the following methods have been removed:
        * `getPackageName()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#getAdditionalInfo()"><code>getAdditionalInfo</code></a> with <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#PACKAGE_NAME"><code>PACKAGE_NAME</code></a> instead, e.g. `getAdditionalInfo().get(PACKAGE_NAME)`.
        * `getClassName()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#getAdditionalInfo()"><code>getAdditionalInfo</code></a> with <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#CLASS_NAME"><code>CLASS_NAME</code></a> instead, e.g. `getAdditionalInfo().get(CLASS_NAME)`.
        * `getMethodName()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#getAdditionalInfo()"><code>getAdditionalInfo</code></a> with <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#METHOD_NAME"><code>METHOD_NAME</code></a> instead, e.g. `getAdditionalInfo().get(METHOD_NAME)`.
        * `getVariableName()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#getAdditionalInfo()"><code>getAdditionalInfo</code></a> with <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#VARIABLE_NAME"><code>VARIABLE_NAME</code></a> instead, e.g. `getAdditionalInfo().get(VARIABLE_NAME)`.
* pmd-apex
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ApexNode.html#"><code>ApexNode</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTApexFile.html#"><code>ASTApexFile</code></a>
        * `#getApexVersion()`: In PMD 6, this method has been deprecated but was defined in the class `ApexRootNode`.
          The version returned is always "Version.CURRENT", as the apex compiler integration
          doesn't use additional information which Apex version actually is used. Therefore, this method can't be
          used to determine the Apex version of the project that is being analyzed.

          If the current version is needed, then `Node.getTextDocument().getLanguageVersion()` can be used. This
          is the version that has been selected via CLI `--use-version` parameter.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ApexNode.html#"><code>ApexNode</code></a>
        * method `jjtAccept()` has been removed.
          Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#acceptVisitor(net.sourceforge.pmd.lang.ast.AstVisitor,P)"><code>acceptVisitor</code></a> instead.
        * method `getNode()` has been removed. The underlying node is only available in AST nodes, but not in rule implementations.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNode.html#"><code>AbstractApexNode</code></a> - method `getNode()` is now package private.
      AST nodes still have access to the underlying Jorje node via the protected property `node`.
    * `net.sourceforge.pmd.lang.apex.ast.ApexParserVisitor`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ApexVisitor.html#"><code>ApexVisitor</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ApexVisitorBase.html#"><code>ApexVisitorBase</code></a> instead.
    * `net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter`
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTAssignmentExpression.html#"><code>ASTAssignmentExpression</code></a> - method `getOperator()` removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTAssignmentExpression.html#getOp()"><code>getOp</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTBinaryExpression.html#"><code>ASTBinaryExpression</code></a> - method `getOperator()` removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTBinaryExpression.html#getOp()"><code>getOp</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTBooleanExpression.html#"><code>ASTBooleanExpression</code></a> - method `getOperator()` removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTBooleanExpression.html#getOp()"><code>getOp</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTPostfixExpression.html#"><code>ASTPostfixExpression</code></a> - method `getOperator()` removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTPostfixExpression.html#getOp()"><code>getOp</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTPrefixExpression.html#"><code>ASTPrefixExpression</code></a> - method `getOperator()` removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTPrefixExpression.html#getOp()"><code>getOp</code></a> instead.
    * `net.sourceforge.pmd.lang.apex.rule.security.Helper` removed. This was actually internal API.
* pmd-java
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/AbstractPackageNameModuleDirective.html#"><code>AbstractPackageNameModuleDirective</code></a> - method `getImage()` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/AbstractPackageNameModuleDirective.html#getPackageName()"><code>getPackageName</code></a> instead.
    * `AbstractTypeDeclaration` - method `getImage()` has been removed.
      Use `getSimpleName()` instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTAnnotation.html#"><code>ASTAnnotation</code></a> - method `getAnnotationName()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTClassType.html#"><code>ASTClassType</code></a>
        * constructor `ASTClassType(java.lang.String)` has been removed.
        * method `getImage()` has been removed.
        * method `isReferenceToClassSameCompilationUnit()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#"><code>ASTFieldDeclaration</code></a> - method `getVariableName()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTLiteral.html#"><code>ASTLiteral</code></a> - the following methods have been removed:
        * `isStringLiteral()` - use `node instanceof ASTStringLiteral` instead.
        * `isCharLiteral()` - use `node instanceof ASTCharLiteral` instead.
        * `isNullLiteral()` - use `node instanceof ASTNullLiteral` instead.
        * `isBooleanLiteral()` - use `node instanceof ASTBooleanLiteral` instead.
        * `isNumericLiteral()` - use `node instanceof ASTNumericLiteral` instead.
        * `isIntLiteral()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTNumericLiteral.html#isIntLiteral()"><code>isIntLiteral</code></a> instead.
        * `isLongLiteral()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTNumericLiteral.html#isLongLiteral()"><code>isLongLiteral</code></a> instead.
        * `isFloatLiteral()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTNumericLiteral.html#isFloatLiteral()"><code>isFloatLiteral</code></a> instead.
        * `isDoubleLiteral()` - use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTNumericLiteral.html#isDoubleLiteral()"><code>isDoubleLiteral</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#"><code>ASTMethodDeclaration</code></a> - methods `getImage()` and `getMethodName()` have been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#getName()"><code>getName</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTMethodReference.html#"><code>ASTMethodReference</code></a> - method `getImage()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTModuleName.html#"><code>ASTModuleName</code></a> - method `getImage()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTPrimitiveType.html#"><code>ASTPrimitiveType</code></a> - method `getImage()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTType.html#"><code>ASTType</code></a>
        * `getTypeImage()` has been removed.
        * `getArrayDepth()` has been removed. It's only available for arrays: <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTArrayType.html#getArrayDepth()"><code>getArrayDepth</code></a>.
        * `isPrimitiveType()` - use `node instanceof ASTPrimitiveType` instead.
        * `isArrayType()` - use `node instanceof ASTArrayType` instead.
        * `isClassOrInterfaceType()` - use `node instanceof ASTClassType` instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTypeDeclaration.html#"><code>ASTTypeDeclaration</code></a> - method `getImage()` has been removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTUnaryExpression.html#"><code>ASTUnaryExpression</code></a> - method `isPrefix()` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTUnaryExpression.html#getOperator()"><code>getOperator</code></a>`.isPrefix()` instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTVariableId.html#"><code>ASTVariableId</code></a> - methods `getImage()` and `getVariableName()` have been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTVariableId.html#getName()"><code>getName</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/JavaComment.html#"><code>JavaComment</code></a> - method `getImage()` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/JavaComment.html#getText()"><code>getText</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/JavaNode.html#"><code>JavaNode</code></a> - method `jjtAccept()` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#acceptVisitor(net.sourceforge.pmd.lang.ast.AstVisitor,P)"><code>acceptVisitor</code></a> instead.
    * `net.sourceforge.pmd.lang.java.ast.JavaParserVisitor`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/JavaVisitor.html#"><code>JavaVisitor</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/JavaVisitorBase.html#"><code>JavaVisitorBase</code></a> instead.
    * `net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter`
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#"><code>ModifierOwner</code></a>
        * `isFinal()` - This is still available in various subtypes, where it makes sense, e.g. <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTLocalVariableDeclaration.html#isFinal()"><code>isFinal</code></a>.
        * `isAbstract()` - This is still available in subtypes, e.g. <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTypeDeclaration.html#isAbstract()"><code>isAbstract</code></a>.
        * `isStrictfp()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasModifiers(net.sourceforge.pmd.lang.java.ast.JModifier,net.sourceforge.pmd.lang.java.ast.JModifier...)"><code>hasModifiers</code></a> instead, e.g. `hasModifiers(STRICTFP)`.
        * `isSynchronized()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasModifiers(net.sourceforge.pmd.lang.java.ast.JModifier,net.sourceforge.pmd.lang.java.ast.JModifier...)"><code>hasModifiers</code></a> instead, e.g. `hasModifiers(SYNCHRONIZED)`.
        * `isNative()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasModifiers(net.sourceforge.pmd.lang.java.ast.JModifier,net.sourceforge.pmd.lang.java.ast.JModifier...)"><code>hasModifiers</code></a> instead, e.g. `hasModifiers(NATIVE)`.
        * `isStatic()` - This is still available in subtypes, e.g. <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#isStatic()"><code>isStatic</code></a>.
        * `isVolatile()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasModifiers(net.sourceforge.pmd.lang.java.ast.JModifier,net.sourceforge.pmd.lang.java.ast.JModifier...)"><code>hasModifiers</code></a> instead, e.g. `hasModifiers(VOLATILE)`.
        * `isTransient()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasModifiers(net.sourceforge.pmd.lang.java.ast.JModifier,net.sourceforge.pmd.lang.java.ast.JModifier...)"><code>hasModifiers</code></a> instead, e.g. `hasModifiers(TRANSIENT)`.
        * `isPrivate()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#getVisibility()"><code>getVisibility</code></a> instead, e.g. `getVisibility() == Visibility.V_PRIVATE`.
        * `isPublic()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#getVisibility()"><code>getVisibility</code></a> instead, e.g. `getVisibility() == Visibility.V_PUBLIC`.
        * `isProtected()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#getVisibility()"><code>getVisibility</code></a> instead, e.g. `getVisibility() == Visibility.V_PROTECTED`.
        * `isPackagePrivate()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#getVisibility()"><code>getVisibility</code></a> instead, e.g. `getVisibility() == Visibility.V_PACKAGE`.
        * `isSyntacticallyAbstract()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasExplicitModifiers(net.sourceforge.pmd.lang.java.ast.JModifier,net.sourceforge.pmd.lang.java.ast.JModifier...)"><code>hasExplicitModifiers</code></a> instead, e.g. `hasExplicitModifiers(ABSTRACT)`.
        * `isSyntacticallyPublic()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasExplicitModifiers(net.sourceforge.pmd.lang.java.ast.JModifier,net.sourceforge.pmd.lang.java.ast.JModifier...)"><code>hasExplicitModifiers</code></a> instead, e.g. `hasExplicitModifiers(PUBLIC)`.
        * `isSyntacticallyStatic()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasExplicitModifiers(net.sourceforge.pmd.lang.java.ast.JModifier,net.sourceforge.pmd.lang.java.ast.JModifier...)"><code>hasExplicitModifiers</code></a> instead, e.g. `hasExplicitModifiers(STATIC)`.
        * `isSyntacticallyFinal()` - Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasExplicitModifiers(net.sourceforge.pmd.lang.java.ast.JModifier,net.sourceforge.pmd.lang.java.ast.JModifier...)"><code>hasExplicitModifiers</code></a> instead, e.g. `hasExplicitModifiers(FINAL)`.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#"><code>TypeNode</code></a> - method `getType()` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#getTypeMirror()"><code>getTypeMirror</code></a> instead.
* pmd-javascript
    * <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.0.0/net/sourceforge/pmd/lang/ecmascript/ast/AbstractEcmascriptNode.html#"><code>AbstractEcmascriptNode</code></a> - method `getNode()` has been removed.
      AST nodes still have access to the underlying Rhino node via the protected property `node`.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.0.0/net/sourceforge/pmd/lang/ecmascript/ast/ASTFunctionNode.html#"><code>ASTFunctionNode</code></a> - method `getBody(int)` removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.0.0/net/sourceforge/pmd/lang/ecmascript/ast/ASTFunctionNode.html#getBody()"><code>getBody</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.0.0/net/sourceforge/pmd/lang/ecmascript/ast/ASTTryStatement.html#"><code>ASTTryStatement</code></a>
        * method `isCatch()` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.0.0/net/sourceforge/pmd/lang/ecmascript/ast/ASTTryStatement.html#hasCatch()"><code>hasCatch</code></a> instead.
        * method `isFinally()` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.0.0/net/sourceforge/pmd/lang/ecmascript/ast/ASTTryStatement.html#hasFinally()"><code>hasFinally</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.0.0/net/sourceforge/pmd/lang/ecmascript/ast/EcmascriptNode.html#"><code>EcmascriptNode</code></a>
        * method `jjtAccept()` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#acceptVisitor(net.sourceforge.pmd.lang.ast.AstVisitor,P)"><code>acceptVisitor</code></a> instead.
        * method `getNode()` has been removed.  The underlying node is only available in AST nodes, but not in rule implementations.
    * `net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserVisitor`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.0.0/net/sourceforge/pmd/lang/ecmascript/ast/EcmascriptVisitor.html#"><code>EcmascriptVisitor</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.0.0/net/sourceforge/pmd/lang/ecmascript/ast/EcmascriptVisitorBase.html#"><code>EcmascriptVisitorBase</code></a> instead.
    * `net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserVisitorAdapter`
* pmd-jsp
    * `net.sourceforge.pmd.lang.jsp.ast.JspParserVisitor`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/7.0.0/net/sourceforge/pmd/lang/jsp/ast/JspVisitor.html#"><code>JspVisitor</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/7.0.0/net/sourceforge/pmd/lang/jsp/ast/JspVisitorBase.html#"><code>JspVisitorBase</code></a> instead.
    * `net.sourceforge.pmd.lang.jsp.ast.JspParserVisitorAdapter`
    * <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/7.0.0/net/sourceforge/pmd/lang/jsp/ast/JspNode.html#"><code>JspNode</code></a> - method `jjtAccept()` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#acceptVisitor(net.sourceforge.pmd.lang.ast.AstVisitor,P)"><code>acceptVisitor</code></a> instead.
* pmd-modelica
    * `net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitor`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/7.0.0/net/sourceforge/pmd/lang/modelica/ast/ModelicaVisitor.html#"><code>ModelicaVisitor</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/7.0.0/net/sourceforge/pmd/lang/modelica/ast/ModelicaVisitorBase.html#"><code>ModelicaVisitorBase</code></a> instead.
    * `net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitorAdapter`
    * <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/7.0.0/net/sourceforge/pmd/lang/modelica/ast/ModelicaNode.html#"><code>ModelicaNode</code></a> - method `jjtAccept()` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#acceptVisitor(net.sourceforge.pmd.lang.ast.AstVisitor,P)"><code>acceptVisitor</code></a> instead.
    * `net.sourceforge.pmd.lang.modelica.rule.AmbiguousResolutionRule`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/7.0.0/net/sourceforge/pmd/lang/modelica/rule/bestpractices/AmbiguousResolutionRule.html#"><code>AmbiguousResolutionRule</code></a> instead.
    * `net.sourceforge.pmd.lang.modelica.rule.ConnectUsingNonConnector`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/7.0.0/net/sourceforge/pmd/lang/modelica/rule/bestpractices/ConnectUsingNonConnectorRule.html#"><code>ConnectUsingNonConnectorRule</code></a>
* pmd-plsql
    * `net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitor`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.0.0/net/sourceforge/pmd/lang/plsql/ast/PlsqlVisitor.html#"><code>PlsqlVisitor</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.0.0/net/sourceforge/pmd/lang/plsql/ast/PlsqlVisitorBase.html#"><code>PlsqlVisitorBase</code></a> instead.
    * `net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter`
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.0.0/net/sourceforge/pmd/lang/plsql/ast/PLSQLNode.html#"><code>PLSQLNode</code></a> - method `jjtAccept()` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#acceptVisitor(net.sourceforge.pmd.lang.ast.AstVisitor,P)"><code>acceptVisitor</code></a> instead.
* pmd-scala
    * The maven module `pmd-scala` has been removed. Use `pmd-scala_2.13` or `pmd-scala_2.12` instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/7.0.0/net/sourceforge/pmd/lang/scala/ast/ScalaNode.html#"><code>ScalaNode</code></a>
        * Method `accept()` has been removed. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#acceptVisitor(net.sourceforge.pmd.lang.ast.AstVisitor,P)"><code>acceptVisitor</code></a> instead.
        * Method `getNode()` has been removed. The underlying node is only available in AST nodes, but not in rule implementations.
    * `AbstractScalaNode` - method `getNode()` has been removed. AST nodes still have access
      to the underlying Scala node via the protected property `node`.
* pmd-visualforce
    * <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.0.0/net/sourceforge/pmd/lang/visualforce/ast/VfNode.html#"><code>VfNode</code></a> - method `jjtAccept()` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#acceptVisitor(net.sourceforge.pmd.lang.ast.AstVisitor,P)"><code>acceptVisitor</code></a> instead.
    * `net.sourceforge.pmd.lang.vf.ast.VfParserVisitor`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.0.0/net/sourceforge/pmd/lang/visualforce/ast/VfVisitor.html#"><code>VfVisitor</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.0.0/net/sourceforge/pmd/lang/visualforce/ast/VfVisitorBase.html#"><code>VfVisitorBase</code></a> instead.
    * `net.sourceforge.pmd.lang.vf.ast.VfParserVisitorAdapter`
    * <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.0.0/net/sourceforge/pmd/lang/visualforce/DataType.html#"><code>DataType</code></a> - method `fromBasicType(BasicType)` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.0.0/net/sourceforge/pmd/lang/visualforce/DataType.html#fromTypeName(java.lang.String)"><code>fromTypeName</code></a> instead.
* pmd-velocity (previously pmd-vm)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/ast/VtlNode.html#"><code>VtlNode</code></a> - method `jjtAccept()` has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#acceptVisitor(net.sourceforge.pmd.lang.ast.AstVisitor,P)"><code>acceptVisitor</code></a> instead.
    * `net.sourceforge.pmd.lang.vm.ast.VmParserVisitor`
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/ast/VtlVisitor.html#"><code>VtlVisitor</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/ast/VtlVisitorBase.html#"><code>VtlVisitorBase</code></a> instead.
    * `net.sourceforge.pmd.lang.vm.ast.VmParserVisitorAdapter`

**Removed classes, interfaces and methods (not previously deprecated)**

* pmd-apex
    * The method `isSynthetic()` in <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTMethod.html#"><code>ASTMethod</code></a> has been removed.
      With the switch from Jorje to Summit AST as underlying parser, no synthetic methods are generated by the
      parser anymore. This also means, that there is no XPath attribute `@Synthetic` anymore.
    * The constant `STATIC_INITIALIZER_METHOD_NAME` in <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/rule/codestyle/FieldDeclarationsShouldBeAtStartRule.html#"><code>FieldDeclarationsShouldBeAtStartRule</code></a>
      has been removed. It was used to filter out synthetic methods, but these are not generated anymore with the
      new parser.
    * The method `getContext()` in <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTReferenceExpression.html#"><code>ASTReferenceExpression</code></a> has been removed.
      It was not used and always returned `null`.
    * The method `getNamespace()` in all AST nodes (defined in <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ApexNode.html#"><code>ApexNode</code></a>) has
      been removed, as it was never fully implemented. It always returned an empty string.
    * The method `getNameSpace()` in <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ApexQualifiedName.html#"><code>ApexQualifiedName</code></a> has been removed.
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
      nodes where needed, e.g. <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTLocalVariableDeclaration.html#isFinal()"><code>ASTLocalVariableDeclaration#isFinal</code></a>.
    * The method `isPackagePrivate()` in <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTClassDeclaration.html#"><code>ASTClassDeclaration</code></a> (formerly ASTClassOrInterfaceDeclaration)
      has been removed.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#hasVisibility(net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility)"><code>hasVisibility</code></a> instead,
      which can correctly differentiate between local and package private classes.

**Renamed classes, interfaces, methods**

* pmd-core
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/util/log/MessageReporter.html#"><code>MessageReporter</code></a> has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/log/PmdReporter.html#"><code>PmdReporter</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/TokenMgrError.html#"><code>TokenMgrError</code></a> has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/LexException.html#"><code>LexException</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/Tokenizer.html#"><code>Tokenizer</code></a> has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CpdLexer.html#"><code>CpdLexer</code></a>. Along with this rename,
      all the implementations have been renamed as well (`Tokenizer` -> `CpdLexer`), e.g. "CppCpdLexer", "JavaCpdLexer".
      This affects all language modules.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/AnyTokenizer.html#"><code>AnyTokenizer</code></a> has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/AnyCpdLexer.html#"><code>AnyCpdLexer</code></a>.

* pmd-java
    * The interface `AccessNode` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ModifierOwner.html#"><code>ModifierOwner</code></a>. This is only relevant
      for Java rules, which use that type directly e.g. through downcasting.
      Or when using the XPath function `pmd-java:nodeIs()`.
    * The node `ASTClassOrInterfaceType` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTClassType.html#"><code>ASTClassType</code></a>. XPath rules
      need to be adjusted.
    * The node `ASTClassOrInterfaceDeclaration` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTClassDeclaration.html#"><code>ASTClassDeclaration</code></a>.
      XPath rules need to be adjusted.
    * The interface `ASTAnyTypeDeclaration` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTypeDeclaration.html#"><code>ASTTypeDeclaration</code></a>.
      This is only relevant for Java rules, which use that type directly, e.g. through downcasting.
      Or when using the XPath function `pmd-java:nodeIs()`.
    * The interface `ASTMethodOrConstructorDeclaration` has been renamed to
      <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTExecutableDeclaration.html#"><code>ASTExecutableDeclaration</code></a>. This is only relevant for Java rules, which use that type
      directly, e.g. through downcasting. Or when using the XPath function `pmd-java:nodeIs()`.
    * The node `ASTVariableDeclaratorId` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTVariableId.html#"><code>ASTVariableId</code></a>. XPath rules
      need to be adjusted.
    * The node `ASTClassOrInterfaceBody` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTClassBody.html#"><code>ASTClassBody</code></a>. XPath rules
      need to be adjusted.
* pmd-scala
    * The interface `ScalaParserVisitor` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/7.0.0/net/sourceforge/pmd/lang/scala/ast/ScalaVisitor.html#"><code>ScalaVisitor</code></a> in order
      to align the naming scheme for the different language modules.
    * The class `ScalaParserVisitorAdapter` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/7.0.0/net/sourceforge/pmd/lang/scala/ast/ScalaVisitorBase.html#"><code>ScalaVisitorBase</code></a> in order
      to align the naming scheme for the different language modules.

**New API**

These were annotated with `@Experimental`, but can now be considered stable.

* pmd-apex
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTCommentContainer.html#"><code>ASTCommentContainer</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/multifile/ApexMultifileAnalysis.html#"><code>ApexMultifileAnalysis</code></a>
* pmd-core
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDReport.html#filterMatches(java.util.function.Predicate)"><code>CPDReport#filterMatches</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrToken.html#getKind()"><code>AntlrToken#getKind</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/impl/javacc/AbstractJjtreeNode.html#"><code>AbstractJjtreeNode</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/impl/TokenDocument.html#"><code>TokenDocument</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/AstInfo.html#getSuppressionComments()"><code>AstInfo#getSuppressionComments</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/AstInfo.html#withSuppressMap(java.util.Map)"><code>AstInfo#withSuppressMap</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/GenericToken.html#getKind()"><code>GenericToken#getKind</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/FileCollector.html#addZipFileWithContent(java.nio.file.Path)"><code>FileCollector#addZipFileWithContent</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/package-summary.html#"><code>net.sourceforge.pmd.lang.document</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getLanguageMetricsProvider()"><code>LanguageVersionHandler#getLanguageMetricsProvider</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDesignerBindings()"><code>LanguageVersionHandler#getDesignerBindings</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/PlainTextLanguage.html#"><code>PlainTextLanguage</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyConstraint.html#getXmlConstraint()"><code>PropertyConstraint#getXmlConstraint</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyConstraint.html#toOptionalConstraint()"><code>PropertyConstraint#toOptionalConstraint</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyConstraint.html#fromPredicate(java.util.function.Predicate,java.lang.String)"><code>PropertyConstraint#fromPredicate</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyConstraint.html#fromPredicate(java.util.function.Predicate,java.lang.String,java.util.Map)"><code>PropertyConstraint#fromPredicate</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/renderers/AbstractRenderer.html#setReportFile(java.lang.String)"><code>AbstractRenderer#setReportFile</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/renderers/Renderer.html#setReportFile(java.lang.String)"><code>Renderer#setReportFile</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/designerbindings/DesignerBindings.html#"><code>DesignerBindings</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/designerbindings/DesignerBindings.TreeIconId.html#"><code>DesignerBindings.TreeIconId</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/designerbindings/RelatedNodesSelector.html#"><code>RelatedNodesSelector</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Report.html#filterViolations(java.util.function.Predicate)"><code>Report#filterViolations</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Report.html#union(net.sourceforge.pmd.Report)"><code>Report#union</code></a>
* pmd-groovy
    * <a href="https://docs.pmd-code.org/apidocs/pmd-groovy/7.0.0/net/sourceforge/pmd/lang/groovy/ast/impl/antlr4/GroovyToken.html#getKind()"><code>GroovyToken#getKind</code></a>
* pmd-html
    * <a href="https://docs.pmd-code.org/apidocs/pmd-html/7.0.0/net/sourceforge/pmd/lang/html/package-summary.html#"><code>net.sourceforge.pmd.lang.html</code></a>
* pmd-java
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTExpression.html#getConversionContext()"><code>ASTExpression#getConversionContext</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/rule/AbstractJavaRulechainRule.html#<init>(java.lang.Class,java.lang.Class...)"><code>AbstractJavaRulechainRule#&lt;init&gt;</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/symbols/table/JSymbolTable.html#"><code>JSymbolTable</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/symbols/JElementSymbol.html#"><code>JElementSymbol</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/symbols/package-summary.html#"><code>net.sourceforge.pmd.lang.java.symbols</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/types/ast/ExprContext.html#"><code>ExprContext</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/types/JIntersectionType.html#getInducedClassType()"><code>JIntersectionType#getInducedClassType</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/types/JTypeMirror.html#streamMethods(java.util.function.Predicate)"><code>JTypeMirror#streamMethods</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/types/JTypeMirror.html#streamDeclaredMethods(java.util.function.Predicate)"><code>JTypeMirror#streamDeclaredMethods</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/types/JTypeMirror.html#getConstructors()"><code>JTypeMirror#getConstructors</code></a>
* pmd-kotlin
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.0.0/net/sourceforge/pmd/lang/kotlin/KotlinLanguageModule.html#"><code>KotlinLanguageModule</code></a>
* pmd-test-schema
    * <a href="https://docs.pmd-code.org/apidocs/pmd-test-schema/7.0.0/net/sourceforge/pmd/test/schema/TestSchemaParser.html#"><code>TestSchemaParser</code></a>

**Removed functionality**

* The CLI parameter `--no-ruleset-compatibility` has been removed. It was only used to allow loading
  some rulesets originally written for PMD 5 also in PMD 6 without fixing the rulesets.
* The class <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSetFactoryCompatibility.html#"><code>RuleSetFactoryCompatibility</code></a> has been removed without replacement.
  The different ways to enable/disable this filter in <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#"><code>PMDConfiguration</code></a>
  (Property "RuleSetFactoryCompatibilityEnabled") and
  <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.0.0/net/sourceforge/pmd/ant/PMDTask.html#"><code>PMDTask</code></a> (Property "noRuleSetCompatibility") have been removed as well.
* `textcolor` renderer (<a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/renderers/TextColorRenderer.html#"><code>TextColorRenderer</code></a>) now renders always in color.
  The property `color` has been removed. The possibility to override this with the system property `pmd.color`
  has been removed as well. If you don't want colors, use `text` renderer (<a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/renderers/TextRenderer.html#"><code>TextRenderer</code></a>).

#### 7.0.0-rc4

**pmd-java**

* Support for Java 19 preview language features have been removed. The version "19-preview" is no longer available.

**Rule properties**

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#"><code>PropertyFactory</code></a> to create properties.
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
    * `net.sourceforge.pmd.cpd.AbstractTokenizer` ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/AnyCpdLexer.html#"><code>AnyCpdLexer</code></a> instead (previously known as AnyTokenizer)
    * `net.sourceforge.pmd.cpd.CPD` ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-cli/7.0.0/net/sourceforge/pmd/cli/PmdCli.html#"><code>PmdCli</code></a> from `pmd-cli` module for CLI support or use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CpdAnalysis.html#"><code>CpdAnalysis</code></a> for programmatic API
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
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#"><code>CPDConfiguration</code></a>
        * `#sourceCodeFor(File)`, `#postConstruct()`, `#tokenizer()`, `#filenameFilter()` removed
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/Mark.html#"><code>Mark</code></a>
        * `#getSourceSlice()`, `#setLineCount(int)`, `#getLineCount()`, `#setSourceCode(SourceCode)` removed
        * `#getBeginColumn()`, `#getBeginLine()`, `#getEndLine()`, `#getEndColumn()` removed
          ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/Mark.html#getLocation()"><code>getLocation</code></a> instead
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/Match.html#"><code>Match</code></a>
        * `#LABEL_COMPARATOR` removed
        * `#setMarkSet(...)`, `#setLabel(...)`, `#getLabel()`, `#addTokenEntry(...)` removed
        * `#getSourceCodeSlice()` removed
          ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDReport.html#getSourceCodeSlice(net.sourceforge.pmd.cpd.Mark)"><code>CPDReport#getSourceCodeSlice</code></a> instead
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/TokenEntry.html#"><code>TokenEntry</code></a>
        * `#getEOF()`, `#clearImages()`, `#getIdentifier()`, `#getIndex()`, `#setHashCode(int)` removed
        * `#EOF` removed ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/TokenEntry.html#isEof()"><code>isEof</code></a> instead
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Parser.ParserTask.html#"><code>Parser.ParserTask</code></a>
        * `#getFileDisplayName()` removed ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Parser.ParserTask.html#getFileId()"><code>getFileId</code></a> instead
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
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/NumericConstraints.html#"><code>NumericConstraints</code></a> (old package: `net.sourceforge.pmd.properties.constraints.NumericConstraints`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyConstraint.html#"><code>PropertyConstraint</code></a> (old package: `net.sourceforge.pmd.properties.constraints.PropertyConstraint`)
        * not experimental anymore
    * <a href="https://docs.pmd-code.org/apidocs/pmd-ant/7.0.0/net/sourceforge/pmd/ant/ReportException.html#"><code>ReportException</code></a> (old package: `net.sourceforge.pmd.cpd`, moved to module `pmd-ant`)
        * it is now a RuntimeException
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDReportRenderer.html#"><code>CPDReportRenderer</code></a> (old package: `net.sourceforge.pmd.cpd.renderer`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/impl/AntlrTokenFilter.html#"><code>AntlrTokenFilter</code></a> (old package: `net.sourceforge.pmd.cpd.token`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/impl/BaseTokenFilter.html#"><code>BaseTokenFilter</code></a> (old package: `net.sourceforge.pmd.cpd.token.internal`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/impl/JavaCCTokenFilter.html#"><code>JavaCCTokenFilter</code></a> (old package: `net.sourceforge.pmd.cpd.token`)

**Changed types and other changes**

* pmd-core
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#"><code>PropertyDescriptor</code></a> is now a class (was an interface)
      and it is not comparable anymore.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#setSourceEncoding(java.nio.charset.Charset)"><code>AbstractConfiguration#setSourceEncoding</code></a>
        * previously this method took a simple String for the encoding.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#"><code>PMDConfiguration</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#"><code>CPDConfiguration</code></a>
        * many getters and setters have been moved to the parent class <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#"><code>AbstractConfiguration</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDListener.html#addedFile(int)"><code>CPDListener#addedFile</code></a>
        * no `File` parameter anymore
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDReport.html#getNumberOfTokensPerFile()"><code>CPDReport#getNumberOfTokensPerFile</code></a> returns a `Map` of `FileId,Integer` instead of `String`
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDReport.html#filterMatches(java.util.function.Predicate)"><code>CPDReport#filterMatches</code></a> now takes a `java.util.function.Predicate`
      as parameter
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CpdLexer.html#"><code>CpdLexer</code></a>
        * Note: CpdLexer was previously named Tokenizer.
        * constants are now <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#"><code>PropertyDescriptor</code></a> instead of `String`,
          to be used as language properties
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CpdLexer.html#tokenize(net.sourceforge.pmd.lang.document.TextDocument,net.sourceforge.pmd.cpd.TokenFactory)"><code>tokenize</code></a>
          changed parameters. Now takes a <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/TextDocument.html#"><code>TextDocument</code></a> and a <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/TokenFactory.html#"><code>TokenFactory</code></a>
          (instead of `SourceCode` and `Tokens`).
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/Language.html#"><code>Language</code></a>
        * method `#createProcessor(LanguagePropertyBundle)` moved to <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/PmdCapableLanguage.html#"><code>PmdCapableLanguage</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/util/StringUtil.html#linesWithTrimIndent(net.sourceforge.pmd.lang.document.Chars)"><code>StringUtil#linesWithTrimIndent</code></a> now takes a `Chars`
      instead of a `String`.
* All language modules (like pmd-apex, pmd-cpp, ...)
    * consistent package naming: `net.sourceforge.pmd.lang.<langId>.cpd`
    * adapted to use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CpdCapableLanguage.html#"><code>CpdCapableLanguage</code></a>
    * consistent static method `#getInstance()`
    * removed constants like `ID`, `TERSE_NAME` or `NAME`. Use `getInstance().getName()` etc. instead

**Internal APIs**

* `net.sourceforge.pmd.properties.internal.PropertyTypeId`

**Deprecated API**

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/Language.html#getTerseName()"><code>Language#getTerseName</code></a> ➡️ use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/Language.html#getId()"><code>getId</code></a> instead

* The method <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTPattern.html#getParenthesisDepth()"><code>ASTPattern#getParenthesisDepth</code></a> has been deprecated and will be removed.
  It was introduced for supporting parenthesized patterns, but that was removed with Java 21. It is only used when
  parsing code as java-19-preview.

**Experimental APIs**

* To support the Java preview language features "String Templates" and "Unnamed Patterns and Variables", the following
  AST nodes have been introduced as experimental:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTemplateExpression.html#"><code>ASTTemplateExpression</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTemplate.html#"><code>ASTTemplate</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTemplateFragment.html#"><code>ASTTemplateFragment</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTUnnamedPattern.html#"><code>ASTUnnamedPattern</code></a>
* The AST nodes for supporting "Record Patterns" and "Pattern Matching for switch" are not experimental anymore:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTRecordPattern.html#"><code>ASTRecordPattern</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTPatternList.html#"><code>ASTPatternList</code></a> (Note: it was renamed from `ASTComponentPatternList`)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTGuard.html#"><code>ASTGuard</code></a> (Note: it was renamed from `ASTSwitchGuard`)

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

* Replaced `RuleViolation::getFilename` with new <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#getFileId()"><code>RuleViolation#getFileId</code></a>, that returns a
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/FileId.html#"><code>FileId</code></a>. This is an identifier for a <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/TextFile.html#"><code>TextFile</code></a>
  and could represent a path name. This allows to have a separate display name, e.g. renderers use
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/FileNameRenderer.html#"><code>FileNameRenderer</code></a> to either display the full path name or a relative path name
  (see <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/renderers/Renderer.html#setFileNameRenderer(net.sourceforge.pmd.reporting.FileNameRenderer)"><code>Renderer#setFileNameRenderer</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/ConfigurableFileNameRenderer.html#"><code>ConfigurableFileNameRenderer</code></a>). Many places where we used a simple String for
  a path-like name before have been adapted to use the new <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/FileId.html#"><code>FileId</code></a>.

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
    * <a href="https://docs.pmd-code.org/apidocs/pmd-go/6.55.0/net/sourceforge/pmd/lang/go/GoLanguageModule.html#"><code>GoLanguageModule</code></a>

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

* <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ApexRootNode.html#getApexVersion()"><code>ApexRootNode#getApexVersion()</code></a> has been deprecated for removal. The version returned is
  always `Version.CURRENT`, as the apex compiler integration doesn't use additional information which Apex version
  actually is used. Therefore, this method can't be used to determine the Apex version of the project
  that is being analyzed.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#setEncoding(java.lang.String)"><code>CPDConfiguration#setEncoding</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getEncoding()"><code>CPDConfiguration#getEncoding</code></a>. Use the methods
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#getSourceEncoding()"><code>getSourceEncoding</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#setSourceEncoding(java.nio.charset.Charset)"><code>setSourceEncoding</code></a> instead. Both are available
  for `CPDConfiguration` which extends `AbstractConfiguration`.
* <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.55.0/net/sourceforge/pmd/cli/BaseCLITest.html#"><code>BaseCLITest</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.55.0/net/sourceforge/pmd/cli/BaseCPDCLITest.html#"><code>BaseCPDCLITest</code></a> have been deprecated for removal without
  replacement. CLI tests should be done in pmd-core only (and in PMD7 in pmd-cli). Individual language modules
  shouldn't need to test the CLI integration logic again. Instead, the individual language modules should test their
  functionality as unit tests.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDConfiguration.LanguageConverter.html#"><code>CPDConfiguration.LanguageConverter</code></a>

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/document/FileCollector.html#addZipFile(java.nio.file.Path)"><code>FileCollector#addZipFile</code></a> has been deprecated. It is replaced
  by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/FileCollector.html#addZipFileWithContent(java.nio.file.Path)"><code>FileCollector#addZipFileWithContent</code></a> which directly adds the
  content of the zip file for analysis.

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setReportShortNames(boolean)"><code>PMDConfiguration#setReportShortNames</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#isReportShortNames()"><code>PMDConfiguration#isReportShortNames</code></a> have been deprecated for removal.
  Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/AbstractConfiguration.html#addRelativizeRoot(java.nio.file.Path)"><code>AbstractConfiguration#addRelativizeRoot</code></a> instead.

**Internal APIs**

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/renderers/CSVWriter.html#"><code>CSVWriter</code></a>
* Some fields in <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.55.0/net/sourceforge/pmd/ant/AbstractAntTestHelper.html"><code>AbstractAntTestHelper</code></a>

**Experimental APIs**

* CPDReport has a new method which limited mutation of a given report:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDReport.html#filterMatches(net.sourceforge.pmd.util.Predicate)"><code>filterMatches</code></a> creates a new CPD report
      with some matches removed with a given predicate based filter.

#### 6.53.0

**Deprecated APIs**

**For removal**

These classes / APIs have been deprecated and will be removed with PMD 7.0.0.

* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/design/ExcessiveLengthRule.html#"><code>ExcessiveLengthRule</code></a> (Java)

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
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a> and `PMD.StatusCode` - PMD 7 will ship with a revamped CLI split from pmd-core. To programmatically launch analysis you can use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PmdAnalysis.html#"><code>PmdAnalysis</code></a>.
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#getAllInputPaths()"><code>PMDConfiguration#getAllInputPaths</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#getInputPathList()"><code>PMDConfiguration#getInputPathList</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setInputPaths(java.util.List)"><code>PMDConfiguration#setInputPaths</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#setInputPathList(java.util.List)"><code>PMDConfiguration#setInputPathList</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#addInputPath(java.lang.String)"><code>PMDConfiguration#addInputPath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#addInputPath(java.nio.file.Path)"><code>PMDConfiguration#addInputPath</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#getInputFilePath()"><code>PMDConfiguration#getInputFilePath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#getInputFile()"><code>PMDConfiguration#getInputFile</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#getIgnoreFilePath()"><code>PMDConfiguration#getIgnoreFilePath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#getIgnoreFile()"><code>PMDConfiguration#getIgnoreFile</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setInputFilePath(java.lang.String)"><code>PMDConfiguration#setInputFilePath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#setInputFilePath(java.nio.file.Path)"><code>PMDConfiguration#setInputFilePath</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setIgnoreFilePath(java.lang.String)"><code>PMDConfiguration#setIgnoreFilePath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#setIgnoreFilePath(java.nio.file.Path)"><code>PMDConfiguration#setIgnoreFilePath</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#getInputUri()"><code>PMDConfiguration#getInputUri</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#getUri()"><code>PMDConfiguration#getUri</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setInputUri(java.lang.String)"><code>PMDConfiguration#setInputUri</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#setInputUri(java.net.URI)"><code>PMDConfiguration#setInputUri</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#getReportFile()"><code>PMDConfiguration#getReportFile</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#getReportFilePath()"><code>PMDConfiguration#getReportFilePath</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setReportFile(java.lang.String)"><code>PMDConfiguration#setReportFile</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#setReportFile(java.nio.file.Path)"><code>PMDConfiguration#setReportFile</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#isStressTest()"><code>PMDConfiguration#isStressTest</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setStressTest(boolean)"><code>PMDConfiguration#setStressTest</code></a> - Will be removed with no replacement.
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#isBenchmark()"><code>PMDConfiguration#isBenchmark</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setBenchmark(boolean)"><code>PMDConfiguration#setBenchmark</code></a> - Will be removed with no replacement, the CLI will still support it.
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPD.html#"><code>CPD</code></a> and `CPD.StatusCode` - PMD 7 will ship with a revamped CLI split from pmd-core. An alternative to programmatically launch CPD analysis will be added in due time.

* In order to reduce the dependency on Apex Jorje classes, the method <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.55.0/net/sourceforge/pmd/lang/vf/DataType.html#fromBasicType(apex.jorje.semantic.symbol.type.BasicType)"><code>DataType#fromBasicType</code></a>
  has been deprecated. The equivalent method <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.55.0/net/sourceforge/pmd/lang/vf/DataType.html#fromTypeName(java.lang.String)"><code>fromTypeName</code></a> should be used instead.

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
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ASTAssignmentExpression.html#getOperator()"><code>ASTAssignmentExpression#getOperator</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ASTBinaryExpression.html#getOperator()"><code>ASTBinaryExpression#getOperator</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ASTBooleanExpression.html#getOperator()"><code>ASTBooleanExpression#getOperator</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ASTPostfixExpression.html#getOperator()"><code>ASTPostfixExpression#getOperator</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ASTPrefixExpression.html#getOperator()"><code>ASTPrefixExpression#getOperator</code></a>

  All these classes have now a new `getOp()` method. Existing code should be refactored to use this method instead.
  It returns the new enums, like <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/AssignmentOperator.html#"><code>AssignmentOperator</code></a>, and avoids
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
* The interface <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/renderer/CPDRenderer.html#"><code>CPDRenderer</code></a> is deprecated. For custom CPD renderers
  the new interface <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/CPDReportRenderer.html#"><code>CPDReportRenderer</code></a> should be used.
* The class <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.55.0/net/sourceforge/pmd/testframework/TestDescriptor.html#"><code>TestDescriptor</code></a> is deprecated, replaced with <a href="https://docs.pmd-code.org/apidocs/pmd-test-schema/7.0.0/net/sourceforge/pmd/test/schema/RuleTestDescriptor.html#"><code>RuleTestDescriptor</code></a>.
* Many methods of <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.55.0/net/sourceforge/pmd/testframework/RuleTst.html#"><code>RuleTst</code></a> have been deprecated as internal API.

**Experimental APIs**

* To support the Java preview language features "Pattern Matching for Switch" and "Record Patterns", the following
  AST nodes have been introduced as experimental:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTSwitchGuard.html#"><code>ASTSwitchGuard</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTRecordPattern.html#"><code>ASTRecordPattern</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTComponentPatternList.html#"><code>ASTComponentPatternList</code></a>

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#setRenderer(net.sourceforge.pmd.cpd.Renderer)"><code>CPDConfiguration#setRenderer</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#setCPDRenderer(net.sourceforge.pmd.cpd.renderer.CPDRenderer)"><code>CPDConfiguration#setCPDRenderer</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getRenderer()"><code>CPDConfiguration#getRenderer</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getCPDRenderer()"><code>CPDConfiguration#getCPDRenderer</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getRendererFromString(java.lang.String,java.lang.String)"><code>CPDConfiguration#getRendererFromString</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getCPDRendererFromString(java.lang.String,java.lang.String)"><code>CPDConfiguration#getCPDRendererFromString</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/renderer/CPDRendererAdapter.html#"><code>CPDRendererAdapter</code></a>

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

- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSetReferenceId.html#toString()"><code>toString</code></a> is now deprecated. The format of this
  method will remain the same until PMD 7. The deprecation is intended to steer users
  away from relying on this format, as it may be changed in PMD 7.
- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#getInputPaths()"><code>getInputPaths</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setInputPaths(java.lang.String)"><code>setInputPaths</code></a> are now deprecated.
  A new set of methods have been added, which use lists and do not rely on comma splitting.

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPDCommandLineInterface.html#"><code>CPDCommandLineInterface</code></a> has been internalized. In order to execute CPD either
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPD.html#runCpd(java.lang.String...)"><code>CPD#runCpd</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/CPD.html#main(java.lang.String[])"><code>CPD#main</code></a>
  should be used.
- Several members of <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.55.0/net/sourceforge/pmd/cli/BaseCPDCLITest.html#"><code>BaseCPDCLITest</code></a> have been deprecated with replacements.
- The methods <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/ant/Formatter.html#start(java.lang.String)"><code>Formatter#start</code></a>,
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/ant/Formatter.html#end(net.sourceforge.pmd.Report)"><code>Formatter#end</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/ant/Formatter.html#getRenderer()"><code>Formatter#getRenderer</code></a>,
  and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/ant/Formatter.html#isNoOutputSupplied()"><code>Formatter#isNoOutputSupplied</code></a> have been internalized.

#### 6.45.0

**Experimental APIs**

* Report has two new methods which allow limited mutations of a given report:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Report.html#filterViolations(java.util.function.Predicate)"><code>Report#filterViolations</code></a> creates a new report with
      some violations removed with a given predicate based filter.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Report.html#union(net.sourceforge.pmd.reporting.Report)"><code>Report#union</code></a> can combine two reports into a single new Report.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/util/Predicate.html#"><code>net.sourceforge.pmd.util.Predicate</code></a> will be replaced in PMD7 with the standard Predicate interface from java8.
* The module `pmd-html` is entirely experimental right now. Anything in the package
  `net.sourceforge.pmd.lang.html` should be used cautiously.

#### 6.44.0

**Deprecated API**

* Several members of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a> have been newly deprecated, including:
    - `PMD#EOL`: use `System#lineSeparator()`
    - `PMD#SUPPRESS_MARKER`: use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#DEFAULT_SUPPRESS_MARKER"><code>DEFAULT_SUPPRESS_MARKER</code></a>
    - `PMD#processFiles`: use the new programmatic API
    - `PMD#getApplicableFiles`: is internal
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#prependClasspath(java.lang.String)"><code>PMDConfiguration#prependClasspath</code></a> is deprecated
  in favour of <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#prependAuxClasspath(java.lang.String)"><code>prependAuxClasspath</code></a>.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#setRuleSets(java.lang.String)"><code>PMDConfiguration#setRuleSets</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMDConfiguration.html#getRuleSets()"><code>getRuleSets</code></a> are deprecated. Use instead
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#setRuleSets(java.util.List)"><code>setRuleSets</code></a>,
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#addRuleSet(java.lang.String)"><code>addRuleSet</code></a>,
  and <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDConfiguration.html#getRuleSetPaths()"><code>getRuleSetPaths</code></a>.
* Several members of <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.55.0/net/sourceforge/pmd/cli/BaseCLITest.html#"><code>BaseCLITest</code></a> have been deprecated with replacements.
* Several members of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cli/PMDCommandLineInterface.html#"><code>PMDCommandLineInterface</code></a> have been explicitly deprecated.
  The whole class however was deprecated long ago already with 6.30.0. It is internal API and should
  not be used.

* In modelica, the rule classes <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/6.55.0/net/sourceforge/pmd/lang/modelica/rule/AmbiguousResolutionRule.html#"><code>AmbiguousResolutionRule</code></a>
  and <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/6.55.0/net/sourceforge/pmd/lang/modelica/rule/ConnectUsingNonConnector.html#"><code>ConnectUsingNonConnector</code></a> have been deprecated,
  since they didn't comply to the usual rule class naming conventions yet.
  The replacements are in the subpackage `bestpractices`.

**Experimental APIs**

*   Together with the new programmatic API the interface
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/TextFile.html#"><code>TextFile</code></a> has been added as *experimental*. It intends
    to replace <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/util/datasource/DataSource.html#"><code>DataSource</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/SourceCode.html#"><code>SourceCode</code></a> in the long term.

    This interface will change in PMD 7 to support read/write operations
    and other things. You don't need to use it in PMD 6, as <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/document/FileCollector.html#"><code>FileCollector</code></a>
    decouples you from this. A file collector is available through <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PmdAnalysis.html#files()"><code>PmdAnalysis#files</code></a>.

#### 6.43.0

**Deprecated API**

Some API deprecations were performed in core PMD classes, to improve compatibility with PMD 7.
- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#"><code>Report</code></a>: the constructor and other construction methods like addViolation or createReport
- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleContext.html#"><code>RuleContext</code></a>: all constructors, getters and setters. A new set
  of stable methods, matching those in PMD 7, was added to replace the `addViolation`
  overloads of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/AbstractRule.html#"><code>AbstractRule</code></a>. In PMD 7, `RuleContext` will
  be the API to report violations, and it can already be used as such in PMD 6.
- The field <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#configuration"><code>configuration</code></a> is unused and will be removed.

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSet.html#"><code>RuleSet</code></a>: methods that serve to apply rules, including `apply`, `start`, `end`, `removeDysfunctionalRules`
- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/renderers/AbstractAccumulatingRenderer.html#renderFileReport(Report)"><code>AbstractAccumulatingRenderer#renderFileReport</code></a> is internal API
  and should not be overridden in own renderers.

**Changed API**

It is now forbidden to report a violation:
- With a `null` node
- With a `null` message
- With a `null` set of format arguments (prefer a zero-length array)

Note that the message is set from the XML rule declaration, so this is only relevant
if you instantiate rules manually.

<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleContext.html#"><code>RuleContext</code></a> now requires setting the current rule before calling
<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Rule.html#apply(java.util.List,net.sourceforge.pmd.RuleContext)"><code>apply</code></a>. This is
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

*   The interface <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTCommentContainer.html#"><code>ASTCommentContainer</code></a> has been added to the Apex AST.
    It provides a way to check whether a node contains at least one comment. Currently, this is only implemented for
    <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ASTCatchBlockStatement.html#"><code>ASTCatchBlockStatement</code></a> and used by the rule
    [`EmptyCatchBlock`](https://docs.pmd-code.org/pmd-doc-7.0.0/pmd_rules_apex_errorprone.html#emptycatchblock).
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
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#isSealed()"><code>ASTClassOrInterfaceDeclaration#isSealed</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#isNonSealed()"><code>ASTClassOrInterfaceDeclaration#isNonSealed</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#getPermittedSubclasses()"><code>ASTClassOrInterfaceDeclaration#getPermittedSubclasses</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTPermitsList.html#"><code>ASTPermitsList</code></a>

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   The inner class <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/TokenEntry.State.html#"><code>net.sourceforge.pmd.cpd.TokenEntry.State</code></a> is considered to be internal API.
    It will probably be moved away with PMD 7.

#### 6.36.0

No changes.

#### 6.35.0

**Deprecated API**

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#doPMD(net.sourceforge.pmd.PMDConfiguration)"><code>PMD#doPMD</code></a> is deprecated.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#runPmd(net.sourceforge.pmd.PMDConfiguration)"><code>PMD#runPmd</code></a> instead.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#run(java.lang.String[])"><code>PMD#run</code></a> is deprecated.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#runPmd(java.lang.String...)"><code>PMD#runPmd</code></a> instead.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/ThreadSafeReportListener.html#"><code>ThreadSafeReportListener</code></a> and the methods to use them in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#"><code>Report</code></a>
    (<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#addListener(net.sourceforge.pmd.ThreadSafeReportListener)"><code>addListener</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#getListeners()"><code>getListeners</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#addListeners(java.util.List)"><code>addListeners</code></a>)
    are deprecated. This functionality will be replaced by another TBD mechanism in PMD 7.

#### 6.34.0

No changes.

#### 6.33.0

No changes.

#### 6.32.0

**Experimental APIs**

*   The experimental class `ASTTypeTestPattern` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTypePattern.html#"><code>ASTTypePattern</code></a>
    in order to align the naming to the JLS.
*   The experimental class `ASTRecordConstructorDeclaration` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTCompactConstructorDeclaration.html#"><code>ASTCompactConstructorDeclaration</code></a>
    in order to align the naming to the JLS.
*   The AST types and APIs around Pattern Matching and Records are not experimental anymore:
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTVariableId.html#isPatternBinding()"><code>ASTVariableId#isPatternBinding</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTPattern.html#"><code>ASTPattern</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTTypePattern.html#"><code>ASTTypePattern</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTRecordDeclaration.html#"><code>ASTRecordDeclaration</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTRecordComponentList.html#"><code>ASTRecordComponentList</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTRecordComponent.html#"><code>ASTRecordComponent</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTRecordBody.html#"><code>ASTRecordBody</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTCompactConstructorDeclaration.html#"><code>ASTCompactConstructorDeclaration</code></a>

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   The protected or public member of the Java rule <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/bestpractices/AvoidUsingHardCodedIPRule.html#"><code>AvoidUsingHardCodedIPRule</code></a>
    are deprecated and considered to be internal API. They will be removed with PMD 7.

#### 6.31.0

**Deprecated API**

*   <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.55.0/net/sourceforge/pmd/lang/xml/rule/AbstractDomXmlRule.html#"><code>AbstractDomXmlRule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.55.0/net/sourceforge/pmd/lang/wsdl/rule/AbstractWsdlRule.html#"><code>AbstractWsdlRule</code></a>
*   A few methods of <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.55.0/net/sourceforge/pmd/lang/xml/rule/AbstractXmlRule.html#"><code>AbstractXmlRule</code></a>

**Experimental APIs**

*   The method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/GenericToken.html#getKind()"><code>GenericToken#getKind</code></a> has been added as experimental. This
    unifies the token interface for both JavaCC and Antlr. The already existing method
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/token/AntlrToken.html#getKind()"><code>AntlrToken#getKind</code></a> is therefore experimental as well. The
    returned constant depends on the actual language and might change whenever the grammar
    of the language is changed.

#### 6.30.0

**Deprecated API**

**Around RuleSet parsing**

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSetFactory.html#"><code>RuleSetFactory</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#"><code>RulesetsFactoryUtils</code></a> have been deprecated in favor of <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSetLoader.html#"><code>RuleSetLoader</code></a>. This is easier to configure, and more maintainable than the multiple overloads of `RulesetsFactoryUtils`.
* Some static creation methods have been added to <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSet.html#"><code>RuleSet</code></a> for simple cases, eg <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/RuleSet.html#forSingleRule(net.sourceforge.pmd.lang.rule.Rule)"><code>forSingleRule</code></a>. These replace some counterparts in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSetFactory.html#"><code>RuleSetFactory</code></a>
* Since <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSets.html#"><code>RuleSets</code></a> is also deprecated, many APIs that require a RuleSets instance now are deprecated, and have a counterpart that expects a `List<RuleSet>`.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSetReferenceId.html#"><code>RuleSetReferenceId</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSetReference.html#"><code>RuleSetReference</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSetFactoryCompatibility.html#"><code>RuleSetFactoryCompatibility</code></a> are deprecated. They are most likely not relevant outside of the implementation of pmd-core.

**Around the `PMD` class**

Many classes around PMD's entry point (<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a>) have been deprecated as internal, including:
* The contents of the packages <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cli/package-summary.html"><code>net.sourceforge.pmd.cli</code></a> in pmd-core, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/processor/package-summary.html"><code>net.sourceforge.pmd.processor</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/SourceCodeProcessor.html#"><code>SourceCodeProcessor</code></a>
* The constructors of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a> (the class will be made a utility class)

**Miscellaneous**

*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTPackageDeclaration.html#getPackageNameImage()"><code>ASTPackageDeclaration#getPackageNameImage</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTTypeParameter.html#getParameterName()"><code>ASTTypeParameter#getParameterName</code></a>
    and the corresponding XPath attributes. In both cases they're replaced with a new method `getName`,
    the attribute is `@Name`.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceBody.html#isAnonymousInnerClass()"><code>ASTClassOrInterfaceBody#isAnonymousInnerClass</code></a>,
    and <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceBody.html#isEnumChild()"><code>ASTClassOrInterfaceBody#isEnumChild</code></a>,
    refs [#905](https://github.com/pmd/pmd/issues/905)

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.55.0/net/sourceforge/pmd/lang/ecmascript/Ecmascript3Handler.html#"><code>net.sourceforge.pmd.lang.ecmascript.Ecmascript3Handler</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.55.0/net/sourceforge/pmd/lang/ecmascript/Ecmascript3Parser.html#"><code>net.sourceforge.pmd.lang.ecmascript.Ecmascript3Parser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.55.0/net/sourceforge/pmd/lang/ecmascript/ast/EcmascriptParser.html#parserOptions"><code>EcmascriptParser#parserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.55.0/net/sourceforge/pmd/lang/ecmascript/ast/EcmascriptParser.html#getSuppressMap()"><code>EcmascriptParser#getSuppressMap</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/ParametricRuleViolation.html#"><code>net.sourceforge.pmd.lang.rule.ParametricRuleViolation</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ParserOptions.html#suppressMarker"><code>ParserOptions#suppressMarker</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/6.55.0/net/sourceforge/pmd/lang/modelica/rule/ModelicaRuleViolationFactory.html#"><code>net.sourceforge.pmd.lang.modelica.rule.ModelicaRuleViolationFactory</code></a>

#### 6.29.0

No changes.

#### 6.28.0

**Deprecated API**

**For removal**

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleViolationComparator.html#"><code>net.sourceforge.pmd.RuleViolationComparator</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/RuleViolation.html#DEFAULT_COMPARATOR"><code>RuleViolation#DEFAULT_COMPARATOR</code></a> instead.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/AbstractTokenizer.html#"><code>net.sourceforge.pmd.cpd.AbstractTokenizer</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/AnyCpdLexer.html#"><code>net.sourceforge.pmd.cpd.AnyCpdLexer</code></a> instead (previously called AnyTokenizer).
* <a href="https://docs.pmd-code.org/apidocs/pmd-fortran/6.55.0/net/sourceforge/pmd/cpd/FortranTokenizer.html#"><code>net.sourceforge.pmd.cpd.FortranTokenizer</code></a>. Was replaced by an <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/AnyCpdLexer.html#"><code>AnyCpdLexer</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-fortran/7.0.0/net/sourceforge/pmd/lang/fortran/FortranLanguageModule.html#createCpdLexer(net.sourceforge.pmd.lang.LanguagePropertyBundle)"><code>FortranLanguageModule#createCpdLexer</code></a> anyway.
* <a href="https://docs.pmd-code.org/apidocs/pmd-perl/6.55.0/net/sourceforge/pmd/cpd/PerlTokenizer.html#"><code>net.sourceforge.pmd.cpd.PerlTokenizer</code></a>. Was replaced by an <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/AnyCpdLexer.html#"><code>AnyCpdLexer</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-perl/7.0.0/net/sourceforge/pmd/lang/perl/PerlLanguageModule.html#createCpdLexer(net.sourceforge.pmd.lang.LanguagePropertyBundle)"><code>PerlLanguageModule#createCpdLexer</code></a> anyway.
* <a href="https://docs.pmd-code.org/apidocs/pmd-ruby/6.55.0/net/sourceforge/pmd/cpd/RubyTokenizer.html#"><code>net.sourceforge.pmd.cpd.RubyTokenizer</code></a>. Was replaced by an <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/cpd/AnyCpdLexer.html#"><code>AnyCpdLexer</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-ruby/7.0.0/net/sourceforge/pmd/lang/ruby/RubyLanguageModule.html#createCpdLexer(net.sourceforge.pmd.lang.LanguagePropertyBundle)"><code>RubyLanguageModule#createCpdLexer</code></a> anyway.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/RuleReference.html#getOverriddenLanguage()"><code>RuleReference#getOverriddenLanguage</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/RuleReference.html#setLanguage(net.sourceforge.pmd.lang.Language)"><code>RuleReference#setLanguage</code></a>
* Antlr4 generated lexers:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-cs/6.55.0/net/sourceforge/pmd/lang/cs/antlr4/CSharpLexer.html#"><code>net.sourceforge.pmd.lang.cs.antlr4.CSharpLexer</code></a> will be moved to package `net.sourceforge.pmd.lang.cs.ast` with PMD 7.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-dart/6.55.0/net/sourceforge/pmd/lang/dart/antlr4/Dart2Lexer.html#"><code>net.sourceforge.pmd.lang.dart.antlr4.Dart2Lexer</code></a> will be renamed to `DartLexer` and moved to package
      `net.sourceforge.pmd.lang.dart.ast` with PMD 7. All other classes in the old package will be removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-go/6.55.0/net/sourceforge/pmd/lang/go/antlr4/GolangLexer.html#"><code>net.sourceforge.pmd.lang.go.antlr4.GolangLexer</code></a> will be moved to package
      `net.sourceforge.pmd.lang.go.ast` with PMD 7. All other classes in the old package will be removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/6.55.0/net/sourceforge/pmd/lang/kotlin/antlr4/Kotlin.html#"><code>net.sourceforge.pmd.lang.kotlin.antlr4.Kotlin</code></a> will be renamed to `KotlinLexer` and moved to package
      `net.sourceforge.pmd.lang.kotlin.ast` with PMD 7.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-lua/6.55.0/net/sourceforge/pmd/lang/lua/antlr4/LuaLexer.html#"><code>net.sourceforge.pmd.lang.lua.antlr4.LuaLexer</code></a> will be moved to package
      `net.sourceforge.pmd.lang.lua.ast` with PMD 7. All other classes in the old package will be removed.

#### 6.27.0

*   XML rule definition in rulesets: In PMD 7, the `language` attribute will be required on all `rule`
    elements that declare a new rule. Some base rule classes set the language implicitly in their
    constructor, and so this is not required in all cases for the rule to work. But this
    behavior will be discontinued in PMD 7, so missing `language` attributes are now
    reported as a forward compatibility warning.

**Deprecated API**

**For removal**

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Rule.html#getParserOptions()"><code>Rule#getParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/Parser.html#getParserOptions()"><code>Parser#getParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/AbstractParser.html#"><code>AbstractParser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleContext.html#removeAttribute(java.lang.String)"><code>RuleContext#removeAttribute</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleContext.html#getAttribute(java.lang.String)"><code>RuleContext#getAttribute</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleContext.html#setAttribute(java.lang.String,java.lang.Object)"><code>RuleContext#setAttribute</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ApexParserOptions.html#"><code>ApexParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTThrowStatement.html#getFirstClassOrInterfaceTypeImage()"><code>ASTThrowStatement#getFirstClassOrInterfaceTypeImage</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.55.0/net/sourceforge/pmd/lang/ecmascript/EcmascriptParserOptions.html#"><code>EcmascriptParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.55.0/net/sourceforge/pmd/lang/ecmascript/rule/EcmascriptXPathRule.html#"><code>EcmascriptXPathRule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.55.0/net/sourceforge/pmd/lang/xml/XmlParserOptions.html#"><code>XmlParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.55.0/net/sourceforge/pmd/lang/xml/rule/XmlXPathRule.html#"><code>XmlXPathRule</code></a>
*   Properties of <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.55.0/net/sourceforge/pmd/lang/xml/rule/AbstractXmlRule.html#"><code>AbstractXmlRule</code></a>

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.ReadableDuration.html#"><code>net.sourceforge.pmd.Report.ReadableDuration</code></a>
*   Many methods of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#"><code>net.sourceforge.pmd.Report</code></a>. They are replaced by accessors
    that produce a List. For example, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#iterator()"><code>iterator()</code></a>
    (and implementing Iterable) and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#isEmpty()"><code>isEmpty()</code></a> are both
    replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/reporting/Report.html#getViolations()"><code>getViolations()</code></a>.

*   The dataflow codebase is deprecated for removal in PMD 7. This includes all code in the following packages, and their subpackages:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.55.0/net/sourceforge/pmd/lang/plsql/dfa/package-summary.html"><code>net.sourceforge.pmd.lang.plsql.dfa</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/dfa/package-summary.html"><code>net.sourceforge.pmd.lang.java.dfa</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/dfa/package-summary.html"><code>net.sourceforge.pmd.lang.dfa</code></a>
    * and the class <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.55.0/net/sourceforge/pmd/lang/plsql/PLSQLDataFlowHandler.html#"><code>PLSQLDataFlowHandler</code></a>

*   <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.55.0/net/sourceforge/pmd/lang/vf/VfSimpleCharStream.html#"><code>VfSimpleCharStream</code></a>

*   <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.55.0/net/sourceforge/pmd/lang/jsp/ast/ASTJspDeclarations.html#"><code>ASTJspDeclarations</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.55.0/net/sourceforge/pmd/lang/jsp/ast/ASTJspDocument.html#"><code>ASTJspDocument</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/6.55.0/net/sourceforge/pmd/lang/scala/ast/ScalaParserVisitorAdapter.html#zero()"><code>ScalaParserVisitorAdapter#zero</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/6.55.0/net/sourceforge/pmd/lang/scala/ast/ScalaParserVisitorAdapter.html#combine(R,R)"><code>ScalaParserVisitorAdapter#combine</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ApexParserVisitorReducedAdapter.html#"><code>ApexParserVisitorReducedAdapter</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaParserVisitorReducedAdapter.html#"><code>JavaParserVisitorReducedAdapter</code></a>

* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/typeresolution/TypeHelper.html#"><code>TypeHelper</code></a> is deprecated in
  favor of <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/types/TypeTestUtil.html#"><code>TypeTestUtil</code></a>, which has the
  same functionality, but a slightly changed API.
* Many of the classes in <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/symboltable/package-summary.html"><code>net.sourceforge.pmd.lang.java.symboltable</code></a>
  are deprecated as internal API.

#### 6.26.0

**Deprecated API**

**For removal**

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/RuleChainVisitor.html#"><code>RuleChainVisitor</code></a> and all implementations in language modules
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/AbstractRuleChainVisitor.html#"><code>AbstractRuleChainVisitor</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/Language.html#getRuleChainVisitorClass()"><code>Language#getRuleChainVisitorClass</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/BaseLanguageModule.html#<init>(java.lang.String,java.lang.String,java.lang.String,java.lang.Class,java.lang.String...)"><code>BaseLanguageModule#&lt;init&gt;</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/ImportWrapper.html#"><code>ImportWrapper</code></a>

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

*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/AbstractIgnoredAnnotationRule.html#"><code>AbstractIgnoredAnnotationRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/AbstractInefficientZeroCheck.html#"><code>AbstractInefficientZeroCheck</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/AbstractJUnitRule.html#"><code>AbstractJUnitRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/AbstractJavaMetricsRule.html#"><code>AbstractJavaMetricsRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/AbstractLombokAwareRule.html#"><code>AbstractLombokAwareRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/AbstractPoorMethodCall.html#"><code>AbstractPoorMethodCall</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/bestpractices/AbstractSunSecureRule.html#"><code>AbstractSunSecureRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/design/AbstractNcssCountRule.html#"><code>AbstractNcssCountRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/documentation/AbstractCommentRule.html#"><code>AbstractCommentRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/performance/AbstractOptimizationRule.html#"><code>AbstractOptimizationRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/regex/RegexHelper.html#"><code>RegexHelper</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/rule/AbstractApexUnitTestRule.html#"><code>AbstractApexUnitTestRule</code></a> (Apex)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/rule/design/AbstractNcssCountRule.html#"><code>AbstractNcssCountRule</code></a> (Apex)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.55.0/net/sourceforge/pmd/lang/plsql/rule/design/AbstractNcssCountRule.html#"><code>AbstractNcssCountRule</code></a> (PLSQL)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ApexParser.html#"><code>ApexParser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ApexHandler.html#"><code>ApexHandler</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleChain.html#"><code>RuleChain</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSets.html#"><code>RuleSets</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#getRuleSets(java.lang.String,net.sourceforge.pmd.RuleSetFactory)"><code>RulesetsFactoryUtils#getRuleSets</code></a>

**For removal**

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/TokenEntry.html#<init>(java.lang.String,java.lang.String,int)"><code>TokenEntry#TokenEntry</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.55.0/net/sourceforge/pmd/testframework/AbstractTokenizerTest.html#"><code>AbstractTokenizerTest</code></a>. Use CpdTextComparisonTest in module pmd-lang-test instead.
    For details see
    [Testing your implementation](pmd_devdocs_major_adding_new_cpd_language.html#testing-your-implementation)
    in the developer documentation.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ASTAnnotation.html#suppresses(net.sourceforge.pmd.Rule)"><code>ASTAnnotation#suppresses</code></a> (Apex)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/rule/ApexXPathRule.html#"><code>ApexXPathRule</code></a> (Apex)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/SymbolTableTestRule.html#"><code>SymbolTableTestRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/performance/InefficientStringBufferingRule.html#isInStringBufferOperation(net.sourceforge.pmd.lang.ast.Node,int,java.lang.String)"><code>InefficientStringBufferingRule#isInStringBufferOperation</code></a>

#### 6.24.0

**Deprecated APIs**

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/BaseLanguageModule.html#addVersion(java.lang.String,net.sourceforge.pmd.lang.LanguageVersionHandler,boolean)"><code>BaseLanguageModule#addVersion(String, LanguageVersionHandler, boolean)</code></a>
*   Some members of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/TokenMgrError.html#"><code>TokenMgrError</code></a>, in particular, a new constructor is available
    that should be preferred to the old ones
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/antlr/AntlrTokenManager.ANTLRSyntaxError.html#"><code>ANTLRSyntaxError</code></a>

**Experimental APIs**

**Note:** Experimental APIs are identified with the annotation <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/annotation/Experimental.html#"><code>Experimental</code></a>,
see its javadoc for details

* The experimental methods in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/BaseLanguageModule.html#"><code>BaseLanguageModule</code></a> have been replaced by a
  definitive API.

#### 6.23.0

**Deprecated APIs**

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/xpath/AbstractXPathRuleQuery.html#"><code>AbstractXPathRuleQuery</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQuery.html#"><code>JaxenXPathRuleQuery</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/xpath/SaxonXPathRuleQuery.html#"><code>SaxonXPathRuleQuery</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/xpath/XPathRuleQuery.html#"><code>XPathRuleQuery</code></a>

**In ASTs**

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated in the **Apex**, **Javascript**, **PL/SQL**, **Scala** and **Visualforce** ASTs:

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/annotation/InternalApi.html#"><code>InternalApi</code></a>. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
*   In the meantime you should use interfaces like <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.0.0/net/sourceforge/pmd/lang/visualforce/ast/VfNode.html#"><code>VfNode</code></a> or
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a>, or the other published interfaces in this package,
    to refer to nodes generically.
*   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The implementation classes of <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Parser.html#"><code>Parser</code></a> (eg <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.55.0/net/sourceforge/pmd/lang/vf/VfParser.html#"><code>VfParser</code></a>) are deprecated and should not be used directly.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getParser()"><code>LanguageVersionHandler#getParser</code></a> instead.
*   The implementation classes of <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/TokenManager.html#"><code>TokenManager</code></a> (eg <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.55.0/net/sourceforge/pmd/lang/vf/VfTokenManager.html#"><code>VfTokenManager</code></a>) are deprecated and should not be used outside of our implementation.
    **This also affects CPD-only modules**.

These deprecations are added to the following language modules in this release.
Please look at the package documentation to find out the full list of deprecations.
* Apex: <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/package-summary.html"><code>net.sourceforge.pmd.lang.apex.ast</code></a>
* Javascript: <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.55.0/net/sourceforge/pmd/lang/ecmascript/ast/package-summary.html"><code>net.sourceforge.pmd.lang.ecmascript.ast</code></a>
* PL/SQL: <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.55.0/net/sourceforge/pmd/lang/plsql/ast/package-summary.html"><code>net.sourceforge.pmd.lang.plsql.ast</code></a>
* Scala: <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/6.55.0/net/sourceforge/pmd/lang/scala/ast/package-summary.html"><code>net.sourceforge.pmd.lang.scala.ast</code></a>
* Visualforce: <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.55.0/net/sourceforge/pmd/lang/vf/ast/package-summary.html"><code>net.sourceforge.pmd.lang.vf.ast</code></a>

These deprecations have already been rolled out in a previous version for the
following languages:
* Java: <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/package-summary.html"><code>net.sourceforge.pmd.lang.java.ast</code></a>
* Java Server Pages: <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.55.0/net/sourceforge/pmd/lang/jsp/ast/package-summary.html"><code>net.sourceforge.pmd.lang.jsp.ast</code></a>
* Velocity Template Language: <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.55.0/net/sourceforge/pmd/lang/vm/ast/package-summary.html"><code>net.sourceforge.pmd.lang.vm.ast</code></a>

Outside of these packages, these changes also concern the following TokenManager
implementations, and their corresponding Parser if it exists (in the same package):

*   <a href="https://docs.pmd-code.org/apidocs/pmd-cpp/6.55.0/net/sourceforge/pmd/lang/cpp/CppTokenManager.html#"><code>CppTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/JavaTokenManager.html#"><code>JavaTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.55.0/net/sourceforge/pmd/lang/ecmascript5/Ecmascript5TokenManager.html#"><code>Ecmascript5TokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.55.0/net/sourceforge/pmd/lang/jsp/JspTokenManager.html#"><code>JspTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-matlab/6.55.0/net/sourceforge/pmd/lang/matlab/MatlabTokenManager.html#"><code>MatlabTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/6.55.0/net/sourceforge/pmd/lang/modelica/ModelicaTokenManager.html#"><code>ModelicaTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-objectivec/6.55.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCTokenManager.html#"><code>ObjectiveCTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.55.0/net/sourceforge/pmd/lang/plsql/PLSQLTokenManager.html#"><code>PLSQLTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-python/6.55.0/net/sourceforge/pmd/lang/python/PythonTokenManager.html#"><code>PythonTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.55.0/net/sourceforge/pmd/lang/vf/VfTokenManager.html#"><code>VfTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.55.0/net/sourceforge/pmd/lang/vm/VmTokenManager.html#"><code>VmTokenManager</code></a>


In the **Java AST** the following attributes are deprecated and will issue a warning when used in XPath rules:

*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAdditiveExpression.html#getImage()"><code>ASTAdditiveExpression#getImage</code></a> - use `getOperator()` instead
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTVariableDeclaratorId.html#getImage()"><code>ASTVariableDeclaratorId#getImage</code></a> - use `getName()` instead
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTVariableDeclaratorId.html#getVariableName()"><code>ASTVariableDeclaratorId#getVariableName</code></a> - use `getName()` instead

**For removal**

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/Parser.html#getTokenManager(java.lang.String,java.io.Reader)"><code>Parser#getTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/TokenManager.html#setFileName(java.lang.String)"><code>TokenManager#setFileName</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/AbstractTokenManager.html#setFileName(java.lang.String)"><code>AbstractTokenManager#setFileName</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/AbstractTokenManager.html#getFileName()"><code>AbstractTokenManager#getFileName</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/token/AntlrToken.html#getType()"><code>AntlrToken#getType</code></a> - use `getKind()` instead.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/ImmutableLanguage.html#"><code>ImmutableLanguage</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/MockRule.html#"><code>MockRule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/Node.html#getFirstParentOfAnyType(java.lang.Class...)"><code>Node#getFirstParentOfAnyType</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/Node.html#getAsDocument()"><code>Node#getAsDocument</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/AbstractNode.html#hasDescendantOfAnyType(java.lang.Class...)"><code>AbstractNode#hasDescendantOfAnyType</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTRecordDeclaration.html#getComponentList()"><code>ASTRecordDeclaration#getComponentList</code></a>
*   Multiple fields, constructors and methods in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/XPathRule.html#"><code>XPathRule</code></a>. See javadoc for details.

#### 6.22.0

**Deprecated APIs**

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/JavaLanguageHandler.html#"><code>JavaLanguageHandler</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/JavaLanguageParser.html#"><code>JavaLanguageParser</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/JavaDataFlowHandler.html#"><code>JavaDataFlowHandler</code></a>
* Implementations of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#"><code>RuleViolationFactory</code></a> in each
  language module, eg <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolationFactory.html#"><code>JavaRuleViolationFactory</code></a>.
  See javadoc of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#"><code>RuleViolationFactory</code></a>.
* Implementations of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleViolation.html#"><code>RuleViolation</code></a> in each language module,
  eg <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolation.html#"><code>JavaRuleViolation</code></a>. See javadoc of
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleViolation.html#"><code>RuleViolation</code></a>.

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/rules/RuleFactory.html#"><code>RuleFactory</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/rules/RuleBuilder.html#"><code>RuleBuilder</code></a>
* Constructors of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSetFactory.html#"><code>RuleSetFactory</code></a>, use factory methods from <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#"><code>RulesetsFactoryUtils</code></a> instead
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#getRulesetFactory(net.sourceforge.pmd.PMDConfiguration,net.sourceforge.pmd.util.ResourceLoader)"><code>getRulesetFactory</code></a>

* <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNode.html#"><code>AbstractApexNode</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNodeBase.html#"><code>AbstractApexNodeBase</code></a>, and the related `visit`
  methods on <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ApexParserVisitor.html#"><code>ApexParserVisitor</code></a> and its implementations.
  Use <a href="https://docs.pmd-code.org/apidocs/pmd-apex/7.0.0/net/sourceforge/pmd/lang/apex/ast/ApexNode.html#"><code>ApexNode</code></a>  instead, now considers comments too.

**For removal**

* pmd-core
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/dfa/DFAGraphRule.html#"><code>DFAGraphRule</code></a> and its implementations
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/dfa/DFAGraphMethod.html#"><code>DFAGraphMethod</code></a>
    * Many methods on the <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a> interface
      and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/AbstractNode.html#"><code>AbstractNode</code></a> base class. See their javadoc for details.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/Node.html#isFindBoundary()"><code>Node#isFindBoundary</code></a> is deprecated for XPath queries.
    * Many APIs of `net.sourceforge.pmd.lang.metrics`, though most of them were internal and
      probably not used directly outside of PMD. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/metrics/MetricsUtil.html#"><code>MetricsUtil</code></a> as
      a replacement for the language-specific façades too.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/QualifiableNode.html#"><code>QualifiableNode</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/QualifiedName.html#"><code>QualifiedName</code></a>
* pmd-java
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/AbstractJavaParser.html#"><code>AbstractJavaParser</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/AbstractJavaHandler.html#"><code>AbstractJavaHandler</code></a>
    * [`ASTAnyTypeDeclaration.TypeKind`](https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getTypeKind()"><code>ASTAnyTypeDeclaration#getTypeKind</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiedName.html#"><code>JavaQualifiedName</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTCatchStatement.html#getBlock()"><code>ASTCatchStatement#getBlock</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#declarationsAreInDefaultPackage()"><code>ASTCompilationUnit#declarationsAreInDefaultPackage</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiableNode.html#"><code>JavaQualifiableNode</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getQualifiedName()"><code>ASTAnyTypeDeclaration#getQualifiedName</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#getQualifiedName()"><code>ASTMethodOrConstructorDeclaration#getQualifiedName</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#getQualifiedName()"><code>ASTLambdaExpression#getQualifiedName</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/qname/package-summary.html#"><code>net.sourceforge.pmd.lang.java.qname</code></a> and its contents
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/MethodLikeNode.html#"><code>MethodLikeNode</code></a>
        * Its methods will also be removed from its implementations,
          <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#"><code>ASTMethodOrConstructorDeclaration</code></a>,
          <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#"><code>ASTLambdaExpression</code></a>.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getImage()"><code>ASTAnyTypeDeclaration#getImage</code></a> will be removed. Please use `getSimpleName()`
      instead. This affects <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnnotationTypeDeclaration.html#getImage()"><code>ASTAnnotationTypeDeclaration#getImage</code></a>,
      <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#getImage()"><code>ASTClassOrInterfaceDeclaration#getImage</code></a>, and
      <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTEnumDeclaration.html#getImage()"><code>ASTEnumDeclaration#getImage</code></a>.
    * Several methods of <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTTryStatement.html#"><code>ASTTryStatement</code></a>, replacements with other names
      have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
    * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
      following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTYieldStatement.html#"><code>ASTYieldStatement</code></a> will not implement <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#"><code>TypeNode</code></a>
      anymore come 7.0.0. Test the type of the expression nested within it.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.html#"><code>JavaMetrics</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/metrics/JavaMetricsComputer.html#"><code>JavaMetricsComputer</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTArguments.html#getArgumentCount()"><code>ASTArguments#getArgumentCount</code></a>.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTArguments.html#size()"><code>size</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTFormalParameters.html#getParameterCount()"><code>ASTFormalParameters#getParameterCount</code></a>.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTFormalParameters.html#size()"><code>size</code></a> instead.
* pmd-apex
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/metrics/ApexMetrics.html#"><code>ApexMetrics</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/metrics/ApexMetricsComputer.html#"><code>ApexMetricsComputer</code></a>

**In ASTs (JSP)**

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the JSP AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/annotation/InternalApi.html#"><code>InternalApi</code></a>. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
*   In the meantime you should use interfaces like <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/7.0.0/net/sourceforge/pmd/lang/jsp/ast/JspNode.html#"><code>JspNode</code></a> or
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a>, or the other published interfaces in this package,
    to refer to nodes generically.
*   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The class <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.55.0/net/sourceforge/pmd/lang/jsp/JspParser.html#"><code>JspParser</code></a> is deprecated and should not be used directly.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getParser()"><code>LanguageVersionHandler#getParser</code></a> instead.

Please look at <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.55.0/net/sourceforge/pmd/lang/jsp/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.jsp.ast</code></a> to find out the full list of deprecations.

**In ASTs (Velocity)**

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the VM AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/annotation/InternalApi.html#"><code>InternalApi</code></a>. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
*   In the meantime you should use interfaces like <a href="https://docs.pmd-code.org/apidocs/pmd-velocity/7.0.0/net/sourceforge/pmd/lang/velocity/ast/VtlNode.html#"><code>VtlNode</code></a> or
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a>, or the other published interfaces in this package,
    to refer to nodes generically.
*   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The package <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.55.0/net/sourceforge/pmd/lang/vm/directive/package-summary.html#"><code>net.sourceforge.pmd.lang.vm.directive</code></a> as well as the classes
    <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.55.0/net/sourceforge/pmd/lang/vm/util/DirectiveMapper.html#"><code>DirectiveMapper</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.55.0/net/sourceforge/pmd/lang/vm/util/LogUtil.html#"><code>LogUtil</code></a> are deprecated
    for removal. They were only used internally during parsing.
*   The class <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.55.0/net/sourceforge/pmd/lang/vm/VmParser.html#"><code>VmParser</code></a> is deprecated and should not be used directly.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getParser()"><code>LanguageVersionHandler#getParser</code></a> instead.

Please look at <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.55.0/net/sourceforge/pmd/lang/vm/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.vm.ast</code></a> to find out the full list of deprecations.

**PLSQL AST**

The production and node <a href="https://javadoc.io/doc/net.sourceforge.pmd/pmd-plsql/6.21.0/net/sourceforge/pmd/lang/plsql/ast/ASTCursorBody.html"><code>ASTCursorBody</code></a> was unnecessary, not used and has been removed. Cursors have been already
parsed as <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/7.0.0/net/sourceforge/pmd/lang/plsql/ast/ASTCursorSpecification.html#"><code>ASTCursorSpecification</code></a>.

#### 6.21.0

**Deprecated APIs**

**Internal API**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/JavaLanguageHandler.html#"><code>JavaLanguageHandler</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/JavaLanguageParser.html#"><code>JavaLanguageParser</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/JavaDataFlowHandler.html#"><code>JavaDataFlowHandler</code></a>
* Implementations of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#"><code>RuleViolationFactory</code></a> in each
  language module, eg <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolationFactory.html#"><code>JavaRuleViolationFactory</code></a>.
  See javadoc of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#"><code>RuleViolationFactory</code></a>.
* Implementations of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleViolation.html#"><code>RuleViolation</code></a> in each language module,
  eg <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolation.html#"><code>JavaRuleViolation</code></a>. See javadoc of
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleViolation.html#"><code>RuleViolation</code></a>.

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/rules/RuleFactory.html#"><code>RuleFactory</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/rules/RuleBuilder.html#"><code>RuleBuilder</code></a>
* Constructors of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSetFactory.html#"><code>RuleSetFactory</code></a>, use factory methods from <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#"><code>RulesetsFactoryUtils</code></a> instead
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#getRulesetFactory(net.sourceforge.pmd.PMDConfiguration,net.sourceforge.pmd.util.ResourceLoader)"><code>getRulesetFactory</code></a>

* <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNode.html#"><code>AbstractApexNode</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNodeBase.html#"><code>AbstractApexNodeBase</code></a>, and the related `visit`
  methods on <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ApexParserVisitor.html#"><code>ApexParserVisitor</code></a> and its implementations.
  Use <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/ApexNode.html#"><code>ApexNode</code></a> instead, now considers comments too.

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/CharStream.html#"><code>CharStream</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/JavaCharStream.html#"><code>JavaCharStream</code></a>,
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/SimpleCharStream.html#"><code>SimpleCharStream</code></a>: these are APIs used by our JavaCC
  implementations and that will be moved/refactored for PMD 7.0.0. They should not
  be used, extended or implemented directly.
* All classes generated by JavaCC, eg <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JJTJavaParserState.html#"><code>JJTJavaParserState</code></a>.
  This includes token classes, which will be replaced with a single implementation, and
  subclasses of <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/ParseException.html#"><code>ParseException</code></a>, whose usages will be replaced
  by just that superclass.

**For removal**

* pmd-core
    * Many methods on the <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a> interface
      and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/AbstractNode.html#"><code>AbstractNode</code></a> base class. See their javadoc for details.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/Node.html#isFindBoundary()"><code>Node#isFindBoundary</code></a> is deprecated for XPath queries.
* pmd-java
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/AbstractJavaParser.html#"><code>AbstractJavaParser</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/AbstractJavaHandler.html#"><code>AbstractJavaHandler</code></a>
    * [`ASTAnyTypeDeclaration.TypeKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getTypeKind()"><code>ASTAnyTypeDeclaration#getTypeKind</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiedName.html#"><code>JavaQualifiedName</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTCatchStatement.html#getBlock()"><code>ASTCatchStatement#getBlock</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#declarationsAreInDefaultPackage()"><code>ASTCompilationUnit#declarationsAreInDefaultPackage</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiableNode.html#"><code>JavaQualifiableNode</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getQualifiedName()"><code>ASTAnyTypeDeclaration#getQualifiedName</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#getQualifiedName()"><code>ASTMethodOrConstructorDeclaration#getQualifiedName</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#getQualifiedName()"><code>ASTLambdaExpression#getQualifiedName</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/qname/package-summary.html#"><code>net.sourceforge.pmd.lang.java.qname</code></a> and its contents
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/MethodLikeNode.html#"><code>MethodLikeNode</code></a>
        * Its methods will also be removed from its implementations,
          <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#"><code>ASTMethodOrConstructorDeclaration</code></a>,
          <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#"><code>ASTLambdaExpression</code></a>.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getImage()"><code>ASTAnyTypeDeclaration#getImage</code></a> will be removed. Please use `getSimpleName()`
      instead. This affects <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTAnnotationTypeDeclaration.html#getImage()"><code>ASTAnnotationTypeDeclaration#getImage</code></a>,
      <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#getImage()"><code>ASTClassOrInterfaceDeclaration#getImage</code></a>, and
      <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTEnumDeclaration.html#getImage()"><code>ASTEnumDeclaration#getImage</code></a>.
    * Several methods of <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTTryStatement.html#"><code>ASTTryStatement</code></a>, replacements with other names
      have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
    * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
      following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTYieldStatement.html#"><code>ASTYieldStatement</code></a> will not implement <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#"><code>TypeNode</code></a>
      anymore come 7.0.0. Test the type of the expression nested within it.

#### 6.20.0

No changes.

#### 6.19.0

**Deprecated APIs**

**For removal**

* pmd-core
    * All the package <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/dcd/package-summary.html#"><code>net.sourceforge.pmd.dcd</code></a> and its subpackages. See <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/dcd/DCD.html#"><code>DCD</code></a>.
    * In <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageRegistry.html#"><code>LanguageRegistry</code></a>:
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageRegistry.html#commaSeparatedTerseNamesForLanguageVersion(java.util.List)"><code>commaSeparatedTerseNamesForLanguageVersion</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageRegistry.html#commaSeparatedTerseNamesForLanguage(java.util.List)"><code>commaSeparatedTerseNamesForLanguage</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageRegistry.html#findAllVersions()"><code>findAllVersions</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageRegistry.html#findLanguageVersionByTerseName(java.lang.String)"><code>findLanguageVersionByTerseName</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageRegistry.html#getInstance()"><code>getInstance</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSet.html#getExcludePatterns()"><code>RuleSet#getExcludePatterns</code></a>. Use the new method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSet.html#getFileExclusions()"><code>getFileExclusions</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSet.html#getIncludePatterns()"><code>RuleSet#getIncludePatterns</code></a>. Use the new method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSet.html#getFileInclusions()"><code>getFileInclusions</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/Parser.html#canParse()"><code>Parser#canParse</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/Parser.html#getSuppressMap()"><code>Parser#getSuppressMap</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/rules/RuleBuilder.html#<init>(java.lang.String,java.lang.String,java.lang.String)"><code>RuleBuilder#RuleBuilder</code></a>. Use the new constructor with the correct ResourceLoader instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/rules/RuleFactory.html#<init>()"><code>RuleFactory#RuleFactory</code></a>. Use the new constructor with the correct ResourceLoader instead.
* pmd-java
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/CanSuppressWarnings.html#"><code>CanSuppressWarnings</code></a> and its implementations
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/AbstractJavaRule.html#isSuppressed(net.sourceforge.pmd.lang.ast.Node)"><code>isSuppressed</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/AbstractJavaRule.html#getDeclaringType(net.sourceforge.pmd.lang.ast.Node)"><code>getDeclaringType</code></a>.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolation.html#isSupressed(net.sourceforge.pmd.lang.ast.Node,net.sourceforge.pmd.Rule)"><code>isSupressed</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclarator.html#"><code>ASTMethodDeclarator</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#getMethodName()"><code>getMethodName</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#getBlock()"><code>getBlock</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTConstructorDeclaration.html#getParameterCount()"><code>getParameterCount</code></a>
* pmd-apex
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/CanSuppressWarnings.html#"><code>CanSuppressWarnings</code></a> and its implementations
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/rule/ApexRuleViolation.html#isSupressed(net.sourceforge.pmd.lang.ast.Node,net.sourceforge.pmd.Rule)"><code>isSupressed</code></a>

**Internal APIs**

* pmd-core
    * All the package <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/util/package-summary.html#"><code>net.sourceforge.pmd.util</code></a> and its subpackages,
      except <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/util/datasource/package-summary.html#"><code>net.sourceforge.pmd.util.datasource</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/util/database/package-summary.html#"><code>net.sourceforge.pmd.util.database</code></a>.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/GridBagHelper.html#"><code>GridBagHelper</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/renderers/ColumnDescriptor.html#"><code>ColumnDescriptor</code></a>


#### 6.18.0

**Changes to Renderer**

*   Each renderer has now a new method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/renderers/Renderer.html#setUseShortNames(java.util.List)"><code>Renderer#setUseShortNames</code></a> which
    is used for implementing the "shortnames" CLI option. The method is automatically called by PMD, if this
    CLI option is in use. When rendering filenames to the report, the new helper method
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/renderers/AbstractRenderer.html#determineFileName(java.lang.String)"><code>AbstractRenderer#determineFileName</code></a> should be used. This will change
    the filename to a short name, if the CLI option "shortnames" is used.

    Not adjusting custom renderers will make them render always the full file names and not honoring the
    CLI option "shortnames".

**Deprecated APIs**

**For removal**

*   The methods <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTImportDeclaration.html#getImportedNameNode()"><code>ASTImportDeclaration#getImportedNameNode</code></a> and
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTImportDeclaration.html#getPackage()"><code>ASTImportDeclaration#getPackage</code></a> have been deprecated and
    will be removed with PMD 7.0.0.
*   The method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleContext.html#setSourceCodeFilename(java.lang.String)"><code>RuleContext#setSourceCodeFilename</code></a> has been deprecated
    and will be removed. The already existing method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleContext.html#setSourceCodeFile(java.io.File)"><code>RuleContext#setSourceCodeFile</code></a>
    should be used instead. The method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleContext.html#getSourceCodeFilename()"><code>RuleContext#getSourceCodeFilename</code></a> still
    exists and returns just the filename without the full path.
*   The method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/processor/AbstractPMDProcessor.html#filenameFrom(net.sourceforge.pmd.util.datasource.DataSource)"><code>AbstractPMDProcessor#filenameFrom</code></a> has been
    deprecated. It was used to determine a "short name" of the file being analyzed, so that the report
    can use short names. However, this logic has been moved to the renderers.
*   The methods <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#metrics()"><code>Report#metrics</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#hasMetrics()"><code>Report#hasMetrics</code></a> have
    been deprecated. They were leftovers from a previous deprecation round targeting
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/stat/StatisticalRule.html#"><code>StatisticalRule</code></a>.

**Internal APIs**

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0. You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* pmd-core
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cache/package-summary.html#"><code>net.sourceforge.pmd.cache</code></a>
* pmd-java
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/typeresolution/package-summary.html#"><code>net.sourceforge.pmd.lang.java.typeresolution</code></a>: Everything, including
      subpackages, except <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/typeresolution/TypeHelper.html#"><code>TypeHelper</code></a> and
      <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/typeresolution/typedefinition/JavaTypeDefinition.html#"><code>JavaTypeDefinition</code></a>.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#getClassTypeResolver()"><code>ASTCompilationUnit#getClassTypeResolver</code></a>


#### 6.17.0

No changes.

#### 6.16.0

**Deprecated APIs**

> Reminder: Please don't use members marked with the annotation <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/annotation/InternalApi.html#"><code>InternalApi</code></a>, as they will
> likely be removed, hidden, or otherwise intentionally broken with 7.0.0.


**In ASTs**

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the Java AST** (with other languages to come):

* Manual instantiation of nodes. **Constructors of node classes are deprecated** and marked
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/annotation/InternalApi.html#"><code>InternalApi</code></a>. Nodes should only be obtained from the parser, which for rules, means
  that never need to instantiate node themselves. Those constructors will be made package private with 7.0.0.
* **Subclassing of abstract node classes, or usage of their type**. Version 7.0.0 will bring a new set of abstractions
  that will be public API, but the base classes are and will stay internal. You should not couple your code to them.
    * In the meantime you should use interfaces like <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/JavaNode.html#"><code>JavaNode</code></a> or
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a>, or the other published interfaces in this package, to refer to nodes generically.
    * Concrete node classes will **be made final** with 7.0.0.
* Setters found in any node class or interface. **Rules should consider the AST immutable**. We will make those
  setters package private with 7.0.0.

Please look at <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.java.ast</code></a> to find out the full list
of deprecations.


#### 6.15.0

**Deprecated APIs**

**For removal**

*   The `DumpFacades` in all languages, that could be used to transform a AST into a textual representation,
    will be removed with PMD 7. The rule designer is a better way to inspect nodes.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/ast/DumpFacade.html#"><code>net.sourceforge.pmd.lang.apex.ast.DumpFacade</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/DumpFacade.html#"><code>net.sourceforge.pmd.lang.java.ast.DumpFacade</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.55.0/net/sourceforge/pmd/lang/ecmascript/ast/DumpFacade.html#"><code>net.sourceforge.pmd.lang.ecmascript.ast.DumpFacade</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.55.0/net/sourceforge/pmd/lang/jsp/ast/DumpFacade.html#"><code>net.sourceforge.pmd.lang.jsp.ast.DumpFacade</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.55.0/net/sourceforge/pmd/lang/plsql/ast/DumpFacade.html#"><code>net.sourceforge.pmd.lang.plsql.ast.DumpFacade</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.55.0/net/sourceforge/pmd/lang/vf/ast/DumpFacade.html#"><code>net.sourceforge.pmd.lang.vf.ast.DumpFacade</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.55.0/net/sourceforge/pmd/lang/vm/ast/AbstractVmNode.html#dump(java.lang.String,boolean,java.io.Writer)"><code>net.sourceforge.pmd.lang.vm.ast.AbstractVmNode#dump</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.55.0/net/sourceforge/pmd/lang/xml/ast/DumpFacade.html#"><code>net.sourceforge.pmd.lang.xml.ast.DumpFacade</code></a>
*   The method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDumpFacade(java.io.Writer,java.lang.String,boolean)"><code>LanguageVersionHandler#getDumpFacade</code></a> will be
    removed as well. It is deprecated, along with all its implementations in the subclasses of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#"><code>LanguageVersionHandler</code></a>.

#### 6.14.0

No changes.

#### 6.13.0

**Command Line Interface**

The start scripts `run.sh`, `pmd.bat` and `cpd.bat` support the new environment variable `PMD_JAVA_OPTS`.
This can be used to set arbitrary JVM options for running PMD, such as memory settings (e.g. `PMD_JAVA_OPTS=-Xmx512m`)
or enable preview language features (e.g. `PMD_JAVA_OPTS=--enable-preview`).

The previously available variables such as `OPTS` or `HEAPSIZE` are deprecated and will be removed with PMD 7.0.0.

**Deprecated API**

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/renderers/CodeClimateRule.html#"><code>CodeClimateRule</code></a> is deprecated in 7.0.0 because it was unused for 2 years and
    created an unwanted dependency.
    Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" will also be removed.
    See [#1702](https://github.com/pmd/pmd/pull/1702) for more.

*   The Apex ruleset `rulesets/apex/ruleset.xml` has been deprecated and will be removed in 7.0.0. Please use the new
    quickstart ruleset `rulesets/apex/quickstart.xml` instead.

#### 6.12.0

No changes.

#### 6.11.0

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/stat/StatisticalRule.html#"><code>StatisticalRule</code></a> and the related helper classes and base rule classes
  are deprecated for removal in 7.0.0. This includes all of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/stat/package-summary.html#"><code>net.sourceforge.pmd.stat</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/stat/package-summary.html#"><code>net.sourceforge.pmd.lang.rule.stat</code></a>,
  and also <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/rule/AbstractStatisticalJavaRule.html#"><code>AbstractStatisticalJavaRule</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.55.0/net/sourceforge/pmd/lang/apex/rule/AbstractStatisticalApexRule.html#"><code>AbstractStatisticalApexRule</code></a> and the like.
  The methods <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Report.html#addMetric(net.sourceforge.pmd.stat.Metric)"><code>Report#addMetric</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/ThreadSafeReportListener.html#metricAdded(net.sourceforge.pmd.stat.Metric)"><code>metricAdded</code></a>
  will also be removed.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertySource.html#setProperty(net.sourceforge.pmd.properties.MultiValuePropertyDescriptor,V...)"><code>setProperty</code></a> is deprecated,
  because <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/MultiValuePropertyDescriptor.html#"><code>MultiValuePropertyDescriptor</code></a> is deprecated as well.

#### 6.10.0

**Properties framework**





The properties framework is about to get a lifting, and for that reason, we need to deprecate a lot of APIs
to remove them in 7.0.0. The proposed changes to the API are described [on the wiki](https://github.com/pmd/pmd/wiki/Property-framework-7-0-0)

**Changes to how you define properties**

* Construction of property descriptors has been possible through builders since 6.0.0. The 7.0.0 API will only allow
  construction through builders. The builder hierarchy, currently found in the package <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/builders/package-summary.html#"><code>net.sourceforge.pmd.properties.builders</code></a>,
  is being replaced by the simpler <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyBuilder.html#"><code>PropertyBuilder</code></a>. Their APIs enjoy a high degree of source compatibility.

* Concrete property classes like <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/IntegerProperty.html#"><code>IntegerProperty</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/StringMultiProperty.html#"><code>StringMultiProperty</code></a> will gradually
  all be deprecated until 7.0.0. Their usages should be replaced by direct usage of the <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#"><code>PropertyDescriptor</code></a>
  interface, e.g. `PropertyDescriptor<Integer>` or `PropertyDescriptor<List<String>>`.

* Instead of spreading properties across countless classes, the utility class <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#"><code>PropertyFactory</code></a> will become
  from 7.0.0 on the only provider for property descriptor builders. Each current property type will be replaced
  by a corresponding method on `PropertyFactory`:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/IntegerProperty.html#"><code>IntegerProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#intProperty(java.lang.String)"><code>PropertyFactory#intProperty</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/IntegerMultiProperty.html#"><code>IntegerMultiProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#intListProperty(java.lang.String)"><code>PropertyFactory#intListProperty</code></a>

    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/FloatProperty.html#"><code>FloatProperty</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/DoubleProperty.html#"><code>DoubleProperty</code></a> are both replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#doubleProperty(java.lang.String)"><code>PropertyFactory#doubleProperty</code></a>.
      Having a separate property for floats wasn't that useful.
        * Similarly, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/FloatMultiProperty.html#"><code>FloatMultiProperty</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/DoubleMultiProperty.html#"><code>DoubleMultiProperty</code></a> are replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#doubleListProperty(java.lang.String)"><code>PropertyFactory#doubleListProperty</code></a>.

    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/StringProperty.html#"><code>StringProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#stringProperty(java.lang.String)"><code>PropertyFactory#stringProperty</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/StringMultiProperty.html#"><code>StringMultiProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#stringListProperty(java.lang.String)"><code>PropertyFactory#stringListProperty</code></a>

    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/RegexProperty.html#"><code>RegexProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#regexProperty(java.lang.String)"><code>PropertyFactory#regexProperty</code></a>

    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/EnumeratedProperty.html#"><code>EnumeratedProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumProperty(java.lang.String,java.util.Map)"><code>PropertyFactory#enumProperty</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/EnumeratedProperty.html#"><code>EnumeratedProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumListProperty(java.lang.String,java.util.Map)"><code>PropertyFactory#enumListProperty</code></a>

    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/BooleanProperty.html#"><code>BooleanProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#booleanProperty(java.lang.String)"><code>PropertyFactory#booleanProperty</code></a>
        * Its multi-valued counterpart, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/BooleanMultiProperty.html#"><code>BooleanMultiProperty</code></a>, is not replaced, because it doesn't have a use case.

    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/CharacterProperty.html#"><code>CharacterProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#charProperty(java.lang.String)"><code>PropertyFactory#charProperty</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/CharacterMultiProperty.html#"><code>CharacterMultiProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#charListProperty(java.lang.String)"><code>PropertyFactory#charListProperty</code></a>

    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/LongProperty.html#"><code>LongProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#longIntProperty(java.lang.String)"><code>PropertyFactory#longIntProperty</code></a>
        * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/LongMultiProperty.html#"><code>LongMultiProperty</code></a> is replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyFactory.html#longIntListProperty(java.lang.String)"><code>PropertyFactory#longIntListProperty</code></a>

    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/MethodProperty.html#"><code>MethodProperty</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/FileProperty.html#"><code>FileProperty</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/TypeProperty.html#"><code>TypeProperty</code></a> and their multi-valued counterparts
      are discontinued for lack of a use-case, and have no planned replacement in 7.0.0 for now.


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

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/EnumeratedPropertyDescriptor.html#"><code>EnumeratedPropertyDescriptor</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/NumericPropertyDescriptor.html#"><code>NumericPropertyDescriptor</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PackagedPropertyDescriptor.html#"><code>PackagedPropertyDescriptor</code></a>,
  and the related builders (in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/builders/package-summary.html#"><code>net.sourceforge.pmd.properties.builders</code></a>) will be removed.
  These specialized interfaces allowed additional constraints to be enforced on the
  value of a property, but made the property class hierarchy very large and impractical
  to maintain. Their functionality will be mapped uniformly to <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/properties/PropertyConstraint.html#"><code>PropertyConstraint</code></a>s,
  which will allow virtually any constraint to be defined, and improve documentation and error reporting. The
  related methods <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyTypeId.html#isPropertyNumeric()"><code>PropertyTypeId#isPropertyNumeric</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyTypeId.html#isPropertyPackaged()"><code>PropertyTypeId#isPropertyPackaged</code></a> are also deprecated.

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/MultiValuePropertyDescriptor.html#"><code>MultiValuePropertyDescriptor</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/SingleValuePropertyDescriptor.html#"><code>SingleValuePropertyDescriptor</code></a>
  are deprecated. 7.0.0 will introduce a new XML syntax which will remove the need for such a divide
  between single- and multi-valued properties. The method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#isMultiValue()"><code>PropertyDescriptor#isMultiValue</code></a> will be removed
  accordingly.

**Changes to the PropertyDescriptor interface**

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#preferredRowCount()"><code>preferredRowCount</code></a> is deprecated with no intended replacement. It was never implemented, and does not belong
  in this interface. The methods <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#uiOrder()"><code>uiOrder</code></a> and `compareTo(PropertyDescriptor)` are deprecated for the
  same reason. These methods mix presentation logic with business logic and are not necessary for PropertyDescriptors to work.
  `PropertyDescriptor` will not extend `Comparable<PropertyDescriptor>` anymore come 7.0.0.
* The method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#propertyErrorFor(net.sourceforge.pmd.Rule)"><code>propertyErrorFor</code></a> is deprecated and will be removed with no intended
  replacement. It's really just a shortcut for `prop.errorFor(rule.getProperty(prop))`.
* `T `<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#valueFrom(java.lang.String)"><code>valueFrom(String)</code></a> and `String `<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#asDelimitedString(java.lang.Object)"><code>asDelimitedString</code></a>`(T)` are deprecated and will be removed. These were
  used to serialize and deserialize properties to/from a string, but 7.0.0 will introduce a more flexible
  XML syntax which will make them obsolete.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#isMultiValue()"><code>isMultiValue</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#type()"><code>type</code></a> are deprecated and won't be replaced. The new XML syntax will remove the need
  for a divide between multi- and single-value properties, and will allow arbitrary types to be represented.
  Since arbitrary types may be represented, `type` will become obsolete as it can't represent generic types,
  which will nevertheless be representable with the XML syntax. It was only used for documentation, but a
  new way to document these properties exhaustively will be added with 7.0.0.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#errorFor(java.lang.Object)"><code>errorFor</code></a> is deprecated as its return type will be changed to `Optional<String>` with the shift to Java 8.

**Deprecated APIs**








**For internalization**

*   The implementation of the adapters for the XPath engines Saxon and Jaxen (package <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/xpath/package-summary.html#"><code>net.sourceforge.pmd.lang.ast.xpath</code></a>)
    are now deprecated. They'll be moved to an internal package come 7.0.0. Only <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/rule/xpath/Attribute.html#"><code>Attribute</code></a> remains public API.

*   The classes <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptorField.html#"><code>PropertyDescriptorField</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/builders/PropertyDescriptorBuilderConversionWrapper.html#"><code>PropertyDescriptorBuilderConversionWrapper</code></a>, and the methods
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#attributeValuesById()"><code>PropertyDescriptor#attributeValuesById</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#isDefinedExternally()"><code>PropertyDescriptor#isDefinedExternally</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertyTypeId.html#getFactory()"><code>PropertyTypeId#getFactory</code></a>.
    These were used to read and write properties to and from XML, but were not intended as public API.

*   The class <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/ValueParserConstants.html#"><code>ValueParserConstants</code></a> and the interface <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/ValueParser.html#"><code>ValueParser</code></a>.

*   All classes from <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/metrics/impl/visitors/package-summary.html#"><code>net.sourceforge.pmd.lang.java.metrics.impl.visitors</code></a> are now considered internal API. They're deprecated
    and will be moved into an internal package with 7.0.0. To implement your own metrics visitors,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaParserVisitorAdapter.html#"><code>JavaParserVisitorAdapter</code></a> should be directly subclassed.

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDataFlowHandler()"><code>LanguageVersionHandler#getDataFlowHandler()</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDFAGraphRule()"><code>LanguageVersionHandler#getDFAGraphRule()</code></a>

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/VisitorStarter.html#"><code>VisitorStarter</code></a>

**For removal**

*   All classes from <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/modules/package-summary.html#"><code>net.sourceforge.pmd.properties.modules</code></a> will be removed.

*   The interface <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/Dimensionable.html#"><code>Dimensionable</code></a> has been deprecated.
    It gets in the way of a grammar change for 7.0.0 and won't be needed anymore (see [#997](https://github.com/pmd/pmd/issues/997)).

*   Several methods from <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTLocalVariableDeclaration.html#"><code>ASTLocalVariableDeclaration</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#"><code>ASTFieldDeclaration</code></a> have
    also been deprecated:

*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#"><code>ASTFieldDeclaration</code></a> won't be a <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#"><code>TypeNode</code></a> come 7.0.0, so
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#getType()"><code>getType</code></a> and
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#getTypeDefinition()"><code>getTypeDefinition</code></a> are deprecated.

*   The method `getVariableName` on those two nodes will be removed, too.

*   All these are deprecated because those nodes may declare several variables at once, possibly
    with different types (and obviously with different names). They both implement `Iterator<`<a href="https://docs.pmd-code.org/apidocs/pmd-java/7.0.0/net/sourceforge/pmd/lang/java/ast/ASTVariableId.html#"><code>ASTVariableId</code></a>`>`
    though, so you should iterate on each declared variable. See [#910](https://github.com/pmd/pmd/issues/910).

*   Visitor decorators are now deprecated and will be removed in PMD 7.0.0. They were originally a way to write
    composable visitors, used in the metrics framework, but they didn't prove cost-effective.

*   In <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.java.ast</code></a>: <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaParserDecoratedVisitor.html#"><code>JavaParserDecoratedVisitor</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaParserControllessVisitor.html#"><code>JavaParserControllessVisitor</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaParserControllessVisitorAdapter.html#"><code>JavaParserControllessVisitorAdapter</code></a>, and <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/JavaParserVisitorDecorator.html#"><code>JavaParserVisitorDecorator</code></a> are deprecated with no intended replacement.


*   The LanguageModules of several languages, that only support CPD execution, have been deprecated. These languages
    are not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
    not affected by this change. The following classes have been deprecated and will be removed with PMD 7.0.0:

*   <a href="https://docs.pmd-code.org/apidocs/pmd-cpp/6.55.0/net/sourceforge/pmd/lang/cpp/CppHandler.html#"><code>CppHandler</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-cpp/6.55.0/net/sourceforge/pmd/lang/cpp/CppLanguageModule.html#"><code>CppLanguageModule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-cpp/6.55.0/net/sourceforge/pmd/lang/cpp/CppParser.html#"><code>CppParser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-cs/6.55.0/net/sourceforge/pmd/lang/cs/CsLanguageModule.html#"><code>CsLanguageModule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-fortran/6.55.0/net/sourceforge/pmd/lang/fortran/FortranLanguageModule.html#"><code>FortranLanguageModule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-groovy/6.55.0/net/sourceforge/pmd/lang/groovy/GroovyLanguageModule.html#"><code>GroovyLanguageModule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-matlab/6.55.0/net/sourceforge/pmd/lang/matlab/MatlabHandler.html#"><code>MatlabHandler</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-matlab/6.55.0/net/sourceforge/pmd/lang/matlab/MatlabLanguageModule.html#"><code>MatlabLanguageModule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-matlab/6.55.0/net/sourceforge/pmd/lang/matlab/MatlabParser.html#"><code>MatlabParser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-objectivec/6.55.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCHandler.html#"><code>ObjectiveCHandler</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-objectivec/6.55.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCLanguageModule.html#"><code>ObjectiveCLanguageModule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-objectivec/6.55.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCParser.html#"><code>ObjectiveCParser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-php/6.55.0/net/sourceforge/pmd/lang/php/PhpLanguageModule.html#"><code>PhpLanguageModule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-python/6.55.0/net/sourceforge/pmd/lang/python/PythonHandler.html#"><code>PythonHandler</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-python/6.55.0/net/sourceforge/pmd/lang/python/PythonLanguageModule.html#"><code>PythonLanguageModule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-python/6.55.0/net/sourceforge/pmd/lang/python/PythonParser.html#"><code>PythonParser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-ruby/6.55.0/net/sourceforge/pmd/lang/ruby/RubyLanguageModule.html#"><code>RubyLanguageModule</code></a>


* Optional AST processing stages like symbol table, type resolution or data-flow analysis will be reified
  in 7.0.0 to factorise common logic and make them extensible. Further explanations about this change can be
  found on [#1426](https://github.com/pmd/pmd/pull/1426). Consequently, the following APIs are deprecated for
  removal:
    * In <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Rule.html#"><code>Rule</code></a>: <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Rule.html#isDfa()"><code>isDfa()</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Rule.html#isTypeResolution()"><code>isTypeResolution()</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/Rule.html#isMultifile()"><code>isMultifile()</code></a> and their
      respective setters.
    * In <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSet.html#"><code>RuleSet</code></a>: <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSet.html#usesDFA(net.sourceforge.pmd.lang.Language)"><code>usesDFA(Language)</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSet.html#usesTypeResolution(net.sourceforge.pmd.lang.Language)"><code>usesTypeResolution(Language)</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSet.html#usesMultifile(net.sourceforge.pmd.lang.Language)"><code>usesMultifile(Language)</code></a>
    * In <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSets.html#"><code>RuleSets</code></a>: <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSets.html#usesDFA(net.sourceforge.pmd.lang.Language)"><code>usesDFA(Language)</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSets.html#usesTypeResolution(net.sourceforge.pmd.lang.Language)"><code>usesTypeResolution(Language)</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/RuleSets.html#usesMultifile(net.sourceforge.pmd.lang.Language)"><code>usesMultifile(Language)</code></a>
    * In <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#"><code>LanguageVersionHandler</code></a>: <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDataFlowFacade()"><code>getDataFlowFacade()</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getSymbolFacade()"><code>getSymbolFacade()</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getSymbolFacade(java.lang.ClassLoader)"><code>getSymbolFacade(ClassLoader)</code></a>,
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getTypeResolutionFacade(java.lang.ClassLoader)"><code>getTypeResolutionFacade(ClassLoader)</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getQualifiedNameResolutionFacade(java.lang.ClassLoader)"><code>getQualifiedNameResolutionFacade(ClassLoader)</code></a>

#### 6.9.0

No changes.

#### 6.8.0

*   A couple of methods and fields in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/AbstractPropertySource.html#"><code>net.sourceforge.pmd.properties.AbstractPropertySource</code></a> have been
    deprecated, as they are replaced by already existing functionality or expose internal implementation
    details: `propertyDescriptors`, `propertyValuesByDescriptor`,
    `copyPropertyDescriptors()`, `copyPropertyValues()`, `ignoredProperties()`, `usesDefaultValues()`,
    `useDefaultValueFor()`.

*   Some methods in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/properties/PropertySource.html#"><code>net.sourceforge.pmd.properties.PropertySource</code></a> have been deprecated as well:
    `usesDefaultValues()`, `useDefaultValueFor()`, `ignoredProperties()`.

*   The class <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/AbstractDelegateRule.html#"><code>net.sourceforge.pmd.lang.rule.AbstractDelegateRule</code></a> has been deprecated and will
    be removed with PMD 7.0.0. It is internally only in use by RuleReference.

*   The default constructor of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/rule/RuleReference.html#"><code>net.sourceforge.pmd.lang.rule.RuleReference</code></a> has been deprecated
    and will be removed with PMD 7.0.0. RuleReferences should only be created by providing a Rule and
    a RuleSetReference. Furthermore, the following methods are deprecated: `setRuleReference()`,
    `hasOverriddenProperty()`, `usesDefaultValues()`, `useDefaultValueFor()`.

#### 6.7.0

*   All classes in the package <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/dfa/report/package-summary.html#"><code>net.sourceforge.pmd.lang.dfa.report</code></a> have been deprecated and will be removed
    with PMD 7.0.0. This includes the class <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/dfa/report/ReportTree.html#"><code>net.sourceforge.pmd.lang.dfa.report.ReportTree</code></a>. The reason is,
    that this class is very specific to Java and not suitable for other languages. It has only been used for
    `YAHTMLRenderer`, which has been rewritten to work without these classes.

*   The nodes RUNSIGNEDSHIFT and RSIGNEDSHIFT are deprecated and will be removed from the AST with PMD 7.0.0.
    These represented the operator of ShiftExpression in two cases out of three, but they're not needed and
    make ShiftExpression inconsistent. The operator of a ShiftExpression is now accessible through
    ShiftExpression#getOperator.

#### 6.5.0

*   The utility class <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/CommentUtil.html#"><code>CommentUtil</code></a> has been deprecated and will be removed
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

* The following classes in package <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/benchmark/package-summary.html#"><code>net.sourceforge.pmd.benchmark</code></a> have been deprecated: <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/benchmark/Benchmark.html#"><code>Benchmark</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/benchmark/Benchmarker.html#"><code>Benchmarker</code></a>,
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/benchmark/BenchmarkReport.html#"><code>BenchmarkReport</code></a>, <code>BenchmarkResult</code>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/benchmark/RuleDuration.html#"><code>RuleDuration</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/benchmark/StringBuilderCR.html#"><code>StringBuilderCR</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/benchmark/TextReport.html#"><code>TextReport</code></a>. Their API is not supported anymore
  and is disconnected from the internals of PMD. Use the newer API based around <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/benchmark/TimeTracker.html#"><code>TimeTracker</code></a> instead, which can be found
  in the same package.
* The class <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/xpath/TypeOfFunction.html#"><code>TypeOfFunction</code></a> has been deprecated. Use the newer <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/xpath/TypeIsFunction.html#"><code>TypeIsFunction</code></a> in the same package.
* The `typeof` methods in <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/xpath/JavaFunctions.html#"><code>JavaFunctions</code></a> have been deprecated.
  Use the newer `typeIs` method in the same class instead.
* The methods `isA`, `isEither` and `isNeither` of <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/typeresolution/TypeHelper.html#"><code>TypeHelper</code></a>.
  Use the new `isExactlyAny` and `isExactlyNone` methods in the same class instead.

#### 6.2.0

*   The static method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cli/PMDParameters.html#transformParametersIntoConfiguration(net.sourceforge.pmd.cli.PMDParameters)"><code>PMDParameters#transformParametersIntoConfiguration</code></a> is now deprecated,
    for removal in 7.0.0. The new instance method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cli/PMDParameters.html#toConfiguration()"><code>toConfiguration</code></a> replaces it.

*   The method <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTConstructorDeclaration.html#getParameters()"><code>ASTConstructorDeclaration#getParameters</code></a> has been deprecated in favor of the new method
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTConstructorDeclaration.html#getFormalParameters()"><code>getFormalParameters</code></a>. This method is available for both
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTConstructorDeclaration.html#"><code>ASTConstructorDeclaration</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.55.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#"><code>ASTMethodDeclaration</code></a>.

#### 6.1.0

* The method <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#getXPathNodeName()"><code>getXPathNodeName</code></a> is added to the <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a> interface, which removes the
  use of `toString` of a node to get its XPath element name (see [#569](https://github.com/pmd/pmd/issues/569)).
    * The default implementation provided in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/lang/ast/AbstractNode.html#"><code>AbstractNode</code></a>, will be removed with 7.0.0
    * With 7.0.0, the `Node.toString` method will not necessarily provide its XPath node
      name anymore.

* The interface <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/Renderer.html#"><code>net.sourceforge.pmd.cpd.Renderer</code></a> has been deprecated. A new interface
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/renderer/CPDRenderer.html#"><code>CPDRenderer</code></a> has been introduced to replace it. The main
  difference is that the new interface is meant to render directly to a `java.io.Writer`
  rather than to a String. This allows to greatly reduce the memory footprint of CPD, as on
  large projects, with many duplications, it was causing `OutOfMemoryError`s (see [#795](https://github.com/pmd/pmd/issues/795)).

  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/cpd/FileReporter.html#"><code>net.sourceforge.pmd.cpd.FileReporter</code></a> has also been deprecated as part of this change, as it's no longer needed.

#### 6.0.1

*   The constant <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.55.0/net/sourceforge/pmd/PMD.html#VERSION"><code>PMD#VERSION</code></a> has been deprecated and will be removed with PMD 7.0.0.
    Please use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.0.0/net/sourceforge/pmd/PMDVersion.html#VERSION"><code>PMDVersion#VERSION</code></a> instead.


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
    * [#4867](https://github.com/pmd/pmd/issues/4867): \[dist] ./mvnw command not found in dist-src
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
    * [#4828](https://github.com/pmd/pmd/issues/4828): \[apex] Support null coalescing operator ?? (apex 60)
    * [#4845](https://github.com/pmd/pmd/issues/4845): \[apex] Use same ANLTR version for apex-parser
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
    * [#4757](https://github.com/pmd/pmd/issues/4757): \[java] Intermittent NPEs while analyzing Java code
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
    * [#4817](https://github.com/pmd/pmd/issues/4817): \[java] UnusedPrivateMethod false-positive used in lambda
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
* [#4562](https://github.com/pmd/pmd/pull/4562): \[apex] Fixes #4556 - Update Apex bind regex match for all possible combinations - [nwcm](https://github.com/nwcm) (@nwcm)
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
* [#4752](https://github.com/pmd/pmd/pull/4752): \[core] Fix flaky LatticeRelationTest - [219sansim](https://github.com/219sansim) (@219sansim)
* [#4754](https://github.com/pmd/pmd/pull/4754): \[java] EmptyControlStatementRule: Add allowCommentedBlocks property - [Andreas Bergander](https://github.com/bergander) (@bergander)
* [#4759](https://github.com/pmd/pmd/pull/4759): \[java] fix: remove delimiter attribute from ruleset category/java/errorprone.xml - [Marcin Dąbrowski](https://github.com/marcindabrowski) (@marcindabrowski)
* [#4825](https://github.com/pmd/pmd/pull/4825): \[plsql] Fix ignored WITH clause for SELECT INTO statements - [Laurent Bovet](https://github.com/lbovet) (@lbovet)
* [#4857](https://github.com/pmd/pmd/pull/4857): \[javascript] Fix UnnecessaryBlock issues with empty statements - [Oleksandr Shvets](https://github.com/oleksandr-shvets) (@oleksandr-shvets)
