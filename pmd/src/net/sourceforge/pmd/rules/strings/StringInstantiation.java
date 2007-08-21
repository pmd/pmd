package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.typeresolution.TypeHelper;

import java.util.List;

public class StringInstantiation extends AbstractRule {

    public Object visit(ASTAllocationExpression node, Object data) {
        if (!(node.jjtGetChild(0) instanceof ASTClassOrInterfaceType)) {
            return data;
        }

        if (!TypeHelper.isA((ASTClassOrInterfaceType) node.jjtGetChild(0), String.class)) {
            return data;
        }

        List exp = node.findChildrenOfType(ASTExpression.class);
        if (exp.size() >= 2) {
            return data;
        }

        if (node.getFirstChildOfType(ASTArrayDimsAndInits.class) != null || node.getFirstChildOfType(ASTAdditiveExpression.class) != null) {
            return data;
        }

        ASTName name =node.getFirstChildOfType(ASTName.class);
        // Literal, i.e., new String("foo")
        if (name == null) {
            addViolation(data, node);
            return data;
        }

        NameDeclaration nd = name.getNameDeclaration();
        if (!(nd instanceof VariableNameDeclaration)) {
            return data;
        }

        VariableNameDeclaration vnd = (VariableNameDeclaration) nd;
        // nd == null in cases like: return new String(str);
        if (vnd == null || TypeHelper.isA(vnd, String.class)) {
            addViolation(data, node);
        }
        return data;
    }
}
