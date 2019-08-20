/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermParam extends AbstractScalaNode<Term.Param> {

    public ASTTermParam(Term.Param scalaNode) {
        super(scalaNode);
    }
}
