/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;

public class SuspiciousHashcodeMethodNameRule extends AbstractJavaRulechainRule {

    public SuspiciousHashcodeMethodNameRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        String name = node.getName();
        if ("hashcode".equalsIgnoreCase(name)
            && !"hashCode".equals(name)
            && node.getArity() == 0
            && JavaRuleUtil.isPrimitiveType(node.getResultTypeNode(), PrimitiveTypeKind.INT)) {

            addViolation(data, node);
        }
        return null;
    }

}
