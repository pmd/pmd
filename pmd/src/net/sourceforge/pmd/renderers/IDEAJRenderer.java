/*
 * User: tom
 * Date: Sep 23, 2002
 * Time: 5:07:40 PM
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;

public class IDEAJRenderer {
    protected String EOL = System.getProperty("line.separator", "\n");

	public String render(Report report, String classAndMethod, String file) {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(rv.getDescription() + EOL);
            buf.append(" at " + classAndMethod + "(" + file + ":" + rv.getLine() +")" + EOL);
        }
        return buf.toString();
	}

    public String render(Report report) {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(rv.getDescription() + EOL);
            buf.append(" at " + getFullyQualifiedClassName(rv.getFilename()) + ".method(" + getSimpleFileName(rv.getFilename()) +":" + rv.getLine() +")" + EOL);
        }
        return buf.toString();
    }

    private String getFullyQualifiedClassName(String in) {
        String inLower = in.toLowerCase();
        String preamble = "c:\\data\\pmd\\pmd\\src\\";
        int preambleend = inLower.indexOf(preamble) + preamble.length();
        String classNameWithSlashes = in.substring(preambleend);
        String className = classNameWithSlashes.replace('\\','.');
        return className.substring(0, className.indexOf(".java"));
    }

    private String getSimpleFileName(String in) {
        int lastSlash = in.lastIndexOf('\\')+1;
        return in.substring(lastSlash);
    }
}
