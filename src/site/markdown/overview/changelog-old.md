# Old Changelog

Previous versions of PMD can be downloaded here:
http://sourceforge.net/projects/pmd/files/pmd/


## 04-December-2015 - 5.3.6

**Feature Request and Improvements:**

*   CPD: New command line parameter `--ignore-usings`: Ignore using directives in C# when comparing text.

**Modified Rules:**

*   java-comments/CommentRequired: New property `serialVersionUIDCommentRequired` which controls the comment requirements
    for *serialVersionUID* fields. By default, no comment is required for this field.

**Pull Requests:**

*   [#25](https://github.com/adangel/pmd/pull/25): Added option to exclude C# using directives from CPD analysis
    *   Note: This also contains the fix from [#23](https://github.com/adangel/pmd/pull/23)
*   [#72](https://github.com/pmd/pmd/pull/72): Added capability in Java and JSP parser for tracking tokens.
*   [#75](https://github.com/pmd/pmd/pull/75): RuleSetFactory Performance Enhancement

**Bugfixes:**

*   java-comments/CommentRequired
    *   [#1434](https://sourceforge.net/p/pmd/bugs/1434/): CommentRequired raises violation on serialVersionUID field
*   java-design/UseNotifyAllInsteadOfNotify
    *   [#1438](https://sourceforge.net/p/pmd/bugs/1438/): UseNotifyAllInsteadOfNotify gives false positive
*   java-finalizers/AvoidCallingFinalize
    *   [#1440](https://sourceforge.net/p/pmd/bugs/1440/): NPE in AvoidCallingFinalize
*   java-imports/UnnecessaryFullyQualifiedName
    *   [#1436](https://sourceforge.net/p/pmd/bugs/1436/): UnnecessaryFullyQualifiedName false positive on clashing static imports with enums
*   java-junit/JUnitAssertionsShouldIncludeMessage
    *   [#1373](https://sourceforge.net/p/pmd/bugs/1373/): JUnitAssertionsShouldIncludeMessage is no longer compatible with TestNG
*   java-migrating/JUnit4TestShouldUseBeforeAnnotation
    *   [#1446](https://sourceforge.net/p/pmd/bugs/1446/): False positive with JUnit4TestShouldUseBeforeAnnotation when TestNG is used
*   java-naming/SuspiciousEqualsMethodName
    *   [#1431](https://sourceforge.net/p/pmd/bugs/1431/): SuspiciousEqualsMethodName false positive
*   java-optimizations/RedundantFieldInitializer
    *   [#1443](https://sourceforge.net/p/pmd/bugs/1443/): RedundantFieldInitializer: False positive for small floats
*   java-unusedcode/UnusedPrivateField
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): False positive in UnusedPrivateField when local variable hides member variable
*   General
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): Java - Parse Error: Cast in return expression
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): Invalid XML Characters in Output
    *   [#1441](https://sourceforge.net/p/pmd/bugs/1441/): PMD: Update documentation how to compile after modularization


## 04-October-2015 - 5.3.5

**Modified Rules:**

*   java-design/CloseResource: New Property *closeAsDefaultTarget* which is *true* by default to stay
    backwards compatible. If this property is *true*, the rule will make sure, that `close` itself is
    always considered as a *closeTarget* - no matter whether it is configured with the *closeTargets* property
    or not.

**Pull Requests:**

*   [#71](https://github.com/pmd/pmd/pull/71): #1410 Improve description of DefaultPackage rule

**Bugfixes:**

*   java-controversial/DefaultPackage:
    *   [#1410](https://sourceforge.net/p/pmd/bugs/1410/): DefaultPackage triggers on field annotated with @VisibleForTesting
*   java-design/CloseResource:
    *   [#1387](https://sourceforge.net/p/pmd/bugs/1387/): CloseResource has false positive for ResultSet
*   java-optimizations/RedundantFieldInitializer
    *   [#1418](https://sourceforge.net/p/pmd/bugs/1418/): RedundantFieldInitializer false positive with large long value
*   java-strings/InsufficientStringBufferDeclaration:
    *   [#1409](https://sourceforge.net/p/pmd/bugs/1409/): NullPointerException in InsufficientStringBufferRule
    *   [#1413](https://sourceforge.net/p/pmd/bugs/1413/): False positive StringBuffer constructor with ?: int value
*   java-unnecessary/UselessParentheses:
    *   [#1407](https://sourceforge.net/p/pmd/bugs/1407/): UselessParentheses "&" and "+" operator precedence


## 18-September-2015 - 5.3.4

**Bugfixes:**

*   [#1370](https://sourceforge.net/p/pmd/bugs/1370/): ConsecutiveAppendsShouldReuse not detected properly on StringBuffer
*   [#1371](https://sourceforge.net/p/pmd/bugs/1371/): InsufficientStringBufferDeclaration not detected properly on StringBuffer
*   [#1380](https://sourceforge.net/p/pmd/bugs/1380/): InsufficientStringBufferDeclaration false positive when literal string passed to a lookup service
*   [#1384](https://sourceforge.net/p/pmd/bugs/1384/): NullPointerException in ConsecutiveLiteralAppendsRule
*   [#1388](https://sourceforge.net/p/pmd/bugs/1388/): ConstructorCallsOverridableMethodRule doesn't work with params?
*   [#1392](https://sourceforge.net/p/pmd/bugs/1392/): SimplifyStartsWith false-negative
*   [#1393](https://sourceforge.net/p/pmd/bugs/1393/): PMD hanging during DataflowAnomalyAnalysis
*   [#1394](https://sourceforge.net/p/pmd/bugs/1394/): dogfood.xml - Unable to exclude rules [UncommentedEmptyMethod]
*   [#1395](https://sourceforge.net/p/pmd/bugs/1395/): UnusedPrivateMethod false positive for array element method call
*   [#1396](https://sourceforge.net/p/pmd/bugs/1396/): PrematureDeclaration lambda false positive
*   [#1397](https://sourceforge.net/p/pmd/bugs/1397/): StringToString should ignore method references
*   [#1398](https://sourceforge.net/p/pmd/bugs/1398/): False positive for GuardLogStatementJavaUtil with Log4j
*   [#1399](https://sourceforge.net/p/pmd/bugs/1399/): False positive for VariableNamingConventions with annotation @interface
*   [#1400](https://sourceforge.net/p/pmd/bugs/1400/): False positive with JUnit4TestShouldUseBeforeAnnotation
*   [#1401](https://sourceforge.net/p/pmd/bugs/1401/): False positive for StringBuilder.append called with constructor
*   [#1402](https://sourceforge.net/p/pmd/bugs/1402/): Windows-Only: File exclusions are not case insensitive
*   [#1403](https://sourceforge.net/p/pmd/bugs/1403/): False positive UnusedPrivateMethod with JAVA8
*   [#1404](https://sourceforge.net/p/pmd/bugs/1404/): Java8 'Unnecessary use of fully qualified name' in Streams Collector
*   [#1405](https://sourceforge.net/p/pmd/bugs/1405/): UnusedPrivateMethod false positive?


## 25-July-2015 - 5.3.3

**Pull Requests:**

*   [#55](https://github.com/pmd/pmd/pull/55): Fix run.sh for paths with spaces

**Bugfixes:**

*   [#1364](https://sourceforge.net/p/pmd/bugs/1364/): FieldDeclarationsShouldBeAtStartOfClass false positive using multiple annotations
*   [#1365](https://sourceforge.net/p/pmd/bugs/1365/): Aggregated javadoc report is missing
*   [#1366](https://sourceforge.net/p/pmd/bugs/1366/): UselessParentheses false positive on multiple equality operators
*   [#1369](https://sourceforge.net/p/pmd/bugs/1369/): ConsecutiveLiteralAppends not detected properly on StringBuffer
*   [#1372](https://sourceforge.net/p/pmd/bugs/1372/): False Negative for CloseResource rule.
*   [#1375](https://sourceforge.net/p/pmd/bugs/1375/): CloseResource not detected properly
*   [#1376](https://sourceforge.net/p/pmd/bugs/1376/): CompareObjectsWithEquals fails for type annotated method parameter
*   [#1379](https://sourceforge.net/p/pmd/bugs/1379/): PMD CLI: Cannot specify multiple properties
*   [#1381](https://sourceforge.net/p/pmd/bugs/1381/): CPD Cannot use CSV/VS Renderers because they don't support encoding property


## 22-May-2015 - 5.3.2

**Bugfixes:**

*   [#1330](https://sourceforge.net/p/pmd/bugs/1330/): AvoidReassigningParameters does not work with varargs
*   [#1335](https://sourceforge.net/p/pmd/bugs/1335/): GuardLogStatementJavaUtil should not apply to SLF4J Logger
*   [#1342](https://sourceforge.net/p/pmd/bugs/1342/): UseConcurrentHashMap false positive (with documentation example)
*   [#1343](https://sourceforge.net/p/pmd/bugs/1343/): MethodNamingConventions for overrided methods
*   [#1345](https://sourceforge.net/p/pmd/bugs/1345/): UseCollectionIsEmpty throws NullPointerException
*   [#1353](https://sourceforge.net/p/pmd/bugs/1353/): False positive "Only One Return" with lambda
*   [#1354](https://sourceforge.net/p/pmd/bugs/1354/): Complex FieldDeclarationsShouldBeAtStartOfClass false positive with Spring annotations
*   [#1355](https://sourceforge.net/p/pmd/bugs/1355/): NullPointerException in a java file having a single comment line


## 20-April-2015 - 5.3.1

**New/Modified/Deprecated Rules:**

*   Language Java, ruleset design.xml: The rule "UseSingleton" *has been renamed* to "UseUtilityClass".
    See also bugs [#1059](https://sourceforge.net/p/pmd/bugs/1059) and [#1339](https://sourceforge.net/p/pmd/bugs/1339/).

**Pull Requests:**

*   [#53](https://github.com/pmd/pmd/pull/53): Fix some NullPointerExceptions

**Bugfixes:**

*   [#1332](https://sourceforge.net/p/pmd/bugs/1332/): False Positive: UnusedPrivateMethod
*   [#1333](https://sourceforge.net/p/pmd/bugs/1333/): Error while processing Java file with Lambda expressions
*   [#1337](https://sourceforge.net/p/pmd/bugs/1337/): False positive "Avoid throwing raw exception types" when exception is not thrown
*   [#1338](https://sourceforge.net/p/pmd/bugs/1338/): The pmd-java8 POM bears the wrong parent module version


## April 1, 2015 - 5.3.0

**New Supported Languages:**

* Matlab (CPD)
* Objective-C (CPD)
* Python (CPD)
* Scala (CPD)

**Feature Requests and Improvements:**

*   XML: Line numbers for XML documents are more accurate. This is a further improvement of [#1054](https://sourceforge.net/p/pmd/bugs/1054/).
*   CPD: New output format 'csv_with_linecount_per_file'
*   [#1320](https://sourceforge.net/p/pmd/bugs/1320/): Enhance SimplifyBooleanReturns checks
*   PMD exits with status `4` if any violations have been found. This behavior has been introduced to ease PMD
    integration into scripts or hooks, such as SVN hooks.

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
* [#1197](https://sourceforge.net/p/pmd/bugs/1197/): JUnit4TestShouldUseTestAnnotation for private method
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
* [#1307](https://sourceforge.net/p/pmd/bugs/1307/): False positive: SingularField and lambda-expression
* [#1308](https://sourceforge.net/p/pmd/bugs/1308/): PMD runs endlessly on some generated files
* [#1312](https://sourceforge.net/p/pmd/bugs/1312/): Rule reference must not override rule name of referenced rule
* [#1313](https://sourceforge.net/p/pmd/bugs/1313/): Missing assertion message in assertEquals with delta not detected
* [#1316](https://sourceforge.net/p/pmd/bugs/1316/): Multi Rule Properties with delimiter not possible
* [#1317](https://sourceforge.net/p/pmd/bugs/1317/): RuntimeException when parsing class with multiple lambdas
* [#1319](https://sourceforge.net/p/pmd/bugs/1319/): PMD stops with NoClassDefFoundError (typeresolution)
* [#1321](https://sourceforge.net/p/pmd/bugs/1321/): CPD format XML fails with NullPointer
* [#1322](https://sourceforge.net/p/pmd/bugs/1322/): MethodReturnsInternalArray on private methods
* [#1323](https://sourceforge.net/p/pmd/bugs/1323/): False positive case of UseAssertTrueInsteadOfAssertEquals
* [#1324](https://sourceforge.net/p/pmd/bugs/1324/): MethodReturnsInternalArray false positive with clone()
* [#1325](https://sourceforge.net/p/pmd/bugs/1325/): Inner class declared within a method fails to parse (ClassCastException)
* [#1326](https://sourceforge.net/p/pmd/bugs/1326/): PMD 5.3.0-SNAPSHOT doesn't compile under Windows

**API Changes:**

*   `net.sourceforge.pmd.cpd.Match.iterator()` now returns an iterator of the new type `net.sourceforge.pmd.cpd.Mark` instead
    of TokenEntry. A `Mark` contains all the informations about each single duplication, including the TokenEntry via `Mark.getToken()`.
    This Mark is useful for reporting the correct line count for each duplication. Previously only one line count was available.
    As for some languages CPD can be instructed to ignore comments, the line count could be different in the different files
    for the same duplication.

*   pmd-test: The utility class `StreamUtil` is deprecated. Just use Apache Commons IO Utils instead.


## December 21, 2014 - 5.2.3:

**Feature Requests and Improvements:**

* [#1288](https://sourceforge.net/p/pmd/bugs/1288/): MethodNamingConventions for native should be deactivated
* [#1293](https://sourceforge.net/p/pmd/bugs/1293/): Disable VariableNamingConventions for native methods

**Modified Rules:**

* [Java / Design / UseVarargs](http://pmd.sourceforge.net/pmd-java/rules/java/design.html#UseVarargs): if `byte[]` is used as the last argument, it is ignored and no violation will be reported.
* [Java / Naming / MethodNamingConventions](http://pmd.sourceforge.net/pmd-java/rules/java/naming.html#MethodNamingConventions): New property `checkNativeMethods`
* [Java / Naming / VariableNamingConventions](http://pmd.sourceforge.net/pmd-java/rules/java/naming.html#VariableNamingConventions): New property `checkNativeMethodParameters`

**Pull requests:**

* [#45](https://github.com/pmd/pmd/pull/45): #1290 RuleSetReferenceId does not process HTTP(S) correctly.
* [#46](https://github.com/pmd/pmd/pull/46): Allow byte[] as no-vargars last argument
* [#47](https://github.com/pmd/pmd/pull/47): Allow byte[] data and byte data[] as no-varargs last argument

**Bugfixes:**

* [#1252](https://sourceforge.net/p/pmd/bugs/1252/): net.sourceforge.pmd.lang.ast.TokenMgrError: Lexical error in file xxx.cpp
* [#1289](https://sourceforge.net/p/pmd/bugs/1289/): CommentRequired not ignored if javadoc {@inheritDoc} anon inner classes
* [#1290](https://sourceforge.net/p/pmd/bugs/1290/): RuleSetReferenceId does not process HTTP(S) correctly.
* [#1294](https://sourceforge.net/p/pmd/bugs/1294/): False positive UnusedPrivateMethod with public inner enum from another class


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


## November 3, 2014 - 5.2.1:

**Bugfixes:**

* [#550](https://sourceforge.net/p/pmd/bugs/550/): False +: MissingBreakInSwitch
* [#1252](https://sourceforge.net/p/pmd/bugs/1252/): net.sourceforge.pmd.lang.ast.TokenMgrError: Lexical error in file xxx.cpp
* [#1253](https://sourceforge.net/p/pmd/bugs/1253/): Document default behaviour when CPD command line arguments "encoding" and "ignoreAnnotations" are not specified
* [#1255](https://sourceforge.net/p/pmd/bugs/1255/): UseUtilityClass false positive with Exceptions
* [#1256](https://sourceforge.net/p/pmd/bugs/1256/): PositionLiteralsFirstInComparisons false positive with Characters
* [#1258](https://sourceforge.net/p/pmd/bugs/1258/): Java 8 Lambda parse error on direct field access
* [#1259](https://sourceforge.net/p/pmd/bugs/1259/): CloseResource rule ignores conditionnals within finally blocks
* [#1261](https://sourceforge.net/p/pmd/bugs/1261/): False positive "Avoid unused private methods" with Generics
* [#1262](https://sourceforge.net/p/pmd/bugs/1262/): False positive for MissingBreakInSwitch
* [#1263](https://sourceforge.net/p/pmd/bugs/1263/): PMD reports CheckResultSet violation in completely unrelated source files.
* [#1272](https://sourceforge.net/p/pmd/bugs/1272/): varargs in methods are causing IndexOutOfBoundException when trying to process files
* [#1273](https://sourceforge.net/p/pmd/bugs/1273/): CheckResultSet false positive in try-with-resources nested in if
* [#1274](https://sourceforge.net/p/pmd/bugs/1274/): ant integration broken with pmd-5.2.0
* [#1275](https://sourceforge.net/p/pmd/bugs/1275/): False positive: UnusedModifier rule for static inner class in enum


## October 17, 2014 - 5.2.0:

**Modularization of the source code:**

The source code of pmd was undergoing a major restructuring. Each language is separated
out into its own module. This reduces the size of the artifacts significantly, if only
one language is needed. It also makes it easier, to add new languages as extensions.

Therefore, the maven coordinates needed to change. In order to just use pmd with java support, you'll need
the following two dependencies:

    <dependency>
        <groupId>net.sourceforge.pmd</groupId>
        <artifactId>pmd-core</artifactId>
        <version>5.2.0</version>
    </dependency>
    <dependency>
        <groupId>net.sourceforge.pmd</groupId>
        <artifactId>pmd-java</artifactId>
        <version>5.2.0</version>
    </dependency>

The binary package still contains all languages and can be used as usual. Have a look at
[the central repository](http://search.maven.org/#search|ga|1|g%3Anet.sourceforge.pmd) for available modules.

**New Languages**

* CPD supports now [Go](https://golang.org/).

**Pull requests:**

* [#9](https://github.com/adangel/pmd/pull/9/): New rule: NoUnsanitizedJSPExpressionRule
* [#44](https://github.com/pmd/pmd/pull/44/): Add GoLang support to CPD

**New/Modified Rules:**

* JSP - Basic ruleset:
    * NoUnsanitizedJSPExpression: Using unsanitized JSP expression can lead to Cross Site Scripting (XSS) attacks


## August 31, 2014 - 5.1.3:

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
* [#1246](https://sourceforge.net/p/pmd/bugs/1246/): False positive from MissingBreakInSwitch
* [#1247](https://sourceforge.net/p/pmd/bugs/1247/): Not able to recognize JDK 8 Static Method References
* [#1249](https://sourceforge.net/p/pmd/bugs/1249/): Regression: UnusedPrivateMethod from 5.0.5 to 5.1.2
* [#1250](https://sourceforge.net/p/pmd/bugs/1250/): Method attribute missing for some violations
* [#1251](https://sourceforge.net/p/pmd/bugs/1251/): UnusedPrivateMethod false positives for boxing & unboxing arguments

**Feature Requests and Improvements:**

* [#1232](https://sourceforge.net/p/pmd/bugs/1232/): Make ShortClassName configurable
* [#1244](https://sourceforge.net/p/pmd/bugs/1244/): FieldDeclarationsShouldBeAtStartOfClass and anonymous classes

**New/Modified Rules:**

* FieldDeclarationsShouldBeAtStartOfClass (ruleset java-design) has a new property called `ignoreAnonymousClassDeclarations`:
  Ignore Field Declarations, that are initialized with anonymous class declarations. This property is enabled by default.
  See [feature #1244](https://sourceforge.net/p/pmd/bugs/1244/).
* ShortClassName (ruleset java-naming) has a new property called `minimum`: Number of characters that are required
  as a minimum for a class name. By default, 5 characters are required - if the class name is shorter, a violation
  will be reported. See [feature #1232](https://sourceforge.net/p/pmd/bugs/1232/).

## July 20, 2014 - 5.1.2:

**Bugfixes:**

* Fixed [bug #1181]: unused import false positive if used as parameter in javadoc only.
* Fixed [bug #1192]: Ecmascript fails to parse this operator " ^= "
* Fixed [bug #1198]: ConfusingTernary does not ignore else if blocks even when property is set
* Fixed [bug #1200]: setRuleSets method javadoc mistype commands instead commas
* Fixed [bug #1201]: Error "Can't find resource null" when ruleset contains spaces after comma
* Fixed [bug #1202]: StackOverflowError in RuleSetReferenceId
* Fixed [bug #1205]: Parse error on lambda with if
* Fixed [bug #1206]: SummaryHTMLRenderer always shows suppressed warnings/violations
* Fixed [bug #1208]: yahtml's outputDir property does not work
* Fixed [bug #1209]: XPath 2.0 following-sibling incorrectly includes context node
* Fixed [bug #1211]: PMD is failing with NPE for rule UseIndexOfChar while analyzing Jdk 8 Lambda expression
* Fixed [bug #1214]: UseCollectionIsEmpty misses some usage
* Fixed [bug #1215]: AvoidInstantiatingObjectsInLoops matches the right side of a list iteration loop
* Fixed [bug #1216]: AtLeastOneConstructor ignores classes with *any* methods
* Fixed [bug #1218]: TooFewBranchesForASwitchStatement misprioritized
* Fixed [bug #1219]: PrimarySuffix/@Image does not work in some cases in xpath 2.0
* Fixed [bug #1223]: UnusedPrivateMethod: Java 8 method reference causing false positives
* Fixed [bug #1224]: GuardDebugLogging broken in 5.1.1 - missing additive statement check in log statement
* Fixed [bug #1226]: False Positive: UnusedPrivateMethod overloading with varargs
* Fixed [bug #1227]: GuardLogStatementJavaUtil doesn't catch log(Level.FINE, "msg" + " msg") calls

[bug #1181]: https://sourceforge.net/p/pmd/bugs/1181/
[bug #1192]: https://sourceforge.net/p/pmd/bugs/1192/
[bug #1198]: https://sourceforge.net/p/pmd/bugs/1198/
[bug #1200]: https://sourceforge.net/p/pmd/bugs/1200/
[bug #1201]: https://sourceforge.net/p/pmd/bugs/1201/
[bug #1202]: https://sourceforge.net/p/pmd/bugs/1202/
[bug #1205]: https://sourceforge.net/p/pmd/bugs/1205/
[bug #1206]: https://sourceforge.net/p/pmd/bugs/1206/
[bug #1208]: https://sourceforge.net/p/pmd/bugs/1208/
[bug #1209]: https://sourceforge.net/p/pmd/bugs/1209/
[bug #1211]: https://sourceforge.net/p/pmd/bugs/1211/
[bug #1214]: https://sourceforge.net/p/pmd/bugs/1214/
[bug #1215]: https://sourceforge.net/p/pmd/bugs/1215/
[bug #1216]: https://sourceforge.net/p/pmd/bugs/1216/
[bug #1218]: https://sourceforge.net/p/pmd/bugs/1218/
[bug #1219]: https://sourceforge.net/p/pmd/bugs/1219/
[bug #1223]: https://sourceforge.net/p/pmd/bugs/1223/
[bug #1224]: https://sourceforge.net/p/pmd/bugs/1224/
[bug #1226]: https://sourceforge.net/p/pmd/bugs/1226/
[bug #1227]: https://sourceforge.net/p/pmd/bugs/1227/

**Feature Requests and Improvements:**

* [#1203]: Make GuardLogStatementJavaUtil configurable
* [#1213]: AvoidLiteralsInIfCondition -- switch for integer comparison with 0
* [#1217]: SystemPrintln always says "System.out.print is used"
* [#1221]: OneDeclarationPerLine really checks for one declaration each statement

[#1203]: https://sourceforge.net/p/pmd/bugs/1203/
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


## April 27, 2014 - 5.1.1:

**Bugfixes:**

* Fixed [bug 1165]: SimplifyConditional false positive
* Fixed [bug 1166]: PLSQL XPath Rules Fail for XPath 1.0
* Fixed [bug 1167]: Error while processing PLSQL file with BOM
* Fixed [bug 1168]: Designer errors when trying to copy xml to clipboard
* Fixed [bug 1170]: false positive with switch in loop
* Fixed [bug 1171]: Specifying minimum priority from command line gives NPE
* Fixed [bug 1173]: Java 8 support: method references
* Fixed [bug 1175]: false positive for StringBuilder.append called 2 consecutive times
* Fixed [bug 1176]: ShortVariable false positive with for-each loops
* Fixed [bug 1177]: Incorrect StringBuffer warning when that class is not used
* Fixed [bug 1178]: LexicalError while parsing Java code aborts CPD run
* Fixed [bug 1180]: False Positive for ConsecutiveAppendsShouldReuse on different variable names
* Fixed [bug 1185]: UnusedModifier throws NPE when parsing enum with a nested static interface
* Fixed [bug 1188]: False positive in UnusedPrivateField
* Fixed [bug 1191]: Ecmascript fails to parse "void(0)"
* Document that PMD requires Java 1.6, see [discussion].
* [Pull request 38]: Some fixes for AbstractCommentRule
* [Pull request 39]: Fixed NPE in ConsecutiveAppendsShouldReuseRule.getVariableAppended()
* [Pull request 40]: Added support for enums in CommentRequiredRule

[bug 1165]: https://sourceforge.net/p/pmd/bugs/1165/
[bug 1166]: https://sourceforge.net/p/pmd/bugs/1166/
[bug 1167]: https://sourceforge.net/p/pmd/bugs/1167/
[bug 1168]: https://sourceforge.net/p/pmd/bugs/1168/
[bug 1170]: https://sourceforge.net/p/pmd/bugs/1170/
[bug 1171]: https://sourceforge.net/p/pmd/bugs/1171/
[bug 1173]: https://sourceforge.net/p/pmd/bugs/1173/
[bug 1175]: https://sourceforge.net/p/pmd/bugs/1175/
[bug 1176]: https://sourceforge.net/p/pmd/bugs/1176/
[bug 1177]: https://sourceforge.net/p/pmd/bugs/1177/
[bug 1178]: https://sourceforge.net/p/pmd/bugs/1178/
[bug 1180]: https://sourceforge.net/p/pmd/bugs/1180/
[bug 1185]: https://sourceforge.net/p/pmd/bugs/1185/
[bug 1188]: https://sourceforge.net/p/pmd/bugs/1188/
[bug 1191]: https://sourceforge.net/p/pmd/bugs/1191/
[discussion]: https://sourceforge.net/p/pmd/discussion/188192/thread/6e86840c/
[Pull request 38]: https://github.com/pmd/pmd/pull/38
[Pull request 39]: https://github.com/pmd/pmd/pull/39
[Pull request 40]: https://github.com/pmd/pmd/pull/40

**CPD Changes:**

- Command Line
    - Added option "--skip-lexical-errors" to skip files, which can't be tokenized
      due to invalid characters instead of aborting CPD. See also [bug 1178].
- Ant
    - New optional parameter "skipDuplicateFiles": Ignore multiple copies of files of the same name and length in
      comparison; defaults to "false".
      This was already a command line option, but now also available in in CPD's ant task.
    - New optional parameter "skipLexicalErros": Skip files which can't be tokenized due to invalid characters
      instead of aborting CPD; defaults to "false".

[bug 1178]: https://sourceforge.net/p/pmd/bugs/1178/


## February 11, 2014 - 5.1.0:

**New/Updated Languages:**

- Java 1.8 support added.
- PLSQL support added; thanks to Stuart Turton. See also http://pldoc.sourceforge.net/
- Apache Velocity support added; thanks to Andrey Utis. See also http://velocity.apache.org

**New/Updated Rulesets and Rules:**

- EcmaScript
    - Controversial ruleset, featuring AvoidWithStatement
    - UseBaseWithParseInt
- Java
    - GuardLogStatement
        - replace xpath implementation of GuardDebugLogging by GuardLogStatement (better perf)
    - CommentRequired
        - basic rule to check for existence for formal (javadoc) comments.
    - AvoidProtectedMethodInFinalClassNotExtending
        - rule to avoid protected methods in final classes that don't extend anything other than Object.
    - ConsecutiveAppendsShouldReuse
        - rule to encourage to reuse StringBuilder.append returned object for consecutive calls.
    - PositionLiteralsFirstInCaseInsensitiveComparisons
        - rule similar to PositionLiteralsFirstInComparisons, but for case insensitive comparisons (equalsIgnoreCase).
          Thanks to Larry Diamond
    - ConfusingTernary
        - new property "ignoreElseIf" to suppress this rule in case of if-else-if-else usage.
          See [feature 1161]: Confusing Ternary should skip else if statements (or have a property to do so)
    - FieldDeclarationsShouldBeAtStartOfClass
        - new property "ignoreEnumDeclarations" which is enabled by default. This relaxes the rule, so
          that enums can be declared before fields and the rule is not triggered.

[feature 1161]: http://sourceforge.net/p/pmd/bugs/1161/


**Bugfixes:**

<http://sourceforge.net/p/pmd/bugs/milestone/PMD-5.1.0/>

* Fixed [bug  881]: private final without setter is flagged
* Fixed [bug 1059]: Change rule name "Use Singleton" should be "Use Utility class"
* Fixed [bug 1106]: PMD 5.0.4 fails with NPE on parsing java enum with inner class instance creation
* Fixed [bug 1045]: //NOPMD not working (or not implemented) with ECMAscript
* Fixed [bug 1054]: XML Rules ever report a line -1 and not the line/column where the error occurs
* Fixed [bug 1115]: commentRequiredRule in pmd 5.1 is not working properly
* Fixed [bug 1120]: equalsnull false positive
* Fixed [bug 1121]: NullPointerException when invoking XPathCLI
* Fixed [bug 1123]: failure in help examples
* Fixed [bug 1124]: PMD.run() multithreading issue
* Fixed [bug 1125]: Missing Static Method In Non Instantiatable Class
* Fixed [bug 1126]: False positive with FieldDeclarationsShouldBeAtStartOfClass for static enums
* Fixed [bug 1130]: CloseResource doesn't recognize custom close method
* Fixed [bug 1131]: CloseResource should complain if code betwen declaration of resource and try
* Fixed [bug 1134]: UseStringBufferLength: false positives
* Fixed [bug 1135]: CheckResultSet ignores results set declared outside of try/catch
* Fixed [bug 1136]: ECMAScript: NullPointerException in getLeft() and getRight()
* Fixed [bug 1140]: public EcmascriptNode getBody(int index)
* Fixed [bug 1141]: ECMAScript: getFinallyBlock() is buggy.
* Fixed [bug 1142]: ECMAScript: getCatchClause() is buggy.
* Fixed [bug 1144]: CPD encoding argument has no effect
* Fixed [bug 1146]: UseArrayListInsteadOfVector false positive when using own Vector class
* Fixed [bug 1147]: EmptyMethodInAbstractClassShouldBeAbstract false positives
* Fixed [bug 1150]: "EmptyExpression" for valid statements!
* Fixed [bug 1154]: Call super onPause when there is no super
* Fixed [bug 1155]: maven pmd plugin does not like empty rule sets
* Fixed [bug 1159]: false positive UnusedFormalParameter readObject(ObjectInputStream) if not used
* Fixed [bug 1164]: Violations are not suppressed with @java.lang.SuppressWarnings("all")

[bug  881]: https://sourceforge.net/p/pmd/bugs/881
[bug 1059]: https://sourceforge.net/p/pmd/bugs/1059
[bug 1045]: https://sourceforge.net/p/pmd/bugs/1045
[bug 1054]: https://sourceforge.net/p/pmd/bugs/1054
[bug 1106]: https://sourceforge.net/p/pmd/bugs/1106
[bug 1115]: https://sourceforge.net/p/pmd/bugs/1115
[bug 1120]: https://sourceforge.net/p/pmd/bugs/1120
[bug 1121]: https://sourceforge.net/p/pmd/bugs/1121
[bug 1123]: https://sourceforge.net/p/pmd/bugs/1123
[bug 1124]: https://sourceforge.net/p/pmd/bugs/1124
[bug 1125]: https://sourceforge.net/p/pmd/bugs/1125
[bug 1126]: https://sourceforge.net/p/pmd/bugs/1126
[bug 1130]: https://sourceforge.net/p/pmd/bugs/1130
[bug 1131]: https://sourceforge.net/p/pmd/bugs/1131
[bug 1134]: https://sourceforge.net/p/pmd/bugs/1134
[bug 1135]: https://sourceforge.net/p/pmd/bugs/1135
[bug 1136]: https://sourceforge.net/p/pmd/bugs/1136
[bug 1140]: https://sourceforge.net/p/pmd/bugs/1140
[bug 1141]: https://sourceforge.net/p/pmd/bugs/1141
[bug 1142]: https://sourceforge.net/p/pmd/bugs/1142
[bug 1144]: https://sourceforge.net/p/pmd/bugs/1144
[bug 1146]: https://sourceforge.net/p/pmd/bugs/1146
[bug 1147]: https://sourceforge.net/p/pmd/bugs/1147
[bug 1150]: https://sourceforge.net/p/pmd/bugs/1150
[bug 1154]: https://sourceforge.net/p/pmd/bugs/1154
[bug 1155]: https://sourceforge.net/p/pmd/bugs/1155
[bug 1159]: https://sourceforge.net/p/pmd/bugs/1159
[bug 1164]: https://sourceforge.net/p/pmd/bugs/1164



**CPD Changes:**
- Command Line
    - Added non-recursive option "--non-recursive" to not scan sub-directories
    - Added option "--exclude" to exclude specific files from being scanned (thanks to Delmas for patch #272)
- CPD is now thread-safe, so that multiple instances of CPD can run concurrently without stepping
    on each other (eg: multi-module Maven projects.). Thanks to David Golpira.

**Miscellaneous:**

- Upgrade to javacc 5.0 (see patch #1109 Patch to build with Javacc 5.0)
- DBURI as DataSource possible - directly scan plsql code stored within the database

**API Changes**

- Deprecated APIs:
    - net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionNode: getBody(int index) deprecated, use getBody() instead
    - net.sourceforge.pmd.lang.ecmascript.ast.ASTTryStatement: isCatch() and isFinally() deprecated, use hasCatch() and hasBody() instead
- Generalize Symbol Table treatement
    - Added net.sourceforge.pmd.lang.symboltable.ScopedNode
    - Added net.sourceforge.pmd.lang.symboltable.Scope
    - Added net.sourceforge.pmd.lang.symboltable.NameDeclaration
    - Added net.sourceforge.pmd.lang.symboltable.NameOccurrence
    - Added net.sourceforge.pmd.lang.symboltable.AbstractScope
    - Added net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration


## August 11, 2013 - 5.0.5:

    Fixed bug  991: AvoidSynchronizedAtMethodLevel for static methods
    Fixed bug 1084: NPE at UselessStringValueOfRule.java:36
    Fixed bug 1091: file extension for fortran seems to be wrong in cpdgui tools
    Fixed bug 1092: Wrong Attribute "excludemarker" in Ant Task Documentation
    Fixed bug 1095: AvoidFinalLocalVariable false positive
    Fixed bug 1099: UseArraysAsList false positives
    Fixed bug 1102: False positive: shift operator parenthesis
    Fixed bug 1104: IdempotentOperation false positive
    Fixed bug 1107: PMD 5.0.4 couldn't parse call of parent outer java class method from inner class
    Fixed bug 1069: Eclipse plugin does not accept project-local config
    Fixed bug 1111: False positive: Useless parentheses
    Fixed bug 1114: CPD - Tokenizer not initialized with requested properties
    Fixed bug 1118: ClassCastException in pmd.lang.ecmascript.ast.ASTElementGet


## May 1, 2013 - 5.0.4:

    Fixed bug  254: False+ : UnusedImport with Javadoc @throws
    Fixed bug  794: False positive on PreserveStackTrace with anonymous inner
    Fixed bug 1063: False+: ArrayIsStoredDirectly
    Fixed bug 1080: net.sourceforge.pmd.cpd.CPDTest test failing
    Fixed bug 1081: Regression: CPD skipping all files when using relative paths
    Fixed bug 1082: CPD performance issue on larger projects
    Fixed bug 1085: NullPointerException by at net.sourceforge.pmd.lang.java.rule.design.GodClassRule.visit(GodClassRule.java:313)
    Fixed bug 1086: Unsupported Element and Attribute in Ant Task Example
    Fixed bug 1087: PreserveStackTrace (still) ignores initCause()
    Fixed bug 1089: When changing priority in a custom ruleset, violations reported twice


## April 5, 2013 - 5.0.3:

    Fixed bug  938: False positive on LooseCoupling for overriding methods
    Fixed bug  940: False positive on UnsynchronizedStaticDateFormatter
    Fixed bug  942: CheckResultSet False Positive and Negative
    Fixed bug  943: PreserveStackTrace false positive if a StringBuffer exists
    Fixed bug  945: PMD generates RuleSets it cannot read.
    Fixed bug  958: Intermittent NullPointerException while loading XPath node attributes
    Fixed bug  968: Issues with JUnit4 @Test annotation with expected exception (Thanks to Yiannis Paschalidis)
    Fixed bug  975: false positive in ClassCastExceptionWithToArray
    Fixed bug  976: UselessStringValueOf wrong when appending character arrays
    Fixed bug  977: MisplacedNullCheck makes false positives
    Fixed bug  984: Cyclomatic complexity should treat constructors like methods
    Fixed bug  985: Suppressed methods shouldn't affect avg CyclomaticComplexity
    Fixed bug  992: Class java.beans.Statement triggered in CloseResource rule
    Fixed bug  997: Rule NonThreadSafeSingleton gives analysis problem
    Fixed bug  999: Law of Demeter: False positives and negatives
    Fixed bug 1002: False +: FinalFieldCouldBeStatic on inner class
    Fixed bug 1005: False + for ConstructorCallsOverridableMethod - overloaded methods
    Fixed bug 1027: PMD Ant: java.lang.ClassCastException
    Fixed bug 1032: ImmutableField Rule: Private field in inner class gives false positive
    Fixed bug 1064: Exception running PrematureDeclaration
    Fixed bug 1068: CPD fails on broken symbolic links
    Fixed bug 1073: Hard coded violation messages CommentSize
    Fixed bug 1074: rule priority doesn't work on group definitions
    Fixed bug 1076: Report.treeIterator() does not return all violations
    Fixed bug 1077: Missing JavaDocs for Xref-Test Files
    Fixed bug 1078: Package statement introduces false positive UnnecessaryFullyQualifiedName violation
    Merged pull request #14: fix Nullpointer Exception when using -l jsp



## February 3, 2013 - 5.0.2:

    Fixed bug  878: False positive: UnusedFormalParameter for abstract methods
    Fixed bug  913: SignatureDeclareThrowsException is raised twice
    Fixed bug  947: CloseResource rule fails if field is marked with annotation
    Fixed bug 1004: targetjdk isn't attribute of PMD task
    Fixed bug 1007: Parse Exception with annotation
    Fixed bug 1011: CloseResource Rule ignores Constructors
    Fixed bug 1012: False positive: Useless parentheses.
    Fixed bug 1020: Parsing Error
    Fixed bug 1026: PMD doesn't handle 'value =' in SuppressWarnings annotation
    Fixed bug 1028: False-positive: Compare objects with equals for Enums
    Fixed bug 1030: CPD Java.lang.IndexOutOfBoundsException: Index:
    Fixed bug 1037: Facing a showstopper issue in PMD Report Class (report listeners)
    Fixed bug 1039: pmd-nicerhtml.xsl is packaged in wrong location
    Fixed bug 1043: node.getEndLine() always returns 0 (ECMAscript)
    Fixed bug 1044: Unknown option: -excludemarker
    Fixed bug 1046: ant task CPDTask doesn't accept ecmascript
    Fixed bug 1047: False Positive in 'for' loops for LocalVariableCouldBeFinal in 5.0.1
    Fixed bug 1048: CommentContent Rule, String Index out of range Exception
    Fixed bug 1049: Errors in "How to write a rule"
    Fixed bug 1055: Please add a colon in the ant output after line,column for Oracle JDeveloper IDE usage
    Fixed bug 1056: "Error while processing" while running on xml file with DOCTYPE reference
    Fixed bug 1060: GodClassRule >>> wrong method



## November 28, 2012 - 5.0.1:

    Fixed bug  820: False+ AvoidReassigningParameters
    Fixed bug 1008: pmd-5.0.0: ImmutableField false positive on self-inc/dec
    Fixed bug 1009: pmd-5.0.0: False + UselessParentheses
    Fixed bug 1003: newline characters stripped from CPD data in PMD 5.0.0
    Fixed bug 1001: InsufficientStringBufferDeclaration fails to parse hex
    Fixed bug  522: InefficientStringBuffering bug false +
    Fixed bug  953: String.InefficientStringBuffering false +
    Fixed bug  981: Unable to parse
    Fixed bug 1010: pmd: parsing of generic method call with super fails
    Fixed bug  996: pmd-4.2.6: MissingBreakInSwitch fails to report violation
    Fixed bug  993: Invalid NPath calculation in return statement. Thanks to Prabhjot Singh for the patch.
    Fixed bug 1023: c/c++ \ as a continuation character not supported
    Fixed bug 1033: False+ : SingularField
    Fixed bug 1025: Regression of Crash in PMDTask due to multithreading (Eclipse and Java 1.5)
    Fixed bug 1017: Type resolution very slow for big project. Thanks to Roman for the patch.
    Fixed bug 1036: Documentation: default threshold values removed from v5.0
    Fixed bug 1035: UseObjectForClearerAPI has misspelled message
    Fixed bug 1031: false DontImportJavaLang
    Fixed bug 1034: UseConcurrentHashMap flags calls to methods that return Map
    Fixed bug 1006: Problem with implementation of getPackageNameImage method
    Fixed bug 1014: AvoidLiteralsInIfCondition must NOT consider null
    Fixed bug 1013: jnlp link for CPD is wrong
    
    PMD Command Line Changes:
      Improved command line interface (CLI) parsing using JCommander.
      Note: this breaks compatibility, but should be easy to fix.
      With "-d" you specify nowtThe source files / source directory to be scanned.
      With "-f" you select the report format (like text, html, ...)
      With "-R" you select the rulesets to be used.
      Example: pmd -d c:\data\pmd\pmd\test-data\Unused1.java -f xml -R rulesets/java/unusedcode.xml
    
    Improved JSP parser to be less strict with not valid XML documents (like HTML). Thanks to Victor Bucutea.
    Fixed bgastviewer not working. Thanks to Victor Bucutea.
    Improved CPD: Support in CPD for IgnoreAnnotations and SuppressWarnings("CPD-START"). Thanks to Matthew Short.
    Fixed C# support for CPD - thanks to TIOBE Software.
    
    New Ecmascript rules:
    
        Basic ruleset: AvoidTrailingComma


## May, 1, 2012 - 5.0.0:

    Fixed bug 3515487: Inconsistent reference to ruleset file in documentation
    Fixed bug 3470274: Using Label for lines in XMLRenderer
    Fixed bug 3175710: NPE in InsufficientStringBufferDeclaration
    
    CPD:
    - Exit with status code 4 when CPD detects code duplication (Patch ID: 3497021)

## January 31, 2012 - 5.0-alpha:

    This version of PMD breaks API compatibility with prior versions of PMD, as well
    as RuleSet XML compatibility. Also the maven coordinates (groupId) have been changed.
    The decision to break compatibility, allows PMD
    internals and code organization to be improved to better handle additional
    languages.  This opportunity was used to remove depreciated APIs, and beat up
    any code which has thumbed its nose at the developers over the years. ;)
    
    The following is relatively complete list of the major changes (this may not be
    100% accurate, see actual source code when in doubt):
    
    Fixed bug (no number) - Fixed UseStringBufferLengthRule only worked once per class
    All StringBuffer-related rules now also catch StringBuilder-related issues in the same way
    
        API Change - Unification of treatment of languages within PMD core:
           Added - net.sourceforge.pmd.lang.Language (now an 'enum')
           Added - net.sourceforge.pmd.lang.LanguageVersion
           Added - net.sourceforge.pmd.lang.LanguageVersionDiscoverer
           Added - net.sourceforge.pmd.lang.LanguageVersionHandler
           Added - net.sourceforge.pmd.lang.XPathHandler
           Added - net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler
           Added - net.sourceforge.pmd.lang.xpath.Initializer
           Added - net.sourceforge.pmd.lang.ast.AbstractTokenManager
           Added - net.sourceforge.pmd.lang.ast.CharStream
           Added - net.sourceforge.pmd.lang.ast.JavaCharStream
           Added - net.sourceforge.pmd.lang.ast.SimpleCharStream
           Added - net.sourceforge.pmd.lang.ast.TokenMgrError
           Added - net.sourceforge.pmd.lang.rule.stat.StatisticalRule
           Added - net.sourceforge.pmd.lang.rule.stat.StatisticalRuleHelper
           Added - net.sourceforge.pmd.lang.java.rule.AbstractStatisticalJavaRule
           Added - net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory
           Added - net.sourceforge.pmd.lang.rule.RuleViolationFactory
           Added - net.sourceforge.pmd.lang.java.rule.JavaRuleViolationFactory
           Added - net.sourceforge.pmd.lang.jsp.rule.JspRuleViolationFactory
           Renamed - net.sourceforge.pmd.AbstractRule to net.sourceforge.pmd.lang.rule.AbstractRule
           Renamed - net.sourceforge.pmd.AbstractJavaRule to net.sourceforge.pmd.lang.java.rule.AbstractJavaRule
           Renamed - net.sourceforge.pmd.AbstractRuleChainVisitor to net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor
           Renamed - net.sourceforge.pmd.RuleChainVisitor to net.sourceforge.pmd.lang.rule.RuleChainVisitor
           Renamed - net.sourceforge.pmd.SourceFileSelector to net.sourceforge.pmd.lang.rule.LanguageFilenameFilter
           Renamed - net.sourceforge.pmd.rule.XPathRule to net.sourceforge.pmd.lang.rule.XPathRule
           Renamed - net.sourceforge.pmd.jsp.rule.AbstractJspRule to net.sourceforge.pmd.lang.jsp.rule.AbstractJspRule
           Renamed - net.sourceforge.pmd.ast.CompilationUnit to net.sourceforge.pmd.lang.ast.RootNode
           Renamed - net.sourceforge.pmd.ast.JavaRuleChainVisitor to net.sourceforge.pmd.lang.java.rule.JavaRuleChainVisitor
           Renamed - net.sourceforge.pmd.jsp.ast.JspRuleChainVisitor to net.sourceforge.pmd.lang.jsp.rule.JspRuleChainVisitor
           Renamed - net.sourceforge.pmd.parser.Parser to net.sourceforge.pmd.lang.Parser
           Renamed - net.sourceforge.pmd.parser.TokenManager to net.sourceforge.pmd.lang.TokenManager
           Renamed - net.sourceforge.pmd.parser.* into net.sourceforge.pmd.lang.{Language}
           Renamed - net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandler to net.sourceforge.pmd.lang.LanguageVersionHandler
           Renamed - net.sourceforge.pmd.sourcetypehandlers.VisitorStarter to net.sourceforge.pmd.lang.VisitorStarter
           Renamed - net.sourceforge.pmd.sourcetypehandlers.* into net.sourceforge.pmd.lang.{Language}
           Renamed - net.sourceforge.pmd.stat.StatisticalRule to net.sourceforge.pmd.lang.rule.StatisticalRuleHelper
           Renamed - net.sourceforge.pmd.jaxen.TypeOfFunction to net.sourceforge.pmd.lang.java.xpath.TypeOfFunction
           Renamed - net.sourceforge.pmd.jaxen.MatchesFunction to net.sourceforge.pmd.lang.xpath.MatchesFunction
           Renamed - net.sourceforge.pmd.jaxen.Attribute to net.sourceforge.pmd.lang.ast.xpath.Attribute
           Renamed - net.sourceforge.pmd.jaxen.AttributeAxisIterator to net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator
           Renamed - net.sourceforge.pmd.jaxen.DocumentNavigator to net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator
           Renamed - net.sourceforge.pmd.jaxen.NodeIterator to net.sourceforge.pmd.lang.ast.xpath.NodeIterator
           Renamed - net.sourceforge.pmd.ast.* into net.sourceforge.pmd.lang.java.ast.*
           Renamed - net.sourceforge.pmd.rules.* into net.sourceforge.pmd.lang.java.rule.* and updated to follow conventions
           Renamed - net.sourceforge.pmd.jsp.ast.* into net.sourceforge.pmd.lang.jsp.ast.*
           Renamed - net.sourceforge.pmd.jsp.rules.* into net.sourceforge.pmd.lang.jsp.ast.rule.* and updated to follow conventions
           Deleted - net.sourceforge.pmd.cpd.cppast.* into net.sourceforge.pmd.lang.cpp.ast.*
           Deleted - net.sourceforge.pmd.CommonAbstractRule
           Deleted - net.sourceforge.pmd.SourceFileConstants
           Deleted - net.sourceforge.pmd.SourceType
           Deleted - net.sourceforge.pmd.SourceTypeDiscoverer
           Deleted - net.sourceforge.pmd.SourceTypeToRuleLanguageMapper
           Deleted - net.sourceforge.pmd.TargetJDK1_3
           Deleted - net.sourceforge.pmd.TargetJDK1_4
           Deleted - net.sourceforge.pmd.TargetJDK1_5
           Deleted - net.sourceforge.pmd.TargetJDK1_6
           Deleted - net.sourceforge.pmd.TargetJDK1_7
           Deleted - net.sourceforge.pmd.TargetJDKVersion
           Deleted - net.sourceforge.pmd.cpd.SourceFileOrDirectoryFilter
           Deleted - net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandlerBroker
           Deleted - net.sourceforge.pmd.ast.JavaCharStream
           Deleted - net.sourceforge.pmd.ast.CharStream
           Deleted - net.sourceforge.pmd.ast.TokenMgrError
           Deleted - net.sourceforge.pmd.jsp.ast.JspCharStream
           Deleted - net.sourceforge.pmd.jsp.ast.TokenMgrError
    
        API Change - Generalize RuleViolation treatment
           Renamed - net.sourceforge.pmd.IRuleViolation to net.sourceforge.pmd.RuleViolation
           Renamed - net.sourceforge.pmd.RuleViolation to net.sourceforge.pmd.lang.rule.AbstractRuleViolation
           Added - net.sourceforge.pmd.RuleViolationComparator
           Added - net.sourceforge.pmd.lang.java.rule.JavaRuleViolation
           Added - net.sourceforge.pmd.lang.jsp.rule.JspRuleViolation
    
        API Change - Generalize DFA treatment
           Renamed - net.sourceforge.pmd.dfa.IDataFlowNode to net.sourceforge.pmd.lang.dfa.DataFlowNode
           Renamed - net.sourceforge.pmd.dfa.DataFlowNode to net.sourceforge.pmd.lang.dfa.AbstractDataFlowNode
           Renamed - net.sourceforge.pmd.dfa.Linker to net.sourceforge.pmd.lang.dfa.Linker
           Renamed - net.sourceforge.pmd.dfa.LinkerException to net.sourceforge.pmd.lang.dfa.LinkerException
           Renamed - net.sourceforge.pmd.dfa.NodeType to net.sourceforge.pmd.lang.dfa.NodeType
           Renamed - net.sourceforge.pmd.dfa.StackObject to net.sourceforge.pmd.lang.dfa.StackObject
           Renamed - net.sourceforge.pmd.dfa.SequenceChecker to net.sourceforge.pmd.lang.dfa.SequenceChecker
           Renamed - net.sourceforge.pmd.dfa.SequenceException to net.sourceforge.pmd.lang.dfa.SequenceException
           Renamed - net.sourceforge.pmd.dfa.StartOrEndDataFlowNode to net.sourceforge.pmd.lang.dfa.StartOrEndDataFlowNode
           Renamed - net.sourceforge.pmd.dfa.Structure to net.sourceforge.pmd.lang.dfa.Structure
           Renamed - net.sourceforge.pmd.dfa.variableaccess.VariableAccess to net.sourceforge.pmd.lang.dfa.VariableAccess
           Renamed - net.sourceforge.pmd.dfa.variableaccess.VariableAccessException to net.sourceforge.pmd.lang.dfa.VariableAccessException
           Renamed - net.sourceforge.pmd.dfa.pathfinder.* to net.sourceforge.pmd.lang.dfa.pathfinder.*
           Renamed - net.sourceforge.pmd.dfa.report.* to net.sourceforge.pmd.lang.dfa.report.*
           Renamed - net.sourceforge.pmd.dfa.DaaRuleViolation to net.sourceforge.pmd.lang.java.dfa.DaaRuleViolation
           Renamed - net.sourceforge.pmd.dfa.DataFlowFacade to net.sourceforge.pmd.lang.java.dfa.DataFlowFacade
           Renamed - net.sourceforge.pmd.dfa.StatementAndBraceFinder to net.sourceforge.pmd.lang.java.dfa.StatementAndBraceFinder
           Renamed - net.sourceforge.pmd.dfa.variableaccess.VariableAccessVisitor to net.sourceforge.pmd.lang.java.dfa.VariableAccessVisitor
           Added - net.sourceforge.pmd.lang.java.dfa.JavaDataFlowNode
           Added - net.sourceforge.pmd.lang.DataFlowHandler
    
       API Change - Generalize Symbol Table treatement
           Deleted - net.sourceforge.pmd.symboltable.JspSymbolFacade
           Deleted - net.sourceforge.pmd.symboltable.JspScopeAndDeclarationFinder
           Renamed - net.sourceforge.pmd.symboltable.* to net.sourceforge.pmd.lang.java.symboltable.*
    
       API Change - Generalize Type Resolution treatment
           Renamed - net.sourceforge.pmd.typeresolution.* to net.sourceforge.pmd.lang.java.typeresolution.*
    
        API Change - Generalize Property Descriptor treatment
           Renamed - net.sourceforge.pmd.properties.* to net.sourceforge.pmd.lang.rule.properties.*
           Renamed - net.sourceforge.pmd.properties.AbstractPMDProperty to net.sourceforge.pmd.lang.rule.properties.AbstractProperty
           Changed - net.sourceforge.pmd.properties.PropertyDescriptor to use Generics, and other changes
           Added - net.sourceforge.pmd.lang.rule.properties.* new types and other API changes
    
        API Change - Generalize AST treatment
           Added - net.sourceforge.pmd.lang.ast.Node (interface extracted from old Node/SimpleNode)
           Added - net.sourceforge.pmd.lang.ast.AbstractNode
           Added - net.sourceforge.pmd.ast.DummyJavaNode
           Added - net.sourceforge.pmd.jsp.ast.AbstractJspNode
           Added - net.sourceforge.pmd.jsp.ast.JspNode
           Renamed - net.sourceforge.pmd.ast.SimpleJavaNode to net.sourceforge.pmd.ast.AbstractJavaNode
           Renamed - net.sourceforge.pmd.ast.SimpleJavaTypeNode to net.sourceforge.pmd.ast.AbstractJavaTypeNode
           Renamed - net.sourceforge.pmd.ast.SimpleJavaAccessNode to net.sourceforge.pmd.ast.AbstractJavaAccessNode
           Renamed - net.sourceforge.pmd.ast.SimpleJavaAccessTypeNode to net.sourceforge.pmd.ast.AbstractJavaAccessTypeNode
           Deleted - net.sourceforge.pmd.ast.Node
           Deleted - net.sourceforge.pmd.ast.SimpleNode
           Deleted - net.sourceforge.pmd.ast.AccessNodeInterface
           Deleted - net.sourceforge.pmd.jsp.ast.Node
           Deleted - net.sourceforge.pmd.jsp.ast.SimpleNode
    
        API Change - General code reorganization/cleanup
           Renamed - net.sourceforge.pmd.AbstractDelegateRule to net.sourceforge.pmd.lang.rule.AbstractDelegateRule
           Renamed - net.sourceforge.pmd.MockRule to net.sourceforge.pmd.lang.rule.MockRule
           Renamed - net.sourceforge.pmd.RuleReference to net.sourceforge.pmd.lang.rule.RuleReference
           Renamed - net.sourceforge.pmd.ScopedLogHandlersManager to net.sourceforge.pmd.util.log.ScopedLogHandlersManager
           Renamed - net.sourceforge.pmd.util.AntLogHandler to net.sourceforge.pmd.util.log.AntLogHandler
           Renamed - net.sourceforge.pmd.util.ConsoleLogHandler to net.sourceforge.pmd.util.log.ConsoleLogHandler
           Renamed - net.sourceforge.pmd.util.PmdLogFormatter to net.sourceforge.pmd.util.log.PmdLogFormatter
    
       API Change - Changes to Rule/RuleSet/RuleSets
          Removed - boolean Rule.include()
          Removed - void Rule.setInclude(boolean)
          Removed - String Rule.getRulePriorityName()
          Removed - String Rule.getExample()
          Removed - Rule.LOWEST_PRIORITY
          Removed - Rule.PRIORITIES
           Removed - Properties Rule.getProperties()
           Removed - Rule.addProperties(Properties)
           Removed - boolean Rule.hasProperty(String)
           Removed - RuleSet.applies(Language,Language)
           Removed - RuleSet.getLanguage()
           Removed - RuleSet.setLanguage(Language)
           Removed - RuleSets.applies(Language,Language)
          Changed - void Rule.setPriority(int) to void Rule.setPriority(RulePriority)
          Changed - int Rule.getPriority() to void RulePriority Rule.getPriority()
           Changed - XXX Rule.getXXXProperty(String) to <T> Rule.getProperty(PropertyDescriptor<T>)
           Changed - XXX Rule.getXXXProperty(PropertyDescriptor) to <T> Rule.getProperty(PropertyDescriptor<T>)
           Changed - Rule.addProperty(String, String) to Rule.setProperty(PropertyDescriptor<T>, T)
           Changed - Rule.setProperty(PropertyDescriptor, Object) to Rule.setProperty(PropertyDescriptor<T>, T)
           Changed - Rule.setProperty(PropertyDescriptor, Object[]) to Rule.setProperty(PropertyDescriptor<T>, T)
           Changed - Rule.propertyValuesByDescriptor() to Rule.getPropertiesByPropertyDescriptor()
           Changed - PropertyDescriptor Rule.propertyDescriptorFor(String) to PropertyDescriptor Rule.getPropertyDescriptor(String)
           Changed - boolean RuleSet.usesDFA() to boolean RuleSet.usesDFA(Language)
           Changed - boolean RuleSet.usesTypeResolution() to boolean RuleSet.usesTypeResolution(Language)
          Added - Rule.setLanguage(Language)
          Added - Language Rule.getLanguage()
          Added - Rule.setMinimumLanguageVersion(LanguageVersion)
          Added - LanguageVersion Rule.getMinimumLanguageVersion()
          Added - Rule.setMaximumLanguageVersion(LanguageVersion)
          Added - LanguageVersion Rule.getMaximumLanguageVersion()
          Added - Rule.setDeprecated(boolean)
          Added - boolean Rule.isDeprecated()
          Added - String Rule.dysfunctionReason();
           Added - Rule.definePropertyDescriptor(PropertyDescriptor)
           Added - List<PropertyDescriptor> Rule.getPropertyDescriptors()
           Added - RuleSet.applies(Rule,LanguageVersion)
    
       API Change - Changes to PMD class
          Renamed - PMD.EXCLUDE_MARKER to PMD.SUPPRESS_MARKER
          Removed - PMD.processFile(InputStream, RuleSet, RuleContext)
          Removed - PMD.processFile(InputStream, String, RuleSet, RuleContext)
          Removed - PMD.processFile(Reader, RuleSet, RuleContext)
          Removed - PMD.processFile(Reader, RuleSets, RuleContext, LanguageVersion)
          Moved - PMD.getExcludeMarker() to Configuration.getSuppressMarker()
          Moved - PMD.setExcludeMarker(String) to Configuration.getSuppressMarker(String)
          Moved - PMD.getClassLoader() to Configuration.getClassLoader()
          Moved - PMD.setClassLoader(ClassLoader) to Configuration.getClassLoader(ClassLoader)
          Moved - PMD.setDefaultLanguageVersion(LanguageVersion) to Configuration.setDefaultLanguageVersion(LanguageVersion)
          Moved - PMD.setDefaultLanguageVersions(List<LanguageVersion>) to Configuration.setDefaultLanguageVersions(List<LanguageVersion>)
          Moved - PMD.createClasspathClassLoader(String) to Configuration.createClasspathClassLoader(String)
    
       API Change - Changes to Node interface
          Renamed - Node.findChildrenOfType(Class) as Node.findDescendantsOfType(Class)
          Renamed - Node.getFirstChildOfType(Class) as Node.getFirstDescendantOfType(Class)
          Renamed - Node.containsChildOfType(Class) as Node.hasDescendantOfType(Class)
          Renamed - Node.getAsXml() as Node.getAsDocument()
          Added - Node.findChildrenOfType(Class), non recursive version
          Added - Node.getFirstChildOfType(Class), non recursive version
    
       API Change - Remove deprecated APIs
          Removed - AccessNode.setXXX() methods, use AccessNode.setXXX(boolean) instead.
          Removed - PMDException.getReason()
          Removed - RuleSetFactory.createRuleSet(String,ClassLoader), use RuleSetFactory.setClassLoader(ClassLoader) and RuleSetFactory.createRuleSets(String) instead.
          Removed - net.sourceforge.pmd.cpd.FileFinder use net.sourceforge.pmd.util.FileFinder instead.
    
       API Change - RuleSetFactory
            Added - RuleSetFactory.setClassLoader(ClassLoader)
            Added - RuleSetFactory.createRuleSets(List<RuleSetReferenceId>)
            Added - RuleSetFactory.createRuleSet(RuleSetReferenceId)
            Added - RuleSetFactory.setClassLoader(ClassLoader)
            Added - RuleSetReferenceId class to handle parsing of RuleSet strings, see RuleSetReferenceId.parse(String)
          Renamed - RuleSetFactory.createSingleRuleSet(String) to RuleSetFactory.createRuleSet(String);
          Removed - RuleSetFactory.createRuleSets(String, ClassLoader), use RuleSetFactory.createRuleSets(String) instead.
          Removed - RuleSetFactory.createSingleRuleSet(String, ClassLoader), use RuleSetFactory.createSingleRuleSet(String) instead.
          Removed - RuleSetFactory.createRuleSet(InputStream, ClassLoader), use RuleSetFactory.createRuleSet(RuleSetReferenceId) instead.
          Removed - ExternalRuleID, use RuleSetReferenceId instead
          Removed - SimpleRuleSetNameMapper, use RuleSetReferenceId instead
    
       API Change - Changes to Renderer class, and Renderer implementations
            Added - Renderer.getName()
            Added - Renderer.setName(String)
            Added - Renderer.getDescription()
            Added - Renderer.setDescription(String)
            Added - Renderer.getPropertyDefinitions()
            Added - Renderer.isShowSuppressedViolations()
            Added - AbstractAccumulatingRenderer
          Removed - Renderer.render(Report)
          Removed - Renderer.render(Report, Writer)
          Renamed - Renderer.showSuppressedViolations(boolean) to Renderer.setShowSuppressedViolations(boolean)
          Renamed - PapariTextRenderer to TextColorRenderer
          Renamed - OntheFlyRenderer to AbstractIncrementingRenderer
    
    PMD command line changes:
    
       Removed -lineprefix use -property linePrefix {value} instead
       Removed -linkprefix use -property linkPrefix {value} instead
       Removed -xslt use -property xsltFilename {value} instead
       Removed -nojsp now obsolete
       Removed -targetjdk use -version {name} {version} instead
       Added -version {name} {version} to set language version to use for a given language
       Added -property {name} {value} as generic way to pass properties to Renderers
       Added -showsuppressed as a means to show suppressed rule violations (consistent with Ant task behavior)
       Renamed 'nicehtml' report to 'xslt'
       Renamed 'papari' report to 'textcolor'
       Renamed -excludemarker option to -suppressmarker
       Renamed -cpus option to -threads
    
    Ant changes:
    
       Removed - <formatter> 'linkPrefix' attribute, use <param name="linkPrefix"> instead
       Removed - <formatter> 'linePrefix' attribute, use <param name="linePrefix"> instead
       Changed - <formatter> is optional - if not specified, falls back to "text" and console output.
       Removed - <pmd> 'targetJDK' attribute to <version>lang version</version> instead
         Added - <param name="name" value="value"/> as generic way to pass properties to Renderers on <formatter>
       Renamed - <pmd> 'excludeMarker' attribute to 'suppressMarker'
       Renamed - <pmd> 'cpus' attribute to 'threads'
    
    Maven changes:
       The new maven coordinates are: net.sourceforge.pmd:pmd, e.g.
       <dependency>
         <groupId>net.sourceforge.pmd</groupId>
         <artifactId>pmd</artifactId>
         <version>5.0</version>
       </dependency>
    
    New features:
    
    New Language 'ecmascript' added, for writing XPathRule and Java Rules against ECMAScript/JavaScript documents (must be standalone, not embedded in HTML).  Many thanks to Rhino!
    New Language 'xml' added, for writing XPathRules against XML documents
    New Language 'xsl' added, as a derivative from XML.
    Rules can now define a 'violationSuppressRegex' property to universally suppress violations with messages matching the given regular expression
    Rules can now define a 'violationSuppressXPath' property to universally suppress violations on nodes which match the given relative XPath expression
    Rules are now directly associated with a corresponding Language, and a can also be associated with a specific Language Version range if desired.
    Rules can now be flagged with deprecated='true' in the RuleSet XML to allow the PMD Project to indicate a Rule (1) is scheduled for removal, (2) has been removed, or (3) has been renamed/moved.
    XPathRules can now query using XPath 2.0 with 'version=2.0"', or XPath 2.0 in XPath 1.0 compatibility mode using 'version="1.0 compatibility"'.  Many thanks to Saxon!
    Rules can now use property values in messages, for example ${propertyName} will expand to the value of the 'propertyName' property on the Rule.
    Rules can now use violation specific values in messages, specifically ${variableName}, ${methodName}, ${className}, ${packageName}.
    New XPath function 'getCommentOn' can be used to search for strings in comments - Thanks to Andy Throgmorton
    
    CPD:
    Add .hxx and .hpp as valid file extension for CPD - Thanks to Ryan Pavlik
    Add options to to the CPD command line task - Thanks to Cd-Man
    Add C# support for CPD - thanks to Florian Bauer
    Fix small bug in Rule Designer UI
    Performance enhacement when parsing Javadoc (Patch ID: 3217201), thanks to Cd-Man
    Rework the XMLRenderer to use proper XML API and strictly uses the system value for encoding (Fix bug: 1435751)
    
    Other changes:
    Rule property API upgrades:
      All numeric property descriptors can specify upper & lower limits
      Newly functional Method & Type descriptors allow rule developers to incorporate/watch for individual methods or types
      Better initialization error detection
      Deprecated old string-keyed property API, will leave some methods behind for XPath rules however
    '41' and '42' shortcuts for rulesets added
    The default Java version processed by PMD is now uniformly Java 1.5.
    RuleViolations in Reports now uses List internally, and RuleViolationComparator is no longer broken
    TokenManager errors now include a file name whenever possible for every AST in PMD
    Added file encoding option to CPD GUI, which already existed for the command line and Ant
    AssignmentInOperand enhanced to catch assignment in 'for' condition, as well as use of increment/decrement operators.  Customization properties added to allow assignment in if/while/for, or use of increment/decrement.
    Fix false positive on CastExpressions for UselessParentheses
    Fix false positive where StringBuffer.setLength(0) was using default constructor size of 16, instead of actual constructor size.
    Fix false negative for non-primitive types for VariableNamingConventions, also expanded scope to local and method/constructors, and enhanced customization options to choose between members/locals/parameters (all checked by default)
    Fix false negative for UseArraysAsList when the array was passed as method parameter - thanks to Andy Throgmorton
    Improve TooManyMethods rule - thanks to a patch from Riku Nykanen
    Improve DoNotCallSystemExit - thanks to a patch from Steven Christou
    Correct -benchmark reporting of Rule visits via the RuleChain
    Creating an Empty Code Ruleset and moved the following rules from Basic ruleset:
            * Empty Code Rules
            * EmptyCatchBlock
            * EmptyIfStmt
            * EmptyWhileStmt
            * EmptyTryBlock
            * EmptyFinallyBlock
            * EmptySwitchStatements
            * EmptySynchronizedBlock
            * EmptyStatementNotInLoop
            * EmptyInitializer
            * EmptyStatementBlock
            * EmptyStaticInitializer
        Basic rulesets still includes a reference to those rules.
    Creating a unnecessary Code Ruleset and moved the following rules from Basic ruleset:
            * UnnecessaryConversionTemporary
            * UnnecessaryReturn
            * UnnecessaryFinalModifier
            * UselessOverridingMethod
            * UselessOperationOnImmutable
            * UnusedNullCheckInEquals
            * UselessParentheses
        Basic rulesets still includes a reference to those rules.
    
    Fixed bug 2920057 - Fixed False + on CloseResource
    Fixed bug 1808110 - Fixed performance issues on PreserveStackTrace
    Fixed bug 2832322 - cpd.xml file tag path attribute should be entity-encoded
    Fixed bug 2826119 - False +: DoubleCheckedLocking warning with volatile field
    Fixed bug 2835074 - False -: DoubleCheckedLocking with reversed null check
    Fixed bug 1932242 - EmptyMethodInAbstractClassShouldBeAbstract false +
    Fixed bug 1928009 - Error using migration ruleset in PMD 4.2
    Fixed bug 1808110 - PreserveStackTrace
    Fixed bug 1988829 - Violation reported without source file name (actually a fix to ConsecutiveLiteralAppends)
    Fixed bug 1989814 - false +: ConsecutiveLiteralAppends
    Fixed bug 1977230 - false positive: UselessOverridingMethod
    Fixed bug 1998185 - BeanMembersShouldSerialize vs @SuppressWarnings("serial")
    Fixed bug 2002722 - false + in UseStringBufferForStringAppends
    Fixed bug 2056318 - False positive for AvoidInstantiatingObjectsInLoops
    Fixed bug 1977438 - False positive for UselessStringValueOf
    Fixed bug 2050064 - False + SuspiciousOctalEscape with backslash literal
    Fixed bug 1556594 - Wonky detection of NullAssignment
    Fixed bug 1481051 - false + UnusedNullCheckInEquals (and other false positives too)
    Fixed bug 1943204 - Ant task: <ruleset> path should be relative to Ant basedir
    Fixed patch 2075906 - Add toString() to the rule UnnecessaryWrapperObjectCreation
    Fixed bug 2315623 - @SuppressWarnings("PMD.UseSingleton") has no effect
    Fixed bug 2230809 - False +: ClassWithOnlyPrivateConstructorsShouldBeFinal
    Fixed bug 2338341 - ArrayIndexOutOfBoundsException in CPD (on Ruby)
    Fixed bug 2315599 - False +: UseSingleton with class containing constructor
    Fixed bug 1955852 - false positives for UnusedPrivateMethod & UnusedLocalVariable
    Fixed bug 2404700 - UseSingleton should not act on enums
    Fixed bug - JUnitTestsShouldIncludeAssert now detects Junit 4 Assert.assert...  constructs
    Fixed bug 1609038 - Xslt report generators break if path contains "java"
    Fixed bug 2142986 - UselessOverridingMethod doesn't consider annotations
    Fixed bug 2027626 - False + : AvoidFinalLocalVariable
    Fixed bug 2606609 - False "UnusedImports" positive in package-info.java
    Fixed bug 2645268 - ClassCastException in UselessOperationOnImmutable.getDeclaration
    Fixed bug 2724653 - AvoidThreadGroup reports false positives
    Fixed bug 2904832 - Type resolution not working for ASTType when using an inner class
    Fixed bug 1435751 - XML format does not support UTF-8
    Fixed bug 3303811 - Deadlink on "Similar projects" page
    Fixed bug 3017616 - Updated documentation regarding Netbeans plugin - thanks to Jesse Glick
    Fixed bug 3427563 - Deprecated class (android.util.config) - thanks to Lukas Reschke for the patch
    
    ruleset.dtd and ruleset_xml_schema.xsd added to jar file in rulesets directory
    bin and java14/bin scripts:
        retroweaver version was not correct in java14/bin scripts
        support for extra languages in cpd.sh
        standard unix scripts can be used with cygwin
    Upgrading UselessOperationOnImmutable to detect more use cases, especially on String and fix false positives
    AvoidDuplicateLiteralRule now has 'skipAnnotations' boolean property
    Fixed false positive in UnusedImports: javadoc comments are parsed to check @see and other tags
    Fixed parsing bug: constant fields in annotation classes
    Bug fix: NPE in MoreThanOneLogger
    UnnecessaryParentheses now checks all expressions, not just return statements
    UnusedFormalParameter now reports violations on the parameter node, not the method/constructor node
    Updates to RuleChain to honor RuleSet exclude-pattern
    Optimizations and false positive fixes in PreserveStackTrace
    @SuppressWarnings("all") disables all warnings
    SingularField now checks for multiple fields in the same declaration
    Java grammar enhanced to include AnnotationMethodDeclaration as parent node of method related children of AnnotationTypeMemberDeclaration
    JavaCC generated artifacts updated to JavaCC 4.1.
    Dependencies updates: asm updated to 3.2
    Ant requirement is now 1.7.0 or higher for compilation
        JUnit testing jar is packaged on 1.7.0+ only in ant binary distributions
        Note that the ant task still works with 1.6.0 and higher
    All comment types are now stored in ASTCompilationUnit, not just formal ones
    Fixed false negative in UselessOverridingMethod
    Fixed handling of escape characters in UseIndexOfChar and AppendCharacterWithChar
    Fixed ClassCastException on generic method in BeanMembersShouldSerialize
    Fixed ClassCastException in symbol table code
    Support for Java 1.4 runtime dropped, PMD now requires Java 5 or higher.  PMD can still process Java 1.4 source files.
    Support for Java 1.7
    Text renderer is now silent if there's no violation instead of displaying "No problems found!"
    RuleSet short names now require a language prefix, 'basic' is now 'java-basic', and 'rulesets/basic.xml' is now 'rulesets/java/basic.xml'
    The JSP RuleSets are now in the 'jsp' language, and are 'jsp-basic', 'jsp-basic-jsf', 'rulesets/jsp/basic.xml' and 'rulesets/jsp/basic-jsp.xml'
    Enhanced logging in the ClassTypeResolver to provide more detailed messaging.
    AvoidUsingHardCodedIP modified to not use InetAddress.getByName(String), instead does better pattern analysis.
    The JSP/JSF parser can now parse Unicode input.
    The JSP/JSP parser can now handle <script>...</script> tags.  The AST HtmlScript node contains the content.
    Added Ecmascript as a supported language for CPD.
    The RuleSet XML Schema namespace is now: http://pmd.sourceforge.net/ruleset/2.0.0
    The RuleSet XML Schema is located in the source at: etc/ruleset_2_0_0.xsd
    The RuleSet DTD is located in the source at: etc/ruleset_2_0_0.dtd
    Improved include/exclude pattern matching performance for ends-with type patterns.
    Modify (and hopefully fixed) CPD algorithm thanks to a patch from Juan Jesús García de Soria.
    Fixed character reference in xml report - thanks to Seko
    Enhanced SuspiciousEqualsMethodName rule - thanks to Andy Throgmorton
    Add a script to launch CPDGUI on Unix system - thanks to Tom Wheeler
    
    New Java rules:
    
        Basic ruleset: ExtendsObject,CheckSkipResult,AvoidBranchingStatementAsLastInLoop,DontCallThreadRun,DontUseFloatTypeForLoopIndices
        Controversial ruleset: AvoidLiteralsInIfCondition, AvoidPrefixingMethodParameters, OneDeclarationPerLine, UseConcurrentHashMap
        Coupling ruleset: LoosePackageCoupling,LawofDemeter
        Design ruleset: LogicInversion,UseVarargs,FieldDeclarationsShouldBeAtStartOfClass,GodClass
        Empty ruleset: EmptyInitializer,EmptyStatementBlock
        Import ruleset: UnnecessaryFullyQualifiedName
        Optimization ruleset: RedundantFieldInitializer
        Naming ruleset: ShortClassName, GenericsNaming
        StrictException ruleset: AvoidThrowingNewInstanceOfSameException, AvoidCatchingGenericException, AvoidLosingExceptionInformation
        Unnecessary ruleset: UselessParentheses
        JUnit ruleset: JUnitTestContainsTooManyAsserts, UseAssertTrueInsteadOfAssertEquals
        Logging with Jakarta Commons ruleset: GuardDebugLogging
    
    New Java ruleset:
        android.xml: new rules specific to the Android platform
    
    New JSP rules:
        Basic ruleset: NoInlineScript
    
    New ECMAScript rules:
        Basic ruleset: AssignmentInOperand,ConsistentReturn,InnaccurateNumericLiteral,ScopeForInVariable,UnreachableCode,EqualComparison,GlobalVariable
        Braces ruleset: ForLoopsMustUseBraces,IfStmtsMustUseBraces,IfElseStmtsMustUseBraces,WhileLoopsMustUseBraces
        Unnecessary ruleset: UnnecessaryParentheses,UnnecessaryBlock
    
    New XML rules:
        Basic ruleset: MistypedCDATASection


## November 4, 2011 - 4.3:

    Add support for Java 7 grammer - thanks to Dinesh Bolkensteyn and SonarSource
    Add options --ignore-literals and --ignore-identifiers to the CPD command line task, thanks to Cd-Man
    Fixed character reference in xml report - thanks to Seko
    Add C# support for CPD - thanks to Florian Bauer
    Fix small bug in Rule Designer UI
    Improve TooManyMethods rule - thanks to a patch from Riku Nykanen
    Improve DoNotCallSystemExit - thanks to a patch from Steven Christou
    Fix false negative for UseArraysAsList when the array was passed as method parameter - thanks to Andy Throgmorton
    Enhanced SuspiciousEqualsMethodName rule - thanks to Andy Throgmorton
    Add a script to launch CPDGUI on Unix system - thanks to Tom Wheeler
    
    New Rule:
        Basic ruleset: DontCallThreadRun - thanks to Andy Throgmorton
        Logging with Jakarta Commons ruleset: GuardDebugLogging


## September 14, 2011 - 4.2.6:

    Fixed bug 2920057 - False + : CloseRessource whith an external getter
    Fixed bug 1808110 - Fixed performance issue on PreserveStackTrace
    Fixed bug 2832322 -  cpd.xml file tag path attribute should be entity-encoded
    Fixed bug 2590258 - NPE with nicerhtml output
    Fixed bug 2317099 - False + in SimplifyCondition
    Fixed bug 2606609 - False "UnusedImports" positive in package-info.java
    Fixed bug 2645268 - ClassCastException in UselessOperationOnImmutable.getDeclaration
    Fixed bug 2724653 - AvoidThreadGroup reports false positives
    Fixed bug 2835074 - False -: DoubleCheckedLocking with reversed null check
    Fixed bug 2826119 - False +: DoubleCheckedLocking warning with volatile field
    Fixed bug 2904832 - Type resolution not working for ASTType when using an inner class
    
    Modify (and hopefully fixed) CPD algorithm thanks to a patch from Juan Jesús García de Soria.
    Correct -benchmark reporting of Rule visits via the RuleChain
    Fix issue with Type Resolution incorrectly handling of Classes with same name as a java.lang Class.
    The JSP/JSF parser can now parse Unicode input.
    The JSP/JSP parser can now handle <script>...</script> tags.  The AST HtmlScript node contains the content.
    Added Ecmascript as a supported language for CPD.
    Improved include/exclude pattern matching performance for ends-with type patterns.
    
    Dependencies updates: asm updated to 3.2
    
    Android ruleset: CallSuperLast rule now also checks for finish() redefinitions
    
    New rule:
        Android: DoNotHardCodeSDCard
        Controversial : AvoidLiteralsInIfCondition (patch 2591627), UseConcurrentHashMap
        StrictExceptions : AvoidCatchingGenericException, AvoidLosingExceptionInformation
        Naming : GenericsNaming
        JSP: NoInlineScript


## February 08, 2009 - 4.2.5:

    Enhanced logging in the ClassTypeResolver to provide more detailed messaging.
    Fixed bug 2315623 - @SuppressWarnings("PMD.UseSingleton") has no effect
    Fixed bug 2230809 - False +: ClassWithOnlyPrivateConstructorsShouldBeFinal
    Fixed bug 2338341 - ArrayIndexOutOfBoundsException in CPD (on Ruby)
    Fixed bug 2315599 - False +: UseSingleton with class containing constructor
    Fixed bug 1955852 - false positives for UnusedPrivateMethod & UnusedLocalVariable
    Fixed bug 2404700 - UseSingleton should not act on enums
    Fixed bug 2225474 - VariableNamingConventions does not work with nonprimitives
    Fixed bug 1609038 - Xslt report generators break if path contains "java"
    Fixed bug - JUnitTestsShouldIncludeAssert now detects Junit 4 Assert.assert...  constructs
    Fixed bug 2142986 - UselessOverridingMethod doesn't consider annotations
    Fixed bug 2027626 - False + : AvoidFinalLocalVariable
    
    New rule:
        StrictExceptions : AvoidThrowingNewInstanceOfSameException
    New ruleset:
        android.xml: new rules specific to the Android platform


## October 12, 2008 - 4.2.4:

    Fixed bug 1481051 - false + UnusedNullCheckInEquals (and other false positives too)
    Fixed bug 1943204 - Ant task: <ruleset> path should be relative to Ant basedir
    Fixed bug 2139720 - Exception in PMD Rule Designer for inline comments in source
    Fixed patch 2075906 - Add toString() to the rule UnnecessaryWrapperObjectCreation
    Fixed ClassCastException on generic method in BeanMembersShouldSerialize
    Fixed ClassCastException in symbol table code


## August 31, 2008 - 4.2.3:

    JavaCC generated artifacts updated to JavaCC 4.1d1.
    Java grammar enhanced to include AnnotationMethodDeclaration as parent node of method related children of AnnotationTypeMemberDeclaration
    Fixes for exclude-pattern
    Updates to RuleChain to honor RuleSet exclude-pattern
    Upgrading UselessOperationOnImmutable to detect more use cases, especially on String and fix false positives
    Fixed bug 1988829 - Violation reported without source file name (actually a fix to ConsecutiveLiteralAppends)
    Fixed bug 1989814 - false +: ConsecutiveLiteralAppends
    Fixed bug 1977230 - false positive: UselessOverridingMethod
    Fixed bug 1998185 - BeanMembersShouldSerialize vs @SuppressWarnings("serial")
    Fixed bug 2002722 - false + in UseStringBufferForStringAppends
    Fixed bug 2056318 - False positive for AvoidInstantiatingObjectsInLoops
    Fixed bug 1977438 - False positive for UselessStringValueOf
    Fixed bug 2050064 - False + SuspiciousOctalEscape with backslash literal
    Fixed bug 1556594 - Wonky detection of NullAssignment
    Optimizations and false positive fixes in PreserveStackTrace
    @SuppressWarnings("all") disables all warnings
    All comment types are now stored in ASTCompilationUnit, not just formal ones
    Fixed false negative in UselessOverridingMethod
    Fixed handling of escape characters in UseIndexOfChar and AppendCharacterWithChar
    
    New rule:
        Basic ruleset:  EmptyInitializer


## May 20, 2008 - 4.2.2:

    Fixed false positive in UnusedImports: javadoc comments are parsed to check @see and other tags
    Fixed parsing bug: constant fields in annotation classes
    Bug fix: NPE in MoreThanOneLogger
    UnnecessaryParentheses now checks all expressions, not just return statements


## April 11, 2008 - 4.2.1:

    '41' and '42' shortcuts for rulesets added
    Fixed bug 1928009 - Error using migration ruleset in PMD 4.2
    Fixed bug 1932242 - EmptyMethodInAbstractClassShouldBeAbstract false +
    Fixed bug 1808110 - PreserveStackTrace
    
    AvoidDuplicateLiteralRule now has 'skipAnnotations' boolean property
    ruleset.dtd and ruleset_xml_schema.xsd added to jar file in rulesets directory
    Update RuleSetWriter to handle non-Apache TRAX implementations, add an option to not use XML Namespaces
    Added file encoding option to CPD GUI, which already existed for the command line and Ant
    bin and java14/bin scripts:
        retroweaver version was not correct in java14/bin scripts
        support for extra languages in cpd.sh
        standard unix scripts can be used with cygwin


## March 25, 2008 - 4.2:

    Fixed bug 1920155 - CheckResultSet: Does not pass for loop conditionals

## March 21, 2008 - 4.2rc2:

    Fixed bug 1912831 - False + UnusedPrivateMethod with varargs
    Fixed bug 1913536 - Rule Designer does not recognize JSP(XML)
    Add -auxclasspath option for specifying Type Resolution classpath from command line and auxclasspath nested element for ant task.
    Fixed formatting problems in loggers.
    
    Ant task upgrade:
        Added a new attribute 'maxRuleCount' to indicate whether or not to fail the build if PMD finds that much violations.


## March 07, 2008 - 4.2rc1:

    Fixed bug 1866198 - PMD should not register global Logger
    Fixed bug 1843273 - False - on SimplifyBooleanReturns
    Fixed bug 1848888 - Fixed false positive in UseEqualsToCompareStrings
    Fixed bug 1874313 - Documentation bugs
    Fixed bug 1855409 - False + in EmptyMethodInAbstractClassShouldBeAbstract
    Fixed bug 1888967 - Updated xpath query to detect more "empty" methods.
    Fixed bug 1891399 - Check for JUnit4 test method fails
    Fixed bug 1894821 - False - for Test Class without Test Cases
    Fixed bug 1882457 - PositionLiteralsFirstInComparisons rule not working OK
    Fixed bug 1842505 - XML output incorrect for inner classes
    Fixed bug 1808158 - Constructor args could also be final
    Fixed bug 1902351 - AvoidReassigningParameters not identify parent field
    Fixed other false positives in EmptyMethodInAbstractClassShouldBeAbstract
    Fixed other issues in SimplifyBooleanReturns
    Modified AvoidReassigningParameter to also check constructor arguments for reassignement
    
    New rules:
        Basic ruleset: AvoidMultipleUnaryOperators
        Controversial ruleset: DoNotCallGarbageCollectionExplicitly,UseObjectForClearerAPI
        Design ruleset : ReturnEmptyArrayRatherThanNull,TooFewBranchesForASwitchStatement,AbstractClassWithoutAnyMethod
        Codesize : TooManyMethods
        StrictExceptions : DoNotThrowExceptionInFinally
        Strings : AvoidStringBufferField
    
    Rule upgrade:
        CyclomaticComplexity now can be configured to display only class average complexity or method complexity, or both.
    
    Designer upgrade:
        A new panel for symbols and a tooltips on AST node that displays line, column and access node attributes (private,
        static, abstract,...)
    
    1.7 added as a valid option for targetjdk.
    New elements under <ruleset>: <exclude-pattern> to match files exclude from processing, with <include-pattern> to override.
    Rules can now be written which produce violations based upon aggregate file processing (i.e. cross/multiple file violations).
    PMD Rule Designer can now shows Symbol Table contents for the selected AST node.
    PMD Rule Designer shows position info in tooltip for AST nodes and highlights matching code for selected AST node in code window.
    CPD Ant task will report to System.out when 'outputFile' not given.
    RuleSetWriter class can be used to Serialize a RuleSet to XML in a standard fashion.  Recommend PMD IDE plugins standardize their behavior.
    retroweaver updated to version 2.0.5.


## November 17, 2007 - 4.1:

    Fixed annotation bug: ClassCastException when a formal parameter had multiple annotations
    Added a Visual Studio renderer for CPD; just use "--format vs".
    Dependencies updates: asm to 3.1, retroweaver to 2.0.2, junit to 4.4
    new ant target ("regress") to test regression bugs only


## November 01, 2007 - 4.1rc1:

    New rules:
        Basic ruleset: AvoidUsingHardCodedIP,CheckResultSet
        Controversial ruleset: AvoidFinalLocalVariable,AvoidUsingShortType,AvoidUsingVolatile,AvoidUsingNativeCode,AvoidAccessibilityAlteration
        Design ruleset: ClassWithOnlyPrivateConstructorsShouldBeFinal,EmptyMethodInAbstractClassShouldBeAbstract
        Imports ruleset: TooManyStaticImports
        J2ee ruleset: DoNotCallSystemExit, StaticEJBFieldShouldBeFinal,DoNotUseThreads
        Strings ruleset: UseEqualsToCompareStrings
    
    Fixed bug 674394  - fixed false positive in DuplicateImports for disambiguation import
    Fixed bug 631681  - fixed false positive in UnusedPrivateField when field is accessed by outer class
    Fixed bug 985989  - fixed false negative in ConstructorCallsOverridableMethod for inner static classes
    Fixed bug 1409944 - fixed false positive in SingularField for lock objects
    Fixed bug 1472195 - fixed false positives in PositionLiteralsFirstInComparisons when the string is used as a parameter
    Fixed bug 1522517 - fixed false positive in UselessOverridingMethod for clone method
    Fixed bug 1744065 - fixed false positive in BooleanInstantiation when a custom Boolean is used
    Fixed bug 1765613 - fixed NullPointerException in CloneMethodMustImplementCloneable when checking enum
    Fixed bug 1740480 - fixed false positive in ImmutableField when the assignment is inside an 'if'
    Fixed bug 1702782 - fixed false positive in UselessOperationOnImmutable when an Immutable on which an operation is performed is compareTo'd
    Fixed bugs 1764288/1744069/1744071 - When using Type Resolution all junit test cases will notice if you're using an extended TestCase
    Fixed bug 1793215 - pmd-nicerhtml.xsl does not display line numbers
    Fixes bug 1796928 - fixed false positive in AvoidThrowingRawExceptionTypes, when a Type name is the same as a RawException.
    Fixed bug 1811506 - False - : UnusedFormalParameter (property "checkall" needs to be set)
    Fixed false negative in UnnecessaryCaseChange
    
    The Java 1.5 source code parser is now the default for testcode used in PMD's unit tests.
    Added TypeResolution to the XPath rule. Use typeof function to determine if a node is of a particular type
    Adding a GenericLiteralChecker, a generic rule that require a regex as property. It will log a violation if a Literal is matched by the regex. See the new rule AvoidUsingHardCodedIP for an example.
    Adding support for multiple line span String in CPD's AbstractTokenizer, this may change, for the better, CPD's Ruby parsing.
    This release adds 'nicehtml', with the plan for the next major release to make nicehtml->html, and html->oldhtml. This feature uses an XSLT transformation, default stylesheet maybe override with '-xslt filename'.
    New CPD command line feature : Using more than one directory for sources. You can now have several '--files' on the command line.
    SingularField greatly improved to generate very few false positives (none?). Moved from controversial to design. Two options added to restore old behaviour (mostly).
    Jaxen updated to 1.1.1, now Literal[@Image='""'] works in XPath expressions.


## July 20, 2007 - 4.0

    Fixed bug 1697397 - fixed false positives in ClassCastExceptionWithToArray
    Fixed bug 1728789 - removed redundant rule AvoidNonConstructorMethodsWithClassName; MethodWithSameNameAsEnclosingClass is faster and does the same thing.


## July 12, 2007 - 4.0rc2:

    New rules:
        Typeresolution ruleset: SignatureDeclareThrowsException - re-implementation using the new Type Resolution facility (old rule is still available)
    Fixed bug 1698550 - CloneMethodMustImplementCloneable now accepts a clone method that throws CloneNotSupportedException in a final class
    Fixed bug 1680568 - The new typeresolution SignatureDeclareThrowsException rule now ignores setUp and tearDown in JUnit 4 tests and tests that do not directly extend TestCase
    The new typeresolution SignatureDeclareThrowsException rule can now ignore JUnit classes completely by setting the IgnoreJUnitCompletely property
    Fixed false positive in UselessOperationOnImmutable
    PMD now defaults to using a Java 1.5 source code parser.


## June 22, 2007 - 4.0rc1:

    New rules:
        Strict exception ruleset: DoNotExtendJavaLangError
        Basic JSP ruleset: JspEncoding
        J2EE ruleset: MDBAndSessionBeanNamingConvention, RemoteSessionInterfaceNamingConvention, LocalInterfaceSessionNamingConvention, LocalHomeNamingConvention, RemoteInterfaceNamingConvention
        Optimizations ruleset: AddEmptyString
        Naming: BooleanGetMethodName
    New rulesets:
        Migrating To JUnit4: Rules that help move from JUnit 3 to JUnit 4
    Fixed bug 1670717 - 'Copy xml to clipboard' menu command now works again in the Designer
    Fixed bug 1618858 - PMD no longer raises an exception on XPath like '//ConditionalExpression//ConditionalExpression'
    Fixed bug 1626232 - Commons logging rules (ProperLogger and UseCorrectExceptionLogging) now catch more cases
    Fixed bugs 1626201 & 1633683 - BrokenNullCheck now catches more cases
    Fixed bug 1626715 - UseAssertSameInsteadOfAssertTrue now correctly checks classes which contain the null constant
    Fixed bug 1531216 - ImmutableField. NameOccurrence.isSelfAssignment now recognizes this.x++ as a self assignment
    Fixed bug 1634078 - StringToString now recognizes toString on a String Array, rather than an element.
    Fixed bug 1631646 - UselessOperationOnImmutable doesn't throw on variable.method().variable.
    Fixed bug 1627830 - UseLocaleWithCaseConversions now works with compound string operations
    Fixed bug 1613807 - DontImportJavaLang rule allows import to Thread inner classes
    Fixed bug 1637573 - The PMD Ant task no longer closes System.out if toConsole is set
    Fixed bug 1451251 - A new UnusedImports rule, using typeresolution, finds unused import on demand rules
    Fixed bug 1613793 - MissingSerialVersionUID rule now doesn't fire on abstract classes
    Fixed bug 1666646 - ImmutableField rule doesn't report against volatile variables
    Fixed bug 1693924 - Type resolution now works for implicit imports
    Fixed bug 1705716 - Annotation declarations now trigger a new scope level in the symbol table.
    Fixed bug 1743938 - False +: InsufficientStringBufferDeclaration with multiply
    Fixed bug 1657957 - UseStringBufferForStringAppends now catches self-assignments
    Applied patch 1612455 - RFE 1411022 CompareObjectsWithEquals now catches the case where comparison is against new Object
    Implemented RFE 1562230 - Added migration rule to check for instantiation of Short/Byte/Long
    Implemented RFE 1627581 - SuppressWarnings("unused") now suppresses all warnings in unusedcode.xml
    XPath rules are now chained together for an extra speedup in processing
    PMD now requires JDK 1.5 to be compiled. Java 1.4 support is provided using Retroweaver
    - PMD will still analyze code from earlier JDKs
    - to run pmd with 1.4, use the files from the java14 directory (weaved pmd jar and support files)
    TypeResolution now looks at some ASTName nodes.
    Memory footprint reduced: most renderers now use less memory by generating reports on the fly.
    Ant task now takes advantage of multithreading code and on the fly renderers
    Ant task now logs more debug info when using -verbose
    PMD command line now has -benchmark: output a benchmark report upon completion; default to System.err


## December 19, 2006 - 3.9:

    New rules:
        Basic ruleset: BigIntegerInstantiation, AvoidUsingOctalValues
        Codesize ruleset: NPathComplexity, NcssTypeCount, NcssMethodCount, NcssConstructorCount
        Design ruleset: UseCollectionIsEmpty
        Strings ruleset: StringBufferInstantiationWithChar
        Typeresolution ruleset: Loose Coupling - This is a re-implementation using the new Type Resolution facility
    Fixed bug 1610730 - MisplacedNullCheck now catches more cases
    Fixed bug 1570915 - AvoidRethrowingException no longer reports a false positive for certain nested exceptions.
    Fixed bug 1571324 - UselessStringValueOf no longer reports a false positive for additive expressions.
    Fixed bug 1573795 - PreserveStackTrace doesn't throw CastClassException on exception with 0 args
    Fixed bug 1573591 - NonThreadSafeSingleton doesn't throw NPE when using this keyword
    Fixed bug 1371753 - UnnecessaryLocalBeforeReturn is now less aggressive in its reporting.
    Fixed bug 1566547 - Annotations with an empty MemberValueArrayInitializer are now parsed properly.
    Fixed bugs 1060761 / 1433119 & RFE 1196954 - CloseResource now takes an optional parameter to identify closure methods
    Fixed bug 1579615 - OverrideBothEqualsAndHashcode no longer throws an Exception on equals methods that don't have Object as a parameter type.
    Fixed bug 1580859 - AvoidDecimalLiteralsInBigDecimalConstructor now catches more cases.
    Fixed bug 1581123 - False +: UnnecessaryWrapperObjectCreation.
    Fixed bug 1592710 - VariableNamingConventions no longer reports false positives on certain enum declarations.
    Fixed bug 1593292 - The CPD GUI now works with the 'by extension' option selected.
    Fixed bug 1560944 - CPD now skips symlinks.
    Fixed bug 1570824 - HTML reports generated on Windows no longer contain double backslashes.  This caused problems when viewing those reports with Apache.
    Fixed bug 1031966 - Re-Implemented CloneMethodMustImplementCloneable as a typeresolution rule. This rule can now detect super classes/interfaces which are cloneable
    Fixed bug 1571309 - Optional command line options may be used either before or after the mandatory arguments
    Applied patch 1551189 - SingularField false + for initialization blocks
    Applied patch 1573981 - false + in CloneMethodMustImplementCloneable
    Applied patch 1574988 - false + in OverrideBothEqualsAndHashcode
    Applied patch 1583167 - Better test code management. Internal JUnits can now be written in XML's
    Applied patch 1613674 - Support classpaths with spaces in pmd.bat
    Applied patch 1615519 - controversial/DefaultPackage XPath rule is wrong
    Applied patch 1615546 - Added option to command line to write directly to a file
    Implemented RFE 1566313 - Command Line now takes minimumpriority attribute to filter out rulesets
    PMD now requires JDK 1.4 to run
    - PMD will still analyze code from earlier JDKs
    - PMD now uses the built-in JDK 1.4 regex utils vs Jakarta ORO
    - PMD now uses the JDK javax.xml APIs rather than being hardcoded to use Xerces and Xalan
    SummaryHTML Report changes from Brent Fisher - now contains linePrefix to support source output from javadoc using "linksource"
    Fixed CSVRenderer - had flipped line and priority columns
    Fixed bug in Ant task - CSV reports were being output as text.
    Fixed false negatives in UseArraysAsList.
    Fixed several JDK 1.5 parsing bugs.
    Fixed several rules (exceptions on jdk 1.5 and jdk 1.6 source code).
    Fixed array handling in AvoidReassigningParameters and UnusedFormalParameter.
    Fixed bug in UselessOverridingMethod: false + when adding synchronization.
    Fixed false positives in LocalVariableCouldBeFinal.
    Fixed false positives in MethodArgumentCouldBeFinal.
    Modified annotation suppression to use @SuppressWarning("PMD") to suppress all warnings and @SuppressWarning("PMD.UnusedLocalVariable") to suppress a particular rule's warnings.
    Rules can now call RuleContext.getSourceType() if they need to make different checks on JDK 1.4 and 1.5 code.
    CloseResource rule now checks code without java.sql import.
    ArrayIsStoredDirectly rule now checks Constructors
    undo/redo added to text areas in Designer.
    Better 'create rule XML' panel in Designer.
    use of entrySet to iterate over Maps.
    1.6 added as a valid option for targetjdk.
    PMD now allows rules to use Type Resolution. This was referenced in patch 1257259.
    Renderers use less memory when generating reports.
    New DynamicXPathRule class to speed up XPath based rules by providing a base type for the XPath expression.
    Multithreaded processing on multi core or multi cpu systems.
    Performance Refactoring, XPath rules re-written as Java:
        AssignmentInOperand
        AvoidDollarSigns
        DontImportJavaLang
        DontImportSun
        MoreThanOneLogger
        SuspiciousHashcodeMethodName
        UselessStringValueOf


## October 4, 2006 - 3.8:

    New rules:
        Basic ruleset: BrokenNullCheck
        Strict exceptions ruleset: AvoidRethrowingException
        Optimizations ruleset: UnnecessaryWrapperObjectCreation
        Strings ruleset: UselessStringValueOf
    Fixed bug 1498910 - AssignmentInOperand no longer has a typo in the message.
    Fixed bug 1498960 - DontImportJavaLang no longer reports static imports of java.lang members.
    Fixed bug 1417106 - MissingBreakInSwitch no longer flags stmts where every case has either a return or a throw.
    Fixed bug 1412529 - UncommentedEmptyConstructor no longer flags private constructors.
    Fixed bug 1462189 - InsufficientStringBufferDeclaration now resets when it reaches setLength the same way it does at a Constructor
    Fixed bug 1497815 - InsufficientStringBufferDeclaration rule now takes the length of the constructor into account, and adds the length of the initial string to its initial length
    Fixed bug 1504842 - ExceptionSignatureDeclaration no longer flags methods starting with 'test'.
    Fixed bug 1516728 - UselessOverridingMethod no longer raises an NPE on methods that use generics.
    Fixed bug 1522054 - BooleanInstantiation now detects instantiations inside method calls.
    Fixed bug 1522056 - UseStringBufferForStringAppends now flags appends which occur in static initializers and constructors
    Fixed bug 1526530 - SingularField now finds fields which are hidden at the method or static level
    Fixed bug 1529805 - UnusedModifier no longer throws NPEs on JDK 1.5 enums.
    Fixed bug 1531593 - UnnecessaryConversionTemporary no longer reports false positives when toString() is invoked inside the call to 'new Long/Integer/etc()'.
    Fixed bug 1512871 - Improved C++ tokenizer error messages - now they include the filename.
    Fixed bug 1531152 - CloneThrowsCloneNotSupportedException now reports the proper line number.
    Fixed bug 1531236 - IdempotentOperations reports fewer false positives.
    Fixed bug 1544564 - LooseCoupling rule now checks for ArrayLists
    Fixed bug 1544565 - NonThreadSafeSingleton now finds if's with compound statements
    Fixed bug 1561784 - AbstractOptimizationRule no longer throws ClassCastExceptions on certain postfix expressions.
    Fixed a bug in AvoidProtectedFieldInFinalClass - it no longer reports false positives for protected fields in inner classes.
    Fixed a bug in the C++ grammar - the tokenizer now properly recognizes macro definitions which are followed by a multiline comment.
    Modified C++ tokenizer to use the JavaCC STATIC option; this results in about a 30% speedup in tokenizing.
    Implemented RFE 1501850 - UnusedFormalParameter now catches cases where a parameter is assigned to but not used.
    Applied patch 1481024 (implementing RFE 1490181)- NOPMD messages can now be reported with a user specified msg, e.g., //NOPMD - this is expected
    Added JSP support to the copy/paste detector.
    Placed JSF/JSP ruleset names in rulesets/jsprulesets.properties
    Added the image to the ASTEnumConstant nodes.
    Added new XSLT stylesheet for CPD XML->HTML from Max Tardiveau.
    Refactored UseIndexOfChar to extract common functionality into AbstractPoorMethodCall.
    Improved CPD GUI and Designer look/functionality; thanks to Brian Remedios for the changes!
    Rewrote the NOPMD mechanism to collect NOPMD markers as the source file is tokenized.  This eliminates an entire scan of each source file.
    Applied patch from Jason Bennett to enhance CyclomaticComplexity rule to account for conditional or/and nodes, do stmts, and catch blocks.
    Applied patch from Xavier Le Vourch to reduce false postives from CloneMethodMustImplementCloneable.
    Updated Jaxen library to beta 10.
    Performance Refactoring, XPath rules re-written as Java:
        BooleanInstantiation
        UselessOperationOnImmutable
        OverrideBothEqualsAndHashcode
        UnnecessaryReturn
        UseStringBufferForStringAppends
        SingularField
        NonThreadSafeSingleton


## June 1, 2006 - 3.7:

    New rules:
        Basic-JSP ruleset: DuplicateJspImport
        Design ruleset: PreserveStackTrace
        J2EE ruleset: UseProperClassLoader
    Implemented RFE 1462019 - Add JSPs to Ant Task
    Implemented RFE 1462020 - Add JSPs to Designer
    Fixed bug 1461426 InsufficientStringBufferDeclaration does not consider paths
    Fixed bug 1462184 False +: InsufficientStringBufferDeclaration - wrong size
    Fixed bug 1465574 - UnusedPrivateMethod no longer reports false positives when a private method is called from a method with a parameter of the same name.
    Fixed bug 1114003 - UnusedPrivateMethod no longer reports false positives when two methods have the same name and number of arguments but different types.  The fix causes PMD to miss a few valid cases, but, c'est la vie.
    Fixed bug 1472843 - UnusedPrivateMethod no longer reports false positives when a private method is only called from a method that contains a variable with the same name as that method.
    Fixed bug 1461442 - UseAssertSameInsteadOfAssertTrue now ignores comparisons to null; UseAssertNullInsteadOfAssertTrue will report those.
    Fixed bug 1474778 - UnnecessaryCaseChange no longer flags usages of toUpperCase(Locale).
    Fixed bug 1423429 - ImmutableField no longer reports false positives on variables which can be set via an anonymous inner class that is created in the constructor.
    Fixed major bug in CPD; it was not picking up files other than .java or .jsp.
    Fixed a bug in CallSuperInConstructor; it now checks inner classes/enums more carefully.
    Fixed a bug in VariableNamingConventions; it was not setting the warning message properly.
    Fixed bug in C/C++ parser; a '$' is now allowed in an identifier.  This is useful in VMS.
    Fixed a symbol table bug; PMD no longer crashes on enumeration declarations in the same scope containing the same field name
    Fixed a bug in ASTVariableDeclaratorId that triggered a ClassCastException if a annotation was used on a parameter.
    Added RuleViolation.getBeginColumn()/getEndColumn()
    Added an optional 'showSuppressed' item to the Ant task; this is false by default and toggles whether or not suppressed items are shown in the report.
    Added an IRuleViolation interface and modified various code classes (include Renderer implementations and Report) to use it.
    Modified JJTree grammar to use conditional node descriptors for various expression nodes and to use node suppression for ASTModifier nodes; this replaces a bunch of DiscardableNodeCleaner hackery.  It also fixed bug 1445026.
    Modified C/CPP grammar to only build the lexical analyzer; we're not using the parser for CPD, just the token manager.  This reduces the PMD jar file size by about 50 KB.


## March 29, 2006 - 3.6:

    New rules:
        Basic ruleset: AvoidThreadGroup
        Design ruleset: UnsynchronizedStaticDateFormatter
        Strings ruleset: InefficientEmptyStringCheck, InsufficientStringBufferDeclaration
        JUnit ruleset: SimplifyBooleanAssertion
        Basic-JSF ruleset: DontNestJsfInJstlIteration
        Basic-JSP ruleset: NoLongScripts, NoScriptlets, NoInlineStyleInformation, NoClassAttribute, NoJspForward, IframeMissingSrcAttribute, NoHtmlComments
    Fixed bug 1414985 - ConsecutiveLiteralAppends now checks for intervening references between appends.
    Fixed bug 1418424 - ConsecutiveLiteralAppends no longer flags appends in separate methods.
    Fixed bug 1416167 - AppendCharacterWithChar now catches cases involving escaped characters.
    Fixed bug 1421409 - Ant task now has setter to allow minimumPriority attribute to be used.
    Fixed bug 1416164 - InefficientStringBuffering no longer reports false positives on the three argument version of StringBuffer.append().
    Fixed bug 1415326 - JUnitTestsShouldContainAsserts no longer errors out on JDK 1.5 generics.
    Fixed bug 1415333 - CyclomaticComplexity no longer errors out on JDK 1.5 enums.
    Fixed bug 1415663 - PMD no longer fails to parse abstract classes declared in a method.
    Fixed bug 1433439 - UseIndexOfChar no longer reports false positives on case like indexOf('a' + getFoo()).
    Fixed bug 1435218 - LoggerIsNotStaticFinal no longer reports false positives for local variables.
    Fixed bug 1413745 - ArrayIsStoredDirectly no longer reports false positives for array deferences.
    Fixed bug 1435751 - Added encoding type of UTF-8 to the CPD XML file.
    Fixed bug 1441539 - ConsecutiveLiteralAppends no longer flags appends() involving method calls.
    Fixed bug 1339470 - PMD no longer fails to parse certain non-static initializers.
    Fixed bug 1425772 - PMD no longer fails with errors in ASTFieldDeclaration when parsing some JDK 1.5 code.
    Fixed bugs 1448123 and 1449175 - AvoidFieldNameMatchingTypeName, SingularField, TooManyFields, and AvoidFieldNameMatchingMethodName no longer error out on enumerations.
    Fixed bug 1444654 - migrating_to_14 and migrating_to_15 no longer refer to rule tests.
    Fixed bug 1445231 - TestClassWithoutTestCases: no longer flags abstract classes.
    Fixed bug 1445765 - PMD no longer uses huge amounts of memory.  However, you need to use RuleViolation.getBeginLine(); RuleViolation.getNode() is no more.
    Fixed bug 1447295 - UseNotifyAllInsteadOfNotify no longer flags notify() methods that have a parameter.
    Fixed bug 1455965 - MethodReturnsInternalArray no longer flags variations on 'return new Object[] {}'.
    Implemented RFE 1415487 - Added a rulesets/releases/35.xml ruleset (and similar rulesets for previous releases) contains rules new to PMD v3.5
    Wouter Zelle fixed a false positive in NonThreadSafeSingleton.
    Wouter Zelle fixed a false positive in InefficientStringBuffering.
    The CPD Ant task now supports an optional 'language' attribute.
    Removed some ill-advised casts from the parsers.
    Fixed bug in CallSuperInConstructor; it no longer flag classes without extends clauses.
    Fixed release packaging; now entire xslt/ directory contents are included.
    Added more XSLT from Dave Corley - you can use them to filter PMD reports by priority level.
    You can now access the name of a MemberValuePair node using getImage().
    PositionLiteralsFirstInComparisons was rewritten in XPath.
    Added a getVersionString method to the TargetJDKVersion interface.
    Added an option '--targetjdk' argument to the Benchmark utility.
    Applied a patch from Wouter Zelle to clean up the Ant Formatter class, fix a TextRenderer bug, and make toConsole cleaner.
    Rewrote AvoidCallingFinalize in Java; fixed bug and runs much faster, too.
    Uploaded ruleset schema to http://pmd.sf.net/ruleset_xml_schema.xsd
    UseIndexOfChar now catches cases involving lastIndexOf.
    Rules are now run in the order in which they're listed in a ruleset file.  Internally, they're now stored in a List vs a Set, and RuleSet.getRules() now returns a Collection.
    Upgraded to JUnit version 3.8.2.


## Jan 25, 2006 - 3.5:

    New rules:
     Basic ruleset: UselessOperationOnImmutable, MisplacedNullCheck, UnusedNullCheckInEquals
     Migration ruleset: IntegerInstantiation
     JUnit ruleset: UseAssertNullInsteadOfAssertTrue
     Strings ruleset: AppendCharacterWithChar, ConsecutiveLiteralAppends, UseIndexOfChar
     Design ruleset: AvoidConstantsInterface
     Optimizations ruleset: UseArraysAsList, AvoidArrayLoops
     Controversial ruleset: BooleanInversion
    Fixed bug 1371980 - InefficientStringBuffering no longer flags StringBuffer methods other than append().
    Fixed bug 1277373 - InefficientStringBuffering now catches more cases.
    Fixed bug 1376760 - InefficientStringBuffering no longer throws a NullPointerException when processing certain expressions.
    Fixed bug 1371757 - Misleading example in AvoidSynchronizedAtMethodLevel
    Fixed bug 1373510 - UseAssertSameInsteadOfAssertTrue no longer has a typo in its message, and its message is more clear.
    Fixed bug 1375290 - @SuppressWarnings annotations are now implemented correctly; they accept one blank argument to suppress all warnings.
    Fixed bug 1376756 - UselessOverridingMethod no longer throws an exception on overloaded methods.
    Fixed bug 1378358 - StringInstantiation no longer throws ClassCastExceptions on certain allocation patterns.
    Fixed bug 1371741 - UncommentedEmptyConstructor no longer flags constructors that consist of a this() or a super() invocation.
    Fixed bug 1277373 - InefficientStringBuffering no longer flags concatenations that involve a static final String.
    Fixed bug 1379701 - CompareObjectsWithEquals no longer flags comparisons of array elements.
    Fixed bug 1380969 - UnusedPrivateMethod no longer flags private static methods that are only invoked in a static context from a field declaration.
    Fixed bug 1384594 - Added a 'prefix' property for BeanMembersShouldSerializeRule
    Fixed bug 1394808 - Fewer missed hits for AppendCharacterWithChar and InefficientStringBuffering, thanks to Allan Caplan for catching these
    Fixed bug 1400754 - A NPE is no longer thrown on certain JDK 1.5 enum usages.
    Partially fixed bug 1371753 - UnnecessaryLocalBeforeReturn message now reflects the fact that that rule flags all types
    Fixed a bug in UseStringBufferLength; it no longers fails with an exception on expressions like StringBuffer.toString.equals(x)
    Fixed a bug in CPD's C/C++ parser so that it no longer fails on multi-line literals; thx to Tom Judge for the nice patch.
    CPD now recognizes '--language c' and '--language cpp' as both mapping to the C/C++ parser.
    Modified renderers to support disabling printing of suppressed warnings.  Introduced a new AbstractRenderer class that all Renderers can extends to get the current behavior - that is, suppressed violations are printed.
    Implemented RFE 1375435 - you can now embed regular expressions inside XPath rules, i.e., //ClassOrInterfaceDeclaration[matches(@Image, 'F?o')].
    Added current CLASSPATH to pmd.bat.
    UnusedFormalParameter now catches unused constructor parameters, and its warning message now reflects whether it caught a method or a constructor param.
    Rebuilt JavaCC parser with JavaCC 4.0.
    Added jakarta-oro-2.0.8.jar as a new dependency to support regular expression in XPath rules.
    Ant task now supports a 'minimumPriority' attribute; only rules with this priority or higher will be run.
    Renamed Ant task 'printToConsole' attribute to 'toConsole' and it can only be used inside a formatter element.
    Added David Corley's Javascript report, more details are here: http://tomcopeland.blogs.com/juniordeveloper/2005/12/demo_of_some_ni.html


## November 30, 2005 - 3.4:

    New rules:
     Basic ruleset: ClassCastExceptionWithToArray, AvoidDecimalLiteralsInBigDecimalConstructor
     Design ruleset: NonThreadSafeSingleton, UncommentedEmptyMethod, UncommentedEmptyConstructor
     Controversial ruleset: DefaultPackage
     Naming ruleset: MisleadingVariableName
     Migration ruleset: ReplaceVectorWithList, ReplaceHashtableWithMap, ReplaceEnumerationWithIterator, AvoidEnumAsIdentifier, AvoidAssertAsIdentifier
     Strings ruleset: UseStringBufferLength
    Fixed bug 1292745 - Removed unused source file ExceptionTypeChecking.java
    Fixed bug 1292609 - The JDK 1.3 parser now correctly handles certain 'assert' usages.  Also added a 'JDK 1.3' menu item to the Designer.
    Fixed bug 1292689 - Corrected description for UnnecessaryLocalBeforeReturn
    Fixed bug 1293157 - UnusedPrivateMethod no longer reports false positives for private methods which are only invoked from static initializers.
    Fixed bug 1293277 - Messages that used 'pluginname' had duplicated messages.
    Fixed bug 1291353 - ASTMethodDeclaration isPublic/isAbstract methods always return true.  The syntactical modifier - i.e., whether or not 'public' was used in the source code in the method declaration - is available via 'isSyntacticallyPublic' and 'isSyntacticallyAbstract'
    Fixed bug 1296544 - TooManyFields no longer checks the wrong property value.
    Fixed bug 1304739 - StringInstantiation no longer crashes on certain String constructor usages.
    Fixed bug 1306180 - AvoidConcatenatingNonLiteralsInStringBuffer no longer reports false positives on certain StringBuffer usages.
    Fixed bug 1309235 - TooManyFields no longer includes static finals towards its count.
    Fixed bug 1312720 - DefaultPackage no longer flags interface fields.
    Fixed bug 1312754 - pmd.bat now handles command line arguments better in WinXP.
    Fixed bug 1312723 - Added isSyntacticallyPublic() behavior to ASTFieldDeclaration nodes.
    Fixed bug 1313216 - Designer was not displaying 'final' attribute for ASTLocalVariableDeclaration nodes.
    Fixed bug 1314086 - Added logging-jakarta-commons as a short name for rulesets/logging-jakarta-commons.xml to SimpleRuleSetNameMapper.
    Fixed bug 1351498 - Improved UnnecessaryCaseChange warning message.
    Fixed bug 1351706 - CompareObjectsWithEquals now catches more cases.
    Fixed bug 1277373 (and 1347286) - InefficientStringBuffering now flags fewer false positives.
    Fixed bug 1363447 - MissingBreakInSwitch no longer reports false positives for switch statements where each switch label has a return statement.
    Fixed bug 1363458 - MissingStaticMethodInNonInstantiatableClass no longer reports cases where there are public static fields.
    Fixed bug 1364816 - ImmutableField no longer reports false positives for fields assigned in an anonymous inner class in a constructor.
    Implemented RFE 1311309 (and 1119854) - Suppressed RuleViolation counts are now included in the reports.
    Implemented RFE 1220371 - Rule violation suppression via annotations.  Per the JLS, @SuppressWarnings can be placed before the following nodes: TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE.
    Implemented RFE 1275547 - OverrideBothEqualsAndHashcode now skips Comparator implementations.
    Applied patch 1306999 - Renamed CloseConnection to CloseResource and added support for checking Statement and ResultSet objects.
    Applied patch 1344754 - EmptyCatchBlock now skips catch blocks that contain comments.  This is also requested in RFE 1347884.
    Renamed AvoidConcatenatingNonLiteralsInStringBuffer to InefficientStringBuffering; new name is a bit more concise.
    Modified LongVariable; now it has a property which can be used to override the minimum reporting value.
    Improved CPD XML report.
    CPD no longer skips header files when checking C/C++ code.
    Reworked CPD command line arguments; old-style arguments will still work for one more version, though.
    Lots of documentation improvements.


## September 15, 2005 - 3.3:

    New rules:
        Design: PositionLiteralsFirstInComparisons,  UnnecessaryLocalBeforeReturn
        Logging-jakarta-commons: ProperLogger
        Basic: UselessOverridingMethod
        Naming: PackageCase, NoPackage
        Strings: UnnecessaryCaseChange
    Implemented RFE 1220171 - rule definitions can now contain a link to an external URL for more information on that rule - for example, a link to the rule's web page.  Thanks to Wouter Zelle for designing and implementing this!
    Implemented RFE 1230685 - The text report now includes parsing errors even if no rule violations are reported
    Implemented RFE 787860 - UseSingleton now accounts for JUnit test suite declarations.
    Implemented RFE 1097090 - The Report object now contains the elapsed time for running PMD.  This shows up in the XML report as an elapsedTime attribute in the pmd element in the format '2m 5s' or '1h 5h 35s' or '25s' .
    Implemented RFE 1246338 - CPD now handles SCCS directories and NTFS junction points.
    Fixed bug 1226858 - JUnitAssertionsShouldIncludeMessage now checks calls to assertFalse.
    Fixed bug 1227001 - AvoidCallingFinalize no longer flags calls to finalize() within finalizers.
    Fixed bug 1229755 - Fixed typo in ArrayIsStoredDirectly description.
    Fixed bug 1229749 - Improved error message when an external rule is not found.
    Fixed bug 1224849 - JUnitTestsShouldContainAsserts no longer skips method declarations which include explicit throws clauses.
    Fixed bug 1225492 - ConstructorCallsOverridableMethod now reports the correct method name.  dvholten's examples in RFE 1235562 also helped with this a great deal.
    Fixed bug 1228589 - DoubleCheckedLocking and ExceptionSignatureDeclaration no longer throw ClassCastExceptions on method declarations that declare generic return types.
    Fixed bug 1235299 - NullAssignment no longer flags null equality comparisons in ternary expressions.
    Fixed bug 1235300 - NullAssignment no longer flags assignments to final fields.
    Fixed bug 1240201 - The UnnecessaryParentheses message is no longer restricted to return statements.
    Fixed bug 1242290 - The JDK 1.5 parser no longer chokes on nested enumerations with a constructor.
    Fixed bug 1242544 - SimplifyConditional no longer flags null checks that precede an instanceof involving an array dereference.
    Fixed bug 1242946 - ArrayIsStoredDirectly no longer reports false positives for equality expression comparisons.  As a bonus, its message now includes the variable name :-)
    Fixed bug 1232648 - MethodReturnsInternalArray no longer reports false positives on return statement expressions that involve method invocations on an internal array.
    Fixed bug 1244428 - MissingStaticMethodInNonInstantiatableClass no longer reports warnings for nested classes.  Some inner class cases will be missed, but false positives will be eliminated as well.
    Fixed bug 1244443 - EqualsNull now catches more cases.
    Fixed bug 1250949 - The JDK 1.5 parser no longer chokes on annotated parameters and annotated local variables.
    Fixed bug 1245139 - TooManyFields no longer throws a ClassCastException when processing anonymous classes.
    Fixed bug 1251256 - ImmutableField no longer skips assignments in try blocks on methods (which led to false positives).
    Fixed bug 1250949 - The JDK 1.5 parser no longer chokes on AnnotationTypeMemberDeclaration with a default value.
    Fixed bug 1245367 - ImmutableField no longer triggers on assignments in loops in constructors.
    Fixed bug 1251269 - AvoidConcatenatingNonLiteralsInStringBuffer no longer triggers on StringBuffer constructors like 'new StringBuffer(1 + getFoo());'
    Fixed bug 1244570 - AvoidConcatenatingNonLiteralsInStringBuffer no longer triggers on certain AST patterns involving local variable declarations inside Statement nodes.
    Fixed bug 695344 - StringInstantiation no longer triggers on the String(byte[]) constructor.
    Fixed bug 1114754 - UnusedPrivateMethod reports fewer false positives.
    Fixed bug 1290718 - Command line parameter documentation is now correct for targetjdk options.
    Applied patch 1228834 - XPath rules can now use properties to customize rules.  Thanks to Wouter Zelle for another great piece of work!
    Fixed a bug in RuleSetFactory that missed some override cases; thx to Wouter Zelle for the report and a fix.
    Fixed a bug in the grammar that didn't allow constructors to have type parameters, which couldn't parse some JDK 1.5 constructs.
    Fixed a bug in ImportFromSamePackage; now it catches the case where a class has an on-demand import for the same package it is in.
    Fixed a bug in CompareObjectsWithEquals; now it catches some local variable cases.
    Fixed a bug in CouplingBetweenObjects; it no longer triggers an exception (which is a bug in the symbol table layer) by calling getEnclosingClassScope() when the node in question isn't enclosed by one.
    Moved AvoidCallingFinalize from the design ruleset to the finalize ruleset and then deleted redundant ExplicitCallToFinalize rule from the finalize ruleset.
    Deleted redundant ExceptionTypeChecking rule from the strictexception ruleset; use AvoidInstanceofChecksInCatchClause in the design ruleset instead.
    Added some new XSLT scripts that create nifty HTML output; thanks to Wouter Zelle for the code.
    Improved UseCorrectExceptionLogging; thx to Wouter Zelle for the new XPath.
    Improved warning message from UnusedPrivateMethod.
    Improved EmptyIfStmt; now it catches the case where an IfStatement is followed by an EmptyStatement node.
    The Ant task now accepts the short names of rulesets (e.g., unusedcode for rulesets/unusedcode.xml).
    Removed unnecessary '.html' suffix from displayed filenames when the linkPrefix attribute is used with the HTML renderer.
    Added an optional 'description' attribute to the 'property' element in the ruleset XML files.
    Added a simplified SimpleNode.addViolation() method to reduce duplicated rule violation creation code.
    Moved from jaxen-1.0-fcs.jar/saxpath-1.0-fcs.jar to jaxen-1.1-beta-7.jar.  This yielded a 20% speed increase in the basic ruleset!


## June 21, 2005 - 3.2:

    New rules: UseCorrectExceptionLogging (logging-jakarta-commons ruleset), AvoidPrintStackTrace (logging-java ruleset), CompareObjectsWithEquals (design ruleset)
    Fixed bug 1201577 - PMD now correctly parses method declarations that return generic types.
    Fixed bug 1205709 - PMD no longer takes a long time to report certain parsing errors.
    Fixed bug 1052356 - ImmutableField no longer triggers on fields which are assigned to in a constructor's try statement.
    Fixed bug 1215854 - Package/class/method names are now filled in whenever possible, and the XML report includes all three.
    Fixed bug 1209719 - MethodArgumentCouldBeFinal no longer triggers on arguments which are modified using postfix or prefix expressions.  A bug in AvoidReassigningParameters was also fixed under the same bug id.
    Fixed bug 1188386 - MethodReturnsInternalArray no longer flags returning a local array declaration.
    Fixed bug 1172137 - PMD no longer locks up when generating a control flow graph for if statements with labelled breaks.
    Fixed bug 1221094 - JUnitTestsShouldContainAsserts no longer flags static methods.
    Fixed bug 1217028 - pmd.bat now correctly passes parameters to PMD.
    Implemented RFE 1188604 - AvoidThrowingCertainExceptionTypes has been split into AvoidThrowingRawExceptionTypes and AvoidThrowingNullPointerException.
    Implemented RFE 1188369 - UnnecessaryBooleanAssertion now checks for things like 'assertTrue(!foo)'.  These should be changed to 'assertFalse(foo)' for clarity.
    Implemented RFE 1199622 - UnusedFormalParameter now defaults to only checking private methods unless a 'checkall' property is set.
    Implemented RFE 1220314 - the symbol table now includes some rudimentary type information.
    Break and continue statement labels (if present) are placed in the image field.
    Fixed bug which caused MissingSerialVersionUID to trigger on all interfaces that implemented other interfaces.
    Modified NullAssignmentRule to catch null assignments in ternary expressions.
    Added two new node types - ASTCatchStatement and ASTFinallyStatement.
    Modified rule XML definition; it no longer includes a symboltable attribute since the symbol table layer is now run for all files analyzed.
    Harden equality of AbstractRule and RuleSet objects (needed for the Eclipse plugin features)
    Change RuleSet.getRuleByName. Now return null instead of throwing a RuntimeException when the rule is not found
    Add .project and .classpath to the module so that it can be checkout as an Eclipse project


## May 10, 2005 - 3.1:

    New rules: SimplifyStartsWith, UnnecessaryParentheses, CollapsibleIfStatements, UseAssertEqualsInsteadOfAssertTrue,  UseAssertSameInsteadOfAssertTrue, UseStringBufferForStringAppends, SimplifyConditional, SingularField
    Fixed bug 1170535 - LongVariable now report variables longer than 17 characters, not 12.
    Fixed bug 1182755 - SystemPrintln no longer overreports problems.
    Fixed bug 1188372 - AtLeastOneConstructor no longer fires on interfaces.
    Fixed bug 1190508 - UnnecessaryBooleanAssertion no longer fires on nested boolean literals.
    Fixed bug 1190461 - UnusedLocal no longer misses usages which are on the RHS of a right bit shift operator.
    Fixed bug 1188371 - AvoidInstantiatingObjectsInLoops no longer fires on instantiations in loops when the 'new' keyword is preceded by a 'return' or a 'throw'.
    Fixed bug 1190526 - TooManyFields now accepts a property setting correctly, and default lower bound is 15 vs 10.
    Fixed bug 1196238 - UnusedImports no longer reports false positives for various JDK 1.5 java.lang subpackages.
    Fixed bug 1169731 - UnusedImports no longer reports false positives on types used inside generics.  This bug also resulted in a bug in ForLoopShouldBeWhileLoop being fixed, thanks Wim!
    Fixed bug 1187325 - UnusedImports no longer reports a false positive on imports which are used inside an Annotation.
    Fixed bug 1189720 - PMD no longer fails to parse generics that use 'member selectors'.
    Fixed bug 1170109 - The Ant task now supports an optional 'targetjdk' attribute that accepts values of '1.3', '1.4', or '1.5'.
    Fixed bug 1183032 - The XMLRenderer no longer throws a SimpleDateFormat exception when run with JDK 1.3.
    Fixed bug 1097256 - The XMLRenderer now supports optional encoding of UTF8 characters using the 'net.sourceforge.pmd.supportUTF8' environment variable.
    Fixed bug 1198832 - AbstractClassWithoutAbstractMethod no longer flags classes which implement interfaces since these can partially implement the interface and thus don't need to explicitly declare abstract methods.
    Implemented RFE 1193979 - BooleanInstantiation now catches cases like Boolean.valueOf(true)
    Implemented RFE 1171095 - LabeledStatement nodes now contain the image of the label.
    Implemented RFE 1176401 - UnusedFormalParameter now flags public methods.
    Implemented RFE 994338 - The msg produced by ConstructorCallsOverridableMethod now includes the offending method name.
    Modified command line parameters; removed -jdk15 and -jdk13 parameters and added a -'targetjdk [1.3|1.4|1.5]' parameter.
    Modified CSVRenderer to include more columns.
    Optimized rules: FinalFieldCouldBeStatic (115 seconds to 7 seconds), SuspiciousConstantFieldName (48 seconds to 14 seconds), UnusedModifer (49 seconds to 4 seconds)


## March 23, 2005 - 3.0:

    New rules: MissingSerialVersionUID, UnnecessaryFinalModifier, AbstractClassDoesNotContainAbstractMethod, MissingStaticMethodInNonInstantiatableClass, AvoidSynchronizedAtMethodLevel, AvoidCallingFinalize, UseNotifyAllInsteadOfNotify, MissingBreakInSwitch, AvoidInstanceofChecksInCatchClause, AvoidFieldNameMatchingTypeName, AvoidFieldNameMatchingMethodName, AvoidNonConstructorMethodsWithClassName, TestClassWithoutTestCases, TooManyFields, CallSuperInConstructor, UnnecessaryBooleanAssertion, UseArrayListInsteadOfVector
    Implemented RFE 1058039 - PMD's command line interface now accepts abbreviated names for the standard rulesets; for example 'java net.sourceforge.pmd.PMD /my/source/code/ text basic,unusedcode' would run the rulesets/basic.xml and the rulesets/unusedcode.xml rulesets on the source in /my/source/code and produce a text report.
    Implemented RFE 1119851 - PMD's Ant task now supports an 'excludeMarker' attribute.
    Fixed bug 994400 - False +: ConstructorCallsOverridableMethodRule, thanks to ereissner for reporting it
    Fixed bug 1146116 - JUnitTestsShouldIncludeAssert no longer crashes on inner Interface
    Fixed bug 1114625 - UnusedPrivateField no longer throws an NPE on standalone postfix expressions which are prefixed with 'this'.
    Fixed bug 1114020 - The Ant task now reports a complete stack trace when run with the -verbose flag.
    Fixed bug 1117983 - MethodArgumentCouldBeFinal no longer reports false positives on try..catch blocks.
    Fixed bug 797742 - PMD now parses JDK 1.5 source code.  Note that it's not perfect yet; more testing/bug reports are welcome!
    Fixed a bug - the StatisticalRule no longer 'merges' data points when using the 'topscore' property.
    Fixed a bug - the PMD Ant task's failOnRuleViolation attribute no longer causes a BuildException in the case when no rule violations occur.
    Modified the XSLT to add a summary section.
    Added Ruby support to CPD.
    Optimized various rules and wrote a benchmarking application; results are here - http://infoether.com/~tom/pmd_timing.txt


## February 1, 2005 - 2.3:

    Fixed bug 1113927 - ExceptionAsFlowControl no longer throws NPEs on code where a throw statement exists without a try statement wrapping it.
    Fixed bug 1113981 - AvoidConcatenatingNonLiteralsInStringBuffer no longer throws NPEs on code where an append appears as a child of an ExplicitConstructorInvocation node.
    Fixed bug 1114039 - AvoidInstantiatingObjectsInLoops's message no longer contains a spelling error.
    Fixed bug 1114029 - The 'optimization' rules no longer throw NPEs at various points.
    Fixed bug 1114251 - The 'sunsecure' rules no longer throw NPEs at various points.


## January 31, 2005 - 2.2:

    New rules: LocalVariableCouldBeFinal, MethodArgumentCouldBeFinal, AvoidInstantiatingObjectsInLoops, ArrayIsStoredDirectly, MethodReturnsInternalArray, AssignmentToNonFinalStatic, AvoidConcatenatingNonLiteralsInStringBuffer
    Fixed bug 1088459 - JUnitTestsShouldContainAsserts no longer throws ClassCastException on interface, native, and abstract method declarations.
    Fixed bug 1100059 - The Ant task now generates a small empty-ish report if there are no violations.
    Implemented RFE 1086168 - PMD XML reports now contain a version and timestamp attribute in the <pmd> element.
    Implemented RFE 1031950 - The PMD Ant task now supports nested ruleset tags
    Fixed a bug in the rule override logic; it no longer requires the "class" attribute of a rule be listed in the overrides section.
    Added 'ignoreLiterals' and 'ignoreIdentifiers' boolean options to the CPD task.
    Cleaned up a good bit of the symbol table code; thanks much to Harald Gurres for the patch.
    CPD now contains a generic copy/paste checker for programs in any language


## December 15, 2004 - 2.1:

    New rules: AvoidProtectedFieldInFinalClass, SystemPrintln
    Fixed bug 1050173 - ImmutableFieldRule no longer reports false positives for static fields.
    Fixed bug 1050286 - ImmutableFieldRule no longer reports false positives for classes which have multiple constructors only a subset of which set certain fields.
    Fixed bug 1055346 - ImmutableFieldRule no longer reports false positive on preinc/predecrement/postfix expressions.
    Fixed bug 1041739 - EmptyStatementNotInLoop no longer reports false positives for nested class declarations in methods.
    Fixed bug 1039963 - CPD no longer fails to parse C++ files with multi-line macros.
    Fixed bug 1053663 - SuspiciousConstantFieldName no longer reports false positives for interface members.
    Fixed bug 1055930 - CouplingBetweenObjectsRule no longer throws a NPE on interfaces
    Fixed a possible NPE in dfa.report.ReportTree.
    Implemented RFE 1058033 - Renamed run.[sh|bat] to pmd.[sh|bat].
    Implemented RFE 1058042 - XML output is more readable now.
    Applied patch 1051956 - Rulesets that reference rules using "ref" can now override various properties.
    Applied patch 1070733 - CPD's Java checker now has an option to ignore both literals and identifiers - this can help find large duplicate code blocks, but can also result in false positives.
    YAHTMLRenderer no longer has dependence on Ant packages.
    Modified the AST to correctly include PostfixExpression nodes.  Previously a statement like "x++;" was embedded in the parent StatementExpression node.
    Moved BooleanInstantiation from the design ruleset to the basic ruleset.
    Updated Xerces libraries to v2.6.2.
    Many rule names had the word "Rule" tacked on to the end.  Various folks thought this was a bad idea, so here are the new names of those rules which were renamed:
    - basic.xml: UnnecessaryConversionTemporary, OverrideBothEqualsAndHashcode, DoubleCheckedLocking
    - braces.xml: WhileLoopsMustUseBraces, IfElseStmtsMustUseBraces, ForLoopsMustUseBraces
    - clone.xml: ProperCloneImplementation
    - codesize.xml: CyclomaticComplexity, ExcessivePublicCount
    - controversial.xml: UnnecessaryConstructor, AssignmentInOperand, DontImportSun, SuspiciousOctalEscape
    - coupling.xml: CouplingBetweenObjects, ExcessiveImports, LooseCoupling
    - design.xml: UseSingleton, SimplifyBooleanReturns, AvoidReassigningParameters, ConstructorCallsOverridableMethod, AccessorClassGeneration, CloseConnection, OptimizableToArrayCall, IdempotentOperations. ImmutableField
    - junit.xml: JUnitAssertionsShouldIncludeMessage, JUnitTestsShouldIncludeAssert
    - logging-java.xml: MoreThanOneLogger, LoggerIsNotStaticFinal
    - naming.xml: ShortMethodName, VariableNamingConventions, ClassNamingConventions, AbstractNaming
    - strictexception.xml: ExceptionAsFlowControl, AvoidCatchingNPE, AvoidThrowingCertainExceptionTypes
    Continued working on JDK 1.5 compatibility - added support for static import statements, varargs, and the new for loop syntax
    - still TODO: generics and annotations (note that autoboxing shouldn't require a grammar change)
    - Good article on features: http://java.sun.com/developer/technicalArticles/releases/j2se15/

## October 19, 2004 - 2.0:

    New rules: InstantiationToGetClass, IdempotentOperationsRule, SuspiciousEqualsMethodName, SimpleDateFormatNeedsLocale, JUnitTestsShouldContainAssertsRule, SuspiciousConstantFieldName, ImmutableFieldRule, MoreThanOneLoggerRule, LoggerIsNotStaticFinalRule, UseLocaleWithCaseConversions
    Applied patch in RFE 992576 - Enhancements to VariableNamingConventionsRule
    Implemented RFE 995910 - The HTML report can now include links to HTMLlized source code - for example, the HTML generated by JXR.
    Implemented RFE 665824 - PMD now ignores rule violations in lines containing the string 'NOPMD'.
    Fixed bug in SimplifyBooleanExpressions - now it catches more cases.
    Fixed bugs in AvoidDuplicateLiterals - now it ignores small duplicate literals, its message is more helpful, and it catches more cases.
    Fixed bug 997893 - UnusedPrivateField now detects assignments to members of private fields as a usage.
    Fixed bug 1020199 - UnusedLocalVariable no longer flags arrays as unused if an assignment is made to an array slot.
    Fixed bug 1027133 - Now ExceptionSignatureDeclaration skips certain JUnit framework methods.
    Fixed bug 1008548 - The 'favorites' ruleset no longer contains a broken reference.
    Fixed bug 1045583 - UnusedModifier now correctly handles anonymous inner classes within interface field declarations.
    Partially fixed bug 998122 - CloseConnectionRule now checks for imports of java.sql before reporting a rule violation.
    Applied patch 1001694 - Now PMD can process zip/jar files of source code.
    Applied patch 1032927 - The XML report now includes the rule priority.
    Added data flow analysis facade from Raik Schroeder.
    Added two new optional attributes to rule definitions - symboltable and dfa.  These allow the symbol table and DFA facades to be configured on a rule-by-rule basis.  Note that if your rule needs the symbol table; you'll need to add symboltable="true" to your rule definition.  FWIW, this also results in about a 5% speedup for rules that don't need either layer.
    Added a "logging" ruleset - thanks to Miguel Griffa for the code!
    Enhanced the ASTViewer - and renamed it 'Designer' - to display data flows.
    Moved development environment to Maven 1.0.
    Moved development environment to Ant 1.6.2.  This is nice because using the new JUnit task attribute "forkmode='perBatch'" cuts test runtime from 90 seconds to 7 seconds.  Sweet.
    MethodWithSameNameAsEnclosingClass now reports a more helpful line number.

## July 14, 2004 - 1.9:

    New rules: CloneMethodMustImplementCloneable, CloneThrowsCloneNotSupportedException, EqualsNull, ConfusingTernary
    Created new "clone" ruleset and moved ProperCloneImplementationRule over from the design ruleset.
    Moved LooseCoupling from design.xml to coupling.xml.
    Some minor performance optimizations - removed some unnecessary casts from the grammar, simplified some XPath rules.
    Postfix expressions (i.e., x++) are now available in the grammar.  To access them, search for StatementExpressions with an image of "++" or "--" - i.e., in XPath, //StatementExpression[@Image="++"].  This is an odd hack and hopefully will get cleared up later.
    Ant task and CLI now used BufferedInputStreams.
    Converted AtLeastOneConstructor rule from Java code to XPath.
    Implemented RFE 743460: The XML report now contains the ruleset name.
    Implemented RFE 958714: Private field and local variables that are assigned but not used are now flagged as unused.
    Fixed bug 962782 - BeanMembersShouldSerializeRule no longer reports set/is as being a violation.
    Fixed bug 977022 - UnusedModifier no longer reports false positives for modifiers of nested classes in interfaces
    Fixed bug 976643 - IfElseStmtsMustUseBracesRule no longer reports false positives for certain if..else constructs.
    Fixed bug 985961 - UseSingletonRule now fires on classes which contain static fields
    Fixed bug 977031 - FinalizeDoesNotCallSuperFinalize no longer reports a false positive when a finalizer contains a call to super.finalize in a try {} finally {} block.

## May 19, 2004 - 1.8:

    New rules: ExceptionAsFlowControlRule, BadComparisonRule, AvoidThrowingCertainExceptionTypesRule, AvoidCatchingNPERule, OptimizableToArrayCallRule
    Major grammar changes - lots of new node types added, many superfluous nodes removed from the runtime AST.  Bug 786611 - http://sourceforge.net/tracker/index.php?func=detail&aid=786611&group_id=56262&atid=479921 - explains it a bit more.
    Fixed bug 786611 - Expressions are no longer over-expanded in the AST
    Fixed bug 874284 - The AST now contains tokens for bitwise or expressions - i.e., "|"

## April 22, 2004 - 1.7:

    Moved development environment to Maven 1.0-RC2.
    Fixed bug 925840 - Messages were no longer getting variable names plugged in correctly
    Fixed bug 919308 - XMLRenderer was still messed up; 'twas missing a quotation mark.
    Fixed bug 923410 - PMD now uses the default platform character set encoding; optionally, you can pass in a character encoding to use.
    Implemented RFE 925839 - Added some more detail to the UseSingletonRule.
    Added an optional 'failuresPropertyName' attribute to the Ant task.
    Refactored away duplicate copies of XPath rule definitions in regress/, yay!
    Removed manifest from jar file; it was only there for the Main-class attribute, and it's not very useful now since PMD has several dependencies.
    Began working on JDK 1.5 compatibility - added support for EnumDeclaration nodes.

## March 15, 2004 - 1.6:

    Fixed bug 895661 - XML reports containing error elements no longer have malformed XML.
    Fixed a bug in UnconditionalIfStatement - it no longer flags things like "if (x==true)".
    Applied Steve Hawkins' improvements to CPD:
    - Various optimizations; now it runs about 4 times faster!
    - fixed "single match per file" bug
    - tweaked source code slicing
    - CSV renderer
    Added two new renderers - SummaryHTMLRenderer and PapariTextRenderer.
    Moved development environment to Ant 1.6 and JavaCC 3.2.

## February 2, 2004 - 1.5:

    New rules: DontImportSunRule, EmptyFinalizer, EmptyStaticInitializer, AvoidDollarSigns, FinalizeOnlyCallsSuperFinalize, FinalizeOverloaded, FinalizeDoesNotCallSuperFinalize, MethodWithSameNameAsEnclosingClass, ExplicitCallToFinalize, NonStaticInitializer, DefaultLabelNotLastInSwitchStmt, NonCaseLabelInSwitchStatement, SuspiciousHashcodeMethodName, EmptyStatementNotInLoop, SuspiciousOctalEscapeRule
    FinalizeShouldBeProtected moved from design.xml to finalizers.xml.
    Added isTrue() to ASTBooleanLiteral.
    Added UnaryExpression to the AST.
    Added isPackagePrivate() to AccessNode.

## January 7, 2004 - 1.4:

    New rules: AbstractNamingRule, ProperCloneImplementationRule
    Fixed bug 840926 - AvoidReassigningParametersRule no longer reports a false positive when assigning a value to an array slot when the array is passed as a parameter to a method
    Fixed bug 760520 - RuleSetFactory is less strict about whitespace in ruleset.xml files.
    Fixed bug 826805 - JumbledIncrementorRule no longer reports a false positive when a outer loop incrementor is used as an array index
    Fixed bug 845343 - AvoidDuplicateLiterals now picks up cases when a duplicate literal appears in field declarations.
    Fixed bug 853409 - VariableNamingConventionsRule no longer requires that non-static final fields be capitalized
    Fixed a bug in OverrideBothEqualsAndHashcodeRule; it no longer reports a false positive when equals() is passed the fully qualified name of Object.
    Implemented RFE 845348 - UnnecessaryReturn yields more useful line numbers now
    Added a ruleset DTD and a ruleset XML Schema.
    Added 'ExplicitExtends' and 'ExplicitImplements' attributes to UnmodifiedClassDeclaration nodes.

## October 23, 2003 - 1.3:

    Relicensed under a BSD-style license.
    Fixed bug 822245 - VariableNamingConventionsRule now handles interface fields correctly.
    Added new rules: EmptySynchronizedBlock, UnnecessaryReturn
    ASTType now has an getDimensions() method.

## October 06, 2003 - 1.2.2:

    Added new rule: CloseConnectionRule
    Fixed bug 782246 - FinalFieldCouldBeStatic no longer flags fields in interfaces.
    Fixed bug 782235 - "ant -version" now prints more details when a file errors out.
    Fixed bug 779874 - LooseCouplingRule no longer triggers on ArrayList
    Fixed bug 781393 - VariableNameDeclaration no longer throws ClassCastExpression since ASTLocalVariableDeclaration now subclasses AccessNode
    Fixed bug 797243 - CPD XML report can no longer contain ]]> (CDEnd)
    Fixed bug 690196 - PMD now handles both JDK 1.3 and 1.4 code - i.e., usage of "assert" as an identifier.
    Fixed bug 805092 - VariableNamingConventionsRule no longer flags serialVersionUID as a violation
    Fixed bug - Specifying a non-existing rule format on the command line no longer results in a ClassNotFoundException.
    XPath rules may now include pluggable parameters.  This feature is very limited.  For now.
    Tweaked CPD time display field
    Made CPD text fields uneditable
    Added more error checking to CPD GUI input
    Added "dialog cancelled" check to CPD "Save" function
    Added Boris Gruschko's AST viewer.
    Added Jeff Epstein's TextPad integration.
    ASTType now has an isArray() method.

## August 1, 2003 - 1.2.1:

    Fixed bug 781077 - line number "-1" no longer appears for nodes with siblings.

## July 30, 2003 - 1.2:

    Added new rules: VariableNamingConventionsRule, MethodNamingConventionsRule, ClassNamingConventionsRule, AvoidCatchingThrowable, ExceptionSignatureDeclaration, ExceptionTypeChecking, BooleanInstantiation
    Fixed bug 583047 - ASTName column numbers are now correct
    Fixed bug 761048 - Symbol table now creates a scope level for anonymous inner classes
    Fixed bug 763529 - AccessorClassGenerationRule no longer crashes when given a final inner class
    Fixed bug 771943 - AtLeastOneConstructorRule and UnnecessaryConstructorRule no longer reports false positives on inner classes.
    Applied patch from Chris Webster to fix another UnnecessaryConstructorRule problem.
    Added ability to accept a comma-delimited string of files and directories on the command line.
    Added a CSVRenderer.
    Added a "-shortfilenames" argument to the PMD command line interface.
    Modified grammer to provide information on whether an initializer block is static.
    ASTViewer now shows node images and modifiers
    ASTViewer now saves last edited text to ~/.pmd_astviewer
    Moved the PMD Swing UI into a separate module - pmd-swingui.
    Updated license.txt to point to new location.

## June 19, 2003 - 1.1:

    Added new rules: FinalizeShouldBeProtected, FinalFieldCouldBeStatic, BeanMembersShouldSerializeRule
    Removed "verbose" attribute from PMD and CPD Ant tasks; now they use built in logging so you can do a "ant -verbose cpd" or "ant -verbose pmd".  Thanks to Philippe T'Seyen for the code.
    Added "excludes" feature to rulesets; thanks to Gael Marziou for the suggestion.
    Removed "LinkedList" from LooseCouplingRule checks; thx to Randall Schulz for the suggestion.
    CPD now processes PHP code.
    Added VBHTMLRenderer; thanks to Vladimir Bossicard for the code.
    Added "Save" item to CPD GUI; thanks to mcclain looney for the patch.
    Fixed bug 732592 - Ant task now accepts a nested classpath element.
    Fixed bug 744915 - UseSingletonRule no longer fires on abstract classes, thanks to Pablo Casado for the bug report.
    Fixed bugs 735396 and 735399 - false positives from ConstructorCallsOverridableMethodRule
    Fixed bug 752809 - UnusedPrivateMethodRule now catches unused private static methods, thanks to Conrad Roche for the bug report.

## April 17, 2003 - 1.05:

    Added new rules: ReturnFromFinallyBlock, SimplifyBooleanExpressions
    Added a new Ant task for CPD; thanks to Andy Glover for the code.
    Added ability to specify a class name as a renderer on the command line or in the formatter "type" attribute of the Ant task.
    Brian Ewins completely rewrote CPD using a portion of the Burrows-Wheeler Transform - it's much, much, much faster now.
    Rebuilt parser with JavaCC 3.0; made several parser optimizations.
    The Ant task now accepts a <classpath> element to aid in loading custom rulesets.  Thanks to Luke Francl for the suggestion.
    Fixed several bugs in UnnecessaryConstructorRule; thanks to Adam Nemeth for the reports and fixes.
    All test-data classes have been inlined into their respective JUnit tests.

## March 21, 2003 - 1.04

    Added new rules: ConstructorCallsOverridableMethodRule, AtLeastOneConstructorRule, JUnitAssertionsShouldIncludeMessageRule, DoubleCheckedLockingRule, ExcessivePublicCountRule, AccessorClassGenerationRule
    The Ant task has been updated; if you set "verbose=true" full stacktraces are printed.  Thx to Paul Roebuck for the suggestion.
    Moved JUnit rules into their own package - "net.sourceforge.pmd.rules.junit".
    Incorporated new ResourceLoader; thanks to Dave Fuller
    Incorporated new XPath-based rule definitions; thanks to Dan Sheppard for the excellent work.
    Fixed bug 697187 - Problem with nested ifs
    Fixed bug 699287 - Grammar bug; good catch by David Whitmore

## February 11, 2003 - 1.03

    Added new rules: CyclomaticComplexityRule, AssignmentInOperandRule
    Added numbering to the HTMLRenderer; thx to Luke Francl for the code.
    Added an optional Ant task attribute 'failOnRuleViolation'.  This stops the build if any rule violations are found.
    Added an XSLT script for processing the PMD XML report; thx to Mats for the code.
    The Ant task now determines whether the formatter toFile attribute is absolute or relative and routes the report appropriately.
    Moved several rules into a new "controversial" ruleset.
    Fixed bug 672742 - grammar typo was hosing up ASTConstructorDeclaration which was hosing up UseSingletonRule
    Fixed bug 674393 - OnlyOneReturn rule no longer counts returns that are inside anonymous inner classes as being inside the containing method.  Thx to C. Lamont Gilbert for the bug report.
    Fixed bug 674420 - AvoidReassigningParametersRule no longer counts parameter field reassignment as a violation.  Thx to C. Lamont Gilbert for the bug report.
    Fixed bug 673662 - The Ant task's "failOnError" attribute works again.  Changed the semantics of this attribute, though, so it fails the build if errors occurred.  A new attribute 'failOnRuleViolation' serves the purpose of stopping the build if rule violations are found.
    Fixed bug 676340 - Symbol table now creates new scope level when it encounters a switch statement.  See the bug for code details; generally, this bug would have triggered runtime exceptions on certain blocks of code.
    Fixed bug 683465 - JavaCC parser no longer has ability to throw java.lang.Error; now it only throws java.lang.RuntimeExceptions.  Thx to Gunnlaugur Thor Briem for a good discussion on this topic.
    Fixed bug in OverrideBothEqualsAndHashcodeRule - it no longer bails out with a NullPtrException on interfaces that declare a method signature "equals(Object)".  Thx to Don Leckie for catching that.

## January 22, 2003 - 1.02:

    Added new rules: ImportFromSamePackageRule, SwitchDensityRule, NullAssignmentRule, UnusedModifierRule, ForLoopShouldBeWhileLoopRule
    Updated LooseCouplingRule to check for usage of Vector; thx to Vladimir for the good catch.
    Updated AvoidDuplicateLiteralsRule to report the line number of the first occurrence of the duplicate String.
    Modified Ant task to use a formatter element; this lets you render a report in several formats without having to rerun PMD.
    Added a new Ant task attribute - shortFilenames.
    Modified Ant task to ignore whitespace in the ruleset attribute
    Added rule priority settings.
    Added alternate row colorization to HTML renderer.
    Fixed bug 650623 - the Ant task now uses relative directories for the report file
    Fixed bug 656944 - PMD no longer prints errors to System.out, instead it just rethrows any exceptions
    Fixed bug 660069 - this was a symbol table bug; thanks to mcclain looney for the report.
    Fixed bug 668119 - OverrideBothEqualsAndHashcodeRule now checks the signature on equals(); thanks to mcclain looney for the report.

## November 07 2002 - 1.01:

    Fixed bug 633879: EmptyFinallyBlockRule now handles multiple catch blocks followed by a finally block.
    Fixed bug 633892: StringToStringRule false positive exposed problem in symbol table usage to declaration code.
    Fixed bug 617971: Statistical rules no longer produce tons of false positives due to accumulated results.
    Fixed bug 633209: OnlyOneReturn rule no longer requires the return stmt to be the last statement.
    Enhanced EmptyCatchBlockRule to flag multiple consecutive empty catch blocks.
    Renamed AvoidStringLiteralsRule to AvoidDuplicateLiteralsRule.
    Modified Ant task to truncate file paths to make the HTML output neater.

## November 04 2002 - 1.0:

    Added new rules: StringToStringRule, AvoidReassigningParametersRule, UnnecessaryConstructorRule, AvoidStringLiteralsRule
    Fixed bug 631010: AvoidDeeplyNestedIfStmtsRule works correctly with if..else stmts now
    Fixed bug 631605: OnlyOneReturn handles line spillover now.
    Moved AvoidDeeplyNestedIfStmts from the braces ruleset to the design ruleset.
    Moved several rules from the design ruleset to the codesize ruleset.
    Added a new "favorites" ruleset.

## October 04 2002 - 1.0rc3:

    Added new rules: OnlyOneReturnRule, JumbledIncrementerRule, AvoidDeeplyNestedIfStmtsRule
    PMD is now built and tested with JUnit 3.8.1 and Ant 1.5.
    Added support for IntelliJ's IDEAJ.
    Fixed bug 610018 - StringInstantiationRule now allows for String(byte[], int, int) usage.
    Fixed bug 610693 - UnusedPrivateInstanceVariable handles parameter shadowing better.
    Fixed bug 616535 - Command line interface input checking is better now.
    Fixed bug 616615 - Command line interface allows the text renderer to be used now
    Fixed a bug - the statistics rules now handle interfaces better.

## September 12 2002 - 1.0rc2:

    Added new rules: JUnitSpellingRule, JUnitStaticSuiteRule, StringInstantiationRule
    Added new rulesets - junit, strings.
    Added a printToConsole attribute to the Ant task so that you can see the report right there in the Ant output.
    Fixed bug in PMD GUI - rules are now saved correctly.
    Fixed bug 597916 - CPD line counts are accurate now.

## September 09 2002 - 1.0rc1:

    Added new rules: UnusedImportsRule, EmptySwitchStmtRule, SwitchStmtsShouldHaveDefaultRule, IfStmtsMustUseBracesRule
    Fixed bug 597813 - Rule properties are now parsed correctly
    Fixed bug 597905 - UseSingletonRule now resets its state correctly
    Moved several rules into a new ruleset - braces.
    Improved CPD by removing import statements and package statements from the token set.
    Added Metrics API to the Report.
    Updated PMD GUI.

## August 16 2002 - 0.9:

    Added new rules: LongParameterListRule, SimplifyBooleanReturnsRule
    Enhanced statistics rules to support various ways of triggering rule violations
    Added rule customization via XML parameters
    Enhanced CopyAndPasteDetector; added a GUI
    Fixed bug 592060 - UnusedPrivateInstanceVariable handles explicitly referenced statics correctly
    Fixed bug 593849 - UnusedPrivateInstanceVariable handles nested classes better

## July 30 2002 - 0.8:

    Added new rule: UnusedFormalParameterRule
    Fixed bug 588083 - ForLoopsNeedBraces rule correctly handles a variety of for statement formats
    Added prototype of the copy and paste detector

## July 25 2002 - 0.7:

    Added new rules: UnusedPrivateMethodRule, WhileLoopsMustUseBracesRule, ForLoopsMustUseBracesRule, LooseCouplingRule
    Fixed bug 583482 - EmptyCatchBlock and EmptyFinallyBlock no longer report an incorrect line number.

## July 18 2002 - 0.6:

    Added new rules: ExcessiveClassLength, ExcessiveMethodLength
    DuplicateImportsRule now reports the correct line number.
    Fixed bug 582639 - Rule violations are now reported on the proper line
    Fixed bug 582509 - Removed unneeded throws clause
    Fixed bug 583009 - Now rulesets.properties is in the jar file

## July 15 2002 - 0.5:

    Added new rules: DontImportJavaLangRule, DuplicateImportsRule
    Added new ruleset: rulesets/imports.xml
    Changed sorting of RuleViolations to group Files together.
    Changed XML Renderer to improved format.
    Created DVSL Stylesheet for the new format.
    Moved the Cougaar rules out of the PMD core.
    Fixed bug 580093 - OverrideBothEqualsAndHashcodeRule reports a more correct line number.
    Fixed bug 581853 - UnusedLocalVariableRule now handles anonymous inner classes correctly.
    Fixed bug 580278 - this was a side effect of bug 581853.
    Fixed bug 580123 - UnusedPrivateInstanceVariable now checks for instance variable usage in inner classes.

## July 10 2002 - 0.4:

    Added new rules: OverrideBothEqualsAndHashcodeRule, EmptyTryBlock, EmptyFinallyBlock
    Reports are now sorted by line number
    RuleSets can now reference rules in other RuleSets
    Fixed bug 579718 - made 'ruleset not found' error message clearer.

## July 03 2002 - 0.3:

    Added new rules: UseSingletonRule, ShortVariableRule, LongVariableRule, ShortMethodNameRule
    Moved rules into RuleSets which are defined in XML files in the ruleset directory
    Ant task:
    -Added a 'failonerror' attribute
    -Changed 'rulesettype' to 'rulesetfiles'
    -Removed 'text' report format; only 'html' and 'xml' are available now

## June 27 2002 - 0.2:

    Added new rules: IfElseStmtsMustUseBracesRule, EmptyWhileStmtRule
    Modified command line interface to accept a rule set
    Fixed bug in EmptyCatchBlockRule
    Fixed typo in UnnecessaryConversionTemporaryRule
    Moved Ant task to the net.sourceforge.pmd.ant package
    Added new HTML report format

## June 25 2002 - 0.1:

    Initial release

The PMD book - $20 - http://pmdapplied.com/
