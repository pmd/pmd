---
title: Release process
permalink: pmd_projectdocs_committers_releasing.html
author: Romain Pelisse <rpelisse@users.sourceforge.net>, Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: April 2021
---

This page describes the current status of the release process.

Since versions 5.4.5 / 5.5.4 there is an automated release process using [travis-ci](https://travis-ci.com)
in place. Since 6.30.0, the automated release process is using [Github Actions](https://github.com/pmd/pmd/actions).

However, there are still a few steps, that need manual examination.

## Overview

This page gives an overview which tasks are automated to do a full release of PMD. This knowledge is
required in order to verify that the release was successful or in case the automated process fails for
some reason. Then individual steps need to be executed manually. Because the build is reproducible, these
steps can be repeated again if the same tag is used.

The three main steps are:

* Preparations (which creates the tag) - use `do-release.sh` for that
* The actual release (which is automated)
* Prepare the next release (make sure the current main branch is ready for further development)

## Preparations

This is the first step. It is always manual and is executed locally. It creates in the end the tag from which
the release is created.

Make sure code is up to date and everything is committed and pushed with git:

    $ ./mvnw clean
    $ git pull
    $ git status

As a help for the preparation task, the script `do-release.sh` guides you through the preparation tasks
and the whole release process. The script requires a specific source code folder and additional checkouts locally,
e.g. it requires that the repo `pmd.github.io` is checked out aside the main pmd repo:

* <https://github.com/pmd/pmd> ➡️ `/home/joe/source/pmd`
* <https://github.com/pmd/pmd.github.io> ➡️ `/home/joe/source/pmd.github.io`

The script `do-release.sh` is called in the directory `/home/joe/source/pmd` and searches for `../pmd.github.io`.

Also make sure, that the repo "pmd.github.io" is locally up to date and has no local changes.

### The Release Notes and docs

Before the release, you need to verify the release notes: Does it contain all the relevant changes for the
release? Is it formatted properly? Are there any typos? Does it render properly?

As the release notes are part of the source code, it is not simple to change it afterwards. While the source
code for a tag cannot be changed anymore, the published release notes on the github releases pages or the
new posts can be changed afterwards (although that's an entirely manual process).

You can find the release notes here: `docs/pages/release_notes.md`.

The date (`date +%d-%B-%Y`) and the version (remove the SNAPSHOT) must be updated in `docs/_config.yml`,  e.g.
in order to release version "6.34.0", the configuration should look like this:

```yaml
pmd:
    version: 6.34.0
    previous_version: 6.33.0
    date: 24-April-2021
    release_type: minor
```

The release type could be one of "bugfix" (e.g. 6.34.x), "minor" (6.x.0), or "major" (x.0.0).

The release notes usual mention any new rules that have been added since the last release.
Please double check the file `pmd-core/src/main/resources/rulesets/releases/<version>.xml`, so
that all new rules are listed.

Add the new rules as comments to the quickstart rulesets:
* `pmd-apex/src/main/resources/rulesets/apex/quickstart.xml`
* `pmd-java/src/main/resources/rulesets/java/quickstart.xml`

We maintain a documentation for the [next major release](pmd_next_major_development.html). Copy the API
changes from the current release notes to this document: `docs/pages/next_major_development.md`.

The designer lives at [pmd/pmd-designer](https://github.com/pmd/pmd-designer).
Update property `pmd-designer.version` in **pom.xml** to reference the latest pmd-designer release.
See <https://search.maven.org/search?q=g:net.sourceforge.pmd%20AND%20a:pmd-ui&core=gav> for the available releases.

Starting with PMD 6.23.0 we'll provide small statistics for every release. This needs to be added
to the release notes as the last section. To count the closed issues and pull requests, the milestone
on github with the title of the new release is searched. Make sure, there is a milestone
on <https://github.com/pmd/pmd/milestones>. The following snippet will
create the numbers, that can be attached to the release notes as a last section:

```shell
LAST_VERSION=6.33.0
NEW_VERSION=6.34.0
NEW_VERSION_COMMITISH=HEAD

echo "### Stats"
echo "* $(git log pmd_releases/${LAST_VERSION}..${NEW_VERSION_COMMITISH} --oneline --no-merges |wc -l) commits"
echo "* $(curl -s "https://api.github.com/repos/pmd/pmd/milestones?state=all&direction=desc&per_page=5"|jq ".[] | select(.title == \"$NEW_VERSION\") | .closed_issues") closed tickets & PRs"
echo "* Days since last release: $(( ( $(date +%s) - $(git log --max-count=1 --format="%at" pmd_releases/${LAST_VERSION}) ) / 86400))"
```

Note: this part is also integrated into `do-release.sh`.

Check in all (version) changes to branch master or any other branch, from which the release takes place:

    $ git commit -a -m "Prepare pmd release <version>"
    $ git push


### The Homepage

The github repo `pmd.github.io` hosts the homepage for [https://pmd.github.io](https://pmd.github.io).
All the following tasks are to be done in this repo.

The new version needs to be entered into `_config.yml`, e.g.:

```yaml
pmd:
  latestVersion: 6.34.0
  latestVersionDate: 24-April-2021
```

Also move the previous version down into the "downloads" section. We usually keep only the last 3 versions
in this list, so remove the oldest version.

Then create a new page for the new release, e.g. `_posts/2021-04-24-PMD-6.34.0.md` and copy
the release notes into this page. This will appear under the news section.

Check in all (version) changes to branch master:

    $ git commit -a -m "Prepare pmd release <version>"
    $ git push


## The actual release

The actual release starts with one last local command: calling **maven-release-plugin**.

This plugin changes the version by basically removing the "-SNAPSHOT" suffix, builds the changed project
locally, commits the version change, creates
a new tag from this commit, changes the version of the project to the next snapshot, commits this change
and pushes everything.

`RELEASE_VERSION` is the version of the release. It is reused for the tag. `DEVELOPMENT_VERSION` is the
next snapshot version after the release.

```shell
RELEASE_VERSION=6.34.0
DEVELOPMENT_VERSION=6.35.0-SNAPSHOT
./mvnw -B release:clean release:prepare \
    -Dtag=pmd_releases/${RELEASE_VERSION} \
    -DreleaseVersion=${RELEASE_VERSION} \
    -DdevelopmentVersion=${DEVELOPMENT_VERSION}
```

Once the maven plugin has pushed the tag, github actions will start and build a new version from this tag. Since
it is a tag build and a released version build, the build script will do a couple of additional stuff.
This is all automated in `.ci/build.sh`.

Here is, what happens:

*   Deploy and release the build to maven central, so that it can be downloaded from
    <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd/>. This is done automatically, if
    all unit tests pass and the build doesn't fail for any other reason.
    The plugin [nexus-staging-maven-plugin](https://github.com/sonatype/nexus-maven-plugins/tree/master/staging/maven-plugin) is used for that.
*   Upload the new binaries to github releases under <https://github.com/pmd/pmd/releases>. It also uploads
    the release notes from `docs/pages/release_notes.md`.
    Note: The during the process, the release is a draft mode and not visible yet.
    At the end of the process, the release will be published.
*   Upload the new binaries additionally to sourceforge, so that they can be downloaded from
    <https://sourceforge.net/projects/pmd/files/pmd/>, including the release notes.
*   Render the documentation in `docs/` with `bundle exec jekyll build` and create a zip file from it.
*   Upload the doc zip file to the current github release under <https://github.com/pmd/pmd/releases> and
    to <https://sourceforge.net/projects/pmd/files/pmd/>.
*   Upload the documentation to <https://docs.pmd-code.org>, e.g. <https://docs.pmd-code.org/pmd-doc-6.34.0/> and
    create a symlink, so that <https://docs.pmd-code.org/latest/> points to the new version.
*   Remove the old snapshot documentation, e.g. so that <https://docs.pmd-code.org/pmd-doc-6.34.0-SNAPSHOT/> is gone.
    Also create a symlink from pmd-doc-6.34.0-SNAPSHOT to pmd-doc-6.34.0, so that old references still work, e.g.
    <https://docs.pmd-code.org/pmd-doc-6.34.0-SNAPSHOT/> points to the released version.
*   Deploy javadoc to "https://docs.pmd-code.org/apidocs/*/RELEASE_VERSION/", e.g.
    <https://docs.pmd-code.org/apidocs/pmd-core/6.34.0/>. This is done for all modules.
*   Remove old javadoc for the SNAPSHOT version, e.g. delete <https://docs.pmd-code.org/apidocs/pmd-core/6.34.0-SNAPSHOT/>.
*   Create a draft news post on <https://sourceforge.net/p/pmd/news/> for the new release. This contains the
    rendered release notes.
*   Add the documentation of the new release to a subfolder on <https://pmd.github.io>, also make
    this folder available as `latest`, so that <https://pmd.github.io/latest/> shows the new
    version and <https://pmd.github.io/pmd-6.34.0/> is the URL for the specific release.
*   Also copy the documentation to sourceforge's web space, so that it is available as
    <https://pmd.sourceforge.io/pmd-6.34.0/>. All previously copied version are listed
    under <https://pmd.sourceforge.io/archive.phtml>.
*   After all this is done, the release on github (<https://github.com/pmd/pmd/releases>) is published
    and the news post on sourceforge (https://sourceforge.net/p/pmd/news/> is publishes as well.
*   The new binary at <https://sourceforge.net/projects/pmd/files/pmd/> is
    selected as the new default for PMD.
*   As a last step, a new baseline for the [regression tester](https://github.com/pmd/pmd-regression-tester)
    is created and uploaded to <https://pmd-code.org/pmd-regression-tester>.

The release on github actions currently takes about 30-45 minutes. Once this is done, you can spread additional
news:

* Write an email to the mailing list

    To: PMD Developers List <pmd-devel@lists.sourceforge.net>
    Subject: [ANNOUNCE] PMD <version> released


    *   Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F<version>
    *   Documentation: https://pmd.github.io/pmd-<version>/

    And Copy-Paste the release notes

* Tweet about the new release

Tweet on <https://twitter.com/pmd_analyzer>, eg.:

    PMD 6.34.0 released: https://github.com/pmd/pmd/releases/tag/pmd_releases/6.34.0 #PMD


### Checklist

| Task | Description | URL | ☐ / ✔ |
|------|-------------|-----|-------|
| maven central | The new version of all artifacts are available in maven central | <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd/> | <input type="checkbox"> |
| github releases | A new release with 3 assets (bin, src, doc) is created | <https://github.com/pmd/pmd/releases> | <input type="checkbox"> |
| sourceforge files | The 3 assets (bin, src, doc) are uploaded, the new version is pre-selected as latest | <https://sourceforge.net/projects/pmd/files/pmd/> | <input type="checkbox"> |
| homepage | Main landing page points to new version, doc for new version is available | <https://pmd.github.io> | <input type="checkbox"> |
| homepage2 | New blogpost for the new release is posted | <https://pmd.github.io/#news> | <input type="checkbox"> |
| docs | New docs are uploaded | <https://docs.pmd-code.org/latest/> | <input type="checkbox"> |
| docs-archive | New docs are also on archive site | <https://pmd.sourceforge.io/archive.phtml> | <input type="checkbox"> |
| javadoc | New javadocs are uploaded | <https://docs.pmd-code.org/apidocs/> | <input type="checkbox"> |
| news | New blogpost on sourceforge is posted | <https://sourceforge.net/p/pmd/news/> | <input type="checkbox"> |
| regression-tester | New release baseline is uploaded | <https://pmd-code.org/pmd-regression-tester> | <input type="checkbox"> |
| mailing list | announcement on mailing list is sent | <https://sourceforge.net/p/pmd/mailman/pmd-devel/> | <input type="checkbox"> |
| twitter | tweet about the new release | <https://twitter.com/pmd_analyzer> | <input type="checkbox"> |

## Prepare the next release

There are a couple of manual steps needed to prepare the current main branch for further development.

*   Move any open issues to the next milestone, close the current milestone
    on <https://github.com/pmd/pmd/milestones> and create a new one for the next
    version (if one doesn't exist already).
*   Update version in **docs/_config.yml**. Note - the next version needs to have a SNAPSHOT
    in it otherwise the javadoc links won't work during development.
    
    ```yaml
    pmd:
        version: 6.35.0-SNAPSHOT
        previous_version: 6.34.0
        date: ??-??-2021
        release_type: minor
    ```

*   Prepare a new empty release notes. Note, this is done by `do-release.sh` already.
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


Finally commit and push the changes:

    $ git commit -m "Prepare next development version"
    $ git push origin master


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

The maintenance or bugfix branch could also be created later when needed from the actual tag. Then only the version on
the maintenance branch needs to be set.
