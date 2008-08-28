package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

public class UselessStringValueOfRule extends AbstractJavaRule {

    @Override
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
            Node gp = parent.jjtGetParent();
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
        if (parent instanceof ASTPrimaryExpression && parent.jjtGetNumChildren() == 1) {
            Node child = parent.jjtGetChild(0);
            if (child instanceof ASTPrimaryPrefix && child.jjtGetNumChildren() == 1) {
                Node gc = child.jjtGetChild(0);
                if (gc instanceof ASTName) {
                    ASTName name = (ASTName) gc;
                    if (name.getNameDeclaration() instanceof VariableNameDeclaration) {
                        VariableNameDeclaration nd = (VariableNameDeclaration) name.getNameDeclaration();
                        if (nd.isPrimitiveType()) {
                            result = true;
                        }
                    }
                } else if (gc instanceof ASTLiteral) {
                    result = !((ASTLiteral) gc).isStringLiteral();
                }
            }
        }
        return result;
    }

}
