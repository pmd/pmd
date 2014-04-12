# Changelog

## ???? ??, 2014 - 5.2.0:

**Bugfixes:**

* Fixed [bug 1166]: PLSQL XPath Rules Fail for XPath 1.0
* Fixed [bug 1168]: Designer errors when trying to copy xml to clipboard
* Fixed [bug 1170]: false positive with switch in loop
* Fixed [bug 1171]: Specifying minimum priority from command line gives NPE
* Fixed [bug 1173]: Java 8 support: method references
* Fixed [bug 1175]: false positive for StringBuilder.append called 2 consecutive times
* Fixed [bug 1176]: ShortVariable false positive with for-each loops
* Fixed [bug 1177]: Incorrect StringBuffer warning when that class is not used
* Fixed [bug 1178]: LexicalError while parsing Java code aborts CPD run
* Fixed [bug 1180]: False Positive for ConsecutiveAppendsShouldReuse on different variable names
* Document that PMD requires Java 1.6, see [discussion].
* [Pull request 38]: Some fixes for AbstractCommentRule

[bug 1166]: https://sourceforge.net/p/pmd/bugs/1166/
[bug 1168]: https://sourceforge.net/p/pmd/bugs/1168/
[bug 1170]: https://sourceforge.net/p/pmd/bugs/1170/
[bug 1171]: https://sourceforge.net/p/pmd/bugs/1171/
[bug 1173]: https://sourceforge.net/p/pmd/bugs/1173/
[bug 1175]: https://sourceforge.net/p/pmd/bugs/1175/
[bug 1176]: https://sourceforge.net/p/pmd/bugs/1176/
[bug 1177]: https://sourceforge.net/p/pmd/bugs/1177/
[bug 1178]: https://sourceforge.net/p/pmd/bugs/1178/
[bug 1180]: https://sourceforge.net/p/pmd/bugs/1180/
[discussion]: https://sourceforge.net/p/pmd/discussion/188192/thread/6e86840c/
[Pull request 38]: https://github.com/pmd/pmd/pull/38

**CPD Changes:**
- Command Line
    - Added option "--skip-lexical-errors" to skip files, which can't be tokenized
      due to invalid characters instead of aborting CPD. See also [bug 1178].
- Ant
    - New optional parameter "skipDuplicateFiles": Ignore multiple copies of files of the same name and length in
      comparison; defaults to "false".
      This was already a command line option, but now also available in in CPD's ant task.
    - New optional parameter "skipLexicalErros": Skip files which can't be tokenized due to invalid characters
      instead of aborting CPD; defaults to "false".

[bug 1178]: https://sourceforge.net/p/pmd/bugs/1178/
