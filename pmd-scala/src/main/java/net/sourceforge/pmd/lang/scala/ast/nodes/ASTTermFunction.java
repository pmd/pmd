/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermFunction extends AbstractScalaNode<Term.Function> {

    public ASTTermFunction(Term.Function scalaNode) {
        super(scalaNode);
    }
}
