/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatTyped extends AbstractScalaNode<Pat.Typed> {

    public ASTPatTyped(Pat.Typed scalaNode) {
        super(scalaNode);
    }
}
