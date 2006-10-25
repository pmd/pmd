package net.sourceforge.pmd.rules;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * An operation on an Immutable object (BigDecimal or BigInteger) won't change
 * the object itself. The result of the operation is a new object. Therefore,
 * ignoring the operation result is an error.
 */
public class UselessOperationOnImmutable extends AbstractRule {

    /**
     * These are the methods which are immutable
     */
    private static final Set targetMethods = CollectionUtil.asSet(new String[] { ".add", ".multiply", ".divide", ".subtract", ".setScale", ".negate", ".movePointLeft", ".movePointRight", ".pow", ".shiftLeft", ".shiftRight" });

    /**
     * These are the classes that the rule can apply to
     */
    private static final Set targetClasses = CollectionUtil.asSet(new String[] { "java.math.BigDecimal", "BigDecimal", "java.math.BigInteger", "BigInteger" });

    public Object visit(ASTLocalVariableDeclaration node, Object data) {

        ASTVariableDeclaratorId var = getDeclaration(node);
        if (var == null) {
            return super.visit(node, data);
        }
        String variableName = var.getImage();
        for (Iterator it = var.getUsages().iterator(); it.hasNext();) {
            NameOccurrence no = (NameOccurrence) it.next();
            ASTName sn = (ASTName) no.getLocation();
            if (!sn.jjtGetParent().jjtGetParent().jjtGetParent().getClass().equals(ASTExpression.class)) {
                String methodCall = sn.getImage().substring(variableName.length());
                if (targetMethods.contains(methodCall)) {
                    addViolation(data, sn);
                }
            }
        }
        return super.visit(node, data);
    }

    /**
     * This method checks the variable declaration if it is on a class we care
     * about. If it is, it returns the DeclaratorId
     * 
     * @param node
     *            The ASTLocalVariableDeclaration which is a problem
     * @return ASTVariableDeclaratorId
     */
    private ASTVariableDeclaratorId getDeclaration(ASTLocalVariableDeclaration node) {
        ASTType type = node.getTypeNode();
        if (targetClasses.contains(type.getTypeImage())) {
            return (ASTVariableDeclaratorId) node.jjtGetChild(1).jjtGetChild(0);
        }
        return null;
    }
}
