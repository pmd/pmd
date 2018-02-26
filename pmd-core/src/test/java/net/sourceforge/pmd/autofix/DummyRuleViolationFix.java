/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix;

import net.sourceforge.pmd.lang.ast.Node;

public class DummyRuleViolationFix implements RuleViolationFix {

    @Override
    public void applyToNode(final Node node) { }
}
