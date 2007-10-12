package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.Report;

public abstract class OnTheFlyRenderer extends AbstractRenderer {

    protected List<Report.ProcessingError> errors = new LinkedList<Report.ProcessingError>();

    protected List<Report.SuppressedViolation> suppressed = new LinkedList<Report.SuppressedViolation>();

    public void render(Writer writer, Report report) throws IOException {
        setWriter(writer);
        start();
        renderFileReport(report);
        end();
    }

    public void renderFileReport(Report report) throws IOException {
        Iterator<IRuleViolation> violations = report.iterator();
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

    public abstract void start() throws IOException;

    public abstract void renderFileViolations(Iterator<IRuleViolation> violations) throws IOException;

    public abstract void end() throws IOException;

}
