


## 27-February-2026 - 7.22.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.22.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Deprecations](#deprecations)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

### 🐛️ Fixed Issues
* doc
  * [#6396](https://github.com/pmd/pmd/pull/6396): \[doc] Mention test-pmd-tool as alternative for testing

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



