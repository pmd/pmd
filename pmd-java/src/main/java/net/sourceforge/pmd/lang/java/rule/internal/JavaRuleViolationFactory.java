/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;

public final class JavaRuleViolationFactory extends DefaultRuleViolationFactory {

    public static final RuleViolationFactory INSTANCE = new JavaRuleViolationFactory();
    private static final ViolationSuppressor JAVA_ANNOT_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String getId() {
            return "@SuppressWarnings";
        }

        @Override
        public Report.SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            if (AnnotationSuppressionUtil.contextSuppresses(node, rv.getRule())) {
                return new SuppressedViolation(rv, this, null);
            }
            return null;
        }
    };

    private JavaRuleViolationFactory() {
        // singleton
    }

    @Override
    protected List<ViolationSuppressor> getSuppressors() {
        return Collections.singletonList(JAVA_ANNOT_SUPPRESSOR);
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
