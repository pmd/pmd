/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.document.TextFile;

/**
 * Abstract base class for {@link Renderer} implementations which can produce
 * output incrementally for {@link RuleViolation}s as source files are
 * processed. Such {@link Renderer}s are able to produce large reports with
 * significantly less working memory at any given time. Variations in the
 * delivery of source file reports are reflected in the output of the
 * {@link Renderer}, so report output can be different between runs.
 *
 * Only processing errors and suppressed violations are accumulated across all
 * files. These are intended to be processed in the {@link #end()} method.
 */
public abstract class AbstractIncrementingRenderer extends AbstractRenderer {

    /**
     * Accumulated processing errors.
     */
    protected List<Report.ProcessingError> errors = new LinkedList<>();

    /**
     * Accumulated configuration errors.
     */
    protected List<Report.ConfigurationError> configErrors = new LinkedList<>();

    /**
     * Accumulated suppressed violations.
     */
    protected List<Report.SuppressedViolation> suppressed = new LinkedList<>();

    public AbstractIncrementingRenderer(String name, String description) {
        super(name, description);
    }

    @Override
    public void start() throws IOException {
        // does nothing - override if necessary
    }

    @Override
    public void startFileAnalysis(TextFile dataSource) {
        // does nothing - override if necessary
    }

    @Override
    public void renderFileReport(Report report) throws IOException {
        Iterator<RuleViolation> violations = report.getViolations().iterator();
        if (violations.hasNext()) {
            renderFileViolations(violations);
            getWriter().flush();
        }

        errors.addAll(report.getProcessingErrors());

        configErrors.addAll(report.getConfigurationErrors());

        if (showSuppressedViolations) {
            suppressed.addAll(report.getSuppressedViolations());
        }
    }

    /**
     * Render a series of {@link RuleViolation}s.
     *
     * @param violations
     *            The iterator of violations to render.
     * @throws IOException
     */
    public abstract void renderFileViolations(Iterator<RuleViolation> violations) throws IOException;

    @Override
    public void end() throws IOException {
        // does nothing - override if necessary
    }
}
