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
    protected <D, R> R acceptVisitor(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.value();
    }
}
