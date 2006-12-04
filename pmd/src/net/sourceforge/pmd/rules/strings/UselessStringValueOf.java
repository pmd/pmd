package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class UselessStringValueOf extends AbstractRule {

    public Object visit(ASTPrimaryPrefix node, Object data) {
        if (node.jjtGetNumChildren() == 0 ||
            !(node.jjtGetChild(0) instanceof ASTName)) {
            return super.visit(node, data);
        }

        String image = ((ASTName) node.jjtGetChild(0)).getImage();

        if ("String.valueOf".equals(image)) {
            Node parent = node.jjtGetParent();
            if (parent.jjtGetNumChildren() != 2) {
                return super.visit(node, data);
            }
            SimpleJavaNode gp = (SimpleJavaNode) parent.jjtGetParent();
            if (parent instanceof ASTPrimaryExpression &&
                    gp instanceof ASTAdditiveExpression &&
                    "+".equals(gp.getImage())) {
                boolean ok = false;
                if (gp.jjtGetChild(0) == parent) {
                    ok = !isPrimitive(gp.jjtGetChild(1));
                } else  {
                    for (int i = 0; !ok && gp.jjtGetChild(i) != parent; i++) {
                        ok = !isPrimitive(gp.jjtGetChild(i));
                    }
                }
                if (ok) {
                    super.addViolation(data, node);
                    return data;
                }
            }
        }
        return super.visit(node, data);
    }

    private static boolean isPrimitive(Node parent) {
        boolean result = false;
        if (parent instanceof ASTPrimaryExpression &&
            parent.jjtGetNumChildren() == 1 &&
            parent.jjtGetChild(0) instanceof ASTPrimaryPrefix &&
            parent.jjtGetChild(0).jjtGetNumChildren() == 1 &&
            parent.jjtGetChild(0).jjtGetChild(0) instanceof ASTName) {
            ASTName name = (ASTName) parent.jjtGetChild(0).jjtGetChild(0);
            if (name.getNameDeclaration() instanceof VariableNameDeclaration) {
                VariableNameDeclaration nd = (VariableNameDeclaration) name.getNameDeclaration();
                if (nd.isPrimitiveType()) {
                    result = true;
                }
            }
        }
        return result;
    }
    
}
