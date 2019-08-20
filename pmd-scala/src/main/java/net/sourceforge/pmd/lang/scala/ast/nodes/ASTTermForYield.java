/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermForYield extends AbstractScalaNode<Term.ForYield> {

    public ASTTermForYield(Term.ForYield scalaNode) {
        super(scalaNode);
    }
}
