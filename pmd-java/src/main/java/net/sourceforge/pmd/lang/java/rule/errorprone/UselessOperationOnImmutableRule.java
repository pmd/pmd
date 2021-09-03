/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * An operation on an Immutable object (String, BigDecimal or BigInteger) won't
 * change the object itself. The result of the operation is a new object.
 * Therefore, ignoring the operation result is an error.
 */
public class UselessOperationOnImmutableRule extends AbstractJavaRulechainRule {

    public UselessOperationOnImmutableRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        ASTExpression qualifier = node.getQualifier();
        if (node.getParent() instanceof ASTExpressionStatement && qualifier != null) {

            // these types are immutable, so any method of those whose
            // result is ignored is a violation
            if (TypeTestUtil.isA(String.class, qualifier)
                || TypeTestUtil.isA(BigDecimal.class, qualifier)
                || TypeTestUtil.isA(BigInteger.class, qualifier)) {
                addViolation(data, node);
            }
        }
        return null;
    }

}
