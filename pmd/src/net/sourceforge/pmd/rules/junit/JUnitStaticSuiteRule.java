/*
 * User: tom
 * Date: Sep 11, 2002
 * Time: 2:27:46 PM
 */
package net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.AccessNode;

public class JUnitStaticSuiteRule extends AbstractRule {

    public Object visit(ASTMethodDeclarator node, Object data) {
        if (!node.getImage().equals("suite")) {
            return data;
        }

        AccessNode parent = (AccessNode) node.jjtGetParent();
        if (!parent.isPublic() || !parent.isStatic()) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }

        return data;
    }

}
