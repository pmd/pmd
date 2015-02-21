# Changelog

## ???? - 5.3.0

**New Supported Languages:**

* Matlab (CPD)
* Objective-C (CPD)
* Python (CPD)
* Scala (CPD)

**Feature Requests and Improvements:**

* XML: Line numbers for XML documents are more accurate. This is a further improvement of [#1054](https://sourceforge.net/p/pmd/bugs/1054/).
* CPD: New output format 'csv_with_linecount_per_file'

**New/Modified Rules:**

**Pull Requests:**

* [#11](https://github.com/adangel/pmd/pull/11): Added support for Python to CPD.
* [#12](https://github.com/adangel/pmd/pull/12): Added support for Matlab to CPD.
* [#13](https://github.com/adangel/pmd/pull/13): Added support for Objective-C to CPD.
* [#14](https://github.com/adangel/pmd/pull/14): Added support for Scala to CPD.
* [#15](https://github.com/adangel/pmd/pull/15): (pmd-cs) Fixed incorrect line numbers after mutiline comments and verbatim strings.
* [#16](https://github.com/adangel/pmd/pull/16): Fixed several C++ lexical / tokenize errors.
* [#17](https://github.com/adangel/pmd/pull/17): Fixed '--files' command line option of CPD, so it also works for files and not only for directories.
* [#18](https://github.com/adangel/pmd/pull/18): Created extra CSV output format `csv_with_linecount_per_file` which outputs the correct line count per file.
* [#48](https://github.com/pmd/pmd/pull/48): Handle NoClassDefFoundError along ClassNotFoundException
* [#49](https://github.com/pmd/pmd/pull/49): Fix some false positives in UnusedPrivateField
* [#50](https://github.com/pmd/pmd/pull/50): Add missing assertions in JUnitAssertionsShouldIncludeMessage test
* [#51](https://github.com/pmd/pmd/pull/51): [JUnit] Check assertion message present in assertEquals with delta

**Bugfixes:**

* [#914](https://sourceforge.net/p/pmd/bugs/914/): False +ve from UnusedImports with wildcard static imports
* [#1296](https://sourceforge.net/p/pmd/bugs/1296/): PMD UnusedPrivateMethod invalid detection of 'private void method(int,boolean,Integer...)'
* [#1298](https://sourceforge.net/p/pmd/bugs/1298/): Member variable int type with value 0xff000000 causes processing error
* [#1299](https://sourceforge.net/p/pmd/bugs/1299/): MethodReturnsInternalArray false positive
* [#1303](https://sourceforge.net/p/pmd/bugs/1303/): OverrideBothEqualsAndHashcodeRule does not work on class implements resolvable interfaces
* [#1304](https://sourceforge.net/p/pmd/bugs/1304/): UseCollectionIsEmpty false positive comparing to 1
* [#1305](https://sourceforge.net/p/pmd/bugs/1305/): variable declaration inside switch causes ClassCastException
* [#1306](https://sourceforge.net/p/pmd/bugs/1306/): False positive on duplicate when using static imports
* [#1308](https://sourceforge.net/p/pmd/bugs/1308/): PMD runs endlessly on some generated files
* [#1312](https://sourceforge.net/p/pmd/bugs/1312/): Rule reference must not override rule name of referenced rule
* [#1313](https://sourceforge.net/p/pmd/bugs/1313/): Missing assertion message in assertEquals with delta not detected

**API Changes:**

* `net.sourceforge.pmd.cpd.Match.iterator()` now returns an iterator of the new type `net.sourceforge.pmd.cpd.Mark` instead
  of TokenEntry. A `Mark` contains all the informations about each single duplication, including the TokenEntry via `Mark.getToken()`.
  This Mark is useful for reporting the correct line count for each duplication. Previously only one line count was available.
  As for some languages CPD can be instructed to ignore comments, the line count could be different in the different files
  for the same duplication.
