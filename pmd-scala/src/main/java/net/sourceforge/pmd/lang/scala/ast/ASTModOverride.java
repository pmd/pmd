/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Mod;

/**
 * The ASTModOverride node implementation.
 */
public class ASTModOverride extends AbstractScalaNode<Mod.Override> {

    /**
     * Create the AST node for this Scala node.
     *
     * @param scalaNode
     *            the underlying Scala node
     */
    public ASTModOverride(Mod.Override scalaNode) {
        super(scalaNode);
    }

    // java.lang pacakage is required or else PMD can't see this Override
    @java.lang.Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
