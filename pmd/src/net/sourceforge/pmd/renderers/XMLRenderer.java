/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.StringUtil;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class XMLRenderer extends OnTheFlyRenderer {

    // FIXME - hardcoded character encoding, booooooo
	protected String encoding = "UTF-8";

    public void start() throws IOException {
        Writer writer = getWriter();
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"" + this.encoding + "\"?>").append(PMD.EOL);
        createVersionAttr(buf);
        createTimestampAttr(buf);
        // FIXME: elapsed time not available until the end of the processing
        //buf.append(createTimeElapsedAttr(report));
        buf.append('>').append(PMD.EOL);
        writer.write(buf.toString());
    }

    public void renderFileViolations(Iterator<IRuleViolation> violations) throws IOException {
        Writer writer = getWriter();
        StringBuffer buf = new StringBuffer();
        String filename = null;

        // rule violations
        while (violations.hasNext()) {
            buf.setLength(0);
            IRuleViolation rv = violations.next();
            if (!rv.getFilename().equals(filename)) { // New File
                if (filename != null) {// Not first file ?
                    buf.append("</file>").append(PMD.EOL);
                }
                filename = rv.getFilename();
                buf.append("<file name=\"");
                StringUtil.appendXmlEscaped(buf, filename);
                buf.append("\">").append(PMD.EOL);
            }

            buf.append("<violation beginline=\"").append(rv.getBeginLine());
            buf.append("\" endline=\"").append(rv.getEndLine());
            buf.append("\" begincolumn=\"").append(rv.getBeginColumn());
            buf.append("\" endcolumn=\"").append(rv.getEndColumn());
            buf.append("\" rule=\"");
            StringUtil.appendXmlEscaped(buf, rv.getRule().getName());
            buf.append("\" ruleset=\"");
            StringUtil.appendXmlEscaped(buf, rv.getRule().getRuleSetName());
            buf.append('"');
            maybeAdd("package", rv.getPackageName(), buf);
            maybeAdd("class", rv.getClassName(), buf);
            maybeAdd("method", rv.getMethodName(), buf);
            maybeAdd("variable", rv.getVariableName(), buf);
            maybeAdd("externalInfoUrl", rv.getRule().getExternalInfoUrl(), buf);
            buf.append(" priority=\"");
            buf.append(rv.getRule().getPriority());
            buf.append("\">").append(PMD.EOL);
            StringUtil.appendXmlEscaped(buf, rv.getDescription());

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

    public void end() throws IOException {
        Writer writer = getWriter();
        StringBuffer buf = new StringBuffer();
        // errors
        for (Report.ProcessingError pe: errors) {
            buf.setLength(0);
            buf.append("<error ").append("filename=\"");
            StringUtil.appendXmlEscaped(buf, pe.getFile());
            buf.append("\" msg=\"");
            StringUtil.appendXmlEscaped(buf, pe.getMsg());
            buf.append("\"/>").append(PMD.EOL);
            writer.write(buf.toString());
        }

        // suppressed violations
        if (showSuppressedViolations) {
            for (Report.SuppressedViolation s: suppressed) {
                buf.setLength(0);
                buf.append("<suppressedviolation ").append("filename=\"");
                StringUtil.appendXmlEscaped(buf, s.getRuleViolation().getFilename());
                buf.append("\" suppressiontype=\"");
                StringUtil.appendXmlEscaped(buf, s.suppressedByNOPMD() ? "nopmd" : "annotation");
                buf.append("\" msg=\"");
                StringUtil.appendXmlEscaped(buf, s.getRuleViolation().getDescription());
                buf.append("\" usermsg=\"");
                StringUtil.appendXmlEscaped(buf, (s.getUserMessage() == null ? "" : s.getUserMessage()));
                buf.append("\"/>").append(PMD.EOL);
                writer.write(buf.toString());
            }
        }

        writer.write("</pmd>");
    }

    private void maybeAdd(String attr, String value, StringBuffer buf) {
        if (value != null && value.length() > 0) {
            buf.append(' ').append(attr).append("=\"");
            StringUtil.appendXmlEscaped(buf, value);
            buf.append('"');
        }
    }

    private void createVersionAttr(StringBuffer buffer) {
        buffer.append("<pmd version=\"").append(PMD.VERSION).append('"');
    }

    private void createTimestampAttr(StringBuffer buffer) {
        buffer.append(" timestamp=\"").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date())).append('"');
    }

    private String createTimeElapsedAttr(Report rpt) {
        Report.ReadableDuration d = new Report.ReadableDuration(rpt.getElapsedTimeInMillis());
        return " elapsedTime=\"" + d.getTime() + "\"";
    }

}
