#!/usr/bin/env bash

cat > /workspaces/event.json <<EOF
{
    "number": ${PMD_CI_PULL_REQUEST_NUMBER},
    "repository": {
        "clone_url": "https://github.com/pmd/pmd.git"
    }
}
EOF
