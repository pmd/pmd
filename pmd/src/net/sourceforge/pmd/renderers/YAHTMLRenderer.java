package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.dfa.report.ReportHTMLPrintVisitor;
import net.sourceforge.pmd.dfa.report.ReportTree;

public class YAHTMLRenderer implements Renderer {

    public String render(Report report) {
        ReportTree tree = report.getViolationTree();
        tree.getRootNode().accept(new ReportHTMLPrintVisitor());
        return "<h3 align=\"center\">The HTML files are created above the project directory.</h3>";
    }

}
