/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

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
        definePropertyDescriptor(HTMLRenderer.HTML_EXTENSION);
    }

    @Override
    public String defaultFileExtension() {
        return "html";
    }

    @Override
    public void outputReport(Report report) throws IOException {
        writer.println("<html><head><title>PMD</title></head><body>");
        renderSummary(report);
        writer.write("<center><h2>Detail</h2></center>");
        writer.println("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>");

        HTMLRenderer htmlRenderer = new HTMLRenderer();
        htmlRenderer.setProperty(HTMLRenderer.LINK_PREFIX, getProperty(HTMLRenderer.LINK_PREFIX));
        htmlRenderer.setProperty(HTMLRenderer.LINE_PREFIX, getProperty(HTMLRenderer.LINE_PREFIX));
        htmlRenderer.setProperty(HTMLRenderer.HTML_EXTENSION, getProperty(HTMLRenderer.HTML_EXTENSION));
        htmlRenderer.setShowSuppressedViolations(showSuppressedViolations);
        htmlRenderer.renderBody(writer, report);

        writer.println("</tr></table></body></html>");
    }

    /**
     * Write a Summary HTML table.
     */
    private void renderSummary(Report report) throws IOException {
        writer.println("<center><h2>Summary</h2></center>");
        writer.println("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">");
        writer.println("<tr><th>Rule name</th><th>Number of violations</th></tr>");
        Map<String, MutableInt> summary = getSummary(report);
        for (Entry<String, MutableInt> entry : summary.entrySet()) {
            String ruleName = entry.getKey();
            writer.write("<tr><td>");
            writer.write(ruleName);
            writer.write("</td><td align=center>");
            writer.write(String.valueOf(entry.getValue().intValue()));
            writer.println("</td></tr>");
        }
        writer.println("</table>");
    }

    private static Map<String, MutableInt> getSummary(Report report) {
        Map<String, MutableInt> summary = new LinkedHashMap<>();
        for (RuleViolation rv : report.getViolations()) {
            String name = rv.getRule().getName();
            MutableInt count = summary.get(name);
            if (count == null) {
                count = new MutableInt(0);
                summary.put(name, count);
            }
            count.increment();
        }
        return summary;
    }

}
