package net.sourceforge.pmd.lang.dfa.report;

public abstract class ReportVisitor {

    public void visit(AbstractReportNode node) {
        node.childrenAccept(this);
    }

}
