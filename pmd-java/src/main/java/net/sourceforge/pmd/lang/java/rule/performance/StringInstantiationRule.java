/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class StringInstantiationRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTConstructorCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        ASTArgumentList args = node.getArguments();
        if (args.size() <= 1
            && TypeTestUtil.isExactlyA(String.class, node.getTypeNode())) {
            if (args.size() == 1 && TypeTestUtil.isExactlyA(byte[].class, args.get(0))) {
                // byte array ctor is ok
                return data;
            }
            addViolation(data, node);
        }
        return data;
    }
}
