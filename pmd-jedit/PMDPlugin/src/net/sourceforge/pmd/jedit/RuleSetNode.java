package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.RuleSet;

public class RuleSetNode {

    private RuleSet rule;

    public RuleSetNode(RuleSet rule) {
        this.rule = rule;
    }

    public String toString() {
        return rule.getName();
    }

    public RuleSet getRuleSet() {
        return rule;
    }
}
