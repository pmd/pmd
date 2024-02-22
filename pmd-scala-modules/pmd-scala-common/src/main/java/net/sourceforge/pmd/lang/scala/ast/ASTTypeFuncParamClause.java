/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Type;

/**
 * The ASTTypeFuncParamClause node implementation.
 */
public final class ASTTypeFuncParamClause extends AbstractScalaNode<Type.FuncParamClause> {

    ASTTypeFuncParamClause(Type.FuncParamClause scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
