/*
 * User: tom
 * Date: Jun 21, 2002
 * Time: 11:26:34 AM
 */
package com.infoether.pmd;

import com.infoether.pmd.ast.*;

import java.util.Iterator;
import java.util.Stack;

public class UnusedPrivateInstanceVariableRule extends AbstractRule implements Rule {

    private Stack nameSpaces = new Stack();
    // TODO
    // this being an instance variable totally hoses up the recursion
    // need to attach it to the report or the stack or something
    // TODO
    private boolean doingIDTraversal;

    public String getDescription() {
        return "Avoid unused private instance variables";
    }

    /**
     * Skip interfaces because they don't have instance variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTClassBody node, Object data) {
        doingIDTraversal = true;
        Namespace nameSpace = new Namespace();
        nameSpaces.push(nameSpace);
        nameSpace.addTable();
        Object report = super.visit(node, data);

        doingIDTraversal = false;
        //System.out.println("data = " + data);
        report = super.visit(node, data);
        reportUnusedInstanceVars((Report)report, nameSpace.peek());

        nameSpace.removeTable();
        nameSpaces.pop();
        return report;
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!doingIDTraversal) {
            return super.visit(node, data);
        }
        //System.out.println("ASTVariableDeclaratorId.getImage() = " + node.getImage() + "; " + node.getBeginLine());
        SimpleNode grandparent = (SimpleNode)node.jjtGetParent().jjtGetParent();
        if (!(grandparent instanceof ASTFieldDeclaration) || grandparent.getImage().indexOf("private") == -1 || grandparent.getImage().indexOf("static") != -1) {
            return super.visit(node, data);
        }
        Namespace group = (Namespace)nameSpaces.peek();
        group.peek().add(new Symbol(node.getImage(), node.getBeginLine()));
        return super.visit(node, data);
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        if (!doingIDTraversal && (node.jjtGetParent() instanceof ASTPrimaryExpression) && (node.getImage() != null)) {
            recordPossibleUsage(node, data);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTName node, Object data) {
        if (!doingIDTraversal && (node.jjtGetParent() instanceof ASTPrimaryPrefix)) {
            recordPossibleUsage(node, data);
        }
        return super.visit(node, data);
    }

    private void recordPossibleUsage(SimpleNode node, Object data) {
        String img = (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(0, node.getImage().indexOf('.'));
        Namespace group = (Namespace)nameSpaces.peek();
        group.peek().recordPossibleUsageOf(new Symbol(img, node.getBeginLine()));
    }

    private void reportUnusedInstanceVars(Report report, SymbolTable table) {
        for (Iterator i = table.getUnusedSymbols(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            report.addRuleViolation(new RuleViolation(this, symbol.getLine(), "Found unused private instance variable '" + symbol.getImage() + "'"));
        }
    }
}
