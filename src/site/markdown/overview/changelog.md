# Changelog

## ???? - 5.2.3:

**Feature Requests and Improvements:**

* [#1288](https://sourceforge.net/p/pmd/bugs/1288/): MethodNamingConventions for native should be deactivated
* [#1293](https://sourceforge.net/p/pmd/bugs/1293/): Disable VariableNamingConventions for native methods

**New/Modified Rules:**

* [Java / Design / UseVarargs](http://pmd.sourceforge.net/pmd-java/rules/java/design.html#UseVarargs): if `byte[]` is used as the last argument, it is ignored and no violation will be reported.
* [Java / Naming / MethodNamingConventions](http://pmd.sourceforge.net/pmd-java/rules/java/naming.html#MethodNamingConventions): New property `checkNativeMethods`
* [Java / Naming / VariableNamingConventions](http://pmd.sourceforge.net/pmd-java/rules/java/naming.html#VariableNamingConventions): New property `checkNativeMethodParameters`

**Pull requests:**

* [#45](https://github.com/pmd/pmd/pull/45): #1290 RuleSetReferenceId does not process HTTP(S) correctly.
* [#46](https://github.com/pmd/pmd/pull/46): Allow byte[] as no-vargars last argument
* [#47](https://github.com/pmd/pmd/pull/47): Allow byte[] data and byte data[] as no-varargs last argument

**Bugfixes:**

* [#1252](https://sourceforge.net/p/pmd/bugs/1252/): net.sourceforge.pmd.lang.ast.TokenMgrError: Lexical error in file xxx.cpp
* [#1290](https://sourceforge.net/p/pmd/bugs/1290/): RuleSetReferenceId does not process HTTP(S) correctly.
* [#1294](https://sourceforge.net/p/pmd/bugs/1294/): False positive UnusedPrivateMethod with public inner enum from another class
