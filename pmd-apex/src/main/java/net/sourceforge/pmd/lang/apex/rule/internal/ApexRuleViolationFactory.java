/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.internal;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.apex.ast.CanSuppressWarnings;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;

public final class ApexRuleViolationFactory extends DefaultRuleViolationFactory {

    public static final ApexRuleViolationFactory INSTANCE = new ApexRuleViolationFactory();
    private static final ViolationSuppressor APEX_ANNOT_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String getId() {
            return "@SuppressWarnings";
        }

        @Override
        public Report.SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            if (isSuppressed(node, rv.getRule())) {
                return new SuppressedViolation(rv, this, null);
            }
            return null;
        }
    };

    private ApexRuleViolationFactory() {
    }

    @Override
    protected List<ViolationSuppressor> getSuppressors() {
        return Collections.singletonList(APEX_ANNOT_SUPPRESSOR);
    }

    /**
     * Check for suppression on this node, on parents, and on contained types
     * for ASTCompilationUnit
     *
     * @param node
     */
    private static boolean isSuppressed(Node node, Rule rule) {
        boolean result = suppresses(node, rule);

        if (!result) {
            Node parent = node.getParent();
            while (!result && parent != null) {
                result = suppresses(parent, rule);
                parent = parent.getParent();
            }
        }

        return result;
    }

    private static boolean suppresses(final Node node, Rule rule) {
        return node instanceof CanSuppressWarnings
            && ((CanSuppressWarnings) node).hasSuppressWarningsAnnotationFor(rule);
    }
}
