/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

/**
 * Renderer to another HTML format.
 * @author Vladimir
 */
public class VBHTMLRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "vbhtml";

    public VBHTMLRenderer() {
    	super(NAME, "Vladimir Bossicard HTML format.");
    }

    public String defaultFileExtension() { return "vb.html"; }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
    	getWriter().write(header());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
		if (!violations.hasNext()) {
		    return;
		}
	
		Writer writer = getWriter();
		StringBuilder sb = new StringBuilder(500);
		String filename = null;
		String lineSep = PMD.EOL;
	
		boolean colorize = false;
		while (violations.hasNext()) {
		    sb.setLength(0);
		    RuleViolation rv = violations.next();
			if (!rv.getFilename().equals(filename)) { // New File
				if (filename != null) {
				    sb.append("</table></br>");
				    colorize = false;
				}
				filename = rv.getFilename();
				sb.append("<table border=\"0\" width=\"80%\">");
				sb.append("<tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;").append(filename).append(
					"</font></tr>");
				sb.append(lineSep);
			    }
	
		    if (colorize) {
				sb.append("<tr id=RowColor1>");
			    } else {
				sb.append("<tr id=RowColor2>");
			    }
	
		    colorize = !colorize;
		    sb.append("<td width=\"50\" align=\"right\"><font class=body>" + rv.getBeginLine()
			    + "&nbsp;&nbsp;&nbsp;</font></td>");
		    sb.append("<td><font class=body>" + rv.getDescription() + "</font></td>");
		    sb.append("</tr>");
		    sb.append(lineSep);
		    writer.write(sb.toString());
			}
		if (filename != null) {
		    writer.write("</table>");
		}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() throws IOException {
		Writer writer = getWriter();
		StringBuilder sb = new StringBuilder();
	
		writer.write("<br>");
	
		// output the problems
		if (!errors.isEmpty()) {
		    sb.setLength(0);
		    sb.append("<table border=\"0\" width=\"80%\">");
		    sb.append("<tr id=TableHeader><td><font class=title>&nbsp;Problems found</font></td></tr>");
		    boolean colorize = false;
		    for (Report.ProcessingError error : errors) {
			if (colorize) {
			    sb.append("<tr id=RowColor1>");
			} else {
			    sb.append("<tr id=RowColor2>");
			}
			colorize = !colorize;
			sb.append("<td><font class=body>").append(error).append("\"</font></td></tr>");
		    }
		    sb.append("</table>");
		    writer.write(sb.toString());
		}
	
		writer.write(footer());
    }

    private String header() {
    	StringBuilder sb = new StringBuilder(600);
		sb.append("<html><head><title>PMD</title></head>");
		sb.append("<style type=\"text/css\">");
		sb.append("<!--" + PMD.EOL);
		sb.append("body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
				+ PMD.EOL);
		sb.append(".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
				+ PMD.EOL);
		sb.append(".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
				+ PMD.EOL);
		sb.append("#TableHeader { background-color: #003366; }" + PMD.EOL);
		sb.append("#RowColor1 { background-color: #eeeeee; }" + PMD.EOL);
		sb.append("#RowColor2 { background-color: white; }" + PMD.EOL);
		sb.append("-->");
		sb.append("</style>");
		sb.append("<body><center>");
		return sb.toString();
    }

    private String footer() {
    	return "</center></body></html>" + PMD.EOL;
    }

}