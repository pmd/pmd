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

            if (r1.getLine() == r2.getLine()) {
                return 1;
            }
            
            // line number diff maps nicely to compare()
            return r1.getLine() - r2.getLine();
        }
    }

    private int line;
    private Rule rule;
    private String description;
    private String filename;
    private int line2 = -1;
    private String packageName;
    private String className;
    private String methodName;
    private String variableName;
    private int beginColumn = -1;
    private int endColumn = -1;

    /**
     * gets the character in the line where the violation starts
     * @return a greater than or zero if set and a negative value if not available
     */
    public final int getBeginColumn() {
        return beginColumn;
    }
    /**
     * gets the character in the line where the violation ends
     * @return a greater than or zero if set and a negative value if not available
     */
    public final int getEndColumn() {
        return endColumn;
    }
    /**
     * sets both beginColumn and endColumn
     * @param begin
     * @param end
     */
    public void setColumnInfo(int begin, int end) {
        this.beginColumn = begin;
        this.endColumn = end;
    }
    public RuleViolation(Rule rule, int line, RuleContext ctx, String packageName, String className, String methodName) {
        this(rule, line, rule.getMessage(), ctx, packageName, className, methodName);
    }

    public RuleViolation(Rule rule, int line, String specificDescription, RuleContext ctx, String packageName, String className, String methodName) {
        this(rule, line, -1, "", specificDescription, ctx, packageName, className, methodName);
    }

    public RuleViolation(Rule rule, int line, int line2, String variableName, String specificDescription, RuleContext ctx, String packageName, String className, String methodName) {
        this.line = line;
        this.line2 = line2;
        this.rule = rule;
        this.description = specificDescription;
        this.filename = ctx.getSourceCodeFilename();
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.variableName = variableName;
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

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getLine2() {
        return line2;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVariableName() {
        return variableName;
    }

    public String toString() {
        return getFilename() + ":" + getRule() + ":" + getDescription() + ":" + getLine();
    }
}
