/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Base class for rules for Modelica language.
 */
public abstract class AbstractModelicaRule extends AbstractRule implements ModelicaVisitor<RuleContext, RuleContext> {

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

    @Override
    public RuleContext visitNode(Node node, RuleContext param) {
        node.children().forEach(c -> c.acceptVisitor(this, param));
        return param;
    }
}
