# PMD CI Scripts

This folder contains scripts used for CI, that are PMD specific.
It uses the common scripts from [build-tools](https://github.com/pmd/build-tools).

## .ci/files/public-env.gpg

This files contains the following environment variables:

*   DANGER_GITHUB_API_TOKEN: Token for danger to add comments to PRs as <https://github.com/pmd-test>
*   PMD_CI_CHUNK_TOKEN: Token for uploading reports to chunk.io

The file is encrypted, so that the tokens are not automatically disabled when github detects them
in clear text.

**Decrypting**:

    gpg --batch --yes --decrypt --passphrase="GnxdjywUEPveyCD1RLiTd7t8CImnefYr" \
        --output .ci/files/public-env .ci/files/public-env.gpg

**Encrypting**:

    gpg --batch --symmetric --cipher-algo AES256 \
        --armor --passphrase="GnxdjywUEPveyCD1RLiTd7t8CImnefYr" \
        --output .ci/files/public-env.gpg .ci/files/public-env

## Known Issues

### Intermittent connection resets or timeouts while downloading dependencies from maven central

Root issue seems to be SNAT Configs in Azure, which closes long running [idle TCP connections
after 4 minutes](https://docs.microsoft.com/en-us/azure/load-balancer/troubleshoot-outbound-connection#idletimeout).

The workaround is described in [actions/virtual-environments#1499](https://github.com/actions/virtual-environments/issues/1499)
and [WAGON-545](https://issues.apache.org/jira/browse/WAGON-545)
and [WAGON-486](https://issues.apache.org/jira/browse/WAGON-486):

The setting `-Dmaven.wagon.httpconnectionManager.ttlSeconds=180 -Dmaven.wagon.http.retryHandler.count=3`
makes sure, that Maven doesn't try to use pooled connections that have been unused for more than 180 seconds.
These settings are placed as environment variable `MAVEN_OPTS` in the workflow, so that they are active for
all Maven executions (including builds done by regression tester).

Alternatively, pooling could be disabled completely via `-Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false`.
This has the consequence, that for each dependency, that is being downloaded, a new https connection is
established.

More information about configuring this can be found at [wagon-http](https://maven.apache.org/wagon/wagon-providers/wagon-http/).

## Remote debugging

Debugging remotely is possible with <https://github.com/mxschmitt/action-tmate>.

Just add the following step into the job:

```
      - name: Setup tmate session
        uses: mxschmitt/action-tmate@v3
```

The workflow `troubleshooting` can be started manually, which already contains the tmate action.

**Note**: This is dangerous for push/pull builds on pmd/pmd, because these have access to the secrets and the SSH session
is not protected. Builds triggered by pull requests from forked repositories don't have access to the secrets.

## Local tests with docker

Using the same docker container as described in [build-env @ build-tools](https://github.com/pmd/build-tools).

### Testing a push build (snapshot)

Start docker without binding to local directory, so that we can do a fresh checkout

    $ docker run \
        --interactive \
        --tty \
        --name pmd-build-env_pmd \
        pmd-build-env:latest


```
export LANG=en_US.UTF-8
export MAVEN_OPTS="-Dmaven.wagon.httpconnectionManager.ttlSeconds=180 -Dmaven.wagon.http.retryHandler.count=3"
export PMD_CI_SCRIPTS_URL=https://raw.githubusercontent.com/pmd/build-tools/master/scripts

export PMD_CI_SECRET_PASSPHRASE="xyz"
export PMD_CI_DEBUG=true

MAIN_BRANCH="master"
eval $(~/create-gh-actions-env.sh push pmd/pmd $MAIN_BRANCH)

cd /workspaces/pmd
rmdir pmd && mkdir pmd
cd pmd
git init
git remote add origin https://github.com/pmd/pmd
git fetch --no-tags --prune --progress --no-recurse-submodules --depth=1 origin +refs/heads/${MAIN_BRANCH}:refs/remotes/origin/${MAIN_BRANCH}
git checkout --progress --force -B ${MAIN_BRANCH} refs/remotes/origin/${MAIN_BRANCH}


f=check-environment.sh; \
        mkdir -p .ci && \
        ( [ -e .ci/$f ] || curl -sSL "${PMD_CI_SCRIPTS_URL}/$f" > ".ci/$f" ) && \
        chmod 755 .ci/$f && \
        .ci/$f

.ci/build.sh
```

### Testing a pull request

Same as the above, but this line changes:

```
eval $(~/create-gh-actions-env.sh pull_request pmd/pmd $MAIN_BRANCH)
```

Maybe update `/workspaces/event.json` to fill in a real pull request number, so that
danger can comment the correct PR.

And the checkout must be different. Example for PR 3220:

```
PMD_CI_PULL_REQUEST_NUMBER=3220
cd /workspace/pmd
rmdir pmd && mkdir pmd
cd pmd
git init
git remote add origin https://github.com/pmd/pmd
git fetch --no-tags --prune --progress --no-recurse-submodules --depth=2 origin +refs/pull/${PMD_CI_PULL_REQUEST_NUMBER}/merge:refs/remotes/pull/${PMD_CI_PULL_REQUEST_NUMBER}/merge
git checkout --progress --force refs/remotes/pull/${PMD_CI_PULL_REQUEST_NUMBER}/merge
```

### Forked build

A build executing on a forked repository.

```
$(~/create-gh-actions-env.sh push adangel/pmd $MAIN_BRANCH)
```


### Performing a release (push) build

```
eval $(~/create-gh-actions-env.sh push pmd/pmd refs/tags/v1.0.0_release_test)
```

Make sure, that `MAVEN_OPTS` contains `-DskipRemoteStaging=true`, so that no maven artifacts are deployed
to maven central (this is set by `create-gh-actions-env.sh`).

And the checkout could be different...


