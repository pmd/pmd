package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import java.util.*;
import net.sourceforge.pmd.ast.ASTBlock;


public class UnusedFormalParameterRule extends AbstractRule {

    /**
     Skip interfaces
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    /**
     * Check to see if the param names are used in the body of the method
     * @param paramNames list of param names to check
     */
    private void checkParamNames(HashSet paramNames, SimpleNode startNode) {

    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isPrivate()) {
            SimpleNode md = (SimpleNode)node.jjtGetChild(1);
            SimpleNode formalParams = (SimpleNode)md.jjtGetChild(0);
            int paramCount = formalParams.jjtGetNumChildren();
            if (paramCount == 0) return data;  //bail out if now paramters
            HashSet paramNames = new HashSet();
            for (int i=0; i<paramCount; i++) {
                ASTName paramName = (ASTName)formalParams.jjtGetChild(i).jjtGetChild(0).jjtGetChild(0);
                paramNames.add(paramName);
            }
            checkParamNames(paramNames, md);
        }
        return data;
    }

}
