# pmd-compat6

This module contains classes from PMD6, that have been removed in PMD7 and also restores
some removed methods.

The goal is, that PMD7 can be used with [Maven PMD Plugin](https://maven.apache.org/plugins/maven-pmd-plugin)
without any further changes to the plugin.

The maven-pmd-plugin uses by default PMD Version 6.55.0, but it can be configured to
[Use a new PMD version at runtime](https://maven.apache.org/plugins/maven-pmd-plugin/examples/upgrading-PMD-at-runtime.html).

Since PMD7 introduces many incompatible changes, another module is needed to restore
compatibility. This is this module.

In order to use this compatibility module, it needs to be added as the _first_ dependency
when configuring maven-pmd-plugin.

It is as simple as adding:

```xml
<dependency>
  <groupId>net.sourceforge.pmd</groupId>
  <artifactId>pmd-compat6</artifactId>
  <version>${pmdVersion}</version>
</dependency>
```

Note: The dependency "pmd-compat6" must be listed _first_ before pmd-core, pmd-java, and the others.

Note: Once the default version of PMD is upgraded to PMD7 in maven-pmd-plugin
(see [MPMD-379](https://issues.apache.org/jira/projects/MPMD/issues/MPMD-379)), this
compatibility module is no longer needed. The module pmd-compat6 might not be maintaned then
any further, hence it is already declared as deprecated.

The primary goal for this module is, to get maven-pmd-plugin working with PMD7. It might
be useful in other contexts, too, but no guarantee is given, that is works.

No guarantee is given, that the (deprecated) module pmd-compat6 is being maintained over the
whole lifetime of PMD 7.
