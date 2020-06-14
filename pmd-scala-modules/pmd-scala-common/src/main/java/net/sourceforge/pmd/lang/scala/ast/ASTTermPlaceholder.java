/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Term;

/**
 * The ASTTermPlaceholder node implementation.
 */
public final class ASTTermPlaceholder extends AbstractScalaNode<Term.Placeholder> {

    ASTTermPlaceholder(Term.Placeholder scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <D, R> R acceptVisitor(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
