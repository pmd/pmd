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
import java.util.Iterator;
import java.util.List;

public class HTMLRenderer extends OnTheFlyRenderer {

    private final String linkPrefix;
    private final String linePrefix;

    private int violationCount = 1;
    boolean colorize = true;

    public HTMLRenderer(String linkPrefix, String linePrefix) {
        this.linkPrefix = linkPrefix;
        this.linePrefix = linePrefix;
        
    }

    public HTMLRenderer() {
        this(null, null);
    }

    public void renderBody(Writer writer, Report report) throws IOException {
        writer.write("<center><h3>PMD report</h3></center>");
        writer.write("<center><h3>Problems found</h3></center>");
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL);
        setWriter(writer);
        renderFileReport(report);
        writer.write("</table>");
        glomProcessingErrors(writer, errors);
        if (showSuppressedViolations) {
            glomSuppressions(writer, suppressed);
        }
    }

    public void start() throws IOException {
        Writer writer = getWriter();
        writer.write("<html><head><title>PMD</title></head><body>" + PMD.EOL);
        writer.write("<center><h3>PMD report</h3></center>");
        writer.write("<center><h3>Problems found</h3></center>");
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL);
    }

    public void renderFileViolations(Iterator<IRuleViolation> violations) throws IOException {
        Writer writer = getWriter();
        glomIRuleViolations(writer, violations);
    }

    public void end() throws IOException {
        Writer writer = getWriter();
        writer.write("</table>");
        glomProcessingErrors(writer, errors);
        if (showSuppressedViolations) {
            glomSuppressions(writer, suppressed);
        }
        writer.write("</body></html>");
    }

    private void glomIRuleViolations(Writer writer, Iterator<IRuleViolation> violations) throws IOException {
        StringBuffer buf = new StringBuffer(500);
        while (violations.hasNext()) {
            IRuleViolation rv = violations.next();
            buf.setLength(0);
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> " + PMD.EOL);
            buf.append("<td align=\"center\">" + violationCount + "</td>" + PMD.EOL);
            buf.append("<td width=\"*%\">" + maybeWrap(rv.getFilename(),linePrefix==null?"":linePrefix + Integer.toString(rv.getBeginLine())) + "</td>" + PMD.EOL);
            buf.append("<td align=\"center\" width=\"5%\">" + Integer.toString(rv.getBeginLine()) + "</td>" + PMD.EOL);

            String d = StringUtil.htmlEncode(rv.getDescription());
            
            if (rv.getRule().getExternalInfoUrl() != null && rv.getRule().getExternalInfoUrl().length() != 0) {
                d = "<a href=\"" + rv.getRule().getExternalInfoUrl() + "\">" + d + "</a>";
            }
            buf.append("<td width=\"*\">" + d + "</td>" + PMD.EOL);
            buf.append("</tr>" + PMD.EOL);
            writer.write(buf.toString());
            violationCount++;
        }
    }

    private void glomProcessingErrors(Writer writer, List<Report.ProcessingError> errors) throws IOException {
        if (!errors.isEmpty()) {
            writer.write("<hr/>");
            writer.write("<center><h3>Processing errors</h3></center>");
            writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>File</th><th>Problem</th></tr>" + PMD.EOL);
    
            StringBuffer buf = new StringBuffer(500);
            boolean colorize = true;
            for (Report.ProcessingError pe: errors) {
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
    }

    private void glomSuppressions(Writer writer, List<Report.SuppressedViolation> suppressed) throws IOException {
        if (!suppressed.isEmpty()) {
            writer.write("<hr/>");
            writer.write("<center><h3>Suppressed warnings</h3></center>");
            writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>" + PMD.EOL);

            StringBuffer buf = new StringBuffer(500);
            boolean colorize = true;
            for (Report.SuppressedViolation sv: suppressed) {
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
                buf.append("<td align=\"center\">" + (sv.suppressedByNOPMD() ? "NOPMD" : "Annotation") + "</td>" + PMD.EOL);
                buf.append("<td align=\"center\">" + (sv.getUserMessage() == null ? "" : sv.getUserMessage()) + "</td>" + PMD.EOL);
                buf.append("</tr>" + PMD.EOL);
                writer.write(buf.toString());
            }
            writer.write("</table>");
        }
    }

    private String maybeWrap(String filename, String line) {
        if (linkPrefix == null) {
            return filename;
        }
        String newFileName = filename.substring(0, filename.lastIndexOf('.')).replace('\\', '/');
        return "<a href=\"" + linkPrefix + newFileName + ".html#" + line + "\">" + newFileName + "</a>";
    }
}
