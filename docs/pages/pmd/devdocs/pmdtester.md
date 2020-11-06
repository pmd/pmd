---
title: Pmdtester
tags: [devdocs]
permalink: pmd_devdocs_pmdtester.html
author: Binguo Bao <djydewang@gmail.com>
---

## Introduction
Pmdtester is a regression testing tool that ensures no new problems and unexpected behaviors will be introduced to PMD after fixing an issue. 
It can also be used to verify, that new rules work as expected. It has been integrated into travis CI and is actually used automatically for PRs.
Regression difference reports are commented back to the PR for the reviewer's information e.g. https://github.com/pmd/pmd/pull/1265#issuecomment-408945709

## Run pmdtester locally
**Install pmdtester**  

`gem install pmdtester --pre`  

**Verifying your local changes and generate a diff-report locally**  

`pmdtester -r YOUR_LOCAL_PMD_GIT_REPO_ROOT_DIR -b master -p YOUR_DEVELOPMENT_BRANCH`  

The regression difference report is placed in the `YOUR_WORKING_DIR/target/reports/diff` directory.

For more documentation on pmdtester, see [README.rdoc](https://github.com/pmd/pmd-regression-tester/blob/master/README.rdoc)
