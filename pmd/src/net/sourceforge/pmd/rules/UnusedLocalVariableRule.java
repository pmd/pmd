/*
 * User: tom
 * Date: Jun 18, 2002
 * Time: 11:02:09 AM
 */
package net.sourceforge.pmd.rules;

import java.util.Iterator;
import java.util.Stack;
import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;

public class UnusedLocalVariableRule extends AbstractRule implements Rule{

    private Stack nameSpaces;

    /**
     * Skip interfaces because they don't have local variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    /**
     * For the purpose of local variables, only an ASTCompilation unit creates a new namespace
     */
    public Object visit(ASTCompilationUnit node, Object data) {
        nameSpaces = new Stack();
        createNamespace(node, data);
        nameSpaces.clear();
        return data;
    }

    // these AST types trigger a new scope
    public Object visit(ASTBlock node, Object data){return addTable(node, data);}
    public Object visit(ASTConstructorDeclaration node, Object data){return addTable(node, data);}
    public Object visit(ASTMethodDeclaration node, Object data){return addTable(node, data);}
    public Object visit(ASTFieldDeclaration node, Object data){return addTable(node, data);}
    public Object visit(ASTTryStatement node, Object data){return addTable(node, data);}
    public Object visit(ASTForStatement node, Object data){return addTable(node, data);}
    // these AST types trigger a new scope

    /**
     * This collects the symbols for later reference
     */
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!(node.jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration)) {
            return super.visit(node, data);
        }
        Namespace nameSpace = (Namespace)nameSpaces.peek();
        nameSpace.peek().add(new Symbol(node.getImage(), node.getBeginLine()));
        return super.visit(node, data);
    }

    /**
     * This records usage of a symbol
     */
    public Object visit(ASTName node, Object data) {
        if (node.jjtGetParent() instanceof ASTPrimaryPrefix) {
            String img = (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(0, node.getImage().indexOf('.'));
            Namespace group = (Namespace)nameSpaces.peek();
            group.peek().recordPossibleUsageOf(new Symbol(img, node.getBeginLine()));
        }
        return super.visit(node, data);
    }

    private void reportUnusedLocals(RuleContext ctx, SymbolTable table) {
        for (Iterator i = table.getUnusedSymbols(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, symbol.getLine(), MessageFormat.format(getMessage(), new Object[] {symbol.getImage()})));
        }
    }

    private Object addTable(SimpleNode node, Object data) {
        Namespace group = (Namespace)nameSpaces.peek();
        group.addTable();
        RuleContext ctx = (RuleContext)data;
        super.visit(node, ctx);
        reportUnusedLocals(ctx, group.peek());
        group.removeTable();
        return ctx;
    }

    private Object createNamespace(SimpleNode node, Object data) {
        nameSpaces.push(new Namespace());
        Object report = super.visit(node, data);
        nameSpaces.pop();
        return report;
    }

}
