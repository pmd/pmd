/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermTry extends AbstractScalaNode<Term.Try> {

    public ASTTermTry(Term.Try scalaNode) {
        super(scalaNode);
    }
}
