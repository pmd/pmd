package net.sourceforge.pmd;


public class RuleViolation {

    private int line;
    private Rule rule;
    private String description;

    public RuleViolation(Rule rule, int line) {
        this(rule, line, rule.getDescription());
    }

    public RuleViolation(Rule rule, int line, String specificDescription) {
        this.line = line;
        this.rule = rule;
        this.description = specificDescription;
    }

    public String getText() {
        return rule.getName() +":" + description + ":" + line;
    }

    public Rule getRule() {
        return rule;
    }

    public String getXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<ruleviolation>");
        buf.append("<line>" + Integer.toString(line) + "</line>");
        buf.append("<description>" + description + "</description>");
        buf.append("</ruleviolation>");
        return buf.toString();
    }
}
