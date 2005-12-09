/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;

public class TextRenderer extends AbstractRenderer implements Renderer {

    public String render(Report report) {
        StringBuffer buf = new StringBuffer();

        if (report.isEmpty()) {
            buf.append("No problems found!");
            addSuppressed(report, buf);
            return buf.toString();
        }

        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(PMD.EOL + rv.getFilename());
            buf.append(":" + Integer.toString(rv.getNode().getBeginLine()));
            buf.append("\t" + rv.getDescription());
        }

        for (Iterator i = report.errors(); i.hasNext();) {
            Report.ProcessingError error = (Report.ProcessingError) i.next();
            buf.append(PMD.EOL + error.getFile());
            buf.append("\t-");
            buf.append("\t" + error.getMsg());
        }

        if (showSuppressedViolations) {
            addSuppressed(report, buf);
        }

        return buf.toString();
    }

    private void addSuppressed(Report report, StringBuffer buf) {
        for (Iterator i = report.getSuppressedRuleViolations().iterator(); i.hasNext();) {
            Report.SuppressedViolation excluded = (Report.SuppressedViolation) i.next();
            buf.append(PMD.EOL + excluded.getRuleViolation().getRule().getName() + " rule violation suppressed by "  + (excluded.suppressedByNOPMD() ? "//NOPMD" : "Annotation") + " in " + excluded.getRuleViolation().getFilename());
        }
    }
}
