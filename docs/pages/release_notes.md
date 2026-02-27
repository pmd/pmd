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
* This release fixes a stored XSS vulnerability in VBHTMLRenderer and YAHTMLRenderer via unescaped violation messages.  
  Affects CI/CD pipelines that run PMD with `--format vbhtml` or `--format yahtml` on untrusted source code
  (e.g. pull requests from external contributors) and expose the HTML report as a build artifact.
  JavaScript executes in the browser context of anyone who opens the report.  
  Note: The default `html` format is **not affected** by unescaped violation messages, but a similar problem
  existed with suppressed violation markers.  
  If you use these reports, it is recommended to upgrade PMD.  
  Reported by [Smaran Chand](https://github.com/smaranchand) (@smaranchand).

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule {% rule java/codestyle/UnnecessaryInterfaceDeclaration %} detects classes that
  implement interfaces that are already implemented by its superclass, and interfaces
  that extend other interfaces already declared by their superinterfaces.  
  These declarations are redundant and can be removed to simplify the code.

#### Changed Rules
* The rule {% rule java/errorprone/CloseResource %} introduces a new property, `allowedResourceMethodPatterns`,
  which lets you specify method invocation patterns whose return values are resources managed externally.
  This is useful for ignoring managed resources - for example, `Reader`/`Writer` instances obtained from
  `HttpServletRequest`/`HttpServletResponse` - because the servlet container, not application code,
  is responsible for closing them. By default, the rule ignores `InputStream`/`OutputStream`/`Reader`/`Writer`
  resources returned by methods on `(Http)ServletRequest` and `(Http)ServletResponse`
  (both `javax.servlet` and `jakarta.servlet`).

### 🐛️ Fixed Issues
* core
  * [#6471](https://github.com/pmd/pmd/issues/6471): \[core] BaseAntlrTerminalNode should return type instead of index for getTokenKind()
  * [#6475](https://github.com/pmd/pmd/issues/6475): \[core] Fix stored XSS in VBHTMLRenderer and YAHTMLRenderer
* doc
  * [#6396](https://github.com/pmd/pmd/pull/6396): \[doc] Mention test-pmd-tool as alternative for testing
* java-bestpractices
  * [#6431](https://github.com/pmd/pmd/issues/6431): \[java] UnitTestShouldIncludeAssert: False positive with SoftAssertionsExtension on parent/grandparent classes
* java-codestyle
  * [#6458](https://github.com/pmd/pmd/pull/6458): \[java] New Rule: UnnecessaryInterfaceDeclaration
* java-errorprone
  * [#5787](https://github.com/pmd/pmd/issues/5787): \[java] InvalidLogMessageFormat: False positive with lombok @<!-- -->Value generated methods
  * [#6436](https://github.com/pmd/pmd/issues/6436): \[java] CloseResource: Allow to ignore managed resources

### 🚨️ API Changes

#### Deprecations
* core
  * {%jdoc core::renderers.CodeClimateIssue %}: This class is an implementation detail of
    {%jdoc core::renderers.CodeClimateRenderer %}. It will be internalized in a future release.
* visualforce
  * {%jdoc visualforce::lang.visualforce.DataType %}. The enum constants have been renamed to follow Java naming
    conventions. The old enum constants are deprecated and should no longer be used.  
    The method {%jdoc !!visualforce::lang.visualforce.DataType#fromString(java.lang.String) %} will return the new
    enum constants.  
    Use {%jdoc !!visualforce::lang.visualforce.DataType#fieldTypeNameOf() %} to get the original field type name.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6396](https://github.com/pmd/pmd/pull/6396): \[doc] Mention test-pmd-tool as alternative for testing - [Beech Horn](https://github.com/metalshark) (@metalshark)
* [#6397](https://github.com/pmd/pmd/pull/6397): \[java] Add support for Lombok-generated getters in symbol resolution - [Anurag Agarwal](https://github.com/altaiezior) (@altaiezior)
* [#6420](https://github.com/pmd/pmd/pull/6420): \[ci] build: Add typos as spell checker - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6432](https://github.com/pmd/pmd/pull/6432): \[java] UnitTestShouldIncludeAssert: False positive with SoftAssertionsExtension on parent/grandparent classes - [Artur Kalimullin](https://github.com/kaliy) (@kaliy)
* [#6434](https://github.com/pmd/pmd/pull/6434): \[java] chore(style): Fix lambda argument indentation for checkstyle compliance - [Kai](https://github.com/aclfe) (@aclfe)
* [#6437](https://github.com/pmd/pmd/pull/6437): \[java] CloseResource: Allow to ignore managed resources - [Gildas Cuisinier](https://github.com/gcuisinier) (@gcuisinier)
* [#6445](https://github.com/pmd/pmd/pull/6445): chore: Fix FieldNamingConventions - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6446](https://github.com/pmd/pmd/pull/6446): \[doc] Add new IntelliJ Plugin "PMD X" - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6447](https://github.com/pmd/pmd/pull/6447): chore: Small release process fixes - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6458](https://github.com/pmd/pmd/pull/6458): \[java] New Rule: UnnecessaryInterfaceDeclaration - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6472](https://github.com/pmd/pmd/pull/6472): \[core] Fix BaseAntlrTerminalNode getTokenKind to return type instead of index - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6475](https://github.com/pmd/pmd/pull/6475): \[core] Fix stored XSS in VBHTMLRenderer and YAHTMLRenderer - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6433](https://github.com/pmd/pmd/pull/6433): Bump PMD from 7.20.0 to 7.21.0
* [#6438](https://github.com/pmd/pmd/pull/6438): chore(deps): bump actions/cache from 5.0.2 to 5.0.3
* [#6439](https://github.com/pmd/pmd/pull/6439): chore(deps): bump ruby/setup-ruby from 1.286.0 to 1.288.0
* [#6440](https://github.com/pmd/pmd/pull/6440): chore(deps): bump scalameta.version from 4.14.6 to 4.14.7
* [#6441](https://github.com/pmd/pmd/pull/6441): chore(deps): bump org.apache.maven.plugins:maven-compiler-plugin from 3.14.1 to 3.15.0
* [#6442](https://github.com/pmd/pmd/pull/6442): chore(deps): bump org.checkerframework:checker-qual from 3.53.0 to 3.53.1
* [#6443](https://github.com/pmd/pmd/pull/6443): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.0.0 to 13.1.0
* [#6444](https://github.com/pmd/pmd/pull/6444): chore(deps): bump com.google.protobuf:protobuf-java from 4.33.4 to 4.33.5
* [#6452](https://github.com/pmd/pmd/pull/6452): chore(deps): bump actions/checkout from 6.0.1 to 6.0.2
* [#6455](https://github.com/pmd/pmd/pull/6455): chore(deps): bump org.apache.maven.plugins:maven-dependency-plugin from 3.9.0 to 3.10.0
* [#6456](https://github.com/pmd/pmd/pull/6456): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.1.0 to 13.2.0
* [#6462](https://github.com/pmd/pmd/pull/6462): chore(deps): bump junit.version from 6.0.2 to 6.0.3
* [#6463](https://github.com/pmd/pmd/pull/6463): chore(deps): bump scalameta.version from 4.14.7 to 4.15.2
* [#6465](https://github.com/pmd/pmd/pull/6465): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.4 to 1.18.5
* [#6468](https://github.com/pmd/pmd/pull/6468): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.4 to 1.18.5
* [#6469](https://github.com/pmd/pmd/pull/6469): chore(deps): bump surefire.version from 3.5.4 to 3.5.5
* [#6470](https://github.com/pmd/pmd/pull/6470): chore(deps): bump org.jetbrains:annotations from 26.0.2-1 to 26.1.0
* [#6473](https://github.com/pmd/pmd/pull/6473): chore(deps): bump nokogiri to 1.19.1
* [#6474](https://github.com/pmd/pmd/pull/6474): chore(deps): bump faraday from 2.13.3 to 2.14.1

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 66 commits
* 16 closed tickets & PRs
* Days since last release: 28

{% endtocmaker %}
