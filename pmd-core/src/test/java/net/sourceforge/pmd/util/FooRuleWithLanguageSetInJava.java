/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class FooRuleWithLanguageSetInJava extends AbstractRule {

    public FooRuleWithLanguageSetInJava() {
        setLanguage(DummyLanguageModule.getInstance());
    }


    @Override
    public void apply(Node node, RuleContext ctx) {
        // do nothing
    }
}
