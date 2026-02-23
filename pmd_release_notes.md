


## 27-February-2026 - 7.22.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.22.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule [`UnnecessaryInterfaceDeclaration`](https://docs.pmd-code.org/pmd-doc-7.22.0-SNAPSHOT/pmd_rules_java_codestyle.html#unnecessaryinterfacedeclaration) detects classes that
  implement interfaces that are already implemented by its superclass, and interfaces
  that extend other interfaces already declared by their superinterfaces.  
  These declarations are redundant and can be removed to simplify the code.

#### Changed Rules
* The rule [`CloseResource`](https://docs.pmd-code.org/pmd-doc-7.22.0-SNAPSHOT/pmd_rules_java_errorprone.html#closeresource) introduces a new property, `allowedResourceMethodPatterns`,
  which lets you specify method invocation patterns whose return values are resources managed externally.
  This is useful for ignoring managed resources - for example, `Reader`/`Writer` instances obtained from
  `HttpServletRequest`/`HttpServletResponse` - because the servlet container, not application code,
  is responsible for closing them. By default, the rule ignores `InputStream`/`OutputStream`/`Reader`/`Writer`
  resources returned by methods on `(Http)ServletRequest` and `(Http)ServletResponse`
  (both `javax.servlet` and `jakarta.servlet`).

### 🐛️ Fixed Issues
* doc
  * [#6396](https://github.com/pmd/pmd/pull/6396): \[doc] Mention test-pmd-tool as alternative for testing
* java-bestpractices
  * [#6431](https://github.com/pmd/pmd/issues/6431): \[java] UnitTestShouldIncludeAssert: False positive with SoftAssertionsExtension on parent/grandparent classes
* java-codestyle
  * [#6458](https://github.com/pmd/pmd/pull/6458): \[java] New Rule: UnnecessaryInterfaceDeclaration
* java-errorprone
  * [#6436](https://github.com/pmd/pmd/issues/6436): \[java] CloseResource: Allow to ignore managed resources

### 🚨️ API Changes

#### Deprecations
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.22.0-SNAPSHOT/net/sourceforge/pmd/renderers/CodeClimateIssue.html#"><code>CodeClimateIssue</code></a>: This class is an implementation detail of
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.22.0-SNAPSHOT/net/sourceforge/pmd/renderers/CodeClimateRenderer.html#"><code>CodeClimateRenderer</code></a>. It will be internalized in a future release.
* visualforce
  * <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.22.0-SNAPSHOT/net/sourceforge/pmd/lang/visualforce/DataType.html#"><code>DataType</code></a>. The enum constants have been renamed to follow Java naming
    conventions. The old enum constants are deprecated and should no longer be used.  
    The method <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.22.0-SNAPSHOT/net/sourceforge/pmd/lang/visualforce/DataType.html#fromString(java.lang.String)"><code>DataType#fromString</code></a> will return the new
    enum constants.  
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/7.22.0-SNAPSHOT/net/sourceforge/pmd/lang/visualforce/DataType.html#fieldTypeNameOf()"><code>DataType#fieldTypeNameOf</code></a> to get the original field type name.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->


