/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatInterpolate extends AbstractScalaNode<Pat.Interpolate> {

    public ASTPatInterpolate(Pat.Interpolate scalaNode) {
        super(scalaNode);
    }
}
