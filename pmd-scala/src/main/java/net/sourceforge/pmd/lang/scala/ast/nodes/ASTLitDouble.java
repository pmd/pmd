/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Lit;

public class ASTLitDouble extends AbstractScalaNode<Lit.Double> {

    public ASTLitDouble(Lit.Double scalaNode) {
        super(scalaNode);
    }

    @Override
    public String getImage() {
        return String.valueOf(getNode().value());
    }
}
