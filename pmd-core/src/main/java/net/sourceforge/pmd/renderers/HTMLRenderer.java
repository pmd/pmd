/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Optional;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * Renderer to basic HTML format.
 *
 * FIXME: this class should just work with the XMLRenderer and then apply an
 * XSLT transformation + stylesheet. No need to hard-code HTML markup here.
 */
public class HTMLRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "html";

    public static final PropertyDescriptor<Optional<String>> LINE_PREFIX =
        PropertyFactory.stringProperty("linePrefix")
                       .desc("Prefix for line number anchor in the source file.")
                       .toOptional("<none>")
                       .defaultValue(Optional.empty())
                       .build();

    public static final PropertyDescriptor<String> LINK_PREFIX =
        PropertyFactory.stringProperty("linkPrefix").desc("Path to HTML source.").defaultValue("").build();

    public static final PropertyDescriptor<Boolean> HTML_EXTENSION =
        PropertyFactory.booleanProperty("htmlExtension")
                       .desc("Replace file extension with .html for the links.")
                       // default value is false - to have the old (pre 6.23.0) behavior, this needs to be set to true.
                       .defaultValue(false)
                       .build();

    private HTMLBodyWriter body;

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

    @Override
    public void start() throws IOException {
        body = new HTMLBodyWriter(
                writer,
                getProperty(LINK_PREFIX),
                getProperty(LINE_PREFIX).orElse(null),
                getProperty(HTML_EXTENSION),
                this::determineFileName
        );
        writer.println("<html><head><title>PMD</title></head><body>");
        body.renderHeader();
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        body.renderViolations(violations);
    }

    @Override
    public void end() throws IOException {
        body.renderFooter(errors, configErrors, suppressed, showSuppressedViolations);
        writer.println("</body></html>");
    }

    /**
     * @deprecated since 7.30.0
     */
    @Deprecated
    public void renderBody(PrintWriter writer, Report report) throws IOException {
        HTMLBodyWriter bodyWriter = new HTMLBodyWriter(
                writer,
                getProperty(LINK_PREFIX),
                getProperty(LINE_PREFIX).orElse(null),
                getProperty(HTML_EXTENSION),
                this::determineFileName
        );
        bodyWriter.renderHeader();
        bodyWriter.renderViolations(report.getViolations().iterator());
        bodyWriter.renderFooter(
                report.getProcessingErrors(),
                report.getConfigurationErrors(),
                report.getSuppressedViolations(),
                showSuppressedViolations
        );
    }
}
