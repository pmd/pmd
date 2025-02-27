/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Flags usage of http request.setHeader('Authorization',..) and suggests using
 * named credentials which helps store credentials for the callout in a safe
 * place.
 *
 * @author sergey.gorbaty
 *
 */
public class ApexSuggestUsingNamedCredRule extends AbstractApexRule {
    private static final String SET_HEADER = "setHeader";
    private static final String AUTHORIZATION = "Authorization";
    private static final String CREDENTIAL_PREFIX = "{!$Credential.";

    private final Set<String> listOfAuthorizationVariables = new HashSet<>();
    private final Set<String> listOfCredentialVariables = new HashSet<>();

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node)) {
            return data;
        }

        for (ASTVariableDeclaration varDecl : node.descendants(ASTVariableDeclaration.class)) {
            findAuthVariables(varDecl);
            findCredentialVariables(varDecl);
        }

        for (ASTField fDecl : node.descendants(ASTField.class)) {
            findAuthFields(fDecl);
            findCredentialFields(fDecl);
        }

        for (ASTMethodCallExpression method : node.descendants(ASTMethodCallExpression.class)) {
            flagAuthorizationHeaders(method, data);
        }

        listOfAuthorizationVariables.clear();
        listOfCredentialVariables.clear();

        return data;
    }

    private void findAuthVariables(final ApexNode<?> node) {
        ASTLiteralExpression literal = node.firstChild(ASTLiteralExpression.class);
        if (literal != null) {
            ASTVariableExpression variable = node.firstChild(ASTVariableExpression.class);
            if (variable != null) {
                if (isAuthorizationLiteral(literal)) {
                    listOfAuthorizationVariables.add(Helper.getFQVariableName(variable));
                }
            }
        }
    }

    private void findCredentialVariables(final ApexNode<?> node) {
        ASTLiteralExpression literal = node.firstChild(ASTLiteralExpression.class);
        if (literal != null) {
            ASTVariableExpression variable = node.firstChild(ASTVariableExpression.class);
            if (variable != null) {
                if (isCredentialLiteral(literal)) {
                    listOfCredentialVariables.add(Helper.getFQVariableName(variable));
                }
            }
        }
    }

    private void findAuthFields(final ASTField fDecl) {
        if ("String".equals(fDecl.getType()) && AUTHORIZATION.equalsIgnoreCase(fDecl.getValue())) {
            listOfAuthorizationVariables.add(Helper.getFQVariableName(fDecl));
        }
    }

    private void findCredentialFields(final ASTField fDecl) {
        if (!"String".equals(fDecl.getType())) {
            return;
        }

        String value = fDecl.getValue();

        if (value == null) {
            return;
        }

        if (value.contains(CREDENTIAL_PREFIX)) {
            listOfCredentialVariables.add(Helper.getFQVariableName(fDecl));
        }
    }

    private void flagAuthorizationHeaders(final ASTMethodCallExpression node, Object data) {
        if (!Helper.isMethodName(node, SET_HEADER)) {
            return;
        }

        runChecks(node, data);
    }

    private void runChecks(final ApexNode<?> node, Object data) {
        ApexNode<?> keyNode = node.getChild(1);
        ApexNode<?> valueNode = node.getChild(2);

        if (keyNode == null || !isAuthorizationReference(keyNode)) {
            return;
        }

        if (valueNode == null || !isCredentialReference(valueNode)) {
            asCtx(data).addViolation(keyNode);
        }
    }

    private boolean isAuthorizationReference(final ApexNode<?> node) {
        if (node instanceof ASTLiteralExpression) {
            return isAuthorizationLiteral((ASTLiteralExpression) node);
        }
        if (node instanceof ASTVariableExpression) {
            return isAuthorizationVariable((ASTVariableExpression) node);
        }
        return node instanceof ASTBinaryExpression && isAuthorizationReference(node.getChild(0));
    }

    private boolean isAuthorizationLiteral(final ASTLiteralExpression literal) {
        return literal.isString() && AUTHORIZATION.equalsIgnoreCase(literal.getImage());
    }

    private boolean isAuthorizationVariable(final ASTVariableExpression variable) {
        return listOfAuthorizationVariables.contains(Helper.getFQVariableName(variable));
    }

    private boolean isCredentialReference(ApexNode<?> node) {
        if (node instanceof ASTLiteralExpression) {
            return isCredentialLiteral((ASTLiteralExpression) node);
        }
        if (node instanceof ASTVariableExpression) {
            return isCredentialVariable((ASTVariableExpression) node);
        }
        return node instanceof ASTBinaryExpression && isCredentialBinaryExpression((ASTBinaryExpression) node);
    }

    private boolean isCredentialLiteral(final ASTLiteralExpression literal) {
        return literal.isString() && literal.getImage().contains(CREDENTIAL_PREFIX);
    }

    private boolean isCredentialVariable(final ASTVariableExpression variable) {
        return listOfCredentialVariables.contains(Helper.getFQVariableName(variable));
    }

    private boolean isCredentialBinaryExpression(ASTBinaryExpression binaryExpression) {
        for (int i = 0; i < binaryExpression.getNumChildren(); i++) {
            if (isCredentialReference(binaryExpression.getChild(i))) {
                return true;
            }
        }
        return false;
    }
}
