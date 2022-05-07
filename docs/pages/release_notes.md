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

### Fixed Issues

* javascript
  * [#3948](https://github.com/pmd/pmd/issues/3948): \[js] Invalid operator error for method property in object literal

### API Changes

#### Deprecated ruleset references

Ruleset references with the following formats are now deprecated and will produce a warning
when used on the CLI or in a ruleset XML file:
- `<lang-name>-<ruleset-name>`, eg `java-basic`, which resolves to `rulesets/java/basic.xml`
- the internal release number, eg `600`, which resolves to `rulesets/releases/600.xml`

Use the explicit forms of these references

#### Deprecated API

- {% jdoc core::RuleSetReferenceId#toString() %} is now deprecated. The format of this
 method will remain the same until PMD 7. The deprecation is intended to steer users
 away from relying on this format, as it may be changed in PMD 7.

### External Contributions

{% endtocmaker %}

