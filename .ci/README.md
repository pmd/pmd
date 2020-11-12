## PMD CI Scripts

This folder contains scripts used for CI.

## Secrets

One secret is required for decrypting the GPG Key with which the PMD Releases are signed and
for a ssh key, which is used to copy files to sourceforge.

## Environment variables

* `PMD_CI_SECRET_PASSPHRASE`
* `CI_DEPLOY_PASSWORD`
* `CI_SIGN_PASSPHRASE`
* ...

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
