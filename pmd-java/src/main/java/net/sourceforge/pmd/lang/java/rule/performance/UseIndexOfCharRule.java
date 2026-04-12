/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 *
 */
public class UseIndexOfCharRule extends AbstractJavaRulechainRule {

    public UseIndexOfCharRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public RuleContext visit(ASTMethodCall node, RuleContext data) {
        if ("indexOf".equals(node.getMethodName()) || "lastIndexOf".equals(node.getMethodName())) {
            if (TypeTestUtil.isA(String.class, node.getQualifier())
                && node.getArguments().size() >= 1) { // there are two overloads of each
                ASTExpression arg = node.getArguments().get(0);
                if (arg instanceof ASTStringLiteral && ((ASTStringLiteral) arg).getConstValue().length() == 1) {
                    data.addViolation(node);
                }
            }
        }
        return data;
    }

}
