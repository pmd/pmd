/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.lang.vm.VmLanguageModule;
import net.sourceforge.pmd.lang.vm.ast.VmNode;
import net.sourceforge.pmd.lang.vm.ast.VmParserVisitor;

public abstract class AbstractVmRule extends AbstractRule implements VmParserVisitor, ImmutableLanguage {

    public AbstractVmRule() {
        super.setLanguage(LanguageRegistry.getLanguage(VmLanguageModule.NAME));
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        ((VmNode) target).jjtAccept(this, ctx);
    }

}
