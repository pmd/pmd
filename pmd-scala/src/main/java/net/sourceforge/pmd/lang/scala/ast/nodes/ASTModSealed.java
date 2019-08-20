/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModSealed extends AbstractScalaNode<Mod.Sealed> {

    public ASTModSealed(Mod.Sealed scalaNode) {
        super(scalaNode);
    }
}
