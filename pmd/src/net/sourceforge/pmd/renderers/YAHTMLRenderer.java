package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.dfa.report.ReportHTMLPrintVisitor;
import net.sourceforge.pmd.dfa.report.ReportTree;

public class YAHTMLRenderer extends AbstractRenderer {

    private String outputDir;

    public YAHTMLRenderer() {
        // TODO output destination
    }

    public YAHTMLRenderer(String outputDir) {
        this.outputDir = outputDir;
    };

    public void render(Writer writer, Report report) throws IOException {
        ReportTree tree = report.getViolationTree();
        tree.getRootNode().accept(new ReportHTMLPrintVisitor(outputDir==null?"..":outputDir));
        writer.write("<h3 align=\"center\">The HTML files are located " + 
                (outputDir==null?"above the project directory":("in '" + outputDir + '\'')) +
                ".</h3>");

    }

}
