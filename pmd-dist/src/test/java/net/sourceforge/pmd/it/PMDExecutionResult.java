/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Collects the result of a PMD execution in order to verify it.
 *
 * @author Andreas Dangel
 */
public class PMDExecutionResult {
    private final int exitCode;
    private final String output;

    PMDExecutionResult(int theExitCode, String theOutput) {
        this.exitCode = theExitCode;
        this.output = theOutput;
    }

    /**
     * Asserts that PMD exited with the expected exit code and that the given expected
     * output is contained in the actual PMD output.
     *
     * @param expectedExitCode the exit code, e.g. 0 if no rule violations are expected, or 4 if violations are found
     * @param expectedOutput the output to search for
     */
    public void assertPMDExecutionResult(int expectedExitCode, String expectedOutput) {
        assertEquals("PMD exited with wrong code", expectedExitCode, exitCode);
        assertNotNull("No output found", output);
        if (!output.contains(expectedOutput)) {
            fail("Expected output '" + expectedOutput + "' not present.\nComplete output:\n\n" + output);
        }
    }
}
