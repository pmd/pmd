package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;

/**
 */
public abstract class OnTheFlyRenderer extends AbstractRenderer {

    protected List<Report.ProcessingError> errors = new LinkedList<Report.ProcessingError>();

    protected List<Report.SuppressedViolation> suppressed = new LinkedList<Report.SuppressedViolation>();

    /**
     * Method render.
     * @param writer Writer
     * @param report Report
     * @throws IOException
     * @see net.sourceforge.pmd.renderers.Renderer#render(Writer, Report)
     */
    public void render(Writer writer, Report report) throws IOException {
        setWriter(writer);
        start();
        renderFileReport(report);
        end();
    }

    /**
     * Method renderFileReport.
     * @param report Report
     * @throws IOException
     * @see net.sourceforge.pmd.renderers.Renderer#renderFileReport(Report)
     */
    public void renderFileReport(Report report) throws IOException {
        Iterator<RuleViolation> violations = report.iterator();
        if (violations.hasNext()) {
            renderFileViolations(violations);
            getWriter().flush();
        }

        /*
         * errors and suppressed violations are inserted in lists and will be
         * processed when all files have been analyzed, i.e. in the end() method.
         */
        for (Iterator<Report.ProcessingError> i = report.errors(); i.hasNext();) {
            errors.add(i.next());
        }

        if (showSuppressedViolations) {
            suppressed.addAll(report.getSuppressedRuleViolations());
        }
    }

    /**
     * Method start.
     * @throws IOException
     * @see net.sourceforge.pmd.renderers.Renderer#start()
     */
    public abstract void start() throws IOException;

    /**
     * Method renderFileViolations.
     * @param violations Iterator<RuleViolation>
     * @throws IOException
     */
    public abstract void renderFileViolations(Iterator<RuleViolation> violations) throws IOException;

    /**
     * Method end.
     * @throws IOException
     * @see net.sourceforge.pmd.renderers.Renderer#end()
     */
    public abstract void end() throws IOException;

}
