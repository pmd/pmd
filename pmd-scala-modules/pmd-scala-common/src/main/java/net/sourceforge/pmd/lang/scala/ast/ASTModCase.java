/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Mod;

/**
 * The ASTModCase node implementation.
 */
public class ASTModCase extends AbstractScalaNode<Mod.Case> {

    @Deprecated
    @InternalApi
    public ASTModCase(Mod.Case scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
