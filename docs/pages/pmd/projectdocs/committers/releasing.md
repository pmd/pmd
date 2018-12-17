---
title: Releasing
permalink: pmd_projectdocs_committers_releasing.html
author: Romain Pelisse <rpelisse@users.sourceforge.net>, Andreas Dangel <adangel@users.sourceforge.net>
---

This page describes the current status of the release process.

Since versions 5.4.5 / 5.5.4 there is an automated release process using [travis-ci](https://travis-ci.org)
in place. However, there are still a few steps, that need manual examination.

Note: You can find a small shell script in the root of the repo: `do-release.sh`. This script guides you
through the release process.


## Preparations

Make sure code is up to date and everything is committed and pushed with git:

    $ ./mvnw clean
    $ git pull
    $ git status



### The Release Notes and docs

You can find the release notes here: `docs/pages/release_notes.md`.

The date and the version must be updated in `docs/_config.yml`,  e.g.

```
pmd:
    version: 6.0.0
    date: 2017-12-15
```


The release notes usual mention any new rules that have been added since the last release.
Please double check the file `pmd-core/src/main/resources/rulesets/releases/<version>.xml`, so
that all new rules are listed.

We maintain a documentation for the [next major release](pmd_next_major_development.html). Copy the API
changes from the current release notes to this document: `docs/pages/next_major_development.md`.

Check in all (version) changes to branch master or any other branch, from which the release takes place:

    $ git commit -a -m "Prepare pmd release <version>"
    $ git push


### The Homepage

The github repo `pmd.github.io` hosts the homepage for [https://pmd.github.io](https://pmd.github.io).

The new version needs to be entered into `_config.yml`, e.g.:

```
pmd:
  latestVersion: 6.0.0
  latestVersionDate: 15th December 2017
```

Also move the previous version down into the "downloads" section.

Then create a new page for the new release, e.g. `_posts/2017-12-15-PMD-6.0.0.md` and copy
the release notes into this page. This will appear under the news section.

Check in all (version) changes to branch master:

    $ git commit -a -m "Prepare pmd release <version>"
    $ git push



## Creating the release

The release is created using the **maven-release-plugin**. This plugin changes the version by basically
removing the "-SNAPSHOT" suffix, builds the changed project locally, commits the version change, creates
a new tag from this commit, changes the version of the project to the next snapshot, commits this change
and pushes everything.

`RELEASE_VERSION` is the version of the release. It is reused for the tag. `DEVELOPMENT_VERSION` is the
next snapshot version after the release.

    mvn -B release:clean release:prepare \
        -Dtag=pmd_releases/${RELEASE_VERSION} \
        -DreleaseVersion=${RELEASE_VERSION} \
        -DdevelopmentVersion=${DEVELOPMENT_VERSION}


Once the maven plugin has pushed the tag, travis-ci will start and build a new version from this tag. Since
it is a tag build and a released version build, travis-ci will do a couple of additional stuff:

*   Deploy and release the build to maven central, so that it can be downloaded from
    <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd/>. This is done automatically, if
    all unit tests pass on travis-ci. The plugin [nexus-staging-maven-plugin](https://github.com/sonatype/nexus-maven-plugins/tree/master/staging/maven-plugin) is used for that.
*   Upload the new binaries to github releases under <https://github.com/pmd/pmd/releases>. It also uploads
    the release notes.
*   Upload the new binaries additionally to sourceforge, so that they can be downloaded from
    <https://sourceforge.net/projects/pmd/files/pmd/>, including the release notes. The new binaries are
    selected as the new default downloads for PMD.
*   Add the documentation of the new release to a subfolder on <https://pmd.github.io>, also make
    this folder available as `latest`.


## After the release

The release on travis currently takes about 30 minutes. Once this is done, you can spread the news:

### Submit a news on SF

Submit news to SF on the [PMD Project News](https://sourceforge.net/p/pmd/news/) page. You can use
the following template:

    PMD <version> released

    *   Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F<version>
    *   Documentation: https://pmd.github.io/pmd-<version>/

    And Copy-Paste the release notes

### Write an email to the mailing list

    To: PMD Developers List <pmd-devel@lists.sourceforge.net>
    Subject: [ANNOUNCE] PMD <version> released


    *   Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F<version>
    *   Documentation: https://pmd.github.io/pmd-<version>/

    And Copy-Paste the release notes


## Prepare the next release

### Prepare the new release notes

*   Update version in **docs/_config.yml**
*   Move version/release info from **docs/pages/release_notes.md** to **docs/pages/release_notes_old.md**.
*   Update version/release info in **docs/pages/release_notes.md**. Use the following template:

{%raw%}
```
---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker %}

### New and noteworthy

### Fixed Issues

### API Changes

### External Contributions

{% endtocmaker %}

```
{%endraw%}


Commit and push

    $ git commit -m "Prepare next development version"
    $ git push origin master


### Close / Create new milestones

Manage the milestones under <https://github.com/pmd/pmd/milestones>.
Maybe there are some milestones on sourceforge, too: <https://sourceforge.net/p/pmd/bugs/milestones>.



## Branches

### Merging

If the release was done on a maintenance branch, such as `pmd/5.4.x`, then this branch should be
merged into the next "higher" branches, such as `pmd/5.5.x` and `master`.

This ensures, that all fixes done on the maintenance branch, finally end up in the other branches.
In theory, the fixes should already be there, but you never now.


### Multiple releases

If releases from multiple branches are being done, the order matters. You should start from the "oldest" branch,
e.g. `pmd/5.4.x`, release from there. Then merge (see above) into the next branch, e.g. `pmd/5.5.x` and release
from there. Then merge into the `master` branch and release from there. This way, the last release done, becomes
automatically the latest release on <https://pmd.github.io/latest/> and on sourceforge.


### (Optional) Create a new release branch

At some point, it might be time for a new maintenance branch. Such a branch is usually created from
the `master` branch. Here are the steps:

*   Create a new branch: `git branch pmd/5.6.x master`
*   Update the version in both the new branch and master, e.g. `mvn versions:set -DnewVersion=5.6.1-SNAPSHOT`
    and `mvn versions:set -DnewVersion=5.7.0-SNAPSHOT`.
*   Update the release notes on both the new branch and master
