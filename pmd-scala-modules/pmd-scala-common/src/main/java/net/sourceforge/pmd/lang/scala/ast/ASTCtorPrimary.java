/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Ctor;

/**
 * The ASTCtorPrimary node implementation.
 */
public final class ASTCtorPrimary extends AbstractScalaNode<Ctor.Primary> {

    ASTCtorPrimary(Ctor.Primary scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
