---
title: GitHub Actions Workflows
permalink: pmd_devdocs_github_actions_workflows.html
summary: |
  PMD uses GitHub Actions as the CI/CD infrastructure to build and release new versions.
  This page gives an overview of how these workflows work and how to use them.
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: March 2025 (7.12.0)
---

{%include note.html content="This page is work in progress and does not yet describe all workflows."%}

## Pull Request Build

* Builds: <https://github.com/pmd/pmd/actions/workflows/pull-requests.yml>
* Workflow file: <https://github.com/pmd/pmd/blob/main/.github/workflows/pull-requests.yml>

This workflow is triggered whenever a pull request is created or synchronized.

In order to avoid unnecessary builds, we use concurrency control to make sure, we cancel any in-progress jobs for
the current pull request when a new commit has been pushed. This means, only the latest commit is built, which
is enough, since only this will be merged in the end. Only the latest build matters.

The workflow is self-contained, e.g. it doesn't depend on the shell scripts from PMD's build-tools.
It also uses only read permissions and doesn't need access to any secrets. It is safe to run also on
forks.

During the build we create a couple of artifacts, that can be downloaded:

* compile-artifact: contains all the built classes (everything in the `target/` directories). It is built under Linux,
  so unfortunately it can only be used to speed up other Linux based builds, but not Windows or MacOS.
* staging-repository: contains all the artifacts of groupId `net.sourceforge.pmd`. Could be used as a local
  repository to test against this built PMD version without the need to deploy the SNAPSHOTs anywhere. It is
  actually used by the dogfood job.
* dist-artifact: contains the binary distribution files, ready to be downloaded. This can be used to test
  PMD with the changes from the pull request without the need to build it locally. It is actually used by the
  regression tester job to avoid building PMD another time.
* docs-artifact: contains the generated rule documentation.
* pmd-regression-tester: contains the generation regression report, if there were any changes to rules.

In order to have fast feedback of the build results, we run a couple of jobs in parallel and not sequentially.
The jobs are:

* "compile": First a fast compile job to sort out any basic problems at the beginning. If this job fails, nothing
  else is executed. It also populates the build cache (maven dependencies) that is reused for the following jobs.
  The created artifacts are: "compile-artifact", "staging-repository", "dist-artifact".
* After this first job, a bunch of other jobs are run in parallel:
    - "verify": runs a complete `./mvnw verify` with all code checks like checkstyle, japicmp, javadoc, etc.
      but excluding unit tests (these are run in a separate job).
      This job is only run on linux. It reuses the already compiled artifacts from the first "compile" job.
    - "verify-unittests": just runs the unit tests on Linux, Windows and MacOS. Only linux reuses the
      "compile-artifact" from the first job. For Windows/MacOS we can't reuse this due to platform specific line
      endings and timestamp issues.
    - "dogfood": runs maven-pmd-plugin on PMD with the latest changes from this very pull request. It uses the
      "staging-repository" artifact.
    - "documentation": generates the rule documentations and builds PMD's documentation page using jekyll.
      It also executes the verification for wrong rule tags and dead links. It creates the artifact "docs-artifact".
    - "regressiontester": runs the [pmdtester](pmd_devdocs_pmdtester.html) to produce the regression report.
      It reuses the artifact "dist-artifact" so that we don't need to build PMD again. It uses a different build
      cache as the other jobs, as this cache now contains the test projects (like Spring Framework) and their
      dependencies. It produces the artifact "pmd-regression-tester" with the regression report.

## Publish Results from Pull Requests

* Builds: <https://github.com/pmd/pmd/actions/workflows/publish-pull-requests.yml>
* Workflow file: <https://github.com/pmd/pmd/blob/main/.github/workflows/publish-pull-requests.yml>

This workflow runs after "Pull Request Build" is completed. It runs in the context of our own
repository and has write permissions and complete access to the configured secrets.
For security reasons, this workflow won't checkout the pull request code and it won't build anything.

It just uses the artifacts from the pull request build, uploads it as static website and adds
a commit status and check status to the PR and finally adds a PR comment.

Both the "docs-artifact" and the "pmd-regression-tester" artifact are uploaded to an AWS S3 bucket
called **pmd-pull-requests**. This bucket is served via AWS Cloudfront (for having TLS) under
the URL <https://pull-requests.pmd-code.org>. Note, there is no directory listing. The results
of each pull request are uploaded under the folder "pr-{PR_NUMBER}/docs" and "pr-{PR_NUMBER}/regression",
respectively. The data in the S3 bucket is available for 60 days, after that the files are removed
(hopefully).

In order to upload the files to AWS S3, we use the [AWS CLI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/s3/index.html)
tool, that is available on the GitHub provided runners for GitHub Actions. It needs the following **secrets**:

* AWS_S3_PMD_PULL_REQUESTS_ACCESS_KEY_ID
* AWS_S3_PMD_PULL_REQUESTS_SECRET_ACCESS_KEY

These are configured at the organization level of pmd: <https://github.com/organizations/pmd/settings/secrets/actions>.

In order to set the commit status and add a check status, we use [GitHub's CLI](https://cli.github.com/manual/) tool
which is also available on the GitHub provided runners for GitHub Actions. It will use the GitHub token
that the workflow is assigned to automatically and that is available as the secret "GITHUB_TOKEN".
The permissions are controlled in the workflow yaml file itself.
See [Automatic token authentication](https://docs.github.com/en/actions/security-for-github-actions/security-guides/automatic-token-authentication).

In the end, we use the action [sticky-pull-request-comment](https://github.com/marocchino/sticky-pull-request-comment)
to create or update a comment on the pull request which shows the regression tester summary.

This workflow is in that sense optional, as the docs-artifact and pmd-regression-tester artifacts can
be manually downloaded from the "Pull Request Build" workflow run. It merely adds convenience by
giving easy access to a preview of the documentation and to the regression tester results.
