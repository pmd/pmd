package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import java.util.*;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.RuleContext;
import java.text.MessageFormat;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;


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
        if (paramNames.isEmpty()) return;  //if there are no more paramNames then there's no reason to keep checking
        int index = 0;
        if (startNode instanceof ASTName) {
            String nodeImage = ((ASTName)startNode).getImage();
            //check to see if there is a "." in the image which indicates a method call on this variable name
            int index2 = nodeImage.indexOf('.');
            if (index2 != -1) {
                nodeImage = nodeImage.substring(0, index2);   //set the node Image to the prefix
            }
            if (paramNames.contains(nodeImage)) {
                paramNames.remove(nodeImage);  //the name is used so let's remove it from the list
            }
        }
        else if (startNode.jjtGetNumChildren() > 0) {
            int limit = startNode.jjtGetNumChildren();
            for (int i=0; i<limit; i++) {
                SimpleNode node = (SimpleNode)startNode.jjtGetChild(i);
                checkParamNames(paramNames, node);
            }
        }
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isPrivate() && ! node.isNative()) {  //make sure it's a private method and not a native method
            SimpleNode md = (SimpleNode)node.jjtGetChild(1);
            SimpleNode formalParams = (SimpleNode)md.jjtGetChild(0);
            int paramCount = formalParams.jjtGetNumChildren();
            if (paramCount == 0) return data;  //bail out if now paramters
            HashSet paramNames = new HashSet();
            for (int i=0; i<paramCount; i++) {
                ASTVariableDeclaratorId paramName = (ASTVariableDeclaratorId)formalParams.jjtGetChild(i).jjtGetChild(1);
                paramNames.add(paramName.getImage());
            }

            //find the block node
            int j = 2;
            SimpleNode blockNode =(SimpleNode)node.jjtGetChild(j);
            int childCount = node.jjtGetNumChildren();
            while (j  <= childCount) {
                if (blockNode instanceof ASTBlock)
                    break;
                blockNode =(SimpleNode)node.jjtGetChild(++j);
            }

            if (blockNode instanceof ASTBlock) {
                checkParamNames(paramNames, blockNode);   //check the block node for the occurence of the parameter names
                if (!paramNames.isEmpty()) {  //there are still names left in the set so the must not have been used
                    RuleContext ctx = (RuleContext)data;
                    System.out.println(paramNames);
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, md.getBeginLine(), MessageFormat.format(getMessage(), paramNames.toArray())));
                }
            }
        }
        return data;
    }

}

