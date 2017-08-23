---
title: Pull-Request Merge
permalink: pmd_devdocs_pull_requests.html
last_updated: August 2017
author: Andreas Dangel <andreas.dangel@adangel.org>
---

## Contributing via pull requests

**TODO:** basically what we have in <https://github.com/pmd/pmd/blob/master/CONTRIBUTING.md>.


## Merging pull requests

### Example 1: Merging PR #123 into master

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

    {% include note.html content="You can get the pull command from github. Use the \"command line instructions\" link." %}

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


### Example 2: Merging PR #124 into a maintenance branch

We ask, to create every pull request against master, to make it easier to contribute.
But if a pull request is intended to fix a bug in an older version of PMD, then we need to backport this pull request.

For older versions, we have maintenance branches, like `pmd/5.8.x`.

**TODO:**

*   create maintenance branch, if needed
*   create temp branch, pull in pr, git rebase master --onto pmd/5.8.x
*   continue with step 3 above (update release notes)
*   merge the maintenance branch into the next higher branch and eventually into master

Note: not using cherry-picking, so that each fix is represented by a single commit. Cherry-picking would
duplicate the commit and you can't see in the log, on which branches the fix has been made
(e.g. gitk and github show the branches, on which the specific commit is contained).
The downside is a more complex history - the maintenance branches and master branch are "connected" and not separate.

Note: merging maintenance branches after a release will produce merge conflicts in `pom.xml` files.
