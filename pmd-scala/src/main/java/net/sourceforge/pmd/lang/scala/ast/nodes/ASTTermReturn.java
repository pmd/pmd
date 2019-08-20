/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermReturn extends AbstractScalaNode<Term.Return> {

    public ASTTermReturn(Term.Return scalaNode) {
        super(scalaNode);
    }
}
