/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Defn;

/**
 * The ASTDefnVar node implementation.
 */
public final class ASTDefnVar extends AbstractScalaNode<Defn.Var> {

    ASTDefnVar(Defn.Var scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
