/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.lang.vm.VmLanguageModule;
import net.sourceforge.pmd.lang.vm.ast.ASTTemplate;
import net.sourceforge.pmd.lang.vm.ast.VmParserVisitor;

public abstract class AbstractVmRule extends AbstractRule implements VmParserVisitor, ImmutableLanguage {

    public AbstractVmRule() {
        super.setLanguage(LanguageRegistry.getLanguage(VmLanguageModule.NAME));
    }

    @Override
    public void apply(final List<? extends Node> nodes, final RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(final List<? extends Node> nodes, final RuleContext ctx) {
        for (final Object element : nodes) {
            final ASTTemplate node = (ASTTemplate) element;
            visit(node, ctx);
        }
    }

}
