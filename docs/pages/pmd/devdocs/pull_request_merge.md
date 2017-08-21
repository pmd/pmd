---
title: Pull-Request Merge
permalink: pmd_devdocs_pull_request_merge.html
last_updated: August 2017
author: Andreas Dangel <andreas.dangel@adangel.org>
---

## Example 1: Merging PR #123 into master

1.  Review

    *   Compilation and checkstyle is checked already by travis build: PRs are automatically verified.
    *   If it is a bug fix, a new unit test, that reproduces the bug, is mandatory.
    *   Add the appropriate labels on the github issue: If the PR fixes a bug, the label "a:bug" should be used.
    *   Make sure, the PR is added to the appropriate milestone.

2.  The actual merge commands:

    We assume, that the PR has been created from the master branch. If this is not the case,
    then we'll either need to rebase or ask for rebasing before merging.

    ```
    git checkout master && git pull origin master
    git checkout -b pr-123
    git pull https://github.com/otheruser/pmd.git branchname
    ```

    Note: You can get the pull command from github. Use the "command line instructions" link.

3.  Update the [release notes](https://github.com/pmd/pmd/blob/master/docs/pages/release_notes.md):

    *   Are there any API changes, that need to be documented? (Section "API Changes")
    *   Are there any significant changes to existing rules, that should be mentioned?
        (Section "Modified Rules" / "New Rules" / "Removed Rules")
    *   If the PR fixes a bug, make sure, it is listed under the section "Fixed Issues".
    *   In any case, add the PR to the section "External Contributions"
    *   Commit this changes with the message:

            Update release notes, refs #123

    Note: If the PR fixes a bug, verify, that we have a commit with the message
    "Fixes #issue-number". If this doesn't exist, you can add it to the commit message when
    updating the release notes: `Update release notes, refs #123, fixes #issue-number`.

4.  Just to be sure, run complete build: `./mvnw clean verify`

5.  Go back to master and merge this temporary branch and push:

    ```
    git checkout master
    git merge --no-ff pr-123
    git push origin master
    ```

    Since the temporary branch is now not needed anymore, you can delete it:
    `git branch -d pr-123`.


## Example 2: Merging PR #124 into a maintenance branch

**TODO**
