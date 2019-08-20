/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermName extends AbstractScalaNode<Term.Name> {

    public ASTTermName(Term.Name scalaNode) {
        super(scalaNode);
    }

    @Override
    public String getImage() {
        return getNode().value();
    }
}
