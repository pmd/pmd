/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermTuple extends AbstractScalaNode<Term.Tuple> {

    public ASTTermTuple(Term.Tuple scalaNode) {
        super(scalaNode);
    }
}
