/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa.report;

public abstract class ReportVisitor {

    public void visit(AbstractReportNode node) {
        node.childrenAccept(this);
    }

}
