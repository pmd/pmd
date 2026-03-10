#!/bin/bash
set -eu

# whether to output GitHub annotations for typos (e.g. in pull requests)
TYPOS_GH_ANNOTATE=${TYPOS_GH_ANNOTATE:=""}
# whether to check for a new version of typos
TYPOS_CHECK_UPDATE=${TYPOS_CHECK_UPDATE:=""}

TYPOS_VERSION="v1.43.5"
if [ -n "$TYPOS_CHECK_UPDATE" ]; then
  echo "Checking for latest typos version..."
  LATEST_TYPO_VERSION=$(curl -s -L \
                        -H "Accept: application/vnd.github+json" \
                        -H "X-GitHub-Api-Version: 2022-11-28" \
                        https://api.github.com/repos/crate-ci/typos/releases/latest|jq --raw-output .name)
  if [ "${LATEST_TYPO_VERSION}" != "${TYPOS_VERSION}" ]; then
    echo "A new version of typos is available: ${LATEST_TYPO_VERSION} (current: ${TYPOS_VERSION})"
    TYPOS_VERSION=${LATEST_TYPO_VERSION}
  else
    echo "You are using the latest version of typos: ${TYPOS_VERSION}"
  fi
fi
REPO_ROOT="$(git rev-parse --path-format=relative --show-toplevel)"
TYPOS_INSTALL_DIR="${REPO_ROOT}.ci/tools/typos"
TYPOS_EXECUTABLE="${TYPOS_INSTALL_DIR}/typos-${TYPOS_VERSION}"
TYPOS_CONFIG="${REPO_ROOT}/.ci/files/typos.toml"

if [ ! -e "${TYPOS_EXECUTABLE}" ]; then
  echo "Installing typos ${TYPOS_VERSION} to ${TYPOS_INSTALL_DIR}"
  mkdir -p "${TYPOS_INSTALL_DIR}"
  pushd "${TYPOS_INSTALL_DIR}"
  wget --progress=dot:mega "https://github.com/crate-ci/typos/releases/download/${TYPOS_VERSION}/typos-${TYPOS_VERSION}-x86_64-unknown-linux-musl.tar.gz"
  tar -x -v -f "typos-${TYPOS_VERSION}-x86_64-unknown-linux-musl.tar.gz" ./typos
  rm "typos-${TYPOS_VERSION}-x86_64-unknown-linux-musl.tar.gz"
  mv ./typos "./typos-${TYPOS_VERSION}"
  popd
fi

if [ -n "$TYPOS_GH_ANNOTATE" ]; then
  # Uses the same approach as in
  # https://github.com/crate-ci/typos/blob/master/action/format_gh.sh
  # but additionally relativizes the file paths to GITHUB_WORKSPACE
  # so that the annotations show up in the pull request changes.
  #
  "${TYPOS_EXECUTABLE}" -c "${TYPOS_CONFIG}" --format json "$@" | (
    grep '"type":"typo"' | while IFS= read -r typo; do
      original_path="$(echo "$typo" | jq --raw-output '.path')"
      relative_path="$(realpath --relative-to="$GITHUB_WORKSPACE" "$original_path")"
      echo "$typo" | jq --arg relative_path "$relative_path" --raw-output \
        '"::warning file=\($relative_path),line=\(.line_num),col=\(.byte_offset + 1)::\"\(.typo)\" should be \"" + (.corrections // [] | join("\" or \"") + "\".")'
    done
  ) || true
fi

"${TYPOS_EXECUTABLE}" -c "${TYPOS_CONFIG}" "$@"
