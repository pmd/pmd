/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Insecure HTTP endpoints passed to (req.setEndpoint)
 * req.setHeader('Authorization') should use named credentials
 *
 * @author sergey.gorbaty
 *
 */
public class ApexInsecureEndpointRule extends AbstractApexRule {
    private static final String SET_ENDPOINT = "setEndpoint";
    private static final Pattern PATTERN = Pattern.compile("^http://.+?$", Pattern.CASE_INSENSITIVE);

    private final Set<String> httpEndpointStrings = new HashSet<>();

    @Override
    public RuleContext visit(ASTAssignmentExpression node, RuleContext data) {
        findInsecureEndpoints(node);
        return data;
    }

    @Override
    public RuleContext visit(ASTVariableDeclaration node, RuleContext data) {
        findInsecureEndpoints(node);
        return data;
    }

    @Override
    public RuleContext visit(ASTFieldDeclaration node, RuleContext data) {
        findInsecureEndpoints(node);
        return data;
    }

    private void findInsecureEndpoints(ApexNode<?> node) {
        ASTVariableExpression variableNode = node.firstChild(ASTVariableExpression.class);
        findInnerInsecureEndpoints(node, variableNode);

        ASTBinaryExpression binaryNode = node.firstChild(ASTBinaryExpression.class);
        if (binaryNode != null) {
            findInnerInsecureEndpoints(binaryNode, variableNode);
        }

    }

    private void findInnerInsecureEndpoints(ApexNode<?> node, ASTVariableExpression variableNode) {
        ASTLiteralExpression literalNode = node.firstChild(ASTLiteralExpression.class);

        if (literalNode != null && variableNode != null) {
            if (literalNode.isString()) {
                String literal = literalNode.getImage();
                if (PATTERN.matcher(literal).matches()) {
                    httpEndpointStrings.add(Helper.getFQVariableName(variableNode));
                }
            }
        }
    }

    @Override
    public RuleContext visit(ASTMethodCallExpression node, RuleContext data) {
        processInsecureEndpoint(node, data);
        return data;
    }

    private void processInsecureEndpoint(ASTMethodCallExpression node, RuleContext data) {
        if (!Helper.isMethodName(node, SET_ENDPOINT)) {
            return;
        }

        ASTBinaryExpression binaryNode = node.firstChild(ASTBinaryExpression.class);
        if (binaryNode != null) {
            runChecks(binaryNode, data);
        }

        runChecks(node, data);

    }

    private void runChecks(ApexNode<?> node, RuleContext data) {
        ASTLiteralExpression literalNode = node.firstChild(ASTLiteralExpression.class);
        if (literalNode != null && literalNode.isString()) {
            String literal = literalNode.getImage();
            if (PATTERN.matcher(literal).matches()) {
                data.addViolation(literalNode);
            }
        }

        ASTVariableExpression variableNode = node.firstChild(ASTVariableExpression.class);
        if (variableNode != null) {
            if (httpEndpointStrings.contains(Helper.getFQVariableName(variableNode))) {
                data.addViolation(variableNode);
            }

        }
    }
}
