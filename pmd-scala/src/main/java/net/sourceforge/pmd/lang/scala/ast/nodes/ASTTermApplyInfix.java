/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermApplyInfix extends AbstractScalaNode<Term.ApplyInfix> {

    public ASTTermApplyInfix(Term.ApplyInfix scalaNode) {
        super(scalaNode);
    }
}
