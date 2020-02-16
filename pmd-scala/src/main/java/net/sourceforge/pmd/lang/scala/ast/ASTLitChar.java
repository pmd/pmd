/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Lit;

/**
 * The ASTLitChar node implementation.
 */
public class ASTLitChar extends AbstractScalaNode<Lit.Char> {

    /**
     * Create the AST node for this Scala node.
     *
     * @param scalaNode
     *            the underlying Scala node
     */
    public ASTLitChar(Lit.Char scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return String.valueOf(getNode().value());
    }
}
