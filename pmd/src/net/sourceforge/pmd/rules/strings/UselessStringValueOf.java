package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleJavaNode;

public class UselessStringValueOf extends AbstractRule {

    public Object visit(ASTPrimaryPrefix node, Object data) {
        if (node.jjtGetNumChildren() == 0 || !node.jjtGetChild(0).getClass().equals(ASTName.class)) {
            return super.visit(node, data);
        }

        String image = ((ASTName) node.jjtGetChild(0)).getImage();

        if ("String.valueOf".equals(image)) {
            Node parent = node.jjtGetParent();
            SimpleJavaNode gp = (SimpleJavaNode) parent.jjtGetParent();
            if (parent instanceof ASTPrimaryExpression &&
                    gp instanceof ASTAdditiveExpression &&
                    gp.jjtGetChild(0) != parent &&
                    "+".equals(gp.getImage())) {
                super.addViolation(data, node);
                return data;
            }
        }
        return super.visit(node, data);
    }

}
