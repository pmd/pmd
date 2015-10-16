/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Renderer to XML format.
 */
public class XMLRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "xml";

    public static final StringProperty ENCODING = new StringProperty("encoding",
            "XML encoding format, defaults to UTF-8.", "UTF-8", 0);
    private boolean useUTF8 = false;


    public XMLRenderer() {
        super(NAME, "XML format.");
        definePropertyDescriptor(ENCODING);
    }

    public XMLRenderer(String encoding) {
        this();
        setProperty(ENCODING, encoding);
    }

    public String defaultFileExtension() {
        return "xml";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
        String encoding = getProperty(ENCODING);
        if (encoding.equalsIgnoreCase("utf-8")) {
            useUTF8 = true;
        }

        Writer writer = getWriter();
        StringBuilder buf = new StringBuilder(500);
        buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>").append(PMD.EOL);
        createVersionAttr(buf);
        createTimestampAttr(buf);
        // FIXME: elapsed time not available until the end of the processing
        // buf.append(createTimeElapsedAttr(report));
        buf.append('>').append(PMD.EOL);
        writer.write(buf.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        Writer writer = getWriter();
        StringBuilder buf = new StringBuilder(500);
        String filename = null;

        // rule violations
        while (violations.hasNext()) {
            buf.setLength(0);
            RuleViolation rv = violations.next();
            if (!rv.getFilename().equals(filename)) { // New File
                if (filename != null) {// Not first file ?
                    buf.append("</file>").append(PMD.EOL);
                }
                filename = rv.getFilename();
                buf.append("<file name=\"");
                StringUtil.appendXmlEscaped(buf, filename, useUTF8);
                buf.append("\">").append(PMD.EOL);
            }

            buf.append("<violation beginline=\"").append(rv.getBeginLine());
            buf.append("\" endline=\"").append(rv.getEndLine());
            buf.append("\" begincolumn=\"").append(rv.getBeginColumn());
            buf.append("\" endcolumn=\"").append(rv.getEndColumn());
            buf.append("\" rule=\"");
            StringUtil.appendXmlEscaped(buf, rv.getRule().getName(), useUTF8);
            buf.append("\" ruleset=\"");
            StringUtil.appendXmlEscaped(buf, rv.getRule().getRuleSetName(), useUTF8);
            buf.append('"');
            maybeAdd("package", rv.getPackageName(), buf);
            maybeAdd("class", rv.getClassName(), buf);
            maybeAdd("method", rv.getMethodName(), buf);
            maybeAdd("variable", rv.getVariableName(), buf);
            maybeAdd("externalInfoUrl", rv.getRule().getExternalInfoUrl(), buf);
            buf.append(" priority=\"");
            buf.append(rv.getRule().getPriority().getPriority());
            buf.append("\">").append(PMD.EOL);
            StringUtil.appendXmlEscaped(buf, rv.getDescription(), useUTF8);

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() throws IOException {
        Writer writer = getWriter();
        StringBuilder buf = new StringBuilder(500);
        // errors
        for (Report.ProcessingError pe : errors) {
            buf.setLength(0);
            buf.append("<error ").append("filename=\"");
            StringUtil.appendXmlEscaped(buf, pe.getFile(), useUTF8);
            buf.append("\" msg=\"");
            StringUtil.appendXmlEscaped(buf, pe.getMsg(), useUTF8);
            buf.append("\"/>").append(PMD.EOL);
            writer.write(buf.toString());
        }

        // suppressed violations
        if (showSuppressedViolations) {
            for (Report.SuppressedViolation s : suppressed) {
                buf.setLength(0);
                buf.append("<suppressedviolation ").append("filename=\"");
                StringUtil.appendXmlEscaped(buf, s.getRuleViolation().getFilename(), useUTF8);
                buf.append("\" suppressiontype=\"");
                StringUtil.appendXmlEscaped(buf, s.suppressedByNOPMD() ? "nopmd" : "annotation", useUTF8);
                buf.append("\" msg=\"");
                StringUtil.appendXmlEscaped(buf, s.getRuleViolation().getDescription(), useUTF8);
                buf.append("\" usermsg=\"");
                StringUtil.appendXmlEscaped(buf, s.getUserMessage() == null ? "" : s.getUserMessage(), useUTF8);
                buf.append("\"/>").append(PMD.EOL);
                writer.write(buf.toString());
            }
        }

        writer.write("</pmd>" + PMD.EOL);
    }

    private void maybeAdd(String attr, String value, StringBuilder buf) {
        if (value != null && value.length() > 0) {
            buf.append(' ').append(attr).append("=\"");
            StringUtil.appendXmlEscaped(buf, value, useUTF8);
            buf.append('"');
        }
    }

    private void createVersionAttr(StringBuilder buffer) {
        buffer.append("<pmd version=\"").append(PMD.VERSION).append('"');
    }

    private void createTimestampAttr(StringBuilder buffer) {
        buffer.append(" timestamp=\"").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date()))
                .append('"');
    }

    // FIXME: elapsed time not available until the end of the processing
    /*
     * private String createTimeElapsedAttr(Report rpt) {
     * Report.ReadableDuration d = new
     * Report.ReadableDuration(rpt.getElapsedTimeInMillis()); return
     * " elapsedTime=\"" + d.getTime() + "\""; }
     */
}
