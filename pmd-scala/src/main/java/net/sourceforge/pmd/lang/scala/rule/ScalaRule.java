/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.scala.ast.ASTSourceNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitor;

/**
 * The default base implementation of a PMD Rule for Scala. Uses the Visitor
 * Pattern to traverse the AST.
 */
public class ScalaRule extends AbstractRule implements ScalaParserVisitor {

    /**
     * Create a new Scala Rule.
     */
    public ScalaRule() {
        super.setLanguage(LanguageRegistry.getLanguage(ScalaLanguageModule.NAME));
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        for (Node node : nodes) {
            if (node instanceof ASTSourceNode) {
                visit((ASTSourceNode) node, ctx);
            }
        }
    }

    @Override
    public Object visit(ASTSourceNode node, Object data) {
        return visit((ScalaNode) node, data);
    }

    @Override
    public Object visit(ScalaNode node, Object data) {
        return node.childrenAccept(this, data);
    }

}
