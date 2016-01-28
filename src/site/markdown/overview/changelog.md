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
*   [#29](https://github.com/adangel/pmd/pull/29): Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#30](https://github.com/adangel/pmd/pull/30): Removed file filter for files that are explicitly specified on the CPD command line using the '--files' command line option.
*   [#31](https://github.com/adangel/pmd/pull/31): Added file encoding detection to CPD.
*   [#79](https://github.com/pmd/pmd/pull/79): do not flag public static void main(String[]) as UseVarargs; ignore @Override for UseVarargs
*   [#80](https://github.com/pmd/pmd/pull/80): Update mvn-plugin.md
*   [#83](https://github.com/pmd/pmd/pull/83): Adds new Code Climate-compliant JSON renderer

**Bugfixes:**

*   java-controversial/AvoidUsingShortType:
    *   [#1449](https://sourceforge.net/p/pmd/bugs/1449/): false positive when casting a variable to short
*   java-design/AccessorClassGeneration:
    *   [#1452](https://sourceforge.net/p/pmd/bugs/1452/): ArrayIndexOutOfBoundsException with Annotations for AccessorClassGenerationRule
*   java-junit/TestClassWithoutTestCases:
    *   [#1453](https://sourceforge.net/p/pmd/bugs/1453/): Test Class Without Test Cases gives false positive
*   General
    *   [#1455](https://sourceforge.net/p/pmd/bugs/1455/): PMD doesn't handle Java 8 explicit receiver parameters

**API Changes:**

**CLI Changes:**

*   CPD: If a complete filename is specified, the language dependent filename filter is not applied. This allows
    to scan files, that are not using the standard file extension. If a directory is specified, the filename filter
    is still applied and only those files with the correct file extension of the language are scanned.
