/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * This is an abstract rule for patterns which compare a method invocation to 0.
 * It could be further abstracted to find code that compares something to
 * another definable pattern
 * 
 * @author acaplan
 */
public abstract class AbstractInefficientZeroCheck extends AbstractJavaRule {

    public abstract boolean appliesToClassName(String name);

    public abstract boolean isTargetMethod(JavaNameOccurrence occ);

    public List<String> getComparisonTargets() {
        return Arrays.asList("0");
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        Node nameNode = node.getTypeNameNode();
        if (nameNode == null
            || nameNode instanceof ASTPrimitiveType
            || !appliesToClassName(node.getNameDeclaration().getTypeImage())) {
            return data;
        }

        List<NameOccurrence> declars = node.getUsages();
        for (NameOccurrence occ: declars) {
            JavaNameOccurrence jocc = (JavaNameOccurrence)occ;
            if (!isTargetMethod(jocc)) {
                continue;
            }
            Node expr = jocc.getLocation().jjtGetParent().jjtGetParent().jjtGetParent();
            checkNodeAndReport(data, jocc.getLocation(), expr);
        }
        return data;
    }

    /**
     * Checks whether the given expression is a equality/relation expression that
     * compares with a size() call.
     * 
     * @param data the rule context
     * @param location the node location to report
     * @param expr the ==, <, > expression
     */
    protected void checkNodeAndReport(Object data, Node location, Node expr) {
        if ((expr instanceof ASTEqualityExpression
            || (expr instanceof ASTRelationalExpression
                    && (">".equals(expr.getImage()) || "<".equals(expr.getImage()))))
            && isCompare(expr)) {
            addViolation(data, location);
        }
    }

    /**
     * We only need to report if this is comparing against one of the comparison targets
     * 
     * @param equality
     * @return true if this is comparing to one of the comparison targets else false
     * @see #getComparisonTargets()
     */
    private boolean isCompare(Node equality) {
        return checkComparison(equality, 0) || checkComparison(equality, 1);

    }

    /**
     * Checks if the equality expression passed in is of comparing against the
     * value passed in as i
     * 
     * @param equality
     * @param i
     *            The ordinal in the equality expression to check
     * @return true if the value in position i is one of the comparison targets, else false
     * @see #getComparisonTargets()
     */
    private boolean checkComparison(Node equality, int i) {
	Node target = equality.jjtGetChild(i).jjtGetChild(0);
        if (target.jjtGetNumChildren() == 0) {
            return false;
        }
        target = target.jjtGetChild(0);
        return target instanceof ASTLiteral && getComparisonTargets().indexOf(target.getImage()) > -1;
    }

}