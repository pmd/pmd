/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dfa.report;

@Deprecated // will be removed with PMD 7.0.0 without replacement. See net.sourceforge.pmd.lang.dfa.report.ReportTree for details.
public abstract class ReportVisitor {

    public void visit(AbstractReportNode node) {
        node.childrenAccept(this);
    }

}
