/*
 * User: tom
 * Date: Jun 18, 2002
 * Time: 11:02:09 AM
 */
package com.infoether.pmd;

import com.infoether.pmd.ast.*;

import java.util.Iterator;
import java.util.Stack;

public class UnusedLocalVariableRule extends AbstractRule implements Rule{

    private Stack tableGroups = new Stack();

    public String getDescription() {
        return "Avoid unused local variables";
    }

    public Object createGroup(SimpleNode node, Object data) {
        tableGroups.push(new Namespace());
        Object report = super.visit(node, data);
        tableGroups.pop();
        return report;
    }

    // these AST types trigger creation of a new symbol table group
    public Object visit(ASTClassBody node, Object data) {return createGroup(node, data);}
    public Object visit(ASTInterfaceDeclaration node, Object data) {return createGroup(node, data);}
    // these AST types trigger creation of a new symbol table group

    // these AST types trigger creation of a new symbol table
    public Object visit(ASTBlock node, Object data){return addTable(node, data);}
    public Object visit(ASTConstructorDeclaration node, Object data){return addTable(node, data);}
    public Object visit(ASTMethodDeclaration node, Object data){return addTable(node, data);}
    public Object visit(ASTFieldDeclaration node, Object data){return addTable(node, data);}
    public Object visit(ASTTryStatement node, Object data){return addTable(node, data);}
    public Object visit(ASTForStatement node, Object data){return addTable(node, data);}
    // these AST types trigger creation of a new symbol table

    // these AST types are variable/name usages
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        //System.out.println("ASTVariableDeclaratorId.getImage() = " + node.getImage());
        if (!(node.jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration)) {
            return super.visit(node, data);
        }
        Namespace group = (Namespace)tableGroups.peek();
        group.peek().add(new Symbol(node.getImage(), node.getBeginLine()));
        return super.visit(node, data);
    }

    public Object visit(ASTName node, Object data) {
        //System.out.println("ASTName.getImage() = " + node.getImage() + "; " + node.getBeginLine());
        if (node.jjtGetParent() instanceof ASTPrimaryPrefix) {
            String img = (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(0, node.getImage().indexOf('.'));
            Namespace group = (Namespace)tableGroups.peek();
            group.peek().recordPossibleUsageOf(new Symbol(img, node.getBeginLine()));
        }
        return super.visit(node, data);
    }
    // these AST types are variable/name usages

    private void reportUnusedLocals(Report report, SymbolTable table) {
        for (Iterator i = table.getUnusedSymbols(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            report.addRuleViolation(new RuleViolation(this, symbol.getLine(), "Found unused local variable '" + symbol.getImage() + "'"));
        }
    }

    private Object addTable(SimpleNode node, Object data) {
        Namespace group = (Namespace)tableGroups.peek();
        group.addTable();
        Object RC = super.visit(node, data);
        reportUnusedLocals((Report)data, group.peek());
        group.removeTable();
        return RC;
    }

}
