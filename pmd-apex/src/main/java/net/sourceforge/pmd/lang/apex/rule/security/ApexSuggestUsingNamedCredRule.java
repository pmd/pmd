/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

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

    public ApexSuggestUsingNamedCredRule() {
        super.addRuleChainVisit(ASTUserClass.class);
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node)) {
            return data;
        }

        List<ASTVariableDeclaration> variableDecls = node.findDescendantsOfType(ASTVariableDeclaration.class);
        for (ASTVariableDeclaration varDecl : variableDecls) {
            findAuthLiterals(varDecl);
        }

        List<ASTField> fieldDecl = node.findDescendantsOfType(ASTField.class);
        for (ASTField fDecl : fieldDecl) {
            findFieldLiterals(fDecl);
        }

        List<ASTMethodCallExpression> methodCalls = node.findDescendantsOfType(ASTMethodCallExpression.class);
        for (ASTMethodCallExpression method : methodCalls) {
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

        final ASTBinaryExpression binaryNode = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (binaryNode != null) {
            runChecks(binaryNode, data);
        }

        runChecks(node, data);

    }

    private void findAuthLiterals(final AbstractApexNode<?> node) {
        ASTLiteralExpression literal = node.getFirstChildOfType(ASTLiteralExpression.class);
        if (literal != null) {
            ASTVariableExpression variable = node.getFirstChildOfType(ASTVariableExpression.class);
            if (variable != null) {
                if (isAuthorizationLiteral(literal)) {
                    listOfAuthorizationVariables.add(Helper.getFQVariableName(variable));
                }
            }
        }
    }

    private void runChecks(final AbstractApexNode<?> node, Object data) {
        ASTLiteralExpression literalNode = node.getFirstChildOfType(ASTLiteralExpression.class);
        if (literalNode != null) {
            if (isAuthorizationLiteral(literalNode)) {
                addViolation(data, literalNode);
            }
        }

        final ASTVariableExpression varNode = node.getFirstChildOfType(ASTVariableExpression.class);
        if (varNode != null) {
            if (listOfAuthorizationVariables.contains(Helper.getFQVariableName(varNode))) {
                addViolation(data, varNode);
            }
        }
    }

    private boolean isAuthorizationLiteral(final ASTLiteralExpression literal) {
        if (literal.isString()) {
            String lit = literal.getImage();
            if (lit.equalsIgnoreCase(AUTHORIZATION)) {
                return true;
            }
        }

        return false;
    }
}
