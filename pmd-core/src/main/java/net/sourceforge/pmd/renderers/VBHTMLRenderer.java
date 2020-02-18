/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

/**
 * Renderer to another HTML format.
 *
 * @author Vladimir
 */
public class VBHTMLRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "vbhtml";

    public VBHTMLRenderer() {
        super(NAME, "Vladimir Bossicard HTML format.");
    }

    @Override
    public String defaultFileExtension() {
        return "vb.html";
    }

    @Override
    public void start() throws IOException {
        getWriter().write(header());
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        if (!violations.hasNext()) {
            return;
        }

        StringBuilder sb = new StringBuilder(500);
        String filename = null;
        String lineSep = PMD.EOL;

        boolean colorize = false;
        while (violations.hasNext()) {
            sb.setLength(0);
            RuleViolation rv = violations.next();
            String nextFilename = determineFileName(rv.getFilename());
            if (!nextFilename.equals(filename)) { // New File
                if (filename != null) {
                    sb.append("</table></br>");
                    colorize = false;
                }
                filename = nextFilename;
                sb.append("<table border=\"0\" width=\"80%\">");
                sb.append("<tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;").append(filename)
                        .append("</font></tr>");
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

    @Override
    public void end() throws IOException {
        StringBuilder sb = new StringBuilder();

        writer.write("<br>");

        // output the problems
        if (!errors.isEmpty()) {
            sb.setLength(0);
            sb.append("<table border=\"0\" width=\"80%\">");
            sb.append("<tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;Problems found</font></td></tr>");
            boolean colorize = false;
            for (Report.ProcessingError error : errors) {
                if (colorize) {
                    sb.append("<tr id=RowColor1>");
                } else {
                    sb.append("<tr id=RowColor2>");
                }
                colorize = !colorize;
                sb.append("<td><font class=body>").append(determineFileName(error.getFile())).append("</font></td>");
                sb.append("<td><font class=body><pre>").append(error.getDetail()).append("</pre></font></td></tr>");
            }
            sb.append("</table>");
            writer.write(sb.toString());
        }

        if (!configErrors.isEmpty()) {
            sb.setLength(0);
            sb.append("<table border=\"0\" width=\"80%\">");
            sb.append("<tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;Configuration problems found</font></td></tr>");
            boolean colorize = false;
            for (Report.ConfigurationError error : configErrors) {
                if (colorize) {
                    sb.append("<tr id=RowColor1>");
                } else {
                    sb.append("<tr id=RowColor2>");
                }
                colorize = !colorize;
                sb.append("<td><font class=body>").append(error.rule().getName()).append("</font></td>");
                sb.append("<td><font class=body>").append(error.issue()).append("</font></td></tr>");
            }
            sb.append("</table>");
            writer.write(sb.toString());
        }

        writer.write(footer());
    }

    private String header() {
        StringBuilder sb = new StringBuilder(600).append("<html><head><title>PMD</title></head>")
                .append("<style type=\"text/css\">").append("<!--" + PMD.EOL)
                .append("body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }")
                .append(PMD.EOL)
                .append(".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }")
                .append(PMD.EOL)
                .append(".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }")
                .append(PMD.EOL).append("#TableHeader { background-color: #003366; }").append(PMD.EOL)
                .append("#RowColor1 { background-color: #eeeeee; }").append(PMD.EOL)
                .append("#RowColor2 { background-color: white; }").append(PMD.EOL).append("-->").append("</style>")
                .append("<body><center>");
        return sb.toString();
    }

    private String footer() {
        return "</center></body></html>" + PMD.EOL;
    }

}
