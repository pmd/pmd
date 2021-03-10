/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;

public abstract class AbstractApexRule extends AbstractRule
        implements ApexParserVisitor {

    public AbstractApexRule() {
        super.setLanguage(LanguageRegistry.getLanguage(ApexLanguageModule.NAME));
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

}
