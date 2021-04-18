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
git fetch --no-tags --prune --progress --no-recurse-submodules --depth=2 origin +refs/heads/${MAIN_BRANCH}:refs/remotes/origin/${MAIN_BRANCH}
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
export LANG=en_US.UTF-8
export PMD_CI_SCRIPTS_URL=https://raw.githubusercontent.com/pmd/build-tools/master/scripts

export PMD_CI_SECRET_PASSPHRASE="xyz"
export PMD_CI_DEBUG=true

TAG_NAME=pmd_releases/6.33.0

eval $(~/create-gh-actions-env.sh push pmd/pmd refs/tags/$TAG_NAME)

cd /workspaces/pmd
rmdir pmd && mkdir pmd
cd pmd
git init
git remote add origin https://github.com/pmd/pmd
git fetch --no-tags --prune --progress --no-recurse-submodules --depth=2 origin +refs/tags/$TAG_NAME:refs/tags/$TAG_NAME
git checkout --progress --force refs/tags/$TAG_NAME

f=check-environment.sh; \
        mkdir -p .ci && \
        ( [ -e .ci/$f ] || curl -sSL "${PMD_CI_SCRIPTS_URL}/$f" > ".ci/$f" ) && \
        chmod 755 .ci/$f && \
        .ci/$f

#
# .ci/build.sh
#
```

Calling `.ci/build.sh` directly would re-release the tag $TAG_NAME - that's why it is commented out.
All the side-effects of a release would be carried out like creating and publishing a release on github,
uploading the release to sourceforge, uploading the docs to pmd.github.io/docs.pmd-code.org, uploading a
new baseline for the regression tester and so on. While the release should be reproducible and therefore should
produce exactly the same artifacts, re-uploading artifacts is not desired just for testing.

Note that maven-central would not be changed, since this is skipped via MAVEN_OPTS:
`MAVEN_OPTS` contains `-DskipRemoteStaging=true`, so that no maven artifacts are deployed
to maven central (this is set by `create-gh-actions-env.sh`).

So for now in order to test the build script, you need to manually edit the script and comment out the
critical lines... (like publish github releases, uploading files to sourceforge ...). Later a
"dry-run" mode could be added.

Make sure to cleanup after the test, e.g. discard the draft github release.
