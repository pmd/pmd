package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTArrayDimsAndInits;

import java.util.List;

public class StringInstantiation extends AbstractRule {

    public Object visit(ASTAllocationExpression node, Object data) {
        if (!(node.jjtGetChild(0) instanceof ASTClassOrInterfaceType)) {
            return data;
        }

        ASTClassOrInterfaceType clz = (ASTClassOrInterfaceType)node.jjtGetChild(0);
        if (!clz.getImage().equals("String")) {
            return data;
        }

        List exp = node.findChildrenOfType(ASTExpression.class);
        if (exp.size() >=2 ){
            return data;
        }

        if (node.getFirstChildOfType(ASTArrayDimsAndInits.class) != null) {
            return data;
        }

        addViolation(data, node);
        return data;
    }
}
