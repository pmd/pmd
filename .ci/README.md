## PMD CI Scripts

This folder contains scripts used for CI.

## Secrets

One secret is required for decrypting the GPG Key with which the PMD Releases are signed and
for a ssh key, which is used to copy files to sourceforge.

## Environment variables

* PMD_CI_SECRET_PASSPHRASE
* CI_DEPLOY_USER
* CI_DEPLOY_PASSWORD
* CI_SIGN_KEY
* CI_SIGN_PASSPHRASE
* PMD_SF_USER
* PMD_SF_APIKEY
* GITHUB_OAUTH_TOKEN
* GITHUB_BASE_URL
* COVERALLS_REPO_TOKEN
* SONAR_TOKEN
* DANGER_GITHUB_API_TOKEN
* PMD_CI_CHUNK_TOKEN

## Encrypting

    gpg --batch --symmetric --cipher-algo AES256 --passphrase="$PMD_CI_SECRET_PASSPHRASE" file.txt

## Known Issues

### Intermittent connection resets or timeouts while downloading dependencies from maven central

Root issue seems to be SNAT Configs in Azure, which closes long running [idle TCP connections
after 4 minutes](https://docs.microsoft.com/en-us/azure/load-balancer/troubleshoot-outbound-connection#idletimeout).

The workaround is described in [actions/virtual-environments#1499](https://github.com/actions/virtual-environments/issues/1499)
and [WAGON-545](https://issues.apache.org/jira/browse/WAGON-545)
and [WAGON-486](https://issues.apache.org/jira/browse/WAGON-486):

The setting `-Dmaven.wagon.httpconnectionManager.ttlSeconds=180 -Dmaven.wagon.http.retryHandler.count=3`
makes sure, that Maven doesn't try to use pooled connections that have been unused for more than 180 seconds.
These settings are placed as environment variable `MAVEN_OPTS` in all workflows, so that they are active for
all Maven executions (including builds done by regression tester).

Alternatively, pooling could be disabled completely via `-Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false`.
This has the consequence, that for each dependency, that is being downloaded, a new https connection is
established.

More information about configuring this can be found at [wagon-http](https://maven.apache.org/wagon/wagon-providers/wagon-http/).

However, this doesn't work when [dokka-maven-plugin](https://github.com/Kotlin/dokka) is used: This plugin
downloads additional dokka plugins at runtime and reconfigures somehow Maven. After this plugin is loaded,
the above system properties have no effect anymore.
See [dokka/dokka-maven-plugin#1625](https://github.com/Kotlin/dokka/issues/1625) and
[dokka/dokka-maven-plugin#1626](https://github.com/Kotlin/dokka/issues/1626).

The workaround now in place is, to download all the dependencies first, see `inc/maven-dependencies.inc`.

## Hints

### Remote debugging

Debugging remotely is possible with <https://github.com/mxschmitt/action-tmate>.

Just add the following step into the job:

```
      - name: Setup tmate session
        uses: mxschmitt/action-tmate@v3
```

The workflow `troubleshooting` can be started manually, which already contains the tmate action.

**Note**: This is dangerous for push/pull builds on pmd/pmd, because these have access to the secrets and the SSH session
is not protected. Builds triggered by pull requests from forked repositories don't have access to the secrets.

### Local tests with docker

Create a local docker container:

```
cd .ci/docker_ubuntu18.04
docker build -t pmd-ci .
```

This container is based on Ubuntu 18.04, which is used for `ubuntu-latest` github actions runner,
see [Virtual Environment](https://github.com/actions/virtual-environments).

You can run a local instance with docker:

```
docker run -it pmd-ci
```

You'll be dropped into a bash.

#### Testing a push build (snapshot)

Start docker without binding to local directory, so that we can do a fresh checkout: `docker run -it pmd-ci`.
You'll be dropped into a bash. Use the following script, to setup and start the build:

```
MAIN_BRANCH="master"
export MAVEN_OPTS="-Dmaven.wagon.httpconnectionManager.ttlSeconds=180 -Dmaven.wagon.http.retryHandler.count=3"
export PMD_CI_JOB_URL="manual job execution in docker"
export PMD_CI_PUSH_COMMIT_COMPARE=""
export PMD_CI_GIT_REF="refs/heads/${MAIN_BRANCH}"

export PMD_CI_SECRET_PASSPHRASE="xyz"

cd /workspaces/pmd
rmdir pmd && mkdir pmd
cd pmd
git init
git remote add origin https://github.com/pmd/pmd
git fetch --no-tags --prune --progress --no-recurse-submodules --depth=1 origin +refs/heads/${MAIN_BRANCH}:refs/remotes/origin/${MAIN_BRANCH}
git checkout --progress --force -B master refs/remotes/origin/${MAIN_BRANCH}

.ci/check-environment.sh

.ci/build.sh
```

#### Performing a release (push) build

Start docker without binding to local directory, so that we can do a fresh checkout: `docker run -it pmd-ci`.
You'll be dropped into a bash. Use the following script, to setup and start the build:

```
TAG_NAME="pmd_releases/0.0.0_release_test"
export MAVEN_OPTS="-Dmaven.wagon.httpconnectionManager.ttlSeconds=180 -Dmaven.wagon.http.retryHandler.count=3"
export PMD_CI_JOB_URL="manual job execution in docker"
export PMD_CI_PUSH_COMMIT_COMPARE=""
export PMD_CI_GIT_REF="refs/tags/${TAG_NAME}"

export PMD_CI_SECRET_PASSPHRASE="xyz"

cd /workspace/pmd
rmdir pmd && mkdir pmd
cd pmd
git init
git remote add origin https://github.com/pmd/pmd
git fetch --no-tags --prune --progress --no-recurse-submodules --depth=1 origin +refs/tags/${TAG_NAME}:refs/tags/${TAG_NAME}
git checkout --progress --force refs/tags/${TAG_NAME}

.ci/check-environment.sh

.ci/build.sh
```

**Warning:** This will build and upload to maven central!


#### Testing a pull request

Start docker without binding to local directory, so that we can do a fresh checkout: `docker run -it pmd-ci`.
You'll be dropped into a bash. Use the following script, to setup and start the build:

```
export MAVEN_OPTS="-Dmaven.wagon.httpconnectionManager.ttlSeconds=180 -Dmaven.wagon.http.retryHandler.count=3"
export PMD_CI_BRANCH="master" # base branch
export PMD_CI_PULL_REQUEST_NUMBER=2913

# these are used by danger
export GITHUB_EVENT_PATH=/workspaces/event.json
export GITHUB_REPOSITORY=pmd/pmd
export GITHUB_ACTION=run1
export GITHUB_EVENT_NAME=pull_request
/home/pmd-ci/create-gh-pull-request-event.sh

cd /workspace/pmd
rmdir pmd && mkdir pmd
cd pmd
git init
git remote add origin https://github.com/pmd/pmd
git fetch --no-tags --prune --progress --no-recurse-submodules --depth=2 origin +refs/pull/${PMD_CI_PULL_REQUEST_NUMBER}/merge:refs/remotes/pull/${PMD_CI_PULL_REQUEST_NUMBER}/merge
git checkout --progress --force refs/remotes/pull/${PMD_CI_PULL_REQUEST_NUMBER}/merge

.ci/check-environment.sh

.ci/build-pr.sh
```
