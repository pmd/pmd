/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;


/**
 * Flags missing @Override annotations.
 *
 * @author Clément Fournier
 * @since 6.2.0
 */
public class MissingOverrideRule extends AbstractJavaRulechainRule {

    public MissingOverrideRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isOverridden() && !node.isAnnotationPresent(Override.class)) {
            addViolation(data, node, new Object[] { PrettyPrintingUtil.displaySignature(node) });
        }
        return data;
    }
}
