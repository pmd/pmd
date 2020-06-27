/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitor;

/**
 * The default base implementation of a PMD Rule for Scala. Uses the Visitor
 * Pattern to traverse the AST.
 */
public class ScalaRule extends AbstractRule implements ScalaParserVisitor<RuleContext, RuleContext> {

    /**
     * Create a new Scala Rule.
     */
    public ScalaRule() {
        super.setLanguage(LanguageRegistry.getLanguage(ScalaLanguageModule.NAME));
    }


    @Override
    public void apply(Node target, RuleContext ctx) {
        ((ScalaNode<?>) target).accept(this, ctx);
    }

    @Override
    public RuleContext visitNode(Node node, RuleContext param) {
        for (Node child : node.children()) {
            child.acceptVisitor(this, param);
        }
        return param;
    }
}
