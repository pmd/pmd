/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermSelect extends AbstractScalaNode<Term.Select> {

    public ASTTermSelect(Term.Select scalaNode) {
        super(scalaNode);
    }
}
