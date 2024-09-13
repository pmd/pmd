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

#### New Git default branch - "main"

We are joining the Git community and updating "master" to "main". Using the term "master" for the main
development branch can be offensive to some people. Existing versions of Git have been always capable of
working with any branch name and since 2.28.0 (July 2020) the default initial branch is configurable
(`init.defaultBranch`). Since October 2020, the default branch for new repositories on GitHub
is "main". Finally, PMD will also use this new name for the main branch.

Why "main"? PMD uses a very simple branching model - pull requests with feature branches and one main development
branch, from which releases are created. That's why "main" is currently the best fitting name.

More information:
- <https://sfconservancy.org/news/2020/jun/23/gitbranchname/>
- <https://github.blog/changelog/2020-10-01-the-default-branch-for-newly-created-repositories-is-now-main/>

What changes?
- We change the default branch on GitHub, so that pull requests are automatically created against `main` from
  now on.
- If you have already a local clone of PMD's repository, you'll need to rename the old master branch locally:
  `git branch --move master main && git branch --set-upstream-to=origin`. More info:
  <https://git-scm.com/book/en/v2/Git-Branching-Branch-Management#_changing_master>
- Some time after this release, we'll delete the old master branch on GitHub. Then only `main` can be used.
- This change is expanded to the other PMD repositories as well, e.g. pmd-designer and pmd-regression-tester.

### 🐛 Fixed Issues
* apex
  * [#5138](https://github.com/pmd/pmd/issues/5138): \[apex] Various false-negatives since 7.3.0 when using triggers
    (ApexCRUDViolation, CognitiveComplexity, OperationWithLimitsInLoop)
  * [#5163](https://github.com/pmd/pmd/issues/5163): \[apex] Parser error when using toLabel in SOSL query
  * [#5182](https://github.com/pmd/pmd/issues/5182): \[apex] Parser error when using GROUPING in a SOQL query
* core
  * [#5059](https://github.com/pmd/pmd/issues/5059): \[core] xml output doesn't escape CDATA inside its own CDATA
* java
  * [#5190](https://github.com/pmd/pmd/issues/5190): \[java] NPE in type inference

### 🚨 API Changes

### ✨ External Contributions
* [#5208](https://github.com/pmd/pmd/pull/5208): \[doc] Added Codety to "Tools / Integrations" - [Tony](https://github.com/random1223) (@random1223)

{% endtocmaker %}

