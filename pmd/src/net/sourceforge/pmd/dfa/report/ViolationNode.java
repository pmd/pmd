package net.sourceforge.pmd.dfa.report;

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
        if(!(arg0 instanceof ViolationNode)) {
            return false;
        }

        ViolationNode vn = (ViolationNode)arg0;

        return vn.getRuleViolation().getFilename().equals(this.getRuleViolation().getFilename()) &&
            vn.getRuleViolation().getLine() == this.getRuleViolation().getLine() &&
            vn.getRuleViolation().getVariableName().equals(this.getRuleViolation().getVariableName());
    }

}
