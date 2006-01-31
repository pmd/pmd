/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTLiteral;

import org.apache.oro.text.perl.Perl5Util;

/**
 * This rule finds the following:
 * 
 * <pre>
 *         StringBuffer.append(&quot;c&quot;); // appends a
 *         single character
 * </pre>
 * 
 * It is preferable to use StringBuffer.append('c'); // appends a single
 * character Implementation of PMD RFE 1373863
 */
public class AppendCharacterWithChar extends AbstractRule {

    private static final String REGEX = "/\"[\\\\]?[\\s\\S]\"/i";

    public Object visit(ASTLiteral node, Object data) {
        ASTBlockStatement bs = (ASTBlockStatement) node
                .getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        String str = node.getImage();
        if (str == null || str.length() < 3 || str.length() > 4) {
            return data;
        }

        // see
        // http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html#package_description
        Perl5Util regexp = new Perl5Util();
        if (regexp.match(REGEX, str)) {
            if (!InefficientStringBuffering.isInStringBufferAppend(node, 8)) {
                return data;
            }
            addViolation(data, node);
        }
        return data;
    }
}
