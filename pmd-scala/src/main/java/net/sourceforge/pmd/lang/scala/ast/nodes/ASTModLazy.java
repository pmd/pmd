/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModLazy extends AbstractScalaNode<Mod.Lazy> {

    public ASTModLazy(Mod.Lazy scalaNode) {
        super(scalaNode);
    }
}
