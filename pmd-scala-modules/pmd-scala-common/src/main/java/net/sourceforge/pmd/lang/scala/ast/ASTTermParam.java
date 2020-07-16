/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Term;

/**
 * The ASTTermParam node implementation.
 */
public class ASTTermParam extends AbstractScalaNode<Term.Param> {

    @Deprecated
    @InternalApi
    public ASTTermParam(Term.Param scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
