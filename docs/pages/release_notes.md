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
is "main". Finally, PMD will also use this new name for the main branch in all our own repositories.

Why "main"? PMD uses a very simple branching model - pull requests with feature branches and one main development
branch, from which releases are created. That's why "main" is currently the best fitting name.

More information:
- <https://sfconservancy.org/news/2020/jun/23/gitbranchname/>
- <https://github.blog/changelog/2020-10-01-the-default-branch-for-newly-created-repositories-is-now-main/>

What changes?
- We change the default branch on GitHub, so that pull requests are automatically created against `main` from
  now on.
- If you have already a local clone of PMD's repository, you'll need to rename the old master branch locally:
  ```
  git branch --move master main
  git fetch origin
  git branch --set-upstream-to=origin/main main
  git remote set-head origin --auto
  ```
  
  More info:
  <https://git-scm.com/book/en/v2/Git-Branching-Branch-Management#_changing_master> and
  <https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-branches-in-your-repository/renaming-a-branch#updating-a-local-clone-after-a-branch-name-changes>
- If you created a fork on GitHub, you'll need to change the default branch in your fork to `main` as
  well (Settings > Default Branch).
- Some time after this release, we'll delete the old master branch on GitHub. Then only `main` can be used.
- This change is expanded to the other PMD repositories as well, e.g. pmd-designer and pmd-regression-tester.

### 🐛 Fixed Issues
* apex
  * [#5138](https://github.com/pmd/pmd/issues/5138): \[apex] Various false-negatives since 7.3.0 when using triggers
    (ApexCRUDViolation, CognitiveComplexity, OperationWithLimitsInLoop)
  * [#5163](https://github.com/pmd/pmd/issues/5163): \[apex] Parser error when using toLabel in SOSL query
  * [#5182](https://github.com/pmd/pmd/issues/5182): \[apex] Parser error when using GROUPING in a SOQL query
  * [#5218](https://github.com/pmd/pmd/issues/5218): \[apex] Parser error when using nested subqueries in SOQL
* core
  * [#5059](https://github.com/pmd/pmd/issues/5059): \[core] xml output doesn't escape CDATA inside its own CDATA
  * [#5201](https://github.com/pmd/pmd/issues/5201): \[core] PMD sarif schema file points to nonexistent location
  * [#5222](https://github.com/pmd/pmd/issues/5222): \[core] RuleReference/RuleSetWriter don't handle changed default property values correctly
* java
  * [#5190](https://github.com/pmd/pmd/issues/5190): \[java] NPE in type inference
* java-codestyle
  * [#5046](https://github.com/pmd/pmd/issues/5046): \[java] LocalVariableCouldBeFinal false positive with try/catch
* java-errorprone
  * [#5068](https://github.com/pmd/pmd/issues/5068): \[java] MissingStaticMethodInNonInstantiatableClass: false positive with builder pattern
  * [#5207](https://github.com/pmd/pmd/issues/5207): \[java] CheckSkipResult: false positve for a private method `void skip(int)` in a subclass of FilterInputStream

### 🚨 API Changes

### ✨ External Contributions
* [#5202](https://github.com/pmd/pmd/pull/5202): \[core] Sarif format: refer to schemastore.org - [David Schach](https://github.com/dschach) (@dschach)
* [#5208](https://github.com/pmd/pmd/pull/5208): \[doc] Added Codety to "Tools / Integrations" - [Tony](https://github.com/random1223) (@random1223)
* [#5224](https://github.com/pmd/pmd/pull/5224): \[java] Fix #5068: Class incorrectly identified as non-instantiatable - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)

{% endtocmaker %}

