/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermDo extends AbstractScalaNode<Term.Do> {

    public ASTTermDo(Term.Do scalaNode) {
        super(scalaNode);
    }
}
