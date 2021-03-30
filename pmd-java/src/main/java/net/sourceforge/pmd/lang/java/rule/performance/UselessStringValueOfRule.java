/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class UselessStringValueOfRule extends AbstractJavaRule {


    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (JavaRuleUtil.isStringConcatExpr(node.getParent())) {
            ASTExpression valueOfArg = getValueOfArg(node);
            if (valueOfArg == null) {
                return data; //not a valueOf call
            } else if (TypeTestUtil.isExactlyA(String.class, valueOfArg)) {
                addViolation(data, node); // valueOf call on a string
                return data;
            }

            ASTExpression sibling = JavaRuleUtil.getOtherOperandIfInInfixExpr(node);
            if (TypeTestUtil.isExactlyA(String.class, sibling)
                && !valueOfArg.getTypeMirror().isArray()
                // In `String.valueOf(a) + String.valueOf(b)`,
                // only report the second call
                && (getValueOfArg(sibling) == null || node.getIndexInParent() == 1)) {
                addViolation(data, node);
            }
        }
        return data;
    }

    private static @Nullable ASTExpression getValueOfArg(ASTExpression expr) {
        if (expr instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) expr;
            if (call.getArguments().size() == 1
                && "valueOf".equals(call.getMethodName())
                && TypeTestUtil.isDeclaredInClass(String.class, call.getMethodType())) {
                return call.getArguments().get(0);
            }
        }
        return null;
    }

}
