/*
 * User: tom
 * Date: Jun 21, 2002
 * Time: 11:26:34 AM
 */
package net.sourceforge.pmd.rules;

import java.util.Iterator;
import java.util.Stack;
import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;

public class UnusedPrivateInstanceVariableRule extends UnusedCodeRule {

    private Stack nameSpaces = new Stack();
    private boolean foundDeclarationsAlready;

    /**
     * Skip interfaces because they don't have instance variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        nameSpaces.clear();
        foundDeclarationsAlready = false;
        super.visit(node, data);
        if (!nameSpaces.isEmpty()) {
            harvestUnused((RuleContext)data, ((Namespace)nameSpaces.peek()).peek());
        }
        return data;
    }


    public Object visit(ASTClassBody node, Object data) {
        if (!foundDeclarationsAlready) {
            foundDeclarationsAlready = true;
            Namespace nameSpace = new Namespace();
            nameSpace.addTable();
            nameSpaces.push(nameSpace);
            for (int i=0;i<node.jjtGetNumChildren(); i++) {
                SimpleNode child = (SimpleNode)node.jjtGetChild(i);
                if (child instanceof ASTClassBodyDeclaration) {
                    if (child.jjtGetNumChildren() > 0 &&  child.jjtGetChild(0) instanceof ASTFieldDeclaration) {
                        ASTFieldDeclaration field = (ASTFieldDeclaration)child.jjtGetChild(0);
                        AccessNode access = (AccessNode)field;
                        if (!access.isPrivate()) {
                            continue;
                        }
                        SimpleNode target = (SimpleNode)field.jjtGetChild(1).jjtGetChild(0);
                        if (target.getImage() != null && target.getImage().equals("serialVersionUID")) {
                            continue;
                        }
                        Namespace group = (Namespace)nameSpaces.peek();
                        group.peek().add(new Symbol(target.getImage(), target.getBeginLine()));
                    }
                }
            }
        }
        super.visit(node, data);
        return data;
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        if ((node.jjtGetParent() instanceof ASTPrimaryExpression) && (node.getImage() != null)) {
            recordPossibleUsage(node);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTName node, Object data) {
        if ((node.jjtGetParent() instanceof ASTPrimaryPrefix)) {
            recordPossibleUsage(node);
        }
        return super.visit(node, data);
    }

    private void recordPossibleUsage(SimpleNode node) {
        String otherImg = (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(node.getImage().indexOf('.')+1);
        Namespace group = (Namespace)nameSpaces.peek();
        group.peek().recordPossibleUsageOf(new Symbol(getEndName(node.getImage()), node.getBeginLine()));
        group.peek().recordPossibleUsageOf(new Symbol(otherImg, node.getBeginLine()));
    }
}
