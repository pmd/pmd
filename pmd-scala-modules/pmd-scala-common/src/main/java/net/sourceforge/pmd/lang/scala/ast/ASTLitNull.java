/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Lit;

/**
 * The ASTLitNull node implementation.
 */
public class ASTLitNull extends AbstractScalaNode<Lit.Null> {

    @Deprecated
    @InternalApi
    public ASTLitNull(Lit.Null scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return String.valueOf(node.value());
    }
}
