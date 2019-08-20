/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermNewAnonymous extends AbstractScalaNode<Term.NewAnonymous> {

    public ASTTermNewAnonymous(Term.NewAnonymous scalaNode) {
        super(scalaNode);
    }
}
