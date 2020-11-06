/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Enumerator;

/**
 * The ASTEnumeratorVal node implementation.
 */
public class ASTEnumeratorVal extends AbstractScalaNode<Enumerator.Val> {

    @Deprecated
    @InternalApi
    public ASTEnumeratorVal(Enumerator.Val scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
