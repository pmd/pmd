# PMD Release Notes

## ????? - 5.6.0-SNAPSHOT

The PMD team is pleased to announce PMD 5.6.0.

The most significant changes are on analysis performance, support for Salesforce's Visualforce language
a whole new **Apex Security Rule Set** and the new **Braces Rule Set for Apex**.

We have added initial support for **incremental analysis**. The experimental feature allows
PMD to cache analysis results between executions to speed up the analysis for all
languages. New CLI flags and Ant options are available to configure it. Currently
*the feature is disabled by default*, but this may change as it matures.

Multithread performance has been enhanced by reducing thread-contention on a
bunch of areas. This is still an area of work, as the speedup of running
multithreaded analysis is still relatively small (4 threads produce less
than a 50% speedup). Future releases will keep improving on this area.

Once again, *Symbol Table* has been an area of great performance improvements.
This time we were able to further improve it's performance by roughly 10% on all
supported languages. In *Java* in particular, several more improvements were possible,
improving *Symbol Table* performance by a whooping 30%, that's over 5X faster
than PMD 5.5.1, when we first started working on it.

Java developers will also appreciate the revamp of `CloneMethodMustImplementCloneable`,
making it over 500X faster, and `PreserveStackTrace` which is now 7X faster.

### Table Of Contents

