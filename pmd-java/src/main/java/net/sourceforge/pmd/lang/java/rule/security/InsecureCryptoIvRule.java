/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

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
public class InsecureCryptoIvRule extends AbstractJavaRule {

    public InsecureCryptoIvRule() {
        addRuleChainVisit(ASTAllocationExpression.class);
    }

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        ASTClassOrInterfaceType declClassName = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
        if (declClassName != null && TypeHelper.isA(declClassName, javax.crypto.spec.IvParameterSpec.class)) {
            Node firstArgument = null;

            ASTArguments arguments = node.getFirstChildOfType(ASTArguments.class);
            if (arguments.getArgumentCount() > 0) {
                firstArgument = arguments.getFirstChildOfType(ASTArgumentList.class).getChild(0);
            }

            if (firstArgument != null) {
                ASTPrimaryPrefix prefix = firstArgument.getFirstDescendantOfType(ASTPrimaryPrefix.class);
                validateProperIv(data, prefix);
            }
        }
        return data;
    }

    private void validateProperIv(Object data, ASTPrimaryPrefix firstArgumentExpression) {
        if (firstArgumentExpression == null) {
            return;
        }

        // named variable
        ASTName namedVar = firstArgumentExpression.getFirstDescendantOfType(ASTName.class);
        if (namedVar != null) {
            // find where it's declared, if possible
            if (namedVar != null && namedVar.getNameDeclaration() instanceof VariableNameDeclaration) {
                VariableNameDeclaration varDecl = (VariableNameDeclaration) namedVar.getNameDeclaration();
                ASTVariableInitializer initializer = varDecl.getAccessNodeParent().getFirstDescendantOfType(ASTVariableInitializer.class);
                if (initializer != null) {
                    validateProperIv(data, initializer.getFirstDescendantOfType(ASTPrimaryPrefix.class));
                }
            }
        }

        // hard coded array
        ASTArrayInitializer arrayInit = firstArgumentExpression.getFirstDescendantOfType(ASTArrayInitializer.class);
        if (arrayInit != null) {
            addViolation(data, firstArgumentExpression);
        }

        // string literal
        ASTLiteral literal = firstArgumentExpression.getFirstDescendantOfType(ASTLiteral.class);
        if (literal != null && literal.isStringLiteral()) {
            addViolation(data, firstArgumentExpression);
        }
    }
}
