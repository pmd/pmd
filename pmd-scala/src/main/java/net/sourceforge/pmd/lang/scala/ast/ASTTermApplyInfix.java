/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Term;

/**
 * The ASTTermApplyInfix node implementation.
 */
public class ASTTermApplyInfix extends AbstractScalaNode<Term.ApplyInfix> {

    @Deprecated
    @InternalApi
    public ASTTermApplyInfix(Term.ApplyInfix scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
