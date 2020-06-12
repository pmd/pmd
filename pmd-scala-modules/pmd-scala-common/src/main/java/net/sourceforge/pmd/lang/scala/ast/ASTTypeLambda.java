/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Type;

/**
 * The ASTTypeLambda node implementation.
 */
public class ASTTypeLambda extends AbstractScalaNode<Type.Lambda> {

    @Deprecated
    @InternalApi
    public ASTTypeLambda(Type.Lambda scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
