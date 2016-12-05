/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTDottedExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import apex.jorje.semantic.ast.expression.VariableExpression;

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
    private static final String[] INTEGER_VALUEOF = new String[] { "Integer", "valueOf" };
    private static final String[] ID_VALUEOF = new String[] { "ID", "valueOf" };
    private static final String[] DOUBLE_VALUEOF = new String[] { "Double", "valueOf" };
    private static final String[] STRING_ISEMPTY = new String[] { "String", "isEmpty" };

    private static final Set<String> urlParameterString = new HashSet<>();

    public ApexXSSFromURLParamRule() {
        setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 50);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
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
            processInlineMethodCalls(methodCall, data, true);
        }

        List<ASTVariableExpression> nodes = node.findChildrenOfType(ASTVariableExpression.class);

        for (ASTVariableExpression varExpression : nodes) {
            StringBuilder sb = new StringBuilder().append(varExpression.getNode().getDefiningType().getApexName())
                    .append(":").append(varExpression.getNode().getIdentifier().value);

            if (urlParameterString.contains(sb.toString())) {
                addViolation(data, nodes.get(0));
            }
        }

        return data;
    }

    private boolean isEscapingMethod(ASTMethodCallExpression methodNode) {
        return isMethodCallChain(methodNode, HTML_ESCAPING) || isMethodCallChain(methodNode, JS_ESCAPING)
                || isMethodCallChain(methodNode, JSINHTML_ESCAPING) || isMethodCallChain(methodNode, URL_ESCAPING)
                || isMethodCallChain(methodNode, INTEGER_VALUEOF) || isMethodCallChain(methodNode, DOUBLE_VALUEOF)
                || isMethodCallChain(methodNode, STRING_ISEMPTY) || isMethodCallChain(methodNode, ID_VALUEOF);
    }

    private void processInlineMethodCalls(ASTMethodCallExpression methodNode, Object data, final boolean isNested) {
        ASTMethodCallExpression nestedCall = methodNode.getFirstChildOfType(ASTMethodCallExpression.class);
        if (nestedCall != null) {

            if (!isEscapingMethod(methodNode)) {
                processInlineMethodCalls(nestedCall, data, true);
            }
        }

        if (isMethodCallChain(methodNode, URL_PARAMETER_METHOD)) {
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
            if (isMethodCallChain(right, URL_PARAMETER_METHOD)) {
                ASTVariableExpression left = node.getFirstChildOfType(ASTVariableExpression.class);

                if (left != null) {
                    VariableExpression n = left.getNode();
                    StringBuilder sb = new StringBuilder().append(n.getDefiningType()).append(":")
                            .append(n.getIdentifier().value);
                    urlParameterString.add(sb.toString());
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

        ASTVariableExpression variable = methodNode.getFirstChildOfType(ASTVariableExpression.class);

        if (variable != null) {

            // safe method
            if (isMethodCallChain(methodNode, INTEGER_VALUEOF) || isMethodCallChain(methodNode, ID_VALUEOF)
                    || isMethodCallChain(methodNode, DOUBLE_VALUEOF) || isMethodCallChain(methodNode, STRING_ISEMPTY)) {
                return;
            }

            VariableExpression n = variable.getNode();
            StringBuilder sb = new StringBuilder().append(n.getDefiningType()).append(":")
                    .append(n.getIdentifier().value);
            if (urlParameterString.contains(sb.toString())) {
                if (!isEscapingMethod(methodNode)) {
                    addViolation(data, variable);
                }
            }
        }

    }

    private void processVariableAssignments(AbstractApexNode<?> node, Object data, final boolean reverseOrder) {
        ASTMethodCallExpression methodCallAssignment = node.getFirstChildOfType(ASTMethodCallExpression.class);
        if (methodCallAssignment != null) {
            processInlineMethodCalls(methodCallAssignment, data, false);
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
            final ASTVariableExpression r = reverseOrder ? nodes.get(0) : nodes.get(1);
            final VariableExpression n = r.getNode();

            StringBuilder sb = new StringBuilder().append(n.getDefiningType()).append(":")
                    .append(n.getIdentifier().value);

            if (urlParameterString.contains(sb.toString())) {
                addViolation(data, r);
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
            final VariableExpression expression = n.getNode();
            StringBuilder sb = new StringBuilder().append(expression.getDefiningType().getApexName()).append(":")
                    .append(expression.getIdentifier().value);

            if (urlParameterString.contains(sb.toString())) {
                addViolation(data, n);
            }
        }
    }

    private boolean isMethodCallChain(ASTMethodCallExpression methodNode, final String... methodNames) {
        String methodName = methodNames[methodNames.length - 1];
        if (Helper.isMethodName(methodNode, methodName)) {
            ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
            if (reference != null) {
                ASTDottedExpression dottedExpression = reference.getFirstChildOfType(ASTDottedExpression.class);
                if (dottedExpression != null) {
                    ASTMethodCallExpression nestedMethod = dottedExpression
                            .getFirstChildOfType(ASTMethodCallExpression.class);
                    if (nestedMethod != null) {
                        String[] newMethodNames = Arrays.copyOf(methodNames, methodNames.length - 1);
                        return isMethodCallChain(nestedMethod, newMethodNames);
                    } else {
                        String[] newClassName = Arrays.copyOf(methodNames, methodNames.length - 1);
                        if (newClassName.length == 1) {
                            return Helper.isMethodName(methodNode, newClassName[0], methodName);
                        }
                    }
                }

            }
        }

        return false;
    }
}
