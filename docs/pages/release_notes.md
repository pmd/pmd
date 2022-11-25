---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### New rules

* The new Java rule {% rule java/design/InvalidJavaBean %} identifies beans, that don't follow the [JavaBeans API specification](https://download.oracle.com/otndocs/jcp/7224-javabeans-1.01-fr-spec-oth-JSpec/),
  like beans with missing getters or setters.

```xml
<rule ref="category/java/design.xml/InvalidJavaBean"/>
```

#### Renamed rules

* The Java rule {% rule java/errorprone/BeanMembersShouldSerialize %} has been renamed to
  {% rule java/errorprone/NonSerializableClass %}. It has been revamped to only check for classes that are marked with
  `Serializable` and reports each field in it, that is not serializable.

  The property `prefix` has been deprecated, since in a serializable class all fields have to be
  serializable regardless of the name.

#### Modified rules

* The rule {% rule java/codestyle/ClassNamingConventions %} has a new property `testClassPattern`, which is applied
  to test classes. By default, test classes should end with the suffix "Test". Test classes are top-level classes, that
  either inherit from JUnit 3 TestCase or have at least one method annotated with the Test annotations from
  JUnit4/5 or TestNG.

* The property `ignoredAnnotations` of rule {% rule java/design/ImmutableField %} has been deprecated  and doesn't
  have any effect anymore.
  Since PMD 6.47.0, the rule only considers fields, that are initialized once and never changed. If the field is just
  declared but never explicitly initialized, it won't be reported. That's the typical case when a framework sets
  the field value by reflection. Therefore, the property is not needed anymore. If there is a special case where
  this rule misidentifies fields as immutable, then the rule should be suppressed for these fields explicitly.

