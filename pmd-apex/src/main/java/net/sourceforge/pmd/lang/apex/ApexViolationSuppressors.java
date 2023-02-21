/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTAnnotationParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.ast.Node;

final class ApexViolationSuppressors {

    private static final ViolationSuppressor APEX_ANNOT_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String getId() {
            return "@SuppressWarnings";
        }

        @Override
        public Report.SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            if (node instanceof ApexNode && isSuppressed((ApexNode<?>) node, rv.getRule())) {
                return new SuppressedViolation(rv, this, null);
            }
            return null;
        }
    };

    static final List<ViolationSuppressor> ALL_APEX_SUPPRESSORS = listOf(APEX_ANNOT_SUPPRESSOR);

    private ApexViolationSuppressors() {
    }

    /**
     * Check for suppression on this node, on parents, and on contained types
     * for ASTCompilationUnit
     */
    private static boolean isSuppressed(ApexNode<?> node, Rule rule) {
        boolean result = suppresses(node, rule);

        if (!result) {
            ApexNode<?> parent = node.getParent();
            while (!result && parent != null) {
                result = suppresses(parent, rule);
                parent = parent.getParent();
            }
        }

        return result;
    }

    private static boolean canSuppressWarnings(ApexNode<?> node) {
        return node instanceof ASTFieldDeclarationStatements
            || node instanceof ASTVariableDeclarationStatements
            || node instanceof ASTField
            || node instanceof ASTMethod
            || node instanceof ASTUserClassOrInterface
            || node instanceof ASTUserEnum
            || node instanceof ASTParameter;
    }

    private static boolean suppresses(final ApexNode<?> node, Rule rule) {
        return canSuppressWarnings(node) && hasSuppressWarningsAnnotationFor(node, rule);
    }

    private static boolean hasSuppressWarningsAnnotationFor(ApexNode<?> node, Rule rule) {
        return node.children(ASTModifierNode.class)
                   .children(ASTAnnotation.class)
                   .any(a -> suppresses(a, rule));
    }

    private static boolean suppresses(ASTAnnotation annot, Rule rule) {
        final String ruleAnno = "PMD." + rule.getName();

        if ("SuppressWarnings".equals(annot.getName())) {
            for (ASTAnnotationParameter param : annot.children(ASTAnnotationParameter.class)) {
                String image = param.getValue();

                if (image != null) {
                    Set<String> paramValues = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                    paramValues.addAll(Arrays.asList(image.replaceAll("\\s+", "").split(",")));
                    if (paramValues.contains("PMD") || paramValues.contains(ruleAnno) || paramValues.contains("all")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
