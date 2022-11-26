/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * Renderer to basic HTML format.
 *
 * FIXME: this class should just work with the XMLRenderer and then apply an
 * XSLT transformation + stylesheet. No need to hard-code HTML markup here.
 */
public class HTMLRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "html";

    // TODO use PropertyDescriptor<Optional<String>> : we need a "blank" default value
    public static final StringProperty LINE_PREFIX = new StringProperty("linePrefix",
                                                                        "Prefix for line number anchor in the source file.", null, 1);

    public static final PropertyDescriptor<String> LINK_PREFIX =
        PropertyFactory.stringProperty("linkPrefix").desc("Path to HTML source.").defaultValue("").build();

    public static final PropertyDescriptor<Boolean> HTML_EXTENSION =
        PropertyFactory.booleanProperty("htmlExtension")
                       .desc("Replace file extension with .html for the links.")
                       // default value is false - to have the old (pre 6.23.0) behavior, this needs to be set to true.
                       .defaultValue(false)
                       .build();

    private String linkPrefix;
    private String linePrefix;
    private boolean replaceHtmlExtension;

    boolean colorize = true;

    public HTMLRenderer() {
        super(NAME, "HTML format");

        definePropertyDescriptor(LINK_PREFIX);
        definePropertyDescriptor(LINE_PREFIX);
        definePropertyDescriptor(HTML_EXTENSION);
    }

    @Override
    public String defaultFileExtension() {
        return "html";
    }

    /**
     * Write the body of the main body of the HTML content.
     *
     * @param writer
     * @param report
     * @throws IOException
     */
    public void renderBody(Writer writer, Report report) throws IOException {
        linkPrefix = getProperty(LINK_PREFIX);
        linePrefix = getProperty(LINE_PREFIX);
        replaceHtmlExtension = getProperty(HTML_EXTENSION);

        writer.write("<center><h3>PMD report</h3></center>");
        writer.write("<center><h3>Problems found</h3></center>");
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL
                + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL);
        setWriter(writer);
        renderFileReport(report);
        writer.write("</table>");
        glomProcessingErrors(writer, errors);
        if (showSuppressedViolations) {
            glomSuppressions(writer, suppressed);
        }
        glomConfigurationErrors(writer, configErrors);
    }

    @Override
    public void start() throws IOException {
        linkPrefix = getProperty(LINK_PREFIX);
        linePrefix = getProperty(LINE_PREFIX);
        replaceHtmlExtension = getProperty(HTML_EXTENSION);

        writer.write("<html><head><title>PMD</title></head><body>" + PMD.EOL);
        writer.write("<center><h3>PMD report</h3></center>");
        writer.write("<center><h3>Problems found</h3></center>");
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL
                + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL);
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        glomRuleViolations(writer, violations);
    }

    @Override
    public void end() throws IOException {
        writer.write("</table>");
        glomProcessingErrors(writer, errors);
        if (showSuppressedViolations) {
            glomSuppressions(writer, suppressed);
        }
        glomConfigurationErrors(writer, configErrors);
        writer.write("</body></html>" + PMD.EOL);
    }

    private void glomRuleViolations(Writer writer, Iterator<RuleViolation> violations) throws IOException {
        int violationCount = 1;

        StringBuilder buf = new StringBuilder(500);

        while (violations.hasNext()) {
            RuleViolation rv = violations.next();
            buf.setLength(0);
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> ").append(PMD.EOL);
            buf.append("<td align=\"center\">").append(violationCount).append("</td>").append(PMD.EOL);
            buf.append("<td width=\"*%\">")
                .append(renderFileName(rv.getFilename(), rv.getBeginLine()))
                .append("</td>")
                .append(PMD.EOL);
            buf.append("<td align=\"center\" width=\"5%\">").append(rv.getBeginLine()).append("</td>").append(PMD.EOL);

            String d = StringEscapeUtils.escapeHtml4(rv.getDescription());

            String infoUrl = rv.getRule().getExternalInfoUrl();
            if (StringUtils.isNotBlank(infoUrl)) {
                d = "<a href=\"" + infoUrl + "\">" + d + "</a>";
            }
            buf.append("<td width=\"*\">")
               .append(d)
               .append("</td>")
               .append(PMD.EOL)
               .append("</tr>").append(PMD.EOL);
            writer.write(buf.toString());
            violationCount++;
        }
    }

    private String renderFileName(String filename, int beginLine) {
        return maybeWrap(StringEscapeUtils.escapeHtml4(determineFileName(filename)),
                linePrefix == null || beginLine < 0 ? "" : linePrefix + beginLine);
    }

    private String renderRuleName(Rule rule) {
        String name = rule.getName();
        String infoUrl = rule.getExternalInfoUrl();
        if (StringUtils.isNotBlank(infoUrl)) {
            return "<a href=\"" + infoUrl + "\">" + name + "</a>";
        }
        return name;
    }

    private void glomProcessingErrors(Writer writer, List<Report.ProcessingError> errors) throws IOException {

        if (errors.isEmpty()) {
            return;
        }

        writer.write("<hr/>");
        writer.write("<center><h3>Processing errors</h3></center>");
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL
                + "<th>File</th><th>Problem</th></tr>" + PMD.EOL);

        StringBuilder buf = new StringBuilder(500);
        boolean colorize = true;
        for (Report.ProcessingError pe : errors) {
            buf.setLength(0);
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> ").append(PMD.EOL);
            buf.append("<td>").append(renderFileName(pe.getFile(), -1)).append("</td>").append(PMD.EOL);
            buf.append("<td><pre>").append(pe.getDetail()).append("</pre></td>").append(PMD.EOL);
            buf.append("</tr>").append(PMD.EOL);
            writer.write(buf.toString());
        }
        writer.write("</table>");
    }

    private void glomSuppressions(Writer writer, List<Report.SuppressedViolation> suppressed) throws IOException {
        if (suppressed.isEmpty()) {
            return;
        }

        writer.write("<hr/>");
        writer.write("<center><h3>Suppressed warnings</h3></center>");
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL
                + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>" + PMD.EOL);

        StringBuilder buf = new StringBuilder(500);
        boolean colorize = true;
        for (Report.SuppressedViolation sv : suppressed) {
            buf.setLength(0);
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> ").append(PMD.EOL);
            RuleViolation rv = sv.getRuleViolation();
            buf.append("<td align=\"left\">").append(renderFileName(rv.getFilename(), rv.getBeginLine())).append("</td>").append(PMD.EOL);
            buf.append("<td align=\"center\">").append(rv.getBeginLine()).append("</td>").append(PMD.EOL);
            buf.append("<td align=\"center\">").append(renderRuleName(rv.getRule())).append("</td>").append(PMD.EOL);
            buf.append("<td align=\"center\">").append(sv.getSuppressor().getId()).append("</td>").append(PMD.EOL);
            buf.append("<td align=\"center\">").append(sv.getUserMessage() == null ? "" : sv.getUserMessage()).append("</td>").append(PMD.EOL);
            buf.append("</tr>").append(PMD.EOL);
            writer.write(buf.toString());
        }
        writer.write("</table>");
    }

    private void glomConfigurationErrors(final Writer writer, final List<ConfigurationError> configErrors) throws IOException {
        if (configErrors.isEmpty()) {
            return;
        }

        writer.write("<hr/>");
        writer.write("<center><h3>Configuration errors</h3></center>");
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL
                + "<th>Rule</th><th>Problem</th></tr>" + PMD.EOL);

        StringBuilder buf = new StringBuilder(500);
        boolean colorize = true;
        for (Report.ConfigurationError ce : configErrors) {
            buf.setLength(0);
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> ").append(PMD.EOL);
            buf.append("<td>").append(renderRuleName(ce.rule())).append("</td>").append(PMD.EOL);
            buf.append("<td>").append(ce.issue()).append("</td>").append(PMD.EOL);
            buf.append("</tr>").append(PMD.EOL);
            writer.write(buf.toString());
        }
        writer.write("</table>");
    }

    private String maybeWrap(String filename, String line) {
        if (StringUtils.isBlank(linkPrefix)) {
            return filename;
        }
        String newFileName = filename.replace('\\', '/');

        if (replaceHtmlExtension) {
            int index = filename.lastIndexOf('.');
            if (index >= 0) {
                newFileName = filename.substring(0, index);
            }
        }

        return "<a href=\"" + linkPrefix + newFileName + (replaceHtmlExtension ? ".html#" : "#") + line + "\">"
            + newFileName + "</a>";
    }
}
