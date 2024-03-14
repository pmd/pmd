/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

/**
 * Constructor and init method might contain DML, which constitutes a CSRF
 * vulnerability
 *
 * @author sergey.gorbaty
 *
 */
public class ApexCSRFRule extends AbstractApexRule {
    public static final String INIT = "init";

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

    @Override
    public Object visit(ASTBlockStatement node, Object data) {
        if (node.getParent() instanceof ASTUserClass && Helper.foundAnyDML(node)) {
            asCtx(data).addViolation(node);
        }
        return data;
    }

    private void checkForCSRF(ASTMethod node, Object data) {
        if (node.isConstructor() && Helper.foundAnyDML(node)) {
            asCtx(data).addViolation(node);
        }

        String name = node.getImage();
        if ((node.isStaticInitializer() || isInitializerMethod(name)) && Helper.foundAnyDML(node)) {
            asCtx(data).addViolation(node);
        }
    }

    private boolean isInitializerMethod(String name) {
        return INIT.equalsIgnoreCase(name);
    }
}
