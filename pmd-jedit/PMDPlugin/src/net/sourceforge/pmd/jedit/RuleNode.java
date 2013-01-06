package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.Rule;

public class RuleNode {

    private Rule rule;
    private final String name;

    public RuleNode( Rule rule ) {
        this.rule = rule;
        StringBuilder sb = new StringBuilder( rule.getName() );
        sb.append(" (").append(rule.getPriority().toString()).append(')');
        name = sb.toString();
    }

    public String toString() {
        return name;
    }

    public Rule getRule() {
        return rule;
    }
}