---
title: Infrastructure
permalink: pmd_projectdocs_committers_infrastructure.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: April 2021
---

This page describes, which infrastructure and services is used by the pmd project.

## github

The main repository is hosted on <https://github.com/pmd>. We own the organization "pmd".

*   source code in git repositories
*   releases
*   issue tracker
*   discussions
*   pull requests
*   github actions for CI

Also the [main landing page](pmd_projectdocs_committers_main_landing_page.html) (<https://pmd.github.io>)
is hosted using github pages.

## sourceforge

Before moving to github, sourceforge was the main place. It is still there: <https://sourceforge.net/projects/pmd/>.

Nowadays it is used for:

*   hosting archive of binaries: https://sourceforge.net/projects/pmd/files/
*   hosting an archive of documentation: https://pmd.sourceforge.io/archive.html
*   mailing lists:
    *   <pmd-commits@lists.sourceforge.net>
    *   <pmd-devel@lists.sourceforge.net>
*   discussion forum

It also contains the old issue tracker.

## domain, email and homepage

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


## other services

*   Deployment to maven central via <https://oss.sonatype.org/> and <https://issues.sonatype.org/browse/OSSRH-2295>
    Uploading requires credentials (CI_DEPLOY_USERNAME, CI_DEPLOY_PASSWORD) and permissions.
*   Hosting eclipse plugin update site via <https://bintray.com/>
    Uploading requires credentials (BINTRAY_USER, BINTRAY_APIKEY)
    Note: This service is retired and the update site is now hosted via Github Pages (<https://github.com/pmd/pmd-eclipse-plugin-p2-site/>).
*   Hosting result reports from pmd-regression-tester via <https://chunk.io/>
    Uploading requires credentials (PMD_CI_CHUNK_TOKEN)
*   Twitter: <https://twitter.com/pmd_analyzer>
*   Rubygems for pmd-regression-tester: <https://rubygems.org/gems/pmdtester>
    Uploading requires credentials (GEM_HOST_API_KEY)
*   sonarcloud: <https://sonarcloud.io/dashboard?id=net.sourceforge.pmd%3Apmd>
    We use the "CI-based Analysis method" with GitHub Actions.
    Documentation: <https://sonarcloud.io/documentation>
    Uploading new analysis results requires credentials (SONAR_TOKEN).
    Login is via github.
*   coveralls: <https://coveralls.io/github/pmd/pmd>
    We don't use the [Coveralls Github Actions](https://github.com/marketplace/actions/coveralls-github-action) but the [coveralls-maven-plugin](https://github.com/trautonen/coveralls-maven-plugin).
    Documentation: <https://docs.coveralls.io/>
    Uploading new results requires credentials (COVERALLS_REPO_TOKEN).
    Login is via github.
*   travis ci was used before github actions: <https://travis-ci.org/github/pmd>

