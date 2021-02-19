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

#### Modified Rules

*   The Apex rule {% rule "apex/documentation/ApexDoc" %} has two new properties: `reportPrivate` and
    `reportProtected`. Previously the rule only considered public and global classes, methods, and
    properties. With these properties, you can verify the existence of ApexDoc comments for private
    and protected methods as well. By default, these properties are disabled to preserve backwards
    compatible behavior.

### Fixed Issues

*   apex-documentation
    *   [#3075](https://github.com/pmd/pmd/issues/3075): \[apex] ApexDoc should support private access modifier

### API Changes

### External Contributions

*   [#3098](https://github.com/pmd/pmd/pull/3098): \[apex] ApexDoc optionally report private and protected

{% endtocmaker %}

