# Changelog

## ????, 2014 - 5.1.2:

**Bugfixes:**

* Fixed [bug #1181]: unused import false positive if used as parameter in javadoc only.
* Fixed [bug #1192]: Ecmascript fails to parse this operator " ^= "
* Fixed [bug #1206]: SummaryHTMLRenderer always shows suppressed warnings/violations
* Fixed [bug #1211]: PMD is failing with NPE for rule UseIndexOfChar while analyzing Jdk 8 Lambda expression
* Fixed [bug #1214]: UseCollectionIsEmpty misses some usage
* Fixed [bug #1215]: AvoidInstantiatingObjectsInLoops matches the right side of a list iteration loop
* Fixed [bug #1216]: AtLeastOneConstructor ignores classes with *any* methods
* Fixed [bug #1218]: TooFewBranchesForASwitchStatement misprioritized
* Fixed [bug #1219]: PrimarySuffix/@Image does not work in some cases in xpath 2.0
* Fixed [bug #1223]: UnusedPrivateMethod: Java 8 method reference causing false positives

[bug #1181]: https://sourceforge.net/p/pmd/bugs/1181/
[bug #1192]: https://sourceforge.net/p/pmd/bugs/1192/
[bug #1206]: https://sourceforge.net/p/pmd/bugs/1206/
[bug #1211]: https://sourceforge.net/p/pmd/bugs/1211/
[bug #1214]: https://sourceforge.net/p/pmd/bugs/1214/
[bug #1215]: https://sourceforge.net/p/pmd/bugs/1215/
[bug #1216]: https://sourceforge.net/p/pmd/bugs/1216/
[bug #1218]: https://sourceforge.net/p/pmd/bugs/1218/
[bug #1219]: https://sourceforge.net/p/pmd/bugs/1219/
[bug #1223]: https://sourceforge.net/p/pmd/bugs/1223/

**Feature Requests and Improvements:**

* [#1213]: AvoidLiteralsInIfCondition -- switch for integer comparison with 0
* [#1217]: SystemPrintln always says "System.out.print is used"
* [#1221]: OneDeclarationPerLine really checks for one declaration each statement

[#1213]: https://sourceforge.net/p/pmd/bugs/1213/
[#1217]: https://sourceforge.net/p/pmd/bugs/1217/
[#1221]: https://sourceforge.net/p/pmd/bugs/1221/


**Pull requests:**

* [#41](https://github.com/pmd/pmd/pull/41): Update to use asm 5.0.2
* [#42](https://github.com/pmd/pmd/pull/42): Add SLF4j Logger type to MoreThanOneLogger rule
* [#43](https://github.com/pmd/pmd/pull/43): Standard and modified cyclomatic complexity

**New Rules:**

* Java - codesize ruleset:
    * StdCyclomaticComplexity: Like CyclomaticComplexityRule, but not including boolean operators
    * ModifiedCyclomaticComplexity: Like StdCyclomaticComplexity, but switch statement plus all cases count as 1
    * Thanks to Alan Hohn
