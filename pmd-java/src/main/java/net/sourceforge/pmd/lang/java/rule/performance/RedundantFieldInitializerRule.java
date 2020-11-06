/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
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
            List<ASTVariableDeclarator> varDecls = fieldDeclaration.findDescendantsOfType(ASTVariableDeclarator.class);
            for (ASTVariableDeclarator varDecl : varDecls) {
                if (hasRedundantInitializer(fieldDeclaration, varDecl)) {
                    addViolation(data, varDecl);
                }
            }
        }
        return data;
    }

    private boolean declaresNotFinalField(ASTFieldDeclaration fieldDeclaration) {
        return !fieldDeclaration.isFinal();
    }

    private boolean hasRedundantInitializer(ASTFieldDeclaration fieldDeclaration, ASTVariableDeclarator varDecl) {
        return declaresFieldOfPrimitiveType(fieldDeclaration)
                && hasRedundantInitializerOfPrimitive(varDecl)
                || hasRedundantInitializerOfReference(varDecl);
    }

    private boolean declaresFieldOfPrimitiveType(ASTFieldDeclaration fieldDeclaration) {
        return fieldDeclaration.getChild(0).getChild(0) instanceof ASTPrimitiveType;
    }

    private boolean hasRedundantInitializerOfPrimitive(ASTVariableDeclarator varDecl) {
        ASTLiteral literal = getLiteralValue(varDecl);
        if (literal != null) {
            if (isNumericLiteral(literal)) {
                return getNumericLiteralValue(literal) == 0;
            }
            if (literal.isCharLiteral()) {
                return hasDefaultCharLiteralValue(literal);
            }
            return isDefaultBooleanLiteral(literal);
        }
        return false;
    }

    private boolean isNumericLiteral(ASTLiteral literal) {
        return literal.isIntLiteral() || literal.isLongLiteral()
                || literal.isFloatLiteral() || literal.isDoubleLiteral();
    }

    private double getNumericLiteralValue(ASTLiteral literal) {
        if (literal.isIntLiteral()) {
            return literal.getValueAsInt();
        }
        if (literal.isLongLiteral()) {
            return literal.getValueAsLong();
        }
        if (literal.isFloatLiteral()) {
            return literal.getValueAsFloat();
        }
        return literal.getValueAsDouble();
    }

    private boolean hasDefaultCharLiteralValue(ASTLiteral literal) {
        String img = literal.getImage();
        return img.contains("\u0000") || img.contains("\\0");
    }

    private boolean isDefaultBooleanLiteral(ASTLiteral literal) {
        ASTBooleanLiteral booleanLiteral = literal.getFirstDescendantOfType(ASTBooleanLiteral.class);
        return booleanLiteral != null && !booleanLiteral.isTrue();
    }

    private boolean hasRedundantInitializerOfReference(ASTVariableDeclarator varDecl) {
        ASTLiteral literal = getLiteralValue(varDecl);
        return literal != null && isNullLiteral(literal);
    }

    private ASTLiteral getLiteralValue(ASTVariableDeclarator varDecl) {
        ASTPrimaryPrefix prefix = varDecl.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        return prefix != null && prefix.getNumChildren() == 1
                ? prefix.getFirstChildOfType(ASTLiteral.class)
                : null;
    }

    private boolean isNullLiteral(ASTLiteral literal) {
        return literal.getFirstDescendantOfType(ASTNullLiteral.class) != null;
    }
}
