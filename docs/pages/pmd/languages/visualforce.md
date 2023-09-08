---
title: Visualforce Support
permalink: pmd_languages_visualforce.html
last_updated: September 2023
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
author: Andreas Dangel
summary: "Visualforce-specific features and guidance"
---

> [Visualforce](https://developer.salesforce.com/docs/atlas.en-us.pages.meta/pages/) consists of a tag-based markup
> language that gives developers way to build applications and customize the Salesforce user interface.

{% include language_info.html name='Salesforce Visualforce' id='vf' implementation='visualforce::lang.vf.VfLanguageModule' supports_pmd=true supports_cpd=true since='5.6.0' %}

## Language Properties

See [Visualforce language properties](pmd_languages_configuration.html#visualforce-language-properties)

## Type resolution

Since PMD 6.30.0 support for type resolution has been added.

The Visualforce AST now can resolve the data type of Visualforce expressions that reference
Apex Controller properties and Custom Object fields. This feature improves the precision of existing rules,
like {% rule vf/security/VfUnescapeEl %}.

This can be configured using two language properties, which can be set as environment variables:

*   `PMD_VF_APEX_DIRECTORIES`: Comma separated list of directories for Apex classes. Absolute or relative
    to the Visualforce directory. Default is `../classes`. Specifying an empty string will disable data type
    resolution for Apex Controller properties.

*   `PMD_VF_OBJECTS_DIRECTORIES`: Comma separated list of directories for Custom Objects. Absolute or relative
    to the Visualforce directory. Default is `../objects`. Specifying an empty string will disable data type
    resolution for Custom Object fields.

{% include warning.html content="
These env vars have changed from PMD 6 to PMD 7:
* `PMD_VF_APEXDIRECTORIES` ➡️ `PMD_VF_APEX_DIRECTORIES`
* `PMD_VF_OBJECTSDIRECTORIES` ➡️ `PMD_VF_OBJECTS_DIRECTORIES`
"%}

This feature is experimental, in particular, expect changes to the way the configuration is specified.
We'll probably extend the CLI instead of relying on environment variables in a future version.

### Sample usage

```
PMD_VF_APEXDIRECTORIES=../classes \
PMD_VF_OBJECTSDIRECTORIES=../objects \
pmd check -d $GITHUB_WORKSPACE/force-app/main/default/pages \
    -R category/vf/security.xml/VfUnescapeEl -f text
```

If you run with debug logging turned on, you might see log messages like this:

```
Okt. 14, 2021 11:30:44 AM net.sourceforge.pmd.lang.vf.VfExpressionTypeVisitor visit
FINE: Unable to determine type for: Account.NotFoundField__c
```

This means, that type resolution didn't work. Maybe the provided directories are missing or do not contain
the needed data.
