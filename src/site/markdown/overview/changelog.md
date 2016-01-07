# Changelog

## ????? - 5.3.7-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

*   A JSON-renderer for PMD which is compatible with CodeClimate. See [PR#83](https://github.com/pmd/pmd/pull/83).

**New/Modified/Deprecated Rules:**

*   java-design/UseVargs: public static void main method is ignored now and so are methods, that are annotated
    with Override. See [PR#79](https://github.com/pmd/pmd/pull/79).

**Pull Requests:**

*   [#27](https://github.com/adangel/pmd/pull/27): Added support for Raw String Literals (C++11).
*   [#29)(https://github.com/adangel/pmd/pull/29): Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#79](https://github.com/pmd/pmd/pull/79): do not flag public static void main(String[]) as UseVarargs; ignore @Override for UseVarargs
*   [#80](https://github.com/pmd/pmd/pull/80): Update mvn-plugin.md
*   [#83](https://github.com/pmd/pmd/pull/83): Adds new Code Climate-compliant JSON renderer

**Bugfixes:**

**API Changes:**

