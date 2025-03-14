---
title: Infrastructure
permalink: pmd_projectdocs_committers_infrastructure.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: February 2025 (7.11.0)
---

This page describes, which infrastructure and services is used by the pmd project.

## GitHub

The main repository is hosted on <https://github.com/pmd>. We own the organization "pmd".

*   source code in git repositories
*   releases
*   issue tracker
*   discussions
*   pull requests
*   [GitHub Actions for CI](pmd_devdocs_github_actions_workflows.html)

Also the [main landing page](pmd_projectdocs_committers_main_landing_page.html) (<https://pmd.github.io>)
is hosted using GitHub pages.

## SourceForge

Before moving to GitHub, SourceForge was the main place. It is still there: <https://sourceforge.net/projects/pmd/>.

Nowadays, it is used for:

*   hosting an archive of binaries: <https://sourceforge.net/projects/pmd/files/>
*   hosting an archive of documentation: <https://pmd.sourceforge.io/archive.html>
*   mailing lists:
    *   <pmd-commits@lists.sourceforge.net>
    *   <pmd-devel@lists.sourceforge.net>
*   discussion forum

It also contains the old issue tracker.

## Domain, mail and homepage

We are using a webhosting package by [Netcup](https://www.netcup.de/).

The following domains are registered for us:

*   pmd-code.org
*   pmd-code.io
*   pmd-code.com

The webhosting package provides these services:

*   email service (including mailbox via IMAP)
*   web pages

The homepage <https://pmd-code.org> redirects to <https://pmd.github.io>.

Some docs are hosted at <https://docs.pmd-code.org/>.


## Other services

*   Deployment to Maven Central via <https://oss.sonatype.org/> and <https://central.sonatype.org/register/central-portal/>
    Uploading requires credentials (CI_DEPLOY_USERNAME, CI_DEPLOY_PASSWORD) and permissions.
*   Twitter: <https://twitter.com/pmd_analyzer>
*   Rubygems for pmd-regression-tester: <https://rubygems.org/gems/pmdtester>
    Uploading requires credentials (GEM_HOST_API_KEY)
*   SonarCloud: <https://sonarcloud.io/dashboard?id=net.sourceforge.pmd%3Apmd>
    We use the "CI-based Analysis method" with GitHub Actions.
    Documentation: <https://sonarcloud.io/documentation>
    Uploading new analysis results requires credentials (SONAR_TOKEN).
    Login is via GitHub.
*   Coveralls: <https://coveralls.io/github/pmd/pmd>
    We don't use the [Coveralls GitHub Actions](https://github.com/marketplace/actions/coveralls-github-action) but the [coveralls-maven-plugin](https://github.com/trautonen/coveralls-maven-plugin).
    Documentation: <https://docs.coveralls.io/>
    Uploading new results requires credentials (COVERALLS_REPO_TOKEN).
    Login is via GitHub.

