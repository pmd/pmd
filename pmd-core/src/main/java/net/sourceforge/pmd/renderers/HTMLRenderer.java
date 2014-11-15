/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Renderer to basic HTML format.
 * 
 * FIXME: this class should just work with the XMLRenderer and then apply 
 * 		an XSLT transformation + stylesheet. No need to hard-code HTML
 * 		markup here.
 */
public class HTMLRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "html";

    public static final StringProperty LINE_PREFIX = new StringProperty("linePrefix", "Prefix for line number anchor in the source file.", null, 1);
    public static final StringProperty LINK_PREFIX = new StringProperty("linkPrefix", "Path to HTML source.", null, 0);

    private String linkPrefix;
    private String linePrefix;

    private int violationCount = 1;
    boolean colorize = true;

    public HTMLRenderer() {
		super(NAME, "HTML format");
	
		definePropertyDescriptor(LINK_PREFIX);
		definePropertyDescriptor(LINE_PREFIX);
    }

    public String defaultFileExtension() { return "html"; }
    
    /**
     * Write the body of the main body of the HTML content.
     *
     * @param writer
     * @param report
     * @throws IOException
     */
    public void renderBody(Writer writer, Report report) throws IOException {
		linkPrefix = getProperty(LINK_PREFIX);
		linePrefix = getProperty(LINE_PREFIX);

		writer.write("<center><h3>PMD report</h3></center>");
		writer.write("<center><h3>Problems found</h3></center>");
		writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL
			+ "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL);
		setWriter(writer);
		renderFileReport(report);
		writer.write("</table>");
		glomProcessingErrors(writer, errors);
		if (showSuppressedViolations) {
		    glomSuppressions(writer, suppressed);
		}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
		Writer writer = getWriter();
		writer.write("<html><head><title>PMD</title></head><body>" + PMD.EOL);
		writer.write("<center><h3>PMD report</h3></center>");
		writer.write("<center><h3>Problems found</h3></center>");
		writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL
			+ "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
		Writer writer = getWriter();
		glomRuleViolations(writer, violations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() throws IOException {
		Writer writer = getWriter();
		writer.write("</table>");
		glomProcessingErrors(writer, errors);
		if (showSuppressedViolations) {
		    glomSuppressions(writer, suppressed);
		}
		writer.write("</body></html>" + PMD.EOL);
    }

    private void glomRuleViolations(Writer writer, Iterator<RuleViolation> violations) throws IOException {
	    
    	StringBuilder buf = new StringBuilder(500);
	    
		while (violations.hasNext()) {
		    RuleViolation rv = violations.next();
		    buf.setLength(0);
		    buf.append("<tr");
		    if (colorize) {
		    	buf.append(" bgcolor=\"lightgrey\"");
		    }
		    colorize = !colorize;
		    buf.append("> " + PMD.EOL);
		    buf.append("<td align=\"center\">" + violationCount + "</td>" + PMD.EOL);
		    buf.append("<td width=\"*%\">"
			    + maybeWrap(rv.getFilename(), linePrefix == null ? "" : linePrefix
				    + Integer.toString(rv.getBeginLine())) + "</td>" + PMD.EOL);
		    buf.append("<td align=\"center\" width=\"5%\">" + Integer.toString(rv.getBeginLine()) + "</td>" + PMD.EOL);
	
		    String d = StringUtil.htmlEncode(rv.getDescription());
	
		    String infoUrl = rv.getRule().getExternalInfoUrl();
		    if (StringUtil.isNotEmpty(infoUrl)) {
				d = "<a href=\"" + infoUrl + "\">" + d + "</a>";
			    }
		    buf.append("<td width=\"*\">" + d + "</td>" + PMD.EOL);
		    buf.append("</tr>" + PMD.EOL);
		    writer.write(buf.toString());
		    violationCount++;
			}
    }

    private void glomProcessingErrors(Writer writer, List<Report.ProcessingError> errors) throws IOException {
	
    	if (errors.isEmpty()) {
    	    return;
    	}
    	
	    writer.write("<hr/>");
	    writer.write("<center><h3>Processing errors</h3></center>");
	    writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL
		    + "<th>File</th><th>Problem</th></tr>" + PMD.EOL);

	    StringBuffer buf = new StringBuffer(500);
	    boolean colorize = true;
	    for (Report.ProcessingError pe : errors) {
			buf.setLength(0);
			buf.append("<tr");
			if (colorize) {
			    buf.append(" bgcolor=\"lightgrey\"");
			}
			colorize = !colorize;
			buf.append("> " + PMD.EOL);
			buf.append("<td>" + pe.getFile() + "</td>" + PMD.EOL);
			buf.append("<td>" + pe.getMsg() + "</td>" + PMD.EOL);
			buf.append("</tr>" + PMD.EOL);
			writer.write(buf.toString());	
		    }
	    writer.write("</table>");
    }

    private void glomSuppressions(Writer writer, List<Report.SuppressedViolation> suppressed) throws IOException {
		if (suppressed.isEmpty()) {
		    return;
		}

		writer.write("<hr/>");
		writer.write("<center><h3>Suppressed warnings</h3></center>");
		writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL
			    + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>"
			    + PMD.EOL);
	
		StringBuilder buf = new StringBuilder(500);
		boolean colorize = true;
		for (Report.SuppressedViolation sv : suppressed) {
			buf.setLength(0);
			buf.append("<tr");
			if (colorize) {
			    buf.append(" bgcolor=\"lightgrey\"");
			}
			colorize = !colorize;
			buf.append("> " + PMD.EOL);
			buf.append("<td align=\"left\">" + sv.getRuleViolation().getFilename() + "</td>" + PMD.EOL);
			buf.append("<td align=\"center\">" + sv.getRuleViolation().getBeginLine() + "</td>" + PMD.EOL);
			buf.append("<td align=\"center\">" + sv.getRuleViolation().getRule().getName() + "</td>" + PMD.EOL);
			buf.append("<td align=\"center\">" + (sv.suppressedByNOPMD() ? "NOPMD" : "Annotation") + "</td>"
					+ PMD.EOL);
			buf.append("<td align=\"center\">" + (sv.getUserMessage() == null ? "" : sv.getUserMessage()) + "</td>"
					+ PMD.EOL);
			buf.append("</tr>" + PMD.EOL);
			writer.write(buf.toString());
			}
		writer.write("</table>");
    }

    private String maybeWrap(String filename, String line) {
		if (StringUtil.isEmpty(linkPrefix)) {
		    return filename;
		}
		String newFileName = filename;
		int index = filename.lastIndexOf('.');
		if (index >= 0) {
		    newFileName = filename.substring(0, index).replace('\\', '/');
		}
		return "<a href=\"" + linkPrefix + newFileName + ".html#" + line + "\">" + newFileName + "</a>";
    }
}
