#!/bin/bash
#
# BSD-style license; for more info see http://pmd.sourceforge.net/license.html
#

# Helper script, that updates the "last_updated" field in the front matter based on the git info.
# It searches in the current directory.
#
# Usage:
# ./update-last-updated.sh
#
# This will update all markdown files. Then review the changed files
# and commit or revert.
#
# ./update-last-updated.sh pages/pmd/about/help.md
#
# Updates a single file only.
#

function process_file() {
  FILE="$1"
  echo -n "$FILE: "
  read -ra COMMIT_INFO < <(git log -1 --pretty="%cI %H" "$FILE")
  #echo "Date: ${COMMIT_INFO[0]}"
  #echo "Commit: ${COMMIT_INFO[1]}"

  DATE=$(date --date="${COMMIT_INFO[0]}" "+%B %Y")

  TAG="$(git describe --contains "${COMMIT_INFO[1]}")"
  TAG="${TAG%%~*}"
  TAG="${TAG#pmd_releases/}"
  #echo "Version: $TAG"

  LAST_UPDATED="last_updated: $DATE ($TAG)"
  echo "$LAST_UPDATED"
  sed -i -e "s/^last_updated:.*\$/$LAST_UPDATED/" "$FILE"
}

# English locale - needed when creating the date
export LANG="en_US.UTF-8"

if [ -n "$1" ]; then
  # single file given
  process_file "$1"
else
  # no file given, search for all markdown files in current directory
  # ignore files in directory "vendor"
  for i in `find . -type d -name vendor -prune -o -type f -name "*.md" -print`; do
    # only consider files, that are under version control
    if git ls-files --error-unmatch "$i" >/dev/null 2>&1; then
      process_file "$i"
    fi
  done
fi
