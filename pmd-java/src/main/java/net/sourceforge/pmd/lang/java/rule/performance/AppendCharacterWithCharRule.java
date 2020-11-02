/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * This rule finds the following:
 *
 * <pre>
 * StringBuffer.append(&quot;c&quot;); // appends a single character
 * </pre>
 *
 * <p>It is preferable to use</p>
 *
 * <pre>StringBuffer.append('c'); // appends a single character</pre>
 *
 * @see <a href="https://sourceforge.net/p/pmd/feature-requests/381/">feature request #381 Single character StringBuffer.append </a>
 */
public class AppendCharacterWithCharRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTStringLiteral.class);
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        if (node.length() == 1 && node.getParent() instanceof ASTArgumentList
            && ((ASTArgumentList) node.getParent()).size() == 1) {
            JavaNode callParent = node.getParent().getParent();
            if (callParent instanceof ASTMethodCall) {
                ASTMethodCall call = (ASTMethodCall) callParent;
                if ("append".equals(call.getMethodName())
                    && (TypeTestUtil.isDeclaredInClass(StringBuilder.class, call.getMethodType())
                    || TypeTestUtil.isDeclaredInClass(StringBuffer.class, call.getMethodType()))
                ) {
                    addViolation(data, node);
                }
            }
        }
        return data;
    }
}
