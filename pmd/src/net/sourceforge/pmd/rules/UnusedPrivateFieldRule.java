/*
 * User: tom
 * Date: Jun 21, 2002
 * Time: 11:26:34 AM
 */
package net.sourceforge.pmd.rules;

import java.util.Iterator;
import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;

public class UnusedPrivateFieldRule extends AbstractRule {

    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        for (Iterator i = node.getScope().getVariableDeclarations(false).keySet().iterator();i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration)i.next();
            if (decl.getAccessNodeParent().isPrivate() && !decl.getImage().equals("serialVersionUID") && !decl.getImage().equals("serialPersistentFields")) {
                RuleContext ctx = (RuleContext)data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getLine(), MessageFormat.format(getMessage(), new Object[] {decl.getImage()})));
            }
        }
        return super.visit(node, data);
    }

}
