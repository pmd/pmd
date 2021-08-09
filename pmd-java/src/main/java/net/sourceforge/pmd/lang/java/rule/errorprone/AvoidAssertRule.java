/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Finds <code>assert</code> statements
 *
 * @author <a href="mailto:nathan.reynolds@oracle.com">Nathan Reynolds</a>
 */
public class AvoidAssertRule extends AbstractJavaRule {

    public AvoidAssertRule() {
        addRuleChainVisit(ASTAssertStatement.class);
    }

    @Override
    public Object visit(ASTAssertStatement assertStatement, Object data) {
        addViolation(data, assertStatement);
        return data;
    }
}
