/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:12:07 PM
 */
package net.sourceforge.pmd.reports;

import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;

public class HTMLReport extends AbstractReport {

    public String render() {
        StringBuffer buf = new StringBuffer("<html><head><title>PMD</title></head><body>" + System.getProperty("line.separator")+ "<table><tr>" + System.getProperty("line.separator")+ "<th>File</th><th>Line</th><th>Problem</th></tr>" + System.getProperty("line.separator"));
        for (Iterator i = violations.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append("<tr>" + System.getProperty("line.separator")+ "<td>" + rv.getFilename() + "</td>" + System.getProperty("line.separator"));
            buf.append("<td>" + Integer.toString(rv.getLine()) + "</td>" + System.getProperty("line.separator"));
            buf.append("<td>" + rv.getDescription() + "</td>" + System.getProperty("line.separator"));
            buf.append("</tr>" + System.getProperty("line.separator"));
        }
        buf.append("</table></body></html>");
        return buf.toString();
    }
}
