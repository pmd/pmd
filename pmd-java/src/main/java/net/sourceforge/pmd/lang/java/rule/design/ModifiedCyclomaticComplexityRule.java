/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * Implements the modified cyclomatic complexity rule
 * <p>
 * Modified rules: Same as standard cyclomatic complexity, but switch statement
 * plus all cases count as 1.
 *
 * @author Alan Hohn, based on work by Donald A. Leckie
 *
 * @since June 18, 2014
 */
@Deprecated
public class ModifiedCyclomaticComplexityRule extends StdCyclomaticComplexityRule {

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        visit((JavaNode) node, data);
        return data;
    }

}
