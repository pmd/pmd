/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.modelica.ModelicaLanguageModule;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaNode;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitor;
import net.sourceforge.pmd.lang.modelica.internal.ModelicaProcessingStage;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;

/**
 * Base class for rules for Modelica language.
 */
public abstract class AbstractModelicaRule extends AbstractRule implements ModelicaParserVisitor, ImmutableLanguage {
    public AbstractModelicaRule() {
        super.setLanguage(LanguageRegistry.getLanguage(ModelicaLanguageModule.NAME));
    }

    @Override
    public void apply(final List<? extends Node> nodes, final RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(final List<? extends Node> nodes, final RuleContext ctx) {
        for (final Object element : nodes) {
            final ASTStoredDefinition node = (ASTStoredDefinition) element;
            visit(node, ctx);
        }
    }

    @Override
    public Object visit(ModelicaNode node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
            node.jjtGetChild(i).jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public boolean dependsOn(AstProcessingStage<?> stage) {
        if (!(stage instanceof ModelicaProcessingStage)) {
            throw new IllegalArgumentException("Processing stage wasn't a Modelica one: " + stage);
        }
        return true;
    }

}
