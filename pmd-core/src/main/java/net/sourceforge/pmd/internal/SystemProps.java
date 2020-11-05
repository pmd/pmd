/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal;

public final class SystemProps {

    public static final String PMD_ERROR_RECOVERY = "pmd.error_recovery";

    private SystemProps() {
    }

    /**
     * In error recovery mode errors like StackOverflowError or AssetionErrors are logged
     * and the execution continues.
     * These exceptions mean, that something went really wrong while executing and
     * depending on where the error occurred, the internal state might be corrupted
     * or not. Hence, it might work to continue and "ignore" (just log) the error
     * or we'll see more problems when continuing. That's why error recovery mode
     * is not enabled by default.
     * <p>
     * The System Property is called {@code pmd.error_recovery}.
     */
    public static boolean isErrorRecoveryMode() {
        return System.getProperty(PMD_ERROR_RECOVERY) != null;
    }
}
