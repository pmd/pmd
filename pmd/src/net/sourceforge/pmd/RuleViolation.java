package net.sourceforge.pmd;


public class RuleViolation {

    private int line;
    private Rule rule;
    private String specificDescription;

    public RuleViolation(Rule rule, int line) {
        this(rule, line, rule.getDescription());
    }

    public RuleViolation(Rule rule, int line, String specificDescription) {
        this.line = line;
        this.rule = rule;
        this.specificDescription = specificDescription;
    }

    public String getText() {
        return rule.getName() +":" + specificDescription + ":" + line;
    }

    public Rule getRule() {
        return rule;
    }
}
