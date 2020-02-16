/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Mod;

/**
 * The ASTModContravariant node implementation.
 */
public class ASTModContravariant extends AbstractScalaNode<Mod.Contravariant> {

    /**
     * Create the AST node for this Scala node.
     *
     * @param scalaNode
     *            the underlying Scala node
     */
    public ASTModContravariant(Mod.Contravariant scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
