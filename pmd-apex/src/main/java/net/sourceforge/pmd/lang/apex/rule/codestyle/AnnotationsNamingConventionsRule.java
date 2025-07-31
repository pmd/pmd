/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Rule that checks if Apex annotations are written in PascalCase. This rule
 * ensures that all annotations follow the standard naming convention where each
 * word in the annotation name starts with a capital letter.
 */
public class AnnotationsNamingConventionsRule extends AbstractApexRule {

    @Override
    public Object visit(ASTAnnotation annotation, Object data) {
        if (annotation.isResolved() && isNotField(annotation) && !isPascalCase(annotation)) {
            asCtx(data).addViolation(annotation, annotation.getRawName(), annotation.getName());
        }
        return data;
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTAnnotation.class);
    }

    /**
     * Fields appear twice in the AST: once inside {@link ASTFieldDeclarationStatements} nodes
     * which might contain one or more {@link ASTFieldDeclarationStatements}. This structure resembles the
     * original source code. Additionally, all fields are also available as {@link ASTField} nodes,
     * but then the annotations are duplicated there. So, we only use the
     * original {@link ASTFieldDeclarationStatements} to avoid reporting the same annotation multiple times.
     */
    private boolean isNotField(ASTAnnotation annotation) {
        return !(annotation.getParent().getParent() instanceof ASTField);
    }

    /**
     * Checks if the annotation name matches its raw name (indicating
     * PascalCase).
     */
    private boolean isPascalCase(ASTAnnotation annotation) {
        return annotation.getName().equals(annotation.getRawName());
    }
}
