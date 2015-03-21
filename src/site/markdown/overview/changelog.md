# Changelog

## ???? - 5.3.0

**New Supported Languages:**

* Matlab (CPD)
* Objective-C (CPD)
* Python (CPD)
* Scala (CPD)

**Feature Requests and Improvements:**

*   XML: Line numbers for XML documents are more accurate. This is a further improvement of [#1054](https://sourceforge.net/p/pmd/bugs/1054/).
*   CPD: New output format 'csv_with_linecount_per_file'
*   [#1320](https://sourceforge.net/p/pmd/bugs/1320/): Enhance SimplifyBooleanReturns checks

**New/Modified/Deprecated Rules:**

The following rules have been
<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #5CB85C; font-size: 75%;">enhanced</span>
:

*   Language Java, ruleset design.xml: The rule "SimplifyBooleanReturns" now also marks methods where the else case is omitted and just a return.
    See also feature [#1320](https://sourceforge.net/p/pmd/bugs/1320/).

The following rules are marked as
<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">deprecated</span>
and will be removed with the next release of PMD.

*   Language Java, ruleset basic.xml: The following rules have been *moved into the `empty.xml` ruleset*. You'll need
    to enable the "empty" ruleset explicitly from now on, if you want to have these rules executed:

    EmptyCatchBlock, EmptyIfStatement, EmptyWhileStmt, EmptyTryBlock, EmptyFinallyBlock, EmptySwitchStatements,
    EmptySynchronizedBlock, EmptyStatementNotInLoop, EmptyInitializer, EmptyStatementBlock, EmptyStaticInitializer.

*   Language Java, ruleset basic.xml: The following rules have been *moved into the `unnecessary.xml` ruleset*. You'll need
    to enable the "unnecessary" ruleset explicitly from now on, if you want to have these rules executed:

    UnnecessaryConversionTemporary, UnnecessaryReturn, UnnecessaryFinalModifier, UselessOverridingMethod,
    UselessOperationOnImmutable, UnusedNullCheckInEquals, UselessParentheses.

*   Language Java, ruleset design.xml: The rule "UncommentedEmptyMethod" *has been renamed* to "UncommentedEmptyMethodBody".
    See also bug [#1283](https://sourceforge.net/p/pmd/bugs/1283/).

*   Language Java, ruleset controversial.xml: The rule "BooleanInversion" is deprecated and *will be removed* with
    the next release. See [#1277](https://sourceforge.net/p/pmd/bugs/1277/) for more details.

**Pull Requests:**

* [#11](https://github.com/adangel/pmd/pull/11): Added support for Python to CPD.
* [#12](https://github.com/adangel/pmd/pull/12): Added support for Matlab to CPD.
* [#13](https://github.com/adangel/pmd/pull/13): Added support for Objective-C to CPD.
* [#14](https://github.com/adangel/pmd/pull/14): Added support for Scala to CPD.
* [#15](https://github.com/adangel/pmd/pull/15): (pmd-cs) Fixed incorrect line numbers after mutiline comments and verbatim strings.
* [#16](https://github.com/adangel/pmd/pull/16): Fixed several C++ lexical / tokenize errors.
* [#17](https://github.com/adangel/pmd/pull/17): Fixed '--files' command line option of CPD, so it also works for files and not only for directories.
* [#18](https://github.com/adangel/pmd/pull/18): Created extra CSV output format `csv_with_linecount_per_file` which outputs the correct line count per file.
* [#19](https://github.com/adangel/pmd/pull/19): Fixed exit status of PMD when error occurs
* [#48](https://github.com/pmd/pmd/pull/48): Handle NoClassDefFoundError along ClassNotFoundException
* [#49](https://github.com/pmd/pmd/pull/49): Fix some false positives in UnusedPrivateField
* [#50](https://github.com/pmd/pmd/pull/50): Add missing assertions in JUnitAssertionsShouldIncludeMessage test
* [#51](https://github.com/pmd/pmd/pull/51): [JUnit] Check assertion message present in assertEquals with delta
* [#52](https://github.com/pmd/pmd/pull/52): Improves JDK8 support for default methods and static methods in interfaces

**Bugfixes:**

* [#914](https://sourceforge.net/p/pmd/bugs/914/): False +ve from UnusedImports with wildcard static imports
* [#1277](https://sourceforge.net/p/pmd/bugs/1277/): Delete BooleanInversion as it makes no sense
* [#1283](https://sourceforge.net/p/pmd/bugs/1283/): Rename UncommentedEmptyMethod to UncommentedEmptyMethodBody
* [#1296](https://sourceforge.net/p/pmd/bugs/1296/): PMD UnusedPrivateMethod invalid detection of 'private void method(int,boolean,Integer...)'
* [#1298](https://sourceforge.net/p/pmd/bugs/1298/): Member variable int type with value 0xff000000 causes processing error
* [#1299](https://sourceforge.net/p/pmd/bugs/1299/): MethodReturnsInternalArray false positive
* [#1302](https://sourceforge.net/p/pmd/bugs/1302/): False Positive: UnusedPrivateField when accessed by inner class
* [#1303](https://sourceforge.net/p/pmd/bugs/1303/): OverrideBothEqualsAndHashcodeRule does not work on class implements resolvable interfaces
* [#1304](https://sourceforge.net/p/pmd/bugs/1304/): UseCollectionIsEmpty false positive comparing to 1
* [#1305](https://sourceforge.net/p/pmd/bugs/1305/): variable declaration inside switch causes ClassCastException
* [#1306](https://sourceforge.net/p/pmd/bugs/1306/): False positive on duplicate when using static imports
* [#1308](https://sourceforge.net/p/pmd/bugs/1308/): PMD runs endlessly on some generated files
* [#1312](https://sourceforge.net/p/pmd/bugs/1312/): Rule reference must not override rule name of referenced rule
* [#1313](https://sourceforge.net/p/pmd/bugs/1313/): Missing assertion message in assertEquals with delta not detected
* [#1316](https://sourceforge.net/p/pmd/bugs/1316/): Multi Rule Properties with delimiter not possible
* [#1317](https://sourceforge.net/p/pmd/bugs/1317/): RuntimeException when parsing class with multiple lambdas
* [#1319](https://sourceforge.net/p/pmd/bugs/1319/): PMD stops with NoClassDefFoundError (typeresolution)
* [#1321](https://sourceforge.net/p/pmd/bugs/1321/): CPD format XML fails with NullPointer

**API Changes:**

*   `net.sourceforge.pmd.cpd.Match.iterator()` now returns an iterator of the new type `net.sourceforge.pmd.cpd.Mark` instead
    of TokenEntry. A `Mark` contains all the informations about each single duplication, including the TokenEntry via `Mark.getToken()`.
    This Mark is useful for reporting the correct line count for each duplication. Previously only one line count was available.
    As for some languages CPD can be instructed to ignore comments, the line count could be different in the different files
    for the same duplication.
