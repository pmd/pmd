package net.sourceforge.pmd.rules.basic;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;

/**
 * Avoid instantiating Boolean objects; you can reference Boolean.TRUE,
 * Boolean.FALSE, or call Boolean.valueOf() instead.
 * 
 * <pre>
 *  public class Foo { 
 *       Boolean bar = new Boolean("true");    // just do a Boolean
 *       bar = Boolean.TRUE;                   //ok
 *       Boolean buz = Boolean.valueOf(false); // just do a Boolean buz = Boolean.FALSE; 
 *  }
 * </pre>
 */
public class BooleanInstantiation extends AbstractRule {

    public Object visit(ASTAllocationExpression node, Object data) {

        if (node.findChildrenOfType(ASTArrayDimsAndInits.class).size() > 0) {
            return super.visit(node, data);
        }
        String typeName = ((ASTClassOrInterfaceType) node.jjtGetChild(0)).getImage();
        if ("Boolean".equals(typeName) || "java.lang.Boolean".equals(typeName)) {
            super.addViolation(data, node);
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTPrimaryPrefix node, Object data) {

        if (node.jjtGetNumChildren() == 0 || !node.jjtGetChild(0).getClass().equals(ASTName.class)) {
            return super.visit(node, data);
        }

        if ("Boolean.valueOf".equals(((ASTName) node.jjtGetChild(0)).getImage())
                || "java.lang.Boolean.valueOf".equals(((ASTName) node.jjtGetChild(0)).getImage())) {
            ASTPrimaryExpression parent = (ASTPrimaryExpression) node.jjtGetParent();
            ASTPrimarySuffix suffix = (ASTPrimarySuffix) parent.getFirstChildOfType(ASTPrimarySuffix.class);
            if (suffix == null) {
                return super.visit(node, data);
            }
            ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) suffix.getFirstChildOfType(ASTPrimaryPrefix.class);
            if (prefix == null) {
                return super.visit(node, data);
            }

            if (prefix.getFirstChildOfType(ASTBooleanLiteral.class) != null) {
                super.addViolation(data, node);
                return data;
            }
            ASTLiteral literal = (ASTLiteral) prefix.getFirstChildOfType(ASTLiteral.class);
            if (literal != null && ("\"true\"".equals(literal.getImage()) || "\"false\"".equals(literal.getImage()))) {
                super.addViolation(data, node);
                return data;
            }
        }
        return super.visit(node, data);
    }
}
