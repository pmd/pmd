---
title: Merging pull requests
permalink: pmd_projectdocs_committers_merging_pull_requests.html
last_updated: August 2017
author: Andreas Dangel <andreas.dangel@adangel.org>
---

## Example 1: Merging PR #123 into master

1.  Review the pull request

    *   Compilation and checkstyle is verified already by travis build: PRs are automatically checked.
    *   If it is a bug fix, a new unit test, that reproduces the bug, is mandatory. Without such a test, we might accidentally reintroduce the bug again.
    *   Add the appropriate labels on the github issue: If the PR fixes a bug, the label "a:bug" should be used.
    *   Make sure, the PR is added to the appropriate milestone. If the PR fixes a bug, make sure, that the bug issue is added to the same milestone.

2.  The actual merge commands:

    We assume, that the PR has been created from the master branch. If this is not the case,
    then we'll either need to rebase or ask for rebasing before merging.

    ```
    git checkout master && git pull origin master                    # make sure, you have the latest code
    git fetch origin pull/123/head:pr-123 && git checkout pr-123     # creates a new temporary branch
    ```

3.  Update the [release notes](https://github.com/pmd/pmd/blob/master/docs/pages/release_notes.md):

    *   Are there any API changes, that need to be documented? (Section "API Changes")
    *   Are there any significant changes to existing rules, that should be mentioned?
        (Section "Modified Rules" / "New Rules" / "Removed Rules")
    *   If the PR fixes a bug, make sure, it is listed under the section "Fixed Issues".
    *   In any case, add the PR to the section "External Contributions"
    *   Commit these changes with the message:

            git add docs/pages/release_notes.md
            git commit -m "Update release notes, refs #123"

    {% include note.html content="If the PR fixes a bug, verify, that we have a commit with the message
    \"Fixes #issue-number\". If this doesn't exist, you can add it to the commit message when
    updating the release notes: `Update release notes, refs #123, fixes #issue-number`.
    This will automatically close the github issue." %}

4.  Now merge the pull request into the master branch:

    ```
    git checkout master
    git merge --no-ff pr-123
    ```

    {%include note.html content="If there are merge conflicts, you'll need to deal with them here." %}

5.  Run the complete build: `./mvnw clean verify`

    {% include note.html content="This will execute all the unit tests and the checkstyle tests. It ensures,
    that the complete project can be build and is functioning on top of the current master." %}

6.  If the build was successful, you are ready to push:

    ```
    git push origin master
    ```

    Since the temporary branch is now not needed anymore, you can delete it:
    `git branch -d pr-123`.


## Example 2: Merging PR #124 into a maintenance branch

We ask, to create every pull request against master, to make it easier to contribute.
But if a pull request is intended to fix a bug in an older version of PMD, then we need to backport this pull request.

### Creating a maintenance branch

For older versions, we use maintenance branches, like `pmd/5.8.x`. If there is no maintenance branch for
the specific version, then we'll have to create it first. Let's say, we want a maintenance branch for
PMD version 5.8.0, so that we can create a bugfix release 5.8.1.

1.  We'll simply create a new branch off of the release tag:

    ```
    git branch pmd/5.8.x pmd_releases/5.8.0 && git checkout pmd/5.8.x
    ```

2.  Now we'll need to adjust the version, since it's currently the same as the release version.
    We'll change the version to the next patch version: "5.8.1-SNAPSHOT".

    ```
    ./mvnw versions:set -DnewVersion=5.8.1-SNAPSHOT
    git add pom.xml \*/pom.xml
    git commit -m "prepare next version 5.8.1-SNAPSHOT"
    ```

### Merging the PR

1.  As above: Review the PR

2.  Fetch the PR and rebase it onto the maintenance branch:

    ```
    git fetch origin pull/124/head:pr-124 && git checkout pr-124     # creates a new temporary branch
    git rebase master --onto pmd/5.8.x
    ./mvnw clean verify                                # make sure, everything works after the rebase
    ```

    {%include note.html content="You might need to fix conflicts / backport the commits for the older
    PMD version." %}

3.  Update the release notes. See above for details.

4.  Now merge the pull request into the maintenance branch:

    ```
    git checkout pmd/5.8.x
    git merge --no-ff pr-124
    ```

5.  Just to be sure, run the complete build again: `./mvnw clean verify`.

6.  If the build was successful, you are ready to push:

    ```
    git push origin pmd/5.8.x
    ```

7.  Since we have rebased the pull request, it won't appear as merged on github.
    You need to manually close the pull request. Leave a comment, that it has been
    rebased onto the maintenance branch.

### Merging into master

Now the PR has been merged into the maintenance branch, but it is missing in any later version of PMD.
Therefore, we merge first into the next minor version maintenance branch (if existing):

    git checkout pmd/5.9.x
    git merge pmd/5.8.x

After that, we merge the changes into the master branch:

    git checkout master
    git merge pmd/5.9.x

{%include note.html content="This ensures, that every change on the maintenance branch eventually ends
up in the master branch and therefore in any future version of PMD.<br>
The downside is however, that there are inevitable merge conflicts for the maven `pom.xml` files, since
every branch changed the version number differently.<br>
We could avoid this by merging only the temporary branch \"pr-124\" into each maintenance branch and
eventually into master, with the risk of missing single commits in a maintenance branch, that have been
done outside the temporary branch." %}

### Merging vs. Cherry-Picking

We are not using cherry-picking, so that each fix is represented by a single commit.
Cherry-picking would duplicate the commit and you can't see in the log, on which branches the fix has been
integrated (e.g. gitk and github show the branches, from which the specific commit is reachable).

The downside is a more complex history - the maintenance branches and master branch are "connected" and not separate.
