/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

/**
 * Detects potential XSS when controller extracts a variable from URL query and
 * uses it without escaping first
 *
 * @author sergey.gorbaty
 *
 */
public class ApexXSSFromURLParamRule extends AbstractApexRule {
    private static final String[] URL_PARAMETER_METHOD = new String[] { "ApexPages", "currentPage", "getParameters",
        "get", };
    private static final String[] HTML_ESCAPING = new String[] { "ESAPI", "encoder", "SFDC_HTMLENCODE" };
    private static final String[] JS_ESCAPING = new String[] { "ESAPI", "encoder", "SFDC_JSENCODE" };
    private static final String[] JSINHTML_ESCAPING = new String[] { "ESAPI", "encoder", "SFDC_JSINHTMLENCODE" };
    private static final String[] URL_ESCAPING = new String[] { "ESAPI", "encoder", "SFDC_URLENCODE" };
    private static final String[] STRING_HTML3 = new String[] { "String", "escapeHtml3" };
    private static final String[] STRING_HTML4 = new String[] { "String", "escapeHtml4" };
    private static final String[] STRING_XML = new String[] { "String", "escapeXml" };
    private static final String[] STRING_ECMASCRIPT = new String[] { "String", "escapeEcmaScript" };
    private static final String[] INTEGER_VALUEOF = new String[] { "Integer", "valueOf" };
    private static final String[] ID_VALUEOF = new String[] { "ID", "valueOf" };
    private static final String[] DOUBLE_VALUEOF = new String[] { "Double", "valueOf" };
    private static final String[] BOOLEAN_VALUEOF = new String[] { "Boolean", "valueOf" };
    private static final String[] STRING_ISEMPTY = new String[] { "String", "isEmpty" };
    private static final String[] STRING_ISBLANK = new String[] { "String", "isBlank" };
    private static final String[] STRING_ISNOTBLANK = new String[] { "String", "isNotBlank" };

    private final Set<String> urlParameterStrings = new HashSet<>();

    public ApexXSSFromURLParamRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 50);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        findTaintedVariables(node, data);
        processVariableAssignments(node, data, false);
        return data;
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        findTaintedVariables(node, data);
        processVariableAssignments(node, data, true);
        return data;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        findTaintedVariables(node, data);
        processVariableAssignments(node, data, true);
        return data;
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        processEscapingMethodCalls(node, data);
        processInlineMethodCalls(node, data, false);
        return data;
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        ASTBinaryExpression binaryExpression = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (binaryExpression != null) {
            processBinaryExpression(binaryExpression, data);
        }

        ASTMethodCallExpression methodCall = node.getFirstChildOfType(ASTMethodCallExpression.class);
        if (methodCall != null) {
            String retType = getReturnType(node);
            if ("string".equalsIgnoreCase(retType)) {
                processInlineMethodCalls(methodCall, data, true);
            }
        }

        List<ASTVariableExpression> nodes = node.findChildrenOfType(ASTVariableExpression.class);

        for (ASTVariableExpression varExpression : nodes) {
            if (urlParameterStrings.contains(Helper.getFQVariableName(varExpression))) {
                addViolation(data, nodes.get(0));
            }
        }

        return data;
    }

    private String getReturnType(ASTReturnStatement node) {
        ASTMethod method = node.getFirstParentOfType(ASTMethod.class);
        if (method != null) {
            return method.getReturnType();
        }

        return "";
    }

    private boolean isEscapingMethod(ASTMethodCallExpression methodNode) {
        // escaping methods
        return Helper.isMethodCallChain(methodNode, HTML_ESCAPING) || Helper.isMethodCallChain(methodNode, JS_ESCAPING)
                || Helper.isMethodCallChain(methodNode, JSINHTML_ESCAPING)
                || Helper.isMethodCallChain(methodNode, URL_ESCAPING)
                || Helper.isMethodCallChain(methodNode, STRING_HTML3)
                || Helper.isMethodCallChain(methodNode, STRING_HTML4)
                || Helper.isMethodCallChain(methodNode, STRING_XML)
                || Helper.isMethodCallChain(methodNode, STRING_ECMASCRIPT)
                // safe casts that eliminate injection
                || Helper.isMethodCallChain(methodNode, INTEGER_VALUEOF)
                || Helper.isMethodCallChain(methodNode, DOUBLE_VALUEOF)
                || Helper.isMethodCallChain(methodNode, BOOLEAN_VALUEOF)
                || Helper.isMethodCallChain(methodNode, ID_VALUEOF)
                // safe boolean methods
                || Helper.isMethodCallChain(methodNode, STRING_ISEMPTY)
                || Helper.isMethodCallChain(methodNode, STRING_ISBLANK)
                || Helper.isMethodCallChain(methodNode, STRING_ISNOTBLANK);
    }

    private void processInlineMethodCalls(ASTMethodCallExpression methodNode, Object data, final boolean isNested) {
        ASTMethodCallExpression nestedCall = methodNode.getFirstChildOfType(ASTMethodCallExpression.class);
        if (nestedCall != null) {

            if (!isEscapingMethod(methodNode)) {
                processInlineMethodCalls(nestedCall, data, true);
            }
        }

        if (Helper.isMethodCallChain(methodNode, URL_PARAMETER_METHOD)) {
            if (isNested) {
                addViolation(data, methodNode);
            }
        }

    }

    private void findTaintedVariables(AbstractApexNode<?> node, Object data) {
        final ASTMethodCallExpression right = node.getFirstChildOfType(ASTMethodCallExpression.class);
        // Looks for: (String) foo =
        // ApexPages.currentPage().getParameters().get(..)

        if (right != null) {
            if (Helper.isMethodCallChain(right, URL_PARAMETER_METHOD)) {
                ASTVariableExpression left = node.getFirstChildOfType(ASTVariableExpression.class);

                String varType = null;

                if (node instanceof ASTVariableDeclaration) {
                    varType = ((ASTVariableDeclaration) node).getType();

                }

                if (left != null) {
                    if (varType == null || !"id".equalsIgnoreCase(varType)) {
                        urlParameterStrings.add(Helper.getFQVariableName(left));
                    }
                }
            }

            processEscapingMethodCalls(right, data);
        }
    }

    private void processEscapingMethodCalls(ASTMethodCallExpression methodNode, Object data) {
        ASTMethodCallExpression nestedCall = methodNode.getFirstChildOfType(ASTMethodCallExpression.class);
        if (nestedCall != null) {
            processEscapingMethodCalls(nestedCall, data);
        }

        final ASTVariableExpression variable = methodNode.getFirstChildOfType(ASTVariableExpression.class);

        if (variable != null) {
            if (urlParameterStrings.contains(Helper.getFQVariableName(variable))) {
                if (!isEscapingMethod(methodNode)) {
                    addViolation(data, variable);
                }
            }
        }

    }

    private void processVariableAssignments(AbstractApexNode<?> node, Object data, final boolean reverseOrder) {
        ASTMethodCallExpression methodCallAssignment = node.getFirstChildOfType(ASTMethodCallExpression.class);
        if (methodCallAssignment != null) {

            String varType = null;
            if (node instanceof ASTVariableDeclaration) {
                varType = ((ASTVariableDeclaration) node).getType();
            }

            if (varType == null || !"id".equalsIgnoreCase(varType)) {
                processInlineMethodCalls(methodCallAssignment, data, false);
            }
        }

        List<ASTVariableExpression> nodes = node.findChildrenOfType(ASTVariableExpression.class);

        switch (nodes.size()) {
        case 1: {
            // Look for: foo + bar
            final List<ASTBinaryExpression> ops = node.findChildrenOfType(ASTBinaryExpression.class);
            if (!ops.isEmpty()) {
                for (ASTBinaryExpression o : ops) {
                    processBinaryExpression(o, data);
                }
            }

        }
            break;
        case 2: {
            // Look for: foo = bar;
            final ASTVariableExpression right = reverseOrder ? nodes.get(0) : nodes.get(1);

            if (urlParameterStrings.contains(Helper.getFQVariableName(right))) {
                addViolation(data, right);
            }
        }
            break;
        default:
            break;
        }

    }

    private void processBinaryExpression(AbstractApexNode<?> node, Object data) {
        ASTBinaryExpression nestedBinaryExpression = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (nestedBinaryExpression != null) {
            processBinaryExpression(nestedBinaryExpression, data);
        }

        ASTMethodCallExpression methodCallAssignment = node.getFirstChildOfType(ASTMethodCallExpression.class);
        if (methodCallAssignment != null) {
            processInlineMethodCalls(methodCallAssignment, data, true);
        }

        final List<ASTVariableExpression> nodes = node.findChildrenOfType(ASTVariableExpression.class);
        for (ASTVariableExpression n : nodes) {

            if (urlParameterStrings.contains(Helper.getFQVariableName(n))) {
                addViolation(data, n);
            }
        }
    }

}
