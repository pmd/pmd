/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

public class MethodWithSameNameAsEnclosingClassRule extends AbstractApexRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }


    @Override
    public RuleContext visit(ASTUserClass node, RuleContext data) {
        String className = node.getSimpleName();

        for (ASTMethod m : node.descendants(ASTMethod.class)) {
            String methodName = m.getImage();

            if (!m.isConstructor() && methodName.equalsIgnoreCase(className)) {
                data.addViolation(m);
            }
        }

        return data;
    }
}
