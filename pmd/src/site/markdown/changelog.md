# Changelog

## ????, 2014 - 5.1.3:

**Bugfixes:**

* [#1156](https://sourceforge.net/p/pmd/bugs/1156/): False failure with "Avoid unused private methods"
* [#1187](https://sourceforge.net/p/pmd/bugs/1187/): double variable with AvoidDecimalLiteralsInBigDecimalConstructor
* [#1228](https://sourceforge.net/p/pmd/bugs/1228/): UnusedPrivateMethod returns false positives
* [#1230](https://sourceforge.net/p/pmd/bugs/1230/): UseCollectionIsEmpty gets false positives
* [#1231](https://sourceforge.net/p/pmd/bugs/1231/): No Error Message on Missing Rule
* [#1233](https://sourceforge.net/p/pmd/bugs/1233/): UnusedPrivateMethod: False positive : method called on returned object. 
* [#1234](https://sourceforge.net/p/pmd/bugs/1234/): Unused private methods still giving false positives in 5.1.3 snapshot
* [#1235](https://sourceforge.net/p/pmd/bugs/1235/): scope dependencies in POM file
* [#1239](https://sourceforge.net/p/pmd/bugs/1239/): StackOverflowError in AbstractTokenizer.parseString running CPD on >1MB JS file
* [#1241](https://sourceforge.net/p/pmd/bugs/1241/): False+ AvoidProtectedMethodInFinalClassNotExtending
* [#1243](https://sourceforge.net/p/pmd/bugs/1243/): Useless Parentheses False Positive
* [#1245](https://sourceforge.net/p/pmd/bugs/1245/): False Positive for Law of Demeter
* [#1247](https://sourceforge.net/p/pmd/bugs/1247/): Not able to recognize JDK 8 Static Method References

**Feature Requests and Improvements:**

* [#1232](https://sourceforge.net/p/pmd/bugs/1232/): Make ShortClassName configurable
* [#1244](https://sourceforge.net/p/pmd/bugs/1244/): FieldDeclarationsShouldBeAtStartOfClass and anonymous classes

**Pull requests:**

**New/Modified Rules:**

* FieldDeclarationsShouldBeAtStartOfClass (ruleset java-design) has a new property called `ignoreAnonymousClassDeclarations`:
  Ignore Field Declarations, that are initialized with anonymous class declarations. This property is enabled by default.
  See [feature #1244](https://sourceforge.net/p/pmd/bugs/1244/).
* ShortClassName (ruleset java-naming) has a new property called `minimum`: Number of characters that are required
  as a minimum for a class name. By default, 5 characters are required - if the class name is shorter, a violation
  will be reported. See [feature #1232](https://sourceforge.net/p/pmd/bugs/1232/).
