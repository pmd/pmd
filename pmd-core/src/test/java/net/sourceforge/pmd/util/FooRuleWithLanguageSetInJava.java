/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;

public class FooRuleWithLanguageSetInJava extends AbstractRule {

    public FooRuleWithLanguageSetInJava() {
        setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
    }


    @Override
    public void apply(Node node, RuleContext ctx) {
        // do nothing
    }
}
