/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

/**
 * Flags dangerous method calls, e.g. FinancialForce
 * Configuration.disableTriggerCRUDSecurity() or System.debug with sensitive
 * input
 *
 *
 * @author sergey.gorbaty
 *
 */
public class ApexDangerousMethodsRule extends AbstractApexRule {
    private static final String BOOLEAN = "boolean";

    private static final Pattern REGEXP = Pattern.compile("^.*?(pass|pwd|crypt|auth|session|token|saml)(?!id|user).*?$",
            Pattern.CASE_INSENSITIVE);

    private static final String DISABLE_CRUD = "disableTriggerCRUDSecurity";
    private static final String CONFIGURATION = "Configuration";
    private static final String SYSTEM = "System";
    private static final String DEBUG = "debug";

    private final Set<String> whiteListedVariables = new HashSet<>();

    public ApexDangerousMethodsRule() {
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

        collectBenignVariables(node);

        List<ASTMethodCallExpression> methodCalls = node.findDescendantsOfType(ASTMethodCallExpression.class);
        for (ASTMethodCallExpression methodCall : methodCalls) {
            if (Helper.isMethodName(methodCall, CONFIGURATION, DISABLE_CRUD)) {
                addViolation(data, methodCall);
            }

            if (Helper.isMethodName(methodCall, SYSTEM, DEBUG)) {
                validateParameters(methodCall, data);
            }
        }

        whiteListedVariables.clear();

        return data;
    }

    private void collectBenignVariables(ASTUserClass node) {
        List<ASTField> fields = node.findDescendantsOfType(ASTField.class);
        for (ASTField field : fields) {
            if (BOOLEAN.equalsIgnoreCase(field.getType())) {
                whiteListedVariables.add(Helper.getFQVariableName(field));
            }

        }

        List<ASTVariableDeclaration> declarations = node.findDescendantsOfType(ASTVariableDeclaration.class);
        for (ASTVariableDeclaration decl : declarations) {
            if (BOOLEAN.equalsIgnoreCase(decl.getType())) {
                whiteListedVariables.add(Helper.getFQVariableName(decl));
            }
        }

    }

    private void validateParameters(ASTMethodCallExpression methodCall, Object data) {
        List<ASTVariableExpression> variables = methodCall.findDescendantsOfType(ASTVariableExpression.class);
        for (ASTVariableExpression var : variables) {
            if (REGEXP.matcher(var.getImage()).matches()) {
                if (!whiteListedVariables.contains(Helper.getFQVariableName(var))) {
                    addViolation(data, methodCall);
                }
            }
        }
    }

}
