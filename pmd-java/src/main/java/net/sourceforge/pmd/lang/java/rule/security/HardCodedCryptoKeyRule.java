/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Finds hard coded encryption keys that are passed to
 * javax.crypto.spec.SecretKeySpec(key, algorithm).
 *
 * @author sergeygorbaty
 * @since 6.4.0
 */
public class HardCodedCryptoKeyRule extends AbstractJavaRulechainRule {

    private static final Class<?> SECRET_KEY_SPEC = javax.crypto.spec.SecretKeySpec.class;

    public HardCodedCryptoKeyRule() {
        super(ASTConstructorCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        if (TypeTestUtil.isA(SECRET_KEY_SPEC, node)) {
            ASTArgumentList arguments = node.getArguments();
            if (arguments.size() > 0) {
                validateProperKeyArgument(data, arguments.get(0));
            }
        }
        return data;
    }

    /**
     * Recursively resolves the argument again, if the variable initializer
     * is itself a expression.
     *
     * <p>Then checks the expression for being a string literal or array
     */
    private void validateProperKeyArgument(Object data, ASTExpression firstArgumentExpression) {
        // named variable
        if (firstArgumentExpression instanceof ASTVariableAccess) {
            ASTVariableAccess varAccess = (ASTVariableAccess) firstArgumentExpression;
            if (varAccess.getSignature() != null && varAccess.getSignature().getSymbol() != null) {
                ASTVariableDeclaratorId varDecl = varAccess.getSignature().getSymbol().tryGetNode();
                validateProperKeyArgument(data, varDecl.getInitializer());
            }
        }

        // hard coded array
        if (firstArgumentExpression instanceof ASTArrayAllocation) {
            ASTArrayInitializer arrayInit = ((ASTArrayAllocation) firstArgumentExpression).getArrayInitializer();
            if (arrayInit != null) {
                addViolation(data, arrayInit);
            }
        }

        // string literal
        ASTStringLiteral literal = firstArgumentExpression.descendants(ASTStringLiteral.class).first();
        if (literal != null) {
            addViolation(data, literal);
        }
    }
}
