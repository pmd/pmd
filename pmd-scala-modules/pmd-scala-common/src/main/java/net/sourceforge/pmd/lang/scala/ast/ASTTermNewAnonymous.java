/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Term;

/**
 * The ASTTermNewAnonymous node implementation.
 */
public class ASTTermNewAnonymous extends AbstractScalaNode<Term.NewAnonymous> {

    @Deprecated
    @InternalApi
    public ASTTermNewAnonymous(Term.NewAnonymous scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
