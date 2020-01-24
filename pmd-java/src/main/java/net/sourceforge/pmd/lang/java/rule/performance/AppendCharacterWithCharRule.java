/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

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
    public Object visit(ASTLiteral node, Object data) {
        ASTBlockStatement bs = node.getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        if (node.isSingleCharacterStringLiteral()) {
            if (!InefficientStringBufferingRule.isInStringBufferOperation(node, 8, "append")) {
                return data;
            }
            addViolation(data, node);
        }
        return data;
    }
}
