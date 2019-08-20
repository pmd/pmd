/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermFor extends AbstractScalaNode<Term.For> {

    public ASTTermFor(Term.For scalaNode) {
        super(scalaNode);
    }
}
