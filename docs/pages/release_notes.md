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

#### Scala cross compilation

Up until now the PMD Scala module has been compiled against scala 2.13 only by default.
However, this makes it impossible to use pmd as a library in scala projects,
that use scala 2.12, e.g. in sbt plugins. Therefore PMD now provides cross compiled pmd-scala
modules for both versions: **scala 2.12** and **scala 2.13**.

The new modules have new maven artifactIds. The old artifactId `net.sourceforge.pmd:pmd-scala:{{ site.pmd.version }}`
is still available, but is deprecated from now on. It has been demoted to be just a delegation to the new
`pmd-scala_2.13` module and will be removed eventually.

The coordinates for the new modules are:

```
<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-scala_2.12</artifactId>
    <version>{{ site.pmd.version }}</version>
</dependency>

<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-scala_2.13</artifactId>
    <version>{{ site.pmd.version }}</version>
</dependency>
```

The command line version of PMD continues to use **scala 2.13**.

### Fixed Issues

*   scala
    *   [#2547](https://github.com/pmd/pmd/pull/2547): \[scala] Add cross compilation for scala 2.12 and 2.13

### API Changes

*   The maven module `net.sourceforge.pmd:pmd-scala` is deprecated. Use `net.sourceforge.pmd:pmd-scala_2.13`
    or `net.sourceforge.pmd:pmd-scala_2.12` instead.

### External Contributions

*   [#2547](https://github.com/pmd/pmd/pull/2547): \[scala] Add cross compilation for scala 2.12 and 2.13 - [João Ferreira](https://github.com/jtjeferreira)

{% endtocmaker %}

