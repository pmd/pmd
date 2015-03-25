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
        // we have to copy the values later from this renderer to the HTML
        // Renderer
        definePropertyDescriptor(HTMLRenderer.LINK_PREFIX);
        definePropertyDescriptor(HTMLRenderer.LINE_PREFIX);
    }

    public String defaultFileExtension() {
        return "html";
    }

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
        writer.write("<center><h2>Summary</h2></center>" + PMD.EOL);
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL);
        writer.write("<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL);
        Map<String, Integer> summary = report.getSummary();
        for (Map.Entry<String, Integer> entry : summary.entrySet()) {
            String ruleName = entry.getKey();
            writer.write("<tr><td>");
            writer.write(ruleName);
            writer.write("</td><td align=center>");
            writer.write(String.valueOf(entry.getValue().intValue()));
            writer.write("</td></tr>" + PMD.EOL);
        }
        writer.write("</table>" + PMD.EOL);
    }
}
