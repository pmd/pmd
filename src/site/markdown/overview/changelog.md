# PMD Release Notes

## ????? - 5.6.0-SNAPSHOT

The PMD team is pleased to announce PMD 5.6.0

The most significant changes are on analysis performance and a whole new **Apex Security Rule Set**.

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

* [New and noteworthy](#New_and_noteworthy)
    * [Incremental Analysis](#Incremental_Analysis)
    * [Apex Security Rule Set](#Apex_Security_Rule_Set)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

#### Incremental Analysis

PMD now support incremental analysis. Analysis results can be cached and reused between runs.
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

##### ApexXSSFromEscapeFalse

Reports on calls to `addError` with disabled escaping. The message passed to `addError`
will be displayed directly to the user in the UI, making it prime ground for XSS
attacks if unescaped.

##### ApexXSSFromURLParam

Makes sure that all values obtained from URL parameters are properly escaped / sanitized
to avoid XSS attacks.

### Fixed Issues

*   General
    *   [#1542](https://sourceforge.net/p/pmd/bugs/1542/): \[java] CPD throws an NPE when parsing enums with -ignore-identifiers
*   apex-apexunit
    *   [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   Java
    *   [#1545](https://sourceforge.net/p/pmd/bugs/1545/): \[java] Symbol Table fails to resolve inner classes
*   java-design
    *   [#1552](https://sourceforge.net/p/pmd/bugs/1552/): \[java] MissingBreakInSwitch - False positive for continue
*   java-imports
    *   [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
    *   [#1547](https://sourceforge.net/p/pmd/bugs/1547/): \[java] UnusedImportRule - False Positive for only usage in Javadoc - {@link ClassName#CONSTANT}
*   java-logging-java
    *   [#1541](https://sourceforge.net/p/pmd/bugs/1541/): \[java] InvalidSlf4jMessageFormat: False positive with placeholder and exception
    *   [#1551](https://sourceforge.net/p/pmd/bugs/1551/): \[java] InvalidSlf4jMessageFormat: fails with NPE
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name
*   psql
    *   [#1549](https://sourceforge.net/p/pmd/bugs/1549/): \[plsql] Parse error for IS [NOT] NULL construct


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

