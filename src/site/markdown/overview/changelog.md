# Changelog

## December 3, 2014 - 5.2.2:

**New Parameters for CPD:**

For the language cpp, the following new parameters are supported:

* `--no-skip-blocks`: Disables skipping of code blocks like a pre-processor. This is by default enabled.
* `--skip-blocks-pattern`: Pattern to find the blocks to skip. Start and End pattern separated by "`|`". Default value is "`#if 0|#endif`".

**Bugfixes:**

* [#1090](https://sourceforge.net/p/pmd/bugs/1090/): cpp parser exception with inline asm
* [#1128](https://sourceforge.net/p/pmd/bugs/1128/): CompareObjectsWithEquals False Positive comparing boolean (primitive) values
* [#1254](https://sourceforge.net/p/pmd/bugs/1254/): CPD run that worked in 5.1.2 fails in 5.1.3 with OOM
* [#1276](https://sourceforge.net/p/pmd/bugs/1276/): False positive in UnusedPrivateMethod with inner enum
* [#1280](https://sourceforge.net/p/pmd/bugs/1280/): False Positive in UnusedImports when import used in javadoc
* [#1281](https://sourceforge.net/p/pmd/bugs/1281/): UnusedPrivateMethod incorrectly flagged for methods nested private classes
* [#1282](https://sourceforge.net/p/pmd/bugs/1282/): False Positive with implicit String.valuesOf() (Java)
* [#1285](https://sourceforge.net/p/pmd/bugs/1285/): Prevent to modify the System environment
* [#1286](https://sourceforge.net/p/pmd/bugs/1286/): UnusedPrivateMethod returns false positives for varags and enums
