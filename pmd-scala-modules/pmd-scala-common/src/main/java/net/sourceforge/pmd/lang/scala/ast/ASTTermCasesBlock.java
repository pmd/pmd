/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Term;

/**
 * The ASTTermCasesBlock node implementation.
 * @since 7.10.0
 */
public final class ASTTermCasesBlock extends AbstractScalaNode<Term.CasesBlock> {

    ASTTermCasesBlock(Term.CasesBlock scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
