/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * Renderer to a summarized HTML format.
 */
public class SummaryHTMLRenderer extends AbstractAccumulatingRenderer {

    public static final String NAME = "summaryhtml";

    private static final PropertyDescriptor<Optional<String>> LINE_PREFIX =
        PropertyFactory.stringProperty("linePrefix")
                       .desc("Prefix for line number anchor in the source file.")
                       .toOptional("<none>")
                       .defaultValue(Optional.empty())
                       .build();

    private static final PropertyDescriptor<String> LINK_PREFIX =
        PropertyFactory.stringProperty("linkPrefix").desc("Path to HTML source.").defaultValue("").build();

    private static final PropertyDescriptor<Boolean> HTML_EXTENSION =
        PropertyFactory.booleanProperty("htmlExtension")
                       .desc("Replace file extension with .html for the links.")
                       .defaultValue(false)
                       .build();

    public SummaryHTMLRenderer() {
        super(NAME, "Summary HTML format.");

        definePropertyDescriptor(LINK_PREFIX);
        definePropertyDescriptor(LINE_PREFIX);
        definePropertyDescriptor(HTML_EXTENSION);
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

        HTMLBodyWriter body = new HTMLBodyWriter(
                writer,
                getProperty(LINK_PREFIX),
                getProperty(LINE_PREFIX).orElse(null),
                getProperty(HTML_EXTENSION),
                this::determineFileName
        );
        body.renderHeader();
        body.renderViolations(report.getViolations().iterator());
        body.renderFooter(
                report.getProcessingErrors(),
                report.getConfigurationErrors(),
                report.getSuppressedViolations(),
                showSuppressedViolations
        );

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
            writer.write(StringEscapeUtils.escapeHtml4(ruleName));
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
