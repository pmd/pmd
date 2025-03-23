/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Name;

/**
 * The ASTNameAnonymous node implementation.
 */
public final class ASTNameAnonymous extends AbstractScalaNode<Name.Anonymous> {

    ASTNameAnonymous(Name.Anonymous scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getValue() {
        return node.value();
    }
}
