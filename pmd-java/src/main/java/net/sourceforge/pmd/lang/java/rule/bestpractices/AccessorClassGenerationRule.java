/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * 1. Note all private constructors. 2. Note all instantiations from outside of
 * the class by way of the private constructor. 3. Flag instantiations.
 *
 * <p>
 * Parameter types can not be matched because they can come as exposed members
 * of classes. In this case we have no way to know what the type is. We can make
 * a best effort though which can filter some?
 * </p>
 *
 * @author CL Gilbert (dnoyeb@users.sourceforge.net)
 * @author David Konecny (david.konecny@)
 * @author Romain PELISSE, belaran@gmail.com, patch bug#1807370
 * @author Juan Martin Sotuyo Dodero (juansotuyo@gmail.com), complete rewrite
 */
public class AccessorClassGenerationRule extends AbstractJavaRulechainRule {

    private final Set<JavaNode> reportedNodes = new HashSet<>();

    public AccessorClassGenerationRule() {
        super(ASTConstructorCall.class);
    }

    @Override
    public void end(RuleContext ctx) {
        super.end(ctx);
        reportedNodes.clear();
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        if (!node.isAnonymousClass()) {
            AccessorMethodGenerationRule.checkMemberAccess(this, (RuleContext) data, node, node.getMethodType().getSymbol(), this.reportedNodes);
        }
        return null;
    }
}
