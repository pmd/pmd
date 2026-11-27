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

### 🚀️ New and noteworthy
#### Security fixes
All known security vulnerabilities are described in full detail on the page
[PMD Security]({{ baseurl }}pmd_about_security.html). This release, the following
security vulnerabilities were fixed:
*   [GHSA-p8c4-pmh9-36vf](https://github.com/pmd/pmd/security/advisories/GHSA-p8c4-pmh9-36vf):
    Uncontrolled recursion (StackOverflowError) in regex matching of several PMD rules for crafted input.  
    Several rules (`UnnecessaryImport`, `AvoidUsingHardCodedIP`, `VfUnescapeEl`) used regular expressions
    that could throw a `StackOverflowError` on crafted, very long input (for example a Javadoc comment,
    string literal or attribute value of a few kilobytes), which aborted the whole analysis run. The patterns
    now match in linear time without deep recursion, so such input no longer causes an error.  
    Affected versions: `net.sourceforge.pmd:pmd-core` >= 7.0.0, <= 7.29.0;
    `net.sourceforge.pmd:pmd-java` >= 6.34.0, <= 7.29.0;
    `net.sourceforge.pmd:pmd-visualforce` >= 5.6.0, <= 7.29.0

#### Updated PMD Designer
This PMD release ships a new version of the pmd-designer.
PMD's binary distribution now ships ControlsFX 8 for Java 8 and ControlsFX 11 for OpenJFX and Java 11+.
Java 9 and 10 are not supported.  
For the changes, see [PMD Designer Changelog (7.30.0)](https://github.com/pmd/pmd-designer/releases/tag/7.30.0).

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule {% rule java/bestpractices/ControlFlowInTest %}
    reports control flow in test methods that can obscure which assertions
    are executed. It checks conditionals, switch statements and expressions,
    explicit throw statements, try statements with catch clauses,
    and plain try-finally statements.
    Loops are allowed by default and can be reported by setting
    `allowLoops` to `false`.

#### Changed Rules
* The Apex rule {% rule apex/bestpractices/ApexUnitTestShouldNotUseSeeAllDataTrue %} has a new property
    `reportConnectApi`. It defaults to `true`. Set it to `false`, and the rule does not report `seeAllData=true` on a
    test class or test method that calls a `ConnectApi` method, since most `ConnectApi` methods require `seeAllData=true`
    in tests. A call to a `setTest...` method does not stop the rule from reporting, because it only registers test data.
    Nor does a call to the regular method whose test data a `setTest...` call in the same test registers
    (see [#7236](https://github.com/pmd/pmd/issues/7236)).

#### Deprecated Rules
*   The Java rule {% rule java/performance/AddEmptyString %} is deprecated without replacement
    and will be removed with PMD 8.0.0. Its performance claim is not valid on modern JDKs:
    constant string concatenations are evaluated at compile time, and replacing concatenation
    with `toString()` or `String.valueOf()` does not generally improve performance.
    The rule is excluded when referencing the whole Performance category, but explicit rule
    references continue to work during the deprecation period.

### 🐛️ Fixed Issues
* apex
    * [#7108](https://github.com/pmd/pmd/issues/7108): \[apex] Expose DML AccessMode (`as user` / `as system`) for XPath rules
    * [#7182](https://github.com/pmd/pmd/issues/7182): \[apex] Expose the referenced type name on ClassRefExpression for XPath rules
* apex-bestpractices
    * [#5669](https://github.com/pmd/pmd/issues/5669): \[apex] ApexUnitTestMethodShouldHaveIsTestAnnotation: Violation range should only cover the method name
    * [#7236](https://github.com/pmd/pmd/issues/7236): \[apex] ApexUnitTestShouldNotUseSeeAllDataTrue: False positive for test classes that call ConnectApi
* apex-design
    * [#4863](https://github.com/pmd/pmd/issues/4863): \[apex] CyclomaticComplexity: False negative with switch statements
* apex-errorprone
    * [#5906](https://github.com/pmd/pmd/issues/5906): \[apex] MethodWithSameNameAsEnclosingClass: Violation range should only cover the method name
* core
    * [#5760](https://github.com/pmd/pmd/issues/5760): \[core] SarifRenderer does not respect relativizeRoots
* java
    * [#7144](https://github.com/pmd/pmd/issues/7144): \[java] Lambda block body that cannot complete normally is not treated as value-compatible
    * [#7240](https://github.com/pmd/pmd/issues/7240): \[java] "Not a Java binary name" when importing a class with non-ASCII (e.g. Japanese) letters in its name; the whole file is skipped 
    * [#7313](https://github.com/pmd/pmd/issues/7313): \[java] Casts to boolean and boolean equality are not folded as constant expressions
* java-bestpractices
    * [#5016](https://github.com/pmd/pmd/issues/5016): \[java] UnusedAssignment: False positive boolean flag for execution and reset in finally
    * [#7070](https://github.com/pmd/pmd/issues/7070): \[java] CheckResultSet: False negative caused by unrelated if/while
    * [#7116](https://github.com/pmd/pmd/issues/7116): \[java] New rule: ControlFlowInTest
    * [#7216](https://github.com/pmd/pmd/issues/7216): \[java] GuardLogStatement: False positive on SLF4J parameterised calls whose arguments are wrapped with logstash value(...)
* java-codestyle
    * [#6731](https://github.com/pmd/pmd/issues/6731): \[java] UnnecessaryFullyQualifiedName: False positive for lombok.Builder annotation
    * [#7215](https://github.com/pmd/pmd/issues/7215): \[java] UnnecessaryImport: False positive for on-demand imports with incomplete aux-classpath
    * [#7396](https://github.com/pmd/pmd/issues/7396): \[java] UnnecessaryImport: Fix StackOverflowError for very long Javadoc references
* java-design
    * [#5798](https://github.com/pmd/pmd/issues/5798): \[java] NPathComplexity: Short-circuit operators outside of conditions counted as branches
    * [#7277](https://github.com/pmd/pmd/issues/7277): \[java] AvoidRethrowingException: False negative if rethrow is wrapped in a block, ExceptionAsFlowControl false positive instead
    * [#7279](https://github.com/pmd/pmd/issues/7279): \[java] InternalApiUsage: False positive when static field is used in constructor
* java-errorprone
    * [#4531](https://github.com/pmd/pmd/issues/4531): \[java] MissingStaticMethodInNonInstantiatableClass: False positive with multiple top-level classes
    * [#7275](https://github.com/pmd/pmd/issues/7275): \[java] AvoidBranchingStatementAsLastInLoop: False negative when the statement is in a bare block
* java-multithreading
    * [#2786](https://github.com/pmd/pmd/issues/2786): \[java] UseConcurrentHashMap: False positive for ConcurrentSkipListMap
* java-performance
    * [#6327](https://github.com/pmd/pmd/issues/6327): \[java] AddEmptyString: Deprecate rule because performance claim is not valid anymore
    * [#7327](https://github.com/pmd/pmd/issues/7327): \[java] AvoidArrayLoops: False negative for a copy loop whose body has no braces
* jsp
    * [#5092](https://github.com/pmd/pmd/issues/5092): \[jsp] Parse error when `<!DOCTYPE>` or `<?xml?>` follows other content
* kotlin
    * [#7168](https://github.com/pmd/pmd/pull/7168): \[kotlin] Add @<!-- -->TypeName to VariableDeclaration, fix destructuring type leak
    * [#7171](https://github.com/pmd/pmd/issues/7171): \[kotlin] Support the ..< range operator (Kotlin 1.9)
* misc
    * [#6655](https://github.com/pmd/pmd/issues/6655): Deprecate ANTLR generated lexer implementation classes

### 🚨️ API Changes and Deprecations
* apex
    * DML statement nodes now expose `@AccessMode`.
      The attribute is always present. Its value is `IMPLICIT`, `USER_MODE`, or `SYSTEM_MODE`.
      `//DmlInsertStatement[@AccessMode='IMPLICIT']` and `//DmlInsertStatement[@AccessMode='SYSTEM_MODE']` select those statements.

#### Experimental API
* kotlin
    * {% jdoc kotlin::lang.kotlin.ast.KtVariableDeclarationAttributes %} now implements
      {% jdoc kotlin::lang.kotlin.ast.HasTypeName %}, so `VariableDeclaration` nodes have a `@TypeName` attribute.
      This covers property and for-loop variables, lambda parameters, and each component of a
      destructuring declaration (`val (a, b) = ...`, `for ((a, b) in ...)`, `{ (a, b) -> ... }`).
      The XPath functions `pmd-kotlin:typeIs`, `pmd-kotlin:typeIsExactly` and `pmd-kotlin:isNullable`
      support `VariableDeclaration` nodes as well.
    * A destructuring `PropertyDeclaration` (e.g. `val (a, b) = Pair(1, "x")`) no longer has a `@TypeName`.
      Before, it wrongly got the type of the first component. To find destructured variables
      by type, query the component nodes instead: `//VariableDeclaration[pmd-kotlin:typeIs('kotlin.Int')]`.

#### Deprecations
* apex
    * {% jdoc !!apex::lang.apex.ast.AbstractDmlStatement#getRunAsMode() %} has been deprecated. Use
      {% jdoc apex::lang.apex.ast.AbstractDmlStatement#getAccessMode() %} instead.
* coco
    * {% jdoc coco::lang.coco.ast.CocoLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* css
    * {% jdoc css::lang.css.ast.CssLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* csharp
    * {% jdoc cs::lang.cs.ast.CSharpLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* dart
    * {% jdoc dart::lang.dart.ast.DartLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* gherkin
    * {% jdoc gherkin::lang.gherkin.ast.GherkinLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* go
    * {% jdoc go::lang.go.ast.GolangLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* javascript
    * {% jdoc javascript::lang.typescript.ast.TypeScriptLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* julia
    * {% jdoc julia::lang.julia.ast.JuliaLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* kotlin
    * {% jdoc kotlin::lang.kotlin.ast.KotlinBaseListener %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * {% jdoc kotlin::lang.kotlin.ast.KotlinLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * {% jdoc kotlin::lang.kotlin.ast.KotlinListener %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* lua
    * {% jdoc lua::lang.lua.ast.LuaLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* rust
    * {% jdoc rust::lang.rust.ast.RustLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* swift
    * {% jdoc swift::lang.swift.ast.SwiftBaseListener %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * {% jdoc swift::lang.swift.ast.SwiftLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * {% jdoc swift::lang.swift.ast.SwiftListener %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* tsql
    * {% jdoc tsql::lang.tsql.ast.TSqlLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* xml
    * {% jdoc xml::lang.xml.ast.XMLLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* The generated `KotlinParser` and `SwiftParser` classes and their nested AST node classes are
  intentionally not deprecated yet. These classes still contain the public AST node types,
  which have no replacement API yet. They will be deprecated once we have decided how to handle
  the inner classes.

#### Other API changes
* xml
    * The new class {% jdoc xml::lang.xml.ast.XmlRootNode %} is the root node of the XML AST. It
      replaces the class `XmlParserImpl.RootXmlNode`, which was in an internal package.
      {% jdoc !!xml::lang.xml.ast.XmlNode#getRoot() %} now returns `XmlRootNode`.
    * {% jdoc xml::lang.xml.ast.XmlParser %} is now public and has moved from package
      `net.sourceforge.pmd.lang.xml` into `net.sourceforge.pmd.lang.xml.ast`, like the parsers of
      other languages.
    * The internal package `net.sourceforge.pmd.lang.xml.ast.internal` has been removed. Its classes
      are now package-private in `net.sourceforge.pmd.lang.xml.ast`.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#7168](https://github.com/pmd/pmd/pull/7168): \[kotlin] Add @<!-- -->TypeName to VariableDeclaration, fix destructuring type leak - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#7172](https://github.com/pmd/pmd/pull/7172): Fix #6655: Deprecate antlr4 generated lexers - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7175](https://github.com/pmd/pmd/pull/7175): \[core] Fix #5760: Honor relative paths in SARIF reports - [subotac](https://github.com/subotac) (@subotac)
* [#7176](https://github.com/pmd/pmd/pull/7176): \[apex] Fix #4863: Count switch cases in cyclomatic complexity - [subotac](https://github.com/subotac) (@subotac)
* [#7179](https://github.com/pmd/pmd/pull/7179): \[kotlin] Fix #7171: Support the ..< range operator (Kotlin 1.9) - [Andrew Hannah](https://github.com/ahannah9) (@ahannah9)
* [#7181](https://github.com/pmd/pmd/pull/7181): \[core] chore: Report unprocessed files in DeterministicOutputListenerWrapper - [Shivam Maurya](https://github.com/shivampwm2020) (@shivampwm2020)
* [#7192](https://github.com/pmd/pmd/pull/7192): \[java] Fix #7070: CheckResultSet under unrelated if and while conditions - [João Gabriel Alves Junior](https://github.com/Gabrieljr42) (@Gabrieljr42)
* [#7195](https://github.com/pmd/pmd/pull/7195): \[doc] Remove unnecessary scrollbars - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#7196](https://github.com/pmd/pmd/pull/7196): \[apex] Fix #7108: Expose DML AccessMode as XPath attribute - [João Gabriel Alves Junior](https://github.com/Gabrieljr42) (@Gabrieljr42)
* [#7198](https://github.com/pmd/pmd/pull/7198): \[java] Fix #6731: Support lombok.Builder (fix UnnecessaryFullyQualifiedName false positive) - [markxu](https://github.com/markjoin123) (@markjoin123)
* [#7201](https://github.com/pmd/pmd/pull/7201): \[apex] Fix #7182: Expose the referenced type name on ClassRefExpression - [Lyin-OTTF](https://github.com/Lyin-OTTF) (@Lyin-OTTF)
* [#7202](https://github.com/pmd/pmd/pull/7202): \[java] Fix #4531: MissingStaticMethodInNonInstantiatableClass false positive with multiple top-level classes - [markxu](https://github.com/markjoin123) (@markjoin123)
* [#7217](https://github.com/pmd/pmd/pull/7217): \[java] Fix #7215: Handle qualified types from unresolved on-demand imports - [Eymen Onar](https://github.com/Eymenonar) (@Eymenonar)
* [#7218](https://github.com/pmd/pmd/pull/7218): \[java] Fix #7144: Fix lambda block body compatibility - [Noor Ahmadi](https://github.com/noor-ahmadi) (@noor-ahmadi)
* [#7225](https://github.com/pmd/pmd/pull/7225): \[java] Fix #7216: GuardLogStatement: accept logstash structured arguments - [Prince Jain](https://github.com/princejain756) (@princejain756)
* [#7235](https://github.com/pmd/pmd/pull/7235): \[java] Fix #5016: UnusedAssignment: field may be read by a method call - [Vincent Potuček](https://github.com/Pankraz76) (@Pankraz76)
* [#7237](https://github.com/pmd/pmd/pull/7237): \[apex] Fix #7236: ApexUnitTestShouldNotUseSeeAllDataTrue: Add reportConnectApi property - [David Schach](https://github.com/dschach) (@dschach)
* [#7243](https://github.com/pmd/pmd/pull/7243): \[java] Lombok: don't double the `is` prefix of boolean getters - [Vincent Potuček](https://github.com/Pankraz76) (@Pankraz76)
* [#7244](https://github.com/pmd/pmd/pull/7244): \[core] Fix #7240: Accept non-ASCII letters in Java binary and package names - [JohnnyXu](https://github.com/xubenqiang) (@xubenqiang)
* [#7246](https://github.com/pmd/pmd/pull/7246): \[java] Fix #7116: Add ControlFlowInTest rule - [Michal Kollarčik](https://github.com/MichalKollarcik) (@MichalKollarcik)
* [#7252](https://github.com/pmd/pmd/pull/7252): \[jsp] Fix #5092: Allow XML declaration and doctype after other content - [Daksh Tyagi](https://github.com/InnoxCodes) (@InnoxCodes)
* [#7253](https://github.com/pmd/pmd/pull/7253): \[java] Fix #5798: NPath should not count && and || in plain value expressions - [Daksh Tyagi](https://github.com/InnoxCodes) (@InnoxCodes)
* [#7255](https://github.com/pmd/pmd/pull/7255): \[dist] Ship ControlsFX 8 and 11 for the designer - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7256](https://github.com/pmd/pmd/pull/7256): \[doc] Fix deleted assertion rule names in PMD 7 release notes - [Greg Oledzki](https://github.com/greg-at-moderne) (@greg-at-moderne)
* [#7257](https://github.com/pmd/pmd/pull/7257): \[cli] Refactor progress bar logic - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7258](https://github.com/pmd/pmd/pull/7258): \[doc] Remove pmd-commits mailing list from doc - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7263](https://github.com/pmd/pmd/pull/7263): \[apex] Fix #5669: Report ApexUnitTestMethodShouldHaveIsTestAnnotation only on the method name - [HawkForge](https://github.com/HawkForge) (@HawkForge)
* [#7264](https://github.com/pmd/pmd/pull/7264): \[apex] Fix #5906: MethodWithSameNameAsEnclosingClass range is too broad - [HawkForge](https://github.com/HawkForge) (@HawkForge)
* [#7280](https://github.com/pmd/pmd/pull/7280): \[java] Fix #7279: InternalApiUsage false positive in records and enums - [dodoshell](https://github.com/dodoshell) (@dodoshell)
* [#7283](https://github.com/pmd/pmd/pull/7283): \[java] Fix #7275: Handle bare blocks in AvoidBranchingStatementAsLastInLoop - [kayokoi Zhang](https://github.com/wozhenbang2004) (@wozhenbang2004)
* [#7286](https://github.com/pmd/pmd/pull/7286): \[doc] Add commercial support section - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7287](https://github.com/pmd/pmd/pull/7287): \[java] Fix #7277: Recognize rethrows through bare blocks - [João Gabriel Alves Junior](https://github.com/Gabrieljr42) (@Gabrieljr42)
* [#7301](https://github.com/pmd/pmd/pull/7301): \[java] Prepare deprecation of asCtx in pmd-java (part of #4814) - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7303](https://github.com/pmd/pmd/pull/7303): \[core] Remove FileUtil.java - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7311](https://github.com/pmd/pmd/pull/7311): \[java] Fix #6237: Deprecate AddEmptyString without replacement - [Amrit Kang](https://github.com/amritkang165) (@amritkang165)
* [#7314](https://github.com/pmd/pmd/pull/7314): \[java] Fix #7313: Fold casts to boolean and boolean equality - [sary](https://github.com/saryn17) (@saryn17)
* [#7334](https://github.com/pmd/pmd/pull/7334): \[doc] Fix dead links in the docs and README - [Mark Santos](https://github.com/markksantos) (@markksantos)
* [#7344](https://github.com/pmd/pmd/pull/7344): \[cli] Fix hanging PmdCliTest under Windows with git bash - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7351](https://github.com/pmd/pmd/pull/7351): \[xml] refactor: move internal AST classes into package lang.xml.ast - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7368](https://github.com/pmd/pmd/pull/7368): chore: Store mvnw.cmd with CRLF as-is - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7369](https://github.com/pmd/pmd/pull/7369): \[doc] Improve issue templates using forms - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7370](https://github.com/pmd/pmd/pull/7370): \[ci] Add PR automation workflows - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7371](https://github.com/pmd/pmd/pull/7371): \[ci] Add workflow to clean up stale pull request reports - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7375](https://github.com/pmd/pmd/pull/7375): \[java] Fix #2786: Exempt ConcurrentMap implementations from UseConcurrentHashMap - [kayokoi Zhang](https://github.com/wozhenbang2004) (@wozhenbang2004)
* [#7396](https://github.com/pmd/pmd/pull/7396): \[java] UnnecessaryImport: Fix StackOverflowError for very long Javadoc references - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#7265](https://github.com/pmd/pmd/pull/7265): Bump PMD from 7.28.0 to 7.29.0
* [#7288](https://github.com/pmd/pmd/pull/7288): chore(deps): bump ruby/setup-ruby from 1.335.0 to 1.336.0
* [#7289](https://github.com/pmd/pmd/pull/7289): chore(deps): bump org.jacoco:jacoco-maven-plugin from 0.8.15 to 0.8.16
* [#7290](https://github.com/pmd/pmd/pull/7290): chore(deps): bump csv from 3.3.6 to 3.3.7 in /docs
* [#7291](https://github.com/pmd/pmd/pull/7291): chore(deps): bump org.apache.maven.plugins:maven-enforcer-plugin from 3.6.3 to 3.7.0
* [#7292](https://github.com/pmd/pmd/pull/7292): chore(deps-dev): bump all-contributors-cli from 6.26.1 to 7.0.0
* [#7293](https://github.com/pmd/pmd/pull/7293): chore(deps): bump com.puppycrawl.tools:checkstyle from 14.3.0 to 14.4.0
* [#7316](https://github.com/pmd/pmd/pull/7316): chore(deps): bump actions/setup-java from 6.1.0 to 6.2.0
* [#7317](https://github.com/pmd/pmd/pull/7317): chore(deps): bump ruby/setup-ruby from 1.336.0 to 1.338.0
* [#7318](https://github.com/pmd/pmd/pull/7318): chore(deps): bump byte.buddy.version from 1.18.16 to 1.18.17
* [#7319](https://github.com/pmd/pmd/pull/7319): chore(deps): bump crate-ci/typos from 1.51.1 to 1.52.0
* [#7320](https://github.com/pmd/pmd/pull/7320): chore(deps-dev): bump com.google.guava:guava from 33.7.2-jre to 33.8.0-jre
* [#7321](https://github.com/pmd/pmd/pull/7321): chore(deps-dev): bump net.logstash.logback:logstash-logback-encoder from 8.1 to 9.0
* [#7322](https://github.com/pmd/pmd/pull/7322): chore(deps-dev): bump org.codehaus.mojo:versions-maven-plugin from 2.23.0 to 2.24.0
* [#7324](https://github.com/pmd/pmd/pull/7324): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.26.5 to 0.27.0
* [#7325](https://github.com/pmd/pmd/pull/7325): chore(deps): bump org.codehaus.mojo:build-helper-maven-plugin from 3.6.2 to 3.7.0
* [#7326](https://github.com/pmd/pmd/pull/7326): chore(deps): bump org.jsoup:jsoup from 1.23.2 to 1.24.1
* [#7350](https://github.com/pmd/pmd/pull/7350): chore(deps): bump addressable from 2.9.0 to 2.9.1 in /docs
* [#7352](https://github.com/pmd/pmd/pull/7352): chore(deps): bump nokogiri from 1.19.4 to 1.19.5 in /.ci/files
* [#7356](https://github.com/pmd/pmd/pull/7356): chore(deps): bump actions/setup-java from 6.2.0 to 6.3.0
* [#7357](https://github.com/pmd/pmd/pull/7357): chore(deps): bump org.codehaus.mojo:build-helper-maven-plugin from 3.7.0 to 3.8.0
* [#7358](https://github.com/pmd/pmd/pull/7358): chore(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.6.4 to 3.7.0
* [#7359](https://github.com/pmd/pmd/pull/7359): chore(deps): bump org.apache.maven:apache-maven from 3.10.0 to 3.10.1
* [#7360](https://github.com/pmd/pmd/pull/7360): chore(deps): bump org.apache.commons:commons-lang3 from 3.21.0 to 3.22.0
* [#7361](https://github.com/pmd/pmd/pull/7361): chore(deps-dev): bump all-contributors-cli from 7.0.0 to 7.0.2
* [#7362](https://github.com/pmd/pmd/pull/7362): chore(deps-dev): bump org.codehaus.mojo:versions-maven-plugin from 2.24.0 to 2.25.2
* [#7363](https://github.com/pmd/pmd/pull/7363): chore(deps-dev): bump org.apache.maven.plugins:maven-help-plugin from 3.5.2 to 3.6.0
* [#7364](https://github.com/pmd/pmd/pull/7364): chore(deps): bump crate-ci/typos from 1.52.0 to 1.52.2
* [#7385](https://github.com/pmd/pmd/pull/7385): chore(deps): bump actions/create-github-app-token from 3.3.0 to 3.4.0
* [#7386](https://github.com/pmd/pmd/pull/7386): chore(deps): bump ruby/setup-ruby from 1.338.0 to 1.343.0
* [#7387](https://github.com/pmd/pmd/pull/7387): chore(deps): bump org.apache.maven:apache-maven from 3.10.1 to 3.10.2
* [#7389](https://github.com/pmd/pmd/pull/7389): chore(deps-dev): bump all-contributors-cli from 7.0.2 to 7.0.3
* [#7390](https://github.com/pmd/pmd/pull/7390): chore(deps-dev): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.8.0.7211 to 5.9.0.7382

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 84 commits
* 73 closed tickets & PRs
* Days since last release: 27

{% endtocmaker %}
