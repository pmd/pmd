/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModOverride extends AbstractScalaNode<Mod.Override> {

    public ASTModOverride(Mod.Override scalaNode) {
        super(scalaNode);
    }
}
