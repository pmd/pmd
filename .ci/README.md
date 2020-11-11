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

