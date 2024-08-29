---
title: Release process
permalink: pmd_projectdocs_committers_releasing.html
author: Romain Pelisse <rpelisse@users.sourceforge.net>, Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: July 2024 (7.5.0)
---

This page describes the current status of the release process.

Since 6.30.0, the automated release process is using [Github Actions](https://github.com/pmd/pmd/actions).

Since 7.0.0-rc4, the release happens in two phases: First pmd-core with all the languages are released.
This allows to release then pmd-designer or any other project, that just depends on pmd-core and the
languages. And in the second phase, pmd-cli and pmd-dist are released. These include e.g. pmd-designer.

While the release is mostly automated, there are still a few steps, that need manual examination.

## Overview

This page gives an overview which tasks are automated to do a full release of PMD. This knowledge is
required in order to verify that the release was successful or in case the automated process fails for
some reason. Then individual steps need to be executed manually. Because the build is reproducible, these
steps can be repeated if the same tag is used.

There is one special case in this project: As outlined above, the release of PMD consists of two phases or parts:
1. All modules except pmd-cli and pmd-dist are released. That means, pmd-core and all the language modules
   are released. This is, so that these libs can be used by pmd-designer to create a new release.
2. pmd-cli and pmd-dist are released after that. Both depend on pmd-designer, and this two-step release
   process is used for now to break the cycling release dependency.

The three main steps are:

* Preparations (which sets the versions and creates the tags) - use `do-release.sh` for that
* The actual release (which is automated) - GitHub Actions will build the tags when they have been pushed.
* Prepare the next release (make sure the current main branch is ready for further development)

## Preparations

This is the first step. It is always manual and is executed locally. It creates in the end the tag from which
the release is created.

Make sure code is up-to-date and everything is committed and pushed with git:

    $ ./mvnw clean
    $ git pull
    $ git status

As a help for the preparation task, the script `do-release.sh` guides you through the preparation tasks
and the whole release process. The script requires a specific source code folder and additional checkouts locally,
e.g. it requires that the repo `pmd.github.io` is checked out aside the main pmd repo:

* <https://github.com/pmd/pmd> ➡️ `/home/joe/source/pmd`
* <https://github.com/pmd/pmd.github.io> ➡️ `/home/joe/source/pmd.github.io`

The script `do-release.sh` is called in the directory `/home/joe/source/pmd` and searches for `../pmd.github.io`.

Also make sure, that the repo "pmd.github.io" is locally up-to-date and has no local changes.

### The Release Notes and docs

Before the release, you need to verify the release notes: Does it contain all the relevant changes for the
release? Is it formatted properly? Are there any typos? Does it render properly?

As the release notes are part of the source code, it is not that simple to change it afterward. While the source
code for a tag cannot be changed anymore, the published release notes on the GitHub Releases pages or the
news posts can be changed afterward (although that's an entirely manual process).

You can find the release notes here: `docs/pages/release_notes.md`.

The date (`date +%Y-%m-%d`) and the version (remove the SNAPSHOT) must be updated in `docs/_config.yml`,  e.g.
in order to release version "7.2.0", the configuration should look like this:

```yaml
pmd:
    version: 7.2.0
    previous_version: 7.1.0
    date: 2024-05-31
    release_type: minor
```

The release type could be one of "bugfix" (e.g. 7.2.x), "minor" (7.x.0), or "major" (x.0.0).

The release notes usually mention any new rules that have been added since the last release.

Add the new rules as comments to the quickstart rulesets:
* `pmd-apex/src/main/resources/rulesets/apex/quickstart.xml`
* `pmd-java/src/main/resources/rulesets/java/quickstart.xml`

The designer lives at [pmd/pmd-designer](https://github.com/pmd/pmd-designer).
Update property `pmd-designer.version` in **pom.xml** to reference the new version, that will be released
shortly. Note: This new version does at the moment not exist. That means, that a full build of the sources
will currently fail. That's why the first phase of the release will build only pmd-core and languages but
not pmd-cli and pmd-dist.

In case, there is no need for a new pmd-designer version, we could stick to the latest already available version.
Then we can skip the release of pmd-designer and immediately start the second phase of the release.

Starting with PMD 7.5.0 we use Dependabot to update dependencies. Dependabot will create pull requests
labeled with `dependencies`. When we merge such a pull request, we should assign it to the correct
milestone. It is important, that the due date of the milestone is set correctly, otherwise the query won't find
the milestone number.
Then we can query which PRs have been merged and generate a section for the release notes:

```shell
NEW_VERSION=7.2.0
MILESTONE_JSON=$(curl -s "https://api.github.com/repos/pmd/pmd/milestones?state=all&direction=desc&per_page=5"|jq ".[] | select(.title == \"$NEW_VERSION\")")
MILESTONE=$(echo "$MILESTONE_JSON" | jq .number)

# determine dependency updates
DEPENDENCIES_JSON=$(curl -s "https://api.github.com/repos/pmd/pmd/issues?labels=dependencies&state=closed&direction=asc&per_page=50&page=1&milestone=${MILESTONE}")
DEPENDENCIES_COUNT=$(echo "$DEPENDENCIES_JSON" | jq length)
if [ $DEPENDENCIES_COUNT -gt 0 ]; then
  echo "### 📦 Dependency updates"
  echo "$DEPENDENCIES_JSON" | jq --raw-output '.[] | "* [#\(.number)](https://github.com/pmd/pmd/issues/\(.number)): \(.title)"'
else
  echo "### 📦 Dependency updates"
  echo "No dependency updates"
fi
```
This section needs to be added to the release notes at the end.

Starting with PMD 6.23.0 we'll provide small statistics for every release. This needs to be added
to the release notes as the last section (after "Dependency updates"). To count the closed issues and pull requests, the milestone
on GitHub with the title of the new release is searched. It is important, that the due date of the milestone
is correctly set, as the returned milestones in the API call are sorted by due date.
Make sure, there is such a milestone on <https://github.com/pmd/pmd/milestones>. The following snippet will
create the numbers, that can be attached to the release notes as a last section. Note: It uses part of the
above code snippet (e.g. NEW_VERSION, MILESTONE, DEPENDENCIES_COUNT):

```shell
LAST_VERSION=7.1.0
NEW_VERSION_COMMITISH=HEAD
STATS_CLOSED_ISSUES=$(echo "$MILESTONE_JSON" | jq .closed_issues)

echo "### Stats"
echo "* $(git log pmd_releases/${LAST_VERSION}..${NEW_VERSION_COMMITISH} --oneline --no-merges |wc -l) commits"
echo "* $(($STATS_CLOSED_ISSUES - $DEPENDENCIES_COUNT)) closed tickets & PRs"
echo "* Days since last release: $(( ( $(date +%s) - $(git log --max-count=1 --format="%at" pmd_releases/${LAST_VERSION}) ) / 86400))"
```

Note: both shell snippets are also integrated into `do-release.sh`.

Check in all (version) changes to branch master or any other branch, from which the release takes place:

    $ git commit -a -m "Prepare pmd release <version>"
    $ git push


### The Homepage

The GitHub repo `pmd.github.io` hosts the homepage for [https://pmd.github.io](https://pmd.github.io).
All the following tasks are to be done in this repo.

The new version needs to be entered into `_config.yml`, e.g.:

```yaml
pmd:
  latestVersion: 7.2.0
  latestVersionDate: 31-May-2024
```

Also move the previous version down into the "downloads" section. We usually keep only the last 3 versions
in this list, so remove the oldest version.

Then create a new page for the new release, e.g. `_posts/2024-05-31-PMD-7.2.0.md` and copy
the release notes into this page. This will appear under the news section.

Note: The release notes typically contain some Jekyll macros for linking to the rule pages. These macros won't
work in a plain markdown version. Therefore, you need to render the release notes first:

```shell
# install bundles needed for rendering release notes
bundle config set --local path vendor/bundle
bundle config set --local with release_notes_preprocessing
bundle install

RELEASE_NOTES_POST="_posts/$(date -u +%Y-%m-%d)-PMD-${RELEASE_VERSION}.md"
echo "Generating ../pmd.github.io/${RELEASE_NOTES_POST}..."
NEW_RELEASE_NOTES=$(bundle exec docs/render_release_notes.rb docs/pages/release_notes.md | tail -n +6)
cat > "../pmd.github.io/${RELEASE_NOTES_POST}" <<EOF
```

Check in all (version, blog post) changes to branch master:

    $ git commit -a -m "Prepare pmd release <version>"
    $ git push


## The actual release

The actual release is done by changing the versions, creating a tag and pushing this tag. Previously this was done
by calling _maven-release-plugin_, but these steps are done without the plugin to have more control. And since we
might reference a not yet released pmd-designer version, the test-build would fail.

We first change the version of PMD and all modules by basically removing the "-SNAPSHOT" suffix, building the changed
project locally with tests (and with skipping pmd-cli and pmd-dist) in order to be sure, everything is in working
order. Then the version changes are committed and a new release tag is created. Then, the versions are changed to
the next snapshot. As last step, everything is pushed.

`RELEASE_VERSION` is the version of the release. It is reused for the tag. `DEVELOPMENT_VERSION` is the
next snapshot version after the release. Skipping the builds of pmd-cli and pmd-dist is done by setting
the property `skip-cli-dist`.

```shell
RELEASE_VERSION=7.2.0
DEVELOPMENT_VERSION=7.3.0-SNAPSHOT
# Change version in the POMs to ${RELEASE_VERSION} and update build timestamp
./mvnw --quiet versions:set -DnewVersion="${RELEASE_VERSION}" -DgenerateBackupPoms=false -DupdateBuildOutputTimestampPolicy=always
# Transform the SCM information in the POM
sed -i "s|<tag>.\+</tag>|<tag>pmd_releases/${RELEASE_VERSION}</tag>|" pom.xml
# Run the project tests against the changed POMs to confirm everything is in running order (skipping cli and dist)
./mvnw clean verify -Dskip-cli-dist -Pgenerate-rule-docs
# Commit and create tag
git commit -a -m "[release] prepare release pmd_releases/${RELEASE_VERSION}"
git tag -m "[release] copy for tag pmd_releases/${RELEASE_VERSION}" "pmd_releases/${RELEASE_VERSION}"
# Update POMs to set the new development version ${DEVELOPMENT_VERSION}
./mvnw --quiet versions:set -DnewVersion="${DEVELOPMENT_VERSION}" -DgenerateBackupPoms=false -DupdateBuildOutputTimestampPolicy=never
sed -i "s|<tag>.\+</tag>|<tag>HEAD</tag>|" pom.xml
git commit -a -m "[release] prepare for next development iteration"
# Push branch and tag pmd_releases/${RELEASE_VERSION}
git push origin "${CURRENT_BRANCH}"
git push origin tag "pmd_releases/${RELEASE_VERSION}"
```

Once we have pushed the tag, GitHub Actions take over and build a new version from this tag. Since
it is a tag build and a release version (version without SNAPSHOT), the build script will do a couple of additional stuff.
This is all automated in `.ci/build.sh`.

Note: The property "skip-cli-dist" is activated, so this release command doesn't include pmd-cli and pmd-dist.
They will be released separately after pmd-designer is released. Since pmd-dist is not included in this first
step, no binaries are created yet.

Here is, what happens:

*   Deploy and release the build to maven central, so that it can be downloaded from
    <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd/>. This is done automatically, if
    the build doesn't fail for any reason. Note, that unit tests are not executed anymore, since they have been
    run already locally before pushing the tag.
    The plugin [nexus-staging-maven-plugin](https://github.com/sonatype/nexus-maven-plugins/tree/master/staging/maven-plugin)
    is used to upload and publish the artifacts to maven central.
*   Create a draft release on GitHub and upload the release notes from `docs/pages/release_notes.md`.
    Note: During the process, the release is a draft mode and not visible yet.
    At the end of the process, the release will be published.
*   Render the documentation in `docs/` with `bundle exec jekyll build` and create a zip file from it.
*   Upload the doc zip file to the current (draft) GitHub Release under <https://github.com/pmd/pmd/releases> and
    to <https://sourceforge.net/projects/pmd/files/pmd/>.
*   Upload the documentation to <https://docs.pmd-code.org>, e.g. <https://docs.pmd-code.org/pmd-doc-7.2.0/> and
    create a symlink, so that <https://docs.pmd-code.org/latest/> points to the new version.
*   Remove the old snapshot documentation, e.g. so that <https://docs.pmd-code.org/pmd-doc-7.2.0-SNAPSHOT/> is gone.
    Also create a symlink from pmd-doc-7.2.0-SNAPSHOT to pmd-doc-7.2.0, so that old references still work, e.g.
    <https://docs.pmd-code.org/pmd-doc-7.2.0-SNAPSHOT/> points to the released version.
*   Deploy javadoc to "https://docs.pmd-code.org/apidocs/*/RELEASE_VERSION/", e.g.
    <https://docs.pmd-code.org/apidocs/pmd-core/7.2.0/>. This is done for all modules.
*   Remove old javadoc for the SNAPSHOT version, e.g. delete <https://docs.pmd-code.org/apidocs/pmd-core/7.2.0-SNAPSHOT/>.
*   Create a draft news post on <https://sourceforge.net/p/pmd/news/> for the new release. This contains the
    rendered release notes.
*   Copy the documentation to sourceforge's web space, so that it is available as
    <https://pmd.sourceforge.io/pmd-7.2.0/>. All previously copied versions are listed
    under <https://pmd.sourceforge.io/archive.phtml>.

The release on GitHub Actions currently takes about 30-45 minutes. Once this is done, you
can proceed with releasing pmd designer, see <https://github.com/pmd/pmd-designer/blob/master/releasing.md>.
Make sure to release the version, you have used earlier for the property `pmd-designer.version`.

Once the pmd-designer release is done, you can proceed with part 2. This is simply triggering manually
a build on GitHub Actions: <https://github.com/pmd/pmd/actions/workflows/build.yml> from the same tag again, but
with the parameter "build_cli_dist_only" set to "true". With this parameter, the script `.ci/build.sh` will
perform the following steps:

*   Build only modules pmd-cli and pmd-dist (via maven parameter `-pl pmd-cli,pmd-dist`).
*   Upload the new binaries to the existing draft GitHub Release under <https://github.com/pmd/pmd/releases>.
*   Upload the new binaries additionally to sourceforge, so that they can be downloaded from
    <https://sourceforge.net/projects/pmd/files/pmd/>.
*   After all this is done, the release on GitHub (<https://github.com/pmd/pmd/releases>) is published
    and the news post on sourceforge <https://sourceforge.net/p/pmd/news/> is published as well.
*   The new binary at <https://sourceforge.net/projects/pmd/files/pmd/> is
    selected as the new default for PMD.
*   As a last step, a new baseline for the [regression tester](https://github.com/pmd/pmd-regression-tester)
    is created and uploaded to <https://pmd-code.org/pmd-regression-tester>.

Once this second GitHub Action run is done, you can spread additional news:

* Write an email to the mailing list

    To: PMD Developers List <pmd-devel@lists.sourceforge.net>
    Subject: [ANNOUNCE] PMD <version> released


    *   Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F<version>
    *   Documentation: https://docs.pmd-code.org/pmd-doc-<version>/

    And Copy-Paste the release notes

* Tweet about the new release

Tweet on <https://twitter.com/pmd_analyzer>, eg.:

    PMD 6.34.0 released: https://github.com/pmd/pmd/releases/tag/pmd_releases/6.34.0 #PMD

* Post the same twitter message into the gitter chat at <https://matrix.to/#/#pmd_pmd:gitter.im>

### Checklist

| Task              | Description                                                                          | URL                                                             | ☐ / ✔                   |
|-------------------|--------------------------------------------------------------------------------------|-----------------------------------------------------------------|-------------------------|
| maven central     | The new version of all artifacts are available in maven central                      | <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd/> | <input type="checkbox"> |
| github releases   | A new release with 3 assets (bin, src, doc) is created                               | <https://github.com/pmd/pmd/releases>                           | <input type="checkbox"> |
| sourceforge files | The 3 assets (bin, src, doc) are uploaded, the new version is pre-selected as latest | <https://sourceforge.net/projects/pmd/files/pmd/>               | <input type="checkbox"> |
| homepage          | Main landing page points to new version, doc for new version is available            | <https://pmd.github.io>                                         | <input type="checkbox"> |
| homepage2         | New blogpost for the new release is posted                                           | <https://pmd.github.io/#news>                                   | <input type="checkbox"> |
| docs              | New docs are uploaded                                                                | <https://docs.pmd-code.org/latest/>                             | <input type="checkbox"> |
| docs2             | New version in the docs is listed under "Version specific documentation"             | <https://docs.pmd-code.org/>                                    | <input type="checkbox"> |
| docs-archive      | New docs are also on archive site                                                    | <https://pmd.sourceforge.io/archive.phtml>                      | <input type="checkbox"> |
| javadoc           | New javadocs are uploaded                                                            | <https://docs.pmd-code.org/apidocs/>                            | <input type="checkbox"> |
| news              | New blogpost on sourceforge is posted                                                | <https://sourceforge.net/p/pmd/news/>                           | <input type="checkbox"> |
| regression-tester | New release baseline is uploaded                                                     | <https://pmd-code.org/pmd-regression-tester>                    | <input type="checkbox"> |
| mailing list      | announcement on mailing list is sent                                                 | <https://sourceforge.net/p/pmd/mailman/pmd-devel/>              | <input type="checkbox"> |
| twitter           | tweet about the new release                                                          | <https://twitter.com/pmd_analyzer>                              | <input type="checkbox"> |
| gitter            | message about the new release                                                        | <https://matrix.to/#/#pmd_pmd:gitter.im>                        | <input type="checkbox"> |

## Prepare the next release

There are a couple of manual steps needed to prepare the current main branch for further development.

*   Move any open issues to the next milestone, close the current milestone
    on <https://github.com/pmd/pmd/milestones> and create a new one for the next
    version (if one doesn't exist already).
*   Update version in **docs/_config.yml**. Note - the next version needs to have a SNAPSHOT
    in it otherwise the javadoc links won't work during development.
    
    ```yaml
    pmd:
        version: 7.3.0-SNAPSHOT
        previous_version: 7.2.0
        date: 2024-??-??
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

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker %}

### 🚀 New and noteworthy

### 🐛 Fixed Issues

### 🚨 API Changes

### ✨ External Contributions

{% endtocmaker %}

```
{%endraw%}


Finally, commit and push the changes:

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
automatically the latest release on <https://docs.pmd-code.org/latest/> and on sourceforge.


### (Optional) Create a new maintenance branch

At some point, it might be time for a new maintenance branch. Such a branch is usually created from
the tag. Here are the steps:

*   Create a new branch: `git branch pmd/7.1.x pmd_releases/7.1.0`
*   Update the version in both the new branch, e.g. `mvn versions:set -DnewVersion=7.1.1-SNAPSHOT`.
*   Update the release notes on both the new branch
