/*
 * User: tom
 * Date: Jun 21, 2002
 * Time: 11:26:34 AM
 */
package net.sourceforge.pmd.rules;

import java.util.Iterator;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;
import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;

public class UnusedPrivateInstanceVariableRule extends AbstractRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.jjtGetParent().jjtGetParent() instanceof ASTFieldDeclaration) {
            RuleContext ctx = (RuleContext)data;
            for (Iterator i =  node.getScope().getUnusedDeclarations(); i.hasNext();) {
                NameDeclaration decl = (NameDeclaration)i.next();
                if (decl.getImage().equals("serialVersionUID")) {
                    continue;
                }
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getLine(), MessageFormat.format(getMessage(), new Object[] {decl.getImage()})));
            }
        }
        return data;
    }
}
