/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.internal;

import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.ListenerInitializer;

import me.tongfei.progressbar.PmdProgressBarFriend;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

/**
 * Collects runtime analysis statistics and displays them live on command line output.
 * Toggled off through --no-progress command line argument.
 */
public final class ProgressBarListener implements GlobalAnalysisListener {
    private ProgressBar progressBar;
    private final AtomicInteger numErrors = new AtomicInteger(0);
    private final AtomicInteger numViolations = new AtomicInteger(0);

    @Override
    public ListenerInitializer initializer() {
        return new ListenerInitializer() {
            @Override
            public void setNumberOfFilesToAnalyze(int totalFiles) {
                // We need to delay initialization until we know how many files there are to avoid a first bogus render
                progressBar = new ProgressBarBuilder()
                        .setTaskName("Processing files")
                        .setStyle(ProgressBarStyle.ASCII)
                        .hideEta()
                        .continuousUpdate()
                        .setInitialMax(totalFiles)
                        .setConsumer(PmdProgressBarFriend.createConsoleConsumer(System.out))
                        .clearDisplayOnFinish()
                        .build();
                progressBar.setExtraMessage(extraMessage());
            }
        };
    }

    /**
     * Updates progress bar string and forces it to be output regardless of its update interval.
     */
    private void refreshProgressBar() {
        progressBar.setExtraMessage(extraMessage());
        progressBar.refresh();
    }

    private String extraMessage() {
        return String.format("Violations:%d, Errors:%d", numViolations.get(), numErrors.get());
    }

    @Override
    public FileAnalysisListener startFileAnalysis(TextFile file) {
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
        progressBar.close();
    }
}
