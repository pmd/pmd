---
title: Incremental Analysis
keywords: [pmd, options, command, incremental, analysis, performance]
tags: [userdocs]
summary: "Explains how to use incremental analysis to speed up analysis"
permalink: pmd_userdocs_incremental_analysis.html
sidebar: pmd_sidebar
---

Ever since PMD 5.6.0, PMD has been able to perform Incremental Analysis.

When performing Incremental Analysis for the first time, PMD will cache analysis data and results.
This allows subsequent analysis to only look into those files that are new / have changed. For
a typical development environment, where you only change a few files at a time, this can reduce
analysis time dramatically.

The generated report will be *exactly the same* as it would if running without incremental analysis.
Files included in the final report will reflect exactly those files in your filesystem. Even if
untouched, files with violations will be listed with full detail. Therefore, its usage is higly recommended.


### Enabling incremental analysis

Incremental analysis is enabled automatically once a location to store the cache has been defined.
From command-line that is done through the [`-cache`](pmd_userdocs_cli_reference.html#cache) argument, but support for the feature is
available for tools integrating PMD such as [Ant](pmd_userdocs_tools_ant.html),
[Maven](pmd_userdocs_tools_maven.html), and Gradle.


### Disabling incremental analysis

By default, PMD will suggest to use an analysis cache by logging a warning.
If you'd like to disable this warning, or ignore the analysis cache for a
few runs, you can use the [`-no-cache`](pmd_userdocs_cli_reference.html#no-cache) switch.
