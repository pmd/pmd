/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Type;

/**
 * The ASTTypeImplicitFunction node implementation.
 */
public final class ASTTypeImplicitFunction extends AbstractScalaNode<Type.ImplicitFunction> {

    ASTTypeImplicitFunction(Type.ImplicitFunction scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
