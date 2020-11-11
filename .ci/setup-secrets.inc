function pmd_ci_setup_env() {
    log_info "Setting up secrets as environment variables..."

    printenv PMD_CI_SECRET_PASSPHRASE | gpg --batch --yes --decrypt \
        --passphrase-fd 0 \
        --output .ci/env .ci/env.gpg

    source .ci/env
    rm .ci/env
}

function pmd_ci_setup_gpg_key() {
    log_info "Setting up GPG release signing key..."

    mkdir -p "${HOME}/.gpg"
    printenv PMD_CI_SECRET_PASSPHRASE | gpg --batch --yes --decrypt \
        --passphrase-fd 0 \
        --output .ci/release-signing-key-D0BF1D737C9A1C22.gpg .ci/release-signing-key-D0BF1D737C9A1C22.gpg.gpg
    gpg --batch --import .ci/release-signing-key-D0BF1D737C9A1C22.gpg
    rm .ci/release-signing-key-D0BF1D737C9A1C22.gpg
}

function pmd_ci_setup_ssh() {
    log_info "Setting up .ssh/id_rsa..."
    mkdir -p ${HOME}/.ssh
    chmod 700 "${HOME}/.ssh"
    printenv PMD_CI_SECRET_PASSPHRASE | gpg --batch --yes --decrypt \
        --passphrase-fd 0 \
        --output ${HOME}/.ssh/id_rsa .ci/id_rsa.gpg
    chmod 600 "${HOME}/.ssh/id_rsa"

    log_info "Setting up .ssh/known_hosts..."
    #
    # https://sourceforge.net/p/forge/documentation/SSH%20Key%20Fingerprints/
    #
    # run locally:
    # ssh-keyscan web.sourceforge.net | tee -a known_hosts
    #
    # verify fingerprints:
    # ssh-keygen -F web.sourceforge.net -l -f known_hosts 
    # # Host web.sourceforge.net found: line 1 
    # web.sourceforge.net RSA SHA256:xB2rnn0NUjZ/E0IXQp4gyPqc7U7gjcw7G26RhkDyk90 
    # # Host web.sourceforge.net found: line 2 
    # web.sourceforge.net ECDSA SHA256:QAAxYkf0iI/tc9oGa0xSsVOAzJBZstcO8HqGKfjpxcY 
    # # Host web.sourceforge.net found: line 3 
    # web.sourceforge.net ED25519 SHA256:209BDmH3jsRyO9UeGPPgLWPSegKmYCBIya0nR/AWWCY 
    #
    # then add output of `ssh-keygen -F web.sourceforge.net -f known_hosts`
    #
    echo 'web.sourceforge.net ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA2uifHZbNexw6cXbyg1JnzDitL5VhYs0E65Hk/tLAPmcmm5GuiGeUoI/B0eUSNFsbqzwgwrttjnzKMKiGLN5CWVmlN1IXGGAfLYsQwK6wAu7kYFzkqP4jcwc5Jr9UPRpJdYIK733tSEmzab4qc5Oq8izKQKIaxXNe7FgmL15HjSpatFt9w/ot/CHS78FUAr3j3RwekHCm/jhPeqhlMAgC+jUgNJbFt3DlhDaRMa0NYamVzmX8D47rtmBbEDU3ld6AezWBPUR5Lh7ODOwlfVI58NAf/aYNlmvl2TZiauBCTa7OPYSyXJnIPbQXg6YQlDknNCr0K769EjeIlAfY87Z4tw==' >> "$HOME/.ssh/known_hosts"
    echo 'web.sourceforge.net ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBCwsY6sZT4MTTkHfpRzYjxG7mnXrGL74RCT2cO/NFvRrZVNB5XNwKNn7G5fHbYLdJ6UzpURDRae1eMg92JG0+yo=' >> "$HOME/.ssh/known_hosts"
    echo 'web.sourceforge.net ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIOQD35Ujalhh+JJkPvMckDlhu4dS7WH6NsOJ15iGCJLC' >> "$HOME/.ssh/known_hosts"

    # add pmd-code.org (ssh-keyscan pmd-code.org)
    echo 'pmd-code.org ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDVsIeF6xU0oPb/bMbxG1nU1NDyBpR/cBEPZcm/PuJwdI9B0ydPHA6FysqAnt32fNFznC2SWisnWyY3iNsP3pa8RQJVwmnnv9OboGFlW2/61o3iRyydcpPbgl+ADdt8iU9fmMI7dC04UqgHGBoqOwVNna9VylTjp5709cK2qHnwU450F6YcOEiOKeZfJvV4PmpJCz/JcsUVqft6StviR31jKnqbnkZdP8qNoTbds6WmGKyXkhHdLSZE7X1CFQH28tk8XFqditX93ezeCiThFL7EleDexV/3+2+cs5878sDMUMzHS5KShTjkxzhHaodhtIEdNesinq/hOPbxAGkQ0FbD' >> $HOME/.ssh/known_hosts
}
