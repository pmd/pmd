/*
 * User: tom
 * Date: Aug 8, 2002
 * Time: 3:33:46 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.SymbolTable;
import net.sourceforge.pmd.Symbol;

import java.util.Iterator;
import java.text.MessageFormat;

public class UnusedCodeRule extends AbstractRule {
    protected void harvestUnused(RuleContext ctx, SymbolTable table) {
        for (Iterator i = table.getUnusedSymbols(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, symbol.getLine(), MessageFormat.format(getMessage(), new Object[] {symbol.getImage()})));
        }
    }
}
