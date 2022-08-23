/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.internal;

/**
 * The execution result of any given command.
 */
public enum ExecutionResult {
    OK(0),
    ERROR(1),
    USAGE_ERROR(2),
    VIOLATIONS_FOUND(4);
    
    private final int exitCode;
    
    ExecutionResult(int exitCode) {
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
