package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.dfa.report.ReportHTMLPrintVisitor;
import net.sourceforge.pmd.dfa.report.ReportTree;

public class YAHTMLRenderer extends AbstractRenderer {

    public void render(Writer writer, Report report) throws IOException {
        ReportTree tree = report.getViolationTree();
        tree.getRootNode().accept(new ReportHTMLPrintVisitor());
        writer.write("<h3 align=\"center\">The HTML files are created above the project directory.</h3>");
    }

}
