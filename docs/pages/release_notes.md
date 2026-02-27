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

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}
