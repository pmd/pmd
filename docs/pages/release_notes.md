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
  * [#5228](https://github.com/pmd/pmd/issues/5228): \[apex] Parser error when using convertCurrency() in SOQL
* core
  * [#5059](https://github.com/pmd/pmd/issues/5059): \[core] xml output doesn't escape CDATA inside its own CDATA
  * [#5201](https://github.com/pmd/pmd/issues/5201): \[core] PMD sarif schema file points to nonexistent location
  * [#5222](https://github.com/pmd/pmd/issues/5222): \[core] RuleReference/RuleSetWriter don't handle changed default property values correctly
  * [#5229](https://github.com/pmd/pmd/issues/5229): \[doc] CLI flag `--show-suppressed` needs to mention xml, html, summaryhtml
* java
  * [#5190](https://github.com/pmd/pmd/issues/5190): \[java] NPE in type inference
* java-codestyle
  * [#5046](https://github.com/pmd/pmd/issues/5046): \[java] LocalVariableCouldBeFinal false positive with try/catch
* java-errorprone
  * [#5068](https://github.com/pmd/pmd/issues/5068): \[java] MissingStaticMethodInNonInstantiatableClass: false positive with builder pattern
  * [#5207](https://github.com/pmd/pmd/issues/5207): \[java] CheckSkipResult: false positve for a private method `void skip(int)` in a subclass of FilterInputStream

### 🚨 API Changes

No changes.

### ✨ Merged pull requests
* [#5186](https://github.com/pmd/pmd/pull/5186): \[java] Cleanup things about implicit classes - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5188](https://github.com/pmd/pmd/pull/5188): \[apex] Use new apex-parser 4.2.0 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5191](https://github.com/pmd/pmd/pull/5191): \[java] Fix #5046 - FPs in LocalVariableCouldBeFinal - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5192](https://github.com/pmd/pmd/pull/5192): \[java] Fix #5190 - NPE in type inference caused by null type - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5195](https://github.com/pmd/pmd/pull/5195): \[apex] Fix various FNs when using triggers - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5202](https://github.com/pmd/pmd/pull/5202): \[core] Sarif format: refer to schemastore.org - [David Schach](https://github.com/dschach) (@dschach)
* [#5208](https://github.com/pmd/pmd/pull/5208): \[doc] Added Codety to "Tools / Integrations" - [Tony](https://github.com/random1223) (@random1223)
* [#5210](https://github.com/pmd/pmd/pull/5210): \[core] Fix PMD's XMLRenderer to escape CDATA - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5211](https://github.com/pmd/pmd/pull/5211): Change branch master to main - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5212](https://github.com/pmd/pmd/pull/5212): \[java] Adjust signature matching in CheckSkipResultRule - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5223](https://github.com/pmd/pmd/pull/5223): \[core] Fix RuleReference / RuleSetWriter handling of properties - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5224](https://github.com/pmd/pmd/pull/5224): \[java] Fix #5068: Class incorrectly identified as non-instantiatable - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5230](https://github.com/pmd/pmd/pull/5230): \[doc] Documentation update for --show-suppressed flag - [David Schach](https://github.com/dschach) (@dschach)
* [#5237](https://github.com/pmd/pmd/pull/5237): \[apex] Support convertCurrency() in SOQL/SOSL - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦 Dependency updates
* [#5185](https://github.com/pmd/pmd/issues/5185): Bump checkstyle from 10.14.0 to 10.18.1
* [#5187](https://github.com/pmd/pmd/issues/5187): Bump org.apache.maven.plugins:maven-install-plugin from 3.1.1 to 3.1.3
* [#5199](https://github.com/pmd/pmd/issues/5199): Bump org.apache.maven.plugins:maven-deploy-plugin from 3.1.1 to 3.1.3
* [#5216](https://github.com/pmd/pmd/issues/5216): Bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.20.0 to 0.23.0
* [#5226](https://github.com/pmd/pmd/issues/5226): Bump rouge from 4.3.0 to 4.4.0 in the all-gems group across 1 directory
* [#5227](https://github.com/pmd/pmd/issues/5227): Bump com.google.code.gson:gson from 2.10.1 to 2.11.0
* [#5232](https://github.com/pmd/pmd/issues/5232): Bump com.google.protobuf:protobuf-java from 3.25.3 to 3.25.5
* [#5233](https://github.com/pmd/pmd/issues/5233): Bump webrick from 1.8.1 to 1.8.2 in /docs

### 📈 Stats
* 60 commits
* 27 closed tickets & PRs
* Days since last release: 27

{% endtocmaker %}
