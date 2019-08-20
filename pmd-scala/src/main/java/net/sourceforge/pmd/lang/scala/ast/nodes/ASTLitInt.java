/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Lit;

public class ASTLitInt extends AbstractScalaNode<Lit.Int> {

    public ASTLitInt(Lit.Int scalaNode) {
        super(scalaNode);
    }

    @Override
    public String getImage() {
        return String.valueOf(getNode().value());
    }
}
