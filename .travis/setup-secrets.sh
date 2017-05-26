#!/bin/bash
set -e

if [ "$TRAVIS_PULL_REQUEST" != "false" ] || [ "${TRAVIS_SECURE_ENV_VARS}" != "true" ]; then
    echo "Not setting up secrets (TRAVIS_PULL_REQUEST=${TRAVIS_PULL_REQUEST} TRAVIS_SECURE_ENV_VARS=${TRAVIS_SECURE_ENV_VARS})."
    exit 0
fi


openssl aes-256-cbc -K $encrypted_5630fbebf057_key -iv $encrypted_5630fbebf057_iv -in .travis/secrets.tar.enc -out .travis/secrets.tar -d
pushd .travis && tar xfv secrets.tar && popd
mkdir -p "$HOME/.ssh"
chmod 700 "$HOME/.ssh"
mv .travis/id_rsa "$HOME/.ssh/id_rsa"
chmod 600 "$HOME/.ssh/id_rsa"
mkdir -p "$HOME/.gpg"
gpg --batch --import .travis/release-signing-key-82DE7BE82166E84E.gpg
rm .travis/secrets.tar
rm .travis/release-signing-key-82DE7BE82166E84E.gpg
