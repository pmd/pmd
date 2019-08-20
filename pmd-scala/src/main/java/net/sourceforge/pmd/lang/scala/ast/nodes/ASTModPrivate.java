/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModPrivate extends AbstractScalaNode<Mod.Private> {

    public ASTModPrivate(Mod.Private scalaNode) {
        super(scalaNode);
    }
}
