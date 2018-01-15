/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix;

import net.sourceforge.pmd.lang.ast.Node;

public class DummyWrongRuleViolationFix implements RuleViolationFix {

    private DummyWrongRuleViolationFix() {

    }

    @Override
    public void applyToNode(final Node node) {

    }
}
