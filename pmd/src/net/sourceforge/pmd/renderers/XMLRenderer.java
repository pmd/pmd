/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class XMLRenderer implements Renderer {

    private String createVersionAttr() {
        return "<pmd version=\"" + PMD.VERSION + "\"";
    }
    private String createTimestampAttr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return " timestamp=\"" + sdf.format(new Date()) + "\"";
    }
    public String render(Report report) {

        StringBuffer buf = new StringBuffer("<?xml version=\"1.0\"?>" + PMD.EOL + createVersionAttr() + createTimestampAttr() + ">" + PMD.EOL);
        String filename = null;

        // rule violations
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            if (!rv.getFilename().equals(filename)) { // New File
                if (filename != null) {// Not first file ?
                    buf.append("</file>"+PMD.EOL);
                }
                filename = rv.getFilename();
                buf.append("<file name=\"");
                StringUtil.appendXmlEscaped(buf, filename);
                buf.append("\">").append(PMD.EOL);
            }

            buf.append("<violation line=\"").append(rv.getLine()).append("\"");
            buf.append(" rule=\"");
            StringUtil.appendXmlEscaped(buf, rv.getRule().getName());
            buf.append("\"");
            buf.append(" ruleset=\"");
            StringUtil.appendXmlEscaped(buf, rv.getRule().getRuleSetName());
            buf.append("\"");
            buf.append(" priority=\"");
            buf.append(rv.getRule().getPriority());
            buf.append("\">");
            buf.append(PMD.EOL);
            StringUtil.appendXmlEscaped(buf, rv.getDescription());

            buf.append(PMD.EOL);
            buf.append("</violation>");
            buf.append(PMD.EOL);
        }
        if (filename != null) { // Not first file ?
            buf.append("</file>"+PMD.EOL);
        }

        // errors
        for (Iterator i = report.errors(); i.hasNext();) {
            Report.ProcessingError pe = (Report.ProcessingError) i.next();
            buf.append("<error ").append("filename=\"");
            StringUtil.appendXmlEscaped(buf, pe.getFile());
            buf.append("\" msg=\"");
            StringUtil.appendXmlEscaped(buf, pe.getMsg());
            buf.append("\"/>").append(PMD.EOL);
        }

        buf.append("</pmd>");
        return buf.toString();
    }

}
