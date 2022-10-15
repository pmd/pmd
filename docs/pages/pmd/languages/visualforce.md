---
title: Visualforce Support
permalink: pmd_languages_visualforce.html
author: Andreas Dangel
last_updated: October 2021
---

## Type resolution

Since PMD 6.30.0 support for type resolution has been added.

The Visualforce AST now can resolve the data type of Visualforce expressions that reference
Apex Controller properties and Custom Object fields. This feature improves the precision of existing rules,
like {% rule vf/security/VfUnescapeEl %}.

This can be configured using two environment variables:

*   `PMD_VF_APEXDIRECTORIES`: Comma separated list of directories for Apex classes. Absolute or relative
    to the Visualforce directory. Default is `../classes`. Specifying an empty string will disable data type
    resolution for Apex Controller properties.

*   `PMD_VF_OBJECTSDIRECTORIES`: Comma separated list of directories for Custom Objects. Absolute or relative
    to the Visualforce directory. Default is `../objects`. Specifying an empty string will disable data type
    resolution for Custom Object fields.

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
