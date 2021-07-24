/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.codestyle.UnnecessaryBoxingRule;

/**
 * @deprecated Replaced by {@link UnnecessaryBoxingRule}.
 */
@Deprecated
public class UnnecessaryWrapperObjectCreationRule extends AbstractJavaRulechainRule {
    private static final Set<String> SUFFIX_SET = setOf("toString", "byteValue",
        "shortValue", "intValue", "longValue", "floatValue", "doubleValue", "charValue", "booleanValue");

    public UnnecessaryWrapperObjectCreationRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (!"valueOf".equals(node.getMethodName()) || !node.getQualifier().getTypeMirror().isBoxedPrimitive()
                || !(node.getParent() instanceof ASTMethodCall)) {
            return data;
        }

        ASTMethodCall nextMethodCall = (ASTMethodCall) node.getParent();
        String methodName = nextMethodCall.getMethodName();
        if (SUFFIX_SET.contains(methodName)) {
            addViolation(data, node);
        }
        
        return data;
    }
}