### Fixed Issues
* cli
    * [#4215](https://github.com/pmd/pmd/discussions/4215): NullPointerException when trying to open designer
* doc
    * [#4207](https://github.com/pmd/pmd/pull/4207): \[doc] List all languages in rule doc
* java
    * [#3643](https://github.com/pmd/pmd/issues/3643): \[java] More parser edge cases
    * [#4152](https://github.com/pmd/pmd/issues/4152): \[java] Parse error on array type annotations
* java-codestyle
    * [#2867](https://github.com/pmd/pmd/issues/2867): \[java] Separate pattern for test classes in ClassNamingConventions rule for Java
    * [#4201](https://github.com/pmd/pmd/issues/4201): \[java] CommentDefaultAccessModifier should consider lombok's @<!-- -->Value
* java-design
    * [#4175](https://github.com/pmd/pmd/issues/4175): \[java] ImmutableField - deprecate property `ignoredAnnotations`
    * [#4177](https://github.com/pmd/pmd/issues/4177): \[java] New Rule InvalidJavaBean
    * [#4188](https://github.com/pmd/pmd/issues/4188): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal false positive with Lombok's @<!-- -->NoArgsConstructor
    * [#4189](https://github.com/pmd/pmd/issues/4189): \[java] AbstractClassWithoutAnyMethod should consider lombok's @<!-- -->AllArgsConstructor
    * [#4200](https://github.com/pmd/pmd/issues/4200): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal should consider lombok's @<!-- -->Value
* java-errorprone
    * [#1668](https://github.com/pmd/pmd/issues/1668): \[java] BeanMembersShouldSerialize is extremely noisy
    * [#4172](https://github.com/pmd/pmd/issues/4172): \[java] InvalidLogMessageFormat false positive on externally formatted strings
    * [#4174](https://github.com/pmd/pmd/issues/4174): \[java] MissingStaticMethodInNonInstantiatableClass does not consider nested builder class
    * [#4176](https://github.com/pmd/pmd/issues/4176): \[java] Rename BeanMembersShouldSerialize to NonSerializableClass
    * [#4185](https://github.com/pmd/pmd/issues/4185): \[java] InvalidLogMessageFormat rule produces a NPE
    * [#4224](https://github.com/pmd/pmd/issues/4224): \[java] MissingStaticMethodInNonInstantiatableClass should consider Lombok's @<!-- -->UtilityClass
    * [#4225](https://github.com/pmd/pmd/issues/4225): \[java] MissingStaticMethodInNonInstantiatableClass should consider Lombok's @<!-- -->NoArgsConstructor
* java-performance
    * [#4183](https://github.com/pmd/pmd/issues/4183): \[java] AvoidArrayLoops regression: from false negative to false positive with final variables

### API Changes

#### PMD CLI

* PMD now supports a new `--use-version` flag, which receives a language-version pair (such as `java-8` or `apex-54`).
This supersedes the usage of `-language` / `-l` and `-version` / `-v`, allowing for multiple versions to be set in a single run.
PMD 7 will completely remove support for `-language` and `-version` in favor of this new flag.

* Support for `-V` is being deprecated in favor of `--verbose` in preparation for PMD 7.
In PMD 7, `-v` will enable verbose mode and `-V` will show the PMD version for consistency with most Unix/Linux tools.

* Support for `-min` is being deprecated in favor of `--minimum-priority` for consistency with most Unix/Linux tools, where `-min` would be equivalent to `-m -i -n`.

#### CPD CLI

* CPD now supports using `-d` or `--dir` as an alias to `--files`, in favor of consistency with PMD.
PMD 7 will remove support for `--files` in favor of these new flags.

#### Linux run.sh parameters

* Using `run.sh cpdgui` will now warn about it being deprecated. Use `run.sh cpd-gui` instead.

* The old designer (`run.sh designerold`) is completely deprecated and will be removed in PMD 7. Switch to the new JavaFX designer: `run.sh designer`.

* The old visual AST viewer (`run.sh bgastviewer`) is completely deprecated and will be removed in PMD 7. Switch to the new JavaFX designer: `run.sh designer` for a visual tool, or use `run.sh ast-dump` for a text-based alternative.

#### Deprecated API

* The following core APIs have been marked as deprecated for removal in PMD 7:
  - {% jdoc core::PMD %} and {% jdoc core::PMD.StatusCode %} - PMD 7 will ship with a revamped CLI split from pmd-core. To programmatically launch analysis you can use {% jdoc core::PmdAnalysis %}.
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
  - {% jdoc core::cpd.CPD %} and {% jdoc core::cpd.CPD.StatusCode %} - PMD 7 will ship with a revamped CLI split from pmd-core. An alterative to programatically launch CPD analysis will be added in due time.

* In order to reduce the dependency on Apex Jorje classes, the method {% jdoc !!visualforce::lang.vf.DataType#fromBasicType(apex.jorje.semantic.symbol.type.BasicType) %}
  has been deprecated. The equivalent method {% jdoc visualforce::lang.vf.DataType#fromTypeName(java.lang.String) %} should be used instead.

### External Contributions
* [#4184](https://github.com/pmd/pmd/pull/4184): \[java]\[doc] TestClassWithoutTestCases - fix small typo in description - [Valery Yatsynovich](https://github.com/valfirst) (@valfirst)
* [#4198](https://github.com/pmd/pmd/pull/4198): \[doc] Add supported CPD languages - [Jeroen van Wilgenburg](https://github.com/jvwilge) (@jvwilge)
* [#4202](https://github.com/pmd/pmd/pull/4202): \[java] Fix #4200 and #4201: ClassWithOnlyPrivateConstructorsShouldBeFinal, CommentDefaultAccessModifier: Exclude lombok @<!-- -->Value annotation - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4205](https://github.com/pmd/pmd/pull/4205): \[doc] Clarify Scala support (no built-in rules) - [Eldrick Wega](https://github.com/Eldrick19) (@Eldrick19)
* [#4226](https://github.com/pmd/pmd/pull/4226): \[visualforce] Replace uses of Jorje types in pmd-visualforce - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4227](https://github.com/pmd/pmd/pull/4227): \[java] Fix #4225 MissingStaticMethodInNonInstantiatableClass: Exclude lombok's @<!-- -->NoArgsConstructor annotation - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4228](https://github.com/pmd/pmd/pull/4228): \[java] Fix #4224 MissingStaticMethodInNonInstantiatableClass: Exclude lombok's UtilityClass - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4232](https://github.com/pmd/pmd/pull/4232): \[doc] Fixing typos - [Andreas Deininger](https://github.com/deining) (@deining)

{% endtocmaker %}

