---
title: GitHub Actions Workflows
permalink: pmd_devdocs_github_actions_workflows.html
summary: |
  PMD uses GitHub Actions as the CI/CD infrastructure to build and release new versions.
  This page gives an overview of how these workflows work and how to use them.
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: May 2025 (7.14.0)
---

{%include note.html content="This page is work in progress and does not yet describe all workflows."%}

## Build, Build Pull Request, Build Snapshot

"Build" itself is a [reuseable workflow](https://docs.github.com/en/actions/sharing-automations/reusing-workflows),
that is called by "Build Pull Request" and "Build Snapshot".

* Workflow files:
  * <https://github.com/pmd/pmd/blob/main/.github/workflows/build.yml>
  * <https://github.com/pmd/pmd/blob/main/.github/workflows/build-pr.yml>
  * <https://github.com/pmd/pmd/blob/main/.github/workflows/build-snapshot.yml>
* Builds:
  * Build Pull Request: <https://github.com/pmd/pmd/actions/workflows/build-pr.yml>
  * Build Snapshot: <https://github.com/pmd/pmd/actions/workflows/build-snapshot.yml>

All these workflows execute exactly the same steps. But only the triggering event is different.
It is designed to run on the main repository in PMD's GitHub organization as well as for forks, as it does
not require any secrets.

"Build Pull Request" is triggered, whenever a pull request is created or synchronized.

"Build Snapshot" is triggered, whenever new commits are pushed to a branch (including the default branch and
including forks).

In order to avoid unnecessary builds, we use concurrency control to make sure, we cancel any in-progress jobs for
the current branch or pull request when a new commit has been pushed. This means, only the latest commit is built,
which is enough, since only this will be released or merged in the end. Only the latest build matters.

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
  regression tester job to avoid building PMD another time. It also includes the SBOM files (in json and xml format).
* docs-artifact: contains the generated PMD documentation including rule descriptions.
* javadocs-artifact: contains the javadoc jars of all PMD modules generated during build.
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
      Since it runs javadoc, it creates the javadocs-artifact.
    - "verify-unittests": just runs the unit tests on Linux, Windows and MacOS. Only linux reuses the
      "compile-artifact" from the first job. For Windows/MacOS we can't reuse this due to platform specific line
      endings and timestamp issues.
    - "dogfood": runs maven-pmd-plugin on PMD with the latest changes from this very pull request. It uses the
      "staging-repository" artifact.
    - "documentation": generates the rule documentations and builds PMD's documentation page using jekyll.
      It also executes the verification for wrong rule tags and dead links. Additional it contains the release
      notes in markdown format, to be used for release publishing. It creates the artifact "docs-artifact".
    - "regressiontester": runs the [pmdtester](pmd_devdocs_pmdtester.html) to produce the regression report.
      It reuses the artifact "dist-artifact" so that we don't need to build PMD again. It uses a different build
      cache as the other jobs, as this cache now contains the test projects (like Spring Framework) and their
      dependencies. It produces the artifact "pmd-regression-tester" with the regression report.

## Publish Results from Pull Requests

* Builds: <https://github.com/pmd/pmd/actions/workflows/publish-pull-requests.yml>
* Workflow file: <https://github.com/pmd/pmd/blob/main/.github/workflows/publish-pull-requests.yml>

This workflow runs after "Build Pull Request", when it is completed. It runs in the context of our own
repository and has write permissions and complete access to the configured secrets.
For security reasons, this workflow won't check out the pull request code and won't build anything.

It just uses the artifacts from the pull request build, uploads it as static website and adds
a commit status and check status to the PR and finally adds a PR comment.

Both the "docs-artifact" and the "pmd-regression-tester" artifact are uploaded to an AWS S3 bucket
called **pmd-pull-requests**. This bucket is served via AWS Cloudfront (for having TLS) under
the URL <https://pull-requests.pmd-code.org>. Note, there is no directory listing. The results
of each pull request are uploaded under the folders "pr-{PR_NUMBER}/{PR_SHA}/docs" and "pr-{PR_NUMBER}/{PR_SHA}/regression",
respectively. The data in the S3 bucket is available for 60 days, after that the files are removed
automatically (hopefully). Since the head SHA of the PR is included in the URL, an updated PR will upload
new versions of the artifacts. Otherwise, Cloudfront's cache might prevent us from seeing the
updated artifacts.

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

In the end, this workflow adds additional links to a pull request page. For the comment, GitHub seems
to automatically add "rel=nofollow" to the links in the text. This is also applied for the check status
pages. However, the links in the commit status are plain links. This might lead to unnecessary
crawling by search engines. To avoid this, the following robots.txt is used
at <https://pull-requests.pmd-code.org/robots.txt> to disallow any (search engine) bot:

```
User-agent: *
Disallow: /
```

The reasons, why we don't want to have the pages there indexed: They are short-lived and only
temporary. These temporary created documentation pages should not end up in any search result.
This also helps to avoid unnecessary traffic and load for both the hosting side and the
crawling side.

## Publish Snapshot

* Builds: <https://github.com/pmd/pmd/actions/workflows/publish-snapshot.yml>
* Workflow file: <https://github.com/pmd/pmd/blob/main/.github/workflows/publish-snapshot.yml>

This runs after "Build Snapshot" of a push on the `main` branch is finished.
It runs in the context of our own repository and has access to all secrets. In order
to have a nicer display in GitHub actions, we leverage "environments", which also
contain secrets.

There is a first job "check-version" that just determines the version of PMD we are building. This is to ensure,
we are actually building a SNAPSHOT version. Then a couple of other jobs are being executed in parallel:

* deploy-to-maven-central: Rebuilds PMD from branch main and deploys the snapshot artifacts to
  <https://oss.sonatype.org/content/repositories/snapshots/net/sourceforge/pmd/>. Rebuilding is necessary in
  order to produce all necessary artifacts (sources, javadoc) and also gpg-sign the artifacts. This
  is not available from the build artifacts of the "Build" workflow.
  * Environment: maven-central
  * Secrets: OSSRH_TOKEN, OSSRH_USERNAME
* deploy-to-sourceforge-files: Downloads the "dist-artifact" and "docs-artifact" from the "Build" workflow,
  gpg-signs the files and uploads them to <https://sourceforge.net/projects/pmd/files/pmd/>.
  * Environment: sourceforge
  * Secrets: PMD_WEB_SOURCEFORGE_NET_DEPLOY_KEY
  * Vars: PMD_WEB_SOURCEFORGE_NET_KNOWN_HOSTS
* deploy-to-sourceforge-io: Uploads the documentation page to be hosted at
  <https://pmd.sourceforge.io/snapshot>.
  * Environment: sourceforge
  * Secrets: PMD_WEB_SOURCEFORGE_NET_DEPLOY_KEY
  * Vars: PMD_WEB_SOURCEFORGE_NET_KNOWN_HOSTS
* deploy-to-pmd-code-doc: Uploads the documentation page to be hosted at
  <https://docs.pmd-code.org/snapshot/>.
  * Environment: pmd-code
  * Secrets: PMD_CODE_ORG_DEPLOY_KEY
  * Vars: PMD_CODE_ORG_KNOWN_HOSTS
* deploy-to-pmd-code-javadoc: Uploads the javadoc of PMD's modules to be hosted at
  <https://docs.pmd-code.org/apidocs/>.
  * Environment: pmd-code
  * Secrets: PMD_CODE_ORG_DEPLOY_KEY
  * Vars: PMD_CODE_ORG_KNOWN_HOSTS
* deploy-to-github-pages: Updates the branch "gh-pages" wit the new documentation page
  and pushes a new commit onto that branch. This will trigger a new github pages deployment,
  so that the new documentation page is available at
  <https://pmd.github.io/pmd/>.
  * Environment: github-pages
  * Secrets: no additional secrets
* create-regression-tester-baseline: Creates a new baseline to be used by the pull request builds
  for regression testing. The baseline is uploaded to <https://pmd-code.org/pmd-regression-tester/main-baseline.zip>.
  * Environment: pmd-code
  * Secrets: PMD_CODE_ORG_DEPLOY_KEY
  * Vars: PMD_CODE_ORG_KNOWN_HOSTS
* run-sonar: Executes [sonar-scanner-plugin](https://github.com/SonarSource/sonar-scanner-maven) and
  uploads the results to sonarcloud at <https://sonarcloud.io/dashboard?id=net.sourceforge.pmd%3Apmd>.
  * Environment: sonarcloud
  * Secrets: SONAR_TOKEN
* run-coveralls: Executes [coveralls-maven-plugin](https://github.com/hazendaz/coveralls-maven-plugin) and
  uploads the results to coveralls at <https://coveralls.io/github/pmd/pmd>.
  * Environment: coveralls
  * Secrets: COVERALLS_REPO_TOKEN

## Secrets and Variables
The "Build" workflow doesn't need any secrets or additional permissions, it just builds and creates artifacts.
This is necessary, so that this workflow can also run on forks, so that contributors can develop in their
own fork and have their build validated already. All branches (not only main) are built. The same workflow is
also used for pull request builds.

On the main PMD repository (<https://github.com/pmd/pmd>) additional workflows are triggered after "Build".
These additional workflows run with privileged mode, so that they have access to all secrets and can
elevate permissions as needed.

Secrets and variables are organized in a hierarchy, beginning at the organization level (those secrets are
available in all repositories), repository level and environment level (environments can be created at the
repository level).

At time of this writing (2025-05-10), the following secrets and variables are configured:

### Organization
See <https://github.com/organizations/pmd/settings/secrets/actions>

* `PMD_ACTIONS_HELPER_ID` and `PMD_ACTIONS_HELPER_PRIVATE_KEY`: These are the app id and private key for our
  custom GitHub App [PMD Actions Helper](https://github.com/organizations/pmd/settings/apps/pmd-actions-helper).
  This is a private app defined at our organization and can only be installed within our organization. With these two
  secrets and the action [create-github-app-token](https://github.com/actions/create-github-app-token) we can
  create a temporary github token, that has more permissions to access other repositories.
  This is used to trigger the workflow in pmd/docker from pmd/pmd to create and upload a new docker image and
  also in "publish-snapshot" to push to repository pmd/pmd-eclipse-plugin-p2-site during the build
  of pmd/pmd-eclipse-plugin.  
  A new private key can be generated through GitHub organization settings.

* `PMD_CI_GPG_PASSPHRASE` and `PMD_CI_GPG_PRIVATE_KEY`: That's the secret GPG key used to sign our releases,
  see [Signed Releases](pmd_userdocs_signed_releases.html). The key is exported in armored format
  (beginning with "-----BEGIN PGP PRIVATE KEY BLOCK-----"). The key is imported during the build either with
  the option "gpg-private-key" of [setup-java](https://github.com/actions/setup-java) action or manually
  via a small shell script ("gpg --import ...").  
  The release signing key is created in such a way, that we use the primary key only for certifying our own
  subkeys (capability C) and we have a separate subkey that is used only for signing releases
  (capability S). In case the signing subkey gets compromised, we can add a new subkey, but keep the
  primary key. The idea is, that only the private key of the subkey is exported and put into the
  secret variable `PMD_CI_GPG_PRIVATE_KEY`. This can be achieved through
  `gpg --armor --export-secret-subkeys 2EFA55D0785C31F956F2F87EA0B5CA1A4E086838 | wl-copy`.  
  More information about creating and renewing this key, see below [Release Signing Keys](#release-signing-keys).

### Repository pmd/pmd
See <https://github.com/pmd/pmd/settings/secrets/actions>

* `AWS_S3_PMD_PULL_REQUESTS_ACCESS_KEY_ID` and `AWS_S3_PMD_PULL_REQUESTS_SECRET_ACCESS_KEY`: Used by
  "Publish Results From Pull Request" to upload the regression report and documentation to the AWS S3
  bucket "pmd-pull-requests": http://pmd-pull-requests.s3-website.eu-central-1.amazonaws.com/.
  This access key corresponds to the user "arn:aws:iam::624352026855:user/pmd-pull-requests", which is
  granted write access to this bucket via the following permission policy:

  ```json
  {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": [
                "s3:Get*",
                "s3:List*",
                "s3:PutObject",
                "s3:DeleteObject"
            ],
            "Resource": [
                "arn:aws:s3:::pmd-pull-requests/*",
                "arn:aws:s3:::pmd-pull-requests"
            ]
        }
    ]
  }
  ```
  New access key/users need to be created via AWS.

### Repository pmd/docker
See <https://github.com/pmd/docker/settings/secrets/actions>

* `DOCKER_USERNAME` and `DOCKER_PASSWORD`: Used by repo pmd/docker to push new images to
  <https://hub.docker.com/u/pmdcode>. This is actually a personal access token of user [andreasdangel](https://hub.docker.com/u/andreasdangel),
  who is the only member of the community organization [pmdcode](https://hub.docker.com/u/pmdcode).

### Repository pmd/pmd-regression-tester - Environment "rubygems"
See <https://github.com/pmd/pmd-regression-tester/settings/secrets/actions>

* `GEM_HOST_API_KEY`: Used to publish a new version into <https://rubygems.org/gems/pmdtester>.
  The key can be generated at <https://rubygems.org/profile/api_keys>.

### Repository pmd/pmd - Environment "maven-central"

* `OSSRH_USERNAME` and `OSSRH_TOKEN`: Used to deploy artifacts to maven central via OSSRH to our namespace
  [net.sourceforge.pmd](https://repo.maven.apache.org/maven2/net/sourceforge/pmd).  
  Login on <https://oss.sonatype.org>, go to your user profile, select "User Token" and "Access User Token".
  You'll see the tokens to be used for username and password.  
  Note: This will soon be migrated to use <https://central.sonatype.com>.

### Repository pmd/pmd - Environment "coveralls"
* `COVERALLS_REPO_TOKEN`: Used to upload coverage results to <https://coveralls.io/github/pmd/pmd>.
  When you log in via GitHub on coveralls.io, the token is displayed on that page.

### Repository pmd/pmd - Environment "sonarcloud"
* `SONAR_TOKEN`: Used to upload new results to <https://sonarcloud.io/dashboard?id=net.sourceforge.pmd%3Apmd>.
  The token can be configured here: <https://sonarcloud.io/account/security/>. Login via GitHub.

### Repository pmd/pmd - Environment "sourceforge"
* `PMD_WEB_SOURCEFORGE_NET_DEPLOY_KEY`: The private ssh key used to access web.sourceforge.net to
  upload files and web pages.  
  It is created with `ssh-keygen -t ed25519 -C "ssh key for pmd. used for github actions push to web.sourceforge.net" -f web.sourceforge.net_deploy_key`.  
  You need to configure the public key part here: <https://sourceforge.net/auth/shell_services>. The user is your
  sourceforge user id.  
  The key should begin with "-----BEGIN OPENSSH PRIVATE KEY-----".

* `PMD_SF_BEARER_TOKEN`: This is needed to access sourceforge API (https://sourceforge.net/p/forge/documentation/Allura%20API/)
  to create new blog entries. The token is created at <https://sourceforge.net/auth/oauth/>.

* `PMD_SF_APIKEY`: This is needed to select the latest release on sourceforge files, see
  <https://sourceforge.net/p/forge/documentation/Using%20the%20Release%20API/>. The key is created at
  <https://sourceforge.net/auth/preferences/> under "Releases API Key".


This environment also has a variable, which is the known_hosts content as `PMD_WEB_SOURCEFORGE_NET_KNOWN_HOSTS`:

```
#
# web.sourceforge.net (https://sourceforge.net/p/forge/documentation/SSH%20Key%20Fingerprints/)
#
# run locally:
# ssh-keyscan web.sourceforge.net | tee -a sf_known_hosts
#
# verify fingerprints:
# ssh-keygen -F web.sourceforge.net -l -f sf_known_hosts
# # Host web.sourceforge.net found: line 1 
# web.sourceforge.net RSA SHA256:xB2rnn0NUjZ/E0IXQp4gyPqc7U7gjcw7G26RhkDyk90 
# # Host web.sourceforge.net found: line 2 
# web.sourceforge.net ECDSA SHA256:QAAxYkf0iI/tc9oGa0xSsVOAzJBZstcO8HqGKfjpxcY 
# # Host web.sourceforge.net found: line 3 
# web.sourceforge.net ED25519 SHA256:209BDmH3jsRyO9UeGPPgLWPSegKmYCBIya0nR/AWWCY 
#
# then add output of `ssh-keygen -F web.sourceforge.net -f sf_known_hosts`
#
web.sourceforge.net ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA2uifHZbNexw6cXbyg1JnzDitL5VhYs0E65Hk/tLAPmcmm5GuiGeUoI/B0eUSNFsbqzwgwrttjnzKMKiGLN5CWVmlN1IXGGAfLYsQwK6wAu7kYFzkqP4jcwc5Jr9UPRpJdYIK733tSEmzab4qc5Oq8izKQKIaxXNe7FgmL15HjSpatFt9w/ot/CHS78FUAr3j3RwekHCm/jhPeqhlMAgC+jUgNJbFt3DlhDaRMa0NYamVzmX8D47rtmBbEDU3ld6AezWBPUR5Lh7ODOwlfVI58NAf/aYNlmvl2TZiauBCTa7OPYSyXJnIPbQXg6YQlDknNCr0K769EjeIlAfY87Z4tw==
web.sourceforge.net ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBCwsY6sZT4MTTkHfpRzYjxG7mnXrGL74RCT2cO/NFvRrZVNB5XNwKNn7G5fHbYLdJ6UzpURDRae1eMg92JG0+yo=
web.sourceforge.net ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIOQD35Ujalhh+JJkPvMckDlhu4dS7WH6NsOJ15iGCJLC
```

### Repository pmd/pmd - Environment "pmd-code"

* `PMD_CODE_ORG_DEPLOY_KEY`: The private ssh key used to access docs.pmd-code.org.
  It is created with `ssh-keygen -t ed25519 -C "ssh key for pmd. used for github actions push to pmd-code.org" -f pmd-code.org_deploy_key`.  
  The public key is configured in `~/.ssh/authorized_keys` on pmd@pmd-code.org. That means, the user is "pmd".  
  The key should begin with "-----BEGIN OPENSSH PRIVATE KEY-----".

This environment also has a variable, which is the known_hosts content as `PMD_CODE_ORG_KNOWN_HOSTS`:

```
#
# pmd-code.org
#
# ssh-keyscan pmd-code.org | tee -a pmd_known_hosts
# ssh-keygen -F pmd-code.org -l -f pmd_known_hosts
# # Host pmd-code.org found: line 1 
# pmd-code.org RSA SHA256:/uKehVNumCNvJL8C5CziwV9KkUUxHfggq0C4GTrUhwg
# # Host pmd-code.org found: line 2 
# pmd-code.org ECDSA SHA256:6aD1r1XuIoc/zgBT3bt1S9L5ToyJzdQ9rrcMchnqiRA
# # Host pmd-code.org found: line 3 
# pmd-code.org ED25519 SHA256:nvkIAzZhYTxXqSU3DWvos83A0EocZ5dsxNkx1LoMZhg
# ssh-keygen -F pmd-code.org -f pmd_known_hosts
pmd-code.org ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDVsIeF6xU0oPb/bMbxG1nU1NDyBpR/cBEPZcm/PuJwdI9B0ydPHA6FysqAnt32fNFznC2SWisnWyY3iNsP3pa8RQJVwmnnv9OboGFlW2/61o3iRyydcpPbgl+ADdt8iU9fmMI7dC04UqgHGBoqOwVNna9VylTjp5709cK2qHnwU450F6YcOEiOKeZfJvV4PmpJCz/JcsUVqft6StviR31jKnqbnkZdP8qNoTbds6WmGKyXkhHdLSZE7X1CFQH28tk8XFqditX93ezeCiThFL7EleDexV/3+2+cs5878sDMUMzHS5KShTjkxzhHaodhtIEdNesinq/hOPbxAGkQ0FbD
pmd-code.org ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBMfSJtZcJCeENSZMvdngr+Hwe7oUVQWWKwC4HnfiOoAh/NSIlzJyQvpoPZxnEFid6Y3ntDK+rnx04Japo63zD8Q=
pmd-code.org ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIFa88nqfMavMH/tGeS5DNrSeM5AVHmZQGHh98vC1717o
```


## Release Signing Keys

### Creating a new key
In general, a key created once should be reused. However, if the key is (potentially) compromised, a new
key needs to be generated. A gpg key consists of a primary key and one or more subkeys. The primary key
defines the identity (fingerprint, key ID) and subkeys can be used for actual signing. The primary key is
then only used to create new subkeys or renew subkeys. For a more safe operation, the primary key should
be kept offline and only the subkeys should be used for signing. A Release Signing Key also doesn't need
a subkey for encryption. In case a signing key gets compromised, the subkey can be revoked and a new key
can be generated. But the primary key still is safe.

Creating such a key is not straightforward, hence this how to (there are a couple of guides
in the internet about best practices):

```
$ gpg --expert --full-generate-key
...
Please select what kind of key you want:
> 8 (RSA (set your own capabilities)
> S (Toggle Sign)
> E (Toggle Encrypt)
> Q
Current allowed actions: Certify
What keysize do you want?
> 4096
Please specify how long the key should be valid.
> 2y
Real name:
> PMD Release Signing Key
Email address:
> releases@pmd-code.org
...
pub   rsa4096 2025-01-04 [C] [expires: 2027-01-04]
      2EFA55D0785C31F956F2F87EA0B5CA1A4E086838
uid                      PMD Release Signing Key <releases@pmd-code.org>
```

Then we create a subkey for signing:
```
$ gpg --edit-key 2EFA55D0785C31F956F2F87EA0B5CA1A4E086838
gpg> addkey
> 4 (RSA (sign only))
keysize:
> 4096
Expiration
> 2y
...
> save
```

Now let's publish the public key:
```
$ gpg --armor --export 2EFA55D0785C31F956F2F87EA0B5CA1A4E086838 | curl -T - https://keys.openpgp.org
Key successfully uploaded. Proceed with verification here:
https://keys.openpgp.org/upload/....
```

Export the key to upload it to <https://keyserver.ubuntu.com/#submitKey>:
`gpg --armor --export 2EFA55D0785C31F956F2F87EA0B5CA1A4E086838 | wl-copy`
Also upload it to <http://pgp.mit.edu/>.

Verify the uploaded (public) key (and expiration date):

```
gpg --armor --export 2EFA55D0785C31F956F2F87EA0B5CA1A4E086838 > release-signing-key-2EFA55D0785C31F956F2F87EA0B5CA1A4E086838-public.asc
gpg --show-keys release-signing-key-2EFA55D0785C31F956F2F87EA0B5CA1A4E086838-public.asc
curl 'https://keys.openpgp.org/vks/v1/by-fingerprint/2EFA55D0785C31F956F2F87EA0B5CA1A4E086838' | gpg --show-keys
curl 'https://keyserver.ubuntu.com/pks/lookup?search=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838&fingerprint=on&exact=on&options=mr&op=get' | gpg --show-keys
curl 'http://pgp.mit.edu/pks/lookup?op=get&search=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838' | gpg --show-keys
```

### Current Key

* Used since January 2025
* Fingerprint `2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838`
* Used for signing artifacts in Maven Central

```
$ gpg --list-keys --fingerprint --with-subkey-fingerprint 2EFA55D0785C31F956F2F87EA0B5CA1A4E086838
pub   rsa4096 2025-01-04 [C] [verfällt: 2027-01-04]
      2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838
uid        [ ultimativ ] PMD Release Signing Key <releases@pmd-code.org>
sub   rsa4096 2025-01-04 [S] [verfällt: 2027-01-04]
      1E04 6C19 ED28 73D8 C08A  F7B8 A063 2691 B78E 3422
```

The public key is available here:
* <https://keys.openpgp.org/search?q=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838>
* <https://keyserver.ubuntu.com/pks/lookup?search=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838&fingerprint=on&op=index>
* <http://pgp.mit.edu/pks/lookup?search=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838&fingerprint=on&op=index>
* <https://github.com/pmd/build-tools/blob/main/scripts/files/release-signing-key-2EFA55D0785C31F956F2F87EA0B5CA1A4E086838-public.asc>


### Old keys

* Fingerprint `EBB2 41A5 45CB 17C8 7FAC B2EB D0BF 1D73 7C9A 1C22`
  * Used until December 2024
  * Replaced as the passphrase has been compromised and therefore the key is potentially
    compromised. Note - as until now (January 2025) we don't have any indication that the key
    actually has been misused.
  * Revoked 2025-01-04.
  * see file `release-signing-key-D0BF1D737C9A1C22-public.asc`.

* Fingerprint `94A5 2756 9CAF 7A47 AFCA  BDE4 86D3 7ECA 8C2E 4C5B`
  * Old key used to sign PMD Designer
  * Revoked 2025-01-04.

### Private key

In order for GitHub Actions to automatically sign the artifacts for snapshot builds and release builds,
we need to make the private key along with the passphrase available. This is done using
multiple [`secrets`](https://help.github.com/en/actions/configuring-and-managing-workflows/creating-and-storing-encrypted-secrets).
The secrets are configured on the organization level of PMD, so that the Release Signing key is available
for all repositories.

To not expose the master key, we only export the subkeys we use for signing and store this in the secret
`PMD_CI_GPG_PRIVATE_KEY`.

For setting up, export the secret key and copy-paste it into a new secret:

```
gpg --armor --export-secret-subkeys 2EFA55D0785C31F956F2F87EA0B5CA1A4E086838 | wl-copy
```

(instead of wl-copy, use xclip or pbcopy, depending on your os).

This private key will be imported by setup-java or a small shell script.

**Note 1:** We use option `--export-secret-subkeys` to only export the subkey and not the master key.
That way, we don't need to transfer the master key.

**Note 2:** In order to use the key later on, the passphrase is needed. This is also setup as a secret:
`PMD_CI_GPG_PASSPHRASE`. This secret is then exported as "MAVEN_GPG_PASSPHRASE" where needed
(`MAVEN_GPG_PASSPHRASE: ${{ secrets.PMD_CI_GPG_PASSPHRASE }}`) in github actions workflows.
See also <https://maven.apache.org/plugins/maven-gpg-plugin/usage.html#sign-artifacts-with-gnupg>.

**Note 3:** The private key is now only secured by the passphrase. It is stored as a GitHub Actions
secret and available in an environment variable. It is not committed as a file anywhere. Note:
When importing the key, it is stored on disk in "~/.gnupg" - hence the GitHub Actions should make sure
to delete this directory on the runner after the workflow is finished in order to not leak
the key to following users of the runner.

### Updating the key

From time to time the key needs to be renewed, passphrase needs to be changed or a whole (sub)key needs to
be replaced.

For renewing or changing the passphrase, import the private master key and public key into your local gpg keystore
(if you don't have it already in your keyring) and renew it.
Make sure to renew all subkeys. Then export the public key again.

For replacing, generate a new (sub) key, just export it.

You can verify the expiration date with `gpg --fingerprint --list-key 2EFA55D0785C31F956F2F87EA0B5CA1A4E086838`:

```
pub   rsa4096 2025-01-04 [C] [expires: 2027-01-04]
      2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838
uid           [ultimate] PMD Release Signing Key <releases@pmd-code.org>
sub   rsa4096 2025-01-04 [S] [expires: 2027-01-04]

```

Upload the exported *public* key to

* <https://keys.openpgp.org/upload>
* <https://keyserver.ubuntu.com/#submitKey>
* <http://pgp.mit.edu/>

Verify the uploaded key expiration date:

```
gpg --show-keys release-signing-key-2EFA55D0785C31F956F2F87EA0B5CA1A4E086838-public.asc
curl 'https://keys.openpgp.org/vks/v1/by-fingerprint/2EFA55D0785C31F956F2F87EA0B5CA1A4E086838' | gpg --show-keys
curl 'https://keyserver.ubuntu.com/pks/lookup?search=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838&fingerprint=on&exact=on&options=mr&op=get' | gpg --show-keys
curl 'http://pgp.mit.edu/pks/lookup?op=get&search=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838' | gpg --show-keys
```

Don't forget to update the secret `PMD_CI_GPG_PRIVATE_KEY` with the renewed private signing subkey.
