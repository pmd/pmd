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

    // TODO
    // This helps resolve forward references by doing two passes
    // i.e., "do the declarations first and the names second"
    // Actually, what I need is a Visitor that does a breadth first search
    // TODO
    private boolean trollingForDeclarations;

    private int depth;

    /**
     * Skip interfaces because they don't have instance variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        nameSpaces.clear();
        depth = 0;
        trollingForDeclarations = false;
        super.visit(node, data);
        return data;
    }

    public Object visit(ASTClassBody node, Object data) {
        depth++;
        // first troll for declarations, but only in the top level class
        if (depth == 1) {
            trollingForDeclarations = true;
            Namespace nameSpace = new Namespace();
            nameSpace.addTable();
            nameSpaces.push(nameSpace);
            super.visit(node, null);
            trollingForDeclarations = false;
        } else {
            trollingForDeclarations = false;
        }

        // troll for usages, regardless of depth
        super.visit(node, null);

        // if we're back at the top level class, harvest
        if (depth == 1) {
            RuleContext ctx = (RuleContext)data;
            harvestUnused(ctx, ((Namespace)nameSpaces.peek()).peek());
        }

        depth--;
        return data;
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!trollingForDeclarations) {
            return super.visit(node, data);
        }
        SimpleNode grandparent = (SimpleNode)node.jjtGetParent().jjtGetParent();
        if (!(grandparent instanceof ASTFieldDeclaration)) {
            return super.visit(node, data);
        }
        AccessNode grandparentAccessNode = (AccessNode)grandparent;
        if (!grandparentAccessNode.isPrivate() || (node.getImage() != null && node.getImage().equals("serialVersionUID"))) {
            return super.visit(node, data);
        }
        Namespace group = (Namespace)nameSpaces.peek();
        group.peek().add(new Symbol(node.getImage(), node.getBeginLine()));
        return super.visit(node, data);
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        if (!trollingForDeclarations && (node.jjtGetParent() instanceof ASTPrimaryExpression) && (node.getImage() != null)) {
            recordPossibleUsage(node);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTName node, Object data) {
        if (!trollingForDeclarations && (node.jjtGetParent() instanceof ASTPrimaryPrefix)) {
            recordPossibleUsage(node);
        }
        return super.visit(node, data);
    }

    private void recordPossibleUsage(SimpleNode node) {
        String img = (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(0, node.getImage().indexOf('.'));
        String otherImg = (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(node.getImage().indexOf('.')+1);
        Namespace group = (Namespace)nameSpaces.peek();
        group.peek().recordPossibleUsageOf(new Symbol(img, node.getBeginLine()));
        group.peek().recordPossibleUsageOf(new Symbol(otherImg, node.getBeginLine()));
    }

}
