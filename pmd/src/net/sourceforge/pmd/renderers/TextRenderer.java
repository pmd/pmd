/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;

public class TextRenderer implements Renderer {

    public String render(Report report) {
        if (report.isEmpty()) {
            return "No problems found!";
        }
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(PMD.EOL + rv.getFilename());
            buf.append("\t" + Integer.toString(rv.getLine()));
            buf.append("\t" + rv.getDescription());
        }
        for (Iterator i = report.errors(); i.hasNext();) {
            Report.ProcessingError error = (Report.ProcessingError) i.next();
            buf.append(PMD.EOL + error.getFile());
            buf.append("\t-");
            buf.append("\t" + error.getMsg());
        }
        return buf.toString();
    }
}
