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

    private final Set<String> listOfAuthorizationVariables = new HashSet<>();

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
            findAuthLiterals(varDecl);
        }

        for (ASTField fDecl : node.descendants(ASTField.class)) {
            findFieldLiterals(fDecl);
        }

        for (ASTMethodCallExpression method : node.descendants(ASTMethodCallExpression.class)) {
            flagAuthorizationHeaders(method, data);
        }

        listOfAuthorizationVariables.clear();

        return data;
    }

    private void findFieldLiterals(final ASTField fDecl) {
        if ("String".equals(fDecl.getType()) && AUTHORIZATION.equalsIgnoreCase(fDecl.getValue())) {
            listOfAuthorizationVariables.add(Helper.getFQVariableName(fDecl));
        }
    }

    private void flagAuthorizationHeaders(final ASTMethodCallExpression node, Object data) {
        if (!Helper.isMethodName(node, SET_HEADER)) {
            return;
        }

        final ASTBinaryExpression binaryNode = node.firstChild(ASTBinaryExpression.class);
        if (binaryNode != null) {
            runChecks(binaryNode, data);
        }

        runChecks(node, data);

    }

    private void findAuthLiterals(final ApexNode<?> node) {
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

    private void runChecks(final ApexNode<?> node, Object data) {
        ASTLiteralExpression literalNode = node.firstChild(ASTLiteralExpression.class);
        if (literalNode != null) {
            if (isAuthorizationLiteral(literalNode)) {
                asCtx(data).addViolation(literalNode);
            }
        }

        final ASTVariableExpression varNode = node.firstChild(ASTVariableExpression.class);
        if (varNode != null) {
            if (listOfAuthorizationVariables.contains(Helper.getFQVariableName(varNode))) {
                asCtx(data).addViolation(varNode);
            }
        }
    }

    private boolean isAuthorizationLiteral(final ASTLiteralExpression literal) {
        if (literal.isString()) {
            String lit = literal.getImage();
            if (AUTHORIZATION.equalsIgnoreCase(lit)) {
                return true;
            }
        }

        return false;
    }
}
