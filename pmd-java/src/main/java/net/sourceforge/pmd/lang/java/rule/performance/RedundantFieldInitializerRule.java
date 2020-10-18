/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCharLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Detects redundant field initializers, i.e. the field initializer expressions
 * the JVM would assign by default.
 *
 * @author lucian.ciufudean@gmail.com
 * @since Apr 10, 2009
 */
public class RedundantFieldInitializerRule extends AbstractJavaRule {

    public RedundantFieldInitializerRule() {
        addRuleChainVisit(ASTFieldDeclaration.class);
    }

    @Override
    public Object visit(ASTFieldDeclaration fieldDeclaration, Object data) {
        if (declaresNotFinalField(fieldDeclaration)) {
            for (ASTVariableDeclarator varDecl : fieldDeclaration.descendants(ASTVariableDeclarator.class)) {
                if (hasRedundantInitializer(fieldDeclaration, varDecl)) {
                    addViolation(data, varDecl);
                }
            }
        }
        return data;
    }

    private boolean declaresNotFinalField(ASTFieldDeclaration fieldDeclaration) {
        return !fieldDeclaration.hasModifiers(JModifier.FINAL);
    }

    private boolean hasRedundantInitializer(ASTFieldDeclaration fieldDeclaration, ASTVariableDeclarator varDecl) {
        return declaresFieldOfPrimitiveType(fieldDeclaration)
            && hasRedundantInitializerOfPrimitive(varDecl)
            || hasRedundantInitializerOfReference(varDecl);
    }

    private boolean declaresFieldOfPrimitiveType(ASTFieldDeclaration fieldDeclaration) {
        return fieldDeclaration.getTypeNode() instanceof ASTPrimitiveType;
    }

    private boolean hasRedundantInitializerOfPrimitive(ASTVariableDeclarator varDecl) {
        ASTLiteral literal = getLiteralValue(varDecl.getInitializer());
        if (literal != null) {
            if (literal instanceof ASTNumericLiteral) {
                return hasDefaultNumericValue((ASTNumericLiteral) literal);
            } else if (literal instanceof ASTCharLiteral) {
                return hasDefaultCharLiteralValue((ASTCharLiteral) literal);
            } else if (literal instanceof ASTBooleanLiteral) {
                return isDefaultBooleanLiteral((ASTBooleanLiteral) literal);
            }
        }
        return false;
    }

    private boolean hasDefaultNumericValue(ASTNumericLiteral literal) {
        return literal.getConstValue().doubleValue() == 0;
    }

    private boolean hasDefaultCharLiteralValue(ASTCharLiteral literal) {
        return literal.getConstValue() == '\0';
    }

    private boolean isDefaultBooleanLiteral(ASTBooleanLiteral literal) {
        return !literal.isTrue();
    }

    private boolean hasRedundantInitializerOfReference(ASTVariableDeclarator varDecl) {
        return getLiteralValue(varDecl.getInitializer()) instanceof ASTNullLiteral;
    }

    private ASTLiteral getLiteralValue(ASTExpression expr) {
        if (expr instanceof ASTLiteral) {
            return (ASTLiteral) expr;
        } else if (expr instanceof ASTCastExpression) {
            return getLiteralValue(((ASTCastExpression) expr).getOperand());
        }
        return null;
    }

}
