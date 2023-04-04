/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Enumerator;

/**
 * The ASTEnumeratorVal node implementation.
 */
public final class ASTEnumeratorVal extends AbstractScalaNode<Enumerator.Val> {

    ASTEnumeratorVal(Enumerator.Val scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
