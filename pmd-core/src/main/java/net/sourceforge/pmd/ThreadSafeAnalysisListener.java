/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.Report.ReportBuilderListener;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.renderers.Renderer;

/**
 * A handler for analysis events. This must be thread safe.
 */
public interface ThreadSafeAnalysisListener {

    /**
     * Handle a new violation (not suppressed).
     */
    default void onRuleViolation(RuleViolation violation) {

    }

    /**
     * Handle a new suppressed violation.
     */
    default void onSuppressedRuleViolation(SuppressedViolation violation) {

    }


    /**
     * Handle an error that occurred while processing a file.
     */
    default void onError(FileAnalysisException exception) {

    }


    /**
     * All files have been processed.
     */
    default void finish() throws Exception {

    }


    static ThreadSafeAnalysisListener noop() {
        return new ThreadSafeAnalysisListener() {};
    }


    static ThreadSafeAnalysisListener forReporter(Renderer renderer) {
        ReportBuilderListener reportBuilder = new ReportBuilderListener();
        return new ThreadSafeAnalysisListener() {

            @Override
            public void onRuleViolation(RuleViolation violation) {
                reportBuilder.onRuleViolation(violation);
            }

            @Override
            public void onSuppressedRuleViolation(SuppressedViolation violation) {
                reportBuilder.onSuppressedRuleViolation(violation);
            }

            @Override
            public void onError(FileAnalysisException exception) {
                reportBuilder.onError(exception);
            }

            @Override
            public void finish() throws Exception {
                reportBuilder.finish();
                renderer.renderFileReport(reportBuilder.getReport());
                renderer.end();
                renderer.flush();
            }
        };
    }

}
