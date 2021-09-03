/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import java.util.List;

import net.sourceforge.pmd.lang.plsql.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;

/**
 * NPath complexity is a measurement of the acyclic execution paths through a
 * function. See Nejmeh, Communications of the ACM Feb 1988 pp 188-200.
 *
 * @author Jason Bennett
 */
public class NPathComplexityRule extends AbstractCounterCheckRule<ExecutableCode> {

    public NPathComplexityRule() {
        super(ExecutableCode.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 200;
    }


    @Override
    protected int getMetric(ExecutableCode node) {
        return new NPathComplexityVisitor().compute(node);
    }

    @Override
    protected Object[] getViolationParameters(ExecutableCode node, int metric) {
        return new Object[] {node.getMethodName(), metric};
    }

    /**
     * Calculate the boolean complexity of the given expression. NPath boolean
     * complexity is the sum of &amp;&amp; and || tokens. This is calculated by summing
     * the number of children of the &amp;&amp;'s (minus one) and the children of the
     * ||'s (minus one).
     *
     * <p>Note that this calculation applies to Cyclomatic Complexity as well.</p>
     *
     * @param expr control structure expression
     *
     * @return complexity of the boolean expression
     */
    static int sumExpressionComplexity(ASTExpression expr) {
        if (expr == null) {
            return 0;
        }

        List<ASTConditionalAndExpression> andNodes = expr.findDescendantsOfType(ASTConditionalAndExpression.class);
        List<ASTConditionalOrExpression> orNodes = expr.findDescendantsOfType(ASTConditionalOrExpression.class);

        int children = 0;

        for (ASTConditionalOrExpression element : orNodes) {
            children += element.getNumChildren();
            children--;
        }

        for (ASTConditionalAndExpression element : andNodes) {
            children += element.getNumChildren();
            children--;
        }

        return children;
    }
}
