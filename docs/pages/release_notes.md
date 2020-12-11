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

##### CPD

* The C# module now supports the new option [`--ignore-literal-sequences`](https://pmd.github.io/latest/pmd_userdocs_cpd.html#-ignore-literal-sequences), which can be used to avoid detection of some uninteresting clones. Support for other languages may be added in the future. See [#2945](https://github.com/pmd/pmd/pull/2945)

* The Scala module now supports [suppression](https://pmd.github.io/latest/pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs. See [#2929](https://github.com/pmd/pmd/pull/2929)


##### Type information for VisualForce

The Visualforce AST now can resolve the data type of Visualforce expressions that reference Apex Controller properties and Custom Object fields. This feature improves the precision of existing rules, like {% rule vf/security/VfUnescapeEl %}.

This can be configured using two environment variables:
* `PMD_VF_APEXDIRECTORIES`: Comma separated list of directories for Apex classes. Absolute or relative to the Visualforce directory. Default is `../classes`. Specifying an empty string will disable data type resolution for Apex Controller properties.
* `PMD_VF_OBJECTSDIRECTORIES`: Comma separated list of directories for Custom Objects. Absolute or relative to the Visualforce directory. Default is `../objects`. Specifying an empty string will disable data type resolution for Custom Object fields.

This feature is experimental, in particular, expect changes to the way the configuration is specified. We'll probably extend the CLI instead of relying on environment variables in a future version.

Thanks to Jeff Bartolotta and Roopa Mohan for contributing this!

### Fixed Issues

*   core
    * [#1939](https://github.com/pmd/pmd/issues/1939): \[core] XPath expressions return handling
    * [#1961](https://github.com/pmd/pmd/issues/1961): \[core] Text renderer should include name of violated rule
    * [#2874](https://github.com/pmd/pmd/pull/2874): \[core] Fix XMLRenderer with UTF-16
*   cs
    * [#2938](https://github.com/pmd/pmd/pull/2938): \[cs] CPD: ignoring using directives could not be disabled
*   java
    * [#2911](https://github.com/pmd/pmd/issues/2911): \[java] `ClassTypeResolver#searchNodeNameForClass` leaks memory
    * [#2934](https://github.com/pmd/pmd/pull/2934): \[java] CompareObjectsWithEquals / UseEqualsToCompareStrings - False negatives with fields
    * [#2940](https://github.com/pmd/pmd/pull/2940): \[java] Catch additional TypeNotPresentExceptions / LinkageErrors
*   scala
    * [#2480](https://github.com/pmd/pmd/issues/2480): \[scala] Support CPD suppressions


### API Changes

#### Deprecated API


##### Around RuleSet parsing

* {% jdoc core::RuleSetFactory %} and {% jdoc core::RuleSetFactoryUtils %} have been deprecated in favor of {% jdoc core::RuleSetLoader %}. This is easier to configure, and more maintainable than the multiple overloads of `RuleSetFactoryUtils`.
* Some static creation methods have been added to {% jdoc core::RuleSet %} for simple cases, eg {% jdoc core::RuleSet#forSingleRule(core::Rule) %}. These replace some counterparts in {% jdoc core::RuleSetFactory %}
* Since {% jdoc core::RuleSets %} is also deprecated, many APIs that require a RuleSets instance now are deprecated, and have a counterpart that expects a `List<RuleSet>`.
* {% jdoc core::RuleSetReferenceId %}, {% jdoc core::RuleSetReference %}, {% jdoc core::RuleSetFactoryCompatibility %} are deprecated. They are most likely not relevant outside of the implementation of pmd-core.

##### Around the `PMD` class

Many classes around PMD's entry point ({% jdoc core::PMD %}) have been deprecated as internal, including:
* The contents of the packages {% jdoc_package core::cli %}, {% jdoc_package core::processor %}
* {% jdoc core::SourceCodeProcessor %}
* The constructors of {% jdoc core::PMD %} (the class will be made a utility class)

##### Miscellaneous

*   {% jdoc !!java::lang.java.ast.ASTPackageDeclaration#getPackageNameImage() %},
    {% jdoc !!java::lang.java.ast.ASTTypeParameter#getParameterName() %}
    and the corresponding XPath attributes. In both cases they're replaced with a new method `getName`,
    the attribute is `@Name`.
*   {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceBody#isAnonymousInnerClass() %},
    and {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceBody#isEnumChild() %},
    refs [#905](https://github.com/pmd/pmd/issues/905)

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc !!javascript::lang.ecmascript.Ecmascript3Handler %}
*   {% jdoc !!javascript::lang.ecmascript.Ecmascript3Parser %}
*   {% jdoc !!javascript::lang.ecmascript.ast.EcmascriptParser#parserOptions %}
*   {% jdoc !!javascript::lang.ecmascript.ast.EcmascriptParser#getSuppressMap() %}
*   {% jdoc !!core::lang.rule.ParametricRuleViolation %}
*   {% jdoc !!core::lang.ParserOptions#suppressMarker %}
*   {% jdoc !!modelica::lang.modelica.rule.ModelicaRuleViolationFactory %}


### External Contributions

*   [#2864](https://github.com/pmd/pmd/pull/2864): [vf] Provide expression type information to Visualforce rules to avoid false positives - [Jeff Bartolotta](https://github.com/jbartolotta-sfdc)
*   [#2914](https://github.com/pmd/pmd/pull/2914): \[core] Include rule name in text renderer - [Gunther Schrijvers](https://github.com/GuntherSchrijvers)
*   [#2925](https://github.com/pmd/pmd/pull/2925): Cleanup: Correct annotation array initializer indents from checkstyle #8083 - [Abhishek Kumar](https://github.com/Abhishek-kumar09)
*   [#2929](https://github.com/pmd/pmd/pull/2929): \[scala] Add support for CPD-ON and CPD-OFF special comments - [Andy Robinson](https://github.com/andyrobinson)
*   [#2936](https://github.com/pmd/pmd/pull/2936): \[java] (doc) Fix typo: "an accessor" not "a" - [Igor Moreno](https://github.com/igormoreno)
*   [#2938](https://github.com/pmd/pmd/pull/2938): \[cs] CPD: fix issue where ignoring using directives could not be disabled - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2945](https://github.com/pmd/pmd/pull/2945): \[cs] Add option to ignore sequences of literals - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2962](https://github.com/pmd/pmd/pull/2962): \[cpp] Add support for C++ 14 binary literals - [Maikel Steneker](https://github.com/maikelsteneker)

{% endtocmaker %}
