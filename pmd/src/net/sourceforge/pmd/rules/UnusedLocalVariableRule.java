/*
 * User: tom
 * Date: Jun 18, 2002
 * Time: 11:02:09 AM
 */
package net.sourceforge.pmd.rules;

import java.util.Iterator;
import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;

public class UnusedLocalVariableRule extends AbstractRule {
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration) {
            RuleContext ctx = (RuleContext)data;
            for (Iterator i =  node.getScope().getVariableDeclarations(false).keySet().iterator(); i.hasNext();) {
                VariableNameDeclaration decl = (VariableNameDeclaration)i.next();
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getLine(), MessageFormat.format(getMessage(), new Object[] {decl.getImage()})));
            }
        }
        return data;
    }
}
