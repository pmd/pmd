/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule;

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
            if ((expr instanceof ASTEqualityExpression ||
                    (expr instanceof ASTRelationalExpression && ">".equals(expr.getImage())))
                && isCompareZero(expr)) {
                addViolation(data, jocc.getLocation());
            }
        }
        return data;
    }

    /**
     * We only need to report if this is comparing against 0
     * 
     * @param equality
     * @return true if this is comparing to 0 else false
     */
    private boolean isCompareZero(Node equality) {
        return checkComparison(equality, 0) || checkComparison(equality, 1);

    }

    /**
     * Checks if the equality expression passed in is of comparing against the
     * value passed in as i
     * 
     * @param equality
     * @param i
     *            The ordinal in the equality expression to check
     * @return true if the value in position i is 0, else false
     */
    private boolean checkComparison(Node equality, int i) {
	Node target = equality.jjtGetChild(i).jjtGetChild(0);
        if (target.jjtGetNumChildren() == 0) {
            return false;
        }
        target = target.jjtGetChild(0);
        return target instanceof ASTLiteral && "0".equals(target.getImage());
    }

}