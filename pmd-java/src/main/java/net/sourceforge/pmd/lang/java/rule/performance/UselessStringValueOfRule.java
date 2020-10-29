/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class UselessStringValueOfRule extends AbstractJavaRule {


    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (node.getParent() instanceof ASTInfixExpression
            && ((ASTInfixExpression) node.getParent()).getOperator() == BinaryOp.ADD) {
            ASTExpression valueOfArg = getValueOfArg(node);
            if (valueOfArg == null) {
                return data; //not a valueOf call
            } else if (TypeTestUtil.isExactlyA(String.class, valueOfArg)) {
                addViolation(data, node); // valueOf call on a string
                return data;
            }

            ASTExpression sibling = (ASTExpression) node.getParent().getChild(1 - node.getIndexInParent());
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
                && call.getMethodName().equals("valueOf")
                && TypeTestUtil.isExactlyA(String.class, call.getMethodType().getDeclaringType().getSymbol())) {
                return call.getArguments().get(0);
            }
        }
        return null;
    }

}
