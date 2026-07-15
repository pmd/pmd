---
title: Merging pull requests
permalink: pmd_projectdocs_committers_merging_pull_requests.html
last_updated: June 2026 (7.27.0)
author: Andreas Dangel <andreas.dangel@adangel.org>
---

## Example 1: Merging PR #123 into main

1.  Review the pull request

    *   Compilation and checkstyle is verified already by GitHub actions build:
        PRs are automatically checked.
    *   If it is a bug fix, a new unit test, that reproduces the bug, is mandatory.
        Without such a test, we might accidentally reintroduce the bug again.
    *   Make sure the PR contains only the commits related to the bugfix/feature.
    *   Add the appropriate labels on the GitHub issue: If the PR fixes a bug, the label "a:bug"
        should be used.
    *   Add the PR to the appropriate milestone:
        If the PR fixes a bug, make sure, that the bug issue is added to the same milestone.
    *   Make sure the target branch is set to `main`

2.  Checkout the branch locally:

    Use the [GitHub CLI tool](https://cli.github.com/) to check out the branch:

    ```
    git checkout main && git pull origin main                        # make sure, you have the latest code
    gh pr checkout https://github.com/pmd/pmd/pull/123 -b pr-123     # creates a new temporary branch "pr-123"
    ```

3.  Now merge the main branch into the pull request branch:

    ```
    git merge main
    ```

    {%include note.html content="If there are merge conflicts, you'll need to deal with them here." %}

4.  Update the [release notes](https://github.com/pmd/pmd/blob/main/docs/pages/release_notes.md):
    
    *   Are there any API changes, that need to be documented? (Section "API Changes")
    *   Are there any significant changes to existing rules, that should be mentioned?
        (Section "Modified Rules" / "New Rules" / "Removed Rules")
        
        Changes for modified rules are e.g. new properties or changed default values for properties.
        
    *   If the PR fixes a bug, make sure, it is listed under the section "Fixed Issues".
        Also make sure, that the PR description mentions this (e.g. "- Fixes #issue-number") and
        that the PR is linked with the issue. Merging this PR will then automatically close the issue.
    *   Commit these changes with the message:
        
        ```
        git add docs/pages/release_notes.md
        git commit -m "[doc] Update release notes (#123)"
        ```
    
5.  Push the PR back to GitHub

    ```
    git push
    ```

    Since the temporary branch is now not needed anymore, you can delete it:
    `git branch -d pr-123`.

6.  On the PR page, wait until the "Squash and Merge" button turns green, then press it.


## Example 2: Merging PR #124 into a maintenance branch

Every change should go into main first.
But in the rare case a fix needs to be made in an older version as well, we need to backport it.

### Creating a maintenance branch

For older versions, we use maintenance branches, like `pmd/7.26.x`. If there is no maintenance branch for
the specific version, then we'll have to create it first. Let's say, we want a maintenance branch for
PMD version 7.26.0, so that we can create a bugfix release 7.26.1.

1.  Make sure your checkout is in a clean state, that is, the output of

    ```
    git status
    ```
   
    ends with `nothing to commit, working tree clean`.

2.  Create a new branch off of the release tag:

    ```
    git branch pmd/7.26.x pmd_releases/7.26.0 && git checkout pmd/7.26.x
    ```

3.  Now we'll need to adjust the version, since it's currently the same as the release version.
    We'll change the version to the next patch version: "7.26.1-SNAPSHOT".

    ```
    ./mvnw versions:set -DnewVersion=7.26.1-SNAPSHOT
    git commit -a -m "Prepare next version 7.26.1-SNAPSHOT"
    git push
    ```

### Backporting the changes from the PR

1.  Switch to the branch we created above, fetch recent changes:

    ```
    git checkout pmd/7.26.x
    git pull
    ```

2.  Cherrypick the already merged PR from main branch into the maintenance branch:

    ```
    git checkout backport-pr-124-to-7.26.x             # creates a new temporary branch
    git cherrypick commit-hash-you-want-to-packport
    ```

    {%include note.html content="At this point, that you will need to fix conflicts / backport the changes for the older
    PMD version. At least in the release notes (see above for details), maybe also in the code." %}

3.  Run the complete build, then push:

    ```
    ./mvnw clean verify -Pgenerate-rule-docs
    git push
    ```

4.  Create a PR on GitHub:

    * Make sure that you merge *from* backport-pr-124-to-7.26.x *to* pmd/7.26.x.
    * Make sure that you mention #124 in the description.

5.  Merge the PR (ideally by a second person)

    Press the big green button if everything looks as expected.

6.  Repeat for every version the fix needs to be backported to.

### Merging vs. Cherry-Picking

Since we are squashing, we are also using cherry-picking. This means that if the same fix is applied to multiple versions,
it appears as several, seemingly unrelated commits. This is fine, since the commit message of the backported fix will
reference the original PR.
This leads to a linear history for each branch - the maintenance branches and main branch are separate and not "connected".
