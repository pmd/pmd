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

#### New rules

*   The new Apex rule {% rule "apex/errorprone/InaccessibleAuraEnabledGetter" %} checks that an `AuraEnabled`
    getter is public or global. This is necessary if it is referenced in Lightning components.
    You can try out this rule like so:

```xml
    <rule ref="category/apex/errorprone.xml/InaccessibleAuraEnabledGetter" />
```

### Fixed Issues


*   apex
    *   [#3321](https://github.com/pmd/pmd/issues/3321): \[apex] New rule to detect inaccessible AuraEnabled getters (summer '21 security update)
    *   [#3332](https://github.com/pmd/pmd/issues/3332): \[apex] CognitiveComplexity - incorrect increment for "else if"


### API Changes

### External Contributions

*   [#3322](https://github.com/pmd/pmd/pull/3322): \[apex] added rule to detect inaccessible AuraEnabled getters - [Philippe Ozil](https://github.com/pozil)


{% endtocmaker %}

