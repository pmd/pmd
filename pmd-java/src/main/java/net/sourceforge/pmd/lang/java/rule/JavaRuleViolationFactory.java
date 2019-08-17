/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public final class JavaRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final RuleViolationFactory INSTANCE = new JavaRuleViolationFactory();

    private JavaRuleViolationFactory() {
    }

    @Override
    protected List<ViolationSuppressor> getSuppressors(Node node, Rule rule) {

        ViolationSuppressor nopmd = ViolationSuppressor.noPmdCommentSuppressor(InternalApiBridge.getSuppressMap((ASTCompilationUnit) node.getRoot()));
        ViolationSuppressor annot = new ViolationSuppressor() {
            @Override
            public String id() {
                return "@SuppressWarnings";
            }

            @Override
            public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv) {
                if (AnnotationSuppressionUtil.contextSuppresses(node, rule)) {
                    return new SuppressedViolation(rv, this, null);
                }
                return null;
            }
        };
        return Arrays.asList(nopmd, annot);
    }

    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
        return new JavaRuleViolation(rule, ruleContext, (JavaNode) node, message);
    }

    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
            int beginLine, int endLine) {
        return new JavaRuleViolation(rule, ruleContext, (JavaNode) node, message, beginLine, endLine);
    }

}
