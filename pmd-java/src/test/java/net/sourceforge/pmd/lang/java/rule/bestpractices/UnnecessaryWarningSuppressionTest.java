/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Collection;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.test.PmdRuleTst;

class UnnecessaryWarningSuppressionTest extends PmdRuleTst {

    @Override
    protected Collection<? extends Rule> getExtraRules() {
        return listOf(new FakeRuleThatReportsIncrements(),
                      new FakeRuleThatReportsDecrements());
    }


    static class FakeRuleThatReportsIncrements extends AbstractJavaRulechainRule {

        FakeRuleThatReportsIncrements() {
            super(ASTUnaryExpression.class);
            setLanguage(LanguageRegistry.PMD.getLanguageById("java"));
            setMessage("VIOLATION OF THE ORIGINAL RULE (++)");
            setName(getClass().getSimpleName());
        }

        @Override
        public Object visit(ASTUnaryExpression node, Object data) {
            if (node.getOperator().isIncrement()) {
                asCtx(data).addViolation(node);
            }
            return null;
        }
    }

    static class FakeRuleThatReportsDecrements extends AbstractJavaRulechainRule {

        FakeRuleThatReportsDecrements() {
            super(ASTUnaryExpression.class);
            setLanguage(LanguageRegistry.PMD.getLanguageById("java"));
            setMessage("VIOLATION OF THE ORIGINAL RULE (--)");
            setName(getClass().getSimpleName());
        }

        @Override
        public Object visit(ASTUnaryExpression node, Object data) {
            if (node.getOperator().isDecrement()) {
                asCtx(data).addViolation(node);
            }
            return null;
        }
    }
}
