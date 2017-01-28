# PMD Release Notes

## ????? - 5.5.3-SNAPSHOT

The PMD team is pleased to announce PMD 5.5.3

The most significant changes are on analysis performance and a whole new **Apex Security Rule Set**.

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

* [New and noteworthy](#New_and_noteworthy)
    * [Apex Security Rule Set](#Apex_Security_Rule_Set)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

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

#### Modified Rules

The Java rule "UseLocaleWithCaseConversions" (ruleset java-design) has been modified, to detect calls
to `toLowerCase` and to `toUpperCase` also within method call chains. This leads to more detected cases
and potentially new false positives.
See also [bugfix #1556](https://sourceforge.net/p/pmd/bugs/1556/).


### Fixed Issues

*   General
    *   [#1511](https://sourceforge.net/p/pmd/bugs/1511/): \[core] Inconsistent behavior of Rule.start/Rule.end
*   apex-apexunit
    *   [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   apex-complexity
    *   [#183](https://github.com/pmd/pmd/issues/183): \[apex] NCSS Method length is incorrect when using method chaining
*   java
    *   [#185](https://github.com/pmd/pmd/issues/185): \[java] CPD runs into NPE when analyzing Lucene
    *   [#206](https://github.com/pmd/pmd/issues/206): \[java] Parse error on annotation fields with generics
    *   [#207](https://github.com/pmd/pmd/issues/207): \[java] Parse error on method reference with generics
    *   [#208](https://github.com/pmd/pmd/issues/208): \[java] Parse error with local class with 2 or more annotations
    *   [#213](https://github.com/pmd/pmd/issues/213): \[java] CPD: OutOfMemory when analyzing Lucene
    *   [#1542](https://sourceforge.net/p/pmd/bugs/1542/): \[java] CPD throws an NPE when parsing enums with -ignore-identifiers
    *   [#1545](https://sourceforge.net/p/pmd/bugs/1545/): \[java] Symbol Table fails to resolve inner classes
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
*   java-logging-java
    *   [#1541](https://sourceforge.net/p/pmd/bugs/1541/): \[java] InvalidSlf4jMessageFormat: False positive with placeholder and exception
    *   [#1551](https://sourceforge.net/p/pmd/bugs/1551/): \[java] InvalidSlf4jMessageFormat: fails with NPE
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


### API Changes

*   `net.sourceforge.pmd.RuleSetFactory` is now immutable and its behavior cannot be changed anymore.
    It provides constructors to create new adjusted instances. This allows to avoid synchronization in RuleSetFactory.
    See [PR #131](https://github.com/pmd/pmd/pull/131).

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

