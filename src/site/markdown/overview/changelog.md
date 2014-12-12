# Changelog

## ???? - 5.2.3:

**Feature Requests and Improvements:**

**New/Modified Rules:**

* [Java / Design / UseVarargs](http://pmd.sourceforge.net/pmd-java/rules/java/design.html#UseVarargs): if `byte[]` is used as the last argument, it is ignored and no violation will be reported.

**Pull requests:**

* [#45](https://github.com/pmd/pmd/pull/45): #1290 RuleSetReferenceId does not process HTTP(S) correctly.
* [#46](https://github.com/pmd/pmd/pull/46): Allow byte[] as no-vargars last argument

**Bugfixes:**

* [#1252](https://sourceforge.net/p/pmd/bugs/1252/): net.sourceforge.pmd.lang.ast.TokenMgrError: Lexical error in file xxx.cpp
* [#1290](https://sourceforge.net/p/pmd/bugs/1290/): RuleSetReferenceId does not process HTTP(S) correctly.
