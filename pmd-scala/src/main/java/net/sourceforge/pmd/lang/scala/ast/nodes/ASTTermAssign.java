/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermAssign extends AbstractScalaNode<Term.Assign> {

    public ASTTermAssign(Term.Assign scalaNode) {
        super(scalaNode);
    }
}
