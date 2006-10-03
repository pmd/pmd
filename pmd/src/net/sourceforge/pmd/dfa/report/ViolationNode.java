package net.sourceforge.pmd.dfa.report;

import net.sourceforge.pmd.IRuleViolation;

public class ViolationNode extends AbstractReportNode {

    private IRuleViolation ruleViolation;

    public ViolationNode(IRuleViolation violation) {
        this.ruleViolation = violation;
    }

    public IRuleViolation getRuleViolation() {
        return ruleViolation;
    }

    public boolean equalsNode(AbstractReportNode arg0) {
        if (!(arg0 instanceof ViolationNode)) {
            return false;
        }

        IRuleViolation rv = ((ViolationNode) arg0).getRuleViolation();

        return rv.getFilename().equals(getRuleViolation().getFilename()) &&
        	rv.getBeginLine() == getRuleViolation().getBeginLine() &&
        	rv.getVariableName().equals(getRuleViolation().getVariableName());
    }

}
