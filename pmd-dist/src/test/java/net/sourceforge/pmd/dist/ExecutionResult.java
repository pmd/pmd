/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dist;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hamcrest.Matcher;

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
        return "ExecutionResult:\n"
            + " exit code: " + exitCode + "\n"
            + " output:\n" + output + "\n"
            + " errorOutput:\n" + errorOutput + "\n"
            + " report:\n" + report + "\n";
    }

    public ExecutionResult assertExitCode(int expectedExitCode) {
        assertEquals(expectedExitCode, exitCode, "Command exited with wrong code.\nComplete result:\n\n" + this);
        return this;
    }

    public ExecutionResult assertReport(Matcher<String> reportMatcher) {
        assertThat("Report", report, reportMatcher);
        return this;
    }

    public ExecutionResult assertStdErr(Matcher<String> matcher) {
        assertThat("Standard error", errorOutput, matcher);
        return this;
    }

    public ExecutionResult assertStdOut(Matcher<String> matcher) {
        assertThat("Standard output", output, matcher);
        return this;
    }

    public String getOutput() {
        return output;
    }

    /**
     * Asserts that the given error message is not in the error output.
     *
     * @param errorMessage the error message to search for
     */
    public void assertNoError(String errorMessage) {
        assertStdErr(not(containsString(errorMessage)));
    }

    /**
     * Asserts that the given error message is not in the report.
     * @param errorMessage the error message to search for
     */
    public void assertNoErrorInReport(String errorMessage) {
        assertReport(not(containsString(errorMessage)));
    }

    public void assertErrorOutputContains(String message) {
        assertStdErr(containsString(message));
    }

    public void assertIdenticalResults(ExecutionResult other) {
        // Notice we don't check for error output, as log messages may differ due to cache
        assertEquals(exitCode, other.exitCode, "Exit codes differ");
        assertEquals(output, other.output, "Outputs differ");
        assertEquals(report, other.report, "Reports differ");
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
