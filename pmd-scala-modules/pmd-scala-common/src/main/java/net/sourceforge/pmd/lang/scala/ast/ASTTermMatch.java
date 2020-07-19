/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Term;

/**
 * The ASTTermMatch node implementation.
 */
public final class ASTTermMatch extends AbstractScalaNode<Term.Match> {

    ASTTermMatch(Term.Match scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
