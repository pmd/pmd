/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;

/**
 * Renderer to a summarized HTML format.
 */
public class SummaryHTMLRenderer extends AbstractRenderer {

    public static final String NAME = "summaryhtml";

    public static final String LINK_PREFIX = HTMLRenderer.LINK_PREFIX;
    public static final String LINE_PREFIX = HTMLRenderer.LINE_PREFIX;

    public SummaryHTMLRenderer(Properties properties) {
	super(NAME, "Summary HTML format.", properties);

	// These properties are defined here, but used by the HTMLRenderer
	super.defineProperty(HTMLRenderer.LINK_PREFIX, "Path to HTML source.");
	super.defineProperty(HTMLRenderer.LINE_PREFIX, "Prefix for line number anchor in the source file.");
    }

    /**
     * {@inheritDoc}
     */
    public void render(Writer writer, Report report) throws IOException {
	writer.write("<html><head><title>PMD</title></head><body>" + PMD.EOL);
	renderSummary(writer, report);
	writer.write("<h2><center>Detail</h2></center>");
	writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL);
	new HTMLRenderer(properties).renderBody(writer, report);
	writer.write("</table></body></html>");
    }

    /**
     * Write a Summary HTML table.
     * 
     * @param writer Writer to write to.
     * @param report Report to write.
     * @throws IOException
     */
    public void renderSummary(Writer writer, Report report) throws IOException {
	StringBuffer buf = new StringBuffer(500);
	buf.append("<h2><center>Summary</h2></center>");
	buf.append("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">");
	buf.append("<th>Rule name</th>");
	buf.append("<th>Number of violations</th>");
	writer.write(buf.toString());
	Map<String, Integer> summary = report.getSummary();
	for (Map.Entry<String, Integer> entry : summary.entrySet()) {
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
