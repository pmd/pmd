/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.apex.ast.ApexInternalAstApi;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.CanSuppressWarnings;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;

public final class ApexRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final ApexRuleViolationFactory INSTANCE = new ApexRuleViolationFactory();

    private ApexRuleViolationFactory() {
    }

    @Override
    protected List<ViolationSuppressor> getSuppressors(Node node, Rule rule) {
        return Arrays.asList(

            ViolationSuppressor.noPmdCommentSuppressor(ApexInternalAstApi.getNoPmdComments((ApexNode<?>) node.getRoot())),
            new ViolationSuppressor() {
                @Override
                public String id() {
                    return "@SuppressWarnings";
                }


                @Override
                public Report.@Nullable SuppressedViolation suppressOrNull(RuleViolation rv) {
                    if (isSuppressed(node, rule)) {
                        return new SuppressedViolation(rv, this, null);
                    }
                    return null;
                }
            }
        );
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
            Node parent = node.jjtGetParent();
            while (!result && parent != null) {
                result = suppresses(parent, rule);
                parent = parent.jjtGetParent();
            }
        }

        return result;
    }

    private static boolean suppresses(final Node node, Rule rule) {
        return node instanceof CanSuppressWarnings
            && ((CanSuppressWarnings) node).hasSuppressWarningsAnnotationFor(rule);
    }
}
