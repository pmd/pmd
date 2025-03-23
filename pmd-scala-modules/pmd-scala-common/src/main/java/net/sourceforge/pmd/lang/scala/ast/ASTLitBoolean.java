/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Lit;

/**
 * The ASTLitBoolean node implementation.
 */
public final class ASTLitBoolean extends AbstractScalaNode<Lit.Boolean> {

    ASTLitBoolean(Lit.Boolean scalaNode) {
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
