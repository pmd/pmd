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

    private Stack tableGroups = new Stack();

    public Object createGroup(SimpleNode node, Object data) {
        tableGroups.push(new Namespace());
        Object report = super.visit(node, data);
        tableGroups.pop();
        return report;
    }

    /**
     * Skip interfaces because they don't have local variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    /**
     * A new ClassBody triggers a new namespace
     */
    public Object visit(ASTClassBody node, Object data) {
        return createGroup(node, data);
    }

    // these AST types trigger creation of a new symbol table scope
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

    private void reportUnusedLocals(RuleContext ctx, SymbolTable table) {
        for (Iterator i = table.getUnusedSymbols(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, symbol.getLine(), MessageFormat.format(getMessage(), new Object[] {symbol.getImage()})));
        }
    }

    private Object addTable(SimpleNode node, Object data) {
        RuleContext ctx = (RuleContext)data;
        Namespace group = (Namespace)tableGroups.peek();
        group.addTable();
        super.visit(node, ctx);
        reportUnusedLocals(ctx, group.peek());
        group.removeTable();
        return ctx;
    }

}
