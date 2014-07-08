/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Map;

import net.sourceforge.pmd.PMD;

/**
 * Renderer to a summarized HTML format.
 */
public class SummaryHTMLRenderer extends AbstractAccumulatingRenderer {

    public static final String NAME = "summaryhtml";

    public SummaryHTMLRenderer() {
	super(NAME, "Summary HTML format.");

	// Note: we define the same properties as HTML Renderer
	// we have to copy the values later from this renderer to the HTML Renderer
	definePropertyDescriptor(HTMLRenderer.LINK_PREFIX);
	definePropertyDescriptor(HTMLRenderer.LINE_PREFIX);
    }

    public String defaultFileExtension() { return "html"; }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void end() throws IOException {
	writer.write("<html><head><title>PMD</title></head><body>" + PMD.EOL);
	renderSummary();
	writer.write("<center><h2>Detail</h2></center>");
	writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL);

	HTMLRenderer htmlRenderer = new HTMLRenderer();
	htmlRenderer.setProperty(HTMLRenderer.LINK_PREFIX, getProperty(HTMLRenderer.LINK_PREFIX));
	htmlRenderer.setProperty(HTMLRenderer.LINE_PREFIX, getProperty(HTMLRenderer.LINE_PREFIX));
	htmlRenderer.setShowSuppressedViolations(showSuppressedViolations);
	htmlRenderer.renderBody(writer, report);

	writer.write("</tr></table></body></html>" + PMD.EOL);
    }

    /**
     * Write a Summary HTML table.
     *
     * @throws IOException
     */
    public void renderSummary() throws IOException {
    	StringBuilder buf = new StringBuilder(500);
		buf.append("<center><h2>Summary</h2></center>");
		buf.append("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>");
		buf.append("<th>Rule name</th>");
		buf.append("<th>Number of violations</th></tr>");
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
