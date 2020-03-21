/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Apex supported non existent annotations for legacy reasons.
 * In the future, use of such non-existent annotations could result in broken apex code that will not compile.
 * This will prevent users of garbage annotations from being able to use legitimate annotations added to apex in the future.
 * A full list of supported annotations can be found at https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_annotation.htm
 *
 * @author a.subramanian
 */
public class AvoidNonExistentAnnotationsRule extends AbstractApexRule {
    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        checkForNonExistentAnnotation(node, node.getModifiers(), data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTUserInterface node, final Object data) {
        checkForNonExistentAnnotation(node, node.getModifiers(), data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTUserEnum node, final Object data) {
        checkForNonExistentAnnotation(node, node.getModifiers(), data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTMethod node, final Object data) {
        return checkForNonExistentAnnotation(node, node.getModifiers(), data);
    }

    @Override
    public Object visit(final ASTProperty node, final Object data) {
        // may have nested methods, don't visit children
        return checkForNonExistentAnnotation(node, node.getModifiers(), data);
    }

    @Override
    public Object visit(final ASTField node, final Object data) {
        return checkForNonExistentAnnotation(node, node.getModifiers(), data);
    }

    private Object checkForNonExistentAnnotation(final ApexNode<?> node, final ASTModifierNode modifierNode, final Object data) {
        for (ASTAnnotation annotation : modifierNode.findChildrenOfType(ASTAnnotation.class)) {
            if (!annotation.isResolved()) {
                addViolationWithMessage(data, node, "Use of non existent annotations will lead to broken Apex code which will not compile in the future.");
            }
        }
        return data;
    }
}
