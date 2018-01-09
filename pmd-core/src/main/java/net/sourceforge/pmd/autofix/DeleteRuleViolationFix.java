/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix;

import net.sourceforge.pmd.RuleViolationFix;
import net.sourceforge.pmd.lang.ast.Node;

public class DeleteRuleViolationFix implements RuleViolationFix {

    @Override
    public void applyFixesToNode(final Node node) {
        node.remove();
    }
}
