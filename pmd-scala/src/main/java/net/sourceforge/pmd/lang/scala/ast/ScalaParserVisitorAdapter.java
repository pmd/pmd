/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

/**
 * An Adapter for the Scala Parser that implements the Visitor Pattern.
 */
public class ScalaParserVisitorAdapter implements ScalaParserVisitor {
    @Override
    public Object visit(ScalaNode node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSourceNode node, Object data) {
        return visit((ScalaNode) node, data);
    }
}
