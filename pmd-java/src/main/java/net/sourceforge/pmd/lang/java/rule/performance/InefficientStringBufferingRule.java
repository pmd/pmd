/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * How this rule works: find additive expressions: + check that the addition is
 * between anything other than two literals if true and also the parent is
 * StringBuffer constructor or append, report a violation.
 *
 * @author mgriffa
 */
public class InefficientStringBufferingRule extends AbstractJavaRulechainRule {

    public InefficientStringBufferingRule() {
        super(ASTConstructorCall.class, ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (JavaRuleUtil.isStringBuilderCtorOrAppend(node)) {
            checkArgument(node.getArguments(), (RuleContext) data);
        }
        return null;
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        if (JavaRuleUtil.isStringBuilderCtorOrAppend(node)) {
            checkArgument(node.getArguments(), (RuleContext) data);
        }
        return null;
    }

    private void checkArgument(ASTArgumentList argList, RuleContext ctx) {
        ASTExpression arg = ASTList.singleOrNull(argList);

        if (JavaAstUtils.isStringConcatExpr(arg)
            // ignore concatenations that produce constants
            && !arg.isCompileTimeConstant()) {
            ctx.addViolation(arg);
        }
    }

    static boolean isInStringBufferOperationChain(Node node, String append) {
        // TODO this was replaced by something that doesn't really work
        // this was/is used by ConsecutiveLiteralAppendsRule
        if (!(node instanceof ASTExpression)) {
            return false;
        }
        Node parent = node.getParent();

        return parent instanceof ASTMethodCall
            && JavaRuleUtil.isStringBuilderCtorOrAppend((ASTMethodCall) parent);
    }
}
