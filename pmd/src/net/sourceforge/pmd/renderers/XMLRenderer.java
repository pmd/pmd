package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.util.StringUtil;

import java.util.Iterator;

public class XMLRenderer implements Renderer {

    public String render(Report report) {
        StringBuffer buf = new StringBuffer("<?xml version=\"1.0\"?><pmd>" + PMD.EOL);
        String filename = "*start*";

        // rule violations
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            if (!rv.getFilename().equals(filename)) { // New File
                if (!filename.equals("*start*")) {
                    buf.append("</file>");
                }
                filename = rv.getFilename();
                buf.append("<file name=\"" + filename + "\">");
                buf.append(PMD.EOL);
            }

            buf.append("<violation ");
            buf.append("line=\"" + Integer.toString(rv.getLine()) + "\" ");
            buf.append("rule=\"" + rv.getRule().getName() + "\">");
            buf.append(PMD.EOL);

            String d = rv.getDescription();
            d = StringUtil.replaceString(d, '&', "&amp;");
            d = StringUtil.replaceString(d, '<', "&lt;");
            d = StringUtil.replaceString(d, '>', "&gt;");
            buf.append(d);

            buf.append(PMD.EOL);
            buf.append("</violation>");
            buf.append(PMD.EOL);
        }
        if (!filename.equals("*start*")) {
            buf.append("</file>");
        }

        // errors
        for (Iterator i = report.errors(); i.hasNext();) {
            Report.ProcessingError pe = (Report.ProcessingError) i.next();
            buf.append(PMD.EOL);
            buf.append("<error ");
            buf.append(PMD.EOL);
            String attrs = "filename=\"" + pe.getFile() + "\" msg=\"" + pe.getMsg() + "\"";
            attrs = StringUtil.replaceString(attrs, '&', "&amp;");
            attrs = StringUtil.replaceString(attrs, '<', "&lt;");
            attrs = StringUtil.replaceString(attrs, '>', "&gt;");
            buf.append(attrs);
            buf.append(PMD.EOL);
            buf.append("/>");
            buf.append(PMD.EOL);
        }

        buf.append("</pmd>");
        return buf.toString();
    }

}
