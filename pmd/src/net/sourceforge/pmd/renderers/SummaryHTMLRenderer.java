package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;

import java.util.Iterator;
import java.util.Map;

public class SummaryHTMLRenderer implements Renderer {
    public String render(Report report) {
        StringBuffer buf = new StringBuffer("<html><head><title>PMD</title></head><body>" + PMD.EOL);
        buf.append(renderSummary(report).toString());
        buf.append("<h2><center>Detail</h2></center>");
        buf.append("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL);
        buf.append(new HTMLRenderer().renderBody(report));
        buf.append("</table></body></html>");
        return buf.toString();
    }

    public String renderSummary(Report report) {
        StringBuffer buf = new StringBuffer();
        buf.append("<h2><center>Summary</h2></center>");
        buf.append("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">");
        buf.append("<th>Rule name</th>");
        buf.append("<th>Number of violations</th>");
        Map summary = report.getSummary();
        for (Iterator i = summary.keySet().iterator(); i.hasNext();) {
            String ruleName = (String)i.next();
            buf.append("<tr>");
            buf.append("<td>" + ruleName + "</td>");
            buf.append("<td align=center>" + String.valueOf(((Integer)summary.get(ruleName)).intValue()) + "</td>");
            buf.append("</tr>");
        }
        buf.append("</table>");
        return buf.toString();
    }
}
