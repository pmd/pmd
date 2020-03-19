/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.lang.vf.VfLanguageModule;
import net.sourceforge.pmd.lang.vf.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.vf.ast.VfNode;
import net.sourceforge.pmd.lang.vf.ast.VfParserVisitor;

public abstract class AbstractVfRule extends AbstractRule implements VfParserVisitor, ImmutableLanguage {

    public AbstractVfRule() {
        super.setLanguage(LanguageRegistry.getLanguage(VfLanguageModule.NAME));
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        for (Object element : nodes) {
            if (element instanceof ASTCompilationUnit) {
                ASTCompilationUnit node = (ASTCompilationUnit) element;
                visit(node, ctx);
            } else {
                VfNode node = (VfNode) element;
                visit(node, ctx);
            }
        }
    }

}
