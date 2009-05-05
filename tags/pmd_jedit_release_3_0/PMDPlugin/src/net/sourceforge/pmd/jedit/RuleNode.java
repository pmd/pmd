package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.Rule;

public class RuleNode {

    private Rule rule;

    public RuleNode(Rule rule) {
        this.rule = rule;
    }

    public String toString() {
        return rule.getName();
    }

    public Rule getRule() {
        return rule;
    }
}
