/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModCovariant extends AbstractScalaNode<Mod.Covariant> {

    public ASTModCovariant(Mod.Covariant scalaNode) {
        super(scalaNode);
    }
}
