package net.sourceforge.pmd;


public class RuleViolation {

    private int line;
    private Rule rule;
    private String description;
    private String filename;

    public RuleViolation(Rule rule, int line, String filename) {
        this(rule, line, rule.getMessage(), filename);
    }

    public RuleViolation(Rule rule, int line, String specificDescription, String filename) {
        this.line = line;
        this.rule = rule;
        this.description = specificDescription;
        this.filename = filename;
    }

    public Rule getRule() {
        return rule;
    }

    public int getLine() {
        return line;
    }

    public String getDescription() {
        return description;
    }

    public String getFilename() {
        return filename;
    }
}
