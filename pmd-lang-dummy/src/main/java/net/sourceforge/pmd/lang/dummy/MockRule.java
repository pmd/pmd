/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dummy;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * A rule for the {@link DummyLanguageModule dummy language}, that does nothing.
 */
public class MockRule extends AbstractRule {

    public MockRule() {
        setLanguage(DummyLanguageModule.getInstance());
    }

    @Override
    public void apply(Node node, RuleContext ctx) {
        // the mock rule does nothing. Usually you would start here to analyze the AST.
    }
}
