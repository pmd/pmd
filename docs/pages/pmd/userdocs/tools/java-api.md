---
title: PMD Java API
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_java_api.html
last_updated: August 2023 (7.0.0)
---

The easiest way to run PMD is to just use a build plugin in your favorite build tool
like [Apache Ant](pmd_userdocs_tools_ant.html), [Apache Maven](pmd_userdocs_tools_maven.html) or
[Gradle](pmd_userdocs_tools_gradle.html).

There are also many integrations for IDEs available, see [Tools](pmd_userdocs_tools.html).

If you have your own build tool or want to integrate PMD in a different way, you can call PMD programmatically,
as described here.

## Dependencies

You'll need to add the dependency to the language, you want to analyze. For Java, it will be
`net.sourceforge.pmd:pmd-java`. If you use Maven, you can add a new (compile time) dependency like this:

``` xml
<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-java</artifactId>
    <version>${pmdVersion}</version>
</dependency>
```

Note: You'll need to select a specific version. This is done in the example via the property `pmdVersion`.

This will transitively pull in the artifact `pmd-core` which contains the API.

## Running PMD programmatically

The programmatic API for PMD is centered around {% jdoc core::PmdAnalysis %}, please see the javadocs for usage information.

## Running CPD programmatically

The programmatic API for CPD is centered around {% jdoc core::cpd.CpdAnalysis %}, please see the javadocs for usage information.
