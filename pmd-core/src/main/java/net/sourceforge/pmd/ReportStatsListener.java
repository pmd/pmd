/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ReportStatsListener.ReportStats;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.BaseResultProducingCloseable;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Counts processing errors.
 *
 * @author Clément Fournier
 */
final class ReportStatsListener extends BaseResultProducingCloseable<ReportStats> implements GlobalAnalysisListener {

    private final AtomicInteger numErrors = new AtomicInteger(0);
    private final AtomicInteger numViolations = new AtomicInteger(0);

    @Override
    public FileAnalysisListener startFileAnalysis(DataSource file) {
        return new FileAnalysisListener() {
            // this object does not need thread-safety so we avoid using atomics,
            // except during the merge.
            private int numErrors = 0;
            private int numViolations = 0;

            @Override
            public void onRuleViolation(RuleViolation violation) {
                numViolations++;
            }

            @Override
            public void onError(ProcessingError error) {
                numErrors++;
            }

            @Override
            public void close() {
                if (numErrors > 0) {
                    ReportStatsListener.this.numErrors.addAndGet(this.numErrors);
                }
                if (numViolations > 0) {
                    ReportStatsListener.this.numViolations.addAndGet(this.numViolations);
                }
            }
        };
    }

    @Override
    protected ReportStats getResultImpl() {
        return new ReportStats(
            numErrors.get(),
            numViolations.get()
        );
    }


    static final class ReportStats {

        private final int numErrors;
        private final int numViolations;

        ReportStats(int numErrors, int numViolations) {
            this.numErrors = numErrors;
            this.numViolations = numViolations;
        }

        public static ReportStats empty() {
            return new ReportStats(0, 0);
        }

        public int getNumErrors() {
            return numErrors;
        }

        public int getNumViolations() {
            return numViolations;
        }

        @Override
        public String toString() {
            return "ReportStats{" +
                "numErrors=" + numErrors +
                ", numViolations=" + numViolations +
                '}';
        }
    }
}
