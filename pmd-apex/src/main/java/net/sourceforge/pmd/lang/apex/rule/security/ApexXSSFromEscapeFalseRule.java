/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Finds all .addError method calls that are not HTML escaped on purpose
 *
 * @author sergey.gorbaty
 *
 */
public class ApexXSSFromEscapeFalseRule extends AbstractApexRule {
    private static final String ADD_ERROR = "addError";

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        for (ASTMethodCallExpression methodCall : node.descendants(ASTMethodCallExpression.class)) {
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
        for (ASTVariableExpression v : methodCall.descendants(ASTVariableExpression.class)) {
            asCtx(data).addViolation(v);
        }
    }

}
