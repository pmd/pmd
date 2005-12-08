/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.util.StringUtil;

import java.util.Iterator;

public class HTMLRenderer extends AbstractRenderer implements Renderer {

    private String linkPrefix;

    public HTMLRenderer(String linkPrefix) {
        this.linkPrefix = linkPrefix;
    }

    public HTMLRenderer() {
        this(null);
    }

    public String render(Report report) {
        StringBuffer buf = new StringBuffer("<html><head><title>PMD</title></head><body>" + PMD.EOL);
        buf.append(renderBody(report));
        buf.append("</body></html>");
        return buf.toString();
    }

    public String renderBody(Report report) {
        StringBuffer buf = glomRuleViolations(report);
        glomProcessingErrors(report, buf);
        if (showSuppressedViolations) {
            glomSuppressions(report, buf);
        }
        return buf.toString();
    }

    private StringBuffer glomRuleViolations(Report report) {
        boolean colorize = true;
        int violationCount = 1;
        StringBuffer buf = new StringBuffer();
        buf.append("<center><h3>PMD report</h3></center>");
        buf.append("<center><h3>Problems found</h3></center>");
        buf.append("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL);
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> " + PMD.EOL);
            buf.append("<td align=\"center\">" + violationCount + "</td>" + PMD.EOL);
            buf.append("<td width=\"*%\">" + maybeWrap(rv.getFilename(), Integer.toString(rv.getNode().getBeginLine())) + "</td>" + PMD.EOL);
            buf.append("<td align=\"center\" width=\"5%\">" + Integer.toString(rv.getNode().getBeginLine()) + "</td>" + PMD.EOL);

            String d = rv.getDescription();
            d = StringUtil.replaceString(d, '&', "&amp;");
            d = StringUtil.replaceString(d, '<', "&lt;");
            d = StringUtil.replaceString(d, '>', "&gt;");
            if (rv.getRule().getExternalInfoUrl() != null && rv.getRule().getExternalInfoUrl().length() != 0) {
                d = "<a href=\"" + rv.getRule().getExternalInfoUrl() + "\">" + d + "</a>";
            }
            buf.append("<td width=\"*\">" + d + "</td>" + PMD.EOL);
            buf.append("</tr>" + PMD.EOL);
            violationCount++;
        }
        if (violationCount > 0) {
            buf.append("</table>");
        }
        return buf;
    }

    private void glomProcessingErrors(Report report, StringBuffer buf) {
        boolean colorize = true;
        int violationCount;
        // errors
        if (report.errors().hasNext()) {
            buf.append("<hr/>");
            buf.append("<center><h3>Processing errors</h3></center>");
            buf.append("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>File</th><th>Problem</th></tr>" + PMD.EOL);
        }
        violationCount = 0;
        for (Iterator i = report.errors(); i.hasNext();) {
            Report.ProcessingError pe = (Report.ProcessingError) i.next();
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> " + PMD.EOL);
            buf.append("<td align=\"center\">" + pe.getFile() + "</td>" + PMD.EOL);
            buf.append("<td align=\"center\">" + pe.getMsg() + "</td>" + PMD.EOL);
            buf.append("</tr>" + PMD.EOL);
            violationCount++;
        }
        if (violationCount > 0) {
            buf.append("</table>");
        }
    }

    private void glomSuppressions(Report report, StringBuffer buf) {
        boolean colorize = true;
        if (!report.getSuppressedRuleViolations().isEmpty()) {
            buf.append("<hr/>");
            buf.append("<center><h3>Suppressed warnings</h3></center>");
            buf.append("<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th></tr>" + PMD.EOL);
        }
        for (Iterator i = report.getSuppressedRuleViolations().iterator(); i.hasNext();) {
            Report.SuppressedViolation sv = (Report.SuppressedViolation) i.next();
            buf.append("<tr");
            if (colorize) {
                buf.append(" bgcolor=\"lightgrey\"");
            }
            colorize = !colorize;
            buf.append("> " + PMD.EOL);
            buf.append("<td align=\"left\">" + sv.getRuleViolation().getFilename() + "</td>" + PMD.EOL);
            buf.append("<td align=\"center\">" + sv.getRuleViolation().getNode().getBeginLine() + "</td>" + PMD.EOL);
            buf.append("<td align=\"center\">" + sv.getRuleViolation().getRule().getName() + "</td>" + PMD.EOL);
            buf.append("<td align=\"center\">" + (sv.suppressedByNOPMD() ? "NOPMD" : "Annotation") + "</td>" + PMD.EOL);
            buf.append("</tr>" + PMD.EOL);
        }
        if (!report.getSuppressedRuleViolations().isEmpty()) {
            buf.append("</table>");
        }
    }

    private String maybeWrap(String filename, String line) {
        if (linkPrefix == null) {
            return filename;
        }
        String newFileName = filename.substring(0, filename.indexOf(".java"));
        return "<a href=\"" + linkPrefix + newFileName + ".html#" + line + "\">" + newFileName + "</a>";
    }
}
