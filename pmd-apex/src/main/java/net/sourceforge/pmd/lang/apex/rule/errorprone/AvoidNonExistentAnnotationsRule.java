/*
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
import net.sourceforge.pmd.reporting.RuleContext;

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
    public RuleContext visit(final ASTUserClass node, final RuleContext data) {
        checkForNonExistentAnnotation(node, node.getModifiers(), data);
        return super.visit(node, data);
    }

    @Override
    public RuleContext visit(final ASTUserInterface node, final RuleContext data) {
        checkForNonExistentAnnotation(node, node.getModifiers(), data);
        return super.visit(node, data);
    }

    @Override
    public RuleContext visit(final ASTUserEnum node, final RuleContext data) {
        checkForNonExistentAnnotation(node, node.getModifiers(), data);
        return super.visit(node, data);
    }

    @Override
    public RuleContext visit(final ASTMethod node, final RuleContext data) {
        return checkForNonExistentAnnotation(node, node.getModifiers(), data);
    }

    @Override
    public RuleContext visit(final ASTProperty node, final RuleContext data) {
        // may have nested methods, don't visit children
        return checkForNonExistentAnnotation(node, node.getModifiers(), data);
    }

    @Override
    public RuleContext visit(final ASTField node, final RuleContext data) {
        return checkForNonExistentAnnotation(node, node.getModifiers(), data);
    }

    private RuleContext checkForNonExistentAnnotation(final ApexNode<?> node, final ASTModifierNode modifierNode, final RuleContext data) {
        if (modifierNode == null) {
            return data;
        }
        for (ASTAnnotation annotation : modifierNode.children(ASTAnnotation.class)) {
            if (!annotation.isResolved()) {
                data.addViolationWithMessage(node, "Use of non existent annotations will lead to broken Apex code which will not compile in the future.");
            }
        }
        return data;
    }
}
