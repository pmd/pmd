/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.MethodScope;

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

            if (r1.getNode().getBeginLine() != r2.getNode().getBeginLine())
                return r1.getNode().getBeginLine() - r2.getNode().getBeginLine();

            if (r1.getDescription() != null && r2.getDescription() != null && !r1.getDescription().equals(r2.getDescription())) {
                return r1.getDescription().compareTo(r2.getDescription());
            }

            if (r1.getNode().getBeginLine() == r2.getNode().getBeginLine()) {
                return 1;
            }
            
            // line number diff maps nicely to compare()
            return r1.getNode().getBeginLine() - r2.getNode().getBeginLine();
        }
    }

    private Rule rule;
    private String description;
    private String filename;
    private SimpleNode node;

    public RuleViolation(Rule rule, RuleContext ctx, SimpleNode node) {
        this(rule, ctx, node, rule.getMessage());
    }

    public RuleViolation(Rule rule, RuleContext ctx, SimpleNode node, String specificMsg) {
        this.rule = rule;
        this.node = node;
        this.filename = ctx.getSourceCodeFilename();
        this.description = specificMsg;
    }

    public Rule getRule() {
        return rule;
    }

    public SimpleNode getNode() {
        return node;
    }

    public String getDescription() {
        return description;
    }

    public String getFilename() {
        return filename;
    }

    public String getClassName() {
        String className;
        if (node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) == null) {
            // This takes care of nodes which are outside a class definition - i.e., import declarations
            className = "";
        } else {
             // default to symbol table lookup
            className = node.getScope().getEnclosingClassScope().getClassName() == null ? "" : node.getScope().getEnclosingClassScope().getClassName();
        }
        return className;
    }

    public String getMethodName() {
        return node.getFirstParentOfType(ASTMethodDeclaration.class) == null ? "" : ((MethodScope)node.getScope().getEnclosingMethodScope()).getName();
    }

    public String getPackageName() {
        return node.getScope().getEnclosingSourceFileScope().getPackageName() == null ? "" : node.getScope().getEnclosingSourceFileScope().getPackageName();
    }

    public String getVariableName() {
        return "";
    }

    public String toString() {
        return getFilename() + ":" + getRule() + ":" + getDescription() + ":" + node.getBeginLine();
    }

}
