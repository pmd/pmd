/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

/**
 * Summarized info about a report.
 *
 * @author Clément Fournier
 */
public final class ReportStats {

    private final int numErrors;
    private final int numViolations;

    ReportStats(int numErrors, int numViolations) {
        this.numErrors = numErrors;
        this.numViolations = numViolations;
    }

    public static ReportStats empty() {
        return new ReportStats(0, 0);
    }

    /**
     * Count of processing errors.
     */
    public int getNumErrors() {
        return numErrors;
    }

    /**
     * Count of found rule violations.
     */
    public int getNumViolations() {
        return numViolations;
    }

    @Override
    public String toString() {
        return "ReportStats{numErrors=" + numErrors + ", numViolations=" + numViolations + '}';
    }
}
