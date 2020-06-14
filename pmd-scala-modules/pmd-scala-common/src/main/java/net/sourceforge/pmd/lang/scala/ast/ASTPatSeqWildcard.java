/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Pat;

/**
 * The ASTPatSeqWildcard node implementation.
 */
public final class ASTPatSeqWildcard extends AbstractScalaNode<Pat.SeqWildcard> {

    ASTPatSeqWildcard(Pat.SeqWildcard scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <D, R> R acceptVisitor(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
