/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:12:07 PM
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;

import java.util.Iterator;

public class HTMLRenderer implements Renderer {

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    public String render(Report report) {
        StringBuffer buf = new StringBuffer("<html><head><title>PMD</title></head><body>" + EOL+ "<table align=center><tr>" + EOL+ "<th>File</th><th>Line</th><th>Problem</th></tr>" + EOL);
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append("<tr>" + EOL+ "<td width=\"*%\">" + rv.getFilename() + "</td>" + EOL);
            buf.append("<td align=center width=\"5%\">" + Integer.toString(rv.getLine()) + "</td>" + EOL);
            buf.append("<td width=\"*\">" + rv.getDescription() + "</td>" + EOL);
            buf.append("</tr>" + EOL);
        }
        buf.append("</table></body></html>");
        return buf.toString();
    }
}
