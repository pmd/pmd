/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermPartialFunction extends AbstractScalaNode<Term.PartialFunction> {

    public ASTTermPartialFunction(Term.PartialFunction scalaNode) {
        super(scalaNode);
    }
}
