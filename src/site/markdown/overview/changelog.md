# PMD Release Notes

## ????? - 5.4.4-SNAPSHOT

The PMD team is pleased to announce PMD 5.4.4

This is a bug fixing release. The most significant changes are on analysis performance.

Multithread performance has been enhanced by reducing thread-contention on a
bunch of areas. This is still an area of work, as the speedup of running
multithreaded analysis is still relatively small (4 threads produce less
than a 50% speedup). Future releases will keep improving on this area.

Once again, *Symbol Table* has been an area of great performance improvements.
This time we were able to further improve it's performance by roughly 10% on all
supported languages. In *Java* in particular, several more improvements were possible,
improving *Symbol Table* performance by a whooping 30%, that's over 5X faster
than PMD 5.4.2, when we first started working on it.

Java developers will also appreciate the revamp of `CloneMethodMustImplementCloneable`,
making it over 500X faster, and `PreserveStackTrace` which is now 7X faster.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

This is a bug fixing release, no major changes were introduced.

#### Modified Rules

The Java rule "UseLocaleWithCaseConversions" (ruleset java-design) has been modified, to detect calls
to `toLowerCase` and to `toUpperCase` also within method call chains. This leads to more detected cases
and potentially new false positives.
See also [bugfix #1556](https://sourceforge.net/p/pmd/bugs/1556/).


### Fixed Issues

*   java
    *   [#206](https://github.com/pmd/pmd/issues/206): \[java] Parse error on annotation fields with generics
    *   [#207](https://github.com/pmd/pmd/issues/207): \[java] Parse error on method reference with generics
    *   [#208](https://github.com/pmd/pmd/issues/208): \[java] Parse error with local class with 2 or more annotations
    *   [#213](https://github.com/pmd/pmd/issues/213): \[java] CPD: OutOfMemory when analyzing Lucene
*   java-design
    *   [#1448](https://sourceforge.net/p/pmd/bugs/1448/): \[java] ImmutableField: Private field in inner class gives false positive with lambdas
    *   [#1495](https://sourceforge.net/p/pmd/bugs/1495/): \[java] UnnecessaryLocalBeforeReturn with assert
    *   [#1552](https://sourceforge.net/p/pmd/bugs/1552/): \[java] MissingBreakInSwitch - False positive for continue
    *   [#1556](https://sourceforge.net/p/pmd/bugs/1556/): \[java] UseLocaleWithCaseConversions does not works with `ResultSet` (false negative)
    *   [#177](https://github.com/pmd/pmd/issues/177): \[java] SingularField with lambdas as final fields
*   java-imports
    *   [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
    *   [#1547](https://sourceforge.net/p/pmd/bugs/1547/): \[java] UnusedImportRule - False Positive for only usage in Javadoc - {@link ClassName#CONSTANT}
    *   [#1555](https://sourceforge.net/p/pmd/bugs/1555/): \[java] UnnecessaryFullyQualifiedName: Really necessary fully qualified name
*   java-unnecessary
    *   [#199](https://github.com/pmd/pmd/issues/199): \[java] UselessParentheses: Parentheses in return statement are incorrectly reported as useless
*   java-strings
    *   [#202](https://github.com/pmd/pmd/issues/202): \[java] \[doc] ConsecutiveAppendsShouldReuse is not really an optimization
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name
*   psql
    *   [#1549](https://sourceforge.net/p/pmd/bugs/1549/): \[plsql] Parse error for IS [NOT] NULL construct
*   javascript
    *   [#201](https://github.com/pmd/pmd/issues/201): \[javascript] template strings are not correctly parsed
*   General
    *   [#1511](https://sourceforge.net/p/pmd/bugs/1511/): \[core] Inconsistent behavior of Rule.start/Rule.end


### External Contributions

*   [#129](https://github.com/pmd/pmd/pull/129): \[plsql] Added correct parse of IS [NOT] NULL and multiline DML
*   [#152](https://github.com/pmd/pmd/pull/152): \[java] fixes #1552 continue does not require break
*   [#154](https://github.com/pmd/pmd/pull/154): \[java] Fix #1547: UnusedImports: Adjust regex to support underscores
*   [#170](https://github.com/pmd/pmd/pull/170): \[core] Ant Task Formatter encoding issue with XMLRenderer
*   [#200](https://github.com/pmd/pmd/pull/200): \[javascript] Templatestring grammar fix

