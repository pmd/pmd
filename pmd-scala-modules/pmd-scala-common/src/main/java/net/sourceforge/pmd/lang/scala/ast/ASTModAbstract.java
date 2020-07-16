/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Mod;

/**
 * The ASTModAbstract node implementation.
 */
public class ASTModAbstract extends AbstractScalaNode<Mod.Abstract> {

    @Deprecated
    @InternalApi
    public ASTModAbstract(Mod.Abstract scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
