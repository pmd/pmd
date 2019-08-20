/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatVar extends AbstractScalaNode<Pat.Var> {

    public ASTPatVar(Pat.Var scalaNode) {
        super(scalaNode);
    }
}
