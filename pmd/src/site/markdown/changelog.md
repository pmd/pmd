# Changelog

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




