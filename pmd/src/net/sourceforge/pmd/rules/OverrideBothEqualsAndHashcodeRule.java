/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 9:18:22 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.*;

public class OverrideBothEqualsAndHashcodeRule extends AbstractRule implements Rule {

    private boolean overridesEquals;
    private boolean overridesHashcode;

    /**
     * TODO this should work OK with inner classes... I think
     */
    public Object visit(ASTClassBody node, Object data) {
        this.overridesEquals = false;
        this.overridesHashcode = false;

        super.visit(node, data);

        if ((overridesEquals && !overridesHashcode) || (!overridesEquals && overridesHashcode)) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, 1));
        }

        this.overridesEquals = false;
        this.overridesHashcode = false;
        return data;
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        // hashcode has no param
        // TODO ensure it returns an int
        if (node.getImage().equals("hashcode") && ((AccessNode)node.jjtGetParent()).isPublic() && !((AccessNode)node.jjtGetParent()).isStatic() && node.jjtGetChild(0).jjtGetNumChildren() == 0) {
            overridesHashcode = true;
        } else if (node.getImage().equals("equals") && ((AccessNode)node.jjtGetParent()).isPublic()  && !((AccessNode)node.jjtGetParent()).isStatic()) {
            // TODO equals has 1 param
            overridesEquals = true;
        }
        return super.visit(node, data);
    }
}
