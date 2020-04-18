/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Defn;

/**
 * The ASTDefnVal node implementation.
 */
public class ASTDefnVal extends AbstractScalaNode<Defn.Val> {

    @Deprecated
    @InternalApi
    public ASTDefnVal(Defn.Val scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
