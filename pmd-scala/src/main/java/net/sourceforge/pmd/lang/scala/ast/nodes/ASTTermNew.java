/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermNew extends AbstractScalaNode<Term.New> {

    public ASTTermNew(Term.New scalaNode) {
        super(scalaNode);
    }
}
