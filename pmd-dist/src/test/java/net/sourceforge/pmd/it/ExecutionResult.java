/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import net.sourceforge.pmd.PMD;

/**
 * Collects the result of a command execution in order to verify it.
 *
 * @author Andreas Dangel
 */
public class ExecutionResult {
    private final int exitCode;
    private final String output;

    ExecutionResult(int theExitCode, String theOutput) {
        this.exitCode = theExitCode;
        this.output = theOutput;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExecutionResult:")
            .append(PMD.EOL)
            .append(" exit code: ").append(exitCode).append(PMD.EOL)
            .append(" output:").append(PMD.EOL).append(output).append(PMD.EOL);
        return sb.toString();
    }

    /**
     * Asserts that the command exited with the expected exit code and that the given expected
     * output is contained in the actual command output.
     *
     * @param expectedExitCode the exit code, e.g. 0 if no rule violations are expected, or 4 if violations are found
     * @param expectedOutput the output to search for
     */
    public void assertExecutionResult(int expectedExitCode, String expectedOutput) {
        assertEquals("Command exited with wrong code", expectedExitCode, exitCode);
        assertNotNull("No output found", output);
        if (!output.contains(expectedOutput)) {
            fail("Expected output '" + expectedOutput + "' not present.\nComplete output:\n\n" + output);
        }
    }
}
