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

public class XMLRenderer extends AbstractRenderer {

    public void render(Writer writer, Report report) throws IOException {

        StringBuffer buf = new StringBuffer();
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL + createVersionAttr() + createTimestampAttr() + createTimeElapsedAttr(report) + '>' + PMD.EOL);
        String filename = null;

        // rule violations
        for (Iterator i = report.iterator(); i.hasNext();) {
            buf.setLength(0);
            IRuleViolation rv = (IRuleViolation) i.next();
            if (!rv.getFilename().equals(filename)) { // New File
                if (filename != null) {// Not first file ?
                    buf.append("</file>").append(PMD.EOL);
                }
                filename = rv.getFilename();
                buf.append("<file name=\"");
                StringUtil.appendXmlEscaped(buf, filename);
                buf.append("\">").append(PMD.EOL);
            }

            buf.append("<violation line=\"").append(rv.getBeginLine());
            buf.append("\" rule=\"");
            StringUtil.appendXmlEscaped(buf, rv.getRule().getName());
            buf.append("\" ruleset=\"");
            StringUtil.appendXmlEscaped(buf, rv.getRule().getRuleSetName());
            buf.append('"');
            maybeAdd("package", rv.getPackageName(), buf);
            maybeAdd("class", rv.getClassName(), buf);
            maybeAdd("method", rv.getMethodName(), buf);
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

        // errors
        for (Iterator i = report.errors(); i.hasNext();) {
            buf.setLength(0);
            Report.ProcessingError pe = (Report.ProcessingError) i.next();
            buf.append("<error ").append("filename=\"");
            StringUtil.appendXmlEscaped(buf, pe.getFile());
            buf.append("\" msg=\"");
            StringUtil.appendXmlEscaped(buf, pe.getMsg());
            buf.append("\"/>").append(PMD.EOL);
            writer.write(buf.toString());
        }

        // suppressed violations
        if (showSuppressedViolations) {
            for (Iterator i = report.getSuppressedRuleViolations().iterator(); i.hasNext();) {
                buf.setLength(0);
                Report.SuppressedViolation suppressed = (Report.SuppressedViolation) i.next();
                buf.append("<suppressedviolation ").append("filename=\"");
                StringUtil.appendXmlEscaped(buf, suppressed.getRuleViolation().getFilename());
                buf.append("\" suppressiontype=\"");
                StringUtil.appendXmlEscaped(buf, suppressed.suppressedByNOPMD() ? "nopmd" : "annotation");
                buf.append("\" msg=\"");
                StringUtil.appendXmlEscaped(buf, suppressed.getRuleViolation().getDescription());
                buf.append("\" usermsg=\"");
                StringUtil.appendXmlEscaped(buf, suppressed.getUserMessage());
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

    private String createVersionAttr() {
        return "<pmd version=\"" + PMD.VERSION + "\"";
    }

    private String createTimestampAttr() {
        return " timestamp=\"" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date()) + "\"";
    }

    private String createTimeElapsedAttr(Report rpt) {
        Report.ReadableDuration d = new Report.ReadableDuration(rpt.getElapsedTimeInMillis());
        return " elapsedTime=\"" + d.getTime() + "\"";
    }

}
