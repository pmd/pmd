/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UnusedFormalParameterRule extends AbstractRule {

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (!node.isPrivate() && !hasProperty("checkall")) {
            return data;
        }

        if (!node.isNative()) {
            Node parent  = node.jjtGetParent().jjtGetParent().jjtGetParent();
            if (parent instanceof ASTClassOrInterfaceDeclaration && !((ASTClassOrInterfaceDeclaration)parent).isInterface()) {
                RuleContext ctx = (RuleContext) data;
                Map vars = node.getScope().getVariableDeclarations();
                for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
                    VariableNameDeclaration nameDecl = (VariableNameDeclaration) i.next();
                    if (!((List) vars.get(nameDecl)).isEmpty()) {
                        continue;
                    }
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, node, MessageFormat.format(getMessage(), new Object[]{nameDecl.getImage()})));
                }
            }
        }
        return data;
    }
}
