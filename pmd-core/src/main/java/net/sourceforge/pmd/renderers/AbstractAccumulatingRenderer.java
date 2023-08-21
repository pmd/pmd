/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Objects;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;

/**
 * Abstract base class for {@link Renderer} implementations which only produce
 * output once all source files are processed. Such {@link Renderer}s use
 * working memory proportional to the number of violations found, which can be
 * quite large in some scenarios. Consider using
 * {@link AbstractIncrementingRenderer} which can use significantly less memory.
 *
 * <p>Subclasses should only implement the {@link #end()} method to output the
 * complete {@link #report}.
 *
 * @see AbstractIncrementingRenderer
 */
public abstract class AbstractAccumulatingRenderer extends AbstractRenderer {


    public AbstractAccumulatingRenderer(String name, String description) {
        super(name, description);
    }

    @Override
    public void start() throws IOException {
        // do nothing
    }

    @Override
    public void end() throws IOException {
        // do nothing
    }

    @Override
    public void startFileAnalysis(TextFile dataSource) {
        Objects.requireNonNull(dataSource);
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated This is internal API. Do not override when extending {@link AbstractAccumulatingRenderer}.
     * In PMD7 this method will be made final.
     */
    @Override
    @InternalApi
    @Deprecated
    public final void renderFileReport(Report report) throws IOException {
        // do nothing, final because it will never be called by the listener
        Objects.requireNonNull(report);
    }

    /**
     * Output the report, called once at the end of the analysis.
     *
     * {@inheritDoc}
     */
    protected abstract void outputReport(Report report) throws IOException;


    @Override
    public GlobalAnalysisListener newListener() throws IOException {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
            this.start();
        }

        return new GlobalAnalysisListener() {
            final GlobalReportBuilderListener reportBuilder = new GlobalReportBuilderListener();

            @Override
            public FileAnalysisListener startFileAnalysis(TextFile file) {
                AbstractAccumulatingRenderer.this.startFileAnalysis(file);
                return reportBuilder.startFileAnalysis(file);
            }

            @Override
            public void onConfigError(ConfigurationError error) {
                reportBuilder.onConfigError(error);
            }

            @Override
            public void close() throws Exception {
                reportBuilder.close();
                try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
                    outputReport(reportBuilder.getResult());
                    end();
                    flush();
                }
            }
        };
    }
}
