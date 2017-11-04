/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;

import apex.jorje.services.Version;

/**
 * Do special checks for apex unit test classes and methods
 * 
 * @author a.subramanian
 */
public abstract class AbstractApexUnitTestRule extends AbstractApexRule {

    public AbstractApexUnitTestRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Bug Risk");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    /**
     * Don't bother visiting this class if it's not a class with @isTest and
     * newer than API v24
     */
    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        final Version classApiVersion = node.getNode().getDefiningType().getCodeUnitDetails().getVersion();
        if (!isTestMethodOrClass(node) && classApiVersion.isGreaterThan(Version.V174)) {
            return data;
        }
        return super.visit(node, data);
    }

    protected boolean isTestMethodOrClass(final ApexNode<?> node) {
        final ASTModifierNode modifierNode = node.getFirstChildOfType(ASTModifierNode.class);
        return modifierNode != null && modifierNode.getNode().getModifiers().isTest();
    }
}
