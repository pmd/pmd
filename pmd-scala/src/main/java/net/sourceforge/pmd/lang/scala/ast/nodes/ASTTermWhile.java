/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermWhile extends AbstractScalaNode<Term.While> {

    public ASTTermWhile(Term.While scalaNode) {
        super(scalaNode);
    }
}
