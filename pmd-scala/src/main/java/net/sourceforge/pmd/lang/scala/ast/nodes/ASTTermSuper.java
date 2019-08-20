/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermSuper extends AbstractScalaNode<Term.Super> {

    public ASTTermSuper(Term.Super scalaNode) {
        super(scalaNode);
    }
}
