/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermAnnotate extends AbstractScalaNode<Term.Annotate> {

    public ASTTermAnnotate(Term.Annotate scalaNode) {
        super(scalaNode);
    }
}
