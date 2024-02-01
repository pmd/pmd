/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.scala.ast.ScalaVisitor;

/**
 * The default base implementation of a PMD Rule for Scala. Uses the Visitor
 * Pattern to traverse the AST.
 */
public class ScalaRule extends AbstractRule implements ScalaVisitor<RuleContext, RuleContext> {

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

    @Override
    public RuleContext visitNode(Node node, RuleContext param) {
        for (Node child : node.children()) {
            child.acceptVisitor(this, param);
        }
        return param;
    }
}
