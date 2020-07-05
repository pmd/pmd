/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * Renderer to XML format.
 */
public class XMLRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "xml";

    // TODO 7.0.0 use PropertyDescriptor<String> or something more specialized
    public static final StringProperty ENCODING = new StringProperty("encoding",
            "XML encoding format, defaults to UTF-8.", "UTF-8", 0);

    public XMLRenderer() {
        super(NAME, "XML format.");
        definePropertyDescriptor(ENCODING);
    }

    public XMLRenderer(String encoding) {
        this();
        setProperty(ENCODING, encoding);
    }

    @Override
    public String defaultFileExtension() {
        return "xml";
    }

    @Override
    public void start() throws IOException {
        String encoding = getProperty(ENCODING);

        StringBuilder buf = new StringBuilder(500);
        buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>").append(PMD.EOL);
        createVersionAttr(buf);
        createTimestampAttr(buf);
        // FIXME: elapsed time not available until the end of the processing
        // buf.append(createTimeElapsedAttr(report));
        buf.append('>').append(PMD.EOL);
        writer.write(buf.toString());
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        StringBuilder buf = new StringBuilder(500);
        String filename = null;

        // rule violations
        while (violations.hasNext()) {
            buf.setLength(0);
            RuleViolation rv = violations.next();
            String nextFilename = determineFileName(rv.getFilename());
            if (!nextFilename.equals(filename)) {
                // New File
                if (filename != null) {
                    // Not first file ?
                    buf.append("</file>").append(PMD.EOL);
                }
                filename = nextFilename;
                buf.append("<file name=\"");
                buf.append(escape(filename));
                buf.append("\">").append(PMD.EOL);
            }

            buf.append("<violation beginline=\"").append(rv.getBeginLine());
            buf.append("\" endline=\"").append(rv.getEndLine());
            buf.append("\" begincolumn=\"").append(rv.getBeginColumn());
            buf.append("\" endcolumn=\"").append(rv.getEndColumn());
            buf.append("\" rule=\"");
            buf.append(escape(rv.getRule().getName()));
            buf.append("\" ruleset=\"");
            buf.append(escape(rv.getRule().getRuleSetName()));
            buf.append('"');
            maybeAdd("package", rv.getPackageName(), buf);
            maybeAdd("class", rv.getClassName(), buf);
            maybeAdd("method", rv.getMethodName(), buf);
            maybeAdd("variable", rv.getVariableName(), buf);
            maybeAdd("externalInfoUrl", rv.getRule().getExternalInfoUrl(), buf);
            buf.append(" priority=\"");
            buf.append(rv.getRule().getPriority().getPriority());
            buf.append("\">").append(PMD.EOL);
            buf.append(escape(rv.getDescription()));

            buf.append(PMD.EOL);
            buf.append("</violation>");
            buf.append(PMD.EOL);
            writer.write(buf.toString());
        }
        if (filename != null) { // Not first file ?
            writer.write("</file>");
            writer.write(PMD.EOL);
        }
    }

    @Override
    public void end() throws IOException {
        StringBuilder buf = new StringBuilder(500);
        // errors
        for (Report.ProcessingError pe : errors) {
            buf.setLength(0);
            buf.append("<error ").append("filename=\"");
            buf.append(escape(determineFileName(pe.getFile())));
            buf.append("\" msg=\"");
            buf.append(escape(pe.getMsg()));
            buf.append("\">").append(PMD.EOL);
            buf.append("<![CDATA[").append(pe.getDetail()).append("]]>").append(PMD.EOL);
            buf.append("</error>").append(PMD.EOL);
            writer.write(buf.toString());
        }

        // suppressed violations
        if (showSuppressedViolations) {
            for (Report.SuppressedViolation s : suppressed) {
                buf.setLength(0);
                buf.append("<suppressedviolation ").append("filename=\"");
                buf.append(escape(determineFileName(s.getRuleViolation().getFilename())));
                buf.append("\" suppressiontype=\"");
                buf.append(escape(s.suppressedByNOPMD() ? "nopmd" : "annotation"));
                buf.append("\" msg=\"");
                buf.append(escape(s.getRuleViolation().getDescription()));
                buf.append("\" usermsg=\"");
                buf.append(escape(s.getUserMessage() == null ? "" : s.getUserMessage()));
                buf.append("\"/>").append(PMD.EOL);
                writer.write(buf.toString());
            }
        }

        // config errors
        for (final Report.ConfigurationError ce : configErrors) {
            buf.setLength(0);
            buf.append("<configerror ").append("rule=\"");
            buf.append(escape(ce.rule().getName()));
            buf.append("\" msg=\"");
            buf.append(escape(ce.issue()));
            buf.append("\"/>").append(PMD.EOL);
            writer.write(buf.toString());
        }

        writer.write("</pmd>" + PMD.EOL);
    }

    private void maybeAdd(String attr, String value, StringBuilder buf) {
        if (value != null && value.length() > 0) {
            buf.append(' ').append(attr).append("=\"");
            buf.append(escape(value));
            buf.append('"');
        }
    }

    private void createVersionAttr(StringBuilder buffer) {
        buffer.append("<pmd xmlns=\"http://pmd.sourceforge.net/report/2.0.0\"").append(PMD.EOL)
            .append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"").append(PMD.EOL)
            .append("    xsi:schemaLocation=\"http://pmd.sourceforge.net/report/2.0.0 http://pmd.sourceforge.net/report_2_0_0.xsd\"").append(PMD.EOL)
            .append("    version=\"").append(PMDVersion.VERSION).append('"');
    }

    private void createTimestampAttr(StringBuilder buffer) {
        buffer.append(" timestamp=\"").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date()))
                .append('"');
    }

    @Override
    public void setReportFile(String reportFilename) {
        String encoding = getProperty(ENCODING);

        try {
            Charset charset = Charset.forName(encoding);
            this.writer = StringUtils.isBlank(reportFilename) ? new OutputStreamWriter(System.out, charset)
                    : Files.newBufferedWriter(new File(reportFilename).toPath(), charset);
        } catch (IOException | UnsupportedCharsetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Escape unicode characters for non UTF-8 encodings.
     */
    private String escape(String text) {
        String result = StringEscapeUtils.escapeXml10(text);
        String encoding = getProperty(ENCODING);
        if (!"UTF-8".equalsIgnoreCase(encoding)) {
            StringBuilder sb = new StringBuilder(result);
            for (int i = 0; i < sb.length(); i++) {
                char c = sb.charAt(i);
                // surrogate characters are not allowed in XML
                if (Character.isHighSurrogate(c)) {
                    char low = sb.charAt(i + 1);
                    int codepoint = Character.toCodePoint(c, low);
                    sb.replace(i, i + 2, "&#x" + Integer.toHexString(codepoint) + ";");
                } else if (c > 0xff) {
                    sb.replace(i, i + 1, "&#x" + Integer.toHexString((int) c) + ";");
                }
            }
            result = sb.toString();
        }
        return result;
    }

    // FIXME: elapsed time not available until the end of the processing
    /*
     * private String createTimeElapsedAttr(Report rpt) {
     * Report.ReadableDuration d = new
     * Report.ReadableDuration(rpt.getElapsedTimeInMillis()); return
     * " elapsedTime=\"" + d.getTime() + "\""; }
     */
}
