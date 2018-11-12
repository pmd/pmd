/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Constructor and init method might contain DML, which constitutes a CSRF
 * vulnerability
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexCSRFRule extends AbstractApexRule {
    public static final String INIT = "init";

    public ApexCSRFRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
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
    public Object visit(ASTMethod node, Object data) {
        if (!Helper.isTestMethodOrClass(node)) {
            checkForCSRF(node, data);
        }
        return data;
    }

    /**
     * @param node
     * @param data
     */
    private void checkForCSRF(ASTMethod node, Object data) {
        if (node.getNode().getMethodInfo().isConstructor()) {
            if (Helper.foundAnyDML(node)) {
                addViolation(data, node);
            }

        }

        String name = node.getNode().getMethodInfo().getName();
        if (name.equalsIgnoreCase(INIT)) {
            if (Helper.foundAnyDML(node)) {
                addViolation(data, node);
            }
        }

    }
}
