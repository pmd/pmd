/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Rule that marks instantiations of new {@link BigInteger} or {@link BigDecimal} objects, when there is a well-known
 * constant available, such as {@link BigInteger#ZERO}.
 */
public class BigIntegerInstantiationRule extends AbstractJavaRulechainRule {

    // BigDecimal.ZERO, ONE, TEN: since 1.5
    private static final Set<String> BIGDECIMAL_CONSTANTS = CollectionUtil.setOf("0", "0.", "1", "10");


    public BigIntegerInstantiationRule() {
        super(ASTConstructorCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        LanguageVersion languageVersion = node.getTextDocument().getLanguageVersion();

        @NonNull
        ASTArgumentList arguments = node.getArguments();
        if (arguments.size() != 1) {
            // only consider the single argument constructors
            return data;
        }

        // might be a String, an Integer, a Long, a Double, or a BigInteger
        Object constValue = arguments.get(0).getConstValue();

        boolean java5 = languageVersion.compareToVersion("1.5") >= 0;
        boolean java9 = languageVersion.compareToVersion("9") >= 0;
        boolean java19 = languageVersion.compareToVersion("19") >= 0;

        if (TypeTestUtil.isA(BigInteger.class, node)) {
            // BigInteger.ZERO, ONE: since 1.2
            if ("0".equals(constValue) || "1".equals(constValue)) {
                asCtx(data).addViolation(node);
            }
            // BigInteger.TEN: since 1.5
            if (java5 && "10".equals(constValue)) {
                asCtx(data).addViolation(node);
            }
            // BigInteger.TWO: since 9
            if (java9 && "2".equals(constValue)) {
                asCtx(data).addViolation(node);
            }
        } else if (TypeTestUtil.isA(BigDecimal.class, node)) {
            // BigDecimal.ZERO, ONE, TEN: since 1.5
            if (java5 && BIGDECIMAL_CONSTANTS.contains(String.valueOf(constValue))) {
                asCtx(data).addViolation(node);
            }
            // BigDecimal.TWO: since 19
            if (java19 && "2".equals(String.valueOf(constValue))) {
                asCtx(data).addViolation(node);
            }
        }
        return data;
    }
}
