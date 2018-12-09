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

#### Kotlin support for CPD

Thanks to [Maikel Steneker](https://github.com/maikelsteneker), CPD now supports [Kotlin](https://kotlinlang.org/).
This means, you can use CPD to find duplicated code in your Kotlin projects.

#### New Rules

*   The new Java rule {% rule "java/codestyle/UseUnderscoresInNumericLiterals" %} (`java-codestyle`)
    verifies that numeric literals over a given length (4 chars by default, but configurable) are using
    underscores every 3 digits for readability. The rule only applies to Java 7+ codebases.

#### Modified Rules

*   The Java rule {% rule "java/bestpractices/JUnitTestsShouldIncludeAssert" %} (`java-bestpractices`)
    now also detects [Soft Assertions](https://github.com/joel-costigliola/assertj-core).

*   The property `exceptionfile` of the rule {% rule java/errorprone/AvoidDuplicateLiterals %} (`java-errorprone`)
    has been deprecated and will be removed with 7.0.0. Please use `exceptionList` instead.

### Fixed Issues
*   all
    *   [#1284](https://github.com/pmd/pmd/issues/1284): \[doc] Keep record of every currently deprecated API
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1328](https://github.com/pmd/pmd/issues/1328): \[ci] Building docs for release fails
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties
    *   [#1468](https://github.com/pmd/pmd/issues/1468): \[doc] Missing escaping leads to XSS
    *   [#1471](https://github.com/pmd/pmd/issues/1471): \[core] XMLRenderer: ProcessingErrors from exceptions without a message missing
    *   [#1477](https://github.com/pmd/pmd/issues/1477): \[core] Analysis cache fails with wildcard classpath entries
*   java
    *   [#1460](https://github.com/pmd/pmd/issues/1460): \[java] Intermittent PMD failure : PMD processing errors while no violations reported
*   java-bestpractices
    *   [#647](https://github.com/pmd/pmd/issues/647): \[java] JUnitTestsShouldIncludeAssertRule should support `this.exception` as well as just `exception`
    *   [#1435](https://github.com/pmd/pmd/issues/1435): \[java] JUnitTestsShouldIncludeAssert: Support AssertJ soft assertions
*   java-codestyle
    *   [#1232](https://github.com/pmd/pmd/issues/1232): \[java] Detector for large numbers not separated by _
    *   [#1372](https://github.com/pmd/pmd/issues/1372): \[java] false positive for UselessQualifiedThis
    *   [#1440](https://github.com/pmd/pmd/issues/1440): \[java] CommentDefaultAccessModifierRule shows incorrect message
*   java-design
    *   [#1151](https://github.com/pmd/pmd/issues/1151): \[java] ImmutableField false positive with multiple constructors
    *   [#1483](https://github.com/pmd/pmd/issues/1483): \[java] Cyclo metric should count conditions of for statements correctly
*   java-errorprone
    *   [#1512](https://github.com/pmd/pmd/issues/1512): \[java] InvalidSlf4jMessageFormatRule causes NPE in lambda and static blocks
*   plsql
    *   [#1454](https://github.com/pmd/pmd/issues/1454): \[plsql] ParseException for IF/CASE statement with >=, <=, !=


### API Changes

#### Properties framework

{% jdoc_nspace :props core::properties %}
{% jdoc_nspace :PDr props::PropertyDescriptor %}
{% jdoc_nspace :PF props::PropertyFactory %}

The properties framework is about to get a lifting, and for that reason, we need to deprecate a lot of APIs
to remove them in 7.0.0. The proposed changes to the API are described [on the wiki](https://github.com/pmd/pmd/wiki/Property-framework-7-0-0)

##### Changes to how you define properties


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



##### Architectural simplifications

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

##### Changes to the PropertyDescriptor interface

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

#### Deprecated APIs

{% jdoc_nspace :xpath core::lang.ast.xpath %}
{% jdoc_nspace :jast java::lang.java.ast %}
{% jdoc_nspace :rule core::Rule %}
{% jdoc_nspace :lvh core::lang.LanguageVersionHandler %}
{% jdoc_nspace :rset core::RuleSet %}
{% jdoc_nspace :rsets core::RuleSets %}

##### For internalization

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

##### For removal

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

### External Contributions

*   [#1384](https://github.com/pmd/pmd/pull/1384): \[java] New Rule - UseUnderscoresInNumericLiterals - [RajeshR](https://github.com/rajeshggwp)
*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)
*   [#1428](https://github.com/pmd/pmd/pull/1428): \[core] Upgrading JCommander from 1.48 to 1.72 - [Thunderforge](https://github.com/Thunderforge)
*   [#1430](https://github.com/pmd/pmd/pull/1430): \[doc] Who really knows regex? - [Dem Pilafian](https://github.com/dpilafian)
*   [#1434](https://github.com/pmd/pmd/pull/1434): \[java] JUnitTestsShouldIncludeAssert: Recognize AssertJ soft assertions as valid assert statements - [Loïc Ledoyen](https://github.com/ledoyen)
*   [#1439](https://github.com/pmd/pmd/pull/1439): \[java] Avoid FileInputStream and FileOutputStream - [reudismam](https://github.com/reudismam)
*   [#1441](https://github.com/pmd/pmd/pull/1441): \[kotlin] [cpd] Added CPD support for Kotlin - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1447](https://github.com/pmd/pmd/pull/1447): \[fortran] Use diamond operator in impl - [reudismam](https://github.com/reudismam)
*   [#1453](https://github.com/pmd/pmd/pull/1453): \[java] Adding the fix for #1440. Showing correct message for CommentDefaultAccessmodifier. - [Rohit Kumar](https://github.com/stationeros)
*   [#1457](https://github.com/pmd/pmd/pull/1457): \[java] Adding test for Issue #647 - [orimarko](https://github.com/orimarko)
*   [#1464](https://github.com/pmd/pmd/pull/1464): \[doc] Fix XSS on documentation web page - [Maxime Robert](https://github.com/marob)
*   [#1469](https://github.com/pmd/pmd/pull/1469): \[core] Configurable max loops in DAAPathFinder - [Alberto Fernández](https://github.com/albfernandez)
*   [#1494](https://github.com/pmd/pmd/pull/1494): \[java] 1151: Rephrase ImmutableField documentation in design.xml - [Robbie Martinus](https://github.com/rmartinus)
*   [#1504](https://github.com/pmd/pmd/pull/1504): \[java] NPE in InvalidSlf4jMessageFormatRule if a logger call with a variable as parameter is not inside a method or constructor - [kris-scheibe](https://github.com/kris-scheibe)

{% endtocmaker %}

{% unless is_release_notes_processor %}
    {% include note.html content="The release notes of previous versions are available [here](pmd_release_notes_old.html)" %}
{% endunless %}

