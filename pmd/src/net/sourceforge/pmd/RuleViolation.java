/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd;

import java.util.Comparator;

public class RuleViolation {

    public static class RuleViolationComparator implements Comparator {
        //
        // Changed logic of Comparator so that rules in the same file
        // get grouped together in the output report.
        // DDP 7/11/2002
        //
        public int compare(Object o1, Object o2) {
            RuleViolation r1 = (RuleViolation) o1;
            RuleViolation r2 = (RuleViolation) o2;
            if (!r1.getFilename().equals(r2.getFilename())) {
                return r1.getFilename().compareTo(r2.getFilename());
            }

            if (r1.getLine() != r2.getLine())
                return r1.getLine() - r2.getLine();

            if (r1.getDescription() != null && r2.getDescription() != null && !r1.getDescription().equals(r2.getDescription())) {
                return r1.getDescription().compareTo(r2.getDescription());
            }
            // line number diff maps nicely to compare()
            return r1.getLine() - r2.getLine();
        }
    }

    private int line;
    private Rule rule;
    private String description;
    private String filename;

    public RuleViolation(Rule rule, int line, RuleContext ctx) {
        this(rule, line, rule.getMessage(), ctx);
    }

    public RuleViolation(Rule rule, int line, String specificDescription, RuleContext ctx) {
        this.line = line;
        this.rule = rule;
        this.description = specificDescription;
        this.filename = ctx.getSourceCodeFilename();
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
