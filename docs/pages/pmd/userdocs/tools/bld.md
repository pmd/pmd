---
title: bld PMD Extension
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_bld.html
last_updated: September 2023
---

> [bld](https://rife2.com/bld) is a new build system that allows you to write your build logic in pure Java.

It has a [PMD Extension](https://github.com/rife2/bld-pmd) for it.

To install, please refer to the [extensions documentation](https://github.com/rife2/bld/wiki/Extensions).

To check all source code using the [java quickstart rule](pmd_rules_java.html), add the following to your build file

```java
@BuildCommand
public void pmd() throws Exception {
    new PmdOperation()
        .fromProject(this)
        .execute();
}
```

```shell
./bld pmd test
```

* Homepage: <https://rife2.com/bld>
* Documentation: <https://github.com/rife2/bld/wiki>
* PMD Extension: <https://github.com/rife2/bld-pmd>
