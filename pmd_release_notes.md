


## 30-October-2026 - 7.29.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.29.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [Changed Rules](#changed-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### Changed Rules
*   The Java rule [`FinalFieldCouldBeStatic`](https://docs.pmd-code.org/pmd-doc-7.29.0-SNAPSHOT/pmd_rules_java_design.html#finalfieldcouldbestatic) no longer reports casts or conditional expressions
    whose constant classification previously depended on a boxed static final field. Direct static field references
    are still reported.
*   The Java rule [`UnconditionalIfStatement`](https://docs.pmd-code.org/pmd-doc-7.29.0-SNAPSHOT/pmd_rules_java_errorprone.html#unconditionalifstatement) now excludes final local boolean constants,
    consistently with its existing exclusion of named compile-time constants used for conditional compilation.
*   The Java rule [`UnusedNullCheckInEquals`](https://docs.pmd-code.org/pmd-doc-7.29.0-SNAPSHOT/pmd_rules_java_errorprone.html#unusednullcheckinequals) now recognizes final local String constants
    and unqualified instance String constants as non-null receivers, avoiding unnecessary reports.
*   The Java rule [`AvoidReassigningLoopVariables`](https://docs.pmd-code.org/pmd-doc-7.29.0-SNAPSHOT/pmd_rules_java_bestpractices.html#avoidreassigningloopvariables), with `forReassign=skip`, now accepts
    a final local constant equal to one as the increment of a conditional skip.
*   The Java rule [`UnusedAssignment`](https://docs.pmd-code.org/pmd-doc-7.29.0-SNAPSHOT/pmd_rules_java_bestpractices.html#unusedassignment) now recognizes final local boolean constants
    when analyzing short-circuit conditions, avoiding false positives caused by assignments that cannot execute.
*   The Java rule [`LiteralsFirstInComparisons`](https://docs.pmd-code.org/pmd-doc-7.29.0-SNAPSHOT/pmd_rules_java_bestpractices.html#literalsfirstincomparisons) now recognizes final local String constants
    and unqualified references to non-static final String constants. This may add violations when such a constant
    is the argument of a comparison, or remove them when it is already the receiver.

### 🐛️ Fixed Issues
* java
    * [#7060](https://github.com/pmd/pmd/issues/7060): \[java] getConstValue() returns null for constant expressions referencing final local variables
* java-bestpractices
    * [#5159](https://github.com/pmd/pmd/issues/5159): \[java] UnusedAssignment false positive when using assert
* java-design
    * [#4815](https://github.com/pmd/pmd/issues/4815): \[java] ExceptionAsFlowControl false-positive on Lambda/asynchronous (7.0.0-rc4)

### 🚨️ API Changes

*   Java constant folding now recognizes final primitive and String variables initialized with constant expressions,
    including local variables and unqualified instance fields. Numeric references are converted to their declared type.
    Boxed fields and field accesses qualified by expressions (such as `this.CONSTANT`) are not compile-time constants.
    These changes affect `ASTExpression.getConstValue()`, `isCompileTimeConstant()`, and the XPath attribute
    `@CompileTimeConstant`; custom Java and XPath rules relying on them may report different results.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



