/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;

import java.util.ArrayList;
import java.util.List;

public class AtLeastOneConstructorRule extends AbstractRule {

    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        List constructors = new ArrayList();
        node.findChildrenOfType(ASTConstructorDeclaration.class, constructors, false);
        if (constructors.isEmpty()) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node, data);
    }
}
