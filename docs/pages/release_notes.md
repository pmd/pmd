---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Updated Apex Support

*   The Apex language support has been bumped to version 54.0 (Spring '22).

#### New rules

*   The new Apex rule {% rule apex/performance/EagerlyLoadedDescribeSObjectResult %} finds
    `DescribeSObjectResult`s which could have been loaded eagerly via `SObjectType.getDescribe()`.

```xml
    <rule ref="category/apex/performance.xml/EagerlyLoadedDescribeSObjectResult" />
```

#### Modified rules

*   The Apex rule {% rule apex/bestpractices/ApexUnitTestClassShouldHaveAsserts %} has a new property
    `additionalAssertMethodPattern`. When specified the pattern is evaluated against each invoked
    method name to determine whether it represents a test assertion in addition to the standard names.

*   The Apex rule {% rule apex/documentation/ApexDoc %} has a new property `reportMissingDescription`.
    If set to `false` (default is `true` if unspecified) doesn't report an issue if the `@description`
    tag is missing. This is consistent with the ApexDoc dialect supported by derivatives such as
    [SfApexDoc](https://gitlab.com/StevenWCox/sfapexdoc) and also with analogous documentation tools for
    other languages, e.g., JavaDoc, ESDoc/JSDoc, etc.

*   The Apex rule {% rule apex/security/ApexCRUDViolation %} has a couple of new properties:
    These allow specification of regular-expression-based patterns for additional methods that should
    be considered valid for pre-CRUD authorization beyond those offered by the system Apex checks and
    ESAPI, e.g., [`sirono-common`'s `AuthorizationUtil` class](https://github.com/SCWells72/sirono-common#authorization-utilities).
    Two new properties have been added per-CRUD operation, one to specify the naming pattern for a method
    that authorizes that operation and another to specify the argument passed to that method that contains
    the `SObjectType` instance of the type being authorized. Here is an example of these new properties:
    
    ```xml
    <rule ref="category/apex/security.xml/ApexCRUDViolation" message="...">
      <priority>3</priority>
      <properties>
        <property name="createAuthMethodPattern" value="AuthorizationUtil\.(is|assert)(Createable|Upsertable)"/>
        <!--
         There's one of these properties for each operation, and the default value is 0 so this is technically
         superfluous, but it's included it here for example purposes.
         -->
        <property name="createAuthMethodTypeParamIndex" value="0"/>
        <property name="readAuthMethodPattern" value="AuthorizationUtil\.(is|assert)Accessible"/>
        <property name="updateAuthMethodPattern" value="AuthorizationUtil\.(is|assert)(Updateable|Upsertable)"/>
        <property name="deleteAuthMethodPattern" value="AuthorizationUtil\.(is|assert)Deletable"/>
        <property name="undeleteAuthMethodPattern" value="AuthorizationUtil\.(is|assert)Undeletable"/>
        <property name="mergeAuthMethodPattern" value="AuthorizationUtil\.(is|assert)Mergeable"/>
      </properties>
    </rule>
    ```

*   The Apex rule {% rule apex/errorprone/EmptyStatementBlock %} has two new properties:
    
    Setting `reportEmptyPrivateNoArgConstructor` to `false` ignores empty private no-arg constructors
    that are commonly used in singleton pattern implementations and utility classes in support of
    prescribed best practices.
    
    Setting `reportEmptyVirtualMethod` to `false` ignores empty virtual methods that are commonly used in
    abstract base classes as default no-op implementations when derived classes typically only override a
    subset of virtual methods.
    
    By default, both properties are `true` to not change the default behaviour of this rule.

*   The Apex rule {% rule apex/errorprone/EmptyCatchBlock %} has two new properties modeled after the analgous Java rule:
    
    The `allowCommentedBlocks` property, when set to `true` (defaults to `false`), ignores empty blocks containing comments, e.g.:

    ```apex
    try {
        doSomethingThatThrowsAnExpectedException();
        System.assert(false, 'Expected to catch an exception.');
    } catch (Exception e) {
        // Expected
    }
    ```

    The `allowExceptionNameRegex` property is a regular expression for exception variable names for which empty catch blocks should be ignored by this rule. For example, using the default property value of `^(ignored|expected)$`, the following empty catch blocks will not be reported:

    ```apex
    try {
        doSomethingThatThrowsAnExpectedException();
        System.assert(false, 'Expected to catch an exception.');
    } catch (IllegalStateException ignored) {
    } catch (NumberFormatException expected) {
    }
    ```

*   The Apex rule {% rule apex/codestyle/OneDeclarationPerLine %} has a new property `reportInForLoopInitializer`:
    If set to `false` (default is `true` if unspecified) doesn't report an issue for multiple declarations in
    a `for` loop's initializer section. This is support the common idiom of one declaration for the loop variable
    and another for the loop bounds condition, e.g.,
    
    ```apex
    for (Integer i = 0, numIterations = computeNumIterations(); i < numIterations; i++) {
    }
    ```

### Fixed Issues

*   apex
    *   [#1089](https://github.com/pmd/pmd/issues/1089): \[apex] ApexUnitTestClassShouldHaveAsserts: Test asserts in other methods not detected
    *   [#1090](https://github.com/pmd/pmd/issues/1090): \[apex] ApexCRUDViolation: checks not detected if done in another method
    *   [#3532](https://github.com/pmd/pmd/issues/3532): \[apex] Promote usage of consistent getDescribe() info
    *   [#3566](https://github.com/pmd/pmd/issues/3566): \[apex] ApexDoc rule should not require "@description"
    *   [#3568](https://github.com/pmd/pmd/issues/3568): \[apex] EmptyStatementBlock: should provide options to ignore empty private constructors and empty virtual methods
    *   [#3569](https://github.com/pmd/pmd/issues/3569): \[apex] EmptyCatchBlock: should provide an option to ignore empty catch blocks in test methods
    *   [#3570](https://github.com/pmd/pmd/issues/3570): \[apex] OneDeclarationPerLine: should provide an option to ignore multiple declarations in a for loop initializer
    *   [#3576](https://github.com/pmd/pmd/issues/3576): \[apex] ApexCRUDViolation should provide an option to specify additional patterns for methods that encapsulate authorization checks
    *   [#3579](https://github.com/pmd/pmd/issues/3579): \[apex] ApexCRUDViolation: false negative with undelete
*   java-errorprone
    *   [#3560](https://github.com/pmd/pmd/issues/3560): \[java] InvalidLogMessageFormat: False positive with message and exception in a block inside a lambda
*   java-performance
    *   [#2364](https://github.com/pmd/pmd/issues/2364): \[java] AddEmptyString false positive in annotation value

### API Changes

### External Contributions

*   [#3538](https://github.com/pmd/pmd/pull/3538): \[apex] New rule EagerlyLoadedDescribeSObjectResult - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3549](https://github.com/pmd/pmd/pull/3549): \[java] Ignore AddEmptyString rule in annotations - [Stanislav Myachenkov](https://github.com/smyachenkov)
*   [#3561](https://github.com/pmd/pmd/pull/3561): \[java] InvalidLogMessageFormat: False positive with message and exception in a block inside a lambda - [Nicolas Filotto](https://github.com/essobedo)
*   [#3565](https://github.com/pmd/pmd/pull/3565): \[doc] Fix resource leak due to Files.walk - [lujiefsi](https://github.com/lujiefsi)
*   [#3571](https://github.com/pmd/pmd/pull/3571): \[apex] Fix for #1089 - Added new configuration property additionalAssertMethodPattern to ApexUnitTestClassShouldHaveAssertsRule - [Scott Wells](https://github.com/SCWells72)
*   [#3572](https://github.com/pmd/pmd/pull/3572): \[apex] Fix for #3566 - Added new configuration property reportMissingDescription to ApexDocRule - [Scott Wells](https://github.com/SCWells72)
*   [#3573](https://github.com/pmd/pmd/pull/3573): \[apex] Fix for #3568 - Added new configuration properties reportEmptyPrivateNoArgConstructor and reportEmptyVirtualMethod to EmptyStatementBlock - [Scott Wells](https://github.com/SCWells72)
*   [#3574](https://github.com/pmd/pmd/pull/3574): \[apex] Fix for #3569 - Added new configuration properties allowCommentedBlocks and allowExceptionNameRegex to EmptyCatchBlock - [Scott Wells](https://github.com/SCWells72)
*   [#3575](https://github.com/pmd/pmd/pull/3575): \[apex] Fix for #3570 - Added new configuration property reportInForLoopInitializer to OneDeclarationPerLine - [Scott Wells](https://github.com/SCWells72)
*   [#3577](https://github.com/pmd/pmd/pull/3577): \[apex] Fix for #3576 - Added new configuration properties \*AuthMethodPattern and \*AuthMethodTypeParamIndex to ApexCRUDViolation rule - [Scott Wells](https://github.com/SCWells72)
*   [#3578](https://github.com/pmd/pmd/pull/3578): \[apex] ApexCRUDViolation: Documentation changes for #3576 - [Scott Wells](https://github.com/SCWells72)
*   [#3580](https://github.com/pmd/pmd/pull/3580): \[doc] Release notes updates for the changes in issue #3569 - [Scott Wells](https://github.com/SCWells72)
*   [#3581](https://github.com/pmd/pmd/pull/3581): \[apex] #3569 - Requested changes for code review feedback - [Scott Wells](https://github.com/SCWells72)

{% endtocmaker %}

