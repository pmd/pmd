/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;

/**
 * @author Vladimir
 * @version $Revision$ $Date$
 */
public class VBHTMLRenderer implements Renderer {

    public String render(Report report) {
        if (report.isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer(header());
        String filename = null;
        String lineSep = PMD.EOL;

        boolean colorize = false;

        for (Iterator iter = report.iterator(); iter.hasNext();) {
            RuleViolation rv = (RuleViolation) iter.next();
            if (!rv.getFilename().equals(filename)) { // New File
                if (filename != null) {
                    sb.append("</table></br>");
                    colorize = false;
                }
                filename = rv.getFilename();
                sb.append("<table border=\"0\" width=\"80%\">");
                sb.append("<tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;").append(filename).append("</font></tr>");
                sb.append(lineSep);
            }

            if (colorize) {
                sb.append("<tr id=RowColor1>");
            } else {
                sb.append("<tr id=RowColor2>");
            }

            colorize = !colorize;
            sb.append("<td width=\"50\" align=\"right\"><font class=body>" + rv.getLine() + "&nbsp;&nbsp;&nbsp;</font></td>");
            sb.append("<td><font class=body>" + rv.getDescription() + "</font></td>");
            sb.append("</tr>");
            sb.append(lineSep);
        }
        if (filename != null) {
            sb.append("</table>");
        }
        sb.append("<br>");

        // output the problems
        Iterator iter = report.errors();
        if (iter.hasNext()) {
            sb.append("<table border=\"0\" width=\"80%\">");
            sb.append("<tr id=TableHeader><td><font class=title>&nbsp;Problems found</font></td></tr>");
            colorize = false;
            while (iter.hasNext()) {
                if (colorize) {
                    sb.append("<tr id=RowColor1>");
                } else {
                    sb.append("<tr id=RowColor2>");
                }
                colorize = !colorize;
                sb.append("<td><font class=body>").append(iter.next()).append("\"</font></td></tr>");
            }
            sb.append("</table>");
        }

        sb.append(footer());

        return sb.toString();
    }

    private String header() {
        StringBuffer sb = new StringBuffer();
        sb.append("<html><head><title>PMD</title></head>");
        sb.append("<style type=\"text/css\">");
        sb.append("<!--" + PMD.EOL);
        sb.append("body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }" + PMD.EOL);
        sb.append(".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }" + PMD.EOL);
        sb.append(".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }" + PMD.EOL);
        sb.append("#TableHeader { background-color: #003366; }" + PMD.EOL);
        sb.append("#RowColor1 { background-color: #eeeeee; }" + PMD.EOL);
        sb.append("#RowColor2 { background-color: white; }" + PMD.EOL);
        sb.append("-->");
        sb.append("</style>");
        sb.append("<body><center>");
        return sb.toString();
    }

    private String footer() {
        return "</center></body></html>";
    }

}