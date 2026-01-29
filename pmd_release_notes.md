


## 30-January-2026 - 7.21.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.21.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [🚀️ New: Java 26 Support](#new-java-26-support)
    * [Changed Rules](#changed-rules)
    * [Build Requirement is Java 21](#build-requirement-is-java-21)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

#### 🚀️ New: Java 26 Support
This release of PMD brings support for Java 26.

There are no new standard language features.

There is one preview language feature:
* [JEP 530: Primitive Types in Patterns, instanceof, and switch (Fourth Preview)](https://openjdk.org/jeps/530)

In order to analyze a project with PMD that uses these preview language features,
you'll need to select the new language version `26-preview`:

    pmd check --use-version java-26-preview ...

Note: Support for Java 24 preview language features have been removed. The version "24-preview"
is no longer available.

#### Changed Rules
The following rules have been changed to use a consistent implementation of enum based
rule properties:
* The property `checkAddressTypes` of rule [`AvoidUsingHardCodedIP`](https://docs.pmd-code.org/pmd-doc-7.21.0-SNAPSHOT/pmd_rules_java_bestpractices.html#avoidusinghardcodedip) has changed:
  * Instead of `IPv4` use `ipv4`
  * Instead of `IPv6` use `ipv6`
  * Instead of `IPv4 mapped IPv6` use `ipv4MappedIpv6`
  * The old values still work, but you'll see a deprecation warning.
* The property `nullCheckBranch` of rule [`ConfusingTernary`](https://docs.pmd-code.org/pmd-doc-7.21.0-SNAPSHOT/pmd_rules_java_codestyle.html#confusingternary) has changed:
  * Instead of `Any` use `any`
  * Instead of `Then` use `then`
  * Instead of `Else` use `else`
  * The old values still work, but you'll see a deprecation warning.
* The property `typeAnnotations` of rule [`ModifierOrder`](https://docs.pmd-code.org/pmd-doc-7.21.0-SNAPSHOT/pmd_rules_java_codestyle.html#modifierorder) has changed:
  * Instead of `ontype` use `onType`
  * Instead of `ondecl` use `onDecl`
  * The old values still work, but you'll see a deprecation warning.
* The values of the properties of rule [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.21.0-SNAPSHOT/pmd_rules_java_documentation.html#commentrequired) have changed:
  * Instead of `Required` use `required`
  * Instead of `Ignored` use `ignored`
  * Instead of `Unwanted` use `unwanted`
  * The old values still work, but you'll see a deprecation warning.

#### Build Requirement is Java 21
From now on, Java 21 or newer is required to build PMD. PMD itself still remains compatible with Java 8,
so that it still can be used in a pure Java 8 environment. This allows us to use the latest
checkstyle version during the build.

### 🐛️ Fixed Issues
* core
  * [#6184](https://github.com/pmd/pmd/issues/6184): \[core] Consistent implementation of enum properties
* apex-codestyle
  * [#6349](https://github.com/pmd/pmd/issues/6349): \[apex] FieldDeclarationsShouldBeAtStart: False positive with properties
* cli
  * [#6290](https://github.com/pmd/pmd/issues/6290): \[cli] Improve Designer start script
* java
  * [#5871](https://github.com/pmd/pmd/issues/5871): \[java] Support Java 26
* java-errorprone
  * [#3601](https://github.com/pmd/pmd/issues/3601): \[java] InvalidLogMessageFormat: False positive when final parameter is Supplier&lt;Throwable&gt;
  * [#5882](https://github.com/pmd/pmd/issues/5882): \[java] UnconditionalIfStatement: False negative when true/false is not literal but local variable
* java-performance
  * [#3857](https://github.com/pmd/pmd/issues/3857): \[java] InsufficientStringBufferDeclaration: False negatives with String constants

### 🚨️ API Changes

#### Deprecations
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/metrics/MetricOption.html#valueName()"><code>MetricOption#valueName</code></a>: When metrics are used for (rule) properties,
    then the conventional enum mapping (from SCREAMING_SNAKE_CASE to camelCase) will be used for the enum values.
    See <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumListProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumListProperty</code></a>.
  * In <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#"><code>PropertyFactory</code></a>:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#enumProperty(java.lang.String,java.util.Map)"><code>enumProperty(String, Map)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumProperty</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#enumProperty(java.lang.String,java.lang.Class)"><code>enumProperty(String, Class)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumProperty</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#enumProperty(java.lang.String,java.lang.Class,java.util.function.Function)"><code>enumProperty(String, Class, Function)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumProperty</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#enumListProperty(java.lang.String,java.util.Map)"><code>enumListProperty(String, Map)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumListProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumListProperty</code></a> instead.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#enumListProperty(java.lang.String,java.lang.Class,java.util.function.Function)"><code>enumListProperty(String, Class, Function)</code></a>. Use
      <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/PropertyFactory.html#conventionalEnumListProperty(java.lang.String,java.lang.Class)"><code>conventionalEnumListProperty</code></a> instead.
* java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_FOR"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_FOR</code></a>. This constant should
    have never been public.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_DO"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_DO</code></a>. This constant should
    have never been public.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_WHILE"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_WHILE</code></a>. This constant should
    have never been public.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_BREAK_LOOP_TYPES"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_BREAK_LOOP_TYPES</code></a>. This property
    descriptor should have been private. It won't be used anymore. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertyDescriptor(java.lang.String)"><code>getPropertyDescriptor</code></a>
    on the rule to retrieve the property descriptor.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_CONTINUE_LOOP_TYPES"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_CONTINUE_LOOP_TYPES</code></a>. This property
    descriptor should have been private. It won't be used anymore. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertyDescriptor(java.lang.String)"><code>getPropertyDescriptor</code></a>
    on the rule to retrieve the property descriptor.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#CHECK_RETURN_LOOP_TYPES"><code>AvoidBranchingStatementAsLastInLoopRule#CHECK_RETURN_LOOP_TYPES</code></a>. This property
    descriptor should have been private. It won't be used anymore. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/properties/AbstractPropertySource.html#getPropertyDescriptor(java.lang.String)"><code>getPropertyDescriptor</code></a>
    on the rule to retrieve the property descriptor.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#check(net.sourceforge.pmd.properties.PropertyDescriptor,net.sourceforge.pmd.lang.ast.Node,java.lang.Object)"><code>AvoidBranchingStatementAsLastInLoopRule#check</code></a>.
    This method should have been private and will be internalized.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#hasPropertyValue(net.sourceforge.pmd.properties.PropertyDescriptor,java.lang.String)"><code>AvoidBranchingStatementAsLastInLoopRule#hasPropertyValue</code></a>.
    This method should have been private and will be internalized.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/rule/errorprone/AvoidBranchingStatementAsLastInLoopRule.html#checksNothing()"><code>AvoidBranchingStatementAsLastInLoopRule#checksNothing</code></a>.
    This method should have been private and will be internalized.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.ClassFanOutOption.html#valueName()"><code>ClassFanOutOption#valueName</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.CycloOption.html#valueName()"><code>CycloOption#valueName</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.NcssOption.html#valueName()"><code>NcssOption#valueName</code></a>
* lang-test
  * <a href="https://docs.pmd-code.org/apidocs/pmd-lang-test/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/test/AbstractMetricTestRule.html#optionMappings()"><code>AbstractMetricTestRule#optionMappings</code></a>. No extra mapping is required anymore.
    The <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/metrics/MetricOption.html#"><code>MetricOption</code></a> enum values are used. See 
    <a href="https://docs.pmd-code.org/apidocs/pmd-lang-test/7.21.0-SNAPSHOT/net/sourceforge/pmd/lang/test/AbstractMetricTestRule.html#AbstractMetricTestRule(net.sourceforge.pmd.lang.metrics.Metric,java.lang.Class)"><code>AbstractMetricTestRule(Metric, Class)</code></a>
    to provide the enum at construction time.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



