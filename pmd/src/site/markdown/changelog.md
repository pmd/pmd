# Changelog

## ???? ??, 2014 - 5.2.0:

**Bugfixes:**

* Fixed [bug 1170]: false positive with switch in loop
* Fixed [bug 1171]: Specifying minimum priority from command line gives NPE
* Fixed [bug 1173]: Java 8 support: method references
* Fixed [bug 1175]: false positive for StringBuilder.append called 2 consecutive times
* Fixed [bug 1176]: ShortVariable false positive with for-each loops
* Fixed [bug 1178]: LexicalError while parsing Java code aborts CPD run

[bug 1170]: https://sourceforge.net/p/pmd/bugs/1170/
[bug 1171]: https://sourceforge.net/p/pmd/bugs/1171/
[bug 1173]: https://sourceforge.net/p/pmd/bugs/1173/
[bug 1175]: https://sourceforge.net/p/pmd/bugs/1175/
[bug 1176]: https://sourceforge.net/p/pmd/bugs/1176/
[bug 1178]: https://sourceforge.net/p/pmd/bugs/1178/

**CPD Changes:**
- Command Line
    - Added option "--skip-lexical-errors" to skip files, which can't be tokenized
      due to invalid characters. See also [bug 1178].

[bug 1178]: https://sourceforge.net/p/pmd/bugs/1178/
