/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Enumerator;

/**
 * The ASTEnumeratorGuard node implementation.
 */
public final class ASTEnumeratorGuard extends AbstractScalaNode<Enumerator.Guard> {

    ASTEnumeratorGuard(Enumerator.Guard scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
