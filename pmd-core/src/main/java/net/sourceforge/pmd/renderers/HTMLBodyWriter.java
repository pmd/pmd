/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.FileNameRenderer;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Writes the body of an HTML report: the table of rule violations and the
 * tables for processing errors, suppressed violations and configuration
 * errors. This is shared by {@link HTMLRenderer} and {@link SummaryHTMLRenderer}.
 */
final class HTMLBodyWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTMLBodyWriter.class);

    private static final Set<String> ALLOWED_SCHEMES = CollectionUtil.setOf("http", "https");

    private final PrintWriter writer;
    private final String linkPrefix;
    private final String linePrefix;
    private final boolean replaceHtmlExtension;
    private final FileNameRenderer fileNameRenderer;

    private boolean colorize = true;
    private int violationCount = 1;

    HTMLBodyWriter(
            PrintWriter writer,
            String linkPrefix,
            String linePrefix,
            boolean replaceHtmlExtension,
            FileNameRenderer fileNameRenderer
    ) {
        this.writer = writer;
        this.linkPrefix = safeUrl(linkPrefix);
        this.linePrefix = linePrefix;
        this.replaceHtmlExtension = replaceHtmlExtension;
        this.fileNameRenderer = fileNameRenderer;
    }

    /**
     * Writes the heading and the opening tags of the violation table.
     */
    void renderHeader() {
        writer.write("<center><h3>PMD report</h3></center>");
        writer.write("<center><h3>Problems found</h3></center>");
        writer.println("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>");
        writer.println("<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>");
    }

    /**
     * Writes the rows of the violation table.
     */
    void renderViolations(Iterator<RuleViolation> violations) {
        StringBuilder buf = new StringBuilder(500);

        while (violations.hasNext()) {
            RuleViolation rv = violations.next();
            buf.setLength(0);
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> ").append(System.lineSeparator());
            buf.append("<td align=\"center\">").append(violationCount).append("</td>").append(System.lineSeparator());
            buf.append("<td width=\"*%\">")
                    .append(renderFileNameEscaped(rv.getFileId(), rv.getBeginLine()))
                    .append("</td>")
                    .append(System.lineSeparator());
            buf.append("<td align=\"center\" width=\"5%\">").append(rv.getBeginLine()).append("</td>").append(System.lineSeparator());

            String d = escape(rv.getDescription());

            String infoUrl = safeUrl(rv.getRule().getExternalInfoUrl());
            if (StringUtils.isNotBlank(infoUrl)) {
                d = "<a href=\"" + infoUrl + "\">" + d + "</a>";
            }
            buf.append("<td width=\"*\">")
                    .append(d)
                    .append("</td>")
                    .append(System.lineSeparator())
                    .append("</tr>")
                    .append(System.lineSeparator());
            writer.write(buf.toString());
            violationCount++;
        }
    }

    /**
     * Writes the closing tag of the violation table and the tables for
     * processing errors, suppressed violations and configuration errors.
     */
    void renderFooter(
            List<Report.ProcessingError> errors,
            List<ConfigurationError> configErrors,
            List<Report.SuppressedViolation> suppressed,
            boolean showSuppressed
    ) {
        writer.write("</table>");
        renderProcessingErrors(errors);
        if (showSuppressed) {
            renderSuppressions(suppressed);
        }
        renderConfigurationErrors(configErrors);
    }

    private void renderProcessingErrors(List<Report.ProcessingError> errors) {
        if (errors.isEmpty()) {
            return;
        }

        writer.write("<hr/>");
        writer.write("<center><h3>Processing errors</h3></center>");
        writer.println("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>");
        writer.println("<th>File</th><th>Problem</th></tr>");

        StringBuilder buf = new StringBuilder(500);
        boolean colorize = true;
        for (Report.ProcessingError pe : errors) {
            buf.setLength(0);
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> ").append(System.lineSeparator());
            buf.append("<td>").append(renderFileNameEscaped(pe.getFileId(), -1)).append("</td>").append(System.lineSeparator());
            buf.append("<td><pre>").append(escape(pe.getDetail())).append("</pre></td>").append(System.lineSeparator());
            buf.append("</tr>").append(System.lineSeparator());
            writer.write(buf.toString());
        }
        writer.write("</table>");
    }

    private void renderSuppressions(List<Report.SuppressedViolation> suppressed) {
        if (suppressed.isEmpty()) {
            return;
        }

        writer.write("<hr/>");
        writer.write("<center><h3>Suppressed warnings</h3></center>");
        writer.println("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>");
        writer.println("<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>");

        StringBuilder buf = new StringBuilder(500);
        boolean colorize = true;
        for (Report.SuppressedViolation sv : suppressed) {
            buf.setLength(0);
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> ").append(System.lineSeparator());
            RuleViolation rv = sv.getRuleViolation();
            String userMessage = Optional.ofNullable(sv.getUserMessage()).orElse("");
            buf.append("<td align=\"left\">").append(renderFileNameEscaped(rv.getFileId(), rv.getBeginLine())).append("</td>").append(System.lineSeparator());
            buf.append("<td align=\"center\">").append(rv.getBeginLine()).append("</td>").append(System.lineSeparator());
            buf.append("<td align=\"center\">").append(renderRuleNameEscaped(rv.getRule())).append("</td>").append(System.lineSeparator());
            buf.append("<td align=\"center\">").append(escape(sv.getSuppressor().getId())).append("</td>").append(System.lineSeparator());
            buf.append("<td align=\"center\">").append(escape(userMessage)).append("</td>").append(System.lineSeparator());
            buf.append("</tr>").append(System.lineSeparator());
            writer.write(buf.toString());
        }
        writer.write("</table>");
    }

    private void renderConfigurationErrors(List<ConfigurationError> configErrors) {
        if (configErrors.isEmpty()) {
            return;
        }

        writer.write("<hr/>");
        writer.write("<center><h3>Configuration errors</h3></center>");
        writer.println("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>");
        writer.println("<th>Rule</th><th>Problem</th></tr>");

        StringBuilder buf = new StringBuilder(500);
        boolean colorize = true;
        for (Report.ConfigurationError ce : configErrors) {
            buf.setLength(0);
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> ").append(System.lineSeparator());
            buf.append("<td>").append(renderRuleNameEscaped(ce.rule())).append("</td>").append(System.lineSeparator());
            buf.append("<td>").append(escape(ce.issue())).append("</td>").append(System.lineSeparator());
            buf.append("</tr>").append(System.lineSeparator());
            writer.write(buf.toString());
        }
        writer.write("</table>");
    }

    /**
     * Converts the given URL to be safe for using as the value of a href attribute.
     *
     * @param raw the URL to convert
     * @return the safe URL or an empty string if the URL is invalid
     */
    private static String safeUrl(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        URI uri;
        try {
            uri = new URI(raw); // rejects spaces, tabs, control chars, quotes
        } catch (URISyntaxException e) {
            LOGGER.debug("Ignoring invalid URL: {}", raw, e);
            return "";
        }

        String scheme = uri.getScheme();
        if (scheme == null && uri.getRawAuthority() == null) {
            // relative reference: allow paths, but not "//host/..."
            return uri.toASCIIString();
        }

        if (scheme == null
                || !ALLOWED_SCHEMES.contains(scheme.toLowerCase(Locale.ROOT))
                || uri.getHost() == null) {   // e.g. "https:foo" (opaque)
            LOGGER.debug("Ignoring URL due to invalid scheme: {}", raw);
            return "";
        }
        return uri.toASCIIString();
    }

    private static String escape(String s) {
        return StringEscapeUtils.escapeHtml4(s);
    }

    private String renderFileNameEscaped(FileId fileId, int beginLine) {
        String actualLineSelector = linePrefix == null || beginLine < 0 ? "" : linePrefix + beginLine;
        String actualLineSelectorEncoded = null;
        try {
            actualLineSelectorEncoded = URLEncoder.encode(actualLineSelector, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return maybeWrap(escape(fileNameRenderer.getDisplayName(fileId)), actualLineSelectorEncoded);
    }

    private String renderRuleNameEscaped(Rule rule) {
        String name = escape(rule.getName());
        String infoUrl = safeUrl(rule.getExternalInfoUrl());
        if (StringUtils.isNotBlank(infoUrl)) {
            return "<a href=\"" + infoUrl + "\">" + name + "</a>";
        }
        return name;
    }

    private String maybeWrap(String filenameEscaped, String line) {
        if (StringUtils.isBlank(linkPrefix)) {
            return filenameEscaped;
        }
        String newFileName = filenameEscaped.replace('\\', '/');

        if (replaceHtmlExtension) {
            int index = filenameEscaped.lastIndexOf('.');
            if (index >= 0) {
                newFileName = filenameEscaped.substring(0, index);
            }
        }

        return "<a href=\"" + linkPrefix + newFileName + (replaceHtmlExtension ? ".html#" : "#") + line + "\">"
            + newFileName + "</a>";
    }
}
