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
import net.sourceforge.pmd.symboltable.Symbol;
import net.sourceforge.pmd.symboltable.Namespace;
import net.sourceforge.pmd.symboltable.SymbolTable;

public class UnusedLocalVariableRule extends AbstractRule {
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration) {
            RuleContext ctx = (RuleContext)data;
            SymbolTable table = ((SimpleNode)node).getSymbolTable();
            for (Iterator i = table.getUnusedSymbols(); i.hasNext();) {
                Symbol symbol = (Symbol)i.next();
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, symbol.getLine(), MessageFormat.format(getMessage(), new Object[] {symbol.getImage()})));
            }
        }
        return data;
    }
}
