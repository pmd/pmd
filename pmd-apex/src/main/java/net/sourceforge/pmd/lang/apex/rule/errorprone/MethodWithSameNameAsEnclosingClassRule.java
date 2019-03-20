/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class MethodWithSameNameAsEnclosingClassRule extends AbstractApexRule {

    @Override
    public Object visit(ASTUserClass node, Object data) {
        String className = node.getImage();

        List<ASTMethod> methods = node.findDescendantsOfType(ASTMethod.class);

        for (ASTMethod m : methods) {
            String methodName = m.getImage();

            if (!m.getNode().getMethodInfo().isConstructor() && methodName.equalsIgnoreCase(className)) {
                addViolation(data, m);
            }
        }

        return super.visit(node, data);
    }
}
