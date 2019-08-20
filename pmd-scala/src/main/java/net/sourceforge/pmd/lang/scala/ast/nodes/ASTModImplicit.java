/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModImplicit extends AbstractScalaNode<Mod.Implicit> {

    public ASTModImplicit(Mod.Implicit scalaNode) {
        super(scalaNode);
    }
}
