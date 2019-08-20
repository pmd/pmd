/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermRepeated extends AbstractScalaNode<Term.Repeated> {

    public ASTTermRepeated(Term.Repeated scalaNode) {
        super(scalaNode);
    }
}
