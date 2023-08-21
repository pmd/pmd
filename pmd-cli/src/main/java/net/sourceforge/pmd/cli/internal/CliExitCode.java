/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.internal;

import net.sourceforge.pmd.PMDConfiguration;

/**
 * The execution result of any given command.
 */
public enum CliExitCode {
    /** No errors, no violations. This is exit code {@code 0}. */
    OK(0),
    /**
     * Errors were detected, PMD may have not run to the end.
     * This is exit code {@code 1}.
     */
    ERROR(1),
    /**
     * Indicates a problem with the CLI parameters: either a required
     * parameter is missing or an invalid parameter was provided.
     */
    USAGE_ERROR(2),
    /**
     * No errors, but PMD found violations. This is exit code {@code 4}.
     * This is only returned if {@link PMDConfiguration#isFailOnViolation()}
     * is set (CLI flag {@code --failOnViolation}).
     */
    VIOLATIONS_FOUND(4);

    private final int exitCode;

    CliExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }

    public static CliExitCode fromInt(int i) {
        switch (i) {
        case 0: return OK;
        case 1: return ERROR;
        case 2: return USAGE_ERROR;
        case 4: return VIOLATIONS_FOUND;
        default:
            throw new IllegalArgumentException("Not a known exit code: " + i);
        }
    }
}
