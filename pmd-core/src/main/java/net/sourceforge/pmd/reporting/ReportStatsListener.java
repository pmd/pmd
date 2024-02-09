/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.util.BaseResultProducingCloseable;

/**
 * Collects summarized info about a PMD run.
 *
 * @author Clément Fournier
 */
public final class ReportStatsListener extends BaseResultProducingCloseable<ReportStats> implements GlobalAnalysisListener {

    private final AtomicInteger numErrors = new AtomicInteger(0);
    private final AtomicInteger numViolations = new AtomicInteger(0);

    @Override
    public FileAnalysisListener startFileAnalysis(TextFile file) {
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


}
