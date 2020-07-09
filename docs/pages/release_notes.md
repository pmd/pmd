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

*   apex-bestpractices
    *   [#2626](https://github.com/pmd/pmd/issues/2626): \[apex] UnusedLocalVariable - false positive on case insensitivity allowed in Apex
*   apex-security
    *   [#2620](https://github.com/pmd/pmd/issues/2620): \[visualforce] False positive on VfUnescapeEl with new Message Channel feature

### API Changes

### External Contributions

*   [#2621](https://github.com/pmd/pmd/pull/2621): \[visualforce] add new safe resource for VfUnescapeEl - [Peter Chittum](https://github.com/pchittum)

{% endtocmaker %}

