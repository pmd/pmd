/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.text.MessageFormat;
import java.util.Iterator;

public class UnusedFormalParameterRule extends AbstractRule {

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isPrivate() && !node.isNative()) {  // make sure it's both private and not native
            RuleContext ctx = (RuleContext) data;
            for (Iterator i = node.getScope().getVariableDeclarations(false).keySet().iterator(); i.hasNext();) {
                VariableNameDeclaration nameDecl = (VariableNameDeclaration) i.next();
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine(), MessageFormat.format(getMessage(), new Object[]{nameDecl.getImage()})));
            }
        }
        return data;
    }
}
