/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Lit;

/**
 * The ASTLitUnit node implementation.
 */
public final class ASTLitUnit extends AbstractScalaNode<Lit.Unit> {

    ASTLitUnit(Lit.Unit scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getValue() {
        return String.valueOf(node.value());
    }
}
