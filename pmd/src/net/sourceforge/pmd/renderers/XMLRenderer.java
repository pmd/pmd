/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:11:11 PM
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;

import java.util.Iterator;

public class XMLRenderer implements Renderer {

    public String render(Report report) {
        StringBuffer buf = new StringBuffer("<?xml version=\"1.0\"?><pmd>" + System.getProperty("line.separator"));
	String filename = "*start*";
	String lineSep = System.getProperty("line.separator");

        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
	    if (!rv.getFilename().equals(filename)) { // New File
		if (!filename.equals("*start*")) {
		    buf.append("</file>");
		}
		filename = rv.getFilename();
		buf.append("<file name=\"" + filename + "\">");
		buf.append( lineSep );
	    }
		
	    buf.append("<violation ");
	    buf.append("line=\"" + Integer.toString( rv.getLine() ) + "\" ");
	    buf.append("rule=\"" + rv.getRule().getName() + "\">" );
	    buf.append( lineSep );
            buf.append(rv.getDescription());
	    buf.append( lineSep );
            buf.append("</violation>");
            buf.append( lineSep );
        }
	if (!filename.equals("*start*")) {
	    buf.append("</file>");
	}
        buf.append("</pmd>");
        return buf.toString();
    }

}
