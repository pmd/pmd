---
title: Adding PMD support for a new dialect for an already existing language
short_title: Adding a new dialect
tags: [devdocs, extending]
summary: "How to add a new dialect."
last_updated: April 2025 (7.13.0)
sidebar: pmd_sidebar
permalink: pmd_devdocs_major_adding_dialect.html
folder: pmd/devdocs
---

{% include callout.html type="info" content="

**What is a dialect?**<br><br>

A dialect is a particular form of another supported language. For example, an XSLT is a particular form of an XML.
Even though the dialect has it's own seantics and uses, the contents are still readable by any tool capable of understanding the base language.

In PMD, a dialect allows to set up completely custom rules, XPath functions, properties and metrics for these files;
while retaining the full support of the underlaying language. That means:

- All rules applicable to the base language are automatically applicable to all files processed as a dialect.
- All XPath functions existing in the base language are available when creating new rules.
- All metrics supported by the base language are available when creating new rules.
- All properties (ie: support to suppress literals in CPD) supported by the base language are supported by the dialect.

" %}

## Steps

### 1.  Create a dialect module
*   Dialects usually reside in the same module of the base language they leverage; but can technically live standalone in a separate module if needed.
*   Create your subclass of `net.sourceforge.pmd.lang.impl.SimpleDialectLanguageModuleBase`, see XSL as an example: [`XslDialectModule`](https://github.com/pmd/pmd/blob/main/pmd-xml/src/main/java/net/sourceforge/pmd/lang/xml/xsl/XslDialectModule.java).
*   For a minimal implementation, it just needs a constructor calling supper with the required metadata.
    Dialect metadata is created through the builder obtained `LanguageMetadata.withId`
    *   Define the human readable name of the language by calling `name`
    *   Define all extensions PMD should consider when applying this dialect by calling `extensions`
    *   Add for each version of your language a call to `addVersion` in your language module’s constructor.
        Use `addDefaultVersion` for defining the default version.
    *   Finalize the metadata construiction by calling `asDialectOf` to reference the base language by id.
*   Create the service registration via the text file `src/main/resources/META-INF/services/net.sourceforge.pmd.lang.Language`.
    Add your fully qualified class name as a single line into it.

### 2. Create a language handler (Optional)
*   This step is only required if you either want the dialect to:
    *   expose additional XPath functions
    *   compute additional metrics
    *   customize violation suppress logic
    *   define {% jdoc core::reporting.ViolationDecorator %}s, to add additional dialect specific information to the
        created violations. The [Java language module](pmd_languages_java.html#violation-decorators) uses this to
        provide the method name or class name, where the violation occurred.
*   To do this, create a new class extending from [`BasePmdDialectLanguageVersionHandler`](https://github.com/pmd/pmd/blob/main/pmd-core/src/main/java/net/sourceforge/pmd/lang/impl/BasePmdDialectLanguageVersionHandler.java), and override the getter corresponding to what you want to extend.
    You don't need to worry about including anything from the base language, only include your extensions. PMD will take care of merging everything together.
*   Ensure to pass a new instance of your dialect handler as a second parameter in your dialect module (see Step 1) when calling `super`.

### 3. Create rules
*   Creating rules is already pretty well documented in PMD - and it’s no different for a new dialect.
*   PMD supports 2 types of rules, through visitors or XPath.
*   To add a visitor rule:
    *   You need to extend the abstract rule provided by the base language, for instance in XML dialects, you would extend [`AbstractXmlRule`](https://github.com/pmd/pmd/blob/main/pmd-xml/src/main/java/net/sourceforge/pmd/lang/xml/rule/AbstractXmlRule.java).
    Note, that all rule classes should be suffixed with `Rule` and should be placed
    in a package the corresponds to their dialect and category.
*   To add an XPath rule you can follow our guide [Writing XPath Rules](pmd_userdocs_extending_writing_xpath_rules.html).
*   When creating the category ruleset XML file, the XML can reference build properties that are replaced
    during the build. This is used for the `externalInfoUrl` attribute of a rule. E.g. we use `${pmd.website.baseurl}`
    to point to the correct webpage (depending on the PMD version).

### 4. Test the rules
*   Testing rules is described in depth in [Testing your rules](pmd_userdocs_extending_testing.html).
    *   Each rule has its own test class: Create a test class for your rule extending `PmdRuleTst`
        *(see
        [`UnavailableFunctionTest`](https://github.com/pmd/pmd/blob/main/pmd-swift/src/test/java/net/sourceforge/pmd/lang/swift/rule/bestpractices/UnavailableFunctionTest.java)
        for example)*
    *   Create a category rule set for your dialect *(see
        [`pmd-swift/src/main/resources/bestpractices.xml`](https://github.com/pmd/pmd/blob/main/pmd-swift/src/main/resources/category/swift/bestpractices.xml)
        for example)*
    *   Place the test XML file with the test cases in the correct location
    *   When executing the test class
        *   this triggers the unit test to read the corresponding XML file with the rule test data
            *(see
            [`UnavailableFunction.xml`](https://github.com/pmd/pmd/blob/main/pmd-swift/src/test/resources/net/sourceforge/pmd/lang/swift/rule/bestpractices/xml/UnavailableFunction.xml)
            for example)*
        *   This test XML file contains sample pieces of code which should trigger a specified number of
            violations of this rule. The unit test will execute the rule on this piece of code, and verify
            that the number of violations matches.
*   To verify the validity of all the created rulesets, create a subclass of `AbstractRuleSetFactoryTest`
    (*see `RuleSetFactoryTest` in pmd-swift for example)*.
    This will load all rulesets and verify, that all required attributes are provided.

    *Note:* You'll need to add your ruleset to `categories.properties`, so that it can be found.

### 5. Create documentation page
Finishing up your new dialect by adding a page in the documentation. Create a new markdown file
`<langId>.md` in `docs/pages/pmd/languages/`. This file should have the following frontmatter:

```
---
title: <Language Name>
permalink: pmd_languages_<langId>.html
last_updated: <Month> <Year> (<PMD Version>)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
---
```

On this page, language specifics can be documented, e.g. when the language was first supported by PMD.
There is also the following Jekyll Include, that creates summary box for the language:

```
{% raw %}
{% include language_info.html name='<Language Name>' id='<langId>' implementation='<langId>::lang.<langId>.<langId>LanguageModule' supports_cpd=true supports_pmd=true %}
{% endraw %}
```

