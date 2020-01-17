/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

/**
 * Finds all .addError method calls that are not HTML escaped on purpose
 *
 * @author sergey.gorbaty
 *
 */
public class ApexXSSFromEscapeFalseRule extends AbstractApexRule {
    private static final String ADD_ERROR = "addError";

    public ApexXSSFromEscapeFalseRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        List<ASTMethodCallExpression> methodCalls = node.findDescendantsOfType(ASTMethodCallExpression.class);
        for (ASTMethodCallExpression methodCall : methodCalls) {
            if (Helper.isMethodName(methodCall, ADD_ERROR)) {
                validateBooleanParameter(methodCall, data);
            }
        }
        return data;
    }

    private void validateBooleanParameter(ASTMethodCallExpression methodCall, Object data) {
        int numberOfChildren = methodCall.getNumChildren();
        if (numberOfChildren == 3) { // addError('',false)
            Object potentialLiteral = methodCall.getChild(2);
            if (potentialLiteral instanceof ASTLiteralExpression) {
                ASTLiteralExpression parameter = (ASTLiteralExpression) potentialLiteral;
                if (parameter.isBoolean()) {
                    boolean paramValue = Boolean.parseBoolean(parameter.getImage());
                    if (!paramValue) {
                        validateLiteralPresence(methodCall, data);
                    }
                }
            }
        }
    }

    private void validateLiteralPresence(ASTMethodCallExpression methodCall, Object data) {
        List<ASTVariableExpression> variables = methodCall.findDescendantsOfType(ASTVariableExpression.class);
        for (ASTVariableExpression v : variables) {
            addViolation(data, v);
        }
    }

}
