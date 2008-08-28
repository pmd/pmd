/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTLiteral;

/**
 * This rule finds the following:
 * <p/>
 * <pre>
 *         StringBuffer.append(&quot;c&quot;); // appends a
 *         single character
 * </pre>
 * <p/>
 * It is preferable to use StringBuffer.append('c'); // appends a single
 * character Implementation of PMD RFE 1373863
 */
public class AppendCharacterWithChar extends AbstractRule {

    public Object visit(ASTLiteral node, Object data) {
        ASTBlockStatement bs = node.getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        if (node.isSingleCharacterStringLiteral()) {
            if (!InefficientStringBuffering.isInStringBufferOperation(node, 8, "append")) {
                return data;
            }
            addViolation(data, node);
        }
        return data;
    }
}
