/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Rule that checks if Apex annotations are written in PascalCase. This rule
 * ensures that all annotations follow the standard naming convention where each
 * word in the annotation name starts with a capital letter.
 */
public class AnnotationsShouldBePascalCaseRule extends AbstractApexRule {

    private static final Set<Class<? extends ApexNode<?>>> VALID_PARENT_TYPES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(ASTUserClass.class, ASTUserInterface.class, ASTMethod.class, ASTField.class)));

    @Override
    public Object visit(ASTAnnotation annotation, Object data) {
        if (annotation.isResolved() && isValidAnnotationContext(annotation) && !isPascalCase(annotation)) {
            asCtx(data).addViolation(annotation);
        }
        return data;
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTAnnotation.class);
    }

    /**
     * Checks if the annotation is in a valid context (direct child of a
     * modifier node that is a direct child of a valid parent type).
     */
    private boolean isValidAnnotationContext(ASTAnnotation annotation) {
        if (!(annotation.getParent() instanceof ASTModifierNode)) {
            return false;
        }
        ASTModifierNode modifierNode = (ASTModifierNode) annotation.getParent();
        return VALID_PARENT_TYPES.stream().anyMatch(type -> type.isInstance(modifierNode.getParent()));
    }

    /**
     * Checks if the annotation name matches its raw name (indicating
     * PascalCase).
     */
    private boolean isPascalCase(ASTAnnotation annotation) {
        return annotation.getName().equals(annotation.getRawName());
    }

}
