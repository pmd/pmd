/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermEta extends AbstractScalaNode<Term.Eta> {

    public ASTTermEta(Term.Eta scalaNode) {
        super(scalaNode);
    }
}
