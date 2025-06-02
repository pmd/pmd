/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.JavaVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * Base class for Java rules. Any rule written in Java to analyse Java source should extend from
 * this base class.
 *
 * TODO add documentation
 *
 */
public abstract class AbstractJavaRule extends AbstractRule implements JavaVisitor {

    @Override
    public Object visitNode(Node node, Object param) {
        node.children().forEach(n -> n.acceptVisitor(this, param));
        return param;
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

}
