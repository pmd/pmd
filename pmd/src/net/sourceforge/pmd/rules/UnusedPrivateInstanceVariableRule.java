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

public class UnusedPrivateInstanceVariableRule extends AbstractRule implements Rule {

    private Stack nameSpaces = new Stack();
    // TODO
    // this being an instance variable totally hoses up the recursion
    // need to attach it to the report or the stack or something
    // I still need something to do forward references, though...
    // and this "do the declarations first and the names second" works
    // Actually, what I need is a Visitor that does a breadth first search
    // TODO
    private boolean doingIDTraversal;
    // TODO
    // this means we don't process nested or inner classes, which is sloppy
    // TODO
    private boolean alreadyWorking;

    /**
     * Skip interfaces because they don't have instance variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTClassBody node, Object data) {
        if (alreadyWorking) {
            return data;
        }
        alreadyWorking = true;
        doingIDTraversal = true;
        Namespace nameSpace = new Namespace();
        nameSpaces.push(nameSpace);
        nameSpace.addTable();
        super.visit(node, null);

        doingIDTraversal = false;
        super.visit(node, null);
        RuleContext ctx = (RuleContext)data;
        reportUnusedInstanceVars(ctx, nameSpace.peek());

        nameSpace.removeTable();
        nameSpaces.pop();
        alreadyWorking = false;
        return data;
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!doingIDTraversal) {
            return super.visit(node, data);
        }
        //System.out.println("ASTVariableDeclaratorId.getImage() = " + node.getImage() + "; " + node.getBeginLine());
        SimpleNode grandparent = (SimpleNode)node.jjtGetParent().jjtGetParent();
        if (!(grandparent instanceof ASTFieldDeclaration)) {
            return super.visit(node, data);
        }
        AccessNode grandparentAccessNode = (AccessNode)grandparent;
        if (!grandparentAccessNode.isPrivate() || grandparentAccessNode.isStatic()) {
            return super.visit(node, data);
        }
        Namespace group = (Namespace)nameSpaces.peek();
        group.peek().add(new Symbol(node.getImage(), node.getBeginLine()));
        return super.visit(node, data);
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        if (!doingIDTraversal && (node.jjtGetParent() instanceof ASTPrimaryExpression) && (node.getImage() != null)) {
            recordPossibleUsage(node);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTName node, Object data) {
        if (!doingIDTraversal && (node.jjtGetParent() instanceof ASTPrimaryPrefix)) {
            recordPossibleUsage(node);
        }
        return super.visit(node, data);
    }

    private void recordPossibleUsage(SimpleNode node) {
        String img = (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(0, node.getImage().indexOf('.'));
        Namespace group = (Namespace)nameSpaces.peek();
        group.peek().recordPossibleUsageOf(new Symbol(img, node.getBeginLine()));
    }

    private void reportUnusedInstanceVars(RuleContext ctx, SymbolTable table) {
        for (Iterator i = table.getUnusedSymbols(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, symbol.getLine(), MessageFormat.format(getMessage(), new Object[] {symbol.getImage()})));
        }
    }
}
