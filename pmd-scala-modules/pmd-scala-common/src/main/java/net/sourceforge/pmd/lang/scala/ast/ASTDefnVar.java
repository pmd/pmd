/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Defn;

/**
 * The ASTDefnVar node implementation.
 */
public class ASTDefnVar extends AbstractScalaNode<Defn.Var> {

    @Deprecated
    @InternalApi
    public ASTDefnVar(Defn.Var scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
