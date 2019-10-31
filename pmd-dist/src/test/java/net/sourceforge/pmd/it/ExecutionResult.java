/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
    private final String errorOutput;
    private final String report;

    ExecutionResult(int theExitCode, String theOutput, String theErrorOutput, String theReport) {
        this.exitCode = theExitCode;
        this.output = theOutput;
        this.errorOutput = theErrorOutput;
        this.report = theReport;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExecutionResult:")
            .append(PMD.EOL)
            .append(" exit code: ").append(exitCode).append(PMD.EOL)
            .append(" output:").append(PMD.EOL).append(output).append(PMD.EOL)
            .append(" errorOutput:").append(PMD.EOL).append(errorOutput).append(PMD.EOL)
            .append(" report:").append(PMD.EOL).append(report).append(PMD.EOL);
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
        assertExecutionResult(expectedExitCode, expectedOutput, null);
    }

    /**
     * Asserts that the command exited with the expected exit code and that the given expected
     * output is contained in the actual command output and the given expected report is in the
     * generated report.
     *
     * @param expectedExitCode the exit code, e.g. 0 if no rule violations are expected, or 4 if violations are found
     * @param expectedOutput the output to search for
     * @param expectedReport the string to search for tin the report
     */
    public void assertExecutionResult(int expectedExitCode, String expectedOutput, String expectedReport) {
        assertEquals("Command exited with wrong code.\nComplete result:\n\n" + this, expectedExitCode, exitCode);
        assertNotNull("No output found", output);
        if (expectedOutput != null && !expectedOutput.isEmpty()) {
            if (!output.contains(expectedOutput)) {
                fail("Expected output '" + expectedOutput + "' not present.\nComplete result:\n\n" + this);
            }
        } else {
            assertTrue("The output should have been empty.\nComplete result:\n\n" + this, output.isEmpty());
        }
        if (expectedReport != null && !expectedReport.isEmpty()) {
            assertTrue("Expected report '" + expectedReport + "'.\nComplete result:\n\n" + this,
                    report.contains(expectedReport));
        }
    }

    /**
     * Asserts that the given error message is not in the error output.
     * @param errorMessage the error message to search for
     */
    public void assertNoError(String errorMessage) {
        assertFalse("Found error message: " + errorMessage + ".\nComplete result:\n\n" + this,
                errorOutput.contains(errorMessage));
    }

    /**
     * Asserts that the given error message is not in the report.
     * @param errorMessage the error message to search for
     */
    public void assertNoErrorInReport(String errorMessage) {
        assertFalse("Found error message in report: " + errorMessage + ".\nComplete result:\n\n" + this,
                report.contains(errorMessage));
    }

    static class Builder {
        private int exitCode;
        private String output;
        private String errorOutput;
        private String report;

        Builder withExitCode(int exitCode) {
            this.exitCode = exitCode;
            return this;
        }

        Builder withOutput(String output) {
            this.output = output;
            return this;
        }

        Builder withErrorOutput(String errorOutput) {
            this.errorOutput = errorOutput;
            return this;
        }

        Builder withReport(String report) {
            this.report = report;
            return this;
        }

        ExecutionResult build() {
            return new ExecutionResult(exitCode, output, errorOutput, report);
        }
    }
}
