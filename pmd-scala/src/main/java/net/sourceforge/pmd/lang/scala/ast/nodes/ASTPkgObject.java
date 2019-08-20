/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pkg;

public class ASTPkgObject extends AbstractScalaNode<Pkg.Object> {

    public ASTPkgObject(Pkg.Object scalaNode) {
        super(scalaNode);
    }
}
