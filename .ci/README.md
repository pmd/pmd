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

Intermittent build failures while downloading dependencies from maven central.
Root issue seems to be SNAT configs in Azure, which closes long running TCP connections
only on one side: https://docs.microsoft.com/en-us/azure/load-balancer/troubleshoot-outbound-connection#idletimeout
The default timeout is 4 minutes.

Workaround as described in https://github.com/actions/virtual-environments/issues/1499 and
https://issues.apache.org/jira/browse/WAGON-545 is applied:

`-Dmaven.wagon.httpconnectionManager.ttlSeconds=180 -Dmaven.wagon.http.retryHandler.count=3`

## Hints

### Remote debugging

Debugging remotely is possible with <https://github.com/mxschmitt/action-tmate>.

Just add the following step into the job:

```
      - name: Setup tmate session
        uses: mxschmitt/action-tmate@v3
```

Note: This is dangerous for push builds, because these have access to the secrets and the SSH session
is not protected.

### Local tests

You can run a local instance with docker:

```
docker run -it --mount type=bind,source=path/to/pmd,target=/workspaces/pmd/pmd ubuntu:latest
```

You'll need to install a few packages before you can start... You can verify with `check-environment.sh`
if everything is prepared.
