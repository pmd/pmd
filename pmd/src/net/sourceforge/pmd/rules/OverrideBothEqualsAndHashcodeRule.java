/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 9:18:22 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassBody;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.ast.SimpleNode;

public class OverrideBothEqualsAndHashcodeRule extends AbstractRule implements Rule {

    private static class ViolationCriteria {
        private boolean overridesEquals;
        private boolean overridesHashcode;

        public void overridesEquals() {
            overridesEquals = true;
        }

        public void overridesHashcode() {
            overridesHashcode = true;
        }

        public boolean isMet() {
            return (overridesEquals && !overridesHashcode) || (!overridesEquals && overridesHashcode);
        }
    }

    private ViolationCriteria criteria;

    /**
     * TODO this should work OK with inner classes... I think
     */
    public Object visit(ASTClassBody node, Object data) {
        criteria = new ViolationCriteria();

        super.visit(node, data);

        if (criteria.isMet()) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }

        criteria = new ViolationCriteria();
        return data;
    }

    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        AccessNode parent = (AccessNode) node.jjtGetParent();
        // hashcode has no params & (TODO) returns an int
        if (node.getImage().equals("hashCode") && parent.isPublic() && !parent.isStatic() && node.jjtGetChild(0).jjtGetNumChildren() == 0) {
            criteria.overridesEquals();
        } else if (node.getImage().equals("equals") && parent.isPublic() && !parent.isStatic() && node.getParameterCount() == 1) {
            SimpleNode paramNode = (SimpleNode) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
            if (paramNode.getImage().equals("Object")) {
                criteria.overridesHashcode();
            }
        }
        return super.visit(node, data);
    }
}
