


## 30-May-2025 - 7.14.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.14.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [PMD CLI now uses threaded execution by default](#pmd-cli-now-uses-threaded-execution-by-default)
    * [New Rule UnnecessaryWarningSuppression (experimental)](#new-rule-unnecessarywarningsuppression-experimental)
    * [Migrating to Central Publisher Portal](#migrating-to-central-publisher-portal)
    * [More CLI parameters shared between PMD and CPD](#more-cli-parameters-shared-between-pmd-and-cpd)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
    * [CLI](#cli)
    * [Deprecations](#deprecations)
    * [Experimental](#experimental)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### PMD CLI now uses threaded execution by default

In the PMD CLI, the `--threads` (`-t`) option can now accept a thread
count given relative to the number of cores of the machine. For instance,
it is now possible to write `-t 1C` to spawn one thread per core, or `-t 0.5C`
to spawn one thread for every other core.

The thread count option now defaults to `1C`, meaning parallel execution
is used by default. You can disable this by using `-t 1`.

#### New Rule UnnecessaryWarningSuppression (experimental)

This new Java rule [`UnnecessaryWarningSuppression`](https://docs.pmd-code.org/pmd-doc-7.14.0-SNAPSHOT/pmd_rules_java_bestpractices.html#unnecessarywarningsuppression) reports unused suppression
annotations and comments. Violations of this rule cannot be suppressed.

How to use it? Just include it in your ruleset:

```xml
<rule ref="category/java/bestpractices.xml/UnnecessaryWarningSuppression" />
```

Note: This rule is currently experimental. It is available for now only for Java.
The rule for now only reports annotations specific to PMD, like `@SuppressWarnings("PMD")`.
In the future we might be able to check for other common ones like `@SuppressWarnings("unchecked")` or `"fallthrough"`.
Since violations of this rule cannot be suppressed, we opted here on the side of false-negatives and
don't report every unused case yet.
However, suppressing specific PMD rules is working as expected.

#### Migrating to Central Publisher Portal

We've now migrated to [Central Publisher Portal](https://central.sonatype.org/publish/publish-portal-guide/).
Snapshots of PMD are still available, however the repository URL changed. To consume these with maven, you can
use the following snippet:

```xml
<repositories>
  <repository>
    <name>Central Portal Snapshots</name>
    <id>central-portal-snapshots</id>
    <url>https://central.sonatype.com/repository/maven-snapshots/</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

Releases of PMD are available on [Maven Central](https://central.sonatype.com/) as before without change.

#### More CLI parameters shared between PMD and CPD

When executing PMD or CPD, the same parameters are now understood for selecting which files should
be analyzed. See [File collection options](https://docs.pmd-code.org/pmd-doc-7.14.0-SNAPSHOT/pmd_userdocs_cli_reference.html#file-collection-options)
for a list of common, shared parameters that are valid for both commands.

### 🐛 Fixed Issues
* core
  * [#648](https://github.com/pmd/pmd/issues/648): \[core] Warn on unneeded suppression
  * [#5700](https://github.com/pmd/pmd/pull/5700): \[core] Don't accidentally catch unexpected runtime exceptions in CpdAnalysis
  * [#5705](https://github.com/pmd/pmd/issues/5705): \[cli] PMD's start script fails if PMD_HOME is set
* java-bestpractices
  * [#5061](https://github.com/pmd/pmd/issues/5061): \[java] UnusedLocalVariable false positive when variable is read as side effect of an assignment
  * [#5621](https://github.com/pmd/pmd/issues/5621): \[java] UnusedPrivateMethod with method ref
  * [#5724](https://github.com/pmd/pmd/issues/5724): \[java] ImplicitFunctionalInterface should not be reported on sealed interfaces
* java-codestyle
  * [#2462](https://github.com/pmd/pmd/issues/2462): \[java] LinguisticNaming must ignore setters that returns current type (Builder pattern)
  * [#5634](https://github.com/pmd/pmd/issues/5634): \[java] CommentDefaultAccessModifier doesn't recognize /* package */ comment at expected location for constructors
* java-design
  * [#5568](https://github.com/pmd/pmd/issues/5568): \[java] High NPathComplexity in `switch` expression
  * [#5647](https://github.com/pmd/pmd/issues/5647): \[java] NPathComplexity does not account for `return`s
* java-errorprone
  * [#5702](https://github.com/pmd/pmd/issues/5702): \[java] InvalidLogMessageFormat: Lombok @<!-- -->Slf4j annotation is not interpreted by PMD
* java-performance
  * [#5711](https://github.com/pmd/pmd/issues/5711): \[java] UseArraysAsList false positive with Sets
* visualforce
  * [#5476](https://github.com/pmd/pmd/issues/5476): \[visualforce] NPE when analyzing standard field references in visualforce page

### 🚨 API Changes
#### CLI
* CPD now supports `--report-file` (-r) and `--exclude-file-list`.
* PMD now supports `--exclude` and `--non-recursive`.
* The option `--ignore-list` in PMD is renamed to `--exclude-file-list`.

#### Deprecations
* CLI
  * The option `--ignore-list` has been deprecated. Use `--exclude-file-list` instead.
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/AstInfo.html#getSuppressionComments()"><code>AstInfo#getSuppressionComments</code></a>: Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/AstInfo.html#getAllSuppressionComments()"><code>getAllSuppressionComments</code></a>
    or <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/AstInfo.html#getSuppressionComment(int)"><code>getSuppressionComment</code></a>.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/AstInfo.html#withSuppressMap()"><code>AstInfo#withSuppressMap</code></a>: Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/AstInfo.html#withSuppressionComments(java.util.Collection)"><code>withSuppressionComments</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/impl/javacc/AbstractTokenManager.html#suppressMap"><code>AbstractTokenManager#suppressMap</code></a>: Don't use this map directly anymore. Instead,
    use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/impl/javacc/AbstractTokenManager.html#getSuppressionComments()"><code>getSuppressionComments</code></a>.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/impl/javacc/AbstractTokenManager.html#getSuppressMap()"><code>AbstractTokenManager#getSuppressMap</code></a>: Use
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/impl/javacc/AbstractTokenManager.html#getSuppressionComments()"><code>getSuppressionComments</code></a> instead.
* pmd-java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/java/ast/ASTCompactConstructorDeclaration.html#getDeclarationNode()"><code>ASTCompactConstructorDeclaration#getDeclarationNode</code></a>: This method just returns `this` and isn't useful.
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.html#NPATH"><code>JavaMetrics#NPATH</code></a>: Use <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.html#NPATH_COMP"><code>NPATH_COMP</code></a>, which is available on more nodes,
    and uses Long instead of BigInteger.

#### Experimental
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/impl/SuppressionCommentImpl.html#"><code>SuppressionCommentImpl</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/rule/impl/UnnecessaryPmdSuppressionRule.html#"><code>UnnecessaryPmdSuppressionRule</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/reporting/RuleContext.html#addViolationNoSuppress(net.sourceforge.pmd.reporting.Reportable,net.sourceforge.pmd.lang.ast.AstInfo,java.lang.String,java.lang.Object...)"><code>RuleContext#addViolationNoSuppress</code></a>
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.14.0-SNAPSHOT/net/sourceforge/pmd/reporting/ViolationSuppressor.SuppressionCommentWrapper.html#"><code>ViolationSuppressor.SuppressionCommentWrapper</code></a>
* pmd-java
  * <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.14.0-SNAPSHOT/net/sourceforge/pmd/lang/java/types/OverloadSelectionResult.html#getTypeToSearch()"><code>OverloadSelectionResult#getTypeToSearch</code></a>

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5599](https://github.com/pmd/pmd/pull/5599): \[java] Rewrite NPath complexity metric - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5609](https://github.com/pmd/pmd/pull/5609): \[core] Add rule to report unnecessary suppression comments/annotations - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5700](https://github.com/pmd/pmd/pull/5700): \[core] Don't accidentally catch unexpected runtime exceptions in CpdAnalysis - [Elliotte Rusty Harold](https://github.com/elharo) (@elharo)
* [#5716](https://github.com/pmd/pmd/pull/5716): Fix #5634: \[java] CommentDefaultAccessModifier: Comment between annotation and constructor not recognized - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5727](https://github.com/pmd/pmd/pull/5727): Fix #5621: \[java] Fix FPs with UnusedPrivateMethod - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5731](https://github.com/pmd/pmd/pull/5731): \[cli] Share more CLI options between CPD and PMD - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5736](https://github.com/pmd/pmd/pull/5736): Fix #5061: \[java] UnusedLocalVariable FP when using compound assignment - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5763](https://github.com/pmd/pmd/pull/5763): \[java] Support annotated constructor return type in symbol API - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5764](https://github.com/pmd/pmd/pull/5764): Fix #2462: \[java] LinguisticNaming should ignore setters for Builders  - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->



