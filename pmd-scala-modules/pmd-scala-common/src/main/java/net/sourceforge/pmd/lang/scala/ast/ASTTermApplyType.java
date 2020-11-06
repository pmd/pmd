/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Term;

/**
 * The ASTTermApplyType node implementation.
 */
public class ASTTermApplyType extends AbstractScalaNode<Term.ApplyType> {

    @Deprecated
    @InternalApi
    public ASTTermApplyType(Term.ApplyType scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
