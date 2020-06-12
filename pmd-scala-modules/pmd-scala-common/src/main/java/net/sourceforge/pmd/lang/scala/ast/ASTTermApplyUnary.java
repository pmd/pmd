/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Term;

/**
 * The ASTTermApplyUnary node implementation.
 */
public class ASTTermApplyUnary extends AbstractScalaNode<Term.ApplyUnary> {

    @Deprecated
    @InternalApi
    public ASTTermApplyUnary(Term.ApplyUnary scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
