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

### Fixed Issues
*   all
    *   [#1284](https://github.com/pmd/pmd/issues/1284): \[doc] Keep record of every currently deprecated API
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1328](https://github.com/pmd/pmd/issues/1328): \[ci] Building docs for release fails
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties
    *   [#1468](https://github.com/pmd/pmd/issues/1468): \[doc] Missing escaping leads to XSS
    *   [#1471](https://github.com/pmd/pmd/issues/1471): \[core] XMLRenderer: ProcessingErrors from exceptions without a message missing
*   java
    *   [#1460](https://github.com/pmd/pmd/issues/1460): \[java] Intermittent PMD failure : PMD processing errors while no violations reported
*   java-bestpractices
    *   [#1435](https://github.com/pmd/pmd/issues/1435): \[java] JUnitTestsShouldIncludeAssert: Support AssertJ soft assertions
*   java-codestyle
    *   [#1232](https://github.com/pmd/pmd/issues/1232): \[java] Detector for large numbers not separated by _
    *   [#1372](https://github.com/pmd/pmd/issues/1372): \[java] false positive for UselessQualifiedThis
    *   [#1440](https://github.com/pmd/pmd/issues/1440): \[java] CommentDefaultAccessModifierRule shows incorrect message
*   java-design
    *   [#1483](https://github.com/pmd/pmd/issues/1483): \[java] Cyclo metric should count conditions of for statements correctly


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


##### Internalized API

The following APIs were never intended as public API and will be internalized or removed with 7.0.0.

* All classes from {% jdoc_package props::modules %} are deprecated and will be removed. 
* The classes {% jdoc props::PropertyDescriptorField %}, {% jdoc props::builders.PropertyDescriptorBuilderConversionWrapper %}, and the methods
  {% jdoc !c!:PDr#attributeValuesById %}, {% jdoc !c!:PDr#isDefinedExternally() %} and {% jdoc !c!props::PropertyTypeId#getFactory() %}.
  These were used to read and write properties to and from XML, but were not intended as public API.
* The class {% jdoc props::ValueParserConstants %} and the interface {% jdoc props::ValueParser %}.
  
#### Deprecated APIs

{% jdoc_nspace :xpath core::lang.ast.xpath %}
{% jdoc_nspace :jast java::lang.java.ast %}

*   The implementation of the adapters for the XPath engines Saxon and Jaxen (package {% jdoc_package :xpath %})
    are now deprecated. They'll be moved to an internal package come 7.0.0. Only {% jdoc xpath::Attribute %} remains public API.

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

*   All classes from {% jdoc_package java::lang.java.metrics.impl.visitors %} are now considered internal API. They're deprecated
    and will be moved into an internal package with 7.0.0. To implement your own metrics visitors,
    {% jdoc jast::JavaParserVisitorAdapter %} should be directly subclassed.


### External Contributions

*   [#1384](https://github.com/pmd/pmd/pull/1384): \[java] New Rule - UseUnderscoresInNumericLiterals - [RajeshR](https://github.com/rajeshggwp)
*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)
*   [#1428](https://github.com/pmd/pmd/pull/1428): \[core] Upgrading JCommander from 1.48 to 1.72 - [Thunderforge](https://github.com/Thunderforge)
*   [#1430](https://github.com/pmd/pmd/pull/1430): \[doc] Who really knows regex? - [Dem Pilafian](https://github.com/dpilafian)
*   [#1434](https://github.com/pmd/pmd/pull/1434): \[java] JUnitTestsShouldIncludeAssert: Recognize AssertJ soft assertions as valid assert statements - [Loïc Ledoyen](https://github.com/ledoyen)
*   [#1441](https://github.com/pmd/pmd/pull/1441): \[kotlin] [cpd] Added CPD support for Kotlin - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1447](https://github.com/pmd/pmd/pull/1447): \[fortran] Use diamond operator in impl - [reudismam](https://github.com/reudismam)
*   [#1453](https://github.com/pmd/pmd/pull/1453): \[java] Adding the fix for #1440. Showing correct message for CommentDefaultAccessmodifier. - [Rohit Kumar](https://github.com/stationeros)
*   [#1464](https://github.com/pmd/pmd/pull/1464): \[doc] Fix XSS on documentation web page - [Maxime Robert](https://github.com/marob)
*   [#1469](https://github.com/pmd/pmd/pull/1469): \[core] Configurable max loops in DAAPathFinder - [Alberto Fernández](https://github.com/albfernandez)

{% endtocmaker %}

{% unless is_release_notes_processor %}
    {% include note.html content="The release notes of previous versions are available [here](pmd_release_notes_old.html)" %}
{% endunless %}

