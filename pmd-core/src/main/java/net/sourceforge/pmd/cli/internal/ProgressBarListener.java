/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.internal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.datasource.DataSource;

import me.tongfei.progressbar.DelegatingProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

/**
 * Collects runtime analysis statistics and displays them live on command line output.
 * Toggled off through --no-progress command line argument.
 */
public final class ProgressBarListener implements GlobalAnalysisListener {
    private final ProgressBar progressBar;
    private final AtomicInteger numErrors = new AtomicInteger(0);
    private final AtomicInteger numViolations = new AtomicInteger(0);
    
    public ProgressBarListener(int totalFiles, Consumer<String> loggingFunction) {
        progressBar = new ProgressBarBuilder()
                .setTaskName("Processing files")
                .setInitialMax(totalFiles)
                .setStyle(ProgressBarStyle.ASCII)
                .continuousUpdate()
                .setConsumer(new DelegatingProgressBarConsumer(loggingFunction))
                .build();
        progressBar.setExtraMessage(extraMessage() + "\r");
    }


    /**
     * Updates progress bar string and forces it to be output regardless of its update interval.
     */
    private void refreshProgressBar() {
        // Use trailing carriage return to interleave with other output
        if (progressBar.getCurrent() != progressBar.getMax()) {
            progressBar.setExtraMessage(extraMessage() + "\r");
        } else {
            // Don't include trailing carriage return on last draw
            progressBar.setExtraMessage(extraMessage() + System.lineSeparator());
        }
        progressBar.refresh();
    }

    private String extraMessage() {
        return String.format("Violations:%d, Errors:%d", numViolations.get(), numErrors.get());
    }

    @Override
    public FileAnalysisListener startFileAnalysis(DataSource file) {
        // Refresh progress on file analysis start
        refreshProgressBar();

        return new FileAnalysisListener() {
            @Override
            public void onRuleViolation(RuleViolation violation) {
                ProgressBarListener.this.numViolations.addAndGet(1);
            }

            @Override
            public void onSuppressedRuleViolation(Report.SuppressedViolation violation) {
                /*Not handled*/
            }

            @Override
            public void onError(Report.ProcessingError error) {
                ProgressBarListener.this.numErrors.addAndGet(1);
            }

            @Override
            public void close() {
                // Refresh progress bar on file analysis end (or file was in cache)
                progressBar.step();
                refreshProgressBar();
            }
        };
    }

    @Override
    public void close() throws Exception {
        /*ProgressBar auto-closed*/
    }
}
