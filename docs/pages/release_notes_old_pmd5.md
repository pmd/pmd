---
title: Old Release Notes PMD 5.x
permalink: pmd_release_notes_old_pmd5.html
---

Previous versions of PMD can be downloaded here: <https://sourceforge.net/projects/pmd/files/pmd/>

## 01-July-2017 - 5.8.1

The PMD team is pleased to announce PMD 5.8.1.

This is a bug fixing release.

### Fixed Issues

*   java
    *   [#471](https://github.com/pmd/pmd/issues/471): \[java] Error while processing class when EnumMap is used in PMD 5.8.0
    *   [#477](https://github.com/pmd/pmd/issues/477): \[core] NoClassDefFoundError under 5.8
    *   [#478](https://github.com/pmd/pmd/issues/478): \[core] Processing issues dealing with anonymous classes

### API Changes

*   The `getGenericArgs()` method introduced to `TypeNode` in 5.8.0 was removed. You can access to generics' info through the `JavaTypeDefinition` object.
*   The `JavaTypeDefinitionBuilder` class introduced in 5.8.0 is not more. You can use factory methods available on `JavaTypeDefinition`

### External Contributions

*   [#472](https://github.com/pmd/pmd/pull/472): \[java] fix error with raw types, bug #471


## 24-June-2017 - 5.8.0

The PMD team is pleased to announce PMD 5.8.0.

This is a minor release.

### New and noteworthy

#### Java Type Resolution

As part of Google Summer of Code 2017, [Bendegúz Nagy](https://github.com/WinterGrascph) has been working on completing type resolution for Java.
His progress so far has allowed to properly resolve, in addition to previously supported statements:

- References to `this` and `super`, even when qualified
- References to fields, even when chained (ie: `this.myObject.aField`), and properly handling inheritance / shadowing

Lambda parameter types where these are infered rather than explicit are still not supported. Expect future releases to do so.


#### Metrics Framework

As part of Google Summer of Code 2017, [Clément Fournier](https://github.com/oowekyala) has been working on
a new metrics framework for object-oriented metrics.

The basic groundwork has been done already and with this release, including a first rule based on the
metrics framework as a proof-of-concept: The rule *CyclomaticComplexity*, currently in the temporary
ruleset *java-metrics*, uses the Cyclomatic Complexity metric to find overly complex code.
This rule will eventually replace the existing three *CyclomaticComplexity* rules that are currently
defined in the *java-codesize* ruleset (see also [issue #445](https://github.com/pmd/pmd/issues/445)).

Since this work is still in progress, the metrics API (package `net.sourceforge.pmd.lang.java.oom`)
is not finalized yet and is expected to change.


#### Modified Rules

*   The Java rule `UnnecessaryFinalModifier` (ruleset java-unnecessary) now also reports on private methods marked as `final`.
    Being private, such methods can't be overriden, and therefore, the final keyword is redundant.

*   The Java rule `PreserveStackTrace` (ruleset java-design) has been relaxed to support the builder pattern on thrown exception.
    This change may introduce some false positives if using the exception in non-orthodox ways for things other than setting the
    root cause of the exception. Contact us if you find any such scenarios.

*   The ruleset java-junit now properly detects JUnit5, and rules are being adapted to the changes on it's API.
    This support is, however, still incomplete. Let us know of any uses we are still missing on the [issue tracker](https://github.com/pmd/pmd/issues)

*   The Java rule `EmptyTryBlock` (ruleset java-empty) now allows empty blocks when using try-with-resources.

*   The Java rule `EmptyCatchBlock` (ruleset java-empty) now exposes a new property called `allowExceptionNameRegex`.
    This allow to setup a regular expression for names of exceptions you wish to ignore for this rule. For instance,
    setting it to `^(ignored|expected)$` would ignore all empty catch blocks where the catched exception is named
    either `ignored` or `expected`. The default ignores no exceptions, being backwards compatible.

#### Deprecated Rules

*   The three complexity rules `CyclomaticComplexity`, `StdCyclomaticComplexity`, `ModifiedCyclomaticComplexity` (ruleset java-codesize) have been deprecated. They will be eventually replaced
    by a new CyclomaticComplexity rule based on the metrics framework. See also [issue #445](https://github.com/pmd/pmd/issues/445).

### Fixed Issues

*   General
    *   [#380](https://github.com/pmd/pmd/issues/380): \[core] NPE in RuleSet.hashCode
    *   [#407](https://github.com/pmd/pmd/issues/407): \[web] Release date is not properly formatted
    *   [#429](https://github.com/pmd/pmd/issues/429): \[core] Error when running PMD from folder with space
*   apex
    *   [#427](https://github.com/pmd/pmd/issues/427): \[apex] CPD error when parsing apex code from release 5.5.3
*   cpp
    *   [#431](https://github.com/pmd/pmd/issues/431): \[cpp] CPD gives wrong duplication blocks for CPP code
*   java
    *   [#414](https://github.com/pmd/pmd/issues/414): \[java] Java 8 parsing problem with annotations for wildcards
    *   [#415](https://github.com/pmd/pmd/issues/415): \[java] Parsing Error when having an Annotated Inner class
    *   [#417](https://github.com/pmd/pmd/issues/417): \[java] Parsing Problem with Annotation for Array Member Types
*   java-design
    *   [#397](https://github.com/pmd/pmd/issues/397): \[java] ConstructorCallsOverridableMethodRule: false positive for method called from lambda expression
    *   [#410](https://github.com/pmd/pmd/issues/410): \[java] ImmutableField: False positive with lombok
    *   [#422](https://github.com/pmd/pmd/issues/422): \[java] PreserveStackTraceRule: false positive when using builder pattern
*   java-empty
    *   [#413](https://github.com/pmd/pmd/issues/413): \[java] EmptyCatchBlock don't fail when exception is named ignore / expected
    *   [#432](https://github.com/pmd/pmd/issues/432): \[java] EmptyTryBlock: false positive for empty try-with-resource
*   java-imports:
    *   [#348](https://github.com/pmd/pmd/issues/348): \[java] imports/UnusedImport rule not considering static inner classes of imports
*   java-junit
    *   [#428](https://github.com/pmd/pmd/issues/428): \[java] PMD requires public modifier on JUnit 5 test
    *   [#465](https://github.com/pmd/pmd/issues/465): \[java] NullPointerException in JUnitTestsShouldIncludeAssertRule
*   java-logging:
    *   [#365](https://github.com/pmd/pmd/issues/365): \[java] InvalidSlf4jMessageFormat does not handle inline incrementation of arguments
*   java-strictexceptions
    *   [#350](https://github.com/pmd/pmd/issues/350): \[java] Throwing Exception in method signature is fine if the method is overriding or implementing something
*   java-typeresolution
    *   [#350](https://github.com/pmd/pmd/issues/350): \[java] Throwing Exception in method signature is fine if the method is overriding or implementing something
*   java-unnecessary
    *   [#421](https://github.com/pmd/pmd/issues/421): \[java] UnnecessaryFinalModifier final in private method
*   jsp
    *   [#311](https://github.com/pmd/pmd/issues/311): \[jsp] Parse error on HTML boolean attribute


### External Contributions

*   [#406](https://github.com/pmd/pmd/pull/406): \[java] False positive with lambda in java-design/ConstructorCallsOverridableMethod
*   [#409](https://github.com/pmd/pmd/pull/409): \[java] Groundwork for the upcoming metrics framework
*   [#416](https://github.com/pmd/pmd/pull/416): \[java] FIXED: Java 8 parsing problem with annotations for wildcards
*   [#418](https://github.com/pmd/pmd/pull/418): \[java] Type resolution: super and this keywords
*   [#423](https://github.com/pmd/pmd/pull/423): \[java] Add field access type resolution in non-generic cases
*   [#425](https://github.com/pmd/pmd/pull/425): \[java] False positive with builder pattern in java-design/PreserveStackTrace
*   [#426](https://github.com/pmd/pmd/pull/426): \[java] UnnecessaryFinalModifier final in private method
*   [#436](https://github.com/pmd/pmd/pull/436): \[java] Metrics framework tests and various improvements
*   [#440](https://github.com/pmd/pmd/pull/440): \[core] Created ruleset schema 3.0.0 (to use metrics)
*   [#443](https://github.com/pmd/pmd/pull/443): \[java] Optimize typeresolution, by skipping package and import declarations in visit(ASTName)
*   [#444](https://github.com/pmd/pmd/pull/444): \[java] [typeresolution]: add support for generic fields
*   [#451](https://github.com/pmd/pmd/pull/451): \[java] Metrics framework: first metrics + first rule


## 20-Mai-2017 - 5.7.0

The PMD team is pleased to announce PMD 5.7.0.

This is a minor release.

### New and noteworthy

#### Modified Rules

*   The rule "FieldDeclarationsShouldBeAtStartOfClass" of the java-design ruleset has a new property `ignoreInterfaceDeclarations`.
    Setting this property to `true` ignores interface declarations, that precede fields.
    Example usage:


    <rule ref="rulesets/java/design.xml/FieldDeclarationsShouldBeAtStartOfClass">
        <properties>
            <property name="ignoreInterfaceDeclarations" value="true"/>
        </properties>
    </rule>

#### Renderers

*   Added the 'empty' renderer which will write nothing.  Does not affect other behaviors, for example the command line PMD exit status
    will still indicate whether violations were found.

### Fixed Issues

*   General
    *   [#377](https://github.com/pmd/pmd/issues/377): \[core] Use maven wrapper and upgrade to maven 3.5.0
    *   [#376](https://github.com/pmd/pmd/issues/376): \[core] Improve build time on travis
*   java
    *   [#378](https://github.com/pmd/pmd/issues/378): \[java] Parser Error for empty statements
*   java-coupling
    *   [#1427](https://sourceforge.net/p/pmd/bugs/1427/): \[java] Law of Demeter violations for the Builder pattern
*   java-design
    *   [#345](https://github.com/pmd/pmd/issues/345): \[java] FieldDeclarationsShouldBeAtStartOfClass: Add ability to ignore interfaces
    *   [#389](https://github.com/pmd/pmd/issues/389): \[java] RuleSetCompatibility - not taking rename of UnusedModifier into account
*   java-junit
    *   [#358](https://github.com/pmd/pmd/issues/358): \[java] Mockito verify method is not taken into account in JUnitTestsShouldIncludeAssert rule
*   java-strings
    *   [#334](https://github.com/pmd/pmd/issues/334): \[java] \[doc] Add suggestion to use StringUtils#isBlank for InefficientEmptyStringCheck
*   jsp-basic
    *   [#369](https://github.com/pmd/pmd/issues/369): \[jsp] Wrong issue "JSP file should use UTF-8 encoding"

### API Changes

*   The method `net.sourceforge.pmd.util.StringUtil#htmlEncode(String)` is deprecated.
    `org.apache.commons.lang3.StringEscapeUtils#escapeHtml4(String)` should be used instead.

### External Contributions

*   [#368](https://github.com/pmd/pmd/pull/368): \[vf] Adding proper AST support for negation expressions
*   [#372](https://github.com/pmd/pmd/pull/372): \[core] Fix XSS in HTML renderer
*   [#374](https://github.com/pmd/pmd/pull/374): \[java] Add property to ignore interfaces in FieldDeclarationsShouldBeAtStartOfClassRule
*   [#381](https://github.com/pmd/pmd/pull/381): \[core] Fix broken link in the site's doc
*   [#382](https://github.com/pmd/pmd/pull/382): \[java] Added documentation details on InefficientEmptyStringCheck
*   [#383](https://github.com/pmd/pmd/pull/383): \[jsp] Fixed JspEncoding false positive
*   [#390](https://github.com/pmd/pmd/pull/390): \[java] Remove trailing whitespaces in design.xml
*   [#391](https://github.com/pmd/pmd/pull/391): \[apex] Fix documentation typo
*   [#392](https://github.com/pmd/pmd/pull/392): \[java] False positive for Law Of Demeter (Builder pattern)
*   [#395](https://github.com/pmd/pmd/pull/395): \[java] Mockito verify method is not taken into account in JUnitTestsShouldIncludeAssert rule


## 29-April-2017 - 5.6.1

The PMD team is pleased to announce PMD 5.6.1.

This is a bug fixing release.

### Fixed Issues

*   General
    *   [#363](https://github.com/pmd/pmd/issues/363): \[core] Rule documentation pages are missing
    *   [#364](https://github.com/pmd/pmd/issues/364): \[core] Stream closed exception when running through maven
    *   [#373](https://github.com/pmd/pmd/issues/373): \[core] RuleSetFactory - add more helper methods


## 22-April-2017 - 5.6.0

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
improving *Symbol Table* performance by a whooping 80%, that's over 15X faster
than PMD 5.5.1, when we first started working on it.

Java developers will also appreciate the revamp of `CloneMethodMustImplementCloneable`,
making it over 500X faster, and `PreserveStackTrace` which is now 7X faster.

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
* They are hard to maintain by being mixed in application code
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

#### New Rules

##### AccessorMethodGeneration (java-design)

When accessing a private field / method from another class, the Java compiler will generate an accessor method
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

*   The Java rule `UnnecessaryLocalBeforeReturn` (ruleset java-design) now has a new property `statementOrderMatters`.
    It is enabled by default to stay backwards compatible. But if this property is set to `false`, this rule
    no longer requires the variable declaration
    and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be
    reported.

*   The Java rule `UseLocaleWithCaseConversions` (ruleset java-design) has been modified, to detect calls
    to `toLowerCase` and to `toUpperCase` also within method call chains. This leads to more detected cases
    and potentially new false positives.
    See also [bugfix #1556](https://sourceforge.net/p/pmd/bugs/1556/).

*   The Java rule `AvoidConstantsInterface` (ruleset java-design) has been removed. It is completely replaced by
    the rule `ConstantsInInterface`.

*   The Java rule `UnusedModifier` (ruleset java-unusedcode) has been moved to the ruleset java-unnecessary
    and has been renamed to `UnnecessaryModifier`.
    Additionally, it has been expanded to consider more redundant modifiers:
    *   Annotations marked as `abstract`.
    *   Nested annotations marked as `static`.
    *   Nested annotations within another interface or annotation marked as `public`.
    *   Classes, interfaces or annotations nested within an annotation marked as `public` or `static`.
    *   Nested enums marked as `static`.

*   The Java rule `JUnitTestsShouldIncludeAssert` (ruleset java-junit) now accepts usage of `@Rule` `ExpectedException`
    to set expectations on exceptions, and are considered as valid assertions.

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
    *   [#324](https://github.com/pmd/pmd/issues/324): \[core] Automated release - github release notes missing
    *   [#337](https://github.com/pmd/pmd/issues/337): \[core] Version 5.5.4 seems to hold file lock on rules JAR (affects Windows only)
*   apex-apexunit
    *   [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   apex-complexity
    *   [#183](https://github.com/pmd/pmd/issues/183): \[apex] NCSS Method length is incorrect when using method chaining
    *   [#251](https://github.com/pmd/pmd/issues/251): \[apex] NCSS Type length is incorrect when using method chaining
*   apex-security
    *   [#264](https://github.com/pmd/pmd/issues/264): \[apex] ApexXSSFromURLParamRule shouldn't enforce ESAPI usage. String.escapeHtml4 is sufficient.
    *   [#315](https://github.com/pmd/pmd/issues/315): \[apex] Documentation flaw on Apex Sharing Violations
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
    *   [#310](https://github.com/pmd/pmd/issues/310): \[java] UnnecessaryLocalBeforeReturn enhancement is overly restrictive -- method order matters
    *   [#352](https://github.com/pmd/pmd/issues/352): \[java] AccessorClassGeneration throws ClassCastException when seeing array construction
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
*   [#340](https://github.com/pmd/pmd/pull/340): \[vf] Multiple parser bug fixes
*   [#341](https://github.com/pmd/pmd/pull/341): \[vf] JSON.parse(..) and NOT(..) are safely evaluated
*   [#343](https://github.com/pmd/pmd/pull/343): \[apex] int,id,boolean,ternary operator condition are not injection in Soql
*   [#344](https://github.com/pmd/pmd/pull/344): \[apex] ApexCRUDViolationRule: Bug fix for ClassCastException
*   [#351](https://github.com/pmd/pmd/pull/351): \[vf] Fixing regression introduced by #341


## 29-April-2017 - 5.5.7

The PMD team is pleased to announce PMD 5.5.7.

This is a bug fixing release.

### Fixed Issues

*   General
    *   [#364](https://github.com/pmd/pmd/issues/364): \[core] Stream closed exception when running through maven


## 19-April-2017 - 5.5.6

The PMD team is pleased to announce PMD 5.5.6.

This is a bug fixing release.

### Fixed Issues

*   General
    *   [#324](https://github.com/pmd/pmd/issues/324): \[core] Automated release - github release notes missing
    *   [#337](https://github.com/pmd/pmd/issues/337): \[core] Version 5.5.4 seems to hold file lock on rules JAR (affects Windows only)


## 27-March-2017 - 5.5.5

The PMD team is pleased to announce PMD 5.5.5.


### Fixed Issues

*   general:
    *   [#305](https://github.com/pmd/pmd/issues/305): \[core] PMD not executing under git bash
*   java:
    *   [#309](https://github.com/pmd/pmd/issues/309): \[java] Parse error on method reference
*   java-design
    *   [#274](https://github.com/pmd/pmd/issues/274): \[java] AccessorMethodGeneration: Method inside static inner class incorrectly reported
    *   [#275](https://github.com/pmd/pmd/issues/275): \[java] FinalFieldCouldBeStatic: Constant in @interface incorrectly reported as "could be made static"
    *   [#282](https://github.com/pmd/pmd/issues/282): \[java] UnnecessaryLocalBeforeReturn false positive when cloning Maps
    *   [#291](https://github.com/pmd/pmd/issues/291): \[java] Improve quality of AccessorClassGeneration
*   java-junit:
    *   [#285](https://github.com/pmd/pmd/issues/285): \[java] JUnitTestsShouldIncludeAssertRule should support @Rule as well as @Test(expected = ...)
*   java-optimizations:
    *   [#222](https://github.com/pmd/pmd/issues/222): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-strings:
    *   [#290](https://github.com/pmd/pmd/issues/290): \[java] InefficientEmptyStringCheck misses String.trim().isEmpty()

### External Contributions

*   [#280](https://github.com/pmd/pmd/pull/280): \[apex] Support for Aggregate Result in CRUD rules
*   [#289](https://github.com/pmd/pmd/pull/289): \[apex] Complex SOQL Crud check bug fixes
*   [#296](https://github.com/pmd/pmd/pull/296): \[apex] Adding String.IsNotBlank to the whitelist to prevent False positives
*   [#303](https://github.com/pmd/pmd/pull/303): \[java] InefficientEmptyStringCheckRule now reports String.trim().isEmpty()
*   [#307](https://github.com/pmd/pmd/pull/307): \[java] Fix false positive with UseStringBufferForStringAppendsRule
*   [#308](https://github.com/pmd/pmd/pull/308): \[java] JUnitTestsShouldIncludeAssertRule supports @Rule annotated ExpectedExceptions


## 25-Februar-2017 - 5.5.4

The PMD team is pleased to announce PMD 5.5.4



### New and noteworthy

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

*   The Java rule `UnusedModifier` (ruleset java-unusedcode) has been expanded to consider more redundant modifiers.
    *   Annotations marked as `abstract`.
    *   Nested annotations marked as `static`.
    *   Nested annotations within another interface or annotation marked as `public`.
    *   Classes, interfaces or annotations nested within an annotation marked as `public` or `static`.
    *   Nested enums marked as `static`.

*   The Java rule `UnnecessaryLocalBeforeReturn` (ruleset java-design) no longer requires the variable declaration
    and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be
    reported.

### Fixed Issues

*   General
    *   [#234](https://github.com/pmd/pmd/issues/234): \[core] Zip file stream closes spuriously when loading rulesets
    *   [#256](https://github.com/pmd/pmd/issues/256): \[core] shortnames option is broken with relative paths
*   apex-complexity
    *   [#251](https://github.com/pmd/pmd/issues/251): \[apex] NCSS Type length is incorrect when using method chaining
*   apex-security
    *   [#264](https://github.com/pmd/pmd/issues/264): \[apex] ApexXSSFromURLParamRule shouldn't enforce ESAPI usage. String.escapeHtml4 is sufficient.
*   java-basic
    *   [#232](https://github.com/pmd/pmd/issues/232): \[java] SimplifiedTernary: Incorrect ternary operation can be simplified.
*   java-coupling
    *   [#270](https://github.com/pmd/pmd/issues/270): \[java] LoD false positive
*   java-design
    *   [#933](https://sourceforge.net/p/pmd/bugs/933/): \[java] UnnecessaryLocalBeforeReturn false positive for SuppressWarnings annotation
    *   [#1496](https://sourceforge.net/p/pmd/bugs/1496/): \[java] New Rule: AccesorMethodGeneration - complements accessor class rule
    *   [#216](https://github.com/pmd/pmd/issues/216): \[java] \[doc] NonThreadSafeSingleton: Be more explicit as to why double checked locking is not recommended
    *   [#219](https://github.com/pmd/pmd/issues/219): \[java] UnnecessaryLocalBeforeReturn: ClassCastException in switch case with local variable returned
    *   [#240](https://github.com/pmd/pmd/issues/240): \[java] UnnecessaryLocalBeforeReturn: Enhance by checking usages
*   java-optimizations
    *   [#215](https://github.com/pmd/pmd/issues/215): \[java] RedundantFieldInitializer report for annotation field not explicitly marked as final
*   java-unusedcode
    *   [#246](https://github.com/pmd/pmd/issues/246): \[java] UnusedModifier doesn't check annotations
    *   [#247](https://github.com/pmd/pmd/issues/247): \[java] UnusedModifier doesn't check annotations inner classes
    *   [#248](https://github.com/pmd/pmd/issues/248): \[java] UnusedModifier doesn't check static keyword on nested enum declaration
    *   [#257](https://github.com/pmd/pmd/issues/257): \[java] UnusedLocalVariable false positive


### External Contributions

*   [#227](https://github.com/pmd/pmd/pull/227): \[apex] Improving detection of getters
*   [#228](https://github.com/pmd/pmd/pull/228): \[apex] Excluding count from CRUD/FLS checks
*   [#229](https://github.com/pmd/pmd/pull/229): \[apex] Dynamic SOQL is safe against Integer, Boolean, Double
*   [#231](https://github.com/pmd/pmd/pull/231): \[apex] CRUD/FLS rule - add support for fields
*   [#266](https://github.com/pmd/pmd/pull/266): \[java] corrected invalid reporting of LoD violation
*   [#268](https://github.com/pmd/pmd/pull/268): \[apex] Support safe escaping via String method
*   [#273](https://github.com/pmd/pmd/pull/273): \[apex] Shade jackson on apex


## 28-January-2017 - 5.5.3

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
* They are hard to maintain by being mixed in application code
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


## 05-November-2016 - 5.5.2

**Summary:**

*   1 new language for CPD: Groovy
*   1 new rule: plsql-strictsyntax/MisplacedPragma
*   12 pull requests
*   17 bug fixes

**New Supported Languages:**

*   CPD now supports Groovy. See [PR#107](https://github.com/pmd/pmd/pull/107).

**Feature Requests and Improvements:**

*   plsql
    *   [#1539](https://sourceforge.net/p/pmd/bugs/1539/): \[plsql] Create new rule for strict syntax checking: MisplacedPragma

**New Rules:**

*   New Rules for plsql
    *   plsql-strictsyntax: MisplacedPragma

**Pull Requests:**

*   [#106](https://github.com/pmd/pmd/pull/106): \[java] CPD: Keep constructor names under ignoreIdentifiers
*   [#107](https://github.com/pmd/pmd/pull/107): \[groovy] Initial support for CPD Groovy
*   [#110](https://github.com/pmd/pmd/pull/110): \[java] Fix parser error (issue 1530)
*   [#111](https://github.com/pmd/pmd/pull/111): \[java] Fix BooleanInstantiationRule for Java 8
*   [#112](https://github.com/pmd/pmd/pull/112): \[java] Fix ClassCastException on CloneMethodMustImplementCloneable
*   [#113](https://github.com/pmd/pmd/pull/113): \[java] Fix ClassCastException on SignatureDeclareThrowsException
*   [#114](https://github.com/pmd/pmd/pull/114): \[core] Remove multihreading workaround for JRE5, as no PMD version supports running on JRE5 anymore
*   [#115](https://github.com/pmd/pmd/pull/115): \[java] Simplify lambda parsing
*   [#116](https://github.com/pmd/pmd/pull/116): \[core] \[java] Improve collection usage
*   [#117](https://github.com/pmd/pmd/pull/117): \[java] Improve symboltable performance
*   [#118](https://github.com/pmd/pmd/pull/118): \[java] Simplify VariableDeclaratorId parsing
*   [#119](https://github.com/pmd/pmd/pull/119): \[plsql] Fix PMD issue 1531- endless loop followed by OOM while parsing (PL)SQL

**Bugfixes:**

*   apex-apexunit
    *   [#1521](https://sourceforge.net/p/pmd/bugs/1521/): \[apex] ApexUnitTestClassShouldHaveAsserts: Parsing error on APEX class: expected one element but was: <BlockStatement, BlockStatement>
*   Java
    *   [#1530](https://sourceforge.net/p/pmd/bugs/1530/): \[java] Parser exception on Java code
    *   [#1490](https://sourceforge.net/p/pmd/bugs/1490/): \[java] PMD Error while processing - NullPointerException
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-design/SingularField
    *   [#1494](https://sourceforge.net/p/pmd/bugs/1494/): \[java] SingularField: lombok.Data false positive
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-logging-java
    *   [#1500](https://sourceforge.net/p/pmd/bugs/1500/) \[java] InvalidSlf4jMessageFormat: doesn't ignore exception param
    *   [#1509](https://sourceforge.net/p/pmd/bugs/1509/) \[java] InvalidSlf4jMessageFormat: NPE
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1532](https://sourceforge.net/p/pmd/bugs/1532/): \[java] CloneMethodMustImplementCloneable: Implemented Interface extends Cloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   java-typeresolution/SignatureDeclareThrowsException
    *   [#1535](https://sourceforge.net/p/pmd/bugs/1535/): \[java] SignatureDeclareThrowsException: ClassCastException with Annotation
*   PLSQL
    *   [#1520](https://sourceforge.net/p/pmd/bugs/1520/): \[plsql] Missing PL/SQL language constructs in parser: Is Of Type, Using
    *   [#1527](https://sourceforge.net/p/pmd/bugs/1527/): \[plsql] PRAGMA AUTONOMOUS_TRANSACTION gives processing errors
    *   [#1531](https://sourceforge.net/p/pmd/bugs/1531/): \[plsql] OOM/Endless loop while parsing (PL)SQL
*   General
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1517](https://sourceforge.net/p/pmd/bugs/1517/): \[java] CPD reports on Java constructors when using ignoreIdentifiers


## 27-July-2016 - 5.5.1

**New Rules:**

*   New rules for Salesforce.com Apex:
    *   apex-apexunit: ApexUnitTestClassShouldHaveAsserts, ApexUnitTestShouldNotUseSeeAllDataTrue

**Pull Requests:**

*   [#101](https://github.com/pmd/pmd/pull/101): \[java] Improve multithreading performance: do not lock on classloader
*   [#102](https://github.com/pmd/pmd/pull/102): \[apex] Restrict AvoidLogicInTrigger rule to max. 1 violation per file
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] \[apex] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled
*   [#104](https://github.com/pmd/pmd/pull/104): \[core] \[java] Close opened file handles
*   [apex #43](https://github.com/Up2Go/pmd/pull/43): \[apex] Basic apex unit test rules

**Bugfixes:**

*   Apex
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   Java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   General
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows
    *   [#1508](https://sourceforge.net/p/pmd/bugs/1508/): \[core] \[java] PMD is leaking file handles


## 25-June-2016 - 5.5.0

**System requirements:**

PMD and CPD need at least a java7 runtime environment. For analyzing Salesforce.com Apex source code,
you'll need a java8 runtime environment.

**New Supported Languages:**

*   Salesforce.com Apex is now supported by PMD and CPD. See [PR#86](https://github.com/pmd/pmd/pull/86).
*   CPD now supports Perl. See [PR#82](https://github.com/pmd/pmd/pull/82).
*   CPD now supports Swift. See [PR#33](https://github.com/adangel/pmd/pull/33).

**New and modified Rules:**

*   New rules in Java:
    *   java-logging-java/InvalidSlf4jMessageFormat: Check for invalid message format in slf4j loggers.
        See [PR#73](https://github.com/pmd/pmd/pull/73).
    *   java-design/ConstantsInInterface: Avoid constants in interfaces.
        Interfaces should define types, constants are implementation details
        better placed in classes or enums. See Effective Java, item 19.
        See [PR#93](https://github.com/pmd/pmd/pull/93).

*   Modified rules in Java:
    *   java-comments/CommentRequired: New property `serialVersionUIDCommentRequired` which controls the comment requirements
        for *serialVersionUID* fields. By default, no comment is required for this field.
    *   java-design/UseVargs: public static void main method is ignored now and so are methods, that are annotated
        with Override. See [PR#79](https://github.com/pmd/pmd/pull/79).

*   New rules for Salesforce.com Apex:
    *   apex-complexity: AvoidDeeplyNestedIfStmts, ExcessiveParameterList, ExcessiveClassLength,
        NcssMethodCount, NcssTypeCount, NcssConstructorCount, StdCyclomaticComplexity,
        TooManyFields, ExcessivePublicCount
    *   apex-performance: AvoidDmlStatementsInLoops, AvoidSoqlInLoops
    *   apex-style: VariableNamingConventions, MethodNamingConventions, ClassNamingConventions,
        MethodWithSameNameAsEnclosingClass, AvoidLogicInTrigger, AvoidGlobalModifier

*   Javascript
    *   New Rule: ecmascript-unnecessary/NoElseReturn: The else block in a if-else-construct is
        unnecessary if the `if` block contains a return. Then the content of the else block can be
        put outside. See [#1486](https://sourceforge.net/p/pmd/bugs/1486/).

**Improvements and CLI changes:**

*   A JSON-renderer for PMD which is compatible with CodeClimate. See [PR#83](https://github.com/pmd/pmd/pull/83).
*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): \[core] \[java] Provide backwards compatibility for PMD configuration file
*   CPD: If a complete filename is specified, the language dependent filename filter is not applied. This allows
    to scan files, that are not using the standard file extension. If a directory is specified, the filename filter
    is still applied and only those files with the correct file extension of the language are scanned.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)
*   CPD: New command line parameter `--ignore-usings`: Ignore using directives in C# when comparing text.
*   PMD: New command line parameter: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.
*   PMD: New command line parameter: `-filelist`- this provides an alternative way to define, which
    files should be process by PMD. With this option, you can provide the path to a single file containing a comma
    delimited list of files to analyze. If this is given, then you don't need to provide `-dir`.
    See [PR#98](https://github.com/pmd/pmd/pull/98).

**Pull Requests:**

*   [#25](https://github.com/adangel/pmd/pull/25): \[cs] Added option to exclude C# using directives from CPD analysis
*   [#27](https://github.com/adangel/pmd/pull/27): \[cpp] Added support for Raw String Literals (C++11).
*   [#29)(https://github.com/adangel/pmd/pull/29): \[jsp] Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#30](https://github.com/adangel/pmd/pull/30): \[core] CPD: Removed file filter for files that are explicitly specified on the CPD command line using the '--files' command line option.
*   [#31](https://github.com/adangel/pmd/pull/31): \[core] CPD: Added file encoding detection to CPD.
*   [#32](https://github.com/adangel/pmd/pull/32): \[objectivec] Extended Objective-C grammar to accept UTF-8 escapes (\uXXXX) in string literals.
*   [#33](https://github.com/adangel/pmd/pull/33): \[swift] Added support for Swift to CPD.
*   [#34](https://github.com/adangel/pmd/pull/34): multiple code improvements: squid:S1192, squid:S1118, squid:S1066, squid:S1854, squid:S2864
*   [#35](https://github.com/adangel/pmd/pull/35): \[javascript] Javascript tokenizer now ignores comment tokens.
*   [#72](https://github.com/pmd/pmd/pull/72): \[java] \[jsp] Added capability in Java and JSP parser for tracking tokens.
*   [#73](https://github.com/pmd/pmd/pull/73): \[java] InvalidSlf4jMessageFormat: Add rule to look for invalid message format in slf4j loggers
*   [#74](https://github.com/pmd/pmd/pull/74): \[java] CommentDefaultAccessModifier: Fix rendering CommentDefaultAccessModifier description as code
*   [#75](https://github.com/pmd/pmd/pull/75): \[core] RuleSetFactory Performance Enhancement
*   [#76](https://github.com/pmd/pmd/pull/76): \[java] DoNotCallGarbageCollectionExplicitly: fix formatting typos in an example
*   [#77](https://github.com/pmd/pmd/pull/77): \[java] \[plsql] Fix various typos
*   [#78](https://github.com/pmd/pmd/pull/78): \[java] MissingStaticMethodInNonInstantiatableClass: Add Builder pattern check
*   [#79](https://github.com/pmd/pmd/pull/79): \[java] UseVarargs: do not flag public static void main(String[]), ignore @Override
*   [#80](https://github.com/pmd/pmd/pull/80): \[site] Update mvn-plugin.md
*   [#82](https://github.com/pmd/pmd/pull/82): \[perl] Add Perl support to CPD.
*   [#83](https://github.com/pmd/pmd/pull/83): \[core] CodeClimateRenderer: Adds new Code Climate-compliant JSON renderer
*   [#84](https://github.com/pmd/pmd/pull/84): \[java] EmptyMethodInAbstractClassShouldBeAbstract: Change rule's description.
*   [#85](https://github.com/pmd/pmd/pull/85): \[java] UseStringBufferForStringAppends: False Positive with Ternary Operator (#1340)
*   [#86](https://github.com/pmd/pmd/pull/86): \[apex] Added language module for Salesforce.com Apex incl. rules ported from Java and new ones.
*   [#87](https://github.com/pmd/pmd/pull/87): \[core] \[apex] Customize Code Climate Json "categories" + "remediation_points" as PMD rule properties
*   [#88](https://github.com/pmd/pmd/pull/88): \[core] \[apex] Fixed typo in ruleset.xml and problems with the CodeClimate renderer
*   [#89](https://github.com/pmd/pmd/pull/89): \[core] Some code enhancements
*   [#90](https://github.com/pmd/pmd/pull/90): \[core] Refactored two test to stop using the deprecated ant class BuildFileTest
*   [#91](https://github.com/pmd/pmd/pull/91): \[core] \[java] \[jsp] \[plsql] \[test] \[vm] Small code enhancements, basically reordering variable declarations, constructors and variable modifiers
*   [#92](https://github.com/pmd/pmd/pull/92): \[core] \[apex] Improved Code Climate Renderer Output and a Bugfix for Apex StdCyclomaticComplexityRule on triggers
*   [#93](https://github.com/pmd/pmd/pull/93): \[java] ConstantsInInterface: Add ConstantsInInterface rule. Effective Java, 19
*   [#94](https://github.com/pmd/pmd/pull/94): \[core] \[apex] Added property, fixed code climate renderer output and deleted unused rulessets
*   [#95](https://github.com/pmd/pmd/pull/95): \[apex] AvoidDmlStatementsInLoops: New apex rule AvoidDmlStatementsInLoops
*   [#96](https://github.com/pmd/pmd/pull/96): \[core] CodeClimateRenderer: Clean up Code Climate renderer
*   [#97](https://github.com/pmd/pmd/pull/97): \[java] BooleanGetMethodName: Don't report bad method names on @Override
*   [#98](https://github.com/pmd/pmd/pull/98): \[core] PMD: Input filelist parameter
*   [#99](https://github.com/pmd/pmd/pull/99): \[apex] Fixed Trigger name is reported incorrectly
*   [#100](https://github.com/pmd/pmd/pull/100): \[core] CSVRenderer: escape filenames with commas in csvrenderer

**Bugfixes:**

*   java-basic
    *   [#1471](https://sourceforge.net/p/pmd/bugs/1471/): \[java] DoubleCheckedLocking: False positives
    *   [#1424](https://sourceforge.net/p/pmd/bugs/1424/): \[java] SimplifiedTernary: False positive with ternary operator
*   java-codesize
    *   [#1457](https://sourceforge.net/p/pmd/bugs/1457/): \[java] TooManyMethods: counts inner class methods
*   java-comments
    *   [#1430](https://sourceforge.net/p/pmd/bugs/1430/): \[java] CommentDefaultAccessModifier: triggers on field
        annotated with @VisibleForTesting
    *   [#1434](https://sourceforge.net/p/pmd/bugs/1434/): \[java] CommentRequired: raises violation on serialVersionUID field
*   java-controversial
    *   [#1449](https://sourceforge.net/p/pmd/bugs/1449/): \[java] AvoidUsingShortType: false positive when casting a variable to short
*   java-design
    *   [#1452](https://sourceforge.net/p/pmd/bugs/1452/): \[java] AccessorClassGenerationRule: ArrayIndexOutOfBoundsException with Annotations
    *   [#1479](https://sourceforge.net/p/pmd/bugs/1479/): \[java] CloseResource: false positive on Statement
    *   [#1438](https://sourceforge.net/p/pmd/bugs/1438/): \[java] UseNotifyAllInsteadOfNotify: false positive
    *   [#1467](https://sourceforge.net/p/pmd/bugs/1467/): \[java] UseUtilityClass: can't correctly check functions with multiple annotations
*   java-finalizers
    *   [#1440](https://sourceforge.net/p/pmd/bugs/1440/): \[java] AvoidCallingFinalize: NPE
*   java-imports
    *   [#1436](https://sourceforge.net/p/pmd/bugs/1436/): \[java] UnnecessaryFullyQualifiedName: false positive on clashing static imports with enums
    *   [#1465](https://sourceforge.net/p/pmd/bugs/1465/): \[java] UnusedImports: False Positve with javadoc @link
*   java-junit
    *   [#1373](https://sourceforge.net/p/pmd/bugs/1373/): \[java] JUnitAssertionsShouldIncludeMessage: is no longer compatible with TestNG
    *   [#1453](https://sourceforge.net/p/pmd/bugs/1453/): \[java] TestClassWithoutTestCases: false positive
*   java-migrating
    *   [#1446](https://sourceforge.net/p/pmd/bugs/1446/): \[java] JUnit4TestShouldUseBeforeAnnotation: False positive when TestNG is used
*   java-naming
    *   [#1431](https://sourceforge.net/p/pmd/bugs/1431/): \[java] SuspiciousEqualsMethodName: false positive
*   java-optimizations
    *   [#1443](https://sourceforge.net/p/pmd/bugs/1443/): \[java] RedundantFieldInitializer: False positive for small floats
    *   [#1340](https://sourceforge.net/p/pmd/bugs/1340/): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-sunsecure
    *   [#1476](https://sourceforge.net/p/pmd/bugs/1476/): \[java] ArrayIsStoredDirectly: False positive
    *   [#1475](https://sourceforge.net/p/pmd/bugs/1475/): \[java] MethodReturnsInternalArray: False positive
*   java-unnecessary
    *   [#1464](https://sourceforge.net/p/pmd/bugs/1464/): \[java] UnnecessaryFinalModifier: false positive on a @SafeVarargs method
    *   [#1422](https://sourceforge.net/p/pmd/bugs/1422/): \[java] UselessQualifiedThis: False positive with Java 8 Function
*   java-unusedcode
    *   [#1456](https://sourceforge.net/p/pmd/bugs/1456/): \[java] UnusedFormalParameter: should ignore overriding methods
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): \[java] UnusedLocalVariable: false positive - parenthesis
    *   [#1480](https://sourceforge.net/p/pmd/bugs/1480/): \[java] UnusedModifier: false positive on public modifier used with inner interface in enum
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): \[java] UnusedPrivateField: False positive when local variable hides member variable
        hides member variable
*   General
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): \[core] XMLRenderer: Invalid XML Characters in Output
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): \[java] Parser Error: Cast in return expression
    *   [#1441](https://sourceforge.net/p/pmd/bugs/1441/): \[site] PMD: Update documentation how to compile after modularization
    *   [#1442](https://sourceforge.net/p/pmd/bugs/1442/): \[java] PDMASMClassLoader: Java 9 Jigsaw readiness
    *   [#1455](https://sourceforge.net/p/pmd/bugs/1455/): \[java] Parser: PMD doesn't handle Java 8 explicit receiver parameters
    *   [#1458](https://sourceforge.net/p/pmd/bugs/1458/): \[xml] Performance degradation scanning large XML files with XPath custom rules
    *   [#1461](https://sourceforge.net/p/pmd/bugs/1461/): \[core] RuleSetFactory: Possible threading issue due to PR#75
    *   [#1470](https://sourceforge.net/p/pmd/bugs/1470/): \[java] Parser: Error with type-bound lambda
    *   [#1478](https://sourceforge.net/p/pmd/bugs/1478/): \[core] PMD CLI: Use first language as default if Java is not available
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): \[core] CPD: no problems found results in blank file instead of empty xml
    *   [#1485](https://sourceforge.net/p/pmd/bugs/1485/): \[apex] Analysis of some apex classes cause a stackoverflow error
    *   [#1488](https://sourceforge.net/p/pmd/bugs/1488/): \[apex] Windows line endings falsify the location of issues
    *   [#1491](https://sourceforge.net/p/pmd/bugs/1491/): \[core] CodeClimateRenderer: corrupt JSON output with real line breaks
    *   [#1492](https://sourceforge.net/p/pmd/bugs/1492/): \[core] PMD CLI: IncompatibleClassChangeError when running PMD


## 27-March-2017 - 5.4.6

The PMD team is pleased to announce PMD 5.4.6.

This is a bug fixing release.

### Table Of Contents

* [Fixed Issues](#Fixed_Issues)
* [External Contributions](#External_Contributions)

### Fixed Issues

*   general:
    *   [#305](https://github.com/pmd/pmd/issues/305): \[core] PMD not executing under git bash
*   java:
    *   [#309](https://github.com/pmd/pmd/issues/309): \[java] Parse error on method reference
*   java-design:
    *   [#275](https://github.com/pmd/pmd/issues/275): \[java] FinalFieldCouldBeStatic: Constant in @interface incorrectly reported as "could be made static"
*   java-junit:
    *   [#285](https://github.com/pmd/pmd/issues/285): \[java] JUnitTestsShouldIncludeAssertRule should support @Rule as well as @Test(expected = ...)
*   java-optimizations:
    *   [#222](https://github.com/pmd/pmd/issues/222): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-strings:
    *   [#290](https://github.com/pmd/pmd/issues/290): \[java] InefficientEmptyStringCheck misses String.trim().isEmpty()

### External Contributions

*   [#303](https://github.com/pmd/pmd/pull/303): \[java] InefficientEmptyStringCheckRule now reports String.trim().isEmpty()
*   [#307](https://github.com/pmd/pmd/pull/307): \[java] Fix false positive with UseStringBufferForStringAppendsRule
*   [#308](https://github.com/pmd/pmd/pull/308): \[java] JUnitTestsShouldIncludeAssertRule supports @Rule annotated ExpectedExceptions


## 25-Februar-2017 - 5.4.5

The PMD team is pleased to announce PMD 5.4.5

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
    *   [Modified Rules](#Modified_Rules)
* [Fixed Issues](#Fixed_Issues)
* [External Contributions](#External_Contributions)

### New and noteworthy

#### Modified Rules

*   The Java rule `UnusedModifier` (ruleset java-unusedcode) has been expanded to consider more redundant modifiers.
    *   Annotations marked as `abstract`.
    *   Nested annotations marked as `static`.
    *   Nested annotations within another interface or annotation marked as `public`.
    *   Classes, interfaces or annotations nested within an annotation marked as `public` or `static`.
    *   Nested enums marked as `static`.

### Fixed Issues

*   general
    *   [#234](https://github.com/pmd/pmd/issues/234): \[core] Zip file stream closes spuriously when loading rulesets
    *   [#256](https://github.com/pmd/pmd/issues/256): \[core] shortnames option is broken with relative paths
*   java-basic
    *   [#232](https://github.com/pmd/pmd/issues/232): \[java] SimplifiedTernary: Incorrect ternary operation can be simplified.
*   java-coupling
    *   [#270](https://github.com/pmd/pmd/issues/270): \[java] LoD false positive
*   java-design
    *   [#216](https://github.com/pmd/pmd/issues/216): \[java] \[doc] NonThreadSafeSingleton: Be more explicit as to why double checked locking is not recommended
    *   [#219](https://github.com/pmd/pmd/issues/219): \[java] UnnecessaryLocalBeforeReturn: ClassCastException in switch case with local variable returned
*   java-optimizations
    *   [#215](https://github.com/pmd/pmd/issues/215): \[java] RedundantFieldInitializer report for annotation field not explicitly marked as final
*   java-unusedcode
    *   [#246](https://github.com/pmd/pmd/issues/246): \[java] UnusedModifier doesn't check annotations
    *   [#247](https://github.com/pmd/pmd/issues/247): \[java] UnusedModifier doesn't check annotations inner classes
    *   [#248](https://github.com/pmd/pmd/issues/248): \[java] UnusedModifier doesn't check static keyword on nested enum declaration
    *   [#257](https://github.com/pmd/pmd/issues/257): \[java] UnusedLocalVariable false positive


### External Contributions

*   [#266](https://github.com/pmd/pmd/pull/266): \[java] corrected invalid reporting of LoD violation


## 28-January-2017 - 5.4.4

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


## 04-November-2016 - 5.4.3

**Summary:**

*   7 pull requests
*   16 bug fixes

**Pull Requests:**

*   [#35](https://github.com/adangel/pmd/pull/35): \[javascript] Javascript tokenizer now ignores comment tokens.
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled
*   [#110](https://github.com/pmd/pmd/pull/110): \[java] Fix parser error (issue 1530)
*   [#111](https://github.com/pmd/pmd/pull/111): \[java] Fix BooleanInstantiationRule for Java 8
*   [#112](https://github.com/pmd/pmd/pull/112): \[java] Fix ClassCastException on CloneMethodMustImplementCloneable
*   [#113](https://github.com/pmd/pmd/pull/113): \[java] Fix ClassCastException on SignatureDeclareThrowsException
*   [#119](https://github.com/pmd/pmd/pull/119): \[plsql] Fix PMD issue 1531- endless loop followed by OOM while parsing (PL)SQL

**Bugfixes:**

*   Java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] CyclomaticComplexity rule causes OOM when class reporting is disabled
    *   [#1530](https://sourceforge.net/p/pmd/bugs/1530/): \[java] Parser exception on Java code
    *   [#1490](https://sourceforge.net/p/pmd/bugs/1490/): \[java] PMD Error while processing - NullPointerException
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-design/SingularField
    *   [#1494](https://sourceforge.net/p/pmd/bugs/1494/): \[java] SingularField: lombok.Data false positive
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1532](https://sourceforge.net/p/pmd/bugs/1532/): \[java] CloneMethodMustImplementCloneable: Implemented Interface extends Cloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   java-typeresolution/SignatureDeclareThrowsException
    *   [#1535](https://sourceforge.net/p/pmd/bugs/1535/): \[java] SignatureDeclareThrowsException: ClassCastException with Annotation
*   PLSQL
    *   [#1520](https://sourceforge.net/p/pmd/bugs/1520/): \[plsql] Missing PL/SQL language constructs in parser: Is Of Type, Using
    *   [#1527](https://sourceforge.net/p/pmd/bugs/1527/): \[plsql] PRAGMA AUTONOMOUS_TRANSACTION gives processing errors
    *   [#1531](https://sourceforge.net/p/pmd/bugs/1531/): \[plsql] OOM/Endless loop while parsing (PL)SQL
*   General
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1508](https://sourceforge.net/p/pmd/bugs/1508/): \[core] \[java] PMD is leaking file handles


## 29-May-2016 - 5.4.2

**New Supported Languages:**

*   CPD supports now Swift (see [PR#33](https://github.com/adangel/pmd/pull/33)).

**Feature Request and Improvements:**

*   A JSON-renderer for PMD which is compatible with CodeClimate. See [PR#83](https://github.com/pmd/pmd/pull/83).
*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): Provide backwards compatibility for PMD configuration file

**Modified Rules:**

*   java-design/UseVargs: public static void main method is ignored now and so are methods, that are annotated
    with Override. See [PR#79](https://github.com/pmd/pmd/pull/79).

**Pull Requests:**

*   [#27](https://github.com/adangel/pmd/pull/27): Added support for Raw String Literals (C++11).
*   [#29](https://github.com/adangel/pmd/pull/29): Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#30](https://github.com/adangel/pmd/pull/30): Removed file filter for files that are explicitly specified on the CPD command line using the '--files' command line option.
*   [#31](https://github.com/adangel/pmd/pull/31): Added file encoding detection to CPD.
*   [#32](https://github.com/adangel/pmd/pull/32): Extended Objective-C grammar to accept UTF-8 escapes (\uXXXX) in string literals.
*   [#33](https://github.com/adangel/pmd/pull/33): Added support for Swift to CPD.
*   [#79](https://github.com/pmd/pmd/pull/79): do not flag public static void main(String[]) as UseVarargs; ignore @Override for UseVarargs
*   [#80](https://github.com/pmd/pmd/pull/80): Update mvn-plugin.md
*   [#83](https://github.com/pmd/pmd/pull/83): Adds new Code Climate-compliant JSON renderer
*   [#85](https://github.com/pmd/pmd/pull/85): #1340 UseStringBufferForStringAppends False Positive with Ternary Operator

**Bugfixes:**

*   java-basic/DoubleCheckedLocking:
    *   [#1471](https://sourceforge.net/p/pmd/bugs/1471/): False positives for DoubleCheckedLocking
*   java-basic/SimplifiedTernary:
    *   [#1424](https://sourceforge.net/p/pmd/bugs/1424/): False positive with ternary operator
*   java-codesize/TooManyMethods:
    *   [#1457](https://sourceforge.net/p/pmd/bugs/1457/): TooManyMethods counts inner class methods
*   java-controversial/AvoidUsingShortType:
    *   [#1449](https://sourceforge.net/p/pmd/bugs/1449/): false positive when casting a variable to short
*   java-design/AccessorClassGeneration:
    *   [#1452](https://sourceforge.net/p/pmd/bugs/1452/): ArrayIndexOutOfBoundsException with Annotations for AccessorClassGenerationRule
*   java-design/CloseResource
    *   [#1479](https://sourceforge.net/p/pmd/bugs/1479/): CloseResource false positive on Statement
*   java-design/UseUtilityClass:
    *   [#1467](https://sourceforge.net/p/pmd/bugs/1467/): UseUtilityClass can't correctly check functions with multiple annotations
*   java-imports/UnusedImports:
    *   [#1465](https://sourceforge.net/p/pmd/bugs/1465/): False Positve UnusedImports with javadoc @link
*   java-junit/TestClassWithoutTestCases:
    *   [#1453](https://sourceforge.net/p/pmd/bugs/1453/): Test Class Without Test Cases gives false positive
*   java-optimizations/UseStringBufferForStringAppends:
    *   [#1340](https://sourceforge.net/p/pmd/bugs/1340/): UseStringBufferForStringAppends False Positive with ternary operator
*   java-sunsecure/ArrayIsStoredDirectly:
    *   [#1475](https://sourceforge.net/p/pmd/bugs/1475/): False positive of MethodReturnsInternalArray
    *   [#1476](https://sourceforge.net/p/pmd/bugs/1476/): False positive of ArrayIsStoredDirectly
*   java-unnecessary/UnnecessaryFinalModifier:
    *   [#1464](https://sourceforge.net/p/pmd/bugs/1464/): UnnecessaryFinalModifier false positive on a @SafeVarargs method
*   java-unusedcode/UnusedFormalParameter:
    *   [#1456](https://sourceforge.net/p/pmd/bugs/1456/): UnusedFormalParameter should ignore overriding methods
*   java-unusedcode/UnusedLocalVariable
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): UnusedLocalVariable - false positive - parenthesis
*   java-unusedcode/UnusedModifier
    *   [#1480](https://sourceforge.net/p/pmd/bugs/1480/): false positive on public modifier used with inner interface in enum
*   General
    *   [#1455](https://sourceforge.net/p/pmd/bugs/1455/): PMD doesn't handle Java 8 explicit receiver parameters
    *   [#1458](https://sourceforge.net/p/pmd/bugs/1458/): Performance degradation scanning large XML files with XPath custom rules
    *   [#1461](https://sourceforge.net/p/pmd/bugs/1461/): Possible threading issue due to PR#75
    *   [#1470](https://sourceforge.net/p/pmd/bugs/1470/): Error with type-bound lambda
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): no problems found results in blank file instead of empty xml

**CLI Changes:**

*   CPD: If a complete filename is specified, the language dependent filename filter is not applied. This allows
    to scan files, that are not using the standard file extension. If a directory is specified, the filename filter
    is still applied and only those files with the correct file extension of the language are scanned.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)
*   New command line parameter for PMD: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.


## 04-December-2015 - 5.4.1

**Feature Request and Improvements:**

*   CPD: New command line parameter `--ignore-usings`: Ignore using directives in C# when comparing text.

**Modified Rules:**

*   java-comments/CommentRequired: New property `serialVersionUIDCommentRequired` which controls the comment requirements
    for *serialVersionUID* fields. By default, no comment is required for this field.

**Pull Requests:**

*   [#25](https://github.com/adangel/pmd/pull/25): Added option to exclude C# using directives from CPD analysis
*   [#72](https://github.com/pmd/pmd/pull/72): Added capability in Java and JSP parser for tracking tokens.
*   [#74](https://github.com/pmd/pmd/pull/74): Fix rendering CommentDefaultAccessModifier description as code
*   [#75](https://github.com/pmd/pmd/pull/75): RuleSetFactory Performance Enhancement

**Bugfixes:**

*   java-comments/CommentDefaultAccessModifier
    *   [#1430](https://sourceforge.net/p/pmd/bugs/1430/): CommentDefaultAccessModifier triggers on field
        annotated with @VisibleForTesting
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
*   java-unnecessary/UselessQualifiedThis
    *   [#1422](https://sourceforge.net/p/pmd/bugs/1422/): UselessQualifiedThis: False positive with Java 8 Function
*   java-unusedcode/UnusedPrivateField
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): False positive in UnusedPrivateField when local variable
        hides member variable
*   General
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): Invalid XML Characters in Output
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): Java - Parse Error: Cast in return expression
    *   [#1441](https://sourceforge.net/p/pmd/bugs/1441/): PMD: Update documentation how to compile after modularization


## 04-October-2015 - 5.4.0


<div style="border: 1px solid red; border-radius: 5px; border-left-width: 10px; padding: 5px 1em; background-color: lightyellow;">
<strong>Note</strong>: PMD 5.4.0 requires JDK 1.7 or above.
</div>

**Summary:**

* 9 new rules
* 4 features requests
* 18 pull requests


**Feature Request and Improvements:**

*   [#1344](https://sourceforge.net/p/pmd/bugs/1344/): AbstractNaming should check reverse
*   [#1361](https://sourceforge.net/p/pmd/bugs/1361/): ShortVariable and ShortMethodName configuration
*   [#1414](https://sourceforge.net/p/pmd/bugs/1414/): Command line parameter to disable "failOnViolation" behavior
    PMD and CPD Command Line Interfaces have a new optional parameter: `failOnViolation`. Executing PMD with the option
    `-failOnViolation false` will perform the PMD checks but won't fail the build and still exit with status 0.
    This is useful if you only want to generate the report with violations but don't want to fail your build.
*   [#1420](https://sourceforge.net/p/pmd/bugs/1420/): UnusedPrivateField: Ignore fields if using lombok

**New Rules:**

*   Java:

    *   Basic: **SimplifiedTernary** (rulesets/java/basic.xml/SimplifiedTernary)<br/>
        Ternary operator with a boolean literal can be simplified with a boolean
        expression.

    *   Clone: **CloneMethodMustBePublic** (rulesets/java/clone.xml/CloneMethodMustBePublic)<br/>
        The java manual says "By convention,
        classes that implement the `Cloneable` interface should override `Object.clone` (which is protected)
        with a public method."

    *   Clone: **CloneMethodReturnTypeMustMatchClassName** (rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName)<br/>
        If a class implements `Cloneable`
        the return type of the method `clone()` must be the class name.

    *   Comments: **CommentDefaultAccessModifier** (rulesets/java/comments.xml/CommentDefaultAccessModifier)<br/>
        In order to avoid mistakes with
        forgotten access modifiers for methods, this rule ensures, that you explicitly mark the usage of the
        default access modifier by placing a comment.

    *   Design: **SingletonClassReturningNewInstance** (rulesets/java/design.xml/SingletonClassReturningNewInstance)<br/>
        Verifies that the method called `getInstance` returns a cached instance and not always a fresh, new instance.

    *   Design: **SingleMethodRule** (rulesets/java/design.xml/SingleMethodSingletonRule)<br/>
        Verifies that there is only one method called
        `getInstance`. If there are more methods that return the singleton, then it can easily happen, that these
        are not the same instances - and thus no singleton.

    *   Unnecessary: **UselessQualifiedThis** (rulesets/java/unnecessary.xml/UselessQualifiedThis)<br/>
        Flags unnecessary qualified usages
        of this, when `this` alone would be unique. E.g. use just `this` instead of `Foo.this`.

*   Maven POM: (The rules can be found in the *pmd-xml* module)

    *   Basic: **ProjectVersionAsDependencyVersion** (rulesets/pom/basic.xml/ProjectVersionAsDependencyVersion)<br/>
        Checks the usage of `${project.version}` in Maven POM files.

    *   Basic: **InvalidDependencyTypes** (rulesets/pom/basic.xml/InvalidDependencyTypes)<br/>
        Verifies that only the default types (jar, war, ...) for dependencies are used.

Ruleset snippet to activate the new rules:

    <rule ref="rulesets/java/basic.xml/SimplifiedTernary"/>
    <rule ref="rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName"/>
    <rule ref="rulesets/java/clone.xml/CloneMethodMustBePublic"/>
    <rule ref="rulesets/java/comments.xml/CommentDefaultAccessModifier"/>
    <rule ref="rulesets/java/design.xml/SingleMethodSingleton"/>
    <rule ref="rulesets/java/design.xml/SingletonClassReturningNewInstance"/>
    <rule ref="rulesets/java/unnecessary.xml/UselessQualifiedThis"/>

    <rule ref="rulesets/pom/basic.xml/ProjectVersionAsDependencyVersion"/>
    <rule ref="rulesets/pom/basic.xml/InvalidDependencyTypes"/>


**Modified Rules:**

*   Java

    *   Basic: **CheckResultSet** (rulesets/java/basic.xml/CheckResultSet)<br/>
        Do not require to check the result of a navigation method, if it is returned.

    *   JUnit: **UseAssertTrueInsteadOfAssertEquals** (rulesets/java/junit.xml/UseAssertTrueInsteadOfAssertEquals)<br/>
        This rule also flags assertEquals, that use Boolean.TRUE/FALSE constants.

    *   Naming: **AbstractNaming** (rulesets/java/naming.xml/AbstractNaming)<br/>
        By default, this rule flags now classes,
        that are named "Abstract" but are not abstract. This behavior can be disabled by setting
        the new property `strict` to false.

    *   Naming: **ShortMethodName** (rulesets/java/naming.xml/ShortMethodName)<br/>
        Additional property `minimum` to configure the minimum required length of a method name.

    *   Naming: **ShortVariable** (rulesets/java/naming.xml/ShortVariable)<br/>
        Additional property `minimum` to configure the minimum required length of a variable name.

    *   UnusedCode: **UnusedPrivateField** (rulesets/java/unusedcode.xml/UnusedPrivateField)<br/>
        This rule won't trigger anymore if [Lombok](https://projectlombok.org) is in use.
        See [#1420](https://sourceforge.net/p/pmd/bugs/1420/).

**Renamed Rules:**

*   Java
    *   Design: **<del>UseSingleton</del>** - **UseUtilityClass** (rulesets/java/design.xml/UseUtilityClass)<br/>
        The rule "UseSingleton" *has been renamed* to "UseUtilityClass".
        See also bugs [#1059](https://sourceforge.net/p/pmd/bugs/1059) and [#1339](https://sourceforge.net/p/pmd/bugs/1339/).

**Removed Rules:**

*   Java
    *   Basic: The following rules of ruleset "Basic" were marked as deprecated and are removed with this release now:<br/>
        <br/>
        EmptyCatchBlock, EmptyIfStatement, EmptyWhileStmt, EmptyTryBlock, EmptyFinallyBlock, EmptySwitchStatements, EmptySynchronizedBlock, EmptyStatementNotInLoop, EmptyInitializer, EmptyStatementBlock, EmptyStaticInitializer
        <br/><br/>
        UnnecessaryConversionTemporary, UnnecessaryReturn, UnnecessaryFinalModifier, UselessOverridingMethod, UselessOperationOnImmutable, UnusedNullCheckInEquals, UselessParentheses
        <br/><br/>
        These rules are still available in the rulesets "Empty" (rulesets/java/empty.xml) and
        "Unnecessary" (rulesets/java/unnecessary.xml) respectively.

    *   Design: The rule "UncommentedEmptyMethod" has been renamed last release to "UncommentedEmptyMethodBody". The
        old rule name reference has been removed with this release now.

    *   Controversial: The rule "BooleanInversion" has been deprecated last release
        and has been removed with this release completely.

**Pull Requests:**

*   [#21](https://github.com/adangel/pmd/pull/21): Added PMD Rules for Singleton pattern violations.
*   [#23](https://github.com/adangel/pmd/pull/23): Extended Objective-C grammar to accept Unicode characters in identifiers
*   [#54](https://github.com/pmd/pmd/pull/54): Add a new rulesets for Maven's POM rules
*   [#55](https://github.com/pmd/pmd/pull/55): Fix run.sh for paths with spaces
*   [#56](https://github.com/pmd/pmd/pull/56): Adding support for WSDL rules
*   [#57](https://github.com/pmd/pmd/pull/57): Add default access modifier as comment rule
*   [#58](https://github.com/pmd/pmd/pull/58): Add rule for unnecessary literal boolean in ternary operators
*   [#59](https://github.com/pmd/pmd/pull/59): Add check to Boxed booleans in UseAssertTrueInsteadOfAssertEquals rule
*   [#60](https://github.com/pmd/pmd/pull/60): Add UselessQualifiedThisRule
*   [#61](https://github.com/pmd/pmd/pull/61): Add CloneMethodReturnTypeMustMatchClassName rule
*   [#62](https://github.com/pmd/pmd/pull/62): Add CloneMethodMustBePublic rule
*   [#63](https://github.com/pmd/pmd/pull/63): Change CheckResultSet to allow for the result of the navigation methods to be returned
*   [#65](https://github.com/pmd/pmd/pull/65): Fix ClassCastException in UselessOverridingMethodRule.
*   [#66](https://github.com/pmd/pmd/pull/66): #1370 ConsecutiveAppendsShouldReuse not detected properly on StringBuffer
*   [#67](https://github.com/pmd/pmd/pull/67): Use Path instead of string to check file exclusions to fix windows-only bug
*   [#68](https://github.com/pmd/pmd/pull/68): #1370 ConsecutiveAppendsShouldReuse not detected properly on StringBuffer
*   [#69](https://github.com/pmd/pmd/pull/69): #1371 InsufficientStringBufferDeclaration not detected properly on StringBuffer
*   [#70](https://github.com/pmd/pmd/pull/70): Fix code example


**Bugfixes:**

*   java-unusedcode/UnusedPrivateMethod:
    *   [#1412](https://sourceforge.net/p/pmd/bugs/1412/): UnusedPrivateMethod false positive: Issue #1403 not completely solved

**API Changes:**

*   pmd requires now JDK 1.7 or above.

*   pmd-core: `net.sourceforge.pmd.lang.symboltable.Scope`:

    The method `addNameOccurrence` returns now a Set of
    NameDeclarations to which the given occurrence has been added. This is useful in case there are ambiguous declarations
    of methods.

*   pmd-core: `net.sourceforge.pmd.lang.symboltable.AbstractScope`:

    The method `findVariableHere` returns now
    a Set of NameDeclarations which match the given occurrence.  This is useful in case there are ambiguous declarations
    of methods.


## 04-November-2016 - 5.3.8

**Summary**

*   1 feature requests
*   6 pull requests
*   17 bug fixes

**Feature Requests and Improvements:**

*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): \[core] \[java] Provide backwards compatibility for PMD configuration file

**Pull Requests:**

*   [#35](https://github.com/adangel/pmd/pull/35): \[javascript] Javascript tokenizer now ignores comment tokens.
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled
*   [#111](https://github.com/pmd/pmd/pull/111): \[java] Fix BooleanInstantiationRule for Java 8
*   [#112](https://github.com/pmd/pmd/pull/112): \[java] Fix ClassCastException on CloneMethodMustImplementCloneable
*   [#113](https://github.com/pmd/pmd/pull/113): \[java] Fix ClassCastException on SignatureDeclareThrowsException
*   [#119](https://github.com/pmd/pmd/pull/119): \[plsql] Fix PMD issue 1531- endless loop followed by OOM while parsing (PL)SQL

**Bugfixes:**

*   java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-design/CloseResource
    *   [#1479](https://sourceforge.net/p/pmd/bugs/1479/): \[java] CloseResource: false positive on Statement
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1532](https://sourceforge.net/p/pmd/bugs/1532/): \[java] CloneMethodMustImplementCloneable: Implemented Interface extends Cloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   java-typeresolution/SignatureDeclareThrowsException
    *   [#1535](https://sourceforge.net/p/pmd/bugs/1535/): \[java] SignatureDeclareThrowsException: ClassCastException with Annotation
*   java-unusedcode/UnusedLocalVariable
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): \[java] UnusedLocalVariable: false positive - parenthesis
*   java-unusedcode/UnusedModifier
    *   [#1480](https://sourceforge.net/p/pmd/bugs/1480/): \[java] UnusedModifier: false positive on public modifier used with inner interface in enum
*   plsql
    *   [#1520](https://sourceforge.net/p/pmd/bugs/1520/): \[plsql] Missing PL/SQL language constructs in parser: Is Of Type, Using
    *   [#1527](https://sourceforge.net/p/pmd/bugs/1527/): \[plsql] PRAGMA AUTONOMOUS_TRANSACTION gives processing errors
    *   [#1531](https://sourceforge.net/p/pmd/bugs/1531/): \[plsql] OOM/Endless loop while parsing (PL)SQL
*   General
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): \[core] CPD: no problems found results in blank file instead of empty xml
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1508](https://sourceforge.net/p/pmd/bugs/1508/): \[core] \[java] PMD is leaking file handles

**API Changes:**

*   New command line parameter for PMD: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)


## 30-April-2016 - 5.3.7

**New Supported Languages:**

*   CPD supports now Swift (see [PR#33](https://github.com/adangel/pmd/pull/33)).

**Feature Request and Improvements:**

*   A JSON-renderer for PMD which is compatible with CodeClimate. See [PR#83](https://github.com/pmd/pmd/pull/83).

**Modified Rules:**

*   java-design/UseVargs: public static void main method is ignored now and so are methods, that are annotated
    with Override. See [PR#79](https://github.com/pmd/pmd/pull/79).

**Pull Requests:**

*   [#27](https://github.com/adangel/pmd/pull/27): Added support for Raw String Literals (C++11).
*   [#29](https://github.com/adangel/pmd/pull/29): Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#30](https://github.com/adangel/pmd/pull/30): Removed file filter for files that are explicitly specified on the CPD command line using the '--files' command line option.
*   [#31](https://github.com/adangel/pmd/pull/31): Added file encoding detection to CPD.
*   [#32](https://github.com/adangel/pmd/pull/32): Extended Objective-C grammar to accept UTF-8 escapes (\uXXXX) in string literals.
*   [#33](https://github.com/adangel/pmd/pull/33): Added support for Swift to CPD.
*   [#79](https://github.com/pmd/pmd/pull/79): do not flag public static void main(String[]) as UseVarargs; ignore @Override for UseVarargs
*   [#80](https://github.com/pmd/pmd/pull/80): Update mvn-plugin.md
*   [#83](https://github.com/pmd/pmd/pull/83): Adds new Code Climate-compliant JSON renderer
*   [#85](https://github.com/pmd/pmd/pull/85): #1340 UseStringBufferForStringAppends False Positive with Ternary Operator

**Bugfixes:**

*   java-basic/DoubleCheckedLocking:
    *   [#1471](https://sourceforge.net/p/pmd/bugs/1471/): False positives for DoubleCheckedLocking
*   java-codesize/TooManyMethods:
    *   [#1457](https://sourceforge.net/p/pmd/bugs/1457/): TooManyMethods counts inner class methods
*   java-controversial/AvoidUsingShortType:
    *   [#1449](https://sourceforge.net/p/pmd/bugs/1449/): false positive when casting a variable to short
*   java-design/AccessorClassGeneration:
    *   [#1452](https://sourceforge.net/p/pmd/bugs/1452/): ArrayIndexOutOfBoundsException with Annotations for AccessorClassGenerationRule
*   java-design/UseUtilityClass:
    *   [#1467](https://sourceforge.net/p/pmd/bugs/1467/): UseUtilityClass can't correctly check functions with multiple annotations
*   java-imports/UnusedImports:
    *   [#1465](https://sourceforge.net/p/pmd/bugs/1465/): False Positve UnusedImports with javadoc @link
*   java-junit/TestClassWithoutTestCases:
    *   [#1453](https://sourceforge.net/p/pmd/bugs/1453/): Test Class Without Test Cases gives false positive
*   java-optimizations/UseStringBufferForStringAppends:
    *   [#1340](https://sourceforge.net/p/pmd/bugs/1340/): UseStringBufferForStringAppends False Positive with ternary operator
*   java-sunsecure/ArrayIsStoredDirectly:
    *   [#1475](https://sourceforge.net/p/pmd/bugs/1475/): False positive of MethodReturnsInternalArray
    *   [#1476](https://sourceforge.net/p/pmd/bugs/1476/): False positive of ArrayIsStoredDirectly
*   java-unnecessary/UnnecessaryFinalModifier:
    *   [#1464](https://sourceforge.net/p/pmd/bugs/1464/): UnnecessaryFinalModifier false positive on a @SafeVarargs method
*   java-unusedcode/UnusedFormalParameter:
    *   [#1456](https://sourceforge.net/p/pmd/bugs/1456/): UnusedFormalParameter should ignore overriding methods
*   General
    *   [#1455](https://sourceforge.net/p/pmd/bugs/1455/): PMD doesn't handle Java 8 explicit receiver parameters
    *   [#1458](https://sourceforge.net/p/pmd/bugs/1458/): Performance degradation scanning large XML files with XPath custom rules
    *   [#1461](https://sourceforge.net/p/pmd/bugs/1461/): Possible threading issue due to PR#75
    *   [#1470](https://sourceforge.net/p/pmd/bugs/1470/): Error with type-bound lambda

**CLI Changes:**

*   CPD: If a complete filename is specified, the language dependent filename filter is not applied. This allows
    to scan files, that are not using the standard file extension. If a directory is specified, the filename filter
    is still applied and only those files with the correct file extension of the language are scanned.


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
* Fixed [bug 1131]: CloseResource should complain if code between declaration of resource and try
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
           Added - net.sourceforge.pmd.lang.rule.xpath.XPathHandler
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
           Renamed - net.sourceforge.pmd.jaxen.Attribute to net.sourceforge.pmd.lang.rule.xpath.Attribute
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


