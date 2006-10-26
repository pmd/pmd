package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTRelationalExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.Iterator;
import java.util.List;

/**
 * This is an abstract rule for patterns which compare a method invocation to 0.
 * It could be further abstracted to find code that compares something to
 * another definable pattern
 * 
 * @author acaplan
 */
public abstract class AbstractInefficientZeroCheck extends AbstractRule {

    public abstract boolean appliesToClassName(String name);

    public abstract boolean isTargetMethod(NameOccurrence occ);

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        SimpleNode nameNode = node.getTypeNameNode();
        if (nameNode instanceof ASTPrimitiveType) {
            return data;
        }
        if (!appliesToClassName(node.getNameDeclaration().getTypeImage())) {
            return data;
        }

        List declars = node.getUsages();
        for (Iterator i = declars.iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence) i.next();
            if (!isTargetMethod(occ)) {
                continue;
            }
            SimpleNode expr = (SimpleNode) occ.getLocation().jjtGetParent().jjtGetParent().jjtGetParent();
            if ((expr instanceof ASTEqualityExpression ||
                    (expr instanceof ASTRelationalExpression && ">".equals(expr.getImage())))
                && isCompareZero(expr)) {
                addViolation(data, occ.getLocation());
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
    private boolean isCompareZero(SimpleNode equality) {
        return (checkComparison(equality, 0) || checkComparison(equality, 1));

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
    private boolean checkComparison(SimpleNode equality, int i) {
        SimpleNode target = (SimpleNode) equality.jjtGetChild(i).jjtGetChild(0);
        if (target.jjtGetNumChildren() == 0) {
            return false;
        }
        target = (SimpleNode) target.jjtGetChild(0);
        return (target instanceof ASTLiteral && "0".equals(target.getImage()));
    }

}