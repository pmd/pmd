---
title: Visualforce Support
permalink: pmd_languages_visualforce.html
last_updated: February 2024 (7.0.0)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
author: Andreas Dangel
summary: "Visualforce-specific features and guidance"
---

> [Visualforce](https://developer.salesforce.com/docs/atlas.en-us.pages.meta/pages/) consists of a tag-based markup
> language that gives developers way to build applications and customize the Salesforce user interface.

{% include language_info.html name='Salesforce Visualforce' id='visualforce' implementation='visualforce::lang.visualforce.VfLanguageModule' supports_pmd=true supports_cpd=true since='5.6.0' %}

{% capture vf_id_note %}
The language id of Visualforce was in PMD 6 just "vf". In PMD 7, this has been changed to "visualforce". Also the
package name of the classes has been changed from vf to "visualforce".
{% endcapture %}
{% include note.html content=vf_id_note %}

## Language Properties

See [Visualforce language properties](pmd_languages_configuration.html#visualforce-language-properties)

## Type resolution

Since PMD 6.30.0 support for type resolution has been added.

The Visualforce AST now can resolve the data type of Visualforce expressions that reference
Apex Controller properties and Custom Object fields. This feature improves the precision of existing rules,
like {% rule visualforce/security/VfUnescapeEl %}.

This can be configured using two language properties, which can be set as environment variables:

*   `PMD_VISUALFORCE_APEX_DIRECTORIES`: Comma separated list of directories for Apex classes. Absolute or relative
    to the Visualforce directory. Default is `../classes`. Specifying an empty string will disable data type
    resolution for Apex Controller properties.

*   `PMD_VISUALFORCE_OBJECTS_DIRECTORIES`: Comma separated list of directories for Custom Objects. Absolute or relative
    to the Visualforce directory. Default is `../objects`. Specifying an empty string will disable data type
    resolution for Custom Object fields.

{% include warning.html content="
These env vars have changed from PMD 6 to PMD 7:
* `PMD_VF_APEXDIRECTORIES` ➡️ `PMD_VISUALFORCE_APEX_DIRECTORIES`
* `PMD_VF_OBJECTSDIRECTORIES` ➡️ `PMD_VISUALFORCE_OBJECTS_DIRECTORIES`
"%}

### Sample usage

```
PMD_VISUALFORCE_APEXDIRECTORIES=../classes \
PMD_VISUALFORCE_OBJECTSDIRECTORIES=../objects \
pmd check -d $GITHUB_WORKSPACE/force-app/main/default/pages \
    -R category/visualforce/security.xml/VfUnescapeEl -f text
```

If you run with debug logging turned on, you might see log messages like this:

```
Okt. 14, 2021 11:30:44 AM net.sourceforge.pmd.lang.visualforce.VfExpressionTypeVisitor visit
FINE: Unable to determine type for: Account.NotFoundField__c
```

This means, that type resolution didn't work. Maybe the provided directories are missing or do not contain
the needed data.
