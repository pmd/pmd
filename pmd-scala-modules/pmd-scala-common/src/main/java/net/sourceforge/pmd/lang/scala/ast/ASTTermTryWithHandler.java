/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Term;

/**
 * The ASTTermTryWithHandler node implementation.
 */
public final class ASTTermTryWithHandler extends AbstractScalaNode<Term.TryWithHandler> {

    ASTTermTryWithHandler(Term.TryWithHandler scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
