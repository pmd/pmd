/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:11:11 PM
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;

public class XMLRenderer implements Renderer {

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    public String render(Report report) {
        StringBuffer buf = new StringBuffer("<?xml version=\"1.0\"?><pmd>" + EOL);
        String filename = "*start*";
        String lineSep = EOL;

        // rule violations
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            if (!rv.getFilename().equals(filename)) { // New File
                if (!filename.equals("*start*")) {
                    buf.append("</file>");
                }
                filename = rv.getFilename();
                buf.append("<file name=\"" + filename + "\">");
                buf.append(lineSep);
            }

            buf.append("<violation ");
            buf.append("line=\"" + Integer.toString(rv.getLine()) + "\" ");
            buf.append("rule=\"" + rv.getRule().getName() + "\">");
            buf.append(lineSep);

            String d = rv.getDescription();
            d = replaceString(d, '&', "&amp;");
            d = replaceString(d, '<', "&lt;");
            d = replaceString(d, '>', "&gt;");
            buf.append(d);

            buf.append(lineSep);
            buf.append("</violation>");
            buf.append(lineSep);
        }
        if (!filename.equals("*start*")) {
            buf.append("</file>");
        }

        // errors
        for (Iterator i = report.errors(); i.hasNext();) {
            Report.ProcessingError pe = (Report.ProcessingError)i.next();
            buf.append(lineSep);
            buf.append("<error ");
            buf.append(lineSep);
            String attrs = "filename=\"" + pe.getFile() +"\" msg=\"" + pe.getMsg() + "\"";
            attrs = replaceString(attrs, '&', "&amp;");
            attrs = replaceString(attrs, '<', "&lt;");
            attrs = replaceString(attrs, '>', "&gt;");
            buf.append(attrs);
            buf.append(lineSep);
            buf.append("/>");
            buf.append(lineSep);
        }

        buf.append("</pmd>");
        return buf.toString();
    }

    private static String replaceString(String d, char oldChar, String newString) {
        StringBuffer desc = new StringBuffer();
        int index = d.indexOf(oldChar);
        int last = 0;
        while (index != -1) {
            desc.append(d.substring(last, index));
            desc.append(newString);
            last = index+1;
            index = d.indexOf(oldChar,last);
        }
        desc.append(d.substring(last));
        return desc.toString();
    }
}
