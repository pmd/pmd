/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermMatch extends AbstractScalaNode<Term.Match> {

    public ASTTermMatch(Term.Match scalaNode) {
        super(scalaNode);
    }
}
