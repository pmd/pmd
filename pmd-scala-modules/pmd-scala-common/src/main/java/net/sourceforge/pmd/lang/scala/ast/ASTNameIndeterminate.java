/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Name;

/**
 * The ASTNameIndeterminate node implementation.
 */
public final class ASTNameIndeterminate extends AbstractScalaNode<Name.Indeterminate> {

    ASTNameIndeterminate(Name.Indeterminate scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.value();
    }
}
