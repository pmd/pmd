/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Name;

/**
 * The ASTNameIndeterminate node implementation.
 */
public class ASTNameIndeterminate extends AbstractScalaNode<Name.Indeterminate> {

    @Deprecated
    @InternalApi
    public ASTNameIndeterminate(Name.Indeterminate scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.value();
    }
}
