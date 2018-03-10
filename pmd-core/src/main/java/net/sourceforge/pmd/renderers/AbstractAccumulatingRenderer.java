/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Abstract base class for {@link Renderer} implementations which only produce
 * output once all source files are processed. Such {@link Renderer}s use
 * working memory proportional to the number of violations found, which can be
 * quite large in some scenarios. Consider using
 * {@link AbstractIncrementingRenderer} which can use significantly less memory.
 *
 * Subclasses should implement the {@link #end()} method to output the
 * {@link #report}.
 *
 * @see AbstractIncrementingRenderer
 */
public abstract class AbstractAccumulatingRenderer extends AbstractRenderer {

    /**
     * The accumulated Report.
     */
    protected Report report;

    public AbstractAccumulatingRenderer(String name, String description) {
        super(name, description);
    }

    @Override
    public void start() throws IOException {
        report = new Report();
    }

    @Override
    public void startFileAnalysis(DataSource dataSource) {
        // does nothing - override if necessary
    }

    @Override
    public void renderFileReport(Report report) throws IOException {
        this.report.merge(report);
    }

    /**
     * Subclasses should output the {@link #report}.
     *
     * {@inheritDoc}
     */
    @Override
    public abstract void end() throws IOException;
}
