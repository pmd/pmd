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

public class HTMLRenderer extends AbstractRenderer {

    private String linkPrefix;
    private String linePrefix;

    public HTMLRenderer(String linkPrefix, String linePrefix) {
        this.linkPrefix = linkPrefix;
        this.linePrefix = linePrefix;
        
    }

    public HTMLRenderer() {
        this(null, null);
    }

    public void render(Writer writer, Report report) throws IOException {
        writer.write("<html><head><title>PMD</title></head><body>" + PMD.EOL);
        renderBody(writer, report);
        writer.write("</body></html>");
    }

    public void renderBody(Writer writer, Report report) throws IOException {
        glomIRuleViolations(writer, report);
        glomProcessingErrors(writer, report);
        if (showSuppressedViolations) {
            glomSuppressions(writer, report);
        }
    }
    
    private void glomIRuleViolations(Writer writer, Report report) throws IOException {
        boolean colorize = true;
        int violationCount = 1;
        StringBuffer buf = new StringBuffer(500);
        writer.write("<center><h3>PMD report</h3></center>");
        writer.write("<center><h3>Problems found</h3></center>");
        writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL);
        for (Iterator i = report.iterator(); i.hasNext();) {
            IRuleViolation rv = (IRuleViolation) i.next();
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
        if (violationCount > 0) {
            writer.write("</table>");
        }
    }

    private void glomProcessingErrors(Writer writer, Report report) throws IOException {
        boolean colorize = true;
        int violationCount;
        // errors
        if (report.errors().hasNext()) {
            writer.write("<hr/>");
            writer.write("<center><h3>Processing errors</h3></center>");
            writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>File</th><th>Problem</th></tr>" + PMD.EOL);
        }
        violationCount = 0;
        StringBuffer buf = new StringBuffer(500);
        for (Iterator i = report.errors(); i.hasNext();) {
            Report.ProcessingError pe = (Report.ProcessingError) i.next();
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
            violationCount++;
        }
        if (violationCount > 0) {
            writer.write("</table>");
        }
    }

    private void glomSuppressions(Writer writer, Report report) throws IOException {
        boolean colorize = true;
        boolean hasSuppressedViolations = !report.getSuppressedRuleViolations().isEmpty();
        if (hasSuppressedViolations) {
            writer.write("<hr/>");
            writer.write("<center><h3>Suppressed warnings</h3></center>");
            writer.write("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>" + PMD.EOL);
        }
        Report.SuppressedViolation sv;
        StringBuffer buf = new StringBuffer(500);
        for (Iterator i = report.getSuppressedRuleViolations().iterator(); i.hasNext();) {
            sv = (Report.SuppressedViolation) i.next();
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
        if (hasSuppressedViolations) {
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
