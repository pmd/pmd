---
title: Continuous Integrations plugins
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_ci.html
author: Romain PELISSE <belaran@gmail.com>, Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: December 2024
---

## Introduction

PMD can be integrated through some of the Continuous Integration tools that exist now.
This page lists some plugins to do so. If you think, something is missing here, please
let us know.

{% include note.html content="The tools are listed in alphabetical order without rating." %}

In general, if PMD is integrated via build tools like [Maven](pmd_userdocs_tools_maven.html) or
[Gradle](pmd_userdocs_tools_gradle.html) you can configure your build to fail if there are violations.
This will also mark your CI pipeline as failed. That means, PMD can be used even without a
special plugin for your CI solution.

However, having an extra plugin can provide better developer experience: Some plugins provide
a nice graph over the found violations, so that you can see immediately the status of your build
concerning rule violations. Or it provides access to PMD's report in an easier way by integrating
the report into the CI interface.
Other plugins display rule violations directly inline in your codebase as annotations
or provide automated code review by adding comments to pull/merge requests.

## Atlassian Bamboo

There are two apps for [Atlassian Bamboo](https://www.atlassian.com/software/bamboo) in the marketplace.
These display violations found by PMD and duplications found by CPD directly in job-build view.

* [View PMD/PHPMD (mess detector)](https://marketplace.atlassian.com/apps/1215327/view-pmd-phpmd-mess-detector)
* [View CPD (copy/paste detector)](https://marketplace.atlassian.com/apps/1215890/view-cpd-copy-paste-detector)

## GitHub Action

PMD provides its own GitHub Action, that can be integrated in custom workflows.

It can execute PMD with your own ruleset against your project. It creates a
[SARIF](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html) report which is uploaded as a
build artifact. Furthermore, the build can be failed based on the number of violations.

The action can also be used as a code scanner to create "Code scanning alerts".

* Homepage: <https://github.com/pmd/pmd-github-action>

## GitLab

[GitLab](https://about.gitlab.com/) provides support for various code scanning tools. The results
(e.g. number of violations) are displayed for merge requests, pipeline runs and for the entire project as
code quality.

GitLab requires a specific report format, which PMD doesn't support natively. Therefore, there is a
component required to convert a PMD report into GitLab's [Code Quality report format](https://docs.gitlab.com/ee/ci/testing/code_quality.html#code-quality-report-format).

* Documentation: <https://docs.gitlab.com/ee/ci/testing/code_quality.html>
* [PMD CI/CD component](https://gitlab.com/explore/catalog/eakca1/codequality-os-scanners-integration)

Additionally, GitLab supports various analyzers for [Static Application Security Testing (SAST)](https://docs.gitlab.com/ee/user/application_security/sast/analyzers.html).
Among these analyzers is also [pmd-apex analyzer](https://gitlab.com/gitlab-org/security-products/analyzers/pmd-apex).

## Jenkins Plugin

[Ullrich Hafner](https://github.com/uhafner) developed the
[Warnings Next Generation](https://plugins.jenkins.io/warnings-ng/) plugin for Jenkins. It supports
PMD among many other linting tools.

* Homepage: <https://plugins.jenkins.io/warnings-ng/>
* Source: <https://github.com/jenkinsci/warnings-ng-plugin> and <https://github.com/jenkinsci/analysis-model>



## MegaLinter

[🦙 Mega-Linter](https://oxsecurity.github.io/megalinter/latest/) analyzes 50 languages, 22 formats, 21 tooling
formats, excessive copy-pastes, spelling mistakes and security issues in your repository sources with a
GitHub Action, other CI tools or locally. 

It [natively embeds PMD](https://oxsecurity.github.io/megalinter/latest/descriptors/java_pmd/).


