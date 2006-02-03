package net.sourceforge.pmd.rules.strings;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

/**
 * This rule finds code which inefficiently determines empty strings. This code
 * 
 * <pre>
 *     if(str.trim().length()==0){....
 * </pre>
 * 
 * is quite inefficient as trim() causes a new String to be created. Smarter
 * code to check for an empty string would be:
 * 
 * <pre>
 * Character.isWhitespace(str.charAt(i));
 * </pre>
 * 
 * @author acaplan
 * 
 */
public class InefficientEmptyStringCheck extends AbstractRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        SimpleNode nameNode = node.getTypeNameNode();
        if (nameNode instanceof ASTPrimitiveType) {
            return data;
        }

        if (!"String".equals(node.getNameDeclaration().getTypeImage())) {
            return data;
        }

        List declars = node.getUsages();
        for (Iterator i = declars.iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence) i.next();
            if (!isStringLength(occ)) {
                continue;
            }
            ASTEqualityExpression equality = (ASTEqualityExpression) occ
                    .getLocation().getFirstParentOfType(
                            ASTEqualityExpression.class);
            if (equality != null && isCompareZero(equality)) {
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
    private boolean isCompareZero(ASTEqualityExpression equality) {
        return (checkComparison(equality, 0) || checkComparison(equality, 1));

    }

    /**
     * Determine if we're dealing with String.length method
     * 
     * @param occ
     *            The name occurance
     * @return true if it's String.length, else false
     */
    private boolean isStringLength(NameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null
                && occ.getNameForWhichThisIsAQualifier().getImage().indexOf(
                        "trim") != -1) {
            Node pExpression = occ.getLocation().jjtGetParent().jjtGetParent();
            if (pExpression.jjtGetNumChildren() >= 3
                    && "length"
                            .equals(((SimpleNode) pExpression.jjtGetChild(2))
                                    .getImage())) {
                return true;
            }
        }
        return false;
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
    private boolean checkComparison(ASTEqualityExpression equality, int i) {
        return (equality.jjtGetChild(i).jjtGetChild(0).jjtGetChild(0) instanceof ASTLiteral && "0"
                .equals(((SimpleNode) equality.jjtGetChild(i).jjtGetChild(0)
                        .jjtGetChild(0)).getImage()));
    }

}