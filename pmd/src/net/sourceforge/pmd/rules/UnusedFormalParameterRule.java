package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import java.util.*;
import net.sourceforge.pmd.ast.ASTBlock;


public class UnusedFormalParameterRule extends AbstractRule {
    private List paramNames = null;

    /**
     Skip interfaces
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isPrivate()) {
            SimpleNode md = (SimpleNode)node.jjtGetChild(1);
            SimpleNode formalParams = (SimpleNode)md.jjtGetChild(0);
            int paramCount = formalParams.jjtGetNumChildren();
            if (paramCount == 0) return data;  //bail out if now paramters
            paramNames = new ArrayList();
            for (int i=0; i<paramCount; i++) {
                ASTName paramName = (ASTName)formalParams.jjtGetChild(i).jjtGetChild(0).jjtGetChild(0);
                paramNames.add(paramName);
            }


        }
        return data;
    }

    public Object visit(ASTBlock node, Object data) {
        if (paramNames != null) {
        }

        return data;
    }
}