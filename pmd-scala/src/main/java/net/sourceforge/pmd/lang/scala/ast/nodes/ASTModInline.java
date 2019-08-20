/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModInline extends AbstractScalaNode<Mod.Inline> {

    public ASTModInline(Mod.Inline scalaNode) {
        super(scalaNode);
    }
}