*   [New and noteworthy](#New_and_noteworthy)
    *   [Incremental Analysis](#Incremental_Analysis)
    *   [Visualforce Support](#Visualforce_support)
    *   [Apex Security Rule Set](#Apex_Security_Rule_Set)
    *   [Apex Braces Rule Set](#Apex_Braces_Rule_Set)
    *   [Apex Rule Suppression](#Apex_Rule_Suppression)
    *   [New Rules](#New_Rules)
    *   [Modified Rules](#Modified_Rules)
    *   [CPD Suppression](#CPD_Suppression)
    *   [CPD filelist command line option)(#CPD_filelist_command_line_option)
*   [Fixed Issues](#Fixed_Issues)
*   [API Changes](#API_Changes)
*   [External Contributions](#External_Contributions)

### New and noteworthy

#### Incremental Analysis

PMD now supports incremental analysis. Analysis results can be cached and reused between runs.
This allows PMD to skip files without violations that have remained unchanged. In future releases,
we plan to extend this behavior to unchanged files with violations too.

The cache is automatically invalidated if:
 * the used PMD version changes
 * the `auxclasspath` changed and any rules require type resolution
 * the configured rule set has changed

This feature is *incubating* and is disabled by default. It's only enabled if you
specifically configure a cache file.

To configure the cache file from CLI, a new `-cache <path/to/file>` flag has been added.

For Ant, a new `cacheLocation` attribute has been added. For instance:

```xml
    <target name="pmd">
        <taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask"/>
        <pmd cacheLocation="build/pmd/pmd.cache">
            <ruleset>rulesets/java/design.xml</ruleset>
            <ruleset>java-basic</ruleset>
            <formatter type="xml" toFile="c:\pmd_report.xml"/>
            <fileset dir="/usr/local/j2sdk1.4.1_01/src/">
                <include name="java/lang/*.java"/>
            </fileset>
        </pmd>
    </target>
```

#### Visualforce Support

Salesforce developers rejoice. To out growing Apex support we have added full Visualforce support.
Both CPD and PD are available. So far only a security ruleset is available (`vf-security`).

##### Visualforce Security Rule Set

###### VfUnescapeEl

The rule looks for Expression Language occurances printing unescaped values from the backend. These
could lead to XSS attacks.

###### VfCsrf

The rule looks for `<apex:page>` tags performing an action on page load, definish such `action`
through Expression Language, as doing so is vulnerable to CSRF attacks.

#### Apex Security Rule Set

A new ruleset focused on security has been added, consisting of a wide range of rules
to detect most common security problems.

##### ApexBadCrypto

The rule makes sure you are using randomly generated IVs and keys for `Crypto` calls.
Hard-wiring these values greatly compromises the security of encrypted data.

For instance, it would report violations on code such as:

```
public class without sharing Foo {
    Blob hardCodedIV = Blob.valueOf('Hardcoded IV 123');
    Blob hardCodedKey = Blob.valueOf('0000000000000000');
    Blob data = Blob.valueOf('Data to be encrypted');
    Blob encrypted = Crypto.encrypt('AES128', hardCodedKey, hardCodedIV, data);
}

```

##### ApexCRUDViolation

The rule validates you are checking for access permissions before a SOQL/SOSL/DML operation.
Since Apex runs in system mode not having proper permissions checks results in escalation of 
privilege and may produce runtime errors. This check forces you to handle such scenarios.

For example, the following code is considered valid:

```
public class Foo {
    public Contact foo(String status, String ID) {
        Contact c = [SELECT Status__c FROM Contact WHERE Id=:ID];

        // Make sure we can update the database before even trying
        if (!Schema.sObjectType.Contact.fields.Name.isUpdateable()) {
            return null;
        }

        c.Status__c = status;
        update c;
        return c;
    }
}
```

##### ApexCSRF

Check to avoid making DML operations in Apex class constructor/init method. This prevents
modification of the database just by accessing a page.

For instance, the following code would be invalid:

```
public class Foo {
    public init() {
        insert data;
    }

    public Foo() {
        insert data;
    }
}
```

##### ApexDangerousMethods

Checks against calling dangerous methods.

For the time being, it reports:

* Against `FinancialForce`'s `Configuration.disableTriggerCRUDSecurity()`. Disabling CRUD security
opens the door to several attacks and requires manual validation, which is unreliable.
* Calling `System.debug` passing sensitive data as parameter, which could lead to exposure
of private data.

##### ApexInsecureEndpoint

Checks against accessing endpoints under plain **http**. You should always use
**https** for security.

##### ApexOpenRedirect

Checks against redirects to user-controlled locations. This prevents attackers from
redirecting users to phishing sites.

For instance, the following code would be reported:

```
public class without sharing Foo {
    String unsafeLocation = ApexPage.getCurrentPage().getParameters.get('url_param');
    PageReference page() {
       return new PageReference(unsafeLocation);
    }
}
```

##### ApexSharingViolations

Detect classes declared without explicit sharing mode if DML methods are used. This
forces the developer to take access restrictions into account before modifying objects.

##### ApexSOQLInjection

Detects the usage of untrusted / unescaped variables in DML queries.

For instance, it would report on:

```
public class Foo {
    public void test1(String t1) {
        Database.query('SELECT Id FROM Account' + t1);
    }
}
```

##### ApexSuggestUsingNamedCred

Detects hardcoded credentials used in requests to an endpoint.

You should refrain from hardcoding credentials:
  * They are hard to mantain by being mixed in application code
  * Particularly hard to update them when used from different classes
  * Granting a developer access to the codebase means granting knowledge
     of credentials, keeping a two-level access is not possible.
  * Using different credentials for different environments is troublesome
     and error-prone.

Instead, you should use *Named Credentials* and a callout endpoint.

For more information, you can check [this](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_callouts_named_credentials.htm)

##### ApexXSSFromEscapeFalse

Reports on calls to `addError` with disabled escaping. The message passed to `addError`
will be displayed directly to the user in the UI, making it prime ground for XSS
attacks if unescaped.

##### ApexXSSFromURLParam

Makes sure that all values obtained from URL parameters are properly escaped / sanitized
to avoid XSS attacks.

#### Apex Braces Rule Set

The Braces Rule Set has been added and serves the same purpose as the Braces Rule Set from Java:
It checks the use and placement of braces around if-statements, for-loops and so on.

##### IfStmtsMustUseBraces

Avoid using if statements without using braces to surround the code block. If the code
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

For instance, the following code shows the different. PMD would report on the not recommended approach:

```
if (foo)    // not recommended
    x++;

if (foo) {  // preferred approach
    x++;
}
```

##### WhileLoopsMustUseBraces

Avoid using 'while' statements without using braces to surround the code block. If the code
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

For instance, the following code shows the different. PMD would report on the not recommended approach:

```
while (true)    // not recommended
      x++;

while (true) {  // preferred approach
      x++;
}
```

##### IfElseStmtsMustUseBraces

Avoid using if..else statements without using surrounding braces. If the code formatting
or indentation is lost then it becomes difficult to separate the code being controlled
from the rest.

For instance, the following code shows the different. PMD would report on the not recommended approach:

```
// this is not recommended
if (foo)
       x = x+1;
   else
       x = x-1;

// preferred approach
if (foo) {
   x = x+1;
} else {
   x = x-1;
}
```

##### ForLoopsMustUseBraces

Avoid using 'for' statements without using surrounding braces. If the code formatting or
indentation is lost then it becomes difficult to separate the code being controlled
from the rest.

For instance, the following code shows the different. PMD would report on the not recommended approach:

```
for (int i = 0; i < 42; i++) // not recommended
    foo();

for (int i = 0; i < 42; i++) { // preferred approach
    foo();
}
```

#### Apex Rule Suppression

Apex violations can now be suppressed very similarly to how it's done in Java, by making use of a
`@SuppressWarnings` annotation.

Supported syntax includes:

```
@SupressWarnings('PMD') // to supress all Apex rules
@SupressWarnings('all') // to supress all Apex rules
@SupressWarnings('PMD.ARuleName') // to supress only the rule named ARuleName
@SupressWarnings('PMD.ARuleName, PMD.AnotherRuleName') // to supress only the rule named ARuleName or AnotherRuleName
```

Notice this last scenario is slightly different to the Java syntax. This is due to differences in the Apex grammar for annotations.

#### New Rules

##### AccessorMethodGeneration (java-design)

When accessing a private field / method from another class, the Java compiler will generate a accessor methods
with package-private visibility. This adds overhead, and to the dex method count on Android. This situation can
be avoided by changing the visibility of the field / method from private to package-private.

For instance, it would report violations on code such as:

```
public class OuterClass {
    private int counter;
    /* package */ int id;

    public class InnerClass {
        InnerClass() {
            OuterClass.this.counter++; // wrong, accessor method will be generated
        }

        public int getOuterClassId() {
            return OuterClass.this.id; // id is package-private, no accessor method needed
        }
    }
}
```

This new rule is part of the `java-design` ruleset.

#### Modified Rules

*   The Java rule `UnnecessaryLocalBeforeReturn` (ruleset java-design) no longer requires the variable declaration
    and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be
    reported.

*   The Java rule `UseLocaleWithCaseConversions` (ruleset java-design) has been modified, to detect calls
    to `toLowerCase` and to `toUpperCase` also within method call chains. This leads to more detected cases
    and potentially new false positives.
    See also [bugfix #1556](https://sourceforge.net/p/pmd/bugs/1556/).

*   The rule `AvoidConstantsInterface` (ruleset java-design) has been removed. It is completely replaced by
    the rule `ConstantsInInterface`.

*   The Java rule `UnusedModifier` (ruleset java-unusedcode) has been moved to the ruleset java-unnecessary
    and has been renamed to `UnnecessaryModifier`.
    Additionally, it has been expanded to consider more redundant modifiers:
    *   Annotations marked as `abstract`.
    *   Nested annotations marked as `static`.
    *   Nested annotations within another interface or annotation marked as `public`.
    *   Classes, interfaces or annotations nested within an annotation marked as `public` or `static`.
    *   Nested enums marked as `static`.

#### CPD Suppression

It is now possible to allow CPD suppression through comments in **Java**. You tell CPD to ignore
the following code with a comment containin `CPD-OFF` and with `CPD-ON` you tell CPD to resume
analysis. The old approach via `@SuppressWarnings` annotation is still supported, but is considered
**deprecated**, since it is limited to locations where the `SuppressWarnings` annotation is allowed.
See [PR #250](https://github.com/pmd/pmd/pull/250).

For example:

```java
    public Object someMethod(int x) throws Exception {
        // some unignored code

        // tell cpd to start ignoring code - CPD-OFF

        // mission critical code, manually loop unroll
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);

        // resume CPD analysis - CPD-ON

        // further code will *not* be ignored
    }
```

#### CPD filelist command line option

CPD now supports the command line option `--filelist`. With that, you can specify a file, which
contains the names and paths of the files, that should be analyzed. This is similar to PMD's filelist option.
You need to use this, if you have a large project with many files, and you hit the command line length limit.


### Fixed Issues

*   General
    *   [#1511](https://sourceforge.net/p/pmd/bugs/1511/): \[core] Inconsistent behavior of Rule.start/Rule.end
    *   [#234](https://github.com/pmd/pmd/issues/234): \[core] Zip file stream closes spuriously when loading rulesets
    *   [#256](https://github.com/pmd/pmd/issues/256): \[core] shortnames option is broken with relative paths
    *   [#305](https://github.com/pmd/pmd/issues/305): \[core] PMD not executing under git bash
*   apex-apexunit
    *   [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   apex-complexity
    *   [#183](https://github.com/pmd/pmd/issues/183): \[apex] NCSS Method length is incorrect when using method chaining
    *   [#251](https://github.com/pmd/pmd/issues/251): \[apex] NCSS Type length is incorrect when using method chaining
*   apex-security
    *   [#264](https://github.com/pmd/pmd/issues/264): \[apex] ApexXSSFromURLParamRule shouldn't enforce ESAPI usage. String.escapeHtml4 is sufficient.
*   java
    *   [#185](https://github.com/pmd/pmd/issues/185): \[java] CPD runs into NPE when analyzing Lucene
    *   [#206](https://github.com/pmd/pmd/issues/206): \[java] Parse error on annotation fields with generics
    *   [#207](https://github.com/pmd/pmd/issues/207): \[java] Parse error on method reference with generics
    *   [#208](https://github.com/pmd/pmd/issues/208): \[java] Parse error with local class with 2 or more annotations
    *   [#213](https://github.com/pmd/pmd/issues/213): \[java] CPD: OutOfMemory when analyzing Lucene
    *   [#309](https://github.com/pmd/pmd/issues/309): \[java] Parse error on method reference
    *   [#1542](https://sourceforge.net/p/pmd/bugs/1542/): \[java] CPD throws an NPE when parsing enums with -ignore-identifiers
    *   [#1545](https://sourceforge.net/p/pmd/bugs/1545/): \[java] Symbol Table fails to resolve inner classes
*   java-basic
    *   [#232](https://github.com/pmd/pmd/issues/232): \[java] SimplifiedTernary: Incorrect ternary operation can be simplified.
*   java-coupling
    *   [#270](https://github.com/pmd/pmd/issues/270): \[java] LoD false positive
*   java-design
    *   [#933](https://sourceforge.net/p/pmd/bugs/933/): \[java] UnnecessaryLocalBeforeReturn false positive for SuppressWarnings annotation
    *   [#1448](https://sourceforge.net/p/pmd/bugs/1448/): \[java] ImmutableField: Private field in inner class gives false positive with lambdas
    *   [#1495](https://sourceforge.net/p/pmd/bugs/1495/): \[java] UnnecessaryLocalBeforeReturn with assert
    *   [#1496](https://sourceforge.net/p/pmd/bugs/1496/): \[java] New Rule: AccesorMethodGeneration - complements accessor class rule
    *   [#1512](https://sourceforge.net/p/pmd/bugs/1512/): \[java] Combine rules AvoidConstantsInInterface and ConstantsInInterface
    *   [#1552](https://sourceforge.net/p/pmd/bugs/1552/): \[java] MissingBreakInSwitch - False positive for continue
    *   [#1556](https://sourceforge.net/p/pmd/bugs/1556/): \[java] UseLocaleWithCaseConversions does not works with `ResultSet` (false negative)
    *   [#177](https://github.com/pmd/pmd/issues/177): \[java] SingularField with lambdas as final fields
    *   [#216](https://github.com/pmd/pmd/issues/216): \[java] \[doc] NonThreadSafeSingleton: Be more explicit as to why double checked locking is not recommended
    *   [#219](https://github.com/pmd/pmd/issues/219): \[java] UnnecessaryLocalBeforeReturn: ClassCastException in switch case with local variable returned
    *   [#240](https://github.com/pmd/pmd/issues/240): \[java] UnnecessaryLocalBeforeReturn: Enhance by checking usages
    *   [#274](https://github.com/pmd/pmd/issues/274): \[java] AccessorMethodGeneration: Method inside static inner class incorrectly reported
    *   [#275](https://github.com/pmd/pmd/issues/275): \[java] FinalFieldCouldBeStatic: Constant in @interface incorrectly reported as "could be made static"
    *   [#282](https://github.com/pmd/pmd/issues/282): \[java] UnnecessaryLocalBeforeReturn false positive when cloning Maps
    *   [#291](https://github.com/pmd/pmd/issues/291): \[java] Improve quality of AccessorClassGeneration
*   java-imports
    *   [#338](https://github.com/pmd/pmd/issues/338): \[java] False positive on DontImportJavaLang when importing java.lang.ProcessBuilder
    *   [#339](https://github.com/pmd/pmd/issues/339): \[java] False positive on DontImportJavaLang when importing Java 7's java.lang.invoke.MethodHandles
    *   [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
    *   [#1547](https://sourceforge.net/p/pmd/bugs/1547/): \[java] UnusedImportRule - False Positive for only usage in Javadoc - {@link ClassName#CONSTANT}
    *   [#1555](https://sourceforge.net/p/pmd/bugs/1555/): \[java] UnnecessaryFullyQualifiedName: Really necessary fully qualified name
*   java-junit
    *   [#285](https://github.com/pmd/pmd/issues/285): \[java] JUnitTestsShouldIncludeAssertRule should support @Rule as well as @Test(expected = ...)
    *   [#330](https://github.com/pmd/pmd/issues/330): \[java] NPE applying rule JUnitTestsShouldIncludeAssert
*   java-logging-java
    *   [#1541](https://sourceforge.net/p/pmd/bugs/1541/): \[java] InvalidSlf4jMessageFormat: False positive with placeholder and exception
    *   [#1551](https://sourceforge.net/p/pmd/bugs/1551/): \[java] InvalidSlf4jMessageFormat: fails with NPE
*   java-optimizations
    *   [#215](https://github.com/pmd/pmd/issues/215): \[java] RedundantFieldInitializer report for annotation field not explicitly marked as final
    *   [#222](https://github.com/pmd/pmd/issues/222): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-strings
    *   [#202](https://github.com/pmd/pmd/issues/202): \[java] \[doc] ConsecutiveAppendsShouldReuse is not really an optimization
    *   [#290](https://github.com/pmd/pmd/issues/290): \[java] InefficientEmptyStringCheck misses String.trim().isEmpty()
*   java-unnecessary
    *   [#199](https://github.com/pmd/pmd/issues/199): \[java] UselessParentheses: Parentheses in return statement are incorrectly reported as useless
*   java-unusedcode
    *   [#246](https://github.com/pmd/pmd/issues/246): \[java] UnusedModifier doesn't check annotations
    *   [#247](https://github.com/pmd/pmd/issues/247): \[java] UnusedModifier doesn't check annotations inner classes
    *   [#248](https://github.com/pmd/pmd/issues/248): \[java] UnusedModifier doesn't check static keyword on nested enum declaration
    *   [#257](https://github.com/pmd/pmd/issues/257): \[java] UnusedLocalVariable false positive
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name
*   psql
    *   [#1549](https://sourceforge.net/p/pmd/bugs/1549/): \[plsql] Parse error for IS [NOT] NULL construct
*   javascript
    *   [#201](https://github.com/pmd/pmd/issues/201): \[javascript] template strings are not correctly parsed


### API Changes

*   `net.sourceforge.pmd.RuleSetFactory` is now immutable and its behavior cannot be changed anymore.
    It provides constructors to create new adjusted instances. This allows to avoid synchronization in RuleSetFactory.
    See [PR #131](https://github.com/pmd/pmd/pull/131).
*   `net.sourceforge.pmd.RuleSet` is now immutable, too, and can only be created via `RuleSetFactory`.
    See [PR #145](https://github.com/pmd/pmd/pull/145).
*   `net.sourceforge.pmd.cli.XPathCLI` has been removed. It's functionality is fully covered by the Designer.
*   `net.sourceforge.pmd.Report` now works with `ThreadSafeReportListener`s. Both `ReportListener` and
    `SynchronizedReportListener` are deprecated in favor of `net.sourceforge.pmd.ThreadSafeReportListener`.
    Therefore, the methods `getSynchronizedListeners()` and `addSynchronizedListeners(...)` have been
    replaced by `getListeners()` and `addListeners(...)`. See [PR #193](https://github.com/pmd/pmd/pull/193).

### External Contributions

*   [#123](https://github.com/pmd/pmd/pull/123): \[apex] Changing method names to lowercase so casing doesn't matter
*   [#129](https://github.com/pmd/pmd/pull/129): \[plsql] Added correct parse of IS [NOT] NULL and multiline DML
*   [#137](https://github.com/pmd/pmd/pull/137): \[apex] Adjusted remediation points
*   [#146](https://github.com/pmd/pmd/pull/146): \[apex] Detection of missing Apex CRUD checks for SOQL/DML operations
*   [#147](https://github.com/pmd/pmd/pull/147): \[apex] Adding XSS detection to return statements
*   [#148](https://github.com/pmd/pmd/pull/148): \[apex] Improving detection of SOQL injection
*   [#149](https://github.com/pmd/pmd/pull/149): \[apex] Whitelisting String.isEmpty and casting
*   [#152](https://github.com/pmd/pmd/pull/152): \[java] fixes #1552 continue does not require break
*   [#154](https://github.com/pmd/pmd/pull/154): \[java] Fix #1547: UnusedImports: Adjust regex to support underscores
*   [#158](https://github.com/pmd/pmd/pull/158): \[apex] Reducing FPs in SOQL with VF getter methods
*   [#160](https://github.com/pmd/pmd/pull/160): \[apex] Flagging of dangerous method call
*   [#163](https://github.com/pmd/pmd/pull/163): \[apex] Flagging of System.debug
*   [#165](https://github.com/pmd/pmd/pull/165): \[apex] Improving open redirect rule to avoid test classes/methods
*   [#167](https://github.com/pmd/pmd/pull/167): \[apex] GC and thread safety changes
*   [#169](https://github.com/pmd/pmd/pull/169): \[apex] Improving detection for DML with inline new object
*   [#170](https://github.com/pmd/pmd/pull/170): \[core] Ant Task Formatter encoding issue with XMLRenderer
*   [#172](https://github.com/pmd/pmd/pull/172): \[apex] Bug fix, detects both Apex fields and class members
*   [#175](https://github.com/pmd/pmd/pull/175): \[apex] ApexXSSFromURLParam: Adding missing casting methods
*   [#176](https://github.com/pmd/pmd/pull/176): \[apex] Bug fix for FP: open redirect for strings prefixed with / is safe
*   [#179](https://github.com/pmd/pmd/pull/179): \[apex] Legacy test class declaration support
*   [#181](https://github.com/pmd/pmd/pull/181): \[apex] Control flow based CRUD rule checking
*   [#184](https://github.com/pmd/pmd/pull/184): \[apex] Improving open redirect detection for static fields & assignment operations
*   [#189](https://github.com/pmd/pmd/pull/189): \[apex] Bug fix of SOQL concatenated vars detection
*   [#191](https://github.com/pmd/pmd/pull/191): \[apex] Detection of sharing violation when Database. methods are used
*   [#192](https://github.com/pmd/pmd/pull/192): \[apex] Dead code removal
*   [#200](https://github.com/pmd/pmd/pull/200): \[javascript] Templatestring grammar fix
*   [#204](https://github.com/pmd/pmd/pull/204): \[apex] Sharing violation SOQL detection bug fix
*   [#214](https://github.com/pmd/pmd/pull/214): \[apex] Sharing violation improving reporting of the correct node, de-duping
*   [#217](https://github.com/pmd/pmd/pull/217): \[core] Make it build on Windows
*   [#227](https://github.com/pmd/pmd/pull/227): \[apex] Improving detection of getters
*   [#228](https://github.com/pmd/pmd/pull/228): \[apex] Excluding count from CRUD/FLS checks
*   [#229](https://github.com/pmd/pmd/pull/229): \[apex] Dynamic SOQL is safe against Integer, Boolean, Double
*   [#231](https://github.com/pmd/pmd/pull/231): \[apex] CRUD/FLS rule - add support for fields
*   [#266](https://github.com/pmd/pmd/pull/266): \[java] corrected invalid reporting of LoD violation
*   [#268](https://github.com/pmd/pmd/pull/268): \[apex] Support safe escaping via String method
*   [#273](https://github.com/pmd/pmd/pull/273): \[apex] Shade jackson on apex
*   [#279](https://github.com/pmd/pmd/pull/279): \[vf] New Salesforce VisualForce language support
*   [#280](https://github.com/pmd/pmd/pull/280): \[apex] Support for Aggregate Result in CRUD rules
*   [#281](https://github.com/pmd/pmd/pull/281): \[apex] Add Braces Rule Set
*   [#283](https://github.com/pmd/pmd/pull/283): \[vf] CSRF in VF controller pages
*   [#284](https://github.com/pmd/pmd/pull/284): \[vf] Adding support for parsing EL in script tags
*   [#287](https://github.com/pmd/pmd/pull/287): \[apex] Make Rule suppression work
*   [#288](https://github.com/pmd/pmd/pull/288): \[vf] Setting the tab size to 4 for VF
*   [#289](https://github.com/pmd/pmd/pull/289): \[apex] Complex SOQL Crud check bug fixes
*   [#296](https://github.com/pmd/pmd/pull/296): \[apex] Adding String.IsNotBlank to the whitelist to prevent False positives
*   [#297](https://github.com/pmd/pmd/pull/297): \[core] CPD: Adding the --filelist option from pmd to cpd
*   [#303](https://github.com/pmd/pmd/pull/303): \[java] InefficientEmptyStringCheckRule now reports String.trim().isEmpty() 
*   [#307](https://github.com/pmd/pmd/pull/307): \[java] Fix false positive with UseStringBufferForStringAppendsRule
*   [#308](https://github.com/pmd/pmd/pull/308): \[java] JUnitTestsShouldIncludeAssertRule supports @Rule annotated ExpectedExceptions
*   [#313](https://github.com/pmd/pmd/pull/313): \[vf] Apex:iFrame not being detected - bug fix
*   [#314](https://github.com/pmd/pmd/pull/314): \[vf] Bug fixes for incorrect Id detection and escaping
*   [#316](https://github.com/pmd/pmd/pull/316): \[apex] Ignoring certain rules in Batch classes, Queueable, and install scripts
*   [#317](https://github.com/pmd/pmd/pull/317): \[apex] Add support for safe ID assignment from URL param
*   [#326](https://github.com/pmd/pmd/pull/326): \[vf] Quote detection improvement and method argument detection
*   [#327](https://github.com/pmd/pmd/pull/327): \[apex] Fixed SOQL injection detection for escaped vars
*   [#331](https://github.com/pmd/pmd/pull/331): \[java] JunitTestsShouldIncludeAssertRule now handles AllocationExpression correctly
*   [#332](https://github.com/pmd/pmd/pull/332): \[java] Future-proof DontImportJavaLangRule

