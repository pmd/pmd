#!/bin/bash
set -eu

TYPOS_GH_ANNOTATE=${TYPOS_GH_ANNOTATE:=""}
TYPOS_INSTALL_DIR="$(dirname "$0")/typos"
REPO_ROOT="$(git rev-parse --path-format=relative --show-toplevel)"
if [ ! -d "$TYPOS_INSTALL_DIR" ]; then
  mkdir -p "${TYPOS_INSTALL_DIR}"
  pushd "${TYPOS_INSTALL_DIR}"
  TYPOS_VERSION="v1.42.3"
  wget --progress=dot:mega https://github.com/crate-ci/typos/releases/download/${TYPOS_VERSION}/typos-${TYPOS_VERSION}-x86_64-unknown-linux-musl.tar.gz
  tar -x -v -f typos-${TYPOS_VERSION}-x86_64-unknown-linux-musl.tar.gz ./typos
  popd
fi

if [ -n "$TYPOS_GH_ANNOTATE" ]; then
  # Uses the same approach as in
  # https://github.com/crate-ci/typos/blob/master/action/format_gh.sh
  # but additionally removes the REPO_ROOT prefix from file paths ("./")
  # so that the paths are relative to GITHUB_WORKSPACE and the annotations
  # show up in the pull request changes.
  #
  "${TYPOS_INSTALL_DIR}"/typos -c "${REPO_ROOT}"/.ci/files/typos.toml --format json "$@" | (
    grep '"type":"typo"' |
      jq --sort-keys --raw-output '"::warning file=\(.path|ltrimstr("'"${REPO_ROOT}"'")),line=\(.line_num),col=\(.byte_offset)::\"\(.typo)\" should be \"" + (.corrections // [] | join("\" or \"") + "\".")' |
      while IFS= read -r line; do
        echo "$line"
      done
  ) || true
fi

"${TYPOS_INSTALL_DIR}"/typos -c "${REPO_ROOT}"/.ci/files/typos.toml "$@"
