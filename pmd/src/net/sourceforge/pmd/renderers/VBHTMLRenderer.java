package net.sourceforge.pmd.renderers;

import java.util.Iterator;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.renderers.Renderer;

/**
 * @version $Revision$ $Date$
 * @author Vladimir
 */
public class VBHTMLRenderer implements Renderer {

    public String render(Report report) {
        if (report.isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        String filename = null;
        String lineSep = CPD.EOL;

        sb.append(header());

        Iterator iter = report.iterator();
        boolean colorize = false;
        while (iter.hasNext()) {
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
        iter = report.errors();
        if (iter.hasNext()) {
            sb.append("<table border=\"0\" width=\"80%\">");
            sb.append("<tr id=TableHeader><td><font class=title>&nbsp;Problems found</font></td></tr>");
            colorize = false;
            while(iter.hasNext()) {
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
        sb.append("<!--" + CPD.EOL);
        sb.append("body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }" + CPD.EOL);
        sb.append(".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }" + CPD.EOL);
        sb.append(".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }" + CPD.EOL);
        sb.append("#TableHeader { background-color: #003366; }" + CPD.EOL);
        sb.append("#RowColor1 { background-color: #eeeeee; }" + CPD.EOL);
        sb.append("#RowColor2 { background-color: white; }" + CPD.EOL);
        sb.append("-->");
        sb.append("</style>");
        sb.append("<body><center>");
        return sb.toString();
    }

    private String footer() {
        return "</center></body></html>";
    }

}