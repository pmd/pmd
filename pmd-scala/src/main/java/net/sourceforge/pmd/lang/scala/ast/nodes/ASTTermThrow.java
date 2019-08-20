/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermThrow extends AbstractScalaNode<Term.Throw> {

    public ASTTermThrow(Term.Throw scalaNode) {
        super(scalaNode);
    }
}
