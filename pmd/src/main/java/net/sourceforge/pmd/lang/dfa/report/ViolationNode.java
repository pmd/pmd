/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa.report;

import net.sourceforge.pmd.RuleViolation;

public class ViolationNode extends AbstractReportNode {

    private RuleViolation ruleViolation;

    public ViolationNode(RuleViolation violation) {
        this.ruleViolation = violation;
    }

    public RuleViolation getRuleViolation() {
        return ruleViolation;
    }

    public boolean equalsNode(AbstractReportNode arg0) {
        if (!(arg0 instanceof ViolationNode)) {
            return false;
        }

        RuleViolation rv = ((ViolationNode) arg0).getRuleViolation();

        return rv.getFilename().equals(getRuleViolation().getFilename()) &&
        	rv.getBeginLine() == getRuleViolation().getBeginLine() &&
        	rv.getBeginColumn() == getRuleViolation().getBeginColumn() &&
        	rv.getEndLine() == getRuleViolation().getEndLine() &&
        	rv.getEndColumn()== getRuleViolation().getEndColumn() &&
        	rv.getVariableName().equals(getRuleViolation().getVariableName());
    }

}
