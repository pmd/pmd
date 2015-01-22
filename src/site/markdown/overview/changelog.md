# Changelog

## ???? - 5.3.0

**New Supported Languages:**

* Matlab (CPD)
* Objective-C (CPD)
* Python (CPD)
* Scala (CPD)

**Feature Requests and Improvements:**

* XML: Line numbers for XML documents are more accurate. This is a further improvement of [#1054](https://sourceforge.net/p/pmd/bugs/1054/).

**New/Modified Rules:**

**Pull Requests:**

* [#11](https://github.com/adangel/pmd/pull/11): Added support for Python to CPD.
* [#12](https://github.com/adangel/pmd/pull/12): Added support for Matlab to CPD.
* [#13](https://github.com/adangel/pmd/pull/13): Added support for Objective-C to CPD.
* [#14](https://github.com/adangel/pmd/pull/14): Added support for Scala to CPD.
* [#15](https://github.com/adangel/pmd/pull/15): (pmd-cs) Fixed incorrect line numbers after mutiline comments and verbatim strings.
* [#16](https://github.com/adangel/pmd/pull/16): Fixed several C++ lexical / tokenize errors.
* [#17](https://github.com/adangel/pmd/pull/17): Fixed '--files' command line option of CPD, so it also works for files and not only for directories.

**Bugfixes:**

* [#914](https://sourceforge.net/p/pmd/bugs/914/): False +ve from UnusedImports with wildcard static imports
* [#1296](https://sourceforge.net/p/pmd/bugs/1296/): PMD UnusedPrivateMethod invalid detection of 'private void method(int,boolean,Integer...)'
* [#1298](https://sourceforge.net/p/pmd/bugs/1298/): Member variable int type with value 0xff000000 causes processing error
* [#1299](https://sourceforge.net/p/pmd/bugs/1299/): MethodReturnsInternalArray false positive
* [#1306](https://sourceforge.net/p/pmd/bugs/1306/): False positive on duplicate when using static imports
