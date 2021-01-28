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
 * Finds hardcoded static Initialization Vectors vectors used with cryptographic
 * operations.
 *
 * <code>
 * //bad: byte[] ivBytes = new byte[] {32, 87, -14, 25, 78, -104, 98, 40};
 * //bad: byte[] ivBytes = "hardcoded".getBytes();
 * //bad: byte[] ivBytes = someString.getBytes();
 * </code>
 *
 * <p>{@link javax.crypto.spec.IvParameterSpec} must not be created from a static sources
 *
 * @author sergeygorbaty
 * @since 6.3.0
 *
 */
public class InsecureCryptoIvRule extends AbstractJavaRulechainRule {

    private static final Class<?> IV_PARAMETER_SPEC = javax.crypto.spec.IvParameterSpec.class;

    public InsecureCryptoIvRule() {
        super(ASTConstructorCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        if (TypeTestUtil.isA(IV_PARAMETER_SPEC, node)) {
            ASTArgumentList arguments = node.getArguments();
            if (arguments.size() > 0) {
                validateProperIv(data, arguments.get(0));
            }
        }
        return data;
    }

    private void validateProperIv(Object data, ASTExpression firstArgumentExpression) {
        if (firstArgumentExpression == null) {
            return;
        }

        // named variable
        if (firstArgumentExpression instanceof ASTVariableAccess) {
            ASTVariableAccess varAccess = (ASTVariableAccess) firstArgumentExpression;
            if (varAccess.getSignature() != null && varAccess.getSignature().getSymbol() != null) {
                ASTVariableDeclaratorId varDecl = varAccess.getSignature().getSymbol().tryGetNode();
                validateProperIv(data, varDecl.getInitializer());
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
