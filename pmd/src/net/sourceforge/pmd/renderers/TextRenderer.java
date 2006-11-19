/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class TextRenderer extends AbstractRenderer {

    public void render(Writer writer, Report report) throws IOException {
        StringBuffer buf = new StringBuffer();

        if (report.isEmpty()) {
            buf.append("No problems found!");
            if (showSuppressedViolations) {
                addSuppressed(report, buf);
            }
            writer.write(buf.toString());
            return;
        }
        Iterator i;
        
        for (i = report.iterator(); i.hasNext();) {
            buf.setLength(0);
            IRuleViolation rv = (IRuleViolation) i.next();
            buf.append(PMD.EOL).append(rv.getFilename());
            buf.append(':').append(Integer.toString(rv.getBeginLine()));
            buf.append('\t').append(rv.getDescription());
            writer.write(buf.toString());
        }

        for (i = report.errors(); i.hasNext();) {
            buf.setLength(0);
            Report.ProcessingError error = (Report.ProcessingError) i.next();
            buf.append(PMD.EOL).append(error.getFile());
            buf.append("\t-\t").append(error.getMsg());
            writer.write(buf.toString());
        }

        if (showSuppressedViolations) {
            buf.setLength(0);
            addSuppressed(report, buf);
            writer.write(buf.toString());
        }
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
