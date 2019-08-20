/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Mod;

public class ASTModAnnot extends AbstractScalaNode<Mod.Annot> {

    public ASTModAnnot(Mod.Annot scalaNode) {
        super(scalaNode);
    }
}
