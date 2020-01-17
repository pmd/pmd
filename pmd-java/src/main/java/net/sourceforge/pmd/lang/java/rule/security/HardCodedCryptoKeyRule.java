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
 * Finds hard coded encryption keys that are passed to
 * javax.crypto.spec.SecretKeySpec(key, algorithm).
 *
 * @author sergeygorbaty
 * @since 6.4.0
 */
public class HardCodedCryptoKeyRule extends AbstractJavaRule {

    private static final Class<?> SECRET_KEY_SPEC = javax.crypto.spec.SecretKeySpec.class;

    public HardCodedCryptoKeyRule() {
        addRuleChainVisit(ASTAllocationExpression.class);
    }

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        ASTClassOrInterfaceType declClassName = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
        if (declClassName != null && TypeHelper.isA(declClassName, SECRET_KEY_SPEC)) {
            Node firstArgument = null;

            ASTArguments arguments = node.getFirstChildOfType(ASTArguments.class);
            if (arguments.getArgumentCount() > 0) {
                firstArgument = arguments.getFirstChildOfType(ASTArgumentList.class).getChild(0);
            }

            if (firstArgument != null) {
                ASTPrimaryPrefix prefix = firstArgument.getFirstDescendantOfType(ASTPrimaryPrefix.class);
                validateProperKeyArgument(data, prefix);
            }
        }
        return data;
    }

    /**
     * Recursively resolves the argument again, if the variable initializer
     * is itself a expression.
     *
     * Then checks the expression for being a string literal or array
     *
     * @param data
     * @param firstArgumentExpression
     */
    private void validateProperKeyArgument(Object data, ASTPrimaryPrefix firstArgumentExpression) {
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
                    validateProperKeyArgument(data, initializer.getFirstDescendantOfType(ASTPrimaryPrefix.class));
                }
            }
        }

        // hard coded array
        ASTArrayInitializer arrayInit = firstArgumentExpression.getFirstDescendantOfType(ASTArrayInitializer.class);
        if (arrayInit != null) {
            addViolation(data, arrayInit);
        }

        // string literal
        ASTLiteral literal = firstArgumentExpression.getFirstDescendantOfType(ASTLiteral.class);
        if (literal != null && literal.isStringLiteral()) {
            addViolation(data, literal);
        }
    }
}
