/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:12:07 PM
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.util.StringUtil;

import java.util.Iterator;

public class HTMLRenderer implements Renderer {

    protected String EOL = System.getProperty("line.separator", "\n");

    public String render(Report report) {
        StringBuffer buf = new StringBuffer("<html><head><title>PMD</title></head><body>" + EOL + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + EOL);
        boolean colorize = true;
        int violationCount = 1;
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
                colorize = false;
            } else {
                colorize = true;
            }
            buf.append("> " + EOL);
            buf.append("<td align=\"center\">" + violationCount + "</td>" + EOL);
            buf.append("<td width=\"*%\">" + rv.getFilename() + "</td>" + EOL);
            buf.append("<td align=\"center\" width=\"5%\">" + Integer.toString(rv.getLine()) + "</td>" + EOL);

            String d = rv.getDescription();
            d = StringUtil.replaceString(d, '&', "&amp;");
            d = StringUtil.replaceString(d, '<', "&lt;");
            d = StringUtil.replaceString(d, '>', "&gt;");
            buf.append("<td width=\"*\">" + d + "</td>" + EOL);

            buf.append("</tr>" + EOL);

            violationCount++;
        }
        buf.append("</table></body></html>");
        return buf.toString();
    }
}
