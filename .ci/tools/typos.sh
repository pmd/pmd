#!/bin/bash
set -eu

# whether to output GitHub annotations for typos (e.g. in pull requests)
TYPOS_GH_ANNOTATE=${TYPOS_GH_ANNOTATE:=""}

TYPOS_VERSION="v1.42.3"
REPO_ROOT="$(git rev-parse --path-format=relative --show-toplevel)"
TYPOS_INSTALL_DIR="${REPO_ROOT}.ci/tools/typos"
TYPOS_EXECUTABLE="${TYPOS_INSTALL_DIR}/typos-${TYPOS_VERSION}"
TYPOS_CONFIG="${REPO_ROOT}/.ci/files/typos.toml"

if [ ! -e "${TYPOS_EXECUTABLE}" ]; then
  echo "Installing typos ${TYPOS_VERSION} to ${TYPOS_INSTALL_DIR}"
  mkdir -p "${TYPOS_INSTALL_DIR}"
  pushd "${TYPOS_INSTALL_DIR}"
  wget --progress=dot:mega https://github.com/crate-ci/typos/releases/download/${TYPOS_VERSION}/typos-${TYPOS_VERSION}-x86_64-unknown-linux-musl.tar.gz
  tar -x -v -f typos-${TYPOS_VERSION}-x86_64-unknown-linux-musl.tar.gz ./typos
  rm typos-${TYPOS_VERSION}-x86_64-unknown-linux-musl.tar.gz
  mv ./typos ./typos-${TYPOS_VERSION}
  popd
fi

if [ -n "$TYPOS_GH_ANNOTATE" ]; then
  # Uses the same approach as in
  # https://github.com/crate-ci/typos/blob/master/action/format_gh.sh
  # but additionally relativizes the file paths to GITHUB_WORKSPACE
  # so that the annotations show up in the pull request changes.
  #
  "${TYPOS_EXECUTABLE}" -c "${TYPOS_CONFIG}" --format json "$@" | (
    grep '"type":"typo"' | (
      typo="$(cat)"
      original_path="$(echo "$typo" | jq --raw-output '.path')"
      relative_path="$(realpath --relative-to="$GITHUB_WORKSPACE" "$original_path")"
      updated_typo="$(echo "$typo" | jq --arg path "$relative_path" '.path=$path')"
      echo "$updated_typo"
    ) |
      jq --raw-output '"::warning file=\(.path),line=\(.line_num),col=\(.byte_offset + 1)::\"\(.typo)\" should be \"" + (.corrections // [] | join("\" or \"") + "\".")' |
      while IFS= read -r line; do
        echo "$line"
      done
  ) || true
fi

"${TYPOS_EXECUTABLE}" -c "${TYPOS_CONFIG}" "$@"
