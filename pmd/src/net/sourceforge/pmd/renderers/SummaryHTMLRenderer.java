package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class SummaryHTMLRenderer extends AbstractRenderer {
    private String linePrefix,linkPrefix;
    public SummaryHTMLRenderer(String linkPrefix, String linePrefix) {
        this.linePrefix = linePrefix;
        this.linkPrefix = linkPrefix;
    }
    
    public SummaryHTMLRenderer(){
        this(null,null);
    }

    public void render(Writer writer, Report report) throws IOException {
        writer.write("<html><head><title>PMD</title></head><body>" + PMD.EOL);
        renderSummary(writer, report);
        writer.write("<h2><center>Detail</h2></center>");
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL);
        new HTMLRenderer(linkPrefix,linePrefix).renderBody(writer, report);
        writer.write("</table></body></html>");
    }

    public void renderSummary(Writer writer, Report report) throws IOException {
        StringBuffer buf = new StringBuffer();
        buf.append("<h2><center>Summary</h2></center>");
        buf.append("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">");
        buf.append("<th>Rule name</th>");
        buf.append("<th>Number of violations</th>");
        writer.write(buf.toString());
        Map<String, Integer> summary = report.getSummary();
        for (Map.Entry<String, Integer> entry: summary.entrySet()) {
            String ruleName = entry.getKey();
            buf.setLength(0);
            buf.append("<tr>");
            buf.append("<td>" + ruleName + "</td>");
            buf.append("<td align=center>" + entry.getValue().intValue() + "</td>");
            buf.append("</tr>");
            writer.write(buf.toString());
        }
        writer.write("</table>");
    }
}
