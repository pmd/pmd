---
title: Building PMD General Info
tags: [devdocs]
permalink: pmd_devdocs_building_general.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: January 2025 (7.10.0)
---

# Before Development

1. Ensure that [Git](https://git-scm.com/) and Java JDK >= 11 are installed. You can get a OpenJDK distribution
   from e.g. [Adoptium](https://adoptium.net/).
2. Fork the [PMD repository](https://github.com/pmd/pmd) on GitHub as explained in [Fork a repository](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo).
3. Clone your forked repository to your computer:
   ```shell
   git clone git@github.com:your_user_name/pmd.git --depth=10 --no-tags
   ```
4. Clone additionally the [build-tools repository](https://github.com/pmd/build-tools). It contains some settings, we'll later use for configuring IDE:
   ```shell
   git clone git@github.com:pmd/build-tools.git
   ```
5. To make sure your Maven environment is correctly setup, we'll build pmd once:

   ```shell
   cd pmd
   ./mvnw clean verify -DskipTests
   ```

   This will help with Maven IDE integration. It may take some time, because it will download all dependencies,
   so go brew some coffee to get ready for the steps to come.

{%capture notetext%}
This only clones the last ten commits and not the whole PMD repository. This makes it faster, as much less data needs
to be downloaded. However, the history is incomplete. If you want to browse/annotate the source with the complete
commit history locally, either clone the repo without the "depth" option or convert it to a "full" clone using
`git fetch --unshallow`.
{%endcapture%}
{%include note.html content=notetext %}

# Development

* Use a IDE, see the other guides
* Contributing via GitHub and Pull Requests:
* setup upstream repo
* create a dev branch
* commit dev branch
* push dev branch
* send a pull request
* keep your fork up to date
