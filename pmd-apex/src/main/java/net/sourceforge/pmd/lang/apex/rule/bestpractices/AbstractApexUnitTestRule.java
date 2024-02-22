/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Do special checks for apex unit test classes and methods
 *
 * @author a.subramanian
 */
abstract class AbstractApexUnitTestRule extends AbstractApexRule {

    /**
     * Don't bother visiting this class if it's not a class with @isTest and
     * newer than API v24 (V176 internal).
     */
    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }
        return super.visit(node, data);
    }

    protected boolean isTestMethodOrClass(final ApexNode<?> node) {
        final ASTModifierNode modifierNode = node.firstChild(ASTModifierNode.class);
        return modifierNode != null && modifierNode.isTest();
    }
}
