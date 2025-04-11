---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### Docker images

PMD is now providing official docker images at <https://hub.docker.com/r/pmdcode/pmd> and
<https://github.com/pmd/docker/pkgs/container/pmd>.

You can now analyze your code with PMD by using docker like so: 

```
docker run --rm --tty -v $PWD:/src pmdcode/pmd:latest check -d . -R rulesets/java/quickstart.xml`
```

More information is available at <https://github.com/pmd/docker>.

### 🐛 Fixed Issues
* core
  * [#5448](https://github.com/pmd/pmd/issues/5448): Maintain a public PMD docker image
  * [#5623](https://github.com/pmd/pmd/issues/5623): \[dist] Make pmd launch script compatible with /bin/sh
* java
  * [#5645](https://github.com/pmd/pmd/issues/5645): \[java] Parse error on switch with yield

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

