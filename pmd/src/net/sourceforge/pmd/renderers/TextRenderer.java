/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.IRuleViolation;

import java.util.Iterator;

public class TextRenderer extends AbstractRenderer implements Renderer {

    public String render(Report report) {
        StringBuffer buf = new StringBuffer();

        if (report.isEmpty()) {
            buf.append("No problems found!");
            if (showSuppressedViolations) {
                addSuppressed(report, buf);
            }
            return buf.toString();
        }
        Iterator i;
        
        for (i = report.iterator(); i.hasNext();) {
            IRuleViolation rv = (IRuleViolation) i.next();
            buf.append(PMD.EOL).append(rv.getFilename());
            buf.append(':').append(Integer.toString(rv.getBeginLine()));
            buf.append('\t').append(rv.getDescription());
        }

        for (i = report.errors(); i.hasNext();) {
            Report.ProcessingError error = (Report.ProcessingError) i.next();
            buf.append(PMD.EOL).append(error.getFile());
            buf.append("\t-\t").append(error.getMsg());
        }

        if (showSuppressedViolations) {
            addSuppressed(report, buf);
        }

        return buf.toString();
    }

    private void addSuppressed(Report report, StringBuffer buf) {
    	
    	Report.SuppressedViolation excluded;
    	
        for (Iterator i = report.getSuppressedRuleViolations().iterator(); i.hasNext();) {
            excluded = (Report.SuppressedViolation) i.next();
            buf.append(PMD.EOL);
            buf.append(excluded.getRuleViolation().getRule().getName());
            buf.append(" rule violation suppressed by ");
            buf.append(excluded.suppressedByNOPMD() ? "//NOPMD" : "Annotation");
            buf.append(" in ").append(excluded.getRuleViolation().getFilename());
        }
    }
}
