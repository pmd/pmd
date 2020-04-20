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
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
