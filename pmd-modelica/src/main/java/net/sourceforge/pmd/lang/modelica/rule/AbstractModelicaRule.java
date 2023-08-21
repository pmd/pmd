/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.rule;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;

/**
 * Base class for rules for Modelica language.
 */
public abstract class AbstractModelicaRule extends AbstractRule implements ModelicaParserVisitor {

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

}
