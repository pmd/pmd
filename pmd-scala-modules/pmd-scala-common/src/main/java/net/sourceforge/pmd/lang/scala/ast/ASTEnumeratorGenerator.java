/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Enumerator;

/**
 * The ASTEnumeratorGenerator node implementation.
 */
public final class ASTEnumeratorGenerator extends AbstractScalaNode<Enumerator.Generator> {

    ASTEnumeratorGenerator(Enumerator.Generator scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
