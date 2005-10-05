/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TextRenderer implements Renderer {

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
            buf.append("\t" + Integer.toString(rv.getNode().getBeginLine()));
            buf.append("\t" + rv.getDescription());
        }

        for (Iterator i = report.errors(); i.hasNext();) {
            Report.ProcessingError error = (Report.ProcessingError) i.next();
            buf.append(PMD.EOL + error.getFile());
            buf.append("\t-");
            buf.append("\t" + error.getMsg());
        }

        addSuppressed(report, buf);
        return buf.toString();
    }

    private void addSuppressed(Report report, StringBuffer buf) {
        Map suppressed = new HashMap();
        for (Iterator i = report.getSuppressedRuleViolations().iterator(); i.hasNext();) {
            RuleViolation excluded = (RuleViolation) i.next();
            if (!suppressed.containsKey(excluded.getFilename())) {
                suppressed.put(excluded.getFilename(), new Integer(0));
            }
            int newValue = ((Integer)suppressed.get(excluded.getFilename())).intValue();
            suppressed.put(excluded.getFilename(), new Integer(++newValue));
        }
        Set keys = suppressed.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();){
            String filename = (String)i.next();
            int count = ((Integer)suppressed.get(filename)).intValue();
            buf.append(PMD.EOL + "Suppressed " + count + " rule violation" + (count == 1 ? "" : "s") + " in " + filename);
        }
    }
}
