# shellcheck shell=sh

# Check whether to use latest version of PMD
if [ "$PMD_VERSION" == 'latest' ]; then
    LATEST_TAG="$(curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/pmd/pmd/releases/latest | jq --raw-output '.tag_name')"
    PMD_VERSION="${LATEST_TAG#"pmd_releases/"}"
fi

# Download PMD
wget https://github.com/pmd/pmd/releases/download/pmd_releases%2F"${PMD_VERSION}"/pmd-bin-"${PMD_VERSION}".zip
unzip pmd-bin-"${PMD_VERSION}".zip
# Now either run the full analysis or files changed based on the settings defined
if [ "$ANALYSE_ALL_CODE" == 'true' ]; then
    pmd-bin-"${PMD_VERSION}"/bin/run.sh pmd -d "$FILE_PATH" -R "$RULES_PATH" -failOnViolation false -f sarif > pmd-raw-output.sarif
else
    if [ "$ACTION_EVENT_NAME" == 'pull_request' ]; then
        # Now to determine whether to get the files changed from a git diff or using the files changed in a GitHub Pull Request
        # Both options will generate a CSV file first with the files changed
        if [ "$FILE_DIFF_TYPE" == 'git' ]; then
            git diff --name-only --diff-filter=d origin/"$CURRENT_CODE"..origin/"${CHANGED_CODE#"refs/heads/"}" | paste -s -d "," >> diff-file.csv
        else
            curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${AUTH_TOKEN}" https://api.github.com/repos/"$REPO_NAME"/pulls/"$PR_NUMBER"/files | jq --raw-output '.[] .filename' | paste -s -d "," >> diff-file.csv
        fi
    else
        # Irrespective of the file type diff selected on a push event, we will always do a git diff (as we can't get that from the GitHub API)
        git diff --name-only --diff-filter=d "$CURRENT_CODE".."$CHANGED_CODE" | paste -s -d "," >> diff-file.csv
    fi
    # Run the analysis
    pmd-bin-"${PMD_VERSION}"/bin/run.sh pmd -filelist diff-file.csv -R "$RULES_PATH" -failOnViolation false -f sarif > pmd-raw-output.sarif
fi
# Loop through each rule and see if an error should be thrown
echo "::set-output name=error-found::false"
while read -r rule; do
    RULE="$(echo "$rule" | jq --raw-output '.id')"
    if [[ $RULE && "$ERROR_RULES" == *"$RULE"* ]]; then
        echo "::set-output name=error-found::true"
        break
    fi
done <<< "$(cat pmd-raw-output.sarif | jq --compact-output '.runs[] .tool .driver .rules[]')"
# Set the correct file location for the report
cat pmd-raw-output.sarif | jq --arg workspace "$WORKSPACE" '(.runs[] .results[] .locations[] .physicalLocation .artifactLocation .uri) |= ltrimstr($workspace)' > pmd-file-locations-output.sarif
# Set the rule level configurations for whether they are notes or errors
cat pmd-file-locations-output.sarif | jq --arg errors "$ERROR_RULES" '((.runs[] .tool .driver .rules[]) | select(.id==($errors | split(",")[]))) += {"defaultConfiguration": {"level": "error"}}' > pmd-errors-output.sarif
cat pmd-errors-output.sarif | jq --arg notes "$NOTE_RULES" '((.runs[] .tool .driver .rules[]) | select(.id==($notes | split(",")[]))) += {"defaultConfiguration": {"level": "note"}}' > pmd-output.sarif