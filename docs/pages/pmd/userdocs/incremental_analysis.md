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
untouched, files with violations will be listed with full detail. Therefore, its usage is highly recommended.


### Enabling incremental analysis

Incremental analysis is enabled automatically once a location to store the cache has been defined.
From command-line that is done through the [`-cache`](pmd_userdocs_cli_reference.html#cache) argument, but support for the feature is
available for tools integrating PMD such as [Ant](pmd_userdocs_tools_ant.html),
[Maven](pmd_userdocs_tools_maven.html), and [Gradle](pmd_userdocs_tools_gradle.html).


### Disabling incremental analysis

By default, PMD will suggest to use an analysis cache by logging a warning.
If you'd like to disable this warning, or ignore the analysis cache for a
few runs, you can use the [`-no-cache`](pmd_userdocs_cli_reference.html#no-cache) switch.


### FAQ

#### When is the cache invalidated?

On the following reasons, the complete cache file is considered invalid:

* The PMD version differs. Since each PMD version might have fixed some false-positives or false-negatives for rules,
  a cache file created with a different version is considered invalid. The version comparison is exact.
* The used ruleset has been changed. If the ruleset is changed in any way (e.g. adding/removing rules, changing
  rule properties, ...), the cache is considered invalid.
* The [`auxclasspath`](pmd_userdocs_cli_reference.html#auxclasspath) changed. The auxclasspath is used during
  type resolution. A changed auxclasspath can result for rules, that use type resolution, in different
  violations. Usually, if the auxclasspath is correct and type resolution works, the rules report less false-positives.
  To make sure, the correct violations are reported, the cache is considered invalid, if the auxclasspath has changed.
* The execution classpath has been changed. On the execution classpath not only the PMD classes are located, but also
  the implementation of e.g. custom rules. If any jar file/class file on the execution classpath is changed, then
  the cache is considered invalid as well.

#### What is stored in the cache file?

The cache file consists of a header and a body. The header stores the information which is used to decided
whether the whole cache file is valid or not (see above). The following information is stored:

* PMD Version
* Ruleset checksum
* Auxclasspath checksum
* Execution classpath checksum

The body contains an entry for every file that has been analyzed. For every file, the following information
is stored:

* The full (absolute) pathname of the file
* The checksum of the file itself
* 0 or more rule violations with all the info (line number, etc.)

You can think of the cache as a Map where the filepath is used as the key
and the violations found in previous runs are the value.

The cache is in the end just a file with serialized data (binary). The implementation is
{% jdoc core::cache.FileAnalysisCache %}.

#### How does PMD detect whether a file has been changed?

When analyzing a file, PMD records the checksum of the file content and stores this
together with the violations in the cache file. When running PMD with the cache file,
PMD looks up the file in the cache and compares the checksums.
If the checksums match, then the file is not even parsed, the rules
are not executed and the violations for this file are entirely used from the cache.
If the checksum doesn't match, then the cached violations are discarded (if there are any)
and the file is fully processed: the file is parsed and all the rules are run for it.
After we are done, the cache is updated with the new violations.

#### Can I reuse a cache created on branch A for analyzing my project on branch B?

This is possible. As long as the same PMD version and same ruleset is used on both branches.
Also note, that if the branch uses a different dependencies, the auxclasspath is different on both
classes, which invalidates the cache completely. If you project uses e.g. Maven for dependency
management and your branch uses different dependencies (either different version or completely different
artifacts), then the auxclasspath is changed.

If files have been renamed on the branch, these files will be analyzed again since PMD uses
the file names to assign existing rule violations from the cache. Also, if the full path name
of the file changes, because the other branch is checked out at a different location, then all
the cached files don't match.

Apart from these restrictions, PMD will only analyze files that changed between runs.
If your previous run was on branch A and then you run on branch B using the same cache file,
it will only look at files that are different between the 2 branches.

#### Can I reuse a cache file across different machines?

This is only possible, if the other machine uses the exact same path names. That means that
your project needs to be checked out into the same directory structure.

Additionally, all the other restrictions apply (same PMD version, same ruleset, same auxclasspath,
same execution classpath).

See also issue [#2063 [core] Support sharing incremental analysis cache file across different machines](https://github.com/pmd/pmd/issues/2063).
