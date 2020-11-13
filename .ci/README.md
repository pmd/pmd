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

You can run a local instance with docker and mount your local pmd checkout into the container:

```
docker run -it --mount type=bind,source=path/to/pmd,target=/workspaces/pmd/pmd pmd-ci
```

You'll be dropped into a bash. Start e.g. with

```
cd workspaces/pmd/pmd
.ci/check-environment.sh
.ci/build-pr.sh
```
