# PMD Release Notes

## ????? - 5.7.0-SNAPSHOT

The PMD team is pleased to announce PMD 5.7.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

### Fixed Issues

### API Changes

*   The method `net.sourceforge.pmd.util.StringUtil#htmlEncode(String)` is deprecated.
    `org.apache.commons.lang3.StringEscapeUtils#escapeHtml4(String)` should be used instead.

### External Contributions

*   [#368](https://github.com/pmd/pmd/pull/368): \[vf] Adding proper AST support for negation expressions
*   [#372](https://github.com/pmd/pmd/pull/372): \[core] Fix XSS in HTML renderer
