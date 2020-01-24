/**
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
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

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

    public ApexInsecureEndpointRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        findInsecureEndpoints(node);
        return data;
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        findInsecureEndpoints(node);
        return data;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        findInsecureEndpoints(node);
        return data;
    }

    private void findInsecureEndpoints(AbstractApexNode<?> node) {
        ASTVariableExpression variableNode = node.getFirstChildOfType(ASTVariableExpression.class);
        findInnerInsecureEndpoints(node, variableNode);

        ASTBinaryExpression binaryNode = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (binaryNode != null) {
            findInnerInsecureEndpoints(binaryNode, variableNode);
        }

    }

    private void findInnerInsecureEndpoints(AbstractApexNode<?> node, ASTVariableExpression variableNode) {
        ASTLiteralExpression literalNode = node.getFirstChildOfType(ASTLiteralExpression.class);

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
    public Object visit(ASTMethodCallExpression node, Object data) {
        processInsecureEndpoint(node, data);
        return data;
    }

    private void processInsecureEndpoint(ASTMethodCallExpression node, Object data) {
        if (!Helper.isMethodName(node, SET_ENDPOINT)) {
            return;
        }

        ASTBinaryExpression binaryNode = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (binaryNode != null) {
            runChecks(binaryNode, data);
        }

        runChecks(node, data);

    }

    private void runChecks(AbstractApexNode<?> node, Object data) {
        ASTLiteralExpression literalNode = node.getFirstChildOfType(ASTLiteralExpression.class);
        if (literalNode != null && literalNode.isString()) {
            String literal = literalNode.getImage();
            if (PATTERN.matcher(literal).matches()) {
                addViolation(data, literalNode);
            }
        }

        ASTVariableExpression variableNode = node.getFirstChildOfType(ASTVariableExpression.class);
        if (variableNode != null) {
            if (httpEndpointStrings.contains(Helper.getFQVariableName(variableNode))) {
                addViolation(data, variableNode);
            }

        }
    }
}
