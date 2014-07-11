# Changelog

## ????, 2014 - 5.1.2:

**Bugfixes:**

* Fixed [bug #1181]: unused import false positive if used as parameter in javadoc only.
* Fixed [bug #1192]: Ecmascript fails to parse this operator " ^= "
* Fixed [bug #1206]: SummaryHTMLRenderer always shows suppressed warnings/violations
* Fixed [bug #1219]: PrimarySuffix/@Image does not work in some cases in xpath 2.0
* Fixed [bug #1221]: OneDeclarationPerLine really checks for one declaration each statement
* Fixed [bug #1223]: UnusedPrivateMethod: Java 8 method reference causing false positives

[bug #1181]: https://sourceforge.net/p/pmd/bugs/1181/
[bug #1192]: https://sourceforge.net/p/pmd/bugs/1192/
[bug #1206]: https://sourceforge.net/p/pmd/bugs/1206/
[bug #1219]: https://sourceforge.net/p/pmd/bugs/1219/
[bug #1221]: https://sourceforge.net/p/pmd/bugs/1221/
[bug #1223]: https://sourceforge.net/p/pmd/bugs/1223/

**Pull requests:**

* [#41](https://github.com/pmd/pmd/pull/41): Update to use asm 5.0.2
* [#42](https://github.com/pmd/pmd/pull/42): Add SLF4j Logger type to MoreThanOneLogger rule

