/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class OverrideBothEqualsAndHashcodeRule extends AbstractApexRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        ApexNode<?> equalsNode = null;
        ApexNode<?> hashNode = null;
        for (ASTMethod method : node.children(ASTMethod.class)) {
            if (equalsNode == null && isEquals(method)) {
                equalsNode = method;
            }
            if (hashNode == null && isHashCode(method)) {
                hashNode = method;
            }
            if (hashNode != null && equalsNode != null) {
                break;
            }
        }

        if (equalsNode != null && hashNode == null) {
            asCtx(data).addViolation(equalsNode);
        } else if (hashNode != null && equalsNode == null) {
            asCtx(data).addViolation(hashNode);
        }

        return data;
    }

    private boolean isEquals(ASTMethod node) {
        int numParams = 0;
        String paramType = null;
        for (int ix = 0; ix < node.getNumChildren(); ix++) {
            ApexNode<?> sn = node.getChild(ix);
            if (sn instanceof ASTParameter) {
                numParams++;
                paramType = ((ASTParameter) sn).getType();
            }
        }
        return numParams == 1 && "equals".equalsIgnoreCase(node.getImage()) && "Object".equalsIgnoreCase(paramType);
    }

    private boolean isHashCode(ASTMethod node) {
        int numParams = 0;
        for (int ix = 0; ix < node.getNumChildren(); ix++) {
            ApexNode<?> sn = node.getChild(ix);
            if (sn instanceof ASTParameter) {
                numParams++;
            }
        }

        return numParams == 0 && "hashCode".equalsIgnoreCase(node.getImage());
    }
}
