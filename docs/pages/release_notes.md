---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% comment %}
This allows to use links e.g. [Basic CLI usage]({{ baseurl }}pmd_userdocs_installation.html) that work both
in the release notes on GitHub (as an absolute url) and on the rendered documentation page (as a relative url).
{% endcomment %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### PMD CLI now uses threaded execution by default

In the PMD CLI, the `--threads` (`-t`) option can now accept a thread
count given relative to the number of cores of the machine. For instance,
it is now possible to write `-t 1C` to spawn one thread per core, or `-t 0.5C`
to spawn one thread for every other core.

The thread count option now defaults to `1C`, meaning parallel execution
is used by default. You can disable this by using `-t 1`.

#### New Rule UnnecessaryWarningSuppression (experimental)

This new Java rule {% rule java/bestpractices/UnnecessaryWarningSuppression %} reports unused suppression
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
be analyzed. See [File collection options]({{ baseurl }}pmd_userdocs_cli_reference.html#file-collection-options)
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
  * {% jdoc !!core::lang.ast.AstInfo#getSuppressionComments() %}: Use {% jdoc core::lang.ast.AstInfo#getAllSuppressionComments() %}
    or {% jdoc core::lang.ast.AstInfo#getSuppressionComment(int) %}.
  * {% jdoc !!core::lang.ast.AstInfo#withSuppressMap() %}: Use {%jdoc core::lang.ast.AstInfo#withSuppressionComments(java.util.Collection) %}
  * {% jdoc !!core::lang.ast.impl.javacc.AbstractTokenManager#suppressMap %}: Don't use this map directly anymore. Instead,
    use {%jdoc core::lang.ast.impl.javacc.AbstractTokenManager#getSuppressionComments() %}.
  * {% jdoc !!core::lang.ast.impl.javacc.AbstractTokenManager#getSuppressMap() %}: Use
    {% jdoc core::lang.ast.impl.javacc.AbstractTokenManager#getSuppressionComments() %} instead.
* pmd-java
  * {% jdoc !!java::lang.java.ast.ASTCompactConstructorDeclaration#getDeclarationNode() %}: This method just returns `this` and isn't useful.
  * {% jdoc !!java::lang.java.metrics.JavaMetrics#NPATH %}: Use {% jdoc java::lang.java.metrics.JavaMetrics#NPATH_COMP %}, which is available on more nodes,
    and uses Long instead of BigInteger.

#### Experimental
* core
  * {%jdoc core::lang.ast.impl.SuppressionCommentImpl %}
  * {%jdoc core::lang.rule.impl.UnnecessaryPmdSuppressionRule %}
  * {%jdoc !!core::reporting.RuleContext#addViolationNoSuppress(core::reporting.Reportable,core::lang.ast.AstInfo,java.lang.String,java.lang.Object...) %}
  * {%jdoc core::reporting.ViolationSuppressor.SuppressionCommentWrapper %}
* pmd-java
  * {%jdoc !!java::lang.java.types.OverloadSelectionResult#getTypeToSearch() %}

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5584](https://github.com/pmd/pmd/pull/5584): \[ci] New workflow "Publish Snapshot" - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5599](https://github.com/pmd/pmd/pull/5599): \[java] Rewrite NPath complexity metric - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5609](https://github.com/pmd/pmd/pull/5609): \[core] Add rule to report unnecessary suppression comments/annotations - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5699](https://github.com/pmd/pmd/pull/5699): Fix #5702: \[java] First-class support for lombok @<!-- -->Slf4j  - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5700](https://github.com/pmd/pmd/pull/5700): \[core] Don't accidentally catch unexpected runtime exceptions in CpdAnalysis - [Elliotte Rusty Harold](https://github.com/elharo) (@elharo)
* [#5712](https://github.com/pmd/pmd/pull/5712): Fix #5711: \[java] UseArrayAsList - only consider List.add - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5715](https://github.com/pmd/pmd/pull/5715): Fix #5476: \[visualforce] Resolve data types of standard object fields - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5716](https://github.com/pmd/pmd/pull/5716): Fix #5634: \[java] CommentDefaultAccessModifier: Comment between annotation and constructor not recognized - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5726](https://github.com/pmd/pmd/pull/5726): Fix #5724: \[java] Implicit functional interface FP with sealed interface - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5727](https://github.com/pmd/pmd/pull/5727): Fix #5621: \[java] Fix FPs with UnusedPrivateMethod - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5728](https://github.com/pmd/pmd/pull/5728): \[ci] Improvements for "Publish Pull Requests" - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5730](https://github.com/pmd/pmd/pull/5730): \[ci] Refactor git-repo-sync - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5731](https://github.com/pmd/pmd/pull/5731): \[cli] Share more CLI options between CPD and PMD - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5736](https://github.com/pmd/pmd/pull/5736): Fix #5061: \[java] UnusedLocalVariable FP when using compound assignment - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5741](https://github.com/pmd/pmd/pull/5741): \[cli] Make CLI default to multithreaded - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5742](https://github.com/pmd/pmd/pull/5742): \[ci] publish-snapshot/old build: migrate to central portal - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5743](https://github.com/pmd/pmd/pull/5743): \[ci] Make build a reuseable workflow - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5744](https://github.com/pmd/pmd/pull/5744): Fix #5705: \[cli] Always determine PMD_HOME based on script location - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5748](https://github.com/pmd/pmd/pull/5748): \[core] Reformat SarifLog to comply to coding standards - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5763](https://github.com/pmd/pmd/pull/5763): \[java] Support annotated constructor return type in symbol API - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5764](https://github.com/pmd/pmd/pull/5764): Fix #2462: \[java] LinguisticNaming should ignore setters for Builders  - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5706](https://github.com/pmd/pmd/pull/5706): Bump PMD from 7.12.0 to 7.13.0
* [#5709](https://github.com/pmd/pmd/pull/5709): Bump com.google.code.gson:gson from 2.13.0 to 2.13.1
* [#5710](https://github.com/pmd/pmd/pull/5710): Bump com.puppycrawl.tools:checkstyle from 10.23.0 to 10.23.1
* [#5717](https://github.com/pmd/pmd/pull/5717): Bump scalameta.version from 4.13.4 to 4.13.5
* [#5718](https://github.com/pmd/pmd/pull/5718): Bump org.checkerframework:checker-qual from 3.49.2 to 3.49.3
* [#5719](https://github.com/pmd/pmd/pull/5719): Bump org.jsoup:jsoup from 1.19.1 to 1.20.1
* [#5751](https://github.com/pmd/pmd/pull/5751): Bump scalameta.version from 4.13.5 to 4.13.6
* [#5754](https://github.com/pmd/pmd/pull/5754): Bump com.google.protobuf:protobuf-java from 4.30.2 to 4.31.0
* [#5766](https://github.com/pmd/pmd/pull/5766): Bump io.github.git-commit-id:git-commit-id-maven-plugin from 9.0.1 to 9.0.2
* [#5767](https://github.com/pmd/pmd/pull/5767): Bump org.mockito:mockito-core from 5.17.0 to 5.18.0
* [#5768](https://github.com/pmd/pmd/pull/5768): Bump com.puppycrawl.tools:checkstyle from 10.23.1 to 10.24.0

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 165 commits
* 33 closed tickets & PRs
* Days since last release: 35

{% endtocmaker %}
