/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;

public class EmacsRenderer implements Renderer {

    protected String EOL = System.getProperty("line.separator", "\n");

    public String render(Report report) {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(EOL).append(rv.getFilename());
            buf.append(":").append(Integer.toString(rv.getLine()));
            buf.append(": ").append(rv.getDescription());
        }
        return buf.toString();
    }
}
