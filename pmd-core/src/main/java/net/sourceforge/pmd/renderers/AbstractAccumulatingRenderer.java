/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Objects;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.GlobalReportBuilderListener;

/**
 * Abstract base class for {@link Renderer} implementations which only produce
 * output once all source files are processed. Such {@link Renderer}s use
 * working memory proportional to the number of violations found, which can be
 * quite large in some scenarios. Consider using
 * {@link AbstractIncrementingRenderer} which can use significantly less memory.
 *
 * <p>Subclasses should only implement the {@link #outputReport(Report)} method to output the
 * complete {@link Report} in the end.
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
     * @implNote The implementation in this class does nothing. All the reported violations and
     * errors are accumulated and can be rendered once with {@link #outputReport(Report)} in the
     * end. Subclasses of {@link AbstractAccumulatingRenderer} cannot override this method
     * anymore.
     */
    @Override
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
