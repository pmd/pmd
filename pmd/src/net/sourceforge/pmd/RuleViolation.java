package net.sourceforge.pmd;

import java.util.Comparator;


public class RuleViolation {

    public static class RuleViolationComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            RuleViolation r1 = (RuleViolation)o1;
            RuleViolation r2 = (RuleViolation)o2;
            // if it's in a different file, they're equal as far as we're concerned
            if (!r1.getFilename().equals(r2.getFilename())) {
                return 0;
            }
            // line number diff maps nicely to compare()
            return r1.getLine() - r2.getLine();
        }
    }

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
